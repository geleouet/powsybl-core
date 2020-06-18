/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.iidm.network.impl;

import com.powsybl.iidm.network.Injection;
import com.powsybl.iidm.network.OperationalLimits;

/**
 * @author Miora Ralambotiana <miora.ralambotiana at rte-france.com>
 */
abstract class AbstractInjection<I extends Injection<I>> extends AbstractConnectable<I> implements OperationalLimitsOwner<Void>, Injection<I> {

    AbstractInjection(String id, String name, boolean fictitious) {
        super(id, name, fictitious);
    }

    @Override
    public void setOperationalLimits(Void side, OperationalLimits operationalLimits) {
        
    }
}
