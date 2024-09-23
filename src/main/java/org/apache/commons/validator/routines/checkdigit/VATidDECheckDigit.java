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

/**
 * deutsche Umsatzsteuer-Identifikationsnummer.
 *
 * <p>
 * siehe bgbl193s0736, Bundesgesetztblatt, Jahrgang 1993, Teil I
 * Anlage 3 (zu §9)
 * <br>
 * hybrides MOD 11,10
 * Aufbau: DEnnnnnnnnp.
 * </p>

produkt := 10
for j=1 to 8 step 1
  summe := cj + produkt
  summe := summe mod 10
  if summe=0 then summe = 10
  produkt := (2*summe) mod 11
end for
pz := 11 - produkt
if pz=10 then pz = 0

Beispiel: DE 136 695 976
 *
 * @since 1.10.0
 */
//TODO nur zur Doku use Modulus11TenCheckDigit
public final class VATidDECheckDigit extends Modulus11TenCheckDigit {

//
//    /** Singleton Check Digit instance */
//    private static final VATidDECheckDigit INSTANCE = new VATidDECheckDigit();
//
//    /**
//     * Gets the singleton instance of this validator.
//     * @return A singleton instance of the class.
//     */
//    public static CheckDigit getInstance() {
//        return INSTANCE;
//    }
//
//    public static final int LEN = 9; // with Check Digit
//
//    /**
//     * Constructs a modulus Check Digit routine.
//     */
//    private VATidDECheckDigit() {
//        super(MODULUS_11); // XXX ist es nun MODULUS_10 oder MODULUS_11 ??? weder - noch!!!!
//    }
//
//    /**
//     * Calculates the <i>weighted</i> value of a character in the
//     * code at a specified position.
//     *
//     * <p>For EC number digits are weighted by their position from left to right.</p>
//     *
//     * @param charValue The numeric value of the character.
//     * @param leftPos The position of the character in the code, counting from left to right
//     * @param rightPos The positionof the character in the code, counting from right to left
//     * @return The weighted value of the character.
//     */
//    @Override
//    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
//        return charValue;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public String calculate(final String code) throws CheckDigitException {
//        if (GenericValidator.isBlankOrNull(code)) {
//            throw new CheckDigitException("Code is missing");
//        }
////        if (GenericTypeValidator.formatShort(value))
////        if (GenericValidator.isShort(code) && GenericTypeValidator.formatShort(code)==0) {
//        // damit wäre "000000003" gültig
//        // if (code.length()>=LEN && GenericTypeValidator.formatLong(code)==0) {
//        if (GenericTypeValidator.formatLong(code)==0) {
//            throw new CheckDigitException("Invalid code, sum is zero");
//        }
//
//        return toCheckDigit(INSTANCE.calculateModulus(code, false));
//    }
//
///*
//produkt := 10
//for j=1 to 8 step 1
//  summe := cj + produkt
//  summe := summe mod 10
//  if summe=0 then summe = 10
//  produkt := (2*summe) mod 11
//end for
//--------------------
//Prüfung der Steuer- und Steueridentifikationsnummer
//Stand: 17. Juni 2024 Seite 8 von 111
//Algorithmus zur Berechnung der Prüfziffer:
//
//int calcPruefzifferSteuerID(const string &pSteuerID) {
//	const int n = 11;
//	const int m = 10;
//	int ziffer;
//	int produkt = m;
//	int summe;
//	unsigned int length = pSteuerID.length();
//	for(unsigned int i=0; i<length; i++) {
//		ziffer = pSteuerID.at(i);
//		summe = (ziffer +produkt) % m;
//		if(summe == 0) {
//			summe = m;
//		}
//		produkt = (2*summe) % n;
//	}
//	int pruefZiffer = n – produkt;
//	return (pruefZiffer == 10) ? 0 : pruefZiffer;
//}
// */
//    protected int calculateModulus(final String code, final boolean includesCheckDigit) throws CheckDigitException {
//        int produkt = 10;
//        int total = 0;
//        for (int i = 0; i < code.length()-(includesCheckDigit ? 1 : 0); i++) {
//            final int lth = code.length() + (includesCheckDigit ? 0 : 1);
//            final int leftPos = i + 1;
//            final int rightPos = lth - i;
//            final int charValue = toInt(code.charAt(i), leftPos, rightPos);
////            total += weightedValue(charValue, leftPos, rightPos);
//            total = weightedValue(charValue, leftPos, rightPos) + produkt;
//            total = total % MODULUS_10;
//            if (total == 0) total = 10;
//            produkt = (2*total) % MODULUS_11;
//        }
//        if (total == 0) {
//            throw new CheckDigitException("Invalid code, sum is zero");
//        }
////        pz := 11 - produkt
////        if pz=10 then pz = 0
//        return produkt==1 ? 0 : MODULUS_11-produkt;
////        return total % modulus;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public boolean isValid(final String code) {
//        if (GenericValidator.isBlankOrNull(code)) {
//            return false;
//        }
//        if (code.length() != LEN) {
//            return false;
//        } else {
//        	// calculateModulus sollte exception liefern bei 000000003
//        }
//        try {
//            final int modulusResult = INSTANCE.calculateModulus(code, true);
//            char ch = code.charAt(code.length() - 1);
//            return modulusResult == Character.getNumericValue(ch);
//        } catch (final CheckDigitException ex) {
//            return false;
//        }
//    }
//
}
