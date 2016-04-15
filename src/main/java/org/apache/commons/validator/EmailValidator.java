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

import org.apache.commons.validator.routines.InetAddressValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Perform email validations.</p>
 * <p>
 * This class is a Singleton; you can retrieve the instance via the getInstance() method.
 * </p>
 * <p>
 * Based on a script by <a href="mailto:stamhankar@hotmail.com">Sandeep V. Tamhankar</a>
 * http://javascript.internet.com
 * </p>
 * <p>
 * This implementation is not guaranteed to catch all possible errors in an email address.
 * For example, an address like nobody@noplace.somedog will pass validator, even though there
 * is no TLD "somedog"
 * </p>.
 *
 * @version $Revision$
 * @since Validator 1.1
 * @deprecated Use the new EmailValidator in the routines package. This class
 * will be removed in a future release.
 */
@Deprecated
public class EmailValidator {

    private static final String SPECIAL_CHARS = "\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]";
    private static final String VALID_CHARS = "[^\\s" + SPECIAL_CHARS + "]";
    private static final String QUOTED_USER = "(\"[^\"]*\")";
    private static final String ATOM = VALID_CHARS + '+';
    private static final String WORD = "((" + VALID_CHARS + "|')+|" + QUOTED_USER + ")";

// NOT USED   private static final Pattern LEGAL_ASCII_PATTERN = Pattern.compile("^\\p{ASCII}+$");
// NOT USED   private static final Pattern EMAIL_PATTERN = Pattern.compile("^(.+)@(.+)$");
    private static final Pattern IP_DOMAIN_PATTERN = Pattern.compile("^\\[(.*)\\]$");
    private static final Pattern TLD_PATTERN = Pattern.compile("^([a-zA-Z]+)$");
            
    private static final Pattern USER_PATTERN = Pattern.compile("^\\s*" + WORD + "(\\." + WORD + ")*$");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^" + ATOM + "(\\." + ATOM + ")*\\s*$");
    private static final Pattern ATOM_PATTERN = Pattern.compile("(" + ATOM + ")");

    /**
     * Singleton instance of this class.
     */
    private static final EmailValidator EMAIL_VALIDATOR = new EmailValidator();

    /**
     * Returns the Singleton instance of this validator.
     * @return singleton instance of this validator.
     */
    public static EmailValidator getInstance() {
        return EMAIL_VALIDATOR;
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
     * @param email The value validation is being performed on.  A <code>null</code>
     * value is considered invalid.
     * @return true if the email address is valid.
     */
    public boolean isValid(String email) {
        return org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(email);
    }

    /**
     * Returns true if the domain component of an email address is valid.
     * @param domain being validated.
     * @return true if the email address's domain is valid.
     */
    protected boolean isValidDomain(String domain) {
        boolean symbolic = false;

        // see if domain is an IP address in brackets
        Matcher ipDomainMatcher = IP_DOMAIN_PATTERN.matcher(domain);

        if (ipDomainMatcher.matches()) {
            InetAddressValidator inetAddressValidator =
                    InetAddressValidator.getInstance();
            if (inetAddressValidator.isValid(ipDomainMatcher.group(1))) {
                return true;
            }
        } else {
            // Domain is symbolic name
            symbolic = DOMAIN_PATTERN.matcher(domain).matches();
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
     * @param user being validated
     * @return true if the user name is valid.
     */
    protected boolean isValidUser(String user) {
        return USER_PATTERN.matcher(user).matches(); 
    }

    /**
     * Validates an IP address. Returns true if valid.
     * @param ipAddress IP address
     * @return true if the ip address is valid.
     */
    protected boolean isValidIpAddress(String ipAddress) {
        Matcher ipAddressMatcher = IP_DOMAIN_PATTERN.matcher(ipAddress);
        for (int i = 1; i <= 4; i++) { // CHECKSTYLE IGNORE MagicNumber
            String ipSegment = ipAddressMatcher.group(i);
            if (ipSegment == null || ipSegment.length() <= 0) {
                return false;
            }

            int iIpSegment = 0;

            try {
                iIpSegment = Integer.parseInt(ipSegment);
            } catch(NumberFormatException e) {
                return false;
            }

            if (iIpSegment > 255) { // CHECKSTYLE IGNORE MagicNumber
                return false;
            }

        }
        return true;
    }

    /**
     * Validates a symbolic domain name.  Returns true if it's valid.
     * @param domain symbolic domain name
     * @return true if the symbolic domain name is valid.
     */
    protected boolean isValidSymbolicDomain(String domain) {
        String[] domainSegment = new String[10]; // CHECKSTYLE IGNORE MagicNumber
        boolean match = true;
        int i = 0;
        Matcher atomMatcher = ATOM_PATTERN.matcher(domain);
        while (match) {
            match = atomMatcher.matches();
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
        
        // Make sure there's a host name preceding the domain.
        if (len < 2) {
            return false;
        }
        
        // TODO: the tld should be checked against some sort of configurable 
        // list
        String tld = domainSegment[len - 1];
        if (tld.length() > 1) {
            if (! TLD_PATTERN.matcher(tld).matches()) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }
    /**
     *   Recursively remove comments, and replace with a single space.  The simpler
     *   regexps in the Email Addressing FAQ are imperfect - they will miss escaped
     *   chars in atoms, for example.
     *   Derived From    Mail::RFC822::Address
     * @param emailStr The email address
     * @return address with comments removed.
    */
    protected String stripComments(String emailStr)  {
     String result = emailStr;
     String commentPat = "^((?:[^\"\\\\]|\\\\.)*(?:\"(?:[^\"\\\\]|\\\\.)*\"(?:[^\"\\\\]|\111111\\\\.)*)*)\\((?:[^()\\\\]|\\\\.)*\\)/";
     Pattern commentMatcher = Pattern.compile(commentPat);
     
     while (commentMatcher.matcher(result).matches()) {
        result = result.replaceFirst(commentPat, "\1 ");
     }
     return result;
    }
}
