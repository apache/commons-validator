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

/**
 * International Standard Serial Number (ISSN)
 * is an eight-digit serial number used to
 * uniquely identify a serial publication.
 * <pre> 
 * The format is:
 * 
 * ISSN dddd-dddC
 * where:
 * d = decimal digit (0-9)
 * C = checksum (0-9 or X)
 * 
 * The checksum is formed by adding the first 7 digits multiplied by
 * the position in the entire number (counting from the right).
 * For example, abcd-efg would be 8a + 7b + 6c + 5d + 4e +3f +2g.
 * The check digit is modulus 11, where the value 10 is represented by 'X'
 * For example:
 * ISSN 0317-8471
 * ISSN 1050-124X
 * </pre>
 * <p>
 * <b>Note:</b> This class expects the input to be numeric only,
 * with all formatting removed.
 * For example:
 * <pre>
 * 03178471
 * 1050124X
 * </pre>
 * @since 1.5.0
 */
public final class ISSNCheckDigit extends ModulusCheckDigit {


    private static final long serialVersionUID = 1L;

    /** Singleton ISSN Check Digit instance */
    public static final CheckDigit ISSN_CHECK_DIGIT = new ISSNCheckDigit();

    /**
     * Creates the instance using a checkdigit modulus of 11
     */
    public ISSNCheckDigit() {
        super(11); // CHECKSTYLE IGNORE MagicNumber
    }

    @Override
    protected int weightedValue(int charValue, int leftPos, int rightPos) throws CheckDigitException {
        return charValue * (9 - leftPos); // CHECKSTYLE IGNORE MagicNumber
    }

    @Override
    protected String toCheckDigit(int charValue) throws CheckDigitException {
        if (charValue == 10) { // CHECKSTYLE IGNORE MagicNumber
            return "X";
        }
        return super.toCheckDigit(charValue);
    }

    @Override
    protected int toInt(char character, int leftPos, int rightPos)
            throws CheckDigitException {
        if (rightPos == 1 && character == 'X') {
            return 10; // CHECKSTYLE IGNORE MagicNumber
        }
        return super.toInt(character, leftPos, rightPos);
    }
}
