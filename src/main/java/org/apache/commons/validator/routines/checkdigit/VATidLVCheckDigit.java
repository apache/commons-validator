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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.DateValidator;

/**
 * Latvian VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * pievienotāsvērtības nodokļa reģistrācijas numurs (PVN)
 * - there are different calculations for legal persons and natural persons
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidLVCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -4171562329195981385L;
    private static final Log LOG = LogFactory.getLog(VATidLVCheckDigit.class);

    /** Singleton Check Digit instance */
    private static final VATidLVCheckDigit INSTANCE = new VATidLVCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    private static final int LEN = 11;
    /**
     * Three is a legal person indicator.
     * Codes starting with x > THREE are given to legal persons.
     */
    private static final int THREE = 3;
    private static final String INVALID_CODE_NEGATIVE = "Invalid code, check digit cannot be negative";

    /** Weighting given to digits depending on their left position */
    private static final int[] POSITION_WEIGHT = { 9, 1, 4, 8, 3, 10, 2, 5, 7, 6 };

    /* TIN Weighting given to digits depending on their left position
    c1 * 1 + c2 * 6 + c3 * 3 + c4 * 7 + c5 * 9 + c6 * 10 + c7 * 5 + c8 * 8 + c9 * 4 + c10 * 2;
     9*C5   1*C1   4*C9   8*C8   3*C3   10*C6   2*C10  5*C7   7*C4   6*C2
die POSITION_WEIGHT für TIN ist eine permutation von POSITION_WEIGHT für firmen
A1 = 9*C1 + 1*C2 + 4*C3 + 8*C4 + 3*C5 + 10*C6 + 2*C7 + 5*C8 + 7*C9 + 6*C10
     */
//    private static final int[] TIN_POSITION_WEIGHT = { 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };

    /**
     * Constructs a modulus 11 Check Digit routine.
     */
    private VATidLVCheckDigit() {
        super(MODULUS_11);
    }

    /**
     * Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.
     *
     * <p>For VATID digits are weighted by their position from left to right.</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        if (leftPos - 1 >= POSITION_WEIGHT.length) return 0;
        final int weight = POSITION_WEIGHT[(leftPos - 1)];
        return charValue * weight;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to calculate <em>Check Digit</em> for legal persons and natural persons.
     * </p>
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
        }
        if (GenericTypeValidator.formatLong(code) == 0) {
            throw new CheckDigitException(CheckDigitException.ZREO_SUM);
        }
        final int c1 = toInt(code.charAt(0), 1, -1);
        if (c1 > THREE) {
            // Legal persons
            final int modulusResult = calculateModulus(code, false);
            final int charValue = 3 - modulusResult;
            if (charValue == -1) {
                throw new CheckDigitException(INVALID_CODE_NEGATIVE);
            }
            return toCheckDigit(charValue > -1 ? charValue : charValue + MODULUS_11);
        }
        // else natural person
        return toCheckDigit(calculateNMIN(code, true));
    }

    /**
     * Calculate a <em>Check Digit</em> for a natural person code.
     *
     * @param code without checkdigit
     * @param invalidDateException, true for testing
     * @return check digit
     * @throws CheckDigitException
     */
    private int calculateNMIN(final String code, boolean invalidDateException) throws CheckDigitException {
        if (code.length() != LEN - 1) {
            throw new CheckDigitException("Invalid code, code.length()=" + code.length());
        }
        final int c1 = toInt(code.charAt(0), 1, -1);
        final int c2 = toInt(code.charAt(1), 2, -1);
        final int dd = 10 * c1 + c2;
        if (dd > 0 && dd <= 31) { // CHECKSTYLE IGNORE MagicNumber
            String mmborn = code.substring(2, 4); // CHECKSTYLE IGNORE MagicNumber
            final int centuryInd = toInt(code.charAt(6), 7, -1);
            String yyborn = code.substring(4, 6); // CHECKSTYLE IGNORE MagicNumber
            if (centuryInd == 0) {
                yyborn = "18" + yyborn;
            } else if (centuryInd == 1) {
                yyborn = "19" + yyborn;
            } else if (centuryInd == 2) {
                yyborn = "20" + yyborn;
            } else {
                yyborn = "??" + yyborn;
            }
            DateValidator dateValidator = new DateValidator();
            String date = mmborn + "/" + String.format("%02d", dd) + "/" + yyborn;
            if (dateValidator.validate(date, "MM/dd/yyyy") == null) {
                if (invalidDateException) {
                     throw new CheckDigitException("Invalid date " + date + " - Invalid NMIN " + code);
                } else {
                    LOG.warn("Invalid century indicator " + centuryInd + " or date " + date + " - Invalid NMIN " + code);
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(code + " is NMIN (TIN) for a person born " + date);
            }
        }
        int cd = vRule1(code);
        if (cd == -1) {
            throw new CheckDigitException(INVALID_CODE_NEGATIVE);
        }
        return cd;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to validate vatin codes for legal persons and natural persons.
     * </p>
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        if (code.length() != LEN) {
            return false;
        }
        try {
            final int c1 = toInt(code.charAt(0), 1, -1);
            if (c1 > THREE) {
                final int modulusResult = INSTANCE.calculateModulus(code, true);
                final int charValue = 3 - modulusResult;
                if (charValue == -1) {
                    throw new CheckDigitException(INVALID_CODE_NEGATIVE);
                }
                final int cd = charValue > -1 ? charValue : charValue + MODULUS_11;
                return cd == Character.getNumericValue(code.charAt(code.length() - 1));
            }
            int ccd = calculateNMIN(code.substring(0, LEN - 1), true);
            return ccd == Character.getNumericValue(code.charAt(code.length() - 1));
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

    private int vRule1(String code) {
        int c1 = Character.getNumericValue(code.charAt(0)); // CHECKSTYLE IGNORE MagicNumber
        int c2 = Character.getNumericValue(code.charAt(1)); // CHECKSTYLE IGNORE MagicNumber
        int c3 = Character.getNumericValue(code.charAt(2)); // CHECKSTYLE IGNORE MagicNumber
        int c4 = Character.getNumericValue(code.charAt(3)); // CHECKSTYLE IGNORE MagicNumber
        int c5 = Character.getNumericValue(code.charAt(4)); // CHECKSTYLE IGNORE MagicNumber
        int c6 = Character.getNumericValue(code.charAt(5)); // CHECKSTYLE IGNORE MagicNumber
        int c7 = Character.getNumericValue(code.charAt(6)); // CHECKSTYLE IGNORE MagicNumber
        int c8 = Character.getNumericValue(code.charAt(7)); // CHECKSTYLE IGNORE MagicNumber
        int c9 = Character.getNumericValue(code.charAt(8)); // CHECKSTYLE IGNORE MagicNumber
        int c10 = Character.getNumericValue(code.charAt(9)); // CHECKSTYLE IGNORE MagicNumber
        int sum = c1 * 1 + c2 * 6 + c3 * 3 + c4 * 7 + c5 * 9 + c6 * 10 + c7 * 5 + c8 * 8 + c9 * 4 + c10 * 2; // CHECKSTYLE IGNORE MagicNumber
        int remainderBy11 = sum % MODULUS_11;
        if (1 - remainderBy11 < -1)
          return 1 - remainderBy11 + MODULUS_11;
        return 1 - remainderBy11;
      }

}
