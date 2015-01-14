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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import junit.framework.TestCase;

import org.apache.commons.validator.util.ValidatorUtils;
                                                          
/**                                                       
 * Performs Validation Test.
 *
 * @version $Revision$
 */
public class ValidatorTest extends TestCase {            
                                                          
   public ValidatorTest(String name) {                  
       super(name);                                      
   }                                                     

   /**
    * Verify that one value generates an error and the other passes.  The validation 
    * method being tested returns an object (<code>null</code> will be considered an error).
    */
   public void testManualObject() {
        //     property name of the method we are validating
        String property = "date";
        // name of ValidatorAction
        String action = "date";
        ValidatorResources resources = setupDateResources(property, action);

      TestBean bean = new TestBean();  
      bean.setDate("2/3/1999");
      
      Validator validator = new Validator(resources, "testForm");
      validator.setParameter(Validator.BEAN_PARAM, bean);

      try {
         ValidatorResults results = validator.validate();
         
         assertNotNull("Results are null.", results);
         
         ValidatorResult result = results.getValidatorResult(property);
         
         assertNotNull("Results are null.", results);
         
         assertTrue("ValidatorResult does not contain '" + action + "' validator result.", result.containsAction(action));
         
         assertTrue("Validation of the date formatting has failed.", result.isValid(action));
      } catch (Exception e) {
         fail("An exception was thrown while calling Validator.validate()");
      }

      bean.setDate("2/30/1999");
      
      try {
         ValidatorResults results = validator.validate();
         
         assertNotNull("Results are null.", results);
         
         ValidatorResult result = results.getValidatorResult(property);
         
         assertNotNull("Results are null.", results);
         
         assertTrue("ValidatorResult does not contain '" + action + "' validator result.", result.containsAction(action));
         
         assertTrue("Validation of the date formatting has passed when it should have failed.", !result.isValid(action));
      } catch (Exception e) {
         fail("An exception was thrown while calling Validator.validate()");
      }

   }
   
   public void testOnlyReturnErrors() throws ValidatorException {
        //     property name of the method we are validating
        String property = "date";
        // name of ValidatorAction
        String action = "date";
        ValidatorResources resources = setupDateResources(property, action);
    
        TestBean bean = new TestBean();
        bean.setDate("2/3/1999");
    
        Validator validator = new Validator(resources, "testForm");
        validator.setParameter(Validator.BEAN_PARAM, bean);
    
        ValidatorResults results = validator.validate();
    
        assertNotNull(results);
    
        // Field passed and should be in results
        assertTrue(results.getPropertyNames().contains(property));
        
        // Field passed but should not be in results
        validator.setOnlyReturnErrors(true);
        results = validator.validate();
        assertFalse(results.getPropertyNames().contains(property));    
   }
   
   public void testOnlyValidateField() throws ValidatorException {
        //     property name of the method we are validating
        String property = "date";
        // name of ValidatorAction
        String action = "date";
        ValidatorResources resources = setupDateResources(property, action);
    
        TestBean bean = new TestBean();
        bean.setDate("2/3/1999");
    
        Validator validator = new Validator(resources, "testForm", property);
        validator.setParameter(Validator.BEAN_PARAM, bean);
    
        ValidatorResults results = validator.validate();
    
        assertNotNull(results);
    
        // Field passed and should be in results
        assertTrue(results.getPropertyNames().contains(property));
   }
   
   
    private ValidatorResources setupDateResources(String property, String action) {
    
        ValidatorResources resources = new ValidatorResources();
    
        ValidatorAction va = new ValidatorAction();
        va.setName(action);
        va.setClassname("org.apache.commons.validator.ValidatorTest");
        va.setMethod("formatDate");
        va.setMethodParams("java.lang.Object,org.apache.commons.validator.Field");
    
        FormSet fs = new FormSet();
        Form form = new Form();
        form.setName("testForm");
        Field field = new Field();
        field.setProperty(property);
        field.setDepends(action);
        form.addField(field);
        fs.addForm(form);
    
        resources.addValidatorAction(va);
        resources.addFormSet(fs);
        resources.process();
    
        return resources;
    }
                                                          
   /**
    * Verify that one value generates an error and the other passes.  The validation 
    * method being tested returns a <code>boolean</code> value.
    */
   public void testManualBoolean() {
      ValidatorResources resources = new ValidatorResources();

      ValidatorAction va = new ValidatorAction();
      va.setName("capLetter");
      va.setClassname("org.apache.commons.validator.ValidatorTest");
      va.setMethod("isCapLetter");
      va.setMethodParams("java.lang.Object,org.apache.commons.validator.Field,java.util.List");
      
      FormSet fs = new FormSet();
      Form form = new Form();
      form.setName("testForm");
      Field field = new Field();
      field.setProperty("letter");
      field.setDepends("capLetter");
      form.addField(field);
      fs.addForm(form);
      
      resources.addValidatorAction(va);
      resources.addFormSet(fs);
      resources.process();

      List<?> l = new ArrayList<Object>();

      TestBean bean = new TestBean();  
      bean.setLetter("A");
      
      Validator validator = new Validator(resources, "testForm");
      validator.setParameter(Validator.BEAN_PARAM, bean);
      validator.setParameter("java.util.List", l);

      try {
         validator.validate();
      } catch (Exception e) {
         fail("An exception was thrown while calling Validator.validate()");
      }

      assertEquals("Validation of the letter 'A'.", 0, l.size());

      l.clear();       
      bean.setLetter("AA");

      try {
         validator.validate();
      } catch (Exception e) {
         fail("An exception was thrown while calling Validator.validate()");
      }
      
      assertEquals("Validation of the letter 'AA'.", 1, l.size());
   }

   /**
    * Checks if the field is one upper case letter between 'A' and 'Z'.
    */
   public static boolean isCapLetter(Object bean, Field field, List<String> l) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      if (value != null && value.length() == 1) {
         if (value.charAt(0) >= 'A' && value.charAt(0) <= 'Z') {
            return true;
         } else {
            l.add("Error");
            return false;
         }
      } else {
         l.add("Error");
         return false;
      }
   }

   /**
    * Formats a <code>String</code> to a <code>Date</code>.  
    * The <code>Validator</code> will interpret a <code>null</code> 
    * as validation having failed.
    */
   public static Date formatDate(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      Date date = null;
      
      try {
         DateFormat formatter = null;
         formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
            
         formatter.setLenient(false);
             
         date = formatter.parse(value);
      } catch (ParseException e) {
         System.out.println("ValidatorTest.formatDate() - " + e.getMessage());
      }
   
      return date;
   }
       
   public class TestBean {
      private String letter = null;
      private String date = null;
      
      public String getLetter() {
         return letter;
      }
      
      public void setLetter(String letter) {
         this.letter = letter;
      }

      public String getDate() {
         return date;
      }
      
      public void setDate(String date) {
         this.date = date;
      }
   }

}                                                         