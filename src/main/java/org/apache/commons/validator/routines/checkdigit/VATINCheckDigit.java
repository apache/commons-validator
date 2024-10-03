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

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <b>VATIN</b> (VAT identification number) Check Digit calculation/validation.
 * <p>
 * kopiert aus IBANCheckDigit TODO kommemtieren und implementieren
 * </p>
 *
 * @since 1.9.0
 */
/*

pro Land anders

 */
public final class VATINCheckDigit extends AbstractCheckDigit implements Serializable {

    private static final long serialVersionUID = -6121493628894479170L;
    private static final int MIN_CODE_LEN = 8;

//    private static final int MAX_ALPHANUMERIC_VALUE = 35; // Character.getNumericValue('Z')
//
//    /** Singleton IBAN Number Check Digit instance */
//    public static final CheckDigit IBAN_CHECK_DIGIT = new VATINCheckDigit();
//
//    private static final long MAX = 999999999;
//
//    private static final long MODULUS = 97;

    /** Singleton Check Digit instance */
    private static final VATINCheckDigit INSTANCE = new VATINCheckDigit();

    /**
     * Gets the singleton instance of this Check Digit calculation/validation.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    private static final int SHORT_CODE_LEN = 2;
    private final ConcurrentMap<String, AbstractCheckDigit> checkDigitMap;

   /**
     * Constructs Check Digit routine for VATIN Numbers.
     */
    public VATINCheckDigit() {
        this.checkDigitMap = createCheckDigitMap(); //createValidators(validators);
    }
//    private ConcurrentMap<String, Validator> createValidators(final Validator[] validators) {
//        final ConcurrentMap<String, Validator> map = new ConcurrentHashMap<>();
//        for (final Validator validator : validators) {
//            map.put(validator.countryCode, validator);
//            for (final String otherCC : validator.otherCountryCodes) {
//                map.put(otherCC, validator);
//            }
//        }
//        return map;
//    }
    private ConcurrentMap<String, AbstractCheckDigit> createCheckDigitMap() {
        final ConcurrentMap<String, AbstractCheckDigit> map = new ConcurrentHashMap<>();
//        for (final Validator validator : validators) {
//            map.put(validator.countryCode, validator);
//        }
        map.put("AT", (AbstractCheckDigit) VATidATCheckDigit.getInstance());
                                        // VATidBECheckDigit extends Modulus97CheckDigit
        map.put("BE", (AbstractCheckDigit) VATidBECheckDigit.getInstance());
        map.put("BG", (AbstractCheckDigit) VATidBGCheckDigit.getInstance());
        map.put("CY", (AbstractCheckDigit) VATidCYCheckDigit.getInstance());
        map.put("CZ", (AbstractCheckDigit) VATidCZCheckDigit.getInstance());
        map.put("DE", (AbstractCheckDigit) Modulus11TenCheckDigit.getInstance());
        map.put("DK", (AbstractCheckDigit) VATidDKCheckDigit.getInstance());
        map.put("EE", (AbstractCheckDigit) VATidEECheckDigit.getInstance());
        map.put("EL", (AbstractCheckDigit) VATidELCheckDigit.getInstance());
        map.put("ES", (AbstractCheckDigit) VATidESCheckDigit.getInstance());
        map.put("FI", (AbstractCheckDigit) VATidFICheckDigit.getInstance());
        map.put("FR", (AbstractCheckDigit) VATidFRCheckDigit.getInstance());
        map.put("HR", (AbstractCheckDigit) Modulus11TenCheckDigit.getInstance());
        map.put("HU", (AbstractCheckDigit) VATidHUCheckDigit.getInstance());
        map.put("IE", (AbstractCheckDigit) VATidIECheckDigit.getInstance());
        map.put("IT", (AbstractCheckDigit) LuhnCheckDigit.LUHN_CHECK_DIGIT);
        map.put("LT", (AbstractCheckDigit) VATidLTCheckDigit.getInstance());
        map.put("LU", (AbstractCheckDigit) VATidLUCheckDigit.getInstance());
        map.put("LV", (AbstractCheckDigit) VATidLVCheckDigit.getInstance());
        map.put("MT", (AbstractCheckDigit) VATidMTCheckDigit.getInstance());
        map.put("NL", (AbstractCheckDigit) VATidNLCheckDigit.getInstance());
        map.put("PL", (AbstractCheckDigit) VATidPLCheckDigit.getInstance());
        map.put("PT", (AbstractCheckDigit) VATidPTCheckDigit.getInstance());
        map.put("RO", (AbstractCheckDigit) VATidROCheckDigit.getInstance());
        map.put("SE", (AbstractCheckDigit) VATidSECheckDigit.getInstance());
        map.put("SI", (AbstractCheckDigit) VATidSICheckDigit.getInstance());
        map.put("SK", (AbstractCheckDigit) VATidSKCheckDigit.getInstance());
        map.put("XI", (AbstractCheckDigit) VATidGBCheckDigit.getInstance());
        return map;
    }
    /**
     * Gets the Check Digit routine for a given VATIN Number
     *
     * @param code a string starting with the ISO country code (e.g. an VATIN)
     *
     * @return the Check Digit routine or {@code null} if there is not one registered.
     */
    public AbstractCheckDigit getCheckDigitMap(final String code) {
        if (code == null || code.length() < SHORT_CODE_LEN) { // ensure we can extract the code
            return null;
        }
        final String key = code.substring(0, SHORT_CODE_LEN);
        return checkDigitMap.get(key);
    }

    /**
     * Calculate the <i>Check Digit</i> for an IBAN code.
     * <p>
     * <b>Note:</b> The check digit is the third and fourth
     * characters and is set to the value "<code>00</code>".
     *
     * @param code The code to calculate the Check Digit for
     * @return The calculated Check Digit as 2 numeric decimal characters, e.g. "42"
     * @throws CheckDigitException if an error occurs calculating
     * the check digit for the specified code
     */
    @Override
    public String calculate(String code) throws CheckDigitException {
        if (code == null || code.length() < MIN_CODE_LEN) {
            throw new CheckDigitException("Invalid Code length=" + (code == null ? 0 : code.length()));
        }
        code = code.substring(0, 2) + "00" + code.substring(4); // CHECKSTYLE IGNORE MagicNumber
        final int modulusResult = 99; //calculateModulus(code);
        final int charValue = 98 - modulusResult; // CHECKSTYLE IGNORE MagicNumber
        final String checkDigit = Integer.toString(charValue);
        return charValue > 9 ? checkDigit : "0" + checkDigit; // CHECKSTYLE IGNORE MagicNumber
    }

    /**
     * Calculate the modulus for a code.
     *
     * @param code The code to calculate the modulus for.
     * @return The modulus value
     * @throws CheckDigitException if an error occurs calculating the modulus
     * for the specified code
     * /
    private int calculateModulus(final String code) throws CheckDigitException {
        final String reformattedCode = code.substring(4) + code.substring(0, 4); // CHECKSTYLE IGNORE MagicNumber
        long total = 0;
        for (int i = 0; i < reformattedCode.length(); i++) {
            final int charValue = Character.getNumericValue(reformattedCode.charAt(i));
            if (charValue < 0 || charValue > MAX_ALPHANUMERIC_VALUE) {
                throw new CheckDigitException("Invalid Character[" + i + "] = '" + charValue + "'");
            }
            total = (charValue > 9 ? total * 100 : total * 10) + charValue; // CHECKSTYLE IGNORE MagicNumber
            if (total > MAX) {
                total %= MODULUS;
            }
        }
        return (int) (total % MODULUS);
    }
*/
    /**
     * Validate the check digit of an VATIN code.
     *
     * @param code The code to validate
     * @return {@code true} if the check digit is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid(final String code) {
        final AbstractCheckDigit routine = getCheckDigitMap(code);
        if (routine == null) {
            System.out.println("No CheckDigit routine for " + code);
            return false;
        }
        final String vatin = code.substring(2);
        return routine.isValid(vatin);
    }

}
