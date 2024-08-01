/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 * Test that the new Var attributes and the digester rule changes work.
 */
public class VarTest extends AbstractCommonTest {

    /**
     * The key used to retrieve the set of validation rules from the xml file.
     */
    protected static final String FORM_KEY = "testForm";

    /**
     * The key used to retrieve the validator action.
     */
    protected static final String ACTION = "byte";

    /**
     * Load {@code ValidatorResources} from validator-multipletest.xml.
     */
    @BeforeEach
    protected void setUp() throws IOException, SAXException {
        // Load resources
        loadResources("VarTest-config.xml");
    }

    @AfterEach
    protected void tearDown() {
    }

    /**
     * With nothing provided, we should fail both because both are required.
     */
    @Test
    public void testVars() {

        final Form form = resources.getForm(Locale.getDefault(), FORM_KEY);

        // Get field 1
        final Field field1 = form.getField("field-1");
        assertNotNull(field1, "field-1 is null.");
        assertEquals("field-1", field1.getProperty(), "field-1 property is wrong");

        // Get var-1-1
        final Var var11 = field1.getVar("var-1-1");
        assertNotNull(var11, "var-1-1 is null.");
        assertEquals("var-1-1", var11.getName(), "var-1-1 name is wrong");
        assertEquals("value-1-1", var11.getValue(), "var-1-1 value is wrong");
        assertEquals("jstype-1-1", var11.getJsType(), "var-1-1 jstype is wrong");
        assertFalse(var11.isResource(), "var-1-1 resource is true");
        assertNull(var11.getBundle(), "var-1-1 bundle is not null.");

        // Get field 2
        final Field field2 = form.getField("field-2");
        assertNotNull(field2, "field-2 is null.");
        assertEquals("field-2", field2.getProperty(), "field-2 property is wrong");

        // Get var-2-1
        final Var var21 = field2.getVar("var-2-1");
        assertNotNull(var21, "var-2-1 is null.");
        assertEquals("var-2-1", var21.getName(), "var-2-1 name is wrong");
        assertEquals("value-2-1", var21.getValue(), "var-2-1 value is wrong");
        assertEquals("jstype-2-1", var21.getJsType(), "var-2-1 jstype is wrong");
        assertTrue(var21.isResource(), "var-2-1 resource is false");
        assertEquals(var21.getBundle(), "bundle-2-1", "var-2-1 bundle is wrong");

        // Get var-2-2
        final Var var22 = field2.getVar("var-2-2");
        assertNotNull(var22, "var-2-2 is null.");
        assertEquals(var22.getName(), "var-2-2", "var-2-2 name is wrong");
        assertEquals(var22.getValue(), "value-2-2", "var-2-2 value is wrong");
        assertNull(var22.getJsType(), "var-2-2 jstype is not null");
        assertFalse(var22.isResource(), "var-2-2 resource is true");
        assertEquals(var22.getBundle(), "bundle-2-2", "var-2-2 bundle is wrong");

    }

}