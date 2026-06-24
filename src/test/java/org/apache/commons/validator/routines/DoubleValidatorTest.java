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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;

/**
 * Tests {@link DoubleValidator}.
 */
class DoubleValidatorTest extends AbstractNumberValidatorTest {

    private static final String NEGATIVE_INFINITY_STRING = Double.toString(Double.NEGATIVE_INFINITY);
    private static final String POSITIVE_INFINITY_STRING = Double.toString(Double.POSITIVE_INFINITY);
    private static final String NAN_STRING = Double.toString(Double.NaN);

    @BeforeEach
    protected void setUp() {
        validator = new DoubleValidator(false, 0);
        strictValidator = new DoubleValidator();
        testPattern = "#,###.#";
        // testValidateMinMax()
        max = null;
        maxPlusOne = null;
        min = null;
        minMinusOne = null;
        // testInvalidStrict()
        invalidStrict = new String[] { null, "", "X", "X12", "12X", "1X2" };
        // testInvalidNotStrict()
        invalid = new String[] { null, "", "X", "X12" };
        // testValid()
        testNumber = Double.valueOf(1234.5);
        testZero = Double.valueOf(0);
        validStrict = new String[] { "0", "1234.5", "1,234.5" };
        validStrictCompare = new Number[] { testZero, testNumber, testNumber };
        valid = new String[] { "0", "1234.5", "1,234.5", "1,234.5", "1234.5X" };
        validCompare = new Number[] { testZero, testNumber, testNumber, testNumber, testNumber };
        testStringUS = "1,234.5";
        testStringDE = "1.234,5";
        // Localized Pattern test
        localeValue = testStringDE;
        localePattern = "#.###,#";
        testLocale = Locale.GERMANY;
        localeExpected = testNumber;
    }

    /**
     * Test Double Range/Min/Max
     */
    @Test
    void testDoubleRangeMinMax() {
        final DoubleValidator validator = (DoubleValidator) strictValidator;
        final Double number9 = validator.validate("9", "#");
        final Double number10 = validator.validate("10", "#");
        final Double number11 = validator.validate("11", "#");
        final Double number19 = validator.validate("19", "#");
        final Double number20 = validator.validate("20", "#");
        final Double number21 = validator.validate("21", "#");
        // Test isInRange()
        assertFalse(validator.isInRange(number9, 10, 20), "isInRange() < min");
        assertTrue(validator.isInRange(number10, 10, 20), "isInRange() = min");
        assertTrue(validator.isInRange(number11, 10, 20), "isInRange() in range");
        assertTrue(validator.isInRange(number20, 10, 20), "isInRange() = max");
        assertFalse(validator.isInRange(number21, 10, 20), "isInRange() > max");
        // Test minValue()
        assertFalse(validator.minValue(number9, 10), "minValue() < min");
        assertTrue(validator.minValue(number10, 10), "minValue() = min");
        assertTrue(validator.minValue(number11, 10), "minValue() > min");
        // Test minValue()
        assertTrue(validator.maxValue(number19, 20), "maxValue() < max");
        assertTrue(validator.maxValue(number20, 20), "maxValue() = max");
        assertFalse(validator.maxValue(number21, 20), "maxValue() > max");
    }

    /**
     * Test Double Range/Min/Max with Double.NEGATIVE_INFINITY and Double.POSITIVE_INFINITY.
     */
    @Test
    void testDoubleRangeMinMaxInfinity() {
        final DoubleValidator validator = (DoubleValidator) strictValidator;
        // isInRange() with NEGATIVE_INFINITY: less than any finite min
        assertFalse(validator.isInRange(Double.NEGATIVE_INFINITY, 10, 20), "isInRange() NEGATIVE_INFINITY");
        // isInRange() with POSITIVE_INFINITY: greater than any finite max
        assertFalse(validator.isInRange(Double.POSITIVE_INFINITY, 10, 20), "isInRange() POSITIVE_INFINITY");
        // minValue() with NEGATIVE_INFINITY: less than any finite min
        assertFalse(validator.minValue(Double.NEGATIVE_INFINITY, 10), "minValue() NEGATIVE_INFINITY");
        // minValue() with POSITIVE_INFINITY: greater than any finite min
        assertTrue(validator.minValue(Double.POSITIVE_INFINITY, 10), "minValue() POSITIVE_INFINITY");
        // maxValue() with NEGATIVE_INFINITY: less than any finite max
        assertTrue(validator.maxValue(Double.NEGATIVE_INFINITY, 20), "maxValue() NEGATIVE_INFINITY");
        // maxValue() with POSITIVE_INFINITY: greater than any finite max
        assertFalse(validator.maxValue(Double.POSITIVE_INFINITY, 20), "maxValue() POSITIVE_INFINITY");
    }

    /**
     * Test Double Range/Min/Max with Double.NaN. NaN comparisons always return false per IEEE 754.
     */
    @Test
    void testDoubleRangeMinMaxNaN() {
        final DoubleValidator validator = (DoubleValidator) strictValidator;
        // isInRange() with NaN: NaN comparisons are always false
        assertFalse(validator.isInRange(Double.NaN, 10, 20), "isInRange() NaN");
        // minValue() with NaN: NaN >= min is always false
        assertFalse(validator.minValue(Double.NaN, 10), "minValue() NaN");
        // maxValue() with NaN: NaN <= max is always false
        assertFalse(validator.maxValue(Double.NaN, 20), "maxValue() NaN");
    }

    /**
     * Test the {@link Number} range checks against a bound that carries more precision than a {@code double}.
     * 2^53 is the largest integer with an exact {@code double} representation, so 2^53 + 1 cannot be narrowed
     * onto the value: a value of 2^53 is below a minimum of 2^53 + 1 and above a maximum of 2^53 - 0.5.
     */
    @Test
    void testDoubleNumberRangeExactBound() {
        final DoubleValidator validator = (DoubleValidator) strictValidator;
        final long maxExactInt = 1L << 53; // 2^53
        final Double value = Double.valueOf(maxExactInt);
        final BigInteger above = BigInteger.valueOf(maxExactInt).add(BigInteger.ONE); // 2^53 + 1
        final BigInteger below = BigInteger.valueOf(maxExactInt).subtract(BigInteger.ONE); // 2^53 - 1
        final BigDecimal justBelow = BigDecimal.valueOf(maxExactInt).subtract(BigDecimal.valueOf(0.5)); // 2^53 - 0.5
        assertFalse(validator.minValue(value, above), "minValue() bound above value");
        assertTrue(validator.minValue(value, below), "minValue() bound below value");
        assertFalse(validator.maxValue(value, justBelow), "maxValue() bound below value");
        assertTrue(validator.maxValue(value, above), "maxValue() bound above value");
        assertFalse(validator.isInRange(value, above, above.add(BigInteger.ONE)), "isInRange() value below range");
    }

    /**
     * Test DoubleValidator validate Methods
     */
    @Test
    void testDoubleValidatorMethods() {
        final Locale locale = Locale.GERMAN;
        final String pattern = "0,00,00";
        final String patternVal = "1,23,45";
        final String germanPatternVal = "1.23.45";
        final String localeVal = "12.345";
        final String defaultVal = "12,345";
        final String xxxx = "XXXX";
        final Double expected = Double.valueOf(12345);
        final DoubleValidator validator = DoubleValidator.getInstance();
        assertEquals(expected, validator.validate(defaultVal), "validate(A) default");
        assertEquals(expected, validator.validate(localeVal, locale), "validate(A) locale");
        assertEquals(expected, validator.validate(patternVal, pattern), "validate(A) pattern");
        assertEquals(expected, validator.validate(germanPatternVal, pattern, Locale.GERMAN), "validate(A) both");
        assertTrue(validator.isValid(defaultVal), "isValid(A) default");
        assertTrue(validator.isValid(localeVal, locale), "isValid(A) locale");
        assertTrue(validator.isValid(patternVal, pattern), "isValid(A) pattern");
        assertTrue(validator.isValid(germanPatternVal, pattern, Locale.GERMAN), "isValid(A) both");
        assertNull(validator.validate(xxxx), "validate(B) default");
        assertNull(validator.validate(xxxx, locale), "validate(B) locale ");
        assertNull(validator.validate(xxxx, pattern), "validate(B) pattern");
        assertNull(validator.validate(patternVal, pattern, Locale.GERMAN), "validate(B) both");
        assertFalse(validator.isValid(xxxx), "isValid(B) default");
        assertFalse(validator.isValid(xxxx, locale), "isValid(B) locale");
        assertFalse(validator.isValid(xxxx, pattern), "isValid(B) pattern");
        assertFalse(validator.isValid(patternVal, pattern, Locale.GERMAN), "isValid(B) both");
    }

    @Test
    void testSpecialSymbols() {
        final DoubleValidator validator = DoubleValidator.getInstance();
        // Double.NaN -> "NaN": NumberFormat parses "NaN" successfully
        // Double.POSITIVE_INFINITY -> "Infinity": NumberFormat cannot parse "Infinity"
        assertNull(validator.validate(POSITIVE_INFINITY_STRING));
        assertFalse(validator.isValid(POSITIVE_INFINITY_STRING));
        // Double.NEGATIVE_INFINITY -> "-Infinity": NumberFormat cannot parse "-Infinity"
        assertNull(validator.validate(NEGATIVE_INFINITY_STRING));
        assertFalse(validator.isValid(NEGATIVE_INFINITY_STRING));
        // infinity is "∞" for Locale.US.
        final String infinity = new DecimalFormatSymbols(Locale.US).getInfinity();
        assertEquals(Double.POSITIVE_INFINITY, validator.validate(infinity, Locale.US));
        assertEquals(Double.NEGATIVE_INFINITY, validator.validate("-" + infinity, Locale.US));
        assertTrue(validator.isValid(infinity, Locale.US));
        assertTrue(validator.isValid("-" + infinity, Locale.US));
        //
        // Double.POSITIVE_INFINITY -> "Infinity": NumberFormat cannot parse "Infinity"
        assertNull(validator.validate(POSITIVE_INFINITY_STRING));
        assertFalse(validator.isValid(POSITIVE_INFINITY_STRING));
        // Double.NEGATIVE_INFINITY -> "-Infinity": NumberFormat cannot parse "-Infinity"
        assertNull(validator.validate(NEGATIVE_INFINITY_STRING));
        assertFalse(validator.isValid(NEGATIVE_INFINITY_STRING));
    }

    /**
     * Test DoubleValidator.validate(String) with Double special values. NumberFormat.parseObject("NaN") succeeds and returns Double.NaN, but "Infinity" and
     * "-Infinity" cause a parse error and return null.
     */
    @Test
    @EnabledForJreRange(min = JRE.JAVA_11)
    void testSpecialSymbolsJava11Plus() {
        final DoubleValidator validator = DoubleValidator.getInstance();
        // Double.NaN -> "NaN": NumberFormat parses "NaN" successfully
        final Double nanResult = validator.validate(NAN_STRING);
        assertTrue(Double.isNaN(nanResult));
        assertTrue(validator.isValid(NAN_STRING, Locale.US));
        assertTrue(validator.isValid(NAN_STRING));
    }

    /**
     * Test DoubleValidator.validate(String) with Double special values. NumberFormat.parseObject("NaN") succeeds and returns Double.NaN, but "Infinity" and
     * "-Infinity" cause a parse error and return null.
     */
    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void testSpecialSymbolsJava8() {
        final DoubleValidator validator = DoubleValidator.getInstance();
        final Double nanResult = validator.validate(NAN_STRING);
        assertNull(nanResult);
        assertFalse(validator.isValid(NAN_STRING, Locale.US));
        assertFalse(validator.isValid(NAN_STRING));
    }
}
