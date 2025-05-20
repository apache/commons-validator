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
package org.apache.commons.validator;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains basic methods for performing validations that return the
 * correctly typed class based on the validation performed.
 */
public class GenericTypeValidator implements Serializable {

    private static final long serialVersionUID = 5487162314134261703L;

    private static final Log LOG = LogFactory.getLog(GenericTypeValidator.class);

    /**
     * Checks if the value can safely be converted to a byte primitive.
     *
     * @param value The value validation is being performed on.
     * @return the converted Byte value.
     */
    public static Byte formatByte(final String value) {
        if (value == null) {
            return null;
        }

        try {
            return Byte.valueOf(value);
        } catch (final NumberFormatException e) {
            return null;
        }

    }

    /**
     * Checks if the value can safely be converted to a byte primitive.
     *
     * @param value  The value validation is being performed on.
     * @param locale The locale to use to parse the number (system default if
     *               null)
     * @return the converted Byte value.
     */
    public static Byte formatByte(final String value, final Locale locale) {
        Byte result = null;

        if (value != null) {
            NumberFormat formatter = null;
            if (locale != null) {
                formatter = NumberFormat.getNumberInstance(locale);
            } else {
                formatter = NumberFormat.getNumberInstance(Locale.getDefault());
            }
            formatter.setParseIntegerOnly(true);
            final ParsePosition pos = new ParsePosition(0);
            final Number num = formatter.parse(value, pos);

            // If there was no error      and we used the whole string
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() &&
                    num.doubleValue() >= Byte.MIN_VALUE &&
                    num.doubleValue() <= Byte.MAX_VALUE) {
                result = Byte.valueOf(num.byteValue());
            }
        }

        return result;
    }

    /**
     * Checks if the field is a valid credit card number.
     *
     * <p>Reference Sean M. Burke's <a href="http://www.ling.nwu.edu/~sburke/pub/luhn_lib.pl">
     * script</a>.</p>
     *
     * @param value The value validation is being performed on.
     * @return the converted Credit Card number.
     */
    public static Long formatCreditCard(final String value) {
        return GenericValidator.isCreditCard(value) ? Long.valueOf(value) : null;
    }

    /**
     * Checks if the field is a valid date.
     *
     * <p>The {@link Locale} is used with {@link java.text.DateFormat}. The {@link java.text.DateFormat#setLenient(boolean)}
     * method is set to {@code false} for all.
     * </p>
     *
     * @param value  The value validation is being performed on.
     * @param locale The Locale to use to parse the date (system default if null)
     * @return the converted Date value.
     */
    public static Date formatDate(final String value, final Locale locale) {
        Date date = null;

        if (value == null) {
            return null;
        }

        try {
            // Get the formatters to check against
            DateFormat formatterShort = null;
            DateFormat formatterDefault = null;
            if (locale != null) {
                formatterShort =
                        DateFormat.getDateInstance(DateFormat.SHORT, locale);
                formatterDefault =
                        DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
            } else {
                formatterShort =
                        DateFormat.getDateInstance(
                                DateFormat.SHORT,
                                Locale.getDefault());
                formatterDefault =
                        DateFormat.getDateInstance(
                                DateFormat.DEFAULT,
                                Locale.getDefault());
            }

            // Turn off lenient parsing
            formatterShort.setLenient(false);
            formatterDefault.setLenient(false);

            // Firstly, try with the short form
            try {
                date = formatterShort.parse(value);
            } catch (final ParseException e) {
                // Fall back on the default one
                date = formatterDefault.parse(value);
            }
        } catch (final ParseException e) {
            // Bad date, so LOG and return null
            if (LOG.isDebugEnabled()) {
                LOG.debug("Date parse failed value=[" + value + "], " +
                        "locale=[" + locale + "] " + e);
            }
        }

        return date;
    }

    /**
     * Checks if the field is a valid date.
     *
     * <p>The pattern is used with {@link java.text.SimpleDateFormat}.
     * If strict is true, then the length will be checked so '2/12/1999' will
     * not pass validation with the format 'MM/dd/yyyy' because the month isn't
     * two digits. The {@link java.text.SimpleDateFormat#setLenient(boolean)}
     * method is set to {@code false} for all.
     * </p>
     *
     * @param value       The value validation is being performed on.
     * @param datePattern The pattern passed to {@link SimpleDateFormat}.
     * @param strict      Whether or not to have an exact match of the
     *                    datePattern.
     * @return the converted Date value.
     */
    public static Date formatDate(final String value, final String datePattern, final boolean strict) {
        Date date = null;

        if (value == null
                || datePattern == null
                || datePattern.isEmpty()) {
            return null;
        }

        try {
            final SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
            formatter.setLenient(false);

            date = formatter.parse(value);

            if (strict && datePattern.length() != value.length()) {
                date = null;
            }
        } catch (final ParseException e) {
            // Bad date so return null
            if (LOG.isDebugEnabled()) {
                LOG.debug("Date parse failed value=[" + value + "], " +
                        "pattern=[" + datePattern + "], " +
                        "strict=[" + strict + "] " + e);
            }
        }

        return date;
    }

    /**
     * Checks if the value can safely be converted to a double primitive.
     *
     * @param value The value validation is being performed on.
     * @return the converted Double value.
     */
    public static Double formatDouble(final String value) {
        if (value == null) {
            return null;
        }

        try {
            return Double.valueOf(value);
        } catch (final NumberFormatException e) {
            return null;
        }

    }

    /**
     * Checks if the value can safely be converted to a double primitive.
     *
     * @param value  The value validation is being performed on.
     * @param locale The locale to use to parse the number (system default if
     *               null)
     * @return the converted Double value.
     */
    public static Double formatDouble(final String value, final Locale locale) {
        Double result = null;

        if (value != null) {
            NumberFormat formatter = null;
            if (locale != null) {
                formatter = NumberFormat.getInstance(locale);
            } else {
                formatter = NumberFormat.getInstance(Locale.getDefault());
            }
            final ParsePosition pos = new ParsePosition(0);
            final Number num = formatter.parse(value, pos);

            // If there was no error      and we used the whole string
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() &&
                    num.doubleValue() >= Double.MAX_VALUE * -1 &&
                    num.doubleValue() <= Double.MAX_VALUE) {
                result = Double.valueOf(num.doubleValue());
            }
        }

        return result;
    }

    /**
     * Checks if the value can safely be converted to a float primitive.
     *
     * @param value The value validation is being performed on.
     * @return the converted Float value.
     */
    public static Float formatFloat(final String value) {
        if (value == null) {
            return null;
        }

        try {
            return Float.valueOf(value);
        } catch (final NumberFormatException e) {
            return null;
        }

    }

    /**
     * Checks if the value can safely be converted to a float primitive.
     *
     * @param value  The value validation is being performed on.
     * @param locale The locale to use to parse the number (system default if
     *               null)
     * @return the converted Float value.
     */
    public static Float formatFloat(final String value, final Locale locale) {
        Float result = null;

        if (value != null) {
            NumberFormat formatter = null;
            if (locale != null) {
                formatter = NumberFormat.getInstance(locale);
            } else {
                formatter = NumberFormat.getInstance(Locale.getDefault());
            }
            final ParsePosition pos = new ParsePosition(0);
            final Number num = formatter.parse(value, pos);

            // If there was no error      and we used the whole string
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() &&
                    num.doubleValue() >= Float.MAX_VALUE * -1 &&
                    num.doubleValue() <= Float.MAX_VALUE) {
                result = Float.valueOf(num.floatValue());
            }
        }

        return result;
    }

    /**
     * Checks if the value can safely be converted to an int primitive.
     *
     * @param value The value validation is being performed on.
     * @return the converted Integer value.
     */
    public static Integer formatInt(final String value) {
        if (value == null) {
            return null;
        }

        try {
            return Integer.valueOf(value);
        } catch (final NumberFormatException e) {
            return null;
        }

    }

    /**
     * Checks if the value can safely be converted to an int primitive.
     *
     * @param value  The value validation is being performed on.
     * @param locale The locale to use to parse the number (system default if
     *               null)
     * @return the converted Integer value.
     */
    public static Integer formatInt(final String value, final Locale locale) {
        Integer result = null;

        if (value != null) {
            NumberFormat formatter = null;
            if (locale != null) {
                formatter = NumberFormat.getNumberInstance(locale);
            } else {
                formatter = NumberFormat.getNumberInstance(Locale.getDefault());
            }
            formatter.setParseIntegerOnly(true);
            final ParsePosition pos = new ParsePosition(0);
            final Number num = formatter.parse(value, pos);

            // If there was no error      and we used the whole string
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() &&
                    num.doubleValue() >= Integer.MIN_VALUE &&
                    num.doubleValue() <= Integer.MAX_VALUE) {
                result = Integer.valueOf(num.intValue());
            }
        }

        return result;
    }

    /**
     * Checks if the value can safely be converted to a long primitive.
     *
     * @param value The value validation is being performed on.
     * @return the converted Long value.
     */
    public static Long formatLong(final String value) {
        if (value == null) {
            return null;
        }

        try {
            return Long.valueOf(value);
        } catch (final NumberFormatException e) {
            return null;
        }

    }

    /**
     * Checks if the value can safely be converted to a long primitive.
     *
     * @param value  The value validation is being performed on.
     * @param locale The locale to use to parse the number (system default if
     *               null)
     * @return the converted Long value.
     */
    public static Long formatLong(final String value, final Locale locale) {
        Long result = null;

        if (value != null) {
            NumberFormat formatter = null;
            if (locale != null) {
                formatter = NumberFormat.getNumberInstance(locale);
            } else {
                formatter = NumberFormat.getNumberInstance(Locale.getDefault());
            }
            formatter.setParseIntegerOnly(true);
            final ParsePosition pos = new ParsePosition(0);
            final Number num = formatter.parse(value, pos);

            // If there was no error      and we used the whole string
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() &&
                    num.doubleValue() >= Long.MIN_VALUE &&
                    num.doubleValue() <= Long.MAX_VALUE) {
                result = Long.valueOf(num.longValue());
            }
        }

        return result;
    }

    /**
     * Checks if the value can safely be converted to a short primitive.
     *
     * @param value The value validation is being performed on.
     * @return the converted Short value.
     */
    public static Short formatShort(final String value) {
        if (value == null) {
            return null;
        }

        try {
            return Short.valueOf(value);
        } catch (final NumberFormatException e) {
            return null;
        }

    }

    /**
     * Checks if the value can safely be converted to a short primitive.
     *
     * @param value  The value validation is being performed on.
     * @param locale The locale to use to parse the number (system default if
     *               null)
     * @return the converted Short value.
     */
    public static Short formatShort(final String value, final Locale locale) {
        Short result = null;

        if (value != null) {
            NumberFormat formatter = null;
            if (locale != null) {
                formatter = NumberFormat.getNumberInstance(locale);
            } else {
                formatter = NumberFormat.getNumberInstance(Locale.getDefault());
            }
            formatter.setParseIntegerOnly(true);
            final ParsePosition pos = new ParsePosition(0);
            final Number num = formatter.parse(value, pos);

            // If there was no error      and we used the whole string
            if (pos.getErrorIndex() == -1 && pos.getIndex() == value.length() &&
                    num.doubleValue() >= Short.MIN_VALUE &&
                    num.doubleValue() <= Short.MAX_VALUE) {
                result = Short.valueOf(num.shortValue());
            }
        }

        return result;
    }

    /**
     * Constructs a new instance.
     *
     * @deprecated Will be private in the next major version.
     */
    @Deprecated
    public GenericTypeValidator() {
        // empty
    }

}

