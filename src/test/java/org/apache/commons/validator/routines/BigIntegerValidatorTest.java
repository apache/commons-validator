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
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link BigIntegerValidator}.
 */
class BigIntegerValidatorTest extends AbstractNumberValidatorTest {

    @BeforeEach
    protected void setUp() {
        validator = new BigIntegerValidator(false, 0);
        strictValidator = new BigIntegerValidator();
        testPattern = "#,###";
        // testValidateMinMax()
        max = null;
        maxPlusOne = null;
        min = null;
        minMinusOne = null;
        // testInvalidStrict()
        invalidStrict = new String[] { null, "", "X", "X12", "12X", "1X2", "1.2" };
        // testInvalidNotStrict()
        invalid = new String[] { null, "", "X", "X12" };
        // testValid()
        testNumber = new BigInteger("1234");
        testZero = new BigInteger("0");
        validStrict = new String[] { "0", "1234", "1,234" };
        validStrictCompare = new Number[] { testZero, testNumber, testNumber };
        valid = new String[] { "0", "1234", "1,234", "1,234.5", "1234X" };
        validCompare = new Number[] { testZero, testNumber, testNumber, testNumber, testNumber };
        testStringUS = "1,234";
        testStringDE = "1.234";
        // Localized Pattern test
        localeValue = testStringDE;
        localePattern = "#.###";
        testLocale = Locale.GERMANY;
        localeExpected = testNumber;
    }

    /**
     * Test a value larger than {@link Long#MAX_VALUE} keeps its magnitude instead of being clamped to {@link Long#MAX_VALUE}.
     */
    @Test
    void testBigIntegerAboveLongMaxValue() {
        // One past Long.MAX_VALUE, so NumberFormat parses it as a Double rather than a Long.
        final BigInteger aboveLong = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
        final String aboveLongStr = aboveLong.toString();
        final BigIntegerValidator instance = BigIntegerValidator.getInstance();
        final BigInteger resultAboveLong = instance.validate(aboveLongStr, "#");
        assertTrue(resultAboveLong.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) > 0);
        assertTrue(resultAboveLong.compareTo(BigInteger.ZERO) > 0);
        assertTrue(resultAboveLong.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0);
        assertTrue(instance.minValue(resultAboveLong, Long.MIN_VALUE));
        assertTrue(instance.minValue(resultAboveLong, Long.MAX_VALUE));
        assertFalse(instance.maxValue(resultAboveLong, Long.MIN_VALUE));
        assertFalse(instance.maxValue(resultAboveLong, Long.MAX_VALUE));
        assertFalse(instance.isInRange(resultAboveLong, Long.MIN_VALUE, Long.MAX_VALUE));
        // BigDecimalValidator already preserves the magnitude, so the two must agree
        assertEquals(BigDecimalValidator.getInstance().validate(aboveLongStr, "#").toBigInteger(), resultAboveLong);
    }

    /**
     * Test a value larger than {@link Long#MAX_VALUE} keeps its magnitude instead of being clamped to {@link Long#MAX_VALUE}.
     */
    @Test
    void testBigIntegerBelowLongMinValue() {
        // One past Long.MAX_VALUE, so NumberFormat parses it as a Double rather than a Long.
        final BigInteger belowLong = BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE);
        final String belowLongStr = belowLong.toString();
        final BigIntegerValidator instance = BigIntegerValidator.getInstance();
        final BigInteger resultBelowLong = instance.validate(belowLongStr, "#");
        assertTrue(resultBelowLong.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0);
        assertTrue(resultBelowLong.compareTo(BigInteger.ZERO) < 0);
        assertTrue(resultBelowLong.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) < 0);
        assertFalse(instance.minValue(resultBelowLong, Long.MIN_VALUE));
        assertFalse(instance.minValue(resultBelowLong, Long.MAX_VALUE));
        assertTrue(instance.maxValue(resultBelowLong, Long.MIN_VALUE));
        assertTrue(instance.maxValue(resultBelowLong, Long.MAX_VALUE));
        assertFalse(instance.isInRange(resultBelowLong, Long.MIN_VALUE, Long.MAX_VALUE));
        // BigDecimalValidator already preserves the magnitude, so the two must agree
        assertEquals(BigDecimalValidator.getInstance().validate(belowLongStr, "#").toBigInteger(), resultBelowLong);
    }

    /**
     * A value carrying more significant digits than a {@code double} can hold must be converted exactly. Scaling {@link Long#MAX_VALUE} up pushes past the
     * roughly 17 significant digits a double can represent, so a result routed through a double would be rounded rather than preserved.
     */
    @Test
    void testBigIntegerExactBeyondDoublePrecision() {
        final BigInteger exact = BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.TEN).add(BigInteger.valueOf(7));
        final String exactStr = exact.toString();
        final BigIntegerValidator instance = BigIntegerValidator.getInstance();
        assertEquals(exact, instance.validate(exactStr, "#"));
        // BigInteger and BigDecimal validators must agree on the exact value
        assertEquals(BigDecimalValidator.getInstance().validate(exactStr, "#").toBigInteger(), instance.validate(exactStr, "#"));
    }

    /**
     * Test BigInteger Range/Min/Max
     */
    @Test
    void testBigIntegerRangeMinMax() {
        final BigIntegerValidator validator = (BigIntegerValidator) strictValidator;
        final BigInteger number9 = validator.validate("9", "#");
        final BigInteger number10 = validator.validate("10", "#");
        final BigInteger number11 = validator.validate("11", "#");
        final BigInteger number19 = validator.validate("19", "#");
        final BigInteger number20 = validator.validate("20", "#");
        final BigInteger number21 = validator.validate("21", "#");
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
     * Test BigIntegerValidator validate Methods
     */
    @Test
    void testBigIntegerValidatorMethods() {
        final Locale locale = Locale.GERMAN;
        final String pattern = "0,00,00";
        final String patternVal = "1,23,45";
        final String germanPatternVal = "1.23.45";
        final String localeVal = "12.345";
        final String defaultVal = "12,345";
        final String xxxx = "XXXX";
        final BigInteger expected = new BigInteger("12345");
        assertEquals(expected, BigIntegerValidator.getInstance().validate(defaultVal), "validate(A) default");
        assertEquals(expected, BigIntegerValidator.getInstance().validate(localeVal, locale), "validate(A) locale ");
        assertEquals(expected, BigIntegerValidator.getInstance().validate(patternVal, pattern), "validate(A) pattern");
        assertEquals(expected, BigIntegerValidator.getInstance().validate(germanPatternVal, pattern, Locale.GERMAN), "validate(A) both");
        assertTrue(BigIntegerValidator.getInstance().isValid(defaultVal), "isValid(A) default");
        assertTrue(BigIntegerValidator.getInstance().isValid(localeVal, locale), "isValid(A) locale ");
        assertTrue(BigIntegerValidator.getInstance().isValid(patternVal, pattern), "isValid(A) pattern");
        assertTrue(BigIntegerValidator.getInstance().isValid(germanPatternVal, pattern, Locale.GERMAN), "isValid(A) both");
        assertNull(BigIntegerValidator.getInstance().validate(xxxx), "validate(B) default");
        assertNull(BigIntegerValidator.getInstance().validate(xxxx, locale), "validate(B) locale ");
        assertNull(BigIntegerValidator.getInstance().validate(xxxx, pattern), "validate(B) pattern");
        assertNull(BigIntegerValidator.getInstance().validate(patternVal, pattern, Locale.GERMAN), "validate(B) both");
        assertFalse(BigIntegerValidator.getInstance().isValid(xxxx), "isValid(B) default");
        assertFalse(BigIntegerValidator.getInstance().isValid(xxxx, locale), "isValid(B) locale ");
        assertFalse(BigIntegerValidator.getInstance().isValid(xxxx, pattern), "isValid(B) pattern");
        assertFalse(BigIntegerValidator.getInstance().isValid(patternVal, pattern, Locale.GERMAN), "isValid(B) both");
    }

    /**
     * Test minValue() against bounds for values outside the long range, using exact BigIntegers so the comparison is not affected by double rounding.
     */
    @Test
    void testMinValueOutsideLongRange() {
        final BigIntegerValidator instance = BigIntegerValidator.getInstance();
        final BigInteger aboveMax = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
        final BigInteger belowMin = BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE);
        // aboveMax is greater than every long, so it is >= any long minimum
        assertTrue(instance.minValue(aboveMax, Long.MAX_VALUE));
        assertTrue(instance.minValue(aboveMax, Long.MIN_VALUE));
        // belowMin is smaller than every long, so it is not >= any long minimum
        assertFalse(instance.minValue(belowMin, Long.MIN_VALUE));
        assertFalse(instance.minValue(belowMin, Long.MAX_VALUE));
    }

    /**
     * The {@link Number} overloads must compare against the exact bound. A non-integer bound was converted with BigInteger.toBigInteger(), which truncates
     * towards zero, so a fractional minimum was floored (wrongly admitting a value below it) and a fractional maximum was floored too (wrongly admitting a value
     * above a negative maximum).
     */
    @Test
    void testNumberRangeFractionalBound() {
        final AbstractNumberValidator instance = BigIntegerValidator.getInstance();
        final Number five = BigInteger.valueOf(5);
        // 5 >= 5.9 is false, but flooring the bound to 5 made it pass
        assertFalse(instance.minValue(five, Double.valueOf(5.9)));
        assertFalse(instance.minValue(five, new BigDecimal("5.9")));
        // a whole-number bound is unaffected
        assertTrue(instance.minValue(five, BigInteger.valueOf(5)));

        final Number minusFive = BigInteger.valueOf(-5);
        // -5 <= -5.9 is false, but flooring the bound to -5 made it pass
        assertFalse(instance.maxValue(minusFive, Double.valueOf(-5.9)));
        assertFalse(instance.maxValue(minusFive, new BigDecimal("-5.9")));
        // -6 <= -5.9 is true
        assertTrue(instance.maxValue(BigInteger.valueOf(-6), Double.valueOf(-5.9)));
    }

    /**
     * A {@link Float} bound must be compared at its real magnitude. The bound is converted through {@link AbstractNumberValidator#toBigDecimal}, which had no
     * {@code Float} branch, so a {@code Float} fell through to {@code new BigDecimal(value.toString())}; {@code Float.toString} emits only the digits needed to
     * round-trip the float, so the bound was read at a coarser value than it actually holds and a value sitting between the two magnitudes was mis-ranged.
     */
    @Test
    void testNumberRangeFloatBound() {
        final AbstractNumberValidator instance = BigIntegerValidator.getInstance();
        final Float bound = Float.valueOf(1e20f);
        final BigInteger exact = new BigDecimal(bound.doubleValue()).toBigInteger(); // what the float really holds
        final BigInteger truncated = new BigDecimal(bound.toString()).toBigInteger(); // what Float.toString shows
        final BigInteger between = truncated.add(BigInteger.ONE); // above the printed value, below the real float
        // guard the fixture: the float prints smaller than it is
        assertTrue(between.compareTo(truncated) > 0 && between.compareTo(exact) < 0);
        assertTrue(instance.maxValue(between, bound), "value below the real float is within the maximum");
        assertFalse(instance.minValue(between, bound), "value below the real float is below the minimum");
        final BigInteger above = exact.add(BigInteger.ONE);
        assertFalse(instance.maxValue(above, bound), "value above the real float exceeds the maximum");
        assertTrue(instance.minValue(above, bound), "value above the real float meets the minimum");
    }

    /**
     * A non-finite {@link Double} bound must not be routed through {@link BigDecimal}, which cannot represent {@code NaN} or an infinity. The {@link Number}
     * overloads previously converted every bound to a {@code BigDecimal} and so threw {@code NumberFormatException} for such a bound, whereas the sibling
     * {@link BigDecimalValidator} already handled it. The behavior now matches: a {@code NaN} bound is never satisfied, and an infinity is an open bound.
     */
    @Test
    void testNumberRangeNonFiniteBound() {
        final AbstractNumberValidator instance = BigIntegerValidator.getInstance();
        final Number value = BigInteger.valueOf(100);
        // NaN bound: nothing compares against NaN
        assertFalse(instance.maxValue(value, Double.NaN));
        assertFalse(instance.minValue(value, Double.NaN));
        assertFalse(instance.isInRange(value, 0, Double.NaN));
        assertFalse(instance.isInRange(value, Double.NaN, 200));
        // POSITIVE_INFINITY as a maximum / NEGATIVE_INFINITY as a minimum are open bounds any finite value meets
        assertTrue(instance.maxValue(value, Double.POSITIVE_INFINITY));
        assertTrue(instance.minValue(value, Double.NEGATIVE_INFINITY));
        assertTrue(instance.isInRange(value, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
        // POSITIVE_INFINITY as a minimum / NEGATIVE_INFINITY as a maximum cannot be met
        assertFalse(instance.minValue(value, Double.POSITIVE_INFINITY));
        assertFalse(instance.maxValue(value, Double.NEGATIVE_INFINITY));
        final Number posInf = Double.valueOf(Double.POSITIVE_INFINITY);
        final Number negInf = Double.valueOf(Double.NEGATIVE_INFINITY);
        final Number nan = Double.valueOf(Double.NaN);
        // A non-finite value against itself: an infinity equals itself, NaN never compares equal
        assertTrue(instance.maxValue(posInf, posInf));
        assertTrue(instance.minValue(posInf, posInf));
        assertTrue(instance.maxValue(negInf, negInf));
        assertTrue(instance.minValue(negInf, negInf));
        assertFalse(instance.maxValue(nan, nan));
        assertFalse(instance.minValue(nan, nan));
        // A non-finite value against a different non-finite bound: -inf < +inf, anything with NaN fails
        assertTrue(instance.maxValue(negInf, posInf));
        assertTrue(instance.minValue(posInf, negInf));
        assertFalse(instance.maxValue(posInf, negInf));
        assertFalse(instance.minValue(negInf, posInf));
        assertFalse(instance.maxValue(posInf, nan));
        assertFalse(instance.minValue(posInf, nan));
    }

    /**
     * The {@link Number} overloads inherited from the superclass must compare the exact value, not a value narrowed to a long, for BigIntegers outside the long
     * range.
     */
    @Test
    void testNumberRangeOutsideLongRange() {
        final AbstractNumberValidator instance = BigIntegerValidator.getInstance();
        final Number min = BigInteger.valueOf(5);
        final Number max = BigInteger.valueOf(100);
        // 2^63 narrows to Long.MIN_VALUE, which the long-based comparison wrongly reports as below the range
        final Number aboveMax = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
        assertTrue(instance.minValue(aboveMax, min));
        assertFalse(instance.maxValue(aboveMax, max));
        // 2^64 + 50 narrows to 50, which the long-based comparison wrongly reports as in range
        final Number wrapsIntoRange = BigInteger.ONE.shiftLeft(Long.SIZE).add(BigInteger.valueOf(50));
        assertEquals(50L, wrapsIntoRange.longValue());
        assertFalse(instance.isInRange(wrapsIntoRange, min, max));
    }
}
