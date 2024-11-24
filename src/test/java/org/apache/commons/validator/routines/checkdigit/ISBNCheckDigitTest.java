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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * ISBN-10/ISBN-13 Check Digit Test.
 */
public class ISBNCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = ISBNCheckDigit.ISBN_CHECK_DIGIT;
        valid = new String[] { "9780072129519", "9780764558313", "1930110995", "020163385X", "1590596277", // ISBN-10 Ubuntu Book
                "9781590596272" // ISBN-13 Ubuntu Book
        };
        missingMessage = "ISBN Code is missing";
        zeroSum = "000000000000";
    }

    /**
     * Sets up routine & valid codes.
     */
    @Test
    public void testInvalidLength() {
        assertFalse(routine.isValid("123456789"), "isValid() Lth 9 ");
        assertFalse(routine.isValid("12345678901"), "isValid() Lth 11");
        assertFalse(routine.isValid("123456789012"), "isValid() Lth 12");
        assertFalse(routine.isValid("12345678901234"), "isValid() Lth 14");

        Exception e = assertThrows(CheckDigitException.class, () -> routine.calculate("12345678"), "calculate() Lth 8");
        assertEquals(e.getMessage(), "Invalid ISBN Length = 8", "calculate() Lth 8");

        e = assertThrows(CheckDigitException.class, () -> routine.calculate("1234567890"), "calculate() Lth 10");
        assertEquals("Invalid ISBN Length = 10", e.getMessage(), "calculate() Lth 10");

        e = assertThrows(CheckDigitException.class, () -> routine.calculate("12345678901"), "calculate() Lth 11");
        assertEquals("Invalid ISBN Length = 11", e.getMessage(), "calculate() Lth 11");

        e = assertThrows(CheckDigitException.class, () -> routine.calculate("1234567890123"), "calculate() Lth 13");
        assertEquals("Invalid ISBN Length = 13", e.getMessage(), "calculate() Lth 13");
    }

}
