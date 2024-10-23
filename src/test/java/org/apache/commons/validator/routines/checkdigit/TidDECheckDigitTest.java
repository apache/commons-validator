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
 * DE Tax Id Check Digit Tests.
 *
Quelle: Bayerisches Landesamt für Steuern:
<a href="https://download.elster.de/download/schnittstellen/Pruefung_der_Steuer_und_Steueridentifikatsnummer.pdf">Prüfung der Steuer- und Steueridentifikationsnummer</a>

Die Steueridentifikationsnummer ist eine elfstellige Ziffernfolge, // Check in TINValidator
die elfte Stelle ist eine Prüfziffer.
Die Steueridentifikationsnummer muss folgenden Kriterien entsprechen:

    Keine führenden Nullen erlaubt,  // Check in TINValidator
      außer es ist eine Testidentifikationsnummer
    In den ersten 10 Stellen der Identifikationsnummer muss genau eine Ziffer doppelt oder dreifach vorkommen.
    Existieren drei gleiche Ziffern an den Positionen 1 bis 10,
     dürfen diese gleichen Ziffern niemals an direkt aufeinander folgenden Stellen stehen.

Beispiele

    02476291358 : TestID da führende 0, doppelte Ziffer : 2
    86095742719 : doppelte Ziffer : 7
    47036892816 : doppelte Ziffer : 8
    65929970489 : keine doppelte Ziffer, dreifache Ziffer : 9
    57549285017 : keine doppelte Ziffer, dreifache Ziffer : 5
    25768131411 : keine doppelte Ziffer, dreifache Ziffer : 1

 */
public class TidDECheckDigitTest extends AbstractCheckDigitTest {

    /**
     * Sets up routine & valid codes.
     */
    @BeforeEach
    protected void setUp() {
        routine = TidDECheckDigit.getInstance();
        valid = new String[] {"81872495633" // aus http://www.pruefziffernberechnung.de/I/Identifikationsnummer.shtml
                , "02476291358"
                , "86095742719"
                , "47036892816"
                , "65929970489"
                , "57549285017"
                , "25768131411"
                , "11012234564" // 2 doppelt und 1 dreifach
                , "11234567890" // doppelte Ziffer : 1 direkt hintereinander
                , "11012345675" // dreifach Ziffer : 1 nicht direkt hintereinander
/* diese Steuernummer wurde vergeben, sie hat aber zwei doppelte Ziffer
                , "12720320213" // 0 und 1 doppelt und 2 vierfach
 */
            };
        invalid = new String[] {"00000000010" // theoretical minimum
                , "99999999994" // theoretical maximum
                , "12345678903" // keine Ziffer doppelt oder dreifach
                , "98765432106" // keine Ziffer doppelt oder dreifach
                , "11012134569" // vierfache Ziffer : 1
                , "11223456785" // zwei doppelte Ziffer : 1 und 2
                , "11212234562" // zwei dreifache Ziffer : 1 und 2
                , "11123456786" // dreifach Ziffer : 1 direkt hintereinander
            };
    }

}
