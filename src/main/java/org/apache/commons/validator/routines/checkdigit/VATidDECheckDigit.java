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
 * deutsche Umsatzsteuer-Identifikationsnummer.
 *
 * <p>
 * siehe bgbl193s0736, Bundesgesetztblatt, Jahrgang 1993, Teil I
 * Anlage 3 (zu §9)
 * <br>
 * hybrides MOD 11,10
 * Aufbau: DEnnnnnnnnp.
 * </p>

produkt := 10
for j=1 to 8 step 1
  summe := cj + produkt
  summe := summe mod 10
  if summe=0 then summe = 10
  produkt := (2*summe) mod 11
end for
pz := 11 - produkt
if pz=10 then pz = 0

Beispiel: DE 136 695 976
 *
 * @since 1.10.0
 */
//TODO remove it nur zur Doku use Modulus11TenCheckDigit
public final class VATidDECheckDigit extends Modulus11TenCheckDigit {

}
