/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.psse.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 * @author José Antonio Marqués <marquesja at aia.es>
 */
public class BusData extends BlockData {

    BusData(PsseVersion psseVersion) {
        super(psseVersion);
    }

    BusData(PsseVersion psseVersion, PsseFileFormat psseFileFormat) {
        super(psseVersion, psseFileFormat);
    }

    List<PsseBus> read(BufferedReader reader, PsseContext context) throws IOException {
        assertMinimumExpectedVersion(PsseBlockData.BusData, PsseVersion.VERSION_33);

        String[] headers = busDataHeaders();
        List<String> records = readRecordBlock(reader);

        context.setBusDataReadFields(readFields(records, headers, context.getDelimiter()));
        return parseRecordsHeader(records, PsseBus.class, headers);
    }

    List<PsseBus> read(JsonNode networkNode, PsseContext context) throws IOException {
        assertMinimumExpectedVersion(PsseBlockData.BusData, PsseVersion.VERSION_35, PsseFileFormat.FORMAT_RAWX);

        JsonNode busNode = networkNode.get("bus");
        if (busNode == null) {
            return new ArrayList<PsseBus>();
        }

        String[] headers = nodeFields(busNode);
        List<String> records = nodeRecords(busNode);

        context.setBusDataReadFields(headers);
        return parseRecordsHeader(records, PsseBus.class, headers);
    }

    private static String[] busDataHeaders() {
        return new String[] {"i", "name", "baskv", "ide", "area", "zone", "owner", "vm", "va", "nvhi", "nvlo", "evhi", "evlo"};
    }
}
