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

import java.math.BigInteger;
import java.util.Locale;

/**
 * Test Case for BigIntegerValidator.
 * 
 * @version $Revision$
 */
public class BigIntegerValidatorTest extends AbstractNumberValidatorTest {

    /**
     * Constructor
     * @param name test name
     */
    public BigIntegerValidatorTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        validator       = new BigIntegerValidator(false, 0);
        strictValidator = new BigIntegerValidator();

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
        testNumber    = new BigInteger("1234");
        testZero      = new BigInteger("0");
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
     * Test BigIntegerValidator validate Methods
     */
    public void testBigIntegerValidatorMethods() {
        Locale locale     = Locale.GERMAN;
        String pattern    = "0,00,00";
        String patternVal = "1,23,45";
        String germanPatternVal = "1.23.45";
        String localeVal  = "12.345";
        String defaultVal = "12,345";
        String XXXX    = "XXXX"; 
        BigInteger expected = new BigInteger("12345");
        assertEquals("validate(A) default", expected, BigIntegerValidator.getInstance().validate(defaultVal));
        assertEquals("validate(A) locale ", expected, BigIntegerValidator.getInstance().validate(localeVal, locale));
        assertEquals("validate(A) pattern", expected, BigIntegerValidator.getInstance().validate(patternVal, pattern));
        assertEquals("validate(A) both",    expected, BigIntegerValidator.getInstance().validate(germanPatternVal, pattern, Locale.GERMAN));

        assertTrue("isValid(A) default", BigIntegerValidator.getInstance().isValid(defaultVal));
        assertTrue("isValid(A) locale ", BigIntegerValidator.getInstance().isValid(localeVal, locale));
        assertTrue("isValid(A) pattern", BigIntegerValidator.getInstance().isValid(patternVal, pattern));
        assertTrue("isValid(A) both",    BigIntegerValidator.getInstance().isValid(germanPatternVal, pattern, Locale.GERMAN));

        assertNull("validate(B) default", BigIntegerValidator.getInstance().validate(XXXX));
        assertNull("validate(B) locale ", BigIntegerValidator.getInstance().validate(XXXX, locale));
        assertNull("validate(B) pattern", BigIntegerValidator.getInstance().validate(XXXX, pattern));
        assertNull("validate(B) both",    BigIntegerValidator.getInstance().validate(patternVal, pattern, Locale.GERMAN));

        assertFalse("isValid(B) default", BigIntegerValidator.getInstance().isValid(XXXX));
        assertFalse("isValid(B) locale ", BigIntegerValidator.getInstance().isValid(XXXX, locale));
        assertFalse("isValid(B) pattern", BigIntegerValidator.getInstance().isValid(XXXX, pattern));
        assertFalse("isValid(B) both",    BigIntegerValidator.getInstance().isValid(patternVal, pattern, Locale.GERMAN));
    }

    /**
     * Test BigInteger Range/Min/Max
     */
    public void testBigIntegerRangeMinMax() {
        BigIntegerValidator validator = (BigIntegerValidator)strictValidator;
        BigInteger number9  = validator.validate("9", "#");
        BigInteger number10 = validator.validate("10", "#");
        BigInteger number11 = validator.validate("11", "#");
        BigInteger number19 = validator.validate("19", "#");
        BigInteger number20 = validator.validate("20", "#");
        BigInteger number21 = validator.validate("21", "#");

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
