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

import org.junit.jupiter.api.Test;

/**
 * Performs Validation Test for {@code float} validations.
 */
class FloatTest extends AbstractNumberTest {

    FloatTest() {
        action = "float";
        formKey = "floatForm";
    }

    /**
     * Tests the float validation.
     */
    @Test
    void testFloat() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();
        info.setValue("0");

        valueTest(info, true);
    }

    /**
     * Tests the float validation failure.
     */
    @Test
    void testFloatFailure() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();

        valueTest(info, false);
    }

    /**
     * Tests the float validation.
     */
    @Test
    void testFloatMax() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();
        info.setValue(Float.toString(Float.MAX_VALUE));

        valueTest(info, true);
    }

    /**
     * Tests the float validation.
     */
    @Test
    void testFloatMin() throws ValidatorException {
        // Create bean to run test on.
        final ValueBean info = new ValueBean();
        info.setValue(Float.toString(Float.MIN_VALUE));

        valueTest(info, true);
    }

}