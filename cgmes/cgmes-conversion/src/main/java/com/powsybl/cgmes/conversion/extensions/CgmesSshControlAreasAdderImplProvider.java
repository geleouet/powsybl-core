/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.cgmes.conversion.extensions;

import com.google.auto.service.AutoService;
import com.powsybl.commons.extensions.ExtensionAdderProvider;
import com.powsybl.iidm.network.Network;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 * @author José Antonio Marqués <marquesja at aia.es>
 */
@AutoService(ExtensionAdderProvider.class)
public class CgmesSshControlAreasAdderImplProvider implements
    ExtensionAdderProvider<Network, CgmesSshControlAreas, CgmesSshControlAreasAdderImpl> {

    @Override
    public String getImplementationName() {
        return "Default";
    }

    @Override
    public Class<? super CgmesSshControlAreasAdderImpl> getAdderClass() {
        return CgmesSshControlAreasAdderImpl.class;
    }

    @Override
    public CgmesSshControlAreasAdderImpl newAdder(Network extendable) {
        return new CgmesSshControlAreasAdderImpl(extendable);
    }
}
