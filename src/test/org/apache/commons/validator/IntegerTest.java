/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/IntegerTest.java,v 1.1 2002/03/30 04:21:03 dwinterfeldt Exp $
 * $Revision: 1.1 $
 * $Date: 2002/03/30 04:21:03 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */


package org.apache.commons.validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import junit.framework.Test;                           
import junit.framework.TestCase;                          
import junit.framework.TestSuite;
import junit.framework.AssertionFailedError;              
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogSource;

                                                          
/**                                                       
 * <p>Performs Validation Test for <code>int</code> validations.</p> 
 *
 * @author David Winterfeldt
 * @version $Revision: 1.1 $ $Date: 2002/03/30 04:21:03 $
*/                                                       
public class IntegerTest extends TestCase {            
   
   /**
    * The key used to retrieve the set of validation 
    * rules from the xml file.
   */
   protected static String FORM_KEY = "intForm";   

   /**
    * The key used to retrieve the validator action.
   */
   protected static String ACTION = "int";

   
   /**
    * Commons Logging instance.
   */
   private Log log = LogSource.getInstance(this.getClass().getName());
   
   /**
    * Resources used for validation tests.
   */
   private ValidatorResources resources = null;
   
   public IntegerTest(String name) {                  
       super(name);                                      
   }                                                     

   /**
    * Start the tests.
    *
    * @param theArgs the arguments. Not used
    */
   public static void main(String[] theArgs) {
       junit.awtui.TestRunner.main(new String[] {IntegerTest.class.getName()});
   }

   /**
    * @return a test suite (<code>TestSuite</code>) that includes all methods
    *         starting with "test"
    */
   public static Test suite() {
       // All methods starting with "test" will be executed in the test suite.
       return new TestSuite(IntegerTest.class);
   }

   /**
    * Load <code>ValidatorResources</code> from 
    * validator-name-required.xml.
   */
   protected void setUp() throws IOException {
      // Load resources
      InputStream in = null;
      resources = new ValidatorResources();
      
      try {
         in = this.getClass().getResourceAsStream("validator-numeric.xml");
         ValidatorResourcesInitializer.initialize(resources, in);
      } catch (IOException e) {
         log.error(e.getMessage(), e);
         throw e;
      } finally {
         if (in != null) {
            try { in.close(); } catch (Exception e) {}	
         }
      }
   }

   protected void tearDown() {
   }

   /**
    * Tests the int validation.
   */
   public void testInt() throws ValidatorException {
      // Create bean to run test on.
      ValueBean info = new ValueBean();
      info.setValue("0");
      
      valueTest(info, true);
   }

   /**
    * Tests the int validation.
   */
   public void testIntMin() throws ValidatorException {
      // Create bean to run test on.
      ValueBean info = new ValueBean();
      info.setValue(new Integer(Integer.MIN_VALUE).toString());
      
      valueTest(info, true);
   }

   /**
    * Tests the int validation.
   */
   public void testIntegerMax() throws ValidatorException {
      // Create bean to run test on.
      ValueBean info = new ValueBean();
      info.setValue(new Integer(Integer.MAX_VALUE).toString());
      
      valueTest(info, true);
   }

   /**
    * Tests the int validation failure.
   */
   public void testIntFailure() throws ValidatorException {
      // Create bean to run test on.
      ValueBean info = new ValueBean();
      
      valueTest(info, false);
   }

   /**
    * Tests the int validation failure.
   */
   public void testIntBeyondMin() throws ValidatorException {
      // Create bean to run test on.
      ValueBean info = new ValueBean();
      info.setValue(Integer.MIN_VALUE + "1");
      
      valueTest(info, false);
   }
   
   /**
    * Tests the int validation failure.
   */
   public void testIntBeyondMax() throws ValidatorException {
      // Create bean to run test on.
      ValueBean info = new ValueBean();
      info.setValue(Integer.MAX_VALUE + "1");
      
      valueTest(info, false);
   }
   
   /**
    * Utlity class to run a test on a value.
    *
    * @param	info	Value to run test on.
    * @param	passed	Whether or not the test is expected to pass.
   */
   private void valueTest(Object info, boolean passed) throws ValidatorException {
      // Construct validator based on the loaded resources 
      // and the form key
      Validator validator = new Validator(resources, FORM_KEY);
      // add the name bean to the validator as a resource 
      // for the validations to be performed on.
      validator.addResource(Validator.BEAN_KEY, info);

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
      assertTrue(ACTION + " value ValidatorResult should contain the '" + ACTION +"' action.", result.containsAction(ACTION));
      assertTrue(ACTION + " value ValidatorResult for the '" + ACTION +"' action should have " + (passed ? "passed" : "failed") + ".", (passed ? result.isValid(ACTION) : !result.isValid(ACTION)));
   }
}                                                         