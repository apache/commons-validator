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

import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.GenericValidator;

/**
 * Estonian VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * Käibemaksukohustuslase registreeri-misnumber (KMKR)
 * </p>
 * <p>
 * See <a href="https://www.emta.ee/en/admin/content/handbook_article/742">Estonian Tax and Customs Board</a>
 * for more details.
 * </p>
 * Estnische Umsatzsteuer-Identifikationsnummer.
 *
 * <p>
 * Aufbau: neun, nur Ziffern.
 * </p>

Beispiel: EE 100931558
aus https://github.com/anghelvalentin/CountryValidator/blob/master/CountryValidator.Tests/CountriesValidators/EstoniaValidatorTests.cs

 *
 * @since 1.10.0
 */
public final class VATidEECheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = 1072398327337581340L;

    /** Singleton Check Digit instance */
    private static final VATidEECheckDigit INSTANCE = new VATidEECheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    static final int LEN = 9; // with Check Digit

    /** Weighting given to digits depending on their left position */
    private static final int[] POSITION_WEIGHT = { 3, 7, 1 };

    /**
     * Constructs a modulus 10 Check Digit routine.
     */
    private VATidEECheckDigit() {
        super(MODULUS_10);
    }

    /**
     * Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.
     *
     * <p>For VATID digits are weighted by their position from left to right.</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        if (leftPos >= LEN) return 0;
        final int weight = POSITION_WEIGHT[(leftPos - 1) % POSITION_WEIGHT.length];
        return charValue * weight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
        }
        if (code.length() >= LEN && GenericTypeValidator.formatLong(code) == 0) {
            throw new CheckDigitException(CheckDigitException.ZREO_SUM);
        }
        return super.calculate(code);
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
            final int cm = INSTANCE.calculateModulus(code, true);
            final int cd = cm == 0 ? 0 : MODULUS_10 - cm;
//  XXX guter Hinweis in HU !!!!
            return cd == Character.getNumericValue(code.charAt(code.length() - 1));
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
