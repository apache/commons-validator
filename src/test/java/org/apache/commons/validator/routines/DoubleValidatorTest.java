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
 * Test Case for DoubleValidator.
 */
public class DoubleValidatorTest extends AbstractNumberValidatorTest {

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
    public void testDoubleRangeMinMax() {
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
     * Test DoubleValidator validate Methods
     */
    @Test
    public void testDoubleValidatorMethods() {
        final Locale locale = Locale.GERMAN;
        final String pattern = "0,00,00";
        final String patternVal = "1,23,45";
        final String germanPatternVal = "1.23.45";
        final String localeVal = "12.345";
        final String defaultVal = "12,345";
        final String xxxx = "XXXX";
        final Double expected = Double.valueOf(12345);
        assertEquals(expected, DoubleValidator.getInstance().validate(defaultVal), "validate(A) default");
        assertEquals(expected, DoubleValidator.getInstance().validate(localeVal, locale), "validate(A) locale");
        assertEquals(expected, DoubleValidator.getInstance().validate(patternVal, pattern), "validate(A) pattern");
        assertEquals(expected, DoubleValidator.getInstance().validate(germanPatternVal, pattern, Locale.GERMAN), "validate(A) both");

        assertTrue(DoubleValidator.getInstance().isValid(defaultVal), "isValid(A) default");
        assertTrue(DoubleValidator.getInstance().isValid(localeVal, locale), "isValid(A) locale");
        assertTrue(DoubleValidator.getInstance().isValid(patternVal, pattern), "isValid(A) pattern");
        assertTrue(DoubleValidator.getInstance().isValid(germanPatternVal, pattern, Locale.GERMAN), "isValid(A) both");

        assertNull(DoubleValidator.getInstance().validate(xxxx), "validate(B) default");
        assertNull(DoubleValidator.getInstance().validate(xxxx, locale), "validate(B) locale ");
        assertNull(DoubleValidator.getInstance().validate(xxxx, pattern), "validate(B) pattern");
        assertNull(DoubleValidator.getInstance().validate(patternVal, pattern, Locale.GERMAN), "validate(B) both");

        assertFalse(DoubleValidator.getInstance().isValid(xxxx), "isValid(B) default");
        assertFalse(DoubleValidator.getInstance().isValid(xxxx, locale), "isValid(B) locale");
        assertFalse(DoubleValidator.getInstance().isValid(xxxx, pattern), "isValid(B) pattern");
        assertFalse(DoubleValidator.getInstance().isValid(patternVal, pattern, Locale.GERMAN), "isValid(B) both");
    }
}
