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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests {@link LEIValidator}.
 */
class LEIValidatorTest {

    private static final LEIValidator VALIDATOR = LEIValidator.getInstance();

    /*
     * LEI codes are taken from public GLEIF data, see https://search.gleif.org/.
     */
    // @formatter:off
    private final String[] validFormat = {
            "5493001KJTIIGC8Y1R12", // Global Legal Entity Identifier Foundation
            "549300WXPZHMNQNPYL49",
            "529900MZNQDC3A3WSZ72",
            "SNZ2OJLFK8MNNCLQOF39",
            "RNVZOEETEJ32KW0QXS82",
            "R0MUWSFPU8MPRO8K5P83",
            "549300JB1P61FUTPEZ75",
            "213800WSGIIZCXF1P572",
            "ZZZZZZZZZZZZZZZZZZ40", // Invented; all letters
            "00000000000000000098", // Invented; all digits
        };
    private final String[] invalidFormat = { null, "", // empty
            "   ", // empty
            "5493001KJTIIGC8Y1R1", // 19 characters
            "5493001KJTIIGC8Y1R120", // 21 characters
            "5493001KJTIIGC8Y1R13", // proper check digits are '12', see above
            "549300WXPZHMNQNPYL94", // proper check digits are '49', see above
            "549300WXPZHMNQNPYL4A", // check digits must be numeric
            "549300WXPZHMNQNPYL00", // 00 is never a valid check digit value
            "5493001kjtiigc8y1r12", // disallow lower case
            "5493001KJTIIGC8Y1R-12", // disallow separators
            "5493001KJTIIGC8Y1R 12", // disallow embedded blanks
            "5493001KJT IGC8Y1R12", // disallow embedded blanks
            "5493001KJTIIGC8Y1RÄ2", // disallow non-ASCII
    };
    // @formatter:on

    @Test
    void testGetInstance() {
        assertSame(VALIDATOR, LEIValidator.getInstance());
    }

    @Test
    void testInvalid() {
        for (final String f : invalidFormat) {
            assertFalse(VALIDATOR.isValid(f), f);
            assertNull(VALIDATOR.validate(f), f);
        }
    }

    @Test
    void testValid() {
        for (final String f : validFormat) {
            assertTrue(VALIDATOR.isValid(f), f);
            assertEquals(f, VALIDATOR.validate(f), f);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { " 5493001KJTIIGC8Y1R12", "5493001KJTIIGC8Y1R12 ", " 5493001KJTIIGC8Y1R12 " })
    void testValidWithSurroundingWhitespace(final String code) {
        // The underlying CodeValidator trims the input.
        assertTrue(VALIDATOR.isValid(code), code);
        assertEquals("5493001KJTIIGC8Y1R12", VALIDATOR.validate(code), code);
    }

}
