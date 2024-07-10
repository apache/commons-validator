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
 * Tests {@link ECIndexNumberValidator}.
 */
public class ECIndexNumberValidatorTest {

    private static final ECIndexNumberValidator VALIDATOR = ECIndexNumberValidator.getInstance();

    private final String[] validFormat = {
            " 000-000-01-8 ", // theoretical minimum with spaces
            "001-001-00-9", // hydrogen - the first entry
            "003-001-00-4", // lithium
            "017-002-01-X", // Hydrochloric acid, Salzsäure
            "033-001-00-X", // arsenic - last anorganic entry
            "607-310-00-0", // kresoxim-methyl - an organic entry
            "650-013-00-6", // asbestos
            "\t999-999-99-5\n", }; // theoretical maximum with white spaces (TAB and NL)

    private final String[] invalidFormat = { null, "", // empty
            "   ", // empty
            "000-000-01-X", // proper check digit is '8', see above
            "001-001-00-0", // proper check digit is '9', see above
            "003-001-00-6", // proper check digit is '4', see above
            "017-002-01-x", // proper check digit is 'X', see above
            "033-001-00-10", // proper check digit is 'X', see above
            "607-310-00-X", // proper check digit is '0', see above
            "650-013-00_6", // no dash
            "999-999-99-9", // proper check digit is '5', see above
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
