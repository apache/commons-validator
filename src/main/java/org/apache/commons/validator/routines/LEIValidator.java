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

import org.apache.commons.validator.routines.checkdigit.Modulus97CheckDigit;

/**
 * <strong>LEI</strong> (Legal Entity Identifier) validation.
 *
 * <p>
 * LEI codes are 20 character alphanumeric codes defined in ISO 17442 which identify legal entities participating in
 * financial transactions.
 * </p>
 *
 * <p>
 * An LEI consists of a 4 character prefix allocated to the Local Operating Unit (LOU) that issued the LEI, 14
 * alphanumeric characters that are specific to the entity, and two numeric check digits calculated using ISO/IEC
 * 7064, MOD 97-10 (see {@link Modulus97CheckDigit}). Only upper case letters and digits are allowed. Earlier
 * versions of the standard reserved characters 5 and 6 as {@code 00}; this is no longer required by ISO 17442-1:2020
 * and is therefore not enforced.
 * </p>
 *
 * <p>
 * Note: the validator checks the format and the check digits only. It does not check whether the LEI has actually been
 * issued, or whether it is still active; that information is available from the
 * <a href="https://www.gleif.org/">Global Legal Entity Identifier Foundation (GLEIF)</a>.
 * </p>
 *
 * <p>
 * See <a href="https://www.gleif.org/en/about-lei/iso-17442-the-lei-code-structure">GLEIF - ISO 17442: The LEI Code
 * Structure</a> and <a href="https://en.wikipedia.org/wiki/Legal_Entity_Identifier">Wikipedia - Legal Entity
 * Identifier</a> for more details.
 * </p>
 *
 * @since 1.11.1
 */
public class LEIValidator implements Serializable {

    private static final long serialVersionUID = -8013046731917548173L;

    private static final String LEI_REGEX = "([A-Z0-9]{18}[0-9]{2})";

    private static final int LEI_LEN = 20;

    private static final CodeValidator VALIDATOR = new CodeValidator(LEI_REGEX, LEI_LEN, Modulus97CheckDigit.MODULUS_97_CHECK_DIGIT);

    /** LEI Code Validator. */
    private static final LEIValidator LEI_VALIDATOR = new LEIValidator();

    /**
     * Gets the singleton instance of the LEI validator.
     *
     * @return A singleton instance of the LEI validator.
     */
    public static LEIValidator getInstance() {
        return LEI_VALIDATOR;
    }

    private LEIValidator() {
    }

    /**
     * Tests whether the code is a valid LEI code after any transformation by the validate routine.
     *
     * @param code The code to validate.
     * @return {@code true} if a valid LEI code, otherwise {@code false}.
     */
    public boolean isValid(final String code) {
        return validate(code) != null;
    }

    /**
     * Checks the code is a valid LEI code.
     *
     * @param code The code to validate.
     * @return A valid LEI code if valid, otherwise {@code null}.
     */
    public Object validate(final String code) {
        return VALIDATOR.validate(code);
    }
}
