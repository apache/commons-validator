/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/TypeTest.java,v 1.16 2004/02/21 17:10:30 rleland Exp $
 * $Revision: 1.16 $
 * $Date: 2004/02/21 17:10:30 $
 *
 * ====================================================================
 * Copyright 2001-2004 The Apache Software Foundation
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
import java.util.Iterator;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;
                                                          
/**                                                       
 * Performs Validation Test for type validations.
 */                                                       
public class TypeTest extends TestCommon {
   
   /**
    * The key used to retrieve the set of validation 
    * rules from the xml file.
    */
   protected static String FORM_KEY = "typeForm";   

   /**
    * The key used to retrieve the validator action.
    */
   protected static String ACTION = "byte";

   public TypeTest(String name) {                  
       super(name);                                      
   }                                                     

   /**
    * Start the tests.
    *
    * @param theArgs the arguments. Not used
    */
   public static void main(String[] theArgs) {
       junit.awtui.TestRunner.main(new String[] {TypeTest.class.getName()});
   }

   /**
    * @return a test suite (<code>TestSuite</code>) that includes all methods
    *         starting with "test"
    */
   public static Test suite() {
       // All methods starting with "test" will be executed in the test suite.
       return new TestSuite(TypeTest.class);
   }

   /**
    * Load <code>ValidatorResources</code> from 
    * validator-type.xml.
    */
   protected void setUp() throws IOException, SAXException {
      // Load resources
      loadResources("validator-type.xml");
   }

   protected void tearDown() {
   }

   /**
    * Tests the byte validation.
    */
   public void testType() throws ValidatorException {
      // Create bean to run test on.
      TypeBean info = new TypeBean();
      info.setByte("12");
      info.setShort("129");
      info.setInteger("-144");
      info.setLong("88000");
      info.setFloat("12.1555f");
      info.setDouble("129.1551511111d");
      
      // Construct validator based on the loaded resources 
      // and the form key
      Validator validator = new Validator(resources, FORM_KEY);
      // add the name bean to the validator as a resource 
      // for the validations to be performed on.
      validator.setParameter(Validator.BEAN_PARAM, info);

      // Get results of the validation.
      ValidatorResults results = null;
      
      // throws ValidatorException, 
      // but we aren't catching for testing 
      // since no validation methods we use 
      // throw this
      results = validator.validate();
      
      assertNotNull("Results are null.", results);
      
      Map hResultValues = results.getResultValueMap();

      assertTrue("Expecting byte result to be an instance of Byte.", (hResultValues.get("byte") instanceof Byte));
      assertTrue("Expecting short result to be an instance of Short.", (hResultValues.get("short") instanceof Short));
      assertTrue("Expecting integer result to be an instance of Integer.", (hResultValues.get("integer") instanceof Integer));
      assertTrue("Expecting long result to be an instance of Long.", (hResultValues.get("long") instanceof Long));
      assertTrue("Expecting float result to be an instance of Float.", (hResultValues.get("float") instanceof Float));
      assertTrue("Expecting double result to be an instance of Double.", (hResultValues.get("double") instanceof Double));
      
      for (Iterator i = hResultValues.keySet().iterator(); i.hasNext(); ) {
         String key = (String)i.next();
         Object value = hResultValues.get(key);
         
         assertNotNull("value ValidatorResults.getResultValueMap() should not be null.", value);
      }
      
      //ValidatorResult result = results.getValidatorResult("value");
      
      //assertNotNull(ACTION + " value ValidatorResult should not be null.", result);
      //assertTrue(ACTION + " value ValidatorResult should contain the '" + ACTION +"' action.", result.containsAction(ACTION));
      //assertTrue(ACTION + " value ValidatorResult for the '" + ACTION +"' action should have " + (passed ? "passed" : "failed") + ".", (passed ? result.isValid(ACTION) : !result.isValid(ACTION)));

   }

}                                                         