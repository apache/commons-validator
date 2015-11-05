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
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;

/**
 * Test the CreditCardValidator class.
 *
 * @version $Revision$
 */
public class CreditCardValidatorTest extends TestCase {
    
    private static final String VALID_VISA = "4417123456789113";
    private static final String ERROR_VISA = "4417123456789112";
    private static final String VALID_SHORT_VISA = "4222222222222";
    private static final String ERROR_SHORT_VISA = "4222222222229";
    private static final String VALID_AMEX = "378282246310005";
    private static final String ERROR_AMEX = "378282246310001";
    private static final String VALID_MASTERCARD = "5105105105105100";
    private static final String ERROR_MASTERCARD = "5105105105105105";
    private static final String VALID_DISCOVER = "6011000990139424";
    private static final String ERROR_DISCOVER = "6011000990139421";
    private static final String VALID_DISCOVER65 = "6534567890123458"; // FIXME need verified test data for Discover with "65" prefix
    private static final String ERROR_DISCOVER65 = "6534567890123450"; // FIXME need verified test data for Discover with "65" prefix
    private static final String VALID_DINERS = "30569309025904";
    private static final String ERROR_DINERS = "30569309025901";
    private static final String VALID_VPAY = "4370000000000061";
    private static final String VALID_VPAY2 = "4370000000000012";
    private static final String ERROR_VPAY = "4370000000000069";

    /**
     * Constructor for CreditCardValidatorTest.
     */
    public CreditCardValidatorTest(String name) {
        super(name);
    }

    public void testIsValid() {
        CreditCardValidator ccv = new CreditCardValidator();
        
        assertNull(ccv.validate(null));

        assertFalse(ccv.isValid(null));
        assertFalse(ccv.isValid(""));
        assertFalse(ccv.isValid("123456789012"));   // too short
        assertFalse(ccv.isValid("12345678901234567890"));   // too long
        assertFalse(ccv.isValid("4417123456789112"));
        assertFalse(ccv.isValid("4417q23456w89113"));
        assertTrue(ccv.isValid(VALID_VISA));
        assertTrue(ccv.isValid(VALID_SHORT_VISA));
        assertTrue(ccv.isValid(VALID_AMEX));
        assertTrue(ccv.isValid(VALID_MASTERCARD));
        assertTrue(ccv.isValid(VALID_DISCOVER));
        assertTrue(ccv.isValid(VALID_DISCOVER65));

        assertFalse(ccv.isValid(ERROR_VISA));
        assertFalse(ccv.isValid(ERROR_SHORT_VISA));
        assertFalse(ccv.isValid(ERROR_AMEX));
        assertFalse(ccv.isValid(ERROR_MASTERCARD));
        assertFalse(ccv.isValid(ERROR_DISCOVER));
        assertFalse(ccv.isValid(ERROR_DISCOVER65));
        
        // disallow Visa so it should fail even with good number
        ccv = new CreditCardValidator(CreditCardValidator.AMEX);
        assertFalse(ccv.isValid("4417123456789113"));
    }
    
    public void testAddAllowedCardType() {
        CreditCardValidator ccv = new CreditCardValidator(CreditCardValidator.NONE);
        // Turned off all cards so even valid numbers should fail
        assertFalse(ccv.isValid(VALID_VISA));
        assertFalse(ccv.isValid(VALID_AMEX));
        assertFalse(ccv.isValid(VALID_MASTERCARD));
        assertFalse(ccv.isValid(VALID_DISCOVER));
        assertFalse(ccv.isValid(VALID_DINERS));
    }

    /**
     * Test the CodeValidator array constructor
     */    
    public void testArrayConstructor() {
        CreditCardValidator ccv = new CreditCardValidator(new CodeValidator[]
               {CreditCardValidator.VISA_VALIDATOR, CreditCardValidator.AMEX_VALIDATOR});
        
        assertTrue(ccv.isValid(VALID_VISA));
        assertTrue(ccv.isValid(VALID_SHORT_VISA));
        assertTrue(ccv.isValid(VALID_AMEX));
        assertFalse(ccv.isValid(VALID_MASTERCARD));
        assertFalse(ccv.isValid(VALID_DISCOVER));

        assertFalse(ccv.isValid(ERROR_VISA));
        assertFalse(ccv.isValid(ERROR_SHORT_VISA));
        assertFalse(ccv.isValid(ERROR_AMEX));
        assertFalse(ccv.isValid(ERROR_MASTERCARD));
        assertFalse(ccv.isValid(ERROR_DISCOVER));

        try {
            new CreditCardValidator((CodeValidator[]) null);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException iae) {
            // expected result
        }
    }

    /**
     * Test the Amex Card validator
     */    
    public void testAmexValidator() {

        CodeValidator validator = CreditCardValidator.AMEX_VALIDATOR;
        RegexValidator regex    = validator.getRegexValidator();

        // ****** Test Regular Expression ******
        // length 15 and start with a "34" or "37"
        assertFalse("Length 12",      regex.isValid("343456789012"));
        assertFalse("Length 13",      regex.isValid("3434567890123"));
        assertFalse("Length 14",      regex.isValid("34345678901234"));
        assertTrue("Length 15",       regex.isValid("343456789012345"));
        assertFalse("Length 16",      regex.isValid("3434567890123456"));
        assertFalse("Length 17",      regex.isValid("34345678901234567"));
        assertFalse("Length 18",      regex.isValid("343456789012345678"));
        assertFalse("Prefix 33",      regex.isValid("333456789012345"));
        assertTrue("Prefix 34",       regex.isValid("343456789012345"));
        assertFalse("Prefix 35",      regex.isValid("353456789012345"));
        assertFalse("Prefix 36",      regex.isValid("363456789012345"));
        assertTrue("Prefix 37",       regex.isValid("373456789012345"));
        assertFalse("Prefix 38",      regex.isValid("383456789012345"));
        assertFalse("Prefix 41",      regex.isValid("413456789012345"));
        assertFalse("Invalid Char",   regex.isValid("3434567x9012345"));

        // *********** Test Validator **********
        assertTrue("Valid regex",     regex.isValid(ERROR_AMEX));
        assertFalse("Invalid",        validator.isValid(ERROR_AMEX));
        assertNull("validate()",      validator.validate(ERROR_AMEX));
        assertEquals(VALID_AMEX,      validator.validate(VALID_AMEX));

        assertTrue("Amex",            validator.isValid(VALID_AMEX));
        assertFalse("Diners",         validator.isValid(VALID_DINERS));
        assertFalse("Discover",       validator.isValid(VALID_DISCOVER));
        assertFalse("Mastercard",     validator.isValid(VALID_MASTERCARD));
        assertFalse("Visa",           validator.isValid(VALID_VISA));
        assertFalse("Visa Short",     validator.isValid(VALID_SHORT_VISA));
        
        assertTrue("Valid-A",         validator.isValid("371449635398431"));
        assertTrue("Valid-B",         validator.isValid("340000000000009"));
        assertTrue("Valid-C",         validator.isValid("370000000000002"));
        assertTrue("Valid-D",         validator.isValid("378734493671000"));
    }

    /**
     * Test the Amex Card option
     */    
    public void testAmexOption() {
        CreditCardValidator validator = new CreditCardValidator(CreditCardValidator.AMEX);
        assertFalse("Invalid",        validator.isValid(ERROR_AMEX));
        assertNull("validate()",      validator.validate(ERROR_AMEX));
        assertEquals(VALID_AMEX,      validator.validate(VALID_AMEX));

        assertTrue("Amex",            validator.isValid(VALID_AMEX));
        assertFalse("Diners",         validator.isValid(VALID_DINERS));
        assertFalse("Discover",       validator.isValid(VALID_DISCOVER));
        assertFalse("Mastercard",     validator.isValid(VALID_MASTERCARD));
        assertFalse("Visa",           validator.isValid(VALID_VISA));
        assertFalse("Visa Short",     validator.isValid(VALID_SHORT_VISA));
    }

    /**
     * Test the Diners Card validator
     */    
    public void testDinersValidator() {

        CodeValidator validator = CreditCardValidator.DINERS_VALIDATOR;
        RegexValidator regex    = validator.getRegexValidator();

        // ****** Test Regular Expression ******
        // length 14 and start with a "300-305" or "3095" or "36" or "38" or "39"
        assertFalse("Length 12-300",  regex.isValid("300456789012"));
        assertFalse("Length 12-36",   regex.isValid("363456789012"));
        assertFalse("Length 13-300",  regex.isValid("3004567890123"));
        assertFalse("Length 13-36",   regex.isValid("3634567890123"));
        assertTrue("Length 14-300",   regex.isValid("30045678901234"));
        assertTrue("Length 14-36",    regex.isValid("36345678901234"));
        assertFalse("Length 15-300",  regex.isValid("300456789012345"));
        assertFalse("Length 15-36",   regex.isValid("363456789012345"));
        assertFalse("Length 16-300",  regex.isValid("3004567890123456"));
        assertFalse("Length 16-36",   regex.isValid("3634567890123456"));
        assertFalse("Length 17-300",  regex.isValid("30045678901234567"));
        assertFalse("Length 17-36",   regex.isValid("36345678901234567"));
        assertFalse("Length 18-300",  regex.isValid("300456789012345678"));
        assertFalse("Length 18-36",   regex.isValid("363456789012345678"));

        assertTrue("Prefix 300",      regex.isValid("30045678901234"));
        assertTrue("Prefix 301",      regex.isValid("30145678901234"));
        assertTrue("Prefix 302",      regex.isValid("30245678901234"));
        assertTrue("Prefix 303",      regex.isValid("30345678901234"));
        assertTrue("Prefix 304",      regex.isValid("30445678901234"));
        assertTrue("Prefix 305",      regex.isValid("30545678901234"));
        assertFalse("Prefix 306",     regex.isValid("30645678901234"));
        assertFalse("Prefix 3094",    regex.isValid("30945678901234"));
        assertTrue( "Prefix 3095",    regex.isValid("30955678901234"));
        assertFalse("Prefix 3096",    regex.isValid("30965678901234"));
        assertFalse("Prefix 35",      regex.isValid("35345678901234"));
        assertTrue("Prefix 36",       regex.isValid("36345678901234"));
        assertFalse("Prefix 37",      regex.isValid("37345678901234"));
        assertTrue("Prefix 38",       regex.isValid("38345678901234"));
        assertTrue("Prefix 39",       regex.isValid("39345678901234"));

        assertFalse("Invalid Char-A", regex.isValid("3004567x901234"));
        assertFalse("Invalid Char-B", regex.isValid("3634567x901234"));

        // *********** Test Validator **********
        assertTrue("Valid regex",     regex.isValid(ERROR_DINERS));
        assertFalse("Invalid",        validator.isValid(ERROR_DINERS));
        assertNull("validate()",      validator.validate(ERROR_DINERS));
        assertEquals(VALID_DINERS,    validator.validate(VALID_DINERS));

        assertFalse("Amex",           validator.isValid(VALID_AMEX));
        assertTrue("Diners",          validator.isValid(VALID_DINERS));
        assertFalse("Discover",       validator.isValid(VALID_DISCOVER));
        assertFalse("Mastercard",     validator.isValid(VALID_MASTERCARD));
        assertFalse("Visa",           validator.isValid(VALID_VISA));
        assertFalse("Visa Short",     validator.isValid(VALID_SHORT_VISA));
        
        assertTrue("Valid-A",         validator.isValid("30000000000004"));
        assertTrue("Valid-B",         validator.isValid("30123456789019"));
        assertTrue("Valid-C",         validator.isValid("36432685260294"));

    }

    /**
     * Test the Diners Card option
     */    
    public void testDinersOption() {
        CreditCardValidator validator = new CreditCardValidator(CreditCardValidator.DINERS);
        assertFalse("Invalid",        validator.isValid(ERROR_DINERS));
        assertNull("validate()",      validator.validate(ERROR_DINERS));
        assertEquals(VALID_DINERS,    validator.validate(VALID_DINERS));

        assertFalse("Amex",           validator.isValid(VALID_AMEX));
        assertTrue("Diners",          validator.isValid(VALID_DINERS));
        assertFalse("Discover",       validator.isValid(VALID_DISCOVER));
        assertFalse("Mastercard",     validator.isValid(VALID_MASTERCARD));
        assertFalse("Visa",           validator.isValid(VALID_VISA));
        assertFalse("Visa Short",     validator.isValid(VALID_SHORT_VISA));
    }

    /**
     * Test the Discover Card validator
     */    
    public void testDiscoverValidator() {

        CodeValidator validator = CreditCardValidator.DISCOVER_VALIDATOR;
        RegexValidator regex    = validator.getRegexValidator();

        // ****** Test Regular Expression ******
        // length 16 and start with either "6011" or or "64[4-9]" or "65"
        assertFalse("Length 12-6011", regex.isValid("601156789012"));
        assertFalse("Length 12-65",   regex.isValid("653456789012"));
        assertFalse("Length 13-6011", regex.isValid("6011567890123"));
        assertFalse("Length 13-65",   regex.isValid("6534567890123"));
        assertFalse("Length 14-6011", regex.isValid("60115678901234"));
        assertFalse("Length 14-65",   regex.isValid("65345678901234"));
        assertFalse("Length 15-6011", regex.isValid("601156789012345"));
        assertFalse("Length 15-65",   regex.isValid("653456789012345"));
        assertTrue("Length 16-6011",  regex.isValid("6011567890123456"));
        assertTrue("Length 16-644",   regex.isValid("6444567890123456"));
        assertTrue("Length 16-648",   regex.isValid("6484567890123456"));
        assertTrue("Length 16-65",    regex.isValid("6534567890123456"));
        assertFalse("Length 17-6011", regex.isValid("60115678901234567"));
        assertFalse("Length 17-65",   regex.isValid("65345678901234567"));
        assertFalse("Length 18-6011", regex.isValid("601156789012345678"));
        assertFalse("Length 18-65",   regex.isValid("653456789012345678"));

        assertFalse("Prefix 640",     regex.isValid("6404567890123456"));
        assertFalse("Prefix 641",     regex.isValid("6414567890123456"));
        assertFalse("Prefix 642",     regex.isValid("6424567890123456"));
        assertFalse("Prefix 643",     regex.isValid("6434567890123456"));
        assertFalse("Prefix 6010",    regex.isValid("6010567890123456"));
        assertFalse("Prefix 6012",    regex.isValid("6012567890123456"));
        assertFalse("Invalid Char",   regex.isValid("6011567x90123456"));

        // *********** Test Validator **********
        assertTrue("Valid regex",     regex.isValid(ERROR_DISCOVER));
        assertTrue("Valid regex65",   regex.isValid(ERROR_DISCOVER65));
        assertFalse("Invalid",        validator.isValid(ERROR_DISCOVER));
        assertFalse("Invalid65",      validator.isValid(ERROR_DISCOVER65));
        assertNull("validate()",      validator.validate(ERROR_DISCOVER));
        assertEquals(VALID_DISCOVER,  validator.validate(VALID_DISCOVER));
        assertEquals(VALID_DISCOVER65, validator.validate(VALID_DISCOVER65));

        assertFalse("Amex",           validator.isValid(VALID_AMEX));
        assertFalse("Diners",         validator.isValid(VALID_DINERS));
        assertTrue("Discover",        validator.isValid(VALID_DISCOVER));
        assertTrue("Discover",        validator.isValid(VALID_DISCOVER65));
        assertFalse("Mastercard",     validator.isValid(VALID_MASTERCARD));
        assertFalse("Visa",           validator.isValid(VALID_VISA));
        assertFalse("Visa Short",     validator.isValid(VALID_SHORT_VISA));
        
        assertTrue("Valid-A",         validator.isValid("6011111111111117"));
        assertTrue("Valid-B",         validator.isValid("6011000000000004"));
        assertTrue("Valid-C",         validator.isValid("6011000000000012"));

    }

    /**
     * Test the Discover Card option
     */    
    public void testDiscoverOption() {
        CreditCardValidator validator = new CreditCardValidator(CreditCardValidator.DISCOVER);
        assertFalse("Invalid",        validator.isValid(ERROR_DISCOVER));
        assertFalse("Invalid65",      validator.isValid(ERROR_DISCOVER65));
        assertNull("validate()",      validator.validate(ERROR_DISCOVER));
        assertEquals(VALID_DISCOVER,  validator.validate(VALID_DISCOVER));
        assertEquals(VALID_DISCOVER65, validator.validate(VALID_DISCOVER65));

        assertFalse("Amex",           validator.isValid(VALID_AMEX));
        assertFalse("Diners",         validator.isValid(VALID_DINERS));
        assertTrue("Discover",        validator.isValid(VALID_DISCOVER));
        assertTrue("Discover",        validator.isValid(VALID_DISCOVER65));
        assertFalse("Mastercard",     validator.isValid(VALID_MASTERCARD));
        assertFalse("Visa",           validator.isValid(VALID_VISA));
        assertFalse("Visa Short",     validator.isValid(VALID_SHORT_VISA));
    }

    /**
     * Test the Mastercard Card validator
     */    
    public void testMastercardValidator() {

        CodeValidator validator = CreditCardValidator.MASTERCARD_VALIDATOR;
        RegexValidator regex    = validator.getRegexValidator();

        // ****** Test Regular Expression ******
        // length 16 and start with a "51-55"
        assertFalse("Length 12",      regex.isValid("513456789012"));
        assertFalse("Length 13",      regex.isValid("5134567890123"));
        assertFalse("Length 14",      regex.isValid("51345678901234"));
        assertFalse("Length 15",      regex.isValid("513456789012345"));
        assertTrue("Length 16",       regex.isValid("5134567890123456"));
        assertFalse("Length 17",      regex.isValid("51345678901234567"));
        assertFalse("Length 18",      regex.isValid("513456789012345678"));
        assertFalse("Prefix 41",      regex.isValid("4134567890123456"));
        assertFalse("Prefix 50",      regex.isValid("5034567890123456"));
        assertTrue("Prefix 51",       regex.isValid("5134567890123456"));
        assertTrue("Prefix 52",       regex.isValid("5234567890123456"));
        assertTrue("Prefix 53",       regex.isValid("5334567890123456"));
        assertTrue("Prefix 54",       regex.isValid("5434567890123456"));
        assertTrue("Prefix 55",       regex.isValid("5534567890123456"));
        assertFalse("Prefix 56",      regex.isValid("5634567890123456"));
        assertFalse("Prefix 61",      regex.isValid("6134567890123456"));
        assertFalse("Invalid Char",   regex.isValid("5134567x90123456"));

        // *********** Test Validator **********
        assertTrue("Valid regex",     regex.isValid(ERROR_MASTERCARD));
        assertFalse("Invalid",        validator.isValid(ERROR_MASTERCARD));
        assertNull("validate()",      validator.validate(ERROR_MASTERCARD));
        assertEquals(VALID_MASTERCARD, validator.validate(VALID_MASTERCARD));

        assertFalse("Amex",           validator.isValid(VALID_AMEX));
        assertFalse("Diners",         validator.isValid(VALID_DINERS));
        assertFalse("Discover",       validator.isValid(VALID_DISCOVER));
        assertTrue("Mastercard",      validator.isValid(VALID_MASTERCARD));
        assertFalse("Visa",           validator.isValid(VALID_VISA));
        assertFalse("Visa Short",     validator.isValid(VALID_SHORT_VISA));
        
        assertTrue("Valid-A",         validator.isValid("5500000000000004"));
        assertTrue("Valid-B",         validator.isValid("5424000000000015"));
        assertTrue("Valid-C",         validator.isValid("5301250070000191"));
        assertTrue("Valid-D",         validator.isValid("5123456789012346"));
        assertTrue("Valid-E",         validator.isValid("5555555555554444"));
    }

    /**
     * Test the Mastercard Card option
     */    
    public void testMastercardOption() {
        CreditCardValidator validator = new CreditCardValidator(CreditCardValidator.MASTERCARD);
        assertFalse("Invalid",        validator.isValid(ERROR_MASTERCARD));
        assertNull("validate()",      validator.validate(ERROR_MASTERCARD));
        assertEquals(VALID_MASTERCARD, validator.validate(VALID_MASTERCARD));

        assertFalse("Amex",           validator.isValid(VALID_AMEX));
        assertFalse("Diners",         validator.isValid(VALID_DINERS));
        assertFalse("Discover",       validator.isValid(VALID_DISCOVER));
        assertTrue("Mastercard",      validator.isValid(VALID_MASTERCARD));
        assertFalse("Visa",           validator.isValid(VALID_VISA));
        assertFalse("Visa Short",     validator.isValid(VALID_SHORT_VISA));
    }

    /**
     * Test the Visa Card validator
     */    
    public void testVisaValidator() {

        CodeValidator validator = CreditCardValidator.VISA_VALIDATOR;
        RegexValidator regex    = validator.getRegexValidator();

        // ****** Test Regular Expression ******
        // length 13 or 16, must start with a "4"
        assertFalse("Length 12",      regex.isValid("423456789012"));
        assertTrue("Length 13",       regex.isValid("4234567890123"));
        assertFalse("Length 14",      regex.isValid("42345678901234"));
        assertFalse("Length 15",      regex.isValid("423456789012345"));
        assertTrue("Length 16",       regex.isValid("4234567890123456"));
        assertFalse("Length 17",      regex.isValid("42345678901234567"));
        assertFalse("Length 18",      regex.isValid("423456789012345678"));
        assertFalse("Invalid Pref-A", regex.isValid("3234567890123"));
        assertFalse("Invalid Pref-B", regex.isValid("3234567890123456"));
        assertFalse("Invalid Char-A", regex.isValid("4234567x90123"));
        assertFalse("Invalid Char-B", regex.isValid("4234567x90123456"));

        // *********** Test Validator **********
        assertTrue("Valid regex",     regex.isValid(ERROR_VISA));
        assertTrue("Valid regex-S",   regex.isValid(ERROR_SHORT_VISA));
        assertFalse("Invalid",        validator.isValid(ERROR_VISA));
        assertFalse("Invalid-S",      validator.isValid(ERROR_SHORT_VISA));
        assertNull("validate()",      validator.validate(ERROR_VISA));
        assertEquals(VALID_VISA,      validator.validate(VALID_VISA));
        assertEquals(VALID_SHORT_VISA, validator.validate(VALID_SHORT_VISA));

        assertFalse("Amex",           validator.isValid(VALID_AMEX));
        assertFalse("Diners",         validator.isValid(VALID_DINERS));
        assertFalse("Discover",       validator.isValid(VALID_DISCOVER));
        assertFalse("Mastercard",     validator.isValid(VALID_MASTERCARD));
        assertTrue("Visa",            validator.isValid(VALID_VISA));
        assertTrue("Visa Short",      validator.isValid(VALID_SHORT_VISA));
        
        assertTrue("Valid-A",         validator.isValid("4111111111111111"));
        assertTrue("Valid-C",         validator.isValid("4543059999999982"));
        assertTrue("Valid-B",         validator.isValid("4462000000000003"));
        assertTrue("Valid-D",         validator.isValid("4508750000000009")); // Electron
        assertTrue("Valid-E",         validator.isValid("4012888888881881"));
    }

    /**
     * Test the Visa Card option
     */    
    public void testVisaOption() {
        CreditCardValidator validator = new CreditCardValidator(CreditCardValidator.VISA);
        assertFalse("Invalid",        validator.isValid(ERROR_VISA));
        assertFalse("Invalid-S",      validator.isValid(ERROR_SHORT_VISA));
        assertNull("validate()",      validator.validate(ERROR_VISA));
        assertEquals(VALID_VISA,      validator.validate(VALID_VISA));
        assertEquals(VALID_SHORT_VISA, validator.validate(VALID_SHORT_VISA));

        assertFalse("Amex",           validator.isValid(VALID_AMEX));
        assertFalse("Diners",         validator.isValid(VALID_DINERS));
        assertFalse("Discover",       validator.isValid(VALID_DISCOVER));
        assertFalse("Mastercard",     validator.isValid(VALID_MASTERCARD));
        assertTrue("Visa",            validator.isValid(VALID_VISA));
        assertTrue("Visa Short",      validator.isValid(VALID_SHORT_VISA));
    }

    public void testVPayOption() {
        CreditCardValidator validator = new CreditCardValidator(CreditCardValidator.VPAY);
        assertTrue("Valid",           validator.isValid(VALID_VPAY));
        assertTrue("Valid",           validator.isValid(VALID_VPAY2));
        assertFalse("Invalid",        validator.isValid(ERROR_VPAY));
        assertEquals(VALID_VPAY,      validator.validate(VALID_VPAY));
        assertEquals(VALID_VPAY2,      validator.validate(VALID_VPAY2));

        assertFalse("Amex",           validator.isValid(VALID_AMEX));
        assertFalse("Diners",         validator.isValid(VALID_DINERS));
        assertFalse("Discover",       validator.isValid(VALID_DISCOVER));
        assertFalse("Mastercard",     validator.isValid(VALID_MASTERCARD));
        assertTrue("Visa",            validator.isValid(VALID_VISA));
        assertTrue("Visa Short",      validator.isValid(VALID_SHORT_VISA));        
    }

    /**
     * Test using separators
     */    
    public void testMastercardUsingSeparators() {

        String MASTERCARD_REGEX_SEP = "^(5[1-5]\\d{2})(?:[- ])?(\\d{4})(?:[- ])?(\\d{4})(?:[- ])?(\\d{4})$";
        CodeValidator validator = new CodeValidator(MASTERCARD_REGEX_SEP, LuhnCheckDigit.LUHN_CHECK_DIGIT);
        RegexValidator regex    = validator.getRegexValidator();

        // ****** Test Regular Expression ******
        // length 16 and start with a "51-55"
        assertEquals("Number",  "5134567890123456", regex.validate("5134567890123456"));
        assertEquals("Hyphen",  "5134567890123456", regex.validate("5134-5678-9012-3456"));
        assertEquals("Space",   "5134567890123456", regex.validate("5134 5678 9012 3456"));
        assertEquals("MixedA",  "5134567890123456", regex.validate("5134-5678 9012-3456"));
        assertEquals("MixedB",  "5134567890123456", regex.validate("5134 5678-9012 3456"));

        assertFalse("Invalid Separator A",  regex.isValid("5134.5678.9012.3456"));
        assertFalse("Invalid Separator B",  regex.isValid("5134_5678_9012_3456"));
        assertFalse("Invalid Grouping A",   regex.isValid("513-45678-9012-3456"));
        assertFalse("Invalid Grouping B",   regex.isValid("5134-567-89012-3456"));
        assertFalse("Invalid Grouping C",   regex.isValid("5134-5678-901-23456"));

        // *********** Test Validator **********
        assertEquals("Valid-A", "5500000000000004", validator.validate("5500-0000-0000-0004"));
        assertEquals("Valid-B", "5424000000000015", validator.validate("5424 0000 0000 0015"));
        assertEquals("Valid-C", "5301250070000191", validator.validate("5301-250070000191"));
        assertEquals("Valid-D", "5123456789012346", validator.validate("5123456789012346"));
    }

}
