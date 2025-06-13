/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
 * Test the GenericValidator class.
 */
class GenericValidatorTest {

    @Test
    void testMaxLength() {

        // Use 0 for line end length
        assertFalse(GenericValidator.maxLength("12345\n\r", 4, 0), "Max=4 End=0");
        assertTrue(GenericValidator.maxLength("12345\n\r", 5, 0), "Max=5 End=0");
        assertTrue(GenericValidator.maxLength("12345\n\r", 6, 0), "Max=6 End=0");
        assertTrue(GenericValidator.maxLength("12345\n\r", 7, 0), "Max=7 End=0");

        // Use 1 for line end length
        assertFalse(GenericValidator.maxLength("12345\n\r", 4, 1), "Max=4 End=1");
        assertFalse(GenericValidator.maxLength("12345\n\r", 5, 1), "Max=5 End=1");
        assertTrue(GenericValidator.maxLength("12345\n\r", 6, 1), "Max=6 End=1");
        assertTrue(GenericValidator.maxLength("12345\n\r", 7, 1), "Max=7 End=1");

        // Use 2 for line end length
        assertFalse(GenericValidator.maxLength("12345\n\r", 4, 2), "Max=4 End=2");
        assertFalse(GenericValidator.maxLength("12345\n\r", 5, 2), "Max=5 End=2");
        assertFalse(GenericValidator.maxLength("12345\n\r", 6, 2), "Max=6 End=2");
        assertTrue(GenericValidator.maxLength("12345\n\r", 7, 2), "Max=7 End=2");
    }

    @Test
    void testMinLength() {

        // Use 0 for line end length
        assertTrue(GenericValidator.minLength("12345\n\r", 5, 0), "Min=5 End=0");
        assertFalse(GenericValidator.minLength("12345\n\r", 6, 0), "Min=6 End=0");
        assertFalse(GenericValidator.minLength("12345\n\r", 7, 0), "Min=7 End=0");
        assertFalse(GenericValidator.minLength("12345\n\r", 8, 0), "Min=8 End=0");

        // Use 1 for line end length
        assertTrue(GenericValidator.minLength("12345\n\r", 5, 1), "Min=5 End=1");
        assertTrue(GenericValidator.minLength("12345\n\r", 6, 1), "Min=6 End=1");
        assertFalse(GenericValidator.minLength("12345\n\r", 7, 1), "Min=7 End=1");
        assertFalse(GenericValidator.minLength("12345\n\r", 8, 1), "Min=8 End=1");

        // Use 2 for line end length
        assertTrue(GenericValidator.minLength("12345\n\r", 5, 2), "Min=5 End=2");
        assertTrue(GenericValidator.minLength("12345\n\r", 6, 2), "Min=6 End=2");
        assertTrue(GenericValidator.minLength("12345\n\r", 7, 2), "Min=7 End=2");
        assertFalse(GenericValidator.minLength("12345\n\r", 8, 2), "Min=8 End=2");
    }

}
