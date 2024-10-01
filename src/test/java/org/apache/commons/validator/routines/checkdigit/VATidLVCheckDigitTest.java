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
 * LV VAT Id Check Digit Tests.
 */
public class VATidLVCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
Beispiele

    LV 54103040601 : gültig "ŽAGARKALNS" SIA
    LV 40003567907 : gültig Pasažieru vilciens, vivi.lv
    LV 40003009497 : valide, aber ungültig, da inaktiv (PROFESIONĀLĀS KARJERAS IZVĒLES CENTRS). Quelle: SAP
    LV 90000069281 : gültig Valsts ieņēmumu dienests, Rīga, aus vid.gov.lv
    LV 40203551190 : gültig, aus https://company.lursoft.lv/en/marani-resto/
    LV 40103161235 : gültig "C.P.S. BALTIC", Rīga
    LV 40103151608 : gültig "LEM MODA", Rīga
    LV 40003022654 : gültig "LIDO", Rīga
    LV 07091910933 : Natural person (inaktiv) PUMPURA REGĪNA
    LV 23028318902 : gültige TIN nach eu check-tin

     */
    @BeforeEach
    protected void setUp() {
        routine = VATidLVCheckDigit.getInstance();
        valid = new String[] {"40003009497" // aus SAP Format 1: Legal persons
            , "54103040601" // "ŽAGARKALNS" SIA
            , "40003567907" // Pasažieru vilciens
            , "90000069281" // Valsts ieņēmumu dienests
            , "40203551190" // lursoft.lv
            , "40103161235" // C.P.S. BALTIC
            , "40103151608" // LEM MODA
            , "40003022654" // LIDO
            , "07091910933", "23028318902", "01010020932", "18097200928", "18097230924"
            , "32053410932", "32013410209", "32579461005" // starts with "32" TIN without date, No VAT payer
            , "32132113936"
            };
        invalid = new String[] {"31129910930"
            };
    }

}
