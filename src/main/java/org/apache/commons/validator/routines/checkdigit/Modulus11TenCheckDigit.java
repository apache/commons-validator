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
 * MOD 11,10 module according to ISO/IEC 7064.
 * <p>
 * See <a href="https://de.wikipedia.org/wiki/ISO/IEC_7064">Wikipedia - ISO/IEC_7064 (de)</a>
 * for more details.
 * </p>
 *
 * <p>
 * This MOD 11,10 module can also be used to validate the VATIN_DE and TIN_DE
 * used in {@link org.apache.commons.validator.routines.checkdigit.VATINCheckDigit}
 *
 * @since 1.10.0
 */
public class Modulus11TenCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -2146331224291537185L;
    private static final Logger LOG = Logger.getLogger(Modulus11TenCheckDigit.class.getName());

    /** Singleton Check Digit instance */
    private static final Modulus11TenCheckDigit INSTANCE = new Modulus11TenCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    static final int MIN_CODE_LEN = 2;

    /**
     * Constructs a modulus Check Digit routine.
     */
    Modulus11TenCheckDigit() {
        super(MODULUS_11);
    }

    /**
     * Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.
     *
     * <p>For EC number digits are weighted by their position from left to right.</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        return charValue;
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
            throw new CheckDigitException(CheckDigitException.ZREO_SUM);
        }

        return toCheckDigit(INSTANCE.calculateModulus(code, false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
/*

Prüfung der Steuer- und Steueridentifikationsnummer
Stand: 17. Juni 2024 Seite 8 von 111
Algorithmus zur Berechnung der Prüfziffer:

int calcPruefzifferSteuerID(const string &pSteuerID) {
	const int n = 11;
	const int m = 10;
	int ziffer;
	int produkt = m;
	int summe;
	unsigned int length = pSteuerID.length();
	for(unsigned int i=0; i<length; i++) {
		ziffer = pSteuerID.at(i);
		summe = (ziffer +produkt) % m;
		if(summe == 0) {
			summe = m;
		}
		produkt = (2*summe) % n;
	}
	int pruefZiffer = n – produkt;
	return (pruefZiffer == 10) ? 0 : pruefZiffer;


 */
    protected int calculateModulus(final String code, final boolean includesCheckDigit) throws CheckDigitException {
        int produkt = MODULUS_10;
        int summe = 0;
        for (int i = 0; i < code.length() - (includesCheckDigit ? 1 : 0); i++) {
            final int lth = code.length() + (includesCheckDigit ? 0 : 1);
            final int leftPos = i + 1;
            final int rightPos = lth - i;
            final int ziffer = toInt(code.charAt(i), leftPos, rightPos);
            summe = weightedValue(ziffer, leftPos, rightPos) + produkt;
            summe = summe % MODULUS_10;
            if (summe == 0) summe = MODULUS_10;
            produkt = 2 * summe % MODULUS_11;
        }
        LOG.fine(code + " produkt=" + produkt + " summe=" + summe);
        int pruefZiffer = MODULUS_11 - produkt;
        return pruefZiffer == MODULUS_10 ? 0 : pruefZiffer;
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
        try {
            if (GenericTypeValidator.formatLong(code.substring(0, code.length() - 1)) == 0) {
                throw new CheckDigitException(CheckDigitException.ZREO_SUM);
            }
            final int modulusResult = INSTANCE.calculateModulus(code, true);
            return modulusResult == Character.getNumericValue(code.charAt(code.length() - 1));
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
