/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/TypeTest.java,v 1.13 2003/09/06 05:17:59 rleland Exp $
 * $Revision: 1.13 $
 * $Date: 2003/09/06 05:17:59 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names, "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
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
import java.util.Iterator;
import java.util.Map;

import junit.framework.Test;

import junit.framework.TestSuite;


import org.xml.sax.SAXException;

                                                          
/**                                                       
 * <p>Performs Validation Test for type validations.</p> 
 *
 * @author David Winterfeldt
 * @version $Revision: 1.13 $ $Date: 2003/09/06 05:17:59 $
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