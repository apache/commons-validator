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
 * CAS Number Check Digit Tests.
 */
public class CASNumberCheckDigitTest extends AbstractCheckDigitTest {

	// valid CAS Number with dashes removed
    private static final String MIN = "10004"; // theoretical
    private static final String WATER = "7732185";
    private static final String ETHANOL = "64175";
    private static final String ASPIRIN = "50782";
    private static final String COFFEIN = "58082";
    private static final String FORMALDEHYDE = "50000";
    private static final String DEXAMETHASONE = "50022";
    private static final String ARSENIC = "7440382";
    private static final String ASBESTOS = "1332214";
    private static final String MAX = "9999999995"; // theoretical

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = CASNumberCheckDigit.getInstance();
        valid = new String[] {MIN, WATER, ETHANOL, ASPIRIN, COFFEIN, FORMALDEHYDE, DEXAMETHASONE, ARSENIC, ASBESTOS, MAX};
        invalid = new String[] { "10005", // wrong check
                "7732-18-5", // format chars
                " 9999999995", "9999999995 ", " 9999999995 ", };
    }

}
