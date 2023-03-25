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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This contains the results of a set of validation rules processed
 * on a JavaBean.
 */
//TODO mutable non-private fields
public class ValidatorResult implements Serializable {

    private static final long serialVersionUID = -3713364681647250531L;

    /**
     * Map of results.  The key is the name of the <code>ValidatorAction</code>
     * and the value is whether or not this field passed or not.
     */
    protected Map<String, ResultStatus> hAction = new HashMap<>();

    /**
     * <code>Field</code> being validated.
     * TODO This variable is not used.  Need to investigate removing it.
     */
    protected Field field;

    /**
     * Constructs a <code>ValidatorResult</code> with the associated field being
     * validated.
     * @param field Field that was validated.
     */
    public ValidatorResult(final Field field) {
        this.field = field;
    }

    /**
     * Add the result of a validator action.
     * @param validatorName Name of the validator.
     * @param result Whether the validation passed or failed.
     */
    public void add(final String validatorName, final boolean result) {
        this.add(validatorName, result, null);
    }

    /**
     * Add the result of a validator action.
     * @param validatorName Name of the validator.
     * @param result Whether the validation passed or failed.
     * @param value Value returned by the validator.
     */
    public void add(final String validatorName, final boolean result, final Object value) {
        hAction.put(validatorName, new ResultStatus(result, value));
    }

    /**
     * Indicate whether a specified validator is in the Result.
     * @param validatorName Name of the validator.
     * @return true if the validator is in the result.
     */
    public boolean containsAction(final String validatorName) {
        return hAction.containsKey(validatorName);
    }

    /**
     * Indicate whether a specified validation passed.
     * @param validatorName Name of the validator.
     * @return true if the validation passed.
     */
    public boolean isValid(final String validatorName) {
        final ResultStatus status = hAction.get(validatorName);
        return (status == null) ? false : status.isValid();
    }

    /**
     * Return the result of a validation.
     * @param validatorName Name of the validator.
     * @return The validation result.
     */
    public Object getResult(final String validatorName) {
        final ResultStatus status = hAction.get(validatorName);
        return (status == null) ? null : status.getResult();
    }

    /**
     * Return an Iterator of the action names contained in this Result.
     * @return The set of action names.
     */
    public Iterator<String> getActions() {
        return Collections.unmodifiableMap(hAction).keySet().iterator();
    }

    /**
     * Return a Map of the validator actions in this Result.
     * @return Map of validator actions.
     * @deprecated Use getActions() to return the set of actions
     *             the isValid(name) and getResult(name) methods
     *             to determine the contents of ResultStatus.
     *
     */
    @Deprecated
    public Map<String, ResultStatus> getActionMap() {
        return Collections.unmodifiableMap(hAction);
    }

    /**
     * Returns the Field that was validated.
     * @return The Field associated with this result.
     */
    public Field getField() {
        return this.field;
    }

    /**
     * Contains the status of the validation.
     */
    protected static class ResultStatus implements Serializable {

        private static final long serialVersionUID = 4076665918535320007L;

        private boolean valid;
        private Object result;

       /**
        * Construct a Result status.
        * @param valid Whether the validator passed or failed.
        * @param result Value returned by the validator.
        */
        public ResultStatus(final boolean valid, final Object result) {
            this.valid = valid;
            this.result = result;
        }
        /**
         * Provided for backwards binary compatibility only.
         *
         * @param ignored ignored by this method
         * @param valid Whether the validator passed or failed.
         * @param result Value returned by the validator.
         *
         * @deprecated Use {@code ResultStatus(boolean, Object)} instead
         */
        @Deprecated
        public ResultStatus(final ValidatorResult ignored, final boolean valid, final Object result) {
            this(valid, result);
        }

        /**
         * Tests whether or not the validation passed.
         * @return true if the result was good.
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * Sets whether or not the validation passed.
         * @param valid Whether the validation passed.
         */
        public void setValid(final boolean valid) {
            this.valid = valid;
        }

        /**
         * Gets the result returned by a validation method.
         * This can be used to retrieve to the correctly
         * typed value of a date validation for example.
         * @return The value returned by the validation.
         */
        public Object getResult() {
            return result;
        }

        /**
         * Sets the result returned by a validation method.
         * This can be used to retrieve to the correctly
         * typed value of a date validation for example.
         * @param result The value returned by the validation.
         */
        public void setResult(final Object result) {
            this.result = result;
        }

    }

}