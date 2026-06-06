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

package org.apache.commons.validator.routines.checkdigit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.IllegalFormatException;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link CheckDigitException}.
 */
class CheckDigitExceptionTest {

    /**
     * Tests that an empty format string with no args produces an empty message.
     */
    @Test
    void testFormatEmptyString() {
        assertEquals("", new CheckDigitException("").getMessage());
    }

    /**
     * Tests that a format string with multiple arguments of mixed types is formatted correctly.
     */
    @Test
    void testFormatMultipleMixedArgs() {
        final CheckDigitException e = new CheckDigitException("Position %d has invalid char '%s' (code %d)", 5, "X", 88);
        assertEquals("Position 5 has invalid char 'X' (code 88)", e.getMessage());
    }

    /**
     * Tests that the varargs constructor formats {@code %n} as the platform line separator.
     */
    @Test
    void testFormatNewline() {
        assertEquals(String.format("Line1%nLine2 %s", "end"), new CheckDigitException("Line1%nLine2 %s", "end").getMessage());
    }

    /**
     * Tests that a format string with no arguments produces the expected message.
     */
    @Test
    void testFormatNoArgs() throws CheckDigitException {
        assertEquals("No arguments here", new CheckDigitException("No arguments here").getMessage());
    }

    /**
     * Tests that a null argument in the varargs is handled and formatted as "null".
     */
    @Test
    void testFormatNullArg() {
        assertEquals("Value is null", new CheckDigitException("Value is %s", (Object) null).getMessage());
    }

    /**
     * Tests that a format string with a single integer argument is formatted correctly.
     */
    @Test
    void testFormatOneIntArg() {
        assertEquals("Invalid Character[3] = '65'", new CheckDigitException("Invalid Character[%d] = '%d'", 3, 65).getMessage());
    }

    /**
     * Tests that a format string with a single string argument is formatted correctly.
     */
    @Test
    void testFormatOneStringArg() {
        assertEquals("Invalid value: ABC", new CheckDigitException("Invalid value: %s", "ABC").getMessage());
    }

    /**
     * Tests that an invalid format string throws {@link IllegalFormatException}.
     */
    @Test
    void testInvalidFormatThrowsException() {
        assertThrows(IllegalFormatException.class, () -> new CheckDigitException("Bad format %q", "x"));
    }

    /**
     * Tests the format used internally by {@link VerhoeffCheckDigit} for invalid characters.
     */
    @Test
    void testVerhoeffStyleFormat() {
        assertEquals("Invalid Character[2] = '42'", new CheckDigitException("Invalid Character[%d] = '%d'", 2, 42).getMessage());
    }
}
