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
package org.apache.commons.validator.routines;

import junit.framework.TestCase;

import org.apache.commons.validator.ResultPair;

/**
 * Performs Validation Test for e-mail validations.
 *
 *
 * @version $Revision$
 */
public class EmailValidatorTest extends TestCase {

    /**
     * The key used to retrieve the set of validation
     * rules from the xml file.
     */
    protected static String FORM_KEY = "emailForm";

   /**
    * The key used to retrieve the validator action.
    */
   protected static String ACTION = "email";

    private EmailValidator validator;

   public EmailValidatorTest(String name) {
       super(name);
   }

   protected void setUp() {
        validator = EmailValidator.getInstance();
   }

   protected void tearDown() {
   }

   /**
    * Tests the e-mail validation.
    */
   public void testEmail()  {
      assertTrue(validator.isValid("jsmith@apache.org"));
   }

   /**
    * Tests the email validation with numeric domains.
    */
    public void testEmailWithNumericAddress()  {
        assertTrue(validator.isValid("someone@[216.109.118.76]"));
        assertTrue(validator.isValid("someone@yahoo.com"));
    }

    /**
     * Tests the e-mail validation.
     */
    public void testEmailExtension()  {
        assertTrue(validator.isValid("jsmith@apache.org"));

        assertTrue(validator.isValid("jsmith@apache.com"));

        assertTrue(validator.isValid("jsmith@apache.net"));

        assertTrue(validator.isValid("jsmith@apache.info"));

        assertFalse(validator.isValid("jsmith@apache."));

        assertFalse(validator.isValid("jsmith@apache.c"));

        assertTrue(validator.isValid("someone@yahoo.museum"));

        assertFalse(validator.isValid("someone@yahoo.mu-seum"));
    }

   /**
    * <p>Tests the e-mail validation with a dash in
    * the address.</p>
    */
   public void testEmailWithDash()  {
       assertTrue(validator.isValid("andy.noble@data-workshop.com"));

       assertFalse(validator.isValid("andy-noble@data-workshop.-com"));

       assertFalse(validator.isValid("andy-noble@data-workshop.c-om"));

       assertFalse(validator.isValid("andy-noble@data-workshop.co-m"));
   }

   /**
    * Tests the e-mail validation with a dot at the end of
    * the address.
    */
   public void testEmailWithDotEnd()  {
      assertFalse(validator.isValid("andy.noble@data-workshop.com."));
   }

    /**
     * Tests the e-mail validation with an RCS-noncompliant character in
     * the address.
     */
    public void testEmailWithBogusCharacter()  {

        assertFalse(validator.isValid("andy.noble@\u008fdata-workshop.com"));

        // The ' character is valid in an email username.
        assertTrue(validator.isValid("andy.o'reilly@data-workshop.com"));

        // But not in the domain name.
        assertFalse(validator.isValid("andy@o'reilly.data-workshop.com"));

        // The + character is valid in an email username.
        assertTrue(validator.isValid("foo+bar@i.am.not.in.us.example.com"));

        // But not in the domain name
        assertFalse(validator.isValid("foo+bar@example+3.com"));

        // Domains with only special characters aren't allowed (VALIDATOR-286) 
        assertFalse(validator.isValid("test@%*.com"));
        assertFalse(validator.isValid("test@^&#.com"));

    }

    public void testVALIDATOR_315() {
        assertFalse(validator.isValid("me@at&t.net"));
        assertTrue(validator.isValid("me@att.net")); // Make sure TLD is not the cause of the failure
    }

    public void testVALIDATOR_278() {
        assertFalse(validator.isValid("someone@-test.com"));// hostname starts with dash/hyphen
        assertFalse(validator.isValid("someone@test-.com"));// hostname ends with dash/hyphen
    }

    public void testValidator235() {
        String version = System.getProperty("java.version");
        if (version.compareTo("1.6") < 0) {
            System.out.println("Cannot run Unicode IDN tests");
            return; // Cannot run the test
        }
        assertTrue("xn--d1abbgf6aiiy.xn--p1ai should validate", validator.isValid("someone@xn--d1abbgf6aiiy.xn--p1ai"));
        assertTrue("президент.рф should validate", validator.isValid("someone@президент.рф"));
        assertTrue("www.b\u00fccher.ch should validate", validator.isValid("someone@www.b\u00fccher.ch"));
        assertFalse("www.\uFFFD.ch FFFD should fail", validator.isValid("someone@www.\uFFFD.ch"));
        assertTrue("www.b\u00fccher.ch should validate", validator.isValid("someone@www.b\u00fccher.ch"));
        assertFalse("www.\uFFFD.ch FFFD should fail", validator.isValid("someone@www.\uFFFD.ch"));
    }

    /**
    * Tests the email validation with commas.
    */
    public void testEmailWithCommas()  {
        assertFalse(validator.isValid("joeblow@apa,che.org"));

        assertFalse(validator.isValid("joeblow@apache.o,rg"));

        assertFalse(validator.isValid("joeblow@apache,org"));

    }

   /**
    * Tests the email validation with spaces.
    */
    public void testEmailWithSpaces()  {
        assertFalse(validator.isValid("joeblow @apache.org")); // TODO - this should be valid?

        assertFalse(validator.isValid("joeblow@ apache.org"));

        assertTrue(validator.isValid(" joeblow@apache.org")); // TODO - this should be valid?

        assertTrue(validator.isValid("joeblow@apache.org "));

        assertFalse(validator.isValid("joe blow@apache.org "));

        assertFalse(validator.isValid("joeblow@apa che.org "));

    }

   /**
    * Tests the email validation with ascii control characters.
    * (i.e. Ascii chars 0 - 31 and 127)
    */
    public void testEmailWithControlChars()  {
        for (char c = 0; c < 32; c++) {
            assertFalse("Test control char " + ((int)c), validator.isValid("foo" + c + "bar@domain.com"));
        }
        assertFalse("Test control char 127", validator.isValid("foo" + ((char)127) + "bar@domain.com"));
    }
    
    /**
     * Test that @localhost and @localhost.localdomain
     *  addresses are declared as valid when requested. 
     */
    public void testEmailLocalhost() {
       // Check the default is not to allow
       EmailValidator noLocal = EmailValidator.getInstance(false);
       EmailValidator allowLocal = EmailValidator.getInstance(true);
       assertEquals(validator, noLocal);
       
       // Depends on the validator
       assertTrue(
             "@localhost.localdomain should be accepted but wasn't",
             allowLocal.isValid("joe@localhost.localdomain")
       );
       assertTrue(
             "@localhost should be accepted but wasn't",
             allowLocal.isValid("joe@localhost")
       );
       
       assertFalse(
             "@localhost.localdomain should be accepted but wasn't",
             noLocal.isValid("joe@localhost.localdomain")
       );
       assertFalse(
             "@localhost should be accepted but wasn't",
             noLocal.isValid("joe@localhost")
       );
    }
    
    /**
     * VALIDATOR-296 - A / or a ! is valid in the user part,
     *  but not in the domain part 
     */
    public void testEmailWithSlashes() {
       assertTrue(
             "/ and ! valid in username",
             validator.isValid("joe!/blow@apache.org")
       );
       assertFalse(
             "/ not valid in domain",
             validator.isValid("joe@ap/ache.org")
       );
       assertFalse(
             "! not valid in domain",
             validator.isValid("joe@apac!he.org")
       );
    }

    /**
     * Write this test according to parts of RFC, as opposed to the type of character
     * that is being tested.
     */
    public void testEmailUserName()  {

        assertTrue(validator.isValid("joe1blow@apache.org"));

        assertTrue(validator.isValid("joe$blow@apache.org"));

        assertTrue(validator.isValid("joe-@apache.org"));

        assertTrue(validator.isValid("joe_@apache.org"));

        assertTrue(validator.isValid("joe+@apache.org")); // + is valid unquoted

        assertTrue(validator.isValid("joe!@apache.org")); // ! is valid unquoted

        assertTrue(validator.isValid("joe*@apache.org")); // * is valid unquoted

        assertTrue(validator.isValid("joe'@apache.org")); // ' is valid unquoted

        assertTrue(validator.isValid("joe%45@apache.org")); // % is valid unquoted

        assertTrue(validator.isValid("joe?@apache.org")); // ? is valid unquoted

        assertTrue(validator.isValid("joe&@apache.org")); // & ditto

        assertTrue(validator.isValid("joe=@apache.org")); // = ditto

        assertTrue(validator.isValid("+joe@apache.org")); // + is valid unquoted

        assertTrue(validator.isValid("!joe@apache.org")); // ! is valid unquoted

        assertTrue(validator.isValid("*joe@apache.org")); // * is valid unquoted

        assertTrue(validator.isValid("'joe@apache.org")); // ' is valid unquoted

        assertTrue(validator.isValid("%joe45@apache.org")); // % is valid unquoted

        assertTrue(validator.isValid("?joe@apache.org")); // ? is valid unquoted

        assertTrue(validator.isValid("&joe@apache.org")); // & ditto

        assertTrue(validator.isValid("=joe@apache.org")); // = ditto

        assertTrue(validator.isValid("+@apache.org")); // + is valid unquoted

        assertTrue(validator.isValid("!@apache.org")); // ! is valid unquoted

        assertTrue(validator.isValid("*@apache.org")); // * is valid unquoted

        assertTrue(validator.isValid("'@apache.org")); // ' is valid unquoted

        assertTrue(validator.isValid("%@apache.org")); // % is valid unquoted

        assertTrue(validator.isValid("?@apache.org")); // ? is valid unquoted

        assertTrue(validator.isValid("&@apache.org")); // & ditto

        assertTrue(validator.isValid("=@apache.org")); // = ditto


        //UnQuoted Special characters are invalid

        assertFalse(validator.isValid("joe.@apache.org")); // . not allowed at end of local part

        assertFalse(validator.isValid(".joe@apache.org")); // . not allowed at start of local part

        assertFalse(validator.isValid(".@apache.org")); // . not allowed alone

        assertTrue(validator.isValid("joe.ok@apache.org")); // . allowed embedded

        assertFalse(validator.isValid("joe..ok@apache.org")); // .. not allowed embedded

        assertFalse(validator.isValid("..@apache.org")); // .. not allowed alone

        assertFalse(validator.isValid("joe(@apache.org"));

        assertFalse(validator.isValid("joe)@apache.org"));

        assertFalse(validator.isValid("joe,@apache.org"));

        assertFalse(validator.isValid("joe;@apache.org"));


        //Quoted Special characters are valid
        assertTrue(validator.isValid("\"joe.\"@apache.org"));

        assertTrue(validator.isValid("\".joe\"@apache.org"));

        assertTrue(validator.isValid("\"joe+\"@apache.org"));

        assertTrue(validator.isValid("\"joe!\"@apache.org"));

        assertTrue(validator.isValid("\"joe*\"@apache.org"));

        assertTrue(validator.isValid("\"joe'\"@apache.org"));

        assertTrue(validator.isValid("\"joe(\"@apache.org"));

        assertTrue(validator.isValid("\"joe)\"@apache.org"));

        assertTrue(validator.isValid("\"joe,\"@apache.org"));

        assertTrue(validator.isValid("\"joe%45\"@apache.org"));

        assertTrue(validator.isValid("\"joe;\"@apache.org"));

        assertTrue(validator.isValid("\"joe?\"@apache.org"));

        assertTrue(validator.isValid("\"joe&\"@apache.org"));

        assertTrue(validator.isValid("\"joe=\"@apache.org"));

        assertTrue(validator.isValid("\"..\"@apache.org"));

    }

    /**
     * These test values derive directly from RFC 822 &
     * Mail::RFC822::Address & RFC::RFC822::Address perl test.pl
     * For traceability don't combine these test values with other tests.
     */
    private static final ResultPair[] testEmailFromPerl = {
        new ResultPair("abigail@example.com", true),
        new ResultPair("abigail@example.com ", true),
        new ResultPair(" abigail@example.com", true),
        new ResultPair("abigail @example.com ", true),
        new ResultPair("*@example.net", true),
        new ResultPair("\"\\\"\"@foo.bar", true),
        new ResultPair("fred&barny@example.com", true),
        new ResultPair("---@example.com", true),
        new ResultPair("foo-bar@example.net", true),
        new ResultPair("\"127.0.0.1\"@[127.0.0.1]", true),
        new ResultPair("Abigail <abigail@example.com>", true),
        new ResultPair("Abigail<abigail@example.com>", true),
        new ResultPair("Abigail<@a,@b,@c:abigail@example.com>", true),
        new ResultPair("\"This is a phrase\"<abigail@example.com>", true),
        new ResultPair("\"Abigail \"<abigail@example.com>", true),
        new ResultPair("\"Joe & J. Harvey\" <example @Org>", true),
        new ResultPair("Abigail <abigail @ example.com>", true),
        new ResultPair("Abigail made this <  abigail   @   example  .    com    >", true),
        new ResultPair("Abigail(the bitch)@example.com", true),
        new ResultPair("Abigail <abigail @ example . (bar) com >", true),
        new ResultPair("Abigail < (one)  abigail (two) @(three)example . (bar) com (quz) >", true),
        new ResultPair("Abigail (foo) (((baz)(nested) (comment)) ! ) < (one)  abigail (two) @(three)example . (bar) com (quz) >", true),
        new ResultPair("Abigail <abigail(fo\\(o)@example.com>", true),
        new ResultPair("Abigail <abigail(fo\\)o)@example.com> ", true),
        new ResultPair("(foo) abigail@example.com", true),
        new ResultPair("abigail@example.com (foo)", true),
        new ResultPair("\"Abi\\\"gail\" <abigail@example.com>", true),
        new ResultPair("abigail@[example.com]", true),
        new ResultPair("abigail@[exa\\[ple.com]", true),
        new ResultPair("abigail@[exa\\]ple.com]", true),
        new ResultPair("\":sysmail\"@  Some-Group. Some-Org", true),
        new ResultPair("Muhammed.(I am  the greatest) Ali @(the)Vegas.WBA", true),
        new ResultPair("mailbox.sub1.sub2@this-domain", true),
        new ResultPair("sub-net.mailbox@sub-domain.domain", true),
        new ResultPair("name:;", true),
        new ResultPair("':;", true),
        new ResultPair("name:   ;", true),
        new ResultPair("Alfred Neuman <Neuman@BBN-TENEXA>", true),
        new ResultPair("Neuman@BBN-TENEXA", true),
        new ResultPair("\"George, Ted\" <Shared@Group.Arpanet>", true),
        new ResultPair("Wilt . (the  Stilt) Chamberlain@NBA.US", true),
        new ResultPair("Cruisers:  Port@Portugal, Jones@SEA;", true),
        new ResultPair("$@[]", true),
        new ResultPair("*()@[]", true),
        new ResultPair("\"quoted ( brackets\" ( a comment )@example.com", true),
        new ResultPair("\"Joe & J. Harvey\"\\x0D\\x0A     <ddd\\@ Org>", true),
        new ResultPair("\"Joe &\\x0D\\x0A J. Harvey\" <ddd \\@ Org>", true),
        new ResultPair("Gourmets:  Pompous Person <WhoZiWhatZit\\@Cordon-Bleu>,\\x0D\\x0A" +
            "        Childs\\@WGBH.Boston, \"Galloping Gourmet\"\\@\\x0D\\x0A" +
            "        ANT.Down-Under (Australian National Television),\\x0D\\x0A" +
            "        Cheapie\\@Discount-Liquors;", true),
        new ResultPair("   Just a string", false),
        new ResultPair("string", false),
        new ResultPair("(comment)", false),
        new ResultPair("()@example.com", false),
        new ResultPair("fred(&)barny@example.com", false),
        new ResultPair("fred\\ barny@example.com", false),
        new ResultPair("Abigail <abi gail @ example.com>", false),
        new ResultPair("Abigail <abigail(fo(o)@example.com>", false),
        new ResultPair("Abigail <abigail(fo)o)@example.com>", false),
        new ResultPair("\"Abi\"gail\" <abigail@example.com>", false),
        new ResultPair("abigail@[exa]ple.com]", false),
        new ResultPair("abigail@[exa[ple.com]", false),
        new ResultPair("abigail@[exaple].com]", false),
        new ResultPair("abigail@", false),
        new ResultPair("@example.com", false),
        new ResultPair("phrase: abigail@example.com abigail@example.com ;", false),
        new ResultPair("invalid�char@example.com", false)
    };

    /**
     * Write this test based on perl Mail::RFC822::Address
     * which takes its example email address directly from RFC822
     *
     * FIXME This test fails so disable it with a leading _ for 1.1.4 release.
     * The real solution is to fix the email parsing.
     */
    public void _testEmailFromPerl()  {
        for (int index = 0; index < testEmailFromPerl.length; index++) {
            String item = testEmailFromPerl[index].item;
            if (testEmailFromPerl[index].valid) {
                assertTrue("Should be OK: "+item, validator.isValid(item));
            } else {
                assertFalse("Should fail: "+item, validator.isValid(item));
            }
        }
    }

    public void testValidator293(){
        assertTrue(validator.isValid("abc-@abc.com"));
        assertTrue(validator.isValid("abc_@abc.com"));
        assertTrue(validator.isValid("abc-def@abc.com"));
        assertTrue(validator.isValid("abc_def@abc.com"));
        assertTrue(validator.isValid("abc@abc_def.com"));
    }
}
