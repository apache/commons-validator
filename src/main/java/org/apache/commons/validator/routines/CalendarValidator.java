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

import java.text.DateFormat;
import java.text.Format;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p><b>Calendar Validation</b> and Conversion routines (<code>java.util.Calendar</code>).</p>
 *
 * <p>This validator provides a number of methods for validating/converting
 *    a <code>String</code> date value to a <code>java.util.Calendar</code> using
 *    <code>java.text.DateFormat</code> to parse either:</p>
 *    <ul>
 *       <li>using the default format for the default <code>Locale</code></li>
 *       <li>using a specified pattern with the default <code>Locale</code></li>
 *       <li>using the default format for a specified <code>Locale</code></li>
 *       <li>using a specified pattern with a specified <code>Locale</code></li>
 *    </ul>
 *
 * <p>For each of the above mechanisms, conversion method (i.e the
 *    <code>validate</code> methods) implementations are provided which
 *    either use the default <code>TimeZone</code> or allow the
 *    <code>TimeZone</code> to be specified.</p>
 *
 * <p>Use one of the <code>isValid()</code> methods to just validate or
 *    one of the <code>validate()</code> methods to validate and receive a
 *    <i>converted</i> <code>Calendar</code> value.</p>
 *
 * <p>Implementations of the <code>validate()</code> method are provided
 *    to create <code>Calendar</code> objects for different <i>time zones</i>
 *    if the system default is not appropriate.</p>
 *
 * <p>Alternatively the CalendarValidator's <code>adjustToTimeZone()</code> method
 *    can be used to adjust the <code>TimeZone</code> of the <code>Calendar</code>
 *    object afterwards.</p>
 *
 * <p>Once a value has been successfully converted the following
 *    methods can be used to perform various date comparison checks:</p>
 *    <ul>
 *       <li><code>compareDates()</code> compares the day, month and
 *           year of two calendars, returning 0, -1 or +1 indicating
 *           whether the first date is equal, before or after the second.</li>
 *       <li><code>compareWeeks()</code> compares the week and
 *           year of two calendars, returning 0, -1 or +1 indicating
 *           whether the first week is equal, before or after the second.</li>
 *       <li><code>compareMonths()</code> compares the month and
 *           year of two calendars, returning 0, -1 or +1 indicating
 *           whether the first month is equal, before or after the second.</li>
 *       <li><code>compareQuarters()</code> compares the quarter and
 *           year of two calendars, returning 0, -1 or +1 indicating
 *           whether the first quarter is equal, before or after the second.</li>
 *       <li><code>compareYears()</code> compares the
 *           year of two calendars, returning 0, -1 or +1 indicating
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
 * @since 1.3.0
 */
public class CalendarValidator extends AbstractCalendarValidator {

    private static final long serialVersionUID = 9109652318762134167L;

    private static final CalendarValidator VALIDATOR = new CalendarValidator();

    /**
     * Return a singleton instance of this validator.
     * @return A singleton instance of the CalendarValidator.
     */
    public static CalendarValidator getInstance() {
        return VALIDATOR;
    }

    /**
     * Construct a <i>strict</i> instance with <i>short</i>
     * date style.
     */
    public CalendarValidator() {
        this(true, DateFormat.SHORT);
    }

    /**
     * Construct an instance with the specified <i>strict</i>
     * and <i>date style</i> parameters.
     *
     * @param strict {@code true} if strict
     *        <code>Format</code> parsing should be used.
     * @param dateStyle the date style to use for Locale validation.
     */
    public CalendarValidator(final boolean strict, final int dateStyle) {
        super(strict, dateStyle, -1);
    }

    /**
     * <p>Validate/convert a <code>Calendar</code> using the default
     *    <code>Locale</code> and <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code>
     *  if invalid.
     */
    public Calendar validate(final String value) {
        return (Calendar)parse(value, (String)null, (Locale)null, (TimeZone)null);
    }

    /**
     * <p>Validate/convert a <code>Calendar</code> using the specified
     *    <code>TimeZone</code> and default <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @param timeZone The Time Zone used to parse the date, system default if null.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code>
     *  if invalid.
     */
    public Calendar validate(final String value, final TimeZone timeZone) {
        return (Calendar)parse(value, (String)null, (Locale)null, timeZone);
    }

    /**
     * <p>Validate/convert a <code>Calendar</code> using the specified
     *    <i>pattern</i> and default <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code> if invalid.
     */
    public Calendar validate(final String value, final String pattern) {
        return (Calendar)parse(value, pattern, (Locale)null, (TimeZone)null);
    }

    /**
     * <p>Validate/convert a <code>Calendar</code> using the specified
     *    <i>pattern</i> and <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @param timeZone The Time Zone used to parse the date, system default if null.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code> if invalid.
     */
    public Calendar validate(final String value, final String pattern, final TimeZone timeZone) {
        return (Calendar)parse(value, pattern, (Locale)null, timeZone);
    }

    /**
     * <p>Validate/convert a <code>Calendar</code> using the specified
     *    <code>Locale</code> and default <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the date format, system default if null.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code> if invalid.
     */
    public Calendar validate(final String value, final Locale locale) {
        return (Calendar)parse(value, (String)null, locale, (TimeZone)null);
    }

    /**
     * <p>Validate/convert a <code>Calendar</code> using the specified
     *    <code>Locale</code> and <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the date format, system default if null.
     * @param timeZone The Time Zone used to parse the date, system default if null.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code> if invalid.
     */
    public Calendar validate(final String value, final Locale locale, final TimeZone timeZone) {
        return (Calendar)parse(value, (String)null, locale, timeZone);
    }

    /**
     * <p>Validate/convert a <code>Calendar</code> using the specified pattern
     *    and <code>Locale</code> and the default <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against, or the
     *        default for the <code>Locale</code> if <code>null</code>.
     * @param locale The locale to use for the date format, system default if null.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code> if invalid.
     */
    public Calendar validate(final String value, final String pattern, final Locale locale) {
        return (Calendar)parse(value, pattern, locale, (TimeZone)null);
    }

    /**
     * <p>Validate/convert a <code>Calendar</code> using the specified
     *    pattern, and <code>Locale</code> and <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against, or the
     *        default for the <code>Locale</code> if <code>null</code>.
     * @param locale The locale to use for the date format, system default if null.
     * @param timeZone The Time Zone used to parse the date, system default if null.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code> if invalid.
     */
    public Calendar validate(final String value, final String pattern, final Locale locale, final TimeZone timeZone) {
        return (Calendar)parse(value, pattern, locale, timeZone);
    }

    /**
     * <p>Adjusts a Calendar's value to a different TimeZone.</p>
     *
     * @param value The value to adjust.
     * @param timeZone The new time zone to use to adjust the Calendar to.
     */
    public static void adjustToTimeZone(final Calendar value, final TimeZone timeZone) {
        if (value.getTimeZone().hasSameRules(timeZone)) {
            value.setTimeZone(timeZone);
        } else {
            final int year   = value.get(Calendar.YEAR);
            final int month  = value.get(Calendar.MONTH);
            final int date   = value.get(Calendar.DATE);
            final int hour   = value.get(Calendar.HOUR_OF_DAY);
            final int minute = value.get(Calendar.MINUTE);
            value.setTimeZone(timeZone);
            value.set(year, month, date, hour, minute);
        }
    }

    /**
     * <p>Compare Dates (day, month and year - not time).</p>
     *
     * @param value The <code>Calendar</code> value to check.
     * @param compare The <code>Calendar</code> to compare the value to.
     * @return Zero if the dates are equal, -1 if first
     * date is less than the seconds and +1 if the first
     * date is greater than.
     */
    public int compareDates(final Calendar value, final Calendar compare) {
        return compare(value, compare, Calendar.DATE);
    }

    /**
     * <p>Compare Weeks (week and year).</p>
     *
     * @param value The <code>Calendar</code> value to check.
     * @param compare The <code>Calendar</code> to compare the value to.
     * @return Zero if the weeks are equal, -1 if first
     * parameter's week is less than the seconds and +1 if the first
     * parameter's week is greater than.
     */
    public int compareWeeks(final Calendar value, final Calendar compare) {
        return compare(value, compare, Calendar.WEEK_OF_YEAR);
    }

    /**
     * <p>Compare Months (month and year).</p>
     *
     * @param value The <code>Calendar</code> value to check.
     * @param compare The <code>Calendar</code> to compare the value to.
     * @return Zero if the months are equal, -1 if first
     * parameter's month is less than the seconds and +1 if the first
     * parameter's month is greater than.
     */
    public int compareMonths(final Calendar value, final Calendar compare) {
        return compare(value, compare, Calendar.MONTH);
    }

    /**
     * <p>Compare Quarters (quarter and year).</p>
     *
     * @param value The <code>Calendar</code> value to check.
     * @param compare The <code>Calendar</code> to check the value against.
     * @return Zero if the quarters are equal, -1 if first
     * parameter's quarter is less than the seconds and +1 if the first
     * parameter's quarter is greater than.
     */
    public int compareQuarters(final Calendar value, final Calendar compare) {
        return compareQuarters(value, compare, 1);
    }

    /**
     * <p>Compare Quarters (quarter and year).</p>
     *
     * @param value The <code>Calendar</code> value to check.
     * @param compare The <code>Calendar</code> to compare the value to.
     * @param monthOfFirstQuarter The  month that the first quarter starts.
     * @return Zero if the quarters are equal, -1 if first
     * parameter's quarter is less than the seconds and +1 if the first
     * parameter's quarter is greater than.
     */
    @Override
    public int compareQuarters(final Calendar value, final Calendar compare, final int monthOfFirstQuarter) {
        return super.compareQuarters(value, compare, monthOfFirstQuarter);
    }

    /**
     * <p>Compare Years.</p>
     *
     * @param value The <code>Calendar</code> value to check.
     * @param compare The <code>Calendar</code> to compare the value to.
     * @return Zero if the years are equal, -1 if first
     * parameter's year is less than the seconds and +1 if the first
     * parameter's year is greater than.
     */
    public int compareYears(final Calendar value, final Calendar compare) {
        return compare(value, compare, Calendar.YEAR);
    }

    /**
     * <p>Convert the parsed <code>Date</code> to a <code>Calendar</code>.</p>
     *
     * @param value The parsed <code>Date</code> object created.
     * @param formatter The Format used to parse the value with.
     * @return The parsed value converted to a <code>Calendar</code>.
     */
    @Override
    protected Object processParsedValue(final Object value, final Format formatter) {
        return ((DateFormat)formatter).getCalendar();
    }

}
