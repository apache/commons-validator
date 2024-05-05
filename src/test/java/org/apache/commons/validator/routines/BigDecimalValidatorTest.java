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

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for BigDecimalValidator.
 */
public class BigDecimalValidatorTest extends AbstractNumberValidatorTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();

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
     * Test BigDecimal Range/Min/Max
     */
    @Test
    public void testBigDecimalRangeMinMax() {
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
    public void testBigDecimalValidatorMethods() {
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
