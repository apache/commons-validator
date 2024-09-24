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
public final class VATidDKCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = 8760206264738893571L;

    /** Singleton Check Digit instance */
    private static final VATidDKCheckDigit INSTANCE = new VATidDKCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    static final int LEN = 8;

    /**
     * Constructs a Check Digit routine.
     */
    private VATidDKCheckDigit() {
        super(MODULUS_11);
    }

    /** Weighting given to digits depending on their left position */
    private static final int[] POSITION_WEIGHT = { 2, 7, 6, 5, 4, 3, 2, 1 };

    /**
     * Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.
     *
     * <p>For VATID digits are weighted by the power of 10 of their right position.</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The position of the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
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
        if (code.length() >= LEN && GenericTypeValidator.formatLong(code.substring(0, LEN)) == 0) {
            throw new CheckDigitException(CheckDigitException.ZREO_SUM);
        }
        int cdValue = super.calculateModulus(code, false);
        return toCheckDigit(cdValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        if (code.length() != LEN) {
            return false;
        }
        try {
            final int modulusResult = INSTANCE.calculateModulus(code, true);
            return modulusResult == 0;
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
