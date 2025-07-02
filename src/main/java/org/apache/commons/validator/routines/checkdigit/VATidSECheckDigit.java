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

import org.apache.commons.validator.GenericValidator;

/**
 * Swedish VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * Registreringsnummer för mervärdesskatt (Momsnummer MomsNr.)
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
// LuhnCheckDigit ist final, cannot be subclassed
public final class VATidSECheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = 4544090452894549769L;

    /** Singleton Check Digit instance */
    private static final VATidSECheckDigit INSTANCE = new VATidSECheckDigit();
    private static final CheckDigit LUHN_CD_INSTANCE = LuhnCheckDigit.LUHN_CHECK_DIGIT;

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }
    static final int LEN = 10; // without suffix "01"

    /** Weighting given to digits depending on their right position */
    private static final int[] POSITION_WEIGHT = {2, 1};

    /**
     * Constructs a Check Digit routine.
     */
    private VATidSECheckDigit() {
        super(MODULUS_10);
    }

    /**
     * <p>Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.</p>
     *
     * <p>For Luhn (from right to left) <b>odd</b> digits are weighted
     * with a factor of <b>one</b> and <b>even</b> digits with a factor
     * of <b>two</b>. Weighted values &gt; 9, have 9 subtracted</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The position of the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        final int weight = POSITION_WEIGHT[rightPos % POSITION_WEIGHT.length];
        final int weightedValue = charValue * weight;
        return weightedValue > 9 ? weightedValue - 9 : weightedValue; // CHECKSTYLE IGNORE MagicNumber
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
            if (code.length() != LEN + 2) {
                return false;
            }
            if (!code.endsWith("01")) {
                return false;
            }
            code = code.substring(0, LEN);
        }
        return LUHN_CD_INSTANCE.isValid(code);
    }

}
