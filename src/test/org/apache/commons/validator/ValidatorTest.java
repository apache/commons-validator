/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/ValidatorTest.java,v 1.4 2002/03/30 04:33:18 dwinterfeldt Exp $
 * $Revision: 1.4 $
 * $Date: 2002/03/30 04:33:18 $
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.text.DateFormat;
import java.text.ParseException;
import junit.framework.Test;                           
import junit.framework.TestCase;                          
import junit.framework.TestSuite;
import junit.framework.AssertionFailedError;              

                                                          
/**                                                       
 * <p>Performs Validation Test.</p> 
 *
 * @author David Winterfeldt
 * @version $Revision: 1.4 $ $Date: 2002/03/30 04:33:18 $
*/                                                       
public class ValidatorTest extends TestCase {            
                                                          
   public ValidatorTest(String name) {                  
       super(name);                                      
   }                                                     

   /**
    * Start the tests. 
    *
    * @param theArgs the arguments. Not used
    */
   public static void main(String[] theArgs) {
       junit.awtui.TestRunner.main(new String[] {ValidatorTest.class.getName()});
   }

   /**
    * @return a test suite (<code>TestSuite</code>) that includes all methods
    *         starting with "test"
    */
   public static Test suite() {
       // All methods starting with "test" will be executed in the test suite.
       return new TestSuite(ValidatorTest.class);
   }

   protected void setUp() {
   }

   protected void tearDown() {
   }

   /**
    * Verify that one value generates an error and the other passes.  The validation 
    * method being tested returns an object (<code>null</code> will be considered an error).
   */
   public void testManualObject() {
      // property name of the method we are validating
      String property = "date";
      // name of ValidatorAction
      String action = "date";
      
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
      resources.put(fs);
      resources.process();

      List l = new ArrayList();

      TestBean bean = new TestBean();  
      bean.setDate("2/3/1999");
      
      Validator validator = new Validator(resources, "testForm");
      validator.addResource(Validator.BEAN_KEY, bean);

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
      resources.put(fs);
      resources.process();

      List l = new ArrayList();

      TestBean bean = new TestBean();  
      bean.setLetter("A");
      
      Validator validator = new Validator(resources, "testForm");
      validator.addResource(Validator.BEAN_KEY, bean);
      validator.addResource("java.util.List", l);

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
   public static boolean isCapLetter(Object bean, Field field, List l) {
      String value = ValidatorUtil.getValueAsString(bean, field.getProperty());

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
      String value = ValidatorUtil.getValueAsString(bean, field.getProperty());
      Date date = null;
      
      try {
         DateFormat formatter = null;
         formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
            
         formatter.setLenient(false);
             
         date = formatter.parse(value);
      } catch (ParseException e) {
         System.out.println("ValidatorTest::formatDate - " + e.getMessage());
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