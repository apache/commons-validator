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

import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;
import org.apache.commons.validator.routines.checkdigit.ISSNCheckDigit;

/**
 * International Standard Serial Number (ISSN)
 * is an eight-digit serial number used to
 * uniquely identify a serial publication.
 * <pre>
 * The format is:
 *
 * ISSN dddd-dddC
 * where:
 * d = decimal digit (0-9)
 * C = checksum (0-9 or X)
 *
 * The checksum is formed by adding the first 7 digits multiplied by
 * the position in the entire number (counting from the right).
 *
 * For example, abcd-efg would be 8a + 7b + 6c + 5d + 4e +3f +2g.
 * The check digit is modulus 11, where the value 10 is represented by 'X'
 * For example:
 * ISSN 0317-8471
 * ISSN 1050-124X
 *
 * This class strips off the 'ISSN ' prefix if it is present before passing
 * the remainder to the checksum routine.
 *
 * </pre>
 * <p>
 * Note: the {@link #isValid(String)} and {@link #validate(String)} methods strip off any leading
 * or trailing spaces before doing the validation.
 * To ensure that only a valid code (without 'ISSN ' prefix) is passed to a method,
 * use the following code:
 * </p>
 * <pre>
 * Object valid = validator.validate(input);
 * if (valid != null) {
 *    some_method(valid.toString());
 * }
 * </pre>
 * @since 1.5.0
 */
public class ISSNValidator implements Serializable {

    private static final long serialVersionUID = 4319515687976420405L;

    private static final String ISSN_REGEX = "(?:ISSN )?(\\d{4})-(\\d{3}[0-9X])$"; // We don't include the '-' in the code, so it is 8 chars

    private static final int ISSN_LEN = 8;

    private static final String ISSN_PREFIX = "977";

    private static final String EAN_ISSN_REGEX = "^(977)(?:(\\d{10}))$";

    private static final int EAN_ISSN_LEN = 13;

    private static final CodeValidator VALIDATOR = new CodeValidator(ISSN_REGEX, ISSN_LEN, ISSNCheckDigit.ISSN_CHECK_DIGIT);

    private static final CodeValidator EAN_VALIDATOR = new CodeValidator(EAN_ISSN_REGEX, EAN_ISSN_LEN, EAN13CheckDigit.EAN13_CHECK_DIGIT);

    /** ISSN Code Validator. */
    private static final ISSNValidator ISSN_VALIDATOR = new ISSNValidator();

    /**
     * Gets the singleton instance of the ISSN validator.
     *
     * @return A singleton instance of the ISSN validator.
     */
    public static ISSNValidator getInstance() {
        return ISSN_VALIDATOR;
    }

    /**
     * Constructs a new instance.
     */
    public ISSNValidator() {
        // empty
    }

    /**
     * Converts an ISSN code to an EAN-13 code.
     * <p>
     * This method requires a valid ISSN code.
     * It may contain a leading 'ISSN ' prefix,
     * as the input is passed through the {@link #validate(String)}
     * method.
     * </p>
     *
     * @param issn The ISSN code to convert
     * @param suffix the two digit suffix, for example, "00"
     * @return A converted EAN-13 code or {@code null}
     * if the input ISSN code is not valid
     */
    public String convertToEAN13(final String issn, final String suffix) {
        if (suffix == null || !suffix.matches("\\d\\d")) {
            throw new IllegalArgumentException("Suffix must be two digits: '" + suffix + "'");
        }
        final Object result = validate(issn);
        if (result == null) {
            return null;
        }
        // Calculate the new EAN-13 code
        final String input = result.toString();
        String ean13 = ISSN_PREFIX + input.substring(0, input.length() - 1) + suffix;
        try {
            final String checkDigit = EAN13CheckDigit.EAN13_CHECK_DIGIT.calculate(ean13);
            ean13 += checkDigit;
            return ean13;
        } catch (final CheckDigitException e) { // Should not happen
            throw new IllegalArgumentException("Check digit error for '" + ean13 + "' - " + e.getMessage());
        }
    }

    /**
     * Extracts an ISSN code from an ISSN-EAN-13 code.
     * <p>
     * This method requires a valid ISSN-EAN-13 code with NO formatting
     * characters.
     * That is a 13 digit EAN-13 code with the '977' prefix.
     * </p>
     *
     * @param ean13 The ISSN code to convert
     * @return A valid ISSN code or {@code null}
     * if the input ISSN EAN-13 code is not valid
     * @since 1.7
     */
    public String extractFromEAN13(final String ean13) {
        String input = ean13.trim();
        if (input.length() != EAN_ISSN_LEN) {
            throw new IllegalArgumentException("Invalid length " + input.length() + " for '" + input + "'");
        }
        if (!input.startsWith(ISSN_PREFIX)) {
            throw new IllegalArgumentException("Prefix must be " + ISSN_PREFIX + " to contain an ISSN: '" + ean13 + "'");
        }
        final Object result = validateEan(input);
        if (result == null) {
            return null;
        }
        // Calculate the ISSN code
        input = result.toString();
        try {
            //CHECKSTYLE:OFF: MagicNumber
            final String issnBase = input.substring(3, 10); // TODO: how to derive these
            //CHECKSTYLE:ON: MagicNumber
            final String checkDigit = ISSNCheckDigit.ISSN_CHECK_DIGIT.calculate(issnBase);
            return issnBase + checkDigit;
        } catch (final CheckDigitException e) { // Should not happen
            throw new IllegalArgumentException("Check digit error for '" + ean13 + "' - " + e.getMessage());
        }
    }

    /**
     * Tests whether the code is a valid ISSN code after any transformation
     * by the validate routine.
     *
     * @param code The code to validate.
     * @return {@code true} if a valid ISSN
     * code, otherwise {@code false}.
     */
    public boolean isValid(final String code) {
        return VALIDATOR.isValid(code);
    }

    /**
     * Checks the code is valid ISSN code.
     * <p>
     * If valid, this method returns the ISSN code with
     * the 'ISSN ' prefix removed (if it was present)
     * </p>
     *
     * @param code The code to validate.
     * @return A valid ISSN code if valid, otherwise {@code null}.
     */
    public Object validate(final String code) {
        return VALIDATOR.validate(code);
    }

    /**
     * Checks the code is a valid EAN code.
     * <p>
     * If valid, this method returns the EAN code
     * </p>
     *
     * @param code The code to validate.
     * @return A valid EAN code if valid, otherwise {@code null}.
     * @since 1.7
     */
    public Object validateEan(final String code) {
        return EAN_VALIDATOR.validate(code);
    }
}
