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

import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.GenericValidator;

/**
 * MOD 97-10 module according to ISO/IEC 7064, MOD 97-10.
 * <p>
 * See <a href="https://de.wikipedia.org/wiki/ISO/IEC_7064">Wikipedia - ISO/IEC_7064 (de)</a>
 * for more details.
 * </p>
 *
 * <p>
 * This MOD 97-10 module can be used to validate the LEI (Legal Entity Identifier), ICD id 0199
 * and in VATidBECheckDigit (VATIN_BE), and also in LeitwegValidator
 *
 * @since 1.10.0
 */
public class Modulus97CheckDigit extends AbstractCheckDigit implements Serializable {

    private static final long serialVersionUID = -5746038014913492394L;

    /** Singleton Check Digit instance */
    private static final Modulus97CheckDigit INSTANCE = new Modulus97CheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    /**
     * the check digit length (two digits)
     */
    public static final int CHECKDIGIT_LEN = 2;
    static final int MIN_CODE_LEN = 4;
    private static final int MAX_ALPHANUMERIC_VALUE = 35; // Character.getNumericValue('Z')
    private static final long MAX = 999999999;

    static final int MODULUS_97 = 97;

    /**
     * The modulus typically 97 but can differ (for example as in VATidLUCheckDigit).
     */
    private final int modulus;

    /**
     * Constructs a {@link CheckDigit} routine for a specified modulus.
     */
    Modulus97CheckDigit() {
        this(MODULUS_97);
    }

    /**
     * Constructs a {@link CheckDigit} routine for a specified modulus.
     *
     * @param modulus The modulus value to use for the check digit calculation
     */
    public Modulus97CheckDigit(final int modulus) {
        this.modulus = modulus;
    }

    /**
     * Calculate the <i>Check Digit</i> for a code.
     * <p>
     * <b>Note:</b> The check digit are the last two characters
     * and is set to the value "<code>00</code>" (check digit placeholder).
     *
     * @param code The code to calculate the Check Digit for
     * @return The calculated Check Digit as 2 numeric decimal characters, e.g. "42"
     * @throws CheckDigitException if an error occurs calculating
     * the check digit for the specified code
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
        }
        if (code.length() < MIN_CODE_LEN) {
            throw new CheckDigitException("Invalid Code length=" + code.length());
        }
        long mr = calculateModulus(code, false);
        mr = mr * 100;  // add check digit placeholder "00" CHECKSTYLE IGNORE MagicNumber
        if (mr > MAX) {
            mr = mr % modulus;
        }
        final int modulusResult = (int) (mr % modulus);
        if (modulusResult == 0) {
            throw new CheckDigitException(CheckDigitException.ZERO_SUM);
        }
        final int cdValue = (modulus + 1 - modulusResult) % modulus;
        return toCheckDigit(cdValue);
    }

    /**
     * Calculate the modulus for a code.
     *
     * @param code The code to calculate the modulus for.
     * @param removeCheckDigit to remove the check digit from code.
     * @return The modulus value
     * @throws CheckDigitException if an error occurs calculating the modulus for the specified code
     */
    protected long calculateModulus(final String code, final boolean removeCheckDigit) throws CheckDigitException {
        // reformattedCode with check digit removed
        final String reformattedCode = removeCheckDigit ? code.substring(0, code.length() - CHECKDIGIT_LEN) : code;
        long total = 0;
        for (int i = 0; i < reformattedCode.length(); i++) {
            final char c = reformattedCode.charAt(i);
            final int charValue = Character.getNumericValue(c);
            if (charValue < 0 || charValue > MAX_ALPHANUMERIC_VALUE) {
                throw new CheckDigitException("Invalid Character[" + i + "] = '" + c + "'");
            }
            total = (charValue > 9 ? total * 100 : total * 10) + charValue; // CHECKSTYLE IGNORE MagicNumber
            if (total > MAX) {
                total = total % modulus;
            }
        }
        return total;
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
        if (icheck == null) {
            return false;
        }
        try {
            final long mr = calculateModulus(code, false);
            final int modulusResult = (int) (mr % modulus);
            return modulusResult == 1;
        } catch (CheckDigitException ex) {
            return false;
        }
    }

    /**
     * Convert an integer value to a check digit.
     * <p>
     * <b>Note:</b> this implementation only handles two-digit numeric values
     * For non-numeric results, override this method to provide
     * integer--&gt;character conversion.
     *
     * @param cdValue The integer value of the check digit
     * @return The converted check digit
     * @throws CheckDigitException if integer character value
     * doesn't represent a numeric character
     */
    public static String toCheckDigit(final int cdValue) throws CheckDigitException {
        if (cdValue > 99) { // CHECKSTYLE IGNORE MagicNumber
            throw new CheckDigitException("Invalid Check Digit Value =" + cdValue);
        }
        final String checkDigit = Integer.toString(cdValue);
        return cdValue > 9 ? checkDigit : "0" + checkDigit; // CHECKSTYLE IGNORE MagicNumber
    }

}