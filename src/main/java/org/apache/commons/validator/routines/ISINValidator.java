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
package org.apache.commons.validator.routines;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.validator.routines.checkdigit.ISINCheckDigit;

/**
 * <strong>ISIN</strong> (International Securities Identifying Number) validation.
 *
 * <p>
 * ISIN Numbers are 12 character alphanumeric codes used to identify Securities.
 * </p>
 *
 * <p>
 * ISINs consist of two alphabetic characters,
 * which are the ISO 3166-1 alpha-2 code for the issuing country,
 * nine alphanumeric characters (the National Securities Identifying Number, or NSIN, which identifies the security),
 * and one numerical check digit.
 * They are 12 characters in length.
 * </p>
 *
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/ISIN">Wikipedia - ISIN</a>
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

    /**
     * All codes from ISO 3166-1 alpha-2 except unassigned code elements.
     *
     * From https://www.iso.org/obp/ui/#iso:pub:PUB500001:en as of 2024-03-23.
     */
    private static final String[] SPECIALS = {
            "AA",
            "AC",
            "AD",
            "AE",
            "AF",
            "AG",
            "AI",
            "AL",
            "AM",
            "AN",
            "AO",
            "AP",
            "AQ",
            "AR",
            "AS",
            "AT",
            "AU",
            "AW",
            "AX",
            "AZ",
            "BA",
            "BB",
            "BD",
            "BE",
            "BF",
            "BG",
            "BH",
            "BI",
            "BJ",
            "BL",
            "BM",
            "BN",
            "BO",
            "BQ",
            "BR",
            "BS",
            "BT",
            "BU",
            "BV",
            "BW",
            "BX",
            "BY",
            "BZ",
            "CA",
            "CC",
            "CD",
            "CF",
            "CG",
            "CH",
            "CI",
            "CK",
            "CL",
            "CM",
            "CN",
            "CO",
            "CP",
            "CQ",
            "CR",
            "CS",
            "CT",
            "CU",
            "CV",
            "CW",
            "CX",
            "CY",
            "CZ",
            "DD",
            "DE",
            "DG",
            "DJ",
            "DK",
            "DM",
            "DO",
            "DY",
            "DZ",
            "EA",
            "EC",
            "EE",
            "EF",
            "EG",
            "EH",
            "EM",
            "EP",
            "ER",
            "ES",
            "ET",
            "EU",
            "EV",
            "EW",
            "EZ",
            "FI",
            "FJ",
            "FK",
            "FL",
            "FM",
            "FO",
            "FQ",
            "FR",
            "FX",
            "GA",
            "GB",
            "GC",
            "GD",
            "GE",
            "GF",
            "GG",
            "GH",
            "GI",
            "GL",
            "GM",
            "GN",
            "GP",
            "GQ",
            "GR",
            "GS",
            "GT",
            "GU",
            "GW",
            "GY",
            "HK",
            "HM",
            "HN",
            "HR",
            "HT",
            "HU",
            "HV",
            "IB",
            "IC",
            "ID",
            "IE",
            "IL",
            "IM",
            "IN",
            "IO",
            "IQ",
            "IR",
            "IS",
            "IT",
            "JA",
            "JE",
            "JM",
            "JO",
            "JP",
            "JT",
            "KE",
            "KG",
            "KH",
            "KI",
            "KM",
            "KN",
            "KP",
            "KR",
            "KW",
            "KY",
            "KZ",
            "LA",
            "LB",
            "LC",
            "LF",
            "LI",
            "LK",
            "LR",
            "LS",
            "LT",
            "LU",
            "LV",
            "LY",
            "MA",
            "MC",
            "MD",
            "ME",
            "MF",
            "MG",
            "MH",
            "MI",
            "MK",
            "ML",
            "MM",
            "MN",
            "MO",
            "MP",
            "MQ",
            "MR",
            "MS",
            "MT",
            "MU",
            "MV",
            "MW",
            "MX",
            "MY",
            "MZ",
            "NA",
            "NC",
            "NE",
            "NF",
            "NG",
            "NH",
            "NI",
            "NL",
            "NO",
            "NP",
            "NQ",
            "NR",
            "NT",
            "NU",
            "NZ",
            "OA",
            "OM",
            "PA",
            "PC",
            "PE",
            "PF",
            "PG",
            "PH",
            "PI",
            "PK",
            "PL",
            "PM",
            "PN",
            "PR",
            "PS",
            "PT",
            "PU",
            "PW",
            "PY",
            "PZ",
            "QA",
            "QM",
            "QN",
            "QO",
            "QP",
            "QQ",
            "QR",
            "QS",
            "QT",
            "QU",
            "QV",
            "QW",
            "QX",
            "QY",
            "QZ",
            "RA",
            "RB",
            "RC",
            "RE",
            "RH",
            "RI",
            "RL",
            "RM",
            "RN",
            "RO",
            "RP",
            "RS",
            "RU",
            "RW",
            "SA",
            "SB",
            "SC",
            "SD",
            "SE",
            "SF",
            "SG",
            "SH",
            "SI",
            "SJ",
            "SK",
            "SL",
            "SM",
            "SN",
            "SO",
            "SR",
            "SS",
            "ST",
            "SU",
            "SV",
            "SX",
            "SY",
            "SZ",
            "TA",
            "TC",
            "TD",
            "TF",
            "TG",
            "TH",
            "TJ",
            "TK",
            "TL",
            "TM",
            "TN",
            "TO",
            "TP",
            "TR",
            "TT",
            "TV",
            "TW",
            "TZ",
            "UA",
            "UG",
            "UK",
            "UM",
            "UN",
            "US",
            "UY",
            "UZ",
            "VA",
            "VC",
            "VD",
            "VE",
            "VG",
            "VI",
            "VN",
            "VU",
            "WF",
            "WG",
            "WK",
            "WL",
            "WO",
            "WS",
            "WV",
            "XA",
            "XB",
            "XC",
            "XD",
            "XE",
            "XF",
            "XG",
            "XH",
            "XI",
            "XJ",
            "XK",
            "XL",
            "XM",
            "XN",
            "XO",
            "XP",
            "XQ",
            "XR",
            "XS",
            "XT",
            "XU",
            "XV",
            "XW",
            "XX",
            "XY",
            "XZ",
            "YD",
            "YE",
            "YT",
            "YU",
            "YV",
            "ZA",
            "ZM",
            "ZR",
            "ZW",
            "ZZ",
    };

    static {
        Arrays.sort(CCODES); // we cannot assume the codes are sorted
        Arrays.sort(SPECIALS); // Just in case ...
    }

    /**
     * Gets the singleton instance of the ISIN validator.
     *
     * @param checkCountryCode whether to check the country-code prefix or not
     * @return A singleton instance of the appropriate ISIN validator.
     */
    public static ISINValidator getInstance(final boolean checkCountryCode) {
        return checkCountryCode ? ISIN_VALIDATOR_TRUE : ISIN_VALIDATOR_FALSE;
    }

    /**
     * Whether to check the country code during validation.
     */
    private final boolean checkCountryCode;

    private ISINValidator(final boolean checkCountryCode) {
        this.checkCountryCode = checkCountryCode;
    }

    private boolean checkCode(final String code) {
        return Arrays.binarySearch(CCODES, code) >= 0 || Arrays.binarySearch(SPECIALS, code) >= 0;
    }

    /**
     * Tests whether the code is a valid ISIN code after any transformation
     * by the validate routine.
     *
     * @param code The code to validate.
     * @return {@code true} if a valid ISIN
     * code, otherwise {@code false}.
     */
    public boolean isValid(final String code) {
        final boolean valid = VALIDATOR.isValid(code);
        if (valid && checkCountryCode) {
            return checkCode(code.substring(0, 2));
        }
        return valid;
    }

    /**
     * Checks the code is valid ISIN code.
     *
     * @param code The code to validate.
     * @return A valid ISIN code if valid, otherwise {@code null}.
     */
    public Object validate(final String code) {
        final Object validate = VALIDATOR.validate(code);
        if (validate != null && checkCountryCode) {
            return checkCode(code.substring(0, 2)) ? validate : null;
        }
        return validate;
    }

}
