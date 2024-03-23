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
package org.apache.commons.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link ISBNValidator}.
 *
 * @deprecated to be removed when the org.apache.commons.validator.ISBNValidator class is removed
 */
@Deprecated
public class ISBNValidatorTest {

    private static final String VALID_ISBN_RAW = "1930110995";
    private static final String VALID_ISBN_DASHES = "1-930110-99-5";
    private static final String VALID_ISBN_SPACES = "1 930110 99 5";
    private static final String VALID_ISBN_X = "0-201-63385-X";
    private static final String INVALID_ISBN = "068-556-98-45";

    @Test
    public void testIsValid() throws Exception {
        final ISBNValidator validator = new ISBNValidator();
        assertFalse(validator.isValid(null));
        assertFalse(validator.isValid(""));
        assertFalse(validator.isValid("1"));
        assertFalse(validator.isValid("12345678901234"));
        assertFalse(validator.isValid("dsasdsadsads"));
        assertFalse(validator.isValid("535365"));
        assertFalse(validator.isValid("I love sparrows!"));
        assertFalse(validator.isValid("--1 930110 99 5"));
        assertFalse(validator.isValid("1 930110 99 5--"));
        assertFalse(validator.isValid("1 930110-99 5-"));

        assertTrue(validator.isValid(VALID_ISBN_RAW));
        assertTrue(validator.isValid(VALID_ISBN_DASHES));
        assertTrue(validator.isValid(VALID_ISBN_SPACES));
        assertTrue(validator.isValid(VALID_ISBN_X));
        assertFalse(validator.isValid(INVALID_ISBN));
    }

}
