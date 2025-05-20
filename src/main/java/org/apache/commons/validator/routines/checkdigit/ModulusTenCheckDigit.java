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

import java.util.Arrays;

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.CodeValidator;

/**
 * General Modulus 10 Check Digit calculation/validation.
 *
 * <h2>How it Works</h2>
 * <p>
 * This implementation calculates/validates the check digit in the following
 * way:
 * <ul>
 * <li>Converting each character to an integer value using
 * {@code Character.getNumericValue(char)} - negative integer values from
 * that method are invalid.</li>
 * <li>Calculating a <em>weighted value</em> by multiplying the character's
 * integer value by a <em>weighting factor</em>. The <em>weighting factor</em> is
 * selected from the configured {@code positionWeight} array based on its
 * position. The {@code postitionWeight} values are used either
 * left-to-right (when {@code useRightPos=false}) or right-to-left (when
 * {@code useRightPos=true}).</li>
 * <li>If {@code sumWeightedDigits=true}, the <em>weighted value</em> is
 * re-calculated by summing its digits.</li>
 * <li>The <em>weighted values</em> of each character are totalled.</li>
 * <li>The total modulo 10 will be zero for a code with a valid Check Digit.</li>
 * </ul>
 * <h2>Limitations</h2>
 * <p>
 * This implementation has the following limitations:
 * <ul>
 * <li>It assumes the last character in the code is the Check Digit and
 * validates that it is a numeric character.</li>
 * <li>The only limitation on valid characters are those that
 * {@code Character.getNumericValue(char)} returns a positive value. If,
 * for example, the code should only contain numbers, this implementation does
 * not check that.</li>
 * <li>There are no checks on code length.</li>
 * </ul>
 * <p>
 * <strong>Note:</strong> This implementation can be combined with the
 * {@link CodeValidator} in order to ensure the length and characters are valid.
 *
 * <h2>Example Usage</h2>
 * <p>
 * This implementation was added after a number of Modulus 10 routines and these
 * are shown re-implemented using this routine below:
 *
 * <p>
 * <strong>ABA Number</strong> Check Digit Routine (equivalent of
 * {@link ABANumberCheckDigit}). Weighting factors are {@code [1, 7, 3]}
 * applied from right to left.
 *
 * <pre>
 * CheckDigit routine = new ModulusTenCheckDigit(new int[] { 1, 7, 3 }, true);
 * </pre>
 *
 * <p>
 * <strong>CUSIP</strong> Check Digit Routine (equivalent of {@link CUSIPCheckDigit}).
 * Weighting factors are {@code [1, 2]} applied from right to left and the
 * digits of the <em>weighted value</em> are summed.
 *
 * <pre>
 * CheckDigit routine = new ModulusTenCheckDigit(new int[] { 1, 2 }, true, true);
 * </pre>
 *
 * <p>
 * <strong>EAN-13 / UPC</strong> Check Digit Routine (equivalent of
 * {@link EAN13CheckDigit}). Weighting factors are {@code [1, 3]} applied
 * from right to left.
 *
 * <pre>
 * CheckDigit routine = new ModulusTenCheckDigit(new int[] { 1, 3 }, true);
 * </pre>
 *
 * <p>
 * <strong>Luhn</strong> Check Digit Routine (equivalent of {@link LuhnCheckDigit}).
 * Weighting factors are {@code [1, 2]} applied from right to left and the
 * digits of the <em>weighted value</em> are summed.
 *
 * <pre>
 * CheckDigit routine = new ModulusTenCheckDigit(new int[] { 1, 2 }, true, true);
 * </pre>
 *
 * <p>
 * <strong>SEDOL</strong> Check Digit Routine (equivalent of {@link SedolCheckDigit}).
 * Weighting factors are {@code [1, 3, 1, 7, 3, 9, 1]} applied from left to
 * right.
 *
 * <pre>
 * CheckDigit routine = new ModulusTenCheckDigit(new int[] { 1, 3, 1, 7, 3, 9, 1 });
 * </pre>
 *
 * @since 1.6
 */
public final class ModulusTenCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -3752929983453368497L;

    /**
     * The weighted values to apply based on the character position
     */
    private final int[] positionWeight;

    /**
     * {@code true} if use positionWeights from right to left
     */
    private final boolean useRightPos;

    /**
     * {@code true} if sum the digits of the weighted value
     */
    private final boolean sumWeightedDigits;

    /**
     * Constructs a modulus 10 Check Digit routine with the specified weighting
     * from left to right.
     *
     * @param positionWeight the weighted values to apply based on the
     *            character position
     */
    public ModulusTenCheckDigit(final int[] positionWeight) {
        this(positionWeight, false, false);
    }

    /**
     * Constructs a modulus 10 Check Digit routine with the specified weighting,
     * indicating whether its from the left or right.
     *
     * @param positionWeight the weighted values to apply based on the
     *            character position
     * @param useRightPos {@code true} if use positionWeights from right to
     *            left
     */
    public ModulusTenCheckDigit(final int[] positionWeight, final boolean useRightPos) {
        this(positionWeight, useRightPos, false);
    }

    /**
     * Constructs a modulus 10 Check Digit routine with the specified weighting,
     * indicating whether its from the left or right and whether the weighted
     * digits should be summed.
     *
     * @param positionWeight the weighted values to apply based on the
     *            character position
     * @param useRightPos {@code true} if use positionWeights from right to
     *            left
     * @param sumWeightedDigits {@code true} if sum the digits of the
     *            weighted value
     */
    public ModulusTenCheckDigit(final int[] positionWeight, final boolean useRightPos, final boolean sumWeightedDigits) {
        this.positionWeight = Arrays.copyOf(positionWeight, positionWeight.length);
        this.useRightPos = useRightPos;
        this.sumWeightedDigits = sumWeightedDigits;
    }

    /**
     * Validate a modulus check digit for a code.
     * <p>
     * Note: assumes last digit is the check digit
     *
     * @param code The code to validate
     * @return {@code true} if the check digit is valid, otherwise
     *         {@code false}
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code) || !Character.isDigit(code.charAt(code.length() - 1))) {
            return false;
        }
        return super.isValid(code);
    }

    /**
     * Convert a character at a specified position to an integer value.
     * <p>
     * <strong>Note:</strong> this implementation only handlers values that
     * Character.getNumericValue(char) returns a non-negative number.
     *
     * @param character The character to convert
     * @param leftPos The position of the character in the code, counting from
     *            left to right (for identifying the position in the string)
     * @param rightPos The position of the character in the code, counting from
     *            right to left (not used here)
     * @return The integer value of the character
     * @throws CheckDigitException if Character.getNumericValue(char) returns a
     *             negative number
     */
    @Override
    protected int toInt(final char character, final int leftPos, final int rightPos) throws CheckDigitException {
        final int num = Character.getNumericValue(character);
        if (num < 0) {
            throw new CheckDigitException("Invalid Character[" + leftPos + "] = '" + character + "'");
        }
        return num;
    }

    /**
     * Return a string representation of this implementation.
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[positionWeight=" + Arrays.toString(positionWeight) + ", useRightPos="
                + useRightPos + ", sumWeightedDigits=" + sumWeightedDigits + "]";
    }

    /**
     * Calculates the <em>weighted</em> value of a character in the code at a
     * specified position.
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from
     *            left to right
     * @param rightPos The position of the character in the code, counting from
     *            right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        final int pos = useRightPos ? rightPos : leftPos;
        final int weight = positionWeight[(pos - 1) % positionWeight.length];
        int weightedValue = charValue * weight;
        if (sumWeightedDigits) {
            weightedValue = sumDigits(weightedValue);
        }
        return weightedValue;
    }

}
