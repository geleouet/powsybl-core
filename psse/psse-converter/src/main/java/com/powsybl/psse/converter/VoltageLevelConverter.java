/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.psse.converter;

import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.Substation;
import com.powsybl.iidm.network.TopologyKind;
import com.powsybl.iidm.network.VoltageLevel;
import com.powsybl.iidm.network.util.ContainersMapping;
import com.powsybl.psse.converter.PsseImporter.PerUnitContext;
import com.powsybl.psse.model.PsseBus;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 * @author José Antonio Marqués <marquesja at aia.es>
 */
public class VoltageLevelConverter extends AbstractConverter {

    public VoltageLevelConverter(PsseBus psseBus, ContainersMapping containerMapping, PerUnitContext perUnitContext, Network network) {
        super(containerMapping, network);
        this.psseBus = psseBus;
        this.perUnitContext = perUnitContext;
    }

    public VoltageLevel create(Substation substation) {
        String voltageLevelId = getContainersMapping().getVoltageLevelId(psseBus.getI());
        double nominalV = perUnitContext.isIgnoreBaseVoltage() || psseBus.getBaskv() == 0 ? 1 : psseBus.getBaskv();
        VoltageLevel voltageLevel = getNetwork().getVoltageLevel(voltageLevelId);

        if (voltageLevel == null) {
            voltageLevel = substation.newVoltageLevel()
                .setId(voltageLevelId)
                .setNominalV(nominalV)
                .setTopologyKind(TopologyKind.BUS_BREAKER)
                .add();
        }
        return voltageLevel;
    }

    private final PsseBus psseBus;
    private final PerUnitContext perUnitContext;
}
