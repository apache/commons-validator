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
 * Netherlands VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * BTW-identificatienummer.
 * Note the last three characters (B + 2-digit company index) are not part of the
 * check digit calculation
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia - VAT IN</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidNLCheckDigit extends Modulus11XCheckDigit {

    private static final long serialVersionUID = 2429529612265735976L;

    /** Singleton Check Digit instance */
    private static final VATidNLCheckDigit INSTANCE = new VATidNLCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    private static final int LEN = 9; // with Check Digit, but without suffix "B99"

    /**
     * {@inheritDoc}
     */
    @Override
    protected int weightedValue(int charValue, int leftPos, int rightPos) throws CheckDigitException {
        if (leftPos < LEN) {
            return charValue * rightPos;
        }
        return 0; // ignore charValue outside the LEN
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
        }
        // Satisfy testZeroSum
        if (GenericTypeValidator.formatLong(code) == 0) {
            throw new CheckDigitException(CheckDigitException.ZERO_SUM);
        }
        return toCheckDigit(INSTANCE.calculateModulus(code, false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String ocode) {
        String code = ocode;
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        if (code.length() > LEN) {
            if (code.length() != LEN + 3) {  // CHECKSTYLE IGNORE MagicNumber
                return false;
            }
            if (code.charAt(LEN) != 'B' || code.endsWith("B00")) {
                return false;
            }
            code = code.substring(0, LEN);
        }
        if (code.length() <= 1) { // minimum length
            return false;
        }
        try {
            final int cd = INSTANCE.calculateModulus(code, true);
            return code.endsWith(toCheckDigit(cd));
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Override because charValue X is an invalid check digit value.
     * </p>
     */
    @Override
    protected String toCheckDigit(final int charValue) throws CheckDigitException {
        if (charValue == X) {
            throw new CheckDigitException("Invalid Check Digit Value =" + +charValue);
        }
        return super.toCheckDigit(charValue);
    }

}
