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

import java.text.Format;
import java.util.Locale;

/**
 * <p><b>Integer Validation</b> and Conversion routines (<code>java.lang.Integer</code>).</p>
 *
 * <p>This validator provides a number of methods for
 *    validating/converting a <code>String</code> value to
 *    a <code>Integer</code> using <code>java.text.NumberFormat</code>
 *    to parse either:</p>
 *    <ul>
 *       <li>using the default format for the default <code>Locale</code></li>
 *       <li>using a specified pattern with the default <code>Locale</code></li>
 *       <li>using the default format for a specified <code>Locale</code></li>
 *       <li>using a specified pattern with a specified <code>Locale</code></li>
 *    </ul>
 *
 * <p>Use one of the <code>isValid()</code> methods to just validate or
 *    one of the <code>validate()</code> methods to validate and receive a
 *    <i>converted</i> <code>Integer</code> value.</p>
 *
 * <p>Once a value has been successfully converted the following
 *    methods can be used to perform minimum, maximum and range checks:</p>
 *    <ul>
 *       <li><code>minValue()</code> checks whether the value is greater
 *           than or equal to a specified minimum.</li>
 *       <li><code>maxValue()</code> checks whether the value is less
 *           than or equal to a specified maximum.</li>
 *       <li><code>isInRange()</code> checks whether the value is within
 *           a specified range of values.</li>
 *    </ul>
 *
 * <p>So that the same mechanism used for parsing an <i>input</i> value
 *    for validation can be used to format <i>output</i>, corresponding
 *    <code>format()</code> methods are also provided. That is you can
 *    format either:</p>
 *    <ul>
 *       <li>using the default format for the default <code>Locale</code></li>
 *       <li>using a specified pattern with the default <code>Locale</code></li>
 *       <li>using the default format for a specified <code>Locale</code></li>
 *       <li>using a specified pattern with a specified <code>Locale</code></li>
 *    </ul>
 *
 * @version $Revision$
 * @since Validator 1.3.0
 */
public class IntegerValidator extends AbstractNumberValidator {

    private static final long serialVersionUID = 422081746310306596L;

    private static final IntegerValidator VALIDATOR = new IntegerValidator();

    /**
     * Return a singleton instance of this validator.
     * @return A singleton instance of the IntegerValidator.
     */
    public static IntegerValidator getInstance() {
        return VALIDATOR;
    }

    /**
     * Construct a <i>strict</i> instance.
     */
    public IntegerValidator() {
        this(true, STANDARD_FORMAT);
    }

    /**
     * <p>Construct an instance with the specified strict setting
     *    and format type.</p>
     *
     * <p>The <code>formatType</code> specified what type of
     *    <code>NumberFormat</code> is created - valid types
     *    are:</p>
     *    <ul>
     *       <li>AbstractNumberValidator.STANDARD_FORMAT -to create
     *           <i>standard</i> number formats (the default).</li>
     *       <li>AbstractNumberValidator.CURRENCY_FORMAT -to create
     *           <i>currency</i> number formats.</li>
     *       <li>AbstractNumberValidator.PERCENT_FORMAT -to create
     *           <i>percent</i> number formats (the default).</li>
     *    </ul>
     *
     * @param strict <code>true</code> if strict
     *        <code>Format</code> parsing should be used.
     * @param formatType The <code>NumberFormat</code> type to
     *        create for validation, default is STANDARD_FORMAT.
     */
    public IntegerValidator(boolean strict, int formatType) {
        super(strict, formatType, false);
    }

    /**
     * <p>Validate/convert an <code>Integer</code> using the default
     *    <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @return The parsed <code>Integer</code> if valid or <code>null</code>
     *  if invalid.
     */
    public Integer validate(String value) {
        return (Integer)parse(value, (String)null, (Locale)null);
    }

    /**
     * <p>Validate/convert an <code>Integer</code> using the
     *    specified <i>pattern</i>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @return The parsed <code>Integer</code> if valid or <code>null</code> if invalid.
     */
    public Integer validate(String value, String pattern) {
        return (Integer)parse(value, pattern, (Locale)null);
    }

    /**
     * <p>Validate/convert an <code>Integer</code> using the
     *    specified <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the number format, system default if null.
     * @return The parsed <code>Integer</code> if valid or <code>null</code> if invalid.
     */
    public Integer validate(String value, Locale locale) {
        return (Integer)parse(value, (String)null, locale);
    }

    /**
     * <p>Validate/convert a <code>Integer</code> using the
     *    specified pattern and/ or <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against, or the
     *        default for the <code>Locale</code> if <code>null</code>.
     * @param locale The locale to use for the date format, system default if null.
     * @return The parsed <code>Integer</code> if valid or <code>null</code> if invalid.
     */
    public Integer validate(String value, String pattern, Locale locale) {
        return (Integer)parse(value, pattern, locale);
    }

    /**
     * Check if the value is within a specified range.
     *
     * @param value The <code>Number</code> value to check.
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
     * @return <code>true</code> if the value is within the
     *         specified range.
     */
    public boolean isInRange(int value, int min, int max) {
        return (value >= min && value <= max);
    }

    /**
     * Check if the value is within a specified range.
     *
     * @param value The <code>Number</code> value to check.
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
     * @return <code>true</code> if the value is within the
     *         specified range.
     */
    public boolean isInRange(Integer value, int min, int max) {
        return isInRange(value.intValue(), min, max);
    }

    /**
     * Check if the value is greater than or equal to a minimum.
     *
     * @param value The value validation is being performed on.
     * @param min The minimum value.
     * @return <code>true</code> if the value is greater than
     *         or equal to the minimum.
     */
    public boolean minValue(int value, int min) {
        return (value >= min);
    }

    /**
     * Check if the value is greater than or equal to a minimum.
     *
     * @param value The value validation is being performed on.
     * @param min The minimum value.
     * @return <code>true</code> if the value is greater than
     *         or equal to the minimum.
     */
    public boolean minValue(Integer value, int min) {
        return minValue(value.intValue(), min);
    }

    /**
     * Check if the value is less than or equal to a maximum.
     *
     * @param value The value validation is being performed on.
     * @param max The maximum value.
     * @return <code>true</code> if the value is less than
     *         or equal to the maximum.
     */
    public boolean maxValue(int value, int max) {
        return (value <= max);
    }

    /**
     * Check if the value is less than or equal to a maximum.
     *
     * @param value The value validation is being performed on.
     * @param max The maximum value.
     * @return <code>true</code> if the value is less than
     *         or equal to the maximum.
     */
    public boolean maxValue(Integer value, int max) {
        return maxValue(value.intValue(), max);
    }

    /**
     * <p>Perform further validation and convert the <code>Number</code> to
     * an <code>Integer</code>.</p>
     *
     * @param value The parsed <code>Number</code> object created.
     * @param formatter The Format used to parse the value with.
     * @return The parsed <code>Number</code> converted to an
     *   <code>Integer</code> if valid or <code>null</code> if invalid.
     */
    @Override
    protected Object processParsedValue(Object value, Format formatter) {

        // Parsed value will be Long if it fits in a long and is not fractional
        if (value instanceof Long) {
            long longValue = ((Long)value).longValue();
            if (longValue >= Integer.MIN_VALUE &&
                longValue <= Integer.MAX_VALUE) {
                return Integer.valueOf((int)longValue);
            }
        }
        return null;
    }
}
