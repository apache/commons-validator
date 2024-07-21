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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.validator.routines.IBANValidator.Validator;
import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link IBANValidator}.
 */
public class IBANValidatorTest {

    private static final IBANValidator VALIDATOR = IBANValidator.getInstance();

    // Unfortunately Java only returns the last match of repeated patterns
    // Use a manual repeat instead
    private static final String IBAN_PART = "(?:(\\d+)!([acn]))"; // Assume all parts are fixed length

    private static final Pattern IBAN_PAT = Pattern
            .compile("([A-Z]{2})" + IBAN_PART + IBAN_PART + IBAN_PART + IBAN_PART + "?" + IBAN_PART + "?" + IBAN_PART + "?" + IBAN_PART + "?");

    // It's not clear whether IBANs can contain lower case characters
    // so we test for both where possible
    // Note that the BIC near the start of the code is always upper case or digits
    // @formatter:off
    private static final String[] VALID_IBAN_FIXTURES = {
            "AD1200012030200359100100",
            "AE070331234567890123456",
            "AL47212110090000000235698741",
            "AT611904300234573201",
            "AZ21NABZ00000000137010001944",
            "BA391290079401028494",
            "BE68539007547034",
            "BG80BNBG96611020345678",
            "BH67BMAG00001299123456",
            "BI4210000100010000332045181",
            "BR1800000000141455123924100C2",
            "BR1800360305000010009795493C1",
            "BR9700360305000010009795493P1",
            "BY13NBRB3600900000002Z00AB00",
            "CH9300762011623852957",
            "CR05015202001026284066",
            "CY17002001280000001200527600",
            "CZ6508000000192000145399",
            "CZ9455000000001011038930",
            "DE89370400440532013000",
            "DJ2110002010010409943020008",
            "DK5000400440116243",
            "DO28BAGR00000001212453611324",
            "EE382200221020145685",
            "EG380019000500000000263180002",
            "ES9121000418450200051332",
            "FI2112345600000785",
            "FI5542345670000081",
              // FI other
              "AX2112345600000785", // FI other
              "AX5542345670000081", // FI other
            "FK88SC123456789012",
            "FO6264600001631634",
            "FR1420041010050500013M02606",
              // FR 'other'
              "BL6820041010050500013M02606", // FR other
              "GF4120041010050500013M02606", // FR other
              "GP1120041010050500013M02606", // FR other
              "MF8420041010050500013M02606", // FR other
              "MQ5120041010050500013M02606", // FR other
              "NC8420041010050500013M02606", // FR other
              "PF5720041010050500013M02606", // FR other
              "PM3620041010050500013M02606", // FR other
              "RE4220041010050500013M02606", // FR other
              "TF2120041010050500013M02606", // FR other
              "WF9120041010050500013M02606", // FR other
              "YT3120041010050500013M02606", // FR other
            "GB29NWBK60161331926819",
              // GB 'other'
//              "IM...", // GB other
//              "JE...", // GB other
//              "GG...", // GB other
            "GE29NB0000000101904917",
            "GI75NWBK000000007099453",
            "GL8964710001000206",
            "GR1601101250000000012300695",
            "GT82TRAJ01020000001210029690",
            "HR1210010051863000160",
            "HU42117730161111101800000000",
            "IE29AIBK93115212345678",
            "IL620108000000099999999",
            "IQ98NBIQ850123456789012",
            "IS140159260076545510730339",
            "IT60X0542811101000000123456",
            "JO94CBJO0010000000000131000302",
            "KW81CBKU0000000000001234560101",
            "KZ86125KZT5004100100",
            "LB62099900000001001901229114",
            "LC55HEMM000100010012001200023015",
            "LI21088100002324013AA",
            "LT121000011101001000",
            "LU280019400644750000",
            "LY83002048000020100120361",
            "LV80BANK0000435195001",
            "LY83002048000020100120361",
            "MC5811222000010123456789030",
            "MD24AG000225100013104168",
            "ME25505000012345678951",
            "MK07250120000058984",
            "MN121234123456789123",
            "MR1300020001010000123456753",
            "MT84MALT011000012345MTLCAST001S",
            "MU17BOMM0101101030300200000MUR",
            "NI45BAPR00000013000003558124",
            "NL91ABNA0417164300",
            "NO9386011117947",
            "OM810180000001299123456",
            "PK36SCBL0000001123456702",
            "PL61109010140000071219812874",
            "PS92PALS000000000400123456702",
            "PT50000201231234567890154",
            "QA58DOHB00001234567890ABCDEFG",
            "RO49AAAA1B31007593840000",
            "RS35260005601001611379",
            "RU0204452560040702810412345678901",
            "SA0380000000608010167519",
            "SC18SSCB11010000000000001497USD",
            "SD8811123456789012",
            "SE4550000000058398257466",
            "SI56191000000123438",
            "SI56263300012039086",
            "SK3112000000198742637541",
            "SM86U0322509800000000270100",
            "SO211000001001000100141",
            "ST68000100010051845310112",
            "SV62CENR00000000000000700025",
            "SV43ACAT00000000000000123123",
            "TL380080012345678910157",
            "TN5910006035183598478831",
            "TR330006100519786457841326",
            "UA213223130000026007233566001",
            "UA213996220000026007233566001",
            "VA59001123000012345678",
            "VG96VPVG0000012345678901",
            "XK051212012345678906",
    };
    // @formatter:on

    // @formatter:off
    private static final String[] INVALID_IBAN_FIXTURES = {
            "",                        // empty
            "   ",                     // empty
            "A",                       // too short
            "AB",                      // too short
            "FR1420041010050500013m02606", // lowercase version
            "MT84MALT011000012345mtlcast001s", // lowercase version
            "LI21088100002324013aa", // lowercase version
            "QA58DOHB00001234567890abcdefg", // lowercase version
            "RO49AAAA1b31007593840000", // lowercase version
            "LC62HEMM000100010012001200023015", // wrong in SWIFT
            "BY00NBRB3600000000000Z00AB00", // Wrong in SWIFT v73
            "ST68000200010192194210112", // ditto
            "SV62CENR0000000000000700025", // ditto
    };
    // @formatter:on

    private static int checkIBAN(final File file, final IBANValidator val) throws Exception {
        // The IBAN Registry (TXT) file is a TAB-separated file
        // Rows are the entry types, columns are the countries
        final CSVFormat format = CSVFormat.DEFAULT.builder().setDelimiter('\t').build();
        final Reader rdr = new InputStreamReader(new FileInputStream(file), "ISO_8859_1");
        try (final CSVParser p = new CSVParser(rdr, format)) {
            CSVRecord country = null;
            CSVRecord cc = null;
            CSVRecord structure = null;
            CSVRecord length = null;
            for (final CSVRecord o : p) {
                final String item = o.get(0);
                if ("Name of country".equals(item)) {
                    country = o;
                } else if ("IBAN prefix country code (ISO 3166)".equals(item)) {
                    cc = o;
                } else if ("IBAN structure".equals(item)) {
                    structure = o;
                } else if ("IBAN length".equals(item)) {
                    length = o;
                }
            }
            assertNotNull(country);
            assertNotNull(length);
            assertNotNull(structure);
            assertNotNull(cc);
            for (int i = 1; i < country.size(); i++) {
                try {

                    final String newLength = length.get(i).split("!")[0]; // El Salvador currently has "28!n"
                    final String newRE = fmtRE(structure.get(i), Integer.parseInt(newLength));
                    final Validator valre = val.getValidator(cc.get(i));
                    if (valre == null) {
                        System.out.println("// Missing entry:");
                        printEntry(cc.get(i), newLength, newRE, country.get(i));
                    } else {
                        final String currentLength = Integer.toString(valre.getIbanLength());
                        final String currentRE = valre.getRegexValidator().toString().replaceAll("^.+?\\{(.+)}", "$1") // Extract RE from RegexValidator{re}
                                                                                                                       // string
                                .replace("\\d", "\\\\d"); // convert \d to \\d
                        // The above assumes that the RegexValidator contains a single Regex
                        if (currentRE.equals(newRE) && currentLength.equals(newLength)) {

                        } else {
                            System.out.println("// Expected: " + newRE + ", " + newLength + " Actual: " + currentRE + ", " + currentLength);
                            printEntry(cc.get(i), newLength, newRE, country.get(i));
                        }

                    }

                } catch (final IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            p.close();
            return country.size();
        }
    }

    private static String fmtRE(final String ibanPat, final int ibanLen) {
        final Matcher m = IBAN_PAT.matcher(ibanPat);
        if (!m.matches()) {
            throw new IllegalArgumentException("Unexpected IBAN pattern " + ibanPat);
        }
        final StringBuilder sb = new StringBuilder();
        final String cc = m.group(1); // country code
        int totalLen = cc.length();
        sb.append(cc);
        int len = Integer.parseInt(m.group(2)); // length of part
        String curType = m.group(3); // part type
        for (int i = 4; i <= m.groupCount(); i += 2) {
            if (m.group(i) == null) { // reached an optional group
                break;
            }
            final int count = Integer.parseInt(m.group(i));
            final String type = m.group(i + 1);
            if (type.equals(curType)) { // more of the same type
                len += count;
            } else {
                sb.append(formatToRE(curType, len));
                totalLen += len;
                curType = type;
                len = count;
            }
        }
        sb.append(formatToRE(curType, len));
        totalLen += len;
        if (ibanLen != totalLen) {
            throw new IllegalArgumentException("IBAN pattern " + ibanPat + " does not match length " + ibanLen);
        }
        return sb.toString();
    }

    // convert IBAN type string and length to regex
    private static String formatToRE(final String type, final int len) {
        final char ctype = type.charAt(0); // assume type.length() == 1
        switch (ctype) {
        case 'n':
            return String.format("\\\\d{%d}", len);
        case 'a':
            return String.format("[A-Z]{%d}", len);
        case 'c':
            return String.format("[A-Z0-9]{%d}", len);
        default:
            throw new IllegalArgumentException("Unexpected type " + type);
        }
    }

    public static void main(final String[] a) throws Exception {
        final IBANValidator validator = new IBANValidator();
        final File ibanTsv = new File("target", "iban-registry.tsv");
        int countries = 0;
        if (ibanTsv.canRead()) {
            countries = checkIBAN(ibanTsv, validator);
        } else {
            System.out.println("Please load the file " + ibanTsv.getCanonicalPath() + " from https://www.swift.com/standards/data-standards/iban");
        }
        System.out.println("Processed " + countries + " countries.");
    }

    private static void printEntry(final String ccode, final String length, final String ib, final String country) {
        final String fmt = String.format("\"%s\"", ib);
        System.out.printf("            new Validator(\"%s\", %s, %-40s), // %s\n", ccode, length, fmt, country);
    }

    @Test
    public void testGetRegexValidatortPatterns() {
        assertNotNull(VALIDATOR.getValidator("GB").getRegexValidator().getPatterns(), "GB");
    }

    @Test
    public void testGetValidator() {
        assertNotNull(VALIDATOR.getValidator("GB"), "GB");
        assertNull(VALIDATOR.getValidator("gb"), "gb");
    }

    @Test
    public void testHasValidator() {
        assertTrue(VALIDATOR.hasValidator("GB"), "GB");
        assertFalse(VALIDATOR.hasValidator("gb"), "gb");
    }

    @Test
    public void testInValid() {
        for (final String f : INVALID_IBAN_FIXTURES) {
            assertFalse(VALIDATOR.isValid(f), f);
        }
    }

    @Test
    public void testNull() {
        assertFalse(VALIDATOR.isValid(null), "isValid(null)");
    }

    @Test
    public void testSetDefaultValidator1() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> VALIDATOR.setValidator("GB", 15, "GB"));
        assertThat(thrown.getMessage(), is(equalTo("The singleton validator cannot be modified")));
    }

    @Test
    public void testSetDefaultValidator2() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> VALIDATOR.setValidator("GB", -1, "GB"));
        assertThat(thrown.getMessage(), is(equalTo("The singleton validator cannot be modified")));
    }

    @Test
    public void testSetValidatorLC() {
        final IBANValidator validator = new IBANValidator();
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> validator.setValidator("gb", 15, "GB"));
        assertThat(thrown.getMessage(), is(equalTo("Invalid country Code; must be exactly 2 upper-case characters")));
    }

    @Test
    public void testSetValidatorLen1() {
        final IBANValidator validator = new IBANValidator();
        assertNotNull(validator.setValidator("GB", -1, ""), "should be present");
        assertNull(validator.setValidator("GB", -1, ""), "no longer present");
    }

    @Test
    public void testSetValidatorLen35() {
        final IBANValidator validator = new IBANValidator();
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> validator.setValidator("GB", 35, "GB"));
        assertThat(thrown.getMessage(), is(equalTo("Invalid length parameter, must be in range 8 to 34 inclusive: 35")));
    }

    @Test
    public void testSetValidatorLen7() {
        final IBANValidator validator = new IBANValidator();
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> validator.setValidator("GB", 7, "GB"));
        assertThat(thrown.getMessage(), is(equalTo("Invalid length parameter, must be in range 8 to 34 inclusive: 7")));
    }

    @Test
    public void testSorted() {
        final IBANValidator validator = new IBANValidator();
        final Validator[] vals = validator.getDefaultValidators();
        assertNotNull(vals);
        for (int i = 1; i < vals.length; i++) {
            if (vals[i].countryCode.compareTo(vals[i - 1].countryCode) <= 0) {
                fail("Not sorted: " + vals[i].countryCode + " <= " + vals[i - 1].countryCode);
            }
        }
    }

    @Test
    public void testValid() {
        for (final String f : VALID_IBAN_FIXTURES) {
            assertTrue(IBANCheckDigit.IBAN_CHECK_DIGIT.isValid(f), "Checksum fail: " + f);
            assertTrue(VALIDATOR.hasValidator(f), "Missing validator: " + f);
            assertTrue(VALIDATOR.isValid(f), f);
        }
    }
}
