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

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.checkdigit.CheckDigit;

/**
 * Generic <strong>Code Validation</strong> providing format, minimum/maximum
 * length and {@link CheckDigit} validations.
 * <p>
 * Performs the following validations on a code:
 * <ul>
 *   <li>if the code is null, return null/false as appropriate</li>
 *   <li>trim the input. If the resulting code is empty, return null/false as appropriate</li>
 *   <li>Check the <em>format</em> of the code using a <em>regular expression.</em> (if specified)</li>
 *   <li>Check the <em>minimum</em> and <em>maximum</em> length  (if specified) of the <em>parsed</em> code
 *      (that is, parsed by the <em>regular expression</em>).</li>
 *   <li>Performs {@link CheckDigit} validation on the parsed code (if specified).</li>
 *   <li>The {@link #validate(String)} method returns the trimmed, parsed input (or null if validation failed)</li>
 * </ul>
 * <p>
 * <strong>Note</strong>
 * The {@link #isValid(String)} method will return true if the input passes validation.
 * Since this includes trimming as well as potentially dropping parts of the input,
 * it is possible for a String to pass validation
 * but fail the checkdigit test if passed directly to it (the check digit routines generally don't trim input
 * nor do they generally check the format/length).
 * To be sure that you are passing valid input to a method use {@link #validate(String)} as follows:
 * <pre>
 * Object valid = validator.validate(input);
 * if (valid != null) {
 *    some_method(valid.toString());
 * }
 * </pre>
 * <p>
 * Configure the validator with the appropriate regular expression, minimum/maximum length
 * and {@link CheckDigit} validator and then call one of the two validation
 * methods provided:</p>
 *    <ul>
 *       <li>{@code boolean isValid(code)}</li>
 *       <li>{@code String validate(code)}</li>
 *    </ul>
 * <p>
 * Codes often include <em>format</em> characters - such as hyphens - to make them
 * more easily human-readable. These can be removed prior to length and check digit
 * validation by specifying them as a <em>non-capturing</em> group in the regular
 * expression (that is, use the {@code (?:   )} notation).
 * <br>
 * Or just avoid using parentheses except for the parts you want to capture
 *
 * @since 1.4
 */
public final class CodeValidator implements Serializable {

    private static final long serialVersionUID = 446960910870938233L;

    /** The format regular expression validator. */
    private final RegexValidator regexValidator;

    /** The minimum length of the code. */
    private final int minLength;

    /** The maximum length of the code. */
    private final int maxLength;

    /** The check digit validation routine. */
    private final CheckDigit checkdigit;

    /**
     * Constructs a code validator with a specified regular expression,
     * validator and {@link CheckDigit} validation.
     *
     * @param regexValidator The format regular expression validator
     * @param checkdigit The check digit validation routine.
     */
    public CodeValidator(final RegexValidator regexValidator, final CheckDigit checkdigit) {
        this(regexValidator, -1, -1, checkdigit);
    }

    /**
     * Constructs a code validator with a specified regular expression,
     * validator, length and {@link CheckDigit} validation.
     *
     * @param regexValidator The format regular expression validator
     * @param length The length of the code
     *  (sets the minimum/maximum to the same value)
     * @param checkdigit The check digit validation routine
     */
    public CodeValidator(final RegexValidator regexValidator, final int length, final CheckDigit checkdigit) {
        this(regexValidator, length, length, checkdigit);
    }

    /**
     * Constructs a code validator with a specified regular expression
     * validator, minimum/maximum length and {@link CheckDigit} validation.
     *
     * @param regexValidator The format regular expression validator
     * @param minLength The minimum length of the code
     * @param maxLength The maximum length of the code
     * @param checkdigit The check digit validation routine
     */
    public CodeValidator(final RegexValidator regexValidator, final int minLength, final int maxLength,
            final CheckDigit checkdigit) {
        this.regexValidator = regexValidator;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.checkdigit = checkdigit;
    }

    /**
     * Constructs a code validator with a specified regular
     * expression and {@link CheckDigit}.
     * The RegexValidator validator is created to be case-sensitive
     *
     * @param regex The format regular expression
     * @param checkdigit The check digit validation routine
     */
    public CodeValidator(final String regex, final CheckDigit checkdigit) {
        this(regex, -1, -1, checkdigit);
    }

    /**
     * Constructs a code validator with a specified regular
     * expression, length and {@link CheckDigit}.
     * The RegexValidator validator is created to be case-sensitive
     *
     * @param regex The format regular expression.
     * @param length The length of the code
     *  (sets the minimum/maximum to the same)
     * @param checkdigit The check digit validation routine
     */
    public CodeValidator(final String regex, final int length, final CheckDigit checkdigit) {
        this(regex, length, length, checkdigit);
    }

    /**
     * Constructs a code validator with a specified regular
     * expression, minimum/maximum length and {@link CheckDigit} validation.
     * The RegexValidator validator is created to be case-sensitive
     *
     * @param regex The regular expression
     * @param minLength The minimum length of the code
     * @param maxLength The maximum length of the code
     * @param checkdigit The check digit validation routine
     */
    public CodeValidator(final String regex, final int minLength, final int maxLength,
            final CheckDigit checkdigit) {
        this.regexValidator = GenericValidator.isBlankOrNull(regex) ? null : new RegexValidator(regex);
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.checkdigit = checkdigit;
    }

    /**
     * Gets the check digit validation routine.
     * <p>
     * <strong>N.B.</strong> Optional, if not set no Check Digit
     * validation will be performed on the code.
     *
     * @return The check digit validation routine
     */
    public CheckDigit getCheckDigit() {
        return checkdigit;
    }

    /**
     * Gets the maximum length of the code.
     * <p>
     * <strong>N.B.</strong> Optional, if less than zero the
     * maximum length will not be checked.
     *
     * @return The maximum length of the code or
     * {@code -1} if the code has no maximum length
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Gets the minimum length of the code.
     * <p>
     * <strong>N.B.</strong> Optional, if less than zero the
     * minimum length will not be checked.
     *
     * @return The minimum length of the code or
     * {@code -1} if the code has no minimum length
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * Gets the <em>regular expression</em> validator.
     * <p>
     * <strong>N.B.</strong> Optional, if not set no regular
     * expression validation will be performed on the code.
     *
     * @return The regular expression validator
     */
    public RegexValidator getRegexValidator() {
        return regexValidator;
    }

    /**
     * Validate the code returning either {@code true}
     * or {@code false}.
     * <p>
     * This calls {@link #validate(String)} and returns false
     * if the return value is null, true otherwise.
     * <p>
     * Note that {@link #validate(String)} trims the input
     * and if there is a {@link RegexValidator} it may also
     * change the input as part of the validation.
     *
     * @param input The code to validate
     * @return {@code true} if valid, otherwise
     * {@code false}
     */
    public boolean isValid(final String input) {
        return validate(input) != null;
    }

    /**
     * Validate the code returning either the valid code or
     * {@code null} if invalid.
     * <p>
     * Note that this method trims the input
     * and if there is a {@link RegexValidator} it may also
     * change the input as part of the validation.
     *
     * @param input The code to validate
     * @return The code if valid, otherwise {@code null}
     * if invalid
     */
    public Object validate(final String input) {
        if (input == null) {
            return null;
        }
        String code = input.trim();
        if (code.isEmpty()) {
            return null;
        }
        // validate/reformat using regular expression
        if (regexValidator != null) {
            code = regexValidator.validate(code);
            if (code == null) {
                return null;
            }
        }
        // check the length (must be done after validate as that can change the code)
        if (minLength >= 0 && code.length() < minLength ||
            maxLength >= 0 && code.length() > maxLength) {
            return null;
        }
        // validate the check digit
        if (checkdigit != null && !checkdigit.isValid(code)) {
            return null;
        }
        return code;
    }

}
