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

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * FR VAT Id Check Digit Tests.
 *
aus https://github.com/anghelvalentin/CountryValidator/blob/master/CountryValidator.Tests/CountriesValidators/FranceValidatorTests.cs
 * FR40303265045 ist gültig : Name SA SODIMAS; Adresse 11 RUE AMPERE, 26600 PONT DE L ISERE (geprüft mit VIES)
 * FR23334175221 ist gültig : Name SAS UNITED PARCEL SERVICE FRANCE SAS (geprüft mit VIES)
 * FRK7399859412 ist gültig : Name SLRL ALTEA EXPERTISE COMPTABLE (geprüft mit VIES)
 *   K7399859412 auch in https://github.com/koblas/stdnum-js/blob/main/src/fr/tva.spec.ts
 *   00300076965 aus old-style in https://help.sap.com/docs/SUPPORT_CONTENT/crm/3354674613.html
 *   83404833048 ungültig aber genutzt in https://en.wikipedia.org/wiki/VAT_identification_number
 *   2H123456789 aus http://sima.cat/nif.php  ????
 */
public class VATidFRCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        checkDigitLth = VATidFRCheckDigit.CHECKDIGIT_LEN;
        routine = VATidFRCheckDigit.getInstance();
/*
23 334175221 [N0, T0, H1, N1, C2, U2, Z2, J3, P3, D4, V4, Q5, V5, E6, K6, R7, W7, L8, A9, F9, X9, 0H, 1H, 2J, 3J, 4J, 5J, 6J, 7K, 8K, 9K, 0V, 1V, 1W, 2W, 3W, 4W, 5W, 6W, 6X, 7X, 8X, 9X]
2H 123456789 32 [H0, Z0, C1, U1, Z1, J2, P2, D3, V3, Q4, E5, K5, R6, W6, F7, L7, A8, F8, X8, M9, S9, 2H, 3H, 4H, 5H, 6H, 7H, 7J, 8J, 9J, 0U, 1U, 2V, 3V, 4V, 5V, 6V, 7W, 8W, 9W]
40 303265045 [N0, T0, H1, N1, C2, U2, Z2, J3, P3, D4, V4, Q5, V5, E6, K6, R7, W7, L8, A9, F9, X9, 0H, 1H, 2J, 3J, 4J, 5J, 6J, 7K, 8K, 9K, 0V, 1V, 1W, 2W, 3W, 4W, 5W, 6W, 6X, 7X, 8X, 9X]
00 300076965 [K0, Q0, E1, W1, L2, R2, F3, L3, A4, S4, X4, M5, B6, T6, Y6, N7, T7, C8, H8, Z8, P9, U9, 0C, 1C, 2C, 3C, 4C, 4D, 5D, 6D, 7D, 8D, 9D, 9E, 0Q, 1Q, 2Q, 3Q, 4R, 5R, 6R, 7R, 8R, 9S]
K7 399859412 06 [B0, Y0, B1, T1, H2, N2, C3, U3, Z3, J4, P4, D5, J5, Q6, V6, E7, K7, R8, W8, L9, R9, 0J, 1J, 1K, 2K, 3K, 4K, 5K, 6K, 6L, 7L, 8L, 9L, 0W, 1X, 2X, 3X, 4X, 5X, 6Y, 7Y, 8Y, 9Y]

 */

        valid = new String[] {"00300076965" // also possible check digit: ...
            , "40303265045" // also possible check digit: "N0" ...
            , "23334175221"
            , "06399859412"
//            , "K7399859412" // wieso gerade K7 aus den vielen möglichen check digits ??? und nicht 06
//            , "2H123456789" // auch 32 wäre möglich
            , "32123456789"
            };
////      invalid = new String[] {"83404833048", "84323140391"};
//        invalid = new String[] {"00300076965"};
//        invalidList = Arrays.asList(invalid);
    }

    protected String checkDigit(final String code) {
        if (code == null || code.length() <= checkDigitLth) {
            return "";
        }
        return code.substring(0, checkDigitLth);
    }

    protected String removeCheckDigit(final String code) {
        if (code == null || code.length() <= checkDigitLth) {
            return null;
        }
        return code.substring(checkDigitLth);
    }

    @Test
    @Override
    public void testIsValidFalse() {
        if (log.isDebugEnabled()) {
            log.debug("testIsValidFalse() for " + routine.getClass().getName());
        }

        // test invalid code values
        for (int i = 0; i < invalid.length; i++) {
            if (log.isDebugEnabled()) {
                log.debug("   " + i + " Testing Invalid Code=[" + invalid[i] + "]");
            }
            String invalidCode = invalid[i];
            System.out.println("   " + i + " Testing Invalid Code=[" + invalidCode + "]");
            assertFalse(routine.isValid(invalidCode), "invalid[" + i + "]: " + invalidCode);
        }

        // test invalid check digit values
        final String[] invalidCheckDigits = createInvalidCodes(valid);
        final Map<String, List<String>> icdmap = new Hashtable<String, List<String>>();
        for (int i = 0; i < invalidCheckDigits.length; i++) {
            if (log.isDebugEnabled()) {
                log.debug("   " + i + " Testing Invalid Check Digit, Code=[" + invalidCheckDigits[i] + "]");
            }
            boolean res = routine.isValid(invalidCheckDigits[i]);
            if (res) {
                log.warn("   " + i + " Testing Invalid Check Digit, Code=[" + invalidCheckDigits[i] + "] is true, expected false.");
                List<String> v = icdmap.get(invalidCheckDigits[i].substring(checkDigitLth));
                if (v == null) {
                    v = new ArrayList<String>();
                    v.add(invalidCheckDigits[i].substring(0, checkDigitLth));
                    icdmap.put(invalidCheckDigits[i].substring(checkDigitLth), v);
                } else {
                    v.add(invalidCheckDigits[i].substring(0, checkDigitLth));
                }
                //map.put(invalidCheckDigits[i].substring(checkDigitLth), invalidCheckDigits[i].substring(0, checkDigitLth));
//                System.out.println("true" + i + " Testing Invalid Check Digit, Code=[" + invalidCheckDigits[i] + "]");
            } else {
                //System.out.println("   " + i + " Testing Invalid Check Digit, Code=[" + invalidCheckDigits[i] + "]");
            }
//            assertFalse(res, "invalid check digit[" + i + "]: " + invalidCheckDigits[i]);
        }
        //System.out.println(map);
        icdmap.forEach((key, value) -> System.out.println(key + " " + value));


    }

    protected String createCode(final String code, final String cd) {
        return cd + code;
    }

}
