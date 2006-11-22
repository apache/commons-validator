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

import org.apache.oro.text.perl.Perl5Util;

/**
 * A class for validating 10 digit ISBN codes.
 * Based on this 
 * <a href="http://www.isbn.org/standards/home/isbn/international/html/usm4.htm">
 * algorithm</a>
 *
 * @version $Revision$ $Date$
 * @since Validator 1.2.0
 */
public class ISBNValidator {

    private static final String SEP = "(\\-|\\s)";
    private static final String GROUP = "(\\d{1,5})";
    private static final String PUBLISHER = "(\\d{1,7})";
    private static final String TITLE = "(\\d{1,6})";
    private static final String CHECK = "([0-9X])";

    /**
     * ISBN consists of 4 groups of numbers separated by either dashes (-)
     * or spaces.  The first group is 1-5 characters, second 1-7, third 1-6,
     * and fourth is 1 digit or an X.
     */
    private static final String ISBN_PATTERN =
        "/^" + GROUP + SEP + PUBLISHER + SEP + TITLE + SEP + CHECK + "$/";

    /**
     * Default Constructor.
     */
    public ISBNValidator() {
        super();
    }

    /**
     * If the ISBN is formatted with space or dash separators its format is
     * validated.  Then the digits in the number are weighted, summed, and
     * divided by 11 according to the ISBN algorithm.  If the result is zero,
     * the ISBN is valid.  This method accepts formatted or raw ISBN codes.
     *
     * @param isbn Candidate ISBN number to be validated. <code>null</code> is
     * considered invalid.
     * @return true if the string is a valid ISBN code.
     */
    public boolean isValid(String isbn) {
        if (isbn == null || isbn.length() < 10 || isbn.length() > 13) {
            return false;
        }

        if (isFormatted(isbn) && !isValidPattern(isbn)) {
            return false;
        }

        isbn = clean(isbn);
        if (isbn.length() != 10) {
            return false;
        }

        return (sum(isbn) % 11) == 0;
    }
    
    /**
     * Returns the sum of the weighted ISBN characters.
     */
    private int sum(String isbn) {
        int total = 0;
        for (int i = 0; i < 9; i++) {
            int weight = 10 - i;
            total += (weight * toInt(isbn.charAt(i)));
        }
        total += toInt(isbn.charAt(9)); // add check digit
        return total;
    }

    /**
     * Removes all non-digit characters except for 'X' which is a valid ISBN
     * character. 
     */
    private String clean(String isbn) {
        StringBuffer buf = new StringBuffer(10);
        
        for (int i = 0; i < isbn.length(); i++) {
            char digit = isbn.charAt(i);
            if (Character.isDigit(digit) || (digit == 'X')) {
                buf.append(digit);
            }
        }

        return buf.toString();
    }

    /**
     * Returns the numeric value represented by the character.  If the 
     * character is not a digit but an 'X', 10 is returned.
     */
    private int toInt(char ch) {
        return (ch == 'X') ? 10 : Character.getNumericValue(ch);
    }
    
    /**
     * Returns true if the ISBN contains one of the separator characters space
     * or dash.
     */
    private boolean isFormatted(String isbn) {
        return ((isbn.indexOf('-') != -1) || (isbn.indexOf(' ') != -1));
    }

    /**
     * Returns true if the ISBN is formatted properly.
     */
    private boolean isValidPattern(String isbn) {
        return new Perl5Util().match(ISBN_PATTERN, isbn);
    }

}
