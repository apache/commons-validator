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

import org.apache.commons.validator.routines.checkdigit.ECIndexNumberCheckDigit;

/**
 * <b>EC index number</b> validation.
 *
 * <p>
 * The European Community index number is a unique nine-digit identifier
 * that is assigned to hazardous chemical substances.
 * <br>
 * For example, the EC index number of lithium is <code>003-001-00-4</code>
 * </p>
 *
 * <p>
 * The Index number for each substance is in the form of a digit sequence of the type
 * <code>ABC-RST-VW-Y</code>. 
 * <br>
 * <code>ABC</code> corresponds to the atomic number of the most characteristic element
 * or the most characteristic organic group in the molecule.
 * <br>
 * <code>RST</code> is the consecutive number of the substance in the series <code>ABC</code>.
 * <br>
 * <code>VW</code> denotes the form in which the substance is produced or placed on the market.
 * <br>
 * <code>Y</code> is the check-digit. Check digit calculation is based on <i>modulus 11</i>
 * with digits being weighted based on their position (from left to right).
 * </p>
 *
 * <p>
 * For further information see
 *  <a href="https://eur-lex.europa.eu/eli/reg/2008/1272/oj?locale=en">CLP_Regulation - ANNEX VI 1.1.1.1.Index numbers</a>
 * </p>
 *
 * @since 1.9.0
 */
public final class ECIndexNumberValidator implements Serializable {

    private static final long serialVersionUID = 6434158079879082191L;

    /** Singleton instance */
    private static final ECIndexNumberValidator INSTANCE = new ECIndexNumberValidator();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the EC Index Number validator.
     */
    public static ECIndexNumberValidator getInstance() {
        return INSTANCE;
    }

    private static final String GROUP = "(\\d{3})";
    private static final String DASH = "(?:\\-)";

    /**
     * EC index number consists of 3 groups of numbers and a check digit separated dashes (-).
     * Example: lithium is 003-001-00-4, HCl is 017-002-01-X
     */
    static final String ECI_REGEX = "^(?:" + GROUP + DASH + GROUP + DASH + "(\\d{2})" + DASH + "([0-9X]))$";

    static final CodeValidator VALIDATOR = new CodeValidator(ECI_REGEX, ECIndexNumberCheckDigit.LEN, 
        ECIndexNumberCheckDigit.getInstance());

    /**
     * Constructs a validator.
     */
    private ECIndexNumberValidator() { }

    /**
     * Tests whether the code is a valid EC index number.
     *
     * @param code The code to validate.
     * @return {@code true} if a EC index number, otherwise {@code false}.
     */
    public boolean isValid(final String code) {
        return VALIDATOR.isValid(code);
    }

    /**
     * Checks the code is valid EC index number.
     *
     * @param code The code to validate.
     * @return An EC index number code with dashes removed if valid, otherwise {@code null}.
     */
    public Object validate(final String code) {
        final Object validate = VALIDATOR.validate(code);
        if (validate != null) {
            return VALIDATOR.isValid(code) ? validate : null;
        }
        return validate;
    }

}
