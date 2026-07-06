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
 * Tests {@link ShortValidator}.
 */
class ShortValidatorTest extends AbstractNumberValidatorTest {

    @BeforeEach
    protected void setUp() {
        validator = new ShortValidator(false, 0);
        strictValidator = new ShortValidator();

        testPattern = "#,###";

        // testValidateMinMax()
        max = Short.valueOf(Short.MAX_VALUE);
        maxPlusOne = Long.valueOf(max.longValue() + 1);
        min = Short.valueOf(Short.MIN_VALUE);
        minMinusOne = Long.valueOf(min.longValue() - 1);

        // testInvalidStrict()
        invalidStrict = new String[] { null, "", "X", "X12", "12X", "1X2", "1.2" };

        // testInvalidNotStrict()
        invalid = new String[] { null, "", "X", "X12" };

        // testValid()
        testNumber = Short.valueOf((short) 1234);
        testZero = Short.valueOf((short) 0);
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
     * The inherited {@link Number}-typed range overloads must compare the exact bound rather than one narrowed to a {@code long}.
     */
    @Test
    void testNumberRangeExactBound() {
        final AbstractNumberValidator instance = ShortValidator.getInstance();
        final Number value = Short.valueOf((short) 100);
        // A bound above the long range narrows to a negative long, wrongly reporting 100 as above the maximum.
        final Number aboveLongMax = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
        assertTrue(instance.maxValue(value, aboveLongMax));
        assertTrue(instance.isInRange(value, BigInteger.ZERO, aboveLongMax));
        // A fractional bound is not floored: 5 >= 5.5 is false.
        assertFalse(instance.minValue(Short.valueOf((short) 5), new BigDecimal("5.5")));
    }

    /**
     * A value whose fractional part is expressed through a negative exponent rather than a decimal point (for example
     * {@code "15E-1"} for 1.5) is parsed to a fractional {@code Double}, not a {@code Long}, because {@code parseIntegerOnly}
     * only stops at the decimal separator. It must be rejected like any other non-integer, matching {@link ByteValidator},
     * {@link IntegerValidator} and {@link LongValidator}, rather than being truncated towards zero.
     */
    @Test
    void testFractionalScientificNotation() {
        final ShortValidator validator = ShortValidator.getInstance();
        assertNull(validator.validate("15E-1", Locale.US), "validate(15E-1)");
        assertFalse(validator.isValid("15E-1", Locale.US), "isValid(15E-1)");
        assertNull(validator.validate("5E-1", Locale.US), "validate(5E-1)");
        assertNull(validator.validate("-15E-1", Locale.US), "validate(-15E-1)");
        assertNull(validator.validate("1E-100", Locale.US), "validate(1E-100)");
    }

    /**
     * Test Short Range/Min/Max
     */
    @Test
    void testShortRangeMinMax() {
        final ShortValidator validator = (ShortValidator) strictValidator;
        final Short number9 = validator.validate("9", "#");
        final Short number10 = validator.validate("10", "#");
        final Short number11 = validator.validate("11", "#");
        final Short number19 = validator.validate("19", "#");
        final Short number20 = validator.validate("20", "#");
        final Short number21 = validator.validate("21", "#");
        final short min = (short) 10;
        final short max = (short) 20;

        // Test isInRange()
        assertFalse(validator.isInRange(number9, min, max), "isInRange() < min");
        assertTrue(validator.isInRange(number10, min, max), "isInRange() = min");
        assertTrue(validator.isInRange(number11, min, max), "isInRange() in range");
        assertTrue(validator.isInRange(number20, min, max), "isInRange() = max");
        assertFalse(validator.isInRange(number21, min, max), "isInRange() > max");

        // Test minValue()
        assertFalse(validator.minValue(number9, min), "minValue() < min");
        assertTrue(validator.minValue(number10, min), "minValue() = min");
        assertTrue(validator.minValue(number11, min), "minValue() > min");

        // Test minValue()
        assertTrue(validator.maxValue(number19, max), "maxValue() < max");
        assertTrue(validator.maxValue(number20, max), "maxValue() = max");
        assertFalse(validator.maxValue(number21, max), "maxValue() > max");
    }

    /**
     * Test ShortValidator validate Methods
     */
    @Test
    void testShortValidatorMethods() {
        final Locale locale = Locale.GERMAN;
        final String pattern = "0,00,00";
        final String patternVal = "1,23,45";
        final String germanPatternVal = "1.23.45";
        final String localeVal = "12.345";
        final String defaultVal = "12,345";
        final String xxxx = "XXXX";
        final Short expected = Short.valueOf((short) 12345);
        assertEquals(expected, ShortValidator.getInstance().validate(defaultVal), "validate(A) default");
        assertEquals(expected, ShortValidator.getInstance().validate(localeVal, locale), "validate(A) locale");
        assertEquals(expected, ShortValidator.getInstance().validate(patternVal, pattern), "validate(A) pattern");
        assertEquals(expected, ShortValidator.getInstance().validate(germanPatternVal, pattern, Locale.GERMAN), "validate(A) both");

        assertTrue(ShortValidator.getInstance().isValid(defaultVal), "isValid(A) default");
        assertTrue(ShortValidator.getInstance().isValid(localeVal, locale), "isValid(A) locale");
        assertTrue(ShortValidator.getInstance().isValid(patternVal, pattern), "isValid(A) pattern");
        assertTrue(ShortValidator.getInstance().isValid(germanPatternVal, pattern, Locale.GERMAN), "isValid(A) both");

        assertNull(ShortValidator.getInstance().validate(xxxx), "validate(B) default");
        assertNull(ShortValidator.getInstance().validate(xxxx, locale), "validate(B) locale");
        assertNull(ShortValidator.getInstance().validate(xxxx, pattern), "validate(B) pattern");
        assertNull(ShortValidator.getInstance().validate(patternVal, pattern, Locale.GERMAN), "validate(B) both");

        assertFalse(ShortValidator.getInstance().isValid(xxxx), "isValid(B) default");
        assertFalse(ShortValidator.getInstance().isValid(xxxx, locale), "isValid(B) locale");
        assertFalse(ShortValidator.getInstance().isValid(xxxx, pattern), "isValid(B) pattern");
        assertFalse(ShortValidator.getInstance().isValid(patternVal, pattern, Locale.GERMAN), "isValid(B) both");
    }
}
