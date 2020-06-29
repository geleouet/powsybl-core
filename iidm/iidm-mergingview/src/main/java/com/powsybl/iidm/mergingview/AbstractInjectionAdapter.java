/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.iidm.mergingview;

import com.powsybl.iidm.network.*;

import java.util.List;

/**
 * @author Thomas Adam <tadam at silicom.fr>
 */
abstract class AbstractInjectionAdapter<I extends Injection<I>> extends AbstractConnectableAdapter<I> implements Injection<I> {

    protected AbstractInjectionAdapter(I delegate, MergingViewIndex index) {
        super(delegate, index);
    }

    @Override
    public final Terminal getTerminal() {
        return getIndex().getTerminal(getDelegate().getTerminal());
    }

    @Override
    public List<OperationalLimits> getOperationalLimits() {
        return getDelegate().getOperationalLimits();
    }

    @Override
    public  <L extends OperationalLimits> L getOperationalLimits(LimitType type, Class<L> limitClazz) {
        return getDelegate().getOperationalLimits(type, limitClazz);
    }

    @Override
    public CurrentLimitsAdder newCurrentLimits() {
        return getDelegate().newCurrentLimits();
    }

    @Override
    public ApparentPowerLimitsAdder newApparentPowerLimits() {
        return getDelegate().newApparentPowerLimits();
    }

    @Override
    public ActivePowerLimitsAdder newActivePowerLimits() {
        return getDelegate().newActivePowerLimits();
    }

    @Override
    public VoltageLimitsAdder newVoltageLimits() {
        return getDelegate().newVoltageLimits();
    }
}
