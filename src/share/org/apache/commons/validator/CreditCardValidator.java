/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/CreditCardValidator.java,v 1.15 2004/02/01 02:41:25 dgraham Exp $
 * $Revision: 1.15 $
 * $Date: 2004/02/01 02:41:25 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names, "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.validator.util.Flags;

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
 * @since Validator 1.1
 */
public class CreditCardValidator {

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
     * @since Validator 1.1.2
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
    private Collection cardTypes = new ArrayList();

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
     */
    public boolean isValid(String card) {
        if ((card == null) || (card.length() < 13) || (card.length() > 19)) {
            return false;
        }

        if (!this.luhnCheck(card)) {
            return false;
        }
        
        Iterator types = this.cardTypes.iterator();
        while (types.hasNext()) {
            CreditCardType type = (CreditCardType) types.next();
            if (type.matches(card)) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Add an allowed CreditCardType that participates in the card 
     * validation algorithm.
     * @param type The type that is now allowed to pass validation.
     * @since Validator 1.1.2
     */
    public void addAllowedCardType(CreditCardType type){
        this.cardTypes.add(type);
    }

    /**
     * Checks for a valid credit card number.
     * @param cardNumber Credit Card Number.
     */
    protected boolean luhnCheck(String cardNumber) {
        // number must be validated as 0..9 numeric first!!
        int digits = cardNumber.length();
        int oddOrEven = digits & 1;
        long sum = 0;
        for (int count = 0; count < digits; count++) {
            int digit = 0;
            try {
                digit = Integer.parseInt(cardNumber.charAt(count) + "");
            } catch(NumberFormatException e) {
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

        return (sum == 0) ? false : (sum % 10 == 0);
    }

    /**
     * Checks for a valid credit card number.
     * @param card Credit Card Number.
     * @deprecated This will be removed in a future release.
     */
    protected boolean isValidPrefix(String card) {

        if (card.length() < 13) {
            return false;
        }
        
        return new Visa().matches(card)
            || new Amex().matches(card)
            || new Mastercard().matches(card)
            || new Discover().matches(card);
    }
    
    /**
     * CreditCardType implementations define how validation is performed
     * for one type/brand of credit card.
     * @since Validator 1.1.2
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
    
    private class Visa implements CreditCardType {
        private static final String PREFIX = "4";
        public boolean matches(String card) {
            return (
                card.substring(0, 1).equals(PREFIX)
                    && (card.length() == 13 || card.length() == 16));
        }
    }
    
    private class Amex implements CreditCardType {
        private static final String PREFIX = "34,37,";
        public boolean matches(String card) {
            String prefix2 = card.substring(0, 2) + ",";
            return ((PREFIX.indexOf(prefix2) != -1) && (card.length() == 15));
        }
    }
    
    private class Discover implements CreditCardType {
        private static final String PREFIX = "6011";
        public boolean matches(String card) {
            return (card.substring(0, 4).equals(PREFIX) && (card.length() == 16));
        }
    }
    
    private class Mastercard implements CreditCardType {
        private static final String PREFIX = "51,52,53,54,55,";
        public boolean matches(String card) {
            String prefix2 = card.substring(0, 2) + ",";
            return ((PREFIX.indexOf(prefix2) != -1) && (card.length() == 16));
        }
    }

}
