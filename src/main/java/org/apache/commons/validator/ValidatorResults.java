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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This contains the results of a set of validation rules processed
 * on a JavaBean.
 */
//TODO mutable non-private fields
public class ValidatorResults implements Serializable {

    private static final long serialVersionUID = -2709911078904924839L;

    /**
     * Map of validation results.
     */
    protected Map<String, ValidatorResult> hResults = new HashMap<>();

    /**
     * Constructs a new instance.
     */
    public ValidatorResults() {
        // empty
    }

    /**
     * Add the result of a validator action.
     *
     * @param field The field validated.
     * @param validatorName The name of the validator.
     * @param result The result of the validation.
     */
    public void add(final Field field, final String validatorName, final boolean result) {
        this.add(field, validatorName, result, null);
    }

    /**
     * Add the result of a validator action.
     *
     * @param field The field validated.
     * @param validatorName The name of the validator.
     * @param result The result of the validation.
     * @param value The value returned by the validator.
     */
    public void add(
            final Field field,
            final String validatorName,
            final boolean result,
            final Object value) {

        ValidatorResult validatorResult = getValidatorResult(field.getKey());

        if (validatorResult == null) {
            validatorResult = new ValidatorResult(field);
            hResults.put(field.getKey(), validatorResult);
        }

        validatorResult.add(validatorName, result, value);
    }

    /**
     * Clear all results recorded by this object.
     */
    public void clear() {
        hResults.clear();
    }

    /**
     * Gets the set of property names for which at least one message has
     * been recorded.
     * @return An unmodifiable Set of the property names.
     */
    public Set<String> getPropertyNames() {
        return Collections.unmodifiableSet(hResults.keySet());
    }

    /**
     * Gets a {@link Map} of any {@code Object}s returned from
     * validation routines.
     *
     * @return Map of objections returned by validators.
     */
    public Map<String, Object> getResultValueMap() {
        final Map<String, Object> results = new HashMap<>();

        for (final String propertyKey : hResults.keySet()) {
            final ValidatorResult vr = getValidatorResult(propertyKey);

            for (final Iterator<String> x = vr.getActions(); x.hasNext();) {
                final String actionKey = x.next();
                final Object result = vr.getResult(actionKey);

                if (result != null && !(result instanceof Boolean)) {
                    results.put(propertyKey, result);
                }
            }
        }

        return results;
    }

    /**
     * Gets the {@code ValidatorResult} associated
     * with the key passed in.  The key the {@code ValidatorResult}
     * is stored under is the {@code Field}'s getKey method.
     *
     * @param key The key generated from {@code Field} (this is often just
     * the field name).
     *
     * @return The result of a specified key.
     */
    public ValidatorResult getValidatorResult(final String key) {
        return hResults.get(key);
    }

    /**
     * Gets {@code true} if there are no messages recorded
     * in this collection, or {@code false} otherwise.
     *
     * @return Whether these results are empty.
     */
    public boolean isEmpty() {
        return hResults.isEmpty();
    }

    /**
     * Merge another ValidatorResults into mine.
     *
     * @param results ValidatorResults to merge.
     */
    public void merge(final ValidatorResults results) {
        hResults.putAll(results.hResults);
    }

}
