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
 * Irish VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia - VAT IN</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidIECheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = 4007034045902340075L;

    /** Singleton Check Digit instance */
    private static final VATidIECheckDigit INSTANCE = new VATidIECheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    static final int LEN = 7; // without Check Digit
    static final int MODULUS_23 = 23;
    // the 8th char is the non numeric check character given by the pos
    private static final String CHECK_CHARACTER = "WABCDEFGHIJKLMNOPQRSTUV";
    // the optional 9th character is non numeric
    static final int POS9 = 9;
    private static final String LETTER9TONUMBER = "ABCDEFGHI";

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
     * {@inheritDoc}
     * <p>
     * Override to get the value of the non numeric 9th character
     * </p>
     */
    @Override
    protected int toInt(final char character, final int leftPos, final int rightPos) throws CheckDigitException {
        if (Character.isDigit(character)) {
            return Character.getNumericValue(character);
        }
        if (leftPos == POS9) {
            return 1 + LETTER9TONUMBER.indexOf(character);
        }
        throw new CheckDigitException("Invalid Character[" + leftPos + "] = '" + character + "'");
    }


    /**
     * Constructs a Check Digit routine.
     */
    private VATidIECheckDigit() {
        super(MODULUS_23);
    }

    /** Weighting given to digits depending on their left position */
    private static final int[] POSITION_WEIGHT = { 8, 7, 6, 5, 4, 3, 2 };
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
        if (leftPos - 1 < POSITION_WEIGHT.length) {
            final int weight = POSITION_WEIGHT[(leftPos - 1)];
            return charValue * weight;
        }
        return leftPos == POS9 ? leftPos * charValue : 0;
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
        int r = super.calculateModulus(code, true);
        return toCheckDigit(r);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        if (code.length() <= LEN) {
            return false;
        }
        String code0 = code.substring(0, LEN) + 0 + code.substring(LEN + 1);
        try {
            if (code.length() >= LEN && GenericTypeValidator.formatLong(code.substring(0, LEN)) == 0) {
                throw new CheckDigitException(CheckDigitException.ZREO_SUM);
            }
            final int modulusResult = INSTANCE.calculateModulus(code0, true);
            return toCheckDigit(modulusResult).charAt(0) == code.charAt(LEN);
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
