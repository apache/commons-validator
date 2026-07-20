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
 * EAN-13 Check Digit Test.
 */
class EAN13CheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = EAN13CheckDigit.EAN13_CHECK_DIGIT;
        valid = new String[] { "9780072129519", "9780764558313", "4025515373438", "0095673400332" };
    }

    /**
     * An EAN-13 code is exactly thirteen digits. Prepending a zero to a valid code lands on a position weighted by the
     * routine so the leading digit contributes nothing, leaving the modulus unchanged and the over-length code valid.
     */
    @ParameterizedTest
    @ValueSource(strings = { "09780072129519", "00095673400332" })
    void testOverLengthRejected(final String code) {
        assertFalse(routine.isValid(code), "Should fail (not thirteen digits): " + code);
    }

}
