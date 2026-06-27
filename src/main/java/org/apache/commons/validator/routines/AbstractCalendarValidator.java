/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
import java.util.concurrent.TimeUnit;

import org.apache.commons.validator.GenericValidator;

/**
 * Abstract class for Date/Time/Calendar validation.
 *
 * <p>This is a <em>base</em> class for building Date / Time
 *    Validators using format parsing.</p>
 *
 * @since 1.3.0
 */
public abstract class AbstractCalendarValidator extends AbstractFormatValidator {

    private static final long serialVersionUID = -1410008585975827379L;

    /** Number of milliseconds in a week, beyond which two instants cannot share a week. */
    private static final long MILLIS_PER_WEEK = TimeUnit.DAYS.toMillis(7);

    /**
     * The date style to use for Locale validation.
     */
    private final int dateStyle;

    /**
     * The time style to use for Locale validation.
     */
    private final int timeStyle;

    /**
     * Constructs an instance with the specified <em>strict</em>,
     * <em>time</em> and <em>date</em> style parameters.
     *
     * @param strict {@code true} if strict
     *        {@code Format} parsing should be used.
     * @param dateStyle the date style to use for Locale validation.
     * @param timeStyle the time style to use for Locale validation.
     */
    public AbstractCalendarValidator(final boolean strict, final int dateStyle, final int timeStyle) {
        super(strict);
        this.dateStyle = dateStyle;
        this.timeStyle = timeStyle;
    }

    /**
     * Compares the field from two calendars indicating whether the field for the
     *    first calendar is equal to, less than or greater than the field from the
     *    second calendar.
     *
     * @param value The Calendar value.
     * @param compare The {@link Calendar} to check the value against.
     * @param field The field to compare for the calendars.
     * @return Zero if the first calendar's field is equal to the seconds, -1
     *         if it is less than the seconds or +1 if it is greater than the seconds.
     */
    private int calculateCompareResult(final Calendar value, final Calendar compare, final int field) {
        return Integer.compare(value.get(field), compare.get(field));
    }

    /**
     * Calculate the quarter for the specified Calendar.
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
     * Compares a calendar value to another, indicating whether it is
     *    equal, less than or more than at a specified level.
     *
     * @param value The Calendar value.
     * @param compare The {@link Calendar} to check the value against.
     * @param field The field <em>level</em> to compare to - for example, specifying
     *        {@code Calendar.MONTH} will compare the year and month
     *        portions of the calendar.
     * @return Zero if the first value is equal to the second, -1
     *         if it is less than the second or +1 if it is greater than the second.
     */
    protected int compare(final Calendar value, final Calendar compare, final int field) {

        int result;

        // Week of Year and Week of Month numbers repeat across the boundaries they reset on, and a
        // week can belong to a different calendar year or month than its number suggests (for
        // example 31 December may fall in week 1 of the following year), so the week is compared by
        // day distance and week number rather than by comparing the calendar year first.
        if (field == Calendar.WEEK_OF_YEAR || field == Calendar.WEEK_OF_MONTH) {
            return compareWeek(value, compare, field);
        }

        // Compare Year
        result = calculateCompareResult(value, compare, Calendar.YEAR);
        if (result != 0 || field == Calendar.YEAR) {
            return result;
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
     * Compares a calendar's quarter value to another, indicating whether it is
     *    equal, less than or more than the specified quarter.
     *
     * @param value The Calendar value.
     * @param compare The {@link Calendar} to check the value against.
     * @param monthOfFirstQuarter The  month that the first quarter starts.
     * @return Zero if the first quarter is equal to the second, -1
     *         if it is less than the second or +1 if it is greater than the second.
     */
    protected int compareQuarters(final Calendar value, final Calendar compare, final int monthOfFirstQuarter) {
        final int valueQuarter = calculateQuarter(value, monthOfFirstQuarter);
        final int compareQuarter = calculateQuarter(compare, monthOfFirstQuarter);
        return Integer.compare(valueQuarter, compareQuarter);
    }

    /**
     * Compares a calendar time value to another, indicating whether it is
     *    equal, less than or more than at a specified level.
     *
     * @param value The Calendar value.
     * @param compare The {@link Calendar} to check the value against.
     * @param field The field <em>level</em> to compare to - for example, specifying
     *        {@code Calendar.MINUTE} will compare the hours and minutes
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
     * Format a value with the specified {@code DateFormat}.
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
     * Format an object into a {@link String} using
     * the specified Locale.
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the Format.
     * @param timeZone The Time Zone used to format the date,
     *  system default if null unless value is a {@link Calendar}.
     * @return The value formatted as a {@link String}.
     */
    public String format(final Object value, final Locale locale, final TimeZone timeZone) {
        return format(value, (String) null, locale, timeZone);
    }

    /**
     * Format an object using the specified pattern and/or
     *    {@link Locale}.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to format the value.
     * @param locale The locale to use for the Format.
     * @return The value formatted as a {@link String}.
     */
    @Override
    public String format(final Object value, final String pattern, final Locale locale) {
        return format(value, pattern, locale, (TimeZone) null);
    }

    /**
     * Format an object using the specified pattern and/or
     *    {@link Locale}.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to format the value.
     * @param locale The locale to use for the Format.
     * @param timeZone The Time Zone used to format the date,
     *  system default if null unless value is a {@link Calendar}.
     * @return The value formatted as a {@link String}.
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
     * Format an object into a {@link String} using
     * the specified pattern.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to format the value.
     * @param timeZone The Time Zone used to format the date,
     *  system default if null unless value is a {@link Calendar}.
     * @return The value formatted as a {@link String}.
     */
    public String format(final Object value, final String pattern, final TimeZone timeZone) {
        return format(value, pattern, (Locale) null, timeZone);
    }

    /**
     * Format an object into a {@link String} using
     * the default Locale.
     *
     * @param value The value validation is being performed on.
     * @param timeZone The Time Zone used to format the date,
     *  system default if null unless value is a {@link Calendar}.
     * @return The value formatted as a {@link String}.
     */
    public String format(final Object value, final TimeZone timeZone) {
        return format(value, (String) null, (Locale) null, timeZone);
    }

    /**
     * Returns a {@code DateFormat} for the specified Locale.
     *
     * @param locale The locale a {@code DateFormat} is required for,
     *        system default if null.
     * @return The {@code DateFormat} to created.
     */
    protected Format getFormat(final Locale locale) {
        final DateFormat formatter;
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
     * Returns a {@code DateFormat} for the specified <em>pattern</em>
     *    and/or {@link Locale}.
     *
     * @param pattern The pattern used to validate the value against or
     *        {@code null} to use the default for the {@link Locale}.
     * @param locale The locale to use for the currency format, system default if null.
     * @return The {@code DateFormat} to created.
     */
    @Override
    protected Format getFormat(final String pattern, final Locale locale) {
        final DateFormat formatter;
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
     * Validate using the specified {@link Locale}.
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
     * Checks if the value is valid against a specified pattern.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against, or the
     *        default for the {@link Locale} if {@code null}.
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
     * rocess the parsed value, performing any further validation
     *    and type conversion required.
     *
     * @param value The parsed object created.
     * @param formatter The Format used to parse the value with.
     * @return The parsed value converted to the appropriate type
     *         if valid or {@code null} if invalid.
     */
    @Override
    protected abstract Object processParsedValue(Object value, Format formatter);

    /**
     * Compares the week two calendars fall in, ordering by the actual week rather than by the
     * {@code WEEK_OF_YEAR} or {@code WEEK_OF_MONTH} number alone. Those numbers repeat across the
     * boundaries they reset on (for example 31 December may be week 1 of the following year, and
     * the first week of a month can hold days carried over from the previous month), so the gap
     * between the two instants is checked first: dates a week or more apart are always in different
     * weeks, and nearer dates share a week only when the week number also matches.
     *
     * @param value The Calendar value.
     * @param compare The {@link Calendar} to check the value against.
     * @param field {@code Calendar.WEEK_OF_YEAR} or {@code Calendar.WEEK_OF_MONTH}.
     * @return Zero if both calendars are in the same week, -1 or +1 otherwise.
     */
    private int compareWeek(final Calendar value, final Calendar compare, final int field) {
        final long millis = value.getTimeInMillis() - compare.getTimeInMillis();
        if (Math.abs(millis) >= MILLIS_PER_WEEK || calculateCompareResult(value, compare, field) != 0) {
            return Long.signum(millis);
        }
        return 0;
    }
}
