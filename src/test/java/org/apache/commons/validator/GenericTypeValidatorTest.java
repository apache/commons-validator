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
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.xml.sax.SAXException;
                                                          
/**                                                       
 * Performs Validation Test for type validations.
 *
 * @version $Revision$
 */
public class GenericTypeValidatorTest extends AbstractCommonTest {
   
   /**
    * The key used to retrieve the set of validation 
    * rules from the xml file.
    */
   protected static String FORM_KEY = "typeForm";   

   /**
    * The key used to retrieve the validator action.
    */
   protected static String ACTION = "byte";

   public GenericTypeValidatorTest(String name) {                  
       super(name);                                      
   }                                                     

   /**
    * Load <code>ValidatorResources</code> from 
    * validator-type.xml.
    */
   @Override
protected void setUp() throws IOException, SAXException {
      // Load resources
      loadResources("GenericTypeValidatorTest-config.xml");
   }

   @Override
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
      
      Map<String, ?> hResultValues = results.getResultValueMap();

      assertTrue("Expecting byte result to be an instance of Byte.", (hResultValues.get("byte") instanceof Byte));
      assertTrue("Expecting short result to be an instance of Short.", (hResultValues.get("short") instanceof Short));
      assertTrue("Expecting integer result to be an instance of Integer.", (hResultValues.get("integer") instanceof Integer));
      assertTrue("Expecting long result to be an instance of Long.", (hResultValues.get("long") instanceof Long));
      assertTrue("Expecting float result to be an instance of Float.", (hResultValues.get("float") instanceof Float));
      assertTrue("Expecting double result to be an instance of Double.", (hResultValues.get("double") instanceof Double));
      
      for (Iterator<String> i = hResultValues.keySet().iterator(); i.hasNext(); ) {
         String key = i.next();
         Object value = hResultValues.get(key);
         
         assertNotNull("value ValidatorResults.getResultValueMap() should not be null.", value);
      }
      
      //ValidatorResult result = results.getValidatorResult("value");
      
      //assertNotNull(ACTION + " value ValidatorResult should not be null.", result);
      //assertTrue(ACTION + " value ValidatorResult should contain the '" + ACTION +"' action.", result.containsAction(ACTION));
      //assertTrue(ACTION + " value ValidatorResult for the '" + ACTION +"' action should have " + (passed ? "passed" : "failed") + ".", (passed ? result.isValid(ACTION) : !result.isValid(ACTION)));

   }
   
   /**
    * Tests the us locale
    */
   public void testUSLocale() throws ValidatorException {
      // Create bean to run test on.
      TypeBean info = new TypeBean();
      info.setByte("12");
      info.setShort("129");
      info.setInteger("-144");
      info.setLong("88000");
      info.setFloat("12.1555");
      info.setDouble("129.1551511111");
      info.setDate("12/21/2010");
      localeTest(info, Locale.US);
   }

   /**
    * Tests the fr locale.
    */
   public void testFRLocale() throws ValidatorException {
      // Create bean to run test on.
      TypeBean info = new TypeBean();
      info.setByte("12");
      info.setShort("-129");
      info.setInteger("1443");
      info.setLong("88000");
      info.setFloat("12,1555");
      info.setDouble("129,1551511111");
      info.setDate("21/12/2010");
      Map<String, ?> map = localeTest(info, Locale.FRENCH);
      assertTrue("float value not correct", ((Float)map.get("float")).intValue() == 12);
      assertTrue("double value not correct", ((Double)map.get("double")).intValue() == 129);
  }

  /**
    * Tests the locale.
    */
   private Map<String, ?> localeTest(TypeBean info, Locale locale) throws ValidatorException {
     
      // Construct validator based on the loaded resources 
      // and the form key
      Validator validator = new Validator(resources, "typeLocaleForm");
      // add the name bean to the validator as a resource 
      // for the validations to be performed on.
      validator.setParameter(Validator.BEAN_PARAM, info);
      validator.setParameter("java.util.Locale", locale);

      // Get results of the validation.
      ValidatorResults results = null;
      
      // throws ValidatorException, 
      // but we aren't catching for testing 
      // since no validation methods we use 
      // throw this
      results = validator.validate();
      
      assertNotNull("Results are null.", results);
      
      Map<String, ?> hResultValues = results.getResultValueMap();

      assertTrue("Expecting byte result to be an instance of Byte for locale: "+locale, (hResultValues.get("byte") instanceof Byte));
      assertTrue("Expecting short result to be an instance of Short for locale: "+locale, (hResultValues.get("short") instanceof Short));
      assertTrue("Expecting integer result to be an instance of Integer for locale: "+locale, (hResultValues.get("integer") instanceof Integer));
      assertTrue("Expecting long result to be an instance of Long for locale: "+locale, (hResultValues.get("long") instanceof Long));
      assertTrue("Expecting float result to be an instance of Float for locale: "+locale, (hResultValues.get("float") instanceof Float));
      assertTrue("Expecting double result to be an instance of Double for locale: "+locale, (hResultValues.get("double") instanceof Double));
      assertTrue("Expecting date result to be an instance of Date for locale: "+locale, (hResultValues.get("date") instanceof Date));
      
      for (Iterator<String> i = hResultValues.keySet().iterator(); i.hasNext(); ) {
         String key = i.next();
         Object value = hResultValues.get(key);
         
         assertNotNull("value ValidatorResults.getResultValueMap() should not be null for locale: "+locale, value);
      }
      return hResultValues;
   }

}                                                         