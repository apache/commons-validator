/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/TestCommon.java,v 1.7 2004/11/26 21:35:19 dgraham Exp $
 * $Revision: 1.7 $
 * $Date: 2004/11/26 21:35:19 $
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

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

/**
 * Consolidates reading in XML config file into parent class.
 */
abstract public class TestCommon extends TestCase {
    
    /**
     * Resources used for validation tests.
     */
    protected ValidatorResources resources = null;

    public TestCommon(String string) {
        super(string);
    }

    /**
     * Load <code>ValidatorResources</code> from
     * validator-numeric.xml.
     */
    protected void loadResources(String file) throws IOException, SAXException {
        // Load resources
        InputStream in = null;

        try {
            in = this.getClass().getResourceAsStream(file);
            resources = new ValidatorResources(in);
        } finally {
            if (in != null) {
				in.close();
			}
        }
    }
}
