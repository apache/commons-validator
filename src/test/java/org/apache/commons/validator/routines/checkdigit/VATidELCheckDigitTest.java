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
 * EL VAT Id Check Digit Tests.
 * <pre>

    EL 123456783 : gültig ΚΟΛΛΑΡΟΣ ΑΝΤΩΝΙΟΣ ΕΥΑΓΓΕΛΟΣ. Quelle: pruefziffernberechnung.de
    EL 040127797 : valide, aber ungültig. Quelle: SAP
    GR 023456780 : nicht valide wg. Prefix, aus https://old.formvalidation.io/validators/vat/
    EL 094259216 : gültig ΙΝΤΕΡ ΝΤΥΝΑΜΙΚ ΑΝΩΝ ΤΟΥΡ ΞΕΝΟΔ ΚΑΙ ΕΜΠΟΡ ΕΤΑΙΡΕΙΑ
    EL 998537832 : gültig TRYGONS ΑΕ aus aus adresslabor.de und
    EL 094327684 : gültig GENERALI HELLAS Α Α Ε
    EL  94327684 : dto aber zu kurz, führende Null fehlt ==> Länge wird in VATINValidator geprüft

 * </pre>
 */
public class VATidELCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = VATidELCheckDigit.getInstance();
        valid = new String[] {"040127797"
            , "023456780", "094259216"
            , "998537832", "094327684"
            , "000000130"
            };
        invalid = new String[] {"0", "00", "000" // ZeroSum
            , "1456785X"
            , "123456781" // check digit expected 3
            , "X2482300"};
    }

}
