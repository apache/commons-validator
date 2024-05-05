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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 * Performs Validation Test for exception handling.
 */
public class ExceptionTest extends AbstractCommonTest {

    /**
     * The key used to retrieve the set of validation rules from the xml file.
     */
    protected static final String FORM_KEY = "exceptionForm";

    /**
     * The key used to retrieve the validator action.
     */
    protected static final String ACTION = "raiseException";

    /**
     * Load <code>ValidatorResources</code> from validator-exception.xml.
     */
    @BeforeEach
    protected void setUp() throws IOException, SAXException {
        loadResources("ExceptionTest-config.xml");
    }

    /**
     * Tests handling of checked exceptions - should become ValidatorExceptions.
     *
     * N.B. This test has been removed (renamed) as it currently serves no purpose. If/When exception handling is changed in Validator 2.0 it can be
     * reconsidered then.
     */
    @Ignore
    public void testCheckedException() {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();
        info.setValue("CHECKED");

        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, info);

        // Get results of the validation which can throw ValidatorException

        // Tests Validator 1.x exception handling
        try {
            validator.validate();
        } catch (final ValidatorException expected) {
            fail("Checked exceptions are not wrapped in ValidatorException in Validator 1.x.");
        } catch (final Exception e) {
            assertTrue("CHECKED-EXCEPTION".equals(e.getMessage()));
        }

        // This will be true in Validator 2.0
//        try {
//            validator.validate();
//            fail("ValidatorException should occur here!");
//        } catch (ValidatorException expected) {
//            assertTrue("CHECKED-EXCEPTION".equals(expected.getMessage()));
//        }
    }

    /**
     * Tests handling of runtime exceptions.
     *
     * N.B. This test has been removed (renamed) as it currently serves no purpose. If/When exception handling is changed in Validator 2.0 it can be
     * reconsidered then.
     */
    @Ignore
    public void testRuntimeException() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();
        info.setValue("RUNTIME");

        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, info);

        // Get results of the validation which can throw ValidatorException
        try {
            validator.validate();
            // fail("RuntimeException should occur here!");
        } catch (final RuntimeException expected) {
            fail("RuntimeExceptions should be treated as validation failures in Validator 1.x.");
            // This will be true in Validator 2.0
            // assertTrue("RUNTIME-EXCEPTION".equals(expected.getMessage()));
        }
    }

    /**
     * Tests handling of checked exceptions - should become ValidatorExceptions.
     */
    @Test
    public void testValidatorException() {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();
        info.setValue("VALIDATOR");

        // Construct validator based on the loaded resources
        // and the form key
        final Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, info);

        // Get results of the validation which can throw ValidatorException
        try {
            validator.validate();
            fail("ValidatorException should occur here!");
        } catch (final ValidatorException expected) {
            assertTrue("VALIDATOR-EXCEPTION".equals(expected.getMessage()));
        }
    }
}
