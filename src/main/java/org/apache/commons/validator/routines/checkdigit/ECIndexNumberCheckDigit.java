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

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.ECIndexNumberValidator;

/**
 * Modulus 11 <b>EC index number</b> Check Digit calculation/validation.
 *
 * <p>
 * EC Index Numbers are a numeric code except for the last (check) digit
 * which can have a value of "X".
 * <br>
 * Note that these <b>do not validate</b> the input for syntax.
 * Such validation is performed by the {@link ECIndexNumberValidator}
 * </p>
 *
 * @since 1.9.0
 */
public final class ECIndexNumberCheckDigit extends ModulusCheckXDigit {

    private static final long serialVersionUID = 2078815937513115949L;

    /** Singleton Check Digit instance */
    private static final ECIndexNumberCheckDigit INSTANCE = new ECIndexNumberCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the EC Index Number validator.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    /**
     * EC index number consists of 4 groups of nine numbers separated by dashes (-).
     * Example: lithium is 003-001-00-4
     * The length without dashes.
     */
    public static final int LEN = 9;

    /**
     * Constructs a modulus 11 Check Digit routine.
     */
    private ECIndexNumberCheckDigit() {
        super();
    }

    /**
     * Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.
     *
     * <p>For EC index number digits are weighted by their position from left to right.</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        return leftPos >= LEN ? 0 : charValue * leftPos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException("Code is missing");
        }
        int modulusResult = INSTANCE.calculateModulus(code, false);
        return toCheckDigit(modulusResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        if (code.length() != LEN) {
            return false;
        }
        try {
            final int modulusResult = INSTANCE.calculateModulus(code, true);
            return toCheckDigit(modulusResult).equals(code.substring(code.length() - 1));
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
