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

import java.util.logging.Logger;

import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.GenericValidator;

/**
 * Romanian VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * cod de înregistrare în scopuri de TVA (TVA)
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia - VAT IN</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidROCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = 159727558301530535L;
    private static final Logger LOG = Logger.getLogger(VATidROCheckDigit.class.getName());

    /** Singleton Check Digit instance */
    private static final VATidROCheckDigit INSTANCE = new VATidROCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    /*
A1 = C1*7 + C2*5 + C3*3 + C4*2 + C5*1 + C6*7 + C7*5 + C8*3 + C9*2
A2 = A1 * 10
R1 = A2 modulo 11
if R1 = 10, then R = 0
Else R = R1
C10 = R

A1 = 0*7 + 0*5 + 1*3 + 1*2 + 1*1 + 9*7 + 8*5 + 6*3 + 9*2 = 145
     */
    static final int LEN = 9; // without Check Digit

    /** Weighting given to digits depending on their left position */
    private static final int[] POSITION_WEIGHT = { 7, 5, 3, 2, 1 };

    /**
     * Constructs a modulus 11 Check Digit routine.
     */
    private VATidROCheckDigit() {
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
        final int weight = POSITION_WEIGHT[(leftPos - 1) % POSITION_WEIGHT.length];
//        LOG.info("charValue="+charValue + ", leftPos="+leftPos + ", rightPos="+rightPos);
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
        String pcode = code;
        LOG.info(code + " # prefix nullen:" + (LEN - code.length()));
        if (code.length() < LEN) {
            pcode = "0000000000".substring(0, LEN - code.length()) + code;
            LOG.info(pcode + " mit prefix nullen");
        }
//        final int modulusResult = calculateModulus(code, false);
        int total = 0;
        for (int i = 0; i < pcode.length(); i++) {
//            final int lth = pcode.length() + 1;
            final int leftPos = i + 1;
//            final int rightPos = lth - i;
            final int charValue = toInt(pcode.charAt(i), leftPos, -1);
            total += weightedValue(charValue, leftPos, -1);
        }
        if (total == 0) {
            throw new CheckDigitException("Invalid code, sum is zero");
        }
        LOG.info(" total=" + total);
//        return total % modulus;
        //A2 = A1 * 10
        //R1 = A2 modulo 11
        final int charValue = total * MODULUS_10 % MODULUS_11;
//        final int charValue = (modulus - modulusResult) % modulus;
        return toCheckDigit(charValue == MODULUS_10 ? 0 : charValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
//        if (code.length() != LEN+1) {
//            return false;
//        }
        try {
            String cd = calculate(code.substring(0, code.length() - 1));
            return code.endsWith(cd);
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
