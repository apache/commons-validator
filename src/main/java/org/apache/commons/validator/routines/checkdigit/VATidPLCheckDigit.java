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

import org.apache.commons.validator.GenericValidator;

/**
 * Polish VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * Numer identyfikacji podatkowej (NIP)
 * </p>
 * <p>
 * See <a href="https://pl.wikipedia.org/wiki/Numer_identyfikacji_podatkowej">Wikipedia - (pl)</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidPLCheckDigit extends Modulus11XCheckDigit {

    private static final long serialVersionUID = -2927100868701459799L;

    /** Singleton Check Digit instance */
    private static final VATidPLCheckDigit INSTANCE = new VATidPLCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    private static final int LEN = 10; // with Check Digit

    /** Weighting given to digits depending on their left position */
    private static final int[] POSITION_WEIGHT = { 6, 5, 7, 2, 3, 4, 5, 6, 7 };

    /**
     * Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.
     *
     * <p>For VATID digits are weighted by their position from left to right.</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The position of the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(int charValue, int leftPos, int rightPos) throws CheckDigitException {
        if (leftPos >= LEN) {
            return 0; // ignore charValue outside the LEN
        }
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
        return toCheckDigit(INSTANCE.calculateModulus(code, false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        try {
            final int cd = INSTANCE.calculateModulus(code, true);
            return code.endsWith(toCheckDigit(cd));
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Override because charValue X is an invalid check digit value.
     * </p>
     */
    @Override
    protected String toCheckDigit(final int charValue) throws CheckDigitException {
        if (charValue == X) {
            throw new CheckDigitException("Invalid Check Digit Value =" + +charValue);
        }
        return super.toCheckDigit(charValue);
    }

}