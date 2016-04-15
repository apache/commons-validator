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

import junit.framework.TestCase;

import java.util.Locale;
import java.text.DecimalFormat;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
/**
 * Base Number Test Case.
 * 
 * @version $Revision$
 */
public abstract class AbstractNumberValidatorTest extends TestCase {

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
    public AbstractNumberValidatorTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Locale.setDefault(Locale.US);

    }

    /**
     * Tear down
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        validator = null;
        strictValidator = null;
    }

    /**
     * Test Format Type
     */
    public void testFormatType() {
        assertEquals("Format Type A", 0, validator.getFormatType());
        assertEquals("Format Type B", AbstractNumberValidator.STANDARD_FORMAT, validator.getFormatType());
    }

    /**
     * Test Min/Max values allowed
     */
    public void testValidateMinMax() {
        DecimalFormat fmt = new DecimalFormat("#");
        if (max != null) {
            assertEquals("Test Max",   max, validator.parse(fmt.format(max), "#", null));
            assertNull("Test Max + 1",      validator.parse(fmt.format(maxPlusOne), "#", null));
            assertEquals("Test Min",   min, validator.parse(fmt.format(min), "#", null));
            assertNull("Test min - 1",      validator.parse(fmt.format(minMinusOne), "#", null));
        }
    }

    /**
     * Test Invalid, strict=true
     */
    public void testInvalidStrict() {
        for (int i = 0; i < invalidStrict.length; i++) {
            String text = "idx=["+i+"] value=[" + invalidStrict[i] + "]";
            assertNull("(A) "  + text, strictValidator.parse(invalidStrict[i], null, Locale.US));
            assertFalse("(B) " + text, strictValidator.isValid(invalidStrict[i], null, Locale.US));
            assertNull("(C) "  + text, strictValidator.parse(invalidStrict[i], testPattern, null));
            assertFalse("(D) " + text, strictValidator.isValid(invalidStrict[i], testPattern, null));
        }
    }

    /**
     * Test Invalid, strict=false
     */
    public void testInvalidNotStrict() {
        for (int i = 0; i < invalid.length; i++) {
            String text = "idx=["+i+"] value=[" + invalid[i] + "]";
            assertNull("(A) "  + text, validator.parse(invalid[i], null, Locale.US));
            assertFalse("(B) " + text, validator.isValid(invalid[i], null, Locale.US));
            assertNull("(C) "  + text, validator.parse(invalid[i], testPattern, null));
            assertFalse("(D) " + text, validator.isValid(invalid[i], testPattern, null));
        }
    }

    /**
     * Test Valid, strict=true
     */
    public void testValidStrict() {
        for (int i = 0; i < validStrict.length; i++) {
            String text = "idx=["+i+"] value=[" + validStrictCompare[i] + "]";
            assertEquals("(A) "  + text, validStrictCompare[i], strictValidator.parse(validStrict[i], null, Locale.US));
            assertTrue("(B) "    + text,                        strictValidator.isValid(validStrict[i], null, Locale.US));
            assertEquals("(C) "  + text, validStrictCompare[i], strictValidator.parse(validStrict[i], testPattern, null));
            assertTrue("(D) "    + text,                        strictValidator.isValid(validStrict[i], testPattern, null));
        }
    }

    /**
     * Test Valid, strict=false
     */
    public void testValidNotStrict() {
        for (int i = 0; i < valid.length; i++) {
            String text = "idx=["+i+"] value=[" + validCompare[i] + "]";
            assertEquals("(A) "  + text, validCompare[i], validator.parse(valid[i], null, Locale.US));
            assertTrue("(B) "    + text,                  validator.isValid(valid[i], null, Locale.US));
            assertEquals("(C) "  + text, validCompare[i], validator.parse(valid[i], testPattern, null));
            assertTrue("(D) "    + text,                  validator.isValid(valid[i], testPattern, null));
        }
    }

    /**
     * Test different Locale
     */
    public void testValidateLocale() {

        assertEquals("US Locale, US Format", testNumber, strictValidator.parse(testStringUS, null, Locale.US));
        assertNull("US Locale, DE Format", strictValidator.parse(testStringDE, null, Locale.US));

        // Default German Locale
        assertEquals("DE Locale, DE Format", testNumber, strictValidator.parse(testStringDE, null, Locale.GERMAN));
        assertNull("DE Locale, US Format", strictValidator.parse(testStringUS, null, Locale.GERMAN));

        // Default Locale has been set to Locale.US in setup()
        assertEquals("Default Locale, US Format", testNumber, strictValidator.parse(testStringUS, null, null));
        assertNull("Default Locale, DE Format", strictValidator.parse(testStringDE, null, null));
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

    /**
     * Test validator serialization.
     */
    public void testSerialization() {
        // Serialize the check digit routine
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(validator);
            oos.flush();
            oos.close();
        } catch (Exception e) {
            fail(validator.getClass().getName() + " error during serialization: " + e);
        }

        // Deserialize the test object
        Object result = null;
        try {
            ByteArrayInputStream bais =
                new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            result = ois.readObject();
            bais.close();
        } catch (Exception e) {
            fail(validator.getClass().getName() + " error during deserialization: " + e);
        }
        assertNotNull(result);
    }

}
