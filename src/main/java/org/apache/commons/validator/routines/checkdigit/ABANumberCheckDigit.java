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
 * Modulus 10 <strong>ABA Number</strong> (or <strong>Routing Transit Number</strong> (RTN)) Check Digit
 * calculation/validation.
 *
 * <p>
 * ABA Numbers (or Routing Transit Numbers) are a nine digit numeric code used
 * to identify American financial institutions for things such as checks or deposits
 * (ABA stands for the American Bankers Association).
 * </p>
 *
 * Check digit calculation is based on <em>modulus 10</em> with digits being weighted
 * based on their position (from right to left) as follows:
 *
 * <ul>
 *   <li>Digits 1, 4 and &amp; 7 are weighted 1</li>
 *   <li>Digits 2, 5 and &amp; 8 are weighted 7</li>
 *   <li>Digits 3, 6 and &amp; 9 are weighted 3</li>
 * </ul>
 *
 * <p>
 * For further information see
 *  <a href="https://en.wikipedia.org/wiki/Routing_transit_number">Wikipedia -
 *  Routing transit number</a>.
 * </p>
 *
 * @since 1.4
 */
public final class ABANumberCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -8255937433810380145L;

    /** Singleton Routing Transit Number Check Digit instance */
    public static final CheckDigit ABAN_CHECK_DIGIT = new ABANumberCheckDigit();

    /** Weighting given to digits depending on their right position */
    private static final int[] POSITION_WEIGHT = {3, 1, 7};

    /**
     * Constructs a modulus 10 Check Digit routine for ABA Numbers.
     */
    public ABANumberCheckDigit() {
    }

    /**
     * Calculates the <em>weighted</em> value of a character in the
     * code at a specified position.
     * <p>
     * ABA Routing numbers are weighted in the following manner:
     * <pre>{@code
     *     left position: 1  2  3  4  5  6  7  8  9
     *            weight: 3  7  1  3  7  1  3  7  1
     * }</pre>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The position of the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        final int weight = POSITION_WEIGHT[rightPos % 3]; // CHECKSTYLE IGNORE MagicNumber
        return charValue * weight;
    }

}
