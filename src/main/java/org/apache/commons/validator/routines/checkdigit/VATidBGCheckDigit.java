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
 * Bulgarian VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * Dank dobawena stoinost (DDS)
 * The Bulgarian VAT (Данък върху добавената стойност) number is either 9 (for legal entities)
 * or 10 digits long (ЕГН for physical persons, foreigners and others).
 * Each type of number has its own check digit algorithm.
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia VATIN</a>
 * and <a href="https://en.wikipedia.org/wiki/Unique_citizenship_number">ЕГН (civil number)</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidBGCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -8667365895223831325L;
    private static final Log LOG = LogFactory.getLog(VATidBGCheckDigit.class);

    /** Singleton Check Digit instance */
    private static final VATidBGCheckDigit INSTANCE = new VATidBGCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    /**
     * There are three length
     * <ul>
     * <li>9 for legal entities (standard DDS),</li>
     * <li>10 for physical persons (civil number),</li>
     * <li>13 for legal entities with branch number (not used for VATIN),</li>
     * </ul>
     */
    static final int LEN = 9; // with Check Digit
    static final int LENCN = 10; // with Check Digit
    static final int LEN13 = 13; // with Check Digit

    /**
     * Constructs a modulus Check Digit routine.
     */
    private VATidBGCheckDigit() {
        super(MODULUS_11);
    }

    /** Weighting for physical persons given to digits depending on their left position */
    private static final int[] POSITION_WEIGHT = { 2, 4, 8, 5, 10, 9, 7, 3, 6 };
    /** Weighting for DDS with branches */
    private static final int[] BRANCH_WEIGHT = { 2, 7, 3, 5 };

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
        final int weight = POSITION_WEIGHT[(leftPos - 1)];
        return charValue * weight;
    }

    private int calculateDDStotal(final String code, final boolean recalculate) throws CheckDigitException {
        final boolean standard = code.length() < LEN;
        int total = 0;
        for (int i = standard ? 0 : LEN - 1; i < code.length(); i++) {
            final int leftPos = i + 1;
            final int charValue = toInt(code.charAt(i), leftPos, -1);
            if (standard) {
                total += charValue * (recalculate ? 2 + leftPos : leftPos);
            } else {
                final int weight = BRANCH_WEIGHT[(leftPos - LEN)];
                total += charValue * (recalculate ? 2 + weight : weight);
            }
        }
        return total;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
        }
        if (GenericTypeValidator.formatLong(code) == 0) {
            throw new CheckDigitException(CheckDigitException.ZERO_SUM);
        }
        if (code.length() + 1 == LEN) { // DDS for legal entities
            int total = calculateDDStotal(code, false);
            if ((total % MODULUS_11) == MODULUS_10) {
                // recalculate with increased weights
                total = calculateDDStotal(code, true);
            }
            return toCheckDigit(total % MODULUS_11 % MODULUS_10);
        } else if (code.length() + 1 == LEN13) {
            if (!isValid(code.substring(0, LEN))) {
                throw new CheckDigitException("Invalid DDC subcode " + code.substring(0, LEN));
            }
            int total = calculateDDStotal(code, false);
            if ((total % MODULUS_11) == MODULUS_10) {
                // recalculate with increased weights
                total = calculateDDStotal(code, true);
            }
            return toCheckDigit(total % MODULUS_11 % MODULUS_10);
        } else if (code.length() + 1 == LENCN) {
            // ЕГН for physical persons, foreigners and others (aka civil number)
            checkCivilNumber(code);
            final int calculateModulus = INSTANCE.calculateModulus(code, false);
            return toCheckDigit(calculateModulus % MODULUS_10);
        }
        throw new CheckDigitException("Invalid DDC " + code);
    }

    private static final int BORN_BEFORE_1900_MOD = 20; // month modifier
    private static final int BORN_AFTER_2000_MOD = 40; // month modifier

    /**
     * Check ЕГН (civil number), which contains a coded birth date.
     * <p>
     * The initial six digits correspond to the birth date.
     * The first two digits are the last two digits of the year,
     * and the last two digits are the day of the month.
     * For people born between 1900 and 1999 the middle digits are the month number: YYMMDD.
     * For people born before 1900, 20 is added to the month: YY M+20 DD.
     * For people born from 2000, 40 is added to the month: YY M+40 DD.
     * </p>
     * @param code
     * @return true for valid coded date
     * @throws CheckDigitException
     */
    private boolean checkCivilNumber(final String code) throws CheckDigitException {
        final int m1 = toInt(code.charAt(2), 3, -1);
        final int m0 = toInt(code.charAt(3), 4, -1);
        final int mm = 10 * m1 + m0;
        String yyborn = "19" + code.substring(0, 2);
        int mmborn = mm;
        if (mm > BORN_BEFORE_1900_MOD) {
            yyborn = "18" + code.substring(0, 2);
            mmborn = mm - BORN_BEFORE_1900_MOD;
            if (mm > BORN_AFTER_2000_MOD) {
                yyborn = "20" + code.substring(0, 2);
                mmborn = mm - BORN_AFTER_2000_MOD;
            }
        }
        final DateValidator dateValidator = new DateValidator();
        final String date = String.format("%02d", mmborn) + "/" + code.substring(4, 6) + "/" + yyborn; // CHECKSTYLE IGNORE MagicNumber
        if (dateValidator.validate(date, "MM/dd/yyyy") == null) {
            throw new CheckDigitException("Invalid date " + date + " - Invalid DDC " + code);
        }

        // The next three digits designate the birth order number,
        // the third digit being even for males and odd for females.
        if (LOG.isDebugEnabled()) {
            final int sexValue = toInt(code.charAt(8), 9, -1);
            final String sex = (sexValue & 1) == 0 ? "male" : "female";
            LOG.debug(code + " is ЕГН for a " + sex + " person born " + date);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        if (code.length() < LEN) {
            return false;
        }
        try {
            final String cd = calculate(code.substring(0, code.length() - 1));
            return code.endsWith(cd);
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
