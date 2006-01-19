/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright 2006 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.validator.routines;

import junit.framework.TestCase;

import java.util.Locale;
import java.math.BigDecimal;
/**
 * Test Case for CurrencyValidator.
 * 
 * @version $Revision$ $Date$
 */
public class CurrencyValidatorTest extends TestCase {
    
    protected CurrencyValidator validator;

    /**
     * Main
     * @param args arguments
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(CurrencyValidatorTest.class);
    }
    
    /**
     * Constructor
     * @param name test name
     */
    public CurrencyValidatorTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        validator = new CurrencyValidator();
    }

    /**
     * Tear down
     * @throws Exception
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        validator = null;
    }

    /**
     * Test Invalid, strict=false
     */
    public void testLocaleCurrency() {
        String inputString = null;
        BigDecimal expected = new BigDecimal("1234.56");
        BigDecimal result = null;

        inputString = "$1,234.56";
        result = validator.validate(inputString, Locale.US);
        assertEquals("US currency", expected, result);

        inputString = "£1,234.56";
        result = validator.validate(inputString, Locale.UK);
        assertEquals("UK currency", expected, result);
    }

}
