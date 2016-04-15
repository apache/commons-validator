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
 * Test ValidatorResults.
 *
 * @version $Revision$
 */
public class ValidatorResultsTest extends AbstractCommonTest {

   private static final String FORM_KEY = "nameForm";
   private static final String firstNameField  = "firstName";
   private static final String middleNameField = "middleName";
   private static final String lastNameField   = "lastName";

   private String firstName;
   private String middleName;
   private String lastName;

   /**
    * Constructor.
    */
   public ValidatorResultsTest(String name) {
       super(name);
   }

   /**
    * Load <code>ValidatorResources</code> from
    * ValidatorResultsTest-config.xml.
    */
   @Override
protected void setUp() throws IOException, SAXException {
      // Load resources
      loadResources("ValidatorResultsTest-config.xml");

      // initialize values
      firstName  = "foo";
      middleName = "123";
      lastName   = "456";

   }

   @Override
protected void tearDown() {
   }

   /**
    * Test all validations ran and passed.
    */
   public void testAllValid() throws ValidatorException {

      // Create bean to run test on.
      NameBean bean = createNameBean();

      // Validate.
      ValidatorResults results = validate(bean);

      // Check results
      checkValidatorResult(results, firstNameField,  "required", true);
      checkValidatorResult(results, middleNameField, "required", true);
      checkValidatorResult(results, middleNameField, "int",      true);
      checkValidatorResult(results, middleNameField, "positive", true);
      checkValidatorResult(results, lastNameField,   "required", true);
      checkValidatorResult(results, lastNameField,   "int",      true);

   }

   /**
    * Test some validations failed and some didn't run.
    */
   public void testErrors() throws ValidatorException {

      middleName = "XXX";
      lastName = null;

      // Create bean to run test on.
      NameBean bean = createNameBean();

      // Validate.
      ValidatorResults results = validate(bean);

      // Check results
      checkValidatorResult(results, firstNameField,  "required", true);
      checkValidatorResult(results, middleNameField, "required", true);
      checkValidatorResult(results, middleNameField, "int",      false);
      checkNotRun(results, middleNameField, "positive");
      checkValidatorResult(results, lastNameField,   "required", false);
      checkNotRun(results, lastNameField,   "int");

   }

   /**
    * Check a validator has not been run for a field and the result.
    */
   private void checkNotRun(ValidatorResults results, String field, String action) {
      ValidatorResult result = results.getValidatorResult(field);
      assertNotNull(field + " result",  result);
      assertFalse(field + "[" + action + "] run", result.containsAction(action));
      // System.out.println(field + "[" + action + "] not run");
   }

   /**
    * Check a validator has run for a field and the result.
    */
   private void checkValidatorResult(ValidatorResults results, String field, String action, boolean expected) {
      ValidatorResult result = results.getValidatorResult(field);
      // System.out.println(field + "[" + action + "]=" + result.isValid(action));
      assertNotNull(field + " result",  result);
      assertTrue(field + "[" + action + "] not run", result.containsAction(action));
      assertEquals(field + "[" + action + "] result", expected, result.isValid(action));
   }

   /**
    * Create a NameBean.
    */
   private NameBean createNameBean() {
      NameBean name = new NameBean();
      name.setFirstName(firstName);
      name.setMiddleName(middleName);
      name.setLastName(lastName);
      return name;
   }

   /**
    * Validate results.
    */
   private ValidatorResults validate(Object bean) throws ValidatorException  {

      // Construct validator based on the loaded resources
      // and the form key
      Validator validator = new Validator(resources, FORM_KEY);

      // add the name bean to the validator as a resource
      // for the validations to be performed on.
      validator.setParameter(Validator.BEAN_PARAM, bean);

      // Get results of the validation.
      ValidatorResults results = validator.validate();

      return results;

   }

}
