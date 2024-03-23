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
package org.apache.commons.validator.routines.checkdigit;

import org.junit.jupiter.api.BeforeEach;

/**
 * EAN-13 Check Digit Test.
 */
public class EAN13CheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = EAN13CheckDigit.EAN13_CHECK_DIGIT;
        valid = new String[] { "9780072129519", "9780764558313", "4025515373438", "0095673400332" };
    }

}
