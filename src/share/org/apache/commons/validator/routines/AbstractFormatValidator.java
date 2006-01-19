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
import java.text.ParsePosition;
import java.util.Locale;
import java.io.Serializable;

/**
 * <p>Abstract class for <i>Format</i> based Validation.</p>
 *
 * <p>This is a <i>base</i> class for building Date and Number
 *    Validators using format parsing.</p>
 * 
 * @version $Revision$ $Date$
 * @since Validator 1.2.1
 */
public abstract class AbstractFormatValidator implements Serializable {

    private boolean strict = true;

    /**
     * Construct an instance with the specified strict setting.
     * 
     * @param strict <code>true</code> if strict 
     *        <code>Format</code> parsing should be used.
     */
    public AbstractFormatValidator(boolean strict) {
        this.strict = strict;
    }

    /**
     * <p>Indicates whether validated values should adhere
     *    strictly to the <code>Format</code> used.</p>
     * 
     * <p>Typically implementations of <code>Format</code>
     *    ignore invalid characters at the end of the value
     *    and just stop parsing. For example parsing a date
     *    value of <code>01/01/20x0</code> using a pattern
     *    of <code>dd/MM/yyyy</code> will result in a year
     *    of <code>20</code> if <code>strict</code> is set
     *    to <code>false</code>, whereas setting <code>strict</code>
     *    to <code>true</code> will cause this value to fail
     *    validation.</p>
     * 
     * @return <code>true</code> if strict <code>Format</code>
     *         parsing should be used.
     */
    public boolean isStrict() {
        return strict;
    }

    /**
     * <p>Validate using the default <code>Locale</code>. 
     * 
     * @param value The value validation is being performed on.
     * @return <code>true</code> if the value is valid.
     */
    public boolean isValid(String value) {
        Object parsedValue = validateObj(value);
        return (parsedValue == null ? false : true);
    }

    /**
     * <p>Validate using the specified <i>pattern</i>. 
     * 
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @return <code>true</code> if the value is valid.
     */
    public boolean isValid(String value, String pattern) {
        Object parsedValue = validateObj(value, pattern);
        return (parsedValue == null ? false : true);
    }

    /**
     * <p>Validate using the specified <code>Locale</code>. 
     * 
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the Format, defaults to the default
     * @return <code>true</code> if the value is valid.
     */
    public boolean isValid(String value, Locale locale) {
        Object parsedValue = validateObj(value, locale);
        return (parsedValue == null ? false : true);
    }

    /**
     * <p>Format an object into a <code>String</code> using
     * the default Locale.</p>
     *
     * @param value The value validation is being performed on.
     * @return The value formatted as a <code>String</code>.
     */
    public String format(Object value) {
        return format(value, Locale.getDefault());
    }

    /**
     * <p>Format an object into a <code>String</code> using
     * the specified pattern.</p>
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to format the value.
     * @return The value formatted as a <code>String</code>.
     */
    public String format(Object value, String pattern) {
        Format formatter = getFormat(pattern);
        return format(value, formatter);
    }

    /**
     * <p>Format an object into a <code>String</code> using
     * the specified Locale.</p>
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the Format.
     * @return The value formatted as a <code>String</code>.
     */
    public String format(Object value, Locale locale) {
        Format formatter = getFormat(locale);
        return format(value, formatter);
    }

    /**
     * <p>Format a value with the specified <code>Format</code>.</p>
     * 
     * @param value The value to be formatted.
     * @param formatter The Format to use.
     * @return The formatted value.
     */
    protected String format(Object value, Format formatter) {
        return formatter.format(value);
    }

    /**
     * <p>Checks if the value is valid using the default Locale.</p>
     *
     * @param value The value validation is being performed on.
     * @return The processed value if valid or <code>null</code> if invalid.
     */
    protected Object validateObj(String value) {
        return validateObj(value, Locale.getDefault());
    }

    /**
     * <p>Checks if the value is valid against a specified pattern.</p>
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @return The parsed value if valid or <code>null</code> if invalid.
     */
    protected Object validateObj(String value, String pattern) {

        if (value == null || value.length() == 0) {
            return null;
        }
        Format formatter = getFormat(pattern);
        return parse(value, formatter);

    }

    /**
     * <p>Checks if the value is valid for a specified <code>Locale</code>.</p>
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the date format, defaults to the default
     * system default if null.
     * @return The parsed value if valid or <code>null</code> if invalid.
     */
    protected Object validateObj(String value, Locale locale) {

        if (value == null || value.length() == 0) {
            return null;
        }
        Format formatter = getFormat(locale);
        return parse(value, formatter);

    }

    /**
     * <p>Parse the value with the specified <code>Format</code>.</p>
     * 
     * @param value The value to be parsed.
     * @param formatter The Format to parse the value with.
     * @return The parsed value if valid or <code>null</code> if invalid.
     */
    protected Object parse(String value, Format formatter) {

        String tempValue = (value == null ? null : value.trim());
        if (tempValue == null || tempValue.length() == 0) {
            return null;
        }

        ParsePosition pos = new ParsePosition(0);
        Object parsedValue = formatter.parseObject(tempValue, pos);

        if (pos.getErrorIndex() > -1) {
            return null;
        }

        if (isStrict() && pos.getIndex() < tempValue.length()) {
            return null;
        }

        return parsedValue;

    }

    /**
     * <p>Returns a <code>Format</code> for the specified pattern.</p>
     * 
     * @param pattern The pattern of the required the <code>Format</code>.
     * @return The <code>Format</code> to created.
     */
    protected abstract Format getFormat(String pattern);

    /**
     * <p>Returns a <code>Format</code> for the specified Locale.</p>
     * 
     * @param locale The locale a <code>Format</code> is required for,
     *        defaults to the default
     * @return The <code>Format</code> to created.
     */
    protected abstract Format getFormat(Locale locale);

}
