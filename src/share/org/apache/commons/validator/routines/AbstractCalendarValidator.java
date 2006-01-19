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

import java.text.Format;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p>Abstract class for Date/Time/Calendar validation.</p>
 * 
 * <p>This is a <i>base</i> class for building Date / Time
 *    Validators using format parsing.</p>
 *    
 * @version $Revision$ $Date$
 * @since Validator 1.2.1
 */
public abstract class AbstractCalendarValidator extends AbstractFormatValidator {

    private int dateStyle = -1;

    private int timeStyle = -1;

    /**
     * Construct a <i>strict</i> instance with <i>short</i>
     * date style.
     */
    public AbstractCalendarValidator() {
        this(true, DateFormat.SHORT, -1);
    }

    /**
     * Construct an instance with the specified <i>strict</i>, 
     * <i>time</i> and <i>date</i> style parameters.
     * 
     * @param strict <code>true</code> if strict 
     *        <code>Format</code> parsing should be used.
     * @param dateStyle the date style to use for Locale validation.
     * @param timeStyle the time style to use for Locale validation.
     */
    public AbstractCalendarValidator(boolean strict, int dateStyle, int timeStyle) {
        super(strict);
        this.dateStyle = dateStyle;
        this.timeStyle = timeStyle;
    }

    /**
     * <p>Creates a <code>SimpleDateFormat</code> for the specified
     *    pattern.</p>
     * 
     * <p>If no pattern is specified the default Locale is used
     *    to determine the <code>DateFormat</code>.
     * 
     * @param pattern The pattern of the required the <code>DateFormat</code>.
     * @return The <code>DateFormat</code> to created.
     */
    protected Format getFormat(String pattern) {

        DateFormat formatter = null; 
        if (pattern == null || pattern.length() == 0) {
            formatter = (DateFormat)getFormat(Locale.getDefault());
        } else {
            formatter = new SimpleDateFormat(pattern);
        }
        formatter.setLenient(false);
        return formatter;

    }

    /**
     * <p>Returns a <code>DateFormat</code> for the specified Locale.</p>
     * 
     * @param locale The locale a <code>DateFormat</code> is required for,
     *        system default if null.
     * @return The <code>DateFormat</code> to created.
     */
    protected Format getFormat(Locale locale) {

        if (locale == null) {
            locale = Locale.getDefault();
        }

        DateFormat formatter = null; 
        if (dateStyle >= 0 && timeStyle >= 0) {
            formatter = DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
        } else if (timeStyle >= 0) {
            formatter = DateFormat.getTimeInstance(timeStyle, locale);
        } else if (dateStyle >= 0) {
            formatter = DateFormat.getDateInstance(dateStyle, locale);
        } else {
            formatter = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        }
        formatter.setLenient(false);
        return formatter;

    }

    /**
     * <p>Checks if the value is valid against a specified pattern.</p>
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @return The parsed value if valid or <code>null</code> if invalid.
     */
    protected Object validateObj(String value, String pattern) {
        return validateObj(value, pattern, (TimeZone)null);
    }

    /**
     * <p>Checks if the value is valid against a specified pattern.</p>
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @param timeZone The Time Zone used to parse the date, system default if null.
     * @return The parsed value if valid or <code>null</code> if invalid.
     */
    protected Object validateObj(String value, String pattern, TimeZone timeZone) {

        if (value == null || value.length() == 0) {
            return null;
        }
        DateFormat formatter = (DateFormat)getFormat(pattern);
        if (timeZone != null) {
            formatter.setTimeZone(timeZone);
        }
        return parse(value, formatter);

    }

    /**
     * <p>Checks if the value is valid for a specified <code>Locale</code>.</p>
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the date format, system default if null.
     * @return The parsed value if valid or <code>null</code> if invalid.
     */
    protected Object validateObj(String value, Locale locale) {
        return validateObj(value, locale, (TimeZone)null);
    }

    /**
     * <p>Checks if the value is valid for a specified <code>Locale</code>.</p>
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the date format, system default if null.
     * @param timeZone The Time Zone used to parse the date, system default if null.
     * @return The parsed value if valid or <code>null</code> if invalid.
     */
    protected Object validateObj(String value, Locale locale, TimeZone timeZone) {

        if (value == null || value.length() == 0) {
            return null;
        }
        DateFormat formatter = (DateFormat)getFormat(locale);
        if (timeZone != null) {
            formatter.setTimeZone(timeZone);
        }
        return parse(value, formatter);

    }

    /**
     * <p>Parse the value with the specified <code>Format</code>.</p>
     * 
     * @param value The value to be parsed.
     * @param formatter The Format to parse the value with.
     * @return The parsed value if valid or <code>null</code> if invalid.
     */
    protected Object parse(String value, Format formatter) {

        Object parsedValue = super.parse(value, formatter);
        if (parsedValue == null) {
            return null;
        }

        // Process the Calendar, not the parsed Date object
        Calendar calendar = ((DateFormat)formatter).getCalendar();
        return processCalendar(calendar);

    }

    /**
     * <p>Perform further validation and convert the <code>Calendar</code> to
     * the appropriate type.</p>
     * 
     * @param calendar The calendar object create from the parsed value.
     * @return The validated/converted <code>Calendar</code> value if valid 
     * or <code>null</code> if invalid.
     */
    protected abstract Object processCalendar(Calendar calendar);

    /**
     * <p>Format a value with the specified <code>DateFormat</code>.</p>
     * 
     * @param value The value to be formatted.
     * @param formatter The Format to use.
     * @return The formatted value.
     */
    protected String format(Object value, Format formatter) {
        if (value instanceof Calendar) {
            value = ((Calendar)value).getTime(); 
        }
        return formatter.format(value);
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
    protected int compare(Calendar value, Calendar compare, int field) {

        int result = 0;

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
        if (result != 0 || (field == Calendar.DATE || 
                          field == Calendar.DAY_OF_MONTH ||
                          field == Calendar.DAY_OF_WEEK ||
                          field == Calendar.DAY_OF_WEEK_IN_MONTH)) {
            return result;
        }

        // Compare Time fields
        return compareTime(value, compare, field);

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
    protected int compareTime(Calendar value, Calendar compare, int field) {

        int result = 0;

        // Compare Hour
        result = calculateCompareResult(value, compare, Calendar.HOUR_OF_DAY);
        if (result != 0 || (field == Calendar.HOUR || field == Calendar.HOUR_OF_DAY)) {
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
     * <p>Compares a calendar's quarter value to another, indicating whether it is
     *    equal, less then or more than the specified quarter.</p>
     * 
     * @param value The Calendar value.
     * @param compare The <code>Calendar</code> to check the value against.
     * @param monthOfFirstQuarter The  month that the first quarter starts.
     * @return Zero if the first quarter is equal to the second, -1
     *         if it is less than the second or +1 if it is greater than the second.  
     */
    protected int compareQuarters(Calendar value, Calendar compare, int monthOfFirstQuarter) {
        int valueQuarter   = calculateQuarter(value, monthOfFirstQuarter);
        int compareQuarter = calculateQuarter(compare, monthOfFirstQuarter);
        if (valueQuarter < compareQuarter) {
            return -1;
        } else if (valueQuarter > compareQuarter) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * <p>Calculate the quarter for the specified Calendar.</p>
     * 
     * @param calendar The Calendar value.
     * @param monthOfFirstQuarter The  month that the first quarter starts.
     * @return The calculated quarter.
     */
    private int calculateQuarter(Calendar calendar, int monthOfFirstQuarter) {
        // Add Year
        int year = calendar.get(Calendar.YEAR);

        int month = (calendar.get(Calendar.MONTH) + 1);
        int relativeMonth = (month >= monthOfFirstQuarter)
                          ? (month - monthOfFirstQuarter)
                          : (month + (12 - monthOfFirstQuarter));
        int quarter = ((relativeMonth / 3) + 1);
        // adjust the year if the quarter doesn't start in January
        if (month < monthOfFirstQuarter) {
            --year;
        }
        return (year * 10) + quarter;
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
    private int calculateCompareResult(Calendar value, Calendar compare, int field) {
        int difference = value.get(field) - compare.get(field);
        if (difference < 0) {
            return -1;
        } else if (difference > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
