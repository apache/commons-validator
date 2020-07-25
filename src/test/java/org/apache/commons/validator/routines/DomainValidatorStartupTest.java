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

import org.apache.commons.validator.routines.DomainValidator.ArrayType;

import junit.framework.TestCase;

/**
 * Startup Tests for the DomainValidator.
 *
 * @version $Revision$
 */
public class DomainValidatorStartupTest extends TestCase {

    private DomainValidator validator;

    @Override
    public void setUp() {
        validator = DomainValidator.getInstance();
        DomainValidator.clearTLDOverrides(); // N.B. this clears the inUse flag, allowing overrides
    }


    public void testUpdateBaseArrays() {
        try {
            DomainValidator.updateTLDOverride(ArrayType.COUNTRY_CODE_RO, new String[]{"com"});
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        try {
            DomainValidator.updateTLDOverride(ArrayType.GENERIC_RO, new String[]{"com"});
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        try {
            DomainValidator.updateTLDOverride(ArrayType.INFRASTRUCTURE_RO, new String[]{"com"});
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        try {
            DomainValidator.updateTLDOverride(ArrayType.LOCAL_RO, new String[]{"com"});
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // expected
        }
    }

    public void testUpdateCountryCode() {
        assertFalse(validator.isValidCountryCodeTld("com")); // cannot be valid
        DomainValidator.updateTLDOverride(ArrayType.COUNTRY_CODE_PLUS, new String[]{"com"});
        assertTrue(validator.isValidCountryCodeTld("com")); // it is now!
        DomainValidator.updateTLDOverride(ArrayType.COUNTRY_CODE_MINUS, new String[]{"com"});
        assertFalse(validator.isValidCountryCodeTld("com")); // show that minus overrides the rest

        assertTrue(validator.isValidCountryCodeTld("ch"));
        DomainValidator.updateTLDOverride(ArrayType.COUNTRY_CODE_MINUS, new String[]{"ch"});
        assertFalse(validator.isValidCountryCodeTld("ch"));
        DomainValidator.updateTLDOverride(ArrayType.COUNTRY_CODE_MINUS, new String[]{"xx"});
        assertTrue(validator.isValidCountryCodeTld("ch"));
    }

    public void testUpdateGeneric() {
        assertFalse(validator.isValidGenericTld("ch")); // cannot be valid
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_PLUS, new String[]{"ch"});
        assertTrue(validator.isValidGenericTld("ch")); // it is now!
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_MINUS, new String[]{"ch"});
        assertFalse(validator.isValidGenericTld("ch")); // show that minus overrides the rest

        assertTrue(validator.isValidGenericTld("com"));
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_MINUS, new String[]{"com"});
        assertFalse(validator.isValidGenericTld("com"));
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_MINUS, new String[]{"xx"}); // change the minus list
        assertTrue(validator.isValidGenericTld("com"));
    }

    public void testVALIDATOR_412() {
        assertFalse(validator.isValidGenericTld("local"));
        assertFalse(validator.isValid("abc.local"));
        assertFalse(validator.isValidGenericTld("pvt"));
        assertFalse(validator.isValid("abc.pvt"));
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_PLUS, new String[]{"local", "pvt"});
        assertTrue(validator.isValidGenericTld("local"));
        assertTrue(validator.isValid("abc.local"));
        assertTrue(validator.isValidGenericTld("pvt"));
        assertTrue(validator.isValid("abc.pvt"));
    }

    public void testCannotUpdate() {
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_PLUS, new String[]{"ch"}); // OK
        DomainValidator dv = DomainValidator.getInstance();
        assertNotNull(dv);
        try {
            DomainValidator.updateTLDOverride(ArrayType.GENERIC_PLUS, new String[]{"ch"});
            fail("Expected IllegalStateException");
        } catch (IllegalStateException ise) {
            // expected
        }
    }
}
