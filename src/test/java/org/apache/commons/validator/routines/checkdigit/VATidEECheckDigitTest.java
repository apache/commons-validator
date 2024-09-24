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
 * EE100931558 ist valide aber ungültig
 * EE100594102 ist gültig

Beispiele

    EE 100594102 : gültig MOVEK GRUPP OÜ
    EE 101571629 : gültig Kaubamaja AS, Tallinn
    EE 100825871 : gültig Osaühing LUCULLUS, Tallinn
    EE 101295701 : gültig A&T Prisce OÜ pikajalaresto.ee, Tallinn
    EE 102264744 : gültig Hansatall, Tartu
    EE 100931558 : valide, aber ungültig, da gelöscht - Name OÜ VANALINNA REISID
    EE 100207415 : valide, aber ungültig, da gelöscht - aus BMF_UID_Konstruktionsregeln.pdf

 */
public class VATidEECheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = VATidEECheckDigit.getInstance();
        valid = new String[] {"100594102", "101571629", "100825871", "101295701", "102264744"
                , "100571620" // check digit zero
                , "100931558", "100207415"};
        invalid = new String[] {"100594103"};
    }

}
