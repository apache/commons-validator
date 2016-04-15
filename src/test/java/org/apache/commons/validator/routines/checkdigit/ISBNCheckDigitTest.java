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
 * ISBN-10/ISBN-13 Check Digit Test.
 *
 * @version $Revision$
 * @since Validator 1.4
 */
public class ISBNCheckDigitTest extends AbstractCheckDigitTest {
    
    /**
     * Constructor
     * @param name test name
     */
    public ISBNCheckDigitTest(String name) {
        super(name);
    }

    /**
     * Set up routine & valid codes.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        routine = ISBNCheckDigit.ISBN_CHECK_DIGIT;
        valid = new String[] {
                "9780072129519",
                "9780764558313",
                "1930110995",
                "020163385X",
                "1590596277",    // ISBN-10 Ubuntu Book
                "9781590596272"  // ISBN-13 Ubuntu Book
                };
        missingMessage = "ISBN Code is missing";
        zeroSum = "000000000000";
    }

    /**
     * Set up routine & valid codes.
     */
    public void testInvalidLength() {
        assertFalse("isValid() Lth 9 ", routine.isValid("123456789"));
        assertFalse("isValid() Lth 11", routine.isValid("12345678901"));
        assertFalse("isValid() Lth 12", routine.isValid("123456789012"));
        assertFalse("isValid() Lth 14", routine.isValid("12345678901234"));

        try {
            routine.calculate("12345678");
            fail("calculate() Lth 8 - expected exception");
        } catch (Exception e) {
            assertEquals("calculate() Lth 8", "Invalid ISBN Length = 8", e.getMessage());
        }

        try {
            routine.calculate("1234567890");
            fail("calculate() Lth 10 - expected exception");
        } catch (Exception e) {
            assertEquals("calculate() Lth 10", "Invalid ISBN Length = 10", e.getMessage());
        }

        try {
            routine.calculate("12345678901");
            fail("calculate() Lth 11 - expected exception");
        } catch (Exception e) {
            assertEquals("calculate() Lth 11", "Invalid ISBN Length = 11", e.getMessage());
        }

        try {
            routine.calculate("1234567890123");
            fail("calculate() Lth 13 - expected exception");
        } catch (Exception e) {
            assertEquals("calculate() Lth 13", "Invalid ISBN Length = 13", e.getMessage());
        }
    }
    

}
