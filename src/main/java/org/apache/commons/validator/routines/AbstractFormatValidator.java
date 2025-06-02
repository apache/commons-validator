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
package org.apache.commons.validator.routines;

import java.io.Serializable;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Locale;

/**
 * <p>Abstract class for <em>Format</em> based Validation.</p>
 *
 * <p>This is a <em>base</em> class for building Date and Number
 *    Validators using format parsing.</p>
 *
 * @since 1.3.0
 */
public abstract class AbstractFormatValidator implements Serializable {

    private static final long serialVersionUID = -4690687565200568258L;

    /**
     * Whether to use strict format.
     */
    private final boolean strict;

    /**
     * Constructs an instance with the specified strict setting.
     *
     * @param strict {@code true} if strict
     *        {@code Format} parsing should be used.
     */
    public AbstractFormatValidator(final boolean strict) {
        this.strict = strict;
    }

    /**
     * <p>Format an object into a {@link String} using
     * the default Locale.</p>
     *
     * @param value The value validation is being performed on.
     * @return The value formatted as a {@link String}.
     */
    public String format(final Object value) {
        return format(value, (String) null, (Locale) null);
    }

    /**
     * <p>Format a value with the specified {@code Format}.</p>
     *
     * @param value The value to be formatted.
     * @param formatter The Format to use.
     * @return The formatted value.
     */
    protected String format(final Object value, final Format formatter) {
        return formatter.format(value);
    }

    /**
     * <p>Format an object into a {@link String} using
     * the specified Locale.</p>
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the Format.
     * @return The value formatted as a {@link String}.
     */
    public String format(final Object value, final Locale locale) {
        return format(value, (String) null, locale);
    }

    /**
     * <p>Format an object into a {@link String} using
     * the specified pattern.</p>
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to format the value.
     * @return The value formatted as a {@link String}.
     */
    public String format(final Object value, final String pattern) {
        return format(value, pattern, (Locale) null);
    }

    /**
     * <p>Format an object using the specified pattern and/or
     *    {@link Locale}.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to format the value.
     * @param locale The locale to use for the Format.
     * @return The value formatted as a {@link String}.
     */
    public String format(final Object value, final String pattern, final Locale locale) {
        return format(value, getFormat(pattern, locale));
    }

    /**
     * <p>Returns a {@code Format} for the specified <em>pattern</em>
     *    and/or {@link Locale}.</p>
     *
     * @param pattern The pattern used to validate the value against or
     *        {@code null} to use the default for the {@link Locale}.
     * @param locale The locale to use for the currency format, system default if null.
     * @return The {@code NumberFormat} to created.
     */
    protected abstract Format getFormat(String pattern, Locale locale);

    /**
     * <p>Indicates whether validated values should adhere
     *    strictly to the {@code Format} used.</p>
     *
     * <p>Typically implementations of {@code Format}
     *    ignore invalid characters at the end of the value
     *    and just stop parsing. For example parsing a date
     *    value of {@code 01/01/20x0} using a pattern
     *    of {@code dd/MM/yyyy} will result in a year
     *    of {@code 20} if {@code strict} is set
     *    to {@code false}, whereas setting {@code strict}
     *    to {@code true} will cause this value to fail
     *    validation.</p>
     *
     * @return {@code true} if strict {@code Format}
     *         parsing should be used.
     */
    public boolean isStrict() {
        return strict;
    }

    /**
     * <p>Validate using the default {@link Locale}.
     *
     * @param value The value validation is being performed on.
     * @return {@code true} if the value is valid.
     */
    public boolean isValid(final String value) {
        return isValid(value, (String) null, (Locale) null);
    }

    /**
     * <p>Validate using the specified {@link Locale}.
     *
     * @param value The value validation is being performed on.
     * @param locale The locale to use for the Format, defaults to the default
     * @return {@code true} if the value is valid.
     */
    public boolean isValid(final String value, final Locale locale) {
        return isValid(value, (String) null, locale);
    }

    /**
     * <p>Validate using the specified <em>pattern</em>.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @return {@code true} if the value is valid.
     */
    public boolean isValid(final String value, final String pattern) {
        return isValid(value, pattern, (Locale) null);
    }

    /**
     * <p>Validate using the specified pattern and/or {@link Locale}.
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to format the value.
     * @param locale The locale to use for the Format, defaults to the default
     * @return {@code true} if the value is valid.
     */
    public abstract boolean isValid(String value, String pattern, Locale locale);

    /**
     * <p>Parse the value with the specified {@code Format}.</p>
     *
     * @param value The value to be parsed.
     * @param formatter The Format to parse the value with.
     * @return The parsed value if valid or {@code null} if invalid.
     */
    protected Object parse(final String value, final Format formatter) {
        final ParsePosition pos = new ParsePosition(0);
        Object parsedValue = formatter.parseObject(value, pos);
        if (pos.getErrorIndex() > -1 || isStrict() && pos.getIndex() < value.length()) {
            return null;
        }
        if (parsedValue != null) {
            parsedValue = processParsedValue(parsedValue, formatter);
        }
        return parsedValue;

    }

    /**
     * <p>Process the parsed value, performing any further validation
     *    and type conversion required.</p>
     *
     * @param value The parsed object created.
     * @param formatter The Format used to parse the value with.
     * @return The parsed value converted to the appropriate type
     *         if valid or {@code null} if invalid.
     */
    protected abstract Object processParsedValue(Object value, Format formatter);

}
