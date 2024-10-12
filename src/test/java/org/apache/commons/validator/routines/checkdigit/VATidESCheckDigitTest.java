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
 * ES VAT Id Check Digit Tests.
 * <pre>

    ES A60195278 : gültig, LIDL SUPERMERCADOS, S.A.U.
    ES F20033361 : gültig, EROSKI, S. COOP. Barrio San Agustín
    ES A13585625 : valide, aber ungültig. Firmensteuernummer, aus pruefziffernberechnung.de
    ES 54362315K : valide, aber ungültig. Privatperson, gleiche Quelle.
    ES 14567852X : nicht valide. Privatperson, aus sap.com und
    ES A58818501 : valide, aber ungültig
    ES X2482300W B58378431: gültig aus https://old.formvalidation.io/validators/vat/
    ES 54362315K X5253868R M1234567L J99216582 B64717838 : valide, aber ungültig (gleiche Quelle),
    ES 54362315Z X2482300A J99216583 : nicht valide (gleiche Quelle).
    ES B28318236 B63618474 F20096525 gültig aus https://www.adresslabor.de/en/products/vat-id-no-check.html
    ES B63879597 J61863718 sind valide, aber ungültig
    ES W8265365J : gültig aus stdnum-js
    ES J99216582 B86670460 Q2876031B N0112768G sind valide, aber ungültig

 * </pre>
 */
public class VATidESCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = VATidESCheckDigit.getInstance();
        valid = new String[] {"A60195278", "F20033361", "A13585625", "54362315K"
            , "A58818501"
            , "X2482300W", "B58378431" // gültig lt. VIES
            , "X5253868R", "M1234567L", "J99216582", "B64717838"
            , "B28318236", "B28318236", "F20096525"  // gültig lt. VIES
            , "B63879597", "J61863718"
            , "W8265365J"  // gültig lt. VIES
            , "J99216582", "B86670460", "Q2876031B", "N0112768G"
            , "A10215" // valid Luhn, but to short - checked in VATINValidator
            };
        invalid = new String[] {"14567852X", "54362315Z", "X2482300A", "J99216583"};
    }

}
