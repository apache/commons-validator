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
package org.apache.commons.validator.routines;

import java.io.Serializable;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * <b>Regular Expression</b> validation (using JDK 1.4+ regex support).
 * <p>
 * This validator provides convenient regular expression validation
 * in one of two ways:
 *
 * <h4>1. One Off validation using the static methods</h4>
 * <ul>
 *   <li>Validate <code>true</code> or <code>false</code>:</li>
 *   <ul>
 *     <li><code>boolean valid = RegexValidator.isValid(value, regex);</code></li>
 *     <li><code>boolean valid = RegexValidator.isValid(value, regex, caseSensitive);</code></li>
 *   </ul>
 *   <li>Validate returning an aggregated String of the matched groups:</li>
 *   <ul>
 *     <li><code>String result = RegexValidator.validate(value, regex);</code></li>
 *     <li><code>String result = RegexValidator.validate(value, regex, caseSensitive);</code></li>
 *   </ul>
 *   <li>Validate returning the matched groups:</li>
 *   <ul>
 *     <li><code>String[] result = RegexValidator.match(value, regex);</code></li>
 *     <li><code>String[] result = RegexValidator.match(value, regex, caseSensitive);</code></li>
 *   </ul>
 * </ul>
 *
 * <h4>2. Re-using cached instances validating against one or more regular expression</h4>
 * Construct the validator either for a single regular expression or a set (array) of 
 * regular expressions. By default validation is <i>case sensitive</i> but constructors
 * are provided to allow  <i>case in-sensitive</i> validation. For example to create
 * a validator which does <i>case in-sensitive</i> validation for a set of regular
 * expressions:
 * <pre>
 *         String[] regexs = new String[] {...};
 *         RegexValidator validator = new RegexValidator(regexs, false);
 * </pre>
 * <p>
 * <ul>
 *   <li>Validate <code>true</code> or <code>false</code>:</li>
 *   <ul>
 *     <li><code>boolean valid = validator.isValid(value);</code></li>
 *   </ul>
 *   <li>Validate returning an aggregated String of the matched groups:</li>
 *   <ul>
 *     <li><code>String result = validator.validate(value);</code></li>
 *   </ul>
 *   <li>Validate returning the matched groups:</li>
 *   <ul>
 *     <li><code>String[] result = validator.match(value);</code></li>
 *   </ul>
 * </ul>
 * <p>
 * Cached instances pre-compile and re-use {@link Pattern}(s) - which according
 * to the {@link Pattern} API are safe to use in a multi-threaded environment.
 *
 * @version $Revision$ $Date$
 * @since Validator 1.4
 */
public final class RegexValidator implements Serializable {

    private static final String MISSING_REGEX = "Regular Expression is missing";
    private Pattern   pattern;
    private Pattern[] patterns;

    /**
     * Construct a <i>case sensitive</i> validator for a single
     * regular expression.
     *
     * @param regex The regular expression this validator will
     * validate against
     */
    public RegexValidator(String regex) {
        this(regex, 0);
    }

    /**
     * Construct a validator for a single regular expression
     * with the specified case sensitivity.
     *
     * @param regex The regular expression this validator will
     * validate against
     * @param caseSensitive when <code>true</code> matching is <i>case
     * sensitive</i>, otherwise matching is <i>case in-sensitive</i>
     */
    public RegexValidator(String regex, boolean caseSensitive) {
        this(regex, (caseSensitive ? 0 : Pattern.CASE_INSENSITIVE));
    }

    /**
     * Construct a validator for a single regular expression with
     * the specified flags.
     *
     * @param regex The regular expression this validator will
     * validate against
     * @param flags Bit mask of matching flags - see {@link Pattern} for values
     */
    private RegexValidator(String regex, int flags) {
        if (regex == null || regex.length() == 0) {
            throw new IllegalArgumentException(MISSING_REGEX);
        }
        pattern = Pattern.compile(regex, flags);
    }

    /**
     * Construct a <i>case sensitive</i> validator for a set
     * of regular expressions.
     *
     * @param regexs The set of regular expressions this validator will
     * validate against
     */
    public RegexValidator(String[] regexs) {
        this(regexs, 0);
    }

    /**
     * Construct a validator for a set of regular expressions
     * with the specified case sensitivity.
     *
     * @param regexs The set of regular expressions this validator will
     * validate against
     * @param caseSensitive when <code>true</code> matching is <i>case
     * sensitive</i>, otherwise matching is <i>case in-sensitive</i>
     */
    public RegexValidator(String[] regexs, boolean caseSensitive) {
        this(regexs, (caseSensitive ? 0: Pattern.CASE_INSENSITIVE));
    }

    /**
     * Construct a validator for a set of regular expressions
     * the specified flags.
     *
     * @param regexs The set of regular expressions this validator will
     * validate against
     * @param flags Bit mask of matching flags - see {@link Pattern} for values
     */
    private RegexValidator(String[] regexs, int flags) {
        if (regexs == null || regexs.length == 0) {
            throw new IllegalArgumentException("Regular expressions are missing");
        }
        patterns = new Pattern[regexs.length];
        for (int i = 0; i < regexs.length; i++) {
            if (regexs[i] == null || regexs[i].length() == 0) {
                throw new IllegalArgumentException("Regular expression[" + i + "] is missing");
            }
            patterns[i] =  Pattern.compile(regexs[i], flags);
        }
    }

    /**
     * Validate a value against the set of regular expressions.
     *
     * @param value The value to validate.
     * @return <code>true</code> if the value is valid 
     * otherwise <code>false</code>.
     */
    public boolean isValid(String value) {
        if (value == null) {
            return false;
        } else if (pattern != null) {
            return pattern.matcher(value).matches();
        } else {
            for (int i = 0; i < patterns.length; i++) {
                if (patterns[i].matcher(value).matches()) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Validate a value against a regular expression
     * (<i>case sensitive</i>).
     *
     * @param value Value to validate
     * @param regex The regular expression to validate against.
     * @return <code>true</code> if the value is valid 
     * otherwise <code>false</code>.
     */
    public static boolean isValid(String value, String regex) {
        return RegexValidator.isValid(value, regex, 0);
    }

    /**
     * Validate a value against a regular expression
     * with the specified case sensitivity.
     *
     * @param value Value to validate
     * @param regex The regular expression to validate against.
     * @param caseSensitive when <code>true</code> matching is <i>case
     * sensitive</i>, otherwise matching is <i>case in-sensitive</i>
     * @return <code>true</code> if the value is valid 
     * otherwise <code>false</code>.
     */
    public static boolean isValid(String value, String regex, boolean caseSensitive) {
        int flags = caseSensitive ?  0 : Pattern.CASE_INSENSITIVE;
        return RegexValidator.isValid(value, regex, flags);
    }

    /**
     * Validate a value against a regular expression with the
     * specified flags.
     *
     * @param value Value to validate
     * @param regex The regular expression to validate against.
     * @param flags Bit mask of matching flags - see {@link Pattern} for values
     * @return <code>true</code> if the value is valid 
     * otherwise <code>false</code>.
     */
    private static boolean isValid(String value, String regex, int flags) {
        if (regex == null || regex.length() == 0) {
            throw new IllegalArgumentException(MISSING_REGEX);
        }
        if (value == null) {
            return false;
        } else {
            return Pattern.compile(regex, flags).matcher(value).matches();
        }
    }

    /**
     * Validate a value against the set of regular expressions
     * returning the array of matched groups.
     *
     * @param value The value to validate.
     * @return String array of the <i>groups</i> matched if
     * valid or <code>null</code> if invalid 
     */
    public String[] match(String value) {
        if (value == null) {
            return null;
        } else if (pattern != null) {
            return RegexValidator.match(value, pattern);
        } else {
            String[] result = null;
            for (int i = 0; i < patterns.length; i++) {
                result = RegexValidator.match(value, patterns[i]);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }
    }

    /**
     * Validate a value against the specified regular expression
     * returning the matched groups (<i>case sensitive</i>).
     *
     * @param value Value to validate
     * @param regex The regular expression to validate against.
     * @return String array of the <i>groups</i> matched if
     * valid or <code>null</code> if invalid 
     */
    public static String[] match(String value, String regex) {
        return RegexValidator.match(value, regex, 0);
    }

    /**
     * Validate a value against a regular expression with the
     * specified <i>case sensitivity</i> returning the matched groups.
     *
     * @param value Value to validate
     * @param regex The regular expression to validate against.
     * @param caseSensitive when <code>true</code> matching is <i>case
     * sensitive</i>, otherwise matching is <i>case in-sensitive</i>
     * @return String array of the <i>groups</i> matched if
     * valid or <code>null</code> if invalid 
     */
    public static String[] match(String value, String regex, boolean caseSensitive) {
        int flags = caseSensitive ?  0 : Pattern.CASE_INSENSITIVE;
        return RegexValidator.match(value, regex, flags);
    }

    /**
     * Validate a value against a regular expression with the
     * specified flags returning the matched groups.
     *
     * @param value Value to validate
     * @param regex The regular expression to validate against.
     * @param caseSensitive when <code>true</code> matching is <i>case
     * sensitive</i>, otherwise matching is <i>case in-sensitive</i>
     * @return String array of the <i>groups</i> matched if
     * valid or <code>null</code> if invalid 
     */
    private static String[] match(String value, String regex, int flags) {
        if (regex == null || regex.length() == 0) {
            throw new IllegalArgumentException(MISSING_REGEX);
        }
        if (value == null) {
            return null;
        }
        return RegexValidator.match(value, Pattern.compile(regex, flags));
    }

    /**
     * Validate a value against the specified pattern
     * returning the matched groups.
     *
     * @param value Value to validate
     * @param pattern The pattern to match against.
     * @return String array of the <i>groups</i> matched if
     * valid or <code>null</code> if invalid 
     */
    private static String[] match(String value, Pattern pattern) {
        Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            int count = matcher.groupCount();
            String[] groups = new String[count];
            for (int i = 0; i < count; i++) {
                groups[i] = matcher.group(i+1);
            }
            return groups;
        } else {
            return null;
        }
    }

    /**
     * Validate a value against the set of regular expressions
     * returning a String value of the aggregated groups.
     *
     * @param value The value to validate.
     * @return Aggregated String value comprised of the
     * <i>groups</i> matched if valid or <code>null</code> if invalid
     */
    public String validate(String value) {
        if (value == null) {
            return null;
        } else if (pattern != null) {
            return RegexValidator.validate(value, pattern);
        } else {
            String result = null;
            for (int i = 0; i < patterns.length; i++) {
                result = RegexValidator.validate(value, patterns[i]);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }
    }

    /**
     * Validate a value against the specified regular expression
     * returning a String value of the aggregated groups
     * (<i>case sensitive</i>).
     *
     * @param value Value to validate
     * @param regex The regular expression to validate against.
     * @return Aggregated String value comprised of the
     * <i>groups</i> matched if valid or <code>null</code> if invalid
     */
    public static String validate(String value, String regex) {
        return RegexValidator.validate(value, regex, 0);
    }

    /**
     * Validate a value against a regular expression with the
     * specified <i>case sensitivity</i> returning a String
     * value of the aggregated groups.
     *
     * @param value Value to validate
     * @param regex The regular expression to validate against.
     * @param caseSensitive when <code>true</code> matching is <i>case
     * sensitive</i>, otherwise matching is <i>case in-sensitive</i>
     * @return Aggregated String value comprised of the
     * <i>groups</i> matched if valid or <code>null</code> if invalid
     */
    public static String validate(String value, String regex, boolean caseSensitive) {
        int flags = caseSensitive ?  0 : Pattern.CASE_INSENSITIVE;
        return RegexValidator.validate(value, regex, flags);
    }

    /**
     * Validate a value against a regular expression with the
     * specified flags returning a String value of the aggregated
     * groups.
     *
     * @param value Value to validate
     * @param regex The regular expression to validate against.
     * @param flags Bit mask of matching flags - see {@link Pattern} for values
     * @return Aggregated String value comprised of the
     * <i>groups</i> matched if valid or <code>null</code> if invalid
     */
    private static String validate(String value, String regex, int flags) {
        if (regex == null || regex.length() == 0) {
            throw new IllegalArgumentException(MISSING_REGEX);
        }
        if (value == null) {
            return null;
        }
        return RegexValidator.validate(value, Pattern.compile(regex, flags));
    }

    /**
     * Validate a value against the specified pattern
     * returning a String value of the aggregated groups.
     *
     * @param value Value to validate
     * @param pattern The pattern to validate against.
     * @return Aggregated String value comprised of the
     * <i>groups</i> matched if valid or <code>null</code> if invalid
     */
    private static String validate(String value, Pattern pattern) {
        Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            int count = matcher.groupCount();
            if (count == 1) {
                return matcher.group(1);
            } else {
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < count; i++) {
                    String component = matcher.group(i+1);
                    if (component != null) {
                        buffer.append(component);
                    }
                }
                return buffer.toString();
            }
        } else {
            return null;
        }
    }

    /**
     * Provide a String representation of this validator.
     * @return A String representation of this validator
     */
    public String toString() {
        if (pattern != null) {
            return "RegexValidator{" + pattern.pattern() + "}";
        } else {
            return "RegexValidator[" + patterns.length + "]";
        }
    }

}
