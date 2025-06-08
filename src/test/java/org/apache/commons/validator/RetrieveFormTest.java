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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 * Tests retrieving forms using different Locales.
 */
class RetrieveFormTest {

    /**
     * Prefix for the forms.
     */
    private static final String FORM_PREFIX = "testForm_";

    /**
     * Prefix for the forms.
     */
    private static final Locale CANADA_FRENCH_XXX = new Locale("fr", "CA", "XXX");

    /**
     * Resources used for validation tests.
     */
    private ValidatorResources resources;

    private void checkForm(final Locale locale, final String formKey, final String expectedVarValue) {

        // Retrieve the Form
        final Form testForm = resources.getForm(locale, formKey);
        assertNotNull(testForm, "Form '" + formKey + "' null for locale " + locale);

        // Validate the expected Form is retrieved by checking the "localeVar"
        // value of the field.
        final Field testField = testForm.getField("testProperty");
        assertEquals(expectedVarValue, testField.getVarValue("localeVar"), "Incorrect Form '" + formKey + "' for locale '" + locale + "'");
    }

    private void checkFormNotFound(final Locale locale, final String formKey) {

        // Retrieve the Form
        final Form testForm = resources.getForm(locale, formKey);
        assertNull(testForm, "Form '" + formKey + "' not null for locale " + locale);

    }

    /**
     * Load {@code ValidatorResources} from multiple xml files.
     */
    @BeforeEach
    protected void setUp() throws IOException, SAXException {
        final InputStream[] streams = { this.getClass().getResourceAsStream("RetrieveFormTest-config.xml") };

        resources = new ValidatorResources(streams);

        for (final InputStream stream : streams) {
            stream.close();
        }
    }

    /**
     * Test a form defined only in the "default" formset.
     */
    @Test
    void testDefaultForm() {

        final String formKey = FORM_PREFIX + "default";

        // *** US locale ***
        checkForm(Locale.US, formKey, "default");

        // *** French locale ***
        checkForm(Locale.FRENCH, formKey, "default");

        // *** France locale ***
        checkForm(Locale.FRANCE, formKey, "default");

        // *** Canadian (English) locale ***
        checkForm(Locale.CANADA, formKey, "default");

        // *** Canadian French locale ***
        checkForm(Locale.CANADA_FRENCH, formKey, "default");

        // *** Canadian French Variant locale ***
        checkForm(CANADA_FRENCH_XXX, formKey, "default");

    }

    /**
     * Test a form not defined
     */
    @Test
    void testFormNotFound() {

        final String formKey = "INVALID_NAME";

        // *** US locale ***
        checkFormNotFound(Locale.US, formKey);

        // *** French locale ***
        checkFormNotFound(Locale.FRENCH, formKey);

        // *** France locale ***
        checkFormNotFound(Locale.FRANCE, formKey);

        // *** Canadian (English) locale ***
        checkFormNotFound(Locale.CANADA, formKey);

        // *** Canadian French locale ***
        checkFormNotFound(Locale.CANADA_FRENCH, formKey);

        // *** Canadian French Variant locale ***
        checkFormNotFound(CANADA_FRENCH_XXX, formKey);

    }

    /**
     * Test a form defined in the "default" formset, formsets where just the "language" is specified and formset where the language and country are specified.
     */
    @Test
    void testLanguageCountryForm() {

        final String formKey = FORM_PREFIX + "language_country";

        // *** US locale ***
        checkForm(Locale.US, formKey, "default");

        // *** French locale ***
        checkForm(Locale.FRENCH, formKey, "fr");

        // *** France locale ***
        checkForm(Locale.FRANCE, formKey, "fr_FR");

        // *** Canadian (English) locale ***
        checkForm(Locale.CANADA, formKey, "default");

        // *** Canadian French locale ***
        checkForm(Locale.CANADA_FRENCH, formKey, "fr_CA");

        // *** Canadian French Variant locale ***
        checkForm(CANADA_FRENCH_XXX, formKey, "fr_CA");

    }

    /**
     * Test a form defined in all the formsets
     */
    @Test
    void testLanguageCountryVariantForm() {

        final String formKey = FORM_PREFIX + "language_country_variant";

        // *** US locale ***
        checkForm(Locale.US, formKey, "default");

        // *** French locale ***
        checkForm(Locale.FRENCH, formKey, "fr");

        // *** France locale ***
        checkForm(Locale.FRANCE, formKey, "fr_FR");

        // *** Canadian (English) locale ***
        checkForm(Locale.CANADA, formKey, "default");

        // *** Canadian French locale ***
        checkForm(Locale.CANADA_FRENCH, formKey, "fr_CA");

        // *** Canadian French Variant locale ***
        checkForm(CANADA_FRENCH_XXX, formKey, "fr_CA_XXX");

    }

    /**
     * Test a form defined in the "default" formset and formsets where just the "language" is specified.
     */
    @Test
    void testLanguageForm() {

        final String formKey = FORM_PREFIX + "language";

        // *** US locale ***
        checkForm(Locale.US, formKey, "default");

        // *** French locale ***
        checkForm(Locale.FRENCH, formKey, "fr");

        // *** France locale ***
        checkForm(Locale.FRANCE, formKey, "fr");

        // *** Canadian (English) locale ***
        checkForm(Locale.CANADA, formKey, "default");

        // *** Canadian French locale ***
        checkForm(Locale.CANADA_FRENCH, formKey, "fr");

        // *** Canadian French Variant locale ***
        checkForm(CANADA_FRENCH_XXX, formKey, "fr");

    }

}
