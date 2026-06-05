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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests that check digit routines reject non-ASCII Unicode digits that {@code Character.getNumericValue} / {@code Character.isDigit} otherwise map to 0-9.
 */
class CheckDigitNonAsciiDigitTest {

    private static String toFullwidth(final String code) {
        final StringBuilder sb = new StringBuilder(code.length());
        for (int i = 0; i < code.length(); i++) {
            final char c = code.charAt(i);
            sb.append(c >= '0' && c <= '9' ? (char) ('０' + (c - '0')) : c);
        }
        return sb.toString();
    }

    private static String toArabicIndic(final String code) {
        final StringBuilder sb = new StringBuilder(code.length());
        for (int i = 0; i < code.length(); i++) {
            final char c = code.charAt(i);
            sb.append(c >= '0' && c <= '9' ? (char) ('٠' + (c - '0')) : c);
        }
        return sb.toString();
    }

    private static void assertRejectsNonAscii(final CheckDigit routine, final String validCode) {
        assertTrue(routine.isValid(validCode), "ASCII: " + validCode);
        assertFalse(routine.isValid(toFullwidth(validCode)), "fullwidth: " + validCode);
        assertFalse(routine.isValid(toArabicIndic(validCode)), "arabic-indic: " + validCode);
        for (char i = 0; i < 32; i++) {
            assertFalse(routine.isValid(validCode.replaceFirst("[a-zA-Z0-9]", Character.valueOf(i).toString())), validCode);
        }
        for (char i = 127; i < 256; i++) {
            assertFalse(routine.isValid(validCode.replaceFirst("[a-zA-Z0-9]", Character.valueOf(i).toString())), validCode);
        }
    }

    @Test
    void testCUSIP() {
        assertRejectsNonAscii(CUSIPCheckDigit.CUSIP_CHECK_DIGIT, "037833100");
    }

    @Test
    void testEAN13() {
        assertRejectsNonAscii(EAN13CheckDigit.EAN13_CHECK_DIGIT, "9780072129519");
    }

    @Test
    void testISIN() {
        assertRejectsNonAscii(ISINCheckDigit.ISIN_CHECK_DIGIT, "US0378331005");
    }

    @Test
    void testISBN10() {
        assertRejectsNonAscii(ISBN10CheckDigit.ISBN10_CHECK_DIGIT, "1930110995");
    }

    @Test
    void testISSN() {
        assertRejectsNonAscii(ISSNCheckDigit.ISSN_CHECK_DIGIT, "03178471");
    }

    @Test
    void testLuhn() {
        assertRejectsNonAscii(LuhnCheckDigit.LUHN_CHECK_DIGIT, "4417123456789113");
    }

    @Test
    void testLuhnInvalid() {
        assertRejectsNonAscii(LuhnCheckDigit.LUHN_CHECK_DIGIT, "4417123456789113");
    }

    @Test
    void testModulusTenLuhn() {
        assertRejectsNonAscii(new ModulusTenCheckDigit(new int[] { 1, 2 }, true, true), "4417123456789113");
    }

    @Test
    void testSedol() {
        assertRejectsNonAscii(SedolCheckDigit.SEDOL_CHECK_DIGIT, "0263494");
    }

    @Test
    void testVerhoeff() {
        assertRejectsNonAscii(VerhoeffCheckDigit.VERHOEFF_CHECK_DIGIT, "1428570");
    }
}
