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
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p><b>Time Validation</b> and Conversion routines (<code>java.util.Calendar</code>).</p>
 *
 * <p>This validator provides a number of methods for
 *    validating/converting a <code>String</code> value to
 *    a Time using <code>java.text.DateFormat</code>
 *    to parse either:</p>
 *    <ul>
 *       <li>using a specified pattern</li>
 *       <li>using the format for a specified <code>Locale</code></li>
 *       <li>using the format for the <i>default</i> <code>Locale</code></li>
 *    </ul>
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
 * <p>Once a value has been sucessfully converted the following
 *    methods can be used to perform various date comparison checks:</p>
 *    <ul>
 *       <li><code>compareTime()</code> compares the hours, minutes, seconds
 *           and milliseconds of two calendars, returing 0, -1 or +1 indicating
 *           whether the first time is equal, before or after the second.</li>
 *       <li><code>compareSeconds()</code> compares the hours, minutes and
 *           seconds of two times, returing 0, -1 or +1 indicating
 *           whether the first is equal to, before or after the second.</li>
 *       <li><code>compareMinutes()</code> compares the hours and minutes
 *           two times, returing 0, -1 or +1 indicating
 *           whether the first is equal to, before or after the second.</li>
 *       <li><code>compareHours()</code> compares the hours
 *           of two times, returing 0, -1 or +1 indicating
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
 * @version $Revision$ $Date$
 * @since Validator 1.2.1
 */
public class TimeValidator extends AbstractCalendarValidator {

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
    public TimeValidator(boolean strict, int timeStyle) {
        super(strict, -1, timeStyle);
    }

    /**
     * <p>Validate/convert a time using the default
     *    <code>Locale</code> and <code>TimeZone</code>. 
     *
     * @param value The value validation is being performed on.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code>
     *  if invalid.
     */
    public Calendar validate(String value) {
        return (Calendar)validateObj(value);
    }

    /**
     * <p>Validate/convert a time using the specified
     *    <code>TimeZone</code> and default  <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @param timeZone The Time Zone used to parse the date, system default if null.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code> if invalid.
     */
    public Calendar validate(String value, TimeZone timeZone) {
        return (Calendar)validateObj(value, Locale.getDefault(), timeZone);
    }

    /**
     * <p>Validate/convert a time using the specified
     *    <i>pattern</i> and default <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code> if invalid.
     */
    public Calendar validate(String value, String pattern) {
        return (Calendar)validateObj(value, pattern);
    }

    /**
     * <p>Validate/convert a time using the specified
     *    <i>pattern</i> and <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @param timeZone The Time Zone used to parse the date, system default if null.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code> if invalid.
     */
    public Calendar validate(String value, String pattern, TimeZone timeZone) {
        return (Calendar)validateObj(value, pattern, timeZone);
    }

    /**
     * <p>Validate/convert a time using the specified
     *    <code>Locale</code> and <code>TimeZone</code>.
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the date format, system default if null.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code> if invalid.
     */
    public Calendar validate(String value, Locale locale) {
        return (Calendar)validateObj(value, locale);
    }

    /**
     * Checks if the value is a valid time for a
     * specified <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the date format, system default if null.
     * @param timeZone The Time Zone used to parse the date, system default if null.
     * @return The parsed <code>Calendar</code> if valid or <code>null</code> if invalid.
     */
    public Calendar validate(String value, Locale locale, TimeZone timeZone) {
        return (Calendar)validateObj(value, locale, timeZone);
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
    public int compareTime(Calendar value, Calendar compare) {
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
    public int compareSeconds(Calendar value, Calendar compare) {
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
    public int compareMinutes(Calendar value, Calendar compare) {
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
    public int compareHours(Calendar value, Calendar compare) {
        return compareTime(value, compare, Calendar.HOUR_OF_DAY);
    }

    /**
     * <p>Perform further validation and convert the <code>Calendar</code> to
     * the appropriate type.</p>
     * 
     * <p>This implementation returns the <code>Calendar</code> object
     *    unchanged</p>
     * 
     * @param calendar The calendar object create from the parsed value.
     * @return The validated/converted <code>Calendar</code> value if valid 
     * or <code>null</code> if invalid.
     */
    protected Object processCalendar(Calendar calendar) {
        return calendar;
    }
}
