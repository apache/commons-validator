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

import org.apache.commons.validator.routines.UUIDValidator.CaseSensitivity;
import org.apache.commons.validator.routines.UUIDValidator.ConversionMode;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UUIDValidatorTest {

    private static final String NIL_UUID = "00000000-0000-0000-0000-000000000000";

    private UUIDValidator uuidValidator = UUIDValidator.getInstance();

    private String input;

    private String converted;

    private boolean valid;

    @Test
    public void checkJavaApiUuidCompatibility() {

        for (int i = 0; i < 1000; i++) {

            input = UUID.randomUUID().toString();

            whenChecksValidity();

            thenIsValid();

        }

    }

    @Test
    public void checksLength() {

        input = "3ff0129c-47f4-445f-865b-cd34d3f478ca12345678";

        whenChecksValidity();

        thenIsInvalid();

    }

    @Test
    public void checksGroupLengths() {

        input = "3ff0129-c47f4-445f-865b-cd34d3f478ca";

        whenChecksValidity();

        thenIsInvalid();

    }

    @Test
    public void allowsNilsPerDefault() {

        input = NIL_UUID;

        whenChecksValidity();

        thenIsValid();

    }

    @Test
    public void doesNotAllowNilIfDeactivated() {

        input = NIL_UUID;
        uuidValidator = new UUIDValidator(true, false, true, true, CaseSensitivity.INSENSITIVE, ConversionMode.LEAVE_AS_IS);

        whenChecksValidity();

        thenIsInvalid();

    }

    @Test
    public void checksVersion() {

        input = "3ff0129c-47f4-f45f-865b-cd34d3f478ca";

        whenChecksValidity();

        thenIsInvalid();

    }

    @Test
    public void checksVariant() {

        input = "3ff0129c-47f4-445f-f65b-cd34d3f478ca";

        whenChecksValidity();

        thenIsInvalid();

    }

    @Test
    public void acceptsUuidVersion1() {

        input = "7961d332-6f94-11ec-90d6-0242ac120003";

        whenChecksValidity();

        thenIsValid();

    }

    @Test
    public void acceptsUuidVersion3() {

        input = "d3e69c9c-b9f0-3d28-8684-2e45f9743218";

        whenChecksValidity();

        thenIsValid();

    }

    @Test
    public void acceptsUuidVersion4() {

        input = "4c2c85ee-521a-43bb-a341-7e7a145a46d2";

        whenChecksValidity();

        thenIsValid();

    }

    @Test
    public void acceptsGuid() {

        input = "3215ab45-c891-499a-9699-7034e9b59518";

        whenChecksValidity();

        thenIsValid();

    }

    @Test
    public void nullInputIsInvalid() {

        input = null;

        whenChecksValidity();

        thenIsInvalid();

    }

    @Test
    public void inputMustHaveAtLeast36Characters() {

        input = "b1184db4-c107-49f9-8e60-132d35fba69";

        whenChecksValidity();

        thenIsInvalid();

    }

    @Test
    public void inputMustConsistOnlyOfHexCharacters() {

        input = "75873784-5ebb-4a00-a17f-aa497268d7bx";

        whenChecksValidity();

        thenIsInvalid();

    }

    @Test
    public void ignoresCasePerDefault() {

        input = "36AF552A-F73F-444B-b5f3-f4ce8e6cbbdd";

        whenChecksValidity();

        thenIsValid();

    }

    @Test
    public void validIfOnlyLowerCaseButContainsUpperCase() {

        input = "36af552a-f73f-444b-b5f3-f4ce8e6cbbdd";
        uuidValidator = new UUIDValidator(false, true, false, false, CaseSensitivity.LOWER_CASE_ONLY, ConversionMode.LEAVE_AS_IS);

        whenChecksValidity();

        thenIsValid();

    }

    @Test
    public void invalidIfOnlyLowerCaseButContainsUpperCase() {

        input = "36af552a-f73f-444b-b5f3-f4ce8e6cbbdD";
        uuidValidator = new UUIDValidator(false, true, false, false, CaseSensitivity.LOWER_CASE_ONLY, ConversionMode.LEAVE_AS_IS);

        whenChecksValidity();

        thenIsInvalid();

    }

    @Test
    public void validIfOnlyUpperCase() {

        input = "79AA5A62-DB94-4856-A51C-2B9DA0DA74F2";
        uuidValidator = new UUIDValidator(false, true, false, false, CaseSensitivity.UPPER_CASE_ONLY, ConversionMode.LEAVE_AS_IS);

        whenChecksValidity();

        thenIsValid();

    }

    @Test
    public void invalidIfOnlyUpperCaseButContainsLowerCase() {

        input = "79AA5A62-DB94-4856-A51C-2B9DA0DA74f2";
        uuidValidator = new UUIDValidator(false, true, false, false, CaseSensitivity.UPPER_CASE_ONLY, ConversionMode.LEAVE_AS_IS);

        whenChecksValidity();

        thenIsInvalid();

    }

    @Test
    public void shortensUuid() {

        input = "1AE43A66-f1f4-424a-8752-34d19bd21d33aaaaaaaaaaaaa";
        uuidValidator = new UUIDValidator(false, true, false, false, CaseSensitivity.INSENSITIVE, ConversionMode.SHORTEN);

        whenChecksValidityAndConvertsInput();

        thenResultEquals("1AE43A66-f1f4-424a-8752-34d19bd21d33");

    }

    @Test
    public void convertsToLowerCaseWithoutShortening() {

        input = "1AE43A66-f1f4-424a-8752-34d19bd21d33aaaaaaaaaaaaa";
        uuidValidator = new UUIDValidator(false, true, false, false, CaseSensitivity.INSENSITIVE, ConversionMode.TO_LOWER_CASE_NO_SHORTENING);

        whenChecksValidityAndConvertsInput();

        thenResultEquals("1ae43a66-f1f4-424a-8752-34d19bd21d33aaaaaaaaaaaaa");

    }

    @Test
    public void convertsToUpperCaseWithoutShortening() {

        input = "1AE43A66-f1f4-424a-8752-34d19bd21d33aaaaaaaaaaaaa";
        uuidValidator = new UUIDValidator(false, true, false, false, CaseSensitivity.INSENSITIVE, ConversionMode.TO_UPPER_CASE_NO_SHORTENING);

        whenChecksValidityAndConvertsInput();

        thenResultEquals("1AE43A66-F1F4-424A-8752-34D19BD21D33AAAAAAAAAAAAA");

    }

    @Test
    public void shortensUuidAndConvertsToLowerCase() {

        input = "1AE43A66-f1f4-424a-8752-34d19bd21d33aaaaaaaaaaaaa";
        uuidValidator = new UUIDValidator(false, true, false, false, CaseSensitivity.INSENSITIVE, ConversionMode.TO_LOWER_CASE_SHORTEN);

        whenChecksValidityAndConvertsInput();

        thenResultEquals("1ae43a66-f1f4-424a-8752-34d19bd21d33");

    }

    @Test
    public void shortensUuidAndConvertsToUpperCase() {

        input = "1AE43A66-f1f4-424a-8752-34d19bd21d33aaaaaaaaaaaaa";
        uuidValidator = new UUIDValidator(false, true, false, false, CaseSensitivity.INSENSITIVE, ConversionMode.TO_UPPER_CASE_SHORTEN);

        whenChecksValidityAndConvertsInput();

        thenResultEquals("1AE43A66-F1F4-424A-8752-34D19BD21D33");

    }

    private void whenChecksValidity() {
        valid = uuidValidator.isValid(input);
    }

    private void whenChecksValidityAndConvertsInput() {
        converted = uuidValidator.validate(input);
    }

    private void thenIsInvalid() {
        assertThat(valid, is(false));
    }

    private void thenIsValid() {
        assertThat(valid, is(true));
    }

    private void thenResultEquals(String expected) {
        assertThat(converted, is(equalTo(expected)));
    }
}