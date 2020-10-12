/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.psse.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.ShuntCompensator;
import com.powsybl.iidm.network.ShuntCompensatorAdder;
import com.powsybl.iidm.network.VoltageLevel;
import com.powsybl.iidm.network.util.ContainersMapping;
import com.powsybl.psse.model.PsseFixedShunt;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 * @author José Antonio Marqués <marquesja at aia.es>
 */
public class FixedShuntCompensatorConverter extends AbstractConverter {

    public FixedShuntCompensatorConverter(PsseFixedShunt psseFixedShunt, ContainersMapping containerMapping, Network network) {
        super(containerMapping, network);
        this.psseFixedShunt = psseFixedShunt;
    }

    public void create() {
        if (psseFixedShunt.getBl() == 0) {
            // TODO : allow import of shunts with Bl= 0 in iidm?
            LOGGER.warn("Shunt ({}) has Bl = 0, not imported ", psseFixedShunt.getI());
            return;
        }

        String busId = getBusId(psseFixedShunt.getI());
        VoltageLevel voltageLevel = getNetwork()
            .getVoltageLevel(getContainersMapping().getVoltageLevelId(psseFixedShunt.getI()));
        ShuntCompensatorAdder adder = voltageLevel.newShuntCompensator()
            .setId(getShuntId(busId))
            .setConnectableBus(busId)
            .setBus(busId)
            .setSectionCount(1);
        adder.newLinearModel()
            .setBPerSection(psseFixedShunt.getBl())// TODO: take into account gl
            .setMaximumSectionCount(1)
            .add();
        ShuntCompensator shunt = adder.add();

        if (psseFixedShunt.getStatus() == 1) {
            shunt.getTerminal().connect();
        }

        if (psseFixedShunt.getGl() != 0) {
            LOGGER.warn("Shunt Gl not supported ({})", psseFixedShunt.getI());
        }
    }

    private String getShuntId(String busId) {
        return busId + "-SH" + psseFixedShunt.getId();
    }

    private final PsseFixedShunt psseFixedShunt;

    private static final Logger LOGGER = LoggerFactory.getLogger(FixedShuntCompensatorConverter.class);
}
