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

import java.io.Serializable;

import org.apache.commons.validator.GenericValidator;

/**
 * <strong>Czech National Bank - Check Digit</strong>
 * <p>
 * Check digit calculation is based on <em>Czech National Bank Check Digit</em>.
 * For sample IBAN <strong>CZ56 0100 0000 1900 0012 3457</strong> we have:
 * <ul>
 *     <li>First part of the client account number: <strong>00019</strong> (a first part containing 6 digits at most, with the
 *     initial zeros having no significance; the
 * first part of the client account identifier need not be contained in the account number)</li>
 *     <li>Second part of the client account number: <strong>0000123457</strong></li>
 * </ul>.
 * The first and second parts of the client account identifier shall be created so that each
 * part separately complies with the check algorithm with the weights described in <em>CNB decree</em> document.
 * <p>
 * For further information see:
 * <ul>
 *   <li>IBAN CNB verification
 *      <a href="https://www.cnb.cz/en/payments/iban/iban-calculator-czech-republic/">Verification of CNB IBAN</a>.
 *   </li>
 *   <li>CNB decree
 *       <a href="https://www.cnb.cz/export/sites/cnb/en/legislation/.galleries/decrees/decree_169_2011.pdf">Official
 *       decree_169_2011</a>.</li>
 * </ul>
 *
 * @since 1.10.0
 */
public class IBANCheckDigitCz extends ModulusCheckDigit implements Serializable {

    private static final int[] POSITION_WEIGHT = new int[] {1, 2, 4, 8, 5, 10, 9, 7, 3, 6};

    /** Singleton IBAN Number Check Digit for Cz instance */
    public static final CheckDigit CNB_CHECK_DIGIT = new IBANCheckDigitCz();

    private IBANCheckDigitCz() {
        super(MODULUS_11);
    }

    @Override
    public boolean isValid(String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        try {
            final String clientAccountFirstPart = code.substring(code.length() - 16, code.length() - 10);
            final String clientAccountSecondPart = code.substring(code.length() - 10);

            final int fpModulusResult = hasFirstPartClientAccount(clientAccountFirstPart) ?
                    calculateModulus(clientAccountFirstPart, true) : 0;
            final int spModulusResult = calculateModulus(clientAccountSecondPart, true);

            return fpModulusResult + spModulusResult == 0;
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

    private boolean hasFirstPartClientAccount(String clientAccountFirstPart) {
        return !clientAccountFirstPart.equals("000000");
    }

    @Override
    protected int weightedValue(int charValue, int leftPos, int rightPos) throws CheckDigitException {
        return charValue * POSITION_WEIGHT[POSITION_WEIGHT.length - leftPos];
    }

}
