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
 * <pre>

    PL 9551908591 : gültig chlodniaszczecinska.pl
    PL 6911713825 : gültig jakbet.pl
    PL 1060000062 : gültig "CUMMINS LTD." aus pl.wikipedia

 * </pre>
 */
public class VATidPLCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = VATidPLCheckDigit.getInstance();
        valid = new String[] {"9551908591", "6911713825", "1060000062", "1234563218", "1234567819"};
        invalid = new String[] {"0000000000", "1234567890"};
    }

}
