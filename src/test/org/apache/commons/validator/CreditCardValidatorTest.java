/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/CreditCardValidatorTest.java,v 1.5 2004/01/17 19:22:02 dgraham Exp $
 * $Revision: 1.5 $
 * $Date: 2004/01/17 19:22:02 $
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

import junit.framework.TestCase;

/**
 * Test the CreditCardValidator class.
 */
public class CreditCardValidatorTest extends TestCase {
    
    private static final String VALID_VISA = "4417123456789113";
    private static final String VALID_SHORT_VISA = "4222222222222";
    private static final String VALID_AMEX = "378282246310005";
    private static final String VALID_MASTERCARD = "5105105105105100";
    private static final String VALID_DISCOVER = "6011000990139424";
    private static final String VALID_DINERS = "30569309025904";

	/**
	 * Constructor for CreditCardValidatorTest.
	 */
	public CreditCardValidatorTest(String name) {
		super(name);
	}

	public void testIsValid() {
        CreditCardValidator ccv = new CreditCardValidator();
        
        assertFalse(ccv.isValid(null));
        assertFalse(ccv.isValid(""));
        assertFalse(ccv.isValid("123456789012"));   // too short
        assertFalse(ccv.isValid("12345678901234567890"));   // too long
        assertFalse(ccv.isValid("4417123456789112"));
        assertFalse(ccv.isValid("4417q23456w89113"));
        assertTrue(ccv.isValid(VALID_VISA));
        assertTrue(ccv.isValid(VALID_SHORT_VISA));
        assertTrue(ccv.isValid(VALID_AMEX));
        assertTrue(ccv.isValid(VALID_MASTERCARD));
        assertTrue(ccv.isValid(VALID_DISCOVER));
        
        // disallow Visa so it should fail even with good number
        ccv = new CreditCardValidator(CreditCardValidator.AMEX);
        assertFalse(ccv.isValid("4417123456789113"));
	}
    
    public void testAddAllowedCardType() {
        CreditCardValidator ccv = new CreditCardValidator(CreditCardValidator.NONE);
        // Turned off all cards so even valid numbers should fail
        assertFalse(ccv.isValid(VALID_VISA));
        assertFalse(ccv.isValid(VALID_AMEX));
        assertFalse(ccv.isValid(VALID_MASTERCARD));
        assertFalse(ccv.isValid(VALID_DISCOVER));
        
        // test our custom type
        ccv.addAllowedCardType(new DinersClub());
        assertTrue(ccv.isValid(VALID_DINERS));
    }
    
    /**
     * Test a custom implementation of CreditCardType.
     */
    private class DinersClub implements CreditCardValidator.CreditCardType {
        private static final String PREFIX = "300,301,302,303,304,305,";
        public boolean matches(String card) {
            String prefix = card.substring(0, 3) + ",";
            return ((PREFIX.indexOf(prefix) != -1) && (card.length() == 14));
        }
    }

}
