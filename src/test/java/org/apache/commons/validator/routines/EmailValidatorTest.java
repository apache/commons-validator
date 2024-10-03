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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.validator.ResultPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Performs Validation Test for e-mail validations.
 */
public class EmailValidatorTest {

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
    private static final ResultPair[] TEST_EMAIL_FROM_PERL = {
        // @formatter:off
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
        new ResultPair("Abigail < (one)  abigail (two) @(three) example . (bar) com (quz) >", true),
        new ResultPair("Abigail (foo) (((baz)(nested) (comment)) ! ) < (one)  abigail (two) @(three) example . (bar) com (quz) >", true),
        new ResultPair("Abigail <abigail(fo\\(o)@example.com>", true),
        new ResultPair("Abigail <abigail(fo\\)o)@example.com> ", true),
        new ResultPair("(foo) abigail@example.com", true),
        new ResultPair("abigail@example.com (foo)", true),
        new ResultPair("\"Abi\\\"gail\" <abigail@example.com>", true),
        new ResultPair("abigail@[example.com]", true),
        new ResultPair("abigail@[exa\\[ple.com]", true),
        new ResultPair("abigail@[exa\\]ple.com]", true),
        new ResultPair("\":sysmail\"@  Some-Group. Some-Org", true),
        new ResultPair("Muhammed.(I am  the greatest) Ali @(the) Vegas.WBA", true),
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
        new ResultPair("Abigail <abigail(fo) o)@example.com>", false),
        new ResultPair("\"Abi\"gail\" <abigail@example.com>", false),
        new ResultPair("abigail@[exa]ple.com]", false),
        new ResultPair("abigail@[exa[ple.com]", false),
        new ResultPair("abigail@[exaple].com]", false),
        new ResultPair("abigail@", false),
        new ResultPair("@example.com", false),
        new ResultPair("phrase: abigail@example.com abigail@example.com ;", false),
        new ResultPair("invalid�char@example.com", false)
    // @formatter:on
    };

    public static void main(final String[] args) {
        final EmailValidator validator = EmailValidator.getInstance();
        for (final String arg : args) {
            System.out.printf("%s: %s%n", arg, validator.isValid(arg));
        }
    }

    private EmailValidator validator;

    @BeforeEach
    public void setUp() {
        validator = EmailValidator.getInstance();
    }

    /**
     * Tests the e-mail validation.
     */
    @Test
    public void testEmail() {
        assertTrue(validator.isValid("jsmith@apache.org"));
    }

    /**
     * Tests the e-mail validation with a user at a TLD
     *
     * https://tools.ietf.org/html/rfc5321#section-2.3.5 (In the case of a top-level domain used by itself in an email address, a single string is used without
     * any dots)
     */
    @Test
    public void testEmailAtTLD() {
        final EmailValidator val = EmailValidator.getInstance(false, true);
        assertTrue(val.isValid("test@com"));
    }

    /**
     * Tests the e-mail validation.
     */
    @Test
    public void testEmailExtension() {
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
     * Write this test based on perl Mail::RFC822::Address which takes its example email address directly from RFC822
     *
     * This test fails so disable it The real solution is to fix the email parsing.
     */
    @Disabled("VALIDATOR-267")
    @Test
    public void testEmailFromPerl() {
        int errors = 0;
        for (final ResultPair element : TEST_EMAIL_FROM_PERL) {
            final String item = element.item;
            final boolean exp = element.valid;
            final boolean act = validator.isValid(item);
            if (act != exp) {
                System.out.printf("%s: expected %s actual %s%n", item, exp, act);
                errors += 1;
            }
        }
        assertEquals(0, errors, "Expected 0 errors");
    }

    /**
     * Test that @localhost and @localhost.localdomain addresses are declared as valid when requested.
     */
    @Test
    public void testEmailLocalhost() {
        // Check the default is not to allow
        final EmailValidator noLocal = EmailValidator.getInstance(false);
        final EmailValidator allowLocal = EmailValidator.getInstance(true);
        assertEquals(validator, noLocal);

        // Depends on the validator
        assertTrue(allowLocal.isValid("joe@localhost.localdomain"), "@localhost.localdomain should be accepted but wasn't");
        assertTrue(allowLocal.isValid("joe@localhost"), "@localhost should be accepted but wasn't");

        assertFalse(noLocal.isValid("joe@localhost.localdomain"), "@localhost.localdomain should be accepted but wasn't");
        assertFalse(noLocal.isValid("joe@localhost"), "@localhost should be accepted but wasn't");
    }

    /**
     * Write this test according to parts of RFC, as opposed to the type of character that is being tested.
     */
    @Test
    public void testEmailUserName() {

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

        // UnQuoted Special characters are invalid

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

        // Quoted Special characters are valid
        assertTrue(validator.isValid("\"joe.\"@apache.org"));

        assertTrue(validator.isValid("\".joe\"@apache.org"));

        assertTrue(validator.isValid("\"joe+\"@apache.org"));

        assertTrue(validator.isValid("\"joe@\"@apache.org"));

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

        // escaped quote character valid in quoted string
        assertTrue(validator.isValid("\"john\\\"doe\"@apache.org"));

        assertTrue(validator.isValid("john56789.john56789.john56789.john56789.john56789.john56789.john@example.com"));

        assertFalse(validator.isValid("john56789.john56789.john56789.john56789.john56789.john56789.john5@example.com"));

        assertTrue(validator.isValid("\\>escape\\\\special\\^characters\\<@example.com"));

        assertTrue(validator.isValid("Abc\\@def@example.com"));

        assertFalse(validator.isValid("Abc@def@example.com"));

        assertTrue(validator.isValid("space\\ monkey@example.com"));
    }

    /**
     * Tests the e-mail validation with an RCS-noncompliant character in the address.
     */
    @Test
    public void testEmailWithBogusCharacter() {

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

    /**
     * Tests the email validation with commas.
     */
    @Test
    public void testEmailWithCommas() {
        assertFalse(validator.isValid("joeblow@apa,che.org"));

        assertFalse(validator.isValid("joeblow@apache.o,rg"));

        assertFalse(validator.isValid("joeblow@apache,org"));

    }

    /**
     * Tests the email validation with ASCII control characters. (i.e. ASCII chars 0 - 31 and 127)
     */
    @Test
    public void testEmailWithControlChars() {
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
    public void testEmailWithDash() {
        assertTrue(validator.isValid("andy.noble@data-workshop.com"));

        assertFalse(validator.isValid("andy-noble@data-workshop.-com"));

        assertFalse(validator.isValid("andy-noble@data-workshop.c-om"));

        assertFalse(validator.isValid("andy-noble@data-workshop.co-m"));
    }

    /**
     * Tests the e-mail validation with a dot at the end of the address.
     */
    @Test
    public void testEmailWithDotEnd() {
        assertFalse(validator.isValid("andy.noble@data-workshop.com."));
    }

    /**
     * Tests the email validation with numeric domains.
     */
    @Test
    public void testEmailWithNumericAddress() {
        assertTrue(validator.isValid("someone@[216.109.118.76]"));
        assertTrue(validator.isValid("someone@yahoo.com"));
    }

    /**
     * VALIDATOR-296 - A / or a ! is valid in the user part, but not in the domain part
     */
    @Test
    public void testEmailWithSlashes() {
        assertTrue(validator.isValid("joe!/blow@apache.org"), "/ and ! valid in username");
        assertFalse(validator.isValid("joe@ap/ache.org"), "/ not valid in domain");
        assertFalse(validator.isValid("joe@apac!he.org"), "! not valid in domain");
    }

    /**
     * Tests the email validation with spaces.
     */
    @Test
    public void testEmailWithSpaces() {
        assertFalse(validator.isValid("joeblow @apache.org"));

        assertFalse(validator.isValid("joeblow@ apache.org"));

        assertFalse(validator.isValid(" joeblow@apache.org"));

        assertFalse(validator.isValid("joeblow@apache.org "));

        assertFalse(validator.isValid("joe blow@apache.org "));

        assertFalse(validator.isValid("joeblow@apa che.org "));

        assertTrue(validator.isValid("\"joeblow \"@apache.org"));

        assertTrue(validator.isValid("\" joeblow\"@apache.org"));

        assertTrue(validator.isValid("\" joe blow \"@apache.org"));

    }

    @Test
    public void testValidator235() {
        final String version = SystemProperties.getJavaVersion();
        if (version.compareTo("1.6") < 0) {
            System.out.println("Cannot run Unicode IDN tests");
            return; // Cannot run the test
        }
        assertTrue(validator.isValid("someone@xn--d1abbgf6aiiy.xn--p1ai"), "xn--d1abbgf6aiiy.xn--p1ai should validate");
        assertTrue(validator.isValid("someone@президент.рф"), "президент.рф should validate");
        assertTrue(validator.isValid("someone@www.b\u00fccher.ch"), "www.b\u00fccher.ch should validate");
        assertFalse(validator.isValid("someone@www.\uFFFD.ch"), "www.\uFFFD.ch FFFD should fail");
        assertTrue(validator.isValid("someone@www.b\u00fccher.ch"), "www.b\u00fccher.ch should validate");
        assertFalse(validator.isValid("someone@www.\uFFFD.ch"), "www.\uFFFD.ch FFFD should fail");
    }

    @Test
    public void testValidator278() {
        assertFalse(validator.isValid("someone@-test.com")); // hostname starts with dash/hyphen
        assertFalse(validator.isValid("someone@test-.com")); // hostname ends with dash/hyphen
    }

    @Test
    public void testValidator293() {
        assertTrue(validator.isValid("abc-@abc.com"));
        assertTrue(validator.isValid("abc_@abc.com"));
        assertTrue(validator.isValid("abc-def@abc.com"));
        assertTrue(validator.isValid("abc_def@abc.com"));
        assertFalse(validator.isValid("abc@abc_def.com"));
    }

    @Test
    public void testValidator315() {
        assertFalse(validator.isValid("me@at&t.net"));
        assertTrue(validator.isValid("me@att.net")); // Make sure TLD is not the cause of the failure
    }

    @Test
    public void testValidator359() {
        final EmailValidator val = EmailValidator.getInstance(false, true);
        assertFalse(val.isValid("test@.com"));
    }

    @Test
    public void testValidator365() {
        assertFalse(validator.isValid("Loremipsumdolorsitametconsecteturadipiscingelit.Nullavitaeligulamattisrhoncusnuncegestasmattisleo."
                + "Donecnonsapieninmagnatristiquedictumaacturpis.Fusceorciduifacilisisutsapieneuconsequatpharetralectus."
                + "Quisqueenimestpulvinarutquamvitaeportamattisex.Nullamquismaurisplaceratconvallisjustoquisportamauris."
                + "Innullalacusconvalliseufringillautvenenatissitametdiam.Maecenasluctusligulascelerisquepulvinarfeugiat."
                + "Sedmolestienullaaliquetorciluctusidpharetranislfinibus.Suspendissemalesuadatinciduntduisitametportaarcusollicitudinnec."
                + "Donecetmassamagna.Curabitururnadiampretiumveldignissimporttitorfringillaeuneque."
                + "Duisantetelluspharetraidtinciduntinterdummolestiesitametfelis.Utquisquamsitametantesagittisdapibusacnonodio."
                + "Namrutrummolestiediamidmattis.Cumsociisnatoquepenatibusetmagnisdisparturientmontesnasceturridiculusmus."
                + "Morbiposueresedmetusacconsectetur.Etiamquisipsumvitaejustotempusmaximus.Sedultriciesplaceratvolutpat."
                + "Integerlacuslectusmaximusacornarequissagittissitametjusto."
                + "Cumsociisnatoquepenatibusetmagnisdisparturientmontesnasceturridiculusmus.Maecenasindictumpurussedrutrumex.Nullafacilisi."
                + "Integerfinibusfinibusmietpharetranislfaucibusvel.Maecenasegetdolorlacinialobortisjustovelullamcorpersem."
                + "Vivamusaliquetpurusidvariusornaresapienrisusrutrumnisitinciduntmollissemnequeidmetus."
                + "Etiamquiseleifendpurus.Nuncfelisnuncscelerisqueiddignissimnecfinibusalibero."
                + "Nuncsemperenimnequesitamethendreritpurusfacilisisac.Maurisdapibussemperfelisdignissimgravida."
                + "Aeneanultricesblanditnequealiquamfinibusodioscelerisqueac.Aliquamnecmassaeumaurisfaucibusfringilla."
                + "Etiamconsequatligulanisisitametaliquamnibhtemporquis.Nuncinterdumdignissimnullaatsodalesarcusagittiseu."
                + "Proinpharetrametusneclacuspulvinarsedvolutpatliberoornare.Sedligulanislpulvinarnonlectuseublanditfacilisisante."
                + "Sedmollisnislalacusauctorsuscipit.Inhachabitasseplateadictumst.Phasellussitametvelittemporvenenatisfeliseuegestasrisus."
                + "Aliquameteratsitametnibhcommodofinibus.Morbiefficiturodiovelpulvinariaculis."
                + "Aeneantemporipsummassaaconsecteturturpisfaucibusultrices.Praesentsodalesmaurisquisportafermentum."
                + "Etiamnisinislvenenatisvelauctorutullamcorperinjusto.Proinvelligulaerat.Phasellusvestibulumgravidamassanonfeugiat."
                + "Maecenaspharetraeuismodmetusegetefficitur.Suspendisseamet@gmail.com"));
    }

    @Test
    public void testValidator374() {
        assertTrue(validator.isValid("abc@school.school"));
    }

    @Test
    public void testValidator473Part1() { // reject null DomainValidator
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> new EmailValidator(false, false, null));
        assertEquals("DomainValidator cannot be null", thrown.getMessage());
    }

    @Test
    public void testValidator473Part2() { // reject null DomainValidator with mismatched allowLocal
        final List<DomainValidator.Item> items = new ArrayList<>();
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> new EmailValidator(false, false, DomainValidator.getInstance(true, items)));
        assertEquals("DomainValidator must agree with allowLocal setting", thrown.getMessage());
    }

    @Test
    public void testValidator473Part3() { // reject null DomainValidator with mismatched allowLocal
        final List<DomainValidator.Item> items = new ArrayList<>();
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> new EmailValidator(true, false, DomainValidator.getInstance(false, items)));
        assertEquals("DomainValidator must agree with allowLocal setting", thrown.getMessage());
    }

    @Test
    public void testValidator473Part4() { // Show that can override domain validation
        assertFalse(validator.isValidDomain("test.local"));
        final List<DomainValidator.Item> items = new ArrayList<>();
        items.add(new DomainValidator.Item(DomainValidator.ArrayType.GENERIC_PLUS, "local"));
        final EmailValidator val = new EmailValidator(true, false, DomainValidator.getInstance(true, items));
        assertTrue(val.isValidDomain("test.local"));
    }
}
