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

import org.junit.jupiter.api.BeforeEach;

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
    GB 107328000 : gültig, IBM UNITED KINGDOM LIMITED // 107328010 ist aoch valide TODO
    GB 766800804 : gültig, IDOX SOFTWARE LTD // ??? cd9755=46 ist falsch TODO
    GB 980780684 : ungültig. Aus https://old.formvalidation.io/validators/vat/
                 und https://github.com/koblas/stdnum-js/blob/main/src/gb/vat.spec.ts
    GB 340804329 : gültig, SUNS LIFESTYLE LIMITED // cd9755=29 TODO
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
//  366303068 und 366303013 sind zwei verschiedene Unternehmen mit die verschiedene PZ haben !!! XXX
        valid = new String[] {"110305878", "366303068", "174918964" // XI
            , "434031494"
            , "613451470", "980780684"
            , "888801276", "888850259", "888851256" // GD + HA
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
            , "426985160", "439432385", "439268659", "428671865", "432880687", "430510547", "427092792"
            , "427264494", "427661973", "428756265", "438017796", "426751194", "428819561", "440211846"
            , "436338390", "433477292"
            };
    }

}
