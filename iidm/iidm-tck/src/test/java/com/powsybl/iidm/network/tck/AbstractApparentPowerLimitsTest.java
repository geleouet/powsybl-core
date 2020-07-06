/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.iidm.network.tck;

import com.powsybl.iidm.network.ApparentPowerLimits;
import com.powsybl.iidm.network.LimitType;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Miora Ralambotiana <miora.ralambotiana at rte-france.com>
 */
public abstract class AbstractApparentPowerLimitsTest {

    private static Network createNetwork() {
        Network network = EurostagTutorialExample1Factory.create();
        Line line = network.getLine("NHV1_NHV2_2");
        line.newApparentPowerLimits1()
                .setPermanentLimit(300)
                .beginTemporaryLimit()
                .setName("20'")
                .setAcceptableDuration(20 * 60)
                .setValue(320)
                .endTemporaryLimit()
                .beginTemporaryLimit()
                .setName("5'")
                .setAcceptableDuration(5 * 60)
                .setValue(350)
                .endTemporaryLimit()
                .add();
        line.newApparentPowerLimits2()
                .setPermanentLimit(310)
                .add();
        return network;
    }

    @Test
    public void test() {
        Network network = createNetwork();
        Line l = network.getLine("NHV1_NHV2_2");

        // limits1
        assertFalse(l.getOperationalLimits1().isEmpty());
        ApparentPowerLimits apparentPowerLimits1 = l.getOperationalLimits1(LimitType.APPARENT_POWER, ApparentPowerLimits.class);
        assertNotNull(apparentPowerLimits1);
        assertEquals(300, apparentPowerLimits1.getPermanentLimit(), 0.0);
        assertEquals(2, apparentPowerLimits1.getTemporaryLimits().size());
        assertEquals("20'", apparentPowerLimits1.getTemporaryLimit(20 * 60).getName());
        assertEquals(320.0, apparentPowerLimits1.getTemporaryLimit(20 * 60).getValue(), 0.0);
        assertEquals("5'", apparentPowerLimits1.getTemporaryLimit(5 * 60).getName());
        assertEquals(350, apparentPowerLimits1.getTemporaryLimit(5 * 60).getValue(), 0.0);

        // limits2
        assertFalse(l.getOperationalLimits2().isEmpty());
        ApparentPowerLimits apparentPowerLimits2 = l.getOperationalLimits2(LimitType.APPARENT_POWER, ApparentPowerLimits.class);
        assertNotNull(apparentPowerLimits2);
        assertEquals(310, apparentPowerLimits2.getPermanentLimit(), 0.0);
        assertTrue(apparentPowerLimits2.getTemporaryLimits().isEmpty());
    }
}