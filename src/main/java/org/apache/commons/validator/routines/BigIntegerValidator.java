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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * <strong>BigInteger Validation</strong> and Conversion routines ({@code java.math.BigInteger}).
 *
 * <p>
 * This validator provides a number of methods for validating/converting a {@link String} value to a {@code BigInteger} using {@link NumberFormat} to parse
 * either:
 * </p>
 * <ul>
 * <li>using the default format for the default {@link Locale}</li>
 * <li>using a specified pattern with the default {@link Locale}</li>
 * <li>using the default format for a specified {@link Locale}</li>
 * <li>using a specified pattern with a specified {@link Locale}</li>
 * </ul>
 *
 * <p>
 * Use one of the {@code isValid()} methods to just validate or one of the {@code validate()} methods to validate and receive a <em>converted</em>
 * {@code BigInteger} value.
 * </p>
 *
 * <p>
 * Once a value has been successfully converted the following methods can be used to perform minimum, maximum and range checks:
 * </p>
 * <ul>
 * <li>{@code minValue()} checks whether the value is greater than or equal to a specified minimum.</li>
 * <li>{@code maxValue()} checks whether the value is less than or equal to a specified maximum.</li>
 * <li>{@code isInRange()} checks whether the value is within a specified range of values.</li>
 * </ul>
 *
 * <p>
 * So that the same mechanism used for parsing an <em>input</em> value for validation can be used to format <em>output</em>, corresponding {@code format()}
 * methods are also provided. That is you can format either:
 * </p>
 * <ul>
 * <li>using the default format for the default {@link Locale}</li>
 * <li>using a specified pattern with the default {@link Locale}</li>
 * <li>using the default format for a specified {@link Locale}</li>
 * <li>using a specified pattern with a specified {@link Locale}</li>
 * </ul>
 *
 * @since 1.3.0
 */
public class BigIntegerValidator extends AbstractNumberValidator {

    private static final long serialVersionUID = 6713144356347139988L;
    private static final BigIntegerValidator VALIDATOR = new BigIntegerValidator();

    /**
     * Gets the singleton instance of this validator.
     *
     * @return A singleton instance of the BigIntegerValidator.
     */
    public static BigIntegerValidator getInstance() {
        return VALIDATOR;
    }

    private static BigInteger toBigInteger(final Object value) {
        if (value instanceof Long) {
            return BigInteger.valueOf(((Long) value).longValue());
        }
        if (value instanceof Double) {
            // No need to roundtrip with a string.
            return BigDecimal.valueOf(((Double) value).doubleValue()).toBigInteger();
        }
        return value instanceof BigInteger ? (BigInteger) value : new BigDecimal(value.toString()).toBigInteger();
    }

    /**
     * Constructs a <em>strict</em> instance.
     */
    public BigIntegerValidator() {
        this(true, STANDARD_FORMAT);
    }

    /**
     * Construct an instance with the specified strict setting and format type.
     *
     * <p>
     * The {@code formatType} specified what type of {@code NumberFormat} is created - valid types are:
     * </p>
     * <ul>
     * <li>AbstractNumberValidator.STANDARD_FORMAT -to create <em>standard</em> number formats (the default).</li>
     * <li>AbstractNumberValidator.CURRENCY_FORMAT -to create <em>currency</em> number formats.</li>
     * <li>AbstractNumberValidator.PERCENT_FORMAT -to create <em>percent</em> number formats (the default).</li>
     * </ul>
     *
     * @param strict     {@code true} if strict {@code Format} parsing should be used.
     * @param formatType The {@code NumberFormat} type to create for validation, default is STANDARD_FORMAT.
     */
    public BigIntegerValidator(final boolean strict, final int formatType) {
        super(strict, formatType, false);
    }

    /**
     * Returns a {@code Format} that parses to a {@code BigDecimal} so the exact value of the input is preserved.
     *
     * <p>
     * The superclass leaves {@link DecimalFormat} in its default mode, where {@code parse} yields a {@code Double} for a
     * value outside the {@code long} range and so rounds an integer carrying more significant digits than a {@code double}
     * can hold. Enabling {@link DecimalFormat#setParseBigDecimal(boolean)} keeps the full magnitude through parsing before
     * it is converted to a {@code BigInteger}.
     * </p>
     *
     * @param pattern The pattern used to validate the value against or {@code null} to use the default for the {@link Locale}.
     * @param locale  The locale to use for the format, system default if null.
     * @return The {@code Format} to use.
     */
    @Override
    protected Format getFormat(final String pattern, final Locale locale) {
        return setParseBigDecimal(super.getFormat(pattern, locale));
    }

    /**
     * Tests if the value is within a specified range.
     *
     * @param value The {@code Number} value to check.
     * @param min   The minimum value of the range.
     * @param max   The maximum value of the range.
     * @return {@code true} if the value is within the specified range.
     */
    public boolean isInRange(final BigInteger value, final long min, final long max) {
        return minValue(value, min) && maxValue(value, max);
    }

    /**
     * Tests if the value is less than or equal to a maximum.
     *
     * @param value The value validation is being performed on.
     * @param max   The maximum value.
     * @return {@code true} if the value is less than or equal to the maximum.
     */
    public boolean maxValue(final BigInteger value, final long max) {
        return value.compareTo(BigInteger.valueOf(max)) <= 0;
    }

    /**
     * Tests if the value is less than or equal to a maximum, comparing the exact values.
     *
     * <p>
     * This overrides the {@link Number} overload inherited from the superclass, which narrows the value to a {@code long} before comparing and so loses
     * magnitude for a {@code BigInteger} outside the long range.
     * </p>
     *
     * @param value The value validation is being performed on.
     * @param max   The maximum value.
     * @return {@code true} if the value is less than or equal to the maximum.
     */
    @Override
    public boolean maxValue(final Number value, final Number max) {
        return toBigInteger(value).compareTo(toBigInteger(max)) <= 0;
    }

    /**
     * Tests if the value is greater than or equal to a minimum.
     *
     * @param value The value validation is being performed on.
     * @param min   The minimum value.
     * @return {@code true} if the value is greater than or equal to the minimum.
     */
    public boolean minValue(final BigInteger value, final long min) {
        return value.compareTo(BigInteger.valueOf(min)) >= 0;
    }

    /**
     * Tests if the value is greater than or equal to a minimum, comparing the exact values.
     *
     * <p>
     * This overrides the {@link Number} overload inherited from the superclass, which narrows the value to a {@code long} before comparing and so loses
     * magnitude for a {@code BigInteger} outside the long range.
     * </p>
     *
     * @param value The value validation is being performed on.
     * @param min   The minimum value.
     * @return {@code true} if the value is greater than or equal to the minimum.
     */
    @Override
    public boolean minValue(final Number value, final Number min) {
        return toBigInteger(value).compareTo(toBigInteger(min)) >= 0;
    }

    /**
     * Converts the parsed value to a {@code BigInteger}.
     *
     * @param value     The parsed {@code Number} object created.
     * @param formatter The Format used to parse the value with.
     * @return The parsed {@code Number} converted to a {@code BigInteger}.
     */
    @Override
    protected Object processParsedValue(final Object value, final Format formatter) {
        return toBigInteger(value);
    }

    /**
     * Validates and converts a {@code BigInteger} using the default {@link Locale}.
     *
     * @param value The value validation is being performed on.
     * @return The parsed {@code BigInteger} if valid or {@code null} if invalid.
     */
    public BigInteger validate(final String value) {
        return (BigInteger) parse(value, (String) null, (Locale) null);
    }

    /**
     * Validates and converts a {@code BigInteger} using the specified {@link Locale}.
     *
     * @param value  The value validation is being performed on.
     * @param locale The locale to use for the number format, system default if null.
     * @return The parsed {@code BigInteger} if valid or {@code null} if invalid.
     */
    public BigInteger validate(final String value, final Locale locale) {
        return (BigInteger) parse(value, (String) null, locale);
    }

    /**
     * Validates and converts a {@code BigInteger} using the specified <em>pattern</em>.
     *
     * @param value   The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @return The parsed {@code BigInteger} if valid or {@code null} if invalid.
     */
    public BigInteger validate(final String value, final String pattern) {
        return (BigInteger) parse(value, pattern, (Locale) null);
    }

    /**
     * Validates and converts a {@code BigInteger} using the specified pattern and/ or {@link Locale}.
     *
     * @param value   The value validation is being performed on.
     * @param pattern The pattern used to validate the value against, or the default for the {@link Locale} if {@code null}.
     * @param locale  The locale to use for the date format, system default if null.
     * @return The parsed {@code BigInteger} if valid or {@code null} if invalid.
     */
    public BigInteger validate(final String value, final String pattern, final Locale locale) {
        return (BigInteger) parse(value, pattern, locale);
    }
}
