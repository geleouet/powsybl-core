package com.powsybl.cgmes.update.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.cgmes.model.CgmesModel;
import com.powsybl.cgmes.update.IidmChangesObject;
import com.powsybl.cgmes.update.CgmesFromIidmModifier;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Country;
import com.powsybl.iidm.network.LccConverterStation;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.Substation;
import com.powsybl.iidm.network.TopologyKind;
import com.powsybl.iidm.network.VoltageLevel;

public class ChangeIidmModel {

    public ChangeIidmModel(Network network) {
        this.network = network;
        changes = new ArrayList<>();
        update = new CgmesFromIidmModifier(network, changes);
    }

    public Network updateImportedModel() throws IOException {

        LOGGER.info("IidmChangesObject list size is 0");

        update.addListenerForUpdates();

        /**
         * Test onCreation
         */
        Substation substation = network.newSubstation()
            .setCountry(Country.AF)
            .setTso("tso")
            .setName("sub")
            .setId("subId")
            .add();
        VoltageLevel voltageLevel = substation.newVoltageLevel()
            .setTopologyKind(TopologyKind.BUS_BREAKER)
            .setId("bbVL")
            .setName("bbVL_name")
            .setNominalV(200.0f)
            .add();
        Bus bus = voltageLevel.getBusBreakerView()
            .newBus()
            .setName("bus1Name")
            .setId("bus1")
            .add();
        LccConverterStation lccConverterStation = voltageLevel.newLccConverterStation()
            .setId("lcc")
            .setName("lcc")
            .setBus("bus1")
            .setLossFactor(0.011f)
            .setPowerFactor(0.5f)
            .setConnectableBus("bus1")
            .add();

        assertTrue(changes.size() == 4);
        LOGGER.info("IidmChangesObject list size is {}", changes.size());

        /**
         * Test onUpdate
         */
        double p1 = 1.0;
        double q1 = 2.0;
        lccConverterStation.getTerminal().setP(p1);
        lccConverterStation.getTerminal().setQ(q1);

        assertTrue(changes.size() == 6);
        LOGGER.info("IidmChangesObject list size is {}", changes.size());

        return network;
    }

    public CgmesModel mapIidmChangesToCgmesTester() {
        return update.mapIidmChangesToCgmes();
    }

    private Network network;
    List<IidmChangesObject> changes;
    CgmesFromIidmModifier update;
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeIidmModel.class);
}
