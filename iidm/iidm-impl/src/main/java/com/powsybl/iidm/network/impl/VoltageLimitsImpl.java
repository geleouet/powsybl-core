/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.iidm.network.impl;

import com.powsybl.iidm.network.VoltageLimits;

/**
 * @author Miora Ralambotiana <miora.ralambotiana at rte-france.com>
 */
class VoltageLimitsImpl implements VoltageLimits {

    private double highVoltage;
    private double lowVoltage;

    VoltageLimitsImpl(double lowVoltage, double highVoltage) {
        this.highVoltage = highVoltage;
        this.lowVoltage = lowVoltage;
    }

    @Override
    public double getHighVoltage() {
        return highVoltage;
    }

    @Override
    public VoltageLimits setHighVoltage(double highVoltage) {
        this.highVoltage = highVoltage;

        return this;
    }

    @Override
    public double getLowVoltage() {
        return lowVoltage;
    }

    @Override
    public VoltageLimits setLowVoltage(double lowVoltage) {
        this.lowVoltage = lowVoltage;
        return this;
    }

    @Override
    public String getUnit() {
        return "kV";
    }
}
