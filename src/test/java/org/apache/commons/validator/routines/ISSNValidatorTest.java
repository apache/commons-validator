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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ISSNValidator}.
 */
public class ISSNValidatorTest {

    private static final ISSNValidator VALIDATOR = ISSNValidator.getInstance();

    private static final String[] VALID_FORMAT = { "ISSN 0317-8471", "1050-124X", "ISSN 1562-6865", "1063-7710", "1748-7188", "ISSN 0264-2875", "1750-0095",
            "1188-1534", "1911-1479", "ISSN 1911-1460", "0001-6772", "1365-201X", "0264-3596", "1144-875X", };

    private static final String[] INVALID_FORMAT = { "", // empty
            "   ", // empty
            "ISBN 0317-8471", // wrong prefix
            "'1050-124X", // leading garbage
            "ISSN1562-6865", // missing separator
            "10637710", // missing separator
            "1748-7188'", // trailing garbage
            "ISSN  0264-2875", // extra space
            "1750 0095", // invalid separator
            "1188_1534", // invalid separator
            "1911-1478", // invalid checkdigit
    };

    /**
     * Test Invalid EAN-13 ISSN prefix codes Test Input length
     */
    @Test
    public void testConversionErrors() {
        final String input1 = "9780072129519";
        assertThrows(IllegalArgumentException.class, () -> VALIDATOR.extractFromEAN13(input1), "Expected IllegalArgumentException for '" + input1 + "'");

        final String input2 = "9791090636071";
        assertThrows(IllegalArgumentException.class, () -> VALIDATOR.extractFromEAN13(input2), "Expected IllegalArgumentException for '" + input2 + "'");

        final String input3 = "03178471";
        assertThrows(IllegalArgumentException.class, () -> VALIDATOR.extractFromEAN13(input3), "Expected IllegalArgumentException for '" + input3 + "'");
    }

    /**
     * Test Invalid ISSN codes
     */
    @Test
    public void testInvalid() {
        for (final String f : INVALID_FORMAT) {
            assertFalse(VALIDATOR.isValid(f), f);
        }
    }

    /**
     * Test valid EAN-13 ISSN codes and extract the ISSN
     */
    @Test
    public void testIsValidExtract() {
        assertEquals("12345679", VALIDATOR.extractFromEAN13("9771234567003"));
        assertEquals("00014664", VALIDATOR.extractFromEAN13("9770001466006"));
        assertEquals("03178471", VALIDATOR.extractFromEAN13("9770317847001"));
        assertEquals("1144875X", VALIDATOR.extractFromEAN13("9771144875007"));
    }

    /**
     * Test isValid() ISSN codes
     */
    @Test
    public void testIsValidISSN() {
        for (final String f : VALID_FORMAT) {
            assertTrue(VALIDATOR.isValid(f), f);
        }
    }

    /**
     * Test isValid() ISSN codes and convert them
     */
    @Test
    public void testIsValidISSNConvert() {
        final CheckDigit ean13cd = EAN13CheckDigit.EAN13_CHECK_DIGIT;
        final Random r = new Random();
        for (final String f : VALID_FORMAT) {
            final String suffix = String.format("%02d", r.nextInt(100));
            final String ean13 = VALIDATOR.convertToEAN13(f, suffix);
            assertTrue(ean13cd.isValid(ean13), ean13);
        }
        // internet samples
        assertEquals(VALIDATOR.convertToEAN13("1144-875X", "00"), "9771144875007");
        assertEquals(VALIDATOR.convertToEAN13("0264-3596", "00"), "9770264359008");
        assertEquals(VALIDATOR.convertToEAN13("1234-5679", "00"), "9771234567003");
    }

    @Test
    public void testIsValidISSNConvertNull() {
        assertNull(VALIDATOR.convertToEAN13(null, "00"));
    }

    @Test
    public void testIsValidISSNConvertSuffix() {
        assertThrows(IllegalArgumentException.class, () -> VALIDATOR.convertToEAN13(null, null));
        assertThrows(IllegalArgumentException.class, () -> VALIDATOR.convertToEAN13(null, ""));
        assertThrows(IllegalArgumentException.class, () -> VALIDATOR.convertToEAN13(null, "0"));
        assertThrows(IllegalArgumentException.class, () -> VALIDATOR.convertToEAN13(null, "A"));
        assertThrows(IllegalArgumentException.class, () -> VALIDATOR.convertToEAN13(null, "AA"));
        assertThrows(IllegalArgumentException.class, () -> VALIDATOR.convertToEAN13(null, "999"));
    }

    /**
     * Test null values
     */
    @Test
    public void testNull() {
        assertFalse(VALIDATOR.isValid(null), "isValid");
    }

    /**
     * Test Invalid EAN-13 ISSN codes
     */
    @Test
    public void testValidCheckDigitEan13() {
        assertNull(VALIDATOR.extractFromEAN13("9771234567001"));
        assertNull(VALIDATOR.extractFromEAN13("9771234567002"));
        assertNotNull(VALIDATOR.extractFromEAN13("9771234567003")); // valid check digit
        assertNull(VALIDATOR.extractFromEAN13("9771234567004"));
        assertNull(VALIDATOR.extractFromEAN13("9771234567005"));
        assertNull(VALIDATOR.extractFromEAN13("9771234567006"));
        assertNull(VALIDATOR.extractFromEAN13("9771234567007"));
        assertNull(VALIDATOR.extractFromEAN13("9771234567008"));
        assertNull(VALIDATOR.extractFromEAN13("9771234567009"));
        assertNull(VALIDATOR.extractFromEAN13("9771234567000"));
    }
}
