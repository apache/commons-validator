/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;
import org.junit.jupiter.api.Test;

/**
 * CodeValidatorTest.java.
 */
class CodeValidatorTest {

    /**
     * Test Check Digit.
     */
    @Test
    void testCheckDigit() {
        CodeValidator validator = new CodeValidator((String) null, -1, -1, (CheckDigit) null);
        final String invalidEAN = "9781930110992";
        final String validEAN = "9781930110991";

        // Test no CheckDigit (i.e. null)
        assertNull(validator.getCheckDigit(), "No CheckDigit");
        assertEquals(invalidEAN, validator.validate(invalidEAN), "No CheckDigit invalid");
        assertEquals(validEAN, validator.validate(validEAN), "No CheckDigit valid");
        assertTrue(validator.isValid(invalidEAN), "No CheckDigit (is) invalid");
        assertTrue(validator.isValid(validEAN), "No CheckDigit (is) valid");

        // Use the EAN-13 check digit routine
        validator = new CodeValidator((String) null, -1, EAN13CheckDigit.EAN13_CHECK_DIGIT);

        assertNotNull(validator.getCheckDigit(), "EAN CheckDigit");
        assertNull(validator.validate(invalidEAN), "EAN CheckDigit invalid");
        assertEquals(validEAN, validator.validate(validEAN), "EAN CheckDigit valid");
        assertFalse(validator.isValid(invalidEAN), "EAN CheckDigit (is) invalid");
        assertTrue(validator.isValid(validEAN), "EAN CheckDigit (is) valid");
        assertNull(validator.validate("978193011099X"), "EAN CheckDigit ex");
    }

    /**
     * Test Regular Expression.
     */
    @Test
    void testConstructors() {
        CodeValidator validator;
        final RegexValidator regex = new RegexValidator("^[0-9]*$");

        // Constructor 1
        validator = new CodeValidator(regex, EAN13CheckDigit.EAN13_CHECK_DIGIT);
        assertEquals(regex, validator.getRegexValidator(), "Constructor 1 - regex");
        assertEquals(-1, validator.getMinLength(), "Constructor 1 - min length");
        assertEquals(-1, validator.getMaxLength(), "Constructor 1 - max length");
        assertEquals(EAN13CheckDigit.EAN13_CHECK_DIGIT, validator.getCheckDigit(), "Constructor 1 - check digit");

        // Constructor 2
        validator = new CodeValidator(regex, 13, EAN13CheckDigit.EAN13_CHECK_DIGIT);
        assertEquals(regex, validator.getRegexValidator(), "Constructor 2 - regex");
        assertEquals(13, validator.getMinLength(), "Constructor 2 - min length");
        assertEquals(13, validator.getMaxLength(), "Constructor 2 - max length");
        assertEquals(EAN13CheckDigit.EAN13_CHECK_DIGIT, validator.getCheckDigit(), "Constructor 2 - check digit");

        // Constructor 3
        validator = new CodeValidator(regex, 10, 20, EAN13CheckDigit.EAN13_CHECK_DIGIT);
        assertEquals(regex, validator.getRegexValidator(), "Constructor 3 - regex");
        assertEquals(10, validator.getMinLength(), "Constructor 3 - min length");
        assertEquals(20, validator.getMaxLength(), "Constructor 3 - max length");
        assertEquals(EAN13CheckDigit.EAN13_CHECK_DIGIT, validator.getCheckDigit(), "Constructor 3 - check digit");

        // Constructor 4
        validator = new CodeValidator("^[0-9]*$", EAN13CheckDigit.EAN13_CHECK_DIGIT);
        assertEquals(validator.getRegexValidator().toString(), "RegexValidator{^[0-9]*$}", "Constructor 4 - regex");
        assertEquals(-1, validator.getMinLength(), "Constructor 4 - min length");
        assertEquals(-1, validator.getMaxLength(), "Constructor 4 - max length");
        assertEquals(EAN13CheckDigit.EAN13_CHECK_DIGIT, validator.getCheckDigit(), "Constructor 4 - check digit");

        // Constructor 5
        validator = new CodeValidator("^[0-9]*$", 13, EAN13CheckDigit.EAN13_CHECK_DIGIT);
        assertEquals(validator.getRegexValidator().toString(), "RegexValidator{^[0-9]*$}", "Constructor 5 - regex");
        assertEquals(13, validator.getMinLength(), "Constructor 5 - min length");
        assertEquals(13, validator.getMaxLength(), "Constructor 5 - max length");
        assertEquals(EAN13CheckDigit.EAN13_CHECK_DIGIT, validator.getCheckDigit(), "Constructor 5 - check digit");

        // Constructor 6
        validator = new CodeValidator("^[0-9]*$", 10, 20, EAN13CheckDigit.EAN13_CHECK_DIGIT);
        assertEquals(validator.getRegexValidator().toString(), "RegexValidator{^[0-9]*$}", "Constructor 6 - regex");
        assertEquals(10, validator.getMinLength(), "Constructor 6 - min length");
        assertEquals(20, validator.getMaxLength(), "Constructor 6 - max length");
        assertEquals(EAN13CheckDigit.EAN13_CHECK_DIGIT, validator.getCheckDigit(), "Constructor 6 - check digit");
    }

    /**
     * Test the minimum/maximum length
     */
    @Test
    void testLength() {
        CodeValidator validator = new CodeValidator((String) null, -1, -1, (CheckDigit) null);
        final String length10 = "1234567890";
        final String length11 = "12345678901";
        final String length12 = "123456789012";
        final String length20 = "12345678901234567890";
        final String length21 = "123456789012345678901";
        final String length22 = "1234567890123456789012";

        assertEquals(-1, validator.getMinLength(), "No min");
        assertEquals(-1, validator.getMaxLength(), "No max");

        assertEquals(length10, validator.validate(length10), "No Length 10");
        assertEquals(length11, validator.validate(length11), "No Length 11");
        assertEquals(length12, validator.validate(length12), "No Length 12");
        assertEquals(length20, validator.validate(length20), "No Length 20");
        assertEquals(length21, validator.validate(length21), "No Length 21");
        assertEquals(length22, validator.validate(length22), "No Length 22");

        validator = new CodeValidator((String) null, 11, -1, (CheckDigit) null);
        assertEquals(11, validator.getMinLength(), "Min 11 - min");
        assertEquals(-1, validator.getMaxLength(), "Min 11 - max");
        assertNull(validator.validate(length10), "Min 11 - 10");
        assertEquals(length11, validator.validate(length11), "Min 11 - 11");
        assertEquals(length12, validator.validate(length12), "Min 11 - 12");
        assertEquals(length20, validator.validate(length20), "Min 11 - 20");
        assertEquals(length21, validator.validate(length21), "Min 11 - 21");
        assertEquals(length22, validator.validate(length22), "Min 11 - 22");

        validator = new CodeValidator((String) null, -1, 21, (CheckDigit) null);
        assertEquals(-1, validator.getMinLength(), "Max 21 - min");
        assertEquals(21, validator.getMaxLength(), "Max 21 - max");
        assertEquals(length10, validator.validate(length10), "Max 21 - 10");
        assertEquals(length11, validator.validate(length11), "Max 21 - 11");
        assertEquals(length12, validator.validate(length12), "Max 21 - 12");
        assertEquals(length20, validator.validate(length20), "Max 21 - 20");
        assertEquals(length21, validator.validate(length21), "Max 21 - 21");
        assertNull(validator.validate(length22), "Max 21 - 22");

        validator = new CodeValidator((String) null, 11, 21, (CheckDigit) null);
        assertEquals(11, validator.getMinLength(), "Min 11 / Max 21 - min");
        assertEquals(21, validator.getMaxLength(), "Min 11 / Max 21 - max");
        assertNull(validator.validate(length10), "Min 11 / Max 21 - 10");
        assertEquals(length11, validator.validate(length11), "Min 11 / Max 21 - 11");
        assertEquals(length12, validator.validate(length12), "Min 11 / Max 21 - 12");
        assertEquals(length20, validator.validate(length20), "Min 11 / Max 21 - 20");
        assertEquals(length21, validator.validate(length21), "Min 11 / Max 21 - 21");
        assertNull(validator.validate(length22), "Min 11 / Max 21 - 22");

        validator = new CodeValidator((String) null, 11, 11, (CheckDigit) null);
        assertEquals(11, validator.getMinLength(), "Exact 11 - min");
        assertEquals(11, validator.getMaxLength(), "Exact 11 - max");
        assertNull(validator.validate(length10), "Exact 11 - 10");
        assertEquals(length11, validator.validate(length11), "Exact 11 - 11");
        assertNull(validator.validate(length12), "Exact 11 - 12");
    }

    /**
     * Test Regular Expression.
     */
    @Test
    void testNoInput() {
        final CodeValidator validator = new CodeValidator((String) null, -1, -1, (CheckDigit) null);
        assertNull(validator.validate(null), "Null");
        assertNull(validator.validate(""), "Zero Length");
        assertNull(validator.validate("   "), "Spaces");
        assertEquals(validator.validate(" A  "), "A", "Trimmed");
    }

    /**
     * Test Regular Expression.
     */
    @Test
    void testRegex() {
        CodeValidator validator = new CodeValidator((String) null, -1, -1, (CheckDigit) null);

        final String value2 = "12";
        final String value3 = "123";
        final String value4 = "1234";
        final String value5 = "12345";
        final String invalid = "12a4";

        // No Regular Expression
        assertNull(validator.getRegexValidator(), "No Regex");
        assertEquals(value2, validator.validate(value2), "No Regex 2");
        assertEquals(value3, validator.validate(value3), "No Regex 3");
        assertEquals(value4, validator.validate(value4), "No Regex 4");
        assertEquals(value5, validator.validate(value5), "No Regex 5");
        assertEquals(invalid, validator.validate(invalid), "No Regex invalid");

        // Regular Expression
        String regex = "^([0-9]{3,4})$";
        validator = new CodeValidator(regex, -1, -1, (CheckDigit) null);
        assertNotNull(validator.getRegexValidator(), "No Regex");
        assertNull(validator.validate(value2), "Regex 2");
        assertEquals(value3, validator.validate(value3), "Regex 3");
        assertEquals(value4, validator.validate(value4), "Regex 4");
        assertNull(validator.validate(value5), "Regex 5");
        assertNull(validator.validate(invalid), "Regex invalid");

        // Reformatted
        regex = "^([0-9]{3})(?:[-\\s])([0-9]{3})$";
        validator = new CodeValidator(new RegexValidator(regex), 6, (CheckDigit) null);
        assertEquals(validator.validate("123-456"), "123456", "Reformat 123-456");
        assertEquals(validator.validate("123 456"), "123456", "Reformat 123 456");
        assertNull(validator.validate("123456"), "Reformat 123456");
        assertNull(validator.validate("123.456"), "Reformat 123.456");

        regex = "^(?:([0-9]{3})(?:[-\\s])([0-9]{3}))|([0-9]{6})$";
        validator = new CodeValidator(new RegexValidator(regex), 6, (CheckDigit) null);
        assertEquals(validator.getRegexValidator().toString(), "RegexValidator{" + regex + "}", "Reformat 2 Regex");
        assertEquals(validator.validate("123-456"), "123456", "Reformat 2 123-456");
        assertEquals(validator.validate("123 456"), "123456", "Reformat 2 123 456");
        assertEquals(validator.validate("123456"), "123456", "Reformat 2 123456");

    }

    @Test
    void testValidator294Part1() {
        CodeValidator validator = new CodeValidator((String) null, 0, -1, (CheckDigit) null);
        assertNull(validator.validate(null), "Null");
        validator = new CodeValidator((String) null, -1, 0, (CheckDigit) null);
        assertNull(validator.validate(null), "Null");
    }

    @Test
    void testValidator294Part2() {
        final CodeValidator validator = new CodeValidator((String) null, -1, 0, (CheckDigit) null);
        assertNull(validator.validate(null), "Null");
    }

}
