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

import java.text.DecimalFormat;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for FloatValidator.
 */
class FloatValidatorTest extends AbstractNumberValidatorTest {

    @BeforeEach
    protected void setUp() {
        validator = new FloatValidator(false, 0);
        strictValidator = new FloatValidator();
        testPattern = "#,###.#";
        // testValidateMinMax()
        max = Float.valueOf(Float.MAX_VALUE);
        maxPlusOne = Double.valueOf(max.doubleValue() * 10);
        min = Float.valueOf(Float.MAX_VALUE * -1);
        minMinusOne = Double.valueOf(min.doubleValue() * 10);
        // testInvalidStrict()
        invalidStrict = new String[] { null, "", "X", "X12", "12X", "1X2" };
        // testInvalidNotStrict()
        invalid = new String[] { null, "", "X", "X12" };
        // testValid()
        testNumber = Float.valueOf(1234.5f);
        testZero = Float.valueOf(0);
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
     * Test Float Range/Min/Max
     */
    @Test
    void testFloatRangeMinMax() {
        final FloatValidator validator = (FloatValidator) strictValidator;
        final Float number9 = validator.validate("9", "#");
        final Float number10 = validator.validate("10", "#");
        final Float number11 = validator.validate("11", "#");
        final Float number19 = validator.validate("19", "#");
        final Float number20 = validator.validate("20", "#");
        final Float number21 = validator.validate("21", "#");
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
     * Test Float Range/Min/Max with Float.NEGATIVE_INFINITY and Float.POSITIVE_INFINITY.
     */
    @Test
    void testFloatRangeMinMaxInfinity() {
        final FloatValidator validator = (FloatValidator) strictValidator;
        // isInRange() with NEGATIVE_INFINITY: less than any finite min
        assertFalse(validator.isInRange(Float.NEGATIVE_INFINITY, 10, 20), "isInRange() NEGATIVE_INFINITY");
        // isInRange() with POSITIVE_INFINITY: greater than any finite max
        assertFalse(validator.isInRange(Float.POSITIVE_INFINITY, 10, 20), "isInRange() POSITIVE_INFINITY");
        // minValue() with NEGATIVE_INFINITY: less than any finite min
        assertFalse(validator.minValue(Float.NEGATIVE_INFINITY, 10), "minValue() NEGATIVE_INFINITY");
        // minValue() with POSITIVE_INFINITY: greater than any finite min
        assertTrue(validator.minValue(Float.POSITIVE_INFINITY, 10), "minValue() POSITIVE_INFINITY");
        // maxValue() with NEGATIVE_INFINITY: less than any finite max
        assertTrue(validator.maxValue(Float.NEGATIVE_INFINITY, 20), "maxValue() NEGATIVE_INFINITY");
        // maxValue() with POSITIVE_INFINITY: greater than any finite max
        assertFalse(validator.maxValue(Float.POSITIVE_INFINITY, 20), "maxValue() POSITIVE_INFINITY");
    }

    /**
     * Test Float Range/Min/Max with Float.NaN. NaN comparisons always return false per IEEE 754.
     */
    @Test
    void testFloatRangeMinMaxNaN() {
        final FloatValidator validator = (FloatValidator) strictValidator;
        // isInRange() with NaN: NaN comparisons are always false
        assertFalse(validator.isInRange(Float.NaN, 10, 20), "isInRange() NaN");
        // minValue() with NaN: NaN >= min is always false
        assertFalse(validator.minValue(Float.NaN, 10), "minValue() NaN");
        // maxValue() with NaN: NaN <= max is always false
        assertFalse(validator.maxValue(Float.NaN, 20), "maxValue() NaN");
    }

    /**
     * Test Float validation for values too small to handle. (slightly different from max/min which are the largest +ve/-ve
     */
    @Test
    void testFloatSmallestValues() {
        final String pattern = "#.#################################################################";
        final DecimalFormat fmt = new DecimalFormat(pattern);
        // Validate Smallest +ve value
        final Float smallestPositive = Float.valueOf(Float.MIN_VALUE);
        final String strSmallestPositive = fmt.format(smallestPositive);
        assertEquals(smallestPositive, FloatValidator.getInstance().validate(strSmallestPositive, pattern), "Smallest +ve");
        // Validate Smallest -ve value
        final Float smallestNegative = Float.valueOf(Float.MIN_VALUE * -1);
        final String strSmallestNegative = fmt.format(smallestNegative);
        assertEquals(smallestNegative, FloatValidator.getInstance().validate(strSmallestNegative, pattern), "Smallest -ve");
        // Validate Too Small +ve
        final Double tooSmallPositive = Double.valueOf((double) Float.MIN_VALUE / (double) 10);
        final String strTooSmallPositive = fmt.format(tooSmallPositive);
        assertFalse(FloatValidator.getInstance().isValid(strTooSmallPositive, pattern), "Too small +ve");
        // Validate Too Small -ve
        final Double tooSmallNegative = Double.valueOf(tooSmallPositive.doubleValue() * -1);
        final String strTooSmallNegative = fmt.format(tooSmallNegative);
        assertFalse(FloatValidator.getInstance().isValid(strTooSmallNegative, pattern), "Too small -ve");
    }

    /**
     * Test FloatValidator validate Methods
     */
    @Test
    void testFloatValidatorMethods() {
        final Locale locale = Locale.GERMAN;
        final String pattern = "0,00,00";
        final String patternVal = "1,23,45";
        final String localeVal = "12.345";
        final String germanPatternVal = "1.23.45";
        final String defaultVal = "12,345";
        final String xxxx = "XXXX";
        final Float expected = Float.valueOf(12345);
        final FloatValidator validator = FloatValidator.getInstance();
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
