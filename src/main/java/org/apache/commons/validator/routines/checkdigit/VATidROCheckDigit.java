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
 * Romanian VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * cod de înregistrare în scopuri de TVA (TVA), sometimes called Codul de identificare fiscală (CIF)
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia - VAT IN</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidROCheckDigit extends Modulus11XCheckDigit {

    private static final long serialVersionUID = 159727558301530535L;

    /** Singleton Check Digit instance */
    private static final VATidROCheckDigit INSTANCE = new VATidROCheckDigit();

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
    private VATidROCheckDigit() {
    }

    private static final int LEN = 10; // with Check Digit

    /** Weighting given to digits depending on their left position */
    private static final int[] POSITION_WEIGHT = { 7, 5, 3, 2, 1 };

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
        final int weight = POSITION_WEIGHT[(leftPos - 1) % POSITION_WEIGHT.length];
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
        String pcode = code;
        // fill with leading zeros:
        if (code.length() < LEN - 1) {
            pcode = "0000000000".substring(0, LEN - 1 - code.length()) + code;
        }
        return toCheckDigit(INSTANCE.calculateModulus(pcode, false));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Override to multiply the total by 10.
     * </p>
     */
    @Override
    protected int calculateModulus(final String code, final boolean includesCheckDigit) throws CheckDigitException {
        int total = 0;
        for (int i = 0; i < code.length(); i++) {
            final int lth = code.length() + (includesCheckDigit ? 0 : 1);
            final int leftPos = i + 1;
            final int rightPos = lth - i;
            final int charValue = toInt(code.charAt(i), leftPos, rightPos);
            total += weightedValue(charValue, leftPos, rightPos);
        }
        if (total == 0) {
            throw new CheckDigitException(CheckDigitException.ZERO_SUM);
        }
        return total * MODULUS_10 % MODULUS_11;
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
            final String cd = calculate(code.substring(0, code.length() - 1));
            return code.endsWith(cd);
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Override to handle charValue X.
     * </p>
     */
    @Override
    protected String toCheckDigit(final int charValue) throws CheckDigitException {
        return charValue == X ? "0" : super.toCheckDigit(charValue);
    }

}
