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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.DateFormat;
import java.text.Format;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for CalendarValidator.
 */
public class CalendarValidatorTest extends AbstractCalendarValidatorTest {

    private static final int DATE_2005_11_23 = 20051123;
    private static final int TIME_12_03_45 = 120345;

    private CalendarValidator calValidator;

    @BeforeEach
    protected void setUp() {
        calValidator = new CalendarValidator();
        validator = calValidator;
    }

    /**
     * Test adjustToTimeZone() method
     */
    @Test
    public void testAdjustToTimeZone() {

        final Calendar calEST = createCalendar(EST, DATE_2005_11_23, TIME_12_03_45);
        final Date dateEST = calEST.getTime();

        final Calendar calGMT = createCalendar(GMT, DATE_2005_11_23, TIME_12_03_45);
        final Date dateGMT = calGMT.getTime();

        final Calendar calCET = createCalendar(EET, DATE_2005_11_23, TIME_12_03_45);
        final Date dateCET = calCET.getTime();

        // Check the dates don't match
        assertFalse(dateGMT.getTime() == dateCET.getTime(), "Check GMT != CET");
        assertFalse(dateGMT.getTime() == dateEST.getTime(), "Check GMT != EST");
        assertFalse(dateCET.getTime() == dateEST.getTime(), "Check CET != EST");

        // EST to GMT and back
        CalendarValidator.adjustToTimeZone(calEST, GMT);
        assertEquals(dateGMT, calEST.getTime(), "EST to GMT");
        assertFalse(dateEST == calEST.getTime(), "Check EST = GMT");
        CalendarValidator.adjustToTimeZone(calEST, EST);
        assertEquals(dateEST, calEST.getTime(), "back to EST");
        assertFalse(dateGMT == calEST.getTime(), "Check EST != GMT");

        // CET to GMT and back
        CalendarValidator.adjustToTimeZone(calCET, GMT);
        assertEquals(dateGMT, calCET.getTime(), "CET to GMT");
        assertFalse(dateCET == calCET.getTime(), "Check CET = GMT");
        CalendarValidator.adjustToTimeZone(calCET, EET);
        assertEquals(dateCET, calCET.getTime(), "back to CET");
        assertFalse(dateGMT == calCET.getTime(), "Check CET != GMT");

        // Adjust to TimeZone with Same rules
        final Calendar calUTC = createCalendar(UTC, DATE_2005_11_23, TIME_12_03_45);
        assertTrue(UTC.hasSameRules(GMT), "SAME: UTC = GMT");
        assertEquals(calUTC.getTime(), calGMT.getTime(), "SAME: Check time (A)");
        assertFalse(GMT.equals(calUTC.getTimeZone()), "SAME: Check GMT(A)");
        assertTrue(UTC.equals(calUTC.getTimeZone()), "SAME: Check UTC(A)");
        CalendarValidator.adjustToTimeZone(calUTC, GMT);
        assertEquals(calUTC.getTime(), calGMT.getTime(), "SAME: Check time (B)");
        assertTrue(GMT.equals(calUTC.getTimeZone()), "SAME: Check GMT(B)");
        assertFalse(UTC.equals(calUTC.getTimeZone()), "SAME: Check UTC(B)");
    }

    /**
     * Test CalendarValidator validate Methods
     */
    @Test
    public void testCalendarValidatorMethods() {
        Locale.setDefault(Locale.US);
        final Locale locale = Locale.GERMAN;
        final String pattern = "yyyy-MM-dd";
        final String patternVal = "2005-12-31";
        final String germanVal = "31 Dez 2005";
        final String germanPattern = "dd MMM yyyy";
        final String localeVal = "31.12.2005";
        final String defaultVal = "12/31/05";
        final String xxxx = "XXXX";
        final Date expected = createCalendar(null, 20051231, 0).getTime();
        assertEquals(expected, CalendarValidator.getInstance().validate(defaultVal).getTime(), "validate(A) default");
        assertEquals(expected, CalendarValidator.getInstance().validate(localeVal, locale).getTime(), "validate(A) locale ");
        assertEquals(expected, CalendarValidator.getInstance().validate(patternVal, pattern).getTime(), "validate(A) pattern");
        assertEquals(expected, CalendarValidator.getInstance().validate(germanVal, germanPattern, Locale.GERMAN).getTime(), "validate(A) both");

        assertTrue(CalendarValidator.getInstance().isValid(defaultVal), "isValid(A) default");
        assertTrue(CalendarValidator.getInstance().isValid(localeVal, locale), "isValid(A) locale ");
        assertTrue(CalendarValidator.getInstance().isValid(patternVal, pattern), "isValid(A) pattern");
        assertTrue(CalendarValidator.getInstance().isValid(germanVal, germanPattern, Locale.GERMAN), "isValid(A) both");

        assertNull(CalendarValidator.getInstance().validate(xxxx), "validate(B) default");
        assertNull(CalendarValidator.getInstance().validate(xxxx, locale), "validate(B) locale ");
        assertNull(CalendarValidator.getInstance().validate(xxxx, pattern), "validate(B) pattern");
        assertNull(CalendarValidator.getInstance().validate("31 Dec 2005", germanPattern, Locale.GERMAN), "validate(B) both");

        assertFalse(CalendarValidator.getInstance().isValid(xxxx), "isValid(B) default");
        assertFalse(CalendarValidator.getInstance().isValid(xxxx, locale), "isValid(B) locale ");
        assertFalse(CalendarValidator.getInstance().isValid(xxxx, pattern), "isValid(B) pattern");
        assertFalse(CalendarValidator.getInstance().isValid("31 Dec 2005", germanPattern, Locale.GERMAN), "isValid(B) both");

        // Test Time Zone
        final TimeZone zone = TimeZone.getDefault().getRawOffset() == EET.getRawOffset() ? EST : EET;
        final Date expectedZone = createCalendar(zone, 20051231, 0).getTime();
        assertFalse(expected.getTime() == expectedZone.getTime(), "default/EET same ");

        assertEquals(expectedZone, CalendarValidator.getInstance().validate(defaultVal, zone).getTime(), "validate(C) default");
        assertEquals(expectedZone, CalendarValidator.getInstance().validate(localeVal, locale, zone).getTime(), "validate(C) locale ");
        assertEquals(expectedZone, CalendarValidator.getInstance().validate(patternVal, pattern, zone).getTime(), "validate(C) pattern");
        assertEquals(expectedZone, CalendarValidator.getInstance().validate(germanVal, germanPattern, Locale.GERMAN, zone).getTime(), "validate(C) both");
    }

    /**
     * Test compare date methods
     */
    @Test
    public void testCompare() {
        final int sameTime = 124522;
        final int testDate = 20050823;
        final Calendar diffHour = createCalendar(GMT, testDate, 115922); // same date, different time
        final Calendar diffMin = createCalendar(GMT, testDate, 124422); // same date, different time
        final Calendar diffSec = createCalendar(GMT, testDate, 124521); // same date, different time

        final Calendar value = createCalendar(GMT, testDate, sameTime); // test value
        final Calendar cal20050824 = createCalendar(GMT, 20050824, sameTime); // +1 day
        final Calendar cal20050822 = createCalendar(GMT, 20050822, sameTime); // -1 day

        final Calendar cal20050830 = createCalendar(GMT, 20050830, sameTime); // +1 week
        final Calendar cal20050816 = createCalendar(GMT, 20050816, sameTime); // -1 week

        final Calendar cal20050901 = createCalendar(GMT, 20050901, sameTime); // +1 month
        final Calendar cal20050801 = createCalendar(GMT, 20050801, sameTime); // same month
        final Calendar cal20050731 = createCalendar(GMT, 20050731, sameTime); // -1 month

        final Calendar cal20051101 = createCalendar(GMT, 20051101, sameTime); // +1 quarter (Feb Start)
        final Calendar cal20051001 = createCalendar(GMT, 20051001, sameTime); // +1 quarter
        final Calendar cal20050701 = createCalendar(GMT, 20050701, sameTime); // same quarter
        final Calendar cal20050630 = createCalendar(GMT, 20050630, sameTime); // -1 quarter

        final Calendar cal20060101 = createCalendar(GMT, 20060101, sameTime); // +1 year
        final Calendar cal20050101 = createCalendar(GMT, 20050101, sameTime); // same year
        final Calendar cal20041231 = createCalendar(GMT, 20041231, sameTime); // -1 year

        assertEquals(1, calValidator.compare(value, diffHour, Calendar.HOUR_OF_DAY), "hour GT");
        assertEquals(0, calValidator.compare(value, diffMin, Calendar.HOUR_OF_DAY), "hour EQ");
        assertEquals(1, calValidator.compare(value, diffMin, Calendar.MINUTE), "mins GT");
        assertEquals(0, calValidator.compare(value, diffSec, Calendar.MINUTE), "mins EQ");
        assertEquals(1, calValidator.compare(value, diffSec, Calendar.SECOND), "secs GT");

        assertEquals(-1, calValidator.compareDates(value, cal20050824), "date LT"); // +1 day
        assertEquals(0, calValidator.compareDates(value, diffHour), "date EQ"); // same day, diff hour
        assertEquals(0, calValidator.compare(value, diffHour, Calendar.DAY_OF_YEAR), "date(B)"); // same day, diff hour
        assertEquals(1, calValidator.compareDates(value, cal20050822), "date GT"); // -1 day

        assertEquals(-1, calValidator.compareWeeks(value, cal20050830), "week LT"); // +1 week
        assertEquals(0, calValidator.compareWeeks(value, cal20050824), "week =1"); // +1 day
        assertEquals(0, calValidator.compareWeeks(value, cal20050822), "week =2"); // same week
        assertEquals(0, calValidator.compare(value, cal20050822, Calendar.WEEK_OF_MONTH), "week =3"); // same week
        assertEquals(0, calValidator.compareWeeks(value, cal20050822), "week =4"); // -1 day
        assertEquals(1, calValidator.compareWeeks(value, cal20050816), "week GT"); // -1 week

        assertEquals(-1, calValidator.compareMonths(value, cal20050901), "mnth LT"); // +1 month
        assertEquals(0, calValidator.compareMonths(value, cal20050830), "mnth =1"); // +1 week
        assertEquals(0, calValidator.compareMonths(value, cal20050801), "mnth =2"); // same month
        assertEquals(0, calValidator.compareMonths(value, cal20050816), "mnth =3"); // -1 week
        assertEquals(1, calValidator.compareMonths(value, cal20050731), "mnth GT"); // -1 month

        assertEquals(-1, calValidator.compareQuarters(value, cal20051101), "qtrA <1"); // +1 quarter (Feb)
        assertEquals(-1, calValidator.compareQuarters(value, cal20051001), "qtrA <2"); // +1 quarter
        assertEquals(0, calValidator.compareQuarters(value, cal20050901), "qtrA =1"); // +1 month
        assertEquals(0, calValidator.compareQuarters(value, cal20050701), "qtrA =2"); // same quarter
        assertEquals(0, calValidator.compareQuarters(value, cal20050731), "qtrA =3"); // -1 month
        assertEquals(1, calValidator.compareQuarters(value, cal20050630), "qtrA GT"); // -1 quarter

        // Change quarter 1 to start in Feb
        assertEquals(-1, calValidator.compareQuarters(value, cal20051101, 2), "qtrB LT"); // +1 quarter (Feb)
        assertEquals(0, calValidator.compareQuarters(value, cal20051001, 2), "qtrB =1"); // same quarter
        assertEquals(0, calValidator.compareQuarters(value, cal20050901, 2), "qtrB =2"); // +1 month
        assertEquals(1, calValidator.compareQuarters(value, cal20050701, 2), "qtrB =3"); // same quarter
        assertEquals(1, calValidator.compareQuarters(value, cal20050731, 2), "qtrB =4"); // -1 month
        assertEquals(1, calValidator.compareQuarters(value, cal20050630, 2), "qtrB GT"); // -1 quarter

        assertEquals(-1, calValidator.compareYears(value, cal20060101), "year LT"); // +1 year
        assertEquals(0, calValidator.compareYears(value, cal20050101), "year EQ"); // same year
        assertEquals(1, calValidator.compareYears(value, cal20041231), "year GT"); // -1 year

        // invalid compare
        try {
            calValidator.compare(value, value, -1);
            fail("Invalid Compare field - expected IllegalArgumentException to be thrown");
        } catch (final IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Invalid field: -1", "check message");
        }
    }

    /**
     * Test Date/Time style Validator (there isn't an implementation for this)
     */
    @Test
    public void testDateTimeStyle() {
        // Set the default Locale
        final Locale origDefault = Locale.getDefault();
        Locale.setDefault(Locale.UK);

        final AbstractCalendarValidator dateTimeValidator = new AbstractCalendarValidator(true, DateFormat.SHORT, DateFormat.SHORT) {
            private static final long serialVersionUID = 1L;

            @Override
            protected Object processParsedValue(final Object value, final Format formatter) {
                return value;
            }
        };
        assertTrue(dateTimeValidator.isValid("31/12/05 14:23"), "validate(A) default");
        assertTrue(dateTimeValidator.isValid("12/31/05 2:23 PM", Locale.US), "validate(A) locale ");

        // Restore the original default
        Locale.setDefault(origDefault);
    }

    /**
     * Test format methods
     */
    @Override
    @Test
    public void testFormat() {
        // Set the default Locale
        final Locale origDefault = Locale.getDefault();
        Locale.setDefault(Locale.UK);

        final Calendar cal20050101 = createCalendar(GMT, 20051231, 11500);
        assertNull(calValidator.format(null), "null");
        assertEquals("31/12/05", calValidator.format(cal20050101), "default");
        assertEquals("12/31/05", calValidator.format(cal20050101, Locale.US), "locale");
        assertEquals(calValidator.format(cal20050101, "yyyy-MM-dd HH:mm"), "2005-12-31 01:15", "patternA");
        assertEquals(calValidator.format(cal20050101, "yyyy-MM-dd z"), "2005-12-31 GMT", "patternB");
        assertEquals("31 Dez 2005", calValidator.format(cal20050101, "dd MMM yyyy", Locale.GERMAN), "both");

        // EST Time Zone
        assertEquals("30/12/05", calValidator.format(cal20050101, EST), "EST default");
        assertEquals("12/30/05", calValidator.format(cal20050101, Locale.US, EST), "EST locale");
        assertEquals(calValidator.format(cal20050101, "yyyy-MM-dd HH:mm", EST), "2005-12-30 20:15", "EST patternA");
        assertEquals(calValidator.format(cal20050101, "yyyy-MM-dd z", EST), "2005-12-30 EST", "EST patternB");
        assertEquals("30 Dez 2005", calValidator.format(cal20050101, "dd MMM yyyy", Locale.GERMAN, EST), "EST both");

        // Restore the original default
        Locale.setDefault(origDefault);
    }

}
