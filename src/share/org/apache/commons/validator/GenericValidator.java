/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/GenericValidator.java,v 1.30 2004/04/08 22:51:17 dgraham Exp $
 * $Revision: 1.30 $
 * $Date: 2004/04/08 22:51:17 $
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
import java.util.Locale;

import org.apache.oro.text.perl.Perl5Util;

/**
 * This class contains basic methods for performing validations.
 */
public class GenericValidator implements Serializable {

    /**
     * UrlValidator used in wrapper method.
     */
    private static final UrlValidator urlValidator = new UrlValidator();

    /**
     * CreditCardValidator used in wrapper method.
     */
    private static final CreditCardValidator creditCardValidator =
            new CreditCardValidator();

    /**
     * <p>Checks if the field isn't null and length of the field is greater than zero not
     * including whitespace.</p>
     *
     * @param value The value validation is being performed on.
     */
    public static boolean isBlankOrNull(String value) {
        return ((value == null) || (value.trim().length() == 0));
    }

    /**
     * <p>Checks if the value matches the regular expression.</p>
     *
     * @param value The value validation is being performed on.
     * @param regexp The regular expression.
     */
    public static boolean matchRegexp(String value, String regexp) {
        if (regexp == null || regexp.length() <= 0) {
            return false;
        }

        Perl5Util matcher = new Perl5Util();
        return matcher.match("/" + regexp + "/", value);
    }

    /**
     * <p>Checks if the value can safely be converted to a byte primitive.</p>
     *
     * @param     value         The value validation is being performed on.
     */
    public static boolean isByte(String value) {
        return (GenericTypeValidator.formatByte(value) != null);
    }

    /**
     * <p>Checks if the value can safely be converted to a short primitive.</p>
     *
     * @param     value         The value validation is being performed on.
     */
    public static boolean isShort(String value) {
        return (GenericTypeValidator.formatShort(value) != null);
    }

    /**
     * <p>Checks if the value can safely be converted to a int primitive.</p>
     *
     * @param     value         The value validation is being performed on.
     */
    public static boolean isInt(String value) {
        return (GenericTypeValidator.formatInt(value) != null);
    }

    /**
     * <p>Checks if the value can safely be converted to a long primitive.</p>
     *
     * @param value The value validation is being performed on.
     */
    public static boolean isLong(String value) {
        return (GenericTypeValidator.formatLong(value) != null);
    }

    /**
     * <p>Checks if the value can safely be converted to a float primitive.</p>
     *
     * @param value The value validation is being performed on.
     */
    public static boolean isFloat(String value) {
        return (GenericTypeValidator.formatFloat(value) != null);
    }

    /**
     * <p>Checks if the value can safely be converted to a double primitive.</p>
     *
     * @param value The value validation is being performed on.
     */
    public static boolean isDouble(String value) {
        return (GenericTypeValidator.formatDouble(value) != null);
    }

    /**
     * <p>Checks if the field is a valid date.  The <code>Locale</code> is
     * used with <code>java.text.DateFormat</code>.  The setLenient method
     * is set to <code>false</code> for all.</p>
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the date format, defaults to the default
     * system default if null.
     */
    public static boolean isDate(String value, Locale locale) {
        return DateValidator.getInstance().isValid(value, locale);
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
    public static boolean isDate(String value, String datePattern, boolean strict) {
        return DateValidator.getInstance().isValid(value, datePattern, strict);
    }

    /**
	* <p>Checks if a value is within a range (min &amp; max specified
	* in the vars attribute).</p>
	*
	* @param 	value 		The value validation is being performed on.
	* @param 	min		The minimum value of the range.
	* @param 	max		The maximum value of the range.
	*/
   public static boolean isInRange(byte value, byte min, byte max) {
	  return ((value >= min) && (value <= max));
   }

    /**
     * <p>Checks if a value is within a range (min &amp; max specified
     * in the vars attribute).</p>
     *
     * @param     value      The value validation is being performed on.
     * @param     min        The minimum value of the range.
     * @param     max        The maximum value of the range.
     */
    public static boolean isInRange(int value, int min, int max) {
        return ((value >= min) && (value <= max));
    }

    /**
     * <p>Checks if a value is within a range (min &amp; max specified
     * in the vars attribute).</p>
     *
     * @param     value      The value validation is being performed on.
     * @param     min        The minimum value of the range.
     * @param     max        The maximum value of the range.
     */
    public static boolean isInRange(float value, float min, float max) {
        return ((value >= min) && (value <= max));
    }

    /**
     * <p>Checks if a value is within a range (min &amp; max specified
     * in the vars attribute).</p>
     *
     * @param     value      The value validation is being performed on.
     * @param     min        The minimum value of the range.
     * @param     max        The maximum value of the range.
     */
    public static boolean isInRange(short value, short min, short max) {
        return ((value >= min) && (value <= max));
    }

   /**
    * <p>Checks if a value is within a range (min &amp; max specified
    * in the vars attribute).</p>
    *
    * @param 	value 		The value validation is being performed on.
    * @param 	min		The minimum value of the range.
    * @param 	max		The maximum value of the range.
    */
   public static boolean isInRange(long value, long min, long max) {
      return ((value >= min) && (value <= max));
   }

    /**
     * <p>Checks if a value is within a range (min &amp; max specified
     * in the vars attribute).</p>
     *
     * @param     value      The value validation is being performed on.
     * @param     min        The minimum value of the range.
     * @param     max        The maximum value of the range.
     */
    public static boolean isInRange(double value, double min, double max) {
        return ((value >= min) && (value <= max));
    }

    /**
     * Checks if the field is a valid credit card number.
     * @param value The value validation is being performed on.
     */
    public static boolean isCreditCard(String value) {
        return creditCardValidator.isValid(value);
    }

    /**
     * <p>Checks if a field has a valid e-mail address.</p>
     *
     * @param value The value validation is being performed on.
     */
    public static boolean isEmail(String value) {
        return EmailValidator.getInstance().isValid(value);
    }

    /**
     * <p>Checks if a field is a valid url address.</p>
     * If you need to modify what is considered valid then
     * consider using the UrlValidator directly.
     *
     * @param value The value validation is being performed on.
     */
    public static boolean isUrl(String value) {
        return urlValidator.isValid(value);
    }

    /**
     * <p>Checks if the value's length is less than or equal to the max.</p>
     *
     * @param value The value validation is being performed on.
     * @param max The maximum length.
     */
    public static boolean maxLength(String value, int max) {
        return (value.length() <= max);
    }

    /**
     * <p>Checks if the value's length is greater than or equal to the min.</p>
     *
     * @param     value         The value validation is being performed on.
     * @param     min        The minimum length.
     */
    public static boolean minLength(String value, int min) {
        return (value.length() >= min);
    }

}
