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

Beispiele:

    XI 110305878 : gültig https://www.bullseyecountrysport.co.uk/contact-us-2-w.asp
    XI 366303068 : gültig donnellygroup.co.uk Company Info Reg. Company Number: NI 643
                 , VAT Reg. No. GB366303068, GB366303013 ist HUITAIHK TECHNOLOGY LIMITED
    XI 174918964 : ungültig https://www.respond.co.uk/ VAT Number GB 174918964
    GB 174918964 : gültig, valid UK VAT number: RESPOND HEALTHCARE LIMITED, CARDIFF
    GB 613451470 : gültig, UNIVERSITY OF LEEDS. Aus https://www.adresslabor.de/en/products/vat-id-no-check.html
    GB 107328000 : gültig, IBM UNITED KINGDOM LIMITED
    GB 766800804 : gültig, IDOX SOFTWARE LTD
    GB 980780684 : ungültig. Aus https://old.formvalidation.io/validators/vat/
                 und https://github.com/koblas/stdnum-js/blob/main/src/gb/vat.spec.ts
    GB 340804329 : gültig, SUNS LIFESTYLE LIMITED
    GB 888801276 : == GD012 gültig, CENTRE FOR MANAGEMENT & POLICY STUDIES CIVIL SERVICE COLLEGE
    GB 888850259 : == HA502 gültig, DEFENCE ELECTRONICS AND COMPONENTS AGENCY
    GB 888851256 : == HA512 gültig, HIGH SPEED TWO (HS2) LIMITED
    XI/GB 434031494 : valide, aber nicht gültig (aus AT-Doku)

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
//              , "110305878", "174918964" // new style XI 9755 => test in VATINValidatorTest
              , "613451470"
              , "107328000"
              , "766800804"
              , "980780684"
              , "888801276"
              , "888850259"
              , "888851256"
              // new style:
//              , "340804329"
              
//            , "434031494"
//            , "", "980780684"
//            , "888801276", "888850259", "888851256" // GD + HA
/* aus http://www.vat-lookup.co.uk/
HERIZ LTD                         GB 431616518     14444294 // cd9755
TAILORED INSTALLATIONS LTD        GB 426985160     14444308
CATADRI TRANS LIMITED             GB 439432385     14444937
LANSUME LIMITED                   GB 439268659     14445009
EVG MODELLING LIMITED             GB 428671865     14444335
TPWV4 LIMITED                     GB 432880687     14444662
SOLOWRLD LIMITED                  GB 432184025     14445450 // cd9755
PHLUSH LTD                        GB 430510547     14444987
FAW MOTORSPORT LTD                GB 427092792     14444664
STARLIGHT FINANCE CONSULTING LTD  GB 427264494     14444676
MANAGING TEST ASSETS LTD          GB 428993836     14444927 // cd9755
SUPER HIERBAS UK LTD              GB 428121323     14445481 // cd9755
DURHAM ROAD PIZZA LTD             GB 430851513     14444711 // cd9755
BRIDGES WOODWORK LTD              GB 427661973     14444699
SANTI SURVEYING LIMITED           GB 428756265     14445513
CALEB DEVELOPMENT LTD             GB 816137833     04714239 // cd97
GAMMA HAT LTD                     GB 438017796     14445403
MID-LINK SOLUTIONS LTD            GB 426751194     14444732
YUNQIANG DECORATIONS LIMITED      GB 428819561     14445495
WATERMORE TECH LIMITED            GB 439997811     14444758 // cd9755
TASTE OF LIFE FOOD LTD            GB 433182417     14445102 // cd9755
TIDAL AND CO LIMITED              GB 440211846     14445100
LIZZAO LTD                        GB 436338390     14445453
DONNE UNITED LIMITED         GB 433477292     14444764
HYPERDROP LIMITED             GB 430416240     14445105 // cd9755
 */
            //, "431616518"
//            , "426985160", "439432385", "439268659", "428671865", "432880687", "430510547", "427092792"
//            , "427264494", "427661973", "428756265", "438017796", "426751194", "428819561", "440211846"
//            , "436338390", "433477292"
            };
        invalid = new String[] {"8888502+4", "1073280+0", "1073280-0"};
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
            System.out.println("XX " + i + " Testing Invalid Code=[" + invalidCode + "]");
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
//        System.out.println(icdmap);
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
