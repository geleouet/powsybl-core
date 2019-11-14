/**
 * Copyright (c) 2017-2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.cgmes.conversion.elements;

import java.util.Map;

import com.powsybl.cgmes.conversion.Context;
import com.powsybl.cgmes.conversion.Conversion;
import com.powsybl.cgmes.model.CgmesNames;
import com.powsybl.triplestore.api.PropertyBag;
import com.powsybl.triplestore.api.PropertyBags;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 * @author José Antonio Marqués <marquesja at aia.es>
 */
public class NewTwoWindingsTransformerConversion extends AbstractTransformerConversion {

    public NewTwoWindingsTransformerConversion(PropertyBags ends,
        Map<String, PropertyBag> powerTransformerRatioTapChanger,
        Map<String, PropertyBag> powerTransformerPhaseTapChanger, Context context) {
        super(STRING_POWER_TRANSFORMER, ends, context);
        this.powerTransformerRatioTapChanger = powerTransformerRatioTapChanger;
        this.powerTransformerPhaseTapChanger = powerTransformerPhaseTapChanger;
    }

    @Override
    public boolean valid() {
        if (!super.valid()) {
            return false;
        }
        if (context.boundary().containsNode(nodeId(1))
            || context.boundary().containsNode(nodeId(2))) {
            invalid("2 windings transformer end point at boundary is not supported");
            return false;
        }
        return true;
    }

    @Override
    public void convert() {
        CgmesT2xModel cgmesT2xModel = load();
        InterpretedModel interpretedModel = interpret(cgmesT2xModel, context.config());
    }

    private CgmesT2xModel load() {
        // ends = ps
        PropertyBag end1 = ps.get(0);
        PropertyBag end2 = ps.get(1);

        double x1 = end1.asDouble(STRING_X);
        double x2 = end2.asDouble(STRING_X);
        double r = end1.asDouble(STRING_R) + end2.asDouble(STRING_R);
        double x = x1 + x2;

        String terminal1 = end1.getId(CgmesNames.TERMINAL);
        String terminal2 = end2.getId(CgmesNames.TERMINAL);

        PropertyBag rtc1 = getTransformerTapChanger(end1, STRING_RATIO_TAP_CHANGER, powerTransformerRatioTapChanger);
        PropertyBag ptc1 = getTransformerTapChanger(end1, STRING_PHASE_TAP_CHANGER, powerTransformerPhaseTapChanger);
        PropertyBag rtc2 = getTransformerTapChanger(end2, STRING_RATIO_TAP_CHANGER, powerTransformerRatioTapChanger);
        PropertyBag ptc2 = getTransformerTapChanger(end2, STRING_PHASE_TAP_CHANGER, powerTransformerPhaseTapChanger);

        double ratedU1 = end1.asDouble(STRING_RATEDU);
        double ratedU2 = end2.asDouble(STRING_RATEDU);

        TapChangerConversion ratioTapChanger1 = getRatioTapChanger(rtc1);
        TapChangerConversion ratioTapChanger2 = getRatioTapChanger(rtc2);
        TapChangerConversion phaseTapChanger1 = getPhaseTapChanger(ptc1, x);
        TapChangerConversion phaseTapChanger2 = getPhaseTapChanger(ptc2, x);

        CgmesT2xModel cgmesT2xModel = new CgmesT2xModel();
        cgmesT2xModel.end1.g = end1.asDouble(STRING_G, 0);
        cgmesT2xModel.end1.b = end1.asDouble(STRING_B);
        cgmesT2xModel.end1.ratioTapChanger = ratioTapChanger1;
        cgmesT2xModel.end1.phaseTapChanger = phaseTapChanger1;
        cgmesT2xModel.end1.ratedU = ratedU1;
        cgmesT2xModel.end1.phaseAngleClock = end1.asInt(STRING_PHASE_ANGLE_CLOCK, 0);
        cgmesT2xModel.end1.terminal = terminal1;

        if (x1 == 0.0) {
            cgmesT2xModel.end1.xIsZero = true;
        } else {
            cgmesT2xModel.end1.xIsZero = false;
        }
        cgmesT2xModel.end1.rtcDefined = rtc1 != null && rtc1.asDouble(STRING_STEP_VOLTAGE_INCREMENT) != 0.0;

        cgmesT2xModel.end2.g = end2.asDouble(STRING_G, 0);
        cgmesT2xModel.end2.b = end2.asDouble(STRING_B);
        cgmesT2xModel.end2.ratioTapChanger = ratioTapChanger2;
        cgmesT2xModel.end2.phaseTapChanger = phaseTapChanger2;
        cgmesT2xModel.end2.ratedU = ratedU2;
        cgmesT2xModel.end2.phaseAngleClock = end2.asInt(STRING_PHASE_ANGLE_CLOCK, 0);
        cgmesT2xModel.end2.terminal = terminal2;

        if (x2 == 0.0) {
            cgmesT2xModel.end2.xIsZero = true;
        } else {
            cgmesT2xModel.end2.xIsZero = false;
        }
        cgmesT2xModel.end2.rtcDefined = rtc2 != null && rtc2.asDouble(STRING_STEP_VOLTAGE_INCREMENT) != 0.0;

        cgmesT2xModel.r = r;
        cgmesT2xModel.x = x;

        return cgmesT2xModel;
    }

    private InterpretedModel interpret(CgmesT2xModel cgmesModel, Conversion.Config alternative) {

        TapChangerAll interpretedTapChanger = ratioPhaseAlternative(cgmesModel, alternative);
        ShuntAll interpretedShunt = shuntAlternative(cgmesModel, alternative);

        PhaseAngleClockAll interpretedClock = phaseAngleClockAlternative(cgmesModel, alternative);
        boolean ratio0AtEnd2 = ratio0Alternative(cgmesModel, alternative);

        InterpretedModel interpretedModel = new InterpretedModel();
        interpretedModel.r = cgmesModel.r;
        interpretedModel.x = cgmesModel.x;

        interpretedModel.end1.g = interpretedShunt.g1;
        interpretedModel.end1.b = interpretedShunt.b1;
        interpretedModel.end1.ratioTapChanger = interpretedTapChanger.ratioTapChanger1;
        interpretedModel.end1.phaseTapChanger = interpretedTapChanger.phaseTapChanger1;
        interpretedModel.end1.ratedU = cgmesModel.end1.ratedU;
        interpretedModel.end1.terminal = cgmesModel.end1.terminal;
        interpretedModel.end1.phaseAngleClock = interpretedClock.phaseAngleClock1;

        interpretedModel.end2.g = interpretedShunt.g2;
        interpretedModel.end2.b = interpretedShunt.b2;
        interpretedModel.end2.ratioTapChanger = interpretedTapChanger.ratioTapChanger2;
        interpretedModel.end2.phaseTapChanger = interpretedTapChanger.phaseTapChanger2;
        interpretedModel.end2.ratedU = cgmesModel.end2.ratedU;
        interpretedModel.end2.terminal = cgmesModel.end2.terminal;
        interpretedModel.end2.phaseAngleClock = interpretedClock.phaseAngleClock2;

        interpretedModel.ratio0AtEnd2 = ratio0AtEnd2;

        return interpretedModel;
    }

    private TapChangerAll ratioPhaseAlternative(CgmesT2xModel cgmesModel, Conversion.Config alternative) {
        TapChangerConversion ratioTapChanger1 = null;
        TapChangerConversion phaseTapChanger1 = null;
        TapChangerConversion ratioTapChanger2 = null;
        TapChangerConversion phaseTapChanger2 = null;

        if (alternative.isXfmr2RatioPhaseEnd1()) {
            ratioTapChanger1 = combineTapChangers(cgmesModel.end1.ratioTapChanger, cgmesModel.end2.ratioTapChanger);
            phaseTapChanger1 = combineTapChangers(cgmesModel.end1.phaseTapChanger, cgmesModel.end2.phaseTapChanger);
        } else if (alternative.isXfmr2RatioPhaseEnd2()) {
            ratioTapChanger2 = combineTapChangers(cgmesModel.end2.ratioTapChanger, cgmesModel.end1.ratioTapChanger);
            phaseTapChanger2 = combineTapChangers(cgmesModel.end2.phaseTapChanger, cgmesModel.end1.phaseTapChanger);
        } else if (alternative.isXfmr2RatioPhaseEnd1End2()) {
            ratioTapChanger1 = cgmesModel.end1.ratioTapChanger;
            phaseTapChanger1 = cgmesModel.end1.phaseTapChanger;
            ratioTapChanger2 = cgmesModel.end2.ratioTapChanger;
            phaseTapChanger2 = cgmesModel.end2.phaseTapChanger;
        } else {
            if (cgmesModel.end1.xIsZero) {
                ratioTapChanger1 = combineTapChangers(cgmesModel.end1.ratioTapChanger, cgmesModel.end2.ratioTapChanger);
                phaseTapChanger1 = combineTapChangers(cgmesModel.end1.phaseTapChanger, cgmesModel.end2.phaseTapChanger);
            } else {
                ratioTapChanger2 = combineTapChangers(cgmesModel.end2.ratioTapChanger, cgmesModel.end1.ratioTapChanger);
                phaseTapChanger2 = combineTapChangers(cgmesModel.end2.phaseTapChanger, cgmesModel.end1.phaseTapChanger);
            }
        }

        if (alternative.isXfmr2Phase1Negate()) {
            negatePhaseTapChanger(phaseTapChanger1);
        }
        if (alternative.isXfmr2Phase2Negate()) {
            negatePhaseTapChanger(phaseTapChanger2);
        }

        TapChangerAll tapChanger22 = new TapChangerAll();
        tapChanger22.ratioTapChanger1 = ratioTapChanger1;
        tapChanger22.phaseTapChanger1 = phaseTapChanger1;
        tapChanger22.ratioTapChanger2 = ratioTapChanger2;
        tapChanger22.phaseTapChanger2 = phaseTapChanger2;

        return tapChanger22;
    }

    private ShuntAll shuntAlternative(CgmesT2xModel cgmesModel, Conversion.Config alternative) {
        double g1 = 0.0;
        double b1 = 0.0;
        double g2 = 0.0;
        double b2 = 0.0;
        if (alternative.isXfmr2ShuntEnd1()) {
            g1 = cgmesModel.end1.g + cgmesModel.end2.g;
            b1 = cgmesModel.end1.b + cgmesModel.end2.b;
        } else if (alternative.isXfmr2ShuntEnd2()) {
            g2 = cgmesModel.end1.g + cgmesModel.end2.g;
            b2 = cgmesModel.end1.b + cgmesModel.end2.b;
        } else if (alternative.isXfmr2ShuntEnd1End2()) {
            g1 = cgmesModel.end1.g;
            b1 = cgmesModel.end1.b;
            g2 = cgmesModel.end2.g;
            b2 = cgmesModel.end2.b;
        } else {
            g1 = (cgmesModel.end1.g + cgmesModel.end2.g) * 0.5;
            b1 = (cgmesModel.end1.b + cgmesModel.end2.b) * 0.5;
            g2 = (cgmesModel.end1.g + cgmesModel.end2.g) * 0.5;
            b2 = (cgmesModel.end1.b + cgmesModel.end2.b) * 0.5;
        }

        ShuntAll shunt22 = new ShuntAll();
        shunt22.g1 = g1;
        shunt22.b1 = b1;
        shunt22.g2 = g2;
        shunt22.b2 = b2;

        return shunt22;
    }

    private PhaseAngleClockAll phaseAngleClockAlternative(CgmesT2xModel cgmesModel, Conversion.Config alternative) {
        int phaseAngleClock1 = 0;
        int phaseAngleClock2 = 0;

        if (alternative.isXfmr2PhaseAngleClockEnd1End2()) {
            if (cgmesModel.end1.phaseAngleClock != 0) {
                if (alternative.isXfmr2PhaseAngleClock1Negate()) {
                    phaseAngleClock2 = cgmesModel.end1.phaseAngleClock;
                } else {
                    phaseAngleClock1 = cgmesModel.end1.phaseAngleClock;
                }
            }
            if (cgmesModel.end2.phaseAngleClock != 0) {
                if (alternative.isXfmr2PhaseAngleClock2Negate()) {
                    phaseAngleClock1 = cgmesModel.end2.phaseAngleClock;
                } else {
                    phaseAngleClock2 = cgmesModel.end2.phaseAngleClock;
                }
            }
        }

        PhaseAngleClockAll phaseAngleClock02 = new PhaseAngleClockAll();
        phaseAngleClock02.phaseAngleClock1 = phaseAngleClock1;
        phaseAngleClock02.phaseAngleClock2 = phaseAngleClock2;

        return phaseAngleClock02;
    }

    private boolean ratio0Alternative(CgmesT2xModel cgmesModel, Conversion.Config alternative) {
        if (cgmesModel.end1.ratedU == cgmesModel.end2.ratedU) {
            return false;
        }

        boolean ratio0AtEnd2 = false;
        if (alternative.isXfmr2Ratio0End1()) {
            ratio0AtEnd2 = false;
        } else if (alternative.isXfmr2Ratio0End2()) {
            ratio0AtEnd2 = true;
        } else if (alternative.isXfmr2Ratio0Rtc()) {
            if (cgmesModel.end1.rtcDefined) {
                ratio0AtEnd2 = false;
            } else {
                ratio0AtEnd2 = true;
            }
        } else {
            if (cgmesModel.end1.xIsZero) {
                ratio0AtEnd2 = false;
            } else {
                ratio0AtEnd2 = true;
            }
        }
        return ratio0AtEnd2;
    }

    static class CgmesT2xModel {
        double r;
        double x;
        CgmesEnd end1 = new CgmesEnd();
        CgmesEnd end2 = new CgmesEnd();
    }

    static class CgmesEnd {
        double g;
        double b;
        TapChangerConversion ratioTapChanger;
        TapChangerConversion phaseTapChanger;
        double ratedU;
        int phaseAngleClock;
        String terminal;
        boolean xIsZero;
        boolean rtcDefined;
    }

    static class InterpretedModel {
        double r;
        double x;
        InterpretedEnd end1 = new InterpretedEnd();
        InterpretedEnd end2 = new InterpretedEnd();
        boolean ratio0AtEnd2;
    }

    static class InterpretedEnd {
        double g;
        double b;
        TapChangerConversion ratioTapChanger;
        TapChangerConversion phaseTapChanger;
        double ratedU;
        String terminal;
        int phaseAngleClock;
    }

    private final Map<String, PropertyBag> powerTransformerRatioTapChanger;
    private final Map<String, PropertyBag> powerTransformerPhaseTapChanger;
}
