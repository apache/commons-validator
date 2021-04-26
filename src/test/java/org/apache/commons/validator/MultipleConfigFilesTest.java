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

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

/**
 * Tests that validator rules split between 2 different XML files get
 * merged properly.
 *
 * @version $Revision$
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
    @Override
    protected void setUp() throws IOException, SAXException {
        InputStream[] streams =
            new InputStream[] {
                this.getClass().getResourceAsStream(
                    "MultipleConfigFilesTest-1-config.xml"),
                this.getClass().getResourceAsStream(
                    "MultipleConfigFilesTest-2-config.xml")};

        this.resources = new ValidatorResources(streams);

        for (InputStream stream : streams) {
            stream.close();
        }
    }

   /**
    * Check the forms and constants from different config files have
    * been merged into the same FormSet.
    */
    public void testMergedConfig() {

        // *********** Default Locale *******************

        // Check the form from the first config file exists
        Form form1 = resources.getForm("", "", "", "testForm1");
        assertNotNull("Form 'testForm1' not found", form1);

        // Check the form from the second config file exists
        Form form2 = resources.getForm("", "", "", "testForm2");
        assertNotNull("Form 'testForm2' not found", form2);

        // Check the Constants  for the form from the first config file
        Field field1 = form1.getField("testProperty1");
        assertEquals("testProperty1 - const 1", "testConstValue1", field1.getVarValue("var11"));
        assertEquals("testProperty1 - const 2", "testConstValue2", field1.getVarValue("var12"));

        // Check the Constants  for the form from the second config file
        Field field2 = form2.getField("testProperty2");
        assertEquals("testProperty2 - const 1", "testConstValue1", field2.getVarValue("var21"));
        assertEquals("testProperty2 - const 2", "testConstValue2", field2.getVarValue("var22"));

        // *********** 'fr' locale *******************

        // Check the form from the first config file exists
        Form form1_fr = resources.getForm("fr", "", "", "testForm1_fr");
        assertNotNull("Form 'testForm1_fr' not found", form1_fr);

        // Check the form from the second config file exists
        Form form2_fr = resources.getForm("fr", "", "", "testForm2_fr");
        assertNotNull("Form 'testForm2_fr' not found", form2_fr);

        // Check the Constants  for the form from the first config file
        Field field1_fr = form1_fr.getField("testProperty1_fr");
        assertEquals("testProperty1_fr - const 1", "testConstValue1_fr", field1_fr.getVarValue("var11_fr"));
        assertEquals("testProperty1_fr - const 2", "testConstValue2_fr", field1_fr.getVarValue("var12_fr"));

        // Check the Constants  for the form from the second config file
        Field field2_fr = form2_fr.getField("testProperty2_fr");
        assertEquals("testProperty2_fr - const 1", "testConstValue1_fr", field2_fr.getVarValue("var21_fr"));
        assertEquals("testProperty2_fr - const 2", "testConstValue2_fr", field2_fr.getVarValue("var22_fr"));
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
        // throws ValidatorException,
        // but we aren't catching for testing
        // since no validation methods we use
        // throw this
        ValidatorResults results = validator.validate();

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
        ValidatorResults results = validator.validate();

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
        ValidatorResults results = validator.validate();

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
        ValidatorResults results = validator.validate();

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
