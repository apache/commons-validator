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

import java.util.regex.Pattern;
import junit.framework.TestCase;

/**
 * ISBNValidator Test Case.
 *
 * @version $Revision$
 */
public class ISBNValidatorTest extends TestCase {

    private final String[] validISBN10Format = new String[] {
            "1234567890",
            "123456789X",
            "12345-1234567-123456-X",
            "12345 1234567 123456 X",
            "1-2-3-4",
            "1 2 3 4",
            };

    private final String[] invalidISBN10Format = new String[] {
            "",                        // empty
            "   ",                     // empty
            "1",                       // too short
            "123456789",               // too short
            "12345678901",             // too long
            "12345678X0",              // X not at end
            "123456-1234567-123456-X", // Group too long
            "12345-12345678-123456-X", // Publisher too long
            "12345-1234567-1234567-X", // Title too long
            "12345-1234567-123456-X2", // Check Digit too long
            "--1 930110 99 5",         // format
            "1 930110 99 5--",         // format
            "1 930110-99 5-",          // format
            "1.2.3.4",                 // Invalid Separator
            "1=2=3=4",                 // Invalid Separator
            "1_2_3_4",                 // Invalid Separator
            "123456789Y",              // Other character at the end
            "dsasdsadsa",              // invalid characters
            "I love sparrows!",        // invalid characters
            "068-556-98-45"            // format
            };

    private final String[] validISBN13Format = new String[] {
            "9781234567890",
            "9791234567890",
            "978-12345-1234567-123456-1",
            "979-12345-1234567-123456-1",
            "978 12345 1234567 123456 1",
            "979 12345 1234567 123456 1",
            "978-1-2-3-4",
            "979-1-2-3-4",
            "978 1 2 3 4",
            "979 1 2 3 4",
            };

    private final String[] invalidISBN13Format = new String[] {
            "",                            // empty
            "   ",                         // empty
            "1",                           // too short
            "978123456789",                // too short
            "97812345678901",              // too long
            "978-123456-1234567-123456-1", // Group too long
            "978-12345-12345678-123456-1", // Publisher too long
            "978-12345-1234567-1234567-1", // Title too long
            "978-12345-1234567-123456-12", // Check Digit too long
            "--978 1 930110 99 1",         // format
            "978 1 930110 99 1--",         // format
            "978 1 930110-99 1-",          // format
            "123-4-567890-12-8",           // format
            "978.1.2.3.4",                 // Invalid Separator
            "978=1=2=3=4",                 // Invalid Separator
            "978_1_2_3_4",                 // Invalid Separator
            "978123456789X",               // invalid character
            "978-0-201-63385-X",           // invalid character
            "dsasdsadsadsa",               // invalid characters
            "I love sparrows!",            // invalid characters
            "979-1-234-567-89-6"           // format
            };

    /**
     * Create a test case with the specified name.
     * @param name The name of the test
     */
    public ISBNValidatorTest(String name) {
        super(name);
    }

    /**
     * Test Valid ISBN-10 formats.
     */
    public void testValidISBN10Format() {
        Pattern pattern = Pattern.compile(ISBNValidator.ISBN10_REGEX);
        for (int i = 0; i < validISBN10Format.length; i++) {
            assertTrue("Pattern[" + i + "]=" + validISBN10Format[i], pattern.matcher(validISBN10Format[i]).matches());
        }
    }

    /**
     * Test Invalid ISBN-10 formats.
     */
    public void testInvalidISBN10Format() {
        ISBNValidator validator = ISBNValidator.getInstance();
        Pattern pattern = Pattern.compile(ISBNValidator.ISBN10_REGEX);
        for (int i = 0; i < invalidISBN10Format.length; i++) {
            assertFalse("Pattern[" + i + "]=" + invalidISBN10Format[i],       pattern.matcher(invalidISBN10Format[i]).matches());
            assertFalse("isValidISBN10[" + i + "]=" + invalidISBN10Format[i], validator.isValidISBN10(invalidISBN10Format[i]));
            assertNull("validateISBN10[" + i + "]=" + invalidISBN10Format[i], validator.validateISBN10(invalidISBN10Format[i]));
        }
    }

    /**
     * Test Valid ISBN-13 formats.
     */
    public void testValidISBN13Format() {
        Pattern pattern = Pattern.compile(ISBNValidator.ISBN13_REGEX);
        for (int i = 0; i < validISBN13Format.length; i++) {
            assertTrue("Pattern[" + i + "]=" + validISBN13Format[i], pattern.matcher(validISBN13Format[i]).matches());
        }
    }

    /**
     * Test Invalid ISBN-13 formats.
     */
    public void testInvalidISBN13Format() {
        Pattern pattern = Pattern.compile(ISBNValidator.ISBN13_REGEX);
        ISBNValidator validator = ISBNValidator.getInstance();
        for (int i = 0; i < invalidISBN13Format.length; i++) {
            assertFalse("Pattern[" + i + "]=" + invalidISBN13Format[i],       pattern.matcher(invalidISBN13Format[i]).matches());
            assertFalse("isValidISBN13[" + i + "]=" + invalidISBN13Format[i], validator.isValidISBN13(invalidISBN13Format[i]));
            assertNull("validateISBN13[" + i + "]=" + invalidISBN13Format[i], validator.validateISBN13(invalidISBN13Format[i]));
        }
    }

    /**
     * Test isValid() ISBN-10 codes
     */
    public void testIsValidISBN10() {
        ISBNValidator validator = ISBNValidator.getInstance();
        assertTrue("isValidISBN10-1", validator.isValidISBN10("1930110995"));
        assertTrue("isValidISBN10-2", validator.isValidISBN10("1-930110-99-5"));
        assertTrue("isValidISBN10-3", validator.isValidISBN10("1 930110 99 5"));
        assertTrue("isValidISBN10-4", validator.isValidISBN10("020163385X"));
        assertTrue("isValidISBN10-5", validator.isValidISBN10("0-201-63385-X"));
        assertTrue("isValidISBN10-6", validator.isValidISBN10("0 201 63385 X"));

        assertTrue("isValid-1", validator.isValid("1930110995"));
        assertTrue("isValid-2", validator.isValid("1-930110-99-5"));
        assertTrue("isValid-3", validator.isValid("1 930110 99 5"));
        assertTrue("isValid-4", validator.isValid("020163385X"));
        assertTrue("isValid-5", validator.isValid("0-201-63385-X"));
        assertTrue("isValid-6", validator.isValid("0 201 63385 X"));
    }

    /**
     * Test isValid() ISBN-13 codes
     */
    public void testIsValidISBN13() {
        ISBNValidator validator = ISBNValidator.getInstance();
        assertTrue("isValidISBN13-1", validator.isValidISBN13("9781930110991"));
        assertTrue("isValidISBN13-2", validator.isValidISBN13("978-1-930110-99-1"));
        assertTrue("isValidISBN13-3", validator.isValidISBN13("978 1 930110 99 1"));
        assertTrue("isValidISBN13-4", validator.isValidISBN13("9780201633856"));
        assertTrue("isValidISBN13-5", validator.isValidISBN13("978-0-201-63385-6"));
        assertTrue("isValidISBN13-6", validator.isValidISBN13("978 0 201 63385 6"));

        assertTrue("isValid-1", validator.isValid("9781930110991"));
        assertTrue("isValid-2", validator.isValid("978-1-930110-99-1"));
        assertTrue("isValid-3", validator.isValid("978 1 930110 99 1"));
        assertTrue("isValid-4", validator.isValid("9780201633856"));
        assertTrue("isValid-5", validator.isValid("978-0-201-63385-6"));
        assertTrue("isValid-6", validator.isValid("978 0 201 63385 6"));
    }

    /**
     * Test validate() ISBN-10 codes (don't convert)
     */
    public void testValidateISBN10() {
        ISBNValidator validator = ISBNValidator.getInstance(false);
        assertEquals("validateISBN10-1", "1930110995", validator.validateISBN10("1930110995"));
        assertEquals("validateISBN10-2", "1930110995", validator.validateISBN10("1-930110-99-5"));
        assertEquals("validateISBN10-3", "1930110995", validator.validateISBN10("1 930110 99 5"));
        assertEquals("validateISBN10-4", "020163385X", validator.validateISBN10("020163385X"));
        assertEquals("validateISBN10-5", "020163385X", validator.validateISBN10("0-201-63385-X"));
        assertEquals("validateISBN10-6", "020163385X", validator.validateISBN10("0 201 63385 X"));

        assertEquals("validate-1", "1930110995", validator.validate("1930110995"));
        assertEquals("validate-2", "1930110995", validator.validate("1-930110-99-5"));
        assertEquals("validate-3", "1930110995", validator.validate("1 930110 99 5"));
        assertEquals("validate-4", "020163385X", validator.validate("020163385X"));
        assertEquals("validate-5", "020163385X", validator.validate("0-201-63385-X"));
        assertEquals("validate-6", "020163385X", validator.validate("0 201 63385 X"));
    }

    /**
     * Test validate() ISBN-10 codes (convert)
     */
    public void testValidateISBN10Convert() {
        ISBNValidator validator = ISBNValidator.getInstance();
        assertEquals("validate-1", "9781930110991", validator.validate("1930110995"));
        assertEquals("validate-2", "9781930110991", validator.validate("1-930110-99-5"));
        assertEquals("validate-3", "9781930110991", validator.validate("1 930110 99 5"));
        assertEquals("validate-4", "9780201633856", validator.validate("020163385X"));
        assertEquals("validate-5", "9780201633856", validator.validate("0-201-63385-X"));
        assertEquals("validate-6", "9780201633856", validator.validate("0 201 63385 X"));
    }

    /**
     * Test validate() ISBN-13 codes
     */
    public void testValidateISBN13() {
        ISBNValidator validator = ISBNValidator.getInstance();
        assertEquals("validateISBN13-1", "9781930110991", validator.validateISBN13("9781930110991"));
        assertEquals("validateISBN13-2", "9781930110991", validator.validateISBN13("978-1-930110-99-1"));
        assertEquals("validateISBN13-3", "9781930110991", validator.validateISBN13("978 1 930110 99 1"));
        assertEquals("validateISBN13-4", "9780201633856", validator.validateISBN13("9780201633856"));
        assertEquals("validateISBN13-5", "9780201633856", validator.validateISBN13("978-0-201-63385-6"));
        assertEquals("validateISBN13-6", "9780201633856", validator.validateISBN13("978 0 201 63385 6"));

        assertEquals("validate-1", "9781930110991", validator.validate("9781930110991"));
        assertEquals("validate-2", "9781930110991", validator.validate("978-1-930110-99-1"));
        assertEquals("validate-3", "9781930110991", validator.validate("978 1 930110 99 1"));
        assertEquals("validate-4", "9780201633856", validator.validate("9780201633856"));
        assertEquals("validate-5", "9780201633856", validator.validate("978-0-201-63385-6"));
        assertEquals("validate-6", "9780201633856", validator.validate("978 0 201 63385 6"));
    }

    /**
     * Test null values
     */
    public void testNull() {
        ISBNValidator validator = ISBNValidator.getInstance();
        assertFalse("isValid",        validator.isValid(null));
        assertFalse("isValidISBN10",  validator.isValidISBN10(null));
        assertFalse("isValidISBN13",  validator.isValidISBN13(null));
        assertNull("validate",        validator.validate(null));
        assertNull("validateISBN10",  validator.validateISBN10(null));
        assertNull("validateISBN13",  validator.validateISBN13(null));
        assertNull("convertToISBN13", validator.convertToISBN13(null));
    }

    /**
     * Test Invalid ISBN-10 codes
     */
    public void testInvalid() {
        ISBNValidator validator = ISBNValidator.getInstance();
        String baseCode = "193011099";
        assertFalse("ISBN10-0", validator.isValid(baseCode + "0"));
        assertFalse("ISBN10-1", validator.isValid(baseCode + "1"));
        assertFalse("ISBN10-2", validator.isValid(baseCode + "2"));
        assertFalse("ISBN10-3", validator.isValid(baseCode + "3"));
        assertFalse("ISBN10-4", validator.isValid(baseCode + "4"));
        assertTrue("ISBN10-5",  validator.isValid(baseCode + "5")); // valid check digit
        assertFalse("ISBN10-6", validator.isValid(baseCode + "6"));
        assertFalse("ISBN10-7", validator.isValid(baseCode + "7"));
        assertFalse("ISBN10-8", validator.isValid(baseCode + "8"));
        assertFalse("ISBN10-9", validator.isValid(baseCode + "9"));
        assertFalse("ISBN10-X", validator.isValid(baseCode + "X"));

        baseCode = "978193011099";
        assertFalse("ISBN13-0", validator.isValid(baseCode + "0"));
        assertTrue("ISBN13-1",  validator.isValid(baseCode + "1")); // valid check digit
        assertFalse("ISBN13-2", validator.isValid(baseCode + "2"));
        assertFalse("ISBN13-3", validator.isValid(baseCode + "3"));
        assertFalse("ISBN13-4", validator.isValid(baseCode + "4"));
        assertFalse("ISBN13-5", validator.isValid(baseCode + "5"));
        assertFalse("ISBN13-6", validator.isValid(baseCode + "6"));
        assertFalse("ISBN13-7", validator.isValid(baseCode + "7"));
        assertFalse("ISBN13-8", validator.isValid(baseCode + "8"));
        assertFalse("ISBN13-9", validator.isValid(baseCode + "9"));
    }

    /**
     * Test method for {@link org.apache.commons.validator.routines.ISBNValidator#convertToISBN13(java.lang.String)}.
     */
    public void testConversionErrors() {
        ISBNValidator validator = ISBNValidator.getInstance();
        String input = null;
        try {
            input = "123456789 ";
            validator.convertToISBN13(input);
            fail("Expected IllegalArgumentException for '" + input + "'"); 
        } catch (IllegalArgumentException e) {
            // expected result
        }
        try {
            input = "12345678901";
            validator.convertToISBN13(input);
            fail("Expected IllegalArgumentException for '" + input + "'"); 
        } catch (IllegalArgumentException e) {
            // expected result
        }
        try {
            input = "";
            validator.convertToISBN13(input);
            fail("Expected IllegalArgumentException for '" + input + "'"); 
        } catch (IllegalArgumentException e) {
            // expected result
        }
        try {
            input = "X234567890";
            validator.convertToISBN13(input);
            fail("Expected IllegalArgumentException for '" + input + "'"); 
        } catch (IllegalArgumentException e) {
            // expected result
        }
    }

}
