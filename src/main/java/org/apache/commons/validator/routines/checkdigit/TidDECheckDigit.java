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

/**
 * deutsche persönliche Steuer-Identifikationsnummer.
 *
 * <p>
 * siehe bgbl193s0736, Bundesgesetztblatt, Jahrgang 1993, Teil I
 * Anlage 3 (zu §9)
 * <br>
 * hybrides MOD 11,10
 * Aufbau: nnnnnnnnnnp.
 * </p>

Quelle: Bayerisches Landesamt für Steuern:
<a href="https://download.elster.de/download/schnittstellen/Pruefung_der_Steuer_und_Steueridentifikatsnummer.pdf">Prüfung der Steuer- und Steueridentifikationsnummer</a>

Beispiel: 02476291358 : TestID da führende 0

 *
 * @since 1.9.0
 */
public final class TidDECheckDigit extends Modulus11TenCheckDigit {

    private static final long serialVersionUID = 4222306963463322195L;
    private static final Logger LOG = Logger.getLogger(TidDECheckDigit.class.getName());

    /** Singleton Check Digit instance */
    private static final TidDECheckDigit INSTANCE = new TidDECheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    /**
     * Constructs a modulus Check Digit routine.
     */
    private TidDECheckDigit() {
        super();
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public String calculate(final String code) throws CheckDigitException {
//        if (GenericValidator.isBlankOrNull(code)) {
//            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
//        }
//        if (GenericTypeValidator.formatLong(code) == 0) {
//            throw new CheckDigitException(CheckDigitException.ZREO_SUM);
//        }
//
//        return toCheckDigit(INSTANCE.calculateModulus(code, false));
//    }

    /**
     * {@inheritDoc} <br>
     * Override to count ciphers to and check the criteria:
     * <p>
     * The first 10 digits of the identification number must contain
     * exactly one duplicate or triplicate digit.
     * </p><p>
     * Are there three identical digits in positions 1 to 10,
     * these identical digits must never be directly next to each other.
     * </p>
     */
    @Override
    protected int calculateModulus(final String code, final boolean includesCheckDigit) throws CheckDigitException {
        int product = MODULUS_10;
        int sum = 0;
        int[] anzahl = new int[10];  // CHECKSTYLE IGNORE MagicNumber
        int[] fstpos = new int[10];  // CHECKSTYLE IGNORE MagicNumber
        for (int i = 0; i < code.length() - (includesCheckDigit ? 1 : 0); i++) {
            final int leftPos = i + 1;
            final int rightPos = -1; // rightPos param not used
            final int cipher = toInt(code.charAt(i), leftPos, rightPos);
            anzahl[cipher]++; // Ziffern zählen
            if (fstpos[cipher] == 0) {
                fstpos[cipher] = i;
            }
            sum = cipher + product;
            sum = sum % MODULUS_10;
            product = 2 * (sum == 0 ? MODULUS_10 : sum) % MODULUS_11;
        }
        // Zifferanzahl auswerten
        int doppelt = 0;
        int dreifach = 0;
        int dreifachz = -1;
        for (int i = 0; i < anzahl.length; i++) {
            if (anzahl[i] == 2) { // CHECKSTYLE IGNORE MagicNumber
                doppelt++;
            }
            if (anzahl[i] == 3) { // CHECKSTYLE IGNORE MagicNumber
                dreifach++;
                dreifachz = i;
            }
        }
        if (doppelt == 0 && dreifach == 0) {
            LOG.warning(code + ": keine doppelt und keine dreifach");
            throw new CheckDigitException("Invalid code, keine doppelt und keine dreifach");
        } else if (doppelt > 1) {
            LOG.warning(code + ": mehrere doppelt");
            throw new CheckDigitException("Invalid code, mehrere doppelt");
        } else if (dreifach > 1) {
            LOG.warning(code + ": mehrere dreifach");
            throw new CheckDigitException("Invalid code, mehrere dreifach");
        } else if (dreifach == 1) {
            int i = code.indexOf("" + dreifachz);
            if (dreifachz == toInt(code.charAt(i + 1), i + 1, -1)
                && dreifachz == toInt(code.charAt(i + 2), i + 2, -1)) {
                LOG.warning(code + ": dreifach direkt hintereinander Ziffer:" + dreifachz);
                throw new CheckDigitException("Invalid code, dreifach direkt hintereinander");
            }
        }
        int pruefZiffer = MODULUS_11 - product;
        return pruefZiffer == MODULUS_10 ? 0 : pruefZiffer;
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public boolean isValid(final String code) {
//        if (GenericValidator.isBlankOrNull(code)) {
//            return false;
//        }
//        if (code.length() < MIN_CODE_LEN) {
//        	return false;
//        }
//        try {
//            if (GenericTypeValidator.formatLong(code.substring(0, code.length() - 1)) == 0) {
//                throw new CheckDigitException(CheckDigitException.ZREO_SUM);
//            }
//            final int modulusResult = INSTANCE.calculateModulus(code, true);
//            return modulusResult == Character.getNumericValue(code.charAt(code.length() - 1));
//        } catch (final CheckDigitException ex) {
//            return false;
//        }
//    }

}
