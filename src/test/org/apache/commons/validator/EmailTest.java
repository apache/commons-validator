/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/EmailTest.java,v 1.18 2003/08/22 04:22:42 rleland Exp $
 * $Revision: 1.18 $
 * $Date: 2003/08/22 04:22:42 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
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
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

/**                                                       
 * <p>Performs Validation Test for e-mail validations.</p> 
 *
 * @author David Winterfeldt
 * @author David Graham
 * @author Rob Leland
 * @version $Revision: 1.18 $ $Date: 2003/08/22 04:22:42 $
*/                                                       
public class EmailTest extends TestCase {            
   
   /**
    * The key used to retrieve the set of validation 
    * rules from the xml file.
   */
   protected static String FORM_KEY = "emailForm";   

   /**
    * The key used to retrieve the validator action.
   */
   protected static String ACTION = "email";

   
   /**
    * Commons Logging instance.
   */
   private Log log = LogFactory.getLog(this.getClass());
   
   /**
    * Resources used for validation tests.
   */
   private ValidatorResources resources = null;
   
   public EmailTest(String name) {                  
       super(name);                                      
   }                                                     

   /**
    * Start the tests.
    *
    * @param theArgs the arguments. Not used
    */
   public static void main(String[] theArgs) {
       junit.awtui.TestRunner.main(new String[] {EmailTest.class.getName()});
   }

   /**
    * @return a test suite (<code>TestSuite</code>) that includes all methods
    *         starting with "test"
    */
   public static Test suite() {
       // All methods starting with "test" will be executed in the test suite.
       return new TestSuite(EmailTest.class);
   }

   /**
    * Load <code>ValidatorResources</code> from 
    * validator-regexp.xml.
   */
   protected void setUp() throws IOException, SAXException {
      // Load resources
      InputStream in = null;
      
      try {
         in = this.getClass().getResourceAsStream("validator-regexp.xml");
         resources = new ValidatorResources(in);
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
    * Tests the e-mail validation.
   */
   public void testEmail() throws ValidatorException {
      // Create bean to run test on.
      ValueBean info = new ValueBean();

      info.setValue("jsmith@apache.org");
      valueTest(info, true);
   }

    /**
     * Tests the e-mail validation.
     */
    public void testEmailExtension() throws ValidatorException {
    	// Create bean to run test on.
    	ValueBean info = new ValueBean();
    
    	info.setValue("jsmith@apache.org");
    	valueTest(info, true);
    
    	info.setValue("jsmith@apache.com");
    	valueTest(info, true);
    
    	info.setValue("jsmith@apache.net");
    	valueTest(info, true);
    
    	info.setValue("jsmith@apache.info");
    	valueTest(info, true);
    
    	info.setValue("jsmith@apache.infoo");
    	valueTest(info, false);
        
        info.setValue("jsmith@apache.");
        valueTest(info, false);
        
        info.setValue("jsmith@apache.c");
        valueTest(info, false);
    }

   /**
    * <p>Tests the e-mail validation with a dash in 
    * the address.</p>
   */
   public void testEmailWithDash() throws ValidatorException {
      // Create bean to run test on.
      ValueBean info = new ValueBean();

      info.setValue("andy.noble@data-workshop.com");
      valueTest(info, true);

      info.setValue("andy-noble@data-workshop.-com");
       valueTest(info, true);
       info.setValue("andy-noble@data-workshop.c-om");
       valueTest(info,true);
       info.setValue("andy-noble@data-workshop.co-m");
       valueTest(info, true);


   }

   /**
    * <p>Tests the e-mail validation with a dot at the end of 
    * the address.</p>
   */
   public void testEmailWithDotEnd() throws ValidatorException {
      // Create bean to run test on.
      ValueBean info = new ValueBean();

      info.setValue("andy.noble@data-workshop.com.");
      valueTest(info, false);

   }

    /**
     * <p>Tests the e-mail validation with an RCS-noncompliant character in 
     * the address.</p>
     */
    public void testEmailWithBogusCharacter() throws ValidatorException {
    	// Create bean to run test on.
    	ValueBean info = new ValueBean();
    
    	info.setValue("andy.noble@\u008fdata-workshop.com");
    	valueTest(info, false);
    
        // The ' character is valid in an email address.
    	info.setValue("andy.o'reilly@data-workshop.com");
    	valueTest(info, true);
        
        info.setValue("foo+bar@i.am.not.in.us.example.com");
        valueTest(info, true);
    }
   
   /**
    * Tests the email validation with commas.
    */

    public void testEmailWithCommas() throws ValidatorException {
       ValueBean info = new ValueBean();
       info.setValue("joeblow@apa,che.org");
       valueTest(info, false);
       info.setValue("joeblow@apache.o,rg");
       valueTest(info, false);
        info.setValue("joeblow@apache,org");
        valueTest(info, false);

    }

    /**
     * Write this test according to parts of RFC, as opposed to the type of character
     * that is being tested.

     * @throws ValidatorException
     */
    public void testEmailUserName() throws ValidatorException {
       ValueBean info = new ValueBean();
       info.setValue("joe1blow@apache.org");
       valueTest(info, true);
        info.setValue("joe$blow@apache.org");
        valueTest(info, true);
        info.setValue("joe-@apache.org");
        valueTest(info, true);
        info.setValue("joe_@apache.org");
        valueTest(info, true);

        //UnQuoted Special characters are invalid

        info.setValue("joe.@apache.org");
        valueTest(info, false);
        info.setValue("joe+@apache.org");
        valueTest(info, false);
        info.setValue("joe!@apache.org");
        valueTest(info, false);
        info.setValue("joe*@apache.org");
        valueTest(info, false);
        info.setValue("joe'@apache.org");
        valueTest(info, false);
        info.setValue("joe(@apache.org");
        valueTest(info, false);
        info.setValue("joe)@apache.org");
        valueTest(info, false);
        info.setValue("joe,@apache.org");
        valueTest(info, false);
        info.setValue("joe%45@apache.org");
        valueTest(info, false);
        info.setValue("joe;@apache.org");
        valueTest(info, false);
        info.setValue("joe?@apache.org");
        valueTest(info, false);
        info.setValue("joe&@apache.org");
        valueTest(info, false);
        info.setValue("joe=@apache.org");
        valueTest(info, false);

        //Quoted Special characters are valid
        info.setValue("\"joe.\"@apache.org");
        valueTest(info, true);
        info.setValue("\"joe+\"@apache.org");
        valueTest(info, true);
        info.setValue("\"joe!\"@apache.org");
        valueTest(info, true);
        info.setValue("\"joe*\"@apache.org");
        valueTest(info, true);
        info.setValue("\"joe'\"@apache.org");
        valueTest(info, true);
        info.setValue("\"joe(\"@apache.org");
        valueTest(info, true);
        info.setValue("\"joe)\"@apache.org");
        valueTest(info, true);
        info.setValue("\"joe,\"@apache.org");
        valueTest(info, true);
        info.setValue("\"joe%45\"@apache.org");
        valueTest(info, true);
        info.setValue("\"joe;\"@apache.org");
        valueTest(info, true);
        info.setValue("\"joe?\"@apache.org");
        valueTest(info, true);
        info.setValue("\"joe&\"@apache.org");
        valueTest(info, true);
        info.setValue("\"joe=\"@apache.org");
        valueTest(info, true);


    }



   /**
    * Utlity class to run a test on a value.
    *
    * @param	info	Value to run test on.
    * @param	passed	Whether or not the test is expected to pass.
   */
   private void valueTest(ValueBean info, boolean passed) throws ValidatorException {
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
      
      ValidatorResult result = results.getValidatorResult("value");

      assertNotNull(ACTION + " value ValidatorResult should not be null.", result);
      assertTrue("Value "+info.getValue()+" ValidatorResult should contain the '" + ACTION +"' action.", result.containsAction(ACTION));
      assertTrue("Value "+info.getValue()+"ValidatorResult for the '" + ACTION +"' action should have " + (passed ? "passed" : "failed") + ".", (passed ? result.isValid(ACTION) : !result.isValid(ACTION)));
   }
}                                                         
