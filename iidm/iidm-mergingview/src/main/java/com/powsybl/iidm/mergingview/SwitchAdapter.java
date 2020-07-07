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
public class SwitchAdapter extends AbstractIdentifiableAdapter<Switch> implements Switch {

    SwitchAdapter(final Switch delegate, final MergingViewIndex index) {
        super(delegate, index);
    }

    @Override
    public VoltageLevelAdapter getVoltageLevel() {
        return getIndex().getVoltageLevel(getDelegate().getVoltageLevel());
    }

    // -------------------------------
    // Simple delegated methods ------
    // -------------------------------
    @Override
    public SwitchKind getKind() {
        return getDelegate().getKind();
    }

    @Override
    public boolean isOpen() {
        return getDelegate().isOpen();
    }

    @Override
    public void setOpen(final boolean open) {
        getDelegate().setOpen(open);
    }

    @Override
    public boolean isRetained() {
        return getDelegate().isRetained();
    }

    @Override
    public void setRetained(final boolean retained) {
        getDelegate().setRetained(retained);
    }

    @Override
    public List<OperationalLimits> getOperationalLimits1() {
        return getDelegate().getOperationalLimits1();
    }

    @Override
    public  <L extends OperationalLimits> L getOperationalLimits1(LimitType limitType, Class<L> limitClazz) {
        return getDelegate().getOperationalLimits1(limitType, limitClazz);
    }

    @Override
    public CurrentLimitsAdder newCurrentLimits1() {
        return getDelegate().newCurrentLimits1();
    }

    @Override
    public ActivePowerLimitsAdder newActivePowerLimits1() {
        return getDelegate().newActivePowerLimits1();
    }

    @Override
    public ApparentPowerLimitsAdder newApparentPowerLimits1() {
        return getDelegate().newApparentPowerLimits1();
    }

    @Override
    public VoltageLimitsAdder newVoltageLimits1() {
        return getDelegate().newVoltageLimits1();
    }

    @Override
    public List<OperationalLimits> getOperationalLimits2() {
        return getDelegate().getOperationalLimits2();
    }

    @Override
    public <L extends OperationalLimits> L getOperationalLimits2(LimitType limitType, Class<L> limitClazz) {
        return getDelegate().getOperationalLimits2(limitType, limitClazz);
    }

    @Override
    public CurrentLimitsAdder newCurrentLimits2() {
        return getDelegate().newCurrentLimits2();
    }

    @Override
    public ActivePowerLimitsAdder newActivePowerLimits2() {
        return getDelegate().newActivePowerLimits2();
    }

    @Override
    public ApparentPowerLimitsAdder newApparentPowerLimits2() {
        return getDelegate().newApparentPowerLimits2();
    }

    @Override
    public VoltageLimitsAdder newVoltageLimits2() {
        return getDelegate().newVoltageLimits2();
    }
}
