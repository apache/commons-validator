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

import org.xml.sax.SAXException;

/**
 * Performs Validation Test.
 *
 * @version $Revision$
 */
public class MultipleTest extends AbstractCommonTest {

   /**
    * The key used to retrieve the set of validation
    * rules from the xml file.
    */
   protected static String FORM_KEY = "nameForm";

   /**
    * The key used to retrieve the validator action.
    */
   protected static String ACTION = "required";



   public MultipleTest(String name) {
       super(name);
   }

   /**
    * Load <code>ValidatorResources</code> from
    * validator-multipletest.xml.
    */
   @Override
protected void setUp() throws IOException, SAXException {
      // Load resources
      loadResources("MultipleTests-config.xml");
   }

   @Override
protected void tearDown() {
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

      assertNotNull("First Name ValidatorResult should not be null.", firstNameResult);
      assertTrue("First Name ValidatorResult should contain the '" + ACTION +"' action.", firstNameResult.containsAction(ACTION));
      assertTrue("First Name ValidatorResult for the '" + ACTION +"' action should have failed.", !firstNameResult.isValid(ACTION));

      assertNotNull("Last Name ValidatorResult should not be null.", lastNameResult);
      assertTrue("Last Name ValidatorResult should contain the '" + ACTION +"' action.", lastNameResult.containsAction(ACTION));
      assertTrue("Last Name ValidatorResult for the '" + ACTION +"' action should have failed.", !lastNameResult.isValid(ACTION));
      assertTrue("Last Name ValidatorResults should not contain the 'int' action.", !lastNameResult.containsAction("int"));
   }

   /**
    * If the first name fails required, and the second test fails int, we should get two errors.
    */
   public void testRequiredFirstNameBlankLastNameShort() throws ValidatorException {
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

      assertNotNull("First Name ValidatorResult should not be null.", firstNameResult);
      assertTrue("First Name ValidatorResult should contain the '" + ACTION +"' action.", firstNameResult.containsAction(ACTION));
      assertTrue("First Name ValidatorResult for the '" + ACTION +"' action should have failed.", !firstNameResult.isValid(ACTION));

      assertNotNull("Last Name ValidatorResult should not be null.", lastNameResult);
      assertTrue("Last Name ValidatorResult should contain the 'int' action.", lastNameResult.containsAction("int"));
      assertTrue("Last Name ValidatorResult for the 'int' action should have failed.", !lastNameResult.isValid("int"));
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

      assertNotNull("First Name ValidatorResult should not be null.", firstNameResult);
      assertTrue("First Name ValidatorResult should contain the '" + ACTION +"' action.", firstNameResult.containsAction(ACTION));
      assertTrue("First Name ValidatorResult for the '" + ACTION +"' action should have passed.", firstNameResult.isValid(ACTION));

      assertNotNull("Last Name ValidatorResult should not be null.", lastNameResult);
      assertTrue("Last Name ValidatorResult should contain the 'int' action.", lastNameResult.containsAction("int"));
      assertTrue("Last Name ValidatorResult for the 'int' action should have failed.", !lastNameResult.isValid("int"));
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

      assertNotNull("First Name ValidatorResult should not be null.", firstNameResult);
      assertTrue("First Name ValidatorResult should contain the '" + ACTION +"' action.", firstNameResult.containsAction(ACTION));
      assertTrue("First Name ValidatorResult for the '" + ACTION +"' action should have passed.", firstNameResult.isValid(ACTION));

      assertNotNull("Last Name ValidatorResult should not be null.", lastNameResult);
      assertTrue("Last Name ValidatorResult should contain the 'int' action.", lastNameResult.containsAction("int"));
      assertTrue("Last Name ValidatorResult for the 'int' action should have passed.", lastNameResult.isValid("int"));
   }

   /**
    * If middle name is not there, then the required dependent test should fail.
    * No other tests should run
    *
    * @throws ValidatorException
    */
   public void testFailingFirstDependentValidator() throws ValidatorException {
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

       results = validator.validate();

       assertNotNull("Results are null.", results);

       ValidatorResult middleNameResult = results.getValidatorResult("middleName");

       assertNotNull("Middle Name ValidatorResult should not be null.", middleNameResult);

       assertTrue("Middle Name ValidatorResult should contain the 'required' action.", middleNameResult.containsAction("required"));
       assertTrue("Middle Name ValidatorResult for the 'required' action should have failed", !middleNameResult.isValid("required"));

       assertTrue("Middle Name ValidatorResult should not contain the 'int' action.", !middleNameResult.containsAction("int"));

       assertTrue("Middle Name ValidatorResult should not contain the 'positive' action.", !middleNameResult.containsAction("positive"));
   }

   /**
    * If middle name is there but not int, then the required dependent test
    * should pass, but the int dependent test should fail. No other tests should
    * run.
    *
    * @throws ValidatorException
    */
   public void testFailingNextDependentValidator() throws ValidatorException {
       // Create bean to run test on.
       NameBean name = new NameBean();
       name.setMiddleName("TEST");

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

       ValidatorResult middleNameResult = results.getValidatorResult("middleName");

       assertNotNull("Middle Name ValidatorResult should not be null.", middleNameResult);

       assertTrue("Middle Name ValidatorResult should contain the 'required' action.", middleNameResult.containsAction("required"));
       assertTrue("Middle Name ValidatorResult for the 'required' action should have passed", middleNameResult.isValid("required"));

       assertTrue("Middle Name ValidatorResult should contain the 'int' action.", middleNameResult.containsAction("int"));
       assertTrue("Middle Name ValidatorResult for the 'int' action should have failed", !middleNameResult.isValid("int"));

       assertTrue("Middle Name ValidatorResult should not contain the 'positive' action.", !middleNameResult.containsAction("positive"));
   }

   /**
    * If middle name is there and a negative int, then the required and int
    * dependent tests should pass, but the positive test should fail.
    *
    * @throws ValidatorException
    */
   public void testPassingDependentsFailingMain() throws ValidatorException {
       // Create bean to run test on.
       NameBean name = new NameBean();
       name.setMiddleName("-2534");

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

       ValidatorResult middleNameResult = results.getValidatorResult("middleName");

       assertNotNull("Middle Name ValidatorResult should not be null.", middleNameResult);

       assertTrue("Middle Name ValidatorResult should contain the 'required' action.", middleNameResult.containsAction("required"));
       assertTrue("Middle Name ValidatorResult for the 'required' action should have passed", middleNameResult.isValid("required"));

       assertTrue("Middle Name ValidatorResult should contain the 'int' action.", middleNameResult.containsAction("int"));
       assertTrue("Middle Name ValidatorResult for the 'int' action should have passed", middleNameResult.isValid("int"));

       assertTrue("Middle Name ValidatorResult should contain the 'positive' action.", middleNameResult.containsAction("positive"));
       assertTrue("Middle Name ValidatorResult for the 'positive' action should have failed", !middleNameResult.isValid("positive"));
   }

   /**
    * If middle name is there and a positve int, then the required and int
    * dependent tests should pass, and the positive test should pass.
    *
    * @throws ValidatorException
    */
   public void testPassingDependentsPassingMain() throws ValidatorException {
       // Create bean to run test on.
       NameBean name = new NameBean();
       name.setMiddleName("2534");

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

       ValidatorResult middleNameResult = results.getValidatorResult("middleName");

       assertNotNull("Middle Name ValidatorResult should not be null.", middleNameResult);

       assertTrue("Middle Name ValidatorResult should contain the 'required' action.", middleNameResult.containsAction("required"));
       assertTrue("Middle Name ValidatorResult for the 'required' action should have passed", middleNameResult.isValid("required"));

       assertTrue("Middle Name ValidatorResult should contain the 'int' action.", middleNameResult.containsAction("int"));
       assertTrue("Middle Name ValidatorResult for the 'int' action should have passed", middleNameResult.isValid("int"));

       assertTrue("Middle Name ValidatorResult should contain the 'positive' action.", middleNameResult.containsAction("positive"));
       assertTrue("Middle Name ValidatorResult for the 'positive' action should have passed", middleNameResult.isValid("positive"));
   }
}
