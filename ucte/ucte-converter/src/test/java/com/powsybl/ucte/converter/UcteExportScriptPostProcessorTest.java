/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.ucte.converter;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UcteExportScriptPostProcessorTest {

    private FileSystem fileSystem;

    @Before
    public void setUp() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void test() throws IOException {
        InMemoryPlatformConfig platformConfig = new InMemoryPlatformConfig(fileSystem);
        Path script = platformConfig.getConfigDir().resolve(UcteExportScriptPostProcessor.DEFAULT_SCRIPT_NAME);
        Files.copy(getClass().getResourceAsStream("/ucte-export-post-processor.groovy"), script);
        test(platformConfig);

        // Test with a custom script name
        script = platformConfig.getConfigDir().resolve("custom-script.groovy");
        Files.copy(getClass().getResourceAsStream("/ucte-export-post-processor.groovy"), script);
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("ucte-export-post-processor");
        moduleConfig.setStringProperty("script", script.toAbsolutePath().toString());
        test(platformConfig);
    }

    private void test(PlatformConfig platformConfig) {
        UcteExportScriptPostProcessor processor = new UcteExportScriptPostProcessor(platformConfig);
        assertEquals("ucteExportScript", processor.getName());

        try {
            processor.process(null, null);
            fail();
        } catch (NullPointerException ignored) {
        } catch (Exception e) {
            fail();
        }
    }
}
