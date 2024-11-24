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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 * This TestCase is a confirmation of the parameter of the validator's method.
 */
public class ParameterTest extends AbstractCommonTest {

    private static final String FORM_KEY = "nameForm";

    private String firstName;

    private String middleName;

    private String lastName;

    private void assertParameterValue(final Validator validator, final String name, final Class<?> type) {
        final Object value = validator.getParameterValue(name);
        assertNotNull(value, "Expected '" + type.getName() + "' but was null");
        assertTrue(type.isInstance(value), "Expected '" + type.getName() + "' but was '" + value.getClass().getName() + "'");
    }

    /**
     * Create a NameBean.
     */
    private NameBean createNameBean() {
        final NameBean name = new NameBean();
        name.setFirstName(firstName);
        name.setMiddleName(middleName);
        name.setLastName(lastName);
        return name;
    }

    /**
     * Load {@code ValidatorResources} from ValidatorResultsTest-config.xml.
     */
    @BeforeEach
    protected void setUp() throws IOException, SAXException {
        // Load resources
        loadResources("ParameterTest-config.xml");

        // initialize values
        firstName = "foo";
        middleName = "123";
        lastName = "456";

    }

    @AfterEach
    protected void tearDown() {
    }

    /**
     * Test all validations ran and passed.
     */
    @Test
    public void testAllValid() {

        // Create bean to run test on.
        final NameBean bean = createNameBean();

        final Validator validator = new Validator(resources, FORM_KEY);

        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, bean);
        validator.setParameter(Validator.LOCALE_PARAM, Locale.getDefault());

        // Get results of the validation.
        try {
            validator.validate();
        } catch (final Exception e) {
            fail("Validator.validate() threw " + e);
        }
        assertParameterValue(validator, Validator.BEAN_PARAM, Object.class);
        assertParameterValue(validator, Validator.FIELD_PARAM, Field.class);
        assertParameterValue(validator, Validator.FORM_PARAM, Form.class);
        assertParameterValue(validator, Validator.LOCALE_PARAM, Locale.class);
        assertParameterValue(validator, Validator.VALIDATOR_ACTION_PARAM, ValidatorAction.class);
        assertParameterValue(validator, Validator.VALIDATOR_PARAM, Validator.class);
        assertParameterValue(validator, Validator.VALIDATOR_RESULTS_PARAM, ValidatorResults.class);
    }
}
