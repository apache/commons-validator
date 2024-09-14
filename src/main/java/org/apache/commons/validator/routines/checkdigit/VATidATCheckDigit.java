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

import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.GenericValidator;

/**
 * Austrian VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * Umsatzsteueridentifikationsnummer (UID-Nr.)
 * </p>
 * <p>
 * See <a href="https://www.bmf.gv.at/dam/jcr:9f9f8d5f-5496-4886-aa4f-81a4e39ba83e/BMF_UID_Konstruktionsregeln.pdf">bmf.gv.at - BMF_UID_Konstruktionsregeln</a>
 * for more details.
 * </p>
Der Algorithmus auf http://www.pruefziffernberechnung.de/U/USt-IdNr.shtml#PZAT isk komplett falsch XXX
Beispiel: AT U13585627

TODO derzeit wird U hier geprüft - das steht im Widersproch zu package Vorgabe:
    <b>do not validate</b> the input for length or syntax
 *
 * @since 1.9.0
 */
public final class VATidATCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = 8481357188785400898L;
	
	/** Singleton Check Digit instance */
    private static final VATidATCheckDigit INSTANCE = new VATidATCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    public static final int LEN = 8; // without constant "U"

    public static String omitU(final String code) {
        return code.substring(1);
    }
    
    /**
     * Constructs a Check Digit routine.
     */
    private VATidATCheckDigit() {
        super(MODULUS_10);
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
        if(leftPos>=LEN) return 0;
        if((leftPos-1) % 2 == 0) {
            return charValue;
        } else {
            // Si = INT(charValue / 5) + (charValue * 2) modulo10
            int i = charValue / 5;
            return i + (charValue*2) % MODULUS_10;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException("Code is missing");
        }
        if (code.length()>=LEN && GenericTypeValidator.formatLong(omitU(code))==0) {
            throw new CheckDigitException("Invalid code, sum is zero");
        }
        if (!code.startsWith("U")) {
            throw new CheckDigitException("Invalid code, does not start with U");
        }
        final int modulusResult = calculateModulus(omitU(code), false);
        // (10 – (R + C2 + C4 + C6 + C8 + 4) modulo 10) modulo 10
        final int cdValue = (MODULUS_10 - modulusResult) % MODULUS_10;
        return toCheckDigit(cdValue);
    }

    protected int calculateModulus(final String code, final boolean includesCheckDigit) throws CheckDigitException {
        int total = 0;
        for (int i = 0; i < code.length(); i++) {
            final int lth = code.length() + (includesCheckDigit ? 0 : 1);
            final int leftPos = i + 1;
            final int rightPos = lth - i;
            final int charValue = toInt(code.charAt(i), leftPos, rightPos);
            total += weightedValue(charValue, leftPos, rightPos);
        }
        if (total == 0) {
            throw new CheckDigitException("Invalid code, sum is zero");
        }
        return (total + 4) % MODULUS_10;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        if (code.length() != LEN + 1) {
            return false;
        }
        if (!code.startsWith("U")) {
            return false;
        }
        try {
            final int cd = (MODULUS_10 - INSTANCE.calculateModulus(omitU(code), true)) % MODULUS_10;
            return cd == Character.getNumericValue(code.charAt(code.length() - 1));
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
