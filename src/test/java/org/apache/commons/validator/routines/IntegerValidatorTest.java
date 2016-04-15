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
 * Test Case for IntegerValidator.
 * 
 * @version $Revision$
 */
public class IntegerValidatorTest extends AbstractNumberValidatorTest {

    /**
     * Constructor
     * @param name test name
     */
    public IntegerValidatorTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        validator       = new IntegerValidator(false, 0);
        strictValidator = new IntegerValidator();

        testPattern = "#,###";

        // testValidateMinMax()
        max = new Integer(Integer.MAX_VALUE);
        maxPlusOne = new Long(max.longValue() + 1);
        min = new Integer(Integer.MIN_VALUE);
        minMinusOne = new Long(min.longValue() - 1);

        // testInvalidStrict()
        invalidStrict = new String[] {null, "", "X", "X12", "12X", "1X2", "1.2"};

        // testInvalidNotStrict()
        invalid       = new String[] {null, "", "X", "X12"};

        // testValid()
        testNumber    = new Integer(1234);
        testZero      = new Integer(0);
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
     * Test IntegerValidator validate Methods
     */
    public void testIntegerValidatorMethods() {
        Locale locale     = Locale.GERMAN;
        String pattern    = "0,00,00";
        String patternVal = "1,23,45";
        String germanPatternVal = "1.23.45";
        String localeVal  = "12.345";
        String defaultVal = "12,345";
        String XXXX    = "XXXX"; 
        Integer expected = new Integer(12345);
        assertEquals("validate(A) default", expected, IntegerValidator.getInstance().validate(defaultVal));
        assertEquals("validate(A) locale ", expected, IntegerValidator.getInstance().validate(localeVal, locale));
        assertEquals("validate(A) pattern", expected, IntegerValidator.getInstance().validate(patternVal, pattern));
        assertEquals("validate(A) both",    expected, IntegerValidator.getInstance().validate(germanPatternVal, pattern, Locale.GERMAN));

        assertTrue("isValid(A) default", IntegerValidator.getInstance().isValid(defaultVal));
        assertTrue("isValid(A) locale ", IntegerValidator.getInstance().isValid(localeVal, locale));
        assertTrue("isValid(A) pattern", IntegerValidator.getInstance().isValid(patternVal, pattern));
        assertTrue("isValid(A) both",    IntegerValidator.getInstance().isValid(germanPatternVal, pattern, Locale.GERMAN));

        assertNull("validate(B) default", IntegerValidator.getInstance().validate(XXXX));
        assertNull("validate(B) locale ", IntegerValidator.getInstance().validate(XXXX, locale));
        assertNull("validate(B) pattern", IntegerValidator.getInstance().validate(XXXX, pattern));
        assertNull("validate(B) both",    IntegerValidator.getInstance().validate(patternVal, pattern, Locale.GERMAN));

        assertFalse("isValid(B) default", IntegerValidator.getInstance().isValid(XXXX));
        assertFalse("isValid(B) locale ", IntegerValidator.getInstance().isValid(XXXX, locale));
        assertFalse("isValid(B) pattern", IntegerValidator.getInstance().isValid(XXXX, pattern));
        assertFalse("isValid(B) both",    IntegerValidator.getInstance().isValid(patternVal, pattern, Locale.GERMAN));
    }

    /**
     * Test Integer Range/Min/Max
     */
    public void testIntegerRangeMinMax() {
        IntegerValidator validator = (IntegerValidator)strictValidator;
        Integer number9  = validator.validate("9", "#");
        Integer number10 = validator.validate("10", "#");
        Integer number11 = validator.validate("11", "#");
        Integer number19 = validator.validate("19", "#");
        Integer number20 = validator.validate("20", "#");
        Integer number21 = validator.validate("21", "#");

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
