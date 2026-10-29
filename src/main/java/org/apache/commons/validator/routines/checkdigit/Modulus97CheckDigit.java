/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
 * <strong>ISO/IEC 7064, MOD 97-10</strong> Check Digit calculation/validation.
 * <p>
 * This routine implements the pure system MOD 97-10 defined in ISO/IEC 7064 for alphanumeric codes whose two
 * numeric check digits are the <em>last</em> two characters of the code. Letters are converted to their numeric
 * value (A=10, B=11, ..., Z=35) before the modulus is calculated; the complete code, including the two check
 * digits, must give a remainder of 1 when divided by 97.
 * </p>
 * <p>
 * This is the check digit scheme used, among others, by the
 * <a href="https://www.gleif.org/en/about-lei/iso-17442-the-lei-code-structure">Legal Entity Identifier (LEI, ISO 17442)</a>,
 * see {@link org.apache.commons.validator.routines.LEIValidator}.
 * </p>
 * <p>
 * Note: {@link IBANCheckDigit} uses the same MOD 97-10 algorithm, but the IBAN check digits are the third and
 * fourth characters of the code, so that routine rearranges the code before calculating the modulus. This class
 * does not rearrange the code.
 * </p>
 * <p>
 * Note: the class does not check the format or length of the code, only the check digits.
 * </p>
 * <p>
 * For further information see
 * <a href="https://en.wikipedia.org/wiki/ISO/IEC_7064">Wikipedia - ISO/IEC 7064</a>.
 * </p>
 *
 * @since 1.11.1
 */
public final class Modulus97CheckDigit extends AbstractCheckDigit implements Serializable {

    private static final long serialVersionUID = 2744431781404126436L;

    /**
     * Singleton MOD 97-10 Check Digit instance.
     */
    public static final CheckDigit MODULUS_97_CHECK_DIGIT = new Modulus97CheckDigit();

    /** Number of check digit characters. */
    private static final int CHECK_DIGIT_LEN = 2;

    /** Minimum length of a code including its check digits: at least one character plus two check digits. */
    private static final int MIN_CODE_LEN = CHECK_DIGIT_LEN + 1;

    private static final long MAX = 999999999;

    private static final long MODULUS = 97;

    /** The check digits are calculated as 98 minus the modulus, giving values in the range 02 to 98. */
    private static final int CHECK_BASE = 98;

    /**
     * Constructs a MOD 97-10 Check Digit routine.
     */
    public Modulus97CheckDigit() {
    }

    /**
     * Calculates the <em>Check Digits</em> for a code.
     * <p>
     * The code must <strong>not</strong> include the check digits.
     * </p>
     *
     * @param code The code to calculate the Check Digits for.
     * @return The calculated Check Digits as 2 numeric decimal characters, for example, "42".
     * @throws CheckDigitException if an error occurs calculating the check digits for the specified code.
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (code == null || code.isEmpty()) {
            throw new CheckDigitException("Invalid Code length=%,d", 0);
        }
        final int modulusResult = calculateModulus(code + "00");
        final int charValue = CHECK_BASE - modulusResult;
        final String checkDigit = Integer.toString(charValue);
        return charValue > 9 ? checkDigit : "0" + checkDigit; // CHECKSTYLE IGNORE MagicNumber
    }

    /**
     * Calculates the modulus for a code.
     *
     * @param code The code to calculate the modulus for.
     * @return The modulus value.
     * @throws CheckDigitException if the code contains a character which is not an ASCII letter or digit.
     */
    private int calculateModulus(final String code) throws CheckDigitException {
        long total = 0;
        for (int i = 0; i < code.length(); i++) {
            final char ch = code.charAt(i);
            if (!isAsciiAlphaNum(ch)) {
                throw new CheckDigitException("Invalid Character[%d] = '%c'", i + 1, ch);
            }
            final int charValue = Character.getNumericValue(ch);
            total = (charValue > 9 ? total * 100 : total * 10) + charValue; // CHECKSTYLE IGNORE MagicNumber
            if (total > MAX) {
                total %= MODULUS;
            }
        }
        return (int) (total % MODULUS);
    }

    /**
     * Validates the check digits of a code.
     * <p>
     * The code must include the two check digits as its last two characters.
     * </p>
     *
     * @param code The code to validate.
     * @return {@code true} if the check digits are valid, otherwise {@code false}.
     */
    @Override
    public boolean isValid(final String code) {
        if (code == null || code.length() < MIN_CODE_LEN) {
            return false;
        }
        final String check = code.substring(code.length() - CHECK_DIGIT_LEN);
        if (!isAsciiDigit(check.charAt(0)) || !isAsciiDigit(check.charAt(1))) {
            return false;
        }
        // 00, 01 and 99 can never be produced by the calculation (98 minus a value in the range 0 to 96)
        if ("00".equals(check) || "01".equals(check) || "99".equals(check)) {
            return false;
        }
        try {
            return calculateModulus(code) == 1;
        } catch (final CheckDigitException ex) {
            return false;
        }
    }
}
