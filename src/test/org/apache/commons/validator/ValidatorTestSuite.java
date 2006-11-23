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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.validator.util.FlagsTest;

/**
 * Test suite for <code>org.apache.commons.validator</code>
 * package.
 *
 * @version $Revision$ $Date$
 */
public class ValidatorTestSuite extends TestCase {

    public ValidatorTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
       TestSuite suite = new TestSuite();

       suite.addTestSuite(ExceptionTest.class);

       suite.addTest(ByteTest.suite());
       suite.addTestSuite(CreditCardValidatorTest.class);
       suite.addTest(CustomValidatorResourcesTest.suite());
       suite.addTest(DateTest.suite());
       suite.addTest(DoubleTest.suite());
       suite.addTest(EmailTest.suite());
       suite.addTest(EntityImportTest.suite());
       suite.addTest(ExtensionTest.suite());        
       suite.addTestSuite(FlagsTest.class);
       suite.addTestSuite(FieldTest.class);
       suite.addTest(FloatTest.suite());
       suite.addTestSuite(GenericValidatorTest.class);
       suite.addTest(IntegerTest.suite());
       suite.addTestSuite(ISBNValidatorTest.class);
       suite.addTest(LocaleTest.suite());
       suite.addTest(LongTest.suite());
       suite.addTestSuite(MultipleConfigFilesTest.class);
       suite.addTest(MultipleTests.suite());
       suite.addTest(ParameterTest.suite());
       suite.addTest(RequiredIfTest.suite());
       suite.addTest(RequiredNameTest.suite());
       suite.addTest(RetrieveFormTest.suite());
       suite.addTest(ShortTest.suite());
       suite.addTest(TypeTest.suite());
       suite.addTest(UrlTest.suite());
       suite.addTest(ValidatorResultsTest.suite());
       suite.addTest(ValidatorTest.suite());
       suite.addTest(VarTest.suite());

       return suite;
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

}
