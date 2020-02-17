/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.iidm.mergingview;

import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.StaticVarCompensator;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Thomas Adam <tadam at silicom.fr>
 */
public class StaticVarCompensatorAdapterTest {
    private MergingView mergingView;

    @Before
    public void setup() {
        mergingView = MergingView.create("StaticVarCompensatorAdapterTest", "iidm");
    }

    @Test
    public void testSetterGetter() {
        final String id = "SVC2";
        Network networkRef = SvcTestCaseFactory.createWithRemoteRegulatingTerminal();
        mergingView.merge(networkRef);

        final StaticVarCompensator svcExpected = networkRef.getStaticVarCompensator(id);
        final StaticVarCompensator svcActual = mergingView.getStaticVarCompensator(id);
        assertNotNull(svcActual);
        assertTrue(svcActual instanceof StaticVarCompensatorAdapter);
        assertSame(mergingView, svcActual.getNetwork());

        assertEquals(svcExpected.getType(), svcActual.getType());
        assertTrue(svcActual.getTerminal() instanceof TerminalAdapter);
        svcActual.getTerminals().forEach(t -> {
            assertTrue(t instanceof TerminalAdapter);
            assertNotNull(t);
        });

        double bMin = svcExpected.getBmin();
        assertEquals(bMin, svcActual.getBmin(), 0.0d);
        assertTrue(svcActual.setBmin(++bMin) instanceof StaticVarCompensatorAdapter);
        assertEquals(bMin, svcActual.getBmin(), 0.0d);

        double bMax = svcExpected.getBmax();
        assertEquals(bMax, svcActual.getBmax(), 0.0d);
        assertTrue(svcActual.setBmax(++bMax) instanceof StaticVarCompensatorAdapter);
        assertEquals(bMax, svcActual.getBmax(), 0.0d);

        double voltageSetPoint = svcExpected.getVoltageSetPoint();
        assertEquals(voltageSetPoint, svcActual.getVoltageSetPoint(), 0.0d);
        assertTrue(svcActual.setVoltageSetPoint(++voltageSetPoint) instanceof StaticVarCompensatorAdapter);
        assertEquals(voltageSetPoint, svcActual.getVoltageSetPoint(), 0.0d);

        double reactivePowerSetPoint = svcExpected.getReactivePowerSetPoint();
        assertEquals(reactivePowerSetPoint, svcActual.getReactivePowerSetPoint(), 0.0d);
        assertTrue(svcActual.setReactivePowerSetPoint(++reactivePowerSetPoint) instanceof StaticVarCompensatorAdapter);
        assertEquals(reactivePowerSetPoint, svcActual.getReactivePowerSetPoint(), 0.0d);

        StaticVarCompensator.RegulationMode regulationMode = svcExpected.getRegulationMode();
        assertEquals(regulationMode, svcActual.getRegulationMode());
        regulationMode = StaticVarCompensator.RegulationMode.VOLTAGE;
        assertTrue(svcActual.setRegulationMode(regulationMode) instanceof StaticVarCompensatorAdapter);
        assertEquals(regulationMode, svcActual.getRegulationMode());

        assertTrue(svcActual.getRegulatingTerminal() instanceof TerminalAdapter);
        assertEquals(svcActual.getRegulatingTerminal(), mergingView.getLoad("L2").getTerminal());
        assertTrue(svcActual.setRegulatingTerminal(mergingView.getLine("L1").getTerminal1()) instanceof StaticVarCompensatorAdapter);
        assertEquals(svcActual.getRegulatingTerminal(), mergingView.getLine("L1").getTerminal1());

        svcActual.remove();
        assertNull(mergingView.getStaticVarCompensator(id));
        assertNull(networkRef.getStaticVarCompensator(id));
    }
}
