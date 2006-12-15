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

import java.util.List;
import java.util.ArrayList;


import junit.framework.TestCase;

/**
 * Luhn Check Digit Test.
 *
 * @version $Revision$
 * @since Validator 1.4
 */
public class CheckDigitTestBase extends TestCase {

    /** Check digit routine being tested */
    protected int checkDigitLth = 1;

    /** Check digit routine being tested */
    protected CheckDigit routine;

    /** Array of valid code values */
    protected String[] valid;

    /** Array of invalid code values */
    protected String[] invalid = new String[] {"12345678A"};

    /** code value which sums to zero */
    protected String zeroSum = "0000000000";

    /** Prefix for error messages */
    protected String msgPrefix = "";

    /**
     * Constructor
     * @param name test name
     */
    public CheckDigitTestBase(String name) {
        super(name);
    }

    /**
     * Tear Down - clears routine and valid codes.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        valid = null;
        routine = null;
    }

    /**
     * Test method for {@link CheckDigit#isValid(java.lang.String)}.
     */
    public void testIsValid() {

        // test valid values
        for (int i = 0; i < valid.length; i++) {
            assertTrue("valid[" + i +"]: " + valid[i], routine.isValid(valid[i]));
        }

        // test invalid code values
        for (int i = 0; i < invalid.length; i++) {
            assertFalse("invalid[" + i +"]: " + invalid[i], routine.isValid(invalid[i]));
        }

        // test invalid check digit values
        String[] invalidCheckDigits = createInvalidCodes(valid);
        for (int i = 0; i < invalidCheckDigits.length; i++) {
            assertFalse("invalid check digit[" + i +"]: " + invalidCheckDigits[i], routine.isValid(invalidCheckDigits[i]));
        }

        // test null
        assertFalse("Test Null", routine.isValid(null));

        // test zero length
        assertFalse("Test Zero Length", routine.isValid(""));
        
        // test zero sum
        assertFalse("Test Zero Sum", routine.isValid(zeroSum));

    }

    /**
     * Test method for {@link CheckDigit#calculate(java.lang.String)}.
     */
    public void testCalculate() {

        // test valid values
        for (int i = 0; i < valid.length; i++) {
            String code = removeCheckDigit(valid[i]);
            String expected = checkDigit(valid[i]);
            try {
                assertEquals("valid[" + i +"]: " + valid[i], expected, routine.calculate(code));
            } catch (Exception e) {
                fail("valid[" + i +"] threw " + e);
            }
        }

        // test null
        try {
            routine.calculate(null);
            fail("Null - expected exception");
        } catch (Exception e) {
            assertEquals("Null Test", msgPrefix +"Code is missing", e.getMessage());
        }

        // test zero length
        try {
            routine.calculate("");
            fail("Zero Length - expected exception");
        } catch (Exception e) {
            assertEquals("Zero Length",  msgPrefix +"Code is missing", e.getMessage());
        }

        // test invalid code values
        for (int i = 0; i < invalid.length; i++) {
            try {
                routine.calculate(invalid[i]);
                fail("Invalid Characters[" + i + "] - expected exception");
            } catch (Exception e) {
                assertTrue("Invalid Character[" +i +"]", e.getMessage().startsWith("Invalid Character["));
            }
        }

        // test zero sum
        try {
            routine.calculate(zeroSum);
            fail("Zero Sum - expected exception");
        } catch (Exception e) {
            assertEquals("Zero Length",  "Invalid code, sum is zero", e.getMessage());
        }
    }

    /**
     * Returns an array of codes with invalid check digits.
     *
     * @param codes Codes with valid check digits
     * @return Codes with invalid check digits
     */
    protected String[] createInvalidCodes(String[] codes) {
        List list = new ArrayList();

        // create invalid check digit values
        for (int i = 0; i < codes.length; i++) {
            String code = removeCheckDigit(codes[i]);
            String check  = checkDigit(codes[i]);
            for (int j = 0; j < 10; j++) {
                String curr =  "" + Character.forDigit(j, 10);
                if (!curr.equals(check)) {
                    list.add(code + curr);
                }
            }
        }
        
        return (String[])list.toArray(new String[list.size()]);
    }

    /**
     * Returns a code with the Check Digit (i.e. last character) removed.
     *
     * @param code The code
     * @return The code without the check digit
     */
    protected String removeCheckDigit(String code) {
        if (code == null || code.length() <= checkDigitLth) {
            return null;
        }
        return code.substring(0, code.length() - checkDigitLth);
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
        int start = code.length() - checkDigitLth;
        return code.substring(start);
    }

}
