/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/Attic/JXPathTest.java,v 1.1 2004/06/08 15:05:38 husted Exp $
 * $Revision: 1.1 $
 * $Date: 2004/06/08 15:05:38 $
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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;

/**                                                       
 * Performs Validation Test for jxpath validations.
 */                                                       
public class JXPathTest extends TestCommon {

   /**
    * The key used to retrieve the validator action.
    */
   protected static String ACTION = "jxpath";


   public JXPathTest(String name) {                  
       super(name);                                      
   }                                                     

   /**
    * Start the tests.
    *
    * @param theArgs the arguments. Not used
    */
   public static void main(String[] theArgs) {
       junit.awtui.TestRunner.main(new String[] {JXPathTest.class.getName()});
   }

   /**
    * @return a test suite (<code>TestSuite</code>) that includes all methods
    *         starting with "test"
    */
   public static Test suite() {
       // All methods starting with "test" will be executed in the test suite.
       return new TestSuite(JXPathTest.class);
   }

   /**
    * Load <code>ValidatorResources</code> from 
    * validator-jxpath.xml.
    */
   protected void setUp() throws IOException, SAXException {
      loadResources("validator-jxpath.xml");
   }

   protected void tearDown() {
   }

   /**
    * Tests the jxpath validation.
    */
   public void testJXPath() throws ValidatorException {
      // Create bean to run test on.
      ValueBean info = new ValueBean();

      info.setValue("foo");
      valueTest(info, "jxpathForm", true);
      info.setValue("foobar");
      valueTest(info, "jxpathForm", false);
   }
   
   /**
    * Tests jxpath validation test for a numeric range
    */
   public void testJXPath_numeric() throws ValidatorException {
      // Create bean to run test on.
      ValueBean info = new ValueBean();

      info.setValue("12");
      valueTest(info, "jxpathForm-numeric",true);
      info.setValue("123");
      valueTest(info, "jxpathForm-numeric",false);
      info.setValue("foobar");
      valueTest(info, "jxpathForm-numeric",false);
   }
   
   /**
    * Tests jxpath validation on nested objects
    */
   public void testJXPath_nested() throws ValidatorException {
      // Create bean to run test on.
      ObjectBean info = new ObjectBean();
      ValueBean kid = new ValueBean();
      kid.setValue("foo");
      info.setObject(kid);
      
      objectTest(info, "jxpathForm-nested",true);
      kid.setValue("foobar");
      objectTest(info, "jxpathForm-nested",false);
   }



   /**
    * Utlity class to run a test on a value.
    *
    * @param info	Value to run test on.
    * @param passed	Whether or not the test is expected to pass.
    */
   private void valueTest(ValueBean info, String form, boolean passed) throws ValidatorException {
      // Construct validator based on the loaded resources 
      // and the form key
      Validator validator = new Validator(resources, form);
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
      
      ValidatorResult result = results.getValidatorResult("value");

      assertNotNull(ACTION + " value ValidatorResult should not be null.", result);
      assertTrue("Value "+info.getValue()+" ValidatorResult should contain the '" + ACTION +"' action.", result.containsAction(ACTION));
      assertTrue("Value "+info.getValue()+"ValidatorResult for the '" + ACTION +"' action should have " + (passed ? "passed" : "failed") + ".", (passed ? result.isValid(ACTION) : !result.isValid(ACTION)));
    }
    
    /**
    * Utlity class to run a test on an object.
    *
    * @param info	Object to run test on.
    * @param passed	Whether or not the test is expected to pass.
    */
   private void objectTest(ObjectBean info, String form, boolean passed) throws ValidatorException {
      // Construct validator based on the loaded resources 
      // and the form key
      Validator validator = new Validator(resources, form);
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
      
      ValidatorResult result = results.getValidatorResult("object");

      assertNotNull(ACTION + " object ValidatorResult should not be null.", result);
      assertTrue("Object "+info.getObject()+" ValidatorResult should contain the '" + ACTION +"' action.", result.containsAction(ACTION));
      assertTrue("Object "+info.getObject()+"ValidatorResult for the '" + ACTION +"' action should have " + (passed ? "passed" : "failed") + ".", (passed ? result.isValid(ACTION) : !result.isValid(ACTION)));
    }
}                                                         
