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


/**
 * Performs Validation Test for <code>double</code> validations.
 *
 * @version $Revision$
 */
public class DoubleTest extends AbstractNumberTest {

    public DoubleTest(String name) {
        super(name);
        ACTION = "double";
        FORM_KEY = "doubleForm";
    }


    /**
     * Tests the double validation.
     */
    public void testDouble() throws ValidatorException {
        // Create bean to run test on.
        ValueBean info = new ValueBean();
        info.setValue("0");

        valueTest(info, true);
    }

    /**
     * Tests the double validation.
     */
    public void testDoubleMin() throws ValidatorException {
        // Create bean to run test on.
        ValueBean info = new ValueBean();
        info.setValue(Double.toString(Double.MIN_VALUE));

        valueTest(info, true);
    }

    /**
     * Tests the double validation.
     */
    public void testDoubleMax() throws ValidatorException {
        // Create bean to run test on.
        ValueBean info = new ValueBean();
        info.setValue(Double.toString(Double.MAX_VALUE));

        valueTest(info, true);
    }

    /**
     * Tests the double validation failure.
     */
    public void testDoubleFailure() throws ValidatorException {
        // Create bean to run test on.
        ValueBean info = new ValueBean();

        valueTest(info, false);
    }

}