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
 * Modulus 10 <b>CAS Registry Number</b> Check Digit calculation/validation.
 *
 * <p>
 * CAS Registry Numbers are a numeric codes.
 * Check digit calculation is based on <i>modulus 10</i> with digits being weighted
 * based on their position (from right to left).
 * <br>
 * The check digit is found by taking the last digit times 1, the preceding digit times 2,
 * the preceding digit times 3 etc., adding all these up and computing the sum modulo 10.
 * <br>
 * For example, the CAS number of water is <code>7732-18-5</code>:
 * the checksum 5 is calculated as (8×1 + 1×2 + 2×3 + 3×4 + 7×5 + 7×6) = 105; 105 mod 10 = 5.
 * <br>
 * Note that these <b>do not validate</b> the input for syntax.
 * Such validation is performed by the {@link org.apache.commons.validator.routines.CASNumberValidator}
 * </p>
 *
 * @since 1.9.0
 */
public final class CASNumberCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -5387334603220786657L;

    /** Singleton Check Digit instance */
    private static final CASNumberCheckDigit INSTANCE = new CASNumberCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the CAS Number validator.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    /**
     * The minimum length without dashes.
     * <p>
     * CAS number consists of 3 groups of numbers separated dashes (-).
     * First group has 2 to 7 digits, the second 2 digits, the third is the check digit.
     * Example: water is 7732-18-5
     * </p>
     */
    public static final int MIN_LEN = 5; // 10-00-4 LEN without SEP
    /** The maximum length without dashes. {@code 9999999-99-5} */
    public static final int MAX_LEN = 10;

    /** Weighting given to digits depending on their right position */
    private static final int[] POSITION_WEIGHT = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    /**
     * Constructs a modulus 10 Check Digit routine for CAS Numbers.
     */
    private CASNumberCheckDigit() { }

    /**
     * Calculates the <i>weighted</i> value of a character in the code at a specified position.
     * <p>
     * CAS numbers are weighted in the following manner:
     * </p>
     * <pre><code>
     *    right position: 1  2  3  4  5  6  7  8  9 10
     *            weight: 1  2  3  4  5  6  7  8  9  0
     * </code></pre>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        final int weight = POSITION_WEIGHT[(rightPos - 1) % MODULUS_10];
        return charValue * weight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException("Code is missing");
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
        if (code.length() < MIN_LEN || code.length() > MAX_LEN) {
            return false;
        }
        try {
            final int modulusResult = INSTANCE.calculateModulus(code, true);
            return modulusResult == Character.getNumericValue(code.charAt(code.length() - 1));
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
