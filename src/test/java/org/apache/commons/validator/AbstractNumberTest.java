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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 * Abstracts number unit tests methods.
 */
public abstract class AbstractNumberTest extends AbstractCommonTest {

    /**
     * The key used to retrieve the set of validation rules from the xml file.
     */
    protected String formKey;

    /**
     * The key used to retrieve the validator action.
     */
    protected String action;

    /**
     * Load {@code ValidatorResources} from validator-numeric.xml.
     */
    @BeforeEach
    protected void setUp() throws IOException, SAXException {
        // Load resources
        loadResources("TestNumber-config.xml");
    }

    @AfterEach
    protected void tearDown() {
    }

    /**
     * Tests the number validation.
     */
    @Test
    void testNumber() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();
        info.setValue("0");
        valueTest(info, true);
    }

    /**
     * Tests the float validation failure.
     */
    @Test
    void testNumberFailure() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();
        valueTest(info, false);
    }

    /**
     * Utility class to run a test on a value.
     *
     * @param info   Value to run test on.
     * @param passed Whether or not the test is expected to pass.
     */
    protected void valueTest(final Object info, final boolean passed) throws ValidatorException {
        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, formKey);
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

        final ValidatorResult result = results.getValidatorResult("value");

        assertNotNull(result, action + " value ValidatorResult should not be null.");
        assertTrue(result.containsAction(action), action + " value ValidatorResult should contain the '" + action + "' action.");
        assertTrue(passed ? result.isValid(action) : !result.isValid(action),
                action + " value ValidatorResult for the '" + action + "' action should have " + (passed ? "passed" : "failed") + ".");
    }

}
