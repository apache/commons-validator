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
 * Test Case for ByteValidator.
 */
public class ByteValidatorTest extends AbstractNumberValidatorTest {

    private static final Byte BYTE_MIN_VAL = Byte.valueOf(Byte.MIN_VALUE);
    private static final Byte BYTE_MAX_VAL = Byte.valueOf(Byte.MAX_VALUE);
    private static final String BYTE_MAX = "127";
    private static final String BYTE_MAX_0 = "127.99999999999999999999999"; // force double rounding
    private static final String BYTE_MAX_1 = "128";
    private static final String BYTE_MIN = "-128";
    private static final String BYTE_MIN_0 = "-128.99999999999999999999999"; // force double rounding";
    private static final String BYTE_MIN_1 = "-129";

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();

        validator = new ByteValidator(false, 0);
        strictValidator = new ByteValidator();

        testPattern = "#,###";

        // testValidateMinMax()
        max = Byte.valueOf(Byte.MAX_VALUE);
        maxPlusOne = Long.valueOf(max.longValue() + 1);
        min = Byte.valueOf(Byte.MIN_VALUE);
        minMinusOne = Long.valueOf(min.longValue() - 1);

        // testInvalidStrict()
        invalidStrict = new String[] { null, "", "X", "X12", "12X", "1X2", "1.2", BYTE_MAX_1, BYTE_MIN_1, BYTE_MAX_0, BYTE_MIN_0 };

        // testInvalidNotStrict()
        invalid = new String[] { null, "", "X", "X12", BYTE_MAX_1, BYTE_MIN_1 };

        // testValid()
        testNumber = Byte.valueOf((byte) 123);
        testZero = Byte.valueOf((byte) 0);
        validStrict = new String[] { "0", "123", ",123", BYTE_MAX, BYTE_MIN };
        validStrictCompare = new Number[] { testZero, testNumber, testNumber, BYTE_MAX_VAL, BYTE_MIN_VAL };
        valid = new String[] { "0", "123", ",123", ",123.5", "123X", BYTE_MAX, BYTE_MIN, BYTE_MAX_0, BYTE_MIN_0 };
        validCompare = new Number[] { testZero, testNumber, testNumber, testNumber, testNumber, BYTE_MAX_VAL, BYTE_MIN_VAL, BYTE_MAX_VAL, BYTE_MIN_VAL };

        testStringUS = ",123";
        testStringDE = ".123";

        // Localized Pattern test
        localeValue = testStringDE;
        localePattern = "#.###";
        testLocale = Locale.GERMANY;
        localeExpected = testNumber;

    }

    /**
     * Test Byte Range/Min/Max
     */
    @Test
    public void testByteRangeMinMax() {
        final ByteValidator validator = (ByteValidator) strictValidator;
        final Byte number9 = validator.validate("9", "#");
        final Byte number10 = validator.validate("10", "#");
        final Byte number11 = validator.validate("11", "#");
        final Byte number19 = validator.validate("19", "#");
        final Byte number20 = validator.validate("20", "#");
        final Byte number21 = validator.validate("21", "#");
        final byte min = (byte) 10;
        final byte max = (byte) 20;

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
     * Test ByteValidator validate Methods
     */
    @Test
    public void testByteValidatorMethods() {
        final Locale locale = Locale.GERMAN;
        final String pattern = "0,00";
        final String patternVal = "1,23";
        final String germanPatternVal = "1.23";
        final String localeVal = ".123";
        final String defaultVal = ",123";
        final String xxxx = "XXXX";
        final Byte expected = Byte.valueOf((byte) 123);
        assertEquals(expected, ByteValidator.getInstance().validate(defaultVal), "validate(A) default");
        assertEquals(expected, ByteValidator.getInstance().validate(localeVal, locale), "validate(A) locale ");
        assertEquals(expected, ByteValidator.getInstance().validate(patternVal, pattern), "validate(A) pattern");
        assertEquals(expected, ByteValidator.getInstance().validate(germanPatternVal, pattern, Locale.GERMAN), "validate(A) both");

        assertTrue(ByteValidator.getInstance().isValid(defaultVal), "isValid(A) default");
        assertTrue(ByteValidator.getInstance().isValid(localeVal, locale), "isValid(A) locale ");
        assertTrue(ByteValidator.getInstance().isValid(patternVal, pattern), "isValid(A) pattern");
        assertTrue(ByteValidator.getInstance().isValid(germanPatternVal, pattern, Locale.GERMAN), "isValid(A) both");

        assertNull(ByteValidator.getInstance().validate(xxxx), "validate(B) default");
        assertNull(ByteValidator.getInstance().validate(xxxx, locale), "validate(B) locale ");
        assertNull(ByteValidator.getInstance().validate(xxxx, pattern), "validate(B) pattern");
        assertNull(ByteValidator.getInstance().validate(patternVal, pattern, Locale.GERMAN), "validate(B) both");

        assertFalse(ByteValidator.getInstance().isValid(xxxx), "isValid(B) default");
        assertFalse(ByteValidator.getInstance().isValid(xxxx, locale), "isValid(B) locale ");
        assertFalse(ByteValidator.getInstance().isValid(xxxx, pattern), "isValid(B) pattern");
        assertFalse(ByteValidator.getInstance().isValid(patternVal, pattern, Locale.GERMAN), "isValid(B) both");
    }
}
