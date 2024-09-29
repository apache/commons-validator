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
//    private static final Logger LOG = Logger.getLogger(VATidLTCheckDigit.class.getName());

    /** Singleton Check Digit instance */
    private static final VATidLTCheckDigit INSTANCE = new VATidLTCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    private static final int LEN = 12; // oder 9

    /**
     * Constructs a modulus 11 Check Digit routine.
     */
    private VATidLTCheckDigit() {
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

A1 = 1*C1 + 2*C2 + 3*C3 + 4*C4 + 5*C5 + 6*C6 + 7*C7 + 8*C8
R1 = A1 modulo 11
If R1 <> 10, then C9 = R1
Else
A2 = 3*C1 + 4*C2 + 5*C3 + 6*C4 + 7*C5 + 8*C6 + 9*C7 + 1*C8
R2 = A2 modulo 11
If R2 = 10, then C9 = 0
Else C9 = R2

     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        if (leftPos - 1 >= LEN) return 0;
        final int weight = leftPos > 7 ? leftPos - 7 : leftPos + 2;  // CHECKSTYLE IGNORE MagicNumber
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
        final int modulusResult = calculateModulus1(code, false);
//        LOG.info(code + " modulusResult="+modulusResult);
        // If R1 <> 10, then C9 = R1
        if (modulusResult == 10) { // CHECKSTYLE IGNORE MagicNumber
            final int r2 = calculateModulus(code, false);
            return toCheckDigit(r2 == 10 ? 0 : r2); // CHECKSTYLE IGNORE MagicNumber
        }
        return toCheckDigit(modulusResult);
    }
    private int calculateModulus1(final String code, final boolean includesCheckDigit) throws CheckDigitException {
        int total = 0;
        for (int i = 0; i < code.length() - (includesCheckDigit ? 1 : 0); i++) {
            final int leftPos = i + 1;
            final int charValue = toInt(code.charAt(i), leftPos, -1);
            total += charValue * (leftPos > 9 ? leftPos - 9 : leftPos); // CHECKSTYLE IGNORE MagicNumber
        }
        if (total == 0) {
            throw new CheckDigitException(CheckDigitException.ZREO_SUM);
        }
        return total % MODULUS_11;
    }

    /**
     * {@inheritDoc}
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
                // TODO stopp bei cd
//                final int r2 = calculateModulus(code, true);
                final int r2 = calculateModulus(code.substring(0, code.length() - 1), false);
                cd = (r2 == 10 ? 0 : r2); // CHECKSTYLE IGNORE MagicNumber
//                LOG.info(code + " cd="+cd+ " r2="+r2);
            }
            return cd == Character.getNumericValue(code.charAt(code.length() - 1));
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
