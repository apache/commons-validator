/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/util/ValidatorUtils.java,v 1.2 2003/05/25 18:18:31 dgraham Exp $
 * $Revision: 1.2 $
 * $Date: 2003/05/25 18:18:31 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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

package org.apache.commons.validator.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Msg;
import org.apache.commons.validator.Var;

/**
 * <p>Basic utility methods.</p>
 *
 * @author David Winterfeldt
 * @author David Graham
 * @version $Revision: 1.2 $ $Date: 2003/05/25 18:18:31 $
*/
public class ValidatorUtils {

	/**
	 * Logger.
	 */
	private static Log log = LogFactory.getLog(ValidatorUtils.class);

	/**
	 * <p>Replace part of a <code>String</code> with another value.</p>
	 *
	 * @param	value		<code>String</code> to perform the replacement on.
	 * @param	key		The name of the constant.
	 * @param	replaceValue	The value of the constant.
	 */
	public static String replace(
		String value,
		String key,
		String replaceValue) {
		if (value == null || key == null || replaceValue == null) {
			return value;
		}

		int pos = value.indexOf(key);

		if (pos < 0) {
			return value;
		}

		int length = value.length();
		int start = pos;
		int end = pos + key.length();

		if (length == key.length()) {
			value = replaceValue;
		} else if (end == length) {
			value = value.substring(0, start) + replaceValue;
		} else {
			value =
				value.substring(0, start)
					+ replaceValue
					+ replace(value.substring(end), key, replaceValue);
		}

		return value;
	}

	/**
	 * Convenience method for getting a value from a bean property as a 
	 * <code>String</code>.
	 */
	public static String getValueAsString(Object bean, String property) {
		Object value = null;

		try {
			value = PropertyUtils.getProperty(bean, property);

		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			log.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			log.error(e.getMessage(), e);
		}

		return (value != null ? value.toString() : null);
	}

	/**
	 * Makes a deep copy of a <code>FastHashMap</code> if the values 
	 * are <code>Msg</code>, <code>Arg</code>, 
	 * or <code>Var</code>.  Otherwise it is a shallow copy.
	 * 
	 * @param map <code>FastHashMap</code> to copy.
	 * @return FastHashMap A copy of the <code>FastHashMap</code> that was 
	 * passed in.
	 */
	public static FastHashMap copyFastHashMap(FastHashMap map) {
		FastHashMap results = new FastHashMap();

		Iterator i = map.keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			Object value = map.get(key);

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

}
