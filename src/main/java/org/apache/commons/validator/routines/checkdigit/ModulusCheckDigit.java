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

import java.io.Serializable;

import org.apache.commons.validator.GenericValidator;

/**
 * Abstract <strong>Modulus</strong> Check digit calculation/validation.
 * <p>
 * Provides a <em>base</em> class for building <em>modulus</em> Check Digit routines.
 * </p>
 * <p>
 * This implementation only handles <em>single-digit numeric</em> codes, such as <strong>EAN-13</strong>. For <em>alphanumeric</em> codes such as <strong>EAN-128</strong> you will need
 * to implement/override the {@code toInt()} and {@code toChar()} methods.
 * </p>
 *
 * @since 1.4
 */
public abstract class ModulusCheckDigit extends AbstractCheckDigit implements Serializable {

    static final int MODULUS_10 = 10;
    static final int MODULUS_11 = 11;
    private static final long serialVersionUID = 2948962251251528941L;

    /**
     * Add together the individual digits in a number.
     *
     * @param number The number whose digits are to be added
     * @return The sum of the digits
     */
    public static int sumDigits(final int number) {
        int total = 0;
        int todo = number;
        while (todo > 0) {
            total += todo % 10; // CHECKSTYLE IGNORE MagicNumber
            todo /= 10; // CHECKSTYLE IGNORE MagicNumber
        }
        return total;
    }

    /**
     * The modulus can be greater than 10 provided that the implementing class overrides toCheckDigit and toInt (for example as in ISBN10CheckDigit).
     */
    private final int modulus;

    /**
     * Constructs a modulus 10 {@link CheckDigit} routine for a specified modulus.
     */
    ModulusCheckDigit() {
        this(MODULUS_10);
    }

    /**
     * Constructs a {@link CheckDigit} routine for a specified modulus.
     *
     * @param modulus The modulus value to use for the check digit calculation
     */
    public ModulusCheckDigit(final int modulus) {
        this.modulus = modulus;
    }

    /**
     * Calculate a modulus <em>Check Digit</em> for a code which does not yet have one.
     *
     * @param code The code for which to calculate the Check Digit;
     * the check digit should not be included
     * @return The calculated Check Digit
     * @throws CheckDigitException if an error occurs calculating the check digit
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException("Code is missing");
        }
        final int modulusResult = calculateModulus(code, false);
        final int charValue = (modulus - modulusResult) % modulus;
        return toCheckDigit(charValue);
    }

    /**
     * Calculate the modulus for a code.
     *
     * @param code The code to calculate the modulus for.
     * @param includesCheckDigit Whether the code includes the Check Digit or not.
     * @return The modulus value
     * @throws CheckDigitException if an error occurs calculating the modulus
     * for the specified code
     */
    protected int calculateModulus(final String code, final boolean includesCheckDigit) throws CheckDigitException {
        int total = 0;
        for (int i = 0; i < code.length(); i++) {
            final int lth = code.length() + (includesCheckDigit ? 0 : 1);
            final int leftPos = i + 1;
            final int rightPos = lth - i;
            final int charValue = toInt(code.charAt(i), leftPos, rightPos);
            total += weightedValue(charValue, leftPos, rightPos);
        }
        if (total == 0) {
            throw new CheckDigitException("Invalid code, sum is zero");
        }
        return total % modulus;
    }

    /**
     * Gets the modulus value this check digit routine is based on.
     *
     * @return The modulus value this check digit routine is based on
     */
    public int getModulus() {
        return modulus;
    }

    /**
     * Validate a modulus check digit for a code.
     *
     * @param code The code to validate
     * @return {@code true} if the check digit is valid, otherwise
     * {@code false}
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        try {
            final int modulusResult = calculateModulus(code, true);
            return modulusResult == 0;
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

    /**
     * Convert an integer value to a check digit.
     * <p>
     * <strong>Note:</strong> this implementation only handles single-digit numeric values
     * For non-numeric characters, override this method to provide
     * integer--&gt;character conversion.
     *
     * @param charValue The integer value of the character
     * @return The converted character
     * @throws CheckDigitException if integer character value
     * doesn't represent a numeric character
     */
    protected String toCheckDigit(final int charValue) throws CheckDigitException {
        if (charValue >= 0 && charValue <= 9) { // CHECKSTYLE IGNORE MagicNumber
            return Integer.toString(charValue);
        }
        throw new CheckDigitException("Invalid Check Digit Value =" + +charValue);
    }

    /**
     * Convert a character at a specified position to an integer value.
     * <p>
     * <strong>Note:</strong> this implementation only handlers numeric values
     * For non-numeric characters, override this method to provide
     * character--&gt;integer conversion.
     *
     * @param character The character to convert
     * @param leftPos The position of the character in the code, counting from left to right (for identifiying the position in the string)
     * @param rightPos The position of the character in the code, counting from right to left (not used here)
     * @return The integer value of the character
     * @throws CheckDigitException if character is non-numeric
     */
    protected int toInt(final char character, final int leftPos, final int rightPos) throws CheckDigitException {
        if (Character.isDigit(character)) {
            return Character.getNumericValue(character);
        }
        throw new CheckDigitException("Invalid Character[" + leftPos + "] = '" + character + "'");
    }

    /**
     * Calculates the <em>weighted</em> value of a character in the
     * code at a specified position.
     * <p>
     * Some modulus routines weight the value of a character
     * depending on its position in the code (for example, ISBN-10), while
     * others use different weighting factors for odd/even positions
     * (for example, EAN or Luhn). Implement the appropriate mechanism
     * required by overriding this method.
     *
     * @param charValue The numeric value of the character
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The position of the character in the code, counting from right to left
     * @return The weighted value of the character
     * @throws CheckDigitException if an error occurs calculating
     * the weighted value
     */
    protected abstract int weightedValue(int charValue, int leftPos, int rightPos) throws CheckDigitException;

}
