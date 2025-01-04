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
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.DateValidator;

/**
 * Czech VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * daňové identifiační číslo (DIČ)
 * </p>
 * <p>
 * See <a href="https://cs.wikipedia.org/wiki/Da%C5%88ov%C3%A9_identifika%C4%8Dn%C3%AD_%C4%8D%C3%ADslo">Wikipedia - cs</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidCZCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -2419923920463044513L;
    private static final Log LOG = LogFactory.getLog(VATidCZCheckDigit.class);

    /** Singleton Check Digit instance */
    private static final VATidCZCheckDigit INSTANCE = new VATidCZCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    /*
     * there ate three length
     * 8 for legal entities (standard DIČ),
     * 9 and 10 for physical persons, IČO : Identifikační číslo osoby
     */
    static final int LEN = 8; // with Check Digit
    static final int LEN9ICO = 9;
    static final int LEN10ICO = 10;

    private static final int[] DIFFTABLE = { 8, 7, 6, 5, 4, 3, 2, 1, 0, 9, 8 };
    private static final String INVALID_START_WITH = "9";
    private static final String INVALID_START_MSG = "Invalid code for legal entity, first char cannot be '9' :";
    private static final int BORN_IN_1900_IND = 54; // year indicator for RČ
    private static final int FEMALE_MOD = 50; // add to month when female for RČ
    private static final int SPECIAL_MOD = 20; // add to month in special cases for RČ

    /**
     * Constructs a modulus 11 Check Digit routine.
     */
    private VATidCZCheckDigit() {
        super(MODULUS_11);
    }

    /**
     * Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.
     *
     * <p>For VATID digits are weighted by their position.</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        final int weight = rightPos == 1 ? 0 : rightPos;
        return charValue * weight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException("Code is missing");
        }
        if (code.length() < LEN) { // legal entity
            if (code.startsWith(INVALID_START_WITH)) {
                throw new CheckDigitException(INVALID_START_MSG + code);
            }
            final int modulusResult = calculateModulus(code, false);
            final int charValue = modulusResult == 0 ? MODULUS_11 : (MODULUS_11 - modulusResult);
            return toCheckDigit(charValue % MODULUS_10);
        }
        if (code.length() + 1 == LEN9ICO) { // individuals (special cases)
            final int modulusResult = calculateModulus6(code, false);
            final int charValue = modulusResult == 0 ? MODULUS_11 : (MODULUS_11 - modulusResult);
            return toCheckDigit(DIFFTABLE[charValue - 1]);
        }

        // RČ : Numbers given out after January 1st 1954 should have 10 digits.
        // The number includes the birth date of the person and their gender.
        return toCheckDigit(calculateRodneCislo(code, false));
    }

    /*
     * Czech Republic uses a system inherited from former Czechoslovakia called Birth Number (Czech: Rodné číslo (RČ))
     *  The form is YYXXDD/SSSC, where XX=MM (month of birth) for male (numbers 01-12)
     *  and XX=MM+50 for female (numbers 51-62),
     *  SSS is a serial number separating persons born on the same date
     *  and C a checksum (full number must be divisible by 11).
     *  Remark: The form before 1 Januar 1954 is YYXXDD/SSS without a checksum.
     */
    private int calculateRodneCislo(final String code, final boolean includesCheckDigit) throws CheckDigitException {
        final int c1 = toInt(code.charAt(0), 1, -1);
        final int c2 = toInt(code.charAt(1), 2, -1);
        final int c3 = toInt(code.charAt(2), 3, -1);
        final int c4 = toInt(code.charAt(3), 4, -1);
        final int c5 = toInt(code.charAt(4), 5, -1);
        final int c6 = toInt(code.charAt(5), 6, -1);
        final int c7 = toInt(code.charAt(6), 7, -1);
        final int c8 = toInt(code.charAt(7), 8, -1);
        final int c9 = toInt(code.charAt(8), 9, -1);
        int cd = includesCheckDigit ? toInt(code.charAt(9), 10, -1) : -1;  // CHECKSTYLE IGNORE MagicNumber
        final int sum = 10 * (c1 + c3 + c5 + c7 + c9)
            + c2 + c4 + c6 + c8 + (cd == -1 ? 0 : cd == 0 ? 10 : cd);  // CHECKSTYLE IGNORE MagicNumber
        if (sum == 0) {
            throw new CheckDigitException(CheckDigitException.ZERO_SUM);
        }
        final int yy = 10 * c1 + c2;
        final int yyborn = yy >= BORN_IN_1900_IND ? 1900 + yy : 2000 + yy;  // CHECKSTYLE IGNORE MagicNumber
        final int mm = 10 * c3 + c4;
        final int mmborn = mm % FEMALE_MOD % SPECIAL_MOD;
        final int ddborn = 10 * c5 + c6;
        if (LOG.isDebugEnabled()) {
            final String sex = mm > FEMALE_MOD ? "female" : "male";
            LOG.debug(code + ": individual (" + sex + ") born=" + yyborn + "/" + mmborn + "/" + ddborn);
        }
        final DateValidator dateValidator = new DateValidator();
        final String date = String.format("%02d", mmborn) + "/" + String.format("%02d", ddborn) + "/" + yyborn;
        if (dateValidator.validate(date, "MM/dd/yyyy") == null) {
            throw new CheckDigitException("Invalid date " + date + " - invalid Rodné číslo (RČ) " + code);
        }
        if (includesCheckDigit) {
            if (sum % MODULUS_11 != 0) {
                throw new CheckDigitException("Invalid code");
            }
        } else {
            cd = (MODULUS_11 - sum % MODULUS_11) % MODULUS_10;
        }
        return cd;
    }

    private int calculateModulus6(final String code, final boolean includesCheckDigit) throws CheckDigitException {
        if (code.charAt(0) != '6') {
            throw new CheckDigitException("Invalid code, first char not '6' :" + code);
        }
        int total = 0;
        for (int i = 1; i < code.length(); i++) {
            final int lth = code.length() + (includesCheckDigit ? 0 : 1);
            final int leftPos = i + 1;
            final int rightPos = lth - i;
            final int charValue = toInt(code.charAt(i), leftPos, rightPos);
            total += weightedValue(charValue, leftPos, rightPos);
        }
        if (total == 0) {
            throw new CheckDigitException(CheckDigitException.ZERO_SUM);
        }
        return total % MODULUS_11;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }

        if (code.length() == LEN10ICO) {
            try {
                final int cd = calculateRodneCislo(code, true);
                return cd == Character.getNumericValue(code.charAt(code.length() - 1));
            } catch (final CheckDigitException ex) {
                return false;
            }
        }
        if (code.length() == LEN9ICO) {
            try {
                final int modulusResult = calculateModulus6(code, true);
                final int charValue = modulusResult == 0 ? MODULUS_11 : (MODULUS_11 - modulusResult);
                return DIFFTABLE[charValue - 1] == Character.getNumericValue(code.charAt(code.length() - 1));
            } catch (final CheckDigitException ex) {
                return false;
            }
        }

        if (code.length() > LEN) {
            return false;
        }
        try {
            if (code.startsWith(INVALID_START_WITH)) {
                throw new CheckDigitException(INVALID_START_MSG + code);
            }
            final int modulusResult = MODULUS_11 - INSTANCE.calculateModulus(code, true);
            return (modulusResult % MODULUS_10) == Character.getNumericValue(code.charAt(code.length() - 1));
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
