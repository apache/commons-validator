/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.validator;

import java.util.Date;
import java.util.Locale;

import org.apache.commons.validator.util.ValidatorUtils;

/**
 * Contains validation methods for different unit tests.
 */
public class GenericTypeValidatorImpl {

    /**
     * Checks if the field can be successfully converted to a {@code byte}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code byte} {@code true} is returned. Otherwise {@code false}.
     */
    public static Byte validateByte(final Object bean, final Field field) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatByte(value);
    }

    /**
     * Checks if the field can be successfully converted to a {@code byte}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code byte} {@code true} is returned. Otherwise {@code false}.
     */
    public static Byte validateByte(final Object bean, final Field field, final Locale locale) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatByte(value, locale);
    }

    /**
     * Checks if the field can be successfully converted to a {@code date}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code date} {@code true} is returned. Otherwise {@code false}.
     */
    public static Date validateDate(final Object bean, final Field field) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        final String datePattern = field.getVarValue("datePattern");
        final String datePatternStrict = field.getVarValue("datePatternStrict");

        Date result = null;
        if (datePattern != null && !datePattern.isEmpty()) {
            result = GenericTypeValidator.formatDate(value, datePattern, false);
        } else if (datePatternStrict != null && !datePatternStrict.isEmpty()) {
            result = GenericTypeValidator.formatDate(value, datePatternStrict, true);
        }

        return result;
    }

    /**
     * Checks if the field can be successfully converted to a {@code date}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code date} {@code true} is returned. Otherwise {@code false}.
     */
    public static Date validateDate(final Object bean, final Field field, final Locale locale) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatDate(value, locale);
    }

    /**
     * Checks if the field can be successfully converted to a {@code double}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code double} {@code true} is returned. Otherwise {@code false}.
     */
    public static Double validateDouble(final Object bean, final Field field) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatDouble(value);
    }

    /**
     * Checks if the field can be successfully converted to a {@code double}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code double} {@code true} is returned. Otherwise {@code false}.
     */
    public static Double validateDouble(final Object bean, final Field field, final Locale locale) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatDouble(value, locale);
    }

    /**
     * Checks if the field can be successfully converted to a {@code float}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code float} {@code true} is returned. Otherwise {@code false}.
     */
    public static Float validateFloat(final Object bean, final Field field) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatFloat(value);
    }

    /**
     * Checks if the field can be successfully converted to a {@code float}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code float} {@code true} is returned. Otherwise {@code false}.
     */
    public static Float validateFloat(final Object bean, final Field field, final Locale locale) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatFloat(value, locale);
    }

    /**
     * Checks if the field can be successfully converted to a {@code int}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code int} {@code true} is returned. Otherwise {@code false}.
     */
    public static Integer validateInt(final Object bean, final Field field) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatInt(value);
    }

    /**
     * Checks if the field can be successfully converted to a {@code int}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code int} {@code true} is returned. Otherwise {@code false}.
     */
    public static Integer validateInt(final Object bean, final Field field, final Locale locale) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatInt(value, locale);
    }

    /**
     * Checks if the field can be successfully converted to a {@code long}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code long} {@code true} is returned. Otherwise {@code false}.
     */
    public static Long validateLong(final Object bean, final Field field) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatLong(value);
    }

    /**
     * Checks if the field can be successfully converted to a {@code long}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code long} {@code true} is returned. Otherwise {@code false}.
     */
    public static Long validateLong(final Object bean, final Field field, final Locale locale) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatLong(value, locale);
    }

    /**
     * Checks if the field can be successfully converted to a {@code short}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code short} {@code true} is returned. Otherwise {@code false}.
     */
    public static Short validateShort(final Object bean, final Field field) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatShort(value);
    }

    /**
     * Checks if the field can be successfully converted to a {@code short}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code short} {@code true} is returned. Otherwise {@code false}.
     */
    public static Short validateShort(final Object bean, final Field field, final Locale locale) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatShort(value, locale);
    }
}