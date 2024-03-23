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
package org.apache.commons.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test the CreditCardValidator class.
 *
 * @deprecated this test can be removed when the deprecated class is removed
 */
@Deprecated
public class CreditCardValidatorTest {

    /**
     * Test a custom implementation of CreditCardType.
     */
    private static class DinersClub implements CreditCardValidator.CreditCardType {
        private static final String PREFIX = "300,301,302,303,304,305,";

        @Override
        public boolean matches(final String card) {
            final String prefix = card.substring(0, 3) + ",";
            return PREFIX.contains(prefix) && card.length() == 14;
        }
    }

    private static final String VALID_VISA = "4417123456789113";
    private static final String VALID_SHORT_VISA = "4222222222222";
    private static final String VALID_AMEX = "378282246310005";
    private static final String VALID_MASTERCARD = "5105105105105100";
    private static final String VALID_DISCOVER = "6011000990139424";

    private static final String VALID_DINERS = "30569309025904";

    @Test
    public void testAddAllowedCardType() {
        final CreditCardValidator ccv = new CreditCardValidator(CreditCardValidator.NONE);
        // Turned off all cards so even valid numbers should fail
        assertFalse(ccv.isValid(VALID_VISA));
        assertFalse(ccv.isValid(VALID_AMEX));
        assertFalse(ccv.isValid(VALID_MASTERCARD));
        assertFalse(ccv.isValid(VALID_DISCOVER));

        // test our custom type
        ccv.addAllowedCardType(new DinersClub());
        assertTrue(ccv.isValid(VALID_DINERS));
    }

    @Test
    public void testIsValid() {
        CreditCardValidator ccv = new CreditCardValidator();

        assertFalse(ccv.isValid(null));
        assertFalse(ccv.isValid(""));
        assertFalse(ccv.isValid("123456789012")); // too short
        assertFalse(ccv.isValid("12345678901234567890")); // too long
        assertFalse(ccv.isValid("4417123456789112"));
        assertFalse(ccv.isValid("4417q23456w89113"));
        assertTrue(ccv.isValid(VALID_VISA));
        assertTrue(ccv.isValid(VALID_SHORT_VISA));
        assertTrue(ccv.isValid(VALID_AMEX));
        assertTrue(ccv.isValid(VALID_MASTERCARD));
        assertTrue(ccv.isValid(VALID_DISCOVER));

        // disallow Visa so it should fail even with good number
        ccv = new CreditCardValidator(CreditCardValidator.AMEX);
        assertFalse(ccv.isValid("4417123456789113"));
    }

}
