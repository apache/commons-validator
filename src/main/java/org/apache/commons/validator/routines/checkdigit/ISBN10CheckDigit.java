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
 * Modulus 11 <b>ISBN-10</b> Check Digit calculation/validation.
 * <p>
 * ISBN-10 Numbers are a numeric code except for the last (check) digit
 * which can have a value of "X".
 * <p>
 * Check digit calculation is based on <i>modulus 11</i> with digits being weighted
 * based by their position, from right to left  with the first digit being weighted
 * 1, the second 2 and so on. If the check digit is calculated as "10" it is converted
 * to "X".
 * <p>
 * <b>N.B.</b> From 1st January 2007 the book industry will start to use a new 13 digit
 * ISBN number (rather than this 10 digit ISBN number) which uses the EAN-13 / UPC
 * (see {@link EAN13CheckDigit}) standard.
 * <p>
 * For further information see:
 * <ul>
 *   <li><a href="https://en.wikipedia.org/wiki/ISBN">Wikipedia - International
 *       Standard Book Number (ISBN)</a>.</li>
 *   <li><a href="http://www.isbn.org/standards/home/isbn/transition.asp">ISBN-13
 *       Transition details</a>.</li>
 * </ul>
 *
 * @since 1.4
 */
public final class ISBN10CheckDigit extends ModulusCheckXDigit {

    private static final long serialVersionUID = 8000855044504864964L;

    /** Singleton ISBN-10 Check Digit instance */
    public static final CheckDigit ISBN10_CHECK_DIGIT = new ISBN10CheckDigit();

    /**
     * Constructs a modulus 11 Check Digit routine for ISBN-10.
     */
    public ISBN10CheckDigit() {
        super();
    }

    /**
     * Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.
     *
     * <p>For ISBN-10 (from right to left) digits are weighted
     * by their position.</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        return charValue * rightPos;
    }

}
