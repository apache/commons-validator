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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private static final Log LOG = LogFactory.getLog(VATidESCheckDigit.class);

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
    private static final String NIF_LETTER = "TRWAGMYFPDXBNJZSQVHLCKE"; // secuenciaLetrasNIF
    // "XYZ" Foreign natural person, "KLM" Persons without DNI
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

    private char calculateNIFletter(final String code) throws CheckDigitException {
        final Long l = GenericTypeValidator.formatLong(code);
        if (l == null) {
            throw new CheckDigitException("Invalid VAT number " + code);
        }
        if (l == 0) {
            throw new CheckDigitException(CheckDigitException.ZERO_SUM);
        }
        return NIF_LETTER.charAt((int) (l % MODULUS_23));
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
            return "" + calculateNIFletter(code);
        }
        if (FORMATOXYZ.indexOf(code.charAt(0)) > -1) {
            // Españoles senza DNI + Extranjeros con NIE
            return "" + calculateNIFletter(code.substring(1));
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
            final char cd = code.charAt(code.length() - 1);
            if (Character.isDigit(code.charAt(0))) {
                // Españoles con DNI
                return cd == calculateNIFletter(code.substring(0, code.length() - 1));
            }
            if (FORMATOXYZ.indexOf(code.charAt(0)) > -1) {
                // Foreign natural person, Extranjero con NIE + Persons without DNI
                return cd == calculateNIFletter(code.substring(1, code.length() - 1));
            }

            // Luhn
            final char c0 = code.charAt(0);
            if (LOG.isDebugEnabled()) {
                switch (c0) {
                case 'A':
                    LOG.debug(code + " : Sociedades anónimas");
                    break;
                case 'B':
                    LOG.debug(code + " : Sociedades de responsabilidad limitada");
                    break;
                case 'C':
                    LOG.debug(code + " : Sociedades colectivas");
                    break;
                case 'D':
                    LOG.debug(code + " : Sociedades comanditarias");
                    break;
                case 'E':
                    LOG.debug(code + " : Comunidades de bienes");
                    break;
                case 'F':
                    LOG.debug(code + " : Sociedades cooperativas");
                    break;
                case 'G':
                    LOG.debug(code + " : Asociaciones y Fundaciones");
                    break;
                case 'H':
                    LOG.debug(code + " : Comunidades de propietarios en régimen de propiedad horizontal");
                    break;
                case 'J':
                    LOG.debug(code + " : Sociedades civiles, con o sin personalidad jurídica");
                    break;
                case 'N':
                    LOG.debug(code + " : Entidades extranjeras");
                    break;
                case 'P':
                    LOG.debug(code + " : Corporaciones Locales");
                    break;
                case 'Q':
                    LOG.debug(code + " : Organismos públicos");
                    break;
                case 'R':
                    LOG.debug(code + " : Congregaciones e instituciones religiosas");
                    break;
                case 'S':
                    LOG.debug(code + " : Órganos de la Administración General del Estado y de las comunidades autónomas");
                    break;
                case 'W':
                    LOG.debug(code + " : Establecimientos permanentes de entidades no residentes en España");
                    break;
                case 'U':
                    LOG.debug(code + " : Uniones Temporales de Empresas");
                    break;
                case 'V':
                    LOG.debug(code + " : Otros tipos no definidos en el resto de claves");
                    break;
                default:
                    LOG.warn(code + " starts with " + c0);
                    break;
                }
            }
            if (FORMATONPQ.indexOf(c0) > -1) {
                return cd == LUHNCHECKLETTER.charAt(calculateLuhn(code.substring(1, code.length() - 1)));
            } else {
                return Character.getNumericValue(cd) == calculateLuhn(code.substring(1, code.length() - 1));
            }
        } catch (final CheckDigitException ex) {
            return false;
        }
    }

}
