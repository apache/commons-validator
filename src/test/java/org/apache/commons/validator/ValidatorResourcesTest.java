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

import java.io.InputStream;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * Test ValidatorResources.
 *
 * @version $Revision$
 */
public class ValidatorResourcesTest extends TestCase {

    /**
     * Constructor.
     */
    public ValidatorResourcesTest(String name) {
        super(name);
    }

    /**
     * Start the tests.
     *
     * @param theArgs the arguments. Not used
     */
    public static void main(String[] theArgs) {
        junit.awtui.TestRunner.main(new String[] {ValidatorResourcesTest.class.getName()});
    }

    /**
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite() {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(ValidatorResourcesTest.class);
    }

    /**
     * Load <code>ValidatorResources</code> from
     * ValidatorResultsTest-config.xml.
     */
    protected void setUp() throws Exception {
    }

    protected void tearDown() {
    }

    /**
     * Test null Input Stream for Validator Resources.
     */
    public void testNullInputStream() throws Exception {

        try {
            new ValidatorResources((InputStream)null);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected result
            // System.out.println("Exception: " + e);
        }

    }

}
