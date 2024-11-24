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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 * Performs Validation Test for e-mail validations.
 *
 *
 * @deprecated to be removed when target class is removed
 */
@Deprecated
public class EmailTest extends AbstractCommonTest {

    /**
     * The key used to retrieve the set of validation rules from the xml file.
     */
    protected static final String FORM_KEY = "emailForm";

    /**
     * The key used to retrieve the validator action.
     */
    protected static final String ACTION = "email";

    /**
     * These test values derive directly from RFC 822 & Mail::RFC822::Address & RFC::RFC822::Address perl test.pl For traceability don't combine these test
     * values with other tests.
     */
    ResultPair[] testEmailFromPerl = { new ResultPair("abigail@example.com", true), new ResultPair("abigail@example.com ", true),
            new ResultPair(" abigail@example.com", true), new ResultPair("abigail @example.com ", true), new ResultPair("*@example.net", true),
            new ResultPair("\"\\\"\"@foo.bar", true), new ResultPair("fred&barny@example.com", true), new ResultPair("---@example.com", true),
            new ResultPair("foo-bar@example.net", true), new ResultPair("\"127.0.0.1\"@[127.0.0.1]", true),
            new ResultPair("Abigail <abigail@example.com>", true), new ResultPair("Abigail<abigail@example.com>", true),
            new ResultPair("Abigail<@a,@b,@c:abigail@example.com>", true), new ResultPair("\"This is a phrase\"<abigail@example.com>", true),
            new ResultPair("\"Abigail \"<abigail@example.com>", true), new ResultPair("\"Joe & J. Harvey\" <example @Org>", true),
            new ResultPair("Abigail <abigail @ example.com>", true), new ResultPair("Abigail made this <  abigail   @   example  .    com    >", true),
            new ResultPair("Abigail(the bitch)@example.com", true), new ResultPair("Abigail <abigail @ example . (bar) com >", true),
            new ResultPair("Abigail < (one)  abigail (two) @(three) example . (bar) com (quz) >", true),
            new ResultPair("Abigail (foo) (((baz)(nested) (comment)) ! ) < (one)  abigail (two) @(three) example . (bar) com (quz) >", true),
            new ResultPair("Abigail <abigail(fo\\(o)@example.com>", true), new ResultPair("Abigail <abigail(fo\\)o)@example.com> ", true),
            new ResultPair("(foo) abigail@example.com", true), new ResultPair("abigail@example.com (foo)", true),
            new ResultPair("\"Abi\\\"gail\" <abigail@example.com>", true), new ResultPair("abigail@[example.com]", true),
            new ResultPair("abigail@[exa\\[ple.com]", true), new ResultPair("abigail@[exa\\]ple.com]", true),
            new ResultPair("\":sysmail\"@  Some-Group. Some-Org", true), new ResultPair("Muhammed.(I am  the greatest) Ali @(the) Vegas.WBA", true),
            new ResultPair("mailbox.sub1.sub2@this-domain", true), new ResultPair("sub-net.mailbox@sub-domain.domain", true), new ResultPair("name:;", true),
            new ResultPair("':;", true), new ResultPair("name:   ;", true), new ResultPair("Alfred Neuman <Neuman@BBN-TENEXA>", true),
            new ResultPair("Neuman@BBN-TENEXA", true), new ResultPair("\"George, Ted\" <Shared@Group.Arpanet>", true),
            new ResultPair("Wilt . (the  Stilt) Chamberlain@NBA.US", true), new ResultPair("Cruisers:  Port@Portugal, Jones@SEA;", true),
            new ResultPair("$@[]", true), new ResultPair("*()@[]", true), new ResultPair("\"quoted ( brackets\" ( a comment )@example.com", true),
            new ResultPair("\"Joe & J. Harvey\"\\x0D\\x0A     <ddd\\@ Org>", true), new ResultPair("\"Joe &\\x0D\\x0A J. Harvey\" <ddd \\@ Org>", true),
            new ResultPair(
                    "Gourmets:  Pompous Person <WhoZiWhatZit\\@Cordon-Bleu>,\\x0D\\x0A" + "        Childs\\@WGBH.Boston, \"Galloping Gourmet\"\\@\\x0D\\x0A"
                            + "        ANT.Down-Under (Australian National Television),\\x0D\\x0A" + "        Cheapie\\@Discount-Liquors;",
                    true),
            new ResultPair("   Just a string", false), new ResultPair("string", false), new ResultPair("(comment)", false),
            new ResultPair("()@example.com", false), new ResultPair("fred(&)barny@example.com", false), new ResultPair("fred\\ barny@example.com", false),
            new ResultPair("Abigail <abi gail @ example.com>", false), new ResultPair("Abigail <abigail(fo(o)@example.com>", false),
            new ResultPair("Abigail <abigail(fo) o)@example.com>", false), new ResultPair("\"Abi\"gail\" <abigail@example.com>", false),
            new ResultPair("abigail@[exa]ple.com]", false), new ResultPair("abigail@[exa[ple.com]", false), new ResultPair("abigail@[exaple].com]", false),
            new ResultPair("abigail@", false), new ResultPair("@example.com", false),
            new ResultPair("phrase: abigail@example.com abigail@example.com ;", false), new ResultPair("invalid�char@example.com", false) };

    /**
     * Load {@code ValidatorResources} from validator-regexp.xml.
     */
    @BeforeEach
    protected void setUp() throws IOException, SAXException {
        loadResources("EmailTest-config.xml");
    }

    /**
     * Tests the e-mail validation.
     */
    @Test
    public void testEmail() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();

        info.setValue("jsmith@apache.org");
        valueTest(info, true);
    }

    /**
     * Tests the e-mail validation with a user at a TLD
     */
    @Test
    public void testEmailAtTLD() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();

        info.setValue("m@de");
        valueTest(info, false);

        final org.apache.commons.validator.routines.EmailValidator validator = org.apache.commons.validator.routines.EmailValidator.getInstance(true, true);
        final boolean result = validator.isValid("m@de");
        assertTrue(result, "Result should have been true");

    }

    /**
     * Tests the e-mail validation.
     */
    @Test
    public void testEmailExtension() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();

        info.setValue("jsmith@apache.org");
        valueTest(info, true);

        info.setValue("jsmith@apache.com");
        valueTest(info, true);

        info.setValue("jsmith@apache.net");
        valueTest(info, true);

        info.setValue("jsmith@apache.info");
        valueTest(info, true);

        info.setValue("jsmith@apache.");
        valueTest(info, false);

        info.setValue("jsmith@apache.c");
        valueTest(info, false);

        info.setValue("someone@yahoo.museum");
        valueTest(info, true);

        info.setValue("someone@yahoo.mu-seum");
        valueTest(info, false);
    }

    /**
     * Write this test based on perl Mail::RFC822::Address which takes its example email address directly from RFC822
     *
     * @throws ValidatorException
     *
     *                            FIXME This test fails so disable it with a leading _ for 1.1.4 release. The real solution is to fix the email parsing.
     */
    @Disabled
    public void testEmailFromPerl() throws ValidatorException {
        final ValueBean info = new ValueBean();
        for (final ResultPair element : testEmailFromPerl) {
            info.setValue(element.item);
            valueTest(info, element.valid);
        }
    }

    /**
     * Test that @localhost and @localhost.localdomain addresses aren't declared valid by default
     */
    @Test
    public void testEmailLocalhost() throws ValidatorException {
        final ValueBean info = new ValueBean();
        info.setValue("joe@localhost");
        valueTest(info, false);
        info.setValue("joe@localhost.localdomain");
        valueTest(info, false);
    }

    /**
     * Write this test according to parts of RFC, as opposed to the type of character that is being tested.
     *
     * <p>
     * <strong>FIXME</strong>: This test fails so disable it with a leading _ for 1.1.4 release. The real solution is to fix the email parsing.
     *
     * @throws ValidatorException
     */
    @Disabled
    public void testEmailUserName() throws ValidatorException {
        final ValueBean info = new ValueBean();
        info.setValue("joe1blow@apache.org");
        valueTest(info, true);
        info.setValue("joe$blow@apache.org");
        valueTest(info, true);
        info.setValue("joe-@apache.org");
        valueTest(info, true);
        info.setValue("joe_@apache.org");
        valueTest(info, true);

        // UnQuoted Special characters are invalid

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

        // Quoted Special characters are valid
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
     * Tests the e-mail validation with an RCS-non-compliant character in the address.
     */
    @Test
    public void testEmailWithBogusCharacter() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();

        info.setValue("andy.noble@\u008fdata-workshop.com");
        valueTest(info, false);

        // The ' character is valid in an email username.
        info.setValue("andy.o'reilly@data-workshop.com");
        valueTest(info, true);

        // But not in the domain name.
        info.setValue("andy@o'reilly.data-workshop.com");
        valueTest(info, false);

        info.setValue("foo+bar@i.am.not.in.us.example.com");
        valueTest(info, true);
    }

    /**
     * Tests the email validation with commas.
     */
    @Test
    public void testEmailWithCommas() throws ValidatorException {
        final ValueBean info = new ValueBean();
        info.setValue("joeblow@apa,che.org");
        valueTest(info, false);
        info.setValue("joeblow@apache.o,rg");
        valueTest(info, false);
        info.setValue("joeblow@apache,org");
        valueTest(info, false);

    }

    /**
     * Tests the email validation with ASCII control characters. (i.e. ASCII chars 0 - 31 and 127)
     */
    @Test
    public void testEmailWithControlChars() {
        final EmailValidator validator = new EmailValidator();
        for (char c = 0; c < 32; c++) {
            assertFalse(validator.isValid("foo" + c + "bar@domain.com"), "Test control char " + (int) c);
        }
        assertFalse(validator.isValid("foo" + (char) 127 + "bar@domain.com"), "Test control char 127");
    }

    /**
     * <p>
     * Tests the e-mail validation with a dash in the address.
     * </p>
     */
    @Test
    public void testEmailWithDash() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();

        info.setValue("andy.noble@data-workshop.com");
        valueTest(info, true);

        info.setValue("andy-noble@data-workshop.-com");
        valueTest(info, false);
        info.setValue("andy-noble@data-workshop.c-om");
        valueTest(info, false);
        info.setValue("andy-noble@data-workshop.co-m");
        valueTest(info, false);

    }

    /**
     * Tests the e-mail validation with a dot at the end of the address.
     */
    @Test
    public void testEmailWithDotEnd() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();

        info.setValue("andy.noble@data-workshop.com.");
        valueTest(info, false);

    }

    /**
     * Tests the email validation with numeric domains.
     */
    @Test
    public void testEmailWithNumericAddress() throws ValidatorException {
        final ValueBean info = new ValueBean();
        info.setValue("someone@[216.109.118.76]");
        valueTest(info, true);
        info.setValue("someone@yahoo.com");
        valueTest(info, true);
    }

    /**
     * Tests the email validation with spaces.
     */
    @Test
    public void testEmailWithSpaces() throws ValidatorException {
        final ValueBean info = new ValueBean();
        info.setValue("joeblow @apache.org");
        valueTest(info, false);
        info.setValue("joeblow@ apache.org");
        valueTest(info, false);
        info.setValue(" joeblow@apache.org");
        valueTest(info, false);
        info.setValue("joeblow@apache.org ");
        valueTest(info, false);
        info.setValue("joe blow@apache.org ");
        valueTest(info, false);
        info.setValue("joeblow@apa che.org ");
        valueTest(info, false);
        info.setValue("\"joe blow\"@apache.org");
        valueTest(info, true);

    }

    /**
     * Utility class to run a test on a value.
     *
     * @param info   Value to run test on.
     * @param passed Whether or not the test is expected to pass.
     */
    private void valueTest(final ValueBean info, final boolean passed) throws ValidatorException {
        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, info);

        // Get results of the validation.
        // throws ValidatorException,
        // but we aren't catching for testing
        // since no validation methods we use
        // throw this
        final ValidatorResults results = validator.validate();

        assertNotNull(results, "Results are null.");

        final ValidatorResult result = results.getValidatorResult("value");

        assertNotNull(result, ACTION + " value ValidatorResult should not be null.");
        assertTrue(result.containsAction(ACTION), "Value " + info.getValue() + " ValidatorResult should contain the '" + ACTION + "' action.");
        assertTrue(passed ? result.isValid(ACTION) : !result.isValid(ACTION),
                "Value " + info.getValue() + "ValidatorResult for the '" + ACTION + "' action should have " + (passed ? "passed" : "failed") + ".");
    }
}
