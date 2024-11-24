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
 * Modulus 10 <strong>Luhn</strong> Check Digit calculation/validation.
 *
 * Luhn check digits are used, for example, by:
 * <ul>
 *    <li><a href="https://en.wikipedia.org/wiki/Credit_card">Credit Card Numbers</a></li>
 *    <li><a href="https://en.wikipedia.org/wiki/IMEI">IMEI Numbers</a> - International
 *        Mobile Equipment Identity Numbers</li>
 * </ul>
 * Check digit calculation is based on <em>modulus 10</em> with digits in
 * an <em>odd</em> position (from right to left) being weighted 1 and <em>even</em>
 * position digits being weighted 2 (weighted values greater than 9 have 9 subtracted).
 *
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/Luhn_algorithm">Wikipedia</a>
 * for more details.
 * </p>
 *
 * @since 1.4
 */
public final class LuhnCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -2976900113942875999L;

    /** Singleton Luhn Check Digit instance */
    public static final CheckDigit LUHN_CHECK_DIGIT = new LuhnCheckDigit();

    /** Weighting given to digits depending on their right position */
    private static final int[] POSITION_WEIGHT = {2, 1};

    /**
     * Constructs a modulus 10 Luhn Check Digit routine.
     */
    public LuhnCheckDigit() {
    }

    /**
     * <p>Calculates the <em>weighted</em> value of a character in the
     * code at a specified position.</p>
     *
     * <p>For Luhn (from right to left) <strong>odd</strong> digits are weighted
     * with a factor of <strong>one</strong> and <strong>even</strong> digits with a factor
     * of <strong>two</strong>. Weighted values &gt; 9, have 9 subtracted</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The position of the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        final int weight = POSITION_WEIGHT[rightPos % 2]; // CHECKSTYLE IGNORE MagicNumber
        final int weightedValue = charValue * weight;
        return weightedValue > 9 ? weightedValue - 9 : weightedValue; // CHECKSTYLE IGNORE MagicNumber
    }
}
