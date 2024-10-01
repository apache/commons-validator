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
 * Netherlands VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * BTW-identificatienummer.
 * Note the last three characters (B + 2-digit company index) are not part of the
 * check digit calculation
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia - VAT IN</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidNLCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = 2429529612265735976L;

    /** Singleton Check Digit instance */
    private static final VATidNLCheckDigit INSTANCE = new VATidNLCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    private static final int LEN = 9; // without suffix "B99"

    /**
     * Constructs a Check Digit routine.
     */
    private VATidNLCheckDigit() {
        super(MODULUS_11);
    }

    /** Weighting given to digits depending on their left position */
    /*
 Ziffern werden von rechts nach links,
 beginnend mit der vorletzten Ziffer (also vor der Stelle der Prüfziffer),
 mit ihrer Position in der Ziffernfolge gewichtet, beginnend mit 2
     */
    private static final int[] POSITION_WEIGHT = { 9, 8, 7, 6, 5, 4, 3, 2 };
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
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
        }
        if (GenericTypeValidator.formatLong(code) == 0) {
            throw new CheckDigitException(CheckDigitException.ZREO_SUM);
        }

        return toCheckDigit(INSTANCE.calculateModulus(code, false) % MODULUS_10);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String ocode) {
        String code = ocode;
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        if (code.length() > LEN) {
            if (code.length() != LEN + 3) {  // CHECKSTYLE IGNORE MagicNumber
                return false;
            }
            if (code.charAt(LEN) != 'B' || code.endsWith("B00")) {
                return false;
            }
            code = code.substring(0, LEN);
        }

        try {
            final int modulusResult = INSTANCE.calculateModulus(code, true);
            final int cdd = modulusResult % MODULUS_10;
            return cdd == Character.getNumericValue(code.charAt(code.length() - 1));
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
