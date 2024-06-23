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

/**
 * General Modulus 97-10 Check Digit calculation/validation.
 * <p>
 * according to ISO/IEC 7064, MOD 97-10
 * https://de.wikipedia.org/wiki/ISO/IEC_7064
 * 
 * <p>
 * used in {@link org.apache.commons.validator.routines.LeitwegValidator}
 * 
 * <p>
 * This module can also be used to validate the LEI (Legal Entity Identifier), ICD id 0199
 */
public class Modulus97CheckDigit implements CheckDigit, Serializable {

    private static final int MIN_CODE_LEN = 4;

    private static final long serialVersionUID = -6006627136518535270L;

    private static final int MAX_ALPHANUMERIC_VALUE = 35; // Character.getNumericValue('Z')
    
    /** Singleton instance */
    public static final CheckDigit MOD_CHECK_DIGIT = new Modulus97CheckDigit();

    private static final long MAX = 999999999;

    private static final long MODULUS = 97;
    
    /**
     * Calculate the <i>Check Digit</i> for a code.
     * <p>
     * <b>Note:</b> The check digit are the last two characters
     * and is set to the value "<code>00</code>".
     *
     * @param code The code to calculate the Check Digit for
     * @return The calculated Check Digit as 2 numeric decimal characters, e.g. "42"
     * @throws CheckDigitException if an error occurs calculating
     * the check digit for the specified code
     */
    @Override
    public String calculate(String code) throws CheckDigitException {
        if (code == null || code.length() < MIN_CODE_LEN) {
            throw new CheckDigitException("Invalid Code length=" +
                    (code == null ? 0 : code.length()));
        }
        // set MagicNumber to 00
        code = code.substring(0, code.length()-2) + "00"; // CHECKSTYLE IGNORE MagicNumber
        int modulusResult = calculateModulus(code);
        int charValue = (98 - modulusResult); // CHECKSTYLE IGNORE MagicNumber
        String checkDigit = Integer.toString(charValue);
        return (charValue > 9 ? checkDigit : "0" + checkDigit); // CHECKSTYLE IGNORE MagicNumber
    }

    /**
     * Validate the check digit.
     *
     * @param code The code to validate
     * @return <code>true</code> if the check digit is valid, otherwise
     * <code>false</code>
     */
    @Override
    public boolean isValid(String code) {
        if (code == null || code.length() < MIN_CODE_LEN) {
            return false;
        }
        String check = code.substring(code.length()-2); // CHECKSTYLE IGNORE MagicNumber
        if ("00".equals(check) || "01".equals(check) || "99".equals(check)) {
            return false;
        }
        
        try {
            int modulusResult = calculateModulus(code);
            return (modulusResult == 1);
        } catch (CheckDigitException ex) { 
            return false;
        }
    }

    /**
     * Calculate the modulus for an id.
     *
     * @param id The code to calculate the modulus for.
     * @return The modulus value
     */
    private int calculateModulus(String code) throws CheckDigitException {
        String reformattedCode = code; // CHECKSTYLE IGNORE MagicNumber
        long total = 0;
        for (int i = 0; i < reformattedCode.length(); i++) {
            char c = reformattedCode.charAt(i);
            int charValue = Character.getNumericValue(c);
            if (charValue < 0 || charValue > MAX_ALPHANUMERIC_VALUE) {
                throw new CheckDigitException("Invalid Character[" +
                        i + "] = '" + charValue + "'");
            }
            total = (charValue > 9 ? total * 100 : total * 10) + charValue; // CHECKSTYLE IGNORE MagicNumber
            if (total > MAX) {
                total = total % MODULUS;
            }
        }
        return (int)(total % MODULUS);
    }

}
