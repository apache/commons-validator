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
 * Latvian VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * pievienotāsvērtības nodokļa reģistrācijas numurs (PVN)
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidLVCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -4171562329195981385L;

    /** Singleton Check Digit instance */
    private static final VATidLVCheckDigit INSTANCE = new VATidLVCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

/* beginnend mit n>3: Legal persons
A1 = 9*C1 + 1*C2 + 4*C3 + 8*C4 + 3*C5 + 10*C6 + 2*C7 + 5*C8 + 7*C9 + 6*C10
R = 3 - (A1 modulo 11)
If R < -1, then C11 = R + 11
If R > -1, then C11 = R
If R = -1, then VAT number is invalid
---
A1 = 9*4 + 1*0 + 4*0 + 8*0 + 3*3 + 10*0 + 2*0 + 5*9 + 7*4 + 6*9 = 172
R = 3 - (172 modulo 11) = 3 - 7 = -4
C11 = -4 + 11 = 7

 */
    private static final int LEN = 11;

    /** Weighting given to digits depending on their left position */
    private static final int[] POSITION_WEIGHT = { 9, 1, 4, 8, 3, 10, 2, 5, 7, 6 };

    /**
     * Constructs a modulus 11 Check Digit routine.
     */
    private VATidLVCheckDigit() {
        super(MODULUS_11);
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
        if (leftPos - 1 >= POSITION_WEIGHT.length) return 0;
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
        if (code.length() >= LEN && GenericTypeValidator.formatLong(code) == 0) {
            throw new CheckDigitException(CheckDigitException.ZREO_SUM);
        }
        final int modulusResult = calculateModulus(code, false);
        final int charValue = 3 - modulusResult;
        if (charValue == -1) {
            throw new CheckDigitException("Invalid code, R==-1");
        }
        return toCheckDigit(charValue > -1 ? charValue : charValue + MODULUS_11);
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
            final int charValue = 3 - modulusResult;
            if (charValue == -1) {
                throw new CheckDigitException("Invalid code, R==-1");
            }
            final int cd = charValue > -1 ? charValue : charValue + MODULUS_11;
            return cd == Character.getNumericValue(code.charAt(code.length() - 1));
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
