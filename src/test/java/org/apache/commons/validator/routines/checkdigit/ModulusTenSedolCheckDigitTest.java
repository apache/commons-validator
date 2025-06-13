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
import org.junit.jupiter.api.Test;

/**
 * ModulusTenCheckDigit SEDOL Test.
 */
class ModulusTenSedolCheckDigitTest extends AbstractCheckDigitTest {

    private static final String[] INVALID_CHECK_DIGITS = { "026349E", // proper check digit is '4', see above
            "087061C", // proper check digit is '2', see above
            "B06LQ9H", // proper check digit is '7', see above
            "343757F", // proper check digit is '5', see above
            "B07LF5F", // proper check digit is '5', see above
    };

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = new ModulusTenCheckDigit(new int[] { 1, 3, 1, 7, 3, 9, 1 });
        valid = new String[] { "0263494", "0870612", "B06LQ97", "3437575", "B07LF55", };
        invalid = new String[] { "123#567" };
        zeroSum = "0000000";
    }

    @Test
    void testValidator346() {
        for (final String invalidCheckDigit : INVALID_CHECK_DIGITS) {
            assertFalse(routine.isValid(invalidCheckDigit), "Should fail: " + invalidCheckDigit);
        }
    }

}
