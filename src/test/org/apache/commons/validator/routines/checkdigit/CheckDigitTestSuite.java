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
package org.apache.commons.validator.routines.checkdigit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test suite for <code>org.apache.commons.validator.routines.checkdigit</code>
 * package.
 *
 * @version $Revision$
 * @since Validator 1.4
 */
public class CheckDigitTestSuite extends TestCase {

    /** 
     * Construct an instance with the specified name
     * @param name name of the test
     */
    public CheckDigitTestSuite(String name) {
        super(name);
    }

    /** 
     * Create a Test Suite
     * @return the test suite.
     */
    public static Test suite() {
       TestSuite suite = new TestSuite();

       suite.addTestSuite(EAN13CheckDigitTest.class);
       suite.addTestSuite(ISBN10CheckDigitTest.class);
       suite.addTestSuite(ISBNCheckDigitTest.class);
       suite.addTestSuite(LuhnCheckDigitTest.class);

       return suite;
    }

    /** 
     * Static main.
     * @param args arguments
     */
    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

}
