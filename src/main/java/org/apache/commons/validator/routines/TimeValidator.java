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
 * <p><b>Time Validation</b> and Conversion routines (<code>java.util.Calendar</code>).</p>
 *
 * <p>This validator provides a number of methods for validating/converting
 *    a <code>String</code> time value to a <code>java.util.Calendar</code> using
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
 *    <i>converted</i> <code>Calendar</code> value for the time.</p>
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
 *    methods can be used to perform various time comparison checks:</p>
 *    <ul>
 *       <li><code>compareTime()</code> compares the hours, minutes, seconds
 *           and milliseconds of two calendars, returning 0, -1 or +1 indicating
 *           whether the first time is equal, before or after the second.</li>
 *       <li><code>compareSeconds()</code> compares the hours, minutes and
 *           seconds of two times, returning 0, -1 or +1 indicating
 *           whether the first is equal to, before or after the second.</li>
 *       <li><code>compareMinutes()</code> compares the hours and minutes
 *           two times, returning 0, -1 or +1 indicating
 *           whether the first is equal to, before or after the second.</li>
 *       <li><code>compareHours()</code> compares the hours
 *           of two times, returning 0, -1 or +1 indicating
 *           whether the first is equal to, before or after the second.</li>
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
public class TimeValidator extends AbstractCalendarValidator {

    private static final long serialVersionUID = 3494007492269691581L;

    private static final TimeValidator VALIDATOR = new TimeValidator();

    /**
     * Return a singleton instance of this validator.
     * @return A singleton instance of the TimeValidator.
     */
    public static TimeValidator getInstance() {
        return VALIDATOR;
    }

    /**
     * Construct a <i>strict</i> instance with <i>short</i>
     * time style.
     */
    public TimeValidator() {
        this(true, DateFormat.SHORT);
    }

    /**
     * Construct an instance with the specified <i>strict</i>
     * and <i>time style</i> parameters.
     *
     * @param strict <code>true</code> if strict
     *        <code>Format</code> parsing should be used.
     * @param timeStyle the time style to use for Locale validation.
     */
    public TimeValidator(final boolean strict, final int timeStyle) {
        super(strict, -1, timeStyle);
    }

    /**
     * <p>Validate/convert a time using the default <code>Locale</code>
     *    and <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code>
     *  if invalid.
     */
    public Calendar validate(final String value) {
        return (Calendar)parse(value, (String)null, (Locale)null, (TimeZone)null);
    }

    /**
     * <p>Validate/convert a time using the specified <code>TimeZone</code>
     *    and default <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @param timeZone The Time Zone used to parse the time, system default if null.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code> if invalid.
     */
    public Calendar validate(final String value, final TimeZone timeZone) {
        return (Calendar)parse(value, (String)null, (Locale)null, timeZone);
    }

    /**
     * <p>Validate/convert a time using the specified <i>pattern</i> and
     *    default <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code> if invalid.
     */
    public Calendar validate(final String value, final String pattern) {
        return (Calendar)parse(value, pattern, (Locale)null, (TimeZone)null);
    }

    /**
     * <p>Validate/convert a time using the specified <i>pattern</i>
     *    and <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @param timeZone The Time Zone used to parse the time, system default if null.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code> if invalid.
     */
    public Calendar validate(final String value, final String pattern, final TimeZone timeZone) {
        return (Calendar)parse(value, pattern, (Locale)null, timeZone);
    }

    /**
     * <p>Validate/convert a time using the specified <code>Locale</code>
     *    default <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the time format, system default if null.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code> if invalid.
     */
    public Calendar validate(final String value, final Locale locale) {
        return (Calendar)parse(value, (String)null, locale, (TimeZone)null);
    }

    /**
     * <p>Validate/convert a time using the specified <code>Locale</code>
     *    and <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the time format, system default if null.
     * @param timeZone The Time Zone used to parse the time, system default if null.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code> if invalid.
     */
    public Calendar validate(final String value, final Locale locale, final TimeZone timeZone) {
        return (Calendar)parse(value, (String)null, locale, timeZone);
    }

    /**
     * <p>Validate/convert a time using the specified pattern and <code>Locale</code>
     *    and the default <code>TimeZone</code>.
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
     * <p>Validate/convert a time using the specified pattern, <code>Locale</code>
     *    and <code>TimeZone</code>.
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
     * <p>Compare Times (hour, minute, second and millisecond - not date).</p>
     *
     * @param value The <code>Calendar</code> value to check.
     * @param compare The <code>Calendar</code> to compare the value to.
     * @return Zero if the hours are equal, -1 if first
     * time is less than the seconds and +1 if the first
     * time is greater than.
     */
    public int compareTime(final Calendar value, final Calendar compare) {
        return compareTime(value, compare, Calendar.MILLISECOND);
    }

    /**
     * <p>Compare Seconds (hours, minutes and seconds).</p>
     *
     * @param value The <code>Calendar</code> value to check.
     * @param compare The <code>Calendar</code> to compare the value to.
     * @return Zero if the hours are equal, -1 if first
     * parameter's seconds are less than the seconds and +1 if the first
     * parameter's seconds are greater than.
     */
    public int compareSeconds(final Calendar value, final Calendar compare) {
        return compareTime(value, compare, Calendar.SECOND);
    }

    /**
     * <p>Compare Minutes (hours and minutes).</p>
     *
     * @param value The <code>Calendar</code> value to check.
     * @param compare The <code>Calendar</code> to compare the value to.
     * @return Zero if the hours are equal, -1 if first
     * parameter's minutes are less than the seconds and +1 if the first
     * parameter's minutes are greater than.
     */
    public int compareMinutes(final Calendar value, final Calendar compare) {
        return compareTime(value, compare, Calendar.MINUTE);
    }

    /**
     * <p>Compare Hours.</p>
     *
     * @param value The <code>Calendar</code> value to check.
     * @param compare The <code>Calendar</code> to compare the value to.
     * @return Zero if the hours are equal, -1 if first
     * parameter's hour is less than the seconds and +1 if the first
     * parameter's hour is greater than.
     */
    public int compareHours(final Calendar value, final Calendar compare) {
        return compareTime(value, compare, Calendar.HOUR_OF_DAY);
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
