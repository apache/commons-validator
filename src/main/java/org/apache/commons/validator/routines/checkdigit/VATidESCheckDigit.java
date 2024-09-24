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

import java.util.logging.Logger;

import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.GenericValidator;

/**
 * Spanish VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * el número de identificación a efectos del Impuesto sobre el Valor Anadido (N.IVA)
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia</a>
 * or <a href="https://es.wikipedia.org/wiki/N%C3%BAmero_de_identificaci%C3%B3n_fiscal">Wikipedia (es)</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidESCheckDigit extends ModulusCheckDigit {

    private static final long serialVersionUID = -7198490175731077347L;
    private static final Logger LOG = Logger.getLogger(VATidESCheckDigit.class.getName());

    /** Singleton Check Digit instance */
    private static final VATidESCheckDigit INSTANCE = new VATidESCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    private static final int MIN_CODE_LEN = 4;
    private static final int MODULUS_23 = 23;
    private static final String CHECK_CHARACTER = "TRWAGMYFPDXBNJZSQVHLCKE";
    // "XYZ" Foreign natural person (NIE) siehe https://github.com/koblas/stdnum-js/blob/main/src/es/nif.ts
    private static final String FORMATOXYZ = "XYZKLM";
    private static final String FORMATONPQ = "NPQRSW";
    private static final String LUHNCHECKLETTER = "JABCDEFGHI";

    /** LUHN Weighting given to digits depending on their right position */
    private static final int[] POSITION_WEIGHT = {2, 1};

    /**
     * Constructs a Check Digit routine.
     */
    private VATidESCheckDigit() {
        super(MODULUS_10);
    }

    /**
     * <p>Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.</p>
     *
     * <p>For Luhn (from right to left) <b>odd</b> digits are weighted
     * with a factor of <b>one</b> and <b>even</b> digits with a factor
     * of <b>two</b>. Weighted values &gt; 9, have 9 subtracted</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos The position of the character in the code, counting from left to right
     * @param rightPos The position of the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(final int charValue, final int leftPos, final int rightPos) {
        final int weight = POSITION_WEIGHT[rightPos % 2]; // CHECKSTYLE IGNORE MagicNumber
        final int weightedValue = charValue * weight;
        return weightedValue > 9 ? weightedValue - 9 : weightedValue; // CHECKSTYLE IGNORE MagicNumber
    }

    private int calculateLuhn(final String code) throws CheckDigitException {
        final int modulusResult = calculateModulus(code, false);
        return (MODULUS_10 - modulusResult) % MODULUS_10;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String calculate(final String code) throws CheckDigitException {
        if (GenericValidator.isBlankOrNull(code)) {
            throw new CheckDigitException(CheckDigitException.MISSING_CODE);
        }
        if (Character.isDigit(code.charAt(0))) {
            // Españoles con DNI
            long value = GenericTypeValidator.formatLong(code);
            if (value == 0) {
                throw new CheckDigitException(CheckDigitException.ZREO_SUM);
            }
            LOG.info("DNI MOD 23:" + value % MODULUS_23);
            return "" + CHECK_CHARACTER.charAt((int) (value % MODULUS_23));
        }
        if (FORMATOXYZ.indexOf(code.charAt(0)) > -1) {
            long value = GenericTypeValidator.formatLong(code.substring(1));
            if (value == 0) {
                throw new CheckDigitException(CheckDigitException.ZREO_SUM);
            }
            LOG.info("NIE (XYZ)+KLM with MOD 23:" + value % MODULUS_23);
            return "" + CHECK_CHARACTER.charAt((int) (value % MODULUS_23));
        }

        // Luhn
        if (FORMATONPQ.indexOf(code.charAt(0)) > -1) {
            return "" + LUHNCHECKLETTER.charAt(calculateLuhn(code.substring(1)));
        }
        return toCheckDigit(calculateLuhn(code.substring(1)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String code) {
        if (GenericValidator.isBlankOrNull(code)) {
            return false;
        }
        if (code.length() <= MIN_CODE_LEN) {
            return false;
        }
        try {
            final char ccd = code.charAt(code.length() - 1);
            if (Character.isDigit(code.charAt(0))) {
                // Españoles con DNI
                long value = GenericTypeValidator.formatLong(code.substring(0, code.length() - 1));
                if (value == 0) {
                    throw new CheckDigitException(CheckDigitException.ZREO_SUM);
                }
                return ccd == CHECK_CHARACTER.charAt((int) (value % MODULUS_23));
            }
            if (FORMATOXYZ.indexOf(code.charAt(0)) > -1) {
                // Foreign natural person, Extranjero con NIE + Persons without DNI
                long value = GenericTypeValidator.formatLong(code.substring(1, code.length() - 1));
                if (value == 0) {
                    throw new CheckDigitException(CheckDigitException.ZREO_SUM);
                }
                return ccd == CHECK_CHARACTER.charAt((int) (value % MODULUS_23));
            }

            // Luhn
            if (FORMATONPQ.indexOf(code.charAt(0)) > -1) {
                return ccd == LUHNCHECKLETTER.charAt(calculateLuhn(code.substring(1, code.length() - 1)));
            }
            if (code.charAt(0) == 'A') {
                LOG.info(code + " : Sociedades anónimas");
            }
            if (code.charAt(0) == 'B') {
                LOG.info(code + " : Sociedades de responsabilidad limitada");
            }
            if (code.charAt(0) == 'C') {
                LOG.info(code + " : Sociedades colectivas");
            }
            if (code.charAt(0) == 'D') {
                LOG.info(code + " : Sociedades comanditarias");
            }
            if (code.charAt(0) == 'E') {
                LOG.info(code + " : Comunidades de bienes");
            }
            if (code.charAt(0) == 'F') {
                LOG.info(code + " : Sociedades cooperativas");
            }
            if (code.charAt(0) == 'G') {
                LOG.info(code + " : Asociaciones y Fundaciones");
            }
            if (code.charAt(0) == 'H') {
                LOG.info(code + " : Comunidades de propietarios en régimen de propiedad horizontal");
            }
            if (code.charAt(0) == 'J') {
                LOG.info(code + " : Sociedades civiles, con o sin personalidad jurídica");
            }
            if (code.charAt(0) == 'U') {
                LOG.info(code + " : Uniones Temporales de Empresas");
            }
            if (code.charAt(0) == 'V') {
                LOG.info(code + " : Otros tipos no definidos en el resto de claves");
            }
            return Character.getNumericValue(code.charAt(code.length() - 1)) == calculateLuhn(code.substring(1, code.length() - 1));
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
