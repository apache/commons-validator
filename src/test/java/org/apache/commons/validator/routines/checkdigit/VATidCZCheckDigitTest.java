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
 * CZ VAT Id Check Digit Tests.
 * <pre>

    CZ 45799504 : gültig Nestlé Česko s.r.o.
    CZ 29042828 : gültig SVĚT KÁVOVARŮ s.r.o., STRANČICE
    CZ 00311391 : gültig CENDIS, s.p., PRAHA
    CZ 24276855 : gültig JM-Networks, s.r.o., PRAHA
    CZ 46505334 : valide und gültig, Skip Events s.r.o. PRAHA 1 - STARÉ MĚSTO. Aus sap.com
    CZ 25123891 : gültig K+K Hotel s.r.o. PRAHA 1 - STARÉ MĚSTO. Aus https://old.formvalidation.io/validators/vat/
    CZ 395601439 640903926: valide, aber ungültig. (9-stellig - gleiche Quelle)
    CZ 7103192745 : gültig Josef Mazánek, CHABAŘOVICE (born 71)
    CZ 991231123 640903926 : valide, aber ungültig. Gleiche Quelle
    CZ 6852294449 7952290292 8160010610 110101111 1152291111 6956220612 7211240180 : aus TIN_-_country_sheet_CZ_de.pdf

 * </pre>
 */
public class VATidCZCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = VATidCZCheckDigit.getInstance();
        valid = new String[] {"19", "00000019" // theoretical minimum
            , "89999991"  // theoretical maximum for legal entity
            , "45799504", "29042828", "00311391", "24276855"
            , "46505334", "25123891"
            // lenght 9 and 10 for physical persons, IČO : Identifikační číslo osoby :
            , "640903926", "600000010" // LEN9ICO starts with "6" => with check digit
            , "7103192745"
            , "6852294449", "6956220612", "7211240180"
            , "7704063345"
            , "9982319996" // RČ theoretical maximum (a women, born 1999-12-31)
            };
        invalid = new String[] {"99999994" // legal entities : first char cannot be '9'
            , "395601439" // LEN9ICO without check digit, Female born 1939-06-01
            , "991231123" // dto
            , "1113311111" // NOT valid, because 2011/13/31 is invalid date
            };
    }

}
