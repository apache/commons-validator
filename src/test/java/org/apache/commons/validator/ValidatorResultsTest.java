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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 * Test ValidatorResults.
 */
class ValidatorResultsTest extends AbstractCommonTest {

    private static final String FORM_KEY = "nameForm";
    private static final String FIRST_NAME_FIELD = "firstName";
    private static final String MIDDLE_NAME_FIELD = "middleName";
    private static final String LAST_NAME_FIELD = "lastName";

    private String firstName;
    private String middleName;
    private String lastName;

    /**
     * Check a validator has not been run for a field and the result.
     */
    private void checkNotRun(final ValidatorResults results, final String field, final String action) {
        final ValidatorResult result = results.getValidatorResult(field);
        assertNotNull(result, field + " result");
        assertFalse(result.containsAction(action), field + "[" + action + "] run");
        // System.out.println(field + "[" + action + "] not run");
    }

    /**
     * Check a validator has run for a field and the result.
     */
    private void checkValidatorResult(final ValidatorResults results, final String field, final String action, final boolean expected) {
        final ValidatorResult result = results.getValidatorResult(field);
        // System.out.println(field + "[" + action + "]=" + result.isValid(action));
        assertNotNull(result, field + " result");
        assertTrue(result.containsAction(action), field + "[" + action + "] not run");
        assertEquals(expected, result.isValid(action), field + "[" + action + "] result");
    }

    /**
     * Create a NameBean.
     */
    private NameBean createNameBean() {
        final NameBean name = new NameBean();
        name.setFirstName(firstName);
        name.setMiddleName(middleName);
        name.setLastName(lastName);
        return name;
    }

    /**
     * Load {@code ValidatorResources} from ValidatorResultsTest-config.xml.
     */
    @BeforeEach
    protected void setUp() throws IOException, SAXException {
        // Load resources
        loadResources("ValidatorResultsTest-config.xml");

        // initialize values
        firstName = "foo";
        middleName = "123";
        lastName = "456";

    }

    @AfterEach
    protected void tearDown() {
    }

    /**
     * Test all validations ran and passed.
     */
    @Test
    void testAllValid() throws ValidatorException {

        // Create bean to run test on.
        final NameBean bean = createNameBean();

        // Validate.
        final ValidatorResults results = validate(bean);

        // Check results
        checkValidatorResult(results, FIRST_NAME_FIELD, "required", true);
        checkValidatorResult(results, MIDDLE_NAME_FIELD, "required", true);
        checkValidatorResult(results, MIDDLE_NAME_FIELD, "int", true);
        checkValidatorResult(results, MIDDLE_NAME_FIELD, "positive", true);
        checkValidatorResult(results, LAST_NAME_FIELD, "required", true);
        checkValidatorResult(results, LAST_NAME_FIELD, "int", true);

    }

    /**
     * Test some validations failed and some didn't run.
     */
    @Test
    void testErrors() throws ValidatorException {

        middleName = "XXX";
        lastName = null;

        // Create bean to run test on.
        final NameBean bean = createNameBean();

        // Validate.
        final ValidatorResults results = validate(bean);

        // Check results
        checkValidatorResult(results, FIRST_NAME_FIELD, "required", true);
        checkValidatorResult(results, MIDDLE_NAME_FIELD, "required", true);
        checkValidatorResult(results, MIDDLE_NAME_FIELD, "int", false);
        checkNotRun(results, MIDDLE_NAME_FIELD, "positive");
        checkValidatorResult(results, LAST_NAME_FIELD, "required", false);
        checkNotRun(results, LAST_NAME_FIELD, "int");

    }

    /**
     * Validate results.
     */
    private ValidatorResults validate(final Object bean) throws ValidatorException {

        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, FORM_KEY);

        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, bean);

        return validator.validate();

    }

}
