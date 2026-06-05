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

/**
 * Abstracts {@link CheckDigit} for implementations.
 */
abstract class AbstractCheckDigit implements CheckDigit {

    /**
     * Result from {@code Character.getNumericValue('Z')}.
     */
    static final int MAX_ALPHANUMERIC_VALUE = 35;

    boolean isAsciiAlpha(final char ch) {
        return ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z'; // CHECKSTYLE IGNORE MagicNumber
    }

    boolean isAsciiAlphaNum(final char ch) {
        return isAsciiDigit(ch) || isAsciiAlpha(ch);
    }

    boolean isAsciiDigit(final char ch) {
        return ch >= '0' && ch <= '9'; // CHECKSTYLE IGNORE MagicNumber
    }

    boolean isAsciiDigit(final int ch) {
        return ch >= 0 && ch <= 9; // CHECKSTYLE IGNORE MagicNumber
    }
}
