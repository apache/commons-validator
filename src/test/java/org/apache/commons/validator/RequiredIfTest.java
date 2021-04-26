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
public class RequiredIfTest extends AbstractCommonTest {

   /**
    * The key used to retrieve the set of validation
    * rules from the xml file.
    */
   protected static String FORM_KEY = "nameForm";

   /**
    * The key used to retrieve the validator action.
    */
   protected static String ACTION = "requiredif";

   public RequiredIfTest(String name) {
       super(name);
   }

   /**
    * Load <code>ValidatorResources</code> from
    * validator-requiredif.xml.
    */
   @Override
protected void setUp() throws IOException, SAXException {
      // Load resources
      loadResources("RequiredIfTest-config.xml");
   }

   @Override
protected void tearDown() {
   }

   /**
    * With nothing provided, we should pass since the fields only fail on
    * null if the other field is non-blank.
    */
   public void testRequired() throws ValidatorException {
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

      assertNotNull("First Name ValidatorResult should not be null.", firstNameResult);
      assertTrue("First Name ValidatorResult should contain the '" + ACTION +"' action.", firstNameResult.containsAction(ACTION));
      assertTrue("First Name ValidatorResult for the '" + ACTION +"' action should have passed.", firstNameResult.isValid(ACTION));

      assertNotNull("Last Name ValidatorResult should not be null.", lastNameResult);
      assertTrue("Last Name ValidatorResult should contain the '" + ACTION +"' action.", lastNameResult.containsAction(ACTION));
      assertTrue("Last Name ValidatorResult for the '" + ACTION +"' action should have passed.", lastNameResult.isValid(ACTION));
   }

   /**
    * Tests the required validation for first name if it is blank.
    */
   public void testRequiredFirstNameBlank() throws ValidatorException {
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

      assertNotNull("First Name ValidatorResult should not be null.", firstNameResult);
      assertTrue("First Name ValidatorResult should contain the '" + ACTION +"' action.", firstNameResult.containsAction(ACTION));
      assertTrue("First Name ValidatorResult for the '" + ACTION +"' action should have failed.", !firstNameResult.isValid(ACTION));

      assertNotNull("Last Name ValidatorResult should not be null.", lastNameResult);
      assertTrue("Last Name ValidatorResult should contain the '" + ACTION +"' action.", lastNameResult.containsAction(ACTION));
      assertTrue("Last Name ValidatorResult for the '" + ACTION +"' action should have passed.", lastNameResult.isValid(ACTION));
   }

   /**
    * Tests the required validation for last name.
    */
   public void testRequiredFirstName() throws ValidatorException {
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

      assertNotNull("First Name ValidatorResult should not be null.", firstNameResult);
      assertTrue("First Name ValidatorResult should contain the '" + ACTION +"' action.", firstNameResult.containsAction(ACTION));
      assertTrue("First Name ValidatorResult for the '" + ACTION +"' action should have passed.", firstNameResult.isValid(ACTION));

      assertNotNull("Last Name ValidatorResult should not be null.", lastNameResult);
      assertTrue("Last Name ValidatorResult should contain the '" + ACTION +"' action.", lastNameResult.containsAction(ACTION));
      assertTrue("Last Name ValidatorResult for the '" + ACTION +"' action should have passed.", lastNameResult.isValid(ACTION));
   }

   /**
    * Tests the required validation for last name if it is blank.
    */
   public void testRequiredLastNameBlank() throws ValidatorException {
      // Create bean to run test on.
      NameBean name = new NameBean();
      name.setFirstName("Joe");
      name.setLastName("");

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

      assertNotNull("First Name ValidatorResult should not be null.", firstNameResult);
      assertTrue("First Name ValidatorResult should contain the '" + ACTION +"' action.", firstNameResult.containsAction(ACTION));
      assertTrue("First Name ValidatorResult for the '" + ACTION +"' action should have passed.", firstNameResult.isValid(ACTION));

      assertNotNull("Last Name ValidatorResult should not be null.", lastNameResult);
      assertTrue("Last Name ValidatorResult should contain the '" + ACTION +"' action.", lastNameResult.containsAction(ACTION));
      assertTrue("Last Name ValidatorResult for the '" + ACTION +"' action should have failed.", !lastNameResult.isValid(ACTION));
   }

   /**
    * Tests the required validation for last name.
    */
   public void testRequiredLastName() throws ValidatorException {
      // Create bean to run test on.
      NameBean name = new NameBean();
      name.setFirstName("Joe");
      name.setLastName("Smith");

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

      assertNotNull("First Name ValidatorResult should not be null.", firstNameResult);
      assertTrue("First Name ValidatorResult should contain the '" + ACTION +"' action.", firstNameResult.containsAction(ACTION));
      assertTrue("First Name ValidatorResult for the '" + ACTION +"' action should have passed.", firstNameResult.isValid(ACTION));

      assertNotNull("Last Name ValidatorResult should not be null.", lastNameResult);
      assertTrue("Last Name ValidatorResult should contain the '" + ACTION +"' action.", lastNameResult.containsAction(ACTION));
      assertTrue("Last Name ValidatorResult for the '" + ACTION +"' action should have passed.", lastNameResult.isValid(ACTION));

   }

}
