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

import junit.framework.TestCase;

import java.util.Locale;
import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;

/**
 * Test Case for CurrencyValidator.
 * 
 * @version $Revision$
 */
public class CurrencyValidatorTest extends TestCase {
    
    private static final char CURRENCY_SYMBOL = '\u00A4';

    private String US_DOLLAR;
    private String UK_POUND;

    /**
     * Constructor
     * @param name test name
     */
    public CurrencyValidatorTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        US_DOLLAR = (new DecimalFormatSymbols(Locale.US)).getCurrencySymbol();
        UK_POUND  = (new DecimalFormatSymbols(Locale.UK)).getCurrencySymbol();
    }

    /**
     * Tear down
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test Format Type
     */
    public void testFormatType() {
        assertEquals("Format Type A", 1, CurrencyValidator.getInstance().getFormatType());
        assertEquals("Format Type B", AbstractNumberValidator.CURRENCY_FORMAT, CurrencyValidator.getInstance().getFormatType());
    }

    /**
     * Test Valid currency values
     */
    public void testValid() {
        // Set the default Locale
        Locale origDefault = Locale.getDefault();
        Locale.setDefault(Locale.UK);

        BigDecimalValidator validator = CurrencyValidator.getInstance();
        BigDecimal expected   = new BigDecimal("1234.56");
        BigDecimal negative   = new BigDecimal("-1234.56");
        BigDecimal noDecimal  = new BigDecimal("1234.00");
        BigDecimal oneDecimal = new BigDecimal("1234.50");

        assertEquals("Default locale", expected, validator.validate(UK_POUND + "1,234.56"));

        assertEquals("UK locale",     expected,   validator.validate(UK_POUND  + "1,234.56",   Locale.UK));
        assertEquals("UK negative",   negative,   validator.validate("-" + UK_POUND  + "1,234.56",  Locale.UK));
        assertEquals("UK no decimal", noDecimal,  validator.validate(UK_POUND  + "1,234",      Locale.UK));
        assertEquals("UK 1 decimal",  oneDecimal, validator.validate(UK_POUND  + "1,234.5",    Locale.UK));
        assertEquals("UK 3 decimal",  expected,   validator.validate(UK_POUND  + "1,234.567",  Locale.UK));
        assertEquals("UK no symbol",  expected,   validator.validate("1,234.56",    Locale.UK));

        assertEquals("US locale",     expected,   validator.validate(US_DOLLAR + "1,234.56",   Locale.US));
        assertEquals("US negative",   negative,   validator.validate("(" + US_DOLLAR + "1,234.56)", Locale.US));
        assertEquals("US no decimal", noDecimal,  validator.validate(US_DOLLAR + "1,234",      Locale.US));
        assertEquals("US 1 decimal",  oneDecimal, validator.validate(US_DOLLAR + "1,234.5",    Locale.US));
        assertEquals("US 3 decimal",  expected,   validator.validate(US_DOLLAR + "1,234.567",  Locale.US));
        assertEquals("US no symbol",  expected,   validator.validate("1,234.56",    Locale.US));

        // Restore the original default
        Locale.setDefault(origDefault);
    }

    /**
     * Test Invalid currency values
     */
    public void testInvalid() {
        BigDecimalValidator validator = CurrencyValidator.getInstance();

        // Invalid Missing
        assertFalse("isValid() Null Value",    validator.isValid(null));
        assertFalse("isValid() Empty Value",   validator.isValid(""));
        assertNull("validate() Null Value",    validator.validate(null));
        assertNull("validate() Empty Value",   validator.validate(""));

        // Invalid UK
        assertFalse("UK wrong symbol",    validator.isValid(US_DOLLAR + "1,234.56",   Locale.UK));
        assertFalse("UK wrong negative",  validator.isValid("(" + UK_POUND  + "1,234.56)", Locale.UK));

        // Invalid US
        assertFalse("US wrong symbol",    validator.isValid(UK_POUND + "1,234.56",   Locale.US));
        assertFalse("US wrong negative",  validator.isValid("-" + US_DOLLAR + "1,234.56",  Locale.US));
    }

    /**
     * Test Valid integer (non-decimal) currency values
     */
    public void testIntegerValid() {
        // Set the default Locale
        Locale origDefault = Locale.getDefault();
        Locale.setDefault(Locale.UK);

        CurrencyValidator validator = new CurrencyValidator();
        BigDecimal expected = new BigDecimal("1234.00");
        BigDecimal negative = new BigDecimal("-1234.00");

        assertEquals("Default locale", expected, validator.validate(UK_POUND +"1,234"));

        assertEquals("UK locale",      expected, validator.validate(UK_POUND + "1,234",   Locale.UK));
        assertEquals("UK negative",    negative, validator.validate("-" + UK_POUND + "1,234",  Locale.UK));

        assertEquals("US locale",      expected, validator.validate(US_DOLLAR + "1,234",   Locale.US));
        assertEquals("US negative",    negative, validator.validate("(" + US_DOLLAR + "1,234)", Locale.US));

        // Restore the original default
        Locale.setDefault(origDefault);
    }

    /**
     * Test Invalid integer (non decimal) currency values
     */
    public void testIntegerInvalid() {
        CurrencyValidator validator = new CurrencyValidator(true, false);

        // Invalid UK - has decimals
        assertFalse("UK positive",    validator.isValid(UK_POUND + "1,234.56",   Locale.UK));
        assertFalse("UK negative",    validator.isValid("-" + UK_POUND + "1,234.56", Locale.UK));

        // Invalid US - has decimals
        assertFalse("US positive",    validator.isValid(US_DOLLAR + "1,234.56",   Locale.US));
        assertFalse("US negative",    validator.isValid("(" + US_DOLLAR + "1,234.56)",  Locale.US));
    }


    /**
     * Test currency values with a pattern
     */
    public void testPattern() {
        // Set the default Locale
        Locale origDefault = Locale.getDefault();
        Locale.setDefault(Locale.UK);

        BigDecimalValidator validator = CurrencyValidator.getInstance();
        String basicPattern = CURRENCY_SYMBOL + "#,##0.000";
        String pattern = basicPattern + ";[" + basicPattern +"]";
        BigDecimal expected   = new BigDecimal("1234.567");
        BigDecimal negative   = new BigDecimal("-1234.567");

        // Test Pattern
        assertEquals("default",        expected,   validator.validate(UK_POUND + "1,234.567", pattern));
        assertEquals("negative",       negative,   validator.validate("[" + UK_POUND + "1,234.567]", pattern));
        assertEquals("no symbol +ve",  expected,   validator.validate("1,234.567",    pattern));
        assertEquals("no symbol -ve",  negative,   validator.validate("[1,234.567]",  pattern));

        // Test Pattern & Locale
        assertEquals("default",        expected,   validator.validate(US_DOLLAR + "1,234.567", pattern, Locale.US));
        assertEquals("negative",       negative,   validator.validate("[" + US_DOLLAR + "1,234.567]", pattern, Locale.US));
        assertEquals("no symbol +ve",  expected,   validator.validate("1,234.567",    pattern, Locale.US));
        assertEquals("no symbol -ve",  negative,   validator.validate("[1,234.567]",  pattern, Locale.US));

        // invalid
        assertFalse("invalid symbol",  validator.isValid(US_DOLLAR + "1,234.567", pattern));
        assertFalse("invalid symbol",  validator.isValid(UK_POUND  + "1,234.567", pattern, Locale.US));

        // Restore the original default
        Locale.setDefault(origDefault);
    }
}
