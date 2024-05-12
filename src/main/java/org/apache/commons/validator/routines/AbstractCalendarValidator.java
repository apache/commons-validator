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
import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.validator.GenericValidator;

/**
 * <p>Abstract class for Date/Time/Calendar validation.</p>
 *
 * <p>This is a <i>base</i> class for building Date / Time
 *    Validators using format parsing.</p>
 *
 * @since 1.3.0
 */
public abstract class AbstractCalendarValidator extends AbstractFormatValidator {

    private static final long serialVersionUID = -1410008585975827379L;

    /**
     * The date style to use for Locale validation.
     */
    private final int dateStyle;

    /**
     * The time style to use for Locale validation.
     */
    private final int timeStyle;

    /**
     * Constructs an instance with the specified <i>strict</i>,
     * <i>time</i> and <i>date</i> style parameters.
     *
     * @param strict {@code true} if strict
     *        <code>Format</code> parsing should be used.
     * @param dateStyle the date style to use for Locale validation.
     * @param timeStyle the time style to use for Locale validation.
     */
    public AbstractCalendarValidator(final boolean strict, final int dateStyle, final int timeStyle) {
        super(strict);
        this.dateStyle = dateStyle;
        this.timeStyle = timeStyle;
    }

    /**
     * <p>Compares the field from two calendars indicating whether the field for the
     *    first calendar is equal to, less than or greater than the field from the
     *    second calendar.
     *
     * @param value The Calendar value.
     * @param compare The <code>Calendar</code> to check the value against.
     * @param field The field to compare for the calendars.
     * @return Zero if the first calendar's field is equal to the seconds, -1
     *         if it is less than the seconds or +1 if it is greater than the seconds.
     */
    private int calculateCompareResult(final Calendar value, final Calendar compare, final int field) {
        final int difference = value.get(field) - compare.get(field);
        if (difference < 0) {
            return -1;
        }
        if (difference > 0) {
            return 1;
        }
        return 0;
    }

    /**
     * <p>Calculate the quarter for the specified Calendar.</p>
     *
     * @param calendar The Calendar value.
     * @param monthOfFirstQuarter The  month that the first quarter starts.
     * @return The calculated quarter.
     */
    private int calculateQuarter(final Calendar calendar, final int monthOfFirstQuarter) {
        // Add Year
        int year = calendar.get(Calendar.YEAR);

        final int month = calendar.get(Calendar.MONTH) + 1;
        final int relativeMonth = month >= monthOfFirstQuarter
                          ? month - monthOfFirstQuarter
                          : month + 12 - monthOfFirstQuarter; // CHECKSTYLE IGNORE MagicNumber
        final int quarter = relativeMonth / 3 + 1; // CHECKSTYLE IGNORE MagicNumber
        // adjust the year if the quarter doesn't start in January
        if (month < monthOfFirstQuarter) {
            --year;
        }
        return year * 10 + quarter; // CHECKSTYLE IGNORE MagicNumber
    }

    /**
     * <p>Compares a calendar value to another, indicating whether it is
     *    equal, less then or more than at a specified level.</p>
     *
     * @param value The Calendar value.
     * @param compare The <code>Calendar</code> to check the value against.
     * @param field The field <i>level</i> to compare to - e.g. specifying
     *        <code>Calendar.MONTH</code> will compare the year and month
     *        portions of the calendar.
     * @return Zero if the first value is equal to the second, -1
     *         if it is less than the second or +1 if it is greater than the second.
     */
    protected int compare(final Calendar value, final Calendar compare, final int field) {

        int result;

        // Compare Year
        result = calculateCompareResult(value, compare, Calendar.YEAR);
        if (result != 0 || field == Calendar.YEAR) {
            return result;
        }

        // Compare Week of Year
        if (field == Calendar.WEEK_OF_YEAR) {
            return calculateCompareResult(value, compare, Calendar.WEEK_OF_YEAR);
        }

        // Compare Day of the Year
        if (field == Calendar.DAY_OF_YEAR) {
            return calculateCompareResult(value, compare, Calendar.DAY_OF_YEAR);
        }

        // Compare Month
        result = calculateCompareResult(value, compare, Calendar.MONTH);
        if (result != 0 || field == Calendar.MONTH) {
            return result;
        }

        // Compare Week of Month
        if (field == Calendar.WEEK_OF_MONTH) {
            return calculateCompareResult(value, compare, Calendar.WEEK_OF_MONTH);
        }

        // Compare Date
        result = calculateCompareResult(value, compare, Calendar.DATE);
        if (result != 0 || field == Calendar.DATE ||
                          field == Calendar.DAY_OF_WEEK ||
                          field == Calendar.DAY_OF_WEEK_IN_MONTH) {
            return result;
        }

        // Compare Time fields
        return compareTime(value, compare, field);

    }

    /**
     * <p>Compares a calendar's quarter value to another, indicating whether it is
     *    equal, less then or more than the specified quarter.</p>
     *
     * @param value The Calendar value.
     * @param compare The <code>Calendar</code> to check the value against.
     * @param monthOfFirstQuarter The  month that the first quarter starts.
     * @return Zero if the first quarter is equal to the second, -1
     *         if it is less than the second or +1 if it is greater than the second.
     */
    protected int compareQuarters(final Calendar value, final Calendar compare, final int monthOfFirstQuarter) {
        final int valueQuarter = calculateQuarter(value, monthOfFirstQuarter);
        final int compareQuarter = calculateQuarter(compare, monthOfFirstQuarter);
        if (valueQuarter < compareQuarter) {
            return -1;
        }
        if (valueQuarter > compareQuarter) {
            return 1;
        }
        return 0;
    }

    /**
     * <p>Compares a calendar time value to another, indicating whether it is
     *    equal, less then or more than at a specified level.</p>
     *
     * @param value The Calendar value.
     * @param compare The <code>Calendar</code> to check the value against.
     * @param field The field <i>level</i> to compare to - e.g. specifying
     *        <code>Calendar.MINUTE</code> will compare the hours and minutes
     *        portions of the calendar.
     * @return Zero if the first value is equal to the second, -1
     *         if it is less than the second or +1 if it is greater than the second.
     */
    protected int compareTime(final Calendar value, final Calendar compare, final int field) {

        int result;

        // Compare Hour
        result = calculateCompareResult(value, compare, Calendar.HOUR_OF_DAY);
        if (result != 0 || field == Calendar.HOUR || field == Calendar.HOUR_OF_DAY) {
            return result;
        }

        // Compare Minute
        result = calculateCompareResult(value, compare, Calendar.MINUTE);
        if (result != 0 || field == Calendar.MINUTE) {
            return result;
        }

        // Compare Second
        result = calculateCompareResult(value, compare, Calendar.SECOND);
        if (result != 0 || field == Calendar.SECOND) {
            return result;
        }

        // Compare Milliseconds
        if (field == Calendar.MILLISECOND) {
            return calculateCompareResult(value, compare, Calendar.MILLISECOND);
        }

        throw new IllegalArgumentException("Invalid field: " + field);

    }

    /**
     * <p>Format a value with the specified <code>DateFormat</code>.</p>
     *
     * @param value The value to be formatted.
     * @param formatter The Format to use.
     * @return The formatted value.
     */
    @Override
    protected String format(Object value, final Format formatter) {
        if (value == null) {
            return null;
        }
        if (value instanceof Calendar) {
            value = ((Calendar) value).getTime();
        }
        return formatter.format(value);
    }

    /**
     * <p>Format an object into a <code>String</code> using
     * the specified Locale.</p>
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the Format.
     * @param timeZone The Time Zone used to format the date,
     *  system default if null (unless value is a <code>Calendar</code>.
     * @return The value formatted as a <code>String</code>.
     */
    public String format(final Object value, final Locale locale, final TimeZone timeZone) {
        return format(value, (String) null, locale, timeZone);
    }

    /**
     * <p>Format an object using the specified pattern and/or
     *    <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to format the value.
     * @param locale The locale to use for the Format.
     * @return The value formatted as a <code>String</code>.
     */
    @Override
    public String format(final Object value, final String pattern, final Locale locale) {
        return format(value, pattern, locale, (TimeZone) null);
    }

    /**
     * <p>Format an object using the specified pattern and/or
     *    <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to format the value.
     * @param locale The locale to use for the Format.
     * @param timeZone The Time Zone used to format the date,
     *  system default if null (unless value is a <code>Calendar</code>.
     * @return The value formatted as a <code>String</code>.
     */
    public String format(final Object value, final String pattern, final Locale locale, final TimeZone timeZone) {
        final DateFormat formatter = (DateFormat) getFormat(pattern, locale);
        if (timeZone != null) {
            formatter.setTimeZone(timeZone);
        } else if (value instanceof Calendar) {
            formatter.setTimeZone(((Calendar) value).getTimeZone());
        }
        return format(value, formatter);
    }

    /**
     * <p>Format an object into a <code>String</code> using
     * the specified pattern.</p>
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to format the value.
     * @param timeZone The Time Zone used to format the date,
     *  system default if null (unless value is a <code>Calendar</code>.
     * @return The value formatted as a <code>String</code>.
     */
    public String format(final Object value, final String pattern, final TimeZone timeZone) {
        return format(value, pattern, (Locale) null, timeZone);
    }

    /**
     * <p>Format an object into a <code>String</code> using
     * the default Locale.</p>
     *
     * @param value The value validation is being performed on.
     * @param timeZone The Time Zone used to format the date,
     *  system default if null (unless value is a <code>Calendar</code>.
     * @return The value formatted as a <code>String</code>.
     */
    public String format(final Object value, final TimeZone timeZone) {
        return format(value, (String) null, (Locale) null, timeZone);
    }

    /**
     * <p>Returns a <code>DateFormat</code> for the specified Locale.</p>
     *
     * @param locale The locale a <code>DateFormat</code> is required for,
     *        system default if null.
     * @return The <code>DateFormat</code> to created.
     */
    protected Format getFormat(final Locale locale) {
        DateFormat formatter;
        if (dateStyle >= 0 && timeStyle >= 0) {
            if (locale == null) {
                formatter = DateFormat.getDateTimeInstance(dateStyle, timeStyle);
            } else {
                formatter = DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
            }
        } else if (timeStyle >= 0) {
            if (locale == null) {
                formatter = DateFormat.getTimeInstance(timeStyle);
            } else {
                formatter = DateFormat.getTimeInstance(timeStyle, locale);
            }
        } else {
            final int useDateStyle = dateStyle >= 0 ? dateStyle : DateFormat.SHORT;
            if (locale == null) {
                formatter = DateFormat.getDateInstance(useDateStyle);
            } else {
                formatter = DateFormat.getDateInstance(useDateStyle, locale);
            }
        }
        formatter.setLenient(false);
        return formatter;

    }

    /**
     * <p>Returns a <code>DateFormat</code> for the specified <i>pattern</i>
     *    and/or <code>Locale</code>.</p>
     *
     * @param pattern The pattern used to validate the value against or
     *        {@code null} to use the default for the <code>Locale</code>.
     * @param locale The locale to use for the currency format, system default if null.
     * @return The <code>DateFormat</code> to created.
     */
    @Override
    protected Format getFormat(final String pattern, final Locale locale) {
        DateFormat formatter;
        final boolean usePattern = !GenericValidator.isBlankOrNull(pattern);
        if (!usePattern) {
            formatter = (DateFormat) getFormat(locale);
        } else if (locale == null) {
            formatter = new SimpleDateFormat(pattern);
        } else {
            final DateFormatSymbols symbols = new DateFormatSymbols(locale);
            formatter = new SimpleDateFormat(pattern, symbols);
        }
        formatter.setLenient(false);
        return formatter;
    }

    /**
     * <p>Validate using the specified <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to format the value.
     * @param locale The locale to use for the Format, defaults to the default
     * @return {@code true} if the value is valid.
     */
    @Override
    public boolean isValid(final String value, final String pattern, final Locale locale) {
        return parse(value, pattern, locale, (TimeZone) null) != null;
    }

    /**
     * <p>Checks if the value is valid against a specified pattern.</p>
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against, or the
     *        default for the <code>Locale</code> if {@code null}.
     * @param locale The locale to use for the date format, system default if null.
     * @param timeZone The Time Zone used to parse the date, system default if null.
     * @return The parsed value if valid or {@code null} if invalid.
     */
    protected Object parse(String value, final String pattern, final Locale locale, final TimeZone timeZone) {
        value = value == null ? null : value.trim();
        final String value1 = value;
        if (GenericValidator.isBlankOrNull(value1)) {
            return null;
        }
        final DateFormat formatter = (DateFormat) getFormat(pattern, locale);
        if (timeZone != null) {
            formatter.setTimeZone(timeZone);
        }
        return parse(value, formatter);

    }

    /**
     * <p>Process the parsed value, performing any further validation
     *    and type conversion required.</p>
     *
     * @param value The parsed object created.
     * @param formatter The Format used to parse the value with.
     * @return The parsed value converted to the appropriate type
     *         if valid or {@code null} if invalid.
     */
    @Override
    protected abstract Object processParsedValue(Object value, Format formatter);
}
