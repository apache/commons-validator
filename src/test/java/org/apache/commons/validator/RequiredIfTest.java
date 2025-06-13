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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 * Performs Validation Test.
 */
class RequiredIfTest extends AbstractCommonTest {

    /**
     * The key used to retrieve the set of validation rules from the xml file.
     */
    protected static final String FORM_KEY = "nameForm";

    /**
     * The key used to retrieve the validator action.
     */
    protected static final String ACTION = "requiredif";

    /**
     * Load {@code ValidatorResources} from validator-requiredif.xml.
     */
    @BeforeEach
    protected void setUp() throws IOException, SAXException {
        // Load resources
        loadResources("RequiredIfTest-config.xml");
    }

    @AfterEach
    protected void tearDown() {
    }

    /**
     * With nothing provided, we should pass since the fields only fail on null if the other field is non-blank.
     */
    @Test
    void testRequired() throws ValidatorException {
        // Create bean to run test on.
        final NameBean name = new NameBean();

        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, name);

        // Get results of the validation.
        // throws ValidatorException,
        // but we aren't catching for testing
        // since no validation methods we use
        // throw this
        final ValidatorResults results = validator.validate();

        assertNotNull(results, "Results are null.");

        final ValidatorResult firstNameResult = results.getValidatorResult("firstName");
        final ValidatorResult lastNameResult = results.getValidatorResult("lastName");

        assertNotNull(firstNameResult, "First Name ValidatorResult should not be null.");
        assertTrue(firstNameResult.containsAction(ACTION), "First Name ValidatorResult should contain the '" + ACTION + "' action.");
        assertTrue(firstNameResult.isValid(ACTION), "First Name ValidatorResult for the '" + ACTION + "' action should have passed.");

        assertNotNull(lastNameResult, "Last Name ValidatorResult should not be null.");
        assertTrue(lastNameResult.containsAction(ACTION), "Last Name ValidatorResult should contain the '" + ACTION + "' action.");
        assertTrue(lastNameResult.isValid(ACTION), "Last Name ValidatorResult for the '" + ACTION + "' action should have passed.");
    }

    /**
     * Tests the required validation for last name.
     */
    @Test
    void testRequiredFirstName() throws ValidatorException {
        // Create bean to run test on.
        final NameBean name = new NameBean();
        name.setFirstName("Test");
        name.setLastName("Test");

        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, name);

        // Get results of the validation.
        final ValidatorResults results = validator.validate();

        assertNotNull(results, "Results are null.");

        final ValidatorResult firstNameResult = results.getValidatorResult("firstName");
        final ValidatorResult lastNameResult = results.getValidatorResult("lastName");

        assertNotNull(firstNameResult, "First Name ValidatorResult should not be null.");
        assertTrue(firstNameResult.containsAction(ACTION), "First Name ValidatorResult should contain the '" + ACTION + "' action.");
        assertTrue(firstNameResult.isValid(ACTION), "First Name ValidatorResult for the '" + ACTION + "' action should have passed.");

        assertNotNull(lastNameResult, "Last Name ValidatorResult should not be null.");
        assertTrue(lastNameResult.containsAction(ACTION), "Last Name ValidatorResult should contain the '" + ACTION + "' action.");
        assertTrue(lastNameResult.isValid(ACTION), "Last Name ValidatorResult for the '" + ACTION + "' action should have passed.");
    }

    /**
     * Tests the required validation for first name if it is blank.
     */
    @Test
    void testRequiredFirstNameBlank() throws ValidatorException {
        // Create bean to run test on.
        final NameBean name = new NameBean();
        name.setFirstName("");
        name.setLastName("Test");

        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, name);

        // Get results of the validation.
        final ValidatorResults results = validator.validate();

        assertNotNull(results, "Results are null.");

        final ValidatorResult firstNameResult = results.getValidatorResult("firstName");
        final ValidatorResult lastNameResult = results.getValidatorResult("lastName");

        assertNotNull(firstNameResult, "First Name ValidatorResult should not be null.");
        assertTrue(firstNameResult.containsAction(ACTION), "First Name ValidatorResult should contain the '" + ACTION + "' action.");
        assertFalse(firstNameResult.isValid(ACTION), "First Name ValidatorResult for the '" + ACTION + "' action should have failed.");

        assertNotNull(lastNameResult, "Last Name ValidatorResult should not be null.");
        assertTrue(lastNameResult.containsAction(ACTION), "Last Name ValidatorResult should contain the '" + ACTION + "' action.");
        assertTrue(lastNameResult.isValid(ACTION), "Last Name ValidatorResult for the '" + ACTION + "' action should have passed.");
    }

    /**
     * Tests the required validation for last name.
     */
    @Test
    void testRequiredLastName() throws ValidatorException {
        // Create bean to run test on.
        final NameBean name = new NameBean();
        name.setFirstName("Joe");
        name.setLastName("Smith");

        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, name);

        // Get results of the validation.
        final ValidatorResults results = validator.validate();

        assertNotNull(results, "Results are null.");

        final ValidatorResult firstNameResult = results.getValidatorResult("firstName");
        final ValidatorResult lastNameResult = results.getValidatorResult("lastName");

        assertNotNull(firstNameResult, "First Name ValidatorResult should not be null.");
        assertTrue(firstNameResult.containsAction(ACTION), "First Name ValidatorResult should contain the '" + ACTION + "' action.");
        assertTrue(firstNameResult.isValid(ACTION), "First Name ValidatorResult for the '" + ACTION + "' action should have passed.");

        assertNotNull(lastNameResult, "Last Name ValidatorResult should not be null.");
        assertTrue(lastNameResult.containsAction(ACTION), "Last Name ValidatorResult should contain the '" + ACTION + "' action.");
        assertTrue(lastNameResult.isValid(ACTION), "Last Name ValidatorResult for the '" + ACTION + "' action should have passed.");

    }

    /**
     * Tests the required validation for last name if it is blank.
     */
    @Test
    void testRequiredLastNameBlank() throws ValidatorException {
        // Create bean to run test on.
        final NameBean name = new NameBean();
        name.setFirstName("Joe");
        name.setLastName("");

        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, name);

        // Get results of the validation.
        final ValidatorResults results = validator.validate();

        assertNotNull(results, "Results are null.");

        final ValidatorResult firstNameResult = results.getValidatorResult("firstName");
        final ValidatorResult lastNameResult = results.getValidatorResult("lastName");

        assertNotNull(firstNameResult, "First Name ValidatorResult should not be null.");
        assertTrue(firstNameResult.containsAction(ACTION), "First Name ValidatorResult should contain the '" + ACTION + "' action.");
        assertTrue(firstNameResult.isValid(ACTION), "First Name ValidatorResult for the '" + ACTION + "' action should have passed.");

        assertNotNull(lastNameResult, "Last Name ValidatorResult should not be null.");
        assertTrue(lastNameResult.containsAction(ACTION), "Last Name ValidatorResult should contain the '" + ACTION + "' action.");
        assertFalse(lastNameResult.isValid(ACTION), "Last Name ValidatorResult for the '" + ACTION + "' action should have failed.");
    }

}
