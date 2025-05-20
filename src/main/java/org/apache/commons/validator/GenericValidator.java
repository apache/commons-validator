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
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.CreditCardValidator;
import org.apache.commons.validator.routines.DateValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;

/**
 * This class contains basic methods for performing validations.
 */
public class GenericValidator implements Serializable {

    private static final long serialVersionUID = -7212095066891517618L;

    /**
     * UrlValidator used in wrapper method.
     */
    private static final UrlValidator URL_VALIDATOR = new UrlValidator();

    /**
     * CreditCardValidator used in wrapper method.
     */
    private static final CreditCardValidator CREDIT_CARD_VALIDATOR = new CreditCardValidator();

    /**
     * Calculate an adjustment amount for line endings.
     *
     * See Bug 37962 for the rationale behind this.
     *
     * @param value The value validation is being performed on.
     * @param lineEndLength The length to use for line endings.
     * @return the adjustment amount.
     */
    private static int adjustForLineEnding(final String value, final int lineEndLength) {
        int nCount = 0;
        int rCount = 0;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '\n') {
                nCount++;
            }
            if (value.charAt(i) == '\r') {
                rCount++;
            }
        }
        final int rnCount = rCount + nCount;
        return nCount * lineEndLength - rnCount;
    }

    /**
     * <p>Checks if the field isn't null and length of the field is greater
     * than zero not including whitespace.</p>
     *
     * @param value The value validation is being performed on.
     * @return true if blank or null.
     */
    public static boolean isBlankOrNull(final String value) {
        // Don't trim is already empty.
        return value == null || value.isEmpty() || value.trim().isEmpty();
    }

    /**
     * <p>Checks if the value can safely be converted to a byte primitive.</p>
     *
     * @param value The value validation is being performed on.
     * @return true if the value can be converted to a Byte.
     */
    public static boolean isByte(final String value) {
        return GenericTypeValidator.formatByte(value) != null;
    }

    /**
     * Checks if the field is a valid credit card number.
     * @param value The value validation is being performed on.
     * @return true if the value is valid Credit Card Number.
     */
    public static boolean isCreditCard(final String value) {
        return CREDIT_CARD_VALIDATOR.isValid(value);
    }

    /**
     * <p>Checks if the field is a valid date.  The {@link Locale} is
     * used with {@link DateFormat}.  The setLenient method
     * is set to {@code false} for all.</p>
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the date format, defaults to the
     * system default if null.
     * @return true if the value can be converted to a Date.
     */
    public static boolean isDate(final String value, final Locale locale) {
        return DateValidator.getInstance().isValid(value, locale);
    }

    /**
     * <p>Checks if the field is a valid date.  The pattern is used with
     * {@link SimpleDateFormat}.  If strict is true, then the
     * length will be checked so '2/12/1999' will not pass validation with
     * the format 'MM/dd/yyyy' because the month isn't two digits.
     * The setLenient method is set to {@code false} for all.</p>
     *
     * @param value The value validation is being performed on.
     * @param datePattern The pattern passed to {@link SimpleDateFormat}.
     * @param strict Whether or not to have an exact match of the datePattern.
     * @return true if the value can be converted to a Date.
     */
    public static boolean isDate(final String value, final String datePattern, final boolean strict) {
        // TODO method isValid() not yet supported in routines version
        return org.apache.commons.validator.DateValidator.getInstance().isValid(value, datePattern, strict);
    }

    /**
     * <p>Checks if the value can safely be converted to a double primitive.</p>
     *
     * @param value The value validation is being performed on.
     * @return true if the value can be converted to a Double.
     */
    public static boolean isDouble(final String value) {
        return GenericTypeValidator.formatDouble(value) != null;
    }

    /**
     * <p>Checks if a field has a valid e-mail address.</p>
     *
     * @param value The value validation is being performed on.
     * @return true if the value is valid Email Address.
     */
    public static boolean isEmail(final String value) {
        return EmailValidator.getInstance().isValid(value);
    }

    /**
     * <p>Checks if the value can safely be converted to a float primitive.</p>
     *
     * @param value The value validation is being performed on.
     * @return true if the value can be converted to a Float.
     */
    public static boolean isFloat(final String value) {
        return GenericTypeValidator.formatFloat(value) != null;
    }

    /**
    * <p>Checks if a value is within a range (min &amp; max specified
    * in the vars attribute).</p>
    *
    * @param value The value validation is being performed on.
    * @param min The minimum value of the range.
    * @param max The maximum value of the range.
     * @return true if the value is in the specified range.
    */
    public static boolean isInRange(final byte value, final byte min, final byte max) {
        return value >= min && value <= max;
    }

    /**
     * <p>Checks if a value is within a range (min &amp; max specified
     * in the vars attribute).</p>
     *
     * @param value The value validation is being performed on.
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
     * @return true if the value is in the specified range.
     */
    public static boolean isInRange(final double value, final double min, final double max) {
        return value >= min && value <= max;
    }

    /**
     * <p>Checks if a value is within a range (min &amp; max specified
     * in the vars attribute).</p>
     *
     * @param value The value validation is being performed on.
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
     * @return true if the value is in the specified range.
     */
    public static boolean isInRange(final float value, final float min, final float max) {
        return value >= min && value <= max;
    }

    /**
     * <p>Checks if a value is within a range (min &amp; max specified
     * in the vars attribute).</p>
     *
     * @param value The value validation is being performed on.
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
     * @return true if the value is in the specified range.
     */
    public static boolean isInRange(final int value, final int min, final int max) {
        return value >= min && value <= max;
    }

    /**
     * <p>Checks if a value is within a range (min &amp; max specified
     * in the vars attribute).</p>
     *
     * @param value The value validation is being performed on.
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
     * @return true if the value is in the specified range.
     */
    public static boolean isInRange(final long value, final long min, final long max) {
        return value >= min && value <= max;
    }

    /**
     * <p>Checks if a value is within a range (min &amp; max specified
     * in the vars attribute).</p>
     *
     * @param value The value validation is being performed on.
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
     * @return true if the value is in the specified range.
     */
    public static boolean isInRange(final short value, final short min, final short max) {
        return value >= min && value <= max;
    }

    /**
     * <p>Checks if the value can safely be converted to an int primitive.</p>
     *
     * @param value The value validation is being performed on.
     * @return true if the value can be converted to an Integer.
     */
    public static boolean isInt(final String value) {
        return GenericTypeValidator.formatInt(value) != null;
    }

    /**
     * <p>Checks if the value can safely be converted to a long primitive.</p>
     *
     * @param value The value validation is being performed on.
     * @return true if the value can be converted to a Long.
     */
    public static boolean isLong(final String value) {
        return GenericTypeValidator.formatLong(value) != null;
    }

    /**
     * <p>Checks if the value can safely be converted to a short primitive.</p>
     *
     * @param value The value validation is being performed on.
     * @return true if the value can be converted to a Short.
     */
    public static boolean isShort(final String value) {
        return GenericTypeValidator.formatShort(value) != null;
    }

    /**
     * <p>Checks if a field is a valid URL address.</p>
     * If you need to modify what is considered valid then
     * consider using the UrlValidator directly.
     *
     * @param value The value validation is being performed on.
     * @return true if the value is valid Url.
     */
    public static boolean isUrl(final String value) {
        return URL_VALIDATOR.isValid(value);
    }

    /**
     * <p>Checks if the value matches the regular expression.</p>
     *
     * @param value The value validation is being performed on.
     * @param regexp The regular expression.
     * @return true if matches the regular expression.
     */
    public static boolean matchRegexp(final String value, final String regexp) {
        if (regexp == null || regexp.isEmpty()) {
            return false;
        }

        return Pattern.matches(regexp, value);
    }

    /**
     * <p>Checks if the value's length is less than or equal to the max.</p>
     *
     * @param value The value validation is being performed on.
     * @param max The maximum length.
     * @return true if the value's length is less than the specified maximum.
     */
    public static boolean maxLength(final String value, final int max) {
        return value.length() <= max;
    }

    /**
     * <p>Checks if the value's adjusted length is less than or equal to the max.</p>
     *
     * @param value The value validation is being performed on.
     * @param max The maximum length.
     * @param lineEndLength The length to use for line endings.
     * @return true if the value's length is less than the specified maximum.
     */
    public static boolean maxLength(final String value, final int max, final int lineEndLength) {
        final int adjustAmount = adjustForLineEnding(value, lineEndLength);
        return value.length() + adjustAmount <= max;
    }

    /**
     * <p>Checks if the value is less than or equal to the max.</p>
     *
     * @param value The value validation is being performed on.
     * @param max The maximum numeric value.
     * @return true if the value is &lt;= the specified maximum.
     */
    public static boolean maxValue(final double value, final double max) {
        return value <= max;
    }

    /**
     * <p>Checks if the value is less than or equal to the max.</p>
     *
     * @param value The value validation is being performed on.
     * @param max The maximum numeric value.
     * @return true if the value is &lt;= the specified maximum.
     */
    public static boolean maxValue(final float value, final float max) {
        return value <= max;
    }

    /**
     * <p>Checks if the value is less than or equal to the max.</p>
     *
     * @param value The value validation is being performed on.
     * @param max The maximum numeric value.
     * @return true if the value is &lt;= the specified maximum.
     */
    public static boolean maxValue(final int value, final int max) {
        return value <= max;
    }

    // See https://issues.apache.org/bugzilla/show_bug.cgi?id=29015 WRT the "value" methods

    /**
     * <p>Checks if the value is less than or equal to the max.</p>
     *
     * @param value The value validation is being performed on.
     * @param max The maximum numeric value.
     * @return true if the value is &lt;= the specified maximum.
     */
    public static boolean maxValue(final long value, final long max) {
        return value <= max;
    }

    /**
     * <p>Checks if the value's length is greater than or equal to the min.</p>
     *
     * @param value The value validation is being performed on.
     * @param min The minimum length.
     * @return true if the value's length is more than the specified minimum.
     */
    public static boolean minLength(final String value, final int min) {
        return value.length() >= min;
    }

    /**
     * <p>Checks if the value's adjusted length is greater than or equal to the min.</p>
     *
     * @param value The value validation is being performed on.
     * @param min The minimum length.
     * @param lineEndLength The length to use for line endings.
     * @return true if the value's length is more than the specified minimum.
     */
    public static boolean minLength(final String value, final int min, final int lineEndLength) {
        final int adjustAmount = adjustForLineEnding(value, lineEndLength);
        return value.length() + adjustAmount >= min;
    }

    /**
     * <p>Checks if the value is greater than or equal to the min.</p>
     *
     * @param value The value validation is being performed on.
     * @param min The minimum numeric value.
     * @return true if the value is &gt;= the specified minimum.
     */
    public static boolean minValue(final double value, final double min) {
        return value >= min;
    }

    /**
     * <p>Checks if the value is greater than or equal to the min.</p>
     *
     * @param value The value validation is being performed on.
     * @param min The minimum numeric value.
     * @return true if the value is &gt;= the specified minimum.
     */
    public static boolean minValue(final float value, final float min) {
        return value >= min;
    }

    /**
     * <p>Checks if the value is greater than or equal to the min.</p>
     *
     * @param value The value validation is being performed on.
     * @param min The minimum numeric value.
     * @return true if the value is &gt;= the specified minimum.
     */
    public static boolean minValue(final int value, final int min) {
        return value >= min;
    }

    /**
     * <p>Checks if the value is greater than or equal to the min.</p>
     *
     * @param value The value validation is being performed on.
     * @param min The minimum numeric value.
     * @return true if the value is &gt;= the specified minimum.
     */
    public static boolean minValue(final long value, final long min) {
        return value >= min;
    }

    /**
     * Constructs a new instance.
     *
     * @deprecated Will be private in the next major version.
     */
    @Deprecated
    public GenericValidator() {
        // empty
    }

}
