/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright 2006 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.validator.routines;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p><b>Date Validation</b> and Conversion routines (<code>java.util.Date</code>).</p>
 *
 * <p>This validator provides a number of methods for
 *    validating/converting a <code>String</code> value to
 *    a <code>Date</code> using <code>java.text.DateFormat</code>
 *    to parse either:</p>
 *    <ul>
 *       <li>using a specified pattern</li>
 *       <li>using the format for a specified <code>Locale</code></li>
 *       <li>using the format for the <i>default</i> <code>Locale</code></li>
 *    </ul>
 *    
 * <p>Use one of the <code>isValid()</code> methods to just validate or
 *    one of the <code>validate()</code> methods to validate and receive a
 *    <i>converted</i> <code>Date</code> value.</p>
 *    
 * <p>Implementations of the <code>validate()</code> method are provided
 *    to create <code>Date</code> objects for different <i>time zones</i>
 *    if the system default is not appropriate.</p>
 * 
 * <p>Once a value has been sucessfully converted the following
 *    methods can be used to perform various date comparison checks:</p>
 *    <ul>
 *       <li><code>compareDates()</code> compares the day, month and
 *           year of two dates, returing 0, -1 or +1 indicating
 *           whether the first date is equal, before or after the second.</li>
 *       <li><code>compareWeeks()</code> compares the week and 
 *           year of two dates, returing 0, -1 or +1 indicating
 *           whether the first week is equal, before or after the second.</li>
 *       <li><code>compareMonths()</code> compares the month and
 *           year of two dates, returing 0, -1 or +1 indicating
 *           whether the first month is equal, before or after the second.</li>
 *       <li><code>compareQuarters()</code> compares the quarter and
 *           year of two dates, returing 0, -1 or +1 indicating
 *           whether the first quarter is equal, before or after the second.</li>
 *       <li><code>compareYears()</code> compares the 
 *           year of two dates, returing 0, -1 or +1 indicating
 *           whether the first year is equal, before or after the second.</li>
 *    </ul>
 * 
 * <p>So that the same mechanism used for parsing an <i>input</i> value 
 *    for validation can be used to format <i>output</i>, corresponding
 *    <code>format()</code> methods are also provided. That is you can 
 *    format either:</p>
 *    <ul>
 *       <li>using a specified pattern</li>
 *       <li>using the format for a specified <code>Locale</code></li>
 *       <li>using the format for the <i>default</i> <code>Locale</code></li>
 *    </ul>
 * 
 * @version $Revision$ $Date$
 * @since Validator 1.2.1
 */
public class DateValidator extends AbstractCalendarValidator {

    private static final DateValidator VALIDATOR = new DateValidator();

    /**
     * Return a singleton instance of this validator.
     * @return A singleton instance of the DateValidator.
     */
    public static DateValidator getInstance() {
        return VALIDATOR;
    }

    /**
     * Construct a <i>strict</i> instance with <i>short</i>
     * date style.
     */
    public DateValidator() {
        this(true, DateFormat.SHORT);
    }

    /**
     * Construct an instance with the specified <i>strict</i>
     * and <i>date style</i> parameters.
     * 
     * @param strict <code>true</code> if strict 
     *        <code>Format</code> parsing should be used.
     * @param dateStyle the date style to use for Locale validation.
     */
    public DateValidator(boolean strict, int dateStyle) {
        super(strict, dateStyle, -1);
    }

    /**
     * <p>Validate/convert a <code>Date</code> using the default
     *    <code>Locale</code> and <code>TimeZone</code>. 
     *
     * @param value The value validation is being performed on.
     * @return The parsed <code>Date</code> if valid or <code>null</code>
     *  if invalid.
     */
    public Date validate(String value) {
        return (Date)validateObj(value);
    }

    /**
     * <p>Validate/convert a <code>Date</code> using the specified
     *    <code>TimeZone</code> and default <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @param timeZone The Time Zone used to parse the date, system default if null.
     * @return The parsed <code>Date</code> if valid or <code>null</code> if invalid.
     */
    public Date validate(String value, TimeZone timeZone) {
        return (Date)validateObj(value, Locale.getDefault(), timeZone);
    }

    /**
     * <p>Validate/convert a <code>Date</code> using the specified
     *    <i>pattern</i> and default <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @return The parsed <code>Date</code> if valid or <code>null</code> if invalid.
     */
    public Date validate(String value, String pattern) {
        return (Date)validateObj(value, pattern);
    }

    /**
     * <p>Validate/convert a <code>Date</code> using the specified
     *    <i>pattern</i> and <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @param timeZone The Time Zone used to parse the date, system default if null.
     * @return The parsed <code>Date</code> if valid or <code>null</code> if invalid.
     */
    public Date validate(String value, String pattern, TimeZone timeZone) {
        return (Date)validateObj(value, pattern, timeZone);
    }

    /**
     * <p>Validate/convert a <code>Date</code> using the specified
     *    <code>Locale</code> and default <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the date format, system default if null.
     * @return The parsed <code>Date</code> if valid or <code>null</code> if invalid.
     */
    public Date validate(String value, Locale locale) {
        return (Date)validateObj(value, locale);
    }

    /**
     * <p>Validate/convert a <code>Date</code> using the specified
     *    <code>Locale</code> and <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the date format, system default if null.
     * @param timeZone The Time Zone used to parse the date, system default if null.
     * @return The parsed <code>Date</code> if valid or <code>null</code> if invalid.
     */
    public Date validate(String value, Locale locale, TimeZone timeZone) {
        return (Date)validateObj(value, locale, timeZone);
    }

    /**
     * <p>Compare Dates (day, month and year - not time).</p>
     * 
     * @param value The <code>Calendar</code> value to check.
     * @param compare The <code>Calendar</code> to compare the value to.
     * @param timeZone The Time Zone used to compare the dates, system default if null.
     * @return Zero if the dates are equal, -1 if first
     * date is less than the seconds and +1 if the first
     * date is greater than.
     */
    public int compareDates(Date value, Date compare, TimeZone timeZone) {
        Calendar calendarValue   = getCalendar(value, timeZone);
        Calendar calendarCompare = getCalendar(compare, timeZone);
        return compare(calendarValue, calendarCompare, Calendar.DATE);
    }

    /**
     * <p>Compare Weeks (week and year).</p>
     * 
     * @param value The <code>Date</code> value to check.
     * @param compare The <code>Date</code> to compare the value to.
     * @param timeZone The Time Zone used to compare the dates, system default if null.
     * @return Zero if the weeks are equal, -1 if first
     * parameter's week is less than the seconds and +1 if the first
     * parameter's week is greater than.
     */
    public int compareWeeks(Date value, Date compare, TimeZone timeZone) {
        Calendar calendarValue   = getCalendar(value, timeZone);
        Calendar calendarCompare = getCalendar(compare, timeZone);
        return compare(calendarValue, calendarCompare, Calendar.WEEK_OF_YEAR);
    }

    /**
     * <p>Compare Months (month and year).</p>
     * 
     * @param value The <code>Date</code> value to check.
     * @param compare The <code>Date</code> to compare the value to.
     * @param timeZone The Time Zone used to compare the dates, system default if null.
     * @return Zero if the months are equal, -1 if first
     * parameter's month is less than the seconds and +1 if the first
     * parameter's month is greater than.
     */
    public int compareMonths(Date value, Date compare, TimeZone timeZone) {
        Calendar calendarValue   = getCalendar(value, timeZone);
        Calendar calendarCompare = getCalendar(compare, timeZone);
        return compare(calendarValue, calendarCompare, Calendar.MONTH);
    }

    /**
     * <p>Compare Quarters (quarter and year).</p>
     * 
     * @param value The <code>Date</code> value to check.
     * @param compare The <code>Date</code> to compare the value to.
     * @param timeZone The Time Zone used to compare the dates, system default if null.
     * @return Zero if the months are equal, -1 if first
     * parameter's quarter is less than the seconds and +1 if the first
     * parameter's quarter is greater than.
     */
    public int compareQuarters(Date value, Date compare, TimeZone timeZone) {
        return compareQuarters(value, compare, timeZone, 1);
    }

    /**
     * <p>Compare Quarters (quarter and year).</p>
     * 
     * @param value The <code>Date</code> value to check.
     * @param compare The <code>Date</code> to compare the value to.
     * @param timeZone The Time Zone used to compare the dates, system default if null.
     * @param monthOfFirstQuarter The  month that the first quarter starts.
     * @return Zero if the quarters are equal, -1 if first
     * parameter's quarter is less than the seconds and +1 if the first
     * parameter's quarter is greater than.
     */
    public int compareQuarters(Date value, Date compare, TimeZone timeZone, int monthOfFirstQuarter) {
        Calendar calendarValue   = getCalendar(value, timeZone);
        Calendar calendarCompare = getCalendar(compare, timeZone);
        return super.compareQuarters(calendarValue, calendarCompare, monthOfFirstQuarter);
    }

    /**
     * <p>Compare Years.</p>
     * 
     * @param value The <code>Date</code> value to check.
     * @param compare The <code>Date</code> to compare the value to.
     * @param timeZone The Time Zone used to compare the dates, system default if null.
     * @return Zero if the years are equal, -1 if first
     * parameter's year is less than the seconds and +1 if the first
     * parameter's year is greater than.
     */
    public int compareYears(Date value, Date compare, TimeZone timeZone) {
        Calendar calendarValue   = getCalendar(value, timeZone);
        Calendar calendarCompare = getCalendar(compare, timeZone);
        return compare(calendarValue, calendarCompare, Calendar.YEAR);
    }

    /**
     * <p>Convert the <code>Calendar</code> to a <code>Date</code></p>
     * 
     * @param calendar The calendar object create from the parsed value.
     * @return The validated/converted <code>Calendar</code> value if valid 
     * or <code>null</code> if invalid.
     */
    protected Object processCalendar(Calendar calendar) {
        return calendar.getTime();
    }

    /**
     * <p>Convert a <code>Date</code> to a <code>Calendar</code>.</p>
     * 
     * @param value The date value to be converted.
     * @return The converted <code>Calendar</code>.
     */
    private Calendar getCalendar(Date value, TimeZone timeZone) {

        Calendar calendar = null;
        if (timeZone != null) {
            calendar = Calendar.getInstance(timeZone);
        } else {
            calendar = Calendar.getInstance();
        }
        calendar.setTime(value);
        return calendar;

    }

}
