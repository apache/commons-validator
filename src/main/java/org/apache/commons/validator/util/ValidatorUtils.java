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
package org.apache.commons.validator.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.FastHashMap; // DEPRECATED
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Msg;
import org.apache.commons.validator.Var;

/**
 * Basic utility methods.
 * <p>
 * The use of FastHashMap is deprecated and will be replaced in a future
 * release.
 * </p>
 */
public class ValidatorUtils {

    private static final Log LOG = LogFactory.getLog(ValidatorUtils.class);

    /**
     * Makes a deep copy of a <code>FastHashMap</code> if the values
     * are <code>Msg</code>, <code>Arg</code>,
     * or <code>Var</code>.  Otherwise it is a shallow copy.
     *
     * @param fastHashMap <code>FastHashMap</code> to copy.
     * @return FastHashMap A copy of the <code>FastHashMap</code> that was
     * passed in.
     * @deprecated This method is not part of Validator's public API.  Validator
     * will use it internally until FastHashMap references are removed.  Use
     * copyMap() instead.
     */
    @Deprecated
    public static FastHashMap copyFastHashMap(final FastHashMap fastHashMap) {
        final FastHashMap results = new FastHashMap();
        @SuppressWarnings("unchecked") // FastHashMap is not generic
        final Iterator<Entry<String, ?>> iterator = fastHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry<String, ?> entry = iterator.next();
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if (value instanceof Msg) {
                results.put(key, ((Msg) value).clone());
            } else if (value instanceof Arg) {
                results.put(key, ((Arg) value).clone());
            } else if (value instanceof Var) {
                results.put(key, ((Var) value).clone());
            } else {
                results.put(key, value);
            }
        }
        results.setFast(true);
        return results;
    }

    /**
     * Makes a deep copy of a <code>Map</code> if the values are
     * <code>Msg</code>, <code>Arg</code>, or <code>Var</code>.  Otherwise,
     * it is a shallow copy.
     *
     * @param map The source Map to copy.
     *
     * @return A copy of the <code>Map</code> that was passed in.
     */
    public static Map<String, Object> copyMap(final Map<String, Object> map) {
        final Map<String, Object> results = new HashMap<>(map.size());
        map.forEach((key, value) -> {
            if (value instanceof Msg) {
                results.put(key, ((Msg) value).clone());
            } else if (value instanceof Arg) {
                results.put(key, ((Arg) value).clone());
            } else if (value instanceof Var) {
                results.put(key, ((Var) value).clone());
            } else {
                results.put(key, value);
            }
        });
        return results;
    }

    /**
     * Convenience method for getting a value from a bean property as a
     * <code>String</code>.  If the property is a <code>String[]</code> or
     * <code>Collection</code> and it is empty, an empty <code>String</code>
     * "" is returned.  Otherwise, property.toString() is returned.  This method
     * may return {@code null} if there was an error retrieving the
     * property.
     *
     * @param bean The bean object.
     * @param property The name of the property to access.
     *
     * @return The value of the property.
     */
    public static String getValueAsString(final Object bean, final String property) {
        Object value = null;

        try {
            value = PropertyUtils.getProperty(bean, property);

        } catch (final ReflectiveOperationException e) {
            LOG.error(e.getMessage(), e);
        }

        if (value == null) {
            return null;
        }

        if (value instanceof String[]) {
            return ((String[]) value).length > 0 ? value.toString() : "";

        }
        if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty() ? "" : value.toString();

        }
        return value.toString();

    }

    /**
     * <p>Replace part of a <code>String</code> with another value.</p>
     *
     * @param value <code>String</code> to perform the replacement on.
     * @param key The name of the constant.
     * @param replaceValue The value of the constant.
     *
     * @return The modified value.
     */
    public static String replace(final String value, final String key, final String replaceValue) {
        if (value == null || key == null || replaceValue == null) {
            return value;
        }
        return value.replace(key, replaceValue);
    }

}
