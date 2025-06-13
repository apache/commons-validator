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

import org.junit.jupiter.api.BeforeEach;

/**
 * ModulusTenCheckDigit Luhn Test.
 */
class ModulusTenLuhnCheckDigitTest extends AbstractCheckDigitTest {

    private static final String VALID_VISA = "4417123456789113";
    private static final String VALID_SHORT_VISA = "4222222222222";
    private static final String VALID_AMEX = "378282246310005";
    private static final String VALID_MASTERCARD = "5105105105105100";
    private static final String VALID_DISCOVER = "6011000990139424";
    private static final String VALID_DINERS = "30569309025904";

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = new ModulusTenCheckDigit(new int[] { 1, 2 }, true, true);

        valid = new String[] { VALID_VISA, VALID_SHORT_VISA, VALID_AMEX, VALID_MASTERCARD, VALID_DISCOVER, VALID_DINERS };
    }
}
