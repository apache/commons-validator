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

import java.io.Serializable;

import org.apache.commons.validator.GenericValidator;

/**
 * Combined <strong>ISBN-10</strong> / <strong>ISBN-13</strong> Check Digit calculation/validation.
 * <p>
 * This implementation validates/calculates ISBN check digits
 * based on the length of the code passed to it - delegating
 * either to the {@link ISBNCheckDigit#ISBN10_CHECK_DIGIT} or the
 * {@link ISBNCheckDigit#ISBN13_CHECK_DIGIT} routines to perform the actual
 * validation/calculation.
 * <p>
 * <strong>N.B.</strong> From 1st January 2007 the book industry will start to use a new 13 digit
 * ISBN number (rather than this 10 digit ISBN number) which uses the EAN-13 / UPC
 * standard.
 *
 * @since 1.4
 */
public final class ISBNCheckDigit extends AbstractCheckDigit implements Serializable {

    private static final long serialVersionUID = 1391849166205184558L;

    /** Singleton ISBN-10 Check Digit instance */
    public static final CheckDigit ISBN10_CHECK_DIGIT = ISBN10CheckDigit.ISBN10_CHECK_DIGIT;

    /** Singleton ISBN-13 Check Digit instance */
    public static final CheckDigit ISBN13_CHECK_DIGIT = EAN13CheckDigit.EAN13_CHECK_DIGIT;

    /** Singleton combined ISBN-10 / ISBN-13 Check Digit instance */
    public static final CheckDigit ISBN_CHECK_DIGIT = new ISBNCheckDigit();

    /**
     * Calculate an ISBN-10 or ISBN-13 check digit, depending
     * on the length of the code.
     * <p>
     * If the length of the code is 9, it is treated as an ISBN-10
     * code or if the length of the code is 12, it is treated as an ISBN-13
     * code.
     *
     * @param code The ISBN code to validate (should have a length of
     * 9 or 12)
     * @return The ISBN-10 check digit if the length is 9 or an ISBN-13
     * check digit if the length is 12.
     * @throws CheckDigitException if the code is missing, or an invalid
     * length (i.e. not 9 or 12) or if there is an error calculating the
     * check digit.
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException("ISBN Code is missing");
        }
        if (code.length() == 9) { // CHECKSTYLE IGNORE MagicNumber
            return ISBN10_CHECK_DIGIT.calculate(code);
        }
        if (code.length() == 12) { // CHECKSTYLE IGNORE MagicNumber
            return ISBN13_CHECK_DIGIT.calculate(code);
        }
        throw new CheckDigitException("Invalid ISBN Length = " + code.length());
    }

    /**
     * <p>Validate an ISBN-10 or ISBN-13 check digit, depending
     * on the length of the code.</p>
     * <p>
     * If the length of the code is 10, it is treated as an ISBN-10
     * code or ff the length of the code is 13, it is treated as an ISBN-13
     * code.
     *
     * @param code The ISBN code to validate (should have a length of
     * 10 or 13)
     * @return {@code true} if the code has a length of 10 and is
     * a valid ISBN-10 check digit or the code has a length of 13 and is
     * a valid ISBN-13 check digit - otherwise {@code false}.
     */
    @Override
    public boolean isValid(final String code) {
        if (code == null) {
            return false;
        }
        if (code.length() == 10) { // CHECKSTYLE IGNORE MagicNumber
            return ISBN10_CHECK_DIGIT.isValid(code);
        }
        if (code.length() == 13) { // CHECKSTYLE IGNORE MagicNumber
            return ISBN13_CHECK_DIGIT.isValid(code);
        }
        return false;
    }

}
