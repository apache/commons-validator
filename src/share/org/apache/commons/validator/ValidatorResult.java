/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/ValidatorResult.java,v 1.9 2003/08/21 21:43:06 rleland Exp $
 * $Revision: 1.9 $
 * $Date: 2003/08/21 21:43:06 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names, "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.validator;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>This contains the results of a set of
 * validation rules processed on JavaBean.</p>
 *
 * @author David Winterfeldt
 * @version $Revision: 1.9 $ $Date: 2003/08/21 21:43:06 $
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
         * Gets whether or not the validation passed.
         * @deprecated Use isValid() instead.
         */
        public boolean getValid() {
            return valid;
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