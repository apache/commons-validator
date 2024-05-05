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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for LongValidator.
 */
public class LongValidatorTest extends AbstractNumberValidatorTest {

    private static final Long LONG_MIN_VAL = Long.valueOf(Long.MIN_VALUE);
    private static final Long LONG_MAX_VAL = Long.valueOf(Long.MAX_VALUE);
    private static final String LONG_MAX = "9223372036854775807";
    private static final String LONG_MAX_0 = "9223372036854775807.99999999999999999999999"; // force double rounding
    private static final String LONG_MAX_1 = "9223372036854775808";
    private static final String LONG_MIN = "-9223372036854775808";
    private static final String LONG_MIN_0 = "-9223372036854775808.99999999999999999999999"; // force double rounding
    private static final String LONG_MIN_1 = "-9223372036854775809";

    private static final String NINES = "9999999999999999999999999999999999999";

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();

        validator = new LongValidator(false, 0);
        strictValidator = new LongValidator();

        testPattern = "#,###";

        // testValidateMinMax()
        max = null;
        maxPlusOne = null;
        min = null;
        minMinusOne = null;

        // testInvalidStrict()
        invalidStrict = new String[] { null, "", "X", "X12", "12X", "1X2", "1.2", LONG_MAX_1, LONG_MIN_1, NINES };

        // testInvalidNotStrict()
        invalid = new String[] { null, "", "X", "X12", "", LONG_MAX_1, LONG_MIN_1, NINES };

        // testValid()
        testNumber = Long.valueOf(1234);
        testZero = Long.valueOf(0);
        validStrict = new String[] { "0", "1234", "1,234", LONG_MAX, LONG_MIN };
        validStrictCompare = new Number[] { testZero, testNumber, testNumber, LONG_MAX_VAL, LONG_MIN_VAL };
        valid = new String[] { "0", "1234", "1,234", "1,234.5", "1234X", LONG_MAX, LONG_MIN, LONG_MAX_0, LONG_MIN_0 };
        validCompare = new Number[] { testZero, testNumber, testNumber, testNumber, testNumber, LONG_MAX_VAL, LONG_MIN_VAL, LONG_MAX_VAL, LONG_MIN_VAL };

        testStringUS = "1,234";
        testStringDE = "1.234";

        // Localized Pattern test
        localeValue = testStringDE;
        localePattern = "#.###";
        testLocale = Locale.GERMANY;
        localeExpected = testNumber;

    }

    /**
     * Test Long Range/Min/Max
     */
    @Test
    public void testLongRangeMinMax() {
        final LongValidator validator = (LongValidator) strictValidator;
        final Long number9 = validator.validate("9", "#");
        final Long number10 = validator.validate("10", "#");
        final Long number11 = validator.validate("11", "#");
        final Long number19 = validator.validate("19", "#");
        final Long number20 = validator.validate("20", "#");
        final Long number21 = validator.validate("21", "#");

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
     * Test LongValidator validate Methods
     */
    @Test
    public void testLongValidatorMethods() {
        final Locale locale = Locale.GERMAN;
        final String pattern = "0,00,00";
        final String patternVal = "1,23,45";
        final String germanPatternVal = "1.23.45";
        final String localeVal = "12.345";
        final String defaultVal = "12,345";
        final String xxxx = "XXXX";
        final Long expected = Long.valueOf(12345);
        assertEquals(expected, LongValidator.getInstance().validate(defaultVal), "validate(A) default");
        assertEquals(expected, LongValidator.getInstance().validate(localeVal, locale), "validate(A) locale");
        assertEquals(expected, LongValidator.getInstance().validate(patternVal, pattern), "validate(A) pattern");
        assertEquals(expected, LongValidator.getInstance().validate(germanPatternVal, pattern, Locale.GERMAN), "validate(A) both");

        assertTrue(LongValidator.getInstance().isValid(defaultVal), "isValid(A) default");
        assertTrue(LongValidator.getInstance().isValid(localeVal, locale), "isValid(A) locale");
        assertTrue(LongValidator.getInstance().isValid(patternVal, pattern), "isValid(A) pattern");
        assertTrue(LongValidator.getInstance().isValid(germanPatternVal, pattern, Locale.GERMAN), "isValid(A) both");

        assertNull(LongValidator.getInstance().validate(xxxx), "validate(B) default");
        assertNull(LongValidator.getInstance().validate(xxxx, locale), "validate(B) locale");
        assertNull(LongValidator.getInstance().validate(xxxx, pattern), "validate(B) pattern");
        assertNull(LongValidator.getInstance().validate(patternVal, pattern, Locale.GERMAN), "validate(B) both");

        assertFalse(LongValidator.getInstance().isValid(xxxx), "isValid(B) default");
        assertFalse(LongValidator.getInstance().isValid(xxxx, locale), "isValid(B) locale");
        assertFalse(LongValidator.getInstance().isValid(xxxx, pattern), "isValid(B) pattern");
        assertFalse(LongValidator.getInstance().isValid(patternVal, pattern, Locale.GERMAN), "isValid(B) both");
    }
}
