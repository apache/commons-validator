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

import junit.framework.Assert;


/**
 * EAN-13 Check Digit Test.
 *
 * @version $Revision$
 * @since Validator 1.4
 */
public class IBANCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Constructor
     * @param name test name
     */
    public IBANCheckDigitTest(String name) {
        super(name);
        checkDigitLth = 2;
    }

    /**
     * Set up routine & valid codes.
     */
    protected void setUp() throws Exception {
        super.setUp();
        routine = IBANCheckDigit.IBAN_CHECK_DIGIT;
        valid  = new String[]  {
                "AD1200012030200359100100",      // Andorra
                "AT611904300234573201",          // Austria
                "BE62510007547061",              // Belgium
                "BE68539007547034",              // Belgium
                "CH3900700115201849173",         // Switzerland
                "CH9300762011623852957",         // Switzerland
                "CY17002001280000001200527600",  // Cyprus
                "CZ6508000000192000145399",      // Czechoslovakia
                "DE89370400440532013000",        // Germany
                "DK5000400440116243",            // Denmark
                "EE382200221020145685",          // Estonia
                "ES8023100001180000012345",      // Spain
                "FI2112345600000785",            // Finland
                "FR1420041010050500013M02606",   // France
                "GB29NWBK60161331926819",        // UK
                "GI75NWBK000000007099453",       // Gibraltar
                "GR1601101250000000012300695",   // Greece
                "HU42117730161111101800000000",  // Hungary
                "IE29AIBK93115212345678",        // Ireland
                "IS140159260076545510730339",    // Iceland
                "IT60X0542811101000000123456",   // Italy
                "LT121000011101001000",          // Lithuania
                "LU280019400644750000",          // Luxembourg
                "LV80BANK0000435195001",         // Latvia
                "MT84MALT011000012345MTLCAST001S",// Malta
                "NL39RABO0300065264",            // Netherlands
                "NL91ABNA0417164300",            // Netherlands
                "NO9386011117947",               // Norway
                "PL27114020040000300201355387",  // Poland
                "PL60102010260000042270201111",  // Poland
                "PT50000201231234567890154",     // Portugal
                "SE3550000000054910000003",      // Sweden
                "SI56191000000123438",           // Slovenia
                "SK3112000000198742637541",      // Slovak Republic

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
    protected String[] createInvalidCodes(String[] codes) {
        List<String> list = new ArrayList<String>();

        // create invalid check digit values
        for (int i = 0; i < codes.length; i++) {
            String code = removeCheckDigit(codes[i]);
            String check  = checkDigit(codes[i]);
            for (int j = 2; j <= 98; j++) { // check digits can be from 02-98 (00 and 01 are not possible)
                String curr =  j > 9 ? "" + j : "0" + j;
                if (!curr.equals(check)) {
                    list.add(code.substring(0, 2) + curr + code.substring(4));
                }
            }
        }

        return (String[])list.toArray(new String[list.size()]);
    }

    /**
     * Returns a code with the Check Digits (i.e. characters 3&4) set to "00".
     *
     * @param code The code
     * @return The code with the zeroed check digits
     */
    protected String removeCheckDigit(String code) {
        return code.substring(0, 2) + "00" + code.substring(4);
    }

    /**
     * Returns the check digit (i.e. last character) for a code.
     *
     * @param code The code
     * @return The check digit
     */
    protected String checkDigit(String code) {
        if (code == null || code.length() <= checkDigitLth) {
            return "";
        }
       return code.substring(2, 4);
    }

    public void testOther() throws Exception {
        BufferedReader rdr = null;
        try {
            rdr = new BufferedReader(
                    new InputStreamReader(
                            this.getClass().getResourceAsStream("IBANtests.txt"),"ASCII"));
            String line;
            while((line=rdr.readLine()) != null) {
                if (!line.startsWith("#") && line.length() > 0) {
                    if (line.startsWith("-")) {
                        line = line.substring(1);
                        Assert.assertFalse(line, routine.isValid(line.replaceAll(" ", "")));
                    } else {
                        Assert.assertTrue(line, routine.isValid(line.replaceAll(" ", "")));
                    }
                }
            }
        } finally {
            if (rdr != null) {
                rdr.close();
            }
        }
    }
}
