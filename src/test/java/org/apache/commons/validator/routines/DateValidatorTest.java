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

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Test Case for DateValidator.
 * 
 * @version $Revision$
 */
public class DateValidatorTest extends AbstractCalendarValidatorTest {

    private DateValidator dateValidator;

    /**
     * Constructor
     * @param name test name
     */
    public DateValidatorTest(String name) {
        super(name);
    }

    /**
     * Set Up.
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();
        dateValidator = new DateValidator();
        validator         = dateValidator;
    }

    /**
     * Test DateValidator validate Methods
     */
    public void testDateValidatorMethods() {
        Locale.setDefault(Locale.US);
        Locale locale     = Locale.GERMAN;
        String pattern    = "yyyy-MM-dd";
        String patternVal = "2005-12-31";
        String germanVal     = "31 Dez 2005";
        String germanPattern = "dd MMM yyyy";
        String localeVal  = "31.12.2005";
        String defaultVal = "12/31/05";
        String XXXX    = "XXXX"; 
        Date expected = createCalendar(null, 20051231, 0).getTime();

        assertEquals("validate(A) default", expected, DateValidator.getInstance().validate(defaultVal));
        assertEquals("validate(A) locale ", expected, DateValidator.getInstance().validate(localeVal, locale));
        assertEquals("validate(A) pattern", expected, DateValidator.getInstance().validate(patternVal, pattern));
        assertEquals("validate(A) both",    expected, DateValidator.getInstance().validate(germanVal, germanPattern, Locale.GERMAN));

        assertTrue("isValid(A) default", DateValidator.getInstance().isValid(defaultVal));
        assertTrue("isValid(A) locale ", DateValidator.getInstance().isValid(localeVal, locale));
        assertTrue("isValid(A) pattern", DateValidator.getInstance().isValid(patternVal, pattern));
        assertTrue("isValid(A) both",    DateValidator.getInstance().isValid(germanVal, germanPattern, Locale.GERMAN));

        assertNull("validate(B) default", DateValidator.getInstance().validate(XXXX));
        assertNull("validate(B) locale ", DateValidator.getInstance().validate(XXXX, locale));
        assertNull("validate(B) pattern", DateValidator.getInstance().validate(XXXX, pattern));
        assertNull("validate(B) both",    DateValidator.getInstance().validate("31 Dec 2005", germanPattern, Locale.GERMAN));

        assertFalse("isValid(B) default", DateValidator.getInstance().isValid(XXXX));
        assertFalse("isValid(B) locale ", DateValidator.getInstance().isValid(XXXX, locale));
        assertFalse("isValid(B) pattern", DateValidator.getInstance().isValid(XXXX, pattern));
        assertFalse("isValid(B) both",    DateValidator.getInstance().isValid("31 Dec 2005", germanPattern, Locale.GERMAN));

        // Test Time Zone
        TimeZone zone = (TimeZone.getDefault().getRawOffset() == EET.getRawOffset() ? EST : EET); 
        Date expectedZone = createCalendar(zone, 20051231, 0).getTime();
        assertFalse("default/zone same "+zone, expected.getTime() == expectedZone.getTime());

        assertEquals("validate(C) default", expectedZone, DateValidator.getInstance().validate(defaultVal, zone));
        assertEquals("validate(C) locale ", expectedZone, DateValidator.getInstance().validate(localeVal, locale, zone));
        assertEquals("validate(C) pattern", expectedZone, DateValidator.getInstance().validate(patternVal, pattern, zone));
        assertEquals("validate(C) both",    expectedZone, DateValidator.getInstance().validate(germanVal, germanPattern, Locale.GERMAN, zone));
    }

    /**
     * Test compare date methods
     */
    public void testCompare() {
        int sameTime  = 124522;
        int testDate = 20050823;
        Date diffHour    = createDate(GMT, testDate, 115922);    // same date, different time

        Date value        = createDate(GMT, testDate, sameTime);   // test value
        Date date20050824 = createDate(GMT, 20050824, sameTime);   // +1 day
        Date date20050822 = createDate(GMT, 20050822, sameTime);   // -1 day

        Date date20050830 = createDate(GMT, 20050830, sameTime);   // +1 week
        Date date20050816 = createDate(GMT, 20050816, sameTime);   // -1 week

        Date date20050901 = createDate(GMT, 20050901, sameTime);   // +1 month
        Date date20050801 = createDate(GMT, 20050801, sameTime);   // same month
        Date date20050731 = createDate(GMT, 20050731, sameTime);   // -1 month

        Date date20051101 = createDate(GMT, 20051101, sameTime);   // +1 quarter (Feb Start)
        Date date20051001 = createDate(GMT, 20051001, sameTime);   // +1 quarter
        Date date20050701 = createDate(GMT, 20050701, sameTime);   // same quarter
        Date date20050630 = createDate(GMT, 20050630, sameTime);   // -1 quarter
        Date date20050110 = createDate(GMT, 20050110, sameTime);   // Previous Year qtr (Fen start)

        Date date20060101 = createDate(GMT, 20060101, sameTime);   // +1 year
        Date date20050101 = createDate(GMT, 20050101, sameTime);   // same year
        Date date20041231 = createDate(GMT, 20041231, sameTime);   // -1 year

        assertEquals("date LT", -1, dateValidator.compareDates(value, date20050824, GMT)); // +1 day
        assertEquals("date EQ", 0,  dateValidator.compareDates(value, diffHour, GMT));    // same day, diff hour
        assertEquals("date GT", 1,  dateValidator.compareDates(value, date20050822, GMT)); // -1 day

        assertEquals("week LT", -1, dateValidator.compareWeeks(value, date20050830, GMT)); // +1 week 
        assertEquals("week =1", 0,  dateValidator.compareWeeks(value, date20050824, GMT)); // +1 day
        assertEquals("week =2", 0,  dateValidator.compareWeeks(value, date20050822, GMT)); // same week
        assertEquals("week =3", 0,  dateValidator.compareWeeks(value, date20050822, GMT)); // -1 day
        assertEquals("week GT", 1,  dateValidator.compareWeeks(value, date20050816, GMT)); // -1 week

        assertEquals("mnth LT", -1, dateValidator.compareMonths(value, date20050901, GMT)); // +1 month 
        assertEquals("mnth =1", 0,  dateValidator.compareMonths(value, date20050830, GMT)); // +1 week 
        assertEquals("mnth =2", 0,  dateValidator.compareMonths(value, date20050801, GMT)); // same month
        assertEquals("mnth =3", 0,  dateValidator.compareMonths(value, date20050816, GMT)); // -1 week
        assertEquals("mnth GT", 1,  dateValidator.compareMonths(value, date20050731, GMT)); // -1 month

        assertEquals("qtrA <1", -1, dateValidator.compareQuarters(value, date20051101, GMT)); // +1 quarter (Feb) 
        assertEquals("qtrA <2", -1, dateValidator.compareQuarters(value, date20051001, GMT)); // +1 quarter 
        assertEquals("qtrA =1", 0,  dateValidator.compareQuarters(value, date20050901, GMT)); // +1 month 
        assertEquals("qtrA =2", 0,  dateValidator.compareQuarters(value, date20050701, GMT)); // same quarter
        assertEquals("qtrA =3", 0,  dateValidator.compareQuarters(value, date20050731, GMT)); // -1 month
        assertEquals("qtrA GT", 1,  dateValidator.compareQuarters(value, date20050630, GMT)); // -1 quarter

        // Change quarter 1 to start in Feb
        assertEquals("qtrB LT", -1, dateValidator.compareQuarters(value, date20051101, GMT, 2)); // +1 quarter (Feb) 
        assertEquals("qtrB =1", 0,  dateValidator.compareQuarters(value, date20051001, GMT, 2));  // same quarter 
        assertEquals("qtrB =2", 0,  dateValidator.compareQuarters(value, date20050901, GMT, 2)); // +1 month 
        assertEquals("qtrB =3", 1,  dateValidator.compareQuarters(value, date20050701, GMT, 2)); // same quarter
        assertEquals("qtrB =4", 1,  dateValidator.compareQuarters(value, date20050731, GMT, 2)); // -1 month
        assertEquals("qtrB GT", 1,  dateValidator.compareQuarters(value, date20050630, GMT, 2)); // -1 quarter
        assertEquals("qtrB prev", 1,  dateValidator.compareQuarters(value, date20050110, GMT, 2)); // Jan Prev year qtr

        assertEquals("year LT", -1, dateValidator.compareYears(value, date20060101, GMT)); // +1 year 
        assertEquals("year EQ", 0,  dateValidator.compareYears(value, date20050101, GMT)); // same year
        assertEquals("year GT", 1,  dateValidator.compareYears(value, date20041231, GMT)); // -1 year

        // Compare using alternative TimeZone
        Date sameDayTwoAm    = createDate(GMT, testDate, 20000);
        assertEquals("date LT", -1, dateValidator.compareDates(value, date20050824, EST)); // +1 day
        assertEquals("date EQ", 0,  dateValidator.compareDates(value, diffHour, EST));    // same day, diff hour
        assertEquals("date EQ", 1,  dateValidator.compareDates(value, sameDayTwoAm, EST));    // same day, diff hour
        assertEquals("date GT", 1,  dateValidator.compareDates(value, date20050822, EST)); // -1 day
    }
}
