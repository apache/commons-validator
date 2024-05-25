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
package org.apache.commons.validator.routines.checkdigit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Check Digit Test.
 */
public abstract class AbstractCheckDigitTest {

    private static final String POSSIBLE_CHECK_DIGITS = "0123456789 ABCDEFHIJKLMNOPQRSTUVWXYZ\tabcdefghijklmnopqrstuvwxyz!@£$%^&*()_+";

    /** Logging instance */
    protected Log log = LogFactory.getLog(getClass());

    /** Check digit routine being tested */
    protected int checkDigitLth = 1;

    /** Check digit routine being tested */
    protected CheckDigit routine;

    /**
     * Array of valid code values These must contain valid strings *including* the check digit.
     *
     * They are passed to: CheckDigit.isValid(expects string including checkdigit) which is expected to return true and
     * AbstractCheckDigitTest.createInvalidCodes() which mangles the last character to check that the result is now invalid. and the truncated string is passed
     * to CheckDigit.calculate(expects string without checkdigit) the result is compared with the last character
     */
    protected String[] valid;

    /**
     * Array of invalid code values
     *
     * These are currently passed to both CheckDigit.calculate(expects a string without checkdigit) which is expected to throw an exception However that only
     * applies if the string is syntactically incorrect; and CheckDigit.isValid(expects a string including checkdigit) which is expected to return false
     *
     * See https://issues.apache.org/jira/browse/VALIDATOR-344 for some dicussion on this
     */
    protected String[] invalid = { "12345678A" };

    /** Code value which sums to zero */
    protected String zeroSum = "0000000000";

    /** Prefix for error messages */
    protected String missingMessage = "Code is missing";

    /**
     * Returns the check digit (i.e. last character) for a code.
     *
     * @param code The code
     * @return The check digit
     */
    protected String checkDigit(final String code) {
        if (code == null || code.length() <= checkDigitLth) {
            return "";
        }
        final int start = code.length() - checkDigitLth;
        return code.substring(start);
    }

    // private static final String POSSIBLE_CHECK_DIGITS = "0123456789";
    /**
     * Returns an array of codes with invalid check digits.
     *
     * @param codes Codes with valid check digits
     * @return Codes with invalid check digits
     */
    protected String[] createInvalidCodes(final String[] codes) {
        final List<String> list = new ArrayList<>();

        // create invalid check digit values
        for (final String fullCode : codes) {
            final String code = removeCheckDigit(fullCode);
            final String check = checkDigit(fullCode);
            for (int j = 0; j < POSSIBLE_CHECK_DIGITS.length(); j++) {
                final String curr = POSSIBLE_CHECK_DIGITS.substring(j, j + 1); // "" + Character.forDigit(j, 10);
                if (!curr.equals(check)) {
                    list.add(code + curr);
                }
            }
        }

        return list.toArray(new String[0]);
    }

    /**
     * Returns a code with the Check Digit (i.e. last character) removed.
     *
     * @param code The code
     * @return The code without the check digit
     */
    protected String removeCheckDigit(final String code) {
        if (code == null || code.length() <= checkDigitLth) {
            return null;
        }
        return code.substring(0, code.length() - checkDigitLth);
    }

    /**
     * Tear Down - clears routine and valid codes.
     */
    @AfterEach
    protected void tearDown() {
        valid = null;
        routine = null;
    }

    /**
     * Test calculate() for invalid values.
     */
    @Test
    public void testCalculateInvalid() {

        if (log.isDebugEnabled()) {
            log.debug("testCalculateInvalid() for " + routine.getClass().getName());
        }

        // test invalid code values
        for (int i = 0; i < invalid.length; i++) {
            try {
                final String code = invalid[i];
                if (log.isDebugEnabled()) {
                    log.debug("   " + i + " Testing Invalid Check Digit, Code=[" + code + "]");
                }
                final String expected = checkDigit(code);
                final String codeWithNoCheckDigit = removeCheckDigit(code);
                if (codeWithNoCheckDigit == null) {
                    throw new CheckDigitException("Invalid Code=[" + code + "]");
                }
                final String actual = routine.calculate(codeWithNoCheckDigit);
                // If exception not thrown, check that the digit is incorrect instead
                if (expected.equals(actual)) {
                    fail("Expected mismatch for " + code + " expected " + expected + " actual " + actual);
                }
            } catch (final CheckDigitException e) {
                // possible failure messages:
                // Invalid ISBN Length ...
                // Invalid Character[ ...
                // Are there any others?
                assertTrue(e.getMessage().startsWith("Invalid "), "Invalid Character[" + i + "]=" + e.getMessage());
// WAS                assertTrue("Invalid Character[" +i +"]=" +  e.getMessage(), e.getMessage().startsWith("Invalid Character["));
            }
        }
    }

    /**
     * Test calculate() for valid values.
     */
    @Test
    public void testCalculateValid() {
        if (log.isDebugEnabled()) {
            log.debug("testCalculateValid() for " + routine.getClass().getName());
        }

        // test valid values
        for (int i = 0; i < valid.length; i++) {
            final String code = removeCheckDigit(valid[i]);
            final String expected = checkDigit(valid[i]);
            try {
                if (log.isDebugEnabled()) {
                    log.debug("   " + i + " Testing Valid Check Digit, Code=[" + code + "] expected=[" + expected + "]");
                }
                assertEquals(expected, routine.calculate(code), "valid[" + i + "]: " + valid[i]);
            } catch (final Exception e) {
                fail("valid[" + i + "]=" + valid[i] + " threw " + e);
            }
        }

    }

    /**
     * Test isValid() for invalid values.
     */
    @Test
    public void testIsValidFalse() {
        if (log.isDebugEnabled()) {
            log.debug("testIsValidFalse() for " + routine.getClass().getName());
        }

        // test invalid code values
        for (int i = 0; i < invalid.length; i++) {
            if (log.isDebugEnabled()) {
                log.debug("   " + i + " Testing Invalid Code=[" + invalid[i] + "]");
            }
            assertFalse(routine.isValid(invalid[i]), "invalid[" + i + "]: " + invalid[i]);
        }

        // test invalid check digit values
        final String[] invalidCheckDigits = createInvalidCodes(valid);
        for (int i = 0; i < invalidCheckDigits.length; i++) {
            if (log.isDebugEnabled()) {
                log.debug("   " + i + " Testing Invalid Check Digit, Code=[" + invalidCheckDigits[i] + "]");
            }
            assertFalse(routine.isValid(invalidCheckDigits[i]), "invalid check digit[" + i + "]: " + invalidCheckDigits[i]);
        }
    }

    /**
     * Test isValid() for valid values.
     */
    @Test
    public void testIsValidTrue() {
        if (log.isDebugEnabled()) {
            log.debug("testIsValidTrue() for " + routine.getClass().getName());
        }

        // test valid values
        for (int i = 0; i < valid.length; i++) {
            if (log.isDebugEnabled()) {
                log.debug("   " + i + " Testing Valid Code=[" + valid[i] + "]");
            }
            assertTrue(routine.isValid(valid[i]), "valid[" + i + "]: " + valid[i]);
        }
    }

    /**
     * Test missing code
     */
    @Test
    public void testMissingCode() {

        // isValid() null
        assertFalse(routine.isValid(null), "isValid() Null");

        // isValid() zero length
        assertFalse(routine.isValid(""), "isValid() Zero Length");

        // isValid() length 1
        // Don't use 0, because that passes for Verhoef (not sure why yet)
        assertFalse(routine.isValid("9"), "isValid() Length 1");

        // calculate() null
        try {
            routine.calculate(null);
            fail("calculate() Null - expected exception");
        } catch (final Exception e) {
            assertEquals(missingMessage, e.getMessage(), "calculate() Null");
        }

        // calculate() zero length
        try {
            routine.calculate("");
            fail("calculate() Zero Length - expected exception");
        } catch (final Exception e) {
            assertEquals(missingMessage, e.getMessage(), "calculate() Zero Length");
        }
    }

    /**
     * Test check digit serialization.
     */
    @Test
    public void testSerialization() {
        // Serialize the check digit routine
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(routine);
            oos.flush();
        } catch (final Exception e) {
            fail(routine.getClass().getName() + " error during serialization: " + e);
        }

        // Deserialize the test object
        Object result = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
            final ObjectInputStream ois = new ObjectInputStream(bais);
            result = ois.readObject();
        } catch (final Exception e) {
            fail(routine.getClass().getName() + " error during deserialization: " + e);
        }
        assertNotNull(result);
    }

    /**
     * Test zero sum
     */
    @Test
    public void testZeroSum() {
        assertFalse(routine.isValid(zeroSum), "isValid() Zero Sum");
        try {
            routine.calculate(zeroSum);
            fail("Zero Sum - expected exception");
        } catch (final Exception e) {
            assertEquals("Invalid code, sum is zero", e.getMessage(), "isValid() Zero Sum");
        }
    }

}
