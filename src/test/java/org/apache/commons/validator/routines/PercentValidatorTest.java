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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.DefaultLocale;

/**
 * Tests {@link PercentValidator}.
 */
class PercentValidatorTest {

    private static final char PERCENT_SYMBOL = '%';

    /** The character locales such as fr-FR use between the number and a trailing percent symbol. */
    private static final char NON_BREAKING_SPACE = '\u00A0';

    protected PercentValidator validator;
    private Locale originalLocale;

    /**
     * Locales whose percent format suffixes the symbol behind a space separator.
     *
     * @return the locales to test.
     */
    static Stream<Locale> suffixSymbolLocales() {
        return Stream.of(Locale.FRANCE, Locale.GERMANY);
    }

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
     * The {@link Number}-typed range overloads inherited through {@link BigDecimalValidator} from {@link AbstractNumberValidator} must compare the exact bound,
     * so a {@code BigInteger} or {@code BigDecimal} bound outside the long range or a fractional bound is not silently truncated.
     */
    @Test
    void testNumberRangeExactBound() {
        final AbstractNumberValidator instance = PercentValidator.getInstance();
        final Number value = new BigDecimal("100");
        // A bound above the long range must not narrow to a negative long and wrongly report 100 as above the maximum.
        final Number aboveLongMax = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
        assertTrue(instance.maxValue(value, aboveLongMax));
        assertTrue(instance.isInRange(value, BigInteger.ZERO, aboveLongMax));
        // A fractional bound is not floored: 5 >= 5.5 is false.
        assertFalse(instance.minValue(new BigDecimal("5"), new BigDecimal("5.5")));
    }

    /**
     * Test percentage values against the JVM's own format for locales that suffix the symbol behind a space separator. The symbol is optional, so its separator
     * has to be optional too. The pattern, separator and input are all derived from the locale data at run time, so the expectations hold on any JVM; on older
     * JVMs whose locale data does not use a space separated suffix symbol the test is skipped.
     */
    @ParameterizedTest
    @MethodSource("suffixSymbolLocales")
    void testSuffixSymbolLocale(final Locale locale) {
        final DecimalFormat format = (DecimalFormat) NumberFormat.getPercentInstance(locale);
        final String pattern = format.toPattern();
        final int symbolIndex = pattern.indexOf(PERCENT_SYMBOL);
        assumeTrue(symbolIndex > 0 && Character.isSpaceChar(pattern.charAt(symbolIndex - 1)),
                () -> locale + " does not use a space separated suffix symbol: " + pattern);
        final char separator = pattern.charAt(symbolIndex - 1);
        final char symbol = format.getDecimalFormatSymbols().getPercent();
        final String withSymbol = format.format(0.12);
        assumeTrue(withSymbol.endsWith(separator + Character.toString(symbol)), () -> locale + " does not format the symbol last: " + withSymbol);
        final String noSymbol = withSymbol.substring(0, withSymbol.length() - 2);

        final BigDecimalValidator instance = PercentValidator.getInstance();
        final BigDecimal expected = new BigDecimal("0.12");
        assertEquals(expected, instance.validate(withSymbol, locale), "symbol: " + locale);
        assertEquals(expected, instance.validate(noSymbol, locale), "no symbol: " + locale);
        assertNull(instance.validate(noSymbol + separator, locale), "separator without symbol: " + locale);
    }

    /**
     * Test percentage values with an explicit pattern that suffixes the symbol behind a non-breaking space, so the expectations are pinned independently of the
     * JVM's locale data. The symbol is optional, so its separator has to be optional too.
     */
    @Test
    void testSuffixSymbolPattern() {
        final BigDecimalValidator instance = PercentValidator.getInstance();
        final String pattern = "#,##0" + NON_BREAKING_SPACE + PERCENT_SYMBOL;
        final BigDecimal expected = new BigDecimal("0.12");

        assertEquals(expected, instance.validate("12" + NON_BREAKING_SPACE + PERCENT_SYMBOL, pattern, Locale.US), "symbol");
        assertEquals(expected, instance.validate("12", pattern, Locale.US), "no symbol");
        assertNull(instance.validate("12" + NON_BREAKING_SPACE, pattern, Locale.US), "separator without symbol");
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
