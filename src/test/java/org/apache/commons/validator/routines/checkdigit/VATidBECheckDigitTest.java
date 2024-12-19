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
 * BE VAT Id Check Digit Tests.
 * <pre>

    BE 0441826783 : gültig AG ROM, Industriestrasse 38, 4700 Eupen
    BE 441826783 : dto neunstellig, nicht gültig (Länge wird nicht in VATidBECheckDigit, sondern in VATINValidator geprüft)
    BE 0888888895 : gültig
    BE 0136695962 : valide, aber ungültig
    BE 0776091951 : valide, aber ungültig

 * </pre>
 */
public class VATidBECheckDigitTest extends AbstractCheckDigitTest {

    public VATidBECheckDigitTest() {
        checkDigitLth = VATidBECheckDigit.CHECKDIGIT_LEN;
    }
   /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = VATidBECheckDigit.getInstance();
        valid = new String[] {"1234567894"
            , "441826783", "0441826783"
            , "136695962", "0136695962"
            , "0776091951", "0888888895"};
    }

}
