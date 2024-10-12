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

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.checkdigit.ABANumberCheckDigit;
import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;
import org.apache.commons.validator.routines.checkdigit.Modulus11TenCheckDigit;
import org.apache.commons.validator.routines.checkdigit.ModulusTenCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidATCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidBECheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidBGCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidCYCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidCZCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidDKCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidELCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidESCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidFICheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidFRCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidGBCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidIECheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidLTCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidLUCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidLVCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidMTCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidNLCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidPLCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidPTCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidROCheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidSECheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidSICheckDigit;
import org.apache.commons.validator.routines.checkdigit.VATidSKCheckDigit;

/**
 * VAT identification number (VATIN) Validator.
 * <p>
 * The validator includes a default set of formats and check routines for European Union countries, see
 * https://en.wikipedia.org/wiki/VAT_identification_number
 * </p>
 * <p>
 * This can be adjusted f.i. adding a new country routines by creating a validator and using the
 * {@link #setValidator(String, int, String, CheckDigit)}
 * method to add (or remove) an entry.
 * </p>
 * <p>
 * For example:
 * </p>
 * <pre>
 * VATINValidator v = new VATINValidator();
 * v.setValidator("GB", 14, "GB(\\d{3})?\\d{9}", VATidGBCheckDigit.getInstance());
 * </pre>
 * <p>
 * The singleton default instance cannot be modified in this way.
 * </p>
 * @since 1.10.0
 */
public class VATINValidator {

    private static final Log LOG = LogFactory.getLog(VATINValidator.class);

    /**
     * The validation class
     */
    public static class Validator {
        /*
         * The minimum length does not appear to be defined.
         * Denmark, Finnland are currently the shortest (including countryCode).
         */
        private static final int MIN_LEN = 10;
        private static final int MAX_LEN = 16;

        final String countryCode;
        final RegexValidator regexValidator;
        final int vatinLength; // used to avoid unnecessary regex matching
        final CheckDigit routine;

        /**
         * Creates the validator.
         * @param cc the country code
         * @param maxLength the max length of the VATIN including country code
         * @param regex the regex to use to check the format, MUST start with the country code.
         * @param routine the Check Digit routine
         */
        public Validator(final String cc, final int maxLength, final String regex, final CheckDigit routine) {
            if (!(cc.length() == 2 && Character.isUpperCase(cc.charAt(0)) && Character.isUpperCase(cc.charAt(1)))) {
                throw new IllegalArgumentException("Invalid country Code; must be exactly 2 upper-case characters");
            }
            if (maxLength > MAX_LEN || maxLength < MIN_LEN) {
                throw new IllegalArgumentException("Invalid length parameter, must be in range " + MIN_LEN + " to " + MAX_LEN + " inclusive: " + maxLength);
            }
            if (!regex.startsWith(cc)) {
                throw new IllegalArgumentException("countryCode '" + cc + "' does not agree with format: " + regex);
            }
            this.countryCode = cc;
            this.vatinLength = maxLength;
            this.regexValidator = new RegexValidator(regex);
            this.routine = routine;
        }
        /**
         * A convinient ctor to create a validator. The country code is prefixed in the regex.
         * @param cc the country code
         * @param routine the Check Digit routine
         * @param maxLength the max length of the VATIN including country code
         * @param regex the regex to use to check the format without country code.
         */
        private Validator(final String cc, final CheckDigit routine, final int maxLength, final String regex) {
            this(cc, maxLength, cc + regex, routine);
        }

        /**
         * Gets the RegexValidator.
         *
         * @return the RegexValidator.
         */
        public RegexValidator getRegexValidator() {
            return regexValidator;
        }
    }

    private static final int COUNTRY_CODE_LEN = 2;
    private static final String INVALID_COUNTRY_CODE = "No CheckDigit routine or invalid country, code=";
    private static final String CANNOT_MODIFY_SINGLETON = "The singleton validator cannot be modified";

    private static final Validator[] DEFAULT_VALIDATORS = {
            new Validator("AT", VATidATCheckDigit.getInstance(), 11, "U\\d{8}"),
            new Validator("BE", VATidBECheckDigit.getInstance(), 12, "[0-1]\\d{9}"),
            new Validator("BG", VATidBGCheckDigit.getInstance(), 12, "(\\d)?\\d{9}"),
            new Validator("CY", VATidCYCheckDigit.getInstance(), 11, "[013459]\\d{7}[A-Z]"),
            new Validator("CZ", VATidCZCheckDigit.getInstance(), 12, "(\\d)?(\\d)?\\d{8}"),
            new Validator("DE", Modulus11TenCheckDigit.getInstance(), 11, "\\d{9}"),
            new Validator("DK", VATidDKCheckDigit.getInstance(), 10, "[1-9]\\d{7}"),
            new Validator("EE", ABANumberCheckDigit.ABAN_CHECK_DIGIT, 11, "\\d{9}"),
            new Validator("EL", VATidELCheckDigit.getInstance(), 11, "\\d{9}"),
            new Validator("ES", VATidESCheckDigit.getInstance(), 11, "[A-Z0-9]\\d{7}[A-Z0-9]"),
            new Validator("FI", VATidFICheckDigit.getInstance(), 10, "\\d{8}"),
            new Validator("FR", VATidFRCheckDigit.getInstance(), 13, "[A-Z0-9]{2}\\d{9}"),
            new Validator("HR", Modulus11TenCheckDigit.getInstance(), 13, "\\d{11}"),
            new Validator("HU", new ModulusTenCheckDigit(new int[] { 1, 3, 7, 9 }, true), 10, "\\d{8}"),
            new Validator("IE", VATidIECheckDigit.getInstance(), 11, "\\d{7}[A-W]([A-I])?"),
            new Validator("IT", LuhnCheckDigit.LUHN_CHECK_DIGIT, 13, "\\d{11}"),
            // optional Group for Temporarily Registered Taxpayers with 12 digits, C11==1
            new Validator("LT", VATidLTCheckDigit.getInstance(), 14, "\\d{9}([0-9]1[0-9])?"),
            new Validator("LU", VATidLUCheckDigit.getInstance(), 13, "\\d{8}"),
            // first digit [4-9] : legal entity , [0-3] : natural person
            new Validator("LV", VATidLVCheckDigit.getInstance(), 13, "\\d\\d{10}"),
            new Validator("MT", VATidMTCheckDigit.getInstance(), 14, "\\d{8}"),
            new Validator("NL", VATidNLCheckDigit.getInstance(), 14, "\\d{9}B\\d{2}"),
            new Validator("PL", VATidPLCheckDigit.getInstance(), 12, "\\d{10}"),
            new Validator("PT", VATidPTCheckDigit.getInstance(), 11, "\\d{9}"),
            new Validator("RO", VATidROCheckDigit.getInstance(), 12, "[1-9](\\d)?(\\d)?(\\d)?(\\d)?(\\d)?(\\d)?(\\d)?(\\d)?\\d"),
            new Validator("SE", VATidSECheckDigit.getInstance(), 14, "\\d{10}01"),
            new Validator("SI", VATidSICheckDigit.getInstance(), 10, "[1-9]\\d{7}"),
            // 2nd digit: one of 2, 3, 4, 7, 8, 9
            new Validator("SK", VATidSKCheckDigit.getInstance(), 12, "[1-9]\\d[2-4,7-9]\\d{7}"),
            new Validator("XI", VATidGBCheckDigit.getInstance(), 14, "(\\d{3})?\\d{9}"),
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
        final Validator validator = getValidator(code);
        if (validator == null || code.length() > validator.vatinLength || !validator.regexValidator.isValid(code)) {
            return false;
        }
        if (validator.routine == null) {
            LOG.warn(INVALID_COUNTRY_CODE + code);
            return false;
        }
        return validator.routine.isValid(code.substring(2));
    }

    /**
     * Installs a validator.
     * Will replace any existing entry which has the same countryCode.
     *
     * @param countryCode the country code
     * @param length the length of the VATIN. Must be &ge; 8 and &le; 32.
     *  If the length is &lt; 0, the validator is removed, and the format is not used.
     * @param format the format of the VATIN for the country (as a regular expression)
     * @param routine the CheckDigit module
     * @return the previous Validator, or {@code null} if there was none
     * @throws IllegalArgumentException if there is a problem
     * @throws IllegalStateException if an attempt is made to modify the singleton validator
     */
    public Validator setValidator(final String countryCode, final int length, final String format, final CheckDigit routine) {
        if (this == DEFAULT_VATIN_VALIDATOR) {
            throw new IllegalStateException(CANNOT_MODIFY_SINGLETON);
        }
        if (length < 0) {
            return validatorMap.remove(countryCode);
        }
        return setValidator(new Validator(countryCode, length, format, routine));
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
            throw new IllegalStateException(CANNOT_MODIFY_SINGLETON);
        }
        return validatorMap.put(validator.countryCode, validator);
    }
}
