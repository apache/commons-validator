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
 * PL VAT Id Check Digit Tests.
 */
/*

Im VIES https://ec.europa.eu/taxation_customs/vies/#/vat-validation

bekommt man Informationen zu den VATINs:
- 1060000062 : CUMMINS LTD
- 1234563218 1234567819 : valide, aber ungültig

- 0000000000 ist lt. wiki valide, aber sinnlos
- 1234567890 liefert PZ 10, das nicht sein darf
 */
public class VATidPLCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = VATidPLCheckDigit.getInstance();
        valid = new String[] {"1060000062", "1234563218", "1234567819"};
    }

}
