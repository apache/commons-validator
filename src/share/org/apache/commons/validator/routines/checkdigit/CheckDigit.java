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
package org.apache.commons.validator.routines.checkdigit;

import org.apache.commons.validator.ISBNValidator;

/**
 * <b>Check Digit</b> calculation and validation.
 * <p>
 * The logic for validating check digits has previously been
 * embedded within the logic for specific code validation, which
 * includes other validations such as verifying the format
 * or length of a code. {@link CheckDigit} provides for separating out
 * the check digit calculation logic enabling it to be more easily
 * tested and reused.
 * <p>
 * Although Commons Validator is primarily concerned with validation,
 * {@link CheckDigit} also defines behaviour for calculating/generating check
 * digits, since it makes sense that users will want to (re-)use the
 * same logic for both. The {@link ISBNValidator} makes specific use
 * of this feature by providing the facility to validate ISBN-10 codes
 * and then convert them to the new ISBN-13 standard.
 * <p>
 * <h3>Implementations</h3>
 * The following check digit implementations are provided as standard:
 * <ul>
 *   <li>{@link ModulusCheckDigit} - an abstract class which provides the logic
 *       for <i>modulus</i> check digit calculation/validation.</li>
 *   <li>{@link EAN13CheckDigit} - check digit calculation/validation for
 *       numeric EAN codes (based on the standard EAN-13).</li>
 *   <li>{@link ISBN10CheckDigit} - check digit calculation/validation for
 *       numeric ISBN-10 codes (the new ISBN-13 code is actually an EAN-13
 *       code and uses the same check digit calculation).</li>
 *   <li>{@link ISBNCheckDigit} - check digit calculation/validation for
 *       both ISBN-10 and ISBN-13 codes.</li>
 *   <li>{@link LuhnCheckDigit} - Luhn check digit calculation/validation
 *       commonly used by credit card numbers.</li>
 * </ul>
 *
 * @version $Revision$ $Date$
 * @since Validator 1.4
 */
public interface CheckDigit {

    /**
     * Calculate the <i>Check Digit</i> for a code.
     *
     * @param code The code to calculate the Check Digit for.
     * @return The calculated Check Digit
     * @throws CheckDigitException if an error occurs.
     */
    public char calculate(String code) throws CheckDigitException;

    /**
     * Validate the check digit for the code.
     *
     * @param code The code to validate.
     * @return <code>true</code> if the check digit is valid, otherwise
     * <code>false</code>.
     */
    public boolean isValid(String code);

}
