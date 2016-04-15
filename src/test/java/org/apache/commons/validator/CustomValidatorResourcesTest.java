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

import junit.framework.TestCase;

/**
 * Test custom ValidatorResources.
 *
 * @version $Revision$
 */
public class CustomValidatorResourcesTest extends TestCase {
    
    /**
     * Construct a test case with the specified name.
     * @param name Name of the test
     */
    public CustomValidatorResourcesTest(String name) {
        super(name);
    }

    /**
     * Set up.
     */
    @Override
    protected void setUp() {
    }

    /**
     * Tear Down
     */
    @Override
    protected void tearDown() {
    }

    /**
     * Test creating a custom validator resources.
     */
    public void testCustomResources() {
        // Load resources
        InputStream in = null;
        try {
            in = this.getClass().getResourceAsStream("TestNumber-config.xml");
        } catch(Exception e) {
            fail("Error loading resources: " + e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch(Exception e) {
            }
        }
    }

}
