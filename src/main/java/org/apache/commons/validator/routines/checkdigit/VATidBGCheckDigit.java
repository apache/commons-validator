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
 * Bulgarian VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * Dank dobawena stoinost (DDS)
 * The Bulgarian VAT (Данък върху добавената стойност) number is either 9 (for legal entities)
 * or 10 digits (for physical persons, foreigners and others) long.
 * Each type of number has its own check digit algorithm.
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidBGCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -8667365895223831325L;
    private static final Logger LOG = Logger.getLogger(VATidBGCheckDigit.class.getName());

    /** Singleton Check Digit instance */
    private static final VATidBGCheckDigit INSTANCE = new VATidBGCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    static final int LEN = 9; // with Check Digit

    /**
     * Constructs a modulus Check Digit routine.
     */
    private VATidBGCheckDigit() {
        super(MODULUS_11);
    }

    /** Weighting given to digits depending on their left position */
    // für TINs aus https://github.com/koblas/stdnum-js/blob/main/src/bg/egn.ts modulus: 11,
    private static final int[] POSITION_WEIGHT = { 2, 4, 8, 5, 10, 9, 7, 3, 6 };

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

    /**
     * {@inheritDoc}
function checkLegal(value: string): boolean {
  const [front, check] = strings.splitAt(value, -1);

  let sum = weightedSum(front, {
    modulus: 11,
    weights: [1, 2, 3, 4, 5, 6, 7, 8],
  });

  if (sum === 10) {
    sum = weightedSum(front, {
      modulus: 11,
      weights: [3, 4, 5, 6, 7, 8, 9, 10], // increased weights
    });
  }

  return String(sum % 10) === check;
}

     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
        }
        if (GenericTypeValidator.formatLong(code) == 0) {
            throw new CheckDigitException(CheckDigitException.ZREO_SUM);
        }
        if (code.length() < LEN) {
            int total = 0;
            for (int i = 0; i < code.length(); i++) {
                final int leftPos = i + 1;
                final int charValue = toInt(code.charAt(i), leftPos, -1);
                total += charValue * leftPos;
            }
            if ((total % MODULUS_11) == MODULUS_10) {
                // recalculate with increased weights
//                LOG.info("legal entity total = "+total + " toCheckDigit(total % MODULUS_11):"+(total % MODULUS_11));
                total = 0;
                for (int i = 0; i < code.length(); i++) {
                    final int leftPos = i + 1;
                    final int charValue = toInt(code.charAt(i), leftPos, -1);
                    total += charValue * (2 + leftPos);
                }
                LOG.info("recalculated legal entity total = " + total + " toCheckDigit(total % MODULUS_11):" + (total % MODULUS_11));
            }
            return toCheckDigit(total % MODULUS_11 % MODULUS_10);
        }
        /* zehnstellige ЕГН mit codiertem Geburtsdatum und Geschlecht

         Die ersten 6 Ziffern sind Geburtsdatum (JJMMTT),
         die nächsten 3 sind in der Reihenfolge der Geburt angegeben (+Codierung von Geschlecht)

         Im Monat MM ist das Jahrhundert codiert:
         Für die vor 01.01.1900 geborenen wird dem Monat die Zahl 20 hinzugefügt.
         Für diejenigen, die nach dem 31.12.1999 geboren sind, wird dem Monat wird die Zahl 40 hinzugefügt.

         Die neunte Ziffer der EGN ist bei Männern gerade und bei Frauen ungerade.
         */
        final int m1 = toInt(code.charAt(2), 3, -1);
        final int m0 = toInt(code.charAt(3), 4, -1);
        final int mm = 10 * m1 + m0;
        String born = "19" + code.substring(0, 2);
        if (mm > 20) {  // CHECKSTYLE IGNORE MagicNumber
            born = "18" + code.substring(0, 2);
            if (mm > 40) {  // CHECKSTYLE IGNORE MagicNumber
                born = "20" + code.substring(0, 2);
            }
        }
        final int sexValue = toInt(code.charAt(8), 9, -1);
        if ( (sexValue & 1) == 0 ) {
            LOG.info(code + " is ЕГН for a male person born in year " + born);
        }
        final int calculateModulus = INSTANCE.calculateModulus(code, false);
        return toCheckDigit(calculateModulus % MODULUS_10);
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
            final String cd = code.substring(code.length() - 1);
            String calculatedCd = calculate(code.substring(0, code.length() - 1));
            return cd.equals(calculatedCd);
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
