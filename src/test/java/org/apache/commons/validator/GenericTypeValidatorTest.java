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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 * Performs Validation Test for type validations.
 */
class GenericTypeValidatorTest extends AbstractCommonTest {

    /**
     * The key used to retrieve the set of validation rules from the xml file.
     */
    protected static final String FORM_KEY = "typeForm";

    /**
     * The key used to retrieve the validator action.
     */
    protected static final String ACTION = "byte";

    /**
     * Tests the locale.
     */
    private Map<String, ?> localeTest(final TypeBean info, final Locale locale) throws ValidatorException {

        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, "typeLocaleForm");
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, info);
        validator.setParameter("java.util.Locale", locale);

        // Get results of the validation.
        // throws ValidatorException,
        // but we aren't catching for testing
        // since no validation methods we use
        // throw this
        final ValidatorResults results = validator.validate();

        assertNotNull(results, "Results are null.");

        final Map<String, ?> hResultValues = results.getResultValueMap();

        assertInstanceOf(Byte.class, hResultValues.get("byte"), "Expecting byte result to be an instance of Byte for locale: " + locale);
        assertInstanceOf(Short.class, hResultValues.get("short"), "Expecting short result to be an instance of Short for locale: " + locale);
        assertInstanceOf(Integer.class, hResultValues.get("integer"), "Expecting integer result to be an instance of Integer for locale: " + locale);
        assertInstanceOf(Long.class, hResultValues.get("long"), "Expecting long result to be an instance of Long for locale: " + locale);
        assertInstanceOf(Float.class, hResultValues.get("float"), "Expecting float result to be an instance of Float for locale: " + locale);
        assertInstanceOf(Double.class, hResultValues.get("double"), "Expecting double result to be an instance of Double for locale: " + locale);
        assertInstanceOf(Date.class, hResultValues.get("date"), "Expecting date result to be an instance of Date for locale: " + locale);

        for (final String key : hResultValues.keySet()) {
            final Object value = hResultValues.get(key);

            assertNotNull(value, "value ValidatorResults.getResultValueMap() should not be null for locale: " + locale);
        }
        return hResultValues;
    }

    /**
     * Load {@code ValidatorResources} from validator-type.xml.
     */
    @BeforeEach
    protected void setUp() throws IOException, SAXException {
        // Load resources
        loadResources("GenericTypeValidatorTest-config.xml");
    }

    @AfterEach
    protected void tearDown() {
    }

    /**
     * Tests the fr locale.
     */
    @Test
    void testFRLocale() throws ValidatorException {
        // Create bean to run test on.
        final TypeBean info = new TypeBean();
        info.setByte("12");
        info.setShort("-129");
        info.setInteger("1443");
        info.setLong("88000");
        info.setFloat("12,1555");
        info.setDouble("129,1551511111");
        info.setDate("21/12/2010");
        final Map<String, ?> map = localeTest(info, Locale.FRENCH);
        assertEquals(12, ((Float) map.get("float")).intValue(), "float value not correct");
        assertEquals(129, ((Double) map.get("double")).intValue(), "double value not correct");
    }

    /**
     * Tests the byte validation.
     */
    @Test
    void testType() throws ValidatorException {
        // Create bean to run test on.
        final TypeBean info = new TypeBean();
        info.setByte("12");
        info.setShort("129");
        info.setInteger("-144");
        info.setLong("88000");
        info.setFloat("12.1555f");
        info.setDouble("129.1551511111d");

        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, info);

        // Get results of the validation.
        // throws ValidatorException,
        // but we aren't catching for testing
        // since no validation methods we use
        // throw this
        final ValidatorResults results = validator.validate();

        assertNotNull(results, "Results are null.");

        final Map<String, ?> hResultValues = results.getResultValueMap();

        assertInstanceOf(Byte.class, hResultValues.get("byte"), "Expecting byte result to be an instance of Byte.");
        assertInstanceOf(Short.class, hResultValues.get("short"), "Expecting short result to be an instance of Short.");
        assertInstanceOf(Integer.class, hResultValues.get("integer"), "Expecting integer result to be an instance of Integer.");
        assertInstanceOf(Long.class, hResultValues.get("long"), "Expecting long result to be an instance of Long.");
        assertInstanceOf(Float.class, hResultValues.get("float"), "Expecting float result to be an instance of Float.");
        assertInstanceOf(Double.class, hResultValues.get("double"), "Expecting double result to be an instance of Double.");

        for (final String key : hResultValues.keySet()) {
            final Object value = hResultValues.get(key);

            assertNotNull(value, "value ValidatorResults.getResultValueMap() should not be null.");
        }

        // ValidatorResult result = results.getValidatorResult("value");

        // assertNotNull(ACTION + " value ValidatorResult should not be null.", result);
        // assertTrue(ACTION + " value ValidatorResult should contain the '" + ACTION +"' action.", result.containsAction(ACTION));
        // assertTrue(ACTION + " value ValidatorResult for the '" + ACTION +"' action should have " + (passed ? "passed" : "failed") + ".", (passed ?
        // result.isValid(ACTION) : !result.isValid(ACTION)));

    }

    /**
     * Tests the us locale
     */
    @Test
    void testUSLocale() throws ValidatorException {
        // Create bean to run test on.
        final TypeBean info = new TypeBean();
        info.setByte("12");
        info.setShort("129");
        info.setInteger("-144");
        info.setLong("88000");
        info.setFloat("12.1555");
        info.setDouble("129.1551511111");
        info.setDate("12/21/2010");
        localeTest(info, Locale.US);
    }

}