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
 * HU VAT Id Check Digit Tests.
 * <p>
 * Hungarien VAT identification number (VATIN) Check Digit is called közösségi adószám (ÁFA).
 * It uses the ModulusTenCheckDigit algorithm. So I check against this.
 * </p>
 * <pre>
 * HU 21376414 10597190 : valide, aber ungültig.
 * HU 12892312 10663103 12188224: gültig
 * </pre>
 */
public class VATidHUCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = new ModulusTenCheckDigit(new int[] { 1, 3, 7, 9 }, true);
        valid = new String[] {"21376414", "10597190", "12892312", "10663103", "12188224"};
    }

}
