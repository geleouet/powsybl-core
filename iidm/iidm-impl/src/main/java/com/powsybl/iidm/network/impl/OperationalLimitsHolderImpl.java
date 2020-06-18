/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.iidm.network.impl;

import com.powsybl.iidm.network.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * @author Miora Ralambotiana <miora.ralambotiana at rte-france.com>
 */
class OperationalLimitsHolderImpl implements OperationalLimitsOwner {

    private final EnumMap<LimitType, OperationalLimits> operationalLimits = new EnumMap<>(LimitType.class);
    private final AbstractIdentifiable identifiable;
    private final String attributeName;

    OperationalLimitsHolderImpl(String attributeName, AbstractIdentifiable identifiable) {
        this.identifiable = identifiable;
        this.attributeName = attributeName;
    }

    @Override
    public void setOperationalLimits(LimitType limitType, OperationalLimits operationalLimits) {
        OperationalLimits oldValue = this.operationalLimits.put(limitType, operationalLimits);
        identifiable.getNetwork().getListeners().notifyUpdate(identifiable, attributeName + "_" + limitType, oldValue, operationalLimits);
    }

    public List<OperationalLimits> getOperationalLimits() {
        return new ArrayList<>(operationalLimits.values());
    }

    public <L extends OperationalLimits> L getOperationalLimits(LimitType type, Class<L> limitClazz) {
        if (type == null) {
            throw new IllegalArgumentException("limit type is null");
        }
        OperationalLimits operationalLimits = this.operationalLimits.get(type);
        if (operationalLimits == null || limitClazz.isInstance(operationalLimits)) {
            return (L) operationalLimits;
        }
        throw new AssertionError("Unexpected class for operational limits of type " + type + ". Expected: " + operationalLimits.getClass().getName() + ", actual: " + limitClazz.getName() + ".");
    }

    public CurrentLimitsAdder newCurrentLimits() {
        return new CurrentLimitsAdderImpl(this);
    }

    public ApparentPowerLimitsAdder newApparentPowerLimits() {
        return null;
    }

    public VoltageLimitsAdder newVoltageLimits() {
        return null;
    }

    @Override
    public String getMessageHeader() {
        return identifiable.getMessageHeader();
    }
}
