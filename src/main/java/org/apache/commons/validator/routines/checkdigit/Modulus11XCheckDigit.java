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
 * Modulus 11-X module for Check Digit calculation/validation of 11-X Numbers.
 * <p>
 * 11-X Numbers are a numeric code except for the last (check) digit
 * which can have a value of "X".
 * </p>
 * <p>
 * Check digit calculation is based on <em>modulus 11</em> with digits being weighted
 * based by their position, from right to left with the first digit being weighted  1,
 * the second 2 and so on. If the check digit is calculated as "10" it is converted to "X".
 * </p>
 * <p>
 * A prominent possible subclass is {@link ISBN10CheckDigit}).
 * This module simplifies some VATIN calculations.
 * </p>
 *
 * @since 1.10.0
 */
public class Modulus11XCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = 5214797259628194566L;

    /**
     * The ALPHABET for the check digit is a number or X which indicates ten.
     */
    static final int X = 10;

    /** Singleton Check Digit instance */
    private static final Modulus11XCheckDigit INSTANCE = new Modulus11XCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }
    Modulus11XCheckDigit() {
        super(MODULUS_11);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Override to handle charValue X.
     * </p>
     */
    @Override
    protected String toCheckDigit(final int charValue) throws CheckDigitException {
        return charValue == X ? "X" : super.toCheckDigit(charValue);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Override to handle weights as right position.
     * </p>
     */
    @Override
    protected int weightedValue(int charValue, int leftPos, int rightPos) throws CheckDigitException {
        return charValue * rightPos;
    }

}
