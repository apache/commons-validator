/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/ValidatorResult.java,v 1.12 2004/04/08 23:05:39 dgraham Exp $
 * $Revision: 1.12 $
 * $Date: 2004/04/08 23:05:39 $
 *
 * ====================================================================
 * Copyright 2001-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Map;

/**
 * This contains the results of a set of validation rules processed 
 * on a JavaBean.
 */
public class ValidatorResult implements Serializable {

    /**
     * Map of results.  The key is the name of the <code>ValidatorAction</code>
     * and the value is whether or not this field passed or not.
     */
    protected Map hAction = new HashMap();

    /**
     * <code>Field</code> being validated.
     * TODO This variable is not used.  Need to investigate removing it.
     */
    protected Field field = null;

    /**
     * Constructs a <code>ValidatorResult</code> with the associated field being
     * validated.
     */
    public ValidatorResult(Field field) {
        this.field = field;
    }

    /**
     * Add the result of a validator action.
     */
    public void add(String validatorName, boolean result) {
        this.add(validatorName, result, null);
    }

    /**
     * Add the result of a validator action.
     */
    public void add(String validatorName, boolean result, Object value) {
        hAction.put(validatorName, new ResultStatus(result, value));
    }

    public boolean containsAction(String validatorName) {
        return hAction.containsKey(validatorName);
    }

    public boolean isValid(String validatorName) {
        ResultStatus status = (ResultStatus) hAction.get(validatorName);
        return (status == null) ? false : status.isValid();
    }

    public Map getActionMap() {
        return Collections.unmodifiableMap(hAction);
    }

    /**
     * Returns the Field that was validated.
     */
    public Field getField() {
        return this.field;
    }

    /**
     * Contains the status of the validation.
     */
    protected class ResultStatus implements Serializable {
        private boolean valid = false;
        private Object result = null;

        public ResultStatus(boolean valid, Object result) {
            this.valid = valid;
            this.result = result;
        }

        /**
         * Tests whether or not the validation passed.
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * Sets whether or not the validation passed.
         */
        public void setValid(boolean valid) {
            this.valid = valid;
        }

        /**
         * Gets the result returned by a validation method.
         * This can be used to retrieve to the correctly
         * typed value of a date validation for example.
         */
        public Object getResult() {
            return result;
        }

        /**
         * Sets the result returned by a validation method.
         * This can be used to retrieve to the correctly
         * typed value of a date validation for example.
         */
        public void setResult(Object result) {
            this.result = result;
        }

    }

}