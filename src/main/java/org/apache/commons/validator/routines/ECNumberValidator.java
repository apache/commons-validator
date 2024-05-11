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

import org.apache.commons.validator.routines.checkdigit.ECNumberCheckDigit;

/**
 * <b>EC number</b> validation.
 *
 * <p>
 * The European Community number (EC number) is a unique seven-digit identifier
 * that is assigned to chemical substances.
 * For example, the EC number of arsenic is 231-148-6:
 * </p>
 *
 * <p>
 * Check digit calculation is based on <i>modulus 11</i> with digits being weighted
 * based on their position (from left to right).
 * </p>
 *
 * <p>
 * For further information see
 *  <a href="https://en.wikipedia.org/wiki/European_Community_number">Wikipedia - EC number</a>.
 * </p>
 *
 * @since 1.9.0
 */
public final class ECNumberValidator implements Serializable {

    private static final long serialVersionUID = 6676297914206256712L;

	/** Singleton instance */
    private static final ECNumberValidator INSTANCE = new ECNumberValidator();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the EC Number validator.
     */
    public static ECNumberValidator getInstance() {
        return INSTANCE;
    }

    private static final String GROUP1 = "([2-9]\\d{2})"; // EINECS numbers starts with 2xx-xxx-C
    private static final String GROUP2 = "(\\d{3})";
    private static final String DASH = "(?:\\-)";

    /**
     * EC number consists of 3 groups of numbers separated dashes (-).
     * Example: dexamethasone is 200-003-9
     */
    static final String EC_REGEX = "^(?:" + GROUP1 + DASH + GROUP2 + DASH + "(\\d))$";

    static final CodeValidator VALIDATOR = new CodeValidator(EC_REGEX, ECNumberCheckDigit.LEN, ECNumberCheckDigit.getInstance());

    /**
     * Constructs a validator.
     */
    private ECNumberValidator() { }

    /**
     * Tests whether the code is a valid EC number.
     *
     * @param code The code to validate.
     * @return {@code true} if a EC number, otherwise {@code false}.
     */
    public boolean isValid(final String code) {
        return VALIDATOR.isValid(code);
    }

    /**
     * Checks the code is valid EC number.
     *
     * @param code The code to validate.
     * @return An EC number code with dashes removed if valid, otherwise {@code null}.
     */
    public Object validate(final String code) {
        final Object validate = VALIDATOR.validate(code);
        if (validate != null) {
            return VALIDATOR.isValid(code) ? validate : null;
        }
        return validate;
    }

}
