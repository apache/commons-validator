/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/Attic/ValidatorUtil.java,v 1.12 2003/08/21 21:43:06 rleland Exp $
 * $Revision: 1.12 $
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

import org.apache.commons.collections.FastHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Basic utility methods.</p>
 *
 * @author David Winterfeldt
 * @author David Graham
 * @version $Revision: 1.12 $ $Date: 2003/08/21 21:43:06 $
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
