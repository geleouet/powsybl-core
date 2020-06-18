/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.iidm.network.impl;

import com.powsybl.iidm.network.*;

import java.util.*;

/**
 * @author Miora Ralambotiana <miora.ralambotiana at rte-france.com>
 */
abstract class AbstractInjection<I extends Injection<I>> extends AbstractConnectable<I> implements Injection<I> {

    private final OperationalLimitsHolderImpl operationalLimitsHolder;

    AbstractInjection(String id, String name, boolean fictitious) {
        super(id, name, fictitious);
        operationalLimitsHolder = new OperationalLimitsHolderImpl("limits", this);
    }

    @Override
    public List<OperationalLimits> getOperationalLimits() {
        return operationalLimitsHolder.getOperationalLimits();
    }

    @Override
    public <L extends OperationalLimits> L getOperationalLimits(LimitType type, Class<L> limitClazz) {
        return operationalLimitsHolder.getOperationalLimits(type, limitClazz);
    }

    @Override
    public CurrentLimitsAdder newCurrentLimits() {
        return operationalLimitsHolder.newCurrentLimits();
    }

    @Override
    public ApparentPowerLimitsAdder newApparentPowerLimits() {
        return operationalLimitsHolder.newApparentPowerLimits();
    }

    @Override
    public VoltageLimitsAdder newVoltageLimits() {
        return operationalLimitsHolder.newVoltageLimits();
    }
}
