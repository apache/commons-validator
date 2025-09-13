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
 * RO VAT Id Check Digit Tests.
 * <pre>
    RO 11198699 : valid MEGAOIL SRL
    RO 15641860 : valid FERTIAGRO S.R.L.
    RO 27825131 : valid NUTRISOYA SRL
    RO 17292740 : valid ATLAS MOTORS SRL
    RO 19125650 : valid KONIG FRANKSTAHL SRL
    RO 6255950  : valid P L NORIS SRL - 7-digits!
 * </pre>
 */
public class VATidROCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = VATidROCheckDigit.getInstance();
        valid = new String[] {"19" , "0000000019" // theoretical minimum
            , "11198699", "15641860", "27825131", "17292740", "19125650", "6255950"
            , "9999999994" // theoretical maximum
            };
    }

}
