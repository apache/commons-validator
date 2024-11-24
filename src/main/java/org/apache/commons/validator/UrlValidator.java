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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.util.Flags;

/**
 * <p>Validates URLs.</p>
 * Behaviour of validation is modified by passing in options:
 * <ul>
 * <li>ALLOW_2_SLASHES - [FALSE]  Allows double '/' characters in the path
 * component.</li>
 * <li>NO_FRAGMENT- [FALSE]  By default fragments are allowed, if this option is
 * included then fragments are flagged as illegal.</li>
 * <li>ALLOW_ALL_SCHEMES - [FALSE] By default only http, https, and ftp are
 * considered valid schemes.  Enabling this option will let any scheme pass validation.</li>
 * </ul>
 *
 * <p>Originally based in on php script by Debbie Dyer, validation.php v1.2b, Date: 03/07/02,
 * https://javascript.internet.com. However, this validation now bears little resemblance
 * to the php original.</p>
 * <pre>
 *   Example of usage:
 *   Construct a UrlValidator with valid schemes of "http", and "https".
 *
 *    String[] schemes = {"http","https"}.
 *    UrlValidator urlValidator = new UrlValidator(schemes);
 *    if (urlValidator.isValid("ftp://foo.bar.com/")) {
 *       System.out.println("URL is valid");
 *    } else {
 *       System.out.println("URL is invalid");
 *    }
 *
 *    prints "URL is invalid"
 *   If instead the default constructor is used.
 *
 *    UrlValidator urlValidator = new UrlValidator();
 *    if (urlValidator.isValid("ftp://foo.bar.com/")) {
 *       System.out.println("URL is valid");
 *    } else {
 *       System.out.println("URL is invalid");
 *    }
 *
 *   prints out "URL is valid"
 *  </pre>
 *
 * @see
 * <a href="http://www.ietf.org/rfc/rfc2396.txt">
 *  Uniform Resource Identifiers (URI): Generic Syntax
 * </a>
 *
 * @since 1.1
 * @deprecated Use the new UrlValidator in the routines package. This class
 * will be removed in a future release.
 */
@Deprecated
public class UrlValidator implements Serializable {

    private static final long serialVersionUID = 24137157400029593L;

    /**
     * Allows all validly formatted schemes to pass validation instead of
     * supplying a set of valid schemes.
     */
    public static final int ALLOW_ALL_SCHEMES = 1 << 0;

    /**
     * Allow two slashes in the path component of the URL.
     */
    public static final int ALLOW_2_SLASHES = 1 << 1;

    /**
     * Enabling this options disallows any URL fragments.
     */
    public static final int NO_FRAGMENTS = 1 << 2;

    private static final String ALPHA_CHARS = "a-zA-Z";

// NOT USED   private static final String ALPHA_NUMERIC_CHARS = ALPHA_CHARS + "\\d";

    private static final String SPECIAL_CHARS = ";/@&=,.?:+$";

    private static final String VALID_CHARS = "[^\\s" + SPECIAL_CHARS + "]";

    // Drop numeric, and  "+-." for now
    private static final String AUTHORITY_CHARS_REGEX = "\\p{Alnum}\\-\\.";

    private static final String ATOM = VALID_CHARS + '+';

    /**
     * This expression derived/taken from the BNF for URI (RFC2396).
     */
    private static final String URL_REGEX =
            "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?";
    //                                                                      12            3  4          5       6   7        8 9
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    /**
     * Schema/Protocol (ie. http:, ftp:, file:, etc).
     */
    private static final int PARSE_URL_SCHEME = 2;

    /**
     * Includes hostname/ip and port number.
     */
    private static final int PARSE_URL_AUTHORITY = 4;

    private static final int PARSE_URL_PATH = 5;

    private static final int PARSE_URL_QUERY = 7;

    private static final int PARSE_URL_FRAGMENT = 9;

    /**
     * Protocol (ie. http:, ftp:,https:).
     */
    private static final Pattern SCHEME_PATTERN = Pattern.compile("^\\p{Alpha}[\\p{Alnum}\\+\\-\\.]*");

    private static final String AUTHORITY_REGEX =
       "^([" + AUTHORITY_CHARS_REGEX + "]*)(:\\d*)?(.*)?";
    //                                                                            1                          2  3       4
    private static final Pattern AUTHORITY_PATTERN = Pattern.compile(AUTHORITY_REGEX);

    private static final int PARSE_AUTHORITY_HOST_IP = 1;

    private static final int PARSE_AUTHORITY_PORT = 2;

    /**
     * Should always be empty.
     */
    private static final int PARSE_AUTHORITY_EXTRA = 3;

    private static final Pattern PATH_PATTERN = Pattern.compile("^(/[-\\w:@&?=+,.!/~*'%$_;]*)?$");

    private static final Pattern QUERY_PATTERN = Pattern.compile("^(.*)$");

    private static final Pattern LEGAL_ASCII_PATTERN = Pattern.compile("^\\p{ASCII}+$");

    private static final Pattern DOMAIN_PATTERN =
            Pattern.compile("^" + ATOM + "(\\." + ATOM + ")*$");

    private static final Pattern PORT_PATTERN = Pattern.compile("^:(\\d{1,5})$");

    private static final Pattern ATOM_PATTERN = Pattern.compile("^(" + ATOM + ").*?$");

    private static final Pattern ALPHA_PATTERN = Pattern.compile("^[" + ALPHA_CHARS + "]");

    /**
     * Holds the set of current validation options.
     */
    private final Flags options;

    /**
     * The set of schemes that are allowed to be in a URL.
     */
    private final Set<String> allowedSchemes = new HashSet<>();

    /**
     * If no schemes are provided, default to this set.
     */
    protected String[] defaultSchemes = {"http", "https", "ftp"};

    /**
     * Create a UrlValidator with default properties.
     */
    public UrlValidator() {
        this(null);
    }

    /**
     * Initialize a UrlValidator with the given validation options.
     * @param options The options should be set using the public constants declared in
     * this class.  To set multiple options you simply add them together.  For example,
     * ALLOW_2_SLASHES + NO_FRAGMENTS enables both of those options.
     */
    public UrlValidator(final int options) {
        this(null, options);
    }

    /**
     * Behavior of validation is modified by passing in several strings options:
     * @param schemes Pass in one or more URL schemes to consider valid, passing in
     *        a null will default to "http,https,ftp" being valid.
     *        If a non-null schemes is specified then all valid schemes must
     *        be specified. Setting the ALLOW_ALL_SCHEMES option will
     *        ignore the contents of schemes.
     */
    public UrlValidator(final String[] schemes) {
        this(schemes, 0);
    }

    /**
     * Behaviour of validation is modified by passing in options:
     * @param schemes The set of valid schemes.
     * @param options The options should be set using the public constants declared in
     * this class.  To set multiple options you simply add them together.  For example,
     * ALLOW_2_SLASHES + NO_FRAGMENTS enables both of those options.
     */
    public UrlValidator(String[] schemes, final int options) {
        this.options = new Flags(options);

        if (this.options.isOn(ALLOW_ALL_SCHEMES)) {
            return;
        }

        if (schemes == null) {
            schemes = defaultSchemes;
        }

        allowedSchemes.addAll(Arrays.asList(schemes));
    }

    /**
     * Returns the number of times the token appears in the target.
     * @param token Token value to be counted.
     * @param target Target value to count tokens in.
     * @return the number of tokens.
     */
    protected int countToken(final String token, final String target) {
        int tokenIndex = 0;
        int count = 0;
        while (tokenIndex != -1) {
            tokenIndex = target.indexOf(token, tokenIndex);
            if (tokenIndex > -1) {
                tokenIndex++;
                count++;
            }
        }
        return count;
    }

    /**
     * <p>Checks if a field has a valid URL address.</p>
     *
     * @param value The value validation is being performed on.  A {@code null}
     * value is considered invalid.
     * @return true if the URL is valid.
     */
    public boolean isValid(final String value) {
        if (value == null) {
            return false;
        }
        if (!LEGAL_ASCII_PATTERN.matcher(value).matches()) {
           return false;
        }

        // Check the whole url address structure
        final Matcher urlMatcher = URL_PATTERN.matcher(value);
        if (!urlMatcher.matches()) {
            return false;
        }

        if (!isValidScheme(urlMatcher.group(PARSE_URL_SCHEME))) {
            return false;
        }

        if (!isValidAuthority(urlMatcher.group(PARSE_URL_AUTHORITY))) {
            return false;
        }

        if (!isValidPath(urlMatcher.group(PARSE_URL_PATH))) {
            return false;
        }

        if (!isValidQuery(urlMatcher.group(PARSE_URL_QUERY))) {
            return false;
        }

        if (!isValidFragment(urlMatcher.group(PARSE_URL_FRAGMENT))) {
            return false;
        }

        return true;
    }

    /**
     * Returns true if the authority is properly formatted.  An authority is the combination
     * of hostname and port.  A {@code null} authority value is considered invalid.
     * @param authority Authority value to validate.
     * @return true if authority (hostname and port) is valid.
     */
    protected boolean isValidAuthority(final String authority) {
        if (authority == null) {
            return false;
        }

        final InetAddressValidator inetAddressValidator =
                InetAddressValidator.getInstance();

        final Matcher authorityMatcher = AUTHORITY_PATTERN.matcher(authority);
        if (!authorityMatcher.matches()) {
            return false;
        }

        boolean hostname = false;
        // check if authority is IP address or hostname
        String hostIP = authorityMatcher.group(PARSE_AUTHORITY_HOST_IP);
        final boolean ipV4Address = inetAddressValidator.isValid(hostIP);

        if (!ipV4Address) {
            // Domain is hostname name
            hostname = DOMAIN_PATTERN.matcher(hostIP).matches();
        }

        //rightmost hostname will never start with a digit.
        if (hostname) {
            // LOW-TECH FIX FOR VALIDATOR-202
            // TODO: Rewrite to use ArrayList and .add semantics: see VALIDATOR-203
            final char[] chars = hostIP.toCharArray();
            int size = 1;
            for (final char element : chars) {
                if (element == '.') {
                    size++;
                }
            }
            final String[] domainSegment = new String[size];
            boolean match = true;
            int segmentCount = 0;
            int segmentLength = 0;

            while (match) {
                final Matcher atomMatcher = ATOM_PATTERN.matcher(hostIP);
                match = atomMatcher.matches();
                if (match) {
                    domainSegment[segmentCount] = atomMatcher.group(1);
                    segmentLength = domainSegment[segmentCount].length() + 1;
                    hostIP =
                            segmentLength >= hostIP.length()
                            ? ""
                            : hostIP.substring(segmentLength);

                    segmentCount++;
                }
            }
            final String topLevel = domainSegment[segmentCount - 1];
            if (topLevel.length() < 2 || topLevel.length() > 4) { // CHECKSTYLE IGNORE MagicNumber (deprecated code)
                return false;
            }

            // First letter of top level must be a alpha
            if (!ALPHA_PATTERN.matcher(topLevel.substring(0, 1)).matches()) {
                return false;
            }

            // Make sure there's a host name preceding the authority.
            if (segmentCount < 2) {
                return false;
            }
        }

        if (!hostname && !ipV4Address) {
            return false;
        }

        final String port = authorityMatcher.group(PARSE_AUTHORITY_PORT);
        if (port != null && !PORT_PATTERN.matcher(port).matches()) {
            return false;
        }

        final String extra = authorityMatcher.group(PARSE_AUTHORITY_EXTRA);
        if (!GenericValidator.isBlankOrNull(extra)) {
            return false;
        }

        return true;
    }

    /**
     * Returns true if the given fragment is null or fragments are allowed.
     * @param fragment Fragment value to validate.
     * @return true if fragment is valid.
     */
    protected boolean isValidFragment(final String fragment) {
        if (fragment == null) {
            return true;
        }

        return options.isOff(NO_FRAGMENTS);
    }

    /**
     * Returns true if the path is valid.  A {@code null} value is considered invalid.
     * @param path Path value to validate.
     * @return true if path is valid.
     */
    protected boolean isValidPath(final String path) {
        if (path == null) {
            return false;
        }

        if (!PATH_PATTERN.matcher(path).matches()) {
            return false;
        }

        final int slash2Count = countToken("//", path);
        if (options.isOff(ALLOW_2_SLASHES) && slash2Count > 0) {
            return false;
        }

        final int slashCount = countToken("/", path);
        final int dot2Count = countToken("..", path);
        if (dot2Count > 0 && slashCount - slash2Count - 1 <= dot2Count) {
            return false;
        }

        return true;
    }

    /**
     * Returns true if the query is null or it's a properly formatted query string.
     * @param query Query value to validate.
     * @return true if query is valid.
     */
    protected boolean isValidQuery(final String query) {
        if (query == null) {
            return true;
        }

        return QUERY_PATTERN.matcher(query).matches();
    }

    /**
     * Validate scheme. If schemes[] was initialized to a non null,
     * then only those scheme's are allowed.  Note this is slightly different
     * than for the constructor.
     * @param scheme The scheme to validate.  A {@code null} value is considered
     * invalid.
     * @return true if valid.
     */
    protected boolean isValidScheme(final String scheme) {
        if (scheme == null) {
            return false;
        }

        if (!SCHEME_PATTERN.matcher(scheme).matches()) {
            return false;
        }

        if (options.isOff(ALLOW_ALL_SCHEMES) && !allowedSchemes.contains(scheme)) {
            return false;
        }

        return true;
    }
}
