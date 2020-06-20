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

import java.util.Locale;

/**
 * Test Case for DoubleValidator.
 * 
 * @version $Revision$
 */
public class DoubleValidatorTest extends AbstractNumberValidatorTest {

    /**
     * Constructor
     * @param name test name
     */
    public DoubleValidatorTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        validator       = new DoubleValidator(false, 0);
        strictValidator = new DoubleValidator();

        testPattern = "#,###.#";

        // testValidateMinMax()
        max = null;
        maxPlusOne = null;
        min = null;
        minMinusOne = null;

        // testInvalidStrict()
        invalidStrict = new String[] {null, "", "X", "X12", "12X", "1X2"};

        // testInvalidNotStrict()
        invalid       = new String[] {null, "", "X", "X12"};

        // testValid()
        testNumber    = Double.valueOf(1234.5);
        testZero      = Double.valueOf(0);
        validStrict          = new String[] {"0", "1234.5", "1,234.5"};
        validStrictCompare   = new Number[] {testZero, testNumber, testNumber};
        valid                = new String[] {"0", "1234.5", "1,234.5", "1,234.5", "1234.5X"};
        validCompare         = new Number[] {testZero, testNumber, testNumber, testNumber, testNumber};

        testStringUS = "1,234.5";
        testStringDE = "1.234,5";

        // Localized Pattern test
        localeValue = testStringDE;
        localePattern = "#.###,#";
        testLocale    = Locale.GERMANY;
        localeExpected = testNumber;

    }

    /**
     * Test DoubleValidator validate Methods
     */
    public void testDoubleValidatorMethods() {
        Locale locale     = Locale.GERMAN;
        String pattern    = "0,00,00";
        String patternVal = "1,23,45";
        String germanPatternVal = "1.23.45";
        String localeVal  = "12.345";
        String defaultVal = "12,345";
        String XXXX    = "XXXX"; 
        Double expected = Double.valueOf(12345);
        assertEquals("validate(A) default", expected, DoubleValidator.getInstance().validate(defaultVal));
        assertEquals("validate(A) locale ", expected, DoubleValidator.getInstance().validate(localeVal, locale));
        assertEquals("validate(A) pattern", expected, DoubleValidator.getInstance().validate(patternVal, pattern));
        assertEquals("validate(A) both",    expected, DoubleValidator.getInstance().validate(germanPatternVal, pattern, Locale.GERMAN));

        assertTrue("isValid(A) default", DoubleValidator.getInstance().isValid(defaultVal));
        assertTrue("isValid(A) locale ", DoubleValidator.getInstance().isValid(localeVal, locale));
        assertTrue("isValid(A) pattern", DoubleValidator.getInstance().isValid(patternVal, pattern));
        assertTrue("isValid(A) both",    DoubleValidator.getInstance().isValid(germanPatternVal, pattern, Locale.GERMAN));

        assertNull("validate(B) default", DoubleValidator.getInstance().validate(XXXX));
        assertNull("validate(B) locale ", DoubleValidator.getInstance().validate(XXXX, locale));
        assertNull("validate(B) pattern", DoubleValidator.getInstance().validate(XXXX, pattern));
        assertNull("validate(B) both",    DoubleValidator.getInstance().validate(patternVal, pattern, Locale.GERMAN));

        assertFalse("isValid(B) default", DoubleValidator.getInstance().isValid(XXXX));
        assertFalse("isValid(B) locale ", DoubleValidator.getInstance().isValid(XXXX, locale));
        assertFalse("isValid(B) pattern", DoubleValidator.getInstance().isValid(XXXX, pattern));
        assertFalse("isValid(B) both",    DoubleValidator.getInstance().isValid(patternVal, pattern, Locale.GERMAN));
    }

    /**
     * Test Double Range/Min/Max
     */
    public void testDoubleRangeMinMax() {
        DoubleValidator validator = (DoubleValidator)strictValidator;
        Double number9  = validator.validate("9", "#");
        Double number10 = validator.validate("10", "#");
        Double number11 = validator.validate("11", "#");
        Double number19 = validator.validate("19", "#");
        Double number20 = validator.validate("20", "#");
        Double number21 = validator.validate("21", "#");

        // Test isInRange()
        assertFalse("isInRange() < min",   validator.isInRange(number9,  10, 20));
        assertTrue("isInRange() = min",    validator.isInRange(number10, 10, 20));
        assertTrue("isInRange() in range", validator.isInRange(number11, 10, 20));
        assertTrue("isInRange() = max",    validator.isInRange(number20, 10, 20));
        assertFalse("isInRange() > max",   validator.isInRange(number21, 10, 20));

        // Test minValue()
        assertFalse("minValue() < min",    validator.minValue(number9,  10));
        assertTrue("minValue() = min",     validator.minValue(number10, 10));
        assertTrue("minValue() > min",     validator.minValue(number11, 10));

        // Test minValue()
        assertTrue("maxValue() < max",     validator.maxValue(number19, 20));
        assertTrue("maxValue() = max",     validator.maxValue(number20, 20));
        assertFalse("maxValue() > max",    validator.maxValue(number21, 20));
    }
}
