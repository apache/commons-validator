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

import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;
import org.apache.commons.validator.util.Flags;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * <p>Perform credit card validations.</p>
 * <p>
 * By default, all supported card types are allowed.  You can specify which 
 * cards should pass validation by configuring the validation options.  For 
 * example,<br/><code>CreditCardValidator ccv = new CreditCardValidator(CreditCardValidator.AMEX + CreditCardValidator.VISA);</code>
 * configures the validator to only pass American Express and Visa cards.
 * If a card type is not directly supported by this class, you can implement
 * the CreditCardType interface and pass an instance into the 
 * <code>addAllowedCardType</code> method.
 * </p>
 * For a similar implementation in Perl, reference Sean M. Burke's
 * <a href="http://www.speech.cs.cmu.edu/~sburke/pub/luhn_lib.html">script</a>.
 * More information is also available
 * <a href="http://www.merriampark.com/anatomycc.htm">here</a>.
 *
 * @version $Revision$ $Date$
 * @since Validator 1.4
 */
public class CreditCardValidator implements Serializable {

    /**
     * Option specifying that no cards are allowed.  This is useful if
     * you want only custom card types to validate so you turn off the
     * default cards with this option.
     * <br/>
     * <pre>
     * CreditCardValidator v = new CreditCardValidator(CreditCardValidator.NONE);
     * v.addAllowedCardType(customType);
     * v.isValid(aCardNumber);
     * </pre>
     */
    public static final int NONE = 0;

    /**
     * Option specifying that American Express cards are allowed.
     */
    public static final int AMEX = 1 << 0;

    /**
     * Option specifying that Visa cards are allowed.
     */
    public static final int VISA = 1 << 1;

    /**
     * Option specifying that Mastercard cards are allowed.
     */
    public static final int MASTERCARD = 1 << 2;

    /**
     * Option specifying that Discover cards are allowed.
     */
    public static final int DISCOVER = 1 << 3;

    /**
     * Option specifying that Diners cards are allowed.
     */
    public static final int DINERS = 1 << 4;
    
    /**
     * The CreditCardTypes that are allowed to pass validation.
     */
    private Collection cardTypes = new ArrayList();

    /**
     * Luhn checkdigit validator for the card numbers.
     */
    private static final CheckDigit LUHN_VALIDATOR = LuhnCheckDigit.INSTANCE;

    /** American Express (Amex) Card Validator */
    public static final CodeValidator AMEX_VALIDATOR = new CodeValidator("^(3[47]\\d{13})$", LUHN_VALIDATOR);

    /** Diners Card Validator */
    public static final CodeValidator DINERS_VALIDATOR = new CodeValidator("^(30[0-5]\\d{11}|36\\d{12})$", LUHN_VALIDATOR);

    /** Discover Card Validator */
    public static final CodeValidator DISCOVER_VALIDATOR = new CodeValidator("^(6011\\d{12})$", LUHN_VALIDATOR);

    /** Mastercard Card Validator */
    public static final CodeValidator MASTERCARD_VALIDATOR = new CodeValidator("^(5[1-5]\\d{14})$", LUHN_VALIDATOR);

    /** Visa Card Validator */
    public static final CodeValidator VISA_VALIDATOR = new CodeValidator("^(4)(\\d{12}|\\d{15})$", LUHN_VALIDATOR);

    /**
     * Create a new CreditCardValidator with default options.
     */
    public CreditCardValidator() {
        this(AMEX + VISA + MASTERCARD + DISCOVER);
    }

    /**
     * Create a new CreditCardValidator with the specified options.
     * @param options Pass in
     * CreditCardValidator.VISA + CreditCardValidator.AMEX to specify that 
     * those are the only valid card types.
     */
    public CreditCardValidator(int options) {
        super();

        Flags f = new Flags(options);
        if (f.isOn(VISA)) {
            this.cardTypes.add(VISA_VALIDATOR);
        }

        if (f.isOn(AMEX)) {
            this.cardTypes.add(AMEX_VALIDATOR);
        }

        if (f.isOn(MASTERCARD)) {
            this.cardTypes.add(MASTERCARD_VALIDATOR);
        }

        if (f.isOn(DISCOVER)) {
            this.cardTypes.add(DISCOVER_VALIDATOR);
        }

        if (f.isOn(DINERS)) {
            this.cardTypes.add(DINERS_VALIDATOR);
        }
    }

    /**
     * Create a new CreditCardValidator with the specified {@link CodeValidator}s.
     * @param creditCardValidators Set of valid code validators
     */
    public CreditCardValidator(CodeValidator[] creditCardValidators) {
        if (creditCardValidators == null) {
            throw new IllegalArgumentException("Card validators are missing");
        }
        for (int i = 0; i < creditCardValidators.length; i++) {
            cardTypes.add(creditCardValidators[i]);
        }
    }

    /**
     * Checks if the field is a valid credit card number.
     * @param card The card number to validate.
     * @return Whether the card number is valid.
     */
    public boolean isValid(String card) {
        if (card == null || card.length() == 0) {
            return false;
        }
        Iterator types = this.cardTypes.iterator();
        while (types.hasNext()) {
            CodeValidator type = (CodeValidator)types.next();
            if (type.isValid(card)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the field is a valid credit card number.
     * @param card The card number to validate.
     * @return The card number if valid or <code>null</code>
     * if invalid.
     */
    public Object validate(String card) {
        if (card == null || card.length() == 0) {
            return null;
        }
        Object result = null;
        Iterator types = this.cardTypes.iterator();
        while (types.hasNext()) {
            CodeValidator type = (CodeValidator)types.next();
            result = type.validate(card);
            if (result != null) {
                return result ;
            }
        }
        return null;

    }

}
