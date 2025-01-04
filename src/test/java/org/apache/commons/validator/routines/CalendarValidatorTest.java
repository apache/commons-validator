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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.time.TimeZones;
import org.apache.commons.validator.util.TestTimeZones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.DefaultLocale;
import org.junitpioneer.jupiter.DefaultTimeZone;

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

        final Calendar calEST = createCalendar(TestTimeZones.EST, DATE_2005_11_23, TIME_12_03_45);
        final Date dateEST = calEST.getTime();

        final Calendar calGMT = createCalendar(TimeZones.GMT, DATE_2005_11_23, TIME_12_03_45);
        final Date dateGMT = calGMT.getTime();

        final Calendar calCET = createCalendar(TestTimeZones.EET, DATE_2005_11_23, TIME_12_03_45);
        final Date dateCET = calCET.getTime();

        // Check the dates don't match
        assertNotEquals(dateGMT.getTime(), dateCET.getTime(), "Check GMT != CET");
        assertNotEquals(dateGMT.getTime(), dateEST.getTime(), "Check GMT != EST");
        assertNotEquals(dateCET.getTime(), dateEST.getTime(), "Check CET != EST");

        // EST to GMT and back
        CalendarValidator.adjustToTimeZone(calEST, TimeZones.GMT);
        assertEquals(dateGMT, calEST.getTime(), "EST to GMT");
        assertNotSame(dateEST, calEST.getTime(), "Check EST = GMT");
        CalendarValidator.adjustToTimeZone(calEST, TestTimeZones.EST);
        assertEquals(dateEST, calEST.getTime(), "back to EST");
        assertNotSame(dateGMT, calEST.getTime(), "Check EST != GMT");

        // CET to GMT and back
        CalendarValidator.adjustToTimeZone(calCET, TimeZones.GMT);
        assertEquals(dateGMT, calCET.getTime(), "CET to GMT");
        assertNotSame(dateCET, calCET.getTime(), "Check CET = GMT");
        CalendarValidator.adjustToTimeZone(calCET, TestTimeZones.EET);
        assertEquals(dateCET, calCET.getTime(), "back to CET");
        assertNotSame(dateGMT, calCET.getTime(), "Check CET != GMT");

        // Adjust to TimeZone with Same rules
        final Calendar calUTC = createCalendar(TestTimeZones.UTC, DATE_2005_11_23, TIME_12_03_45);
        assertTrue(TestTimeZones.UTC.hasSameRules(TimeZones.GMT), "SAME: UTC = GMT");
        assertEquals(calUTC.getTime(), calGMT.getTime(), "SAME: Check time (A)");
        assertNotEquals(TimeZones.GMT, calUTC.getTimeZone(), "SAME: Check GMT(A)");
        assertEquals(TestTimeZones.UTC, calUTC.getTimeZone(), "SAME: Check UTC(A)");
        CalendarValidator.adjustToTimeZone(calUTC, TimeZones.GMT);
        assertEquals(calUTC.getTime(), calGMT.getTime(), "SAME: Check time (B)");
        assertEquals(TimeZones.GMT, calUTC.getTimeZone(), "SAME: Check GMT(B)");
        assertNotEquals(TestTimeZones.UTC, calUTC.getTimeZone(), "SAME: Check UTC(B)");
    }

    /**
     * Test CalendarValidator validate Methods
     */
    @Test
    @DefaultLocale(country = "US", language = "en")
    public void testCalendarValidatorMethods() {
        final Locale locale = Locale.GERMAN;
        final String pattern = "yyyy-MM-dd";
        final String patternVal = "2005-12-31";
        final String germanPattern = "dd MMM yyyy";
        // Don't rely on specific German format - it varies between JVMs
        final DateFormat df = new SimpleDateFormat(germanPattern, locale);
        final Calendar cal = Calendar.getInstance(Locale.US);
        cal.set(2005, 11, 31); // month is 0-based
        final String germanVal = df.format(cal.getTime());
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
        final TimeZone zone = TimeZone.getDefault().getRawOffset() == TestTimeZones.EET.getRawOffset() ? TestTimeZones.EST : TestTimeZones.EET;
        final Date expectedZone = createCalendar(zone, 20051231, 0).getTime();
        assertNotEquals(expected.getTime(), expectedZone.getTime(), "default/EET same ");

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
        final Calendar diffHour = createCalendar(TimeZones.GMT, testDate, 115922); // same date, different time
        final Calendar diffMin = createCalendar(TimeZones.GMT, testDate, 124422); // same date, different time
        final Calendar diffSec = createCalendar(TimeZones.GMT, testDate, 124521); // same date, different time

        final Calendar value = createCalendar(TimeZones.GMT, testDate, sameTime); // test value
        final Calendar cal20050824 = createCalendar(TimeZones.GMT, 20050824, sameTime); // +1 day
        final Calendar cal20050822 = createCalendar(TimeZones.GMT, 20050822, sameTime); // -1 day

        final Calendar cal20050830 = createCalendar(TimeZones.GMT, 20050830, sameTime); // +1 week
        final Calendar cal20050816 = createCalendar(TimeZones.GMT, 20050816, sameTime); // -1 week

        final Calendar cal20050901 = createCalendar(TimeZones.GMT, 20050901, sameTime); // +1 month
        final Calendar cal20050801 = createCalendar(TimeZones.GMT, 20050801, sameTime); // same month
        final Calendar cal20050731 = createCalendar(TimeZones.GMT, 20050731, sameTime); // -1 month

        final Calendar cal20051101 = createCalendar(TimeZones.GMT, 20051101, sameTime); // +1 quarter (Feb Start)
        final Calendar cal20051001 = createCalendar(TimeZones.GMT, 20051001, sameTime); // +1 quarter
        final Calendar cal20050701 = createCalendar(TimeZones.GMT, 20050701, sameTime); // same quarter
        final Calendar cal20050630 = createCalendar(TimeZones.GMT, 20050630, sameTime); // -1 quarter

        final Calendar cal20060101 = createCalendar(TimeZones.GMT, 20060101, sameTime); // +1 year
        final Calendar cal20050101 = createCalendar(TimeZones.GMT, 20050101, sameTime); // same year
        final Calendar cal20041231 = createCalendar(TimeZones.GMT, 20041231, sameTime); // -1 year

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
        final Exception e = assertThrows(IllegalArgumentException.class, () -> calValidator.compare(value, value, -1), "Invalid Compare field");
        assertEquals("Invalid field: -1", e.getMessage(), "check message");
    }

    /**
     * Test Date/Time style Validator (there isn't an implementation for this)
     */
    @Test
    @DefaultLocale(country = "UK", language = "en")
    public void testDateTimeStyle() {
        // Formats vary between JVMs, so create the test strings using the current JVM
        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        final Calendar cal = Calendar.getInstance();
        cal.set(2005, 11, 31, 14, 23); // month is 0-based
        final String val = df.format(cal.getTime());

        final DateFormat usdf = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
        final Calendar uscal = Calendar.getInstance(Locale.US);
        uscal.set(2005, 11, 31, 14, 23); // month is 0-based
        final String usval = usdf.format(uscal.getTime());

        final AbstractCalendarValidator dateTimeValidator = new AbstractCalendarValidator(true, DateFormat.SHORT, DateFormat.SHORT) {
            private static final long serialVersionUID = 1L;

            @Override
            protected Object processParsedValue(final Object value, final Format formatter) {
                return value;
            }
        };
        assertTrue(dateTimeValidator.isValid(val), "validate(A) default");
        assertTrue(dateTimeValidator.isValid(usval, Locale.US), "validate(A) locale ");
    }

    /**
     * Test format methods
     */
    @Override
    @Test
    @DefaultLocale(country = "UK", language = "en")
    @DefaultTimeZone("GMT")
    public void testFormat() {
        final Calendar cal20051231 = createCalendar(TimeZones.GMT, 20051231, 11500);
        // validator defaults to SHORT, but the format varies between JVMs
        final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        final String val = df.format(cal20051231.getTime());
        final DateFormat dfus = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
        final String usval = dfus.format(cal20051231.getTime());
        final String germanPattern = "dd MMM yyyy";
        final DateFormat dedf = new SimpleDateFormat(germanPattern, Locale.GERMAN);
        final String deval = dedf.format(cal20051231.getTime());
        assertNull(calValidator.format(null), "null");
        assertEquals(val, calValidator.format(cal20051231), "default");
        assertEquals(usval, calValidator.format(cal20051231, Locale.US), "locale");
        assertEquals("2005-12-31 01:15", calValidator.format(cal20051231, "yyyy-MM-dd HH:mm"), "patternA");
        assertEquals("2005-12-31 GMT", calValidator.format(cal20051231, "yyyy-MM-dd z"), "patternB");
        assertEquals(deval, calValidator.format(cal20051231, germanPattern, Locale.GERMAN), "both");

        // EST Time Zone
        final DateFormat dfest = DateFormat.getDateInstance(DateFormat.SHORT);
        dfest.setTimeZone(TestTimeZones.EST);
        final String valest = dfest.format(cal20051231.getTime());

        final DateFormat dfusest = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
        dfusest.setTimeZone(TestTimeZones.EST);
        final String valusest = dfusest.format(cal20051231.getTime());

        final DateFormat dedfest = new SimpleDateFormat(germanPattern, Locale.GERMAN);
        dedfest.setTimeZone(TestTimeZones.EST);
        final String devalest = dedfest.format(cal20051231.getTime());

        assertEquals(valest, calValidator.format(cal20051231, TestTimeZones.EST), "EST default");
        assertEquals(valusest, calValidator.format(cal20051231, Locale.US, TestTimeZones.EST), "EST locale");

        final String patternA = "yyyy-MM-dd HH:mm";
        final DateFormat dfA = new SimpleDateFormat(patternA);
        dfA.setTimeZone(TestTimeZones.EST);
        final String valA = dfA.format(cal20051231.getTime());
        assertEquals(valA, calValidator.format(cal20051231, patternA, TestTimeZones.EST), "EST patternA");

        final String patternB = "yyyy-MM-dd z";
        final DateFormat dfB = new SimpleDateFormat(patternB);
        dfB.setTimeZone(TestTimeZones.EST);
        final String valB = dfB.format(cal20051231.getTime());

        assertEquals(valB, calValidator.format(cal20051231, patternB, TestTimeZones.EST), "EST patternB");
        assertEquals(devalest, calValidator.format(cal20051231, germanPattern, Locale.GERMAN, TestTimeZones.EST), "EST both");
    }

}
