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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * XI VAT Id Check Digit Tests.
 */
/*

    XI 110305878 : gültig bullseyecountrysport
    XI 366303068 : gültig donnellygroup.co.uk Company Info Reg. Company Number: NI 643,
    GB 366303013 ist HUITAIHK TECHNOLOGY LIMITED, ABERDEEN
    XI 174918964 : valide, aber ungültig da nicht in Nordirland
    GB 174918964 : gültig, valid UK VAT number: RESPOND HEALTHCARE LIMITED, CARDIFF
    GB 613451470 : gültig, UNIVERSITY OF LEEDS. Aus adresslabor.de
    GB 107328000 : gültig, IBM UNITED KINGDOM LIMITED
    // VAT with branch test in VATINValidatorTest
    GB 107328000001 Nr mit Niederlassung : gültig, IBM UK RENTALS LTD
    GB 107328000002 Nr mit Niederlassung : gültig, IBM UNITED KINGDOM HOLDINGS LTD
    GB 766800804 : gültig, IDOX SOFTWARE LTD
    GB 980780684 : ungültig. Aus formvalidation.io und koblas/stdnum-js
    GB 340804329 : gültig, SUNS LIFESTYLE LIMITED
    GB 888801276 : == GD012 gültig, CENTRE FOR MANAGEMENT & POLICY STUDIES CIVIL SERVICE COLLEGE
    GB 888850259 : == HA502 gültig, DEFENCE ELECTRONICS AND COMPONENTS AGENCY
    GB 888851256 : == HA512 gültig, HIGH SPEED TWO (HS2) LIMITED
    XI/GB 434031494 : valide, aber nicht gültig (aus AT-Doku)
VAT Mod 97   : GB 562235945 nicht gültig
VAT Mod 9755 : GB 562235987 nicht gültig
 */
public class VATidXICheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        checkDigitLth = VATidGBCheckDigit.CHECKDIGIT_LEN;
        routine = VATidGBCheckDigit.getInstance();

//  366303068 (old style) 366303013 sind zwei verschiedene Unternehmen, die verschiedene PZ haben
        valid = new String[] {"366303068" // old style XI
              , "434031494"
              , "110305836" // 1103058 36 old style for testing
//              , "110305878", "174918964" // new style XI 9755 => test in VATINValidatorTest
              , "613451470"
              , "107328000"
              , "766800804"
              , "980780684"
              , "888801276"
              , "888850259"
              , "888851256"
              , "816137833"
              , "562235945"
              /* new style 9755 cannot be tested here
              , "340804329"
              , "431616518"
              , "426985160"
              , "439432385"
              , "439268659"
              , "428671865"
              , "432880687"
              , "432184025"
              , "430510547"
              , "427092792"
              , "427264494"
              , "428993836"
              , "428121323"
              , "430851513"
              , "428756265"
              , "438017796"
              , "426751194"
              , "428819561"
              , "439997811"
              , "433182417"
              , "440211846"
              , "436338390"
              , "433477292"
              , "430416240"
               */
            };
        invalid = new String[] {"8888502+4", "1073280+0", "1073280-0"};
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to handle two digits as <em>Check Digit</em>. The original test assumes
     * that there is only one valid check digit. All other generated test (createInvalidCodes)
     * codes should assertFalse. This is not so for XI/GB VATINS. There are two valid check digits,
     * one calculated with old style MOD97 and the other with MOD9755.
     * I collect the test results for each test case and print the result at the end.
     * Example: 5622359 45 + [87] means: the old style cd is 45, and the MOD9755-cd is 87
     * </p>
     */
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
                int l = invalidCheckDigits[i].length();
                List<String> v = icdmap.get(invalidCheckDigits[i].substring(0, l - checkDigitLth));
                if (v == null) {
                    v = new ArrayList<String>();
                    v.add(invalidCheckDigits[i].substring(l - checkDigitLth));
                    icdmap.put(invalidCheckDigits[i].substring(0, l - checkDigitLth), v);
                } else {
                    v.add(invalidCheckDigits[i].substring(l - checkDigitLth));
                }
                //map.put(invalidCheckDigits[i].substring(checkDigitLth), invalidCheckDigits[i].substring(0, checkDigitLth));
//                System.out.println("true" + i + " Testing Invalid Check Digit, Code=[" + invalidCheckDigits[i] + "]");
            } else {
                //System.out.println("   " + i + " Testing Invalid Check Digit, Code=[" + invalidCheckDigits[i] + "]");
            }
//            assertFalse(res, "invalid check digit[" + i + "]: " + invalidCheckDigits[i]);
        }
        // now print the results
        List<String> validList = Arrays.asList(valid);
        icdmap.forEach((key, value) -> {
            for (String v : validList) {
                if (v.startsWith(key)) {
                    System.out.println(key + " " + v.substring(v.length() - 2) + " + " + value);
                }
            }
        });
    }

}
