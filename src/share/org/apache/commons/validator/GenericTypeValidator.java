/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/GenericTypeValidator.java,v 1.5 2002/10/11 01:16:36 turner Exp $
 * $Revision: 1.5 $
 * $Date: 2002/10/11 01:16:36 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;


/**
 * <p>This class contains basic methods for 
 * performing validations that return the 
 * correctly typed class based on the 
 * validation performed.</p>
 *
 * @author David Winterfeldt
 * @version $Revision: 1.5 $ $Date: 2002/10/11 01:16:36 $
*/
public class GenericTypeValidator implements Serializable {
    
    /**
     * <p>Checks if the value can safely be converted to a byte primitive.</p>
     *
     * @param 	value 		The value validation is being performed on.
    */
    public static Byte formatByte(String value) {
       Byte result = null;
       
       try {
          result = new Byte(value);
       } catch (Exception e) {
       }
       
       return result;
    }

    /**
     * <p>Checks if the value can safely be converted to a short primitive.</p>
     *
     * @param 	value 		The value validation is being performed on.
    */
    public static Short formatShort(String value) {
       Short result = null;
       
       try {
          result = new Short(value);
       } catch (Exception e) {
       }
       
       return result;
    }

    /**
     * <p>Checks if the value can safely be converted to a int primitive.</p>
     *
     * @param 	value 		The value validation is being performed on.
    */
    public static Integer formatInt(String value) {
       Integer result = null;
       
       try {
          result = new Integer(value);
       } catch (Exception e) {
       }
       
       return result;
    }

    /**
     * <p>Checks if the value can safely be converted to a long primitive.</p>
     *
     * @param 	value 		The value validation is being performed on.
    */
    public static Long formatLong(String value) {
       Long result = null;
       
       try {
          result = new Long(value);
       } catch (Exception e) {
       }
       
       return result;
    }

    /**
     * <p>Checks if the value can safely be converted to a float primitive.</p>
     *
     * @param 	value 		The value validation is being performed on.
    */
    public static Float formatFloat(String value) {
       Float result = null;
       
       try {
          result = new Float(value);
       } catch (Exception e) {
       }
       
       return result;
    }

    /**
     * <p>Checks if the value can safely be converted to a double primitive.</p>
     *
     * @param 	value 		The value validation is being performed on.
    */
    public static Double formatDouble(String value) {
       Double result = null;
       
       try {
          result = new Double(value);
       } catch (Exception e) {
       }
       
       return result;
    }

    /**
     * <p>Checks if the field is a valid date.  The <code>Locale</code> is 
     * used with <code>java.text.DateFormat</code>.  The setLenient method 
     * is set to <code>false</code> for all.</p>
     *
     * @param 	value 		The value validation is being performed on.
     * @param 	Locale        	The Locale to use to parse the date (system default if null)
    */
    public static Date formatDate(String value, Locale locale) {
	Date date = null;

	if (value != null) {
	   try {
	      DateFormat formatter = null;
	      if (locale != null) {
	         formatter = DateFormat.getDateInstance(DateFormat.SHORT, locale);
	      } else {
	         formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
	      }
	         
              formatter.setLenient(false);
              
              date = formatter.parse(value);
    	   } catch (ParseException e) {
           }
        }

        return date;
    }	
    
    /**
     * <p>Checks if the field is a valid date.  The pattern is used with 
     * <code>java.text.SimpleDateFormat</code>.  If strict is true, then the 
     * length will be checked so '2/12/1999' will not pass validation with 
     * the format 'MM/dd/yyyy' because the month isn't two digits. 
     * The setLenient method is set to <code>false</code> for all.</p>
     *
     * @param 	value 		The value validation is being performed on.
     * @param 	datePattern	The pattern passed to <code>SimpleDateFormat</code>.
     * @param 	strict	        Whether or not to have an exact match of the datePattern.
    */
    public static Date formatDate(String value, String datePattern, boolean strict) {
        Date date = null;

	if (value != null && datePattern != null && datePattern.length() > 0) {
	   try {
              SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
              formatter.setLenient(false);
              
              date = formatter.parse(value);
              
              if (strict) {
                 if (datePattern.length() != value.length()) {
                    date = null;
                 }
              }
    	   } catch (ParseException e) {
           }
        }

        return date;
    }	

    /**
     * <p>Checks if the field is a valid credit card number.</p>
     * <p>Translated to Java by Ted Husted (<a href="mailto:husted@apache.org">husted@apache.org</a>).<br>
     * &nbsp;&nbsp;&nbsp; Reference Sean M. Burke's script at http://www.ling.nwu.edu/~sburke/pub/luhn_lib.pl</p>
     *
     * @param 	value 		The value validation is being performed on.
    */
    public static Long formatCreditCard(String value) {
       Long result = null;
       
       try {
          if (GenericValidator.validateCreditCardLuhnCheck(value) && GenericValidator.validateCreditCardPrefixCheck(value)) {
             result = new Long(value);
          }
       } catch (Exception e) {
       }
       
       return result;
    }

}
