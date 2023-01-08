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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;


/**
 * IBAN Check Digit Test.
 *
 * @since 1.4
 */
public class IBANCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Constructor
     * @param name test name
     */
    public IBANCheckDigitTest(final String name) {
        super(name);
        checkDigitLth = 2;
    }

    /**
     * Set up routine & valid codes.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        routine = IBANCheckDigit.IBAN_CHECK_DIGIT;
        valid  = new String[]  {
                "AD1200012030200359100100",      // Andorra
                "AE070331234567890123456",       // United Arab Emirates
                "AL47212110090000000235698741",  // Albania
                "AT611904300234573201",          // Austria
                "AZ21NABZ00000000137010001944",  // Azerbaijan
                "BA391290079401028494",          // Bosnia and Herzegovina
                "BE62510007547061",              // Belgium
                "BE68539007547034",              // Belgium
                "BG80BNBG96611020345678",        // Bulgaria
                "BH67BMAG00001299123456",        // Bahrain
                "BI4210000100010000332045181",   // Burundi
                "BR1800000000141455123924100C2", // Brazil
                "BY13NBRB3600900000002Z00AB00",  // Belarus
                "CH3900700115201849173",         // Switzerland
                "CH9300762011623852957",         // Switzerland
                "CR05015202001026284066",        // Costa Rica
                "CY17002001280000001200527600",  // Cyprus
                "CZ6508000000192000145399",      // Czechoslovakia
                "DE89370400440532013000",        // Germany
                "DK5000400440116243",            // Denmark
                "DO28BAGR00000001212453611324",  // Dominican Republic
                "EE382200221020145685",          // Estonia
                "ES8023100001180000012345",      // Spain
                "FI2112345600000785",            // Finland
                "FO6264600001631634",            // Denmark (Faroes)
                "FR1420041010050500013M02606",   // France
                "GB29NWBK60161331926819",        // UK
                "GI75NWBK000000007099453",       // Gibraltar
                "GL8964710001000206",            // Denmark (Greenland)
                "GR1601101250000000012300695",   // Greece
                "GT82TRAJ01020000001210029690",  // Guatemala
                "HR1210010051863000160",         // Croatia
                "HU42117730161111101800000000",  // Hungary
                "IE29AIBK93115212345678",        // Ireland
                "IL620108000000099999999",       // Israel
                "IQ98NBIQ850123456789012",       // Iraq
                "IS140159260076545510730339",    // Iceland
                "IT60X0542811101000000123456",   // Italy
                "JO94CBJO0010000000000131000302",// Jordan
                "KW81CBKU0000000000001234560101",// Kuwait
                "KZ86125KZT5004100100",          // Kazakhstan
                "LB62099900000001001901229114",  // Lebanon
                "LC55HEMM000100010012001200023015",//Saint Lucia
                "LI21088100002324013AA",         // Liechtenstein (Principality of)
                "LT121000011101001000",          // Lithuania
                "LU280019400644750000",          // Luxembourg
                "LV80BANK0000435195001",         // Latvia
                "MC5811222000010123456789030",   // Monaco
                "MD24AG000225100013104168",      // Moldova
                "ME25505000012345678951",        // Montenegro
                "MK07250120000058984",           // Macedonia, Former Yugoslav Republic of
                "MR1300020001010000123456753",   // Mauritania
                "MT84MALT011000012345MTLCAST001S",// Malta
                "MU17BOMM0101101030300200000MUR",// Mauritius
                "NL39RABO0300065264",            // Netherlands
                "NL91ABNA0417164300",            // Netherlands
                "NO9386011117947",               // Norway
                "PK36SCBL0000001123456702",      // Pakistan
                "PL27114020040000300201355387",  // Poland
                "PL60102010260000042270201111",  // Poland
                "PS92PALS000000000400123456702", // Palestine, State of
                "PT50000201231234567890154",     // Portugal
                "QA58DOHB00001234567890ABCDEFG", // Qatar
                "RO49AAAA1B31007593840000",      // Romania
                "RS35260005601001611379",        // Serbia
                "SA0380000000608010167519",      // Saudi Arabia
                "SC18SSCB11010000000000001497USD",// Seychelles
                "SE3550000000054910000003",      // Sweden
                "SD2129010501234001",            // Sudan
                "SI56191000000123438",           // Slovenia
                "SK3112000000198742637541",      // Slovak Republic
                "SM86U0322509800000000270100",   // San Marino
                "ST68000100010051845310112",     // Sao Tome and Principe
                "SV62CENR00000000000000700025",  // El Salvador
                "TL380080012345678910157",       // Timor-Leste
                "TN5910006035183598478831",      // Tunisia
                "TR330006100519786457841326",    // Turkey
                "UA213223130000026007233566001", // Ukraine
                "VA59001123000012345678",        // Vatican City State
                "VG96VPVG0000012345678901",      // Virgin Islands, British
                "XK051212012345678906",          // Republic of Kosovo

                // Codes AA and ZZ will never be used as ISO countries nor in IBANs
                // add some dummy calculated codes to test the limits
                // Current minimum length is Norway = 15
                // Current maximum length is Malta  = 31
                // N.B. These codes will fail online checkers which validate the IBAN format
                //234567890123456789012345678901
                "AA0200000000053",
                "AA9700000000089",
                "AA9800000000071",
                "ZZ02ZZZZZZZZZZZZZZZZZZZZZZZZZ04",
                "ZZ97ZZZZZZZZZZZZZZZZZZZZZZZZZ40",
                "ZZ98ZZZZZZZZZZZZZZZZZZZZZZZZZ22",
                };
        /*
         *  sources
         *  https://intranet.birmingham.ac.uk/finance/documents/public/IBAN.pdf
         *  http://www.paymentscouncil.org.uk/resources_and_publications/ibans_in_europe/
         */
        invalid = new String[] {
                "510007+47061BE63",
                "IE01AIBK93118702569045",
                "AA0000000000089",
                "AA9900000000053",
        };
        zeroSum = null;
        missingMessage = "Invalid Code length=0";

    }

    /**
     * Test zero sum
     */
    @Override
    public void testZeroSum() {
        // ignore, don't run this test

        // example code used to create dummy IBANs
//        try {
//            for(int i=0; i<97;i++) {
//                String check = String.format("ZZ00ZZZZZZZZZZZZZZZZZZZZZZZZZ%02d", new Object[]{Integer.valueOf(i)});
//                String chk = routine.calculate(check);
//                if (chk.equals("97")||chk.equals("98")||chk.equals("02")) {
//                    System.out.println(check+ " "+chk);
//                }
//            }
//        } catch (CheckDigitException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Returns an array of codes with invalid check digits.
     *
     * @param codes Codes with valid check digits
     * @return Codes with invalid check digits
     */
    @Override
    protected String[] createInvalidCodes(final String[] codes) {
        final List<String> list = new ArrayList<>();

        // create invalid check digit values
        for (final String code2 : codes) {
            final String code = removeCheckDigit(code2);
            final String check  = checkDigit(code2);
            for (int j = 2; j <= 98; j++) { // check digits can be from 02-98 (00 and 01 are not possible)
                final String curr =  j > 9 ? "" + j : "0" + j;
                if (!curr.equals(check)) {
                    list.add(code.substring(0, 2) + curr + code.substring(4));
                }
            }
        }

        return list.toArray(new String[0]);
    }

    /**
     * Returns a code with the Check Digits (i.e. characters 3&4) set to "00".
     *
     * @param code The code
     * @return The code with the zeroed check digits
     */
    @Override
    protected String removeCheckDigit(final String code) {
        return code.substring(0, 2) + "00" + code.substring(4);
    }

    /**
     * Returns the check digit (i.e. last character) for a code.
     *
     * @param code The code
     * @return The check digit
     */
    @Override
    protected String checkDigit(final String code) {
        if (code == null || code.length() <= checkDigitLth) {
            return "";
        }
       return code.substring(2, 4);
    }

    public void testOther() throws Exception {
        try (BufferedReader rdr = new BufferedReader(
                new InputStreamReader(
                        this.getClass().getResourceAsStream("IBANtests.txt"),"ASCII"))) {
            String line;
            while((line=rdr.readLine()) != null) {
                if (!line.startsWith("#") && !line.isEmpty()) {
                    if (line.startsWith("-")) {
                        line = line.substring(1);
                        Assert.assertFalse(line, routine.isValid(line.replace(" ", "")));
                    } else {
                        Assert.assertTrue(line, routine.isValid(line.replace(" ", "")));
                    }
                }
            }
        }
    }
}
