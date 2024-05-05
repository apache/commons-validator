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

import org.junit.jupiter.api.Test;

/**
 * Performs Validation Test for <code>short</code> validations.
 */
public class ShortTest extends AbstractNumberTest {

    public ShortTest() {
        formKey = "shortForm";
        action = "short";
    }

    /**
     * Tests the short validation failure.
     */
    @Test
    public void testShortBeyondMax() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();
        info.setValue(Short.MAX_VALUE + "1");

        valueTest(info, false);
    }

    /**
     * Tests the short validation failure.
     */
    @Test
    public void testShortBeyondMin() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();
        info.setValue(Short.MIN_VALUE + "1");

        valueTest(info, false);
    }

    /**
     * Tests the short validation.
     */
    @Test
    public void testShortMax() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();
        info.setValue(Short.toString(Short.MAX_VALUE));

        valueTest(info, true);
    }

    /**
     * Tests the short validation.
     */
    @Test
    public void testShortMin() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();
        info.setValue(Short.toString(Short.MIN_VALUE));

        valueTest(info, true);
    }
}