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

import org.apache.commons.validator.util.ValidatorUtils;

/**
 * Contains validation methods for different unit tests.
 */
public class GenericValidatorImpl {

    public static final String FIELD_TEST_NULL = "NULL";

    public static final String FIELD_TEST_NOTNULL = "NOTNULL";

    public static final String FIELD_TEST_EQUAL = "EQUAL";

    private static boolean isStringOrNull(final Object o) {
        if (o == null) {
            return true; // TODO this condition is not exercised by any tests currently
        }
        return o instanceof String;
    }

    /**
     * Checks if the field can be successfully converted to a {@code byte}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code byte} {@code true} is returned. Otherwise {@code false}.
     */
    public static boolean validateByte(final Object bean, final Field field) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericValidator.isByte(value);
    }

    /**
     * Checks if the field can be successfully converted to a {@code double}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code double} {@code true} is returned. Otherwise {@code false}.
     */
    public static boolean validateDouble(final Object bean, final Field field) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericValidator.isDouble(value);
    }

    /**
     * Checks if the field is an e-mail address.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field is an e-mail address {@code true} is returned. Otherwise {@code false}.
     */
    public static boolean validateEmail(final Object bean, final Field field) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericValidator.isEmail(value);
    }

    /**
     * Checks if the field can be successfully converted to a {@code float}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code float} {@code true} is returned. Otherwise {@code false}.
     */
    public static boolean validateFloat(final Object bean, final Field field) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericValidator.isFloat(value);
    }

    /**
     * Checks if the field can be successfully converted to a {@code int}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code int} {@code true} is returned. Otherwise {@code false}.
     */
    public static boolean validateInt(final Object bean, final Field field) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericValidator.isInt(value);
    }

    /**
     * Checks if the field can be successfully converted to a {@code long}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code long} {@code true} is returned. Otherwise {@code false}.
     */
    public static boolean validateLong(final Object bean, final Field field) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericValidator.isLong(value);
    }

    /**
     * Checks if field is positive assuming it is an integer
     *
     * @param bean  The value validation is being performed on.
     * @param field Description of the field to be evaluated
     * @return boolean If the integer field is greater than zero, returns true, otherwise returns false.
     */
    public static boolean validatePositive(final Object bean, final Field field) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatInt(value).intValue() > 0;
    }

    /**
     * Throws a runtime exception if the value of the argument is "RUNTIME", an exception if the value of the argument is "CHECKED", and a ValidatorException
     * otherwise.
     *
     * @throws RuntimeException   with "RUNTIME-EXCEPTION as message" if value is "RUNTIME"
     * @throws Exception          with "CHECKED-EXCEPTION" as message if value is "CHECKED"
     * @throws ValidatorException with "VALIDATOR-EXCEPTION" as message otherwise
     */
    public static boolean validateRaiseException(final Object bean, final Field field) throws Exception {

        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        if ("RUNTIME".equals(value)) {
            throw new RuntimeException("RUNTIME-EXCEPTION");

        }
        if ("CHECKED".equals(value)) {
            throw new Exception("CHECKED-EXCEPTION");

        }
        throw new ValidatorException("VALIDATOR-EXCEPTION");
    }

    /**
     * Checks if the field is required.
     *
     * @return boolean If the field isn't {@code null} and has a length greater than zero, {@code true} is returned. Otherwise {@code false}.
     */
    public static boolean validateRequired(final Object bean, final Field field) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return !GenericValidator.isBlankOrNull(value);
    }

    public static boolean validateRequiredIf(final Object bean, final Field field, final Validator validator) {

        final Object form = validator.getParameterValue(Validator.BEAN_PARAM);
        String value = null;
        boolean required = false;
        if (isStringOrNull(bean)) {
            value = (String) bean;
        } else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        int i = 0;
        String fieldJoin = "AND";
        if (!GenericValidator.isBlankOrNull(field.getVarValue("fieldJoin"))) {
            fieldJoin = field.getVarValue("fieldJoin");
        }
        if (fieldJoin.equalsIgnoreCase("AND")) {
            required = true;
        }
        while (!GenericValidator.isBlankOrNull(field.getVarValue("field[" + i + "]"))) {
            String dependProp = field.getVarValue("field[" + i + "]");
            final String dependTest = field.getVarValue("fieldTest[" + i + "]");
            final String dependTestValue = field.getVarValue("fieldValue[" + i + "]");
            String dependIndexed = field.getVarValue("fieldIndexed[" + i + "]");
            if (dependIndexed == null) {
                dependIndexed = "false";
            }
            boolean thisRequired = false;
            if (field.isIndexed() && Boolean.parseBoolean(dependIndexed)) {
                final String key = field.getKey();
                if (key.contains("[") && key.contains("]")) {
                    final String ind = key.substring(0, key.indexOf(".") + 1);
                    dependProp = ind + dependProp;
                }
            }
            final String dependVal = ValidatorUtils.getValueAsString(form, dependProp);
            if (dependTest.equals(FIELD_TEST_NULL)) {
                if (dependVal != null && !dependVal.isEmpty()) {
                    thisRequired = false;
                } else {
                    thisRequired = true;
                }
            }
            if (dependTest.equals(FIELD_TEST_NOTNULL)) {
                if (dependVal != null && !dependVal.isEmpty()) {
                    thisRequired = true;
                } else {
                    thisRequired = false;
                }
            }
            if (dependTest.equals(FIELD_TEST_EQUAL)) {
                thisRequired = dependTestValue.equalsIgnoreCase(dependVal);
            }
            if (fieldJoin.equalsIgnoreCase("AND")) {
                required = required && thisRequired;
            } else {
                required = required || thisRequired;
            }
            i++;
        }
        if (required) {
            if (value != null && !value.isEmpty()) {
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * Checks if the field can be successfully converted to a {@code short}.
     *
     * @param bean  The value validation is being performed on.
     * @param field the field to use
     * @return boolean If the field can be successfully converted to a {@code short} {@code true} is returned. Otherwise {@code false}.
     */
    public static boolean validateShort(final Object bean, final Field field) {
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericValidator.isShort(value);
    }

}
