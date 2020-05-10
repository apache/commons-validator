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

import java.util.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Test Case for TimeValidator.
 * 
 * @version $Revision$
 */
public class TimeValidatorTest extends TestCase {

    protected static final TimeZone GMT = TimeZone.getTimeZone("GMT"); // 0 offset
    protected static final TimeZone EST = TimeZone.getTimeZone("EST"); // - 5 hours

    protected TimeValidator validator;

    protected String[] patternValid = new String[] {
                       "23-59-59" 
                      ,"00-00-00"
                      ,"00-00-01"
                      ,"0-0-0" 
                      ,"1-12-1"
                      ,"10-49-18"
                      ,"16-23-46"};
    protected Date[] patternExpect = new Date[] {
                       createDate(null, 235959, 0)
                      ,createDate(null, 0, 0)
                      ,createDate(null, 1, 0)
                      ,createDate(null, 0, 0)
                      ,createDate(null, 11201, 0)
                      ,createDate(null, 104918, 0)
                      ,createDate(null, 162346, 0)};
    protected String[] localeValid = new String[] {
                      "23:59" 
                     ,"00:00"
                     ,"00:01"
                     ,"0:0" 
                     ,"1:12"
                     ,"10:49"
                     ,"16:23"};
    protected Date[] localeExpect = new Date[] {
                      createDate(null, 235900, 0)
                     ,createDate(null, 0, 0)
                     ,createDate(null, 100, 0)
                     ,createDate(null, 0, 0)
                     ,createDate(null, 11200, 0)
                     ,createDate(null, 104900, 0)
                     ,createDate(null, 162300, 0)};
    protected String[] patternInvalid = new String[] {
                         "24-00-00"  // midnight
                        ,"24-00-01"  // past midnight
                        ,"25-02-03"  // invalid hour 
                        ,"10-61-31"  // invalid minute
                        ,"10-01-61"  // invalid second
                        ,"05:02-29"  // invalid sep 
                        ,"0X-01:01"  // invalid sep
                        ,"05-0X-01"  // invalid char
                        ,"10-01-0X"  // invalid char
                        ,"01:01:05"  // invalid pattern
                        ,"10-10"     // invalid pattern
                        ,"10--10"    // invalid pattern
                        ,"10-10-"};  // invalid pattern
    protected String[] localeInvalid = new String[] {
                         "24:00"  // midnight
                        ,"24:00"  // past midnight
                        ,"25:02"  // invalid hour 
                        ,"10:61"  // invalid minute
                        ,"05-02"  // invalid sep 
                        ,"0X:01"  // invalid sep
                        ,"05:0X"  // invalid char
                        ,"01-01"  // invalid pattern
                        ,"10:"     // invalid pattern
                        ,"10::1"    // invalid pattern
                        ,"10:1:"};  // invalid pattern

    private Locale origDefault;
    private TimeZone defaultZone;

    /**
     * Constructor
     * @param name test name
     */
    public TimeValidatorTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validator = new TimeValidator();
        defaultZone = TimeZone.getDefault();
        origDefault = Locale.getDefault();
    }

    /**
     * Tear down
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        validator = null;
        Locale.setDefault(origDefault);
        TimeZone.setDefault(defaultZone);
    }

    /**
     * Test Valid Dates with "pattern" validation
     */
    public void testPatternValid() {
        for (int i = 0; i < patternValid.length; i++) {
            String text = i + " value=[" +patternValid[i]+"] failed ";
            Calendar calendar = validator.validate(patternValid[i], "HH-mm-ss");
            assertNotNull("validateObj() " + text,  calendar);
            Date date = calendar.getTime();
            assertTrue("isValid() " + text,  validator.isValid(patternValid[i], "HH-mm-ss"));
            assertEquals("compare " + text, patternExpect[i], date);
        }
    }

    /**
     * Test Invalid Dates with "pattern" validation
     */
    public void testPatternInvalid() {
        for (int i = 0; i < patternInvalid.length; i++) {
            String text = i + " value=[" +patternInvalid[i]+"] passed ";
            Object date = validator.validate(patternInvalid[i], "HH-mm-ss");
            assertNull("validate() " + text + date,  date);
            assertFalse("isValid() " + text,  validator.isValid(patternInvalid[i], "HH-mm-ss"));
        }
    }

    /**
     * Test Valid Dates with "locale" validation
     */
    public void testLocaleValid() {
        for (int i = 0; i < localeValid.length; i++) {
            String text = i + " value=[" +localeValid[i]+"] failed ";
            Calendar calendar = validator.validate(localeValid[i], Locale.UK);
            assertNotNull("validate() " + text,  calendar);
            Date date = calendar.getTime();
            assertTrue("isValid() " + text,  validator.isValid(localeValid[i], Locale.UK));
            assertEquals("compare " + text, localeExpect[i], date);
        }
    }

    /**
     * Test Invalid Dates with "locale" validation
     */
    public void testLocaleInvalid() {
        for (int i = 0; i < localeInvalid.length; i++) {
            String text = i + " value=[" +localeInvalid[i]+"] passed ";
            Object date = validator.validate(localeInvalid[i], Locale.US);
            assertNull("validate() " + text + date,  date);
            assertFalse("isValid() " + text,  validator.isValid(localeInvalid[i], Locale.UK));
        }
    }

    /**
     * Test time zone methods.
     */
    public void testTimeZone() {
        // Set the default Locale & TimeZone
        Locale.setDefault(Locale.UK);
        TimeZone.setDefault(GMT);

        Calendar result = null;

        // Default Locale, Default TimeZone
        result = validator.validate("18:01");
        assertNotNull("default result", result);
        assertEquals("default zone",  GMT, result.getTimeZone());
        assertEquals("default hour",   18, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("default minute", 01, result.get(Calendar.MINUTE));
        result = null;

        // Default Locale, diff TimeZone
        result = validator.validate("16:49", EST);
        assertNotNull("zone result", result);
        assertEquals("zone zone",  EST, result.getTimeZone());
        assertEquals("zone hour",   16, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("zone minute", 49, result.get(Calendar.MINUTE));
        result = null;

        // Pattern, diff TimeZone
        result = validator.validate("14-34", "HH-mm", EST);
        assertNotNull("pattern result", result);
        assertEquals("pattern zone",  EST, result.getTimeZone());
        assertEquals("pattern hour",   14, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("pattern minute", 34, result.get(Calendar.MINUTE));
        result = null;

        // Locale, diff TimeZone
        result = validator.validate("7:18 PM", Locale.US, EST);
        assertNotNull("locale result", result);
        assertEquals("locale zone",  EST, result.getTimeZone());
        assertEquals("locale hour",   19, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("locale minute", 18, result.get(Calendar.MINUTE));
        result = null;

        // Locale & Pattern, diff TimeZone
        result = validator.validate("31/Dez/05 21-05", "dd/MMM/yy HH-mm", Locale.GERMAN, EST);
        assertNotNull("pattern result", result);
        assertEquals("pattern zone",  EST, result.getTimeZone());
        assertEquals("pattern day",  2005, result.get(Calendar.YEAR));
        assertEquals("pattern day",    11, result.get(Calendar.MONTH)); // months are 0-11
        assertEquals("pattern day",    31, result.get(Calendar.DATE));
        assertEquals("pattern hour",   21, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("pattern minute", 05, result.get(Calendar.MINUTE));
        result = null;

        // Locale & Pattern, default TimeZone
        result = validator.validate("31/Dez/05 21-05", "dd/MMM/yy HH-mm", Locale.GERMAN);
        assertNotNull("pattern result", result);
        assertEquals("pattern zone",  GMT, result.getTimeZone());
        assertEquals("pattern day",  2005, result.get(Calendar.YEAR));
        assertEquals("pattern day",    11, result.get(Calendar.MONTH)); // months are 0-11
        assertEquals("pattern day",    31, result.get(Calendar.DATE));
        assertEquals("pattern hour",   21, result.get(Calendar.HOUR_OF_DAY));
        assertEquals("pattern minute", 05, result.get(Calendar.MINUTE));
        result = null;

    }

    /**
     * Test Invalid Dates with "locale" validation
     */
    public void testFormat() {
        // Set the default Locale
        Locale.setDefault(Locale.UK);

        Object test = TimeValidator.getInstance().validate("16:49:23", "HH:mm:ss");
        assertNotNull("Test Date ", test);
        assertEquals("Format pattern", "16-49-23", validator.format(test, "HH-mm-ss"));
        assertEquals("Format locale",  "4:49 PM",  validator.format(test, Locale.US));
        assertEquals("Format default", "16:49",  validator.format(test));

    }

    /**
     * Test compare date methods
     */
    public void testCompare() {
        int testTime = 154523;
        int min = 100;
        int hour = 10000;

        Calendar milliGreater = createTime(GMT, testTime, 500); // > milli sec
        Calendar value        = createTime(GMT, testTime, 400); // test value
        Calendar milliLess    = createTime(GMT, testTime, 300); // < milli sec

        Calendar secGreater   = createTime(GMT, testTime + 1, 100);   // +1 sec
        Calendar secLess      = createTime(GMT, testTime - 1, 100);   // -1 sec

        Calendar minGreater   = createTime(GMT, testTime + min, 100);   // +1 min
        Calendar minLess      = createTime(GMT, testTime - min, 100);   // -1 min

        Calendar hourGreater  = createTime(GMT, testTime + hour, 100);   // +1 hour
        Calendar hourLess     = createTime(GMT, testTime - hour, 100);   // -1 hour

        assertEquals("mili LT", -1, validator.compareTime(value, milliGreater)); // > milli
        assertEquals("mili EQ", 0,  validator.compareTime(value, value));        // same time
        assertEquals("mili GT", 1,  validator.compareTime(value, milliLess));    // < milli

        assertEquals("secs LT", -1, validator.compareSeconds(value, secGreater));   // +1 sec
        assertEquals("secs =1", 0,  validator.compareSeconds(value, milliGreater)); // > milli
        assertEquals("secs =2", 0,  validator.compareSeconds(value, value));        // same time
        assertEquals("secs =3", 0,  validator.compareSeconds(value, milliLess));    // < milli
        assertEquals("secs GT", 1,  validator.compareSeconds(value, secLess));      // -1 sec

        assertEquals("mins LT", -1, validator.compareMinutes(value, minGreater));   // +1 min
        assertEquals("mins =1", 0,  validator.compareMinutes(value, secGreater));   // +1 sec
        assertEquals("mins =2", 0,  validator.compareMinutes(value, value));        // same time
        assertEquals("mins =3", 0,  validator.compareMinutes(value, secLess));      // -1 sec
        assertEquals("mins GT", 1,  validator.compareMinutes(value, minLess));      // -1 min

        assertEquals("hour LT", -1, validator.compareHours(value, hourGreater));   // +1 hour
        assertEquals("hour =1", 0,  validator.compareHours(value, minGreater));   // +1 min
        assertEquals("hour =2", 0,  validator.compareHours(value, value));        // same time
        assertEquals("hour =3", 0,  validator.compareHours(value, minLess));      // -1 min
        assertEquals("hour GT", 1,  validator.compareHours(value, hourLess));      // -1 hour

    }

    /**
     * Create a calendar instance for a specified time zone, date and time.
     * 
     * @param zone The time zone
     * @param time the time in HH:mm:ss format
     * @param millisecond the milliseconds
     * @return the new Calendar instance.
     */
    protected static Calendar createTime(TimeZone zone, int time, int millisecond) {
        Calendar calendar = zone == null ? Calendar.getInstance()
                                         : Calendar.getInstance(zone);
        int hour = ((time / 10000) * 10000);
        int min  = ((time / 100) * 100) - hour;
        int sec  = time - (hour + min);
        calendar.set(Calendar.YEAR,  1970);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE,  1);
        calendar.set(Calendar.HOUR_OF_DAY,  (hour / 10000));
        calendar.set(Calendar.MINUTE, (min / 100));
        calendar.set(Calendar.SECOND,  sec);
        calendar.set(Calendar.MILLISECOND,  millisecond);
        return calendar;
    }

    /**
     * Create a date instance for a specified time zone, date and time.
     * 
     * @param zone The time zone
     * @param time the time in HH:mm:ss format
     * @param millisecond the milliseconds
     * @return the new Date instance.
     */
    protected static Date createDate(TimeZone zone, int time, int millisecond) {
        Calendar calendar = createTime(zone, time, millisecond);
        return calendar.getTime();
    }
}
