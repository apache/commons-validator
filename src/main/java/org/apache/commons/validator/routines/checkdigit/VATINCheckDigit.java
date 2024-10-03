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

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b>VATIN</b> (VAT identification number) Check Digit calculation/validation.
 * <p>
 * Each country has a different routine. There are special notation for Greece and Northern Ireland.<br>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia - VATIN</a>
 * and <a href="https://en.wikipedia.org/wiki/European_Union_value_added_tax">Wikipedia - EU VAT</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATINCheckDigit extends AbstractCheckDigit implements Serializable {

    private static final long serialVersionUID = -6121493628894479170L;
    private static final Log LOG = LogFactory.getLog(VATINCheckDigit.class);

    /** Singleton Check Digit instance */
    private static final VATINCheckDigit INSTANCE = new VATINCheckDigit();

    /**
     * Gets the singleton instance of this Check Digit calculation/validation.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    private static final int COUNTRY_CODE_LEN = 2;
    /**
     * This message is used for EL VATIN codes accidentally used with prefix GR.
     * Or codes starting with MC for Monaco. Monaco is not part of EU, but some companies use FR-VATINS.
     */
    private static final String INVALID_COUNTRY_CODE = "No CheckDigit routine or invalid country, code=";
    private final ConcurrentMap<String, AbstractCheckDigit> checkDigitMap;

   /**
     * Constructs Check Digit routine for VATIN Numbers.
     */
    private VATINCheckDigit() {
        this.checkDigitMap = createCheckDigitMap(); //createValidators(validators);
    }

    /*
     * Note:
     * Greece uses language code EL instead its country code.
     * Northern Ireland is part of EU but has no country code, so XI is used.
     * United Kingdom is not part of EU, but the check digit routine is used for Northern Ireland.
     * Some countries (DE, HR, LT) use a general check digit routine.
     */
    private ConcurrentMap<String, AbstractCheckDigit> createCheckDigitMap() {
        final ConcurrentMap<String, AbstractCheckDigit> map = new ConcurrentHashMap<>();
        map.put("AT", (AbstractCheckDigit) VATidATCheckDigit.getInstance());
        map.put("BE", (AbstractCheckDigit) VATidBECheckDigit.getInstance());
        map.put("BG", (AbstractCheckDigit) VATidBGCheckDigit.getInstance());
        map.put("CY", (AbstractCheckDigit) VATidCYCheckDigit.getInstance());
        map.put("CZ", (AbstractCheckDigit) VATidCZCheckDigit.getInstance());
        map.put("DE", (AbstractCheckDigit) Modulus11TenCheckDigit.getInstance());
        map.put("DK", (AbstractCheckDigit) VATidDKCheckDigit.getInstance());
        map.put("EE", (AbstractCheckDigit) VATidEECheckDigit.getInstance());
        map.put("EL", (AbstractCheckDigit) VATidELCheckDigit.getInstance());
        map.put("ES", (AbstractCheckDigit) VATidESCheckDigit.getInstance());
        map.put("FI", (AbstractCheckDigit) VATidFICheckDigit.getInstance());
        map.put("FR", (AbstractCheckDigit) VATidFRCheckDigit.getInstance());
        map.put("HR", (AbstractCheckDigit) Modulus11TenCheckDigit.getInstance());
        map.put("HU", (AbstractCheckDigit) VATidHUCheckDigit.getInstance());
        map.put("IE", (AbstractCheckDigit) VATidIECheckDigit.getInstance());
        map.put("IT", (AbstractCheckDigit) LuhnCheckDigit.LUHN_CHECK_DIGIT);
        map.put("LT", (AbstractCheckDigit) VATidLTCheckDigit.getInstance());
        map.put("LU", (AbstractCheckDigit) VATidLUCheckDigit.getInstance());
        map.put("LV", (AbstractCheckDigit) VATidLVCheckDigit.getInstance());
        map.put("MT", (AbstractCheckDigit) VATidMTCheckDigit.getInstance());
        map.put("NL", (AbstractCheckDigit) VATidNLCheckDigit.getInstance());
        map.put("PL", (AbstractCheckDigit) VATidPLCheckDigit.getInstance());
        map.put("PT", (AbstractCheckDigit) VATidPTCheckDigit.getInstance());
        map.put("RO", (AbstractCheckDigit) VATidROCheckDigit.getInstance());
        map.put("SE", (AbstractCheckDigit) VATidSECheckDigit.getInstance());
        map.put("SI", (AbstractCheckDigit) VATidSICheckDigit.getInstance());
        map.put("SK", (AbstractCheckDigit) VATidSKCheckDigit.getInstance());
        map.put("XI", (AbstractCheckDigit) VATidGBCheckDigit.getInstance());
        return map;
    }

    /**
     * Gets the Check Digit routine for a given VATIN Number
     *
     * @param code a string with the ISO country code
     *  (European Union VATIN countries + XI for Northern Ireland)
     *
     * @return the Check Digit routine or {@code null} if there is no routine registered.
     */
    public AbstractCheckDigit getCheckDigitMap(final String code) {
        if (code == null || code.length() < COUNTRY_CODE_LEN) { // ensure we can extract the code
            return null;
        }
        final String key = code.substring(0, COUNTRY_CODE_LEN);
        return checkDigitMap.get(key);
    }

    /**
     * {@inheritDoc}
     * For a country specific VATIN code.
     *
     */
    @Override
    public String calculate(String code) throws CheckDigitException {
        final AbstractCheckDigit routine = getCheckDigitMap(code);
        if (routine == null) {
            throw new CheckDigitException(INVALID_COUNTRY_CODE + code);
        }
        final String vatin = code.substring(2);
        return routine.calculate(vatin);
    }

    /**
     * {@inheritDoc}
     * For a country specific VATIN code.
     *
     * @param code The code to validate, the string must start with country code and include the check digit.
     */
    @Override
    public boolean isValid(final String code) {
        final AbstractCheckDigit routine = getCheckDigitMap(code);
        if (routine == null) {
            LOG.warn(INVALID_COUNTRY_CODE + code);
            return false;
        }
        final String vatin = code.substring(2);
        return routine.isValid(vatin);
    }

}
