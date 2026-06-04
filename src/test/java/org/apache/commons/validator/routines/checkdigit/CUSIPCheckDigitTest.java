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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * CUSIP Check Digit Test.
 */
class CUSIPCheckDigitTest extends AbstractCheckDigitTest {

    private static final String[] VALID = new String[] { "DUS0421C5", "037833100", "931142103", "837649128", "392690QT3", "594918104", "86770G101", "Y8295N109",
            "G8572F100", "17275R102", "EJ7125481" };

    static String[] cloneValid() {
        return VALID.clone();
    }

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = CUSIPCheckDigit.CUSIP_CHECK_DIGIT;
        valid = cloneValid();
        invalid = new String[] { "0378#3100" };
    }

    @ParameterizedTest
    @ValueSource(strings = { "DUS0421CW", "DUS0421CN", "DUS0421CE" })
    void testValidator336InvalidCheckDigits(final String invalidCheckDigit) {
        assertFalse(routine.isValid(invalidCheckDigit), "Should fail: " + invalidCheckDigit);
    }

    @ParameterizedTest
    @MethodSource("org.apache.commons.validator.routines.checkdigit.CUSIPCheckDigitTest#cloneValid")
    void testValidator336ValidCheckDigits(final String validCheckDigit) {
        assertTrue(routine.isValid(validCheckDigit), "Should fail: " + validCheckDigit);
    }
}
