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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Base Calendar Test Case.
 */
public abstract class AbstractCalendarValidatorTest {

    protected static final TimeZone GMT = TimeZone.getTimeZone("GMT"); // 0 offset

    protected static final TimeZone EST = TimeZone.getTimeZone("EST"); // - 5 hours
    protected static final TimeZone EET = TimeZone.getTimeZone("EET"); // + 2 hours
    protected static final TimeZone UTC = TimeZone.getTimeZone("UTC"); // + 2 hours

    /**
     * Create a calendar instance for a specified time zone, date and time.
     *
     * @param zone The time zone
     * @param date The date in yyyyMMdd format
     * @param time the time in HH:mm:ss format
     * @return the new Calendar instance.
     */
    protected static Calendar createCalendar(final TimeZone zone, final int date, final int time) {
        final Calendar calendar = zone == null ? Calendar.getInstance() : Calendar.getInstance(zone);
        final int year = date / 10000 * 10000;
        final int mth = date / 100 * 100 - year;
        final int day = date - (year + mth);
        final int hour = time / 10000 * 10000;
        final int min = time / 100 * 100 - hour;
        final int sec = time - (hour + min);
        calendar.set(Calendar.YEAR, year / 10000);
        calendar.set(Calendar.MONTH, mth / 100 - 1);
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour / 10000);
        calendar.set(Calendar.MINUTE, min / 100);
        calendar.set(Calendar.SECOND, sec);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * Create a date instance for a specified time zone, date and time.
     *
     * @param zone The time zone
     * @param date The date in yyyyMMdd format
     * @param time the time in HH:mm:ss format
     * @return the new Date instance.
     */
    protected static Date createDate(final TimeZone zone, final int date, final int time) {
        final Calendar calendar = createCalendar(zone, date, time);
        return calendar.getTime();
    }

    protected AbstractCalendarValidator validator;
    protected String[] patternValid = { "2005-01-01", "2005-12-31", "2004-02-29" // valid leap
            , "2005-04-30", "05-12-31", "2005-1-1", "05-1-1" };
    protected String[] localeValid = { "01/01/2005", "12/31/2005", "02/29/2004" // valid leap
            , "04/30/2005", "12/31/05", "1/1/2005", "1/1/05" };
    protected Date[] patternExpect = { createDate(null, 20050101, 0), createDate(null, 20051231, 0), createDate(null, 20040229, 0),
            createDate(null, 20050430, 0), createDate(null, 20051231, 0), createDate(null, 20050101, 0), createDate(null, 20050101, 0) };

    protected String[] patternInvalid = { "2005-00-01" // zero month
            , "2005-01-00" // zero day
            , "2005-13-03" // month invalid
            , "2005-04-31" // invalid day
            , "2005-03-32" // invalid day
            , "2005-02-29" // invalid leap
            , "200X-01-01" // invalid char
            , "2005-0X-01" // invalid char
            , "2005-01-0X" // invalid char
            , "01/01/2005" // invalid pattern
            , "2005-01" // invalid pattern
            , "2005--01" // invalid pattern
            , "2005-01-" }; // invalid pattern

    protected String[] localeInvalid = { "01/00/2005" // zero month
            , "00/01/2005" // zero day
            , "13/01/2005" // month invalid
            , "04/31/2005" // invalid day
            , "03/32/2005" // invalid day
            , "02/29/2005" // invalid leap
            , "01/01/200X" // invalid char
            , "01/0X/2005" // invalid char
            , "0X/01/2005" // invalid char
            , "01-01-2005" // invalid pattern
            , "01/2005" // invalid pattern
            // -------- ,"/01/2005" ---- passes on some JDK
            , "01//2005" }; // invalid pattern

    /**
     * Tear down
     */
    @AfterEach
    protected void tearDown() {
        validator = null;
    }

    /**
     * Test Invalid Dates with "locale" validation
     */
    @Test
    public void testFormat() {

        // Create a Date or Calendar
        final Object test = validator.parse("2005-11-28", "yyyy-MM-dd", null, null);
        assertNotNull(test, "Test Date");
        assertEquals("28.11.05", validator.format(test, "dd.MM.yy"), "Format pattern");
        assertEquals("11/28/05", validator.format(test, Locale.US), "Format locale");
    }

    /**
     * Test Invalid Dates with "locale" validation
     */
    @Test
    public void testLocaleInvalid() {
        for (int i = 0; i < localeInvalid.length; i++) {
            final String text = i + " value=[" + localeInvalid[i] + "] passed ";
            final Object date = validator.parse(localeInvalid[i], null, Locale.US, null);
            assertNull(date, "validateObj() " + text + date);
            assertFalse(validator.isValid(localeInvalid[i], Locale.US), "isValid() " + text);
        }
    }

    /**
     * Test Valid Dates with "locale" validation
     */
    @Test
    public void testLocaleValid() {
        for (int i = 0; i < localeValid.length; i++) {
            final String text = i + " value=[" + localeValid[i] + "] failed ";
            Object date = validator.parse(localeValid[i], null, Locale.US, null);
            assertNotNull(date, "validateObj() " + text + date);
            assertTrue(validator.isValid(localeValid[i], Locale.US), "isValid() " + text);
            if (date instanceof Calendar) {
                date = ((Calendar) date).getTime();
            }
            assertEquals(patternExpect[i], date, "compare " + text);
        }
    }

    /**
     * Test Invalid Dates with "pattern" validation
     */
    @Test
    public void testPatternInvalid() {
        for (int i = 0; i < patternInvalid.length; i++) {
            final String text = i + " value=[" + patternInvalid[i] + "] passed ";
            final Object date = validator.parse(patternInvalid[i], "yy-MM-dd", null, null);
            assertNull(date, "validateObj() " + text + date);
            assertFalse(validator.isValid(patternInvalid[i], "yy-MM-dd"), "isValid() " + text);
        }
    }

    /**
     * Test Valid Dates with "pattern" validation
     */
    @Test
    public void testPatternValid() {
        for (int i = 0; i < patternValid.length; i++) {
            final String text = i + " value=[" + patternValid[i] + "] failed ";
            Object date = validator.parse(patternValid[i], "yy-MM-dd", null, null);
            assertNotNull(date, "validateObj() " + text + date);
            assertTrue(validator.isValid(patternValid[i], "yy-MM-dd"), "isValid() " + text);
            if (date instanceof Calendar) {
                date = ((Calendar) date).getTime();
            }
            assertEquals(patternExpect[i], date, "compare " + text);
        }
    }

    /**
     * Test validator serialization.
     */
    @Test
    public void testSerialization() {
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

}
