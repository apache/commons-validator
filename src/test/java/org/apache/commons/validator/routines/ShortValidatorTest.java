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
 * Test Case for ShortValidator.
 * 
 * @version $Revision$
 */
public class ShortValidatorTest extends AbstractNumberValidatorTest {

    /**
     * Constructor
     * @param name test name
     */
    public ShortValidatorTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        validator       = new ShortValidator(false, 0);
        strictValidator = new ShortValidator();

        testPattern = "#,###";

        // testValidateMinMax()
        max = new Short(Short.MAX_VALUE);
        maxPlusOne = new Long(max.longValue() + 1);
        min = new Short(Short.MIN_VALUE);
        minMinusOne = new Long(min.longValue() - 1);

        // testInvalidStrict()
        invalidStrict = new String[] {null, "", "X", "X12", "12X", "1X2", "1.2"};

        // testInvalidNotStrict()
        invalid       = new String[] {null, "", "X", "X12"};

        // testValid()
        testNumber    = new Short((short)1234);
        testZero      = new Short((short)0);
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
     * Test ShortValidator validate Methods
     */
    public void testShortValidatorMethods() {
        Locale locale     = Locale.GERMAN;
        String pattern    = "0,00,00";
        String patternVal = "1,23,45";
        String germanPatternVal = "1.23.45";
        String localeVal  = "12.345";
        String defaultVal = "12,345";
        String XXXX    = "XXXX"; 
        Short expected = new Short((short)12345);
        assertEquals("validate(A) default", expected, ShortValidator.getInstance().validate(defaultVal));
        assertEquals("validate(A) locale ", expected, ShortValidator.getInstance().validate(localeVal, locale));
        assertEquals("validate(A) pattern", expected, ShortValidator.getInstance().validate(patternVal, pattern));
        assertEquals("validate(A) both",    expected, ShortValidator.getInstance().validate(germanPatternVal, pattern, Locale.GERMAN));

        assertTrue("isValid(A) default", ShortValidator.getInstance().isValid(defaultVal));
        assertTrue("isValid(A) locale ", ShortValidator.getInstance().isValid(localeVal, locale));
        assertTrue("isValid(A) pattern", ShortValidator.getInstance().isValid(patternVal, pattern));
        assertTrue("isValid(A) both",    ShortValidator.getInstance().isValid(germanPatternVal, pattern, Locale.GERMAN));

        assertNull("validate(B) default", ShortValidator.getInstance().validate(XXXX));
        assertNull("validate(B) locale ", ShortValidator.getInstance().validate(XXXX, locale));
        assertNull("validate(B) pattern", ShortValidator.getInstance().validate(XXXX, pattern));
        assertNull("validate(B) both",    ShortValidator.getInstance().validate(patternVal, pattern, Locale.GERMAN));

        assertFalse("isValid(B) default", ShortValidator.getInstance().isValid(XXXX));
        assertFalse("isValid(B) locale ", ShortValidator.getInstance().isValid(XXXX, locale));
        assertFalse("isValid(B) pattern", ShortValidator.getInstance().isValid(XXXX, pattern));
        assertFalse("isValid(B) both",    ShortValidator.getInstance().isValid(patternVal, pattern, Locale.GERMAN));
    }

    /**
     * Test Short Range/Min/Max
     */
    public void testShortRangeMinMax() {
        ShortValidator validator = (ShortValidator)strictValidator;
        Short number9  = validator.validate("9", "#");
        Short number10 = validator.validate("10", "#");
        Short number11 = validator.validate("11", "#");
        Short number19 = validator.validate("19", "#");
        Short number20 = validator.validate("20", "#");
        Short number21 = validator.validate("21", "#");
        short min = (short)10;
        short max = (short)20;

        // Test isInRange()
        assertFalse("isInRange() < min",   validator.isInRange(number9,  min, max));
        assertTrue("isInRange() = min",    validator.isInRange(number10, min, max));
        assertTrue("isInRange() in range", validator.isInRange(number11, min, max));
        assertTrue("isInRange() = max",    validator.isInRange(number20, min, max));
        assertFalse("isInRange() > max",   validator.isInRange(number21, min, max));

        // Test minValue()
        assertFalse("minValue() < min",    validator.minValue(number9,  min));
        assertTrue("minValue() = min",     validator.minValue(number10, min));
        assertTrue("minValue() > min",     validator.minValue(number11, min));

        // Test minValue()
        assertTrue("maxValue() < max",     validator.maxValue(number19, max));
        assertTrue("maxValue() = max",     validator.maxValue(number20, max));
        assertFalse("maxValue() > max",    validator.maxValue(number21, max));
    }
}
