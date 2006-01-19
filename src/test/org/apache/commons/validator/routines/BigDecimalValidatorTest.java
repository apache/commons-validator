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

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Test Case for BigDecimalValidator.
 * 
 * @version $Revision$ $Date$
 */
public class BigDecimalValidatorTest extends BaseNumberValidatorTest {

    /**
     * Main
     * @param args arguments
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(BigDecimalValidatorTest.class);
    }

    /**
     * Constructor
     * @param name test name
     */
    public BigDecimalValidatorTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();

        validator       = new BigDecimalValidator(false);
        strictValidator = new BigDecimalValidator(true);

        testPattern = "#,###.###";

        // testValidateMinMax()
        max = null;
        maxPlusOne = null;
        min = null;
        minMinusOne = null;

        // testInvalidStrict()
        invalidStrict = new String[] {null, "", "X", "X12", "12X", "1X2", "1.234X"};

        // testInvalidNotStrict()
        invalid       = new String[] {null, "", "X", "X12"};

        // testValid()
        testNumber    = new BigDecimal("1234.5");
        Number testNumber2 = new BigDecimal(".1");
        Number testNumber3 = new BigDecimal("12345.67899");
        testZero      = new BigDecimal("0");
        validStrict          = new String[] {"0", "1234.5", "1,234.5", ".1", "12345.678990"};
        validStrictCompare   = new Number[] {testZero, testNumber, testNumber, testNumber2, testNumber3};
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
     * Test BigDecimalValidator validate Methods
     */
    public void testBigDecimalValidatorMethods() {
        Locale locale     = Locale.GERMAN;
        String pattern    = "0,00,00";
        String patternVal = "1,23,45";
        String localeVal  = "12.345";
        String defaultVal = "12,345";
        String XXXX    = "XXXX"; 
        BigDecimal expected = new BigDecimal(12345);
        assertEquals("validate(A) default", expected, BigDecimalValidator.getInstance().validate(defaultVal));
        assertEquals("validate(A) locale ", expected, BigDecimalValidator.getInstance().validate(localeVal, locale));
        assertEquals("validate(A) pattern", expected, BigDecimalValidator.getInstance().validate(patternVal, pattern));

        assertTrue("isValid(A) default", BigDecimalValidator.getInstance().isValid(defaultVal));
        assertTrue("isValid(A) locale ", BigDecimalValidator.getInstance().isValid(localeVal, locale));
        assertTrue("isValid(A) pattern", BigDecimalValidator.getInstance().isValid(patternVal, pattern));

        assertNull("validate(B) default", BigDecimalValidator.getInstance().validate(XXXX));
        assertNull("validate(B) locale ", BigDecimalValidator.getInstance().validate(XXXX, locale));
        assertNull("validate(B) pattern", BigDecimalValidator.getInstance().validate(XXXX, pattern));

        assertFalse("isValid(B) default", BigDecimalValidator.getInstance().isValid(XXXX));
        assertFalse("isValid(B) locale ", BigDecimalValidator.getInstance().isValid(XXXX, locale));
        assertFalse("isValid(B) pattern", BigDecimalValidator.getInstance().isValid(XXXX, pattern));
    }

    /**
     * Test BigDecimal Range/Min/Max
     */
    public void testBigDecimalRangeMinMax() {
        BigDecimalValidator validator = (BigDecimalValidator)strictValidator;
        BigDecimal number9  = validator.validate("9", "#");
        BigDecimal number10 = validator.validate("10", "#");
        BigDecimal number11 = validator.validate("11", "#");
        BigDecimal number19 = validator.validate("19", "#");
        BigDecimal number20 = validator.validate("20", "#");
        BigDecimal number21 = validator.validate("21", "#");

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
