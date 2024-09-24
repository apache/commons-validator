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
 * DK VAT Id Check Digit Tests.
 */
/*

Im VIES https://ec.europa.eu/taxation_customs/vies/#/vat-validation

bekommt man Informationen zu den VATINs:
- 88146328 : Tangvejen 5 v/Arne Sørensen
- 13585628 : Brørup Boligforening
- 13748136 : aus https://web.archive.org/web/20120917151518/http://www.erhvervsstyrelsen.dk/modulus_11

 */
public class VATidDKCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        checkDigitLth = 0;
        routine = VATidDKCheckDigit.getInstance();
        valid = new String[] {"88146328", "13585628", "13748136"};
    }

    protected String checkDigit(final String code) {
        return "0"; // No check digit in DK VATIN, it is always 0.
    }

}
