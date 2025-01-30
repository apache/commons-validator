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
 * NL VAT Id Check Digit Tests.
 * <pre>
     without last 3 chars
    NL 123456782B12 , 010000446B01 : valide, aber ungültig
    NL 003660564B01 : gültig STAM + DE KONING BOUW B.V. EINDHOVEN
    NL 004495445B01 : gültig OPENJONGERENVERENIGING DE KOORNBEURS, DELFT
    NL 809944686B01 , 803872987B01 : valide, aber ungültig
    NL004350351B91 , see https://github.com/apache/commons-validator/pull/271#issuecomment-2622952256
    NL123456789B13 , BMF_UID_Konstruktionsregeln_Stand_November 2020.pdf

 * </pre>
 */
public class VATidNLCheckDigitTest extends AbstractCheckDigitTest {

   /*
    * Sets up routine & valid codes.
    */
    @BeforeEach
    protected void setUp() {
        routine = VATidNLCheckDigit.getInstance();
        valid = new String[] {"123456782", "010000446", "003660564", "004495445", "809944686"
            , "803872987"
            , "004350351B91", "123456789B13"
            };
        invalid = new String[] {"010001440"}; // checkdigit X / 10 is invalid
    }

    protected String checkDigit(final String code) {
        if (code == null || code.length() <= checkDigitLth) {
            return "";
        }
        int cdl = checkDigitLth;
        try {
            if (code.charAt(9) == 'B') {
                cdl = 2;
           }
        } catch (final IndexOutOfBoundsException e) {
            // expected for MOD-11 Validation
        }
        final int start = code.length() - cdl;
        return code.substring(start);
    }

    protected String removeCheckDigit(final String code) {
        if (code == null || code.length() <= checkDigitLth) {
            return null;
        }
        int cdl = checkDigitLth;
        try {
            if (code.charAt(9) == 'B') {
                cdl = 2;
           }
        } catch (final IndexOutOfBoundsException e) {
            // expected for MOD-11 Validation
        }
        return code.substring(0, code.length() - cdl);
    }

}
