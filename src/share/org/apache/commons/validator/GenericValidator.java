/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/GenericValidator.java,v 1.4 2002/03/30 04:25:59 dwinterfeldt Exp $
 * $Revision: 1.4 $
 * $Date: 2002/03/30 04:25:59 $
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
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;


/**
 * <p>This class contains basic methods for 
 * performing validations.</p>
 *
 * @author David Winterfeldt
 * @version $Revision: 1.4 $ $Date: 2002/03/30 04:25:59 $
*/
public class GenericValidator implements Serializable {

    /**
     * <p>Checks if the field isn't null and length of the field is greater than zero not 
     * including whitespace.</p>
     *
     * @param 	value 		The value validation is being performed on.
    */
    public static boolean isBlankOrNull(String value) {
       return ((value == null) || (value.trim().length() == 0));
    }

    /**
     * <p>Checks if the value matches the regular expression.</p>
     *
     * @param 	value 		The value validation is being performed on.
     * @param 	regexp		The regular expression.
    */
    public static boolean matchRegexp(String value, String regexp) throws RESyntaxException {
       if (regexp != null && regexp.length() > 0) {
          RE r = new RE(regexp);
          return (r.match(value));
       } else {
       	  return false;
       }
    }        
    

    /**
     * <p>Checks if the value can safely be converted to a byte primitive.</p>
     *
     * @param 	value 		The value validation is being performed on.
    */
    public static boolean isByte(String value) {
       return (GenericTypeValidator.formatByte(value) != null);
    }

    /**
     * <p>Checks if the value can safely be converted to a short primitive.</p>
     *
     * @param 	value 		The value validation is being performed on.
    */
    public static boolean isShort(String value) {
       return (GenericTypeValidator.formatShort(value) != null);
    }

    /**
     * <p>Checks if the value can safely be converted to a int primitive.</p>
     *
     * @param 	value 		The value validation is being performed on.
    */
    public static boolean isInt(String value) {
       return (GenericTypeValidator.formatInt(value) != null);
    }

    /**
     * <p>Checks if the value can safely be converted to a long primitive.</p>
     *
     * @param 	value 		The value validation is being performed on.
    */
    public static boolean isLong(String value) {
       return (GenericTypeValidator.formatLong(value) != null);
    }

    /**
     * <p>Checks if the value can safely be converted to a float primitive.</p>
     *
     * @param 	value 		The value validation is being performed on.
    */
    public static boolean isFloat(String value) {
       return (GenericTypeValidator.formatFloat(value) != null);
    }

    /**
     * <p>Checks if the value can safely be converted to a double primitive.</p>
     *
     * @param 	value 		The value validation is being performed on.
    */
    public static boolean isDouble(String value) {
       return (GenericTypeValidator.formatDouble(value) != null);
    }

    /**
     * <p>Checks if the field is a valid date.  The <code>Locale</code> is 
     * used with <code>java.text.DateFormat</code>.  The setLenient method 
     * is set to <code>false</code> for all.</p>
     *
     * @param 	value 		The value validation is being performed on.
     * @param 	datePattern	The pattern passed to <code>SimpleDateFormat</code>.
    */
    public static boolean isDate(String value, Locale locale) {
	boolean bValid = true;

	if (value != null) {
	   try {
	      DateFormat formatter = null;
	      if (locale != null) {
	         formatter = DateFormat.getDateInstance(DateFormat.SHORT, locale);
	      } else {
	         formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
	      }
	         
              formatter.setLenient(false);
              
              Date date = formatter.parse(value);
    	   } catch (ParseException e) {
    	      bValid = false;
           }
        } else {
           bValid = false;	
        }

        return bValid;
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
    public static boolean isDate(String value, String datePattern, boolean strict) {

	boolean bValid = true;

	if (value != null && datePattern != null && datePattern.length() > 0) {
	   try {
              SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
              formatter.setLenient(false);
              
              Date date = formatter.parse(value);
              
              if (strict) {
                 if (datePattern.length() != value.length()) {
                    bValid = false;
                 }
              }

    	   } catch (ParseException e) {
    	      bValid = false;
           }
        } else {
           bValid = false;	
        }

        return bValid;
    }	


    /**
     * <p>Checks if a value is within a range (min &amp; max specified 
     * in the vars attribute).</p>
     *
     * @param 	value 		The value validation is being performed on.
     * @param 	min		The minimum value of the range.
     * @param 	max		The maximum value of the range.
    */
    public static boolean isInRange(int value, int min, int max) {
       return ((value >= min) && (value <= max));
    }

    /**
     * <p>Checks if the field is a valid credit card number.</p>
     * <p>Translated to Java by Ted Husted (<a href="mailto:husted@apache.org">husted@apache.org</a>).<br>
     * &nbsp;&nbsp;&nbsp; Reference Sean M. Burke's script at http://www.ling.nwu.edu/~sburke/pub/luhn_lib.pl</p>
     *
     * @param 	value 		The value validation is being performed on.
    */
    public static boolean isCreditCard(String value) {
       return (validateCreditCardLuhnCheck(value) && validateCreditCardPrefixCheck(value));
    }
        
    /**
     * <p>Checks for a valid credit card number.</p>
     * <p>Translated to Java by Ted Husted (<a href="mailto:husted@apache.org">husted@apache.org</a>).<br>
     * &nbsp;&nbsp;&nbsp; Reference Sean M. Burke's script at http://www.ling.nwu.edu/~sburke/pub/luhn_lib.pl</p>
     *
     * @param 	cardNumber 		Credit Card Number
    */
    protected static boolean validateCreditCardLuhnCheck(String cardNumber) {
        // number must be validated as 0..9 numeric first!!
        int no_digit = cardNumber.length();
        int oddoeven = no_digit & 1;
        long sum = 0;
        for (int count = 0; count < no_digit; count++) {
           int digit = 0;
           try {
              digit = Integer.parseInt(String.valueOf(cardNumber.charAt(count)));
           } catch (NumberFormatException e) {
              return false;
           }
           if (((count & 1) ^ oddoeven) == 0) { // not
               digit *= 2;
               if (digit > 9) { 
                  digit -= 9;
               }
           }
           sum += digit;
        }
        if (sum == 0) {
           return false;
        }
        
        if (sum % 10 == 0) {
           return true;
        }
        
        return false;
    }

    /**
     * <p>Checks for a valid credit card number.</p>
     * <p>Translated to Java by Ted Husted (<a href="mailto:husted@apache.org">husted@apache.org</a>).<br>
     * &nbsp;&nbsp;&nbsp; Reference Sean M. Burke's script at http://www.ling.nwu.edu/~sburke/pub/luhn_lib.pl</p>
     *
     * @param 	cardNumber 		Credit Card Number
    */
    protected static boolean validateCreditCardPrefixCheck(String cardNumber) {
        final String AX_PREFIX = "34,37,";
        final String VS_PREFIX = "4";
        final String MC_PREFIX = "51,52,53,54,55,";
        final String DS_PREFIX = "6011";

        int length = cardNumber.length();
        if (length < 13) {
           return false;
        }

        boolean valid = false;
        int cardType = 0;

        String prefix2 = cardNumber.substring(0,2) + ",";

        if (AX_PREFIX.indexOf(prefix2) != -1) {
           cardType = 3;
        }
        if (cardNumber.substring(0,1).equals(VS_PREFIX)) {
           cardType = 4;
        }
        if (MC_PREFIX.indexOf(prefix2) != -1) {
           cardType = 5;
        }
        if (cardNumber.substring(0,4).equals(DS_PREFIX)) {
           cardType = 6;
        }

        if ((cardType==3) && (length==15)) {
           valid = true;
        }
        if ((cardType==4) && ((length==13) || (length==16))) {
           valid = true;
        }
        if ((cardType==5) && (length==16)) {
           valid = true;
        }
        if ((cardType==6) && (length==16)) {
           valid = true;
        }

        return valid;
    }

    /**
     * <p>Checks if a field has a valid e-mail address.</p>
     * <p>Based on a script by Sandeep V. Tamhankar (stamhankar@hotmail.com), 
     * http://javascript.internet.com</p>
     *
     * @param 	value 		The value validation is being performed on.
    */
    public static boolean isEmail(String value) {
       boolean bValid = true;
       
       try {
          RE emailPat = new RE("^(.+)@(.+)$");
          String specialChars = "\\(\\)<>@,;:\\\\\\\"\\.\\[\\]";
          // ??? '/' character was removed before '[' and ']'
          // I'm not sure if that was the right thing to do.
          String validChars = "[^\\s" + specialChars + "]";
          String quotedUser = "(\"[^\"]*\")";
          //RE ipDomainPat = new RE("^\\[(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\]$");
          RE ipDomainPat = new RE("^(\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})$");
          String atom = validChars + '+';
          String word = "(" + atom + "|" + quotedUser + ")";
          RE userPat = new RE("^" + word + "(\\." + word + ")*$");
          RE domainPat = new RE("^" + atom + "(\\." + atom +")*$");

	  boolean matchEmailPat = emailPat.match(value);
	  	      
          if (!matchEmailPat) {
             bValid = false;
          }

          if (bValid) {
             if (matchEmailPat) {
                String user = emailPat.getParen(1);
                
                // See if "user" is valid 
                if (!userPat.match(user)) {
                   bValid = false;
                }
             } else {
                bValid = false;
             }
          }
          
          String domain = emailPat.getParen(2);
          
          if (bValid) {
             if (ipDomainPat.match(domain)) {
                 // this is an IP address
                 for (int i = 1; i <= 4; i++) {
                    String ipSegment = ipDomainPat.getParen(i);
                    
                    if (ipSegment != null && ipSegment.length() > 0) {
                       int iIpSegment = 0;
                       
                       try {
                       	  iIpSegment = Integer.parseInt(ipSegment);
                       } catch (Exception e) {
                          bValid = false;
                       }
                       
                       if (iIpSegment > 255) {
                          bValid = false;
                       }
                    } else {
                       bValid = false;
                    }
                 }
             } else {
               if (bValid) {
                  // Domain is symbolic name
                  if (!domainPat.match(domain)) {
                     bValid = false;
                  }
               }
               if (bValid) {
                  RE atomPat = new RE("(" + atom + ")");
                  boolean match = true;
                  int pos = 0;
                  int i = 0;
                  String[] domainSegment = new String[10];
                  
                  while (match) {
                     match = atomPat.match(domain, pos);
                     if (match) {
                        domainSegment[i] = atomPat.getParen(1);
                        pos += domainSegment[i].length() + 1;
                        i++;
                     } else {
                     }
                  }
                  int len = i;
                  if (domainSegment[len - 1].length() < 2 || domainSegment[len - 1].length() > 3) {
                     bValid = false;
                  }
               
                  // Make sure there's a host name preceding the domain.
                  if (len < 2) {
                     bValid = false;
                  }
               }               	
	     }
          }
       } catch (RESyntaxException e) {
          bValid = false;
       } catch (Exception e) {
          bValid = false;
       }
       
       return bValid;
    }

    /**
     * <p>Checks if the value's length is less than or equal to the max.</p>
     *
     * @param 	value 		The value validation is being performed on.
     * @param 	max		The maximum length.
    */
    public static boolean maxLength(String value, int max) {
       return (value.length() <= max);
    } 

    /**
     * <p>Checks if the value's length is greater than or equal to the min.</p>
     *
     * @param 	value 		The value validation is being performed on.
     * @param 	min		The minimum length.
    */
    public static boolean minLength(String value, int min) {
       return (value.length() >= min);
    } 

}
