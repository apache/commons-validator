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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.DomainValidator.ArrayType;
import org.bitstrings.test.junit.runner.ClassLoaderPerTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Startup Tests for the DomainValidator.
 */
@RunWith(ClassLoaderPerTestRunner.class)
public class DomainValidatorStartupTest {

    @Test
    public void testUpdateBaseArrayCC() {
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> DomainValidator.updateTLDOverride(ArrayType.COUNTRY_CODE_RO, "com"));
        assertThat(thrown.getMessage(), is(equalTo("Cannot update the table: COUNTRY_CODE_RO")));
    }

    @Test
    public void testUpdateBaseArrayGeneric() {
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> DomainValidator.updateTLDOverride(ArrayType.GENERIC_RO, "com"));
        assertThat(thrown.getMessage(), is(equalTo("Cannot update the table: GENERIC_RO")));
    }

    @Test
    public void testUpdateBaseArrayInfra() {
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> DomainValidator.updateTLDOverride(ArrayType.INFRASTRUCTURE_RO, "com"));
        assertThat(thrown.getMessage(), is(equalTo("Cannot update the table: INFRASTRUCTURE_RO")));
    }

    @Test
    public void testUpdateBaseArrayLocal() {
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> DomainValidator.updateTLDOverride(ArrayType.LOCAL_RO, "com"));
        assertThat(thrown.getMessage(), is(equalTo("Cannot update the table: LOCAL_RO")));
    }

    @Test
    public void testUpdateCountryCode1a() {
        final DomainValidator validator = DomainValidator.getInstance();
        assertFalse(validator.isValidCountryCodeTld("com")); // cannot be valid
    }

    @Test
    public void testUpdateCountryCode1b() {
        DomainValidator.updateTLDOverride(ArrayType.COUNTRY_CODE_PLUS, "com");
        final DomainValidator validator = DomainValidator.getInstance();
        assertTrue(validator.isValidCountryCodeTld("com")); // it is now!
    }

    @Test
    public void testUpdateCountryCode2() {
        DomainValidator.updateTLDOverride(ArrayType.COUNTRY_CODE_PLUS, "com");
        DomainValidator.updateTLDOverride(ArrayType.COUNTRY_CODE_MINUS, "com");
        final DomainValidator validator = DomainValidator.getInstance();
        assertFalse(validator.isValidCountryCodeTld("com")); // show that minus overrides the rest
    }

    @Test
    public void testUpdateCountryCode3a() { // show ch is valid
        final DomainValidator validator = DomainValidator.getInstance();
        assertTrue(validator.isValidCountryCodeTld("ch"));
    }

    @Test
    public void testUpdateCountryCode3b() { // show ch can be made invalid
        DomainValidator.updateTLDOverride(ArrayType.COUNTRY_CODE_MINUS, "ch");
        final DomainValidator validator = DomainValidator.getInstance();
        assertFalse(validator.isValidCountryCodeTld("ch"));
    }

    @Test
    public void testUpdateCountryCode3c() { // show ch can be made valid again by replacing the CC array
        DomainValidator.updateTLDOverride(ArrayType.COUNTRY_CODE_MINUS, "ch");
        DomainValidator.updateTLDOverride(ArrayType.COUNTRY_CODE_MINUS, "xx");
        final DomainValidator validator = DomainValidator.getInstance();
        assertTrue(validator.isValidCountryCodeTld("ch"));
    }

    @Test
    public void testUpdateGeneric1() {
        final DomainValidator validator = DomainValidator.getInstance();
        assertFalse(validator.isValidGenericTld("ch")); // cannot be valid
    }

    @Test
    public void testUpdateGeneric2() {
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_PLUS, "ch");
        final DomainValidator validator = DomainValidator.getInstance();
        assertTrue(validator.isValidGenericTld("ch")); // it is now!
    }

    @Test
    public void testUpdateGeneric3() {
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_PLUS, "ch");
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_MINUS, "ch");
        final DomainValidator validator = DomainValidator.getInstance();
        assertFalse(validator.isValidGenericTld("ch")); // show that minus overrides the rest
        assertTrue(validator.isValidGenericTld("com"));
    }

    @Test
    public void testUpdateGeneric4() {
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_PLUS, "ch");
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_MINUS, "ch");
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_MINUS, "com");
        final DomainValidator validator = DomainValidator.getInstance();
        assertFalse(validator.isValidGenericTld("com"));
    }

    @Test
    public void testUpdateGeneric5() {
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_PLUS, "ch");
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_MINUS, "ch");
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_MINUS, "com");
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_MINUS, "xx"); // change the minus list
        final DomainValidator validator = DomainValidator.getInstance();
        assertTrue(validator.isValidGenericTld("com"));
    }

    @Test
    public void testVALIDATOR_412a() {
        final DomainValidator validator = DomainValidator.getInstance();
        assertFalse(validator.isValidGenericTld("local"));
        assertFalse(validator.isValid("abc.local"));
        assertFalse(validator.isValidGenericTld("pvt"));
        assertFalse(validator.isValid("abc.pvt"));
    }

    @Test
    public void testVALIDATOR_412b() {
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_PLUS, "local", "pvt");
        final DomainValidator validator = DomainValidator.getInstance();
        assertTrue(validator.isValidGenericTld("local"));
        assertTrue(validator.isValid("abc.local"));
        assertTrue(validator.isValidGenericTld("pvt"));
        assertTrue(validator.isValid("abc.pvt"));
    }

    @Test
    public void testVALIDATOR_412c() {
        final DomainValidator validator = DomainValidator.getInstance(true);
        assertFalse(validator.isValidLocalTld("local"));
        assertFalse(validator.isValid("abc.local"));
        assertFalse(validator.isValidLocalTld("pvt"));
        assertFalse(validator.isValid("abc.pvt"));
    }

    @Test
    public void testVALIDATOR_412d() {
        DomainValidator.updateTLDOverride(ArrayType.LOCAL_PLUS, "local", "pvt");
        final DomainValidator validator = DomainValidator.getInstance(true);
        assertTrue(validator.isValidLocalTld("local"));
        assertTrue(validator.isValidLocalTld("pvt"));
        assertTrue(validator.isValid("abc.local"));
        assertTrue(validator.isValid("abc.pvt"));
    }

    @Test
    public void testCannotUpdate() {
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_PLUS, "ch"); // OK
        final DomainValidator dv = DomainValidator.getInstance();
        assertNotNull(dv);
        try {
            DomainValidator.updateTLDOverride(ArrayType.GENERIC_PLUS, "ch");
            fail("Expected IllegalStateException");
        } catch (final IllegalStateException ise) {
            // expected
        }
    }

    @Test
    public void testInstanceOverride() { // Show that the instance picks up static values
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_PLUS, "gp");
        DomainValidator.updateTLDOverride(ArrayType.GENERIC_MINUS, "com");
        DomainValidator.updateTLDOverride(ArrayType.COUNTRY_CODE_PLUS, "cp");
        DomainValidator.updateTLDOverride(ArrayType.COUNTRY_CODE_MINUS, "ch");
        DomainValidator validator = DomainValidator.getInstance(false);
        assertTrue(validator.isValidGenericTld("gp"));
        assertFalse(validator.isValidGenericTld("com"));
        assertTrue(validator.isValidCountryCodeTld("cp"));
        assertFalse(validator.isValidCountryCodeTld("ch"));

        // show we can override them for a new instance
        final List<DomainValidator.Item> items = new ArrayList<>();
        items.add(new DomainValidator.Item(ArrayType.GENERIC_MINUS, "" ));
        items.add(new DomainValidator.Item(ArrayType.COUNTRY_CODE_MINUS, ""));
        validator = DomainValidator.getInstance(false, items);
        assertTrue(validator.isValidGenericTld("gp"));
        assertTrue(validator.isValidGenericTld("com")); // Should be true again
        assertTrue(validator.isValidCountryCodeTld("cp"));
        assertTrue(validator.isValidCountryCodeTld("ch")); // Should be true again

        // Show the class overrides are unaffected
        validator = DomainValidator.getInstance(false);
        assertTrue(validator.isValidGenericTld("gp"));
        assertFalse(validator.isValidGenericTld("com"));
        assertTrue(validator.isValidCountryCodeTld("cp"));
        assertFalse(validator.isValidCountryCodeTld("ch"));
    }
}
