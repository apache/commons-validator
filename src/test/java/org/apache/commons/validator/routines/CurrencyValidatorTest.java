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
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.DefaultLocale;

/**
 * Test Case for CurrencyValidator.
 */
class CurrencyValidatorTest {

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
    void testFormatType() {
        assertEquals(1, CurrencyValidator.getInstance().getFormatType(), "Format Type A");
        assertEquals(AbstractNumberValidator.CURRENCY_FORMAT, CurrencyValidator.getInstance().getFormatType(), "Format Type B");
    }

    /**
     * Test Invalid integer (non decimal) currency values
     */
    @Test
    void testIntegerInvalid() {
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
    @DefaultLocale("en-GB")
    void testIntegerValid() {
        final CurrencyValidator validator = new CurrencyValidator();
        final BigDecimal expected = new BigDecimal("1234.00");
        final BigDecimal negative = new BigDecimal("-1234.00");
        // Generate the exected strings, as these vary between JVMs
        final String ukPlus = NumberFormat.getCurrencyInstance(Locale.UK).format(1234);
        final String ukMinus = NumberFormat.getCurrencyInstance(Locale.UK).format(-1234);
        final String usPlus = NumberFormat.getCurrencyInstance(Locale.US).format(1234);
        final String usMinus = NumberFormat.getCurrencyInstance(Locale.US).format(-1234);

        assertEquals(expected, validator.validate(ukPlus), "Default locale");

        assertEquals(expected, validator.validate(ukPlus, Locale.UK), "UK locale");
        assertEquals(negative, validator.validate(ukMinus, Locale.UK), "UK negative");

        assertEquals(expected, validator.validate(usPlus, Locale.US), "US locale");
        assertEquals(negative, validator.validate(usMinus, Locale.US), "US negative");
    }

    /**
     * Test Invalid currency values
     */
    @Test
    void testInvalid() {
        final BigDecimalValidator validator = CurrencyValidator.getInstance();

        final String ukPlus = NumberFormat.getCurrencyInstance(Locale.UK).format(1234.56);
        final String usPlus = NumberFormat.getCurrencyInstance(Locale.US).format(1234.56);
        final String ukMinus = NumberFormat.getCurrencyInstance(Locale.UK).format(-1234.56);
        final String usMinus = NumberFormat.getCurrencyInstance(Locale.US).format(-1234.56);

        // Invalid Missing
        assertFalse(validator.isValid(null), "isValid() Null Value");
        assertFalse(validator.isValid(""), "isValid() Empty Value");
        assertNull(validator.validate(null), "validate() Null Value");
        assertNull(validator.validate(""), "validate() Empty Value");

        // Invalid UK
        assertFalse(validator.isValid(usPlus, Locale.UK), "UK wrong symbol: " + usPlus);
        if (ukMinus.startsWith("-")) {
            assertFalse(validator.isValid("(" + ukPound + "1,234.56)", Locale.UK), "UK wrong negative: " + ukMinus);
        } else {
            assertFalse(validator.isValid("-" + ukPound + "1,234.56", Locale.UK), "UK wrong negative: " + ukMinus);
        }

        // Invalid US
        assertFalse(validator.isValid(ukPlus, Locale.US), "US wrong symbol: " + ukPlus);
        if (usMinus.startsWith("-")) {
            assertFalse(validator.isValid("(" + usDollar + "1,234.56)", Locale.US), "UK wrong negative: " + usMinus);
        } else {
            assertFalse(validator.isValid("-" + usDollar + "1,234.56", Locale.US), "UK wrong negative: " + usMinus);
        }
    }

    /**
     * Test currency values with a pattern
     */
    @Test
    @DefaultLocale("en-GB")
    void testPattern() {
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
    }

    /**
     * Test Valid currency values
     */
    @Test
    @DefaultLocale("en-GB")
    void testValid() {
        final BigDecimalValidator validator = CurrencyValidator.getInstance();
        final BigDecimal expected = new BigDecimal("1234.56");
        final BigDecimal negative = new BigDecimal("-1234.56");
        final BigDecimal noDecimal = new BigDecimal("1234.00");
        final BigDecimal oneDecimal = new BigDecimal("1234.50");

        // Generate the exected strings, as these vary between JVMs
        final String ukPlus = NumberFormat.getCurrencyInstance(Locale.UK).format(1234.56);
        final String ukPlus0Decimal = NumberFormat.getCurrencyInstance(Locale.UK).format(1234);
        final String ukPlus1Decimal = NumberFormat.getCurrencyInstance(Locale.UK).format(1234.5);
        // Note that NumberFormat may perform rounding up or truncation, so we cheat
        final String ukPlus3Decimal = NumberFormat.getCurrencyInstance(Locale.UK).format(1234.56) + "7";
        final String ukMinus = NumberFormat.getCurrencyInstance(Locale.UK).format(-1234.56);
        final String usPlus = NumberFormat.getCurrencyInstance(Locale.US).format(1234.56);
        final String usPlus0Decimal = NumberFormat.getCurrencyInstance(Locale.US).format(1234);
        final String usPlus1Decimal = NumberFormat.getCurrencyInstance(Locale.US).format(1234.5);
        // Note that NumberFormat may perform rounding up or truncation, so we cheat
        final String usPlus3Decimal = NumberFormat.getCurrencyInstance(Locale.US).format(1234.56) + "7";
        final String usMinus = NumberFormat.getCurrencyInstance(Locale.US).format(-1234.56);

        assertEquals(expected, validator.validate(ukPlus), "Default locale");

        assertEquals(expected, validator.validate(ukPlus, Locale.UK), "UK locale");
        assertEquals(negative, validator.validate(ukMinus, Locale.UK), "UK negative");
        assertEquals(noDecimal, validator.validate(ukPlus0Decimal, Locale.UK), "UK no decimal");
        assertEquals(oneDecimal, validator.validate(ukPlus1Decimal, Locale.UK), "UK 1 decimal");
        assertEquals(expected, validator.validate(ukPlus3Decimal, Locale.UK), "UK 3 decimal: " + ukPlus3Decimal);
        // TODO: The following expects the value to be truncated, rather than rounded up - is that correct?
        assertEquals(expected, validator.validate(ukPlus3Decimal, Locale.UK), "UK 3 decimal: " + ukPlus3Decimal);
        assertEquals(expected, validator.validate("1,234.56", Locale.UK), "UK no symbol");

        assertEquals(expected, validator.validate(usPlus, Locale.US), "US locale");
        assertEquals(negative, validator.validate(usMinus, Locale.US), "US negative");
        assertEquals(noDecimal, validator.validate(usPlus0Decimal, Locale.US), "US no decimal");
        assertEquals(oneDecimal, validator.validate(usPlus1Decimal, Locale.US), "US 1 decimal");
        // TODO: The following expects the value to be truncated, rather than rounded up - is that correct?
        assertEquals(expected, validator.validate(usPlus3Decimal, Locale.US), "US 3 decimal: " + usPlus3Decimal);
        assertEquals(expected, validator.validate("1,234.56", Locale.US), "US no symbol");
    }
}
