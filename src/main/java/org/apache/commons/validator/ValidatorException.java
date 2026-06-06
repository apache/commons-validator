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

import java.util.IllegalFormatException;

/**
 * The base exception for the Validator Framework. All other {@code Exception}s thrown during calls to {@code Validator.validate()} are considered errors.
 */
public class ValidatorException extends Exception {

    private static final long serialVersionUID = 1025759372615616964L;

    /**
     * Constructs an Exception with no specified detail message.
     */
    public ValidatorException() {
    }

    /**
     * Constructs an Exception with the specified detail message.
     *
     * @param message The error message.
     */
    public ValidatorException(final String message) {
        super(message);
    }

    /**
     * Constructs an Exception with a message and the underlying cause.
     *
     * @param format See {@link String#format(String, Object...)}.
     * @param args   See {@link String#format(String, Object...)}.
     * @throws IllegalFormatException See {@link String#format(String, Object...)}.
     * @since 1.11.0
     */
    public ValidatorException(final String format, final Object... args) {
        super(String.format(format, args));
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <em>not</em> automatically incorporated in this exception's detail message.
     * </p>
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code null} value is permitted, and indicates that
     *                the cause is nonexistent or unknown.)
     * @since 1.11.0
     */
    public ValidatorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message of {@code (cause==null ? null : cause.toString())} (which typically contains the
     * class and detail message of {@code cause}). This constructor is useful for exceptions that are little more than wrappers for other throwables (for
     * example, {@link java.security.PrivilegedActionException}).
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code null} value is permitted, and indicates that the
     *              cause is nonexistent or unknown.)
     * @since 1.11.0
     */
    public ValidatorException(final Throwable cause) {
        super(cause);
    }
}