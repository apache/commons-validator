/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/LocaleTest.java,v 1.11 2003/10/05 16:41:09 dgraham Exp $
 * $Revision: 1.11 $
 * $Date: 2003/10/05 16:41:09 $
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
import java.util.Locale;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;
                                                          
/**                                                       
 * <p>Performs Validation Test for <code>long</code> validations.</p> 
 *
 * @author David Winterfeldt
 * @version $Revision: 1.11 $ $Date: 2003/10/05 16:41:09 $
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
    * @param	info	Value to run test on.
    * @param	passed	Whether or not the test is expected to pass.
   */
   private void valueTest(Object name, Locale loc, boolean firstGood, boolean lastGood) throws ValidatorException {
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
	  assertNull(ACTION + " firstName ValidatorResult should be null.", resultfirst);
      } else {
	  assertNotNull(ACTION + " firstName ValidatorResult should not be null.", resultfirst);
      }
      if (lastGood) {
	  assertNull(ACTION + " lastName ValidatorResult should be null.", resultlast);
      } else {
	  assertNotNull(ACTION + " lastName ValidatorResult should not be null.", resultlast);
      }
   }
}
