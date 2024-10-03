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
 * SI VAT Id Check Digit Tests.
 */
public class VATidSICheckDigitTest extends AbstractCheckDigitTest {

   /*
    * Sets up routine & valid codes.

Beispiele
- SI `15012557` : valide, aber ungültig. Quelle bmf.gv.at
- SI `59082437` : valide, aber ungültig. Quelle pruefziffernberechnung.de
- SI `50223054` : gültig POSTOJNSKA JAMA, D.D. POSTOJNA. Quelle https://old.formvalidation.io/validators/vat/
- SI `45063575` `56494416` : valide, aber ungültig. Quelle https://www.adresslabor.de/en/products/vat-id-no-check.html
- SI `21649405` : gültig STANOVANJSKO PODJETJE KONJICE D.O.O.
    */
    @BeforeEach
    protected void setUp() {
        routine = VATidSICheckDigit.getInstance();
        valid = new String[] {"00000019" // theoretical minimum (invalid, because starts with 0)
            , "15012557", "59082437", "50223054", "45063575", "56494416", "21649405"
            , "15012670" // checkdigit zero
            , "99999994" // theoretical maximum
            };
    }

}
