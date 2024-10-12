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
 * BG VAT Id Check Digit Tests.
 * <pre>

    BG 108511243 : gültig ПАНОРАМА-2000 - ООД
    BG 206425654 : gültig https://nutrinitylabs.com/contacts
    BG 131324923 : gültig МАКСИМА БЪЛГАРИЯ - ЕООД, Adresse БОТЕВГРАДСКО ШОСЕ №247 ет.2 обл.СОФИЯ, гр.СОФИЯ 1517
    BG 121759222 : gültig ОМВ БЪЛГАРИЯ - ООД, Adresse Гжк Малинова долинаДонка Ушлинова №2 бл.сграда 4 ет.+1 ап.помещение 411 обл.СОФИЯ, гр.СОФИЯ 1766
    BG 207402758 : gültig Тегел Груп - ЕООД, Adresse Георги Кондолов №38 обл.БУРГАС, с.ЛОЗЕНЕЦ 8277
    BG 205153347 : gültig ВАЛЕРИЯ-64 - ООД, Adresse Първи май №74 обл.КЪРДЖАЛИ, гр.КЪРДЖАЛИ 6600
    BG 204407312 : gültig ИВКОН СТРОЙ - ООД, Adresse ул. Богдан №20 вх.А ет.4 ап.12 обл.СОФИЯ, гр.СОФИЯ 1000
    BG 201334001 : gültig КАРТ 8 - ЕООД, Adresse ул. "Балкан" №23 обл.СТАРА ЗАГОРА, с.ЕЛХОВО 6174
    BG 131129282 : gültig КАУФЛАНД БЪЛГАРИЯ ЕООД ЕНД КО - КД (Kaufland)
    BG 203519454 : gültig 43-ти километър - ЕООД
    BG 207350980 : gültig АИД-8485 - ЕООД, Adresse жк МЕСТНОСТ ЗАХАРИДЕВО №608 Б ет.1 ап.2 обл.ПЛОВДИВ, с.МАРКОВО 4108
    BG 207546057 : gültig Глобал Ексчейндж Къренси Ексчейндж България - ЕООД, Adresse Проф. Фритьоф Нансен №37А ет.5 обл.СОФИЯ, гр.СОФИЯ 1142
    BG 831650349 : gültig (Nestlé) НЕСТЛЕ БЪЛГАРИЯ - АД
    BG 207839658 : gültig ЕКОНТ ЕКСПРЕС - АД
    BG 175074752 : gültig ПРОФИ КРЕДИТ БЪЛГАРИЯ - ЕООД. Aus old.formvalidation.io
    BG 7523169263 8032056031 7542011030 7111042925: valide ???, aber ungültig (gleiche Quelle)

Примери:
       ЕГН Значение
7524169268 Мъж, с дата на раждане 16.04.1875 г.
7501010010 Жена, с дата на раждане 01.01.1975 г.
7552010005 Мъж, с дата на раждане 01.12.2075 г.
8032056031 Жена, с дата на раждане 05.12.1880 г.
8001010008 Мъж, с дата на раждане 01.01.1980 г.
7552011038 Жена, с дата на раждане 01.12.2075 г.
8141010016 Жена, с дата на раждане 01.01.2081 г

 * </pre>
 */
public class VATidBGCheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = VATidBGCheckDigit.getInstance();
        valid = new String[] {"108511243", "206425654", "131324923", "121759222", "207402758"
            , "205153347", "204407312", "201334001", "131129282", "203519454"
            , "207350980", "207546057", "831650349", "207839658", "175074752"
            , "217839654", "175074767", "474074760" // increased weights
            , "8319195360016", "8319195360048" // Unicredit Bulbank with branches 001 004
            // 10 digits : ЕГН (civil number), which contains a coded birth date
            , "7524169268", "7501010010", "7552010005", "8032056031", "8001010008", "7552011038", "8141010016"
        };
        invalid = new String[] { "10851124" // to short
                , "7502300013"     // invalid date 30 Feb
                , "8319195370016"  // Invalid DDC subcode
            };
    }

}
