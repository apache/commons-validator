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

import java.io.Serializable;

import org.apache.commons.validator.GenericValidator;

/**
 * <strong>Verhoeff</strong> (Dihedral) Check Digit calculation/validation.
 * <p>
 * Check digit calculation for numeric codes using a
 * <a href="https://en.wikipedia.org/wiki/Dihedral_group">Dihedral Group</a>
 * of order 10.
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/Verhoeff_algorithm">Wikipedia
 *  - Verhoeff algorithm</a> for more details.
 *
 * @since 1.4
 */
public final class VerhoeffCheckDigit extends AbstractCheckDigit implements Serializable {

    private static final long serialVersionUID = 4138993995483695178L;

    /** Singleton Verhoeff Check Digit instance */
    public static final CheckDigit VERHOEFF_CHECK_DIGIT = new VerhoeffCheckDigit();

    /** D - multiplication table */
    private static final int[][] D_TABLE = {
        {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
        {1, 2, 3, 4, 0, 6, 7, 8, 9, 5},
        {2, 3, 4, 0, 1, 7, 8, 9, 5, 6},
        {3, 4, 0, 1, 2, 8, 9, 5, 6, 7},
        {4, 0, 1, 2, 3, 9, 5, 6, 7, 8},
        {5, 9, 8, 7, 6, 0, 4, 3, 2, 1},
        {6, 5, 9, 8, 7, 1, 0, 4, 3, 2},
        {7, 6, 5, 9, 8, 2, 1, 0, 4, 3},
        {8, 7, 6, 5, 9, 3, 2, 1, 0, 4},
        {9, 8, 7, 6, 5, 4, 3, 2, 1, 0}};

    /** P - permutation table */
    private static final int[][] P_TABLE = {
        {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
        {1, 5, 7, 6, 2, 8, 3, 0, 9, 4},
        {5, 8, 0, 3, 7, 9, 6, 1, 4, 2},
        {8, 9, 1, 6, 0, 4, 3, 5, 2, 7},
        {9, 4, 5, 3, 1, 2, 6, 8, 7, 0},
        {4, 2, 8, 6, 5, 7, 3, 9, 0, 1},
        {2, 7, 9, 3, 8, 0, 6, 4, 1, 5},
        {7, 0, 4, 6, 9, 1, 3, 2, 5, 8}};

    /** Inverse table */
    private static final int[] INV_TABLE = {0, 4, 3, 2, 1, 5, 6, 7, 8, 9};

    /**
     * Calculate a Verhoeff <em>Check Digit</em> for a code.
     *
     * @param code The code to calculate the Check Digit for
     * @return The calculated Check Digit
     * @throws CheckDigitException if an error occurs calculating
     * the check digit for the specified code
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException("Code is missing");
        }
        final int checksum = calculateChecksum(code, false);
        return Integer.toString(INV_TABLE[checksum]);
    }

    /**
     * Calculate the checksum.
     *
     * @param code The code to calculate the checksum for.
     * @param includesCheckDigit Whether the code includes the Check Digit or not.
     * @return The checksum value
     * @throws CheckDigitException if the code contains an invalid character (i.e. not numeric)
     */
    private int calculateChecksum(final String code, final boolean includesCheckDigit) throws CheckDigitException {
        int checksum = 0;
        for (int i = 0; i < code.length(); i++) {
            final int idx = code.length() - (i + 1);
            final int num = Character.getNumericValue(code.charAt(idx));
            if (num < 0 || num > 9) { // CHECKSTYLE IGNORE MagicNumber
                throw new CheckDigitException("Invalid Character[" +
                        i + "] = '" + (int) code.charAt(idx) + "'");
            }
            final int pos = includesCheckDigit ? i : i + 1;
            checksum = D_TABLE[checksum][P_TABLE[pos % 8][num]]; // CHECKSTYLE IGNORE MagicNumber
        }
        return checksum;
    }

    /**
     * Validate the Verhoeff <em>Check Digit</em> for a code.
     *
     * @param code The code to validate
     * @return {@code true} if the check digit is valid,
     * otherwise {@code false}
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        try {
            return calculateChecksum(code, true) == 0;
        } catch (final CheckDigitException e) {
            return false;
        }
    }

}
