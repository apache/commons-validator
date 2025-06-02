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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.validator.GenericValidator;

/**
 * <p>Abstract class for Number Validation.</p>
 *
 * <p>This is a <em>base</em> class for building Number
 *    Validators using format parsing.</p>
 *
 * @since 1.3.0
 */
public abstract class AbstractNumberValidator extends AbstractFormatValidator {

    private static final long serialVersionUID = -3088817875906765463L;

    /** Standard {@code NumberFormat} type */
    public static final int STANDARD_FORMAT = 0;

    /** Currency {@code NumberFormat} type */
    public static final int CURRENCY_FORMAT = 1;

    /** Percent {@code NumberFormat} type */
    public static final int PERCENT_FORMAT = 2;

    /**
     * {@code true} if fractions are allowed or {@code false} if integers only.
     */
    private final boolean allowFractions;

    /**
     * The {@code NumberFormat} type to create for validation, default is STANDARD_FORMAT.
     */
    private final int formatType;

    /**
     * Constructs an instance with specified <em>strict</em>
     * and <em>decimal</em> parameters.
     *
     * @param strict {@code true} if strict
     *        {@code Format} parsing should be used.
     * @param formatType The {@code NumberFormat} type to
     *        create for validation, default is STANDARD_FORMAT.
     * @param allowFractions {@code true} if fractions are
     *        allowed or {@code false} if integers only.
     */
    public AbstractNumberValidator(final boolean strict, final int formatType, final boolean allowFractions) {
        super(strict);
        this.allowFractions = allowFractions;
        this.formatType = formatType;
    }

    /**
     * <p>Returns the <em>multiplier</em> of the {@code NumberFormat}.</p>
     *
     * @param format The {@code NumberFormat} to determine the
     *        multiplier of.
     * @return The multiplying factor for the format.
     */
    protected int determineScale(final NumberFormat format) {
        if (!isStrict()) {
            return -1;
        }
        if (!isAllowFractions() || format.isParseIntegerOnly()) {
            return 0;
        }
        final int minimumFraction = format.getMinimumFractionDigits();
        final int maximumFraction = format.getMaximumFractionDigits();
        if (minimumFraction != maximumFraction) {
            return -1;
        }
        int scale = minimumFraction;
        if (format instanceof DecimalFormat) {
            final int multiplier = ((DecimalFormat) format).getMultiplier();
            if (multiplier == 100) { // CHECKSTYLE IGNORE MagicNumber
                scale += 2; // CHECKSTYLE IGNORE MagicNumber
            } else if (multiplier == 1000) { // CHECKSTYLE IGNORE MagicNumber
                scale += 3; // CHECKSTYLE IGNORE MagicNumber
            }
        } else if (formatType == PERCENT_FORMAT) {
            scale += 2; // CHECKSTYLE IGNORE MagicNumber
        }
        return scale;
    }

    /**
     * <p>Returns a {@code NumberFormat} for the specified Locale.</p>
     *
     * @param locale The locale a {@code NumberFormat} is required for,
     *   system default if null.
     * @return The {@code NumberFormat} to created.
     */
    protected Format getFormat(final Locale locale) {
        final NumberFormat formatter;
        switch (formatType) {
        case CURRENCY_FORMAT:
            if (locale == null) {
                formatter = NumberFormat.getCurrencyInstance();
            } else {
                formatter = NumberFormat.getCurrencyInstance(locale);
            }
            break;
        case PERCENT_FORMAT:
            if (locale == null) {
                formatter = NumberFormat.getPercentInstance();
            } else {
                formatter = NumberFormat.getPercentInstance(locale);
            }
            break;
        default:
            if (locale == null) {
                formatter = NumberFormat.getInstance();
            } else {
                formatter = NumberFormat.getInstance(locale);
            }
            if (!isAllowFractions()) {
                formatter.setParseIntegerOnly(true);
            }
            break;
        }
        return formatter;
    }

    /**
     * <p>Returns a {@code NumberFormat} for the specified <em>pattern</em>
     *    and/or {@link Locale}.</p>
     *
     * @param pattern The pattern used to validate the value against or
     *        {@code null} to use the default for the {@link Locale}.
     * @param locale The locale to use for the currency format, system default if null.
     * @return The {@code NumberFormat} to created.
     */
    @Override
    protected Format getFormat(final String pattern, final Locale locale) {
        final NumberFormat formatter;
        final boolean usePattern = !GenericValidator.isBlankOrNull(pattern);
        if (!usePattern) {
            formatter = (NumberFormat) getFormat(locale);
        } else if (locale == null) {
            formatter = new DecimalFormat(pattern);
        } else {
            final DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
            formatter = new DecimalFormat(pattern, symbols);
        }
        if (!isAllowFractions()) {
            formatter.setParseIntegerOnly(true);
        }
        return formatter;
    }

    /**
     * <p>Indicates the type of {@code NumberFormat} created
     *    by this validator instance.</p>
     *
     * @return the format type created.
     */
    public int getFormatType() {
        return formatType;
    }

    /**
     * <p>Indicates whether the number being validated is
     *    a decimal or integer.</p>
     *
     * @return {@code true} if decimals are allowed
     *       or {@code false} if the number is an integer.
     */
    public boolean isAllowFractions() {
        return allowFractions;
    }

    /**
     * Check if the value is within a specified range.
     *
     * @param value The value validation is being performed on.
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
     * @return {@code true} if the value is within the
     *         specified range.
     */
    public boolean isInRange(final Number value, final Number min, final Number max) {
        return minValue(value, min) && maxValue(value, max);
    }

    /**
     * <p>Validate using the specified {@link Locale}.</p>
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against, or the
     *        default for the {@link Locale} if {@code null}.
     * @param locale The locale to use for the date format, system default if null.
     * @return {@code true} if the value is valid.
     */
    @Override
    public boolean isValid(final String value, final String pattern, final Locale locale) {
        return parse(value, pattern, locale) != null;
    }

    /**
     * Check if the value is less than or equal to a maximum.
     *
     * @param value The value validation is being performed on.
     * @param max The maximum value.
     * @return {@code true} if the value is less than
     *         or equal to the maximum.
     */
    public boolean maxValue(final Number value, final Number max) {
        if (isAllowFractions()) {
            return value.doubleValue() <= max.doubleValue();
        }
        return value.longValue() <= max.longValue();
    }

    /**
     * Check if the value is greater than or equal to a minimum.
     *
     * @param value The value validation is being performed on.
     * @param min The minimum value.
     * @return {@code true} if the value is greater than
     *         or equal to the minimum.
     */
    public boolean minValue(final Number value, final Number min) {
        if (isAllowFractions()) {
            return value.doubleValue() >= min.doubleValue();
        }
        return value.longValue() >= min.longValue();
    }

    /**
     * <p>Parse the value using the specified pattern.</p>
     *
     * @param value The value validation is being performed on.
     * @param pattern The pattern used to validate the value against, or the
     *        default for the {@link Locale} if {@code null}.
     * @param locale The locale to use for the date format, system default if null.
     * @return The parsed value if valid or {@code null} if invalid.
     */
    protected Object parse(String value, final String pattern, final Locale locale) {
        value = value == null ? null : value.trim();
        final String value1 = value;
        if (GenericValidator.isBlankOrNull(value1)) {
            return null;
        }
        final Format formatter = getFormat(pattern, locale);
        return parse(value, formatter);

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
    @Override
    protected abstract Object processParsedValue(Object value, Format formatter);
}
