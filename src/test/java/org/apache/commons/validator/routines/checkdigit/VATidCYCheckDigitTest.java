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
 * CY VAT Id Check Digit Tests.
 */
/*

Beispiele

    CY 30010823A : LIDL Cyprus
    CY 10259033P : valide, aber ungültig. Aus formvalidation.io und stdnum-js

       12000139V is invalid

 */
public class VATidCYCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = VATidCYCheckDigit.getInstance();
        valid = new String[] {"30010823A", "10259033P"
            , "61234567I" // is not valid because starts with "6", checked in VATINValidator
        };
        invalid = new String[] {"12000139V" // starts with "12"
            , "00000000A" // sum is zero
        };
    }

}
