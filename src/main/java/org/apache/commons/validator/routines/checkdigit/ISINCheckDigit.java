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

/**
 * Modulus 10 <b>ISIN</b> (International Securities Identifying Number) Check Digit calculation/validation.
 *
 * <p>
 * ISIN Numbers are 12 character alphanumeric codes used
 * to identify Securities.
 * </p>
 *
 * <p>
 * Check digit calculation uses the <i>Modulus 10 Double Add Double</i> technique
 * with every second digit being weighted by 2. Alphabetic characters are
 * converted to numbers by their position in the alphabet starting with A being 10.
 * Weighted numbers greater than ten are treated as two separate numbers.
 * </p>
 *
 * <p>
 * See <a href="http://en.wikipedia.org/wiki/ISIN">Wikipedia - ISIN</a>
 * for more details.
 * </p>
 *
 * @since 1.4
 */
public final class ISINCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -1239211208101323599L;

    private static final int MAX_ALPHANUMERIC_VALUE = 35; // Character.getNumericValue('Z')

    /** Singleton ISIN Check Digit instance */
    public static final CheckDigit ISIN_CHECK_DIGIT = new ISINCheckDigit();

    /** weighting given to digits depending on their right position */
    private static final int[] POSITION_WEIGHT = {2, 1};

    /**
     * Construct an ISIN Indetifier Check Digit routine.
     */
    public ISINCheckDigit() {
        super(10); // CHECKSTYLE IGNORE MagicNumber
    }

    /**
     * Calculate the modulus for an ISIN code.
     *
     * @param code The code to calculate the modulus for.
     * @param includesCheckDigit Whether the code includes the Check Digit or not.
     * @return The modulus value
     * @throws CheckDigitException if an error occurs calculating the modulus
     * for the specified code
     */
    @Override
    protected int calculateModulus(final String code, final boolean includesCheckDigit) throws CheckDigitException {
        final StringBuilder transformed = new  StringBuilder(code.length() * 2); // CHECKSTYLE IGNORE MagicNumber
        if (includesCheckDigit) {
            final char checkDigit = code.charAt(code.length()-1); // fetch the last character
            if (!Character.isDigit(checkDigit)){
                throw new CheckDigitException("Invalid checkdigit["+ checkDigit+ "] in " + code);
            }
        }
        for (int i = 0; i < code.length(); i++) {
            final int charValue = Character.getNumericValue(code.charAt(i));
            if (charValue < 0 || charValue > MAX_ALPHANUMERIC_VALUE) {
                throw new CheckDigitException("Invalid Character[" +
                        (i + 1) + "] = '" + charValue + "'");
            }
             // this converts alphanumerics to two digits
             // so there is no need to overload toInt()
            transformed.append(charValue);
        }
        return super.calculateModulus(transformed.toString(), includesCheckDigit);
    }

    /**
     * <p>Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.</p>
     *
     * <p>For ISIN (from right to left) <b>odd</b> digits are weighted
     * with a factor of <b>one</b> and <b>even</b> digits with a factor
     * of <b>two</b>. Weighted values are reduced to their digital root</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos  The position of the character in the code, counting from left to right
     * @param rightPos The position of the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        final int weight = POSITION_WEIGHT[rightPos % 2]; // CHECKSTYLE IGNORE MagicNumber
        final int weightedValue = charValue * weight;
        return ModulusCheckDigit.sumDigits(weightedValue);
    }
}
