/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/Attic/ValidatorUtil.java,v 1.14 2004/02/21 17:10:29 rleland Exp $
 * $Revision: 1.14 $
 * $Date: 2004/02/21 17:10:29 $
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

import org.apache.commons.collections.FastHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Basic utility methods.
 *
 * @deprecated This class has moved to the org.apache.commons.validator.util
 * package.
 */
public class ValidatorUtil {

    /**
     * Delimiter to put around a regular expression following Perl 5 syntax.
     * @deprecated Use "/" directly.
     */
    public final static String REGEXP_DELIMITER = "/";

    /**
     * Logger.
     * @deprecated Subclasses should use their own logging instance.
     */
    protected static Log log = LogFactory.getLog(ValidatorUtil.class);

    /**
     * <p>Replace part of a <code>String</code> with another value.</p>
     *
     * @param    value        <code>String</code> to perform the replacement on.
     * @param    key        The name of the constant.
     * @param    replaceValue    The value of the constant.
     */
    public static String replace(
            String value,
            String key,
            String replaceValue) {

        return org.apache.commons.validator.util.ValidatorUtils.replace(value, key, replaceValue);
    }

    /**
     * Convenience method for getting a value from a bean property as a
     * <code>String</code>.
     */
    public static String getValueAsString(Object bean, String property) {
        return org.apache.commons.validator.util.ValidatorUtils.getValueAsString(bean, property);
    }

    /**
     * Makes a deep copy of a <code>FastHashMap</code> if the values
     * are <code>String</code>, <code>Msg</code>, <code>Arg</code>,
     * or <code>Var</code>.  Otherwise it is a shallow copy.
     *
     * @param map <code>FastHashMap</code> to copy.
     * @return FastHashMap A copy of the <code>FastHashMap</code> that was
     * passed in.
     */
    public static FastHashMap copyFastHashMap(FastHashMap map) {
        return org.apache.commons.validator.util.ValidatorUtils.copyFastHashMap(map);
    }

    /**
     * Adds a '/' on either side of the regular expression.
     * @deprecated Use "/" directly.
     */
    public static String getDelimitedRegExp(String regexp) {
        return (REGEXP_DELIMITER + regexp + REGEXP_DELIMITER);
    }

}
