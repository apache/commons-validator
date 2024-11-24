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
package org.apache.commons.validator.routines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.junit.jupiter.api.Test;

/**
 * Test Case for RegexValidatorTest.
 */
public class RegexValidatorTest {

    private static final String REGEX = "^([abc]*)(?:\\-)([DEF]*)(?:\\-)([123]*)$";

    private static final String COMPONENT_1 = "([abc]{3})";
    private static final String COMPONENT_2 = "([DEF]{3})";
    private static final String COMPONENT_3 = "([123]{3})";
    private static final String SEPARATOR_1 = "(?:\\-)";
    private static final String SEPARATOR_2 = "(?:\\s)";
    private static final String REGEX_1 = "^" + COMPONENT_1 + SEPARATOR_1 + COMPONENT_2 + SEPARATOR_1 + COMPONENT_3 + "$";
    private static final String REGEX_2 = "^" + COMPONENT_1 + SEPARATOR_2 + COMPONENT_2 + SEPARATOR_2 + COMPONENT_3 + "$";
    private static final String REGEX_3 = "^" + COMPONENT_1 + COMPONENT_2 + COMPONENT_3 + "$";
    private static final String[] MULTIPLE_REGEX = { REGEX_1, REGEX_2, REGEX_3 };

    /**
     * Compare two arrays
     *
     * @param label  Label for the test
     * @param expect Expected array
     * @param result Actual array
     */
    private void checkArray(final String label, final String[] expect, final String[] result) {

        // Handle nulls
        if (expect == null || result == null) {
            if (expect == null && result == null) {
                return; // valid, both null
            }
            fail(label + " Null expect=" + expect + " result=" + result);
            return; // not strictly necessary, but prevents possible NPE below
        }

        // Check Length
        if (expect.length != result.length) {
            fail(label + " Length expect=" + expect.length + " result=" + result.length);
        }

        // Check Values
        for (int i = 0; i < expect.length; i++) {
            assertEquals(expect[i], result[i], label + " value[" + i + "]");
        }
    }

    /**
     * Test exceptions
     */
    @Test
    public void testExceptions() {
        final String invalidRegex = "^([abCD12]*$";
        try {
            new RegexValidator(invalidRegex);
        } catch (final PatternSyntaxException e) {
            // expected
        }
    }

    @Test
    public void testGetPatterns() {
        final RegexValidator regexValidator = new RegexValidator(MULTIPLE_REGEX);
        assertNotSame(regexValidator.getPatterns(), regexValidator.getPatterns());
        final Pattern[] patterns = regexValidator.getPatterns();
        assertEquals(REGEX_1, patterns[0].pattern());
        assertEquals(REGEX_2, patterns[1].pattern());
        assertEquals(REGEX_3, patterns[2].pattern());
    }

    /**
     * Test exceptions
     */
    @Test
    public void testMissingRegex() {

        // Single Regular Expression - null
        Exception e = assertThrows(IllegalArgumentException.class, () -> new RegexValidator((String) null), "Single Null");
        assertEquals("Regular expression[0] is missing", e.getMessage(), "Single Null");

        // Single Regular Expression - Zero Length
        e = assertThrows(IllegalArgumentException.class, () -> new RegexValidator(""), "Single Zero Length");
        assertEquals("Regular expression[0] is missing", e.getMessage(), "Single Zero Length");

        // Multiple Regular Expression - Null array
        e = assertThrows(IllegalArgumentException.class, () -> new RegexValidator((String[]) null), "Null Array");
        assertEquals("Regular expressions are missing", e.getMessage(), "Null Array");

        // Multiple Regular Expression - Zero Length array
        e = assertThrows(IllegalArgumentException.class, () -> new RegexValidator(), "Zero Length Array");
        assertEquals("Regular expressions are missing", e.getMessage(), "Zero Length Array");

        // Multiple Regular Expression - Array has Null
        e = assertThrows(IllegalArgumentException.class, () -> new RegexValidator(new String[]{"ABC", null}), "Array has Null");
        assertEquals("Regular expression[1] is missing", e.getMessage(), "Array has Null");

        // Multiple Regular Expression - Array has Zero Length
        e = assertThrows(IllegalArgumentException.class, () -> new RegexValidator(new String[]{"", "ABC"}), "Array has Zero Length");
        assertEquals("Regular expression[0] is missing", e.getMessage(), "Array has Zero Length");
    }

    /**
     * Test with multiple regular expressions (case in-sensitive).
     */
    @Test
    public void testMultipleInsensitive() {

        // Set up In-sensitive Validators
        final RegexValidator multiple = new RegexValidator(MULTIPLE_REGEX, false);
        final RegexValidator single1 = new RegexValidator(REGEX_1, false);
        final RegexValidator single2 = new RegexValidator(REGEX_2, false);
        final RegexValidator single3 = new RegexValidator(REGEX_3, false);

        // Set up test values
        String value = "AAC FDE 321";
        final String expect = "AACFDE321";
        final String[] array = { "AAC", "FDE", "321" };

        // isValid()
        assertTrue(multiple.isValid(value), "isValid() Multiple");
        assertFalse(single1.isValid(value), "isValid() 1st");
        assertTrue(single2.isValid(value), "isValid() 2nd");
        assertFalse(single3.isValid(value), "isValid() 3rd");

        // validate()
        assertEquals(expect, multiple.validate(value), "validate() Multiple");
        assertNull(single1.validate(value), "validate() 1st");
        assertEquals(expect, single2.validate(value), "validate() 2nd");
        assertNull(single3.validate(value), "validate() 3rd");

        // match()
        checkArray("match() Multiple", array, multiple.match(value));
        checkArray("match() 1st", null, single1.match(value));
        checkArray("match() 2nd", array, single2.match(value));
        checkArray("match() 3rd", null, single3.match(value));

        // All invalid
        value = "AAC*FDE*321";
        assertFalse(multiple.isValid(value), "isValid() Invalid");
        assertNull(multiple.validate(value), "validate() Invalid");
        assertNull(multiple.match(value), "match() Multiple");
    }

    /**
     * Test with multiple regular expressions (case sensitive).
     */
    @Test
    public void testMultipleSensitive() {

        // Set up Sensitive Validators
        final RegexValidator multiple = new RegexValidator(MULTIPLE_REGEX);
        final RegexValidator single1 = new RegexValidator(REGEX_1);
        final RegexValidator single2 = new RegexValidator(REGEX_2);
        final RegexValidator single3 = new RegexValidator(REGEX_3);

        // Set up test values
        String value = "aac FDE 321";
        final String expect = "aacFDE321";
        final String[] array = { "aac", "FDE", "321" };

        // isValid()
        assertTrue( multiple.isValid(value), "Sensitive isValid() Multiple");
        assertFalse(single1.isValid(value), "Sensitive isValid() 1st");
        assertTrue( single2.isValid(value), "Sensitive isValid() 2nd");
        assertFalse(single3.isValid(value), "Sensitive isValid() 3rd");

        // validate()
        assertEquals(expect, multiple.validate(value), "Sensitive validate() Multiple");
        assertNull(single1.validate(value), "Sensitive validate() 1st");
        assertEquals(expect, single2.validate(value), "Sensitive validate() 2nd");
        assertNull(single3.validate(value), "Sensitive validate() 3rd");

        // match()
        checkArray("Sensitive match() Multiple", array, multiple.match(value));
        checkArray("Sensitive match() 1st", null, single1.match(value));
        checkArray("Sensitive match() 2nd", array, single2.match(value));
        checkArray("Sensitive match() 3rd", null, single3.match(value));

        // All invalid
        value = "AAC*FDE*321";
        assertFalse(multiple.isValid(value), "isValid() Invalid");
        assertNull(multiple.validate(value), "validate() Invalid");
        assertNull(multiple.match(value), "match() Multiple");
    }

    /**
     * Test Null value
     */
    @Test
    public void testNullValue() {

        final RegexValidator validator = new RegexValidator(REGEX);
        assertFalse(validator.isValid(null), "Instance isValid()");
        assertNull(validator.validate(null), "Instance validate()");
        assertNull(validator.match(null), "Instance match()");
    }

    /**
     * Test instance methods with single regular expression.
     */
    @Test
    public void testSingle() {
        final RegexValidator sensitive = new RegexValidator(REGEX);
        final RegexValidator insensitive = new RegexValidator(REGEX, false);

        // isValid()
        assertTrue( sensitive.isValid("ac-DE-1"), "Sensitive isValid() valid");
        assertFalse(sensitive.isValid("AB-de-1"), "Sensitive isValid() invalid");
        assertTrue( insensitive.isValid("AB-de-1"), "Insensitive isValid() valid");
        assertFalse(insensitive.isValid("ABd-de-1"), "Insensitive isValid() invalid");

        // validate()
        assertEquals(sensitive.validate("ac-DE-1"), "acDE1", "Sensitive validate() valid");
        assertNull(sensitive.validate("AB-de-1"), "Sensitive validate() invalid");
        assertEquals(insensitive.validate("AB-de-1"), "ABde1", "Insensitive validate() valid");
        assertNull(insensitive.validate("ABd-de-1"), "Insensitive validate() invalid");

        // match()
        checkArray("Sensitive match() valid", new String[] { "ac", "DE", "1" }, sensitive.match("ac-DE-1"));
        checkArray("Sensitive match() invalid", null, sensitive.match("AB-de-1"));
        checkArray("Insensitive match() valid", new String[] { "AB", "de", "1" }, insensitive.match("AB-de-1"));
        checkArray("Insensitive match() invalid", null, insensitive.match("ABd-de-1"));
        assertEquals(new RegexValidator("^([A-Z]*)$").validate("ABC"), "ABC", "validate one");
        checkArray("match one", new String[] { "ABC" }, new RegexValidator("^([A-Z]*)$").match("ABC"));
    }

    /**
     * Test toString() method
     */
    @Test
    public void testToString() {
        final RegexValidator single = new RegexValidator(REGEX);
        assertEquals(single.toString(), "RegexValidator{" + REGEX + "}", "Single");

        final RegexValidator multiple = new RegexValidator(REGEX, REGEX);
        assertEquals(multiple.toString(), "RegexValidator{" + REGEX + "," + REGEX + "}", "Multiple");
    }

}
