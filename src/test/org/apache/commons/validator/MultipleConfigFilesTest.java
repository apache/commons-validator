/*
 * $Id$
 * $Rev$
 * $Date$
 *
 * ====================================================================
 * Copyright 2001-2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.validator;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

/**
 * Tests that validator rules split between 2 different XML files get 
 * merged properly.
 */
public class MultipleConfigFilesTest extends TestCase {

    /**
     * Resources used for validation tests.
     */
    private ValidatorResources resources = null;
    
    /**
     * The key used to retrieve the set of validation
     * rules from the xml file.
     */
    private static final String FORM_KEY = "nameForm";

    /**
     * The key used to retrieve the validator action.
     */
    private static final String ACTION = "required";

    /**
     * Constructor for MultipleConfigFilesTest.
     * @param name
     */
    public MultipleConfigFilesTest(String name) {
        super(name);
    }

    /** 
     * Load <code>ValidatorResources</code> from multiple xml files.
     */
    protected void setUp() throws IOException, SAXException {
        InputStream[] streams =
            new InputStream[] {
                this.getClass().getResourceAsStream(
                    "validator-multiple-config-1.xml"),
                this.getClass().getResourceAsStream(
                    "validator-multiple-config-2.xml")};

        this.resources = new ValidatorResources(streams);

        for (int i = 0; i < streams.length; i++) {
            streams[i].close();
        }
    }

    /**
    * With nothing provided, we should fail both because both are required.
    */
    public void testBothBlank() throws ValidatorException {
        // Create bean to run test on.
        NameBean name = new NameBean();

        // Construct validator based on the loaded resources
        // and the form key
        Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, name);

        // Get results of the validation.
        ValidatorResults results = null;

        // throws ValidatorException,
        // but we aren't catching for testing
        // since no validation methods we use
        // throw this
        results = validator.validate();

        assertNotNull("Results are null.", results);

        ValidatorResult firstNameResult = results.getValidatorResult("firstName");
        ValidatorResult lastNameResult = results.getValidatorResult("lastName");

        assertNotNull(firstNameResult);
        assertTrue(firstNameResult.containsAction(ACTION));
        assertTrue(!firstNameResult.isValid(ACTION));

        assertNotNull(lastNameResult);
        assertTrue(lastNameResult.containsAction(ACTION));
        assertTrue(!lastNameResult.isValid(ACTION));
        assertTrue(!lastNameResult.containsAction("int"));
    }

    /**
     * If the first name fails required, and the second test fails int, we should get two errors.
    */
    public void testRequiredFirstNameBlankLastNameShort()
        throws ValidatorException {
        // Create bean to run test on.
        NameBean name = new NameBean();
        name.setFirstName("");
        name.setLastName("Test");

        // Construct validator based on the loaded resources
        // and the form key
        Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, name);

        // Get results of the validation.
        ValidatorResults results = null;

        results = validator.validate();

        assertNotNull("Results are null.", results);

        ValidatorResult firstNameResult = results.getValidatorResult("firstName");
        ValidatorResult lastNameResult = results.getValidatorResult("lastName");

        assertNotNull(firstNameResult);
        assertTrue(firstNameResult.containsAction(ACTION));
        assertTrue(!firstNameResult.isValid(ACTION));

        assertNotNull(lastNameResult);
        assertTrue(lastNameResult.containsAction("int"));
        assertTrue(!lastNameResult.isValid("int"));
    }

    /**
     * If the first name is there, and the last name fails int, we should get one error.
    */
    public void testRequiredLastNameShort() throws ValidatorException {
        // Create bean to run test on.
        NameBean name = new NameBean();
        name.setFirstName("Test");
        name.setLastName("Test");

        // Construct validator based on the loaded resources
        // and the form key
        Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, name);

        // Get results of the validation.
        ValidatorResults results = null;

        results = validator.validate();

        assertNotNull("Results are null.", results);

        ValidatorResult firstNameResult = results.getValidatorResult("firstName");
        ValidatorResult lastNameResult = results.getValidatorResult("lastName");

        assertNotNull(firstNameResult);
        assertTrue(firstNameResult.containsAction(ACTION));
        assertTrue(firstNameResult.isValid(ACTION));

        assertNotNull(lastNameResult);
        assertTrue(lastNameResult.containsAction("int"));
        assertTrue(!lastNameResult.isValid("int"));
    }

    /**
     * If first name is ok and last name is ok and is an int, no errors.
    */
    public void testRequiredLastNameLong() throws ValidatorException {
        // Create bean to run test on.
        NameBean name = new NameBean();
        name.setFirstName("Joe");
        name.setLastName("12345678");

        // Construct validator based on the loaded resources
        // and the form key
        Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, name);

        // Get results of the validation.
        ValidatorResults results = null;

        results = validator.validate();

        assertNotNull("Results are null.", results);

        ValidatorResult firstNameResult = results.getValidatorResult("firstName");
        ValidatorResult lastNameResult = results.getValidatorResult("lastName");

        assertNotNull(firstNameResult);
        assertTrue(firstNameResult.containsAction(ACTION));
        assertTrue(firstNameResult.isValid(ACTION));

        assertNotNull(lastNameResult);
        assertTrue(lastNameResult.containsAction("int"));
        assertTrue(lastNameResult.isValid("int"));
    }

}
