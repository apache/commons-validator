/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/FloatTest.java,v 1.14 2004/02/21 17:10:30 rleland Exp $
 * $Revision: 1.14 $
 * $Date: 2004/02/21 17:10:30 $
 *
 * ====================================================================
 * Copyright 2001-2004 The Apache Software Foundation
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


package org.apache.commons.validator;

import junit.framework.Test;
import junit.framework.TestSuite;


/**                                                       
 * Performs Validation Test for <code>float</code> validations.
 */
public class FloatTest extends TestNumber {

    public FloatTest(String name) {
        super(name);
        ACTION = "float";
        FORM_KEY = "floatForm";
    }

    /**
     * Start the tests.
     *
     * @param theArgs the arguments. Not used
     */
    public static void main(String[] theArgs) {
        junit.awtui.TestRunner.main(new String[]{FloatTest.class.getName()});
    }

    /**
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite() {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(FloatTest.class);
    }


    /**
     * Tests the float validation.
     */
    public void testFloat() throws ValidatorException {
        // Create bean to run test on.
        ValueBean info = new ValueBean();
        info.setValue("0");

        valueTest(info, true);
    }

    /**
     * Tests the float validation.
     */
    public void testFloatMin() throws ValidatorException {
        // Create bean to run test on.
        ValueBean info = new ValueBean();
        info.setValue(new Float(Float.MIN_VALUE).toString());

        valueTest(info, true);
    }

    /**
     * Tests the float validation.
     */
    public void testFloatMax() throws ValidatorException {
        // Create bean to run test on.
        ValueBean info = new ValueBean();
        info.setValue(new Float(Float.MAX_VALUE).toString());

        valueTest(info, true);
    }

    /**
     * Tests the float validation failure.
     */
    public void testFloatFailure() throws ValidatorException {
        // Create bean to run test on.
        ValueBean info = new ValueBean();

        valueTest(info, false);
    }

}                                                         