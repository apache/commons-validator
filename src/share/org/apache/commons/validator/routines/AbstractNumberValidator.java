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
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * <p>Abstract class for Number Validation.</p>
 *
 * <p>This is a <i>base</i> class for building Number
 *    Validators using format parsing.</p>
 *    
 * @version $Revision$ $Date$
 * @since Validator 1.2.1
 */
public abstract class AbstractNumberValidator extends AbstractFormatValidator {

    private boolean decimal;

    /**
     * Construct a <i>strict</i> instance for <i>decimal</i>
     * formats.
     */
    public AbstractNumberValidator() {
        this(true, true);
    }

    /**
     * Construct an instance with specified <i>strict</i>
     * and <i>decimal</i> parameters.
     * 
     * @param strict <code>true</code> if strict 
     *        <code>Format</code> parsing should be used.
     * @param decimal Set whether this validator handlers decimal
     * values (or only integer numbers).
     */
    public AbstractNumberValidator(boolean strict, boolean decimal) {
        super(strict);
        this.decimal = decimal;
    }

    /**
     * Check if the value is within a specified range.
     * 
     * @param value The value validation is being performed on.
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
     * @return <code>true</code> if the value is within the
     *         specified range.
     */
    public boolean isInRange(Number value, Number min, Number max) {
        return (minValue(value, min) && maxValue(value, max));
    }

    /**
     * Check if the value is greater than or equal to a minimum.
     * 
     * @param value The value validation is being performed on.
     * @param min The minimum value.
     * @return <code>true</code> if the value is greater than
     *         or equal to the minimum.
     */
    public boolean minValue(Number value, Number min) {
        if (decimal) {
            return (value.doubleValue() >= min.doubleValue());
        } else {
            return (value.longValue() >= min.longValue());
        }
    }

    /**
     * Check if the value is less than or equal to a maximum.
     * 
     * @param value The value validation is being performed on.
     * @param max The maximum value.
     * @return <code>true</code> if the value is less than
     *         or equal to the maximum.
     */
    public boolean maxValue(Number value, Number max) {
        if (decimal) {
            return (value.doubleValue() <= max.doubleValue());
        } else {
            return (value.longValue() <= max.longValue());
        }
    }

    /**
     * <p>Creates a <code>DecimalFormat</code> for the specified
     *    pattern.</p>
     * 
     * <p>If no pattern is specified the default Locale is used
     *    to determine the <code>NumberFormat</code>.
     * 
     * @param pattern The pattern of the required the <code>DecimalFormat</code>.
     * @return The <code>NumberFormat</code> to created.
     */
    protected Format getFormat(String pattern) {

        NumberFormat formatter = null;
        if (pattern == null || pattern.length() == 0) {
            formatter = (NumberFormat)getFormat(Locale.getDefault());
        } else {
            formatter = new DecimalFormat(pattern);
        }
        if (isStrict() && !decimal) {
            formatter.setParseIntegerOnly(true);
        }
        return formatter;

    }

    /**
     * <p>Returns a <code>NumberFormat</code> for the specified Locale.</p>
     * 
     * @param locale The locale a <code>NumberFormat</code> is required
     *        for, defaults to the default.
     * @return The <code>NumberFormat</code> to created.
     */
    protected Format getFormat(Locale locale) {

        if (locale == null) {
            locale = Locale.getDefault();
        }

        NumberFormat formatter = NumberFormat.getInstance(locale);
        if (isStrict() && !decimal) {
            formatter.setParseIntegerOnly(true);
        }

        return formatter;

    }

    /**
     * <p>Parse the value with the specified <code>NumberFormat</code>.</p>
     * 
     * @param value The value to be parsed.
     * @param formatter The Format to parse the value with.
     * @return The parsed value if valid or <code>null</code> if invalid.
     */
    protected Object parse(String value, Format formatter) {

        Number number = (Number)super.parse(value, formatter);
        if (number == null) {
            return null;
        }

        // Process the parsed Number
        return processNumber(number);

    }

    /**
     * <p>Perform further validation and convert the <code>Number</code> to
     * the appropriate type.</p>
     * 
     * @param number The number object created from the parsed value.
     * @return The validated/converted <code>Number</code> value if valid 
     * or <code>null</code> if invalid.
     */
    protected abstract Object processNumber(Number number);

}
