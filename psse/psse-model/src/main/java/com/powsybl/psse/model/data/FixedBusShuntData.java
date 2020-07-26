/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.psse.model.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.powsybl.psse.model.PsseConstants.PsseFileFormat;
import com.powsybl.psse.model.PsseConstants.PsseVersion;
import com.powsybl.psse.model.PsseContext;
import com.powsybl.psse.model.PsseFixedShunt;
import com.powsybl.psse.model.PsseRawModel;

/**
 *
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 * @author José Antonio Marqués <marquesja at aia.es>
 */
class FixedBusShuntData extends BlockData {

    public FixedBusShuntData(PsseVersion psseVersion) {
        super(psseVersion);
    }

    FixedBusShuntData(PsseVersion psseVersion, PsseFileFormat psseFileFormat) {
        super(psseVersion, psseFileFormat);
    }

    List<PsseFixedShunt> read(BufferedReader reader, PsseContext context) throws IOException {
        assertMinimumExpectedVersion(PsseBlockData.FIXED_BUS_SHUNT_DATA, PsseVersion.VERSION_33);

        List<String> records = readRecordBlock(reader);
        String[] headers = fixedBusShuntDataHeaders(this.getPsseVersion());
        context.setFixedBusShuntDataReadFields(readFields(records, headers, context.getDelimiter()));

        return parseRecordsHeader(records, PsseFixedShunt.class, headers);
    }

    List<PsseFixedShunt> readx(JsonNode networkNode, PsseContext context) {
        assertMinimumExpectedVersion(PsseBlockData.FIXED_BUS_SHUNT_DATA, PsseVersion.VERSION_35, PsseFileFormat.FORMAT_RAWX);

        JsonNode fixedShuntNode = networkNode.get("fixshunt");
        if (fixedShuntNode == null) {
            return new ArrayList<>();
        }

        String[] headers = nodeFields(fixedShuntNode);
        List<String> records = nodeRecords(fixedShuntNode);

        context.setFixedBusShuntDataReadFields(headers);
        return parseRecordsHeader(records, PsseFixedShunt.class, headers);
    }

    void write(PsseRawModel model, PsseContext context, OutputStream outputStream) {
        assertMinimumExpectedVersion(PsseBlockData.FIXED_BUS_SHUNT_DATA, PsseVersion.VERSION_33);

        String[] headers = context.getFixedBusShuntDataReadFields();
        BlockData.<PsseFixedShunt>writeBlock(PsseFixedShunt.class, model.getFixedShunts(), headers,
            BlockData.quoteFieldsInsideHeaders(fixedBusShuntDataQuoteFields(this.getPsseVersion()), headers),
            context.getDelimiter().charAt(0), outputStream);
        BlockData.writeEndOfBlockAndComment("END OF FIXED SHUNT DATA, BEGIN GENERATOR DATA", outputStream);
    }

    static String[] fixedBusShuntDataHeaders(PsseVersion version) {
        if (version == PsseVersion.VERSION_35) {
            return new String[] {"ibus", "shntid", "stat", "gl", "bl"};
        } else {
            return new String[] {"i", "id", "status", "gl", "bl"};
        }
    }

    static String[] fixedBusShuntDataQuoteFields(PsseVersion version) {
        if (version == PsseVersion.VERSION_35) {
            return new String[] {"shntid"};
        } else {
            return new String[] {"id"};
        }
    }
}
