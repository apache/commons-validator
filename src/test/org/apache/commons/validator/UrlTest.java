/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/UrlTest.java,v 1.2 2003/05/01 02:40:22 dgraham Exp $
 * $Revision: 1.2 $
 * $Date: 2003/05/01 02:40:22 $
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

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p>Performs Validation Test for url validations.</p>
 *
 * @author Robert Leland
 * @version $Revision: 1.2 $ $Date: 2003/05/01 02:40:22 $
 */
public class UrlTest extends TestCase {
   class TestPair {
      public String item;
      public boolean valid;

      public TestPair(String item, boolean valid) {
         this.item = item;
         this.valid = valid;  //Weather the individual part of url is valid.
      }
   }

   public UrlTest(String testName) {
      super(testName);
   }

   public static Test suite() {
      return new TestSuite(UrlTest.class);
   }

   protected void setUp() {
      for (int index = 0; index < testPartsIndex.length - 1; index++) {
         testPartsIndex[index] = 0;
      }
   }

   protected void tearDown() {
   }

   public void testIsValid() {
      testIsValid(testParts,null);
      System.out.println("\nTesting options ....");
      String[] options = {UrlValidator.OPTION_ALLOW_2_SLASH,UrlValidator.OPTION_NO_FRAGMENT};
      setUp();
      testIsValid(testPartsOptions,options);
   }

   public void testIsValid(Object[] testObjects,String[] options) {
      UrlValidator urlVal = new UrlValidator(options);
      final int statusPerLine = 6;
      int printed = 0;
      do {
         StringBuffer testBuffer = new StringBuffer();
         boolean expected = true;
         for (int testPartsIndexIndex = 0; testPartsIndexIndex < testPartsIndex.length; ++testPartsIndexIndex) {
            int index = testPartsIndex[testPartsIndexIndex];
            TestPair[] part = (TestPair[]) testObjects[testPartsIndexIndex];
            testBuffer.append(part[index].item);
            expected &= part[index].valid;
         }
         String url = testBuffer.toString();
         boolean result = urlVal.isValid(url);
         //System.out.print(testPartsIndextoString());
         assertEquals(url, expected, result);
         printed++;
         if (printed == statusPerLine) {
            //System.out.println();
            printed = 0;
         }
      } while (incrementTestPartsIndex(testPartsIndex, testObjects));
      //System.out.println();
   }

   static boolean incrementTestPartsIndex(int[] testPartsIndex, Object[] testParts) {
      boolean carry = true;  //add 1 to lowest order part.
      boolean maxIndex = true;
      for (int testPartsIndexIndex = testPartsIndex.length - 1; testPartsIndexIndex >= 0; --testPartsIndexIndex) {
         int index = testPartsIndex[testPartsIndexIndex];
         TestPair[] part = (TestPair[]) testParts[testPartsIndexIndex];
         if (carry) {
            if (index < part.length - 1) {
               index++;
               testPartsIndex[testPartsIndexIndex] = index;
               carry = false;
            } else {
               testPartsIndex[testPartsIndexIndex] = 0;
               carry = true;
            }
         }
         maxIndex &= (index == (part.length - 1));
      }


      return (!maxIndex);
   }

   private String testPartsIndextoString() {
      StringBuffer carryMsg = new StringBuffer("{");
      for (int testPartsIndexIndex = 0; testPartsIndexIndex < testPartsIndex.length; ++testPartsIndexIndex) {
         carryMsg.append(testPartsIndex[testPartsIndexIndex]);
         if (testPartsIndexIndex < testPartsIndex.length - 1) {
            carryMsg.append(',');
         } else {
            carryMsg.append('}');
         }
      }
      return carryMsg.toString();

   }

   public void testValidateUrl() {
      assertTrue(true);
   }

   /**
    * Only used to debug the unit tests.
    * @param argv
    */
   public static void main(String[] argv) {

      UrlTest fct = new UrlTest("url test");
      fct.setUp();
      fct.testIsValid();
   }

   /**
    * The data given below approximates the 4 parts of a URL
    * <scheme>://<authority><path>?<query> except that the port number
    * is broken out of authority to increase the number of permutations.
    * A complete URL is composed of a scheme+authority+port+path+query,
    * all of which must be individually valid for the entire URL to be considered
    * valid.
    */
   TestPair[] testScheme = {new TestPair("http://", true),
                            new TestPair("ftp://", true),
                            new TestPair("h3t://", true),
                            new TestPair("3ht://", false),
                            new TestPair("http:/", false),
                            new TestPair("http:", false),
                            new TestPair("http/", false),
                            new TestPair("", true)};
   TestPair[] testAuthority = {new TestPair("www.google.com", true),
                               new TestPair("go.com", true),
                               new TestPair("go.au", true),
                               new TestPair("0.0.0.0", true),
                               new TestPair("255.255.255.255", true),
                               new TestPair("256.256.256.256", false),
                               new TestPair("255.com", true),
                               new TestPair("1.2.3.4.5", false),
                               new TestPair("1.2.3.4.", false),
                               new TestPair("1.2.3", false),
                               new TestPair(".1.2.3.4", false),
                               new TestPair("go.a", false),
                               new TestPair("go.a1a", true),
                               new TestPair("go.1aa", false),
                               new TestPair("aaa.", false),
                               new TestPair(".aaa", false),
                               new TestPair("aaa", false),
                               new TestPair("", false)
   };
   TestPair[] testPort = {new TestPair(":80", true),
                          new TestPair(":65535", true),
                          new TestPair(":0", true),
                          new TestPair("", true),
                          new TestPair(":-1", false),
                          new TestPair(":65636", true),
                          new TestPair(":65a", false)
   };
   TestPair[] testPath = {new TestPair("/test1", true),
                          new TestPair("/t123", true),
                          new TestPair("/$23", true),
                          new TestPair("/..", false),
                          new TestPair("/../", false),
                          new TestPair("/test1/", false),
                          new TestPair("", false),
                          new TestPair("/test1/file", true),
                          new TestPair("/..//file", false),
                          new TestPair("/test1//file", false)
   };
   //Test allow2slash, noFragment
   TestPair[] testPathOptions = {new TestPair("/test1", true),
                                 new TestPair("/t123", true),
                                 new TestPair("/$23", true),
                                 new TestPair("/..", false),
                                 new TestPair("/../", false),
                                 new TestPair("/test1/",false),
                                 new TestPair("/#", false),
                                 new TestPair("", false),
                                 new TestPair("/test1/file", true),
                                 new TestPair("/t123/file", true),
                                 new TestPair("/$23/file", true),
                                 new TestPair("/../file", false),
                                 new TestPair("/..//file", false),
                                 new TestPair("/test1//file", true),
                                 new TestPair("/#/file", false)
   };

   TestPair[] testQuery = {new TestPair("?action=view", true),
                           new TestPair("?action=edit&mode=up", true),
                           new TestPair("", true)
   };

   Object[] testParts = {testScheme, testAuthority, testPort, testPath, testQuery};
   Object[] testPartsOptions = {testScheme, testAuthority, testPort, testPathOptions, testQuery};
   int[] testPartsIndex = {0, 0, 0, 0, 0};
}