/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/GenericValidator.java,v 1.27 2003/08/26 16:11:01 rleland Exp $
 * $Revision: 1.27 $
 * $Date: 2003/08/26 16:11:01 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names, "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.validator;

import java.io.Serializable;
import java.util.Locale;

import org.apache.oro.text.perl.Perl5Util;

/**
 * <p>This class contains basic methods for performing validations.</p>
 *
 * @author David Winterfeldt
 * @author James Turner
 * @author <a href="mailto:husted@apache.org">Ted Husted</a>
 * @author David Graham
 * @author Robert Leland
 * @version $Revision: 1.27 $ $Date: 2003/08/26 16:11:01 $
 */
public class GenericValidator implements Serializable {

    /**
     * Delimiter to put around a regular expression following Perl 5 syntax.
     * @deprecated Use ValidatorUtils.REGEXP_DELIMITER instead.
     */
    public final static String REGEXP_DELIM = ValidatorUtil.REGEXP_DELIMITER;

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
     * Checks for a valid credit card number.
     *
     * @param cardNumber Credit Card Number.
     * @deprecated This functionality has moved to CreditCardValidator.
     */
    protected static boolean validateCreditCardLuhnCheck(String cardNumber) {
        return (new CreditCardValidator()).luhnCheck(cardNumber);
    }

    /**
     * Checks for a valid credit card number.
     *
     * @param cardNumber Credit Card Number.
     * @deprecated This functionality has move to CreditCardValidator.
     */
    protected boolean validateCreditCardPrefixCheck(String cardNumber) {
        return (new CreditCardValidator()).isValidPrefix(cardNumber);
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

    /**
     * Adds a '/' on either side of the regular expression.
     * @deprecated use ValidatorUtils.getDelimitedRegExp() instead.
     */
    protected static String getDelimittedRegexp(String regexp) {
        return ValidatorUtil.getDelimitedRegExp(regexp);
    }
}
