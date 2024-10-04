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
package org.apache.commons.validator.routines;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.commons.validator.routines.VATINValidator.Validator;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link VATINValidator}.
 */
public class VATINValidatorTest {

    private static final VATINValidator VALIDATOR = VATINValidator.getInstance();

    // Eclipse 3.6 allows you to turn off formatting by placing a special comment, like
    // @formatter:off
    private static final String[] VALID_VATIN_FIXTURES = {
            "ATU10223006",  // see BMF_UID_Konstruktionsregeln
            "ATU13585627",  // see http://www.pruefziffernberechnung.de/U/USt-IdNr.shtml#PZAT
            "ATU54065602",  // https://zukunftsregion-steyr.at/impressum
            "BE0136695962", // see http://www.pruefziffernberechnung.de/U/USt-IdNr.shtml#PZBE (neues Format)
            "BE0776091951", // see BMF_UID_Konstruktionsregeln
            "BG831650349",  // (Nestlé) НЕСТЛЕ БЪЛГАРИЯ - АД
            "BG8001010008", // a male person born 01.01.1980
            "CY30010823A",  // LIDL Cyprus
            "CZ29042828",   // legal entity SVĚT KÁVOVARŮ s.r.o., STRANČICE
            "CZ6852294449", // physical person
            "DE000000011",  // theoretical minimum
            "DE136695976",  // see http://www.pruefziffernberechnung.de/U/USt-IdNr.shtml#PZDE
            "DK88146328",   // see BMF_UID_Konstruktionsregeln
            "DK13585628",   // see http://www.pruefziffernberechnung.de/U/USt-IdNr.shtml#PZDK
            "EE101571629",   // Kaubamaja AS, Tallinn
            "EE100594102",   // MOVEK GRUPP
            "EL998537832",   // TRYGONS ΑΕ
            "EL094327684",   // GENERALI HELLAS Α Α Ε
            "ESA60195278",   // LIDL SUPERMERCADOS, S.A.U.
            "ES54362315K",   // Españoles con DNI
            "ESB58378431",   // Sociedades de responsabilidad limitada
            "ESX2482300W",   // Extranjeros residentes
            "ESW8265365J",   // Establecimientos permanentes de entidades no residentes en España
            "FI13669598",    // see http://www.pruefziffernberechnung.de/U/USt-IdNr.shtml#PZFI
            "FI01745928",    // see https://tarkistusmerkit.teppovuori.fi/tarkmerk.htm#y-tunnus2
            "FI09853608",    // see BMF_UID_Konstruktionsregeln
            "FR40303265045", // SA SODIMAS; Adresse 11 RUE AMPERE, 26600 PONT DE L ISERE
            "FRK7399859412", // SLRL ALTEA EXPERTISE COMPTABLE
            "FR06399859412", "FRB0399859412", "FRY0399859412", "FRB1399859412", // not unique check digit
            "FRT1399859412", "FRH2399859412", "FRN2399859412", "FRC3399859412", "FRU3399859412",
            "FRZ3399859412", "FRJ4399859412", "FRP4399859412", "FRD5399859412", "FRJ5399859412",
            "FRQ6399859412", "FRV6399859412", "FRE7399859412", "FRR8399859412", "FRW8399859412",
            "FRL9399859412", "FRR9399859412", "FR0J399859412", "FR1J399859412", "FR2K399859412",
            "FR3K399859412", "FR4K399859412", "FR5K399859412", "FR6K399859412", "FR6L399859412",
            "FR7L399859412", "FR8L399859412", "FR9L399859412", "FR0W399859412", "FR1X399859412",
            "FR2X399859412", "FR3X399859412", "FR4X399859412", "FR5X399859412", "FR6Y399859412",
            "FR7Y399859412", "FR8Y399859412", "FR9Y399859412",
            "FR37000005990", "FR64000059252", "FR03000116300", // special cases Monaco (not EU) with French VATIN (invalid SIREN)
            "HR33392005961",  // NASTAVNI ZAVOD ZA JAVNO ZDRAVSTVO DR. ANDRIJA ŠTA, Zagreb
            "HU12892312",     // CNCEDU KERESKEDELMI ÉS SZOLGÁLTATÓ KORLÁTOLT FELELŐSSÉGŰ TÁRSASÁG
            "IE6388047V",     // GOOGLE IRELAND LIMITED
            "IE3628739UA",    // check digit next-to-last position
            "IT00950501007",  // BANCA D'ITALIA
            "IT02866820240",  // see https://www.valbruna-stainless-steel.com/
            "LT582708716",    // ASIMA
            "LT100014579016", // Senoji rotonda
            "LU25180625",     // snct.lu
            "LV40003567907",  // legal entity Pasažieru vilciens
            "LV07091910933",  // Natural person, born 1919
            "LV01010020932",  // Natural person, born 2000/01/01
            "LV32053410932",  // Natural person, no birthday coded
            "MT20200019",     // BAJADA NEW ENERGY LIMITED, MRS3000 Marsa
            "NL003660564B01", // STAM + DE KONING BOUW B.V. EINDHOVEN
            "NL004495445B01", // OPENJONGERENVERENIGING DE KOORNBEURS, DELFT
            "PL1060000062",   // aus https://pl.wikipedia.org/wiki/Numer_identyfikacji_podatkowej
            "PT510728189",    // CLARANET II SOLUTIONS, S.A. PORTO
            "RO27825131",     // NUTRISOYA SRL
            "RO6255950",      // P L NORIS SRL
            "SE556680144401", // XLN Audio AB, STOCKHOLM
            "SI21649405",     // STANOVANJSKO PODJETJE KONJICE D.O.O.
            "SK2120567108",   // Markon s.r.o. Bratislava

            "XI110305878",    // https://www.bullseyecountrysport.co.uk/contact-us-2-w.asp
            "XI366303068",    // donnellygroup.co.uk
    };
    // @formatter:on

    // @formatter:off
    private static final String[] INVALID_VATIN_FIXTURES = {
            "",                        // empty
            "   ",                     // empty
            "A",                       // too short
            "AB",                      // too short
            "AT99999999",     // too short, missing U:ATU
            "ATu99999999",    // lowercase U:ATU
            "ATU9999999",     // too short
            "BE136695962",    // aus http://www.pruefziffernberechnung.de/U/USt-IdNr.shtml#PZBE (altes 9-stelliges Format)
            "CY61234567I",    // Must not start with 2,6,7,8
            "DE00000000",     // too short
            "EL94327684",     // zu kurz, führende Null fehlt, korrekt: EL094327684
            "GR023456780",    // falscher Prefix, sollte EL sein!
            "ESA10215",       // too short
            "LT100008668621", // C11 is not 1
            "LV18097230924",  // invalid century
            "NL004495445B00", // Suffix "00"
            "RO027825131",    // Must not start with 0
            "SE136695975523", // Must not end with 23
            "SI00000019",     // Must not start with 0
            "SK0000000011",   // Must not start with 0
            "SK1111111111",   // 3rd digit must not be 1
            "MC37000005990",  // No CheckDigit routine for Monaco
    };
    // @formatter:on

    @Test
    public void testGetRegexValidatortPatterns() {
        assertNotNull(VALIDATOR.getValidator("DE").getRegexValidator().getPatterns(), "DE");
    }

    @Test
    public void testGetValidator() {
        assertNotNull(VALIDATOR.getValidator("FI"), "FI");
        assertNull(VALIDATOR.getValidator("fi"), "fi");
    }

    @Test
    public void testHasValidator() {
        assertTrue(VALIDATOR.hasValidator("FI"), "FI");
        assertFalse(VALIDATOR.hasValidator("fi"), "fi");
    }

    @Test
    public void testInValid() {
        for (final String f : INVALID_VATIN_FIXTURES) {
            assertFalse(VALIDATOR.isValid(f), f);
        }
    }

    @Test
    public void testNull() {
        assertFalse(VALIDATOR.isValid(null), "isValid(null)");
    }

    @Test
    public void testSetDefaultValidator1() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> VALIDATOR.setValidator("GB", 15, "GB", null));
        assertThat(thrown.getMessage(), is(equalTo("The singleton validator cannot be modified")));
    }

    @Test
    public void testSetDefaultValidator2() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> VALIDATOR.setValidator("GB", -1, "GB", null));
        assertThat(thrown.getMessage(), is(equalTo("The singleton validator cannot be modified")));
    }

    @Test
    public void testSetValidatorLC() {
        final VATINValidator validator = new VATINValidator();
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> validator.setValidator("gb", 15, "GB", null));
        assertThat(thrown.getMessage(), is(equalTo("Invalid country Code; must be exactly 2 upper-case characters")));
    }

    @Test
    public void testSetValidatorLen1() {
        final VATINValidator validator = new VATINValidator();
        assertNotNull(validator.setValidator("DE", -1, "", null), "should be present");
        assertNull(validator.setValidator("DE", -1, "", null), "no longer present");
    }

    private static final String INVALID_LENGTH = "Invalid length parameter, must be in range 10 to 16 inclusive:";

    @Test
    public void testSetValidatorLen35() {
        final VATINValidator validator = new VATINValidator();
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> validator.setValidator("DE", 35, "DE", null));
//        System.out.println("testSetValidatorLen35 : thrown.getMessage():" + thrown.getMessage());
        assertThat(thrown.getMessage(), is(equalTo(INVALID_LENGTH + " 35")));
    }

    @Test
    public void testSetValidatorLen7() {
        final VATINValidator validator = new VATINValidator();
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> validator.setValidator("GB", 7, "GB", null));
        assertThat(thrown.getMessage(), is(equalTo(INVALID_LENGTH + " 7")));
    }

    @Test
    public void testSorted() {
        final VATINValidator validator = new VATINValidator();
        final VATINValidator.Validator[] vals = validator.getDefaultValidators();
        assertNotNull(vals);
        for (int i = 1; i < vals.length; i++) {
            if (vals[i].countryCode.compareTo(vals[i - 1].countryCode) <= 0) {
                fail("Not sorted: " + vals[i].countryCode + " <= " + vals[i - 1].countryCode);
            }
        }
    }

    @Test
    public void testValid() {
        for (final String f : VALID_VATIN_FIXTURES) {
            assertTrue(VALIDATOR.isValid(f), "CheckDigit fail: " + f);
            assertTrue(VALIDATOR.hasValidator(f), "Missing validator: " + f);
            assertTrue(VALIDATOR.isValid(f), f);
        }
    }

    @Test
    public void testAddValididator() {
        final String code = "GB888851256";
        VATINValidator myValidator = new VATINValidator();
        // cannot validate a GB-VATIN-code because GB is not EU
        assertFalse(myValidator.isValid(code));
        // use the Northern Ireland (XI) validator for GB
        Validator v = myValidator.getValidator("XI123");
        assertNotNull(v);
        assertEquals("XI", v.countryCode, "countryCode");
        assertFalse(myValidator.hasValidator("GB"), "hasValidator GB");
        // add the XI validator routine for GB
        assertNull(myValidator.setValidator("GB", 14, "GB(\\d{3})?\\d{9}", v.routine), "no previous Validator");
        assertTrue(myValidator.hasValidator("GB"), "hasValidator GB");
        // now we can validate the GB-VATIN-code
        assertTrue(myValidator.isValid(code));
    }

}
