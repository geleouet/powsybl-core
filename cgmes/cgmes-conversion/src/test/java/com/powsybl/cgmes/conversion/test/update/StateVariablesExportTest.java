/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.cgmes.conversion.test.update;

import com.powsybl.cgmes.conformity.test.CgmesConformity1Catalog;
import com.powsybl.cgmes.conversion.CgmesExport;
import com.powsybl.cgmes.conversion.CgmesImport;
import com.powsybl.cgmes.conversion.update.CgmesExportContext;
import com.powsybl.cgmes.conversion.update.StateVariablesExport;
import com.powsybl.cgmes.model.CgmesOnDataSource;
import com.powsybl.commons.AbstractConverterTest;
import com.powsybl.commons.datasource.ReadOnlyDataSource;
import com.powsybl.computation.DefaultComputationManagerConfig;
import com.powsybl.iidm.import_.ImportConfig;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.NetworkFactory;
import com.powsybl.iidm.xml.NetworkXml;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.assertEquals;

/**
 * @author Miora Ralambotiana <miora.ralambotiana at rte-france.com>
 */
public class StateVariablesExportTest extends AbstractConverterTest {

    private static final String CONFORMITY_DIR = "/conformity/cas-1.1.3-data-4.0.3/";

    @Test
    public void microGridBE() throws IOException, XMLStreamException {
        String microGridDir = "MicroGrid/BaseCase/";
        String microGridBeDir = microGridDir + "CGMES_v2.4.15_MicroGridTestConfiguration_BC_BE_v2/";
        String microGridBd = microGridDir + "CGMES_v2.4.15_MicroGridTestConfiguration_BD_v2/";
        test(CgmesConformity1Catalog.microGridBaseCaseBE().dataSource(),
                CONFORMITY_DIR + microGridBeDir + "MicroGridTestConfiguration_BC_BE_EQ_V2.xml",
                CONFORMITY_DIR + microGridBeDir + "MicroGridTestConfiguration_BC_BE_TP_V2.xml",
                CONFORMITY_DIR + microGridBeDir + "MicroGridTestConfiguration_BC_BE_SSH_V2.xml",
                CONFORMITY_DIR + microGridBd + "MicroGridTestConfiguration_EQ_BD.xml",
                CONFORMITY_DIR + microGridBd + "MicroGridTestConfiguration_TP_BD.xml",
                2);
    }

    @Test
    public void smallGrid() throws IOException, XMLStreamException {
        String smallGridDir = "SmallGrid/BusBranch/";
        String smallGridBaseDir = smallGridDir + "CGMES_v2.4.15_SmallGridTestConfiguration_BaseCase_Complete_v3.0.0/";
        String smallGridBd = smallGridDir + "CGMES_v2.4.15_SmallGridTestConfiguration_Boundary_v3.0.0/";
        test(CgmesConformity1Catalog.smallBusBranch().dataSource(),
                CONFORMITY_DIR + smallGridBaseDir + "SmallGridTestConfiguration_BC_EQ_v3.0.0.xml",
                CONFORMITY_DIR + smallGridBaseDir + "SmallGridTestConfiguration_BC_TP_v3.0.0.xml",
                CONFORMITY_DIR + smallGridBaseDir + "SmallGridTestConfiguration_BC_SSH_v3.0.0.xml",
                CONFORMITY_DIR + smallGridBd + "SmallGridTestConfiguration_EQ_BD_v3.0.0.xml",
                CONFORMITY_DIR + smallGridBd + "SmallGridTestConfiguration_TP_BD_v3.0.0.xml",
                4);
    }

    private void test(ReadOnlyDataSource ds, String eq, String tp, String ssh, String eqBd, String tpBd, int svVersion) throws IOException, XMLStreamException {
        check(eq, getName(ds, this::eq));
        check(tp, getName(ds, this::tp));
        check(ssh, getName(ds, this::ssh));
        check(eqBd, getName(ds, this::eqBd));
        check(tpBd, getName(ds, this::tpBd));
        testxxx(ds, eq, tp, ssh, eqBd, tpBd, svVersion);
    }

    private void check(String expectedPathname, String actual) {
        Path expectedPath = Paths.get(expectedPathname);
        String expected = expectedPath.getName(expectedPath.getNameCount() - 1).toString();
        assertEquals(expected, actual);
    }

    private boolean eq(String name) {
        return !name.contains("_BD") && name.contains("_EQ");
    }

    private boolean tp(String name) {
        return !name.contains("_BD") && name.contains("_TP");
    }

    private boolean ssh(String name) {
        return name.contains("_SSH");
    }

    private boolean eqBd(String name) {
        return name.contains("_EQ") && name.contains("_BD");
    }

    private boolean tpBd(String name) {
        return name.contains("_TP") && name.contains("_BD");
    }

    private String getName(ReadOnlyDataSource ds, Predicate<String> file) {
        CgmesOnDataSource ns = new CgmesOnDataSource(ds);
        return ns.names().stream().filter(n -> file.test(n)).findFirst().get();
    }

    private InputStream newInputStream(ReadOnlyDataSource ds, Predicate<String> file) throws IOException {
        return ds.newInputStream(getName(ds, file));
    }

    private void testxxx(ReadOnlyDataSource dataSource, String eq, String tp, String ssh, String eqBd, String tpBd, int svVersion) throws IOException, XMLStreamException {
        Properties properties = new Properties();
        properties.put("iidm.import.cgmes.profile-used-for-initial-state-values", "SV");

        // Import original
        Network expected = new CgmesImport().importData(dataSource, NetworkFactory.findDefault(), properties);

        // Export SV
        Path test = tmpDir.resolve("test.xml");
        try (OutputStream os = Files.newOutputStream(test)) {
            XMLStreamWriter writer = CgmesExport.initializeWriter(os);
            CgmesExportContext context = new CgmesExportContext(expected);
            context.getSvModelDescription().setVersion(svVersion);
            StateVariablesExport.write(expected, writer, context);
        }

        // Zip with new SV
        try (OutputStream fos = Files.newOutputStream(tmpDir.resolve("repackaged.zip"));
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            try (InputStream eqIs = newInputStream(dataSource, this::eq)) {
                zipFile("EQ.xml", eqIs, zipOut);
            }
            try (InputStream tpIs = newInputStream(dataSource, this::tp)) {
                zipFile("TP.xml", tpIs, zipOut);
            }
            try (InputStream sshIs = newInputStream(dataSource, this::ssh)) {
                zipFile("SSH.xml", sshIs, zipOut);
            }
            try (InputStream svIs = Files.newInputStream(test)) {
                zipFile("SV.xml", svIs, zipOut);
            }
            try (InputStream eqBdIs = newInputStream(dataSource, this::eqBd)) {
                zipFile("EQ_BD.xml", eqBdIs, zipOut);
            }
            try (InputStream tpBdIs = newInputStream(dataSource, this::tpBd)) {
                zipFile("TP_BD.xml", tpBdIs, zipOut);
            }
        }

        // Import with new SV
        Network actual = Importers.loadNetwork(tmpDir.resolve("repackaged.zip"),
                DefaultComputationManagerConfig.load().createShortTimeExecutionComputationManager(), ImportConfig.load(), properties);

        // Export original and with new SV
        NetworkXml.writeAndValidate(expected, tmpDir.resolve("expected.xml"));
        NetworkXml.writeAndValidate(actual, tmpDir.resolve("actual.xml"));

        // Compare
        try (InputStream expIs = Files.newInputStream(tmpDir.resolve("expected.xml"));
             InputStream actIs = Files.newInputStream(tmpDir.resolve("actual.xml"))) {
            compareXmlWithDelta(expIs, actIs);
        }
    }

    private static void zipFile(String entryName, InputStream toZip, ZipOutputStream zipOut) throws IOException {
        ZipEntry zipEntry = new ZipEntry(entryName);
        zipOut.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = toZip.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
    }

    private static void compareXmlWithDelta(InputStream expected, InputStream actual) {
        Source control = Input.fromStream(expected).build();
        Source test = Input.fromStream(actual).build();
        Diff myDiff = DiffBuilder.compare(control).withTest(test).ignoreWhitespace().ignoreComments().build();
        for (Difference diff : myDiff.getDifferences()) {
            if (diff.getComparison().getControlDetails().getXPath().endsWith("forecastDistance")) {
                continue;
            }
            debugComparison(diff.getComparison(), diff.getResult());
            double exp = Double.parseDouble((String) diff.getComparison().getControlDetails().getValue());
            double act = Double.parseDouble((String) diff.getComparison().getTestDetails().getValue());
            assertEquals(exp, act, getDelta(diff.getComparison().getControlDetails().getXPath()));
        }
    }

    private static double getDelta(String xPath) {
        if (xPath.contains("danglingLine")) {
            if (xPath.endsWith("p") || xPath.endsWith("q")) {
                return 1e-1;
            }
            return 1e-5;
        }
        return 0.0;
    }

    private static void debugComparison(Comparison comparison, ComparisonResult comparisonResult) {
        if (comparisonResult.equals(ComparisonResult.DIFFERENT)) {
            LOG.error("comparison {}", comparison.getType());
            LOG.error("    control {}", comparison.getControlDetails().getXPath());
            debugNode(comparison.getControlDetails().getTarget());
            LOG.error("    test    {}", comparison.getTestDetails().getXPath());
            debugNode(comparison.getTestDetails().getTarget());
            LOG.error("    result  {}", comparisonResult);
        }
    }

    private static void debugNode(Node n) {
        if (n != null) {
            debugAttributes(n, "            ");
            int maxNodes = 5;
            for (int k = 0; k < maxNodes && k < n.getChildNodes().getLength(); k++) {
                Node n1 = n.getChildNodes().item(k);
                LOG.error("            {} {}", n1.getLocalName(), n1.getTextContent());
                debugAttributes(n1, "                ");
            }
            if (n.getChildNodes().getLength() > maxNodes) {
                LOG.error("            ...");
            }
        }
    }

    private static void debugAttributes(Node n, String indent) {
        if (n.getAttributes() != null) {
            debugAttribute(n, CgmesExport.RDF_NAMESPACE, "resource", indent);
            debugAttribute(n, CgmesExport.RDF_NAMESPACE, "about", indent);
        }
    }

    private static void debugAttribute(Node n, String namespace, String localName, String indent) {
        Node a = n.getAttributes().getNamedItemNS(namespace, localName);
        if (a != null) {
            LOG.error("{}{} = {}", indent, localName, a.getTextContent());
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(StateVariablesExport.class);
}
