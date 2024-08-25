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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 * Tests that validator rules split between 2 different XML files get merged properly.
 */
public class MultipleConfigFilesTest {

    /**
     * The key used to retrieve the set of validation rules from the xml file.
     */
    private static final String FORM_KEY = "nameForm";

    /**
     * The key used to retrieve the validator action.
     */
    private static final String ACTION = "required";

    /**
     * Resources used for validation tests.
     */
    private ValidatorResources resources;

    /**
     * Load {@code ValidatorResources} from multiple xml files.
     */
    @BeforeEach
    protected void setUp() throws IOException, SAXException {
        final InputStream[] streams = { this.getClass().getResourceAsStream("MultipleConfigFilesTest-1-config.xml"),
                this.getClass().getResourceAsStream("MultipleConfigFilesTest-2-config.xml") };

        resources = new ValidatorResources(streams);

        for (final InputStream stream : streams) {
            stream.close();
        }
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

        assertNotNull(firstNameResult);
        assertTrue(firstNameResult.containsAction(ACTION));
        assertTrue(!firstNameResult.isValid(ACTION));

        assertNotNull(lastNameResult);
        assertTrue(lastNameResult.containsAction(ACTION));
        assertTrue(!lastNameResult.isValid(ACTION));
        assertTrue(!lastNameResult.containsAction("int"));
    }

    /**
     * Check the forms and constants from different config files have been merged into the same FormSet.
     */
    @Test
    public void testMergedConfig() {

        // *********** Default Locale *******************

        // Check the form from the first config file exists
        final Form form1 = resources.getForm("", "", "", "testForm1");
        assertNotNull(form1, "Form 'testForm1' not found");

        // Check the form from the second config file exists
        final Form form2 = resources.getForm("", "", "", "testForm2");
        assertNotNull(form2, "Form 'testForm2' not found");

        // Check the Constants for the form from the first config file
        final Field field1 = form1.getField("testProperty1");
        assertEquals("testConstValue1", field1.getVarValue("var11"), "testProperty1 - const 1");
        assertEquals("testConstValue2", field1.getVarValue("var12"), "testProperty1 - const 2");

        // Check the Constants for the form from the second config file
        final Field field2 = form2.getField("testProperty2");
        assertEquals("testConstValue1", field2.getVarValue("var21"), "testProperty2 - const 1");
        assertEquals("testConstValue2", field2.getVarValue("var22"), "testProperty2 - const 2");

        // *********** 'fr' locale *******************

        // Check the form from the first config file exists
        final Form form1Fr = resources.getForm("fr", "", "", "testForm1_fr");
        assertNotNull(form1Fr, "Form 'testForm1_fr' not found");

        // Check the form from the second config file exists
        final Form form2Fr = resources.getForm("fr", "", "", "testForm2_fr");
        assertNotNull(form2Fr, "Form 'testForm2_fr' not found");

        // Check the Constants for the form from the first config file
        final Field field1Fr = form1Fr.getField("testProperty1_fr");
        assertEquals("testConstValue1_fr", field1Fr.getVarValue("var11_fr"), "testProperty1_fr - const 1");
        assertEquals("testConstValue2_fr", field1Fr.getVarValue("var12_fr"), "testProperty1_fr - const 2");

        // Check the Constants for the form from the second config file
        final Field field2Fr = form2Fr.getField("testProperty2_fr");
        assertEquals("testConstValue1_fr", field2Fr.getVarValue("var21_fr"), "testProperty2_fr - const 1");
        assertEquals("testConstValue2_fr", field2Fr.getVarValue("var22_fr"), "testProperty2_fr - const 2");
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

        assertNotNull(firstNameResult);
        assertTrue(firstNameResult.containsAction(ACTION));
        assertTrue(!firstNameResult.isValid(ACTION));

        assertNotNull(lastNameResult);
        assertTrue(lastNameResult.containsAction("int"));
        assertTrue(!lastNameResult.isValid("int"));
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

        assertNotNull(firstNameResult);
        assertTrue(firstNameResult.containsAction(ACTION));
        assertTrue(firstNameResult.isValid(ACTION));

        assertNotNull(lastNameResult);
        assertTrue(lastNameResult.containsAction("int"));
        assertTrue(lastNameResult.isValid("int"));
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

        assertNotNull(firstNameResult);
        assertTrue(firstNameResult.containsAction(ACTION));
        assertTrue(firstNameResult.isValid(ACTION));

        assertNotNull(lastNameResult);
        assertTrue(lastNameResult.containsAction("int"));
        assertTrue(!lastNameResult.isValid("int"));
    }

}
