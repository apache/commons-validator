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
import java.text.Format;

/**
 * <p><strong>Currency Validation</strong> and Conversion routines ({@code java.math.BigDecimal}).</p>
 *
 * <p>This is one implementation of a currency validator that has the following features:</p>
 *    <ul>
 *       <li>It is <em>lenient</em> about the presence of the <em>currency symbol</em></li>
 *       <li>It converts the currency to a {@code java.math.BigDecimal}</li>
 *    </ul>
 *
 * <p>However any of the <em>number</em> validators can be used for <em>currency</em> validation.
 *    For example, if you wanted a <em>currency</em> validator that converts to a
 *    {@code java.lang.Integer} then you can simply instantiate an
 *    {@code IntegerValidator} with the appropriate <em>format type</em>:</p>
 *
 *    <p>{@code ... = new IntegerValidator(false, IntegerValidator.CURRENCY_FORMAT);}</p>
 *
 * <p>Pick the appropriate validator, depending on the type (for example, Float, Double, Integer, Long and so on)
 *    you want the currency converted to. One thing to note - only the CurrencyValidator
 *    implements <em>lenient</em> behavior regarding the currency symbol.</p>
 *
 * @since 1.3.0
 */
public class CurrencyValidator extends BigDecimalValidator {

    private static final long serialVersionUID = -4201640771171486514L;

    private static final CurrencyValidator VALIDATOR = new CurrencyValidator();

    /** DecimalFormat's currency symbol */
    private static final char CURRENCY_SYMBOL = '\u00A4';

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the CurrencyValidator.
     */
    public static BigDecimalValidator getInstance() {
        return VALIDATOR;
    }

    /**
     * Constructs a <em>strict</em> instance.
     */
    public CurrencyValidator() {
        this(true, true);
    }

    /**
     * Constructs an instance with the specified strict setting.
     *
     * @param strict {@code true} if strict
     *        {@code Format} parsing should be used.
     * @param allowFractions {@code true} if fractions are
     *        allowed or {@code false} if integers only.
     */
    public CurrencyValidator(final boolean strict, final boolean allowFractions) {
        super(strict, CURRENCY_FORMAT, allowFractions);
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
        Object parsedValue = super.parse(value, formatter);
        if (parsedValue != null || !(formatter instanceof DecimalFormat)) {
            return parsedValue;
        }

        // Re-parse using a pattern without the currency symbol
        final DecimalFormat decimalFormat = (DecimalFormat) formatter;
        final String pattern = decimalFormat.toPattern();
        if (pattern.indexOf(CURRENCY_SYMBOL) >= 0) {
            final StringBuilder buffer = new StringBuilder(pattern.length());
            for (int i = 0; i < pattern.length(); i++) {
                if (pattern.charAt(i) != CURRENCY_SYMBOL) {
                    buffer.append(pattern.charAt(i));
                }
            }
            decimalFormat.applyPattern(buffer.toString());
            parsedValue = super.parse(value, decimalFormat);
        }
        return parsedValue;
    }
}
