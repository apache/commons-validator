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
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link BigDecimalValidator}.
 */
class BigDecimalValidatorTest extends AbstractNumberValidatorTest {

    @BeforeEach
    protected void setUp() {
        validator = new BigDecimalValidator(false);
        strictValidator = new BigDecimalValidator();

        testPattern = "#,###.###";

        // testValidateMinMax()
        max = null;
        maxPlusOne = null;
        min = null;
        minMinusOne = null;

        // testInvalidStrict()
        invalidStrict = new String[] { null, "", "X", "X12", "12X", "1X2", "1.234X" };

        // testInvalidNotStrict()
        invalid = new String[] { null, "", "X", "X12" };

        // testValid()
        testNumber = new BigDecimal("1234.5");
        final Number testNumber2 = new BigDecimal(".1");
        final Number testNumber3 = new BigDecimal("12345.67899");
        testZero = new BigDecimal("0");
        validStrict = new String[] { "0", "1234.5", "1,234.5", ".1", "12345.678990" };
        validStrictCompare = new Number[] { testZero, testNumber, testNumber, testNumber2, testNumber3 };
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
     * Tests isInRange(), minValue(), and maxValue() with BigDecimal values whose magnitude exceeds Double.MAX_VALUE or whose positive magnitude is below
     * Double.MIN_VALUE (the smallest positive non-zero double).
     *
     * <p>
     * Because the implementation converts the BigDecimal to a {@code double} via {@link BigDecimal#doubleValue()}, values beyond {@code ±Double.MAX_VALUE}
     * overflow to {@code ±Double.POSITIVE_INFINITY} / {@code Double.NEGATIVE_INFINITY}, and tiny positive values below {@code Double.MIN_VALUE} underflow to
     * {@code 0.0}. The tests document this behaviour.
     * </p>
     */
    @Test
    void testBigDecimalBeyondDoubleRange() {
        final BigDecimalValidator validator = BigDecimalValidator.getInstance();
        //
        // (1) Values above Double.MAX_VALUE (positive overflow -> POSITIVE_INFINITY via doubleValue())
        final BigDecimal aboveMaxDouble = new BigDecimal(Double.MAX_VALUE).multiply(BigDecimal.TEN);
        // aboveMaxDouble.doubleValue() == POSITIVE_INFINITY
        assertTrue(validator.minValue(aboveMaxDouble, 0), "minValue: value above Double.MAX_VALUE should be >= 0");
        assertFalse(validator.maxValue(aboveMaxDouble, Double.MAX_VALUE), "maxValue: value above Double.MAX_VALUE should not be <= Double.MAX_VALUE");
        assertFalse(validator.isInRange(aboveMaxDouble, 0, Double.MAX_VALUE), "isInRange: value above Double.MAX_VALUE should not be in [0, Double.MAX_VALUE]");
        assertTrue(validator.isInRange(aboveMaxDouble, 0, Double.POSITIVE_INFINITY),
                "isInRange: value above Double.MAX_VALUE should be in [0, POSITIVE_INFINITY]");
        //
        // (2) Values below -Double.MAX_VALUE (negative overflow -> NEGATIVE_INFINITY via doubleValue())
        final BigDecimal belowNegMaxDouble = new BigDecimal(-Double.MAX_VALUE).multiply(BigDecimal.TEN);
        // belowNegMaxDouble.doubleValue() == NEGATIVE_INFINITY
        assertFalse(validator.minValue(belowNegMaxDouble, -Double.MAX_VALUE), "minValue: value below -Double.MAX_VALUE should not be >= -Double.MAX_VALUE");
        assertTrue(validator.maxValue(belowNegMaxDouble, 0), "maxValue: value below -Double.MAX_VALUE should be <= 0");
        assertFalse(validator.isInRange(belowNegMaxDouble, -Double.MAX_VALUE, 0),
                "isInRange: value below -Double.MAX_VALUE should not be in [-Double.MAX_VALUE, 0]");
        assertTrue(validator.isInRange(belowNegMaxDouble, Double.NEGATIVE_INFINITY, 0),
                "isInRange: value below -Double.MAX_VALUE should be in [NEGATIVE_INFINITY, 0]");
        // (3) Tiny positive value below Double.MIN_VALUE (underflows to 0.0 via doubleValue())
        // Double.MIN_VALUE is the smallest positive non-zero double (~4.9e-324).
        // A BigDecimal smaller than that underflows to 0.0 when converted to double.
        final BigDecimal belowMinDouble = new BigDecimal(Double.MIN_VALUE).divide(BigDecimal.TEN);
        // belowMinDouble.doubleValue() == 0.0 (underflow)
        assertTrue(validator.minValue(belowMinDouble, 0), "minValue: value below Double.MIN_VALUE underflows to 0.0; 0.0 >= 0 is true");
        assertTrue(validator.maxValue(belowMinDouble, Double.MIN_VALUE),
                "maxValue: value below Double.MIN_VALUE underflows to 0.0; 0.0 <= Double.MIN_VALUE is true");
        assertTrue(validator.isInRange(belowMinDouble, 0, Double.MIN_VALUE),
                "isInRange: value below Double.MIN_VALUE underflows to 0.0; 0.0 is in [0, Double.MIN_VALUE]");
    }

    /**
     * Tests isInRange(), minValue(), and maxValue() with BigDecimal values that lie within Double range but cannot be represented exactly as a {@code double}.
     *
     * <p>
     * 2<sup>53</sup>&nbsp;+&nbsp;1 is the smallest positive integer a {@code double} cannot hold, so {@link BigDecimal#doubleValue()} rounds it back to
     * 2<sup>53</sup>. Comparing through {@code doubleValue()} therefore reported it as equal to (and not above) 2<sup>53</sup>, accepting a value that exceeds the
     * supplied maximum. The comparison is done against the exact BigDecimal so the rounding no longer leaks into the result.
     * </p>
     */
    @Test
    void testBigDecimalCompareWithinDoubleRange() {
        final BigDecimalValidator validator = BigDecimalValidator.getInstance();
        final BigDecimal twoPow53 = BigDecimal.valueOf(2).pow(53);
        final double bound = twoPow53.doubleValue();
        // 2^53 + 1 rounds down to 2^53 as a double
        final BigDecimal aboveBound = twoPow53.add(BigDecimal.ONE);
        assertFalse(validator.maxValue(aboveBound, bound), "maxValue: 2^53 + 1 is greater than 2^53");
        assertFalse(validator.isInRange(aboveBound, 0, bound), "isInRange: 2^53 + 1 is above [0, 2^53]");
        assertTrue(validator.minValue(aboveBound, bound), "minValue: 2^53 + 1 is greater than 2^53");
        // 2^53 + 3 rounds up to 2^53 + 4 as a double
        final BigDecimal belowBound = twoPow53.add(BigDecimal.valueOf(3));
        final double higherBound = twoPow53.add(BigDecimal.valueOf(4)).doubleValue();
        assertFalse(validator.minValue(belowBound, higherBound), "minValue: 2^53 + 3 is less than 2^53 + 4");
        assertFalse(validator.isInRange(belowBound, higherBound, higherBound), "isInRange: 2^53 + 3 is below [2^53 + 4, 2^53 + 4]");
    }

    /**
     * Test BigDecimal Range/Min/Max
     */
    @Test
    void testBigDecimalRangeMinMax() {
        final BigDecimalValidator validator = new BigDecimalValidator(true, AbstractNumberValidator.STANDARD_FORMAT, true);
        final BigDecimal number9 = new BigDecimal("9");
        final BigDecimal number10 = new BigDecimal("10");
        final BigDecimal number11 = new BigDecimal("11");
        final BigDecimal number19 = new BigDecimal("19");
        final BigDecimal number20 = new BigDecimal("20");
        final BigDecimal number21 = new BigDecimal("21");
        final float min = 10;
        final float max = 20;
        // Test isInRange()
        assertFalse(validator.isInRange(number9, min, max), "isInRange(A) < min");
        assertTrue(validator.isInRange(number10, min, max), "isInRange(A) = min");
        assertTrue(validator.isInRange(number11, min, max), "isInRange(A) in range");
        assertTrue(validator.isInRange(number20, min, max), "isInRange(A) = max");
        assertFalse(validator.isInRange(number21, min, max), "isInRange(A) > max");
        // Test minValue()
        assertFalse(validator.minValue(number9, min), "minValue(A) < min");
        assertTrue(validator.minValue(number10, min), "minValue(A) = min");
        assertTrue(validator.minValue(number11, min), "minValue(A) > min");
        // Test minValue()
        assertTrue(validator.maxValue(number19, max), "maxValue(A) < max");
        assertTrue(validator.maxValue(number20, max), "maxValue(A) = max");
        assertFalse(validator.maxValue(number21, max), "maxValue(A) > max");
    }

    /**
     * Test BigDecimalValidator validate Methods
     */
    @Test
    void testBigDecimalValidatorMethods() {
        final Locale locale = Locale.GERMAN;
        final String pattern = "0,00,00";
        final String patternVal = "1,23,45";
        final String germanPatternVal = "1.23.45";
        final String localeVal = "12.345";
        final String defaultVal = "12,345";
        final String xxxx = "XXXX";
        final BigDecimal expected = new BigDecimal(12345);
        assertEquals(expected, BigDecimalValidator.getInstance().validate(defaultVal), "validate(A) default");
        assertEquals(expected, BigDecimalValidator.getInstance().validate(localeVal, locale), "validate(A) locale ");
        assertEquals(expected, BigDecimalValidator.getInstance().validate(patternVal, pattern), "validate(A) pattern");
        assertEquals(expected, BigDecimalValidator.getInstance().validate(germanPatternVal, pattern, Locale.GERMAN), "validate(A) both");

        assertTrue(BigDecimalValidator.getInstance().isValid(defaultVal), "isValid(A) default");
        assertTrue(BigDecimalValidator.getInstance().isValid(localeVal, locale), "isValid(A) locale ");
        assertTrue(BigDecimalValidator.getInstance().isValid(patternVal, pattern), "isValid(A) pattern");
        assertTrue(BigDecimalValidator.getInstance().isValid(germanPatternVal, pattern, Locale.GERMAN), "isValid(A) both");

        assertNull(BigDecimalValidator.getInstance().validate(xxxx), "validate(B) default");
        assertNull(BigDecimalValidator.getInstance().validate(xxxx, locale), "validate(B) locale");
        assertNull(BigDecimalValidator.getInstance().validate(xxxx, pattern), "validate(B) pattern");
        assertNull(BigDecimalValidator.getInstance().validate(patternVal, pattern, Locale.GERMAN), "validate(B) both");

        assertFalse(BigDecimalValidator.getInstance().isValid(xxxx), "isValid(B) default");
        assertFalse(BigDecimalValidator.getInstance().isValid(xxxx, locale), "isValid(B) locale");
        assertFalse(BigDecimalValidator.getInstance().isValid(xxxx, pattern), "isValid(B) pattern");
        assertFalse(BigDecimalValidator.getInstance().isValid(patternVal, pattern, Locale.GERMAN), "isValid(B) both");
    }
}
