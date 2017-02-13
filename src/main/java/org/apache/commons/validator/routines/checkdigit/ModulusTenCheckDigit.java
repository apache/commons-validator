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

import java.util.Arrays;

import org.apache.commons.validator.routines.CodeValidator;

/**
 * General Modulus 10 Check Digit calculation/validation.
 *
 * <h3>How if Works</h3>
 * <p>
 * This implementation calculates/validates the check digit in the following
 * way:
 * <ul>
 * <li>Converting each character to an integer value using
 * <code>Character.getNumericValue(char)</code> - negative integer values from
 * that method are invalid.</li>
 * <li>Calculating a <i>weighted value</i> by multiplying the character's
 * integer value by a <i>weighting factor</i>. The <i>weighting factor</i> is
 * selected from the configured <code>postitionWeight</code> array based on its
 * position. The <code>postitionWeight</code> values are used either
 * left-to-right (when <code>useRightPos=false</code>) or right-to-left (when
 * <code>useRightPos=true</code>).</li>
 * <li>If <code>sumWeightedDigits=true</code>, the <i>weighted value</i> is
 * re-calculated by summing its digits.</li>
 * <li>The <i>weighted values</i> of each character are totalled.</li>
 * <li>The total modulo 10 will be zero for a code with a valid Check Digit.</li>
 * </ul>
 * <h3>Limitations</h3>
 * <p>
 * This implementation has the following limitations:
 * <ul>
 * <li>It assumes the last character in the code is the Check Digit and
 * validates that it is a numeric character.</li>
 * <li>The only limitation on valid characters are those that
 * <code>Character.getNumericValue(char)</code> returns a positive value. If,
 * for example, the code should only contain numbers, this implementation does
 * not check that.</li>
 * <li>There are no checks on code length.</li>
 * </ul>
 * <p>
 * <b>Note:</b> This implementation can be combined with the
 * {@link CodeValidator} in order to ensure the length and characters are valid.
 * 
 * <h3>Example Usage</h3>
 * <p>
 * This implementation was added after a number of Modulus 10 routines and these
 * are shown re-implemented using this routine below:
 * 
 * <p>
 * <b>ABA Number</b> Check Digit Routine (equivalent of
 * {@link ABANumberCheckDigit}). Weighting factors are <code>[1, 7, 3]</code>
 * applied from right to left.
 * 
 * <pre>
 * CheckDigit routine = new ModulusTenCheckDigit(new int[] { 1, 7, 3 }, true);
 * </pre>
 * 
 * <p>
 * <b>CUSIP</b> Check Digit Routine (equivalent of {@link CUSIPCheckDigit}).
 * Weighting factors are <code>[1, 2]</code> applied from right to left and the
 * digits of the <i>weighted value</i> are summed.
 * 
 * <pre>
 * CheckDigit routine = new ModulusTenCheckDigit(new int[] { 1, 2 }, true, true);
 * </pre>
 *
 * <p>
 * <b>EAN-13 / UPC</b> Check Digit Routine (equivalent of
 * {@link EAN13CheckDigit}). Weighting factors are <code>[1, 3]</code> applied
 * from right to left.
 * 
 * <pre>
 * CheckDigit routine = new ModulusTenCheckDigit(new int[] { 1, 3 }, true);
 * </pre>
 *
 * <p>
 * <b>Luhn</b> Check Digit Routine (equivalent of {@link LuhnCheckDigit}).
 * Weighting factors are <code>[1, 2]</code> applied from right to left and the
 * digits of the <i>weighted value</i> are summed.
 * 
 * <pre>
 * CheckDigit routine = new ModulusTenCheckDigit(new int[] { 1, 2 }, true, true);
 * </pre>
 *
 * <p>
 * <b>SEDOL</b> Check Digit Routine (equivalent of {@link SedolCheckDigit}).
 * Weighting factors are <code>[1, 3, 1, 7, 3, 9, 1]</code> applied from left to
 * right.
 * 
 * <pre>
 * CheckDigit routine = new ModulusTenCheckDigit(new int[] { 1, 3, 1, 7, 3, 9, 1 });
 * </pre>
 *
 * @since Validator 1.6
 * @version $Revision: 1739356 $
 */
public final class ModulusTenCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -3752929983453368497L;

    private final int[] postitionWeight;
    private final boolean useRightPos;
    private final boolean sumWeightedDigits;

    /**
     * Construct a modulus 10 Check Digit routine with the specified weighting
     * from left to right.
     * 
     * @param postitionWeight the weighted values to apply based on the
     *            character position
     */
    public ModulusTenCheckDigit(int[] postitionWeight) {
        this(postitionWeight, false, false);
    }

    /**
     * Construct a modulus 10 Check Digit routine with the specified weighting,
     * indicating whether its from the left or right.
     * 
     * @param postitionWeight the weighted values to apply based on the
     *            character position
     * @param useRightPos <code>true</code> if use positionWeights from right to
     *            left
     */
    public ModulusTenCheckDigit(int[] postitionWeight, boolean useRightPos) {
        this(postitionWeight, useRightPos, false);
    }

    /**
     * Construct a modulus 10 Check Digit routine with the specified weighting,
     * indicating whether its from the left or right and whether the weighted
     * digits should be summed.
     * 
     * @param postitionWeight the weighted values to apply based on the
     *            character position
     * @param useRightPos <code>true</code> if use positionWeights from right to
     *            left
     * @param sumWeightedDigits <code>true</code> if sum the digits of the
     *            weighted value
     */
    public ModulusTenCheckDigit(int[] postitionWeight, boolean useRightPos, boolean sumWeightedDigits) {
        super(10); // CHECKSTYLE IGNORE MagicNumber
        this.postitionWeight = Arrays.copyOf(postitionWeight, postitionWeight.length);
        this.useRightPos = useRightPos;
        this.sumWeightedDigits = sumWeightedDigits;
    }

    /**
     * Validate a modulus check digit for a code.
     * <p>
     * Note: assumes last digit is the check digit
     *
     * @param code The code to validate
     * @return <code>true</code> if the check digit is valid, otherwise
     *         <code>false</code>
     */
    @Override
    public boolean isValid(String code) {
        if (code == null || code.length() == 0) {
            return false;
        }
        if (!Character.isDigit(code.charAt(code.length() - 1))) {
            return false;
        }

        return super.isValid(code);
    }

    /**
     * Convert a character at a specified position to an integer value.
     * <p>
     * <b>Note:</b> this implementation only handlers values that
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
    protected int toInt(char character, int leftPos, int rightPos) throws CheckDigitException {
        int num = Character.getNumericValue(character);
        if (num < 0) {
            throw new CheckDigitException("Invalid Character[" + leftPos + "] = '" + character + "'");
        }
        return num;
    }

    /**
     * Calculates the <i>weighted</i> value of a character in the code at a
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
    protected int weightedValue(int charValue, int leftPos, int rightPos) {
        int pos = useRightPos ? rightPos : leftPos;
        int weight = postitionWeight[(pos - 1) % postitionWeight.length];
        int weightedValue = charValue * weight;
        if (sumWeightedDigits) {
            weightedValue = ModulusCheckDigit.sumDigits(weightedValue);
        }
        return weightedValue;
    }

    /**
     * Return a string representation of this implementation.
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[postitionWeight=" + Arrays.toString(postitionWeight) + ", useRightPos="
                + useRightPos + ", sumWeightedDigits=" + sumWeightedDigits + "]";
    }

}
