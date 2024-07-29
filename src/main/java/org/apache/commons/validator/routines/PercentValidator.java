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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;

/**
 * <p><b>Percentage Validation</b> and Conversion routines ({@code java.math.BigDecimal}).</p>
 *
 * <p>This is one implementation of a percent validator that has the following features:</p>
 *    <ul>
 *       <li>It is <em>lenient</em> about the presence of the <em>percent symbol</em></li>
 *       <li>It converts the percent to a {@code java.math.BigDecimal}</li>
 *    </ul>
 *
 * <p>However any of the <em>number</em> validators can be used for <em>percent</em> validation.
 *    For example, if you wanted a <em>percent</em> validator that converts to a
 *    {@code java.lang.Float} then you can simply instantiate an
 *    {@code FloatValidator} with the appropriate <em>format type</em>:</p>
 *
 *    <p>{@code ... = new FloatValidator(false, FloatValidator.PERCENT_FORMAT);}</p>
 *
 * <p>Pick the appropriate validator, depending on the type (i.e Float, Double or BigDecimal)
 *    you want the percent converted to. Please note, it makes no sense to use
 *    one of the validators that doesn't handle fractions (i.e. byte, short, integer, long
 *    and BigInteger) since percentages are converted to fractions (i.e {@code 50%} is
 *    converted to {@code 0.5}).</p>
 *
 * @since 1.3.0
 */
public class PercentValidator extends BigDecimalValidator {

    private static final long serialVersionUID = -3508241924961535772L;

    private static final PercentValidator VALIDATOR = new PercentValidator();

    /** DecimalFormat's percent (thousand multiplier) symbol */
    private static final char PERCENT_SYMBOL = '%';

    private static final BigDecimal POINT_ZERO_ONE = new BigDecimal("0.01");

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the PercentValidator.
     */
    public static BigDecimalValidator getInstance() {
        return VALIDATOR;
    }

    /**
     * Constructs a <em>strict</em> instance.
     */
    public PercentValidator() {
        this(true);
    }

    /**
     * Constructs an instance with the specified strict setting.
     *
     * @param strict {@code true} if strict
     *        {@code Format} parsing should be used.
     */
    public PercentValidator(final boolean strict) {
        super(strict, PERCENT_FORMAT, true);
    }

    /**
     * <p>Parse the value with the specified {@code Format}.</p>
     *
     * <p>This implementation is lenient whether the currency symbol
     *    is present or not. The default {@code NumberFormat}
     *    behavior is for the parsing to "fail" if the currency
     *    symbol is missing. This method re-parses with a format
     *    without the currency symbol if it fails initially.</p>
     *
     * @param value The value to be parsed.
     * @param formatter The Format to parse the value with.
     * @return The parsed value if valid or {@code null} if invalid.
     */
    @Override
    protected Object parse(final String value, final Format formatter) {

        // Initial parse of the value
        BigDecimal parsedValue = (BigDecimal) super.parse(value, formatter);
        if (parsedValue != null || !(formatter instanceof DecimalFormat)) {
            return parsedValue;
        }

        // Re-parse using a pattern without the percent symbol
        final DecimalFormat decimalFormat = (DecimalFormat) formatter;
        final String pattern = decimalFormat.toPattern();
        if (pattern.indexOf(PERCENT_SYMBOL) >= 0) {
            final StringBuilder buffer = new StringBuilder(pattern.length());
            for (int i = 0; i < pattern.length(); i++) {
                if (pattern.charAt(i) != PERCENT_SYMBOL) {
                    buffer.append(pattern.charAt(i));
                }
            }
            decimalFormat.applyPattern(buffer.toString());
            parsedValue = (BigDecimal) super.parse(value, decimalFormat);

            // If parsed OK, divide by 100 to get percent
            if (parsedValue != null) {
                parsedValue = parsedValue.multiply(POINT_ZERO_ONE);
            }

        }
        return parsedValue;
    }
}
