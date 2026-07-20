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
 * ABA Number Check Digit Test.
 */
class ABANumberCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = ABANumberCheckDigit.ABAN_CHECK_DIGIT;
        valid = new String[] { "123456780", "123123123", "011000015", "111000038", "231381116", "121181976" };
    }

    /**
     * An ABA number is exactly nine digits. Prepending a zero to a valid code lands on a position weighted zero, so the
     * modulus was unaffected and the over-length code validated.
     */
    @ParameterizedTest
    @ValueSource(strings = { "0123456780", "00123456780", "0011000015" })
    void testOverLengthRejected(final String code) {
        assertFalse(routine.isValid(code), "Should fail (not nine digits): " + code);
    }

}
