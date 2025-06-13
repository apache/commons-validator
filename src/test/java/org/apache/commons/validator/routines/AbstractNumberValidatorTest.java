/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.validator.routines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.DefaultLocale;

/**
 * Base Number Test Case.
 */
@DefaultLocale(country = "US", language = "en")
public abstract class AbstractNumberValidatorTest {

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
     * Tear down
     */
    @AfterEach
    protected void tearDown() {
        validator = null;
        strictValidator = null;
    }

    /**
     * Test format() methods
     */
    @Test
    void testFormat() {
        final Number number = new BigDecimal("1234.5");
        assertEquals("1,234.5", strictValidator.format(number, Locale.US), "US Locale, US Format");
        assertEquals("1.234,5", strictValidator.format(number, Locale.GERMAN), "DE Locale, DE Format");
        assertEquals("12,34.50", strictValidator.format(number, "#,#0.00"), "Pattern #,#0.00");
    }

    /**
     * Test Format Type
     */
    @Test
    void testFormatType() {
        assertEquals(0, validator.getFormatType(), "Format Type A");
        assertEquals(AbstractNumberValidator.STANDARD_FORMAT, validator.getFormatType(), "Format Type B");
    }

    /**
     * Test Invalid, strict=false
     */
    @Test
    void testInvalidNotStrict() {
        for (int i = 0; i < invalid.length; i++) {
            final String text = "idx=[" + i + "] value=[" + invalid[i] + "]";
            assertNull(validator.parse(invalid[i], null, Locale.US), "(A) " + text);
            assertFalse(validator.isValid(invalid[i], null, Locale.US), "(B) " + text);
            assertNull(validator.parse(invalid[i], testPattern, null), "(C) " + text);
            assertFalse(validator.isValid(invalid[i], testPattern, null), "(D) " + text);
        }
    }

    /**
     * Test Invalid, strict=true
     */
    @Test
    void testInvalidStrict() {
        for (int i = 0; i < invalidStrict.length; i++) {
            final String text = "idx=[" + i + "] value=[" + invalidStrict[i] + "]";
            assertNull(strictValidator.parse(invalidStrict[i], null, Locale.US), "(A) " + text);
            assertFalse(strictValidator.isValid(invalidStrict[i], null, Locale.US), "(B) " + text);
            assertNull(strictValidator.parse(invalidStrict[i], testPattern, null), "(C) " + text);
            assertFalse(strictValidator.isValid(invalidStrict[i], testPattern, null), "(D) " + text);
        }
    }

    /**
     * Test Range/Min/Max
     */
    @Test
    void testRangeMinMax() {
        final Number number9 = Integer.valueOf(9);
        final Number number10 = Integer.valueOf(10);
        final Number number11 = Integer.valueOf(11);
        final Number number19 = Integer.valueOf(19);
        final Number number20 = Integer.valueOf(20);
        final Number number21 = Integer.valueOf(21);

        // Test isInRange()
        assertFalse(strictValidator.isInRange(number9, number10, number20), "isInRange() < min");
        assertTrue(strictValidator.isInRange(number10, number10, number20), "isInRange() = min");
        assertTrue(strictValidator.isInRange(number11, number10, number20), "isInRange() in range");
        assertTrue(strictValidator.isInRange(number20, number10, number20), "isInRange() = max");
        assertFalse(strictValidator.isInRange(number21, number10, number20), "isInRange() > max");

        // Test minValue()
        assertFalse(strictValidator.minValue(number9, number10), "minValue() < min");
        assertTrue(strictValidator.minValue(number10, number10), "minValue() = min");
        assertTrue(strictValidator.minValue(number11, number10), "minValue() > min");

        // Test minValue()
        assertTrue(strictValidator.maxValue(number19, number20), "maxValue() < max");
        assertTrue(strictValidator.maxValue(number20, number20), "maxValue() = max");
        assertFalse(strictValidator.maxValue(number21, number20), "maxValue() > max");
    }

    /**
     * Test validator serialization.
     */
    @Test
    void testSerialization() {
        // Serialize the check digit routine
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(validator);
            oos.flush();
        } catch (final Exception e) {
            fail(validator.getClass().getName() + " error during serialization: " + e);
        }

        // Deserialize the test object
        Object result = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
            final ObjectInputStream ois = new ObjectInputStream(bais);
            result = ois.readObject();
        } catch (final Exception e) {
            fail(validator.getClass().getName() + " error during deserialization: " + e);
        }
        assertNotNull(result);
    }

    /**
     * Test different Locale
     */
    @Test
    void testValidateLocale() {

        assertEquals(testNumber, strictValidator.parse(testStringUS, null, Locale.US), "US Locale, US Format");
        assertNull(strictValidator.parse(testStringDE, null, Locale.US), "US Locale, DE Format");

        // Default German Locale
        assertEquals(testNumber, strictValidator.parse(testStringDE, null, Locale.GERMAN), "DE Locale, DE Format");
        assertNull(strictValidator.parse(testStringUS, null, Locale.GERMAN), "DE Locale, US Format");

        // Default Locale has been set to Locale.US in setup()
        assertEquals(testNumber, strictValidator.parse(testStringUS, null, null), "Default Locale, US Format");
        assertNull(strictValidator.parse(testStringDE, null, null), "Default Locale, DE Format");
    }

    /**
     * Test Min/Max values allowed
     */
    @Test
    void testValidateMinMax() {
        final DecimalFormat fmt = new DecimalFormat("#");
        if (max != null) {
            assertEquals(max, validator.parse(fmt.format(max), "#", null), "Test Max");
            assertNull(validator.parse(fmt.format(maxPlusOne), "#", null), "Test Max + 1");
            assertEquals(min, validator.parse(fmt.format(min), "#", null), "Test Min");
            assertNull(validator.parse(fmt.format(minMinusOne), "#", null), "Test min - 1");
        }
    }

    /**
     * Test Valid, strict=false
     */
    @Test
    void testValidNotStrict() {
        for (int i = 0; i < valid.length; i++) {
            final String text = "idx=[" + i + "] value=[" + validCompare[i] + "]";
            assertEquals(validCompare[i], validator.parse(valid[i], null, Locale.US), "(A) " + text);
            assertTrue(validator.isValid(valid[i], null, Locale.US), "(B) " + text);
            assertEquals(validCompare[i], validator.parse(valid[i], testPattern, null), "(C) " + text);
            assertTrue(validator.isValid(valid[i], testPattern, null), "(D) " + text);
        }
    }

    /**
     * Test Valid, strict=true
     */
    @Test
    void testValidStrict() {
        for (int i = 0; i < validStrict.length; i++) {
            final String text = "idx=[" + i + "] value=[" + validStrictCompare[i] + "]";
            assertEquals(validStrictCompare[i], strictValidator.parse(validStrict[i], null, Locale.US), "(A) " + text);
            assertTrue(strictValidator.isValid(validStrict[i], null, Locale.US), "(B) " + text);
            assertEquals(validStrictCompare[i], strictValidator.parse(validStrict[i], testPattern, null), "(C) " + text);
            assertTrue(strictValidator.isValid(validStrict[i], testPattern, null), "(D) " + text);
        }
    }

}
