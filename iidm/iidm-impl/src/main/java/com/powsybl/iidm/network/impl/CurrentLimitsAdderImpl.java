/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.iidm.network.impl;

import com.powsybl.iidm.network.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class CurrentLimitsAdderImpl extends AbstractLoadingLimitsAdder<CurrentLimits, CurrentLimitsAdder> implements CurrentLimitsAdder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentLimitsAdderImpl.class);

    /**
     * @deprecated Use {@link #CurrentLimitsAdderImpl(OperationalLimitsOwner)} instead.
     */
    @Deprecated
    public <S, O extends CurrentLimitsOwner<S>> CurrentLimitsAdderImpl(S side, O owner) {
        super(owner);
    }

    public CurrentLimitsAdderImpl(OperationalLimitsOwner owner) {
        super(owner);
    }

    @Override
    public CurrentLimitsImpl add() {
        ValidationUtil.checkPermanentLimit(owner, permanentLimit);
        checkTemporaryLimits();
        CurrentLimitsImpl limits = new CurrentLimitsImpl(permanentLimit, temporaryLimits, owner);
        owner.setOperationalLimits(LimitType.CURRENT, limits);
        return limits;
    }

}
