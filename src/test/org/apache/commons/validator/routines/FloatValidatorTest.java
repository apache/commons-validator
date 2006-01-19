/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright 2006 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
 * Test Case for FloatValidator.
 * 
 * @version $Revision$ $Date$
 */
public class FloatValidatorTest extends BaseNumberValidatorTest {

    /**
     * Main
     * @param args arguments
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(FloatValidatorTest.class);
    }

    /**
     * Constructor
     * @param name test name
     */
    public FloatValidatorTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();

        validator       = new FloatValidator(false);
        strictValidator = new FloatValidator(true);

        testPattern = "#,###.#";

        // testValidateMinMax()
        max = new Float(Float.MAX_VALUE);
        maxPlusOne = new Double(max.doubleValue() * 10);
        min = new Float(Float.MAX_VALUE * -1);
        minMinusOne = new Double(min.doubleValue() * 10);

        // testInvalidStrict()
        invalidStrict = new String[] {null, "", "X", "X12", "12X", "1X2"};

        // testInvalidNotStrict()
        invalid       = new String[] {null, "", "X", "X12"};

        // testValid()
        testNumber    = new Float(1234.5);
        testZero      = new Float(0);
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
     * Test FloatValidator validate Methods
     */
    public void testFloatValidatorMethods() {
        Locale locale     = Locale.GERMAN;
        String pattern    = "0,00,00";
        String patternVal = "1,23,45";
        String localeVal  = "12.345";
        String defaultVal = "12,345";
        String XXXX    = "XXXX"; 
        Float expected = new Float(12345);
        assertEquals("validate(A) default", expected, FloatValidator.getInstance().validate(defaultVal));
        assertEquals("validate(A) locale ", expected, FloatValidator.getInstance().validate(localeVal, locale));
        assertEquals("validate(A) pattern", expected, FloatValidator.getInstance().validate(patternVal, pattern));

        assertTrue("isValid(A) default", FloatValidator.getInstance().isValid(defaultVal));
        assertTrue("isValid(A) locale ", FloatValidator.getInstance().isValid(localeVal, locale));
        assertTrue("isValid(A) pattern", FloatValidator.getInstance().isValid(patternVal, pattern));

        assertNull("validate(B) default", FloatValidator.getInstance().validate(XXXX));
        assertNull("validate(B) locale ", FloatValidator.getInstance().validate(XXXX, locale));
        assertNull("validate(B) pattern", FloatValidator.getInstance().validate(XXXX, pattern));

        assertFalse("isValid(B) default", FloatValidator.getInstance().isValid(XXXX));
        assertFalse("isValid(B) locale ", FloatValidator.getInstance().isValid(XXXX, locale));
        assertFalse("isValid(B) pattern", FloatValidator.getInstance().isValid(XXXX, pattern));
    }

    /**
     * Test Float Range/Min/Max
     */
    public void testFloatRangeMinMax() {
        FloatValidator validator = (FloatValidator)strictValidator;
        Float number9  = validator.validate("9", "#");
        Float number10 = validator.validate("10", "#");
        Float number11 = validator.validate("11", "#");
        Float number19 = validator.validate("19", "#");
        Float number20 = validator.validate("20", "#");
        Float number21 = validator.validate("21", "#");

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
