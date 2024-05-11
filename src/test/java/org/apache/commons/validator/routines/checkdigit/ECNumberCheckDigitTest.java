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
 * EC Number Check Digit Tests.
 */
public class ECNumberCheckDigitTest extends AbstractCheckDigitTest {

	// valid EC Number with dashes removed
    private static final String MIN = "2000002"; // theoretical minimum
    private static final String FORMALDEHYDE = "2000018";
    private static final String DEXAMETHASONE = "2000039";
    private static final String ARSENIC = "2311486";
    private static final String ASBESTOS = "6037214";
    private static final String MAX = "9999992"; // theoretical

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = ECNumberCheckDigit.getInstance();
        valid = new String[] {MIN, FORMALDEHYDE, DEXAMETHASONE, ARSENIC, ASBESTOS, MAX};
        invalid = new String[] { "0000014", // wrong check
                "200-001-8", // format chars
                " 9999992", "9999992 ", " 9999992 ", };
    }

}
