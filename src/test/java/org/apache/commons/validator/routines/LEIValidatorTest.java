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
package org.apache.commons.validator.routines;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LEIValidatorTest {

    private final String[] validLEIFormat = new String[] {
        "54930084UKLVMY22DS16", // G.E. Financing GmbH
        "213800WSGIIZCXF1P572", // Jaguar Land Rover Ltd
        "M07J9MTYHFCSVRBV2631", // RWE AG (old Style, before 2012.Nov.30)
        "529900CLVK38HUKPKF71", // SWKBank
    };

    private final String[] invalidLEIFormat = new String[] {
        "M07J9MTYHFCSVRBV2600", // invalid check digit
        "529900CLVK38HUKPKF77", // invalid check digit
    };

    private static final LEIValidator VALIDATOR = LEIValidator.getInstance();

    @Test
    public void testValid() {
        for(String f : validLEIFormat) {
            assertTrue(f, VALIDATOR.isValid(f));
        }
    }

    @Test
    public void testInValid() {
        for(String f : invalidLEIFormat) {
            assertFalse(f, VALIDATOR.isValid(f));
        }
    }

}
