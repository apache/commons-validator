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
 * Luxemburg VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * Numéro d'identification à la taxe sur la valeur ajoutée {@code 123456pp}.
 * </p>
 * <p>
 * The check digits are calculated as 89 - MOD 89
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia - VAT IN</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidLUCheckDigit extends Modulus97CheckDigit {

    private static final long serialVersionUID = 6690723004719444647L;

    /** Singleton Check Digit instance */
    private static final VATidLUCheckDigit INSTANCE = new VATidLUCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    static final int MODULUS_89 = 89;

    /**
     * Constructs a Check Digit routine.
     */
    private VATidLUCheckDigit() {
        super(MODULUS_89);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
        }
        if (code.length() < MIN_CODE_LEN) {
            throw new CheckDigitException("Invalid Code length=" + code.length());
        }
        final long mr = calculateModulus(code, false);
        if (mr == 0) {
            throw new CheckDigitException(CheckDigitException.ZERO_SUM);
        }
        final int modulusResult = (int) (mr % MODULUS_89);
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
        if (code.length() < MIN_CODE_LEN) {
            return false;
        }

        final String check = code.substring(code.length() - CHECKDIGIT_LEN);
        final Integer icheck = GenericTypeValidator.formatInt(check);
        // formatInt accepts "+0" as 0, avoid this
        if (icheck == null || !Character.isDigit(check.charAt(0))) {
            return false;
        }
        try {
            final long mr = calculateModulus(code, true);
            if (mr == 0) {
                throw new CheckDigitException(CheckDigitException.ZERO_SUM);
            }
            final int modulusResult = (int) (mr % MODULUS_89);
            return icheck.intValue() == modulusResult;
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
