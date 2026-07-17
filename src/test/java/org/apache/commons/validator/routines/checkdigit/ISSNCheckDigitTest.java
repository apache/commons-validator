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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * ISSN Check Digit Test.
 */
class ISSNCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = ISSNCheckDigit.ISSN_CHECK_DIGIT;
        valid = new String[] { "03178471", "1050124X", "15626865", "10637710", "17487188", "02642875", "17500095", "11881534", "19111479", "19111460",
                "00016772", "1365201X", };
        invalid = new String[] { "03178472", // wrong check
                "1050-124X", // format char
                " 1365201X", "1365201X ", " 1365201X ", };
        missingMessage = "Code is missing";
        zeroSum = "00000000";
    }

    /**
     * An ISSN is exactly eight characters. Appending a character to a valid ISSN pushes the extra character to a
     * position weighted zero (the routine weights each position by {@code 9 - leftPos}), so the modulus was unaffected
     * and every over-length code validated. The first ten append a digit to a valid ISSN; the last is longer still.
     */
    @ParameterizedTest
    @ValueSource(strings = { "031784710", "031784711", "031784712", "031784713", "031784714", "031784715", "031784716", "031784717", "031784718", "031784719",
            "0317847100", })
    void testOverLengthRejected(final String code) {
        assertFalse(routine.isValid(code), "Should fail (not eight characters): " + code);
    }

}
