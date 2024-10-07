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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.routines.SireneValidator;
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
/*
Beispiele

    FR 00300076965 : valide, aber ungültig aus sap.com
    FR 55502090897 : gültig, aus flexitec.fr
    FR 62914691134 : gültig, aus renoloctp-44.fr
    FR 39423495704 : gültig, aus tibco.fr
    FR 56395208796 : gültig, aus dekra-norisko.fr
    FR 21431506443 : gültig, aus de-pinho.fr
    FR 87791184211 : gültig, aus hoffert-architecture.com
    FR 82419238647 : gültig, aus kisscut-68.fr
    FR 58349403683 : gültig, aus emtez.fr
    FR 50818467615 : gültig, aus 123parebrise.fr
    FR 42916220106 : gültig, aus sccu-colmar.fr
    FR 85343262622 : lidl
    FR 88882736077 : e.leclerc
    FR 40303265045 : gültig : Name SA SODIMAS; Adresse 11 RUE AMPERE, 26600 PONT DE L ISERE / Quelle: anghelvalentin
    FR 23334175221 : gültig : Name SAS UNITED PARCEL SERVICE FRANCE SAS / Quelle: dto
    FR K7399859412 : gültig : Name ALTEA EXPERTISE COMPTABLE / Quelle: dto und koblas/stdnum-js
    FR 83404833048 : valide, aber ungültig weil nicht mehr aktiv (Établissement fermé depuis le 31/12/2021 au répertoire Sirene), aus en.wikipedia

 */
public class VATidFRCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        checkDigitLth = VATidFRCheckDigit.CHECKDIGIT_LEN;
        routine = VATidFRCheckDigit.getInstance();
/* not SIREN:
2H 123456789 32 [H0, Z0, C1, U1, Z1, J2, P2, D3, V3, Q4, E5, K5, R6, W6, F7, L7, A8, F8, X8, M9, S9, 2H, 3H, 4H, 5H, 6H, 7H, 7J, 8J, 9J, 0U, 1U, 2V, 3V, 4V, 5V, 6V, 7W, 8W, 9W]

00 300076965 [K0, Q0, E1, W1, L2, R2, F3, L3, A4, S4, X4, M5, B6, T6, Y6, N7, T7, C8, H8, Z8, P9, U9, 0C, 1C, 2C, 3C, 4C, 4D, 5D, 6D, 7D, 8D, 9D, 9E, 0Q, 1Q, 2Q, 3Q, 4R, 5R, 6R, 7R, 8R, 9S]
23 334175221 [N0, T0, H1, N1, C2, U2, Z2, J3, P3, D4, V4, Q5, V5, E6, K6, R7, W7, L8, A9, F9, X9, 0H, 1H, 2J, 3J, 4J, 5J, 6J, 7K, 8K, 9K, 0V, 1V, 1W, 2W, 3W, 4W, 5W, 6W, 6X, 7X, 8X, 9X]
40 303265045 [N0, T0, H1, N1, C2, U2, Z2, J3, P3, D4, V4, Q5, V5, E6, K6, R7, W7, L8, A9, F9, X9, 0H, 1H, 2J, 3J, 4J, 5J, 6J, 7K, 8K, 9K, 0V, 1V, 1W, 2W, 3W, 4W, 5W, 6W, 6X, 7X, 8X, 9X]
K7 399859412 06 [B0, Y0, B1, T1, H2, N2, C3, U3, Z3, J4, P4, D5, J5, Q6, V6, E7, K7, R8, W8, L9, R9, 0J, 1J, 1K, 2K, 3K, 4K, 5K, 6K, 6L, 7L, 8L, 9L, 0W, 1X, 2X, 3X, 4X, 5X, 6Y, 7Y, 8Y, 9Y]

 */

        valid = new String[] {"00300076965"
            , "55502090897"
            , "62914691134"
            , "39423495704"
            , "56395208796"
            , "21431506443"
            , "87791184211"
            , "82419238647"
            , "58349403683"
            , "50818467615"
            , "42916220106"
            , "85343262622"
            , "88882736077"
            , "40303265045" // also possible check digit: "N0" ...
            , "23334175221"
            , "06399859412"
//          , "K7399859412" // wieso gerade K7 aus den vielen möglichen check digits ??? und nicht 06
            , "83404833048"
// not SIREN:           , "2H123456789" // auch 32 wäre möglich
// not SIREN:           , "32123456789"
// valid but not SIREN aus Monaco            , "37000005990"
            , "11123456782" // konstruiert: L'identifiant n'existe pas dans le répertoire Sirene.
/*
isValid: 123456956
isValid: 123456964
isValid: 123456972
isValid: 123456980
isValid: 123456998
isValid: 123457004
isValid: 123457012
isValid: 123457020
isValid: 123457038
isValid: 123457046
isValid: 123457053
isValid: 123457061
isValid: 123457079
isValid: 123457087
isValid: 123457095
isValid: 123457103
isValid: 123457111
isValid: 123457129
isValid: 123457137
isValid: 123457145
isValid: 123457152
isValid: 123457160
isValid: 123457178
isValid: 123457186
isValid: 123457194
isValid: 123457202
isValid: 123457210
isValid: 123457228
isValid: 123457236
isValid: 123457244
isValid: 123457251
isValid: 123457269
isValid: 123457277
isValid: 123457285
isValid: 123457293
isValid: 123457301
isValid: 123457319
isValid: 123457327
isValid: 123457335
isValid: 123457343
isValid: 123457350
isValid: 123457368
isValid: 123457376
isValid: 123457384
isValid: 123457392
isValid: 123457400
isValid: 123457418
isValid: 123457426
isValid: 123457434
isValid: 123457442
isValid: 123457459
isValid: 123457467
isValid: 123457475
isValid: 123457483
isValid: 123457491
isValid: 123457509
isValid: 123457517
isValid: 123457525
isValid: 123457533
isValid: 123457541
isValid: 123457558
isValid: 123457566
isValid: 123457574
isValid: 123457582
isValid: 123457590
isValid: 123457608
isValid: 123457616
isValid: 123457624
isValid: 123457632
isValid: 123457640
isValid: 123457657
isValid: 123457665
isValid: 123457673
isValid: 123457681
isValid: 123457699
isValid: 123457707
isValid: 123457715
isValid: 123457723
isValid: 123457731
isValid: 123457749
isValid: 123457756
isValid: 123457764
isValid: 123457772
isValid: 123457780

 * /
//            , "U2123456790" // C2 U2 Z2 ungültiig
            , "89123456808"
            , "16123456816"
            , "40123456824"
            , "64123456832"
            , "88123456840"
            , "42123456857"
//            , "F6123456865" oder L6123456865
            , "90123456873"
            , "17123456881"
            , "71123456899"
            , "95123456907"
            , "22123456915"
            , "46123456923"
//            , "E4123456931" "K4123456931 n=26
            , "27123456949"
            , "48123456956"
            , "72123456964"
            , "96123456972"
            , "23123456980"           */
            , "95952418325" // mistral.al
            , "02813197589" // SAS VIRTUO TECHNOLOGIES starts with 0
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
//                log.warn("   " + i + " Testing Invalid Check Digit, Code=[" + invalidCheckDigits[i] + "] is true, expected false.");
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
        List<String> validList = Arrays.asList(valid);
        icdmap.forEach((key, value) -> {
            for (String v : validList) {
                if (v.endsWith(key)) {
                    System.out.println(key + " " + v.substring(0, 2) + " + " + value);
                }
            }
        });
    }

    protected String createCode(final String code, final String cd) {
        return cd + code;
    }

    @Test
    public void testconstruct() {
//                      22345679 // nichts in VIES gefunden
//    	String start = "39985942";  // auch nichts
    	String start = "79316629"; 
        final int s = GenericTypeValidator.formatInt(start); // nichts in VIES gefunden
        for (int i = s; i < s + 10; i++) {
            for (int cd = 0; cd < 10; cd++) {
                String siren = ""+i + cd;
                if (SireneValidator.getInstance().isValid(siren)) {
                    System.out.println("SIREN isValid: " + siren);
                    try {
						String ccd = routine.calculate(siren);
	                    System.out.println("VATIIN: " +ccd+ siren);
					} catch (CheckDigitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }
        }
    }

}
