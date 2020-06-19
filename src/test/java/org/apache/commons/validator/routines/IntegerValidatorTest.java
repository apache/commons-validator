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

    private static final Integer INT_MIN_VAL = Integer.valueOf(Integer.MIN_VALUE);
    private static final Integer INT_MAX_VAL = Integer.valueOf(Integer.MAX_VALUE);
    private static final String INT_MAX   =  "2147483647";
    private static final String INT_MAX_0 =  "2147483647.99999999999999999999999"; // force double rounding
    private static final String INT_MAX_1 =  "2147483648";
    private static final String INT_MIN   = "-2147483648";
    private static final String INT_MIN_0 = "-2147483648.99999999999999999999999"; // force double rounding";
    private static final String INT_MIN_1 = "-2147483649";

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
        max = Integer.valueOf(Integer.MAX_VALUE);
        maxPlusOne = Long.valueOf(max.longValue() + 1);
        min = Integer.valueOf(Integer.MIN_VALUE);
        minMinusOne = Long.valueOf(min.longValue() - 1);

        // testInvalidStrict()
        invalidStrict = new String[] {null, "", "X", "X12", "12X", "1X2", "1.2", INT_MAX_1, INT_MIN_1};

        // testInvalidNotStrict()
        invalid       = new String[] {null, "", "X", "X12", INT_MAX_1, INT_MIN_1};

        // testValid()
        testNumber    = Integer.valueOf(1234);
        testZero      = Integer.valueOf(0);
        validStrict          = new String[] {"0", "1234", "1,234", INT_MAX, INT_MIN};
        validStrictCompare   = new Number[] {testZero, testNumber, testNumber, INT_MAX_VAL, INT_MIN_VAL};
        valid                = new String[] {"0", "1234", "1,234", "1,234.5", "1234X", INT_MAX, INT_MIN, INT_MAX_0, INT_MIN_0};
        validCompare         = new Number[] {testZero, testNumber, testNumber, testNumber, testNumber, INT_MAX_VAL, INT_MIN_VAL, INT_MAX_VAL, INT_MIN_VAL};

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
        Integer expected = Integer.valueOf(12345);
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
    public void testMinMaxValues() {
        assertTrue("2147483647 is max integer", validator.isValid("2147483647"));
        assertFalse("2147483648 > max integer", validator.isValid("2147483648"));
        assertTrue("-2147483648 is min integer", validator.isValid("-2147483648"));
        assertFalse("-2147483649 < min integer", validator.isValid("-2147483649"));
    }
}
