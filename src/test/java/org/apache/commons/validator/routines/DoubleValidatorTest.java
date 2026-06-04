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

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

/**
 * Tests {@link DoubleValidator}.
 */
class DoubleValidatorTest extends AbstractNumberValidatorTest {

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
     * Test DoubleValidator.validate(String) with Double special values. NumberFormat.parseObject("NaN") succeeds and returns Double.NaN, but "Infinity" and
     * "-Infinity" cause a parse error and return null.
     */
    @Test
    @EnabledForJreRange(min = JRE.JAVA_11)
    void testDoubleValidateSpecialValuesJava11Plus() {
        final DoubleValidator validator = DoubleValidator.getInstance();
        // Double.NaN -> "NaN": NumberFormat parses "NaN" successfully
        final Double nanResult = validator.validate(Double.toString(Double.NaN));
        assertTrue(Double.isNaN(nanResult), "validate(\"NaN\") should return Double.NaN");
        assertTrue(validator.isValid(Double.toString(Double.NaN)), "isValid(\"NaN\") should be true");
        // Double.POSITIVE_INFINITY -> "Infinity": NumberFormat cannot parse "Infinity"
        assertNull(validator.validate(Double.toString(Double.POSITIVE_INFINITY)));
        assertFalse(validator.isValid(Double.toString(Double.POSITIVE_INFINITY)));
        // Double.NEGATIVE_INFINITY -> "-Infinity": NumberFormat cannot parse "-Infinity"
        assertNull(validator.validate(Double.toString(Double.NEGATIVE_INFINITY)));
        assertFalse(validator.isValid(Double.toString(Double.NEGATIVE_INFINITY)));
    }

    /**
     * Test DoubleValidator.validate(String) with Double special values. NumberFormat.parseObject("NaN") succeeds and returns Double.NaN, but "Infinity" and
     * "-Infinity" cause a parse error and return null.
     */
    @Test
    void testDoubleValidateSpecialValuesJava8() {
        final DoubleValidator validator = DoubleValidator.getInstance();
        // Double.NaN -> "NaN": NumberFormat parses "NaN" successfully
        final Double nanResult = validator.validate(Double.toString(Double.NaN));
        assertNull(nanResult);
        assertFalse(validator.isValid(Double.toString(Double.NaN)), "isValid(\"NaN\") should be true");
        // Double.POSITIVE_INFINITY -> "Infinity": NumberFormat cannot parse "Infinity"
        assertNull(validator.validate(Double.toString(Double.POSITIVE_INFINITY)));
        assertFalse(validator.isValid(Double.toString(Double.POSITIVE_INFINITY)));
        // Double.NEGATIVE_INFINITY -> "-Infinity": NumberFormat cannot parse "-Infinity"
        assertNull(validator.validate(Double.toString(Double.NEGATIVE_INFINITY)));
        assertFalse(validator.isValid(Double.toString(Double.NEGATIVE_INFINITY)));
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
}
