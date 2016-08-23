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

public class ISINValidatorTest extends TestCase {
    
    private static final ISINValidator VALIDATOR = ISINValidator.getInstance();

    private final String[] validISIN = new String[] {
        "US0378331005",
        "DE0005140008",
        "GB00B03MLX29",
        "XS1454967487"
    };
    
    private final String[] invalidISIN = new String[] {
        "1234",          // too short
        "US03783310051", // too long
        "us-783310051", // invalid characters
        "YY0005140008",  // invalid country code
        "GB10B03MLX29",  // invalid checksum
    };
    
    public ISINValidatorTest(String name) {
        super(name);
    }
    
    /**
     * Test isValid() ISIN codes
     */
    public void testIsValidISSN() {
        for(String s : validISIN) {
            assertTrue(s, VALIDATOR.isValid(s));            
        }
    }
    
    /**
     * Test isValid() ISIN codes
     */
    public void testInvalidISSN() {
        for(String s : invalidISIN) {
            assertFalse(s, VALIDATOR.isValid(s));            
        }
    }

    /**
     * Test null values
     */
    public void testNull() {
        assertFalse("isValid", VALIDATOR.isValid(null));
    }
    
}
