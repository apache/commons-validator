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
 * PT VAT Id Check Digit Tests.
 */
public class VATidPTCheckDigitTest extends AbstractCheckDigitTest {

   /*
    * Sets up routine & valid codes.
    *
Beispiele

    PT 502757191 : valide, aber ungültig. Quelle bmf.gv.at
    PT 136695973 : valide, aber ungültig. Quelle pruefziffernberechnung.de
    PT 501964843 : valide, aber ungültig. Quelle https://old.formvalidation.io/validators/vat/
    PT 504308548 : valide, aber ungültig. Quelle https://www.adresslabor.de/en/products/vat-id-no-check.html
    PT 510728189 : gültig CLARANET II SOLUTIONS, S.A. PORTO

    */
    @BeforeEach
    protected void setUp() {
        routine = VATidPTCheckDigit.getInstance();
        valid = new String[] {"000000019" // theoretical minimum
            , "502757191", "136695973", "501964843", "504308548", "510728189"
            , "000000310" // checkdigit zero
            , "999999990" // theoretical maximum
            };
        invalid = new String[] {"0"};
    }

}
