/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.validator.routines.checkdigit;

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.CodeValidator;

/**
 * Modulus 10 <strong>CAS Registry Number</strong> (or <strong>Chemical Abstracts Service</strong> (CAS RN)) Check Digit
 * calculation/validation.
 *
 * <p>
 * CAS Numbers are unique identification numbers used
 * to identify chemical substance described in the open scientific literature.
 * </p>
 *
 * <p>
 * Check digit calculation is based on <em>modulus 10</em> with digits being weighted
 * based on their position (from right to left).
 * </p>
 *
 * <p>
 * The check digit is found by taking the last digit times 1, the preceding digit times 2,
 * the preceding digit times 3 etc., adding all these up and computing the sum modulo 10.
 * For example, the CAS number of water is {@code 7732-18-5}:
 * the checksum 5 is calculated as (8×1 + 1×2 + 2×3 + 3×4 + 7×5 + 7×6) = 105; 105 mod 10 = 5.
 * </p>
 *
 * <p>
 * For further information see
 *  <a href="https://en.wikipedia.org/wiki/CAS_Registry_Number">Wikipedia - CAS Registry Number</a>.
 * </p>
 *
 * @since 1.9.0
 */
public final class CASNumberCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -5387334603220786657L;

    /** Singleton Check Digit instance */
    private static final CASNumberCheckDigit INSTANCE = new CASNumberCheckDigit();

    /**
     * CAS number consists of 3 groups of numbers separated dashes (-).
     * First group has 2 to 7 digits.
     * Example: water is 7732-18-5
     */
    private static final String GROUP1 = "(\\d{2,7})";

    private static final String DASH = "(?:\\-)";
    static final String CAS_REGEX = "^(?:" + GROUP1 + DASH + "(\\d{2})" + DASH + "(\\d))$";
    private static final int CAS_MIN_LEN = 4; // 9-99-9 LEN without SEP

    /** maximum capacity of 1,000,000,000 == 9999999-99-9*/
    private static final int CAS_MAX_LEN = 10;
    static final CodeValidator REGEX_VALIDATOR = new CodeValidator(CAS_REGEX, CAS_MIN_LEN, CAS_MAX_LEN, null);
    /** Weighting given to digits depending on their right position */
    private static final int[] POSITION_WEIGHT = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the CAS Number validator.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    /**
     * Constructs a modulus 10 Check Digit routine for CAS Numbers.
     */
    private CASNumberCheckDigit() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException("Code is missing");
        }
        final int modulusResult = INSTANCE.calculateModulus(code, false);
        return toCheckDigit(modulusResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        final Object cde = REGEX_VALIDATOR.validate(code);
        if (!(cde instanceof String)) {
            return false;
        }
        try {
            final int modulusResult = INSTANCE.calculateModulus((String) cde, true);
            return modulusResult == Character.getNumericValue(code.charAt(code.length() - 1));
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

    /**
     * Calculates the <em>weighted</em> value of a character in the code at a specified position.
     * <p>
     * CAS numbers are weighted in the following manner:
     * </p>
     * <pre>{@code
     *    right position: 1  2  3  4  5  6  7  8  9 10
     *            weight: 1  2  3  4  5  6  7  8  9  0
     * }</pre>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The position of the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        final int weight = POSITION_WEIGHT[(rightPos - 1) % MODULUS_10];
        return charValue * weight;
    }

}
