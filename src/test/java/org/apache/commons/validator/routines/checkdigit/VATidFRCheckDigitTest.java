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

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * FR VAT Id Check Digit Tests.
 */
public class VATidFRCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        checkDigitLth = VATidFRCheckDigit.CHECKDIGIT_LEN;
        routine = VATidFRCheckDigit.getInstance();

        valid = new String[] {"00300076965"
            , "55502090897"
            , "40303265045"
            , "23334175221"
            , "06399859412" // SLRL ALTEA EXPERTISE COMPTABLE the real check digit is "K7"
//          , "K7399859412" // this valid code with check digit "K7" cannot be tested here
//            because French VATIN check digit are ambiguous and calculate returns only the numeric one
//            => this can be tested in VATINValidatorTest
            , "83404833048"
            // the following constructed code contains a valid SIREN number which is not registerted in SIRENE
            , "11123456782" // search in SIRENE result: L'identifiant n'existe pas dans le répertoire Sirene.
            };
        invalid = new String[] {"+6399859412"};
    }

    protected String checkDigit(final String code) {
        if (code == null || code.length() <= checkDigitLth) {
            return "";
        }
        return code.substring(0, checkDigitLth);
    }

    protected String removeCheckDigit(final String code) {
        if (code == null || code.length() <= checkDigitLth) {
            return null;
        }
        return code.substring(checkDigitLth);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to handle two digits prefix as <em>Check Digit</em>. The original test assumes
     * that there is only one valid check digit. All other generated test (createInvalidCodes)
     * codes should assertFalse. This is not so for FR VATINS. There are more valid check digits,
     * one calculated with old style MOD97 and results to numeric and
     * the other "new style" results to letters and digits.
     * I collect the test results for each test case and print the result at the end.
     * Example: 404833048 [C0, U0, J1, P1, ... 7U, 7V, 8V, 9V]  shows all the new style check digits
     * </p>
     */
    @Test
    @Override
    public void testIsValidFalse() {
        if (log.isDebugEnabled()) {
            log.debug("testIsValidFalse() for " + routine.getClass().getName());
        }

        // test invalid code values
        for (int i = 0; i < invalid.length; i++) {
            if (log.isDebugEnabled()) {
                log.debug("   " + i + " Testing Invalid Code=[" + invalid[i] + "]");
            }
            final String invalidCode = invalid[i];
            System.out.println("   " + i + " Testing Invalid Code=[" + invalidCode + "]");
            assertFalse(routine.isValid(invalidCode), "invalid[" + i + "]: " + invalidCode);
        }

        // test invalid check digit values
        final String[] invalidCheckDigits = createInvalidCodes(valid);
        final Map<String, List<String>> icdmap = new Hashtable<String, List<String>>();
        for (int i = 0; i < invalidCheckDigits.length; i++) {
            if (log.isDebugEnabled()) {
                log.debug("   " + i + " Testing Invalid Check Digit, Code=[" + invalidCheckDigits[i] + "]");
            }
            final boolean res = routine.isValid(invalidCheckDigits[i]);
            if (res) {
                List<String> v = icdmap.get(invalidCheckDigits[i].substring(checkDigitLth));
                if (v == null) {
                    v = new ArrayList<String>();
                    v.add(invalidCheckDigits[i].substring(0, checkDigitLth));
                    icdmap.put(invalidCheckDigits[i].substring(checkDigitLth), v);
                } else {
                    v.add(invalidCheckDigits[i].substring(0, checkDigitLth));
                }
            }
        }
        // now print the results
        icdmap.forEach((key, value) -> System.out.println(key + " " + value));


    }

    protected String createCode(final String code, final String cd) {
        return cd + code;
    }

}
