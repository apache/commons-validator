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

    private static final String MIN = "000-001-6"; // theoretical
    private static final String FORMALDEHYDE = "200-001-8"; // this is the first entry in EINECS
    private static final String DEXAMETHASONE = "200-003-9";
    private static final String ARSENIC = "231-148-6";
    private static final String ASBESTOS = "603-721-4";
    private static final String MAX = "999-999-2"; // theoretical

    /**
     * {@inheritDoc}
     */
    @Override
    protected String removeCheckDigit(final String code) {
        final String cde = (String) ECNumberCheckDigit.REGEX_VALIDATOR.validate(code);
        if (cde == null || cde.length() <= checkDigitLth) {
            return null;
        }
        return cde.substring(0, cde.length() - checkDigitLth);
    }

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = ECNumberCheckDigit.getInstance();
        valid = new String[] {MIN, FORMALDEHYDE, DEXAMETHASONE, ARSENIC, ASBESTOS, MAX};
    }

}
