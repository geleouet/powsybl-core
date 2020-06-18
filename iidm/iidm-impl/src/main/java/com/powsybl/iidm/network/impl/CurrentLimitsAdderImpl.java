/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.iidm.network.impl;

import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.LoadingLimits.TemporaryLimit;
import com.powsybl.iidm.network.impl.CurrentLimitsImpl.TemporaryLimitImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class CurrentLimitsAdderImpl implements CurrentLimitsAdder {

    private static final Comparator<Integer> ACCEPTABLE_DURATION_COMPARATOR = (acceptableDuraction1, acceptableDuraction2) -> acceptableDuraction2 - acceptableDuraction1;

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentLimitsAdderImpl.class);

    private final OperationalLimitsOwner owner;

    private double permanentLimit = Double.NaN;

    private final TreeMap<Integer, TemporaryLimit> temporaryLimits = new TreeMap<>(ACCEPTABLE_DURATION_COMPARATOR);

    public class TemporaryLimitAdderImpl implements TemporaryLimitAdder<CurrentLimitsAdder> {

        private String name;

        private double value = Double.NaN;

        private Integer acceptableDuration;

        private boolean fictitious = false;

        private boolean ensureNameUnicity = false;

        @Override
        public TemporaryLimitAdder<CurrentLimitsAdder> setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public TemporaryLimitAdder<CurrentLimitsAdder> setValue(double value) {
            this.value = value;
            return this;
        }

        @Override
        public TemporaryLimitAdder<CurrentLimitsAdder> setAcceptableDuration(int acceptableDuration) {
            this.acceptableDuration = acceptableDuration;
            return this;
        }

        @Override
        public TemporaryLimitAdder<CurrentLimitsAdder> setFictitious(boolean fictitious) {
            this.fictitious = fictitious;
            return this;
        }

        @Override
        public TemporaryLimitAdder<CurrentLimitsAdder> ensureNameUnicity() {
            this.ensureNameUnicity = true;
            return this;
        }

        @Override
        public CurrentLimitsAdderImpl endTemporaryLimit() {
            if (Double.isNaN(value)) {
                throw new ValidationException(owner, "temporary limit value is not set");
            }
            if (value <= 0) {
                throw new ValidationException(owner, "temporary limit value must be > 0");
            }
            if (acceptableDuration == null) {
                throw new ValidationException(owner, "acceptable duration is not set");
            }
            if (acceptableDuration < 0) {
                throw new ValidationException(owner, "acceptable duration must be >= 0");
            }
            checkAndGetUniqueName();
            temporaryLimits.put(acceptableDuration, new TemporaryLimitImpl(name, value, acceptableDuration, fictitious));
            return CurrentLimitsAdderImpl.this;
        }

        private void checkAndGetUniqueName() {
            if (name == null) {
                throw new ValidationException(owner, "name is not set");
            }
            if (ensureNameUnicity) {
                int i = 0;
                String uniqueName = name;
                while (i < Integer.MAX_VALUE && nameExists(uniqueName)) {
                    uniqueName = name + "#" + i;
                    i++;
                }
                name = uniqueName;
            }
        }

        private boolean nameExists(String name) {
            return temporaryLimits.values().stream().anyMatch(t -> t.getName().equals(name));
        }
    }

    /**
     * @deprecated Use {@link #CurrentLimitsAdderImpl(OperationalLimitsOwner)} instead.
     */
    @Deprecated
    public <S, O extends CurrentLimitsOwner<S>> CurrentLimitsAdderImpl(S side, O owner) {
        this.owner = owner;
    }

    public CurrentLimitsAdderImpl(OperationalLimitsOwner owner) {
        this.owner = owner;
    }

    @Override
    public double getPermanentLimit() {
        return permanentLimit;
    }

    @Override
    public CurrentLimitsAdder setPermanentLimit(double limit) {
        this.permanentLimit = limit;
        return this;
    }

    @Override
    public double getTemporaryLimitValue(int acceptableDuration) {
        return Optional.ofNullable(temporaryLimits.get(acceptableDuration)).map(TemporaryLimit::getValue).orElse(Double.NaN);
    }

    @Override
    public TemporaryLimitAdder<CurrentLimitsAdder> beginTemporaryLimit() {
        return new TemporaryLimitAdderImpl();
    }

    private void checkTemporaryLimits() {
        // check temporary limits are consistents with permanent
        double previousLimit = Double.NaN;
        for (TemporaryLimit tl : temporaryLimits.values()) { // iterate in ascending order
            if (tl.getValue() <= permanentLimit) {
                LOGGER.debug("{}, temporary limit should be greather than permanent limit", owner.getMessageHeader());
            }
            if (Double.isNaN(previousLimit)) {
                previousLimit = tl.getValue();
            } else {
                if (tl.getValue() <= previousLimit) {
                    LOGGER.debug("{} : temporary limits should be in ascending value order", owner.getMessageHeader());
                }
            }
        }
        // check name unicity
        temporaryLimits.values().stream()
                .collect(Collectors.groupingBy(TemporaryLimit::getName))
                .forEach((name, temporaryLimits1) -> {
                    if (temporaryLimits1.size() > 1) {
                        throw new ValidationException(owner, temporaryLimits1.size() + "temporary limits have the same name " + name);
                    }
                });
    }

    @Override
    public CurrentLimits add() {
        ValidationUtil.checkPermanentLimit(owner, permanentLimit);
        checkTemporaryLimits();
        CurrentLimitsImpl limits = new CurrentLimitsImpl(permanentLimit, temporaryLimits, owner);
        owner.setOperationalLimits(LimitType.CURRENT, limits);
        return limits;
    }

}
