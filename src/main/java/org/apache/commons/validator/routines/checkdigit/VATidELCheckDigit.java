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
 * Greek VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * Arithmos Forologikou Mitroou FPA
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidELCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -1693031762197317534L;

    /** Singleton Check Digit instance */
    private static final VATidELCheckDigit INSTANCE = new VATidELCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    static final int LEN = 9; // with Check Digit

    /** Weighting given to digits depending on their left position */
//    private static final int[] POSITION_WEIGHT = { 3, 7, 1 };

    /**
     * Constructs a modulus 10 Check Digit routine.
Check digit = A2 modulo 10
A2 = A1 modulo 11
A1 = 256*C1 + 128*C2 + 64*C3 + 32*C4 + 16*C5 + 8*C6 + 4*C7 + 2*C8
----------
A1 = 256*0 + 128*4 + 64*0 + 32*1 + 16*2 + 8*7 + 4*7 + 2*9= 678
A2 = 678 modulo 11 = 7
C9 = A2 modulo 10 = 7
--------------
A1 = 2*A1 + Zif
0
i	Zif	A1		A2
0	0	0
1	4	0+4
2	0	8+0
3	1	16+1
4	2	34+2
5	7	72+7
6	7	158+7
7	9	330+9
8	p	678		7


     */
    private VATidELCheckDigit() {
        super(MODULUS_11);
    }

    /**
     * Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.
     *
     * <p>For VATID digits are weighted by their position from right to left.</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        if (leftPos >= LEN) return 0;
        final int weight = (int) Math.pow(2, rightPos - 1);
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
        if (GenericTypeValidator.formatLong(code) == 0) {
            throw new CheckDigitException(CheckDigitException.ZREO_SUM);
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
        if (code.length() < LEN) {
        	return false;
        }
        try {
            if (GenericTypeValidator.formatLong(code.substring(0, code.length() - 1)) == 0) {
                throw new CheckDigitException(CheckDigitException.ZREO_SUM);
            }
            final int modulusResult = INSTANCE.calculateModulus(code, true);
            return modulusResult == Character.getNumericValue(code.charAt(code.length() - 1));
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
