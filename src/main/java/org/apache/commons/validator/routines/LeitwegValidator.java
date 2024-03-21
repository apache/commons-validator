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
import org.apache.commons.validator.routines.checkdigit.Modulus97CheckDigit;

/**
 * Leitweg Validator.
 * <p>
 * In e-invoices to German public entities there is a specific identification schema called Leitweg-ID. 
 *  The schema can be found in the most recent version of the 
 *  <A HREF="https://en.wikipedia.org/wiki/ISO/IEC_6523">ISO 6523 ICD</A> list. 
 *  The prefix of this specfic ICD identifier is 0204. 
 * </p>
 * <p>
 * The spec "Formatspezifikation Leitweg-ID 2.0.1" (in deu) can be found in 
 * <A HREF="https://www.xoev.de/">KoSIT</A>.
 * 
 *  In short: there are three parts separated by "-" HYPHEN-MINUS
 * <br>
 * - a mandatory numeric general starting with two chars region id, "02" for Hamburg, "99" for federal institutons
 * <br>
 * - an optional alphanumeric detail 
 * <br>
 * - two mandatory check digits calculated according to modulus 97 algorithm
 * <br>
 * 
 * Example: <code>992-90009-96</code> for         Deutsche Bahn AG
 */
public class LeitwegValidator {

    private static final Log LOG = LogFactory.getLog(LeitwegValidator.class);

    private final Validator formatValidator;

    private static final char MINUS = '\u002D'; // '-' Separator
    
    private static final Validator DEFAULT_FORMAT =
     new Validator("^(01|02|03|04|05|06|07|08|09|10|11|12|13|14|16|99)((\\d)((\\d{2})(\\d{3}|\\d{4}|\\d{7})?)?)?"
       +"(-([A-Za-z0-9]{1,30}))?" // optional alphanumeric detail
       +"-(\\d{2})$");            // two mandatory check digits

    /*
     * in theory the shortest Leitweg-ID has a minimal general part and check digits  
     *  <code>99-92</code>
     */
    private static final int MIN_CODE_LEN = 5;
    
    /* 
     * sum of
     * maximal general part length: 12
     * maximal detail part length: 30
     * check digits length: 2
     */
    private static final int MAX_CODE_LEN = 44;

    /**
     * The validation class
     */
    public static class Validator {
        final RegexValidator validator;

        /**
         * Creates the format validator
         * 
         * @param format the regex to use to check the format
         */
        public Validator(String format) {
            this.validator = new RegexValidator(format);
        }
    }

    /** The singleton instance which uses the default formats */
    public static final LeitwegValidator DEFAULT_LEITWEG_VALIDATOR = new LeitwegValidator();

    /**
     * Return a singleton instance of the validator using the default formats
     *
     * @return A singleton instance of the validator
     */
    public static LeitwegValidator getInstance() {
        return DEFAULT_LEITWEG_VALIDATOR;
    }

    /**
     * Create a default format validator.
     */
    public LeitwegValidator() {
        this.formatValidator = DEFAULT_FORMAT;
    }

    public RegexValidator getFormatValidator() {
        return formatValidator.validator;
    }

    /**
     * Validate a Leitweg-ID
     *
     * @param id The value validation is being performed on
     * @return <code>true</code> if the value is valid
     */
    public boolean isValid(String id) {

        id = id.trim();
        if (id == null || id.length() > MAX_CODE_LEN || id.length() < MIN_CODE_LEN) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("format length error for "+id);
            }
            return false;
        }
        
        // format check:
        // der RegexValidator kann mehrere pattern prüfen!!!
        if(!formatValidator.validator.isValid(id)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("format "+id+" is NOT valid.");
            }
            return false;
        }
        return Modulus97CheckDigit.MOD_CHECK_DIGIT.isValid(removeMinus(id));
    }

    private String removeMinus(String id) {
        return id.replace(""+MINUS, "");
    }
    
    /**
     * Check the id is a valid MOD 97-10 id.
     * <p>
     * If valid, this method returns the Leitweg-ID with
     * formatting characters removed (i.e. hyphen).
     *
     * @param code The Leitweg-ID to validate.
     * @return A Leitweg-ID without hyphen if valid, otherwise <code>null</code>.
     * 
     * <p>
     * <b>Note</b>
     * The return is not valid Leitweg-ID because HYPHEN-MINUS is part of the format.
     */
    public String validate(String code) {
        if(isValid(code)) return removeMinus(code);
        return null;
    }
}
