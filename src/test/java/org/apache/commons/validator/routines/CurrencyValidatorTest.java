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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for CurrencyValidator.
 */
public class CurrencyValidatorTest {

    private static final char CURRENCY_SYMBOL = '\u00A4';

    private String usDollar;
    private String ukPound;

    @BeforeEach
    protected void setUp() {
        usDollar = new DecimalFormatSymbols(Locale.US).getCurrencySymbol();
        ukPound = new DecimalFormatSymbols(Locale.UK).getCurrencySymbol();
    }

    /**
     * Test Format Type
     */
    @Test
    public void testFormatType() {
        assertEquals(1, CurrencyValidator.getInstance().getFormatType(), "Format Type A");
        assertEquals(AbstractNumberValidator.CURRENCY_FORMAT, CurrencyValidator.getInstance().getFormatType(), "Format Type B");
    }

    /**
     * Test Invalid integer (non decimal) currency values
     */
    @Test
    public void testIntegerInvalid() {
        final CurrencyValidator validator = new CurrencyValidator(true, false);

        // Invalid UK - has decimals
        assertFalse(validator.isValid(ukPound + "1,234.56", Locale.UK), "UK positive");
        assertFalse(validator.isValid("-" + ukPound + "1,234.56", Locale.UK), "UK negative");

        // Invalid US - has decimals
        assertFalse(validator.isValid(usDollar + "1,234.56", Locale.US), "US positive");
        assertFalse(validator.isValid("(" + usDollar + "1,234.56)", Locale.US), "US negative");
    }

    /**
     * Test Valid integer (non-decimal) currency values
     */
    @Test
    public void testIntegerValid() {
        // Set the default Locale
        final Locale origDefault = Locale.getDefault();
        Locale.setDefault(Locale.UK);

        final CurrencyValidator validator = new CurrencyValidator();
        final BigDecimal expected = new BigDecimal("1234.00");
        final BigDecimal negative = new BigDecimal("-1234.00");

        assertEquals(expected, validator.validate(ukPound + "1,234"), "Default locale");

        assertEquals(expected, validator.validate(ukPound + "1,234", Locale.UK), "UK locale");
        assertEquals(negative, validator.validate("-" + ukPound + "1,234", Locale.UK), "UK negative");

        assertEquals(expected, validator.validate(usDollar + "1,234", Locale.US), "US locale");
        assertEquals(negative, validator.validate("(" + usDollar + "1,234)", Locale.US), "US negative");

        // Restore the original default
        Locale.setDefault(origDefault);
    }

    /**
     * Test Invalid currency values
     */
    @Test
    public void testInvalid() {
        final BigDecimalValidator validator = CurrencyValidator.getInstance();

        // Invalid Missing
        assertFalse(validator.isValid(null), "isValid() Null Value");
        assertFalse(validator.isValid(""), "isValid() Empty Value");
        assertNull(validator.validate(null), "validate() Null Value");
        assertNull(validator.validate(""), "validate() Empty Value");

        // Invalid UK
        assertFalse(validator.isValid(usDollar + "1,234.56", Locale.UK), "UK wrong symbol");
        assertFalse(validator.isValid("(" + ukPound + "1,234.56)", Locale.UK), "UK wrong negative");

        // Invalid US
        assertFalse(validator.isValid(ukPound + "1,234.56", Locale.US), "US wrong symbol");
        assertFalse(validator.isValid("-" + usDollar + "1,234.56", Locale.US), "US wrong negative");
    }

    /**
     * Test currency values with a pattern
     */
    @Test
    public void testPattern() {
        // Set the default Locale
        final Locale origDefault = Locale.getDefault();
        Locale.setDefault(Locale.UK);

        final BigDecimalValidator validator = CurrencyValidator.getInstance();
        final String basicPattern = CURRENCY_SYMBOL + "#,##0.000";
        final String pattern = basicPattern + ";[" + basicPattern + "]";
        final BigDecimal expected = new BigDecimal("1234.567");
        final BigDecimal negative = new BigDecimal("-1234.567");

        // Test Pattern
        assertEquals(expected, validator.validate(ukPound + "1,234.567", pattern), "default");
        assertEquals(negative, validator.validate("[" + ukPound + "1,234.567]", pattern), "negative");
        assertEquals(expected, validator.validate("1,234.567", pattern), "no symbol +ve");
        assertEquals(negative, validator.validate("[1,234.567]", pattern), "no symbol -ve");

        // Test Pattern & Locale
        assertEquals(expected, validator.validate(usDollar + "1,234.567", pattern, Locale.US), "default");
        assertEquals(negative, validator.validate("[" + usDollar + "1,234.567]", pattern, Locale.US), "negative");
        assertEquals(expected, validator.validate("1,234.567", pattern, Locale.US), "no symbol +ve");
        assertEquals(negative, validator.validate("[1,234.567]", pattern, Locale.US), "no symbol -ve");

        // invalid
        assertFalse(validator.isValid(usDollar + "1,234.567", pattern), "invalid symbol");
        assertFalse(validator.isValid(ukPound + "1,234.567", pattern, Locale.US), "invalid symbol");

        // Restore the original default
        Locale.setDefault(origDefault);
    }

    /**
     * Test Valid currency values
     */
    @Test
    public void testValid() {
        // Set the default Locale
        final Locale origDefault = Locale.getDefault();
        Locale.setDefault(Locale.UK);

        final BigDecimalValidator validator = CurrencyValidator.getInstance();
        final BigDecimal expected = new BigDecimal("1234.56");
        final BigDecimal negative = new BigDecimal("-1234.56");
        final BigDecimal noDecimal = new BigDecimal("1234.00");
        final BigDecimal oneDecimal = new BigDecimal("1234.50");

        assertEquals(expected, validator.validate(ukPound + "1,234.56"), "Default locale");

        assertEquals(expected, validator.validate(ukPound + "1,234.56", Locale.UK), "UK locale");
        assertEquals(negative, validator.validate("-" + ukPound + "1,234.56", Locale.UK), "UK negative");
        assertEquals(noDecimal, validator.validate(ukPound + "1,234", Locale.UK), "UK no decimal");
        assertEquals(oneDecimal, validator.validate(ukPound + "1,234.5", Locale.UK), "UK 1 decimal");
        assertEquals(expected, validator.validate(ukPound + "1,234.567", Locale.UK), "UK 3 decimal");
        assertEquals(expected, validator.validate("1,234.56", Locale.UK), "UK no symbol");

        assertEquals(expected, validator.validate(usDollar + "1,234.56", Locale.US), "US locale");
        assertEquals(negative, validator.validate("(" + usDollar + "1,234.56)", Locale.US), "US negative");
        assertEquals(noDecimal, validator.validate(usDollar + "1,234", Locale.US), "US no decimal");
        assertEquals(oneDecimal, validator.validate(usDollar + "1,234.5", Locale.US), "US 1 decimal");
        assertEquals(expected, validator.validate(usDollar + "1,234.567", Locale.US), "US 3 decimal");
        assertEquals(expected, validator.validate("1,234.56", Locale.US), "US no symbol");

        // Restore the original default
        Locale.setDefault(origDefault);
    }
}
