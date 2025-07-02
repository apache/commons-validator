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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class SireneValidatorTest {

    private final List<String> validFormat = Arrays.asList(new String[] {
        "123456782", // SIREN
        "572141885", // SIREN
        "502090897", // SIREN flexitec.fr
        "910167758", "893560474", // SIREN overseas department Reunion
        "82425921200028 ", "82882805300029", // SIRET overseas department Reunion
        "57214188502180", // SIRET
        "50810215900334", // SIRET
        "40483304800022", // valid but not longer active in SIRENE catalogue
    });

    private final List<String> invalidFormat = Arrays.asList(new String[] {
        "12345678",        // invalid length, to short
        "1234567820",      // invalid length, to long for SIREN, to short for SIRET
        "572141885021810", // invalid length, to long
        "123456780",       // invalid check digit "0" should be "2"
        "57214188502181",  // invalid check digit "1" should be "0"
        "50810215000333",  // SIRET with invalid SIREN check digit "0" at pos 9
    });

    private static final SireneValidator VALIDATOR = SireneValidator.getInstance();

    @Test
    public void testValid() {
        validFormat.forEach(e -> {
            assertTrue(VALIDATOR.isValid(e), e);
        });
    }

    @Test
    public void testInValid() {
        invalidFormat.forEach(e -> {
            assertFalse(VALIDATOR.isValid(e), e);
        });
    }

}
