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
 * Lithuanian VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * Pridetines vertes mokescio moketojo kodas (PVM MK)
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidLTCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -5818846157214697674L;

    /** Singleton Check Digit instance */
    private static final VATidLTCheckDigit INSTANCE = new VATidLTCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    private static final int POS9 = 9;
    private static final int LEN = 12;

    /**
     * Constructs a modulus 11 Check Digit routine.
     */
    private VATidLTCheckDigit() {
        super(MODULUS_11);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to provide two {@code}calculateModulus methods,
     * the second is used when the first results to 10
     * </p>
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
        }
        if (code.length() >= LEN && GenericTypeValidator.formatLong(code) == 0) {
            throw new CheckDigitException(CheckDigitException.ZERO_SUM);
        }
        final int modulusResult = calculateModulus1(code, false);
        if (modulusResult == 10) { // CHECKSTYLE IGNORE MagicNumber
            final int r2 = calculateModulus(code, false);
            return toCheckDigit(r2 == 10 ? 0 : r2); // CHECKSTYLE IGNORE MagicNumber
        }
        return toCheckDigit(modulusResult);
    }

    /*
     * weighted values are 1 2 3 4 5 6 7 8 9 1 2
     * leftPos > LEN9 ? leftPos - LEN9 : leftPos
     *
     * For the second calculateModulus method the weighted values are increase by 2,
     * weighted values are 3 4 5 6 7 8 9 1 2 3 4 (see
     */
    private int calculateModulus1(final String code, final boolean includesCheckDigit) throws CheckDigitException {
        int total = 0;
        for (int i = 0; i < code.length() - (includesCheckDigit ? 1 : 0); i++) {
            final int leftPos = i + 1;
            final int charValue = toInt(code.charAt(i), leftPos, -1);
            total += charValue * (leftPos > POS9 ? leftPos - POS9 : leftPos);
        }
        if (total == 0) {
            throw new CheckDigitException(CheckDigitException.ZERO_SUM);
        }
        return total % MODULUS_11;
    }

    /**
     * Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.
     *
     * <p>For the second calculateModulus method digits are weighted by their position+2 from left to right.</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        if (leftPos - 1 >= LEN) return 0;
        final int weight = leftPos > 7 ? leftPos - 7 : leftPos + 2;  // CHECKSTYLE IGNORE MagicNumber
        return charValue * weight;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to provide two {@code}calculateModulus methods,
     * the second is used when the first results to 10
     * </p>
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        if (code.length() > LEN) {
            return false;
        }
        try {
            final int modulusResult = INSTANCE.calculateModulus1(code, true);
            int cd = modulusResult;
            if (modulusResult == 10) { // CHECKSTYLE IGNORE MagicNumber
                final int r2 = calculateModulus(code.substring(0, code.length() - 1), false);
                cd = (r2 == 10 ? 0 : r2); // CHECKSTYLE IGNORE MagicNumber
            }
            return cd == Character.getNumericValue(code.charAt(code.length() - 1));
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
