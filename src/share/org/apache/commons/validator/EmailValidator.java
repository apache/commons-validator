/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/EmailValidator.java,v 1.3 2003/05/02 05:25:28 dgraham Exp $
 * $Revision: 1.3 $
 * $Date: 2003/05/02 05:25:28 $
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

import org.apache.oro.text.perl.Perl5Util;

/**
 * <p>Perform email validations.</p>
 * <p>
 * This class is a Singleton; you can retrieve the instance via the getInstance() method.
 * </p>
 * <p>
 * Based on a script by <a href="mailto:stamhankar@hotmail.com">Sandeep V. Tamhankar</a>
 * http://javascript.internet.com
 * </p>
 *
 * @author David Winterfeldt
 * @author James Turner
 * @author <a href="mailto:husted@apache.org">Ted Husted</a>
 * @author David Graham
 * @version $Revision: 1.3 $ $Date: 2003/05/02 05:25:28 $
 */
public class EmailValidator {
    
    private static final String specialChars = "\\(\\)<>@,;:\\\\\\\"\\.\\[\\]";
    private static final String validChars = "[^\\s" + specialChars + "]";
    private static final String quotedUser = "(\"[^\"]*\")";
    private static final String atom = validChars + '+';
    private static final String word = "(" + atom + "|" + quotedUser + ")";
    
    // Each pattern must be surrounded by /
    private static final String legalAsciiPat = "/^[\\000-\\177]+$/";
    private static final String emailPat = "/^(.+)@(.+)$/";
    private static final String ipDomainPat =
    	"/^(\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})$/";
        
    private static final String userPat = "/^" + word + "(\\." + word + ")*$/";
    private static final String domainPat = "/^" + atom + "(\\." + atom + ")*$/";
    private static final String atomPat = "/(" + atom + ")/";

	/**
	 * Singleton instance of this class.
	 */
	private static final EmailValidator instance = new EmailValidator();

	/**
	 * Returns the Singleton instance of this validator.
	 */
	public static EmailValidator getInstance() {
		return instance;
	}

	/**
	 * Protected constructor for subclasses to use.
	 */
	protected EmailValidator() {
		super();
	}

	/**
	 * <p>Checks if a field has a valid e-mail address.</p>
	 *
	 * @param value The value validation is being performed on.
	 */
	public boolean isValid(String value) {
		boolean symbolic = false;

		try {
			Perl5Util matchAsciiPat = new Perl5Util();
			if (!matchAsciiPat.match(legalAsciiPat, value)) {
				return false;
			}

			// Check the whole email address structure
			Perl5Util matchEmailPat = new Perl5Util();
			if (!matchEmailPat.match(emailPat, value)) {
				return false;
			}

			if (value.endsWith(".")) {
				return false;
			}

			// Check the user component of the email address

			String user = matchEmailPat.group(1);

			// See if "user" is valid
			Perl5Util matchUserPat = new Perl5Util();
			if (!matchUserPat.match(userPat, user)) {
				return false;
			}

			// Check the domain component of the email address
			String domain = matchEmailPat.group(2);

			// check if domain is IP address or symbolic
			Perl5Util matchIPPat = new Perl5Util();
			boolean ipAddress = matchIPPat.match(ipDomainPat, domain);

			if (ipAddress) {
				if (!checkIpAddress(matchIPPat)) {
					return false;
				}
			} else {
				// Domain is symbolic name
				Perl5Util matchDomainPat = new Perl5Util();
				symbolic = matchDomainPat.match(domainPat, domain);
			}

			if (symbolic) {
				if (!checkSymbolicDomain(domain)) {
					return false;
				}
			} else {
				return false;
			}

		} catch (Exception e) {
			return false;
		}

		return true;
	}

    /**
     * Validates an IP address. Returns true if valid.
     */
	private boolean checkIpAddress(Perl5Util matchIPPat) {
		for (int i = 1; i <= 4; i++) {
			String ipSegment = matchIPPat.group(i);
			if (ipSegment == null || ipSegment.length() <= 0) {
				return false;
			}
			int iIpSegment = 0;
			try {
				iIpSegment = Integer.parseInt(ipSegment);
			} catch (NumberFormatException e) {
				return false;
			}

			if (iIpSegment > 255) {
				return false;
			}

		}
		return true;
	}

    /**
     * Validates a symbolic domain name.  Returns true if it's valid.
     */
	private boolean checkSymbolicDomain(String domain) {
    	String[] domainSegment = new String[10];
    	boolean match = true;
    	int i = 0;
    	Perl5Util matchAtomPat = new Perl5Util();
    
    	while (match) {
    		match = matchAtomPat.match(atomPat, domain);
    		if (match) {
    			domainSegment[i] = matchAtomPat.group(1);
    			int l = domainSegment[i].length() + 1;
    			domain =
    				(l >= domain.length())
    					? ""
    					: domain.substring(l);
    			i++;
    		}
    	}
    
    	int len = i;
    	if (domainSegment[len - 1].length() < 2
    		|| domainSegment[len - 1].length() > 4) {
    
    		return false;
    	}
    
    	// Make sure there's a host name preceding the domain.
    	if (len < 2) {
    		return false;
    	}
        
    	return true;
	}

}
