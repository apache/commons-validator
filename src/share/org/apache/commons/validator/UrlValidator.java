/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/UrlValidator.java,v 1.1 2003/04/30 19:04:24 rleland Exp $
 * $Revision: 1.1 $
 * $Date: 2003/04/30 19:04:24 $
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

import java.io.Serializable;

import org.apache.oro.text.perl.Perl5Util;

/**
 * <p>Validates URL.</p>
 * Behavour of validation is modified by passing in options:
 *   allow2Slash - [FALSE]. which allows double '/' characters in the path  component
 *   noFragment - [FALSE] By default fragments are allowed, if the noFragment option is
 *              included then fragments are flagged as illegal.
 * <p>Originally based in on php script by Debbie Dyer, validation.php v1.2b, Date: 03/07/02,
 * http://javascript.internet.com. However, this validation now bears little resemblance
 * to the PHP original.</p>
 * @see
 *   <a href='http://www.ietf.org/rfc/rfc2396.txt' >
 *  Uniform Resource Identifiers (URI): Generic Syntax
 *  </a>
 *
 * @author Robert Leland
 * @version $Revision: 1.1 $ $Date: 2003/04/30 19:04:24 $
 */
public class UrlValidator implements Serializable {
   /**
    * Allow a double slash in the path componet such that
    * path//file is treated as path/file
    */
   public static final String OPTION_ALLOW_2_SLASH = "allow2Slash";
   public static final String OPTION_NO_FRAGMENT = "noFragment";
   boolean allow2Slash = false;
   boolean noFragment = false;

   public UrlValidator() {

   }

   /**
    * Behavour of validation is modified by passing in several strings options:
    *   allow2Slash - Allows double '/' characters in the path  component
    *   noFragment - If the noFragment option is included then fragments are flagged as illegal.
    **/
   public UrlValidator(String[] options) {
      int optionsIndex = 0;
      while ((options != null) && (optionsIndex < options.length)) {
         String option = options[optionsIndex];
         if ((option != null) && (option.equals(OPTION_ALLOW_2_SLASH))) {
            allow2Slash = true;
         }
         if ((option != null) && (option.equals(OPTION_NO_FRAGMENT))) {
            noFragment = true;
         }
         optionsIndex++;
      }

   }

   /**
    * Behavour of validation is modified by passing in options:
    *   allow2Slash - If True, allows double '/' characters in the path  component
    *   noFragment - If true, fragments are flagged as illegal.
    **/

   public UrlValidator(boolean allow2Slash, boolean noFragment) {
      this.allow2Slash = allow2Slash;
      this.noFragment = noFragment;

   }


   /**
    * <p>Checks if a field has a valid url address.</p>
    *
    * @param 	value 		The value validation is being performed on.
    */
   public boolean isValid(String value) {
      boolean bValid = true;
      try {
         String alphaChars = "a-zA-Z"; //used
         String alphaNumChars = alphaChars + "\\d"; //used
         String specialChars = ";/@&=,.?:+$";
         String validChars = "[^\\s" + specialChars + "]";

         String schemeChars = alphaChars; // Drop numeric, and  "+-." for now
         String authorityChars = alphaNumChars + "\\-\\.";
         String atom = validChars + '+';

         // Each pattern must be surrounded by /
         String legalAsciiPat = ValidatorUtil.getDelimitedRegExp("^[\\000-\\177]+$");
// ----- This expressions derived from the BNF for URI (RFC2396) -------------
         String urlPat = ValidatorUtil.getDelimitedRegExp("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");
         //                                    12            3  4          5       6   7        8 9
         final int PARSE_SCHEME = 2; //Schema, (Protocol), http:, ftp:, file:, ...
         final int PARSE_AUTHORITY = 4; //Includes host/ip port
         final int PARSE_PATH = 5;
         final int PARSE_QUERY = 7;
         final int PARSE_FRAGMENT = 9;
         //Protocol eg: http:, ftp:,https:
         String schemePat = ValidatorUtil.getDelimitedRegExp("^[" + schemeChars + "]");
         String authorityPat = ValidatorUtil.getDelimitedRegExp("^([" + authorityChars + "]*)(:\\d*)?(.*)?");
         //                                          1                          2  3       4
         final int PARSE_HOST_IP = 1;
         final int PARSE_PORT = 2;
         final int PARSE_AUTH_EXTRA = 3;//Should always be empty.
         String ipV4DomainPat = ValidatorUtil.getDelimitedRegExp("^(\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})$");
         String domainPat = ValidatorUtil.getDelimitedRegExp("^" + atom + "(\\." + atom + ")*$");
         String portPat = ValidatorUtil.getDelimitedRegExp("^:(\\d{1,5})$");
         String pathPat = ValidatorUtil.getDelimitedRegExp("^(/[-a-zA-Z0-9_:@&?=+,.!/~*'%$]*)$");
         String queryPat = ValidatorUtil.getDelimitedRegExp("^(.*)$");
         String atomPat = ValidatorUtil.getDelimitedRegExp("(" + atom + ")");
         String alphaPat = ValidatorUtil.getDelimitedRegExp("^[" + alphaChars + "]");

         Perl5Util matchUrlPat = new Perl5Util();
         Perl5Util matchSchemePat = new Perl5Util();
         Perl5Util matchAuthorityPat = new Perl5Util();

         Perl5Util matchIPV4Pat = new Perl5Util();
         Perl5Util matchDomainPat = new Perl5Util();
         Perl5Util matchPortPat = new Perl5Util();
         Perl5Util matchPathPat = new Perl5Util();
         Perl5Util matchQueryPat = new Perl5Util();
         Perl5Util matchAtomPat = new Perl5Util();
         Perl5Util matchAsciiPat = new Perl5Util();

         Perl5Util matchAlphaPat = new Perl5Util();


         if (!matchAsciiPat.match(legalAsciiPat, value)) {
            return false;
         }

         // Check the whole url address structure
         bValid = matchUrlPat.match(urlPat, value);

         if (value.endsWith(".")) {
            bValid = false;
         }

         // Check the scheme component of the url address
         if (bValid) {

            String scheme = matchUrlPat.group(PARSE_SCHEME);

            // See if "scheme" is valid
            bValid = matchSchemePat.match(schemePat, scheme);
         }

         // Check the domain component of the url address
         if (bValid) {
            // Check the whole url address structure
            bValid = matchAuthorityPat.match(authorityPat, matchUrlPat.group(PARSE_AUTHORITY));

            if (bValid) {
               boolean ipV4Address = false;
               boolean hostname = false;
               // check if authority is IP address or hostname
               String hostIP = matchAuthorityPat.group(PARSE_HOST_IP);
               ipV4Address = matchIPV4Pat.match(ipV4DomainPat, hostIP);

               if (ipV4Address) {
                  // this is an IP address so check components
                  for (int i = 1; i <= 4; i++) {
                     String ipSegment = matchIPV4Pat.group(i);
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
                  // Domain is hostname name
                  hostname = matchDomainPat.match(domainPat, hostIP);
               }
               //rightmost hostname will never start with a digit.
               if (hostname) {
                  // this is a hostname authority so check components
                  String[] domainSegment = new String[10];
                  boolean match = true;
                  int segmentCount = 0;
                  int segmentLength = 0;

                  while (match) {
                     match = matchAtomPat.match(atomPat, hostIP);
                     if (match) {
                        domainSegment[segmentCount] = matchAtomPat.group(1);
                        segmentLength = domainSegment[segmentCount].length() + 1;
                        hostIP = (segmentLength >= hostIP.length()) ? "" : hostIP.substring(segmentLength);
                        segmentCount++;
                     }
                  }
                  String topLevel = domainSegment[segmentCount - 1];
                  if (topLevel.length() < 2 || topLevel.length() > 4) {
                     bValid = false;
                  }

                  // First letter of top level must be a alpha
                  boolean isAlpha;
                  isAlpha = matchAlphaPat.match(alphaPat, topLevel.substring(0, 1));
                  if (!isAlpha) {
                     bValid = false;
                  }

                  // Make sure there's a host name preceding the authority.
                  if (segmentCount < 2) {
                     bValid = false;
                  }
               }

               if (bValid) {
                  bValid = (hostname || ipV4Address);
               }

               if (bValid) {
                  String port = matchAuthorityPat.group(PARSE_PORT);
                  if (port != null) {
                     bValid = matchPortPat.match(portPat, port);
                  }
               }

               if (bValid) {
                  String extra = matchAuthorityPat.group(PARSE_AUTH_EXTRA);
                  bValid = ((extra == null) || (extra.length() == 0));
               }


               // Check the path component of the url address
               if (bValid) {

                  String path = matchUrlPat.group(PARSE_PATH);
                  // See if "path" is valid
                  bValid = matchPathPat.match(pathPat, path);
                  if (bValid) {  //Shouldn't end with a '/'
                     bValid = (path.lastIndexOf("/") < (path.length() - 1));
                  }
                  if (bValid) {
                     int slash2Count = countToken("//", path);
                     if (!allow2Slash) {
                        bValid = (slash2Count == 0);
                     }
                     if (bValid) {
                        int slashCount = countToken("/", path);
                        int dot2Count = countToken("..", path);
                        if (dot2Count > 0) {
                           bValid = ((slashCount - slash2Count - 1) > dot2Count);
                        }
                     }
                  }

               }
            }
            // Check the query component of the url address
            if (bValid) {

               String query = matchUrlPat.group(PARSE_QUERY);
               if (null != query) {
                  // See if "query" is valid
                  bValid = matchQueryPat.match(queryPat, query);
               }
            }
            // Check the fragment component of the url address
            if (bValid) {
               String fragment = matchUrlPat.group(PARSE_FRAGMENT);
               if (null != fragment) {
                  bValid = (noFragment == false);
               }
            }

         }
      } catch (Exception e) {
         bValid = false;
      }

      return bValid;
   }

   protected static int countToken(String token, String target) {
      int tokenIndex = 0;
      int count = 0;
      while (tokenIndex != -1) {
         tokenIndex = target.indexOf(token, tokenIndex);
         if (tokenIndex > -1) {
            tokenIndex++;
            count++;
         }
      }
      return count;
   }
}
