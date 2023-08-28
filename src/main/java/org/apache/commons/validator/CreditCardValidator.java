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
package org.apache.commons.validator;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.validator.util.Flags;

/**
 * Perform credit card validations.
 *
 * <p>
 * By default, all supported card types are allowed.  You can specify which
 * cards should pass validation by configuring the validation options. For
 * example,
 * </p>
 *
 * <pre>
 * <code>CreditCardValidator ccv = new CreditCardValidator(CreditCardValidator.AMEX + CreditCardValidator.VISA);</code>
 * </pre>
 *
 * <p>
 * configures the validator to only pass American Express and Visa cards.
 * If a card type is not directly supported by this class, you can implement
 * the CreditCardType interface and pass an instance into the
 * <code>addAllowedCardType</code> method.
 * </p>
 *
 * <p>
 * For a similar implementation in Perl, reference Sean M. Burke's
 * <a href="http://www.speech.cs.cmu.edu/~sburke/pub/luhn_lib.html">script</a>.
 * More information is also available
 * <a href="http://www.merriampark.com/anatomycc.htm">here</a>.
 * </p>
 *
 * @since 1.1
 * @deprecated Use the new CreditCardValidator in the routines package. This class
 * will be removed in a future release.
 */
// CHECKSTYLE:OFF (deprecated code)
@Deprecated
public class CreditCardValidator {

    /**
     * Option specifying that no cards are allowed.  This is useful if
     * you want only custom card types to validate so you turn off the
     * default cards with this option.
     * <pre>
     * <code>
     * CreditCardValidator v = new CreditCardValidator(CreditCardValidator.NONE);
     * v.addAllowedCardType(customType);
     * v.isValid(aCardNumber);
     * </code>
     * </pre>
     * @since 1.1.2
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
     * The CreditCardTypes that are allowed to pass validation.
     */
    private final Collection<CreditCardType> cardTypes = new ArrayList<>();

    /**
     * Create a new CreditCardValidator with default options.
     */
    public CreditCardValidator() {
        this(AMEX + VISA + MASTERCARD + DISCOVER);
    }

    /**
     * Creates a new CreditCardValidator with the specified options.
     * @param options Pass in
     * CreditCardValidator.VISA + CreditCardValidator.AMEX to specify that
     * those are the only valid card types.
     */
    public CreditCardValidator(final int options) {
        final Flags f = new Flags(options);
        if (f.isOn(VISA)) {
            this.cardTypes.add(new Visa());
        }

        if (f.isOn(AMEX)) {
            this.cardTypes.add(new Amex());
        }

        if (f.isOn(MASTERCARD)) {
            this.cardTypes.add(new Mastercard());
        }

        if (f.isOn(DISCOVER)) {
            this.cardTypes.add(new Discover());
        }
    }

    /**
     * Checks if the field is a valid credit card number.
     * @param card The card number to validate.
     * @return Whether the card number is valid.
     */
    public boolean isValid(final String card) {
        if ((card == null) || (card.length() < 13) || (card.length() > 19)) {
            return false;
        }

        if (!this.luhnCheck(card)) {
            return false;
        }

        for (final Object cardType : this.cardTypes) {
            final CreditCardType type = (CreditCardType) cardType;
            if (type.matches(card)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds an allowed CreditCardType that participates in the card
     * validation algorithm.
     * @param type The type that is now allowed to pass validation.
     * @since 1.1.2
     */
    public void addAllowedCardType(final CreditCardType type){
        this.cardTypes.add(type);
    }

    /**
     * Checks for a valid credit card number.
     * @param cardNumber Credit Card Number.
     * @return Whether the card number passes the luhnCheck.
     */
    protected boolean luhnCheck(final String cardNumber) {
        // number must be validated as 0..9 numeric first!!
        final int digits = cardNumber.length();
        final int oddOrEven = digits & 1;
        long sum = 0;
        for (int count = 0; count < digits; count++) {
            int digit = 0;
            try {
                digit = Integer.parseInt(cardNumber.charAt(count) + "");
            } catch(final NumberFormatException e) {
                return false;
            }

            if (((count & 1) ^ oddOrEven) == 0) { // not
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
        }

        return sum != 0 && (sum % 10 == 0);
    }

    /**
     * CreditCardType implementations define how validation is performed
     * for one type/brand of credit card.
     * @since 1.1.2
     */
    public interface CreditCardType {

        /**
         * Returns true if the card number matches this type of credit
         * card.  Note that this method is <strong>not</strong> responsible
         * for analyzing the general form of the card number because
         * <code>CreditCardValidator</code> performs those checks before
         * calling this method.  It is generally only required to valid the
         * length and prefix of the number to determine if it's the correct
         * type.
         * @param card The card number, never null.
         * @return true if the number matches.
         */
        boolean matches(String card);

    }

    /**
     *  Change to support Visa Carte Blue used in France
     *  has been removed - see Bug 35926
     */
    private static class Visa implements CreditCardType {
        private static final String PREFIX = "4";
        @Override
        public boolean matches(final String card) {
            return (
                card.substring(0, 1).equals(PREFIX)
                    && (card.length() == 13 || card.length() == 16));
        }
    }

    private static class Amex implements CreditCardType {
        private static final String PREFIX = "34,37,";
        @Override
        public boolean matches(final String card) {
            final String prefix2 = card.substring(0, 2) + ",";
            return ((PREFIX.contains(prefix2)) && (card.length() == 15));
        }
    }

    private static class Discover implements CreditCardType {
        private static final String PREFIX = "6011";
        @Override
        public boolean matches(final String card) {
            return (card.substring(0, 4).equals(PREFIX) && (card.length() == 16));
        }
    }

    private static class Mastercard implements CreditCardType {
        private static final String PREFIX = "51,52,53,54,55,";
        @Override
        public boolean matches(final String card) {
            final String prefix2 = card.substring(0, 2) + ",";
            return ((PREFIX.contains(prefix2)) && (card.length() == 16));
        }
    }

}
