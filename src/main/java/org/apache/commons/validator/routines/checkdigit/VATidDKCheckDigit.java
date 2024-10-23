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
 * Danish VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * Nmomsregistreringsnummer (SE-nr.)
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia - VAT IN</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidDKCheckDigit extends Modulus11XCheckDigit {

    private static final long serialVersionUID = 8760206264738893571L;

    /** Weighting given to digits depending on their left position */
    private static final int[] POSITION_WEIGHT = { 2, 7, 6, 5, 4, 3, 2, 1 };

    /** Singleton Check Digit instance */
    private static final VATidDKCheckDigit INSTANCE = new VATidDKCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    /**
     * Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.
     *
     * <p>For VATID digits are weighted by their position from left to right.</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(int charValue, int leftPos, int rightPos) throws CheckDigitException {
        final int weight = POSITION_WEIGHT[(leftPos - 1)];
        return charValue * weight;
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
        return toCheckDigit(INSTANCE.calculateModulus(code, false));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Override because valid codes has "0" calculated check digit
     * and hence Danish VATIN does not contain a check digit.
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
