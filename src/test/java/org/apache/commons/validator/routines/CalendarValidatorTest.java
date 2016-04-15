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

import java.text.Format;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Test Case for CalendarValidator.
 * 
 * @version $Revision$
 */
public class CalendarValidatorTest extends AbstractCalendarValidatorTest {
    
    private static final int DATE_2005_11_23 = 20051123;
    private static final int TIME_12_03_45   = 120345;

    private CalendarValidator calValidator;

    /**
     * Constructor
     * @param name test name
     */
    public CalendarValidatorTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        calValidator = new CalendarValidator();
        validator         = calValidator;
    }

    /**
     * Test CalendarValidator validate Methods
     */
    public void testCalendarValidatorMethods() {
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
        assertEquals("validate(A) default", expected, CalendarValidator.getInstance().validate(defaultVal).getTime());
        assertEquals("validate(A) locale ", expected, CalendarValidator.getInstance().validate(localeVal, locale).getTime());
        assertEquals("validate(A) pattern", expected, CalendarValidator.getInstance().validate(patternVal, pattern).getTime());
        assertEquals("validate(A) both",    expected, CalendarValidator.getInstance().validate(germanVal, germanPattern, Locale.GERMAN).getTime());

        assertTrue("isValid(A) default", CalendarValidator.getInstance().isValid(defaultVal));
        assertTrue("isValid(A) locale ", CalendarValidator.getInstance().isValid(localeVal, locale));
        assertTrue("isValid(A) pattern", CalendarValidator.getInstance().isValid(patternVal, pattern));
        assertTrue("isValid(A) both",    CalendarValidator.getInstance().isValid(germanVal, germanPattern, Locale.GERMAN));

        assertNull("validate(B) default", CalendarValidator.getInstance().validate(XXXX));
        assertNull("validate(B) locale ", CalendarValidator.getInstance().validate(XXXX, locale));
        assertNull("validate(B) pattern", CalendarValidator.getInstance().validate(XXXX, pattern));
        assertNull("validate(B) both",    CalendarValidator.getInstance().validate("31 Dec 2005", germanPattern, Locale.GERMAN));

        assertFalse("isValid(B) default", CalendarValidator.getInstance().isValid(XXXX));
        assertFalse("isValid(B) locale ", CalendarValidator.getInstance().isValid(XXXX, locale));
        assertFalse("isValid(B) pattern", CalendarValidator.getInstance().isValid(XXXX, pattern));
        assertFalse("isValid(B) both",    CalendarValidator.getInstance().isValid("31 Dec 2005", germanPattern, Locale.GERMAN));

        // Test Time Zone
        TimeZone zone = (TimeZone.getDefault().getRawOffset() == EET.getRawOffset() ? EST : EET); 
        Date expectedZone = createCalendar(zone, 20051231, 0).getTime();
        assertFalse("default/EET same ", expected.getTime() == expectedZone.getTime());

        assertEquals("validate(C) default", expectedZone, CalendarValidator.getInstance().validate(defaultVal, zone).getTime());
        assertEquals("validate(C) locale ", expectedZone, CalendarValidator.getInstance().validate(localeVal, locale, zone).getTime());
        assertEquals("validate(C) pattern", expectedZone, CalendarValidator.getInstance().validate(patternVal, pattern, zone).getTime());
        assertEquals("validate(C) both",    expectedZone, CalendarValidator.getInstance().validate(germanVal, germanPattern, Locale.GERMAN, zone).getTime());
    }

    /**
     * Test compare date methods
     */
    public void testCompare() {
        int sameTime = 124522;
        int testDate = 20050823;
        Calendar diffHour    = createCalendar(GMT, testDate, 115922);    // same date, different time
        Calendar diffMin     = createCalendar(GMT, testDate, 124422);    // same date, different time
        Calendar diffSec     = createCalendar(GMT, testDate, 124521);    // same date, different time

        Calendar value       = createCalendar(GMT, testDate, sameTime);   // test value
        Calendar cal20050824 = createCalendar(GMT, 20050824, sameTime);   // +1 day
        Calendar cal20050822 = createCalendar(GMT, 20050822, sameTime);   // -1 day

        Calendar cal20050830 = createCalendar(GMT, 20050830, sameTime);   // +1 week
        Calendar cal20050816 = createCalendar(GMT, 20050816, sameTime);   // -1 week

        Calendar cal20050901 = createCalendar(GMT, 20050901, sameTime);   // +1 month
        Calendar cal20050801 = createCalendar(GMT, 20050801, sameTime);   // same month
        Calendar cal20050731 = createCalendar(GMT, 20050731, sameTime);   // -1 month

        Calendar cal20051101 = createCalendar(GMT, 20051101, sameTime);   // +1 quarter (Feb Start)
        Calendar cal20051001 = createCalendar(GMT, 20051001, sameTime);   // +1 quarter
        Calendar cal20050701 = createCalendar(GMT, 20050701, sameTime);   // same quarter
        Calendar cal20050630 = createCalendar(GMT, 20050630, sameTime);   // -1 quarter

        Calendar cal20060101 = createCalendar(GMT, 20060101, sameTime);   // +1 year
        Calendar cal20050101 = createCalendar(GMT, 20050101, sameTime);   // same year
        Calendar cal20041231 = createCalendar(GMT, 20041231, sameTime);   // -1 year

        assertEquals("hour GT", 1, calValidator.compare(value, diffHour, Calendar.HOUR_OF_DAY));
        assertEquals("hour EQ", 0, calValidator.compare(value, diffMin,  Calendar.HOUR_OF_DAY));
        assertEquals("mins GT", 1, calValidator.compare(value, diffMin,  Calendar.MINUTE));
        assertEquals("mins EQ", 0, calValidator.compare(value, diffSec,  Calendar.MINUTE));
        assertEquals("secs GT", 1, calValidator.compare(value, diffSec,  Calendar.SECOND));

        assertEquals("date LT", -1, calValidator.compareDates(value, cal20050824)); // +1 day
        assertEquals("date EQ", 0,  calValidator.compareDates(value, diffHour));    // same day, diff hour
        assertEquals("date(B)", 0,  calValidator.compare(value, diffHour, Calendar.DAY_OF_YEAR));    // same day, diff hour
        assertEquals("date GT", 1,  calValidator.compareDates(value, cal20050822)); // -1 day

        assertEquals("week LT", -1, calValidator.compareWeeks(value, cal20050830)); // +1 week 
        assertEquals("week =1", 0,  calValidator.compareWeeks(value, cal20050824)); // +1 day
        assertEquals("week =2", 0,  calValidator.compareWeeks(value, cal20050822)); // same week
        assertEquals("week =3", 0,  calValidator.compare(value, cal20050822, Calendar.WEEK_OF_MONTH)); // same week
        assertEquals("week =4", 0,  calValidator.compareWeeks(value, cal20050822)); // -1 day
        assertEquals("week GT", 1,  calValidator.compareWeeks(value, cal20050816)); // -1 week

        assertEquals("mnth LT", -1, calValidator.compareMonths(value, cal20050901)); // +1 month 
        assertEquals("mnth =1", 0,  calValidator.compareMonths(value, cal20050830)); // +1 week 
        assertEquals("mnth =2", 0,  calValidator.compareMonths(value, cal20050801)); // same month
        assertEquals("mnth =3", 0,  calValidator.compareMonths(value, cal20050816)); // -1 week
        assertEquals("mnth GT", 1,  calValidator.compareMonths(value, cal20050731)); // -1 month

        assertEquals("qtrA <1", -1, calValidator.compareQuarters(value, cal20051101)); // +1 quarter (Feb) 
        assertEquals("qtrA <2", -1, calValidator.compareQuarters(value, cal20051001)); // +1 quarter 
        assertEquals("qtrA =1", 0,  calValidator.compareQuarters(value, cal20050901)); // +1 month 
        assertEquals("qtrA =2", 0,  calValidator.compareQuarters(value, cal20050701)); // same quarter
        assertEquals("qtrA =3", 0,  calValidator.compareQuarters(value, cal20050731)); // -1 month
        assertEquals("qtrA GT", 1,  calValidator.compareQuarters(value, cal20050630)); // -1 quarter

        // Change quarter 1 to start in Feb
        assertEquals("qtrB LT", -1, calValidator.compareQuarters(value, cal20051101, 2)); // +1 quarter (Feb) 
        assertEquals("qtrB =1", 0,  calValidator.compareQuarters(value, cal20051001, 2));  // same quarter 
        assertEquals("qtrB =2", 0,  calValidator.compareQuarters(value, cal20050901, 2)); // +1 month 
        assertEquals("qtrB =3", 1,  calValidator.compareQuarters(value, cal20050701, 2)); // same quarter
        assertEquals("qtrB =4", 1,  calValidator.compareQuarters(value, cal20050731, 2)); // -1 month
        assertEquals("qtrB GT", 1,  calValidator.compareQuarters(value, cal20050630, 2)); // -1 quarter

        assertEquals("year LT", -1, calValidator.compareYears(value, cal20060101)); // +1 year 
        assertEquals("year EQ", 0,  calValidator.compareYears(value, cal20050101)); // same year
        assertEquals("year GT", 1,  calValidator.compareYears(value, cal20041231)); // -1 year

        // invalid compare
        try {
            calValidator.compare(value, value,  -1);
            fail("Invalid Compare field - expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("check message", "Invalid field: -1", e.getMessage());
        }
    }

    /**
     * Test Date/Time style Validator (there isn't an implementation for this)
     */
    public void testDateTimeStyle() {
        // Set the default Locale
        Locale origDefault = Locale.getDefault();
        Locale.setDefault(Locale.UK);

        AbstractCalendarValidator dateTimeValidator = 
            new AbstractCalendarValidator(true, DateFormat.SHORT, DateFormat.SHORT) {
                private static final long serialVersionUID = 1L;

            @Override
            protected Object processParsedValue(Object value, Format formatter) {
                return value;
            }
        };
        assertTrue("validate(A) default", dateTimeValidator.isValid("31/12/05 14:23"));
        assertTrue("validate(A) locale ", dateTimeValidator.isValid("12/31/05 2:23 PM", Locale.US));

        // Restore the original default
        Locale.setDefault(origDefault);
    }

    /**
     * Test format methods
     */
    @Override
    public void testFormat() {
        // Set the default Locale
        Locale origDefault = Locale.getDefault();
        Locale.setDefault(Locale.UK);

        Calendar cal20050101 = createCalendar(GMT, 20051231, 11500);
        assertNull("null", calValidator.format(null));
        assertEquals("default",  "31/12/05",         calValidator.format(cal20050101));
        assertEquals("locale",   "12/31/05",         calValidator.format(cal20050101, Locale.US));
        assertEquals("patternA", "2005-12-31 01:15", calValidator.format(cal20050101, "yyyy-MM-dd HH:mm"));
        assertEquals("patternB", "2005-12-31 GMT",   calValidator.format(cal20050101, "yyyy-MM-dd z"));
        assertEquals("both",     "31 Dez 2005",      calValidator.format(cal20050101, "dd MMM yyyy", Locale.GERMAN));

        // EST Time Zone
        assertEquals("EST default",  "30/12/05",         calValidator.format(cal20050101, EST));
        assertEquals("EST locale",   "12/30/05",         calValidator.format(cal20050101, Locale.US, EST));
        assertEquals("EST patternA", "2005-12-30 20:15", calValidator.format(cal20050101, "yyyy-MM-dd HH:mm", EST));
        assertEquals("EST patternB", "2005-12-30 EST",   calValidator.format(cal20050101, "yyyy-MM-dd z", EST));
        assertEquals("EST both",     "30 Dez 2005",      calValidator.format(cal20050101, "dd MMM yyyy", Locale.GERMAN, EST));

        // Restore the original default
        Locale.setDefault(origDefault);
    }

    /**
     * Test adjustToTimeZone() method
     */
    public void testAdjustToTimeZone() {

        Calendar calEST = createCalendar(EST, DATE_2005_11_23, TIME_12_03_45);
        Date dateEST = calEST.getTime();

        Calendar calGMT = createCalendar(GMT, DATE_2005_11_23, TIME_12_03_45);
        Date dateGMT = calGMT.getTime();

        Calendar calCET = createCalendar(EET, DATE_2005_11_23, TIME_12_03_45);
        Date dateCET = calCET.getTime();

        // Check the dates don't match
        assertFalse("Check GMT != CET", dateGMT.getTime() == dateCET.getTime());
        assertFalse("Check GMT != EST", dateGMT.getTime() == dateEST.getTime());
        assertFalse("Check CET != EST", dateCET.getTime() == dateEST.getTime());

        // EST to GMT and back
        CalendarValidator.adjustToTimeZone(calEST, GMT);
        assertEquals("EST to GMT", dateGMT, calEST.getTime());
        assertFalse("Check EST = GMT", dateEST == calEST.getTime());
        CalendarValidator.adjustToTimeZone(calEST, EST);
        assertEquals("back to EST", dateEST, calEST.getTime());
        assertFalse("Check EST != GMT", dateGMT == calEST.getTime());

        // CET to GMT and back
        CalendarValidator.adjustToTimeZone(calCET, GMT);
        assertEquals("CET to GMT", dateGMT, calCET.getTime());
        assertFalse("Check CET = GMT", dateCET == calCET.getTime());
        CalendarValidator.adjustToTimeZone(calCET, EET);
        assertEquals("back to CET", dateCET, calCET.getTime());
        assertFalse("Check CET != GMT", dateGMT == calCET.getTime());

        // Adjust to TimeZone with Same rules
        Calendar calUTC = createCalendar(UTC, DATE_2005_11_23, TIME_12_03_45);
        assertTrue("SAME: UTC = GMT",  UTC.hasSameRules(GMT));
        assertEquals("SAME: Check time (A)", calUTC.getTime(), calGMT.getTime());
        assertFalse("SAME: Check GMT(A)", GMT.equals(calUTC.getTimeZone()));
        assertTrue("SAME: Check UTC(A)",  UTC.equals(calUTC.getTimeZone()));
        CalendarValidator.adjustToTimeZone(calUTC, GMT);
        assertEquals("SAME: Check time (B)", calUTC.getTime(), calGMT.getTime());
        assertTrue("SAME: Check GMT(B)", GMT.equals(calUTC.getTimeZone()));
        assertFalse("SAME: Check UTC(B)",  UTC.equals(calUTC.getTimeZone()));
    }
    
}
