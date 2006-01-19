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
 * Test Case for LongValidator.
 * 
 * @version $Revision$ $Date$
 */
public class LongValidatorTest extends BaseNumberValidatorTest {

    /**
     * Main
     * @param args arguments
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(LongValidatorTest.class);
    }

    /**
     * Constructor
     * @param name test name
     */
    public LongValidatorTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();

        validator       = new LongValidator(false);
        strictValidator = new LongValidator(true);

        testPattern = "#,###";

        // testValidateMinMax()
        max =  null;
        maxPlusOne = null;
        min = null;
        minMinusOne = null;

        // testInvalidStrict()
        invalidStrict = new String[] {null, "", "X", "X12", "12X", "1X2", "1.2"};

        // testInvalidNotStrict()
        invalid       = new String[] {null, "", "X", "X12"};

        // testValid()
        testNumber    = new Long(1234);
        testZero      = new Long(0);
        validStrict          = new String[] {"0", "1234", "1,234"};
        validStrictCompare   = new Number[] {testZero, testNumber, testNumber};
        valid                = new String[] {"0", "1234", "1,234", "1,234.5", "1234X"};
        validCompare         = new Number[] {testZero, testNumber, testNumber, testNumber, testNumber};

        testStringUS = "1,234";
        testStringDE = "1.234";

        // Localized Pattern test
        localeValue = testStringDE;
        localePattern = "#.###";
        testLocale    = Locale.GERMANY;
        localeExpected = testNumber;

    }

    /**
     * Test LongValidator validate Methods
     */
    public void testLongValidatorMethods() {
        Locale locale     = Locale.GERMAN;
        String pattern    = "0,00,00";
        String patternVal = "1,23,45";
        String localeVal  = "12.345";
        String defaultVal = "12,345";
        String XXXX    = "XXXX"; 
        Long expected = new Long(12345);
        assertEquals("validate(A) default", expected, LongValidator.getInstance().validate(defaultVal));
        assertEquals("validate(A) locale ", expected, LongValidator.getInstance().validate(localeVal, locale));
        assertEquals("validate(A) pattern", expected, LongValidator.getInstance().validate(patternVal, pattern));

        assertTrue("isValid(A) default", LongValidator.getInstance().isValid(defaultVal));
        assertTrue("isValid(A) locale ", LongValidator.getInstance().isValid(localeVal, locale));
        assertTrue("isValid(A) pattern", LongValidator.getInstance().isValid(patternVal, pattern));

        assertNull("validate(B) default", LongValidator.getInstance().validate(XXXX));
        assertNull("validate(B) locale ", LongValidator.getInstance().validate(XXXX, locale));
        assertNull("validate(B) pattern", LongValidator.getInstance().validate(XXXX, pattern));

        assertFalse("isValid(B) default", LongValidator.getInstance().isValid(XXXX));
        assertFalse("isValid(B) locale ", LongValidator.getInstance().isValid(XXXX, locale));
        assertFalse("isValid(B) pattern", LongValidator.getInstance().isValid(XXXX, pattern));
    }

    /**
     * Test Long Range/Min/Max
     */
    public void testLongRangeMinMax() {
        LongValidator validator = (LongValidator)strictValidator;
        Long number9  = validator.validate("9", "#");
        Long number10 = validator.validate("10", "#");
        Long number11 = validator.validate("11", "#");
        Long number19 = validator.validate("19", "#");
        Long number20 = validator.validate("20", "#");
        Long number21 = validator.validate("21", "#");

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
