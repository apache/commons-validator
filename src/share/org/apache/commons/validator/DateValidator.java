/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/DateValidator.java,v 1.1 2003/05/01 02:42:30 dgraham Exp $
 * $Revision: 1.1 $
 * $Date: 2003/05/01 02:42:30 $
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

package org.apache.commons.validator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * <p>Perform date validations.</p>
 * <p>
 * This class is a Singleton; you can retrieve the instance via the getInstance() method.
 * </p>
 *
 * @author David Winterfeldt
 * @author James Turner
 * @author <a href="mailto:husted@apache.org">Ted Husted</a>
 * @author David Graham
 * @version $Revision: 1.1 $
 */
public class DateValidator {

	/**
	 * Singleton instance of this class.
	 */
	private static final DateValidator instance = new DateValidator();

	/**
	 * Returns the Singleton instance of this validator.
	 */
	public static DateValidator getInstance() {
		return instance;
	}

	/**
	 * Protected constructor for subclasses to use.
	 */
	protected DateValidator() {
		super();
	}

	/**
	 * <p>Checks if the field is a valid date.  The pattern is used with
	 * <code>java.text.SimpleDateFormat</code>.  If strict is true, then the
	 * length will be checked so '2/12/1999' will not pass validation with
	 * the format 'MM/dd/yyyy' because the month isn't two digits.
	 * The setLenient method is set to <code>false</code> for all.</p>
	 *
	 * @param value The value validation is being performed on.
	 * @param datePattern The pattern passed to <code>SimpleDateFormat</code>.
	 * @param strict Whether or not to have an exact match of the datePattern.
	 */
	public boolean isValid(String value, String datePattern, boolean strict) {

		if (value == null
			|| datePattern == null
			|| datePattern.length() <= 0) {
                
			return false;
		}

		SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
		formatter.setLenient(false);

		try {
			formatter.parse(value);
		} catch (ParseException e) {
			return false;
		}

		if (strict && (datePattern.length() != value.length())) {
			return false;
		}

		return true;
	}

	/**
	 * <p>Checks if the field is a valid date.  The <code>Locale</code> is
	 * used with <code>java.text.DateFormat</code>.  The setLenient method
	 * is set to <code>false</code> for all.</p>
	 *
	 * @param value The value validation is being performed on.
	 * @param locale The locale to use for the date format, defaults to the default 
	 * system default if null.
	 */
	public boolean isValid(String value, Locale locale) {

		if (value == null) {
			return false;
		}

		DateFormat formatter = null;
		if (locale != null) {
			formatter = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		} else {
			formatter =
				DateFormat.getDateInstance(
					DateFormat.SHORT,
					Locale.getDefault());
		}

		formatter.setLenient(false);

		try {
			formatter.parse(value);
		} catch (ParseException e) {
			return false;
		}

		return true;
	}

}
