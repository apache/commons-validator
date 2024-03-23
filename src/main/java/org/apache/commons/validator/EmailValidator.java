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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.InetAddressValidator;

/**
 * <p>Perform email validations.</p>
 * <p>
 * This class is a Singleton; you can retrieve the instance via the getInstance() method.
 * </p>
 * <p>
 * Based on a script by <a href="mailto:stamhankar@hotmail.com">Sandeep V. Tamhankar</a>
 * https://javascript.internet.com
 * </p>
 * <p>
 * This implementation is not guaranteed to catch all possible errors in an email address.
 * For example, an address like nobody@noplace.somedog will pass validator, even though there
 * is no TLD "somedog"
 * </p>.
 *
 * @since 1.1
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
    }

    /**
     * <p>Checks if a field has a valid e-mail address.</p>
     *
     * @param email The value validation is being performed on.  A {@code null}
     * value is considered invalid.
     * @return true if the email address is valid.
     */
    public boolean isValid(final String email) {
        return org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(email);
    }

    /**
     * Returns true if the domain component of an email address is valid.
     * @param domain being validated.
     * @return true if the email address's domain is valid.
     */
    protected boolean isValidDomain(final String domain) {
        boolean symbolic = false;

        // see if domain is an IP address in brackets
        final Matcher ipDomainMatcher = IP_DOMAIN_PATTERN.matcher(domain);

        if (ipDomainMatcher.matches()) {
            final InetAddressValidator inetAddressValidator =
                    InetAddressValidator.getInstance();
            if (inetAddressValidator.isValid(ipDomainMatcher.group(1))) {
                return true;
            }
        } else {
            // Domain is symbolic name
            symbolic = DOMAIN_PATTERN.matcher(domain).matches();
        }

        if (!symbolic) {
            return false;
        }
        if (!isValidSymbolicDomain(domain)) {
            return false;
        }

        return true;
    }

    /**
     * Validates an IP address. Returns true if valid.
     * @param ipAddress IP address
     * @return true if the ip address is valid.
     */
    protected boolean isValidIpAddress(final String ipAddress) {
        final Matcher ipAddressMatcher = IP_DOMAIN_PATTERN.matcher(ipAddress);
        for (int i = 1; i <= 4; i++) { // CHECKSTYLE IGNORE MagicNumber
            final String ipSegment = ipAddressMatcher.group(i);
            if (ipSegment == null || ipSegment.isEmpty()) {
                return false;
            }

            int iIpSegment = 0;

            try {
                iIpSegment = Integer.parseInt(ipSegment);
            } catch (final NumberFormatException e) {
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
        final String[] domainSegment = new String[10]; // CHECKSTYLE IGNORE MagicNumber
        boolean match = true;
        int i = 0;
        final Matcher atomMatcher = ATOM_PATTERN.matcher(domain);
        while (match) {
            match = atomMatcher.matches();
            if (match) {
                domainSegment[i] = atomMatcher.group(1);
                final int l = domainSegment[i].length() + 1;
                domain =
                        l >= domain.length()
                        ? ""
                        : domain.substring(l);

                i++;
            }
        }

        final int len = i;

        // Make sure there's a host name preceding the domain.
        if (len < 2) {
            return false;
        }

        final String tld = domainSegment[len - 1];
        if (tld.length() <= 1) {
            return false;
        }
        if (! TLD_PATTERN.matcher(tld).matches()) {
            return false;
        }

        return true;
    }

    /**
     * Returns true if the user component of an email address is valid.
     * @param user being validated
     * @return true if the user name is valid.
     */
    protected boolean isValidUser(final String user) {
        return USER_PATTERN.matcher(user).matches();
    }

    /**
     * Recursively remove comments, and replace with a single space. The simpler regexps in the Email Addressing FAQ are imperfect - they will miss escaped
     * chars in atoms, for example. Derived From Mail::RFC822::Address
     *
     * @param emailStr The email address
     * @return address with comments removed.
     */
    protected String stripComments(final String emailStr) {
        String result = emailStr;
        final String commentPat = "^((?:[^\"\\\\]|\\\\.)*(?:\"(?:[^\"\\\\]|\\\\.)*\"(?:[^\"\\\\]|\111111\\\\.)*)*)\\((?:[^()\\\\]|\\\\.)*\\)/";
        final Pattern commentMatcher = Pattern.compile(commentPat);

        while (commentMatcher.matcher(result).matches()) {
            result = result.replaceFirst(commentPat, "\1 ");
        }
        return result;
    }
}
