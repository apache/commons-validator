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
 * <p><strong>Calendar Validation</strong> and Conversion routines ({@code java.util.Calendar}).</p>
 *
 * <p>This validator provides a number of methods for validating/converting
 *    a {@link String} date value to a {@code java.util.Calendar} using
 *    {@link DateFormat} to parse either:</p>
 *    <ul>
 *       <li>using the default format for the default {@link Locale}</li>
 *       <li>using a specified pattern with the default {@link Locale}</li>
 *       <li>using the default format for a specified {@link Locale}</li>
 *       <li>using a specified pattern with a specified {@link Locale}</li>
 *    </ul>
 *
 * <p>For each of the above mechanisms, conversion method (i.e the
 *    {@code validate} methods) implementations are provided which
 *    either use the default {@code TimeZone} or allow the
 *    {@code TimeZone} to be specified.</p>
 *
 * <p>Use one of the {@code isValid()} methods to just validate or
 *    one of the {@code validate()} methods to validate and receive a
 *    <em>converted</em> {@link Calendar} value.</p>
 *
 * <p>Implementations of the {@code validate()} method are provided
 *    to create {@link Calendar} objects for different <em>time zones</em>
 *    if the system default is not appropriate.</p>
 *
 * <p>Alternatively the CalendarValidator's {@code adjustToTimeZone()} method
 *    can be used to adjust the {@code TimeZone} of the {@link Calendar}
 *    object afterwards.</p>
 *
 * <p>Once a value has been successfully converted the following
 *    methods can be used to perform various date comparison checks:</p>
 *    <ul>
 *       <li>{@code compareDates()} compares the day, month and
 *           year of two calendars, returning 0, -1 or +1 indicating
 *           whether the first date is equal, before or after the second.</li>
 *       <li>{@code compareWeeks()} compares the week and
 *           year of two calendars, returning 0, -1 or +1 indicating
 *           whether the first week is equal, before or after the second.</li>
 *       <li>{@code compareMonths()} compares the month and
 *           year of two calendars, returning 0, -1 or +1 indicating
 *           whether the first month is equal, before or after the second.</li>
 *       <li>{@code compareQuarters()} compares the quarter and
 *           year of two calendars, returning 0, -1 or +1 indicating
 *           whether the first quarter is equal, before or after the second.</li>
 *       <li>{@code compareYears()} compares the
 *           year of two calendars, returning 0, -1 or +1 indicating
 *           whether the first year is equal, before or after the second.</li>
 *    </ul>
 *
 * <p>So that the same mechanism used for parsing an <em>input</em> value
 *    for validation can be used to format <em>output</em>, corresponding
 *    {@code format()} methods are also provided. That is you can
 *    format either:</p>
 *    <ul>
 *       <li>using a specified pattern</li>
 *       <li>using the format for a specified {@link Locale}</li>
 *       <li>using the format for the <em>default</em> {@link Locale}</li>
 *    </ul>
 *
 * @since 1.3.0
 */
public class CalendarValidator extends AbstractCalendarValidator {

    private static final long serialVersionUID = 9109652318762134167L;

    private static final CalendarValidator VALIDATOR = new CalendarValidator();

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
            final int year = value.get(Calendar.YEAR);
            final int month = value.get(Calendar.MONTH);
            final int date = value.get(Calendar.DATE);
            final int hour = value.get(Calendar.HOUR_OF_DAY);
            final int minute = value.get(Calendar.MINUTE);
            value.setTimeZone(timeZone);
            value.set(year, month, date, hour, minute);
        }
    }

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the CalendarValidator.
     */
    public static CalendarValidator getInstance() {
        return VALIDATOR;
    }

    /**
     * Constructs a <em>strict</em> instance with <em>short</em>
     * date style.
     */
    public CalendarValidator() {
        this(true, DateFormat.SHORT);
    }

    /**
     * Constructs an instance with the specified <em>strict</em>
     * and <em>date style</em> parameters.
     *
     * @param strict {@code true} if strict
     *        {@code Format} parsing should be used.
     * @param dateStyle the date style to use for Locale validation.
     */
    public CalendarValidator(final boolean strict, final int dateStyle) {
        super(strict, dateStyle, -1);
    }

    /**
     * <p>Compare Dates (day, month and year - not time).</p>
     *
     * @param value The {@link Calendar} value to check.
     * @param compare The {@link Calendar} to compare the value to.
     * @return Zero if the dates are equal, -1 if first
     * date is less than the seconds and +1 if the first
     * date is greater than.
     */
    public int compareDates(final Calendar value, final Calendar compare) {
        return compare(value, compare, Calendar.DATE);
    }

    /**
     * <p>Compare Months (month and year).</p>
     *
     * @param value The {@link Calendar} value to check.
     * @param compare The {@link Calendar} to compare the value to.
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
     * @param value The {@link Calendar} value to check.
     * @param compare The {@link Calendar} to check the value against.
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
     * @param value The {@link Calendar} value to check.
     * @param compare The {@link Calendar} to compare the value to.
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
     * <p>Compare Weeks (week and year).</p>
     *
     * @param value The {@link Calendar} value to check.
     * @param compare The {@link Calendar} to compare the value to.
     * @return Zero if the weeks are equal, -1 if first
     * parameter's week is less than the seconds and +1 if the first
     * parameter's week is greater than.
     */
    public int compareWeeks(final Calendar value, final Calendar compare) {
        return compare(value, compare, Calendar.WEEK_OF_YEAR);
    }

    /**
     * <p>Compare Years.</p>
     *
     * @param value The {@link Calendar} value to check.
     * @param compare The {@link Calendar} to compare the value to.
     * @return Zero if the years are equal, -1 if first
     * parameter's year is less than the seconds and +1 if the first
     * parameter's year is greater than.
     */
    public int compareYears(final Calendar value, final Calendar compare) {
        return compare(value, compare, Calendar.YEAR);
    }

    /**
     * <p>Convert the parsed {@code Date} to a {@link Calendar}.</p>
     *
     * @param value The parsed {@code Date} object created.
     * @param formatter The Format used to parse the value with.
     * @return The parsed value converted to a {@link Calendar}.
     */
    @Override
    protected Object processParsedValue(final Object value, final Format formatter) {
        return ((DateFormat) formatter).getCalendar();
    }

    /**
     * <p>Validate/convert a {@link Calendar} using the default
     *    {@link Locale} and {@code TimeZone}.
     *
     * @param value The value validation is being performed on.
     * @return The parsed {@link Calendar} if valid or {@code null}
     *  if invalid.
     */
    public Calendar validate(final String value) {
        return (Calendar) parse(value, (String) null, (Locale) null, (TimeZone) null);
    }

    /**
     * <p>Validate/convert a {@link Calendar} using the specified
     *    {@link Locale} and default {@code TimeZone}.
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the date format, system default if null.
     * @return The parsed {@link Calendar} if valid or {@code null} if invalid.
     */
    public Calendar validate(final String value, final Locale locale) {
        return (Calendar) parse(value, (String) null, locale, (TimeZone) null);
    }

    /**
     * <p>Validate/convert a {@link Calendar} using the specified
     *    {@link Locale} and {@code TimeZone}.
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the date format, system default if null.
     * @param timeZone The Time Zone used to parse the date, system default if null.
     * @return The parsed {@link Calendar} if valid or {@code null} if invalid.
     */
    public Calendar validate(final String value, final Locale locale, final TimeZone timeZone) {
        return (Calendar) parse(value, (String) null, locale, timeZone);
    }

    /**
     * <p>Validate/convert a {@link Calendar} using the specified
     *    <em>pattern</em> and default {@code TimeZone}.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @return The parsed {@link Calendar} if valid or {@code null} if invalid.
     */
    public Calendar validate(final String value, final String pattern) {
        return (Calendar) parse(value, pattern, (Locale) null, (TimeZone) null);
    }

    /**
     * <p>Validate/convert a {@link Calendar} using the specified pattern
     *    and {@link Locale} and the default {@code TimeZone}.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against, or the
     *        default for the {@link Locale} if {@code null}.
     * @param locale The locale to use for the date format, system default if null.
     * @return The parsed {@link Calendar} if valid or {@code null} if invalid.
     */
    public Calendar validate(final String value, final String pattern, final Locale locale) {
        return (Calendar) parse(value, pattern, locale, (TimeZone) null);
    }

    /**
     * <p>Validate/convert a {@link Calendar} using the specified
     *    pattern, and {@link Locale} and {@code TimeZone}.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against, or the
     *        default for the {@link Locale} if {@code null}.
     * @param locale The locale to use for the date format, system default if null.
     * @param timeZone The Time Zone used to parse the date, system default if null.
     * @return The parsed {@link Calendar} if valid or {@code null} if invalid.
     */
    public Calendar validate(final String value, final String pattern, final Locale locale, final TimeZone timeZone) {
        return (Calendar) parse(value, pattern, locale, timeZone);
    }

    /**
     * <p>Validate/convert a {@link Calendar} using the specified
     *    <em>pattern</em> and {@code TimeZone}.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @param timeZone The Time Zone used to parse the date, system default if null.
     * @return The parsed {@link Calendar} if valid or {@code null} if invalid.
     */
    public Calendar validate(final String value, final String pattern, final TimeZone timeZone) {
        return (Calendar) parse(value, pattern, (Locale) null, timeZone);
    }

    /**
     * <p>Validate/convert a {@link Calendar} using the specified
     *    {@code TimeZone} and default {@link Locale}.
     *
     * @param value The value validation is being performed on.
     * @param timeZone The Time Zone used to parse the date, system default if null.
     * @return The parsed {@link Calendar} if valid or {@code null}
     *  if invalid.
     */
    public Calendar validate(final String value, final TimeZone timeZone) {
        return (Calendar) parse(value, (String) null, (Locale) null, timeZone);
    }

}
