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
 * Abstract <b>Modulus 11</b> Check digit calculation/validation.
 * <p>
 * Provides a <i>base</i> class for building <i>modulus 11</i> Check Digit routines.
 * </p>
 * <p>
 * This implementation handles <i>single-digit numeric</i> codes 
 * except for the last (check) digit which can have a value of "X", 
 * such as <b>ISSN</b> or {@link ECIndexNumberValidator}.
 * </p>
 *
 * @since 1.9.0
 */
public abstract class ModulusCheckXDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -713912580806785481L;

    /**
     * Constructs a modulus 11 {@link CheckDigit} routine for a specified modulus.
     */
    ModulusCheckXDigit() {
        super(MODULUS_11);
    }

    /**
     * <p>Convert an integer value to a character at a specified position.</p>
     *
     * <p>Value '10' for position 1 (check digit) converted to 'X'.</p>
     *
     * @param charValue The integer value of the character.
     * @return The converted character.
     * @throws CheckDigitException if an error occurs.
     */
    @Override
    protected String toCheckDigit(final int charValue) throws CheckDigitException {
        if (charValue == 10) {  // CHECKSTYLE IGNORE MagicNumber
            return "X";
        }
        return super.toCheckDigit(charValue);
    }

    /**
     * <p>Convert a character at a specified position to an integer value.</p>
     *
     * <p>Character 'X' check digit converted to 10.</p>
     *
     * @param character The character to convert.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The position of the character in the code, counting from right to left
     * @return The integer value of the character.
     * @throws CheckDigitException if an error occurs.
     */
    @Override
    protected int toInt(final char character, final int leftPos, final int rightPos) throws CheckDigitException {
        if (rightPos == 1 && character == 'X') {
            return 10;  // CHECKSTYLE IGNORE MagicNumber
        }
        return super.toInt(character, leftPos, rightPos);
    }

}
