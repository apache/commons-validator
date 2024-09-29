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

/**
 * Italian VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * Partita IVA (IVA = Imposta sul Valore Aggiunto)
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia - VAT IN</a>
 * for more details.
 * </p>
 * Italienische Umsatzsteuer-Identifikationsnummer.
 *
 * <p>
 * Aufbau: 11, nur Ziffern.
 * </p>

 11 digits (the first 7 digits is a progressive number, the following 3 means the province of residence,
 the last digit is a check number - The check digit is calculated using Luhn's Algorithm.)

 * @since 1.9.0
 */
// cannot subclass the final class LuhnCheckDigit - only for information
public final class VATidITCheckDigit { // extends LuhnCheckDigit {
    private VATidITCheckDigit() {} // aviod instantiation
}
