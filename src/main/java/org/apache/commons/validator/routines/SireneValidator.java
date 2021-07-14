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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;

/**
 * SIRENE (FR System Information et Repertoire des Entreprise et des Etablissements) Validator.
 *
 * <p>
 * International Code Designator, ICD : 0002 for SIRENE
 * <br>
 * Issuing Organization :
 * Institut National de la Statistique et des Etudes Economiques, (I.N.S.E.E.),
 * Departement des Repertoires, 18, Bd Adolphe Pinard, 75675 PARIS Cedex 14
 * </p>
 * <p>
 * Structure of Code :
 * <br>
 * 1) Number of characters: 9 characters ("SIREN") 14 " 9+5 ("SIRET"), 
 * <br>
 * The 9 character number designates an organization, 
 * the 14 character number designates a specific establishment of the organization designated by the first 9 characters. 
 * <br>
 * 2) Check digits: 9th & 14th character respectively
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/SIRET_code">Wikipedia - SIRET</a>
 * for more details.
 * </p>
 */
public class SireneValidator {

    private static final Log LOG = LogFactory.getLog(SireneValidator.class);

    final Validator formatValidator;

    private static final Validator DEFAULT_FORMAT =
     new Validator( new String[]
       { "^(\\d{9})$"  // SIREN
       , "^(\\d{14})$" // SIRET
     });

    private static final int SIREN_CODE_LEN = 9;
    private static final int SIRET_CODE_LEN = 14;

    /**
     * The validation class
     */
    public static class Validator {
        final RegexValidator validator;

        /**
         * Creates the format validator
         * 
         * @param formats the regex's to use to check the format
         */
        public Validator(String[] formats) {
            this.validator = new RegexValidator(formats);
        }
    }

    /** The singleton instance which uses the default formats */
    public static final SireneValidator DEFAULT_SIRENE_VALIDATOR = new SireneValidator();

    /**
     * Return a singleton instance of the SIRENE validator using the default formats
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
     * @param id The value validation is being performed on
     * @return <code>true</code> if the value is valid
     */
    public boolean isValid(String id) {

        id = id.trim();
        if (id == null || (id.length() != SIREN_CODE_LEN && id.length() != SIRET_CODE_LEN)) {
            return false;
        }
        
        // format check:
        if(!formatValidator.validator.isValid(id)) return false;

        if(id.length() == SIREN_CODE_LEN) {
            return LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(id);
        }
        if(!LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(id.substring(0, SIREN_CODE_LEN))) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(id + " is SIRET, SIREN check digit at 9 is NOT valid.");
            }
            return false;
        }
        
        return LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(id);
    }

}
