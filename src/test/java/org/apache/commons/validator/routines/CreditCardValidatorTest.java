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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.commons.validator.routines.CreditCardValidator.CreditCardRange;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;
import org.junit.jupiter.api.Test;

/**
 * Test the CreditCardValidator class.
 */
public class CreditCardValidatorTest {

    private static final String VALID_VISA = "4417123456789113"; // 16
    private static final String ERROR_VISA = "4417123456789112";
    private static final String VALID_SHORT_VISA = "4222222222222"; // 13
    private static final String ERROR_SHORT_VISA = "4222222222229";
    private static final String VALID_AMEX = "378282246310005"; // 15
    private static final String ERROR_AMEX = "378282246310001";
    private static final String VALID_MASTERCARD = "5105105105105100";
    private static final String ERROR_MASTERCARD = "5105105105105105";
    private static final String VALID_DISCOVER = "6011000990139424";
    private static final String ERROR_DISCOVER = "6011000990139421";
    private static final String VALID_DISCOVER65 = "6534567890123458"; // FIXME need verified test data for Discover with "65" prefix
    private static final String ERROR_DISCOVER65 = "6534567890123450"; // FIXME need verified test data for Discover with "65" prefix
    private static final String VALID_DINERS = "30569309025904"; // 14
    private static final String ERROR_DINERS = "30569309025901";
    private static final String VALID_VPAY = "4370000000000061"; // 16
    private static final String VALID_VPAY2 = "4370000000000012";
    private static final String ERROR_VPAY = "4370000000000069";

    private static final String[] VALID_CARDS = { VALID_VISA, VALID_SHORT_VISA, VALID_AMEX, VALID_MASTERCARD, VALID_DISCOVER, VALID_DISCOVER65, VALID_DINERS,
            VALID_VPAY, VALID_VPAY2, "60115564485789458", // VALIDATOR-403
    };

    private static final String[] ERROR_CARDS = { ERROR_VISA, ERROR_SHORT_VISA, ERROR_AMEX, ERROR_MASTERCARD, ERROR_DISCOVER, ERROR_DISCOVER65, ERROR_DINERS,
            ERROR_VPAY,
//        ERROR_VPAY2,
            "", "12345678901", // too short (11)
            "12345678901234567890", // too long (20)
            "4417123456789112", // invalid check digit
    };

    @Test
    public void testAddAllowedCardType() {
        final CreditCardValidator ccv = new CreditCardValidator(CreditCardValidator.NONE);
        // Turned off all cards so even valid numbers should fail
        assertFalse(ccv.isValid(VALID_VISA));
        assertFalse(ccv.isValid(VALID_AMEX));
        assertFalse(ccv.isValid(VALID_MASTERCARD));
        assertFalse(ccv.isValid(VALID_DISCOVER));
        assertFalse(ccv.isValid(VALID_DINERS));
    }

    /**
     * Test the Amex Card option
     */
    @Test
    public void testAmexOption() {
        final CreditCardValidator validator = new CreditCardValidator(CreditCardValidator.AMEX);
        assertFalse(validator.isValid(ERROR_AMEX), "Invalid");
        assertNull(validator.validate(ERROR_AMEX), "validate()");
        assertEquals(VALID_AMEX, validator.validate(VALID_AMEX));

        assertTrue(validator.isValid(VALID_AMEX), "Amex");
        assertFalse(validator.isValid(VALID_DINERS), "Diners");
        assertFalse(validator.isValid(VALID_DISCOVER), "Discover");
        assertFalse(validator.isValid(VALID_MASTERCARD), "Mastercard");
        assertFalse(validator.isValid(VALID_VISA), "Visa");
        assertFalse(validator.isValid(VALID_SHORT_VISA), "Visa Short");
    }

    /**
     * Test the Amex Card validator
     */
    @Test
    public void testAmexValidator() {

        final CodeValidator validator = CreditCardValidator.AMEX_VALIDATOR;
        final RegexValidator regex = validator.getRegexValidator();

        // ****** Test Regular Expression ******
        // length 15 and start with a "34" or "37"
        assertFalse(regex.isValid("343456789012"), "Length 12");
        assertFalse(regex.isValid("3434567890123"), "Length 13");
        assertFalse(regex.isValid("34345678901234"), "Length 14");
        assertTrue(regex.isValid("343456789012345"), "Length 15");
        assertFalse(regex.isValid("3434567890123456"), "Length 16");
        assertFalse(regex.isValid("34345678901234567"), "Length 17");
        assertFalse(regex.isValid("343456789012345678"), "Length 18");
        assertFalse(regex.isValid("333456789012345"), "Prefix 33");
        assertTrue(regex.isValid("343456789012345"), "Prefix 34");
        assertFalse(regex.isValid("353456789012345"), "Prefix 35");
        assertFalse(regex.isValid("363456789012345"), "Prefix 36");
        assertTrue(regex.isValid("373456789012345"), "Prefix 37");
        assertFalse(regex.isValid("383456789012345"), "Prefix 38");
        assertFalse(regex.isValid("413456789012345"), "Prefix 41");
        assertFalse(regex.isValid("3434567x9012345"), "Invalid Char");

        // *********** Test Validator **********
        assertTrue(regex.isValid(ERROR_AMEX), "Valid regex");
        assertFalse(validator.isValid(ERROR_AMEX), "Invalid");
        assertNull(validator.validate(ERROR_AMEX), "validate()");
        assertEquals(VALID_AMEX, validator.validate(VALID_AMEX));

        assertTrue(validator.isValid(VALID_AMEX), "Amex");
        assertFalse(validator.isValid(VALID_DINERS), "Diners");
        assertFalse(validator.isValid(VALID_DISCOVER), "Discover");
        assertFalse(validator.isValid(VALID_MASTERCARD), "Mastercard");
        assertFalse(validator.isValid(VALID_VISA), "Visa");
        assertFalse(validator.isValid(VALID_SHORT_VISA), "Visa Short");

        assertTrue(validator.isValid("371449635398431"), "Valid-A");
        assertTrue(validator.isValid("340000000000009"), "Valid-B");
        assertTrue(validator.isValid("370000000000002"), "Valid-C");
        assertTrue(validator.isValid("378734493671000"), "Valid-D");
    }

    /**
     * Test the CodeValidator array constructor
     */
    @Test
    public void testArrayConstructor() {
        final CreditCardValidator ccv = new CreditCardValidator(new CodeValidator[] { CreditCardValidator.VISA_VALIDATOR, CreditCardValidator.AMEX_VALIDATOR });

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
        } catch (final IllegalArgumentException iae) {
            // expected result
        }
    }

    /**
     * Test the Diners Card option
     */
    @Test
    public void testDinersOption() {
        final CreditCardValidator validator = new CreditCardValidator(CreditCardValidator.DINERS);
        assertFalse(validator.isValid(ERROR_DINERS), "Invalid");
        assertNull(validator.validate(ERROR_DINERS), "validate()");
        assertEquals(VALID_DINERS, validator.validate(VALID_DINERS));

        assertFalse(validator.isValid(VALID_AMEX), "Amex");
        assertTrue(validator.isValid(VALID_DINERS), "Diners");
        assertFalse(validator.isValid(VALID_DISCOVER), "Discover");
        assertFalse(validator.isValid(VALID_MASTERCARD), "Mastercard");
        assertFalse(validator.isValid(VALID_VISA), "Visa");
        assertFalse(validator.isValid(VALID_SHORT_VISA), "Visa Short");
    }

    /**
     * Test the Diners Card validator
     */
    @Test
    public void testDinersValidator() {

        final CodeValidator validator = CreditCardValidator.DINERS_VALIDATOR;
        final RegexValidator regex = validator.getRegexValidator();

        // ****** Test Regular Expression ******
        // length 14 and start with a "300-305" or "3095" or "36" or "38" or "39"
        assertFalse(regex.isValid("300456789012"), "Length 12-300");
        assertFalse(regex.isValid("363456789012"), "Length 12-36");
        assertFalse(regex.isValid("3004567890123"), "Length 13-300");
        assertFalse(regex.isValid("3634567890123"), "Length 13-36");
        assertTrue(regex.isValid("30045678901234"), "Length 14-300");
        assertTrue(regex.isValid("36345678901234"), "Length 14-36");
        assertFalse(regex.isValid("300456789012345"), "Length 15-300");
        assertFalse(regex.isValid("363456789012345"), "Length 15-36");
        assertFalse(regex.isValid("3004567890123456"), "Length 16-300");
        assertFalse(regex.isValid("3634567890123456"), "Length 16-36");
        assertFalse(regex.isValid("30045678901234567"), "Length 17-300");
        assertFalse(regex.isValid("36345678901234567"), "Length 17-36");
        assertFalse(regex.isValid("300456789012345678"), "Length 18-300");
        assertFalse(regex.isValid("363456789012345678"), "Length 18-36");

        assertTrue(regex.isValid("30045678901234"), "Prefix 300");
        assertTrue(regex.isValid("30145678901234"), "Prefix 301");
        assertTrue(regex.isValid("30245678901234"), "Prefix 302");
        assertTrue(regex.isValid("30345678901234"), "Prefix 303");
        assertTrue(regex.isValid("30445678901234"), "Prefix 304");
        assertTrue(regex.isValid("30545678901234"), "Prefix 305");
        assertFalse(regex.isValid("30645678901234"), "Prefix 306");
        assertFalse(regex.isValid("30945678901234"), "Prefix 3094");
        assertTrue(regex.isValid("30955678901234"), "Prefix 3095");
        assertFalse(regex.isValid("30965678901234"), "Prefix 3096");
        assertFalse(regex.isValid("35345678901234"), "Prefix 35");
        assertTrue(regex.isValid("36345678901234"), "Prefix 36");
        assertFalse(regex.isValid("37345678901234"), "Prefix 37");
        assertTrue(regex.isValid("38345678901234"), "Prefix 38");
        assertTrue(regex.isValid("39345678901234"), "Prefix 39");

        assertFalse(regex.isValid("3004567x901234"), "Invalid Char-A");
        assertFalse(regex.isValid("3634567x901234"), "Invalid Char-B");

        // *********** Test Validator **********
        assertTrue(regex.isValid(ERROR_DINERS), "Valid regex");
        assertFalse(validator.isValid(ERROR_DINERS), "Invalid");
        assertNull(validator.validate(ERROR_DINERS), "validate()");
        assertEquals(VALID_DINERS, validator.validate(VALID_DINERS));

        assertFalse(validator.isValid(VALID_AMEX), "Amex");
        assertTrue(validator.isValid(VALID_DINERS), "Diners");
        assertFalse(validator.isValid(VALID_DISCOVER), "Discover");
        assertFalse(validator.isValid(VALID_MASTERCARD), "Mastercard");
        assertFalse(validator.isValid(VALID_VISA), "Visa");
        assertFalse(validator.isValid(VALID_SHORT_VISA), "Visa Short");

        assertTrue(validator.isValid("30000000000004"), "Valid-A");
        assertTrue(validator.isValid("30123456789019"), "Valid-B");
        assertTrue(validator.isValid("36432685260294"), "Valid-C");

    }

    /**
     * Test the Discover Card option
     */
    @Test
    public void testDiscoverOption() {
        final CreditCardValidator validator = new CreditCardValidator(CreditCardValidator.DISCOVER);
        assertFalse(validator.isValid(ERROR_DISCOVER), "Invalid");
        assertFalse(validator.isValid(ERROR_DISCOVER65), "Invalid65");
        assertNull(validator.validate(ERROR_DISCOVER), "validate()");
        assertEquals(VALID_DISCOVER, validator.validate(VALID_DISCOVER));
        assertEquals(VALID_DISCOVER65, validator.validate(VALID_DISCOVER65));

        assertFalse(validator.isValid(VALID_AMEX), "Amex");
        assertFalse(validator.isValid(VALID_DINERS), "Diners");
        assertTrue(validator.isValid(VALID_DISCOVER), "Discover");
        assertTrue(validator.isValid(VALID_DISCOVER65), "Discover");
        assertFalse(validator.isValid(VALID_MASTERCARD), "Mastercard");
        assertFalse(validator.isValid(VALID_VISA), "Visa");
        assertFalse(validator.isValid(VALID_SHORT_VISA), "Visa Short");
    }

    /**
     * Test the Discover Card validator
     */
    @Test
    public void testDiscoverValidator() {

        final CodeValidator validator = CreditCardValidator.DISCOVER_VALIDATOR;
        final RegexValidator regex = validator.getRegexValidator();

        // ****** Test Regular Expression ******
        // length 16 and start with either "6011" or or "64[4-9]" or "65"
        assertFalse(regex.isValid("601156789012"), "Length 12-6011");
        assertFalse(regex.isValid("653456789012"), "Length 12-65");
        assertFalse(regex.isValid("6011567890123"), "Length 13-6011");
        assertFalse(regex.isValid("6534567890123"), "Length 13-65");
        assertFalse(regex.isValid("60115678901234"), "Length 14-6011");
        assertFalse(regex.isValid("65345678901234"), "Length 14-65");
        assertFalse(regex.isValid("601156789012345"), "Length 15-6011");
        assertFalse(regex.isValid("653456789012345"), "Length 15-65");
        assertTrue(regex.isValid("6011567890123456"), "Length 16-6011");
        assertTrue(regex.isValid("6444567890123456"), "Length 16-644");
        assertTrue(regex.isValid("6484567890123456"), "Length 16-648");
        assertTrue(regex.isValid("6534567890123456"), "Length 16-65");
        assertFalse(regex.isValid("65345678901234567"), "Length 17-65");
        assertFalse(regex.isValid("601156789012345678"), "Length 18-6011");
        assertFalse(regex.isValid("653456789012345678"), "Length 18-65");

        assertFalse(regex.isValid("6404567890123456"), "Prefix 640");
        assertFalse(regex.isValid("6414567890123456"), "Prefix 641");
        assertFalse(regex.isValid("6424567890123456"), "Prefix 642");
        assertFalse(regex.isValid("6434567890123456"), "Prefix 643");
        assertFalse(regex.isValid("6010567890123456"), "Prefix 6010");
        assertFalse(regex.isValid("6012567890123456"), "Prefix 6012");
        assertFalse(regex.isValid("6011567x90123456"), "Invalid Char");

        // *********** Test Validator **********
        assertTrue(regex.isValid(ERROR_DISCOVER), "Valid regex");
        assertTrue(regex.isValid(ERROR_DISCOVER65), "Valid regex65");
        assertFalse(validator.isValid(ERROR_DISCOVER), "Invalid");
        assertFalse(validator.isValid(ERROR_DISCOVER65), "Invalid65");
        assertNull(validator.validate(ERROR_DISCOVER), "validate()");
        assertEquals(VALID_DISCOVER, validator.validate(VALID_DISCOVER));
        assertEquals(VALID_DISCOVER65, validator.validate(VALID_DISCOVER65));

        assertFalse(validator.isValid(VALID_AMEX), "Amex");
        assertFalse(validator.isValid(VALID_DINERS), "Diners");
        assertTrue(validator.isValid(VALID_DISCOVER), "Discover");
        assertTrue(validator.isValid(VALID_DISCOVER65), "Discover");
        assertFalse(validator.isValid(VALID_MASTERCARD), "Mastercard");
        assertFalse(validator.isValid(VALID_VISA), "Visa");
        assertFalse(validator.isValid(VALID_SHORT_VISA), "Visa Short");

        assertTrue(validator.isValid("6011111111111117"), "Valid-A");
        assertTrue(validator.isValid("6011000000000004"), "Valid-B");
        assertTrue(validator.isValid("6011000000000012"), "Valid-C");

    }

    @Test
    public void testDisjointRange() {
        CreditCardValidator ccv = new CreditCardValidator(new CreditCardRange[] { new CreditCardRange("305", "4", new int[] { 13, 16 }), });
        assertEquals(13, VALID_SHORT_VISA.length());
        assertEquals(16, VALID_VISA.length());
        assertEquals(14, VALID_DINERS.length());
        assertTrue(ccv.isValid(VALID_SHORT_VISA));
        assertTrue(ccv.isValid(VALID_VISA));
        assertFalse(ccv.isValid(ERROR_SHORT_VISA));
        assertFalse(ccv.isValid(ERROR_VISA));
        assertFalse(ccv.isValid(VALID_DINERS));
        ccv = new CreditCardValidator(new CreditCardRange[] {
                // add 14 as a valid length
                new CreditCardRange("305", "4", new int[] { 13, 14, 16 }), });
        assertTrue(ccv.isValid(VALID_DINERS));
    }

    @Test
    public void testGeneric() {
        final CreditCardValidator ccv = CreditCardValidator.genericCreditCardValidator();
        for (final String s : VALID_CARDS) {
            assertTrue(ccv.isValid(s), s);
        }
        for (final String s : ERROR_CARDS) {
            assertFalse(ccv.isValid(s), s);
        }
    }

    @Test
    public void testIsValid() {
        CreditCardValidator ccv = new CreditCardValidator();

        assertNull(ccv.validate(null));

        assertFalse(ccv.isValid(null));
        assertFalse(ccv.isValid(""));
        assertFalse(ccv.isValid("123456789012")); // too short
        assertFalse(ccv.isValid("12345678901234567890")); // too long
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

    /**
     * Test the Mastercard Card option
     */
    @Test
    public void testMastercardOption() {
        final CreditCardValidator validator = new CreditCardValidator(CreditCardValidator.MASTERCARD);
        assertFalse(validator.isValid(ERROR_MASTERCARD), "Invalid");
        assertNull(validator.validate(ERROR_MASTERCARD), "validate()");
        assertEquals(VALID_MASTERCARD, validator.validate(VALID_MASTERCARD));

        assertFalse(validator.isValid(VALID_AMEX), "Amex");
        assertFalse(validator.isValid(VALID_DINERS), "Diners");
        assertFalse(validator.isValid(VALID_DISCOVER), "Discover");
        assertTrue(validator.isValid(VALID_MASTERCARD), "Mastercard");
        assertFalse(validator.isValid(VALID_VISA), "Visa");
        assertFalse(validator.isValid(VALID_SHORT_VISA), "Visa Short");
    }

    /**
     * Test using separators
     */
    @Test
    public void testMastercardUsingSeparators() {

        final String masterCardRegExSep = "^(5[1-5]\\d{2})(?:[- ])?(\\d{4})(?:[- ])?(\\d{4})(?:[- ])?(\\d{4})$";
        final CodeValidator validator = new CodeValidator(masterCardRegExSep, LuhnCheckDigit.LUHN_CHECK_DIGIT);
        final RegexValidator regex = validator.getRegexValidator();

        // ****** Test Regular Expression ******
        // length 16 and start with a "51-55"
        assertEquals("5134567890123456", regex.validate("5134567890123456"), "Number");
        assertEquals("5134567890123456", regex.validate("5134-5678-9012-3456"), "Hyphen");
        assertEquals("5134567890123456", regex.validate("5134 5678 9012 3456"), "Space");
        assertEquals("5134567890123456", regex.validate("5134-5678 9012-3456"), "MixedA");
        assertEquals("5134567890123456", regex.validate("5134 5678-9012 3456"), "MixedB");

        assertFalse(regex.isValid("5134.5678.9012.3456"), "Invalid Separator A");
        assertFalse(regex.isValid("5134_5678_9012_3456"), "Invalid Separator B");
        assertFalse(regex.isValid("513-45678-9012-3456"), "Invalid Grouping A");
        assertFalse(regex.isValid("5134-567-89012-3456"), "Invalid Grouping B");
        assertFalse(regex.isValid("5134-5678-901-23456"), "Invalid Grouping C");

        // *********** Test Validator **********
        assertEquals("5500000000000004", validator.validate("5500-0000-0000-0004"), "Valid-A");
        assertEquals("5424000000000015", validator.validate("5424 0000 0000 0015"), "Valid-B");
        assertEquals("5301250070000191", validator.validate("5301-250070000191"), "Valid-C");
        assertEquals("5123456789012346", validator.validate("5123456789012346"), "Valid-D");
    }

    /**
     * Test the Mastercard Card validator
     */
    @Test
    public void testMastercardValidator() {

        final CodeValidator validator = CreditCardValidator.MASTERCARD_VALIDATOR;
        final RegexValidator regex = validator.getRegexValidator();

        // ****** Test Regular Expression ******
        // length 16 and start with a "51-55"
        assertFalse(regex.isValid("513456789012"), "Length 12");
        assertFalse(regex.isValid("5134567890123"), "Length 13");
        assertFalse(regex.isValid("51345678901234"), "Length 14");
        assertFalse(regex.isValid("513456789012345"), "Length 15");
        assertTrue(regex.isValid("5134567890123456"), "Length 16");
        assertFalse(regex.isValid("51345678901234567"), "Length 17");
        assertFalse(regex.isValid("513456789012345678"), "Length 18");
        assertFalse(regex.isValid("4134567890123456"), "Prefix 41");
        assertFalse(regex.isValid("5034567890123456"), "Prefix 50");
        assertTrue(regex.isValid("5134567890123456"), "Prefix 51");
        assertTrue(regex.isValid("5234567890123456"), "Prefix 52");
        assertTrue(regex.isValid("5334567890123456"), "Prefix 53");
        assertTrue(regex.isValid("5434567890123456"), "Prefix 54");
        assertTrue(regex.isValid("5534567890123456"), "Prefix 55");
        assertFalse(regex.isValid("5634567890123456"), "Prefix 56");
        assertFalse(regex.isValid("6134567890123456"), "Prefix 61");
        assertFalse(regex.isValid("5134567x90123456"), "Invalid Char");

        // *********** Test Validator **********
        assertTrue(regex.isValid(ERROR_MASTERCARD), "Valid regex");
        assertFalse(validator.isValid(ERROR_MASTERCARD), "Invalid");
        assertNull(validator.validate(ERROR_MASTERCARD), "validate()");
        assertEquals(VALID_MASTERCARD, validator.validate(VALID_MASTERCARD));

        assertFalse(validator.isValid(VALID_AMEX), "Amex");
        assertFalse(validator.isValid(VALID_DINERS), "Diners");
        assertFalse(validator.isValid(VALID_DISCOVER), "Discover");
        assertTrue(validator.isValid(VALID_MASTERCARD), "Mastercard");
        assertFalse(validator.isValid(VALID_VISA), "Visa");
        assertFalse(validator.isValid(VALID_SHORT_VISA), "Visa Short");

        assertTrue(validator.isValid("5500000000000004"), "Valid-A");
        assertTrue(validator.isValid("5424000000000015"), "Valid-B");
        assertTrue(validator.isValid("5301250070000191"), "Valid-C");
        assertTrue(validator.isValid("5123456789012346"), "Valid-D");
        assertTrue(validator.isValid("5555555555554444"), "Valid-E");

        final RegexValidator rev = validator.getRegexValidator();
        final String pad = "0000000000";
        assertFalse(rev.isValid("222099" + pad), "222099");
        for (int i = 222100; i <= 272099; i++) {
            final String j = Integer.toString(i) + pad;
            assertTrue(rev.isValid(j), j);
        }
        assertFalse(rev.isValid("272100" + pad), "272100");
    }

    @Test
    public void testRangeGenerator() {
        final CreditCardValidator ccv = new CreditCardValidator(
                new CodeValidator[] { CreditCardValidator.AMEX_VALIDATOR, CreditCardValidator.VISA_VALIDATOR, CreditCardValidator.MASTERCARD_VALIDATOR,
                        CreditCardValidator.DISCOVER_VALIDATOR, },
                // Add missing validator
                new CreditCardRange[] { new CreditCardRange("300", "305", 14, 14), // Diners
                        new CreditCardRange("3095", null, 14, 14), // Diners
                        new CreditCardRange("36", null, 14, 14), // Diners
                        new CreditCardRange("38", "39", 14, 14), // Diners
                }
        // we don't have any VPAY examples yet that aren't handled by VISA
        );
        for (final String s : VALID_CARDS) {
            assertTrue(ccv.isValid(s), s);
        }
        for (final String s : ERROR_CARDS) {
            assertFalse(ccv.isValid(s), s);
        }
    }

    @Test
    public void testRangeGeneratorNoLuhn() {
        final CodeValidator cv = CreditCardValidator
                .createRangeValidator(new CreditCardRange[] { new CreditCardRange("1", null, 6, 7), new CreditCardRange("644", "65", 8, 8) }, null);
        assertTrue(cv.isValid("1990000"));
        assertTrue(cv.isValid("199000"));
        assertFalse(cv.isValid("000000"));
        assertFalse(cv.isValid("099999"));
        assertFalse(cv.isValid("200000"));

        assertFalse(cv.isValid("64399999"));
        assertTrue(cv.isValid("64400000"));
        assertTrue(cv.isValid("64900000"));
        assertTrue(cv.isValid("65000000"));
        assertTrue(cv.isValid("65999999"));
        assertFalse(cv.isValid("66000000"));
    }

    @Test
    public void testValidLength() {
        assertTrue(CreditCardValidator.validLength(14, new CreditCardRange("", "", 14, 14)));
        assertFalse(CreditCardValidator.validLength(15, new CreditCardRange("", "", 14, 14)));
        assertFalse(CreditCardValidator.validLength(13, new CreditCardRange("", "", 14, 14)));

        assertFalse(CreditCardValidator.validLength(14, new CreditCardRange("", "", 15, 17)));
        assertTrue(CreditCardValidator.validLength(15, new CreditCardRange("", "", 15, 17)));
        assertTrue(CreditCardValidator.validLength(16, new CreditCardRange("", "", 15, 17)));
        assertTrue(CreditCardValidator.validLength(17, new CreditCardRange("", "", 15, 17)));
        assertFalse(CreditCardValidator.validLength(18, new CreditCardRange("", "", 15, 17)));

        assertFalse(CreditCardValidator.validLength(14, new CreditCardRange("", "", new int[] { 15, 17 })));
        assertTrue(CreditCardValidator.validLength(15, new CreditCardRange("", "", new int[] { 15, 17 })));
        assertFalse(CreditCardValidator.validLength(16, new CreditCardRange("", "", new int[] { 15, 17 })));
        assertTrue(CreditCardValidator.validLength(17, new CreditCardRange("", "", new int[] { 15, 17 })));
        assertFalse(CreditCardValidator.validLength(18, new CreditCardRange("", "", new int[] { 15, 17 })));
    }

    /**
     * Test the Visa Card option
     */
    @Test
    public void testVisaOption() {
        final CreditCardValidator validator = new CreditCardValidator(CreditCardValidator.VISA);
        assertFalse(validator.isValid(ERROR_VISA), "Invalid");
        assertFalse(validator.isValid(ERROR_SHORT_VISA), "Invalid-S");
        assertNull(validator.validate(ERROR_VISA), "validate()");
        assertEquals(VALID_VISA, validator.validate(VALID_VISA));
        assertEquals(VALID_SHORT_VISA, validator.validate(VALID_SHORT_VISA));

        assertFalse(validator.isValid(VALID_AMEX), "Amex");
        assertFalse(validator.isValid(VALID_DINERS), "Diners");
        assertFalse(validator.isValid(VALID_DISCOVER), "Discover");
        assertFalse(validator.isValid(VALID_MASTERCARD), "Mastercard");
        assertTrue(validator.isValid(VALID_VISA), "Visa");
        assertTrue(validator.isValid(VALID_SHORT_VISA), "Visa Short");
    }

    /**
     * Test the Visa Card validator
     */
    @Test
    public void testVisaValidator() {

        final CodeValidator validator = CreditCardValidator.VISA_VALIDATOR;
        final RegexValidator regex = validator.getRegexValidator();

        // ****** Test Regular Expression ******
        // length 13 or 16, must start with a "4"
        assertFalse(regex.isValid("423456789012"), "Length 12");
        assertTrue(regex.isValid("4234567890123"), "Length 13");
        assertFalse(regex.isValid("42345678901234"), "Length 14");
        assertFalse(regex.isValid("423456789012345"), "Length 15");
        assertTrue(regex.isValid("4234567890123456"), "Length 16");
        assertFalse(regex.isValid("42345678901234567"), "Length 17");
        assertFalse(regex.isValid("423456789012345678"), "Length 18");
        assertFalse(regex.isValid("3234567890123"), "Invalid Pref-A");
        assertFalse(regex.isValid("3234567890123456"), "Invalid Pref-B");
        assertFalse(regex.isValid("4234567x90123"), "Invalid Char-A");
        assertFalse(regex.isValid("4234567x90123456"), "Invalid Char-B");

        // *********** Test Validator **********
        assertTrue(regex.isValid(ERROR_VISA), "Valid regex");
        assertTrue(regex.isValid(ERROR_SHORT_VISA), "Valid regex-S");
        assertFalse(validator.isValid(ERROR_VISA), "Invalid");
        assertFalse(validator.isValid(ERROR_SHORT_VISA), "Invalid-S");
        assertNull(validator.validate(ERROR_VISA), "validate()");
        assertEquals(VALID_VISA, validator.validate(VALID_VISA));
        assertEquals(VALID_SHORT_VISA, validator.validate(VALID_SHORT_VISA));

        assertFalse(validator.isValid(VALID_AMEX), "Amex");
        assertFalse(validator.isValid(VALID_DINERS), "Diners");
        assertFalse(validator.isValid(VALID_DISCOVER), "Discover");
        assertFalse(validator.isValid(VALID_MASTERCARD), "Mastercard");
        assertTrue(validator.isValid(VALID_VISA), "Visa");
        assertTrue(validator.isValid(VALID_SHORT_VISA), "Visa Short");

        assertTrue(validator.isValid("4111111111111111"), "Valid-A");
        assertTrue(validator.isValid("4543059999999982"), "Valid-C");
        assertTrue(validator.isValid("4462000000000003"), "Valid-B");
        assertTrue(validator.isValid("4508750000000009"), "Valid-D"); // Electron
        assertTrue(validator.isValid("4012888888881881"), "Valid-E");
    }

    @Test
    public void testVPayOption() {
        final CreditCardValidator validator = new CreditCardValidator(CreditCardValidator.VPAY);
        assertTrue(validator.isValid(VALID_VPAY), "Valid");
        assertTrue(validator.isValid(VALID_VPAY2), "Valid");
        assertFalse(validator.isValid(ERROR_VPAY), "Invalid");
        assertEquals(VALID_VPAY, validator.validate(VALID_VPAY));
        assertEquals(VALID_VPAY2, validator.validate(VALID_VPAY2));

        assertFalse(validator.isValid(VALID_AMEX), "Amex");
        assertFalse(validator.isValid(VALID_DINERS), "Diners");
        assertFalse(validator.isValid(VALID_DISCOVER), "Discover");
        assertFalse(validator.isValid(VALID_MASTERCARD), "Mastercard");
        assertTrue(validator.isValid(VALID_VISA), "Visa");
        assertTrue(validator.isValid(VALID_SHORT_VISA), "Visa Short");
    }
}
