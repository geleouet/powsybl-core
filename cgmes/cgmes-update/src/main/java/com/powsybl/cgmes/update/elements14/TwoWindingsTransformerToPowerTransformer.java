package com.powsybl.cgmes.update.elements14;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.powsybl.cgmes.model.CgmesModel;
import com.powsybl.cgmes.update.CgmesPredicateDetails;
import com.powsybl.cgmes.update.ConversionMapper;
import com.powsybl.cgmes.update.IidmChange;
import com.powsybl.iidm.network.TwoWindingsTransformer;
import com.powsybl.triplestore.api.PropertyBag;
import com.powsybl.triplestore.api.PropertyBags;

/**
 * For conversion onCreate of TwoWindingsTransformer we need to create
 * additional elements: End1 and End2, tapChangers, tables, if required. All
 * have distinct ID (Subject) and contain reference to the parent
 * PowerTransformer element.
 */
public class TwoWindingsTransformerToPowerTransformer implements ConversionMapper {

    public TwoWindingsTransformerToPowerTransformer(IidmChange change, CgmesModel cgmes) {
        this.change = change;
        this.cgmes = cgmes;
        this.currId = change.getIdentifiableId();
        this.newTwoWindingsTransformer = (TwoWindingsTransformer) change.getIdentifiable();
        this.name = newTwoWindingsTransformer.getName();
        this.idEnd1 = (getEndsId().get("idEnd1") != null) ? getEndsId().get("idEnd1")
            : currId.concat("_OR");
        this.idEnd2 = (getEndsId().get("idEnd2") != null) ? getEndsId().get("idEnd2")
            : currId.concat("_CL");
        this.idPHTC = getTapChangerId("PhaseTapChanger");
        this.idRTTC = getTapChangerId("RatioTapChanger");
        this.idPHTCTable = getTapChangerTableId(idPHTC, "PhaseTapChanger");
        this.idRTTCTable = getTapChangerTableId(idRTTC, "RatioTapChanger");
        this.idPHTCTablePoint = getTapChangerTablePointId(idPHTCTable, "PhaseTapChanger");
        this.idRTTCTablePoint = getTapChangerTablePointId(idRTTCTable, "RatioTapChanger");
    }

    @Override
    public Multimap<String, CgmesPredicateDetails> mapIidmToCgmesPredicates() {

        final Multimap<String, CgmesPredicateDetails> map = ArrayListMultimap.create();
        TwoWindingsTransformer newTwoWindingsTransformer = (TwoWindingsTransformer) change.getIdentifiable();
        String ptId = newTwoWindingsTransformer.getId();

        map.put("rdfType", new CgmesPredicateDetails("rdf:type", "_EQ", false, "cim:PowerTransformer"));

        String name = newTwoWindingsTransformer.getName();
        map.put("name", new CgmesPredicateDetails("cim:IdentifiedObject.name", "_EQ", false, name));

        String substationId = newTwoWindingsTransformer.getSubstation().getId();
        map.put("equipmentContainer", new CgmesPredicateDetails(
            "cim:Equipment.MemberOf_EquipmentContainer", "_EQ", true, substationId));

        /**
         * PowerTransformerEnd1
         */
        map.put("rdfTypeEnd1", new CgmesPredicateDetails("rdf:type", "_EQ", false, "cim:TransformerWinding", idEnd1));

        map.put("powerTransformerEnd1", new CgmesPredicateDetails(
            "cim:TransformerWinding.MemberOf_PowerTransformer", "_EQ", true, ptId, idEnd1));

        map.put("end1Type", new CgmesPredicateDetails(
            "cim:TransformerWinding.windingType", "_EQ", false, "cim:WindingType.primary", idEnd1));

        double r0 = newTwoWindingsTransformer.getR();
        double x0 = newTwoWindingsTransformer.getX();
        double b0 = newTwoWindingsTransformer.getB();
        double g0 = newTwoWindingsTransformer.getG();
        double r2 = 0;
        double x2 = 0;
        double b2 = 0;
        double g2 = 0;
        double ratedU1 = newTwoWindingsTransformer.getRatedU1();
        double ratedU2 = newTwoWindingsTransformer.getRatedU2();
        double rho0 = ratedU2 / ratedU1;
        double rho0Square = rho0 * rho0;
        double r1 = (r0 - r2) / rho0Square;
        double x1 = (x0 - x2) / rho0Square;
        double b1 = (b0 + b2) * rho0Square;
        double g1 = (g0 + g2) * rho0Square;
        
        if (!String.valueOf(b1).equals("NaN")) {
            map.put("b", new CgmesPredicateDetails(
                "cim:TransformerWinding.b", "_EQ", false, String.valueOf(b1), idEnd1));
        }

        if (!String.valueOf(r1).equals("NaN")) {
            map.put("r", new CgmesPredicateDetails(
                "cim:TransformerWinding.r", "_EQ", false, String.valueOf(r1), idEnd1));
        }

        if (!String.valueOf(x1).equals("NaN")) {
            map.put("x", new CgmesPredicateDetails(
                "cim:TransformerWinding.x", "_EQ", false, String.valueOf(x1), idEnd1));
        }

        if (!String.valueOf(g1).equals("NaN")) {
            map.put("g", new CgmesPredicateDetails(
                "cim:TransformerWinding.g", "_EQ", false, String.valueOf(g1), idEnd1));
        }

        if (!String.valueOf(ratedU1).equals("NaN")) {
            map.put("ratedU1", new CgmesPredicateDetails(
                "cim:TransformerWinding.ratedU", "_EQ", false, String.valueOf(ratedU1), idEnd1));
        }

        /**
         * PowerTransformerEnd2
         */
        map.put("rdfTypeEnd2", new CgmesPredicateDetails("rdf:type", "_EQ", false, "cim:TransformerWinding", idEnd2));

        map.put("powerTransformerEnd2", new CgmesPredicateDetails(
            "cim:TransformerWinding.MemberOf_PowerTransformer", "_EQ", true, ptId, idEnd2));

        map.put("end2Type", new CgmesPredicateDetails(
            "cim:TransformerWinding.windingType", "_EQ", false, "cim:WindingType.secondary", idEnd2));

        map.put("bEnd2", new CgmesPredicateDetails(
            "cim:TransformerWinding.b", "_EQ", false, String.valueOf(b2), idEnd2));

        map.put("rEnd2", new CgmesPredicateDetails(
            "cim:TransformerWinding.r", "_EQ", false, String.valueOf(r2), idEnd2));

        map.put("xEnd2", new CgmesPredicateDetails(
            "cim:TransformerWinding.x", "_EQ", false, String.valueOf(x2), idEnd2));

        map.put("gEnd2", new CgmesPredicateDetails(
            "cim:TransformerWinding.g", "_EQ", false, String.valueOf(g2), idEnd2));

        if (!String.valueOf(ratedU2).equals("NaN")) {
            map.put("ratedU2", new CgmesPredicateDetails("cim:TransformerWinding.ratedU", "_EQ", false,
                String.valueOf(ratedU2), idEnd2));
        }
        /**
         * PhaseTapChanger
         */
        if (newTwoWindingsTransformer.getPhaseTapChanger() != null) {
            PhaseTapChangerToPhaseTapChanger phtc = new PhaseTapChangerToPhaseTapChanger(change, cgmes);
            map.putAll(phtc.mapIidmToCgmesPredicates());
        }
        /**
         * RatioTapChanger
         */
        if (newTwoWindingsTransformer.getRatioTapChanger() != null) {
            RatioTapChangerToRatioTapChanger rttc = new RatioTapChangerToRatioTapChanger(change, cgmes);
            map.putAll(rttc.mapIidmToCgmesPredicates());
        }
        return map;
    }

    /**
     * Check if TransformerWinding elements already exist in grid, if yes - returns
     * the id.
     *
     */
    private Map<String, String> getEndsId() {
        PropertyBags transformerEnds = cgmes.transformerEnds();
        Map<String, String> ids = new HashMap<>();
        Iterator i = transformerEnds.iterator();

        while (i.hasNext()) {
            PropertyBag pb = (PropertyBag) i.next();
            String windingType = pb.get("windingType");
            if (pb.getId("PowerTransformer").equals(currId)
                && windingType.endsWith("primary")) {
                idEnd1 = pb.getId("TransformerWinding");
                ids.put("idEnd1", idEnd1);
            } else if (pb.getId("PowerTransformer").equals(currId)
                && windingType.endsWith("secondary")) {
                idEnd2 = pb.getId("TransformerWinding");
                ids.put("idEnd2", idEnd2);
            } else {
                continue;
            }
        }
        return ids;
    }

    /**
     * Check if TapChanger elements already exist in grid, if yes - returns the id.
     *
     */
    private String getTapChangerId(String tapChangerType) {
        PropertyBags tapChangers = (tapChangerType.equals("RatioTapChanger")) ? cgmes.ratioTapChangers()
            : cgmes.phaseTapChangers();
        Iterator i = tapChangers.iterator();

        while (i.hasNext()) {
            PropertyBag pb = (PropertyBag) i.next();
            if (pb.getId("TransformerWinding").equals(idEnd1)) {
                return pb.getId(tapChangerType);
            } else {
                continue;
            }
        }
        return (tapChangerType.equals("RatioTapChanger")) ? idEnd1.concat("_RTTC") : idEnd1.concat("_PHTC");
    }

    /**
     * TODO elena check cim14 might have no table Check if TapChangerTable elements
     * already exist in grid, if yes - returns the id. Otherwise, return
     * concatenated string.
     */
    private String getTapChangerTableId(String tapChangerId, String tapChangerType) {

        String propertyName = tapChangerType.concat("Table");
        PropertyBags tapChangers = (tapChangerType.equals("RatioTapChanger")) ? cgmes.ratioTapChangers()
            : cgmes.phaseTapChangers();
        Iterator i = tapChangers.iterator();
        while (i.hasNext()) {
            PropertyBag pb = (PropertyBag) i.next();
            if (pb.getId(tapChangerType).equals(tapChangerId) && pb.containsKey(propertyName)) {

                return pb.getId(propertyName);
            } else {
                continue;
            }
        }
        return tapChangerId.concat("_Table");
    }

    private String getTapChangerTablePointId(String tapChangerTableId, String tapChangerType) {

        String propertyName = tapChangerType.concat("TablePoint");
        PropertyBags phaseTapChangerTable = (tapChangerType.equals("RatioTapChanger"))
            ? cgmes.ratioTapChangerTable(idRTTCTable)
            : cgmes.phaseTapChangerTable(idPHTCTable);
        Iterator i = phaseTapChangerTable.iterator();
        while (i.hasNext()) {
            PropertyBag pb = (PropertyBag) i.next();
            if (pb.getId("step").equals("check")) {
                return pb.getId(propertyName);
            } else {
                continue;
            }
        }
        return tapChangerTableId.concat("_TablePoint").concat("step number");
    }

    private IidmChange change;
    private CgmesModel cgmes;
    TwoWindingsTransformer newTwoWindingsTransformer;
    String currId;
    String name;
    String idEnd1;
    String idEnd2;
    String idRTTC;
    String idPHTC;
    String idPHTCTable;
    String idRTTCTable;
    String idPHTCTablePoint;
    String idRTTCTablePoint;
}