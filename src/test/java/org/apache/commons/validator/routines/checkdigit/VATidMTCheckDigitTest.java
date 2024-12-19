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
 * MT VAT Id Check Digit Tests.
 * <pre>

    MT 15121333 : valide, aber ungültig. Aus sap.com
    MT 11679112 : valide, aber ungültig. Aus formvalidation.io
    MT 20200019 : gültig. BAJADA NEW ENERGY LIMITED, MRS3000 Marsa

 * </pre>
 */
public class VATidMTCheckDigitTest extends AbstractCheckDigitTest {

    public VATidMTCheckDigitTest() {
        checkDigitLth = VATidMTCheckDigit.CHECKDIGIT_LEN;
    }

   /**
     * Sets up routine & valid, invalid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = VATidMTCheckDigit.getInstance();
        valid = new String[] {"15121333", "11679112", "10008937", "20200019"};
        invalid = new String[] {"00000000", "00008900"};
    }

}
