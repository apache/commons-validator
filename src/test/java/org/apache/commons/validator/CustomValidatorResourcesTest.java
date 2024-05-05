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

import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test custom ValidatorResources.
 */
public class CustomValidatorResourcesTest {

    /**
     * Sets up.
     */
    @BeforeEach
    protected void setUp() {
    }

    /**
     * Tear Down
     */
    @AfterEach
    protected void tearDown() {
    }

    /**
     * Test creating a custom validator resources.
     */
    @Test
    public void testCustomResources() {
        // Load resources
        InputStream in = null;
        try {
            in = this.getClass().getResourceAsStream("TestNumber-config.xml");
        } catch (final Exception e) {
            fail("Error loading resources: " + e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final Exception ignore) {
                // ignore
            }
        }
    }

}
