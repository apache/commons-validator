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

/**
 * A class for validating 10 digit ISBN codes.
 * Based on this
 * <a href="http://www.isbn.org/standards/home/isbn/international/html/usm4.htm">
 * algorithm</a>
 *
 * <b>NOTE:</b> This has been replaced by the new
 *  {@link org.apache.commons.validator.routines.ISBNValidator}.
 *
 * @since 1.2.0
 * @deprecated Use the new ISBNValidator in the routines package
 */
@Deprecated
public class ISBNValidator {

    /**
     * Constructs a new instance.
     */
    public ISBNValidator() {
    }

    /**
     * If the ISBN is formatted with space or dash separators its format is
     * validated.  Then the digits in the number are weighted, summed, and
     * divided by 11 according to the ISBN algorithm.  If the result is zero,
     * the ISBN is valid.  This method accepts formatted or raw ISBN codes.
     *
     * @param isbn Candidate ISBN number to be validated. {@code null} is
     * considered invalid.
     * @return true if the string is a valid ISBN code.
     */
    public boolean isValid(final String isbn) {
        return org.apache.commons.validator.routines.ISBNValidator.getInstance().isValidISBN10(isbn);
    }

}
