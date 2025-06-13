/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.validator;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Tests entity imports.
 */
class EntityImportTest extends AbstractCommonTest {

    /**
     * Tests the entity import loading the {@code byteForm} form.
     */
    @Test
    void testEntityImport() throws Exception {
        final URL url = getClass().getResource("EntityImportTest-config.xml");
        final ValidatorResources resources = new ValidatorResources(url.toExternalForm());
        assertNotNull(resources.getForm(Locale.getDefault(), "byteForm"), "Form should be found");
    }

    /**
     * Tests loading ValidatorResources from a URL
     */
    @Test
    void testParseURL() throws Exception {
        final URL url = getClass().getResource("EntityImportTest-config.xml");
        final ValidatorResources resources = new ValidatorResources(url);
        assertNotNull(resources.getForm(Locale.getDefault(), "byteForm"), "Form should be found");
    }
}
