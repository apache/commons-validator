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
 * Portugal VAT identification number (VATIN) Check Digit calculation/validation.
 * <p>
 * o número de identificacao para efeitos do imposto sobre o valor acrescentado (NIPC)
 * </p>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/VAT_identification_number">Wikipedia - VAT IN</a>
 * for more details.
 * </p>
 *
 * @since 1.10.0
 */
public final class VATidPTCheckDigit extends Modulus11XCheckDigit {

    private static final long serialVersionUID = 3389131219768039368L;

    /** Singleton Check Digit instance */
    private static final VATidPTCheckDigit INSTANCE = new VATidPTCheckDigit();

    /**
     * Gets the singleton instance of this validator.
     * @return A singleton instance of the class.
     */
    public static CheckDigit getInstance() {
        return INSTANCE;
    }

    /**
     * Constructs a new instance.
     */
    private VATidPTCheckDigit() {
    }

    /**
     * {@inheritDoc}
     * <p>
     * Override to handle charValue X.
     * </p>
     */
    @Override
    protected String toCheckDigit(final int charValue) throws CheckDigitException {
        return charValue == X ? "0" : super.toCheckDigit(charValue);
    }

}
