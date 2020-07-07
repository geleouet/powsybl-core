/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.iidm.network;

import java.util.Collections;
import java.util.List;

/**
 * @author Miora Ralambotiana <miora.ralambotiana at rte-france.com>
 */
public interface OperationalLimitsSidedHolder {

    default List<OperationalLimits> getOperationalLimits1() {
        return Collections.emptyList();
    }

    default <L extends OperationalLimits> L getOperationalLimits1(LimitType limitType, Class<L> limitClazz) {
        return null;
    }

    default CurrentLimitsAdder newCurrentLimits1() {
        throw new UnsupportedOperationException();
    }

    default ActivePowerLimitsAdder newActivePowerLimits1() {
        throw new UnsupportedOperationException();
    }

    default ApparentPowerLimitsAdder newApparentPowerLimits1() {
        throw new UnsupportedOperationException();
    }

    default VoltageLimitsAdder newVoltageLimits1() {
        throw new UnsupportedOperationException();
    }

    default List<OperationalLimits> getOperationalLimits2() {
        return Collections.emptyList();
    }

    default <L extends OperationalLimits> L getOperationalLimits2(LimitType limitType, Class<L> limitClazz) {
        return null;
    }

    default CurrentLimitsAdder newCurrentLimits2() {
        throw new UnsupportedOperationException();
    }

    default ActivePowerLimitsAdder newActivePowerLimits2() {
        throw new UnsupportedOperationException();
    }

    default ApparentPowerLimitsAdder newApparentPowerLimits2() {
        throw new UnsupportedOperationException();
    }

    default VoltageLimitsAdder newVoltageLimits2() {
        throw new UnsupportedOperationException();
    }
}
