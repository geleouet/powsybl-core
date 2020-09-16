/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.iidm.network.impl;

import com.powsybl.iidm.network.LimitType;

/**
 * @deprecated Use {@link OperationalLimitsOwner} instead.
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@Deprecated
public interface CurrentLimitsOwner<SIDE> extends OperationalLimitsOwner {

    default void setCurrentLimits(SIDE side, CurrentLimitsImpl limits) {
        setOperationalLimits(LimitType.CURRENT, limits);
    }
}
