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
 * SK VAT Id Check Digit Tests.
 * <pre>
    SK 2120567108 : valide Markon s.r.o. Bratislava
    SK 2021896096 : valide SEDLÁČEK, advokátska kancelária s.r.o
    SK 4030000007 : valide, aber ungültig. Aus bmf.gv.at
    SK 2022749619 : valide, aber ungültig. Aus https://old.formvalidation.io/validators/vat/
 * </pre>
 */
public class VATidSKCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        checkDigitLth = 0;
        routine = VATidSKCheckDigit.getInstance();
        valid = new String[] {"0000000011", "11" // theoretical minimum
            , "2120567108", "2021896096", "4030000007", "2022749619"
            , "1111111111" // here valid, NOT valid, because 3rd digit is 1, checked in VATINValidator
            , "9999999999" // theoretical maximum
            };
        invalid = new String[] {"999X999999", "0000000010"};
    }

    protected String checkDigit(final String code) {
        return "0"; // No check digit in SK VATIN, it is always 0.
    }

}