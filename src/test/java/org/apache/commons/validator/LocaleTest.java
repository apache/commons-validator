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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 * Performs Validation Test for locale validations.
 */
class LocaleTest extends AbstractCommonTest {

    /**
     * The key used to retrieve the set of validation rules from the xml file.
     */
    protected static final String FORM_KEY = "nameForm";

    /** The key used to retrieve the validator action. */
    protected static final String ACTION = "required";

    /**
     * Load {@code ValidatorResources} from validator-locale.xml.
     *
     * @throws IOException  If something goes wrong
     * @throws SAXException If something goes wrong
     */
    @BeforeEach
    protected void setUp() throws IOException, SAXException {
        // Load resources
        loadResources("LocaleTest-config.xml");
    }

    /** The teardown method for JUnit */
    @AfterEach
    protected void tearDown() {
    }

    /**
     * See what happens when we try to validate with a Locale, Country and variant. Also check if the added locale validation field is getting used.
     *
     * @throws ValidatorException If something goes wrong
     */
    @Test
    void testLocale1() throws ValidatorException {
        // Create bean to run test on.
        final NameBean name = new NameBean();
        name.setFirstName("");
        name.setLastName("");

        valueTest(name, new Locale("en", "US", "TEST1"), false, false, false);
    }

    /**
     * See what happens when we try to validate with a Locale, Country and variant
     *
     * @throws ValidatorException If something goes wrong
     */
    @Test
    void testLocale2() throws ValidatorException {
        // Create bean to run test on.
        final NameBean name = new NameBean();
        name.setFirstName("");
        name.setLastName("");

        valueTest(name, new Locale("en", "US", "TEST2"), true, false, true);
    }

    /**
     * See what happens when we try to validate with a Locale, Country and variant
     *
     * @throws ValidatorException If something goes wrong
     */
    @Test
    void testLocale3() throws ValidatorException {
        // Create bean to run test on.
        final NameBean name = new NameBean();
        name.setFirstName("");
        name.setLastName("");

        valueTest(name, new Locale("en", "UK"), false, true, true);
    }

    /**
     * See if a locale of en_UK_TEST falls back to en_UK instead of default form set. Bug #16920 states that this isn't happening, even though it is passing
     * this test. see #16920.
     *
     * @throws ValidatorException If something goes wrong
     */
    @Test
    void testLocale4() throws ValidatorException {
        // Create bean to run test on.
        final NameBean name = new NameBean();
        name.setFirstName("");
        name.setLastName("");

        valueTest(name, new Locale("en", "UK", "TEST"), false, true, true);
    }

    /**
     * See if a locale of language=en falls back to default form set.
     *
     * @throws ValidatorException If something goes wrong
     */
    @Test
    void testLocale5() throws ValidatorException {
        // Create bean to run test on.
        final NameBean name = new NameBean();
        name.setFirstName("");
        name.setLastName("");

        valueTest(name, new Locale("en", ""), false, false, true);
    }

    /**
     * Utility class to run a test on a value.
     *
     * @param name       param
     * @param loc        param
     * @param firstGood  param
     * @param lastGood   param
     * @param middleGood param
     * @throws ValidatorException If something goes wrong
     */
    private void valueTest(final Object name, final Locale loc, final boolean firstGood, final boolean lastGood, final boolean middleGood)
            throws ValidatorException {

        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, name);
        validator.setParameter(Validator.LOCALE_PARAM, loc);
        // Get results of the validation.
        // throws ValidatorException,
        // but we aren't catching for testing
        // since no validation methods we use
        // throw this
        final ValidatorResults results = validator.validate();

        assertNotNull(results, "Results are null.");

        final ValidatorResult resultlast = results.getValidatorResult("lastName");
        final ValidatorResult resultfirst = results.getValidatorResult("firstName");
        final ValidatorResult resultmiddle = results.getValidatorResult("middleName");

        if (firstGood) {
            assertNull(resultfirst);
        } else {
            assertNotNull(resultfirst);
        }

        if (middleGood) {
            assertNull(resultmiddle);
        } else {
            assertNotNull(resultmiddle);
        }

        if (lastGood) {
            assertNull(resultlast);
        } else {
            assertNotNull(resultlast);
        }
    }
}
