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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.validator.routines.checkdigit.VATINCheckDigit;

// kopiert aus IBANValidator TODO kommentieren + implementieren
/**
 * VAT identification number (VATIN) Validator.
 * <p>
 * The validator includes a default set of formats for European countries, see
 * https://en.wikipedia.org/wiki/VAT_identification_number
 * </p>
// * <p>
// * This can get out of date, but the set can be adjusted by creating a validator and using the
// * {@link #setValidator(String, int, String)} or
// * {@link #setValidator(Validator)}
// * method to add (or remove) an entry.
// * </p>
// * <p>
// * For example:
// * </p>
// * <pre>
// * IBANValidator ibv = new IBANValidator();
// * ibv.setValidator("XX", 12, "XX\\d{10}")
// * </pre>
// * <p>
// * The singleton default instance cannot be modified in this way.
 * </p>
 * @since 1.9.0
 */
public class VATINValidator {

    /**
     * The validation class
     */
    public static class Validator {
        /*
         * The minimum length does not appear to be defined.
         * Denmark, Finnland are currently the shortest (including countryCode).
         */
        private static final int MIN_LEN = 10;
        private static final int MAX_LEN = 16; // non EU: China 18

        final String countryCode;
        final String[] otherCountryCodes;
        final RegexValidator regexValidator;
        final int vatinLength; // used to avoid unnecessary regex matching

        /**
         * Creates the validator.
         * @param countryCode the country code
         * @param maxLength the max length of the VATIN including country code
         * @param regexWithCC the regex to use to check the format, the regex MUST start with the country code.
         */
        public Validator(final String countryCode, final int maxLength, final String regexWithCC) {
            this(countryCode, maxLength, regexWithCC.substring(countryCode.length()), new String[] {});
        }

        /**
         * Creates the validator.
         * @param countryCode the country code
         * @param maxLength the max length of the VATIN including country code
         * @param regexWithoutCC the regex to use to check the format, the regex MUST NOT start with the country code.
         */
        Validator(final String countryCode, final int maxLength, final String regexWithoutCC, final String... otherCountryCodes) {
            if (!(countryCode.length() == 2 && Character.isUpperCase(countryCode.charAt(0)) && Character.isUpperCase(countryCode.charAt(1)))) {
                throw new IllegalArgumentException("Invalid country Code; must be exactly 2 upper-case characters");
            }
            if (maxLength > MAX_LEN || maxLength < MIN_LEN) {
                throw new IllegalArgumentException("Invalid length parameter, must be in range " + MIN_LEN + " to " + MAX_LEN + " inclusive: " + maxLength);
            }
            final String regex = countryCode + regexWithoutCC;
            if (!regex.startsWith(countryCode)) {
                throw new IllegalArgumentException("countryCode '" + countryCode + "' does not agree with format: " + regex);
            }
            this.countryCode = countryCode;
            this.otherCountryCodes = otherCountryCodes.clone();
            final List<String> regexList = new ArrayList<>(this.otherCountryCodes.length + 1);
            regexList.add(countryCode + regexWithoutCC);
            for (final String otherCc : otherCountryCodes) {
                regexList.add(otherCc + regexWithoutCC);
            }
            this.vatinLength = maxLength;
            this.regexValidator = new RegexValidator(regexList);
        }

        /**
         * Gets the RegexValidator.
         *
         * @return the RegexValidator.
         * @since 1.8
         */
        public RegexValidator getRegexValidator() {
            return regexValidator;
        }
    }

    private static final int COUNTRY_CODE_LEN = 2;

/*

Foreign companies that trade with private individuals and non-business organisations in the EU may have a VATIN
starting with "EU" instead of a country code, e.g. Godaddy EU826010755
                                          and Amazon (AWS) EU826009064.
 */
    private static final Validator[] DEFAULT_VALIDATORS = {                   //
            new Validator("AT", 11, "ATU\\d{8}"),                             // Austria  ATU9999999p
            new Validator("BE", 12, "BE[0-1]\\d{9}"),                         // Belgium  BE99999999pp
            // altes Format 9-stellig ist ungültig !!!
            new Validator("BG", 12, "BG(\\d)?\\d{9}"),                        // Bulgaria BG99999999(9)p
            new Validator("CY", 11, "CY[013459]\\d{7}[A-Z]"),                 // Cyprus   CY99999999L
            new Validator("CZ", 12, "CZ(\\d)?(\\d)?\\d{8}"),                  // Czechia  CZ9999999(99)p
            new Validator("DE", 11, "DE\\d{9}"),                              // Germany  DE99999999p
            new Validator("DK", 10, "DK[1-9]\\d{7}"),                         // Denmark  DK99999999
            new Validator("EE", 11, "EE\\d{9}"),                              // Estonia  EE99999999p
            new Validator("EL", 11, "EL\\d{9}"),                              // Greece   EL99999999p
            new Validator("ES", 11, "ES[A-Z0-9]\\d{7}[A-Z0-9]"),              // Spain    ESX9999999P
            new Validator("EU", 11, "EU\\d{9}"),                              // Foreign companies
            new Validator("FI", 10, "FI\\d{8}"),                              // Finland  FI9999999p
            new Validator("FR", 13, "FR[A-Z0-9]{2}\\d{9}"),                   // France   FRXX999999999
            new Validator("HR", 13, "HR\\d{11}"),                             // Croatia  HR9999999999p
            new Validator("HU", 10, "HU\\d{8}"),                              // Hungary  HU9999999p
            new Validator("IE", 11, "IE\\d{7}[A-W]([A-I])?"),                 // Ireland  IE9999999a(A)
            new Validator("IT", 13, "IT\\d{11}"),                             // Italy    IT9999999999p
            // optional Group for Temporarily Registered Taxpayers with 12 digits, C11==1
            new Validator("LT", 14, "LT\\d{9}([0-9]1[0-9])?"),                // Lithuania LT99999999p or 12 digits
            new Validator("LU", 13, "LU\\d{8}"),                              // Luxembourg LU999999pp
            new Validator("LV", 13, "LV[4-9]\\d{10}"),                        // Latvia      LV9999999999p
            new Validator("MT", 14, "MT\\d{8}"),                              // Malta       MT999999pp
            new Validator("NL", 14, "NL\\d{9}B\\d{2}"),                       // Netherlands NL99999999pB01
            new Validator("PL", 12, "PL\\d{10}"),                             // Poland      PL999999999p
            new Validator("PT", 11, "PT\\d{9}"),                              // Portugal    PT99999999p
            new Validator("RO", 12, "RO[1-9](\\d)?(\\d)?(\\d)?(\\d)?(\\d)?(\\d)?(\\d)?(\\d)?\\d"), // Romania     RO999999999p
            new Validator("SE", 14, "SE\\d{10}01"),                           // Sweden      SE999999999p01
            new Validator("SI", 10, "SI[1-9]\\d{7}"),                         // Slovenia    SI19999999p
            // ne of 2, 3, 4, 7, 8, 9
            new Validator("SK", 12, "SK[1-9]\\d[2-4,7-9]\\d{7}"),             // Slovakia    SK19999999999
            new Validator("XI", 14, "XI(\\d{3})?\\d{9}"),                     // North.Ireland XI9999999999
    };

    /** The singleton instance which uses the default formats */
    public static final VATINValidator DEFAULT_VATIN_VALIDATOR = new VATINValidator();

    /**
     * Gets the singleton instance of the VATIN validator using the default formats
     *
     * @return A singleton instance of the validator
     */
    public static VATINValidator getInstance() {
        return DEFAULT_VATIN_VALIDATOR;
    }

    private final ConcurrentMap<String, Validator> validatorMap;

    /**
     * Create a default validator.
     */
    public VATINValidator() {
        this(DEFAULT_VALIDATORS);
    }

    /**
     * Create an VATIN validator from the specified map of VATIN formats.
     *
     * @param validators map of VATIN formats
     */
    public VATINValidator(final Validator[] validators) {
        this.validatorMap = createValidators(validators);
    }

    private ConcurrentMap<String, Validator> createValidators(final Validator[] validators) {
        final ConcurrentMap<String, Validator> map = new ConcurrentHashMap<>();
        for (final Validator validator : validators) {
            map.put(validator.countryCode, validator);
            for (final String otherCC : validator.otherCountryCodes) {
                map.put(otherCC, validator);
            }
        }
        return map;
    }

    /**
     * Gets a copy of the default Validators.
     *
     * @return a copy of the default Validator array
     */
    public Validator[] getDefaultValidators() {
        return Arrays.copyOf(DEFAULT_VALIDATORS, DEFAULT_VALIDATORS.length);
    }

    /**
     * Gets the Validator for a given VATIN
     *
     * @param code a string starting with the ISO country code (e.g. "BG831650349" Nestlé.bg)
     *
     * @return the validator or {@code null} if there is no one registered.
     */
    public Validator getValidator(final String code) {
        if (code == null || code.length() < COUNTRY_CODE_LEN) { // ensure we can extract the key
            return null;
        }
        final String key = code.substring(0, COUNTRY_CODE_LEN);
        return validatorMap.get(key);
    }

    /**
     * Does the class have the required validator?
     *
     * @param code the code to check
     * @return true if there is a validator for the country
     */
    public boolean hasValidator(final String code) {
        return getValidator(code) != null;
    }

    /**
     * Validate a VATIN Code
     *
     * @param code The value validation is being performed on (e.g. "BG831650349" Nestlé.bg)
     * @return {@code true} if the value is valid
     */
    public boolean isValid(final String code) {
        final Validator formatValidator = getValidator(code);
        if (formatValidator == null || code.length() > formatValidator.vatinLength || !formatValidator.regexValidator.isValid(code)) {
            return false;
        }
        return VATINCheckDigit.getInstance().isValid(code);
    }

    /**
     * Installs a validator.
     * Will replace any existing entry which has the same countryCode.
     *
     * @param countryCode the country code
     * @param length the length of the VATIN. Must be &ge; 8 and &le; 32.
     *  If the length is &lt; 0, the validator is removed, and the format is not used.
     * @param format the format of the VATIN for the country (as a regular expression)
     * @return the previous Validator, or {@code null} if there was none
     * @throws IllegalArgumentException if there is a problem
     * @throws IllegalStateException if an attempt is made to modify the singleton validator
     */
    public Validator setValidator(final String countryCode, final int length, final String format) {
        if (this == DEFAULT_VATIN_VALIDATOR) {
            throw new IllegalStateException("The singleton validator cannot be modified");
        }
        if (length < 0) {
            return validatorMap.remove(countryCode);
        }
        return setValidator(new Validator(countryCode, length, format));
    }

    /**
     * Installs a validator.
     * Will replace any existing entry which has the same countryCode
     *
     * @param validator the instance to install.
     * @return the previous Validator, or {@code null} if there was none
     * @throws IllegalStateException if an attempt is made to modify the singleton validator
     */
    public Validator setValidator(final Validator validator) {
        if (this == DEFAULT_VATIN_VALIDATOR) {
            throw new IllegalStateException("The singleton validator cannot be modified");
        }
        return validatorMap.put(validator.countryCode, validator);
    }
}
