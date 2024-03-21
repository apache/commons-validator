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

public class LeitwegValidatorTest {

    private final String[] validLeitwegFormat = new String[] {
        "992-90009-96",           // Deutsche Bahn AG
        "051700052052-31001-35",  // Xanten, Stadt
        "053340002002-33004-23",  // Aachener Parkhaus GmbH
        "05711-06001-79",         // UNI BI Lkr:Bielefeld, Stadt
        "05913-99001-25",         // Kassenärztliche Vereinigung Westfalen-Lippe, 44141 Dortmund, x-rechnung@kvwl.de
        "11-2000001000-30",       // ITDZ, Berlin
    };

    private final String[] invalidLeitwegFormat = new String[] {
        "9992",                   // "99-92" without -
        "00-290009-64",           // invalid region "00"
    };

    private static final LeitwegValidator VALIDATOR = LeitwegValidator.getInstance();

    @Test
    public void testValid() {
        for(String f : validLeitwegFormat) {
            assertTrue(f, VALIDATOR.isValid(f));
        }
    }

    @Test
    public void testInValid() {
        for(String f : invalidLeitwegFormat) {
            assertFalse(f, VALIDATOR.isValid(f));
        }
    }

}
