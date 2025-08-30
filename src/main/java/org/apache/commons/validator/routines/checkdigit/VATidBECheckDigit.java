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

/**
 * Belgian VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * Numéro T.V.A. BTW-nummer (Nº TVA BTW-nr.) old schema {@code 1234567pp}.
 * Note the check digit has two characters and that the old numbering schema only had 9 characters,
 * just adding a zero in front makes it a valid number in the new schema {@code 01234567pp}.
 * </p>
 * <p>
 * The check digits are calculated as 97 - MOD 97
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia - VAT IN</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidBECheckDigit extends Modulus97CheckDigit {

    private static final long serialVersionUID = 4622288405648808179L;

    /** Singleton Check Digit instance */
    private static final VATidBECheckDigit INSTANCE = new VATidBECheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    /**
     * Constructs a Check Digit routine.
     */
    private VATidBECheckDigit() {
        super();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden because the check digits are calculated as 97 - modulusResult
     * </p>
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
        }
        try {
            final long l = Long.parseLong(code); // throws NumberFormatException
            if (l == 0) {
                throw new CheckDigitException(CheckDigitException.ZERO_SUM);
            }
            final int r = (int) (l % MODULUS_97); // MOD 97 reminder
            return toCheckDigit(MODULUS_97 - r);
        } catch (final NumberFormatException ex) {
            // Expected exception for high codes f.i. 99999999999999999999999
            throw new CheckDigitException("Invalid Code " + code);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        if (code.length() <= CHECKDIGIT_LEN) {
            return false;
        }
        final String check = code.substring(code.length() - CHECKDIGIT_LEN);
        try {
            final String cd = calculate(code.substring(0, code.length() - CHECKDIGIT_LEN)); // throws CheckDigitException
            return cd.equals(check);
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
