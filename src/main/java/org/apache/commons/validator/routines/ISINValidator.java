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
package org.apache.commons.validator.routines;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.validator.routines.checkdigit.ISINCheckDigit;

/**
 * <b>ISIN</b> (International Securities Identifying Number) validation.
 *
 * <p>
 * ISIN Numbers are 12 character alphanumeric codes used to identify Securities.
 * </p>
 *
 * <p>
 * ISINs consist of two alphabetic characters,
 * which are the ISO 3166-1 alpha-2 code for the issuing country,
 * nine alpha-numeric characters (the National Securities Identifying Number, or NSIN, which identifies the security),
 * and one numerical check digit.
 * They are 12 characters in length.
 * </p>
 *
 * <p>
 * See <a href="http://en.wikipedia.org/wiki/ISIN">Wikipedia - ISIN</a>
 * for more details.
 * </p>
 *
 * @since 1.7
 */
public class ISINValidator implements Serializable {

    private static final long serialVersionUID = -5964391439144260936L;

    private static final String ISIN_REGEX = "([A-Z]{2}[A-Z0-9]{9}[0-9])";

    private static final CodeValidator VALIDATOR = new CodeValidator(ISIN_REGEX, 12, ISINCheckDigit.ISIN_CHECK_DIGIT);

    /** ISIN Code Validator (no countryCode check) */
    private static final ISINValidator ISIN_VALIDATOR_FALSE = new ISINValidator(false);

    /** ISIN Code Validator (with countryCode check) */
    private static final ISINValidator ISIN_VALIDATOR_TRUE = new ISINValidator(true);

    private static final String [] CCODES = Locale.getISOCountries();

    private static final String [] SPECIALS = {
            "EZ", // http://www.anna-web.org/standards/isin-iso-6166/
            "XS", // https://www.isin.org/isin/
        };

    static {
        Arrays.sort(CCODES); // we cannot assume the codes are sorted
        Arrays.sort(SPECIALS); // Just in case ...
    }

    private final boolean checkCountryCode;

    /**
     * Return a singleton instance of the ISIN validator
     * @param checkCountryCode whether to check the country-code prefix or not
     * @return A singleton instance of the appropriate ISIN validator.
     */
    public static ISINValidator getInstance(boolean checkCountryCode) {
        return checkCountryCode ? ISIN_VALIDATOR_TRUE : ISIN_VALIDATOR_FALSE;
    }

    private ISINValidator(boolean checkCountryCode) {
        this.checkCountryCode = checkCountryCode;
    }

    /**
     * Check the code is a valid ISIN code after any transformation
     * by the validate routine.
     * @param code The code to validate.
     * @return <code>true</code> if a valid ISIN
     * code, otherwise <code>false</code>.
     */
    public boolean isValid(String code) {
        final boolean valid = VALIDATOR.isValid(code);
        if (valid && checkCountryCode) {
            return checkCode(code.substring(0,2));
        }
        return valid;
    }

    /**
     * Check the code is valid ISIN code.
     *
     * @param code The code to validate.
     * @return A valid ISIN code if valid, otherwise <code>null</code>.
     */
    public Object validate(String code) {
        final Object validate = VALIDATOR.validate(code);
        if (validate != null && checkCountryCode) {
            return checkCode(code.substring(0,2)) ? validate : null;
        }
        return validate;
    }

    private boolean checkCode(String code) {
        return Arrays.binarySearch(CCODES, code) >= 0
               ||
               Arrays.binarySearch(SPECIALS, code) >= 0
        ;
    }

}
