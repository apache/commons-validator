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
 * DE VAT Id Check Digit Tests.
 */
public class VATidDECheckDigitTest extends AbstractCheckDigitTest {

    // valid VAT Id without DE prefix
    private static final String MIN = "000000011"; // theoretical minimum
    private static final String MAX = "999999995"; // theoretical
    private static final String GEIGER = "128514248"; // https://www.geigergruppe.com/de-de/impressum/
    private static final String ITDZ = "205130669"; // https://www.itdz-berlin.de/allgemeines/impressum/

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = Modulus11TenCheckDigit.getInstance();
        valid = new String[] {MIN, GEIGER, ITDZ, "136586130", "136695976"
            , "294776378", "811128135", "294776378", MAX};
        invalid = new String[] {
            "000000003" // sum is zero
        };
    }

}
