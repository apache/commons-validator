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
 * Modulus 11 <b>ISBN-10</b> Check Digit calculation/validation.
 * <p>
 * Calculation of the <b>ISBN-10</b> Check Digit is based on the
 * following criteria:
 * <p>
 * <ul>
 *   <li>Modulus 11.</li>
 *   <li>Digits weight by their position (right to left)</li>
 *   <li>If checkdigit value is 10 --> Character 'X'</li>
 * </ul>
 *
 * <p>
 * <b>N.B.</b> From 1st January 2007 the book industry will start to use a new 13 digit
 * ISBN number (rather than this 10 digit ISBN number) which uses the EAN-13
 * (see {@link EAN13CheckDigit}) standard. For more information see:
 * <p>
 * For further information see:
 * <ul>
 *   <li><a href="http://en.wikipedia.org/wiki/ISBN">Wikipedia - International
 *       Standard Book Number (ISBN)</a>.</li>
 *   <li><a href="http://www.isbn.org/standards/home/isbn/transition.asp">ISBN-13
 *       Transition details</a>.</li>
 * </ul>
 *
 * @version $Revision$ $Date$
 * @since Validator 1.4
 */
public final class ISBN10CheckDigit extends ModulusCheckDigit implements Serializable {

    /** Static ISBN-10 check digit instance */
    public static final CheckDigit INSTANCE = new ISBN10CheckDigit();

    /**
     * Construct a modulus 11 Check Digit routine for ISBN-10.
     */
    public ISBN10CheckDigit() {
        super(11);
    }

    /**
     * Calculates the <i>weighted</i> value of a charcter in the
     * code at a specified position.
     *
     * <p>For ISBN-10 (from right to left) digits are weighted
     * by their position.</p>
     *
     * @param charValue The numeric value of the character.
     * @param position The position of a character in the code.
     * @return The weighted value of the character.
     */
    protected int weightedValue(int charValue, int position) {
        return (charValue * position);
    }

    /**
     * <p>Convert a character at a specified position to an
     * integer value.</p>
     *
     * <p>Character 'X' check digit converted to 10.</p>
     *
     * @param character The character to convert.
     * @param position The position of a character in the code.
     * @return The integer value of the character.
     * @throws CheckDigitException if an error occurs.
     */
    protected int toInt(char character, int position)
            throws CheckDigitException {
        if (position == 1 && character == 'X') {
            return 10;
        } else {
            return super.toInt(character, position);
        }
    }

    /**
     * <p>Convert an integer value to a character at a specified position.</p>
     *
     * <p>Value '10' for position 1 (check digit) converted to 'X'.</p>
     *
     * @param charValue The integer value of the character.
     * @param position The position of a character in the code.
     * @return The converted character.
     * @throws CheckDigitException if an error occurs.
     */
    protected char toChar(int charValue, int position)
            throws CheckDigitException {
        if (position == 1 && charValue == 10) {
            return 'X';
        } else {
            return super.toChar(charValue, position);
        }
    }

}
