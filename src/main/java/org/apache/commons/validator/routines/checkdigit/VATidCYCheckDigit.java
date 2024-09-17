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
 * Cypriot VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * Arithmos Egrafis FPA (ΦΠΑ)
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
/*
function CYVATCheckDigit (vatnumber) {

  // Checks the check digits of a Cypriot VAT number.

  // Not allowed to start with '12'
  if (Number(vatnumber.slice(0,2) == 12)) return false;

  // Extract the next digit and multiply by the counter.
  var total = 0;
  for (var i = 0; i < 8; i++) {
    var temp = Number(vatnumber.charAt(i));
    if (i % 2 == 0) {
      switch (temp) {
        case 0: temp = 1; break;
        case 1: temp = 0; break;
        case 2: temp = 5; break;
        case 3: temp = 7; break;
        case 4: temp = 9; break;
        default: temp = temp*2 + 3;
      }
    }
    total += temp;
  }

  // Establish check digit using modulus 26, and translate to char. equivalent.
  total = total % 26;
  total = String.fromCharCode(total+65);

  // Check to see if the check digit given is correct
  if (total == vatnumber.substr (8,1))
    return true;
  else
    return false;
}

 */
public final class VATidCYCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -844683638838062022L;
    private static final Logger LOG = Logger.getLogger(VATidCYCheckDigit.class.getName());

    /** Singleton Check Digit instance */
    private static final VATidCYCheckDigit INSTANCE = new VATidCYCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    static final int LEN = 9; // with Check Digit
    static final int MODULUS_26 = 26;

    /** Weighting given to digits depending on their left position */
/*
const weights: Record<string, number> = {
  '0': 1,
  '1': 0,
  '2': 5,
  '3': 7,
  '4': 9,
  '5': 13, == temp*2 + 3
  '6': 15,
  '7': 17,
  '8': 19,
  '9': 21,
};
 */
//    private static final int[] POSITION_WEIGHT = { 1, 0, 5, 7, 9 };
    private static final int[] POSITION_WEIGHT = { 1, 0, 5, 7, 9, 13, 15, 17, 19, 21 };
    private static final String CHECK_CHARACTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Constructs a modulus 11 Check Digit routine.
     */
    private VATidCYCheckDigit() {
        super(MODULUS_26);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Override to get the non numeric check character
     * </p>
     */
    @Override
    protected String toCheckDigit(final int charValue) throws CheckDigitException {
        if (charValue >= 0 && charValue <= CHECK_CHARACTER.length() - 1) {
            return "" + CHECK_CHARACTER.charAt(charValue);
        }
        throw new CheckDigitException("Invalid Check Digit Value =" + charValue);
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
        if (leftPos % 2 == 0) return charValue;
//        LOG.info("charValue="+charValue + " leftPos="+leftPos + " res="+POSITION_WEIGHT[charValue]);
        return POSITION_WEIGHT[charValue];
//        final int weight = POSITION_WEIGHT[(leftPos - 1) % POSITION_WEIGHT.length];
//        LOG.info("charValue="+charValue + " leftPos="+leftPos + " res="+(leftPos<=POSITION_WEIGHT.length ? weight : (leftPos-1) * 2 + 3));
//        return leftPos * weight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
        }
        if (code.startsWith("12")) {
            throw new CheckDigitException("Invalid code, not allowed to start with '12' :" + code);
        }
        if (code.length() >= LEN && GenericTypeValidator.formatLong(code.substring(0, LEN)) == 0) {
            throw new CheckDigitException(CheckDigitException.ZREO_SUM);
        }
        final int modulusResult = calculateModulus(code, false);
        LOG.info(code + " calculateModulus mod 26=" + modulusResult);
        final int charValue = modulusResult;
        return toCheckDigit(charValue);
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
            if (code.startsWith("12")) {
                throw new CheckDigitException("Invalid code, not allowed to start with '12' :" + code);
            }
            final int modulusResult = calculateModulus(code.substring(0, LEN - 1), false);
//            LOG.info(code + " calculateModulus mod 26="+modulusResult);
            return toCheckDigit(modulusResult).charAt(0) == code.charAt(LEN - 1);
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
