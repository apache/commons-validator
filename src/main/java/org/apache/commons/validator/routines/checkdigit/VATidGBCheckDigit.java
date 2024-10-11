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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.GenericValidator;

/**
 * United Kingdom value added tax registration number
 * <p>
 * The VAT number can either be a 9-digit standard number,
 * a 12-digit standard number followed by a 3-digit branch identifier,
 * a 5-digit number for government departments (first two digits are GD) or
 * a 5-digit number for health authorities (first two digits are HA).
 * The 9-digit variants use a weighted checksum.
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia - VAT IN</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidGBCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -1297016213259512985L;
    private static final Log LOG = LogFactory.getLog(VATidGBCheckDigit.class);

    /** Singleton Check Digit instance */
    private static final VATidGBCheckDigit INSTANCE = new VATidGBCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    static final int LEN = 9; // with Check Digit
    static final int CHECKDIGIT_LEN = 2;

    private static final int MODULUS_97 = 97;
    private static final int MODULUS97_55 = 55; // the modifier for Modulus 9755 algorithm

    /**
     * Constructs a Check Digit routine.
     */
    private VATidGBCheckDigit() {
        super(MODULUS_97);
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
     * @param rightPos The position of the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        if (leftPos > LEN - CHECKDIGIT_LEN) return 0;
        final int weight = POSITION_WEIGHT[(leftPos - 1)];
        return charValue * weight;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to handle two digits as <em>Check Digit</em> and MOD 9755
     * </p>
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
        }
        if (code.length() < LEN - CHECKDIGIT_LEN) {
            throw new CheckDigitException("Invalid Code length=" + code.length());
        }

        int modulusResult = calculateModulus(code, false);
        if (LOG.isDebugEnabled()) {
            int mr55 = (MODULUS97_55 + modulusResult) % MODULUS_97;
            int newStyle = MODULUS_97 - mr55;
            LOG.debug(code + " modulusResult=" + modulusResult + " - old style cd = " + (MODULUS_97 - modulusResult)
                + " and MOD9755-style " + newStyle);
        }
        // There are more than one possible VATIN check digits for a given code,
        // one old style MOD 97 and one new style MOD 9755
        // thus, it isn't possible to compute the right one.
        // here I return old style MOD 97 check digit
        return Modulus97CheckDigit.toCheckDigit(modulusResult == 0 ? 0 : MODULUS_97 - modulusResult);
        // this retuens MOD 9755
        //return Modulus97CheckDigit.toCheckDigit(newStyle);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to handle two digits as <em>Check Digit</em>, ignore tree branch digits and MOD 9755
     * </p>
     */
    @Override
    public boolean isValid(final String ocode) {
        String code = ocode;
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        if (code.length() < LEN) {
            return false;
        }
        if (code.length() > LEN) {
            if (code.length() != LEN + 3) {  // CHECKSTYLE IGNORE MagicNumber
                return false;
            }
            // ignore tree branch digits
            code = code.substring(0, LEN);
        }

        try {
            // without the leading "0" the following would be valid 8888502+4
            Integer cd = GenericTypeValidator.formatInt("0" + code.substring(code.length() - CHECKDIGIT_LEN));
            if (cd == null) {
                throw new CheckDigitException("Invalid Code " + code);
            }
            if (cd >= MODULUS_97) {
                throw new CheckDigitException("Invalid Code " + code + " check digit >= " + MODULUS_97);
            }
            int modulusResult = calculateModulus(code.substring(0, LEN - CHECKDIGIT_LEN), false);
            if (0 == (modulusResult + cd) % MODULUS_97) {
                return true; // old style MOD 97
            }
            if (0 == (modulusResult + cd + MODULUS97_55) % MODULUS_97) {
                return true; // new style MOD 9755
            }
            return false;
        } catch (final CheckDigitException ex) {
            return false;
        }

    }

}
