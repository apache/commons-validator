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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.validator.routines.IBANValidator.Validator;
import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests {@link IBANValidator}.
 */
public class IBANValidatorTest {

    private static final IBANValidator VALIDATOR = IBANValidator.getInstance();

    // Unfortunately Java only returns the last match of repeated patterns
    // Use a manual repeat instead
    private static final String IBAN_PART = "(?:(\\d+)!([acn]))"; // Assume all parts are fixed length

    private static final Pattern IBAN_PAT = Pattern
            .compile(IBAN_PART + IBAN_PART + IBAN_PART + IBAN_PART + "?" + IBAN_PART + "?" + IBAN_PART + "?" + IBAN_PART + "?");

    /*
     * The IBAN registry should be available from here:
     * https://www.swift.com/standards/data-standards/iban-international-bank-account-number
     * Care must be taken not to accidentally change the encoding, which for v99 appears to be Windows-1252 (cp1252)
     * (N.B. even this encoding may not properly account for all characters)
     * Please ensure you download from the page (right-click), and do not edit the file after download, as that may
     * change the contents.
     * At present the code does not need the entries which are likely to contain non-ASCII characters, but a corrupt
     * file helps no-one.
     */
    private static final String IBAN_REGISTRY = "iban_registry_v99.txt";
    private static final Charset IBAN_REGISTRY_CHARSET = Charset.forName("windows-1252");
    private static final int MS_PER_DAY = 1000 * 60 * 60 * 24;
    private static final long MAX_AGE_DAYS = 180; // how old registry can get (approx 6 months)



    // It's not clear whether IBANs can contain lower case characters
    // so we test for both where possible
    // Note that the BIC near the start of the code is always upper case or digits
    // @formatter:off
    private static final List<String> VALID_IBAN_FIXTURES = Arrays.asList(
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
            "HN88CABF00000000000250005469",
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
            "YE15CBYE0001018861234567891234"
    );
    // @formatter:on

    // @formatter:off
    private static final List<String> INVALID_IBAN_FIXTURES = Arrays.asList(
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
            "ST68000200010192194210112", // ditto - invalid example
            "SV62CENR0000000000000700025", // ditto
            "NI04BAPR00000013000003558124", // invalid example
            "RU1704452522540817810538091310419" // invalid example
    );
    // @formatter:on

    private static String fmtRE(final String ibanPat) {
        final Matcher m = IBAN_PAT.matcher(ibanPat);
        if (!m.matches()) {
            throw new IllegalArgumentException("Unexpected IBAN pattern " + ibanPat);
        }
        final StringBuilder sb = new StringBuilder();
        int len = Integer.parseInt(m.group(1)); // length of part
        String curType = m.group(2); // part type
        for (int i = 3; i <= m.groupCount(); i += 2) {
            if (m.group(i) == null) { // reached an optional group
                break;
            }
            final int count = Integer.parseInt(m.group(i));
            final String type = m.group(i + 1);
            if (type.equals(curType)) { // more of the same type
                len += count;
            } else {
                sb.append(formatToRE(curType, len));
                curType = type;
                len = count;
            }
        }
        sb.append(formatToRE(curType, len));
        return sb.toString();
    }

    // convert IBAN type string and length to regex
    private static String formatToRE(final String type, final int len) {
        final char ctype = type.charAt(0); // assume type.length() == 1
        switch (ctype) {
        case 'n':
            return String.format("\\d{%d}", len);
        case 'a':
            return String.format("[A-Z]{%d}", len);
        case 'c':
            return String.format("[A-Z0-9]{%d}", len);
        default:
            throw new IllegalArgumentException("Unexpected type " + type);
        }
    }

    static Collection<Arguments> ibanRegistrySource() throws Exception {
        final Path ibanRegistry = Paths.get(IBANValidator.class.getResource(IBAN_REGISTRY).toURI());

        final CSVFormat format = CSVFormat.DEFAULT.builder().setDelimiter('\t').build();
        final Reader rdr = Files.newBufferedReader(ibanRegistry, IBAN_REGISTRY_CHARSET);

        CSVRecord country = null;
        CSVRecord cc = null;
        CSVRecord additionalCc = null;
        CSVRecord structure = null;
        CSVRecord length = null;

        try (CSVParser p = new CSVParser(rdr, format)) {
            for (final CSVRecord o : p) {
                final String item = o.get(0);
                switch (item) {
                    case "Name of country":
                        country = o;
                        break;
                    case "IBAN prefix country code (ISO 3166)":
                        cc = o;
                        break;
                    case "Country code includes other countries/territories":
                        additionalCc = o;
                        break;
                    case "IBAN structure":
                        structure = o;
                        break;
                    case "IBAN length":
                        length = o;
                        break;
                    default:
                        break;
                }
            }
        }

        assertNotNull(country);
        assertNotNull(cc);
        assertNotNull(additionalCc);
        assertNotNull(structure);
        assertNotNull(length);

        final Collection<Arguments> result = new ArrayList<>();
        for (int i = 1; i < country.size(); i++) {
            final String ac = additionalCc.get(i);
            final List<String> aCountry = Arrays.stream(ac.split(","))
                    .filter(s -> !"N/A".equals(s))
                    .map(s -> s.replace("(French part)", "")) // special case
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            result.add(Arguments.of(country.get(i), cc.get(i), aCountry, Integer.parseInt(length.get(i)), structure.get(i)));
        }

        return result;
    }

    static Collection<Arguments> ibanRegistrySourceExamples() throws Exception {
        final Path ibanRegistry = Paths.get(IBANValidator.class.getResource(IBAN_REGISTRY).toURI());

        final CSVFormat format = CSVFormat.DEFAULT.builder().setDelimiter('\t').build();
        final Reader rdr = Files.newBufferedReader(ibanRegistry, IBAN_REGISTRY_CHARSET);

        CSVRecord country = null;
        CSVRecord electronicExample = null;
        CSVRecord lastUpdateDate = null;

        try (CSVParser p = new CSVParser(rdr, format)) {
            for (final CSVRecord o : p) {
                final String item = o.get(0);
                switch (item) {
                    case "Name of country":
                        country = o;
                        break;
                    case "IBAN electronic format example":
                        electronicExample = o;
                        break;
                    case "Last update date":
                        lastUpdateDate = o;
                        break;
                    default:
                        break;
                }
            }
        }

        assertNotNull(country);
        final int arraySize = country.size();
        assertNotNull(electronicExample);
        assertEquals(arraySize, electronicExample.size());
        assertNotNull(lastUpdateDate);
        assertEquals(arraySize, lastUpdateDate.size());

        final Collection<Arguments> result = new ArrayList<>();
        Date lastDate = new Date(0);
        String lastUpdated = null;
        for (int i = 1; i < country.size(); i++) {
            result.add(Arguments.of(country.get(i), electronicExample.get(i)));
            final String mmyy = lastUpdateDate.get(i);
            final Date dt = DateValidator.getInstance().validate(mmyy, "MMM-yy", Locale.ROOT);
            if (dt.after(lastDate)) {
                lastDate = dt;
                lastUpdated = mmyy;
            }
        }
        final long age = (new Date().getTime() - lastDate.getTime()) / MS_PER_DAY;
        if (age > MAX_AGE_DAYS) { // not necessarily a failure
            System.out.println("WARNING: expected recent last update date, but found: " + lastUpdated);
        }
        return result;
    }

    public static Stream<Arguments> validateIbanStatuses() {
        return Stream.of(
                Arguments.of("XX", IBANValidatorStatus.UNKNOWN_COUNTRY),
                Arguments.of("AD0101", IBANValidatorStatus.INVALID_LENGTH),
                Arguments.of("AD12XX012030200359100100", IBANValidatorStatus.INVALID_PATTERN),
                Arguments.of("AD9900012030200359100100", IBANValidatorStatus.INVALID_CHECKSUM),
                Arguments.of("AD1200012030200359100100", IBANValidatorStatus.VALID)
        );
    }

    @ParameterizedTest
    @MethodSource("ibanRegistrySourceExamples")
    public void testExampleAccountsShouldBeValid(final String countryName, final String example) {
        Assumptions.assumeFalse(INVALID_IBAN_FIXTURES.contains(example), "Skip invalid example: " + example + " for " + countryName);
        assertTrue(IBANValidator.getInstance().isValid(example), "IBAN validator returned false for " + example + " for " + countryName);
    }

    @Test
    public void testGetRegexValidatorPatterns() {
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

    @ParameterizedTest
    @FieldSource("INVALID_IBAN_FIXTURES")
    public void testInValid(final String invalidIban) {
        assertNotNull(INVALID_IBAN_FIXTURES); // ensure field is marked as being used
        assertFalse(VALIDATOR.isValid(invalidIban), invalidIban);
    }

    @ParameterizedTest
    @FieldSource("VALID_IBAN_FIXTURES")
    public void testMoreValid(final String invalidIban) {
        assertNotNull(VALID_IBAN_FIXTURES); // ensure field is marked as being used
        assertTrue(VALIDATOR.isValid(invalidIban), invalidIban);
    }

    @Test
    public void testNull() {
        assertFalse(VALIDATOR.isValid(null), "isValid(null)");
    }

    @Test
    public void testSetDefaultValidator1() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> VALIDATOR.setValidator("GB", 15, "GB"));
        assertEquals("The singleton validator cannot be modified", thrown.getMessage());

    }

    @Test
    public void testSetDefaultValidator2() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> VALIDATOR.setValidator("GB", -1, "GB"));
        assertEquals("The singleton validator cannot be modified", thrown.getMessage());
    }

    @Test
    public void testSetValidatorLC() {
        final IBANValidator validator = new IBANValidator();
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> validator.setValidator("gb", 15, "GB"));
        assertEquals("Invalid country Code; must be exactly 2 upper-case characters", thrown.getMessage());
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
        assertEquals("Invalid length parameter, must be in range 8 to 34 inclusive: 35", thrown.getMessage());
    }

    @Test
    public void testSetValidatorLen7() {
        final IBANValidator validator = new IBANValidator();
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> validator.setValidator("GB", 7, "GB"));
        assertEquals("Invalid length parameter, must be in range 8 to 34 inclusive: 7", thrown.getMessage());
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

    @ParameterizedTest
    @FieldSource("VALID_IBAN_FIXTURES")
    public void testValid(final String iban) {
        assertTrue(IBANCheckDigit.IBAN_CHECK_DIGIT.isValid(iban), "Checksum fail: " + iban);
        assertTrue(VALIDATOR.hasValidator(iban), "Missing validator: " + iban);
        assertTrue(VALIDATOR.isValid(iban), iban);
    }

    @ParameterizedTest
    @MethodSource("validateIbanStatuses")
    public void testValidateIbanStatuses(final String iban, final IBANValidatorStatus expectedStatus) {
        assertEquals(expectedStatus, IBANValidator.getInstance().validate(iban));
    }

    @ParameterizedTest
    @MethodSource("ibanRegistrySource")
    public void testValidatorShouldExistWithProperConfiguration(final String countryName, final String countryCode, final List<String> acountyCode,
            final int ibanLength, final String structure) throws Exception {
        final String countryInfo = " countryCode: " + countryCode + ", countryName: " + countryName;
        final Validator validator = IBANValidator.getInstance().getValidator(countryCode);

        assertNotNull(validator, "IBAN validator returned null for" + countryInfo);
        assertEquals(ibanLength, validator.getIbanLength(), "IBAN length should be " + ibanLength + " for" + countryInfo);

        final List<String> allPatterns = Arrays.stream(validator.getRegexValidator().getPatterns()).map(Pattern::pattern).collect(Collectors.toList());

        final String re = fmtRE(structure.substring(2));
        assertTrue(allPatterns.remove(countryCode + re), "No pattern " + countryCode + re + " found for " + countryInfo);
        for (final String ac : acountyCode) {
            assertTrue(allPatterns.remove(ac + re), "No additional country code " + ac + " found for " + countryInfo);
        }
        assertTrue(allPatterns.isEmpty(), "Unrecognized patterns: " + allPatterns + " for" + countryInfo);
    }
}
