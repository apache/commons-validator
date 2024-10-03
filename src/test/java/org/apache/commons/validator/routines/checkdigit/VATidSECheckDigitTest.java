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
 * SE VAT Id Check Digit Tests.
 */
public class VATidSECheckDigitTest extends AbstractCheckDigitTest {

   /**
     * Sets up routine & valid codes.

     without last 2 chars, which are constant "01" - will be checked as regex in VATINValidator

    SE 1366959755 01 : valide, aber ungültig. Quelle bmf.gv.at
    SE 5561888404 01 : gültig OLLE SVENSSONS PARTIAFFÄR AKTIEBOLAG. Quelle pruefziffernberechnung.de
    SE 1234567897 01 : valide, aber ungültig. Quelle https://old.formvalidation.io/validators/vat/
    SE 5560528514 01 : gültig Scandinavian Eyewear AB, JÖNKÖPING. Quelle https://www.adresslabor.de/en/products/vat-id-no-check.html
    SE 5566801444 01 : gültig XLN Audio AB, STOCKHOLM
    SE 5565102471 01 : gültig amo kraftkabel AB, ALSTERMO
     */
    @BeforeEach
    protected void setUp() {
        routine = VATidSECheckDigit.getInstance();
        valid = new String[] {"0000000018" // theoretical minimum (invalid, because starts with 0)
            , "1366959755", "5561888404", "1234567897", "5560528514", "5566801444", "5565102471"
            , "5565102570" // checkdigit zero
            , "9999999999" // theoretical maximum
            };
    }

}
