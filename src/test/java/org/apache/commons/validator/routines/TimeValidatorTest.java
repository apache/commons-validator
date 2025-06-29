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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.time.TimeZones;
import org.apache.commons.validator.util.TestTimeZones;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.DefaultLocale;
import org.junitpioneer.jupiter.DefaultTimeZone;

/**
 * Test Case for TimeValidator.
 */
class TimeValidatorTest {

    /**
     * Create a date instance for a specified time zone, date and time.
     *
     * @param zone        The time zone
     * @param time        the time in HH:mm:ss format
     * @param millisecond the milliseconds
     * @return the new Date instance.
     */
    protected static Date createDate(final TimeZone zone, final int time, final int millisecond) {
        final Calendar calendar = createTime(zone, time, millisecond);
        return calendar.getTime();
    }

    /**
     * Create a calendar instance for a specified time zone, date and time.
     *
     * @param zone        The time zone
     * @param time        the time in HH:mm:ss format
     * @param millisecond the milliseconds
     * @return the new Calendar instance.
     */
    protected static Calendar createTime(final TimeZone zone, final int time, final int millisecond) {
        final Calendar calendar = zone == null ? Calendar.getInstance() : Calendar.getInstance(zone);
        final int hour = time / 10000 * 10000;
        final int min = time / 100 * 100 - hour;
        final int sec = time - (hour + min);
        calendar.set(Calendar.YEAR, 1970);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, hour / 10000);
        calendar.set(Calendar.MINUTE, min / 100);
        calendar.set(Calendar.SECOND, sec);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar;
    }

    protected TimeValidator validator;
    protected String[] patternValid = { "23-59-59", "00-00-00", "00-00-01", "0-0-0", "1-12-1", "10-49-18", "16-23-46" };
    protected Date[] patternExpect = { createDate(null, 235959, 0), createDate(null, 0, 0), createDate(null, 1, 0), createDate(null, 0, 0),
            createDate(null, 11201, 0), createDate(null, 104918, 0), createDate(null, 162346, 0) };
    protected String[] localeValid = { "23:59", "00:00", "00:01", "0:0", "1:12", "10:49", "16:23" };
    protected Date[] localeExpect = { createDate(null, 235900, 0), createDate(null, 0, 0), createDate(null, 100, 0), createDate(null, 0, 0),
            createDate(null, 11200, 0), createDate(null, 104900, 0), createDate(null, 162300, 0) };

    protected String[] patternInvalid = { "24-00-00" // midnight
            , "24-00-01" // past midnight
            , "25-02-03" // invalid hour
            , "10-61-31" // invalid minute
            , "10-01-61" // invalid second
            , "05:02-29" // invalid sep
            , "0X-01:01" // invalid sep
            , "05-0X-01" // invalid char
            , "10-01-0X" // invalid char
            , "01:01:05" // invalid pattern
            , "10-10" // invalid pattern
            , "10--10" // invalid pattern
            , "10-10-" }; // invalid pattern
    protected String[] localeInvalid = { "24:00" // midnight
            , "24:00" // past midnight
            , "25:02" // invalid hour
            , "10:61" // invalid minute
            , "05-02" // invalid sep
            , "0X:01" // invalid sep
            , "05:0X" // invalid char
            , "01-01" // invalid pattern
            , "10:" // invalid pattern
            , "10::1" // invalid pattern
            , "10:1:" }; // invalid pattern

    @BeforeEach
    protected void setUp() {
        validator = new TimeValidator();
    }

    /**
     * Tear down
     */
    @AfterEach
    protected void tearDown() {
        validator = null;
    }

    /**
     * Test compare date methods
     */
    @Test
    void testCompare() {
        final int testTime = 154523;
        final int min = 100;
        final int hour = 10000;

        final Calendar milliGreater = createTime(TimeZones.GMT, testTime, 500); // > milli sec
        final Calendar value = createTime(TimeZones.GMT, testTime, 400); // test value
        final Calendar milliLess = createTime(TimeZones.GMT, testTime, 300); // < milli sec

        final Calendar secGreater = createTime(TimeZones.GMT, testTime + 1, 100); // +1 sec
        final Calendar secLess = createTime(TimeZones.GMT, testTime - 1, 100); // -1 sec

        final Calendar minGreater = createTime(TimeZones.GMT, testTime + min, 100); // +1 min
        final Calendar minLess = createTime(TimeZones.GMT, testTime - min, 100); // -1 min

        final Calendar hourGreater = createTime(TimeZones.GMT, testTime + hour, 100); // +1 hour
        final Calendar hourLess = createTime(TimeZones.GMT, testTime - hour, 100); // -1 hour

        assertEquals(-1, validator.compareTime(value, milliGreater), "milli LT"); // > milli
        assertEquals(0, validator.compareTime(value, value), "milli EQ"); // same time
        assertEquals(1, validator.compareTime(value, milliLess), "milli GT"); // < milli

        assertEquals(-1, validator.compareSeconds(value, secGreater), "secs LT"); // +1 sec
        assertEquals(0, validator.compareSeconds(value, milliGreater), "secs =1"); // > milli
        assertEquals(0, validator.compareSeconds(value, value), "secs =2"); // same time
        assertEquals(0, validator.compareSeconds(value, milliLess), "secs =3"); // < milli
        assertEquals(1, validator.compareSeconds(value, secLess), "secs GT"); // -1 sec

        assertEquals(-1, validator.compareMinutes(value, minGreater), "mins LT"); // +1 min
        assertEquals(0, validator.compareMinutes(value, secGreater), "mins =1"); // +1 sec
        assertEquals(0, validator.compareMinutes(value, value), "mins =2"); // same time
        assertEquals(0, validator.compareMinutes(value, secLess), "mins =3"); // -1 sec
        assertEquals(1, validator.compareMinutes(value, minLess), "mins GT"); // -1 min

        assertEquals(-1, validator.compareHours(value, hourGreater), "hour LT"); // +1 hour
        assertEquals(0, validator.compareHours(value, minGreater), "hour =1"); // +1 min
        assertEquals(0, validator.compareHours(value, value), "hour =2"); // same time
        assertEquals(0, validator.compareHours(value, minLess), "hour =3"); // -1 min
        assertEquals(1, validator.compareHours(value, hourLess), "hour GT"); // -1 hour

    }

    /**
     * Test Invalid Dates with "locale" validation
     */
    @Test
    @DefaultLocale("en-GB")
    void testFormat() {
        // The JVM format varies; calculate expected results
        final Calendar cal = createTime(null, 164923, 0);
        final DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
        final String val = df.format(cal.getTime());
        final DateFormat dfus = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US);
        final String valus = dfus.format(cal.getTime());
        final Object test = TimeValidator.getInstance().validate("16:49:23", "HH:mm:ss");
        assertNotNull(test, "Test Date ");
        assertEquals("16-49-23", validator.format(test, "HH-mm-ss"), "Format pattern");
        assertEquals(valus, validator.format(test, Locale.US), "Format locale");
        assertEquals(val, validator.format(test), "Format default");
    }

    /**
     * Test Invalid Dates with "locale" validation
     */
    @Test
    void testLocaleInvalid() {
        for (int i = 0; i < localeInvalid.length; i++) {
            final String text = i + " value=[" + localeInvalid[i] + "] passed ";
            final Object date = validator.validate(localeInvalid[i], Locale.US);
            assertNull(date, "validate() " + text + date);
            assertFalse(validator.isValid(localeInvalid[i], Locale.UK), "isValid() " + text);
        }
    }

    /**
     * Test Valid Dates with "locale" validation
     */
    @Test
    void testLocaleValid() {
        for (int i = 0; i < localeValid.length; i++) {
            final String text = i + " value=[" + localeValid[i] + "] failed ";
            final Calendar calendar = validator.validate(localeValid[i], Locale.UK);
            assertNotNull(calendar, "validate() " + text);
            final Date date = calendar.getTime();
            assertTrue(validator.isValid(localeValid[i], Locale.UK), "isValid() " + text);
            assertEquals(localeExpect[i], date, "compare " + text);
        }
    }

    /**
     * Test Invalid Dates with "pattern" validation
     */
    @Test
    void testPatternInvalid() {
        for (int i = 0; i < patternInvalid.length; i++) {
            final String text = i + " value=[" + patternInvalid[i] + "] passed ";
            final Object date = validator.validate(patternInvalid[i], "HH-mm-ss");
            assertNull(date, "validate() " + text + date);
            assertFalse(validator.isValid(patternInvalid[i], "HH-mm-ss"), "isValid() " + text);
        }
    }

    /**
     * Test Valid Dates with "pattern" validation
     */
    @Test
    void testPatternValid() {
        for (int i = 0; i < patternValid.length; i++) {
            final String text = i + " value=[" + patternValid[i] + "] failed ";
            final Calendar calendar = validator.validate(patternValid[i], "HH-mm-ss");
            assertNotNull(calendar, "validateObj() " + text);
            final Date date = calendar.getTime();
            assertTrue(validator.isValid(patternValid[i], "HH-mm-ss"), "isValid() " + text);
            assertEquals(patternExpect[i], date, "compare " + text);
        }
    }

    /**
     * Test time zone methods.
     */
    @Test
    @DefaultLocale("en-GB")
    @DefaultTimeZone("GMT")
    void testTimeZone() {
        Calendar result = validator.validate("18:01");
        assertNotNull(result, "default result");
        assertEquals(TimeZones.GMT, result.getTimeZone(), "default zone");
        assertEquals(18, result.get(Calendar.HOUR_OF_DAY), "default hour");
        assertEquals(01, result.get(Calendar.MINUTE), "default minute");
        result = null;

        // Default Locale, diff TimeZone
        result = validator.validate("16:49", TestTimeZones.EST);
        assertNotNull(result, "zone result");
        assertEquals(TestTimeZones.EST, result.getTimeZone(), "zone zone");
        assertEquals(16, result.get(Calendar.HOUR_OF_DAY), "zone hour");
        assertEquals(49, result.get(Calendar.MINUTE), "zone minute");
        result = null;

        // Pattern, diff TimeZone
        result = validator.validate("14-34", "HH-mm", TestTimeZones.EST);
        assertNotNull(result, "pattern result");
        assertEquals(TestTimeZones.EST, result.getTimeZone(), "pattern zone");
        assertEquals(14, result.get(Calendar.HOUR_OF_DAY), "pattern hour");
        assertEquals(34, result.get(Calendar.MINUTE), "pattern minute");
        result = null;

        // Locale, diff TimeZone
        final DateFormat usdf = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US);
        final Calendar uscal = Calendar.getInstance(Locale.US);
        uscal.set(2005, Calendar.JANUARY, 1, 19, 18); // month is 0-based
        final String usVal = usdf.format(uscal.getTime());
        result = validator.validate(usVal, Locale.US, TestTimeZones.EST);
        assertNotNull(result, "locale result: " + usVal);
        assertEquals(TestTimeZones.EST, result.getTimeZone(), "locale zone: " + usVal);
        assertEquals(19, result.get(Calendar.HOUR_OF_DAY), "locale hour: " + usVal);
        assertEquals(18, result.get(Calendar.MINUTE), "locale minute: " + usVal);
        result = null;

        final String dateTimePattern = "dd/MMM/yy HH-mm";
        final Calendar decal = Calendar.getInstance(Locale.GERMAN);
        decal.set(2005, 11, 31, 21, 05); // month is 0-based
        final String germanSample = new SimpleDateFormat(dateTimePattern, Locale.GERMAN).format(decal.getTime());

        // Locale & Pattern, diff TimeZone
        result = validator.validate(germanSample, dateTimePattern, Locale.GERMAN, TestTimeZones.EST);
        assertNotNull(result, "pattern result: " + germanSample);
        assertEquals(TestTimeZones.EST, result.getTimeZone(), "pattern zone");
        assertEquals(2005, result.get(Calendar.YEAR), "pattern day");
        assertEquals(11, result.get(Calendar.MONTH), "pattern day"); // months are 0-11
        assertEquals(31, result.get(Calendar.DATE), "pattern day");
        assertEquals(21, result.get(Calendar.HOUR_OF_DAY), "pattern hour");
        assertEquals(05, result.get(Calendar.MINUTE), "pattern minute");
        result = null;

        // Locale & Pattern, default TimeZone
        result = validator.validate(germanSample, dateTimePattern, Locale.GERMAN);
        assertNotNull(result, "pattern result: " + germanSample);
        assertEquals(TimeZones.GMT, result.getTimeZone(), "pattern zone");
        assertEquals(2005, result.get(Calendar.YEAR), "pattern day");
        assertEquals(11, result.get(Calendar.MONTH), "pattern day"); // months are 0-11
        assertEquals(31, result.get(Calendar.DATE), "pattern day");
        assertEquals(21, result.get(Calendar.HOUR_OF_DAY), "pattern hour");
        assertEquals(05, result.get(Calendar.MINUTE), "pattern minute");
        result = null;

    }
}
