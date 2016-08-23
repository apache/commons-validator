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

import java.io.Serializable;
import java.util.Locale;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;

/**
 * International Securities Identification Number (ISIN) uniquely identifies a
 * bonds, commercial paper, stocks or warrant.
 */
public class ISINValidator implements Serializable {
    
    private static final long serialVersionUID = -1303801732679730694L;
    
    private static final int ISIN_LENGTH = 12;
    
    private static final ISINValidator VALIDATOR = new ISINValidator();
    
    /**
     * Validates a isin
     * @param isin The isin to validate
     * @return true, if the isin is valid
     */
    public boolean isValid(String isin) {

        // check invariant
        if (isin == null || isin.length() != ISIN_LENGTH || !isin.matches("^[A-Z0-9]{12}$")) {
            return false;
        }

        // check country code or international code
        String countryCode = isin.substring(0, 2);
        if (!countryCode.equals("XS")) {
            boolean foundCode = false;
            for (String cc : Locale.getISOCountries()) {
                if (cc.equals(countryCode)) {
                    foundCode = true;
                    break;
                }
            }
            if (!foundCode) {
                return false;
            }
        }
        
        // convert letters do digits
        StringBuilder sb = new StringBuilder(isin);
        final int offset = 55;
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            if (Character.isUpperCase(c)) {
                String n = String.valueOf((int)c - offset);
                int j = n.length() - 1;
                sb.replace(i, i+1, n);
                i += j;
            }
        }
        
        // check digit (Luhn algorithm)
        return LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(sb.toString());
    }
    
    /**
     * Return a singleton instance of the ISIN validator
     *
     * @return A singleton instance of the ISIN validator.
     */
    public static ISINValidator getInstance() {
        return VALIDATOR;
    }

}
