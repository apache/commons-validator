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

import java.util.logging.Logger;

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
    private static final Logger LOG = Logger.getLogger(VATidCZCheckDigit.class.getName());

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
    private static final int SPECIAL_MALE_MOD = 20; // male month modifier for RČ
    private static final int FEMALE_MOD = 50; // female month modifier for RČ
    private static final int SPECIAL_FEMALE_MOD = 70; // male month modifier for RČ

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
     * <p>For VATID digits are weighted by their position from left to right.</p>
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
        /* legal entity : LEN with Check Digit = 8, without Check Digit less then 8
C1 Cannot be 9.

A1 = 8*C1 + 7*C2 + 6*C3 + 5*C4 + 4*C5 + 3*C6 + 2*C7
A2= nearest higher multiple of 11
if A1 mod 11 = 0 then A2= A1 + 11
else A2 = CEIL(A1/11, 1) * 11
D = A2 -A1
C8 = D mod 10
        */
            if (code.startsWith(INVALID_START_WITH)) {
                throw new CheckDigitException(INVALID_START_MSG + code);
            }
            final int modulusResult = calculateModulus(code, false);
//            final int charValue = modulusResult == 0 ? MODULUS_11 : (MODULUS_11 - modulusResult) % MODULUS_11;
            final int charValue = modulusResult == 0 ? MODULUS_11 : (MODULUS_11 - modulusResult);
            LOG.info(code + ": legal entity length=" + code.length()
            + " A1 mod 11 aka modulusResult=" + modulusResult
            + " A2 aka charValue=" + charValue);
            return toCheckDigit(charValue % MODULUS_10);
        }
        if (code.length() + 1 == LEN9ICO) {
            LOG.info(code + ": individuals length=" + code.length());
        /* individuals (special cases) : LEN without Check Digit = 8
C1=6

A1 = 8*C2 + 7*C3 + 6*C4 + 5*C5 + 4*C6 + 3*C7 + 2*C8
A2= nearest higher multiple of 11
If A1 mod 11 = 0
then
A2= A1 + 11
else
A2 = CEIL(A1/11, 1) * 11
D = A2 -A1
C9 depends on the difference according to the following table:
D => C9
1 => 8
2 => 7
3 => 6
4 => 5
5 => 4
6 => 3
7 => 2
8 => 1
9 => 0
10 => 9
11 => 8

640903926
A1 = 8*4 + 7*0 + 6*9 + 5*0 + 4*3 + 3*9 + 2*2 = 129
A1 mod 11 <> 0
A2 = CEIL(129/11, 1) * 11 = 132
D = 132 - 129 = 3
C9 = 6
 */
            final int modulusResult = calculateModulus6(code, false);
            final int charValue = modulusResult == 0 ? MODULUS_11 : (MODULUS_11 - modulusResult) % MODULUS_11;
            LOG.info(code + ": individuals length=" + code.length()
            + " A1 mod 11 aka modulusResult=" + modulusResult
            + " D aka charValue=" + charValue);

            return toCheckDigit(DIFFTABLE[charValue - 1]);
        }
        /* For individuals (other case): LEN without Check Digit = 9
         * The number (with Check Digit) represented by C1C2C3C4C5C6C7C8C9C10 must be divided by 11 without remainder.
C10 :
A1 = C1*C2 + C3*C4 + C5*C6 + C7*C8 + C9*C10
A1 must be divisible by 11 with no remainder.
wie C7 C8 C9 zustande kommen wird nicht verraten


Numbers given out after January 1st 1954 should have 10 digits.
The number includes the birth date of the person and their gender.

    const [yy, mm, dd] = strings.splitAt(value, 2, 4, 6);
    // Gender encode in month, Y2K incoded as well
//Month of birth,
//    More than 1 and less than 12 for men or
//    More than 21 and less than 32 for men or
//    More than 51 and less than 62 for women or
//    More than 71 and less 82 for women.
    const mon = (parseInt(mm, 10) % 50) % 20;
    let year = parseInt(yy, 10) + 1900;
    if (value.length === 9) {
      if (year > 1980) {
        year -= 100;
      }
      if (year > 1953) {
        return { isValid: false, error: new exceptions.InvalidComponent() };
      }
    } else if (year < 1954) {
      year += 100;
    }
    if (!isValidDate(String(year), String(mon), dd, true)) { ...
         */
        //return calculateRodneCislo(code);
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
        int cd = includesCheckDigit ? toInt(code.charAt(9), 10, -1) : 0;  // CHECKSTYLE IGNORE MagicNumber
        final int sum = 10 * (c1 + c3 + c5 + c7 + c9) + c2 + c4 + c6 + c8 + cd;
        if (sum == 0) {
            throw new CheckDigitException(CheckDigitException.ZREO_SUM);
        }
        final int yy = 10 * c1 + c2;
        final int yyborn = yy >= BORN_IN_1900_IND ? 1900 + yy : 2000 + yy;  // CHECKSTYLE IGNORE MagicNumber
        final int mm = 10 * c3 + c4;
        int mmborn = mm;
        final String sex = mm > FEMALE_MOD ? "female" : "male";
        if (mm > SPECIAL_MALE_MOD) {
            if (mm > FEMALE_MOD) {
                if (mm > SPECIAL_FEMALE_MOD) {
                    mmborn -= SPECIAL_FEMALE_MOD;
                } else {
                    mmborn -= FEMALE_MOD;
                }
            } else {
                mmborn -= SPECIAL_MALE_MOD;
            }
        }
        final int ddborn = 10 * c5 + c6;
        LOG.info(code + ": individual (" + sex + ") born=" + yyborn + "/" + mmborn + "/" + ddborn
            + " sum=" + sum + " sum%11=" + sum % MODULUS_11);
        DateValidator dateValidator = new DateValidator();
        String date = String.format("%02d", mmborn) + "/" + String.format("%02d", ddborn) + "/" + yyborn;
        if (dateValidator.validate(date, "MM/dd/yyyy") == null) {
            throw new CheckDigitException("Invalid date " + date + " - invalid Rodné číslo (RČ) " + code);
        }
        if (includesCheckDigit) {
            if (sum % MODULUS_11 != 0) {
                throw new CheckDigitException("Invalid code");
            }
        } else {
            cd = MODULUS_11 - sum % MODULUS_11;
        }
        return cd;
    }

    protected int calculateModulus6(final String code, final boolean includesCheckDigit) throws CheckDigitException {
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
            throw new CheckDigitException(CheckDigitException.ZREO_SUM);
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
        if (code.length() == LEN10ICO) try {
            int cd = calculateRodneCislo(code, true);
            return cd == Character.getNumericValue(code.charAt(code.length() - 1));
        } catch (final CheckDigitException ex) {
            return false;
        }
        if (code.length() == LEN9ICO) try {
            final int modulusResult = calculateModulus6(code, true);
            final int charValue = modulusResult == 0 ? MODULUS_11 : (MODULUS_11 - modulusResult) % MODULUS_11;
            LOG.info(code + ": individuals length=" + code.length()
            + " A1 mod 11 aka modulusResult=" + modulusResult
            + " D aka charValue=" + charValue);
            return DIFFTABLE[charValue - 1] == Character.getNumericValue(code.charAt(code.length() - 1));
        } catch (final CheckDigitException ex) {
            return false;
        }
        if (code.length() <= LEN) try {
            if (code.startsWith(INVALID_START_WITH)) {
                throw new CheckDigitException(INVALID_START_MSG + code);
            }
            final int modulusResult = MODULUS_11 - INSTANCE.calculateModulus(code, true);
            LOG.info(code + ": legal entity length=" + code.length()
            + " modulusResult=" + modulusResult + " checkdigit=" + modulusResult % MODULUS_10);
            return (modulusResult % MODULUS_10) == Character.getNumericValue(code.charAt(code.length() - 1));
        } catch (final CheckDigitException ex) {
            return false;
        }
        return false;
    }

}
