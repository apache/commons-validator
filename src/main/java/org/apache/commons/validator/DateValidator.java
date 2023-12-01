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
package org.apache.commons.validator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * <p>Perform date validations.</p>
 * <p>
 * This class is a Singleton; you can retrieve the instance via the
 * getInstance() method.
 * </p>
 *
 * @since 1.1
 * @deprecated Use the new DateValidator, CalendarValidator or TimeValidator in the
 * routines package. This class will be removed in a future release.
 */
@Deprecated
public class DateValidator {

    /**
     * Singleton instance of this class.
     */
    private static final DateValidator DATE_VALIDATOR = new DateValidator();

    /**
     * Returns the Singleton instance of this validator.
     * @return A singleton instance of the DateValidator.
     */
    public static DateValidator getInstance() {
        return DATE_VALIDATOR;
    }

    /**
     * Protected constructor for subclasses to use.
     */
    protected DateValidator() {
    }

    /**
     * <p>Checks if the field is a valid date.  The <code>Locale</code> is
     * used with <code>java.text.DateFormat</code>.  The setLenient method
     * is set to {@code false} for all.</p>
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the date format, defaults to the default
     * system default if null.
     * @return true if the date is valid.
     */
    public boolean isValid(final String value, final Locale locale) {

        if (value == null) {
            return false;
        }

        DateFormat formatter;
        if (locale != null) {
            formatter = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        } else {
            formatter =
                    DateFormat.getDateInstance(
                            DateFormat.SHORT,
                            Locale.getDefault());
        }

        formatter.setLenient(false);

        try {
            formatter.parse(value);
        } catch (final ParseException e) {
            return false;
        }

        return true;
    }

    /**
     * <p>Checks if the field is a valid date.  The pattern is used with
     * <code>java.text.SimpleDateFormat</code>.  If strict is true, then the
     * length will be checked so '2/12/1999' will not pass validation with
     * the format 'MM/dd/yyyy' because the month isn't two digits.
     * The setLenient method is set to {@code false} for all.</p>
     *
     * @param value The value validation is being performed on.
     * @param datePattern The pattern passed to <code>SimpleDateFormat</code>.
     * @param strict Whether or not to have an exact match of the datePattern.
     * @return true if the date is valid.
     */
    public boolean isValid(final String value, final String datePattern, final boolean strict) {

        if (value == null
                || datePattern == null
                || datePattern.isEmpty()) {

            return false;
        }

        final SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        formatter.setLenient(false);

        try {
            formatter.parse(value);
        } catch (final ParseException e) {
            return false;
        }

        if (strict && datePattern.length() != value.length()) {
            return false;
        }

        return true;
    }

}
