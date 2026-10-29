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
package org.apache.commons.validator.routines.checkdigit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * ISO/IEC 7064, MOD 97-10 Check Digit Test.
 */
class Modulus97CheckDigitTest extends AbstractCheckDigitTest {

    Modulus97CheckDigitTest() {
        checkDigitLth = 2;
    }

    /**
     * Returns an array of codes with invalid check digits.
     *
     * @param codes Codes with valid check digits
     * @return Codes with invalid check digits
     */
    @Override
    protected String[] createInvalidCodes(final String[] codes) {
        final List<String> list = new ArrayList<>();
        // create invalid check digit values
        for (final String fullCode : codes) {
            final String code = removeCheckDigit(fullCode);
            final String check = checkDigit(fullCode);
            for (int j = 0; j <= 99; j++) { // only one of the 100 two-digit values can be correct
                final String curr = j > 9 ? "" + j : "0" + j;
                if (!curr.equals(check)) {
                    list.add(code + curr);
                }
            }
        }
        return list.toArray(new String[0]);
    }

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = Modulus97CheckDigit.MODULUS_97_CHECK_DIGIT;
        /*
         * LEI codes (ISO 17442) are taken from public GLEIF data, see https://search.gleif.org/.
         * 5493001KJTIIGC8Y1R12 is the LEI of GLEIF itself.
         * The other codes are the ISO/IEC 7064 example "794" -> "79444" and the German Leitweg-ID example
         * "04011000-1234512345-06" (without separators), which also uses MOD 97-10.
         */
        valid = new String[] { "5493001KJTIIGC8Y1R12", // GLEIF
                "549300WXPZHMNQNPYL49", "529900MZNQDC3A3WSZ72", "SNZ2OJLFK8MNNCLQOF39", "RNVZOEETEJ32KW0QXS82", "R0MUWSFPU8MPRO8K5P83",
                "549300JB1P61FUTPEZ75", "213800WSGIIZCXF1P572", "ZZZZZZZZZZZZZZZZZZ40", "00000000000000000098", "79444", "04011000123451234506", };
        invalid = new String[] { "549300WXPZHMNQNPYL4A", // check digits must be numeric
                "549300WXPZHMNQNPYL00", // 00 is never a valid check digit value
                "5493#0WXPZHMNQNPYL49", // invalid character
                "ABC", // check digits must be numeric
        };
        zeroSum = null;
        missingMessage = "Invalid Code length=0";
    }

    /**
     * MOD 97-10 check digits can only be in the range 02 to 98, so 00, 01 and 99 are always rejected,
     * even if the modulus calculation would give a remainder of 1.
     */
    @Test
    void testReservedCheckDigitValues() {
        assertFalse(routine.isValid("549300WXPZHMNQNPYL00"));
        assertFalse(routine.isValid("549300WXPZHMNQNPYL01"));
        assertFalse(routine.isValid("549300WXPZHMNQNPYL99"));
        // "9800" is 9800 mod 97 = 1 and "0001" is 1 mod 97 = 1, but the check digits are reserved values
        assertFalse(routine.isValid("9800"));
        assertFalse(routine.isValid("0001"));
    }

    /**
     * The calculation only ever produces two digit check values in the range 02 to 98.
     */
    @Test
    void testCalculateRange() throws CheckDigitException {
        for (int i = 0; i < 97; i++) {
            final String code = String.format("ZZ%02d", i);
            final String check = routine.calculate(code);
            assertEquals(2, check.length(), code);
            final int value = Integer.parseInt(check);
            assertTrue(value >= 2 && value <= 98, code + " -> " + check);
            assertTrue(routine.isValid(code + check), code + check);
        }
    }

    /**
     * Test zero sum: not applicable to MOD 97-10, a code consisting only of zeros has valid check digits "98".
     */
    @Override
    @Test
    void testZeroSum() {
        assertTrue(routine.isValid("00000000000000000098"));
        assertFalse(routine.isValid("00000000000000000000"));
    }
}
