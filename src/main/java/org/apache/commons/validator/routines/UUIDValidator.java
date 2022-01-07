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

/**
 * <b>Universally unique identifier (UUID)</b> Code Validation.
 * <p>
 * This validator validates the input represents a valid UUID textual
 * representation versions 1 through 5, variant 2
 * <p>
 * The {@code validate()} method returns the UUID in lower-case
 * characters if valid or {@code null} if invalid.
 *
 * <ul>
 *   <li><a href="https://en.wikipedia.org/wiki/Universally_unique_identifier">
 *     Wikipedia - Universally unique identifier (UUID)</a></li>
 *   <li><a href="https://datatracker.ietf.org/doc/html/rfc4122">RFC 4122 -
 *   A Universally Unique IDentifier (UUID) URN Namespace</a></li>
 * </ul>
 *
 * @version $Revision$
 * @since Validator 1.8
 */
public class UUIDValidator implements Serializable {

    private static final long serialVersionUID = -3955609566771929194L;

    /**
     * The UUID textual representation length
     */
    private static final int UUID_LEN = 36;

    /**
     * The lengths of each group (divided by dashes)
     */
    private static final int[] GROUP_LENGTHS = {8, 4, 4, 4, 12};

    /**
     * UUID Code Validator with default settings
     */
    private static final UUIDValidator UUID_VALIDATOR = new UUIDValidator();

    /**
     * Whether to expect exact lengths on input validation
     */
    private final boolean exactLength;

    /**
     * UUIDs consisting of only zeros (nil UUIDs) should be valid
     */
    private final boolean allowNil;

    /**
     * Should UUID version should be validated (must be 1 to 5)
     */
    private final boolean checkVersion;

    /**
     * Should UUID variant / type should be validated (must be 2)
     */
    private final boolean checkVariant;

    /**
     * How the case of hex digits should matter during validation
     */
    private final CaseSensitivity caseSensitivity;

    /**
     * Should the output be different from the input
     */
    private final ConversionMode conversionMode;

    /**
     * determines whether to check the character case of the input
     */
    public enum CaseSensitivity {
        /**
         * Ignore case of the input
         */
        INSENSITIVE,
        /**
         * All characters of the input must be lower case characters
         */
        LOWER_CASE_ONLY,
        /**
         * All characters of the input must be upper case characters
         */
        UPPER_CASE_ONLY
    }

    /**
     * enables configuration on how the output of the method
     * {@link #validate(CharSequence)} method will look like
     */
    public enum ConversionMode {
        /**
         * Doesn't change the case of the input
         */
        LEAVE_AS_IS,
        /**
         * Doesn't change the case of the input, but shortens to {@link #UUID_LEN} characters length
         */
        SHORTEN,
        /**
         * Converts all output characters to lower case and doesn't shorten to {@link #UUID_LEN} characters length
         */
        TO_LOWER_CASE_NO_SHORTENING,
        /**
         * Converts all output characters to upper case and doesn't shorten to {@link #UUID_LEN} characters length
         */
        TO_UPPER_CASE_NO_SHORTENING,
        /**
         * Converts all output characters to lower case and shortens to {@link #UUID_LEN} characters length
         */
        TO_LOWER_CASE_SHORTEN,
        /**
         * Converts all output characters to upper case and shortens to {@link #UUID_LEN} characters length
         */
        TO_UPPER_CASE_SHORTEN,
    }

    /**
     * Construct an UUID validator that checks whether the input length are larger than
     * {@link #UUID_LEN} characters (default is 36), allows nil UUIDs, checks version and variant,
     * but doesn't check the character case and won't change the output
     */
    public UUIDValidator() {
        this(true, true, true, true, CaseSensitivity.INSENSITIVE, ConversionMode.LEAVE_AS_IS);
    }

    /**
     * Construct an UUID validator indicating whether
     * the exact UUID length should be checked
     *
     * @param exactLength     {@code true} if the length of the UUID should be checked.
     * @param allowNil        {@code true} if {@code 00000000-0000-0000-0000-000000000000} should be regarded as valid.
     * @param checkVersion    {@code true} if UUID version should be checked (1 to 5).
     * @param checkVariant    {@code true} if UUID variant (type) should be checked (0 and 1)
     * @param caseSensitivity Checks the character case of the input (see {@link CaseSensitivity}.
     * @param conversionMode  (see {@link ConversionMode}.
     */
    public UUIDValidator(boolean exactLength, boolean allowNil, boolean checkVersion, boolean checkVariant, CaseSensitivity caseSensitivity, ConversionMode conversionMode) {
        this.exactLength = exactLength;
        this.allowNil = allowNil;
        this.checkVersion = checkVersion;
        this.checkVariant = checkVariant;
        this.caseSensitivity = caseSensitivity;
        this.conversionMode = conversionMode;
    }

    /**
     * Return a singleton instance of the UUID validator with default settings.
     * <p>
     * Per default the length of the input won't be checked
     *
     * @return A singleton instance of the UUID validator.
     */
    public static UUIDValidator getInstance() {
        return UUID_VALIDATOR;
    }

    /**
     * Check the input is a valid UUID.
     * <p>
     * {@code null} is considered as invalid
     *
     * @param input The input to validate.
     * @return {@code true} if a valid UUID, otherwise {@code false}.
     */
    public boolean isValid(CharSequence input) {
        return validate(input) != null;
    }

    /**
     * Check the input is a valid UUID.
     * <p>
     * If valid, this method returns the UUID with
     * lower-case characters.
     * <p>
     * {@code null} is considered as invalid
     *
     * @param input The input to validate.
     * @return A valid UUID code if valid, otherwise {@code null}.
     */
    public String validate(CharSequence input) {

        if (input == null) {
            return null;
        }

        int inputLength = input.length();
        if (inputLength < UUID_LEN) {
            return null;
        }
        if (exactLength && inputLength != UUID_LEN) {
            return null;
        }

        StringBuilder output = buildOutput(inputLength);

        int groupIndex = 0;
        int groupLength = 0;
        int checksum = 0;
        int version = -1;
        int variant = -1;
        for (int charIndex = 0; charIndex < inputLength; charIndex++) {
            char ch = input.charAt(charIndex);

            if (ch == '-') {
                groupIndex++;
                groupLength = 0;
            } else {

                groupLength++;
                if (exactLength && groupLength > GROUP_LENGTHS[groupIndex]) {
                    return null;
                }

                int value = Character.digit(ch, 16);
                if (value == -1) {
                    // not a hex digit
                    return null;
                }
                if (caseSensitivity == CaseSensitivity.LOWER_CASE_ONLY && value > 9 && Character.isUpperCase(ch)) {
                    return null;
                }
                if (caseSensitivity == CaseSensitivity.UPPER_CASE_ONLY && value > 9 && Character.isLowerCase(ch)) {
                    return null;
                }
                checksum += value;
                version = extractVersion(version, charIndex, value);
                variant = extractVariant(variant, charIndex, value);

            }

            append(output, ch);

            if (charIndex + 1 == UUID_LEN && (conversionMode == ConversionMode.SHORTEN || conversionMode == ConversionMode.TO_LOWER_CASE_SHORTEN || conversionMode == ConversionMode.TO_UPPER_CASE_SHORTEN)) {
                break;
            }
        }

        if (!allowNil && checksum == 0) {
            return null;
        }

        if ((checksum > 0 || !allowNil) && checkVersion && (version < 1 || version > 5)) {
            return null;
        }

        if ((checksum > 0 || !allowNil) && checkVariant && (variant < 0 || variant > 2)) {
            return null;
        }

        if (conversionMode == ConversionMode.LEAVE_AS_IS) {
            return input.toString();
        }
        return output.toString();
    }

    /**
     * Get the 4 bit UUID version from the current value
     *
     * @param version The old version (in case the version has already been extracted)
     * @param index   The index of the current value to find the version to extract
     * @param value   The numeric value at the character position
     */
    private int extractVersion(int version, int index, int value) {
        if (index == 14) {
            version = value;
        }
        return version;
    }

    /**
     * Get the 3 bit UUID variant from the current value
     *
     * @param variant The old variant (in case the variant has already been extracted)
     * @param index   The index of the current value to find the variant to extract
     * @param value   The numeric value at the character position
     */
    private int extractVariant(int variant, int index, int value) {
        if (index == 19) {
            // 0xxx
            if (value >> 3 == 0) {
                return 0;
            }
            // 10xx
            if (value >> 2 == 2) {
                return 1;
            }
            // 110x
            if (value >> 1 == 6) {
                return 2;
            }
        }
        return variant;
    }


    /**
     * If the conversion mode is not {@link ConversionMode#LEAVE_AS_IS}, an output buffer will be created
     *
     * @param inputLength The length of the original input
     * @return a {@link StringBuilder} with the according length or {@code null} if conversion mode is {@link ConversionMode#LEAVE_AS_IS}
     */
    private StringBuilder buildOutput(int inputLength) {
        if (conversionMode != ConversionMode.LEAVE_AS_IS) {
            return new StringBuilder(determineOutputLength(inputLength));
        }
        return null;
    }

    /**
     * Appends the character to the {@link StringBuilder} if conversion mode is not {@link ConversionMode#LEAVE_AS_IS}
     *
     * @param output The output buffer
     * @param ch     The char to append
     */
    private void append(StringBuilder output, char ch) {
        switch (conversionMode) {
            case TO_LOWER_CASE_NO_SHORTENING:
            case TO_LOWER_CASE_SHORTEN:
                output.append(Character.toLowerCase(ch));
                break;
            case TO_UPPER_CASE_NO_SHORTENING:
            case TO_UPPER_CASE_SHORTEN:
                output.append(Character.toUpperCase(ch));
                break;
            case SHORTEN:
                output.append(ch);
                break;
            case LEAVE_AS_IS:
                break;
        }
    }

    /**
     * Depending on the conversion mode the length of the output buffer will be determined.
     *
     * @param inputLength The original input length
     * @return {@link #UUID_LEN} if the input should be shortened or the original input length
     */
    private int determineOutputLength(int inputLength) {
        switch (conversionMode) {
            case SHORTEN:
            case TO_LOWER_CASE_SHORTEN:
            case TO_UPPER_CASE_SHORTEN:
                return UUID_LEN;
            default:
                return inputLength;
        }
    }

}
