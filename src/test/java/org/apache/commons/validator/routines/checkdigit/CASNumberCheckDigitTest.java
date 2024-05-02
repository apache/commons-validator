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
 * CAS Number Check Digit Test.
 */
public class CASNumberCheckDigitTest extends AbstractCheckDigitTest {

    private static final String WATER = "7732-18-5";
    private static final String ETHANOL = "64-17-5";
    private static final String ASPIRIN = "50-78-2";
    private static final String COFFEIN = "58-08-2";
    private static final String FORMALDEHYDE = "50-00-0";
    private static final String DEXAMETHASONE = "50-02-2";
    private static final String ARSENIC = "7440-38-2";
    private static final String ASBESTOS = "1332-21-4";
    
    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = CASNumberCheckDigit.CAS_CHECK_DIGIT;
        valid = new String[] {WATER, ETHANOL, ASPIRIN, COFFEIN, FORMALDEHYDE, DEXAMETHASONE, ARSENIC, ASBESTOS };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String removeCheckDigit(final String code) {
    	String cde = (String)CASNumberCheckDigit.REGEX_VALIDATOR.validate(code);
    	if(cde == null || cde.length() <= checkDigitLth) return null;
    	return cde.substring(0, cde.length() - checkDigitLth);
    }
    
}
