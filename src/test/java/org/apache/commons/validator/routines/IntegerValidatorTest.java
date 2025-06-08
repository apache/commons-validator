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

/**
 * Test Case for IntegerValidator.
 */
class IntegerValidatorTest extends AbstractNumberValidatorTest {

    private static final Integer INT_MIN_VAL = Integer.valueOf(Integer.MIN_VALUE);
    private static final Integer INT_MAX_VAL = Integer.valueOf(Integer.MAX_VALUE);
    private static final String INT_MAX = "2147483647";
    private static final String INT_MAX_0 = "2147483647.99999999999999999999999"; // force double rounding
    private static final String INT_MAX_1 = "2147483648";
    private static final String INT_MIN = "-2147483648";
    private static final String INT_MIN_0 = "-2147483648.99999999999999999999999"; // force double rounding";
    private static final String INT_MIN_1 = "-2147483649";

    @BeforeEach
    protected void setUp() {
        validator = new IntegerValidator(false, 0);
        strictValidator = new IntegerValidator();

        testPattern = "#,###";

        // testValidateMinMax()
        max = Integer.valueOf(Integer.MAX_VALUE);
        maxPlusOne = Long.valueOf(max.longValue() + 1);
        min = Integer.valueOf(Integer.MIN_VALUE);
        minMinusOne = Long.valueOf(min.longValue() - 1);

        // testInvalidStrict()
        invalidStrict = new String[] { null, "", "X", "X12", "12X", "1X2", "1.2", INT_MAX_1, INT_MIN_1 };

        // testInvalidNotStrict()
        invalid = new String[] { null, "", "X", "X12", INT_MAX_1, INT_MIN_1 };

        // testValid()
        testNumber = Integer.valueOf(1234);
        testZero = Integer.valueOf(0);
        validStrict = new String[] { "0", "1234", "1,234", INT_MAX, INT_MIN };
        validStrictCompare = new Number[] { testZero, testNumber, testNumber, INT_MAX_VAL, INT_MIN_VAL };
        valid = new String[] { "0", "1234", "1,234", "1,234.5", "1234X", INT_MAX, INT_MIN, INT_MAX_0, INT_MIN_0 };
        validCompare = new Number[] { testZero, testNumber, testNumber, testNumber, testNumber, INT_MAX_VAL, INT_MIN_VAL, INT_MAX_VAL, INT_MIN_VAL };

        testStringUS = "1,234";
        testStringDE = "1.234";

        // Localized Pattern test
        localeValue = testStringDE;
        localePattern = "#.###";
        testLocale = Locale.GERMANY;
        localeExpected = testNumber;
    }

    /**
     * Test Integer Range/Min/Max
     */
    @Test
    void testIntegerRangeMinMax() {
        final IntegerValidator validator = (IntegerValidator) strictValidator;
        final Integer number9 = validator.validate("9", "#");
        final Integer number10 = validator.validate("10", "#");
        final Integer number11 = validator.validate("11", "#");
        final Integer number19 = validator.validate("19", "#");
        final Integer number20 = validator.validate("20", "#");
        final Integer number21 = validator.validate("21", "#");

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
     * Test IntegerValidator validate Methods
     */
    @Test
    void testIntegerValidatorMethods() {
        final Locale locale = Locale.GERMAN;
        final String pattern = "0,00,00";
        final String patternVal = "1,23,45";
        final String germanPatternVal = "1.23.45";
        final String localeVal = "12.345";
        final String defaultVal = "12,345";
        final String xxxx = "XXXX";
        final Integer expected = Integer.valueOf(12345);
        assertEquals(expected, IntegerValidator.getInstance().validate(defaultVal), "validate(A) default");
        assertEquals(expected, IntegerValidator.getInstance().validate(localeVal, locale), "validate(A) locale");
        assertEquals(expected, IntegerValidator.getInstance().validate(patternVal, pattern), "validate(A) pattern");
        assertEquals(expected, IntegerValidator.getInstance().validate(germanPatternVal, pattern, Locale.GERMAN), "validate(A) both");

        assertTrue(IntegerValidator.getInstance().isValid(defaultVal), "isValid(A) default");
        assertTrue(IntegerValidator.getInstance().isValid(localeVal, locale), "isValid(A) locale");
        assertTrue(IntegerValidator.getInstance().isValid(patternVal, pattern), "isValid(A) pattern");
        assertTrue(IntegerValidator.getInstance().isValid(germanPatternVal, pattern, Locale.GERMAN), "isValid(A) both");

        assertNull(IntegerValidator.getInstance().validate(xxxx), "validate(B) default");
        assertNull(IntegerValidator.getInstance().validate(xxxx, locale), "validate(B) locale");
        assertNull(IntegerValidator.getInstance().validate(xxxx, pattern), "validate(B) pattern");
        assertNull(IntegerValidator.getInstance().validate(patternVal, pattern, Locale.GERMAN), "validate(B) both");

        assertFalse(IntegerValidator.getInstance().isValid(xxxx), "isValid(B) default");
        assertFalse(IntegerValidator.getInstance().isValid(xxxx, locale), "isValid(B) locale");
        assertFalse(IntegerValidator.getInstance().isValid(xxxx, pattern), "isValid(B) pattern");
        assertFalse(IntegerValidator.getInstance().isValid(patternVal, pattern, Locale.GERMAN), "isValid(B) both");
    }

    @Test
    void testMinMaxValues() {
        assertTrue(validator.isValid("2147483647"), "2147483647 is max integer");
        assertFalse(validator.isValid("2147483648"), "2147483648 > max integer");
        assertTrue(validator.isValid("-2147483648"), "-2147483648 is min integer");
        assertFalse(validator.isValid("-2147483649"), "-2147483649 < min integer");
    }
}
