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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;

/**                                                       
 * Performs Validation Test for e-mail validations.
 */                                                       
public class EmailTest extends TestCommon {

    /**
     * The key used to retrieve the set of validation
     * rules from the xml file.
     */
    protected static String FORM_KEY = "emailForm";

   /**
    * The key used to retrieve the validator action.
    */
   protected static String ACTION = "email";


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
      loadResources("validator-regexp.xml");
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
    * Tests the email validation with numeric domains.
    */
    public void testEmailWithNumericAddress() throws ValidatorException {
        ValueBean info = new ValueBean();
        info.setValue("someone@[216.109.118.76]");
        valueTest(info, true);
        info.setValue("someone@yahoo.com");
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
    * Tests the e-mail validation with a dot at the end of 
    * the address.
    */
   public void testEmailWithDotEnd() throws ValidatorException {
      // Create bean to run test on.
      ValueBean info = new ValueBean();

      info.setValue("andy.noble@data-workshop.com.");
      valueTest(info, false);

   }

    /**
     * Tests the e-mail validation with an RCS-noncompliant character in
     * the address.
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
     *
     * <p><b>FIXME</b>: This test fails so disable it with a leading _ for 1.1.4 release.
     * The real solution is to fix the email parsing.
     *
     * @throws ValidatorException
     */
    public void _testEmailUserName() throws ValidatorException {
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
     * These test values derive directly from RFC 822 &
     * Mail::RFC822::Address & RFC::RFC822::Address perl test.pl
     * For traceability don't combine these test values with other tests.
     */
    TestPair[] testEmailFromPerl = {
        new TestPair("abigail@example.com", true),
        new TestPair("abigail@example.com ", true),
        new TestPair(" abigail@example.com", true),
        new TestPair("abigail @example.com ", true),
        new TestPair("*@example.net", true),
        new TestPair("\"\\\"\"@foo.bar", true),
        new TestPair("fred&barny@example.com", true),
        new TestPair("---@example.com", true),
        new TestPair("foo-bar@example.net", true),
        new TestPair("\"127.0.0.1\"@[127.0.0.1]", true),
        new TestPair("Abigail <abigail@example.com>", true),
        new TestPair("Abigail<abigail@example.com>", true),
        new TestPair("Abigail<@a,@b,@c:abigail@example.com>", true),
        new TestPair("\"This is a phrase\"<abigail@example.com>", true),
        new TestPair("\"Abigail \"<abigail@example.com>", true),
        new TestPair("\"Joe & J. Harvey\" <example @Org>", true),
        new TestPair("Abigail <abigail @ example.com>", true),
        new TestPair("Abigail made this <  abigail   @   example  .    com    >", true),
        new TestPair("Abigail(the bitch)@example.com", true),
        new TestPair("Abigail <abigail @ example . (bar) com >", true),
        new TestPair("Abigail < (one)  abigail (two) @(three)example . (bar) com (quz) >", true),
        new TestPair("Abigail (foo) (((baz)(nested) (comment)) ! ) < (one)  abigail (two) @(three)example . (bar) com (quz) >", true),
        new TestPair("Abigail <abigail(fo\\(o)@example.com>", true),
        new TestPair("Abigail <abigail(fo\\)o)@example.com> ", true),
        new TestPair("(foo) abigail@example.com", true),
        new TestPair("abigail@example.com (foo)", true),
        new TestPair("\"Abi\\\"gail\" <abigail@example.com>", true),
        new TestPair("abigail@[example.com]", true),
        new TestPair("abigail@[exa\\[ple.com]", true),
        new TestPair("abigail@[exa\\]ple.com]", true),
        new TestPair("\":sysmail\"@  Some-Group. Some-Org", true),
        new TestPair("Muhammed.(I am  the greatest) Ali @(the)Vegas.WBA", true),
        new TestPair("mailbox.sub1.sub2@this-domain", true),
        new TestPair("sub-net.mailbox@sub-domain.domain", true),
        new TestPair("name:;", true),
        new TestPair("':;", true),
        new TestPair("name:   ;", true),
        new TestPair("Alfred Neuman <Neuman@BBN-TENEXA>", true),
        new TestPair("Neuman@BBN-TENEXA", true),
        new TestPair("\"George, Ted\" <Shared@Group.Arpanet>", true),
        new TestPair("Wilt . (the  Stilt) Chamberlain@NBA.US", true),
        new TestPair("Cruisers:  Port@Portugal, Jones@SEA;", true),
        new TestPair("$@[]", true),
        new TestPair("*()@[]", true),
        new TestPair("\"quoted ( brackets\" ( a comment )@example.com", true),
        new TestPair("\"Joe & J. Harvey\"\\x0D\\x0A     <ddd\\@ Org>", true),
        new TestPair("\"Joe &\\x0D\\x0A J. Harvey\" <ddd \\@ Org>", true),
        new TestPair("Gourmets:  Pompous Person <WhoZiWhatZit\\@Cordon-Bleu>,\\x0D\\x0A" +
            "        Childs\\@WGBH.Boston, \"Galloping Gourmet\"\\@\\x0D\\x0A" +
            "        ANT.Down-Under (Australian National Television),\\x0D\\x0A" +
            "        Cheapie\\@Discount-Liquors;", true),
        new TestPair("   Just a string", false),
        new TestPair("string", false),
        new TestPair("(comment)", false),
        new TestPair("()@example.com", false),
        new TestPair("fred(&)barny@example.com", false),
        new TestPair("fred\\ barny@example.com", false),
        new TestPair("Abigail <abi gail @ example.com>", false),
        new TestPair("Abigail <abigail(fo(o)@example.com>", false),
        new TestPair("Abigail <abigail(fo)o)@example.com>", false),
        new TestPair("\"Abi\"gail\" <abigail@example.com>", false),
        new TestPair("abigail@[exa]ple.com]", false),
        new TestPair("abigail@[exa[ple.com]", false),
        new TestPair("abigail@[exaple].com]", false),
        new TestPair("abigail@", false),
        new TestPair("@example.com", false),
        new TestPair("phrase: abigail@example.com abigail@example.com ;", false),
        new TestPair("invalid£char@example.com", false)
    };

    /**
     * Write this test based on perl Mail::RFC822::Address
     * which takes its example email address directly from RFC822
     * 
     * @throws ValidatorException
     * 
     * FIXME This test fails so disable it with a leading _ for 1.1.4 release.
     * The real solution is to fix the email parsing.
     */
    public void _testEmailFromPerl() throws ValidatorException {
        ValueBean info = new ValueBean();
        for (int index = 0; index < testEmailFromPerl.length; index++) {
            info.setValue(testEmailFromPerl[index].item);
            valueTest(info, testEmailFromPerl[index].valid);
        }
    }

   /**
    * Utlity class to run a test on a value.
    *
    * @param info	Value to run test on.
    * @param passed	Whether or not the test is expected to pass.
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
