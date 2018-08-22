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
 * Modulus 10 <b>ISIN</b> (International Securities Identifying Number) Check Digit calculation/validation.
 *
 * <p>
 * ISIN Numbers are 12 character alphanumeric codes used
 * to identify Securities.
 * </p>
 *
 * <p>
 * Check digit calculation uses the <i>Modulus 10 Double Add Double</i> technique
 * with every second digit being weighted by 2. Alphabetic characters are
 * converted to numbers by their position in the alphabet starting with A being 10.
 * Weighted numbers greater than ten are treated as two separate numbers.
 * </p>
 *
 * <p>
 * See <a href="http://en.wikipedia.org/wiki/ISIN">Wikipedia - ISIN</a>
 * for more details.
 * </p>
 *
 * @version $Revision$
 * @since Validator 1.4
 */
public final class ISINCheckDigit extends LuhnCheckDigit {

    private static final long serialVersionUID = -1239211208101323599L;

    private static final int MAX_ALPHANUMERIC_VALUE = 35; // Character.getNumericValue('Z')

    /** Singleton ISIN Check Digit instance */
    public static final CheckDigit ISIN_CHECK_DIGIT = new ISINCheckDigit();

    /**
     * Construct an ISIN Identifier Check Digit routine.
     */
    public ISINCheckDigit() {
        super();
    }

    /**
     * Calculate the modulus for an ISIN code.
     *
     * @param code The code to calculate the modulus for.
     * @param includesCheckDigit Whether the code includes the Check Digit or not.
     * @return The modulus value
     * @throws CheckDigitException if an error occurs calculating the modulus
     * for the specified code
     */
    @Override
    protected int calculateModulus(String code, boolean includesCheckDigit) throws CheckDigitException {
        StringBuilder transformed = new  StringBuilder(code.length() * 2);
        if (includesCheckDigit) {
            char checkDigit = code.charAt(code.length()-1); // fetch the last character
            if (!Character.isDigit(checkDigit)){
                throw new CheckDigitException("Invalid checkdigit["+ checkDigit+ "] in " + code);
            }
        }
        for (int i = 0; i < code.length(); i++) {
            int charValue = Character.getNumericValue(code.charAt(i));
            if (charValue < 0 || charValue > MAX_ALPHANUMERIC_VALUE) {
                throw new CheckDigitException("Invalid Character[" +
                        (i + 1) + "] = '" + charValue + "'");
            }
             // this converts alphanumerics to two digits
             // so there is no need to overload toInt()
            transformed.append(charValue);
        }
        return super.calculateModulus(transformed.toString(), includesCheckDigit);
    }

}
