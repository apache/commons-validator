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
 * IE VAT Id Check Digit Tests.
 * <pre>

    IE 9700053D : gültig APPLE DISTRIBUTION INTERNATIONAL LTD, HOLLYHILL INDUSTRIAL ESTATE, CORK
    IE 6388047V : gültig GOOGLE IRELAND LIMITED
    IE 8473625E : achtstellig, ungültig aus pruefziffernberechnung.de
    IE 3628739L : achtstellig, ungültig aus BMF_UID_Konstruktionsregeln.pdf bmf.gv.at
    IE 3628739UA : neunstellig, ungültig aus BMF_UID_Konstruktionsregeln.pdf bmf.gv.at
    IE 6433435F : gültig EOBO LIMITED, SHANNON aus https://old.formvalidation.io/validators/vat/
    IE 6433435OA : valide, aber ungültig, aus https://old.formvalidation.io/validators/vat/
    IE 9950958B : gültig HAUPPAUGE DIGITAL EUROPE SARL, DUBLIN aus adresslabor.de und
    IE 2251597K, 8Y93637V (old Style), 6693587J alle ungültig

 * </pre>
 */
public class VATidIECheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = VATidIECheckDigit.getInstance();
        valid = new String[] {"3628739L", "3628739UA"
            , "9700053D", "6388047V", "8473625E"
            , "6433435F", "6433435OA", "0936378V"
            };
        invalid = new String[] {"99509582" // check digit 2 instead B
            , "0000000IA" // sum is zero
            };
    }

    private static final int LENGTH9 = VATidIECheckDigit.LEN + 2;

    /**
     * {@inheritDoc}
     * <p>
     * Override for long code format to replace the check character with 0
     * </p>
     */
    @Override
    protected String removeCheckDigit(final String code) {
        if (code == null || code.length() <= checkDigitLth) {
            return null;
        }
        if (code.length() >= LENGTH9) {
            // set checkDigit to 0
            return code.substring(0, VATidIECheckDigit.LEN) + 0 + code.substring(VATidIECheckDigit.LEN + 1);
        }
        return super.removeCheckDigit(code);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Override for long code format to get the check character which is not the last character
     * </p>
     */
    protected String checkDigit(final String code) {
        if (code == null || code.length() <= checkDigitLth) {
            return "";
        }
        if (code.length() >= LENGTH9) {
            final int start = LENGTH9 - checkDigitLth;
            return code.substring(start - 1, start);
        }
        return super.checkDigit(code);
    }

}
