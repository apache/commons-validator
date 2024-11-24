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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;

/**
 * Perform credit card validations.
 *
 * <p>
 * By default, AMEX + VISA + MASTERCARD + DISCOVER card types are allowed.  You can specify which
 * cards should pass validation by configuring the validation options. For
 * example,
 * </p>
 *
 * <pre>
 * {@code CreditCardValidator ccv = new CreditCardValidator(CreditCardValidator.AMEX + CreditCardValidator.VISA);}
 * </pre>
 *
 * <p>
 * configures the validator to only pass American Express and Visa cards.
 * If a card type is not directly supported by this class, you can create an
 * instance of the {@link CodeValidator} class and pass it to a {@link CreditCardValidator}
 * constructor along with any existing validators. For example:
 * </p>
 *
 * <pre>
 * <code>CreditCardValidator ccv = new CreditCardValidator(
 *     new CodeValidator[] {
 *         CreditCardValidator.AMEX_VALIDATOR,
 *         CreditCardValidator.VISA_VALIDATOR,
 *         new CodeValidator("^(4)(\\d{12,18})$", LUHN_VALIDATOR) // add VPAY
 * };</code>
 * </pre>
 *
 * <p>
 * Alternatively you can define a validator using the {@link CreditCardRange} class.
 * For example:
 * </p>
 *
 * <pre>
 * <code>CreditCardValidator ccv = new CreditCardValidator(
 *    new CreditCardRange[]{
 *        new CreditCardRange("300", "305", 14, 14), // Diners
 *        new CreditCardRange("3095", null, 14, 14), // Diners
 *        new CreditCardRange("36",   null, 14, 14), // Diners
 *        new CreditCardRange("38",   "39", 14, 14), // Diners
 *        new CreditCardRange("4",    null, new int[] {13, 16}), // VISA
 *    }
 * );
 * </code>
 * </pre>
 * <p>
 * This can be combined with a list of {@code CodeValidator}s
 * </p>
 * <p>
 * More information can be found in Michael Gilleland's essay
 * <a href="http://web.archive.org/web/20120614072656/http://www.merriampark.com/anatomycc.htm">Anatomy of Credit Card Numbers</a>.
 * </p>
 *
 * @since 1.4
 */
public class CreditCardValidator implements Serializable {

    /**
     * Class that represents a credit card range.
     * @since 1.6
     */
    public static class CreditCardRange {
        final String low; // e.g. 34 or 644
        final String high; // e.g. 34 or 65
        final int minLen; // e.g. 16 or -1
        final int maxLen; // e.g. 19 or -1
        final int[] lengths; // e.g. 16,18,19

        /**
         * Create a credit card range specifier for use in validation
         * of the number syntax including the IIN range.
         * <p>
         * The low and high parameters may be shorter than the length
         * of an IIN (currently 6 digits) in which case subsequent digits
         * are ignored and may range from 0-9.
         * </p>
         * <p>
         * The low and high parameters may be different lengths.
         * e.g. Discover "644" and "65".
         * </p>
         * @param low the low digits of the IIN range
         * @param high the high digits of the IIN range
         * @param minLen the minimum length of the entire number
         * @param maxLen the maximum length of the entire number
         */
        public CreditCardRange(final String low, final String high, final int minLen, final int maxLen) {
            this.low = low;
            this.high = high;
            this.minLen = minLen;
            this.maxLen = maxLen;
            this.lengths = null;
        }

        /**
         * Create a credit card range specifier for use in validation
         * of the number syntax including the IIN range.
         * <p>
         * The low and high parameters may be shorter than the length
         * of an IIN (currently 6 digits) in which case subsequent digits
         * are ignored and may range from 0-9.
         * </p>
         * <p>
         * The low and high parameters may be different lengths.
         * e.g. Discover "644" and "65".
         * </p>
         * @param low the low digits of the IIN range
         * @param high the high digits of the IIN range
         * @param lengths array of valid lengths
         */
        public CreditCardRange(final String low, final String high, final int [] lengths) {
            this.low = low;
            this.high = high;
            this.minLen = -1;
            this.maxLen = -1;
            this.lengths = lengths.clone();
        }
    }

    private static final long serialVersionUID = 5955978921148959496L;

    private static final int MIN_CC_LENGTH = 12; // minimum allowed length

    private static final int MAX_CC_LENGTH = 19; // maximum allowed length

    /**
     * Option specifying that no cards are allowed.  This is useful if
     * you want only custom card types to validate so you turn off the
     * default cards with this option.
     *
     * <pre>
     * {@code
     * CreditCardValidator v = new CreditCardValidator(CreditCardValidator.NONE);
     * v.addAllowedCardType(customType);
     * v.isValid(aCardNumber);
     * }
     * </pre>
     */
    public static final long NONE = 0;

    /**
     * Option specifying that American Express cards are allowed.
     */
    public static final long AMEX = 1 << 0;

    /**
     * Option specifying that Visa cards are allowed.
     */
    public static final long VISA = 1 << 1;

    /**
     * Option specifying that Mastercard cards are allowed.
     */
    public static final long MASTERCARD = 1 << 2;

    /**
     * Option specifying that Discover cards are allowed.
     */
    public static final long DISCOVER = 1 << 3; // CHECKSTYLE IGNORE MagicNumber

    /**
     * Option specifying that Diners cards are allowed.
     */
    public static final long DINERS = 1 << 4; // CHECKSTYLE IGNORE MagicNumber

    /**
     * Option specifying that VPay (Visa) cards are allowed.
     * @since 1.5.0
     */
    public static final long VPAY = 1 << 5; // CHECKSTYLE IGNORE MagicNumber

    /**
     * Option specifying that Mastercard cards (pre Oct 2016 only) are allowed.
     * @deprecated for use until Oct 2016 only
     */
    @Deprecated
    public static final long MASTERCARD_PRE_OCT2016 = 1 << 6; // CHECKSTYLE IGNORE MagicNumber

    /**
     * Luhn checkdigit validator for the card numbers.
     */
    private static final CheckDigit LUHN_VALIDATOR = LuhnCheckDigit.LUHN_CHECK_DIGIT;

    /**
     * American Express (Amex) Card Validator
     * <ul>
     * <li>34xxxx (15)</li>
     * <li>37xxxx (15)</li>
     * </ul>
     */
    public static final CodeValidator AMEX_VALIDATOR = new CodeValidator("^(3[47]\\d{13})$", LUHN_VALIDATOR);

    /**
     * Diners Card Validator
     * <ul>
     * <li>300xxx - 305xxx (14)</li>
     * <li>3095xx (14)</li>
     * <li>36xxxx (14)</li>
     * <li>38xxxx (14)</li>
     * <li>39xxxx (14)</li>
     * </ul>
     */
    public static final CodeValidator DINERS_VALIDATOR = new CodeValidator("^(30[0-5]\\d{11}|3095\\d{10}|36\\d{12}|3[8-9]\\d{12})$", LUHN_VALIDATOR);

    /**
     * Discover Card regular expressions
     * <ul>
     * <li>6011xx (16)</li>
     * <li>644xxx - 65xxxx (16)</li>
     * </ul>
     */
    private static final RegexValidator DISCOVER_REGEX = new RegexValidator("^(6011\\d{12,13})$", "^(64[4-9]\\d{13})$", "^(65\\d{14})$", "^(62[2-8]\\d{13})$");

    /** Discover Card Validator */
    public static final CodeValidator DISCOVER_VALIDATOR = new CodeValidator(DISCOVER_REGEX, LUHN_VALIDATOR);

    /**
     * Mastercard regular expressions
     * <ul>
     * <li>2221xx - 2720xx (16)</li>
     * <li>51xxx - 55xxx (16)</li>
     * </ul>
     */
    private static final RegexValidator MASTERCARD_REGEX = new RegexValidator(
            "^(5[1-5]\\d{14})$",   // 51 - 55 (pre Oct 2016)
            // valid from October 2016
            "^(2221\\d{12})$",     // 222100 - 222199
            "^(222[2-9]\\d{12})$", // 222200 - 222999
            "^(22[3-9]\\d{13})$",  // 223000 - 229999
            "^(2[3-6]\\d{14})$",   // 230000 - 269999
            "^(27[01]\\d{13})$",   // 270000 - 271999
            "^(2720\\d{12})$"      // 272000 - 272099
        );

    /** Mastercard Card Validator */
    public static final CodeValidator MASTERCARD_VALIDATOR = new CodeValidator(MASTERCARD_REGEX, LUHN_VALIDATOR);

    /**
     * Mastercard Card Validator (pre Oct 2016)
     * @deprecated for use until Oct 2016 only
     */
    @Deprecated
    public static final CodeValidator MASTERCARD_VALIDATOR_PRE_OCT2016 = new CodeValidator("^(5[1-5]\\d{14})$", LUHN_VALIDATOR);

    /**
     * Visa Card Validator
     * <p>
     * 4xxxxx (13 or 16)
     * </p>
     */
    public static final CodeValidator VISA_VALIDATOR = new CodeValidator("^(4)(\\d{12}|\\d{15})$", LUHN_VALIDATOR);

    /**
     * VPay (Visa) Card Validator
     * <p>
     * 4xxxxx (13-19)
     * </p>
     * @since 1.5.0
     */
    public static final CodeValidator VPAY_VALIDATOR = new CodeValidator("^(4)(\\d{12,18})$", LUHN_VALIDATOR);

    // package protected for unit test access
    static CodeValidator createRangeValidator(final CreditCardRange[] creditCardRanges, final CheckDigit digitCheck) {
        return new CodeValidator(
                // must be numeric (rest of validation is done later)
                new RegexValidator("(\\d+)") {
                    private static final long serialVersionUID = 1L;
                    private final transient CreditCardRange[] ccr = creditCardRanges.clone();

                    @Override
                    public boolean isValid(final String value) {
                        return validate(value) != null;
                    }

                    @Override
                    public String[] match(final String value) {
                        return new String[] { validate(value) };
                    }

                    @Override
                    // must return full string
                    public String validate(final String value) {
                        if (super.match(value) != null) {
                            final int length = value.length();
                            for (final CreditCardRange range : ccr) {
                                if (validLength(length, range)) {
                                    if (range.high == null) { // single prefix only
                                        if (value.startsWith(range.low)) {
                                            return value;
                                        }
                                    } else if (range.low.compareTo(value) <= 0 // no need to trim value here
                                            &&
                                    // here we have to ignore digits beyond the prefix
                                            range.high.compareTo(value.substring(0, range.high.length())) >= 0) {
                                        return value;
                                    }
                                }
                            }
                        }
                        return null;
                    }
                }, digitCheck);
    }

    /**
     * Creates a new generic CreditCardValidator which validates the syntax and check digit only.
     * Does not check the Issuer Identification Number (IIN)
     *
     * @return the validator
     * @since 1.6
     */
    public static CreditCardValidator genericCreditCardValidator() {
        return genericCreditCardValidator(MIN_CC_LENGTH, MAX_CC_LENGTH);
    }

    /**
     * Creates a new generic CreditCardValidator which validates the syntax and check digit only.
     * Does not check the Issuer Identification Number (IIN)
     *
     * @param length exact length
     * @return the validator
     * @since 1.6
     */
    public static CreditCardValidator genericCreditCardValidator(final int length) {
        return genericCreditCardValidator(length, length);
    }

    /**
     * Creates a new generic CreditCardValidator which validates the syntax and check digit only.
     * Does not check the Issuer Identification Number (IIN)
     *
     * @param minLen minimum allowed length
     * @param maxLen maximum allowed length
     * @return the validator
     * @since 1.6
     */
    public static CreditCardValidator genericCreditCardValidator(final int minLen, final int maxLen) {
        return new CreditCardValidator(new CodeValidator[] {new CodeValidator("(\\d+)", minLen, maxLen, LUHN_VALIDATOR)});
    }

    // package protected for unit test access
    static boolean validLength(final int valueLength, final CreditCardRange range) {
        if (range.lengths != null) {
            for (final int length : range.lengths) {
                if (valueLength == length) {
                    return true;
                }
            }
            return false;
        }
        return valueLength >= range.minLen && valueLength <= range.maxLen;
    }

    /**
     * The CreditCardTypes that are allowed to pass validation.
     */
    private final List<CodeValidator> cardTypes = new ArrayList<>();

    /**
     * Constructs a new CreditCardValidator with default options.
     * The default options are:
     * AMEX, VISA, MASTERCARD and DISCOVER
     */
    public CreditCardValidator() {
        this(AMEX + VISA + MASTERCARD + DISCOVER);
    }

    /**
     * Constructs a new CreditCardValidator with the specified {@link CodeValidator}s.
     * @param creditCardValidators Set of valid code validators
     */
    public CreditCardValidator(final CodeValidator[] creditCardValidators) {
        if (creditCardValidators == null) {
            throw new IllegalArgumentException("Card validators are missing");
        }
        Collections.addAll(cardTypes, creditCardValidators);
    }

    /**
     * Constructs a new CreditCardValidator with the specified {@link CodeValidator}s
     * and {@link CreditCardRange}s.
     * <p>
     * This can be used to combine predefined validators such as {@link #MASTERCARD_VALIDATOR}
     * with additional validators using the simpler {@link CreditCardRange}s.
     * @param creditCardValidators Set of valid code validators
     * @param creditCardRanges Set of valid code validators
     * @since 1.6
     */
    public CreditCardValidator(final CodeValidator[] creditCardValidators, final CreditCardRange[] creditCardRanges) {
        if (creditCardValidators == null) {
            throw new IllegalArgumentException("Card validators are missing");
        }
        if (creditCardRanges == null) {
            throw new IllegalArgumentException("Card ranges are missing");
        }
        Collections.addAll(cardTypes, creditCardValidators);
        Collections.addAll(cardTypes, createRangeValidator(creditCardRanges, LUHN_VALIDATOR));
    }

    /**
     * Constructs a new CreditCardValidator with the specified {@link CreditCardRange}s.
     * @param creditCardRanges Set of valid code validators
     * @since 1.6
     */
    public CreditCardValidator(final CreditCardRange[] creditCardRanges) {
        if (creditCardRanges == null) {
            throw new IllegalArgumentException("Card ranges are missing");
        }
        Collections.addAll(cardTypes, createRangeValidator(creditCardRanges, LUHN_VALIDATOR));
    }

    /**
     * Constructs a new CreditCardValidator with the specified options.
     * @param options Pass in
     * CreditCardValidator.VISA + CreditCardValidator.AMEX to specify that
     * those are the only valid card types.
     */
    public CreditCardValidator(final long options) {
        if (isOn(options, VISA)) {
            cardTypes.add(VISA_VALIDATOR);
        }

        if (isOn(options, VPAY)) {
            cardTypes.add(VPAY_VALIDATOR);
        }

        if (isOn(options, AMEX)) {
            cardTypes.add(AMEX_VALIDATOR);
        }

        if (isOn(options, MASTERCARD)) {
            cardTypes.add(MASTERCARD_VALIDATOR);
        }

        if (isOn(options, MASTERCARD_PRE_OCT2016)) {
            cardTypes.add(MASTERCARD_VALIDATOR_PRE_OCT2016);
        }

        if (isOn(options, DISCOVER)) {
            cardTypes.add(DISCOVER_VALIDATOR);
        }

        if (isOn(options, DINERS)) {
            cardTypes.add(DINERS_VALIDATOR);
        }
    }

    /**
     * Tests whether the given flag is on.  If the flag is not a power of 2
     * (ie. 3) this tests whether the combination of flags is on.
     *
     * @param options The options specified.
     * @param flag Flag value to check.
     *
     * @return whether the specified flag value is on.
     */
    private boolean isOn(final long options, final long flag) {
        return (options & flag) > 0;
    }

    /**
     * Checks if the field is a valid credit card number.
     * @param card The card number to validate.
     * @return Whether the card number is valid.
     */
    public boolean isValid(final String card) {
        if (GenericValidator.isBlankOrNull(card)) {
            return false;
        }
        for (final CodeValidator cardType : cardTypes) {
            if (cardType.isValid(card)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the field is a valid credit card number.
     * @param card The card number to validate.
     * @return The card number if valid or {@code null}
     * if invalid.
     */
    public Object validate(final String card) {
        if (GenericValidator.isBlankOrNull(card)) {
            return null;
        }
        Object result = null;
        for (final CodeValidator cardType : cardTypes) {
            result = cardType.validate(card);
            if (result != null) {
                return result;
            }
        }
        return null;

    }

}
