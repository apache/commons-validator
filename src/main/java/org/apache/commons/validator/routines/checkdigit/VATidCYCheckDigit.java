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
 * Cypriot VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * Arithmos Egrafis FPA (ΦΠΑ)
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidCYCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -844683638838062022L;

    /** Singleton Check Digit instance */
    private static final VATidCYCheckDigit INSTANCE = new VATidCYCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    static final int LEN = 9; // with Check Digit
    static final int MODULUS_26 = 26;

    /** Weighting given to digits depending on their left position */
    private static final int[] POSITION_WEIGHT = { 1, 0, 5, 7, 9, 13, 15, 17, 19, 21 };
    private static final String CHECK_CHARACTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String INVALID_START_WITH = "12";
    private static final String INVALID_START_MSG = "Invalid code, not allowed to start with '12' :";

    /**
     * Constructs a modulus 11 Check Digit routine.
     */
    private VATidCYCheckDigit() {
        super(MODULUS_26);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Override to get the non numeric check character
     * </p>
     */
    @Override
    protected String toCheckDigit(final int charValue) throws CheckDigitException {
        if (charValue >= 0 && charValue <= CHECK_CHARACTER.length() - 1) {
            return "" + CHECK_CHARACTER.charAt(charValue);
        }
        throw new CheckDigitException("Invalid Check Digit Value =" + charValue);
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
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        if (leftPos % 2 == 0) return charValue;
        return POSITION_WEIGHT[charValue];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
        }
        if (code.startsWith(INVALID_START_WITH)) {
            throw new CheckDigitException(INVALID_START_MSG + code);
        }
        if (code.length() >= LEN && GenericTypeValidator.formatLong(code.substring(0, LEN)) == 0) {
            throw new CheckDigitException(CheckDigitException.ZREO_SUM);
        }
        return toCheckDigit(calculateModulus(code, false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        if (code.length() < LEN) {
            return false;
        }
        try {
            if (code.startsWith(INVALID_START_WITH)) {
                throw new CheckDigitException(INVALID_START_MSG + code);
            }
            final int modulusResult = calculateModulus(code.substring(0, LEN - 1), false);
            return toCheckDigit(modulusResult).charAt(0) == code.charAt(LEN - 1);
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
