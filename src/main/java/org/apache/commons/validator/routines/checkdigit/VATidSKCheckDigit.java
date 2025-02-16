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
package org.apache.commons.validator.routines.checkdigit;

import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.GenericValidator;

/**
 * Slovakia VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * Identifikačné číslo pre daň z pridanej hodnoty (IČ DPH).
 * The IČ DPH is a 10-digit number used for VAT purposes. It has a straightforward checksum.
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidSKCheckDigit extends Modulus11XCheckDigit {

    private static final long serialVersionUID = 5022933940504538766L;

    /** Singleton Check Digit instance */
    private static final VATidSKCheckDigit INSTANCE = new VATidSKCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    /**
     * Constructs a new instance.
     */
    private VATidSKCheckDigit() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
        }
        // Satisfy testZeroSum
        final Long l = GenericTypeValidator.formatLong(code);
        if (l == null) {
            throw new CheckDigitException("Invalid code " + code);
        }
        if (l == 0) {
            throw new CheckDigitException(CheckDigitException.ZERO_SUM);
        }
        return toCheckDigit((int) (l % MODULUS_11));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Override because valid codes has "0" calculated check digit
     * and hence Slovakian VATIN does not contain a check digit.
     * </p>
     */
    @Override
    public boolean isValid(final String code) {
        try {
            return "0".equals(calculate(code));
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Override because charValue 0 is the only valid check digit value.
     * </p>
     */
    @Override
    protected String toCheckDigit(final int charValue) throws CheckDigitException {
        if (charValue == 0) {
            return super.toCheckDigit(charValue);
        }
        throw new CheckDigitException("Invalid Check Digit Value =" + +charValue);
    }

}
