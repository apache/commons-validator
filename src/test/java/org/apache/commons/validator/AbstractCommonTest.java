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

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

/**
 * Consolidates reading in XML config file into parent class.
 */
abstract public class AbstractCommonTest {

    /**
     * Resources used for validation tests.
     */
    protected ValidatorResources resources;
    protected String name;

    /**
     * Load <code>ValidatorResources</code> from validator-numeric.xml.
     */
    protected void loadResources(final String file) throws IOException, SAXException {
        // Load resources
        try (InputStream in = this.getClass().getResourceAsStream(file)) {
            resources = new ValidatorResources(in);
        }
    }
}
