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

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * ISIN Check Digit Test.
 */
public class ISINCheckDigitTest extends AbstractCheckDigitTest {

    private static final String[] INVALID_CHECK_DIGITS = { "US037833100O", // proper check digit is '5', see above
            "BMG8571G109D", // proper check digit is '6', see above
            "AU0000XVGZAD", // proper check digit is '3', see above
            "GB000263494I", // proper check digit is '6', see above
            "FR000402625C", // proper check digit is '0', see above
            "DK000976334H", // proper check digit is '4', see above
    };

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = ISINCheckDigit.ISIN_CHECK_DIGIT;
        valid = new String[] { "US0378331005", "BMG8571G1096", "AU0000XVGZA3", "GB0002634946", "FR0004026250", "3133EHHF3", // see VALIDATOR-422 Valid
                                                                                                                            // check-digit, but not valid ISIN
                "DK0009763344", "dk0009763344", // TODO lowercase is currently accepted, but is this valid?
                "AU0000xvgza3", // lowercase NSIN
                "EZ0000000003", // Invented; for use in ISINValidatorTest
                "XS0000000009", // ditto
                "AA0000000006", // ditto
        };
        invalid = new String[] { "0378#3100" };
    }

    @Test
    public void testValidator345() {
        for (final String invalidCheckDigit : INVALID_CHECK_DIGITS) {
            assertFalse(routine.isValid(invalidCheckDigit), "Should fail: " + invalidCheckDigit);
        }
    }
}
