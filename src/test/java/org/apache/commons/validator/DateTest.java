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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 * Abstracts date unit tests methods.
 */
public class DateTest extends AbstractCommonTest {

    /**
     * The key used to retrieve the set of validation rules from the xml file.
     */
    protected static final String FORM_KEY = "dateForm";

    /**
     * The key used to retrieve the validator action.
     */
    protected static final String ACTION = "date";

    /**
     * Load {@code ValidatorResources} from validator-numeric.xml.
     */
    @BeforeEach
    protected void setUp() throws IOException, SAXException {
        // Load resources
        loadResources("DateTest-config.xml");
    }

    /**
     * Tests the date validation.
     */
    @Test
    public void testInvalidDate() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();
        info.setValue("12/01as/2005");
        valueTest(info, false);
    }

    /**
     * Tests the date validation.
     */
    @Test
    public void testValidDate() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();
        info.setValue("12/01/2005");
        valueTest(info, true);
    }

    /**
     * Utlity class to run a test on a value.
     *
     * @param info   Value to run test on.
     * @param passed Whether or not the test is expected to pass.
     */
    protected void valueTest(final Object info, final boolean passed) throws ValidatorException {
        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, info);
        validator.setParameter(Validator.LOCALE_PARAM, Locale.US);

        // Get results of the validation.
        // throws ValidatorException,
        // but we aren't catching for testing
        // since no validation methods we use
        // throw this
        final ValidatorResults results = validator.validate();

        assertNotNull(results, "Results are null.");

        final ValidatorResult result = results.getValidatorResult("value");

        assertNotNull(result, () -> ACTION + " value ValidatorResult should not be null.");
        assertTrue(result.containsAction(ACTION), () -> ACTION + " value ValidatorResult should contain the '" + ACTION + "' action.");
        assertTrue(passed ? result.isValid(ACTION) : !result.isValid(ACTION),
                () -> ACTION + " value ValidatorResult for the '" + ACTION + "' action should have " + (passed ? "passed" : "failed") + ".");
    }

}
