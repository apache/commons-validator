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
package org.apache.commons.validator.routines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.DefaultLocale;

/**
 * Test Case for PercentValidator.
 */
class PercentValidatorTest {

    protected PercentValidator validator;
    private Locale originalLocale;

    @BeforeEach
    protected void setUp() {
        originalLocale = Locale.getDefault();
        validator = new PercentValidator();
    }

    /**
     * Tear down
     */
    @AfterEach
    protected void tearDown() {
        Locale.setDefault(originalLocale);
        validator = null;
    }

    /**
     * Test Format Type
     */
    @Test
    void testFormatType() {
        assertEquals(2, PercentValidator.getInstance().getFormatType(), "Format Type A");
        assertEquals(AbstractNumberValidator.PERCENT_FORMAT, PercentValidator.getInstance().getFormatType(), "Format Type B");
    }

    /**
     * Test Invalid percentage values
     */
    @Test
    void testInvalid() {
        final BigDecimalValidator validator = PercentValidator.getInstance();

        // Invalid Missing
        assertFalse(validator.isValid(null), "isValid() Null Value");
        assertFalse(validator.isValid(""), "isValid() Empty Value");
        assertNull(validator.validate(null), "validate() Null Value");
        assertNull(validator.validate(""), "validate() Empty Value");

        // Invalid UK
        assertFalse(validator.isValid("12@", Locale.UK), "UK wrong symbol"); // ???
        assertFalse(validator.isValid("(12%)", Locale.UK), "UK wrong negative");

        // Invalid US - can't find a Locale with different symbols!
        assertFalse(validator.isValid("12@", Locale.US), "US wrong symbol"); // ???
        assertFalse(validator.isValid("(12%)", Locale.US), "US wrong negative");
    }

    /**
     * Test Valid percentage values
     */
    @Test
    @DefaultLocale("en-GB")
    void testValid() {
        final BigDecimalValidator validator = PercentValidator.getInstance();
        final BigDecimal expected = new BigDecimal("0.12");
        final BigDecimal negative = new BigDecimal("-0.12");
        final BigDecimal hundred = new BigDecimal("1.00");

        assertEquals(expected, validator.validate("12%"), "Default locale");
        assertEquals(negative, validator.validate("-12%"), "Default negative");

        // Invalid UK
        assertEquals(expected, validator.validate("12%", Locale.UK), "UK locale");
        assertEquals(negative, validator.validate("-12%", Locale.UK), "UK negative");
        assertEquals(expected, validator.validate("12", Locale.UK), "UK No symbol");

        // Invalid US - can't find a Locale with different symbols!
        assertEquals(expected, validator.validate("12%", Locale.US), "US locale");
        assertEquals(negative, validator.validate("-12%", Locale.US), "US negative");
        assertEquals(expected, validator.validate("12", Locale.US), "US No symbol");

        assertEquals(hundred, validator.validate("100%"), "100%");
    }

}
