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
 * LT VAT Id Check Digit Tests.
 */
public class VATidLTCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = VATidLTCheckDigit.getInstance();
        valid = new String[] {"213179412" // aus BMF_UID_Konstruktionsregeln.pdf
            , "290061371314" // 12-stellig aus BMF_UID_Konstruktionsregeln.pdf
            , "100014579016" // Senoji rotonda
            , "100008668610" // Geltonas namas
            , "582708716" // ASIMA
            , "237153113" // RIMI
            , "230335113" // maxima
            , "321389515" // kautra
            , "100008668621" // TODO invalid C11 = 1
            };
        invalid = new String[] {"07091910933" // aus SAP Format 2: Natural person
//            ,"07091910931" // mit anderer Prüfziffer
            };
    }

}
