/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/GenericTypeValidator.java,v 1.13 2004/02/21 17:10:29 rleland Exp $
 * $Revision: 1.13 $
 * $Date: 2004/02/21 17:10:29 $
 *
 * ====================================================================
 * Copyright 2001-2004 The Apache Software Foundation
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

package org.apache.commons.validator;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains basic methods for performing validations that return the
 * correctly typed class based on the validation performed.
 */
public class GenericTypeValidator implements Serializable {
    /*
    * Logger.
    */
   private static Log log = LogFactory.getLog(GenericTypeValidator.class);

    /**
     * Checks if the value can safely be converted to a byte primitive.
     *
     * @param value The value validation is being performed on.
     */
    public static Byte formatByte(String value) {
        if (value == null) {
            return null;
        }

        try {
            return new Byte(value);
        } catch(NumberFormatException e) {
            return null;
        }

    }

    /**
     * Checks if the value can safely be converted to a short primitive.
     *
     * @param value The value validation is being performed on.
     */
    public static Short formatShort(String value) {
        if (value == null) {
            return null;
        }

        try {
            return new Short(value);
        } catch(NumberFormatException e) {
            return null;
        }

    }

    /**
     * Checks if the value can safely be converted to a int primitive.
     *
     * @param value The value validation is being performed on.
     */
    public static Integer formatInt(String value) {
        if (value == null) {
            return null;
        }

        try {
            return new Integer(value);
        } catch(NumberFormatException e) {
            return null;
        }

    }

    /**
     * Checks if the value can safely be converted to a long primitive.
     *
     * @param value The value validation is being performed on.
     */
    public static Long formatLong(String value) {
        if (value == null) {
            return null;
        }

        try {
            return new Long(value);
        } catch(NumberFormatException e) {
            return null;
        }

    }

    /**
     * Checks if the value can safely be converted to a float primitive.
     *
     * @param value The value validation is being performed on.
     */
    public static Float formatFloat(String value) {
        if (value == null) {
            return null;
        }

        try {
            return new Float(value);
        } catch(NumberFormatException e) {
            return null;
        }

    }

    /**
     * Checks if the value can safely be converted to a double primitive.
     *
     * @param value The value validation is being performed on.
     */
    public static Double formatDouble(String value) {
        if (value == null) {
            return null;
        }

        try {
            return new Double(value);
        } catch(NumberFormatException e) {
            return null;
        }

    }

    /**
     * <p>Checks if the field is a valid date.  The <code>Locale</code> is
     * used with <code>java.text.DateFormat</code>.  The setLenient method
     * is set to <code>false</code> for all.</p>
     *
     * @param value The value validation is being performed on.
     * @param locale The Locale to use to parse the date (system default if null)
     */
    public static Date formatDate(String value, Locale locale) {
        Date date = null;

        if (value == null) {
            return null;
        }

        try {
            DateFormat formatter = null;
            if (locale != null) {
                formatter =
                        DateFormat.getDateInstance(DateFormat.SHORT, locale);
            } else {
                formatter =
                        DateFormat.getDateInstance(
                                DateFormat.SHORT,
                                Locale.getDefault());
            }

            formatter.setLenient(false);

            date = formatter.parse(value);
        } catch(ParseException e) {
            // Bad date so return null
            log.warn(value, e);
        }

        return date;
    }

    /**
     * <p>Checks if the field is a valid date.  The pattern is used with
     * <code>java.text.SimpleDateFormat</code>.  If strict is true, then the
     * length will be checked so '2/12/1999' will not pass validation with
     * the format 'MM/dd/yyyy' because the month isn't two digits.
     * The setLenient method is set to <code>false</code> for all.</p>
     *
     * @param value The value validation is being performed on.
     * @param datePattern The pattern passed to <code>SimpleDateFormat</code>.
     * @param strict Whether or not to have an exact match of the datePattern.
     */
    public static Date formatDate(String value, String datePattern, boolean strict) {
        Date date = null;

        if (value == null
                || datePattern == null
                || datePattern.length() == 0) {
            return null;
        }

        try {
            SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
            formatter.setLenient(false);

            date = formatter.parse(value);

            if (strict) {
                if (datePattern.length() != value.length()) {
                    date = null;
                }
            }
        } catch(ParseException e) {
            // Bad date so return null
            log.warn(value, e);
        }

        return date;
    }

    /**
     * <p>Checks if the field is a valid credit card number.</p>
     * <p>Reference Sean M. Burke's
     * <a href="http://www.ling.nwu.edu/~sburke/pub/luhn_lib.pl">script</a>.</p>
     *
     * @param value The value validation is being performed on.
     */
    public static Long formatCreditCard(String value) {
        return GenericValidator.isCreditCard(value) ? new Long(value) : null;
    }

}
