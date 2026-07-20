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

/**
 * Modulus 10 <strong>CUSIP</strong> (North American Securities) Check Digit calculation/validation.
 *
 * <p>
 * CUSIP Numbers are 9 character alphanumeric codes used to identify North American Securities.
 * </p>
 *
 * <p>
 * Check digit calculation uses the <em>Modulus 10 Double Add Double</em> technique with every second digit being weighted by 2. Alphabetic characters are
 * converted to numbers by their position in the alphabet starting with A being 10. Weighted numbers greater than ten are treated as two separate numbers.
 * </p>
 *
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/CUSIP">Wikipedia - CUSIP</a> for more details.
 * </p>
 *
 * @since 1.4
 */
public final class CUSIPCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = 666941918490152456L;

    /**
     * Singleton CUSIP Check Digit instance.
     */
    public static final CheckDigit CUSIP_CHECK_DIGIT = new CUSIPCheckDigit();

    /** Weighting given to digits depending on their right position */
    private static final int[] POSITION_WEIGHT = { 2, 1 };

    /** A CUSIP is exactly nine characters, the last being the check digit. */
    private static final int CUSIP_LEN = 9;

    /**
     * Constructs a CUSIP Identifier Check Digit routine.
     */
    public CUSIPCheckDigit() {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The weight is taken from {@code rightPos}, which does not change when a character is prepended, so
     * {@code ModulusCheckDigit} would accept an over-length code whose leading character lands on a no-op weight (for
     * example {@code 0037833100}). The nine-character length is checked here before the check digit test.
     * </p>
     */
    @Override
    public boolean isValid(final String code) {
        if (code != null && code.length() != CUSIP_LEN) {
            return false;
        }
        return super.isValid(code);
    }

    /**
     * Convert a character at a specified position to an integer value.
     *
     * @param character The character to convert.
     * @param leftPos   The position of the character in the code, counting from left to right.
     * @param rightPos  The position of the character in the code, counting from right to left.
     * @return The integer value of the character.
     * @throws CheckDigitException if the character is not alphanumeric.
     */
    @Override
    protected int toInt(final char character, final int leftPos, final int rightPos) throws CheckDigitException {
        final int charValue = Character.getNumericValue(character);
        // the final character is only allowed to reach 9
        final int charValueMax = rightPos == 1 ? 9 : 35; // CHECKSTYLE IGNORE MagicNumber
        if (charValue > charValueMax || !isAsciiAlphaNum(character)) {
            throw new CheckDigitException("Invalid Character[%d,%d] = '%d' out of range 0 to %d", leftPos, rightPos, charValue, charValueMax);
        }
        return charValue;
    }

    /**
     * Calculates the <em>weighted</em> value of a character in the code at a specified position.
     *
     * <p>
     * For CUSIP (from right to left) <strong>odd</strong> digits are weighted with a factor of <strong>one</strong> and <strong>even</strong> digits with a
     * factor of <strong>two</strong>. Weighted values &gt; 9, have 9 subtracted
     * </p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos   The position of the character in the code, counting from left to right.
     * @param rightPos  The position of the character in the code, counting from right to left.
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        final int weight = POSITION_WEIGHT[rightPos % 2];
        final int weightedValue = charValue * weight;
        return sumDigits(weightedValue);
    }
}
