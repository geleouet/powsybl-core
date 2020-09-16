/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.cgmes.conversion.extensions;

import com.powsybl.cgmes.conversion.elements.CgmesTopologyKind;
import com.powsybl.iidm.mergingview.MergingView;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.network.test.NetworkTest1Factory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Miora Ralambotiana <miora.ralambotiana at rte-france.com>
 */
public class MergedCimCharacteristicsTest {

    private MergingView network;

    @Before
    public void setUp() {
        Network network1 = EurostagTutorialExample1Factory.create();
        network1.newExtension(CimCharacteristicsAdder.class)
                .setCimVersion(16)
                .setTopologyKind(CgmesTopologyKind.BUS_BRANCH)
                .add();

        Network network2 = NetworkTest1Factory.create();
        network2.newExtension(CimCharacteristicsAdder.class)
                .setCimVersion(16)
                .setTopologyKind(CgmesTopologyKind.NODE_BREAKER)
                .add();

        network = MergingView.create("network", "test");
        network.merge(network1, network2);
    }

    @Test
    public void getExtensionTest() {
        CimCharacteristics mergedExt1 = network.getExtension(CimCharacteristics.class);
        assertNotNull(mergedExt1);
        assertEquals(16, mergedExt1.getCimVersion());
        assertEquals(CgmesTopologyKind.NODE_BREAKER, mergedExt1.getTopologyKind());
    }

    @Test
    public void getExtensionByNameTest() {
        CimCharacteristics mergedExt2 = network.getExtensionByName("cimCharacteristics");
        assertNotNull(mergedExt2);
        assertEquals(16, mergedExt2.getCimVersion());
        assertEquals(CgmesTopologyKind.NODE_BREAKER, mergedExt2.getTopologyKind());
    }
}
