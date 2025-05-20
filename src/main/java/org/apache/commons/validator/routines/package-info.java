/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This package contains <em>independent</em> validation routines.
 * <h2>Table of Contents</h2>
 * <ul>
 * <li>1. <a href="#overview">Overview</a></li>
 * <li>2. <a href="#date">Date and Time Validators</a>
 * <ul>
 * <li>2.1 <a href="#date.overview">Overview</a></li>
 * <li>2.2 <a href="#date.validate">Validating a Date Value</a></li>
 * <li>2.3 <a href="#date.format">Formatting</a></li>
 * <li>2.4 <a href="#date.timezone">Time Zones</a></li>
 * <li>2.5 <a href="#date.compare">Comparing Dates and Times</a></li>
 * </ul>
 * </li>
 * <li>3. <a href="#numeric">Numeric Validators</a>
 * <ul>
 * <li>3.1 <a href="#numeric.overview">Overview</a></li>
 * <li>3.2 <a href="#numeric.validate">Validating a Numeric Value</a></li>
 * <li>3.3 <a href="#numeric.format">Formatting</a></li>
 * <li>3.4 <a href="#numeric.compare">Comparing Numbers</a></li>
 * <li>3.5 <a href="#numeric.currency">Currency Validation</a></li>
 * <li>3.6 <a href="#numeric.percent">Percent Validation</a></li>
 * </ul>
 * </li>
 * <li>4. <a href="#other">Other Validators</a>
 * <ul>
 * <li>4.1 <a href="#other.overview">Overview</a></li>
 * <li>4.2 <a href="#other.regex">Regular Expression validation</a></li>
 * <li>4.3 <a href="#other.checkdigit">Check Digit Validation/Calculation</a></li>
 * <li>4.4 <a href="#other.code">General Code Validation</a></li>
 * <li>4.5 <a href="#other.iban">IBAN Validation</a></li>
 * <li>4.6 <a href="#other.isbn">ISBN Validation</a></li>
 * <li>4.7 <a href="#other.inet">IP Address Validation</a></li>
 * <li>4.8 <a href="#other.email">Email Address Validation</a></li>
 * <li>4.9 <a href="#other.url">URL Validation</a></li>
 * <li>4.10 <a href="#other.domain">Domain Name Validation</a></li>
 * </ul>
 * </li>
 * </ul>
 * <a id="overview"></a>
 * <h2>1. Overview</h2>
 * <p>
 * Commons Validator serves two purposes:
 * </p>
 * <ul>
 * <li>To provide standard, independent validation routines/functions.</li>
 * <li>To provide a <em>mini</em> framework for Validation.</li>
 * </ul>
 * <p>
 * This package has been created, since version 1.3.0, in an attempt to clearly
 * separate these two concerns and is the location for the standard, independent
 * validation routines/functions in <em>Commons Validator</em>.
 * </p>
 * <p>
 * The contents of this package have no dependencies on the framework aspect of
 * Commons Validator and can be used on their own.
 * </p>
 * <a id="date"></a>
 * <h2>2. Date and Time Validators</h2>
 * <a id="date.overview"></a>
 * <h2>2.1 Overview</h2>
 * <p>
 * The date and time validators either validate according to a specified <em>format</em>
 * or use a standard <em>format</em> for a specified {@link java.util.Locale}.
 * </p>
 * <ul>
 * <li><a href="DateValidator.html">Date Validator</a> - validates dates
 * converting to a {@code java.util.Date} type.</li>
 * <li><a href="CalendarValidator.html">Calendar Validator</a> - validates dates
 * converting to a {@code java.util.Calendar} type.</li>
 * <li><a href="TimeValidator.html">Time Validator</a> - validates times
 * converting to a {@code java.util.Calendar} type.</li>
 * </ul>
 * <a id="date.validate"></a>
 * <h2>2.2 Validating a Date Value</h2>
 * <p>
 * You can either use one of the {@code isValid()} methods to just determine
 * if a date is valid, or use one of the {@code validate()} methods to
 * validate a date and convert it to a {@code java.util.Date}...
 * </p>
 * <pre>
 * // Get the Date validator
 * DateValidator validator = DateValidator.getInstance();
 * // Validate/Convert the date
 * Date fooDate = validator.validate(fooString, "dd/MM/yyyy");
 * if (fooDate == null) {
 * // error...not a valid date
 * return;
 * }
 * </pre>
 * <p>The following methods are provided to validate a date/time (return a boolean result):
 * </p>
 * <ul>
 * <li>{@code isValid(<em>value</em>)}</li>
 * <li>{@code isValid(<em>value</em>, <em>pattern</em>)}</li>
 * <li>{@code isValid(<em>value</em>, Locale)}</li>
 * <li>{@code isValid(<em>value</em>, <em>pattern</em>, Locale)}</li>
 * </ul>
 * <p>The following methods are provided to validate a date/time and convert it to either a
 * {@code java.util.Date} or {@code java.util.Calendar}:
 * </p>
 * <ul>
 * <li>{@code validate(<em>value</em>)}</li>
 * <li>{@code validate(<em>value</em>, <em>pattern</em>)}</li>
 * <li>{@code validate(<em>value</em>, Locale)}</li>
 * <li>{@code validate(<em>value</em>, <em>pattern</em>, Locale)}</li>
 * </ul>
 * <a id="date.format"></a>
 * <h2>2.3 Formatting</h2>
 * <p>
 * Formatting and validating are two sides of the same coin. Typically
 * <em>input</em> values which are converted from Strings according to a
 * specified <em>format</em> also have to be rendered for <em>output</em> in
 * the same format. These validators provide the mechanism for formatting from
 * date/time objects to Strings. The following methods are provided to format
 * date/time values as Strings:
 * </p>
 * <ul>
 * <li>{@code format(<em>date/calendar</em>)}</li>
 * <li>{@code format(<em>date/calendar</em>, <em>pattern</em>)}</li>
 * <li>{@code format(<em>date/calendar</em>, Locale)}</li>
 * <li>{@code format(<em>date/calendar</em>, <em>pattern</em>, Locale)}</li>
 * </ul>
 * <a id="date.timezone"></a>
 * <h2>2.4 Time Zones</h2>
 * <p>
 * If the date being parsed relates to a different time zone than the
 * system default, you can specify the {@code TimeZone} to use when
 * validating/converting:
 * </p>
 * <pre>
 * // Get the GMT time zone
 * TimeZone GMT = TimeZone.getInstance("GMT");
 * // Validate/Convert the date using GMT
 * Date fooDate = validator.validate(fooString, "dd/MM/yyyy", GMT);
 * </pre>
 * <p>The following Time Zone <em>flavors</em> of the Validation/Conversion methods
 * are provided:</p>
 * <ul>
 * <li>{@code validate(<em>value</em>, TimeZone)}</li>
 * <li>{@code validate(<em>value</em>, <em>pattern</em>, TimeZone)}</li>
 * <li>{@code validate(<em>value</em>, Locale, TimeZone)}</li>
 * <li>{@code validate(<em>value</em>, <em>pattern</em>, Locale, TimeZone)}</li>
 * </ul>
 * <a id="date.compare"></a>
 * <h2>2.5 Comparing Dates and Times</h2>
 * <p>
 * As well as validating that a value is a valid date or time, these validators
 * also provide <em>date comparison</em> functions. The {@code DateValidator}
 * and {@code CalendarValidator} provide functions for comparing years,
 * quarters, months, weeks and dates and the {@code TimeValidator} provides
 * functions for comparing hours, minutes, seconds and milliseconds.
 * For example, to check that a date is in the current month, you could use
 * the {@code compareMonths()} method, which compares the year and month
 * components of a date:
 * </p>
 * <pre>
 * // Check if the date is in the current month
 * int compare = validator.compareMonths(fooDate, new Date(), null);
 * if (compare == 0) {
 * // do current month processing
 * return;
 * }
 * // Check if the date is in the previous quarter
 * compare = validator.compareQuarters(fooDate, new Date(), null);
 * if (compare &lt; 0) {
 * // do previous quarter processing
 * return;
 * }
 * // Check if the date is in the next year
 * compare = validator.compareYears(fooDate, new Date(), null);
 * if (compare &gt; 0) {
 * // do next year processing
 * return;
 * }
 * </pre>
 * <a id="numeric"></a>
 * <h2>3 Numeric Validators</h2>
 * <a id="numeric.overview"></a>
 * <h2>3.1 Overview</h2>
 * <p>
 * The numeric validators either validate according to a specified <em>format</em>
 * or use a standard <em>format</em> for a specified {@link java.util.Locale} or use
 * a <em>custom</em> format for a specified {@link  java.util.Locale}.
 * </p>
 * <ul>
 * <li><a href="ByteValidator.html">Byte Validator</a> - validates numbers
 * converting to a {@code java.lang.Byte} type.</li>
 * <li><a href="ShortValidator.html">Short Validator</a> - validates numbers
 * converting to a {@code java.lang.Short} type.</li>
 * <li><a href="IntegerValidator.html">Integer Validator</a> - validates numbers
 * converting to a {@code java.lang.Integer} type.</li>
 * <li><a href="LongValidator.html">Long Validator</a> - validates numbers
 * converting to a {@code java.lang.Long} type.</li>
 * <li><a href="FloatValidator.html">Float Validator</a> - validates numbers
 * converting to a {@code java.lang.Float} type.</li>
 * <li><a href="DoubleValidator.html">Double Validator</a> - validates numbers
 * converting to a {@code java.lang.Double} type.</li>
 * <li><a href="BigIntegerValidator.html">BigInteger Validator</a> - validates numbers
 * converting to a {@code java.math.BigInteger} type.</li>
 * <li><a href="BigDecimalValidator.html">BigDecimal Validator</a> - validates numbers
 * converting to a {@code java.math.BigDecimal} type.</li>
 * </ul>
 * <a id="numeric.validate"></a>
 * <h2>3.2 Validating a Numeric Value</h2>
 * <p>
 * You can either use one of the {@code isValid()} methods to just determine
 * if a number is valid, or use one of the {@code validate()} methods to
 * validate a number and convert it to an appropriate type.
 * </p>
 * <p>
 * The following example validates an integer against a custom pattern
 * for the <em>German</em> locale. Please note the format is specified using
 * the standard symbols for {@link java.text.DecimalFormat} so although
 * the decimal separator is indicated as a period (".") in the format, the
 * validator will check using the German decimal separator - which is a comma (",").
 * </p>
 * <pre>
 * // Get the Integer validator
 * IntegerValidator validator = IntegerValidator.getInstance();
 * // Validate/Convert the number
 * Integer fooInteger = validator.validate(fooString, "#,##0.00", Locale.GERMAN);
 * if (fooInteger == null) {
 * // error...not a valid Integer
 * return;
 * }
 * </pre>
 * <p>The following methods are provided to validate a number (return a boolean result):</p>
 * <ul>
 * <li>{@code isValid(<em>value</em>)}</li>
 * <li>{@code isValid(<em>value</em>, <em>pattern</em>)}</li>
 * <li>{@code isValid(<em>value</em>, Locale)}</li>
 * <li>{@code isValid(<em>value</em>, <em>pattern</em>, Locale)}</li>
 * </ul>
 * <p>The following methods are provided to validate a number and convert it one of
 * the {@code java.lang.Number} implementations:</p>
 * <ul>
 * <li>{@code validate(<em>value</em>)}</li>
 * <li>{@code validate(<em>value</em>, <em>pattern</em>)}</li>
 * <li>{@code validate(<em>value</em>, Locale)}</li>
 * <li>{@code validate(<em>value</em>, <em>pattern</em>, Locale)}</li>
 * </ul>
 * <a id="numeric.format"></a>
 * <h2>3.3 Formatting</h2>
 * <p>
 * Formatting and validating are two sides of the same coin. Typically
 * <em>input</em> values which are converted from Strings according to a
 * specified <em>format</em> also have to be rendered for <em>output</em> in
 * the same format. These validators provide the mechanism for formatting from
 * numeric objects to Strings. The following methods are provided to format
 * numeric values as Strings:
 * </p>
 * <ul>
 * <li>{@code format(<em>number</em>)}</li>
 * <li>{@code format(<em>number</em>, <em>pattern</em>)}</li>
 * <li>{@code format(<em>number</em>, Locale)}</li>
 * <li>{@code format(<em>number</em>, <em>pattern</em>, Locale)}</li>
 * </ul>
 * <a id="numeric.compare"></a>
 * <h2>3.4 Comparing Numbers</h2>
 * <p>
 * As well as validating that a value is a valid number, these validators
 * also provide functions for validating the <em>minimum</em>, <em>maximum</em>
 * and <em>range</em> of a value.
 * </p>
 * <pre>
 * // Check the number is between 25 and 75
 * if (validator.isInRange(fooInteger, 25, 75) {
 * // valid...in the specified range
 * return;
 * }
 * </pre>
 * <a id="numeric.currency"></a>
 * <h2>3.5 Currency Validation</h2>
 * <p>
 * A default <a href="CurrencyValidator.html">Currency Validator</a>
 * implementation is provided, although all the <em>numeric</em> validators
 * support currency validation. The default implementation converts
 * currency amounts to a {@code java.math.BigDecimal} and additionally
 * it provides <em>lenient</em> currency symbol validation. That is, currency
 * amounts are valid with <em>or</em> without the currency symbol.
 * </p>
 * <pre>
 * BigDecimalValidator validator = CurrencyValidator.getInstance();
 * BigDecimal fooAmount = validator.validate("$12,500.00", Locale.US);
 * if (fooAmount == null) {
 * // error...not a valid currency amount
 * return;
 * }
 * // Check the amount is a minimum of $1,000
 * if (validator.minValue(fooAmount, 1000) {
 * // valid...in the specified range
 * return;
 * }
 * </pre>
 * <p>
 * If, for example, you want to use the <a href="IntegerValidator.html">Integer
 * Validator</a> to validate a currency, then you can simply create a
 * new instance with the appropriate <em>format style</em>. Note that
 * the other validators do not support the <em>lenient</em> currency symbol
 * validation.
 * </p>
 * <pre>
 * IntegerValidator validator =
 * new IntegerValidator(true, IntegerValidator.CURRENCY_FORMAT);
 * String pattern = "#,###" + '\u00A4' + '\u00A4';  // Use international symbol
 * Integer fooAmount = validator.validate("10.100EUR", pattern, Locale.GERMAN);
 * if (fooAmount == null) {
 * // error...not a valid currency amount
 * return;
 * }
 * </pre>
 * <a id="numeric.percent"></a>
 * <h2>3.6 Percent Validation</h2>
 * <p>
 * A default <a href="PercentValidator.html">Percent Validator</a>
 * implementation is provided, although the <em>Float</em>,
 * <em>Double</em> and <em>BigDecimal</em> validators also support
 * percent validation. The default implementation converts
 * percent amounts to a {@code java.math.BigDecimal} and additionally
 * it provides <em>lenient</em> percent symbol validation. That is, percent
 * amounts are valid with <em>or</em> without the percent symbol.
 * </p>
 * <pre>
 * BigDecimalValidator validator = PercentValidator.getInstance();
 * BigDecimal fooPercent = validator.validate("20%", Locale.US);
 * if (fooPercent == null) {
 * // error...not a valid percent
 * return;
 * }
 * // Check the percent is between 10% and 90%
 * if (validator.isInRange(fooPercent, 0.1, 0.9) {
 * // valid...in the specified range
 * return;
 * }
 * </pre>
 * <p>
 * If, for example, you want to use the <a href="FloatValidator.html">Float
 * Validator</a> to validate a percent, then you can simply create a
 * new instance with the appropriate <em>format style</em>. Note that
 * the other validators do not support the <em>lenient</em> percent symbol
 * validation.
 * </p>
 * <pre>
 * FloatValidator validator =
 * new FloatValidator(true, FloatValidator.PERCENT_FORMAT);
 * Float fooPercent = validator.validate("20%", "###%");
 * if (fooPercent == null) {
 * // error...not a valid percent
 * return;
 * }
 * </pre>
 * <p>
 * <strong>Note</strong>: in theory the other numeric validators besides
 * <em>Float</em>, <em>Double</em> and <em>BigDecimal</em> (that is, <em>Byte</em>,
 * <em>Short</em>, <em>Integer</em>, <em>Long</em> and <em>BigInteger</em>)
 * also support percent validation. However, since they don't allow fractions
 * they will only work with percentages greater than 100%.
 * </p>
 * <a id="other"></a>
 * <h2>4. Other Validators</h2>
 * <a id="other.overview"></a>
 * <h2>4.1 Overview</h2>
 * <p>
 * This section lists other available validators.
 * </p>
 * <ul>
 * <li><a href="#other.regex">Regular Expressions</a> - validates
 * using Java 1.4+ regular expression support</li>
 * <li><a href="#other.checkdigit">Check Digit</a> - validates/calculates
 * check digits (for example, EAN/UPC, credit card, ISBN).</li>
 * <li><a href="#other.code">Code Validation</a> - provides generic
 * code validation - format, minimum/maximum length and check digit.</li>
 * <li><a href="#other.iban">IBAN Validation</a> - provides International Bank Account Number validation.</li>
 * <li><a href="#other.isbn">ISBN Validation</a> - provides ISBN-10
 * and ISBN-13 validation.</li>
 * <li><a href="#other.inet">IP Address Validation</a> - provides IPv4 address
 * validation.</li>
 * <li><a href="#other.email">Email Address Validation</a> - provides email
 * address validation according to RFC 822 standards.</li>
 * <li><a href="#other.url">URL Validation</a> - provides URL validation on
 * scheme, domain, and authority.</li>
 * <li><a href="#other.domain">Domain Name Validation</a> - provides domain
 * name and IANA TLD validation.</li>
 * </ul>
 * <a id="other.regex"></a>
 * <h2>4.2 Regular Expression Validation</h2>
 * <p>
 * Regular expression validation can be done either by using the <em>static</em>
 * methods provided by <a href="RegexValidator.html">RegexValidator</a> or
 * by creating a new instance, which caches and re-uses compiled Patterns.
 * </p>
 * <ul>
 * <li><strong>Method Flavours</strong> - three <em>flavors</em> of validation methods are provided:</li>
 * <li>
 * <ul>
 * <li>{@code isValid()} methods return true/false to indicate
 * whether validation was successful.</li>
 * <li>{@code validate()} methods return a {@link java.lang.String}
 * value of the matched <em>groups</em> aggregated together or
 * {@code null} if invalid.</li>
 * <li>{@code match()} methods return a {@link java.lang.String} array
 * of the matched <em>groups</em> or {@code null} if invalid.</li>
 * </ul>
 * </li>
 * <li><strong>Case Sensitivity</strong> - matching can be done in either a
 * <i>case-sensitive</i> or <em>case-insensitive</em> way.</li>
 * <li><strong>Multiple Expressions</strong> - instances of the
 * <a href="RegexValidator.html">RegexValidator</a>
 * can be created to either match against a single regular expression
 * or set (String array) of regular expressions.</li>
 * </ul>
 * <p>
 * Below is an example of using one of the static methods to validate,
 * matching in a <em>case insensitive</em> manner and returning a String
 * of the matched groups (which doesn't include the hyphen).
 * </p>
 * <pre>
 * // set up the parameters
 * boolean caseSensitive   = false;
 * String regex            = "^([A-Z]*)(?:\\-)([A-Z]*)$";
 * // validate - result should be a String of value "abcdef"
 * String result = RegexValidator.validate("abc-def", regex, caseSensitive);
 * </pre>
 * <p>The following static methods are provided for regular expression validation:
 * </p>
 * <ul>
 * <li>{@code isValid(<em>value</em>, <em>regex</em>)}</li>
 * <li>{@code isValid(<em>value</em>, <em>regex</em>, <em>caseSensitive</em>)}</li>
 * <li>{@code validate(<em>value</em>, <em>regex</em>)}</li>
 * <li>{@code validate(<em>value</em>, <em>regex</em>, <em>caseSensitive</em>)}</li>
 * <li>{@code match(<em>value</em>, <em>regex</em>)}</li>
 * <li>{@code match(<em>value</em>, <em>regex</em>, <em>caseSensitive</em>)}</li>
 * </ul>
 * <p>
 * Below is an example of creating an instance of
 * <a href="RegexValidator.html">RegexValidator</a> matching in a <em>case insensitive</em>
 * manner against a set of regular expressions:
 * </p>
 * <pre>
 * // set up the parameters
 * boolean caseSensitive = false;
 * String regex1   = "^([A-Z]*)(?:\\-)([A-Z]*)*$"
 * String regex2   = "^([A-Z]*)$";
 * String[] regexs = new String[] {regex1, regex1};
 * // Create the validator
 * RegexValidator validator = new RegexValidator(regexs, caseSensitive);
 * // Validate true/false
 * boolean valid = validator.isValid("abc-def");
 * // Validate and return a String
 * String result = validator.validate("abc-def");
 * // Validate and return a String[]
 * String[] groups = validator.match("abc-def");
 * </pre>
 * <p>See the
 * <a href="RegexValidator.html">RegexValidator</a> Javadoc for a full list
 * of the available constructors.
 * </p>
 * <a id="other.checkdigit"></a>
 * <h2>4.3 Check Digit validation/calculation</h2>
 * <p>
 * <a href="checkdigit/CheckDigit.html">CheckDigit</a> defines a new
 * type for the calculation and validation of check digits with the
 * following methods:
 * </p>
 * <ul>
 * <li>{@code isValid(<em>code</em>)} - validates the check digit of a code,
 * returning {@code true} or {@code false}.</li>
 * <li>{@code calculate(<em>code</em>)} - calculates the check digit for a code
 * returning the check digit character.</li>
 * </ul>
 * <p>
 * The following implementations are provided:
 * </p>
 * <ul>
 * <li><a href="checkdigit/ABANumberheckDigit.html">ABANumberCheckDigit</a>
 * for <strong>ABA Number</strong> (or <strong>Routing Transit Number</strong> (RTN)) check digit calculation.</li>
 * <li><a href="checkdigit/CUSIPCheckDigit.html">CUSIPCheckDigit</a>
 * for <strong>CUSIP</strong> (North American Securities) check digit calculation.</li>
 * <li><a href="checkdigit/EAN13CheckDigit.html">EAN13CheckDigit</a>
 * for <strong>EAN-13</strong>, <strong>UPC</strong>, <strong>ISBN-13</strong> check digit calculation.</li>
 * <li><a href="checkdigit/IBANCheckDigit.html">IBANCheckDigit</a>
 * for <strong>IBAN</strong> check digit calculation.</li>
 * <li><a href="checkdigit/ISBNCheckDigit.html">ISBNCheckDigit</a>
 * for <strong>ISBN-10</strong> and <strong>ISBN-13</strong> check digit calculation.</li>
 * <li><a href="checkdigit/ISBN10CheckDigit.html">ISBN10CheckDigit</a>
 * for <strong>ISBN-10</strong> check digit calculation.</li>
 * <li><a href="checkdigit/ISINCheckDigit.html">ISINCheckDigit</a>
 * for <strong>ISIN</strong> International Securities Identifying Number check digit calculation.</li>
 * <li><a href="checkdigit/LuhnCheckDigit.html">LuhnCheckDigit</a>
 * for <strong>Luhn</strong> check digit calculation - used by <strong>credit cards</strong>.</li>
 * <li><a href="checkdigit/ModulusCheckDigit.html">ModulusCheckDigit</a>
 * - <strong>abstract</strong> class for custom <strong>modulus</strong> check digit
 * implementations.</li>
 * <li><a href="checkdigit/SedolCheckDigit.html">SedolCheckDigit</a>
 * for <strong>SEDOL</strong> (UK Securities) check digit calculation.</li>
 * <li><a href="checkdigit/VerhoeffCheckDigit.html">VerhoeffCheckDigit</a>
 * for <strong>Verhoeff</strong> (Dihedral) check digit calculation.</li>
 * </ul>
 * <p>
 * The following examples show validating the check digit of a code:
 * </p>
 * <pre>
 * // Luhn check digit validation
 * boolean valid = LuhnCheckDigit.INSTANCE.isValid(code);
 * // EAN / UPC / ISBN-13 check digit validation
 * boolean valid = EAN13CheckDigit.INSTANCE.isValid(code);
 * // ISBN-10 check digit validation
 * boolean valid = ISBNCheckDigit.ISBN10.isValid(code);
 * boolean valid = ISBN10CheckDigit.INSTANCE.isValid(code);
 * // ISBN-13 check digit validation
 * boolean valid = ISBNCheckDigit.ISBN13.isValid(code);
 * // ISBN-10 or ISBN-13 check digit validation
 * boolean valid = ISBNCheckDigit.ISBN.isValid(code);
 * </pre>
 * <p>
 * The following examples show calculating the check digit of a code:
 * </p>
 * <pre>
 * // Luhn check digit validation
 * char checkdigit = LuhnCheckDigit.INSTANCE.calculate(code);
 * // EAN / UPC / ISBN-13 check digit validation
 * char checkdigit = EAN13CheckDigit.INSTANCE.calculate(code);
 * // ISBN-10 check digit validation
 * char checkdigit = ISBNCheckDigit.ISBN10.isValid(code);
 * char checkdigit = ISBN10CheckDigit.INSTANCE.calculate(code);
 * // ISBN-13 check digit validation
 * char checkdigit = ISBNCheckDigit.ISBN13.calculate(code);
 * // ISBN-10 or ISBN-13 check digit validation
 * char checkdigit = ISBNCheckDigit.ISBN.calculate(code);
 * </pre>
 * <a id="other.code"></a>
 * <h2>4.4 General Code validation</h2>
 * <p>
 * <a href="CodeValidator.html">CodeValidator</a> provides a generic
 * implementation for validating codes. It performs the following
 * validations on a code:
 * </p>
 * <ul>
 * <li><strong>Format</strong> - the format of the code is validated using
 * a <em>regular expression</em> (see  <a href="RegexValidator.html">RegexValidator</a>).</li>
 * <li><strong>Length</strong> - the minimum/maximum length of the code is
 * checked - after being parsed by the regular expression - with which
 * <em>format</em> characters can be removed with the use of
 * <em>non-capturing</em> groups.</li>
 * <li><strong>Check Digit</strong> - a <a href="checkdigit/CheckDigit.html">CheckDigit</a>
 * routine checks that code's check digit is valid.</li>
 * </ul>
 * <p>
 * For example to create a validator to validate EAN-13 codes (numeric,
 * with a length of 13):
 * </p>
 * <pre>
 * // Create an EAN-13 code validator
 * CodeValidator validator = new CodeValidator("^[0-9]*$", 13, EAN13CheckDigit.INSTANCE);
 * // Validate an EAN-13 code
 * if (!validator.isValid(code)) {
 * ... // invalid
 * }
 * </pre>
 * <a id="other.iban"></a>
 * <h2>4.5 IBAN validation</h2>
 * <p>
 * <a href="IBANValidator.html">IBANValidator</a> provides validation of
 * International Bank Account Number.
 * </p>
 * <p>
 * The validator includes a default set of formats derived from the IBAN registry at
 * https://www.swift.com/standards/data-standards/iban.
 * </p>
 * <p>
 * For example:
 * </p>
 * <pre>
 * // Get an IBANValidator
 * IBANValidator validator = IBANValidator.getInstance();
 * // Validate an IBAN
 * if (!validator.isValid(candidateIBAN)) {
 * ... // invalid
 * }
 * </pre>
 * <a id="other.isbn"></a>
 * <h2>4.6 ISBN validation</h2>
 * <p>
 * <a href="ISBNValidator.html">ISBNValidator</a> provides ISBN-10
 * and ISBN-13 validation and can <em>optionally</em> convert
 * ISBN-10 codes to ISBN-13.
 * </p>
 * <ul>
 * <li><strong>ISBN-10</strong> - validates using a
 * <a href="CodeValidator.html">CodeValidator</a> with the
 * <a href="checkdigit/ISBN10CheckDigit.html">ISBN10CheckDigit</a>
 * routine.</li>
 * <li>
 * <ul>
 * <li>{@code isValidISBN10(<em>value</em>)} - returns a boolean</li>
 * <li>{@code validateISBN10(<em>value</em>)} - returns a reformatted ISBN-10 code</li>
 * </ul>
 * </li>
 * <li><strong>ISBN-13</strong> - validates using a
 * <a href="CodeValidator.html">CodeValidator</a> with the
 * <a href="checkdigit/EAN13CheckDigit.html">EAN13CheckDigit</a>
 * routine.</li>
 * <li>
 * <ul>
 * <li>{@code isValidISBN13(<em>value</em>)} - returns a boolean</li>
 * <li>{@code validateISBN13(<em>value</em>)} - returns a reformatted ISBN-13 code</li>
 * </ul>
 * </li>
 * <li><strong>ISBN-10</strong> and <strong>ISBN-13</strong> - validates codes are either
 * valid ISBN-10 or valid ISBN-13 - optionally can convert ISBN-10 codes to ISBN-13.</li>
 * <li>
 * <ul>
 * <li>{@code isValid(<em>value</em>)} - returns a boolean</li>
 * <li>{@code validate(<em>value</em>)} - returns a reformatted ISBN code
 * (converts ISBN-10 to ISBN-13 if the <em>convert</em> option is {@code true}).</li>
 * </ul>
 * </li>
 * </ul>
 * <p>
 * For example to validate
 * </p>
 * <pre>
 * // Validate an ISBN-10 or ISBN-13 code
 * if (!ISBNValidator.getInstance().isValid(code)) {
 * ... // invalid
 * }
 * // Validate an ISBN-10 or ISBN-13 code (converting to ISBN-13)
 * String code = ISBNValidator.getInstance().validate(code);
 * // Validate an ISBN-10 or ISBN-13 code (not converting)
 * String code = ISBNValidator.getInstance(false).validate(code);
 * </pre>
 * <a id="other.inet"></a>
 * <h2>4.7 IP Address Validation</h2>
 * <p>
 * <a href="InetAddressValidator.html">InetAddressValidator</a> provides
 * IPv4 address validation.
 * </p>
 * <p>
 * For example:
 * </p>
 * <pre>
 * // Get an InetAddressValidator
 * InetAddressValidator validator = InetAddressValidator.getInstance();
 * // Validate an IPv4 address
 * if (!validator.isValid(candidateInetAddress)) {
 * ... // invalid
 * }
 * </pre>
 * <a id="other.email"></a>
 * <h2>4.8 Email Address Validation</h2>
 * <p>
 * <a href="EmailValidator.html">EmailValidator</a> provides email address
 * validation according to RFC 822 standards.
 * </p>
 * <p>
 * For example:
 * </p>
 * <pre>
 * // Get an EmailValidator
 * EmailValidator validator = EmailValidator.getInstance();
 * // Validate an email address
 * boolean isAddressValid = validator.isValid("user@apache.org");
 * // Validate a variable containing an email address
 * if (!validator.isValid(addressFromUserForm)) {
 * webController.sendRedirect(ERROR_REDIRECT, "Email address isn't valid");
 * // etc.
 * }
 * </pre>
 * <a id="other.url"></a>
 * <h2>4.9 URL Validation</h2>
 * <p>
 * <a href="UrlValidator.html">UrlValidator</a> provides URL validation by
 * checking the scheme, authority, path, query, and fragment in turn. Clients
 * may specify valid schemes to be used in validating in addition to or instead of
 * the default values (HTTP, HTTPS, FTP). The UrlValidator also supports options
 * that change the parsing rules; for example, the ALLOW_2_SLASHES option instructs
 * the Validator to allow consecutive slash characters in the path component, which
 * is considered an error by default.
 * For more information on the available options, see the UrlValidator documentation.
 * </p>
 * <p>
 * For example:
 * </p>
 * <pre>
 * // Get an UrlValidator
 * UrlValidator defaultValidator = new UrlValidator(); // default schemes
 * if (defaultValidator.isValid("http://www.apache.org")) {
 * ... // valid
 * }
 * if (!defaultValidator.isValid("http//www.oops.com")) {
 * ... // invalid
 * }
 * // Get an UrlValidator with custom schemes
 * String[] customSchemes = { "sftp", "scp", "https" };
 * UrlValidator customValidator = new UrlValidator(customSchemes);
 * if (!customValidator.isValid("http://www.apache.org")) {
 * ... // invalid due to insecure protocol
 * }
 * // Get an UrlValidator that allows double slashes in the path
 * UrlValidator doubleSlashValidator = new UrlValidator(UrlValidator.ALLOW_2_SLASHES);
 * if (doubleSlashValidator.isValid("http://www.apache.org//projects")) {
 * ... // valid only in this Validator instance
 * }
 * </pre>
 * <a id="other.domain"></a>
 * <h2>4.10 Domain Name Validation</h2>
 * <p>
 * <a href="DomainValidator.html">DomainValidator</a> provides validation of Internet
 * domain names as specified by RFC1034/RFC1123 and according to the IANA-recognized
 * list of top-level domains (TLDs). Clients may validate an entire domain name, a
 * TLD of any category, or a TLD within a specific category.
 * </p>
 * <p>
 * For example:
 * </p>
 * <pre>
 * // Get a DomainValidator
 * DomainValidator validator = DomainValidator.getInstance();
 * // Validate a domain name
 * if (validator.isValid("www.apache.org")) {
 * ... // valid
 * }
 * if (!validator.isValid("www.apache.wrong")) {
 * ... // invalid
 * }
 * // Validate a TLD
 * if (validator.isValidTld(".com")) {
 * ... // valid
 * }
 * if (validator.isValidTld("org")) {
 * ... // valid, the leading dot is optional
 * }
 * if (validator.isValidTld(".us")) {
 * ... // valid, country code TLDs are also accepted
 * }
 * // Validate TLDs in categories
 * if (validator.isValidGenericTld(".name")) {
 * ... // valid
 * }
 * if (!validator.isValidGenericTld(".uk")) {
 * ... // invalid, .uk is a country code TLD
 * }
 * if (!validator.isValidCountryCodeTld(".info")) {
 * ... // invalid, .info is a generic TLD
 * }
 * </pre>
 */
package org.apache.commons.validator.routines;