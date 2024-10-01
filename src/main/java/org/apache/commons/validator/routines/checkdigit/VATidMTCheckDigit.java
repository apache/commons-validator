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
 * Malta VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * value added tax identification number {@code 123456pp}.
 * <br>
 * The check digits are calculated as MOD 37
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia - VAT IN</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidMTCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = 2611549149021044487L;

    /** Singleton Check Digit instance */
    private static final VATidMTCheckDigit INSTANCE = new VATidMTCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    static final int CHECKDIGIT_LEN = 2;
    static final int MIN_CODE_LEN = 4;
    static final int MODULUS_37 = 37;

    /**
     * Constructs a Check Digit routine.
     */
    private VATidMTCheckDigit() {
        super(MODULUS_37);
    }

    /** Weighting given to digits depending on their left position */
    private static final int[] POSITION_WEIGHT = { 3, 4, 6, 7, 8, 9 };

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
     *
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
        }
        final int cdValue = MODULUS_37 - calculateModulus(code, false);
        return Modulus97CheckDigit.toCheckDigit(cdValue == 0 ? MODULUS_37 : cdValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        if (code.length() < MIN_CODE_LEN) {
            return false;
        }

        String check = code.substring(code.length() - CHECKDIGIT_LEN);
        Integer icheck = GenericTypeValidator.formatInt(check);
        if (icheck == null) {
            return false;
        }
        try {
            int modulusResult = calculateModulus(code, true);
            return icheck.intValue() == MODULUS_37 - modulusResult;
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
