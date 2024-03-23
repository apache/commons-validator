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

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link ISBNValidator}.
 */
public class ISBNValidatorTest {

    private final String[] validISBN10Format = { "1234567890", "123456789X", "12345-1234567-123456-X", "12345 1234567 123456 X", "1-2-3-4", "1 2 3 4", };

    private final String[] invalidISBN10Format = { "", // empty
            "   ", // empty
            "1", // too short
            "123456789", // too short
            "12345678901", // too long
            "12345678X0", // X not at end
            "123456-1234567-123456-X", // Group too long
            "12345-12345678-123456-X", // Publisher too long
            "12345-1234567-1234567-X", // Title too long
            "12345-1234567-123456-X2", // Check Digit too long
            "--1 930110 99 5", // format
            "1 930110 99 5--", // format
            "1 930110-99 5-", // format
            "1.2.3.4", // Invalid Separator
            "1=2=3=4", // Invalid Separator
            "1_2_3_4", // Invalid Separator
            "123456789Y", // Other character at the end
            "dsasdsadsa", // invalid characters
            "I love sparrows!", // invalid characters
            "068-556-98-45" // format
    };

    private final String[] validISBN13Format = { "9781234567890", "9791234567890", "978-12345-1234567-123456-1", "979-12345-1234567-123456-1",
            "978 12345 1234567 123456 1", "979 12345 1234567 123456 1", "978-1-2-3-4", "979-1-2-3-4", "978 1 2 3 4", "979 1 2 3 4", };

    private final String[] invalidISBN13Format = { "", // empty
            "   ", // empty
            "1", // too short
            "978123456789", // too short
            "97812345678901", // too long
            "978-123456-1234567-123456-1", // Group too long
            "978-12345-12345678-123456-1", // Publisher too long
            "978-12345-1234567-1234567-1", // Title too long
            "978-12345-1234567-123456-12", // Check Digit too long
            "--978 1 930110 99 1", // format
            "978 1 930110 99 1--", // format
            "978 1 930110-99 1-", // format
            "123-4-567890-12-8", // format
            "978.1.2.3.4", // Invalid Separator
            "978=1=2=3=4", // Invalid Separator
            "978_1_2_3_4", // Invalid Separator
            "978123456789X", // invalid character
            "978-0-201-63385-X", // invalid character
            "dsasdsadsadsa", // invalid characters
            "I love sparrows!", // invalid characters
            "979-1-234-567-89-6" // format
    };

    /**
     * Test method for {@link org.apache.commons.validator.routines.ISBNValidator#convertToISBN13(java.lang.String)}.
     */
    @Test
    public void testConversionErrors() {
        final ISBNValidator validator = ISBNValidator.getInstance();
        String input = null;
        try {
            input = "123456789 ";
            validator.convertToISBN13(input);
            fail("Expected IllegalArgumentException for '" + input + "'");
        } catch (final IllegalArgumentException e) {
            // expected result
        }
        try {
            input = "12345678901";
            validator.convertToISBN13(input);
            fail("Expected IllegalArgumentException for '" + input + "'");
        } catch (final IllegalArgumentException e) {
            // expected result
        }
        try {
            input = "";
            validator.convertToISBN13(input);
            fail("Expected IllegalArgumentException for '" + input + "'");
        } catch (final IllegalArgumentException e) {
            // expected result
        }
        try {
            input = "X234567890";
            validator.convertToISBN13(input);
            fail("Expected IllegalArgumentException for '" + input + "'");
        } catch (final IllegalArgumentException e) {
            // expected result
        }
    }

    /**
     * Test Invalid ISBN-10 codes
     */
    @Test
    public void testInvalid() {
        final ISBNValidator validator = ISBNValidator.getInstance();
        String baseCode = "193011099";
        assertFalse(validator.isValid(baseCode + "0"), "ISBN10-0");
        assertFalse(validator.isValid(baseCode + "1"), "ISBN10-1");
        assertFalse(validator.isValid(baseCode + "2"), "ISBN10-2");
        assertFalse(validator.isValid(baseCode + "3"), "ISBN10-3");
        assertFalse(validator.isValid(baseCode + "4"), "ISBN10-4");
        assertTrue(validator.isValid(baseCode + "5"), "ISBN10-5"); // valid check digit
        assertFalse(validator.isValid(baseCode + "6"), "ISBN10-6");
        assertFalse(validator.isValid(baseCode + "7"), "ISBN10-7");
        assertFalse(validator.isValid(baseCode + "8"), "ISBN10-8");
        assertFalse(validator.isValid(baseCode + "9"), "ISBN10-9");
        assertFalse(validator.isValid(baseCode + "X"), "ISBN10-X");

        baseCode = "978193011099";
        assertFalse(validator.isValid(baseCode + "0"), "ISBN13-0");
        assertTrue(validator.isValid(baseCode + "1"), "ISBN13-1"); // valid check digit
        assertFalse(validator.isValid(baseCode + "2"), "ISBN13-2");
        assertFalse(validator.isValid(baseCode + "3"), "ISBN13-3");
        assertFalse(validator.isValid(baseCode + "4"), "ISBN13-4");
        assertFalse(validator.isValid(baseCode + "5"), "ISBN13-5");
        assertFalse(validator.isValid(baseCode + "6"), "ISBN13-6");
        assertFalse(validator.isValid(baseCode + "7"), "ISBN13-7");
        assertFalse(validator.isValid(baseCode + "8"), "ISBN13-8");
        assertFalse(validator.isValid(baseCode + "9"), "ISBN13-9");
    }

    /**
     * Test Invalid ISBN-10 formats.
     */
    @Test
    public void testInvalidISBN10Format() {
        final ISBNValidator validator = ISBNValidator.getInstance();
        final Pattern pattern = Pattern.compile(ISBNValidator.ISBN10_REGEX);
        for (int i = 0; i < invalidISBN10Format.length; i++) {
            assertFalse(pattern.matcher(invalidISBN10Format[i]).matches(), "Pattern[" + i + "]=" + invalidISBN10Format[i]);
            assertFalse(validator.isValidISBN10(invalidISBN10Format[i]), "isValidISBN10[" + i + "]=" + invalidISBN10Format[i]);
            assertNull(validator.validateISBN10(invalidISBN10Format[i]), "validateISBN10[" + i + "]=" + invalidISBN10Format[i]);
        }
    }

    /**
     * Test Invalid ISBN-13 formats.
     */
    @Test
    public void testInvalidISBN13Format() {
        final Pattern pattern = Pattern.compile(ISBNValidator.ISBN13_REGEX);
        final ISBNValidator validator = ISBNValidator.getInstance();
        for (int i = 0; i < invalidISBN13Format.length; i++) {
            assertFalse(pattern.matcher(invalidISBN13Format[i]).matches(), "Pattern[" + i + "]=" + invalidISBN13Format[i]);
            assertFalse(validator.isValidISBN13(invalidISBN13Format[i]), "isValidISBN13[" + i + "]=" + invalidISBN13Format[i]);
            assertNull(validator.validateISBN13(invalidISBN13Format[i]), "validateISBN13[" + i + "]=" + invalidISBN13Format[i]);
        }
    }

    /**
     * Test isValid() ISBN-10 codes
     */
    @Test
    public void testIsValidISBN10() {
        final ISBNValidator validator = ISBNValidator.getInstance();
        assertTrue(validator.isValidISBN10("1930110995"), "isValidISBN10-1");
        assertTrue(validator.isValidISBN10("1-930110-99-5"), "isValidISBN10-2");
        assertTrue(validator.isValidISBN10("1 930110 99 5"), "isValidISBN10-3");
        assertTrue(validator.isValidISBN10("020163385X"), "isValidISBN10-4");
        assertTrue(validator.isValidISBN10("0-201-63385-X"), "isValidISBN10-5");
        assertTrue(validator.isValidISBN10("0 201 63385 X"), "isValidISBN10-6");

        assertTrue(validator.isValid("1930110995"), "isValid-1");
        assertTrue(validator.isValid("1-930110-99-5"), "isValid-2");
        assertTrue(validator.isValid("1 930110 99 5"), "isValid-3");
        assertTrue(validator.isValid("020163385X"), "isValid-4");
        assertTrue(validator.isValid("0-201-63385-X"), "isValid-5");
        assertTrue(validator.isValid("0 201 63385 X"), "isValid-6");
    }

    /**
     * Test isValid() ISBN-13 codes
     */
    @Test
    public void testIsValidISBN13() {
        final ISBNValidator validator = ISBNValidator.getInstance();
        assertTrue(validator.isValidISBN13("9781930110991"), "isValidISBN13-1");
        assertTrue(validator.isValidISBN13("978-1-930110-99-1"), "isValidISBN13-2");
        assertTrue(validator.isValidISBN13("978 1 930110 99 1"), "isValidISBN13-3");
        assertTrue(validator.isValidISBN13("9780201633856"), "isValidISBN13-4");
        assertTrue(validator.isValidISBN13("978-0-201-63385-6"), "isValidISBN13-5");
        assertTrue(validator.isValidISBN13("978 0 201 63385 6"), "isValidISBN13-6");

        assertTrue(validator.isValid("9781930110991"), "isValid-1");
        assertTrue(validator.isValid("978-1-930110-99-1"), "isValid-2");
        assertTrue(validator.isValid("978 1 930110 99 1"), "isValid-3");
        assertTrue(validator.isValid("9780201633856"), "isValid-4");
        assertTrue(validator.isValid("978-0-201-63385-6"), "isValid-5");
        assertTrue(validator.isValid("978 0 201 63385 6"), "isValid-6");
    }

    /**
     * Test null values
     */
    @Test
    public void testNull() {
        final ISBNValidator validator = ISBNValidator.getInstance();
        assertFalse(validator.isValid(null), "isValid");
        assertFalse(validator.isValidISBN10(null), "isValidISBN10");
        assertFalse(validator.isValidISBN13(null), "isValidISBN13");
        assertNull(validator.validate(null), "validate");
        assertNull(validator.validateISBN10(null), "validateISBN10");
        assertNull(validator.validateISBN13(null), "validateISBN13");
        assertNull(validator.convertToISBN13(null), "convertToISBN13");
    }

    /**
     * Test validate() ISBN-10 codes (don't convert)
     */
    @Test
    public void testValidateISBN10() {
        final ISBNValidator validator = ISBNValidator.getInstance(false);
        assertEquals(validator.validateISBN10("1930110995"), "1930110995", "validateISBN10-1");
        assertEquals(validator.validateISBN10("1-930110-99-5"), "1930110995", "validateISBN10-2");
        assertEquals(validator.validateISBN10("1 930110 99 5"), "1930110995", "validateISBN10-3");
        assertEquals(validator.validateISBN10("020163385X"), "020163385X", "validateISBN10-4");
        assertEquals(validator.validateISBN10("0-201-63385-X"), "020163385X", "validateISBN10-5");
        assertEquals(validator.validateISBN10("0 201 63385 X"), "020163385X", "validateISBN10-6");

        assertEquals(validator.validate("1930110995"), "1930110995", "validate-1");
        assertEquals(validator.validate("1-930110-99-5"), "1930110995", "validate-2");
        assertEquals(validator.validate("1 930110 99 5"), "1930110995", "validate-3");
        assertEquals(validator.validate("020163385X"), "020163385X", "validate-4");
        assertEquals(validator.validate("0-201-63385-X"), "020163385X", "validate-5");
        assertEquals(validator.validate("0 201 63385 X"), "020163385X", "validate-6");
    }

    /**
     * Test validate() ISBN-10 codes (convert)
     */
    @Test
    public void testValidateISBN10Convert() {
        final ISBNValidator validator = ISBNValidator.getInstance();
        assertEquals(validator.validate("1930110995"), "9781930110991", "validate-1");
        assertEquals(validator.validate("1-930110-99-5"), "9781930110991", "validate-2");
        assertEquals(validator.validate("1 930110 99 5"), "9781930110991", "validate-3");
        assertEquals(validator.validate("020163385X"), "9780201633856", "validate-4");
        assertEquals(validator.validate("0-201-63385-X"), "9780201633856", "validate-5");
        assertEquals(validator.validate("0 201 63385 X"), "9780201633856", "validate-6");
    }

    /**
     * Test validate() ISBN-13 codes
     */
    @Test
    public void testValidateISBN13() {
        final ISBNValidator validator = ISBNValidator.getInstance();
        assertEquals(validator.validateISBN13("9781930110991"), "9781930110991", "validateISBN13-1");
        assertEquals(validator.validateISBN13("978-1-930110-99-1"), "9781930110991", "validateISBN13-2");
        assertEquals(validator.validateISBN13("978 1 930110 99 1"), "9781930110991", "validateISBN13-3");
        assertEquals(validator.validateISBN13("9780201633856"), "9780201633856", "validateISBN13-4");
        assertEquals(validator.validateISBN13("978-0-201-63385-6"), "9780201633856", "validateISBN13-5");
        assertEquals(validator.validateISBN13("978 0 201 63385 6"), "9780201633856", "validateISBN13-6");

        assertEquals(validator.validate("9781930110991"), "9781930110991", "validate-1");
        assertEquals(validator.validate("978-1-930110-99-1"), "9781930110991", "validate-2");
        assertEquals(validator.validate("978 1 930110 99 1"), "9781930110991", "validate-3");
        assertEquals(validator.validate("9780201633856"), "9780201633856", "validate-4");
        assertEquals(validator.validate("978-0-201-63385-6"), "9780201633856", "validate-5");
        assertEquals(validator.validate("978 0 201 63385 6"), "9780201633856", "validate-6");
    }

    /**
     * Test Valid ISBN-10 formats.
     */
    @Test
    public void testValidISBN10Format() {
        final Pattern pattern = Pattern.compile(ISBNValidator.ISBN10_REGEX);
        for (int i = 0; i < validISBN10Format.length; i++) {
            assertTrue(pattern.matcher(validISBN10Format[i]).matches(), "Pattern[" + i + "]=" + validISBN10Format[i]);
        }
    }

    /**
     * Test Valid ISBN-13 formats.
     */
    @Test
    public void testValidISBN13Format() {
        final Pattern pattern = Pattern.compile(ISBNValidator.ISBN13_REGEX);
        for (int i = 0; i < validISBN13Format.length; i++) {
            assertTrue(pattern.matcher(validISBN13Format[i]).matches(), "Pattern[" + i + "]=" + validISBN13Format[i]);
        }
    }

}
