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

import org.apache.commons.validator.routines.checkdigit.CASNumberCheckDigit;

/**
 * <b>CAS Registry Number</b> (or <b>Chemical Abstracts Service</b> (CAS RN)) validation.
 *
 * <p>
 * CAS Numbers are unique identification numbers used
 * to identify chemical substance described in the open scientific literature.
 * </p>
 *
 * <p>
 * Check digit calculation is based on <i>modulus 10</i> with digits being weighted
 * based on their position (from right to left).
 * </p>
 *
 * <p>
 * For further information see
 *  <a href="https://en.wikipedia.org/wiki/CAS_Registry_Number">Wikipedia - CAS Registry Number</a>.
 * </p>
 *
 * @since 1.9.0
 */
public final class CASNumberValidator implements Serializable {

    private static final long serialVersionUID = -5750098586109080748L;

	/** Singleton instance */
    private static final CASNumberValidator INSTANCE = new CASNumberValidator();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the CAS Number validator.
     */
    public static CASNumberValidator getInstance() {
        return INSTANCE;
    }

    private static final String GROUP1 = "([1-9]\\d{1,6})";
    private static final String DASH = "(?:\\-)";

    /**
     * CAS number consists of 3 groups of numbers separated dashes (-).
     * First group has 2 to 7 digits.
     * Example: water is 7732-18-5
     */
    static final String CAS_REGEX = "^(?:" + GROUP1 + DASH + "(\\d{2})" + DASH + "(\\d))$";

    static final CodeValidator VALIDATOR = new CodeValidator(CAS_REGEX, 
        CASNumberCheckDigit.MIN_LEN, CASNumberCheckDigit.MAX_LEN, 
        CASNumberCheckDigit.getInstance());

    /**
     * Constructs a validator.
     */
    private CASNumberValidator() { }

    /**
     * Tests whether the code is a valid CAS number.
     *
     * @param code The code to validate.
     * @return {@code true} if a CAS number, otherwise {@code false}.
     */
    public boolean isValid(final String code) {
        return VALIDATOR.isValid(code);
    }

    /**
     * Checks the code is valid CAS number.
     *
     * @param code The code to validate.
     * @return A CAS number code with dashes removed if valid, otherwise {@code null}.
     */
    public Object validate(final String code) {
        final Object validate = VALIDATOR.validate(code);
        if (validate != null) {
            return VALIDATOR.isValid(code) ? validate : null;
        }
        return validate;
    }

}
