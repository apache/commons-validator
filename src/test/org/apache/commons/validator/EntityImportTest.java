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
import junit.framework.TestSuite;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.util.Locale;
import java.net.URL;


/**                                                       
 * Tests entity imports.
 *
 * @version $Revision$ $Date$
 */
public class EntityImportTest extends TestCommon {

    public EntityImportTest(String name) {
        super(name);
    }

    /**
     * Start the tests.
     *
     * @param theArgs the arguments. Not used
     */
    public static void main(String[] theArgs) {
        junit.awtui.TestRunner.main(new String[]{EntityImportTest.class.getName()});
    }

    /**
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite() {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(EntityImportTest.class);
    }

    /**
     * Tests the entity import loading the <code>byteForm</code> form.
     */
    public void testEntityImport() throws Exception {
        URL url = getClass().getResource("EntityImportTest-config.xml");
        ValidatorResources resources = new ValidatorResources(url.toExternalForm());
        assertNotNull("Form should be found", resources.getForm(Locale.getDefault(), "byteForm"));
    }  

    /**
     * Tests loading ValidatorResources from a URL
     */
    public void testParseURL() throws Exception {
        URL url = getClass().getResource("EntityImportTest-config.xml");
        ValidatorResources resources = new ValidatorResources(url);
        assertNotNull("Form should be found", resources.getForm(Locale.getDefault(), "byteForm"));
    }
}
