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
 * LV VAT Id Check Digit Tests.
 */
public class VATidLVCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = VATidLVCheckDigit.getInstance();
        valid = new String[] {"40003009497" // aus SAP Format 1: Legal persons
            , "54103040601" // "ŽAGARKALNS" SIA
            , "40003567907" // Pasažieru vilciens
            , "90000069281" //
            , "40203551190" //
            };
        invalid = new String[] {"07091910933" // aus SAP Format 2: Natural person born DDMMYY 070919
//                ,"07091910931" // mit anderer Prüfziffer
            };
    }

}
