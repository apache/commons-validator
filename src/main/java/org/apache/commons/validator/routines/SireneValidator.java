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

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;

/**
 * SIRENE (French System Information et Repertoire des Entreprise et des Etablissements) Validator.
 *
 * <p>
 * International Code Designator, ICD : 0002 for SIRENE
 * </p>
 * <p>
 * Issuing Organization :
 * Institut National de la Statistique et des Etudes Economiques, (I.N.S.E.E.),
 * Departement des Repertoires, 18, Bd Adolphe Pinard, 75675 PARIS Cedex 14
 * </p>
 * <p>
 * Structure of Code :
 * </p>
 * <p>
 * 1) Number of characters: 9 characters ("SIREN") 14 " 9+5 ("SIRET"),
 * </p>
 * <p>
 * The 9 character number designates an organization,
 * the 14 character number designates a specific establishment of the organization designated by the first 9 characters.
 * </p>
 * <p>
 * 2) Check digits: 9th and 14th character respectively
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/SIRET_code">Wikipedia - SIRET</a> for more details.
 * </p>
 * @since 1.10.0
 */
public class SireneValidator {

    final Validator formatValidator;

    private static final Validator DEFAULT_FORMAT =
     new Validator(new String[]
       { "^(\\d{9})$"  // SIREN
       , "^(\\d{14})$" // SIRET
     });

    private static final int SIREN_CODE_LEN = 9;
    private static final int SIRET_CODE_LEN = 14;

    /**
     * The format validation class contains regex for SIREN and SIRET.
     */
    public static class Validator {
        final RegexValidator validator;

        /**
         * Creates the format validator
         *
         * @param formats the regex to use to check the format
         */
        public Validator(final String[] formats) {
            this.validator = new RegexValidator(formats);
        }
    }

    /** The singleton instance which uses the default formats */
    private static final SireneValidator DEFAULT_SIRENE_VALIDATOR = new SireneValidator();

    /**
     * Gets the singleton instance of the SIRENE validator using the default formats
     *
     * @return A singleton instance of the validator
     */
    public static SireneValidator getInstance() {
        return DEFAULT_SIRENE_VALIDATOR;
    }

    /**
     * Create a default format validator.
     */
    public SireneValidator() {
        this.formatValidator = DEFAULT_FORMAT;
    }

    /**
     * Validate a SIRENE-ID (SIREN or SIRET)
     *
     * @param code The value validation is being performed on
     * @return <code>true</code> if the value is valid
     */
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        final String id = code.trim();
        if (id.length() != SIREN_CODE_LEN && id.length() != SIRET_CODE_LEN) {
            return false;
        }
        // format check:
        if (!formatValidator.validator.isValid(id)) {
            return false;
        }
        if (id.length() == SIREN_CODE_LEN) {
            return LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(id);
        }
        if (!LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(id.substring(0, SIREN_CODE_LEN))) {
            return false;
        }
        // check SIRET:
        return LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(id);
    }

}
