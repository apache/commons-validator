/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/CreditCardValidator.java,v 1.5 2003/06/07 18:31:18 dgraham Exp $
 * $Revision: 1.5 $
 * $Date: 2003/06/07 18:31:18 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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

/**
 * <p>Perform credit card validations.</p>
 * <p>
 * This class is a Singleton; you can retrieve the instance via the getInstance() method.
 * </p>
 * Reference Sean M. Burke's script at
 * http://www.ling.nwu.edu/~sburke/pub/luhn_lib.pl
 *
 * @author David Winterfeldt
 * @author James Turner
 * @author <a href="mailto:husted@apache.org">Ted Husted</a>
 * @author David Graham
 * @version $Revision: 1.5 $ $Date: 2003/06/07 18:31:18 $
 */
public class CreditCardValidator {

	private static final String AMEX_PREFIX = "34,37,";
	private static final String VISA_PREFIX = "4";
	private static final String MASTERCARD_PREFIX = "51,52,53,54,55,";
	private static final String DISCOVER_PREFIX = "6011";

	/**
	 * Singleton instance of this class.
	 */
	private static final CreditCardValidator instance =
		new CreditCardValidator();

	/**
	 * Returns the Singleton instance of this validator.
	 */
	public static CreditCardValidator getInstance() {
		return instance;
	}

	/**
	 * Protected constructor for subclasses to use.
	 */
	protected CreditCardValidator() {
		super();
	}

	/**
	 * Checks if the field is a valid credit card number.
	 * @param card The card number to validate.
	 */
	public boolean isValid(String card) {
		if (card.length() < 13) {
			return false;
		}

		if (!this.luhnCheck(card)) {
			return false;
		}

		if (this.isVisa(card)
			|| this.isAmex(card)
			|| this.isMastercard(card)
			|| this.isDiscover(card)) {

			return true;
		}

		return false;
	}

	/**
	 * Checks for a valid credit card number.
	 *
	 * @param cardNumber Credit Card Number.
	 */
	protected boolean luhnCheck(String cardNumber) {
		// number must be validated as 0..9 numeric first!!
		int digits = cardNumber.length();
		int oddoeven = digits & 1;
		long sum = 0;
		for (int count = 0; count < digits; count++) {
			int digit = 0;
			try {
				digit =
					Integer.parseInt(String.valueOf(cardNumber.charAt(count)));
			} catch (NumberFormatException e) {
				return false;
			}

			if (((count & 1) ^ oddoeven) == 0) { // not
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

		if (sum % 10 == 0) {
			return true;
		}

		return false;
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
