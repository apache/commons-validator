/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/UrlValidator.java,v 1.7 2003/05/03 05:08:01 dgraham Exp $
 * $Revision: 1.7 $
 * $Date: 2003/05/03 05:08:01 $
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
import java.util.HashSet;

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
 * <pre>
 *   Example of usage:
 *   Construct a UrlValidator with valid schemes of "http", and "https".
 *
 *    String[] schemes = {"http","https"}.
 *    Urlvalidator urlValidator = new Urlvalidator(schemes);
 *    if (urlValidator.isValid("ftp")) {
 *       System.out.println("url is valid");
 *    } else {
 *       System.out.println("url is invalid");
 *    }
 *
 *    prints "url is invalid"
 *   If instead the default constructor is used.
 *
 *    Urlvalidator urlValidator = new Urlvalidator();
 *    if (urlValidator.isValid("ftp")) {
 *       System.out.println("url is valid");
 *    } else {
 *       System.out.println("url is invalid");
 *    }
 *
 *   prints out "url is valid"
 *  </pre>
 * @see
 *   <a href='http://www.ietf.org/rfc/rfc2396.txt' >
 *  Uniform Resource Identifiers (URI): Generic Syntax
 *  </a>
 *
 * @author Robert Leland
 * @version $Revision: 1.7 $ $Date: 2003/05/03 05:08:01 $
 */
public class UrlValidator implements Serializable {
   private static final String alphaChars = "a-zA-Z"; 
   private static final String alphaNumChars = alphaChars + "\\d"; 
   private static final String specialChars = ";/@&=,.?:+$";
   private static final String validChars = "[^\\s" + specialChars + "]";

   private static final String schemeChars = alphaChars; // Drop numeric, and  "+-." for now
   private static final String authorityChars = alphaNumChars + "\\-\\.";
   private static final String atom = validChars + '+';

   /**
    * This expression derived/taken from the BNF for URI (RFC2396).
    */
   private static final String urlPat = "/^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?/";
   //                                                                      12            3  4          5       6   7        8 9
   
   /**
    * Schema/Protocol (ie. http:, ftp:, file:, etc).
    */
   private static final int PARSE_URL_SCHEME = 2; 
   
   /**
    * Includes hostname/ip and port number.
    */
   private static final int PARSE_URL_AUTHORITY = 4;
    
   private static final int PARSE_URL_PATH = 5;
   private static final int PARSE_URL_QUERY = 7;
   private static final int PARSE_URL_FRAGMENT = 9;
   
   /**
    * Protocol (ie. http:, ftp:,https:).
    */
   private static final String schemePat = "/^[" + schemeChars + "]/";
   private static final String authorityPat = "/^([" + authorityChars + "]*)(:\\d*)?(.*)?/";
   //                                                                            1                          2  3       4
   private static final int PARSE_AUTHORITY_HOST_IP = 1;
   private static final int PARSE_AUTHORITY_PORT = 2;
   
   /**
    * Should always be empty.
    */
   private static final int PARSE_AUTHORITY_EXTRA = 3;

   private static final String pathPat = "/^(/[-a-zA-Z0-9_:@&?=+,.!/~*'%$]*)$/";
   private static final String queryPat = "/^(.*)$/";
   private static final String legalAsciiPat = "/^[\\000-\\177]+$/";
   private static final String ipV4DomainPat = "/^(\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})$/";
   private static final String domainPat = "/^" + atom + "(\\." + atom + ")*$/";
   private static final String portPat = "/^:(\\d{1,5})$/";
   private static final String atomPat = "/(" + atom + ")/";
   private static final String alphaPat = "/^[" + alphaChars + "]/";

   // Non static fields
   private boolean allow2Slash = false;
   private boolean allowAllScheme = false;
   private boolean noFragment = false;
   private HashSet allowedSchemeSet;
   protected String[] defaultSchemeSet = {"http", "https", "ftp"};

   /**
    * Create a UrlValidator with default properties.
    */
   public UrlValidator() {
      this(null);
   }

   /**
    * Behavour of validation is modified by passing in several strings options:
    * @param schemes Pass in one or more url scheme to consider valid, passing in
    *        a null will default to "http,https,ftp" being valid.
    *        If a non-null schemes is specified then all valid schemes must
    *        be specified. Setting the allowAllScheme option to true will
    *        ignore the contents of schemes.
    */
   public UrlValidator(String[] schemes) {
      this(schemes, false, false, false);
   }

   /**
    * Behavour of validation is modified by passing in options:
    *   @param allowAllScheme If true, allows all validly formatted schemes. [false]
    *   @param allow2Slash If true, allows double '/' characters in the path  component, [false]
    *   @param noFragment If true, fragments are flagged as illegal.[false]
    */
   public UrlValidator(String[] schemes, boolean allowAllScheme, boolean allow2Slash, boolean noFragment) {
      this.allowAllScheme = allowAllScheme;
      this.allow2Slash = allow2Slash;
      this.noFragment = noFragment;
      
      if (!this.allowAllScheme) {
         if (schemes == null) {
            this.allowedSchemeSet = new HashSet(defaultSchemeSet.length);
            for (int sIndex = 0; sIndex < defaultSchemeSet.length; sIndex++) {
               this.allowedSchemeSet.add(defaultSchemeSet[sIndex]);
            }

         } else if (schemes != null) {
            this.allowedSchemeSet = new HashSet(schemes.length);
            for (int sIndex = 0; sIndex < schemes.length; sIndex++) {
               this.allowedSchemeSet.add(schemes[sIndex]);
            }
         }
      }

   }

    /**
     * <p>Checks if a field has a valid url address.</p>
     *
     * @param value The value validation is being performed on.
     * @return true if the url is valid.
     */
    public boolean isValid(String value) {
    	try {
    		Perl5Util matchUrlPat = new Perl5Util();
    		Perl5Util matchAsciiPat = new Perl5Util();
    
    		if (!matchAsciiPat.match(legalAsciiPat, value)) {
    			return false;
    		}
    
    		// Check the whole url address structure
    		if (!matchUrlPat.match(urlPat, value)) {
    			return false;
    		}
    
    		if (!isValidScheme(matchUrlPat.group(PARSE_URL_SCHEME))) {
    			return false;
    		}
    
    		if (!isValidAuthority(matchUrlPat.group(PARSE_URL_AUTHORITY))) {
    			return false;
    		}
    
    		if (!isValidPath(matchUrlPat.group(PARSE_URL_PATH))) {
    			return false;
    		}
    
    		if (!isValidQuery(matchUrlPat.group(PARSE_URL_QUERY))) {
    			return false;
    		}
    
    		if (!isValidFragment(matchUrlPat.group(PARSE_URL_FRAGMENT))) {
    			return false;
    		}
    
    	} catch (Exception e) {
            // TODO Do we need to catch Exception?
    		return false;
    	}
    
    	return true;
    }

   /**
    * Validate scheme. If schemes[] was initialized to a non null,
    * then only those scheme's are allowed.  Note this is slightly different
    * than for the Constructor.
    * @param scheme The scheme to validate.
    * @return   true is valid.
    */
   protected boolean isValidScheme(String scheme) {
      Perl5Util matchSchemePat = new Perl5Util();
      boolean bValid = matchSchemePat.match(schemePat, scheme);
      if (bValid) {
         if (allowedSchemeSet != null) {
            bValid = allowedSchemeSet.contains(scheme);
         }
      }
      return bValid;
   }

    /**
     * Returns true if the authority is properly formatted.  An authority is the combination
     * of hostname and port.
     */
   protected boolean isValidAuthority(String authority) {
      boolean bValid = true;
      Perl5Util matchAuthorityPat = new Perl5Util();
      Perl5Util matchIPV4Pat = new Perl5Util();
      Perl5Util matchDomainPat = new Perl5Util();
      Perl5Util matchAtomPat = new Perl5Util();
      Perl5Util matchPortPat = new Perl5Util();
      Perl5Util matchAlphaPat = new Perl5Util();

      bValid = matchAuthorityPat.match(authorityPat, authority);

      if (bValid) {
         boolean ipV4Address = false;
         boolean hostname = false;
         // check if authority is IP address or hostname
         String hostIP = matchAuthorityPat.group(PARSE_AUTHORITY_HOST_IP);
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
            String port = matchAuthorityPat.group(PARSE_AUTHORITY_PORT);
            if (port != null) {
               bValid = matchPortPat.match(portPat, port);
            }
         }

         if (bValid) {
            String extra = matchAuthorityPat.group(PARSE_AUTHORITY_EXTRA);
            bValid = ((extra == null) || (extra.length() == 0));
         }


      }
      return bValid;
   }

   protected boolean isValidPath(String path) {
      Perl5Util matchPathPat = new Perl5Util();
      boolean bValid = true;

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
      return bValid;
   }

	/**
	 * Returns true if the query is null or it's a properly formatted query string.
	 */
	protected boolean isValidQuery(String query) {
		if (query == null) {
			return true;
		}

		Perl5Util matchQueryPat = new Perl5Util();
		return matchQueryPat.match(queryPat, query);
	}

	/**
	 * Returns true if the given fragment is null or fragments are allowed.
	 */
	protected boolean isValidFragment(String fragment) {
		if (fragment == null) {
			return true;
		}

		return (noFragment == false);
	}

    
    /**
     * Returns the number of times the token appears in the target.
     */
    protected int countToken(String token, String target) {
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
