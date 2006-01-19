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

import java.util.Locale;

/**
 * <p><b>Short Validation</b> and Conversion routines (<code>java.lang.Short</code>).</p>
 *
 * <p>This validator provides a number of methods for
 *    validating/converting a <code>String</code> value to
 *    a <code>Short</code> using <code>java.text.NumberFormat</code>
 *    to parse either:</p>
 *    <ul>
 *       <li>using a specified pattern</li>
 *       <li>using the format for a specified <code>Locale</code></li>
 *       <li>using the format for the <i>default</i> <code>Locale</code></li>
 *    </ul>
 *    
 * <p>Use one of the <code>isValid()</code> methods to just validate or
 *    one of the <code>validate()</code> methods to validate and receive a
 *    <i>converted</i> <code>Short</code> value.</p>
 * 
 * <p>Once a value has been sucessfully converted the following
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
 *       <li>using a specified pattern</li>
 *       <li>using the format for a specified <code>Locale</code></li>
 *       <li>using the format for the <i>default</i> <code>Locale</code></li>
 *    </ul>
 *
 * @version $Revision$ $Date$
 * @since Validator 1.2.1
 */
public class ShortValidator extends AbstractNumberValidator {

    private static final ShortValidator VALIDATOR = new ShortValidator();

    /**
     * Return a singleton instance of this validator.
     * @return A singleton instance of the ShortValidator.
     */
    public static ShortValidator getInstance() {
        return VALIDATOR;
    }

    /**
     * Construct a <i>strict</i> instance.
     */
    public ShortValidator() {
        this(true);
    }

    /**
     * Construct an instance with the specified strict setting.
     * 
     * @param strict <code>true</code> if strict 
     *        <code>Format</code> parsing should be used.
     */
    public ShortValidator(boolean strict) {
        super(strict, false);
    }

    /**
     * <p>Validate/convert a <code>Short</code> using the default
     *    <code>Locale</code>. 
     *
     * @param value The value validation is being performed on.
     * @return The parsed <code>Short</code> if valid or <code>null</code>
     *  if invalid.
     */
    public Short validate(String value) {
        return (Short)validateObj(value);
    }

    /**
     * <p>Validate/convert a <code>Short</code> using the
     *    specified <i>pattern</i>. 
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @return The parsed <code>Short</code> if valid or <code>null</code> if invalid.
     */
    public Short validate(String value, String pattern) {
        return (Short)validateObj(value, pattern);
    }

    /**
     * <p>Validate/convert a <code>Short</code> using the
     *    specified <code>Locale</code>. 
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the date format, defaults to the default
     * system default if null.
     * @return The parsed <code>Short</code> if valid or <code>null</code> if invalid.
     */
    public Short validate(String value, Locale locale) {
        return (Short)validateObj(value, locale);
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
    public boolean isInRange(short value, short min, short max) {
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
    public boolean isInRange(Short value, short min, short max) {
        return isInRange(value.shortValue(), min, max);
    }

    /**
     * Check if the value is greater than or equal to a minimum.
     * 
     * @param value The value validation is being performed on.
     * @param min The minimum value.
     * @return <code>true</code> if the value is greater than
     *         or equal to the minimum.
     */
    public boolean minValue(short value, short min) {
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
    public boolean minValue(Short value, short min) {
        return minValue(value.shortValue(), min);
    }

    /**
     * Check if the value is less than or equal to a maximum.
     * 
     * @param value The value validation is being performed on.
     * @param max The maximum value.
     * @return <code>true</code> if the value is less than
     *         or equal to the maximum.
     */
    public boolean maxValue(short value, short max) {
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
    public boolean maxValue(Short value, short max) {
        return maxValue(value.shortValue(), max);
    }

    /**
     * <p>Perform further validation and convert the <code>Number</code> to
     * a <code>Short</code>.</p>
     * 
     * @param number The number validation is being performed on.
     * @return The validated/converted <code>Short</code> value if valid 
     * or <code>null</code> if invalid.
     */
    protected Object processNumber(Number number) {

        long longValue = number.longValue();

        if (longValue < Short.MIN_VALUE) {
            return null;
        }

        if (longValue > Short.MAX_VALUE) {
            return null;
        }

        return new Short(number.shortValue());

    }
}
