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

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Test Case for BigDecimalValidator.
 * 
 * @version $Revision$
 */
public class BigDecimalValidatorTest extends AbstractNumberValidatorTest {

    /**
     * Constructor
     * @param name test name
     */
    public BigDecimalValidatorTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        validator       = new BigDecimalValidator(false);
        strictValidator = new BigDecimalValidator();

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
        Locale locale           = Locale.GERMAN;
        String pattern          = "0,00,00";
        String patternVal       = "1,23,45";
        String germanPatternVal = "1.23.45";
        String localeVal        = "12.345";
        String defaultVal       = "12,345";
        String XXXX             = "XXXX"; 
        BigDecimal expected = new BigDecimal(12345);
        assertEquals("validate(A) default", expected, BigDecimalValidator.getInstance().validate(defaultVal));
        assertEquals("validate(A) locale ", expected, BigDecimalValidator.getInstance().validate(localeVal, locale));
        assertEquals("validate(A) pattern", expected, BigDecimalValidator.getInstance().validate(patternVal, pattern));
        assertEquals("validate(A) both",    expected, BigDecimalValidator.getInstance().validate(germanPatternVal, pattern, Locale.GERMAN));

        assertTrue("isValid(A) default", BigDecimalValidator.getInstance().isValid(defaultVal));
        assertTrue("isValid(A) locale ", BigDecimalValidator.getInstance().isValid(localeVal, locale));
        assertTrue("isValid(A) pattern", BigDecimalValidator.getInstance().isValid(patternVal, pattern));
        assertTrue("isValid(A) both",    BigDecimalValidator.getInstance().isValid(germanPatternVal, pattern, Locale.GERMAN));

        assertNull("validate(B) default", BigDecimalValidator.getInstance().validate(XXXX));
        assertNull("validate(B) locale ", BigDecimalValidator.getInstance().validate(XXXX, locale));
        assertNull("validate(B) pattern", BigDecimalValidator.getInstance().validate(XXXX, pattern));
        assertNull("validate(B) both",    BigDecimalValidator.getInstance().validate(patternVal, pattern, Locale.GERMAN));

        assertFalse("isValid(B) default", BigDecimalValidator.getInstance().isValid(XXXX));
        assertFalse("isValid(B) locale ", BigDecimalValidator.getInstance().isValid(XXXX, locale));
        assertFalse("isValid(B) pattern", BigDecimalValidator.getInstance().isValid(XXXX, pattern));
        assertFalse("isValid(B) both",    BigDecimalValidator.getInstance().isValid(patternVal, pattern, Locale.GERMAN));
    }

    /**
     * Test BigDecimal Range/Min/Max
     */
    public void testBigDecimalRangeMinMax() {
        BigDecimalValidator validator = new BigDecimalValidator(true, AbstractNumberValidator.STANDARD_FORMAT, true);
        BigDecimal number9  = new BigDecimal("9");
        BigDecimal number10 = new BigDecimal("10");
        BigDecimal number11 = new BigDecimal("11");
        BigDecimal number19 = new BigDecimal("19");
        BigDecimal number20 = new BigDecimal("20");
        BigDecimal number21 = new BigDecimal("21");
        
        float min = 10;
        float max = 20;

        // Test isInRange()
        assertFalse("isInRange(A) < min",   validator.isInRange(number9,  min, max));
        assertTrue("isInRange(A) = min",    validator.isInRange(number10, min, max));
        assertTrue("isInRange(A) in range", validator.isInRange(number11, min, max));
        assertTrue("isInRange(A) = max",    validator.isInRange(number20, min, max));
        assertFalse("isInRange(A) > max",   validator.isInRange(number21, min, max));

        // Test minValue()
        assertFalse("minValue(A) < min",    validator.minValue(number9,  min));
        assertTrue("minValue(A) = min",     validator.minValue(number10, min));
        assertTrue("minValue(A) > min",     validator.minValue(number11, min));

        // Test minValue()
        assertTrue("maxValue(A) < max",     validator.maxValue(number19, max));
        assertTrue("maxValue(A) = max",     validator.maxValue(number20, max));
        assertFalse("maxValue(A) > max",    validator.maxValue(number21, max));
    }
}
