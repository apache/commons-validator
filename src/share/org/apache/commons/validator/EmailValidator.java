/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/EmailValidator.java,v 1.6 2003/05/03 21:21:04 dgraham Exp $
 * $Revision: 1.6 $
 * $Date: 2003/05/03 21:21:04 $
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
 * @version $Revision: 1.6 $ $Date: 2003/05/03 21:21:04 $
 */
public class EmailValidator {
    
    private static final String SPECIAL_CHARS = "\\(\\)<>@,;:\\\\\\\"\\.\\[\\]";
    private static final String VALID_CHARS = "[^\\s" + SPECIAL_CHARS + "]";
    private static final String QUOTED_USER = "(\"[^\"]*\")";
    private static final String ATOM = VALID_CHARS + '+';
    private static final String WORD = "(" + ATOM + "|" + QUOTED_USER + ")";
    
    // Each pattern must be surrounded by /
    private static final String LEGAL_ASCII_PATTERN = "/^[\\000-\\177]+$/";
    private static final String EMAIL_PATTERN = "/^(.+)@(.+)$/";
    private static final String IP_DOMAIN_PATTERN =
    	"/^(\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})$/";
        
    private static final String USER_PATTERN = "/^" + WORD + "(\\." + WORD + ")*$/";
    private static final String DOMAIN_PATTERN = "/^" + ATOM + "(\\." + ATOM + ")*$/";
    private static final String ATOM_PATTERN = "/(" + ATOM + ")/";

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
	 * @param value The value validation is being performed on.  A <code>null</code>
     * value is considered invalid.
	 */
	public boolean isValid(String email) {
		if (email == null) {
			return false;
		}
        
		Perl5Util matchAsciiPat = new Perl5Util();
		if (!matchAsciiPat.match(LEGAL_ASCII_PATTERN, email)) {
			return false;
		}

		// Check the whole email address structure
		Perl5Util emailMatcher = new Perl5Util();
		if (!emailMatcher.match(EMAIL_PATTERN, email)) {
			return false;
		}

		if (email.endsWith(".")) {
			return false;
		}

        if (!isValidUser(emailMatcher.group(1))) {
        	return false;
        }
        
        if (!isValidDomain(emailMatcher.group(2))) {
        	return false;
        }

		return true;
	}

    /**
     * Returns true if the domain component of an email address is valid.
     */
	private boolean isValidDomain(String domain) {
        boolean symbolic = false;
		Perl5Util ipAddressMatcher = new Perl5Util();
		
		if (ipAddressMatcher.match(IP_DOMAIN_PATTERN, domain)) {
			if (!isValidIpAddress(ipAddressMatcher)) {
				return false;
			}
		} else {
			// Domain is symbolic name
			Perl5Util domainMatcher = new Perl5Util();
			symbolic = domainMatcher.match(DOMAIN_PATTERN, domain);
		}
		
		if (symbolic) {
			if (!isValidSymbolicDomain(domain)) {
				return false;
			}
		} else {
			return false;
		}
        
		return true;
	}

    /**
     * Returns true if the user component of an email address is valid.
     */
	private boolean isValidUser(String user) {
		Perl5Util userMatcher = new Perl5Util();
		return userMatcher.match(USER_PATTERN, user);
	}

    /**
     * Validates an IP address. Returns true if valid.
     */
	private boolean isValidIpAddress(Perl5Util ipAddressMatcher) {
		for (int i = 1; i <= 4; i++) {
			String ipSegment = ipAddressMatcher.group(i);
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
	private boolean isValidSymbolicDomain(String domain) {
    	String[] domainSegment = new String[10];
    	boolean match = true;
    	int i = 0;
    	Perl5Util atomMatcher = new Perl5Util();
    
    	while (match) {
    		match = atomMatcher.match(ATOM_PATTERN, domain);
    		if (match) {
    			domainSegment[i] = atomMatcher.group(1);
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
