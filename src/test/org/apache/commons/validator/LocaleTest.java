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
import java.util.Locale;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;
                                                          
/**                                                       
 * Performs Validation Test for <code>long</code> validations.
 */                                                       
public class LocaleTest extends TestCommon {
   
   /**
    * The key used to retrieve the set of validation 
    * rules from the xml file.
    */
   protected static String FORM_KEY = "nameForm";   

   /**
    * The key used to retrieve the validator action.
    */
   protected static String ACTION = "required";


   public LocaleTest(String name) {                  
       super(name);                                      
   }                                                     

   /**
    * Start the tests.
    *
    * @param theArgs the arguments. Not used
    */
   public static void main(String[] theArgs) {
       junit.awtui.TestRunner.main(new String[] {LocaleTest.class.getName()});
   }

   /**
    * @return a test suite (<code>TestSuite</code>) that includes all methods
    *         starting with "test"
    */
   public static Test suite() {
       // All methods starting with "test" will be executed in the test suite.
       return new TestSuite(LocaleTest.class);
   }

   /**
    * Load <code>ValidatorResources</code> from 
    * validator-locale.xml.
    */
   protected void setUp() throws IOException, SAXException {
      // Load resources
      loadResources("validator-locale.xml");
   }

   protected void tearDown() {
   }

   /**
    * See what happens when we try to validate with a Locale, Country and variant
    */
   public void testLocale1() throws ValidatorException {
      // Create bean to run test on.
      NameBean name = new NameBean();
      name.setFirstName("");
      name.setLastName("");
      
      valueTest(name, new Locale("en", "US", "TEST1"), false, false);
   }

   /**
    * See what happens when we try to validate with a Locale, Country and variant
    */
   public void testLocale2() throws ValidatorException {
      // Create bean to run test on.
      NameBean name = new NameBean();
      name.setFirstName("");
      name.setLastName("");
      
      valueTest(name, new Locale("en", "US", "TEST2"), true, false);
   }

   /**
    * See what happens when we try to validate with a Locale, Country and variant
    */
   public void testLocale3() throws ValidatorException {
      // Create bean to run test on.
      NameBean name = new NameBean();
      name.setFirstName("");
      name.setLastName("");
      
      valueTest(name, new Locale("en", "UK"), false, true);
   }

   /**
    * Utlity class to run a test on a value.
    *
    * @param info	Value to run test on.
    * @param passed	Whether or not the test is expected to pass.
    */
    private void valueTest(Object name, Locale loc, boolean firstGood, boolean lastGood)
        throws ValidatorException {
            
        // Construct validator based on the loaded resources 
        // and the form key
        Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource 
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, name);
        validator.setParameter(Validator.LOCALE_PARAM, loc);
        // Get results of the validation.
        ValidatorResults results = null;
    
        // throws ValidatorException, 
        // but we aren't catching for testing 
        // since no validation methods we use 
        // throw this
        results = validator.validate();
    
        assertNotNull("Results are null.", results);
    
        ValidatorResult resultlast = results.getValidatorResult("lastName");
        ValidatorResult resultfirst = results.getValidatorResult("firstName");
    
        if (firstGood) {
            assertNull(resultfirst);
        } else {
            assertNotNull(resultfirst);
        }
        
        if (lastGood) {
            assertNull(resultlast);
        } else {
            assertNotNull(resultlast);
        }
    }
}
