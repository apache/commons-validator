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

import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.GenericValidator;

/**
 * United Kingdom value added tax registration number
 * <p>
 * The VAT number can either be a 9-digit standard number,
 * a 12-digit standard number followed by a 3-digit branch identifier,
 * a 5-digit number for government departments (first two digits are GD) or
 * a 5-digit number for health authorities (first two digits are HA).
 * The 9-digit variants use a weighted checksum.
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia - VAT IN</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidGBCheckDigit extends Modulus97CheckDigit {

    private static final long serialVersionUID = -1297016213259512985L;
    private static final Logger LOG = Logger.getLogger(VATidGBCheckDigit.class.getName());

    /** Singleton Check Digit instance */
    private static final VATidGBCheckDigit INSTANCE = new VATidGBCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    static final int LEN = 9; // with Check Digit

    /**
     * Constructs a Check Digit routine.
     */
    private VATidGBCheckDigit() {
        super();
    }

    protected int toInt(final char character, final int leftPos, final int rightPos) throws CheckDigitException {
        if (Character.isDigit(character)) {
            return Character.getNumericValue(character);
        }
        throw new CheckDigitException("Invalid Character[" + leftPos + "] = '" + character + "'");
    }
    @Override
    protected long calculateModulus(final String code, final boolean includesCheckDigit) throws CheckDigitException {
        String reformattedCode = includesCheckDigit ? code.substring(0, code.length() - CHECKDIGIT_LEN) : code;
        long total = 0;
        for (int i = 0; i < reformattedCode.length(); i++) {
            final int lth = reformattedCode.length() + 1; // + (includesCheckDigit ? 0 : 1);
            final int leftPos = i + 1;
            final int rightPos = lth - i;
            final int charValue = toInt(code.charAt(i), leftPos, rightPos);
//            total += weightedValue(charValue, leftPos, rightPos);
            final int weight = rightPos == 1 ? 0 : rightPos;
            total += charValue * weight;
//            if (code.equals("110305878")) {
//                LOG.info(code + " i=" + i + " charValue=" + charValue + " weight=" + weight + " total=" + total);
//            }
//            LOG.info(code + " i="+i + " charValue="+charValue + " weight="+weight + " total="+total);
        }
        if (total == 0) {
            throw new CheckDigitException(CheckDigitException.ZREO_SUM);
        }
        return total;
    }
    /**
     * {@inheritDoc}
[C8 C9] // Gewichtung ist rightPos
R1 =  (8*C1 + 7*C2 + 6*C3 + 5*C4 + 4*C5 + 3*C6 + 2*C7 + C8C9) modulo 97
R2 = ((8*C1 + 7*C2 + 6*C3 + 5*C4 + 4*C5 + 3*C6 + 2*C7 + C8C9) + 55) modulo 97
Either R1 or R2 must equal to zero

434031494
R1 = (8*4 + 7*3 + 6*4 + 5*0 + 4*3 + 3*1 + 2*4 + 94) modulo 97
= (32 + 21 + 24 + 0 + 12 + 3 + 8 + 94) modulo 97
= 194 modulo 97 = 0
R2 = ((8*4 + 7*3 + 6*4 + 5*0 + 4*3 + 3*1
+ 2*4 + 94) + 55) modulo 97
= ((32 + 21 + 24 + 0 + 12 + 3 + 8 + 94) + 55) modulo 97
= (194 + 55) modulo 97 = 55
R1 equals to zero and R2 equals to 55
and therefore one result equals to zero
and therefore the VAT number is
syntactically correct. The VAT number
does not fall into the restricted range of MOD97 (R1) and is therefore valid.
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
        }
        if (code.length() < MIN_CODE_LEN) {
            throw new CheckDigitException("Invalid Code length=" + code.length());
        }
// N(1-7)={0000001-0019999,1000000-9999999}
/*
cd = 97 - total % 97
total % 97 = 97 - cd

cd = 97 - (total+55) % 97
cd = 97 - (total % 97 + 55 % 97)
cd = 97 - (total % 97 + 55)
cd = (97-55) - (total % 97)
cd = (42) - (total % 97)
cd = 139 - (total % 97)

 */
        long mr = calculateModulus(code, false);
        int modulusResult = (int) (mr % MODULUS_97);
//        long mod97 = GenericTypeValidator.formatLong(code) % MODULUS_97;
//        LOG.info(code +" mod97="+mod97 + " mr="+mr + " cd9755="+(MODULUS_97 - (mr+55) % MODULUS_97) + " "+(139-modulusResult)
//                + " OR cd97="+(MODULUS_97 - modulusResult) );
        if ((MODULUS_97 - (mr + 55) % MODULUS_97) == (139 - modulusResult)) { // CHECKSTYLE IGNORE MagicNumber
            return toCheckDigit(139 - modulusResult); // CHECKSTYLE IGNORE MagicNumber
        }
        // The check digits are calculated as 97 - MOD 97
        return toCheckDigit(modulusResult == 0 ? 0 : MODULUS_97 - modulusResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        if (code.length() < MIN_CODE_LEN) {
            return false;
        }

        String check = code.substring(code.length() - CHECKDIGIT_LEN);
        Integer icheck = GenericTypeValidator.formatInt(check);
        if (icheck == null) {
            return false;
        }
        try {
            long mr = calculateModulus(code, true);
            int modulusResult = (int) (mr % MODULUS_97);
//            if(modulusResult==0) {
//                throw new CheckDigitException("Invalid code, sum is zero");
//            }
            LOG.info(code + " cd=" + (MODULUS_97 - (mr + 55) % MODULUS_97) + " " + (139 - modulusResult) // CHECKSTYLE IGNORE MagicNumber
                    + " OR " + (MODULUS_97 - modulusResult) );
            int cdValue = modulusResult == 0 ? 0 : MODULUS_97 - modulusResult;
            if ((MODULUS_97 - (mr + 55) % MODULUS_97) == (139 - modulusResult)) { // CHECKSTYLE IGNORE MagicNumber
                cdValue = (139 - modulusResult); // CHECKSTYLE IGNORE MagicNumber
            }
            return icheck.intValue() == cdValue;
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
