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
 * Tests {@link ECNumberValidator}.
 */
public class ECNumberValidatorTest {

    private static final ECNumberValidator VALIDATOR = ECNumberValidator.getInstance();

    private static final String FORMALDEHYDE = "200-001-8"; // this is the first entry in EINECS
    private static final String DEXAMETHASONE = "200-003-9";
    private static final String ARSENIC = "231-148-6";
    private static final String ASBESTOS = "603-721-4";

    private final String[] validFormat = {
            " 200-000-2 ", // theoretical EINECS minimum with spaces
            FORMALDEHYDE,
            DEXAMETHASONE,
            ARSENIC,
            ASBESTOS,
            "\t999-999-2\n" }; // theoretical maximum with white spaces (TAB and NL)

    private final String[] invalidFormat = { null, "", // empty
            "   ", // empty
            "200-001-2", // proper check digit is '8', see above
            "200-003-X", // proper check digit is '9', see above
            "231-148-6!", // proper check digit is '6', see above
            "603-721-0", // proper check digit is '4', see above
            "603-721+4", // no dash
            "999-999-9", // proper check digit is '2', see above
            "999999999" };

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
