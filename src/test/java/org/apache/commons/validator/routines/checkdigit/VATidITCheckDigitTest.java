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
 * IT VAT Id Check Digit Tests.
 * </pre>

    IT 01680870670 : gültig "Confezioni Valentina Srl" in Provinz Teramo (TE)
    IT 04536640404 : gültig spiagge.it, Rimini (RN)
    IT 00950501007 : gültig "BANCA D'ITALIA" aus https://github.com/digitalfondue/vatchecker
    IT 02866820240 : gültig aus https://www.valbruna-stainless-steel.com/
    IT 12345670017 : gültig geprüft mit VIES, id aus http://sima.cat/nif.php Provincia di Torino
    IT 00000010215 : valide, aber ungültig (aus BMF_UID_Konstruktionsregeln.pdf bmf.gv.at)
    IT 12345670785 : valide, aber ungültig (aus pruefziffernberechnung.de)
    IT 12345678903 : nicht valide, da y1-3 = 890 (aus icosaedro.it) - ist aber Luhn-valide

 * </pre>
 */
public class VATidITCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = LuhnCheckDigit.LUHN_CHECK_DIGIT;
        valid = new String[] {"01680870670"
            , "04536640404"
            , "00950501007"
            , "02866820240"
            , "12345670017"
            , "00000010215"
            , "12345670785"
            , "12345678903"
            };
    }

}
