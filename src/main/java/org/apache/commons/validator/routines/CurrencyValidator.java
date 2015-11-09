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

import java.text.DecimalFormat;
import java.text.Format;

/**
 * <p><b>Currency Validation</b> and Conversion routines (<code>java.math.BigDecimal</code>).</p>
 *
 * <p>This is one implementation of a currency validator that has the following features:</p>
 *    <ul>
 *       <li>It is <i>lenient</i> about the the presence of the <i>currency symbol</i></li>
 *       <li>It converts the currency to a <code>java.math.BigDecimal</code></li>
 *    </ul>
 *
 * <p>However any of the <i>number</i> validators can be used for <i>currency</i> validation.
 *    For example, if you wanted a <i>currency</i> validator that converts to a
 *    <code>java.lang.Integer</code> then you can simply instantiate an
 *    <code>IntegerValidator</code> with the appropriate <i>format type</i>:</p>
 *
 *    <p><code>... = new IntegerValidator(false, IntegerValidator.CURRENCY_FORMAT);</code></p>
 *
 * <p>Pick the appropriate validator, depending on the type (e.g Float, Double, Integer, Long etc)
 *    you want the currency converted to. One thing to note - only the CurrencyValidator
 *    implements <i>lenient</i> behaviour regarding the currency symbol.</p>
 *
 * @version $Revision$
 * @since Validator 1.3.0
 */
public class CurrencyValidator extends BigDecimalValidator {

    private static final long serialVersionUID = -4201640771171486514L;

    private static final CurrencyValidator VALIDATOR = new CurrencyValidator();

    /** DecimalFormat's currency symbol */
    private static final char CURRENCY_SYMBOL = '\u00A4';

    /**
     * Return a singleton instance of this validator.
     * @return A singleton instance of the CurrencyValidator.
     */
    public static BigDecimalValidator getInstance() {
        return VALIDATOR;
    }

    /**
     * Construct a <i>strict</i> instance.
     */
    public CurrencyValidator() {
        this(true, true);
    }

    /**
     * Construct an instance with the specified strict setting.
     *
     * @param strict <code>true</code> if strict
     *        <code>Format</code> parsing should be used.
     * @param allowFractions <code>true</code> if fractions are
     *        allowed or <code>false</code> if integers only.
     */
    public CurrencyValidator(boolean strict, boolean allowFractions) {
        super(strict, CURRENCY_FORMAT, allowFractions);
    }

    /**
     * <p>Parse the value with the specified <code>Format</code>.</p>
     *
     * <p>This implementation is lenient whether the currency symbol
     *    is present or not. The default <code>NumberFormat</code>
     *    behaviour is for the parsing to "fail" if the currency
     *    symbol is missing. This method re-parses with a format
     *    without the currency symbol if it fails initially.</p>
     *
     * @param value The value to be parsed.
     * @param formatter The Format to parse the value with.
     * @return The parsed value if valid or <code>null</code> if invalid.
     */
    protected Object parse(String value, Format formatter) {

        // Initial parse of the value
        Object parsedValue = super.parse(value, formatter);
        if (parsedValue != null || !(formatter instanceof DecimalFormat)) {
            return parsedValue;
        }

        // Re-parse using a pattern without the currency symbol
        DecimalFormat decimalFormat = (DecimalFormat)formatter;
        String pattern = decimalFormat.toPattern();
        if (pattern.indexOf(CURRENCY_SYMBOL) >= 0) {
            StringBuilder buffer = new StringBuilder(pattern.length());
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
