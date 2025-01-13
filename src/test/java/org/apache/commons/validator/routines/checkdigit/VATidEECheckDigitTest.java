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
 * EE VAT Id Check Digit Tests.
 * <p>
 * Estonian VAT identification number (VATIN) Check Digit is called
 * Käibemaksukohustuslase registreeri-misnumber (KMKR).
 * It uses the same algorithm as ABA. So I check against ABANumberCheckDigit.
 * </p>
 * <p>
 * See <a href="https://www.emta.ee/en/admin/content/handbook_article/742">Estonian Tax and Customs Board</a>
 * for more details.
 * </p>
 * <pre>

    EE 100594102 : gültig MOVEK GRUPP OÜ
    EE 101571629 : gültig Kaubamaja AS, Tallinn
    EE 100825871 : gültig Osaühing LUCULLUS, Tallinn
    EE 101295701 : gültig A&T Prisce OÜ pikajalaresto.ee, Tallinn
    EE 102264744 : gültig Hansatall, Tartu
    EE 100931558 : valide, aber ungültig, da gelöscht - Name OÜ VANALINNA REISID
    EE 100207415 : valide, aber ungültig, da gelöscht - aus BMF_UID_Konstruktionsregeln.pdf
    EE 100931558 : valide, aber ungültig

 * </pre>
 */
public class VATidEECheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = ABANumberCheckDigit.ABAN_CHECK_DIGIT;
//        same to:
//        routine = new ModulusTenCheckDigit(new int[] { 1, 7, 3 }, true);
        valid = new String[] {"100594102", "101571629", "100825871", "101295701", "102264744"
                , "100571620" // check digit zero
                , "100931558", "100207415"};
        invalid = new String[] {"100594103"};
    }

}
