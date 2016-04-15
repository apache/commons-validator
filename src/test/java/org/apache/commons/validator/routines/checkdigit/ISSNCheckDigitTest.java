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


/**
 * ISSN Check Digit Test.
 *
 * @since 1.5.0
 */
public class ISSNCheckDigitTest extends AbstractCheckDigitTest {
    
    /**
     * Constructor
     * @param name test name
     */
    public ISSNCheckDigitTest(String name) {
        super(name);
    }

    /**
     * Set up routine & valid codes.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        routine = ISSNCheckDigit.ISSN_CHECK_DIGIT;
        valid = new String[] {
                "03178471",
                "1050124X",
                "15626865",
                "10637710",
                "17487188",
                "02642875",
                "17500095",
                "11881534",
                "19111479",
                "19111460",
                "00016772",
                "1365201X",
                };
        invalid = new String[] {
                "03178472", // wrong check
                "1050-124X", // format char
                " 1365201X",
                "1365201X ",
                " 1365201X ",
        };
        missingMessage = "Code is missing";
        zeroSum = "00000000";
    }

}
