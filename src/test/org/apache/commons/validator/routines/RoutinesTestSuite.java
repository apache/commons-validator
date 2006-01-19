/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright 2006 The Apache Software Foundation
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

package org.apache.commons.validator.routines;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test suite for <code>org.apache.commons.validator.routines</code>
 * package.
 */
public class RoutinesTestSuite extends TestCase {

    public RoutinesTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
       TestSuite suite = new TestSuite();

       suite.addTestSuite(BigDecimalValidatorTest.class);
       suite.addTestSuite(BigIntegerValidatorTest.class);
       suite.addTestSuite(ByteValidatorTest.class);
       suite.addTestSuite(CalendarValidatorTest.class);
       suite.addTestSuite(CurrencyValidatorTest.class);
       suite.addTestSuite(DateValidatorTest.class);
       suite.addTestSuite(DoubleValidatorTest.class);
       suite.addTestSuite(FloatValidatorTest.class);
       suite.addTestSuite(IntegerValidatorTest.class);
       suite.addTestSuite(LongValidatorTest.class);
       suite.addTestSuite(PercentValidatorTest.class);
       suite.addTestSuite(ShortValidatorTest.class);
       suite.addTestSuite(TimeValidatorTest.class);

       return suite;
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

}
