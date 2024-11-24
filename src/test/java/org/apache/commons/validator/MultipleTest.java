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
public class MultipleTest extends AbstractCommonTest {

    /**
     * The key used to retrieve the set of validation rules from the xml file.
     */
    protected static final String FORM_KEY = "nameForm";

    /**
     * The key used to retrieve the validator action.
     */
    protected static final String ACTION = "required";

    /**
     * Load {@code ValidatorResources} from validator-multipletest.xml.
     */
    @BeforeEach
    protected void setUp() throws IOException, SAXException {
        // Load resources
        loadResources("MultipleTests-config.xml");
    }

    @AfterEach
    protected void tearDown() {
    }

    /**
     * With nothing provided, we should fail both because both are required.
     */
    @Test
    public void testBothBlank() throws ValidatorException {
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
        assertFalse(firstNameResult.isValid(ACTION), "First Name ValidatorResult for the '" + ACTION + "' action should have failed.");

        assertNotNull(lastNameResult, "Last Name ValidatorResult should not be null.");
        assertTrue(lastNameResult.containsAction(ACTION), "Last Name ValidatorResult should contain the '" + ACTION + "' action.");
        assertFalse(lastNameResult.isValid(ACTION), "Last Name ValidatorResult for the '" + ACTION + "' action should have failed.");
        assertFalse(lastNameResult.containsAction("int"), "Last Name ValidatorResults should not contain the 'int' action.");
    }

    /**
     * If middle name is not there, then the required dependent test should fail. No other tests should run
     *
     * @throws ValidatorException
     */
    @Test
    public void testFailingFirstDependentValidator() throws ValidatorException {
        // Create bean to run test on.
        final NameBean name = new NameBean();

        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, name);

        // Get results of the validation.
        final ValidatorResults results = validator.validate();

        assertNotNull(results, "Results are null.");

        final ValidatorResult middleNameResult = results.getValidatorResult("middleName");

        assertNotNull(middleNameResult, "Middle Name ValidatorResult should not be null.");

        assertTrue(middleNameResult.containsAction("required"), "Middle Name ValidatorResult should contain the 'required' action.");
        assertFalse(middleNameResult.isValid("required"), "Middle Name ValidatorResult for the 'required' action should have failed");

        assertFalse(middleNameResult.containsAction("int"), "Middle Name ValidatorResult should not contain the 'int' action.");

        assertFalse(middleNameResult.containsAction("positive"), "Middle Name ValidatorResult should not contain the 'positive' action.");
    }

    /**
     * If middle name is there but not int, then the required dependent test should pass, but the int dependent test should fail. No other tests should run.
     *
     * @throws ValidatorException
     */
    @Test
    public void testFailingNextDependentValidator() throws ValidatorException {
        // Create bean to run test on.
        final NameBean name = new NameBean();
        name.setMiddleName("TEST");

        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, name);

        // Get results of the validation.
        final ValidatorResults results = validator.validate();

        assertNotNull(results, "Results are null.");

        final ValidatorResult middleNameResult = results.getValidatorResult("middleName");

        assertNotNull(middleNameResult, "Middle Name ValidatorResult should not be null.");

        assertTrue(middleNameResult.containsAction("required"), "Middle Name ValidatorResult should contain the 'required' action.");
        assertTrue(middleNameResult.isValid("required"), "Middle Name ValidatorResult for the 'required' action should have passed");

        assertTrue(middleNameResult.containsAction("int"), "Middle Name ValidatorResult should contain the 'int' action.");
        assertFalse(middleNameResult.isValid("int"), "Middle Name ValidatorResult for the 'int' action should have failed");

        assertFalse(middleNameResult.containsAction("positive"), "Middle Name ValidatorResult should not contain the 'positive' action.");
    }

    /**
     * If middle name is there and a negative int, then the required and int dependent tests should pass, but the positive test should fail.
     *
     * @throws ValidatorException
     */
    @Test
    public void testPassingDependentsFailingMain() throws ValidatorException {
        // Create bean to run test on.
        final NameBean name = new NameBean();
        name.setMiddleName("-2534");

        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, name);

        // Get results of the validation.
        final ValidatorResults results = validator.validate();

        assertNotNull(results, "Results are null.");

        final ValidatorResult middleNameResult = results.getValidatorResult("middleName");

        assertNotNull(middleNameResult, "Middle Name ValidatorResult should not be null.");

        assertTrue(middleNameResult.containsAction("required"), "Middle Name ValidatorResult should contain the 'required' action.");
        assertTrue(middleNameResult.isValid("required"), "Middle Name ValidatorResult for the 'required' action should have passed");

        assertTrue(middleNameResult.containsAction("int"), "Middle Name ValidatorResult should contain the 'int' action.");
        assertTrue(middleNameResult.isValid("int"), "Middle Name ValidatorResult for the 'int' action should have passed");

        assertTrue(middleNameResult.containsAction("positive"), "Middle Name ValidatorResult should contain the 'positive' action.");
        assertFalse(middleNameResult.isValid("positive"), "Middle Name ValidatorResult for the 'positive' action should have failed");
    }

    /**
     * If middle name is there and a positive int, then the required and int dependent tests should pass, and the positive test should pass.
     *
     * @throws ValidatorException
     */
    @Test
    public void testPassingDependentsPassingMain() throws ValidatorException {
        // Create bean to run test on.
        final NameBean name = new NameBean();
        name.setMiddleName("2534");

        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, name);

        // Get results of the validation.
        final ValidatorResults results = validator.validate();

        assertNotNull(results, "Results are null.");

        final ValidatorResult middleNameResult = results.getValidatorResult("middleName");

        assertNotNull(middleNameResult, "Middle Name ValidatorResult should not be null.");

        assertTrue(middleNameResult.containsAction("required"), "Middle Name ValidatorResult should contain the 'required' action.");
        assertTrue(middleNameResult.isValid("required"), "Middle Name ValidatorResult for the 'required' action should have passed");

        assertTrue(middleNameResult.containsAction("int"), "Middle Name ValidatorResult should contain the 'int' action.");
        assertTrue(middleNameResult.isValid("int"), "Middle Name ValidatorResult for the 'int' action should have passed");

        assertTrue(middleNameResult.containsAction("positive"), "Middle Name ValidatorResult should contain the 'positive' action.");
        assertTrue(middleNameResult.isValid("positive"), "Middle Name ValidatorResult for the 'positive' action should have passed");
    }

    /**
     * If the first name fails required, and the second test fails int, we should get two errors.
     */
    @Test
    public void testRequiredFirstNameBlankLastNameShort() throws ValidatorException {
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
        assertTrue(lastNameResult.containsAction("int"), "Last Name ValidatorResult should contain the 'int' action.");
        assertFalse(lastNameResult.isValid("int"), "Last Name ValidatorResult for the 'int' action should have failed.");
    }

    /**
     * If first name is ok and last name is ok and is an int, no errors.
     */
    @Test
    public void testRequiredLastNameLong() throws ValidatorException {
        // Create bean to run test on.
        final NameBean name = new NameBean();
        name.setFirstName("Joe");
        name.setLastName("12345678");

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
        assertTrue(lastNameResult.containsAction("int"), "Last Name ValidatorResult should contain the 'int' action.");
        assertTrue(lastNameResult.isValid("int"), "Last Name ValidatorResult for the 'int' action should have passed.");
    }

    /**
     * If the first name is there, and the last name fails int, we should get one error.
     */
    @Test
    public void testRequiredLastNameShort() throws ValidatorException {
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
        assertTrue(lastNameResult.containsAction("int"), "Last Name ValidatorResult should contain the 'int' action.");
        assertFalse(lastNameResult.isValid("int"), "Last Name ValidatorResult for the 'int' action should have failed.");
    }
}
