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

import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;
import org.apache.commons.validator.routines.checkdigit.ISBN10CheckDigit;

/**
 * <b>ISBN-10</b> and <b>ISBN-13</b> Code Validation.
 * <p>
 * This validator validates the code is either a valid ISBN-10
 * (using a {@link CodeValidator} with the {@link ISBN10CheckDigit})
 * or a valid ISBN-13 code (using a {@link CodeValidator} with the
 * the {@link EAN13CheckDigit} routine).
 * <p>
 * The <code>validate()</code> methods return the ISBN code with formatting
 * characters removed if valid or {@code null} if invalid.
 * <p>
 * This validator also provides the facility to convert ISBN-10 codes to
 * ISBN-13 if the <code>convert</code> property is {@code true}.
 * <p>
 * From 1st January 2007 the book industry will start to use a new 13 digit
 * ISBN number (rather than this 10 digit ISBN number). ISBN-13 codes are
 * <a href="https://en.wikipedia.org/wiki/European_Article_Number">EAN</a>
 * codes, for more information see:</p>
 *
 * <ul>
 *   <li><a href="https://en.wikipedia.org/wiki/ISBN">Wikipedia - International
 *       Standard Book Number (ISBN)</a>.</li>
 *   <li>EAN - see
 *       <a href="https://en.wikipedia.org/wiki/European_Article_Number">Wikipedia -
 *       European Article Number</a>.</li>
 *   <li><a href="http://www.isbn.org/standards/home/isbn/transition.asp">ISBN-13
 *       Transition details</a>.</li>
 * </ul>
 *
 * <p>ISBN-13s are either prefixed with 978 or 979. 978 prefixes are only assigned
 * to the ISBN agency. 979 prefixes may be assigned to ISBNs or ISMNs
 * (<a href="https://www.ismn-international.org/">International
 * Standard Music Numbers</a>).
 * <ul>
 *     <li>979-0 are assigned to the ISMN agency</li>
 *     <li>979-10, 979-11, 979-12 are assigned to the ISBN agency</li>
 * </ul>
 * All other 979 prefixed EAN-13 numbers have not yet been assigned to an agency. The
 * validator validates all 13 digit codes with 978 or 979 prefixes.
 *
 * @since 1.4
 */
public class ISBNValidator implements Serializable {

    private static final int ISBN_10_LEN = 10;

    private static final long serialVersionUID = 4319515687976420405L;

    private static final String SEP = "(?:\\-|\\s)";
    private static final String GROUP = "(\\d{1,5})";
    private static final String PUBLISHER = "(\\d{1,7})";
    private static final String TITLE = "(\\d{1,6})";

    /**
     * ISBN-10 consists of 4 groups of numbers separated by either dashes (-)
     * or spaces.  The first group is 1-5 characters, second 1-7, third 1-6,
     * and fourth is 1 digit or an X.
     */
    static final String ISBN10_REGEX = "^(?:(\\d{9}[0-9X])|(?:" + GROUP + SEP + PUBLISHER + SEP + TITLE + SEP + "([0-9X])))$";

    /**
     * ISBN-13 consists of 5 groups of numbers separated by either dashes (-)
     * or spaces.  The first group is 978 or 979, the second group is
     * 1-5 characters, third 1-7, fourth 1-6, and fifth is 1 digit.
     */
    static final String ISBN13_REGEX = "^(978|979)(?:(\\d{10})|(?:" + SEP + GROUP + SEP + PUBLISHER + SEP + TITLE + SEP + "([0-9])))$";

    /** ISBN Code Validator (which converts ISBN-10 codes to ISBN-13 */
    private static final ISBNValidator ISBN_VALIDATOR = new ISBNValidator();

    /** ISBN Code Validator (which converts ISBN-10 codes to ISBN-13 */
    private static final ISBNValidator ISBN_VALIDATOR_NO_CONVERT = new ISBNValidator(false);

    /**
     * Gets the singleton instance of the ISBN validator which
     * converts ISBN-10 codes to ISBN-13.
     *
     * @return A singleton instance of the ISBN validator.
     */
    public static ISBNValidator getInstance() {
        return ISBN_VALIDATOR;
    }

    /**
     * Gets the singleton instance of the ISBN validator specifying
     * whether ISBN-10 codes should be converted to ISBN-13.
     *
     * @param convert {@code true} if valid ISBN-10 codes
     * should be converted to ISBN-13 codes or {@code false}
     * if valid ISBN-10 codes should be returned unchanged.
     * @return A singleton instance of the ISBN validator.
     */
    public static ISBNValidator getInstance(final boolean convert) {
        return convert ? ISBN_VALIDATOR : ISBN_VALIDATOR_NO_CONVERT;
    }

    /** ISBN-10 Code Validator */
    private final CodeValidator isbn10Validator = new CodeValidator(ISBN10_REGEX, 10, ISBN10CheckDigit.ISBN10_CHECK_DIGIT);

    /** ISBN-13 Code Validator */
    private final CodeValidator isbn13Validator = new CodeValidator(ISBN13_REGEX, 13, EAN13CheckDigit.EAN13_CHECK_DIGIT);

    private final boolean convert;

    /**
     * Constructs an ISBN validator which converts ISBN-10 codes
     * to ISBN-13.
     */
    public ISBNValidator() {
        this(true);
    }

    /**
     * Constructs an ISBN validator indicating whether
     * ISBN-10 codes should be converted to ISBN-13.
     *
     * @param convert {@code true} if valid ISBN-10 codes
     * should be converted to ISBN-13 codes or {@code false}
     * if valid ISBN-10 codes should be returned unchanged.
     */
    public ISBNValidator(final boolean convert) {
        this.convert = convert;
    }

    /**
     * Convert an ISBN-10 code to an ISBN-13 code.
     * <p>
     * This method requires a valid ISBN-10 with NO formatting
     * characters.
     *
     * @param isbn10 The ISBN-10 code to convert
     * @return A converted ISBN-13 code or {@code null}
     * if the ISBN-10 code is not valid
     */
    public String convertToISBN13(final String isbn10) {

        if (isbn10 == null) {
            return null;
        }

        final String input = isbn10.trim();
        if (input.length() != ISBN_10_LEN) {
            throw new IllegalArgumentException("Invalid length " + input.length() + " for '" + input + "'");
        }

        // Calculate the new ISBN-13 code (drop the original checkdigit)
        String isbn13 = "978" + input.substring(0, ISBN_10_LEN - 1);
        try {
            final String checkDigit = isbn13Validator.getCheckDigit().calculate(isbn13);
            isbn13 += checkDigit;
            return isbn13;
        } catch (final CheckDigitException e) {
            throw new IllegalArgumentException("Check digit error for '" + input + "' - " + e.getMessage());
        }

    }

    /**
     * Check the code is either a valid ISBN-10 or ISBN-13 code.
     *
     * @param code The code to validate.
     * @return {@code true} if a valid ISBN-10 or
     * ISBN-13 code, otherwise {@code false}.
     */
    public boolean isValid(final String code) {
        return isValidISBN13(code) || isValidISBN10(code);
    }

    /**
     * Check the code is a valid ISBN-10 code.
     *
     * @param code The code to validate.
     * @return {@code true} if a valid ISBN-10
     * code, otherwise {@code false}.
     */
    public boolean isValidISBN10(final String code) {
        return isbn10Validator.isValid(code);
    }

    /**
     * Check the code is a valid ISBN-13 code.
     *
     * @param code The code to validate.
     * @return {@code true} if a valid ISBN-13
     * code, otherwise {@code false}.
     */
    public boolean isValidISBN13(final String code) {
        return isbn13Validator.isValid(code);
    }

    /**
     * Check the code is either a valid ISBN-10 or ISBN-13 code.
     * <p>
     * If valid, this method returns the ISBN code with
     * formatting characters removed (i.e. space or hyphen).
     * <p>
     * Converts an ISBN-10 codes to ISBN-13 if
     * <code>convertToISBN13</code> is {@code true}.
     *
     * @param code The code to validate.
     * @return A valid ISBN code if valid, otherwise {@code null}.
     */
    public String validate(final String code) {
        String result = validateISBN13(code);
        if (result == null) {
            result = validateISBN10(code);
            if (result != null && convert) {
                result = convertToISBN13(result);
            }
        }
        return result;
    }

    /**
     * Check the code is a valid ISBN-10 code.
     * <p>
     * If valid, this method returns the ISBN-10 code with
     * formatting characters removed (i.e. space or hyphen).
     *
     * @param code The code to validate.
     * @return A valid ISBN-10 code if valid,
     * otherwise {@code null}.
     */
    public String validateISBN10(final String code) {
        final Object result = isbn10Validator.validate(code);
        return result == null ? null : result.toString();
    }

    /**
     * Check the code is a valid ISBN-13 code.
     * <p>
     * If valid, this method returns the ISBN-13 code with
     * formatting characters removed (i.e. space or hyphen).
     *
     * @param code The code to validate.
     * @return A valid ISBN-13 code if valid,
     * otherwise {@code null}.
     */
    public String validateISBN13(final String code) {
        final Object result = isbn13Validator.validate(code);
        return result == null ? null : result.toString();
    }

}
