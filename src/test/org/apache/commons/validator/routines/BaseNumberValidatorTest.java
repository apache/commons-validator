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

import junit.framework.TestCase;

import java.util.Locale;
import java.text.DecimalFormat;
import java.math.BigDecimal;
/**
 * Base Number Test Case.
 * 
 * @version $Revision$ $Date$
 */
public class BaseNumberValidatorTest extends TestCase {

    protected AbstractNumberValidator validator;
    protected AbstractNumberValidator strictValidator;

    protected Number max;
    protected Number maxPlusOne;
    protected Number min;
    protected Number minMinusOne;
    protected String[] invalid;
    protected String[] valid;
    protected Number[] validCompare;

    protected String[] invalidStrict;
    protected String[] validStrict;
    protected Number[] validStrictCompare;

    protected String testPattern;
    protected Number testNumber;
    protected Number testZero;
    protected String testStringUS;
    protected String testStringDE;

    protected String localeValue;
    protected String localePattern;
    protected Locale testLocale;
    protected Number localeExpected;

    /**
     * Constructor
     * @param name test name
     */
    public BaseNumberValidatorTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();

        Locale.setDefault(Locale.US);

    }

    /**
     * Tear down
     * @throws Exception
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        validator = null;
        strictValidator = null;
    }

    /**
     * Test Min/Max values allowed
     */
    public void testValidateMinMax() {
        DecimalFormat fmt = new DecimalFormat("#");
        if (max != null) {
            assertEquals("Test Max",   max, validator.validateObj(fmt.format(max)));
            assertNull("Test Max + 1",      validator.validateObj(fmt.format(maxPlusOne)));
            assertEquals("Test Min",   min, validator.validateObj(fmt.format(min)));
            assertNull("Test min - 1",      validator.validateObj(fmt.format(minMinusOne)));
        }
    }

    /**
     * Test Invalid, strict=true
     */
    public void testInvalidStrict() {
        for (int i = 0; i < invalidStrict.length; i++) {
            String text = "idx=["+i+"] value=[" + invalidStrict[i] + "]";
            assertNull("(A) "  + text, strictValidator.validateObj(invalidStrict[i], Locale.US));
            assertFalse("(B) " + text, strictValidator.isValid(invalidStrict[i], Locale.US));
            assertNull("(C) "  + text, strictValidator.validateObj(invalidStrict[i], testPattern));
            assertFalse("(D) " + text, strictValidator.isValid(invalidStrict[i], testPattern));
        }
    }

    /**
     * Test Invalid, strict=false
     */
    public void testInvalidNotStrict() {
        for (int i = 0; i < invalid.length; i++) {
            String text = "idx=["+i+"] value=[" + invalid[i] + "]";
            assertNull("(A) "  + text, validator.validateObj(invalid[i], Locale.US));
            assertFalse("(B) " + text, validator.isValid(invalid[i], Locale.US));
            assertNull("(C) "  + text, validator.validateObj(invalid[i], testPattern));
            assertFalse("(D) " + text, validator.isValid(invalid[i], testPattern));
        }
    }

    /**
     * Test Valid, strict=true
     */
    public void testValidStrict() {
        for (int i = 0; i < validStrict.length; i++) {
            String text = "idx=["+i+"] value=[" + validStrictCompare[i] + "]";
            assertEquals("(A) "  + text, validStrictCompare[i], strictValidator.validateObj(validStrict[i], Locale.US));
            assertTrue("(B) "    + text,                        strictValidator.isValid(validStrict[i], Locale.US));
            assertEquals("(C) "  + text, validStrictCompare[i], strictValidator.validateObj(validStrict[i], testPattern));
            assertTrue("(D) "    + text,                        strictValidator.isValid(validStrict[i], testPattern));
        }
    }

    /**
     * Test Valid, strict=false
     */
    public void testValidNotStrict() {
        for (int i = 0; i < valid.length; i++) {
            String text = "idx=["+i+"] value=[" + validCompare[i] + "]";
            assertEquals("(A) "  + text, validCompare[i], validator.validateObj(valid[i], Locale.US));
            assertTrue("(B) "    + text,                  validator.isValid(valid[i], Locale.US));
            assertEquals("(C) "  + text, validCompare[i], validator.validateObj(valid[i], testPattern));
            assertTrue("(D) "    + text,                  validator.isValid(valid[i], testPattern));
        }
    }

    /**
     * Test different Locale
     */
    public void testValidateLocale() {

        assertEquals("US Locale, US Format", testNumber, strictValidator.validateObj(testStringUS, Locale.US));
        assertNull("US Locale, DE Format", strictValidator.validateObj(testStringDE, Locale.US));

        // Default German Locale
        assertEquals("DE Locale, DE Format", testNumber, strictValidator.validateObj(testStringDE, Locale.GERMAN));
        assertNull("DE Locale, US Format", strictValidator.validateObj(testStringUS, Locale.GERMAN));

        // Default Locale has been set to Locale.US in setup()
        assertEquals("Default Locale, US Format", testNumber, strictValidator.validateObj(testStringUS));
        assertNull("Default Locale, DE Format", strictValidator.validateObj(testStringDE));
    }

    /**
     * Test format() methods
     */
    public void testFormat() {
        Number number = new BigDecimal("1234.5");
        assertEquals("US Locale, US Format", "1,234.5", strictValidator.format(number, Locale.US));
        assertEquals("DE Locale, DE Format", "1.234,5", strictValidator.format(number, Locale.GERMAN));
        assertEquals("Pattern #,#0.00", "12,34.50",  strictValidator.format(number, "#,#0.00"));
    }

    /**
     * Test Range/Min/Max
     */
    public void testRangeMinMax() {
        Number number9 = new Integer(9);
        Number number10 = new Integer(10);
        Number number11 = new Integer(11);
        Number number19 = new Integer(19);
        Number number20 = new Integer(20);
        Number number21 = new Integer(21);

        // Test isInRange()
        assertFalse("isInRange() < min",   strictValidator.isInRange(number9 ,  number10, number20));
        assertTrue("isInRange() = min",    strictValidator.isInRange(number10 , number10, number20));
        assertTrue("isInRange() in range", strictValidator.isInRange(number11 , number10, number20));
        assertTrue("isInRange() = max",    strictValidator.isInRange(number20 , number10, number20));
        assertFalse("isInRange() > max",   strictValidator.isInRange(number21 , number10, number20));

        // Test minValue()
        assertFalse("minValue() < min",    strictValidator.minValue(number9 ,  number10));
        assertTrue("minValue() = min",     strictValidator.minValue(number10 , number10));
        assertTrue("minValue() > min",     strictValidator.minValue(number11 , number10));

        // Test minValue()
        assertTrue("maxValue() < max",     strictValidator.maxValue(number19 , number20));
        assertTrue("maxValue() = max",     strictValidator.maxValue(number20 , number20));
        assertFalse("maxValue() > max",    strictValidator.maxValue(number21 , number20));
    }

}
