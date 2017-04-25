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

import junit.framework.TestCase;

/**
 * ISINValidator Test Case.
 *
 * @since 1.7
 */
public class ISINValidatorTest extends TestCase {

    private static final ISINValidator VALIDATOR_TRUE = ISINValidator.getInstance(true);
    
    private static final ISINValidator VALIDATOR_FALSE = ISINValidator.getInstance(false);

    private final String[] validFormat = new String[] {
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
            "XS0000000009",
            };

    private final String[] invalidFormat = new String[] {
            null,
            "",                        // empty
            "   ",                     // empty
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
    private final String[] invalidFormatTrue = new String[] {
            "AA0000000006", // Invalid country code            
            };

    public ISINValidatorTest(String name) {
        super(name);
    }

    public void testIsValidTrue() {
        for(String f : validFormat) {
            assertTrue(f, VALIDATOR_TRUE.isValid(f));            
        }
    }

    public void testInvalidTrue() {
        for(String f : invalidFormat) {
            assertFalse(f, VALIDATOR_TRUE.isValid(f));            
        }
        for(String f : invalidFormatTrue) {
            assertFalse(f, VALIDATOR_TRUE.isValid(f));            
        }
    }

    public void testIsValidFalse() {
        for(String f : validFormat) {
            assertTrue(f, VALIDATOR_FALSE.isValid(f));            
        }
    }

    public void testInvalidFalse() {
        for(String f : invalidFormat) {
            assertFalse(f, VALIDATOR_FALSE.isValid(f));            
        }
    }

}
