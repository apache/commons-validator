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
package org.apache.commons.validator.routines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link CASNumberValidator}.
 */
public class CASNumberValidatorTest {

    private static final CASNumberValidator VALIDATOR = CASNumberValidator.getInstance();

    private static final String WATER = "7732-18-5";
    private static final String ETHANOL = "64-17-5";
    private static final String ASPIRIN = "50-78-2";
    private static final String COFFEIN = "58-08-2";
    private static final String FORMALDEHYDE = "50-00-0";
    private static final String DEXAMETHASONE = "50-02-2";
    private static final String ARSENIC = "7440-38-2";
    /**
     * There are different CAS numbers for asbestos:
     * <br/><a href="https://commonchemistry.cas.org/detail?cas_rn=1332-21-4">cas_rn=1332-21-4</a>
     * <br/>https://commonchemistry.cas.org/detail?cas_rn=132207-32-0 Chrysotile asbestos
     * <br/>https://commonchemistry.cas.org/detail?cas_rn=12172-73-5  Amosite asbestos
     * <br/>https://commonchemistry.cas.org/detail?cas_rn=77536-66-4  Actinolite asbestos
     * <br/>https://commonchemistry.cas.org/detail?cas_rn=77536-68-6  Tremolite asbestos
     * <br/>https://commonchemistry.cas.org/detail?cas_rn=77536-67-5  Anthophyllite asbestos
     * <br/>https://commonchemistry.cas.org/detail?cas_rn=12001-28-4  Crocidolite asbestos
     * <br/>https://commonchemistry.cas.org/detail?cas_rn=12001-29-5  Chrysotile
     * <br/>and some deleted or replaced CAS RNs
     */
    private static final String ASBESTOS = "1332-21-4";

    private final String[] validFormat = {
            " 10-00-4 ", // theoretical minimum with spaces
            WATER,
            ETHANOL,
            ASPIRIN,
            COFFEIN,
            FORMALDEHYDE,
            DEXAMETHASONE,
            ARSENIC,
            ASBESTOS,
            "\t9999999-99-5\n", }; // theoretical maximum with white spaces (TAB and NL)

    private final String[] invalidFormat = { null, "", // empty
            "   ", // empty
            "7732-18-V", // proper check digit is '5', see above
            "0064-17-5", // leading zeros
            "50-78-02",  // proper check digit is '2', see above
            "58-08-0",   // proper check digit is '2', see above
            "50-00-X",   // proper check digit is '0', see above
            "7440-38.2", // no dash
            "9999999-99-9", // proper check digit is '5', see above
            "999999999999" };

    @Test
    public void testInvalidFalse() {
        for (final String f : invalidFormat) {
            assertFalse(VALIDATOR.isValid(f), f);
        }
    }

    @Test
    public void testIsValidTrue() {
        for (final String f : validFormat) {
            assertTrue(VALIDATOR.isValid(f), f);
        }
    }

    @Test
    public void testValidate() {
        for (final String f : validFormat) {
            assertEquals(VALIDATOR.validate(f), f.replaceAll("\\-", "").trim());
        }
        for (final String f : invalidFormat) {
            assertNull(VALIDATOR.validate(f));
        }
    }

}
