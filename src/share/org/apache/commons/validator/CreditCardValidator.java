/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/CreditCardValidator.java,v 1.10 2003/08/21 19:40:13 rleland Exp $
 * $Revision: 1.10 $
 * $Date: 2003/08/21 19:40:13 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.commons.validator.util.Flags;

/**
 * <p>Perform credit card validations.</p>
 * <p>
 * By default, all supported card types are allowed.  You can specify which cards 
 * should pass validation by configuring the validation options.  For example,<br/>
 * <code>CreditCardValidator ccv = new CreditCardValidator(CreditCardValidator.AMEX + CreditCardValidator.VISA);</code>
 * configures the validator to only pass American Express and Visa cards. 
 * </p>
 * For a similar implementation in Perl, reference Sean M. Burke's 
 * <a href="http://www.speech.cs.cmu.edu/~sburke/pub/luhn_lib.html">script</a>.  
 * More information is also available 
 * <a href="http://www.merriampark.com/anatomycc.htm">here</a>.
 *
 * @author David Winterfeldt
 * @author James Turner
 * @author <a href="mailto:husted@apache.org">Ted Husted</a>
 * @author David Graham
 * @since Validator 1.1
 * @version $Revision: 1.10 $ $Date: 2003/08/21 19:40:13 $
 */
public class CreditCardValidator {

	private static final String AMEX_PREFIX = "34,37,";
	private static final String VISA_PREFIX = "4";
	private static final String MASTERCARD_PREFIX = "51,52,53,54,55,";
	private static final String DISCOVER_PREFIX = "6011";

	/**
	 * Option specifying that American Express cards are allowed.
	 */
	public static final int AMEX = 1;

	/**
	 * Option specifying that Visa cards are allowed.
	 */
	public static final int VISA = 2;

	/**
	 * Option specifying that Mastercard cards are allowed.
	 */
	public static final int MASTERCARD = 4;

	/**
	 * Option specifying that Discover cards are allowed.
	 */
	public static final int DISCOVER = 8;

	/**
	 * The default validation options allow all supported card types.
	 */
	private static final Flags defaultOptions =
		new Flags(AMEX + VISA + MASTERCARD + DISCOVER);

	/**
	 * The current set of validation options.
	 */
	private Flags options = null;

	/**
	 * Create a new CreditCardValidator with default options.
	 */
	public CreditCardValidator() {
		super();
		this.options = defaultOptions;
	}

	/**
	 * Create a new CreditCardValidator with the specified options.  Pass in 
	 * CreditCardValidator.VISA + CreditCardValidator.AMEX to specify that those are the
	 * only valid card types. 
	 */
	public CreditCardValidator(int options) {
		super();
		this.options = new Flags(options);
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

		if (this.isVisa(card)) {
			return this.options.isOn(VISA);
		}

		if (this.isAmex(card)) {
			return this.options.isOn(AMEX);
		}

		if (this.isMastercard(card)) {
			return this.options.isOn(MASTERCARD);
		}

		if (this.isDiscover(card)) {
			return this.options.isOn(DISCOVER);
		}

		return false;
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
				digit =
					Integer.parseInt(String.valueOf(cardNumber.charAt(count)));
			} catch (NumberFormatException e) {
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

		if (sum == 0) {
			return false;
		}

		return (sum % 10 == 0);
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

		return (
			this.isVisa(card)
				|| this.isAmex(card)
				|| this.isMastercard(card)
				|| this.isDiscover(card));
	}

	/**
	 * Returns true if the card is American Express.
	 */
	private boolean isAmex(String card) {
		String prefix2 = card.substring(0, 2) + ",";

		return ((AMEX_PREFIX.indexOf(prefix2) != -1) && (card.length() == 15));
	}

	/**
	 * Returns true if the card is Visa.
	 */
	private boolean isVisa(String card) {
		return (
			card.substring(0, 1).equals(VISA_PREFIX)
				&& (card.length() == 13 || card.length() == 16));
	}

	/**
	 * Returns true if the card is Mastercard.
	 */
	private boolean isMastercard(String card) {
		String prefix2 = card.substring(0, 2) + ",";

		return (
			(MASTERCARD_PREFIX.indexOf(prefix2) != -1)
				&& (card.length() == 16));
	}

	/**
	 * Returns true if the card is Discover.
	 */
	private boolean isDiscover(String card) {
		return (
			card.substring(0, 4).equals(DISCOVER_PREFIX)
				&& (card.length() == 16));
	}

}
