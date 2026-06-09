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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.IllegalFormatException;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ValidatorException}.
 */
public class ValidatorExceptionTest {

    /**
     * Tests the no-arg constructor.
     */
    @Test
    public void testConstructorNoArg() {
        final ValidatorException e = new ValidatorException();
        assertNull(e.getMessage());
        assertNull(e.getCause());
    }

    /**
     * Tests the constructor with a cause.
     */
    @Test
    public void testConstructorWithCause() {
        final Throwable cause = new RuntimeException("root cause");
        final ValidatorException e = new ValidatorException(cause);
        assertSame(cause, e.getCause());
    }

    /**
     * Tests the constructor with a format string and multiple arguments.
     */
    @Test
    public void testConstructorWithFormatMultipleArgs() {
        final ValidatorException e = new ValidatorException("%s failed with code %d at %s", "validation", 404, "field1");
        assertEquals("validation failed with code 404 at field1", e.getMessage());
        assertNull(e.getCause());
    }

    /**
     * Tests the constructor with a format string and no arguments.
     */
    @Test
    public void testConstructorWithFormatNoArgs() {
        final ValidatorException e = new ValidatorException("no format args");
        assertEquals("no format args", e.getMessage());
        assertNull(e.getCause());
    }

    /**
     * Tests the constructor with a format string and one argument.
     */
    @Test
    public void testConstructorWithFormatOneArg() {
        final ValidatorException e = new ValidatorException("value is %d", 42);
        assertEquals("value is 42", e.getMessage());
        assertNull(e.getCause());
    }

    /**
     * Tests the constructor with an invalid format string throws {@link IllegalFormatException}.
     */
    @Test
    public void testConstructorWithInvalidFormat() {
        assertThrows(IllegalFormatException.class, () -> new ValidatorException("%d is not a string", "oops"));
    }

    /**
     * Tests the constructor with a message.
     */
    @Test
    public void testConstructorWithMessage() {
        final String message = "test error message";
        final ValidatorException e = new ValidatorException(message);
        assertEquals(message, e.getMessage());
        assertNull(e.getCause());
    }

    /**
     * Tests the constructor with a message and cause.
     */
    @Test
    public void testConstructorWithMessageAndCause() {
        final String message = "error with cause";
        final Throwable cause = new RuntimeException("root cause");
        final ValidatorException e = new ValidatorException(message, cause);
        assertEquals(message, e.getMessage());
        assertSame(cause, e.getCause());
    }

    /**
     * Tests the constructor with a message and a null cause.
     */
    @Test
    public void testConstructorWithMessageAndNullCause() {
        final String message = "error with null cause";
        final ValidatorException e = new ValidatorException(message, (Throwable) null);
        assertEquals(message, e.getMessage());
        assertNull(e.getCause());
    }

    /**
     * Tests the constructor with a null cause.
     */
    @Test
    public void testConstructorWithNullCause() {
        final ValidatorException e = new ValidatorException((Throwable) null);
        assertNull(e.getCause());
        assertNull(e.getMessage());
    }

    /**
     * Tests the constructor with a null format string throws {@link NullPointerException}.
     */
    @Test
    public void testConstructorWithNullFormat() {
        assertThrows(NullPointerException.class, () -> new ValidatorException((String) null, "arg"));
    }

    /**
     * Tests the constructor with a null message.
     */
    @Test
    public void testConstructorWithNullMessage() {
        final ValidatorException e = new ValidatorException((String) null);
        assertNull(e.getMessage());
        assertNull(e.getCause());
    }

    /**
     * Tests the constructor with a null message and a cause.
     */
    @Test
    public void testConstructorWithNullMessageAndCause() {
        final Throwable cause = new RuntimeException("root cause");
        final ValidatorException e = new ValidatorException((String) null, cause);
        assertNull(e.getMessage());
        assertSame(cause, e.getCause());
    }

    /**
     * Tests the constructor with a null message and a null cause.
     */
    @Test
    public void testConstructorWithNullMessageAndNullCause() {
        final ValidatorException e = new ValidatorException((String) null, (Throwable) null);
        assertNull(e.getMessage());
        assertNull(e.getCause());
    }

    /**
     * Tests that ValidatorException is an instance of Exception.
     */
    @Test
    public void testIsException() {
        final ValidatorException e = new ValidatorException();
        assertEquals(Exception.class, e.getClass().getSuperclass());
    }

    /**
     * Tests that ValidatorException can be thrown and caught.
     */
    @Test
    public void testThrowAndCatch() {
        final String message = "thrown exception";
        final ValidatorException e = assertThrows(ValidatorException.class, () -> {
            throw new ValidatorException(message);
        });
        assertEquals(message, e.getMessage());
    }
}
