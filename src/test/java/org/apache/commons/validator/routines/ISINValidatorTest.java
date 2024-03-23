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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link ISINValidator}.
 */
public class ISINValidatorTest {

    private static final ISINValidator VALIDATOR_TRUE = ISINValidator.getInstance(true);

    private static final ISINValidator VALIDATOR_FALSE = ISINValidator.getInstance(false);

    // @formatter:off
    private final String[] validFormat = {
            "US0378331005",
            "BMG8571G1096",
            "AU0000XVGZA3",
            "GB0002634946",
            "FR0004026250",
            "DK0009763344",
            "GB00B03MLX29",
            "US7562071065",
            "US56845T3059",
            "LU0327357389",
            "US032511BN64",
            "INE112A01023",
            "EZ0000000003", // Invented; for use in ISINValidator
            "EU000A0VUCF1",
            "XA2053913989",
            "XB0000000008",
            "XC0009698371",
            "XD0000000006",
            "XF0000000004",
            "QS0000000008",
            "QT0000000007",
            "QW0000000002",
            "XS0000000009", };

    private final String[] invalidFormat = { null, "", // empty
            "   ", // empty
            "US037833100O", // proper check digit is '5', see above
            "BMG8571G109D", // proper check digit is '6', see above
            "AU0000XVGZAD", // proper check digit is '3', see above
            "GB000263494I", // proper check digit is '6', see above
            "FR000402625C", // proper check digit is '0', see above
            "DK000976334H", // proper check digit is '4', see above
            "3133EHHF3", // see VALIDATOR-422 Valid check-digit, but not valid ISIN
            "AU0000xvgzA3", // disallow lower case NSIN
            "gb0002634946", // disallow lower case ISO code
    };

    // Invalid codes if country checking is enabled
    private final String[] invalidFormatTrue = { "AB0000000006", // Invalid country code
    };

    @Test
    public void testInvalidFalse() {
        for (final String f : invalidFormat) {
            assertFalse(VALIDATOR_FALSE.isValid(f), f);
        }
    }

    @Test
    public void testInvalidTrue() {
        for (final String f : invalidFormat) {
            assertFalse(VALIDATOR_TRUE.isValid(f), f);
        }
        for (final String f : invalidFormatTrue) {
            assertFalse(VALIDATOR_TRUE.isValid(f), f);
        }
    }

    @Test
    public void testIsValidFalse() {
        for (final String f : validFormat) {
            assertTrue(VALIDATOR_FALSE.isValid(f), f);
        }
    }

    @Test
    public void testIsValidTrue() {
        for (final String f : validFormat) {
            assertTrue(VALIDATOR_TRUE.isValid(f), f);
        }
    }

}
