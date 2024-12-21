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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Case for DateValidator.
 */
public class DateValidatorTest extends AbstractCalendarValidatorTest {

    private DateValidator dateValidator;
    private Locale originalLocale;

    /**
     * Sets up test fixtures.
     */
    @BeforeEach
    protected void setUp() {
        originalLocale = Locale.getDefault();
        dateValidator = new DateValidator();
        validator = dateValidator;
    }

    @AfterEach
    protected void tearDown() {
        Locale.setDefault(originalLocale);
    }

    /**
     * Test compare date methods
     */
    @Test
    public void testCompare() {
        final int sameTime = 124522;
        final int testDate = 20050823;
        final Date diffHour = createDate(GMT, testDate, 115922); // same date, different time

        final Date value = createDate(GMT, testDate, sameTime); // test value
        final Date date20050824 = createDate(GMT, 20050824, sameTime); // +1 day
        final Date date20050822 = createDate(GMT, 20050822, sameTime); // -1 day

        final Date date20050830 = createDate(GMT, 20050830, sameTime); // +1 week
        final Date date20050816 = createDate(GMT, 20050816, sameTime); // -1 week

        final Date date20050901 = createDate(GMT, 20050901, sameTime); // +1 month
        final Date date20050801 = createDate(GMT, 20050801, sameTime); // same month
        final Date date20050731 = createDate(GMT, 20050731, sameTime); // -1 month

        final Date date20051101 = createDate(GMT, 20051101, sameTime); // +1 quarter (Feb Start)
        final Date date20051001 = createDate(GMT, 20051001, sameTime); // +1 quarter
        final Date date20050701 = createDate(GMT, 20050701, sameTime); // same quarter
        final Date date20050630 = createDate(GMT, 20050630, sameTime); // -1 quarter
        final Date date20050110 = createDate(GMT, 20050110, sameTime); // Previous Year qtr (Fen start)

        final Date date20060101 = createDate(GMT, 20060101, sameTime); // +1 year
        final Date date20050101 = createDate(GMT, 20050101, sameTime); // same year
        final Date date20041231 = createDate(GMT, 20041231, sameTime); // -1 year

        assertEquals(-1, dateValidator.compareDates(value, date20050824, GMT), "date LT"); // +1 day
        assertEquals(0, dateValidator.compareDates(value, diffHour, GMT), "date EQ"); // same day, diff hour
        assertEquals(1, dateValidator.compareDates(value, date20050822, GMT), "date GT"); // -1 day

        assertEquals(-1, dateValidator.compareWeeks(value, date20050830, GMT), "week LT"); // +1 week
        assertEquals(0, dateValidator.compareWeeks(value, date20050824, GMT), "week =1"); // +1 day
        assertEquals(0, dateValidator.compareWeeks(value, date20050822, GMT), "week =2"); // same week
        assertEquals(0, dateValidator.compareWeeks(value, date20050822, GMT), "week =3"); // -1 day
        assertEquals(1, dateValidator.compareWeeks(value, date20050816, GMT), "week GT"); // -1 week

        assertEquals(-1, dateValidator.compareMonths(value, date20050901, GMT), "mnth LT"); // +1 month
        assertEquals(0, dateValidator.compareMonths(value, date20050830, GMT), "mnth =1"); // +1 week
        assertEquals(0, dateValidator.compareMonths(value, date20050801, GMT), "mnth =2"); // same month
        assertEquals(0, dateValidator.compareMonths(value, date20050816, GMT), "mnth =3"); // -1 week
        assertEquals(1, dateValidator.compareMonths(value, date20050731, GMT), "mnth GT"); // -1 month

        assertEquals(-1, dateValidator.compareQuarters(value, date20051101, GMT), "qtrA <1"); // +1 quarter (Feb)
        assertEquals(-1, dateValidator.compareQuarters(value, date20051001, GMT), "qtrA <2"); // +1 quarter
        assertEquals(0, dateValidator.compareQuarters(value, date20050901, GMT), "qtrA =1"); // +1 month
        assertEquals(0, dateValidator.compareQuarters(value, date20050701, GMT), "qtrA =2"); // same quarter
        assertEquals(0, dateValidator.compareQuarters(value, date20050731, GMT), "qtrA =3"); // -1 month
        assertEquals(1, dateValidator.compareQuarters(value, date20050630, GMT), "qtrA GT"); // -1 quarter

        // Change quarter 1 to start in Feb
        assertEquals(-1, dateValidator.compareQuarters(value, date20051101, GMT, 2), "qtrB LT"); // +1 quarter (Feb)
        assertEquals(0, dateValidator.compareQuarters(value, date20051001, GMT, 2), "qtrB =1"); // same quarter
        assertEquals(0, dateValidator.compareQuarters(value, date20050901, GMT, 2), "qtrB =2"); // +1 month
        assertEquals(1, dateValidator.compareQuarters(value, date20050701, GMT, 2), "qtrB =3"); // same quarter
        assertEquals(1, dateValidator.compareQuarters(value, date20050731, GMT, 2), "qtrB =4"); // -1 month
        assertEquals(1, dateValidator.compareQuarters(value, date20050630, GMT, 2), "qtrB GT"); // -1 quarter
        assertEquals(1, dateValidator.compareQuarters(value, date20050110, GMT, 2), "qtrB prev"); // Jan Prev year qtr

        assertEquals(-1, dateValidator.compareYears(value, date20060101, GMT), "year LT"); // +1 year
        assertEquals(0, dateValidator.compareYears(value, date20050101, GMT), "year EQ"); // same year
        assertEquals(1, dateValidator.compareYears(value, date20041231, GMT), "year GT"); // -1 year

        // Compare using alternative TimeZone
        final Date sameDayTwoAm = createDate(GMT, testDate, 20000);
        assertEquals(-1, dateValidator.compareDates(value, date20050824, EST), "date LT"); // +1 day
        assertEquals(0, dateValidator.compareDates(value, diffHour, EST), "date EQ"); // same day, diff hour
        assertEquals(1, dateValidator.compareDates(value, sameDayTwoAm, EST), "date EQ"); // same day, diff hour
        assertEquals(1, dateValidator.compareDates(value, date20050822, EST), "date GT"); // -1 day
    }

    /**
     * Test DateValidator validate Methods
     */
    @Test
    public void testDateValidatorMethods() {
        Locale.setDefault(Locale.US);
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

        assertEquals(expected, DateValidator.getInstance().validate(defaultVal), "validate(A) default");
        assertEquals(expected, DateValidator.getInstance().validate(localeVal, locale), "validate(A) locale ");
        assertEquals(expected, DateValidator.getInstance().validate(patternVal, pattern), "validate(A) pattern");
        assertEquals(expected, DateValidator.getInstance().validate(germanVal, germanPattern, Locale.GERMAN), "validate(A) both");

        assertTrue(DateValidator.getInstance().isValid(defaultVal), "isValid(A) default");
        assertTrue(DateValidator.getInstance().isValid(localeVal, locale), "isValid(A) locale ");
        assertTrue(DateValidator.getInstance().isValid(patternVal, pattern), "isValid(A) pattern");
        assertTrue(DateValidator.getInstance().isValid(germanVal, germanPattern, Locale.GERMAN), "isValid(A) both");

        assertNull(DateValidator.getInstance().validate(xxxx), "validate(B) default");
        assertNull(DateValidator.getInstance().validate(xxxx, locale), "validate(B) locale ");
        assertNull(DateValidator.getInstance().validate(xxxx, pattern), "validate(B) pattern");
        assertNull(DateValidator.getInstance().validate("31 Dec 2005", germanPattern, Locale.GERMAN), "validate(B) both");

        assertFalse(DateValidator.getInstance().isValid(xxxx), "isValid(B) default");
        assertFalse(DateValidator.getInstance().isValid(xxxx, locale), "isValid(B) locale ");
        assertFalse(DateValidator.getInstance().isValid(xxxx, pattern), "isValid(B) pattern");
        assertFalse(DateValidator.getInstance().isValid("31 Dec 2005", germanPattern, Locale.GERMAN), "isValid(B) both");

        // Test Time Zone
        final TimeZone zone = TimeZone.getDefault().getRawOffset() == EET.getRawOffset() ? EST : EET;
        final Date expectedZone = createCalendar(zone, 20051231, 0).getTime();
        assertNotEquals(expected.getTime(), expectedZone.getTime(), "default/zone same " + zone);

        assertEquals(expectedZone, DateValidator.getInstance().validate(defaultVal, zone), "validate(C) default");
        assertEquals(expectedZone, DateValidator.getInstance().validate(localeVal, locale, zone), "validate(C) locale ");
        assertEquals(expectedZone, DateValidator.getInstance().validate(patternVal, pattern, zone), "validate(C) pattern");
        assertEquals(expectedZone, DateValidator.getInstance().validate(germanVal, germanPattern, Locale.GERMAN, zone), "validate(C) both");
    }
}
