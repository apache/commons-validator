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
 * AT VAT Id Check Digit Tests.
 */
/*
Beispiele

    AT U13585627 : valide, aber ungültig
    AT U54065602 : gültig TIC Technology & Innovation Center Steyr GmbH, Im Stadtgut A 1, AT-4407 Dietach
    AT U26218303 : gültig Maltaholz GmbH, Karnerau 21, AT-9853 Gmünd

 */
public class VATidATCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = VATidATCheckDigit.getInstance();
        valid = new String[] {"U10223006", "U13585627"
           , "U54065602", "U26218303"
           };
        invalid = new String[] {"u10223006", "X13585627", "U00000000"};
    }

}
