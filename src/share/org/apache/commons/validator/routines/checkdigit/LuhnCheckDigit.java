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


/**
 * Modulus 10 <b>Luhn</b> Check Digit calculation/validation.
 * <p>
 * Luhn check digits are used, for example, by credit card numbers. See
 * <a href="http://en.wikipedia.org/wiki/Luhn_algorithm">Wikipedia - Luhn
 * algorithm</a> for details.
 * <p>
 * Calculation based on the following criteria:
 * <p>
 *
 * <ul>
 *   <li>Modulus 10.</li>
 *   <li>Odd digits weighted by one (right to left)</li>
 *   <li>Even digits weighted by two (right to left)</li>
 *   <li>Weighted values > 9 have 9 subtracted.</li>
 * </ul>
 *
 * <p>
 * See <a href="http://en.wikipedia.org/wiki/Luhn_algorithm">Wikipedia</a>
 * for more details.
 *
 * @version $Revision$ $Date$
 * @since Validator 1.4
 */
public final class LuhnCheckDigit extends ModulusCheckDigit implements Serializable {

    /** Static Luhn Check Digit instance */
    public static final CheckDigit INSTANCE = new LuhnCheckDigit();

    private static final int ODD_WEIGHT  = 1;
    private static final int EVEN_WEIGHT = 2;

    /**
     * Construct a modulus 10 Luhn Check Digit routine.
     */
    public LuhnCheckDigit() {
        super(10);
    }

    /**
     * <p>Calculates the <i>weighted</i> value of a charcter in the
     * code at a specified position.</p>
     *
     * <p>For Luhn (from right to left) <b>odd</b> digits are weighted
     * with a factor of <b>one</b> and <b>even</b> digits with a factor
     * of <b>two</b>. Weighted values > 9, have 9 subtracted</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right 
     * @param rightPos The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    protected int weightedValue(int charValue, int leftPos, int rightPos) {
        boolean oddPosition = (rightPos % 2 == 1);
        int weight = (oddPosition  ? ODD_WEIGHT : EVEN_WEIGHT);
        int weightedValue = (charValue * weight);
        return (weightedValue > 9 ? (weightedValue - 9) : weightedValue);
    }
}
