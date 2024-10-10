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
 * LU VAT Id Check Digit Tests.
 */
public class VATidLUCheckDigitTest extends AbstractCheckDigitTest {

    public VATidLUCheckDigitTest() {
        checkDigitLth = VATidLUCheckDigit.CHECKDIGIT_LEN;
    }
   /**
     * Sets up routine & valid codes.

    LU 25180625 : gültig aus snct.lu
    LU 15027442 : gültig HITEC LUXEMBOURG SA, L-8212 MAMER
    LU 13669580 : valide, aber ungültig. Aus sap-docu und pruefziffernberechnung.de
    LU 10000356 : valide, aber ungültig. Aus BMF_UID_Konstruktionsregeln.

     */
    @BeforeEach
    protected void setUp() {
        routine = VATidLUCheckDigit.getInstance();
        valid = new String[] {"25180625", "15027442", "13669580", "10000356"};
        invalid = new String[] {"00000000", "00008900"};
    }

}
