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
 * MOD 97 Check Digit Test.
 */
public class Modulus97CheckDigitTest extends AbstractCheckDigitTest {

    private static final String MIN = "000195"; // theoretical minimum
    private static final String MAX = "99999999999999999928"; // theoretical
    private static final String LONG = "999999999999999999999999999916"; // theoretical
    
    private final String[] validLEIFormat = new String[] {
            "54930084UKLVMY22DS16", // G.E. Financing GmbH
            "213800WSGIIZCXF1P572", // Jaguar Land Rover Ltd
            "M07J9MTYHFCSVRBV2631", // RWE AG (old Style, before 2012.Nov.30)
            "529900CLVK38HUKPKF71", // SWKBank
        };
//    "992-90009-96",           // Deutsche Bahn AG
//    "051700052052-31001-35",  // Xanten, Stadt
//    "053340002002-33004-23",  // Aachener Parkhaus GmbH
//    "05711-06001-79",         // UNI BI Lkr:Bielefeld, Stadt
//    "05913-99001-25",         // Kassenärztliche Vereinigung Westfalen-Lippe, 44141 Dortmund, x-rechnung@kvwl.de
//    "11-2000001000-30",       // ITDZ, Berlin

    public Modulus97CheckDigitTest() {
        checkDigitLth = Modulus97CheckDigit.CHECKDIGIT_LEN;
    }
    
    /**
     * Sets up routine & valid codes.
     * 
     */
    @BeforeEach
    protected void setUp() {
        routine = Modulus97CheckDigit.getInstance();
        valid = new String[] { "529900CLVK38HUKPKF71"
//        		, MIN, MAX, LONG
//        		, "510007547061BE62" , "1904300234573201AT61" // IBAN : BE62510007547061 AT611904300234573201
//        		, "54930084UKLVMY22DS16" // LEI G.E. Financing GmbH
//        		, "9929000996" // Leitweg
        		
//        		, "0441826783", "136695962", "0136695962", "0776091951" // VATidBE
//        		, "0888888895", "0899999949", "0999999922", "1999999943", "9999999917"
//        		, "366303068","434031494","888859134" // VATidXI passen alle nicht
        };
//        invalid = new String[] { "196", // to short <== MIN_CODE_LEN = 4
//        		"0000014",   // wrong check
//        		"200-001-8", // format chars
//        };
    }
}
