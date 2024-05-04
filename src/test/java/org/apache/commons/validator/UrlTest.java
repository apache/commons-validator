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
package org.apache.commons.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Performs Validation Test for url validations.
 *
 * @deprecated to be removed when org.apache.commons.validator.UrlValidator is removed
 */
@Deprecated
public class UrlTest {

    static boolean incrementTestPartsIndex(final int[] testPartsIndex, final Object[] testParts) {
        boolean carry = true; // add 1 to lowest order part.
        boolean maxIndex = true;
        for (int testPartsIndexIndex = testPartsIndex.length - 1; testPartsIndexIndex >= 0; --testPartsIndexIndex) {
            int index = testPartsIndex[testPartsIndexIndex];
            final ResultPair[] part = (ResultPair[]) testParts[testPartsIndexIndex];
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
            maxIndex &= index == part.length - 1;
        }

        return !maxIndex;
    }

    /**
     * Only used to debug the unit tests.
     *
     * @param argv
     */
    public static void main(final String[] argv) {

        final UrlTest fct = new UrlTest();
        fct.setUp();
        fct.testIsValid();
        fct.testIsValidScheme();
    }

    private final boolean printStatus = false;

    private final boolean printIndex = false; // print index that indicates current scheme,host,port,path, query test were using.

    /**
     * The data given below approximates the 4 parts of a URL <scheme>://<authority><path>?<query> except that the port number is broken out of authority to
     * increase the number of permutations. A complete URL is composed of a scheme+authority+port+path+query, all of which must be individually valid for the
     * entire URL to be considered valid.
     */
    ResultPair[] testUrlScheme = { new ResultPair("http://", true), new ResultPair("ftp://", true), new ResultPair("h3t://", true),
            new ResultPair("3ht://", false), new ResultPair("http:/", false), new ResultPair("http:", false), new ResultPair("http/", false),
            new ResultPair("://", false), new ResultPair("", true) };

    ResultPair[] testUrlAuthority = { new ResultPair("www.google.com", true), new ResultPair("go.com", true), new ResultPair("go.au", true),
            new ResultPair("0.0.0.0", true), new ResultPair("255.255.255.255", true), new ResultPair("256.256.256.256", false), new ResultPair("255.com", true),
            new ResultPair("1.2.3.4.5", false), new ResultPair("1.2.3.4.", false), new ResultPair("1.2.3", false), new ResultPair(".1.2.3.4", false),
            new ResultPair("go.a", false), new ResultPair("go.a1a", true), new ResultPair("go.1aa", false), new ResultPair("aaa.", false),
            new ResultPair(".aaa", false), new ResultPair("aaa", false), new ResultPair("", false) };

    ResultPair[] testUrlPort = { new ResultPair(":80", true), new ResultPair(":65535", true), new ResultPair(":0", true), new ResultPair("", true),
            new ResultPair(":-1", false), new ResultPair(":65636", true), new ResultPair(":65a", false) };

    ResultPair[] testPath = { new ResultPair("/test1", true), new ResultPair("/t123", true), new ResultPair("/$23", true), new ResultPair("/..", false),
            new ResultPair("/../", false), new ResultPair("/test1/", true), new ResultPair("", true), new ResultPair("/test1/file", true),
            new ResultPair("/..//file", false), new ResultPair("/test1//file", false) };

    // Test allow2slash, noFragment
    ResultPair[] testUrlPathOptions = { new ResultPair("/test1", true), new ResultPair("/t123", true), new ResultPair("/$23", true),
            new ResultPair("/..", false), new ResultPair("/../", false), new ResultPair("/test1/", true), new ResultPair("/#", false), new ResultPair("", true),
            new ResultPair("/test1/file", true), new ResultPair("/t123/file", true), new ResultPair("/$23/file", true), new ResultPair("/../file", false),
            new ResultPair("/..//file", false), new ResultPair("/test1//file", true), new ResultPair("/#/file", false) };

    ResultPair[] testUrlQuery = { new ResultPair("?action=view", true), new ResultPair("?action=edit&mode=up", true), new ResultPair("", true) };

    Object[] testUrlParts = { testUrlScheme, testUrlAuthority, testUrlPort, testPath, testUrlQuery };

    Object[] testUrlPartsOptions = { testUrlScheme, testUrlAuthority, testUrlPort, testUrlPathOptions, testUrlQuery };

    int[] testPartsIndex = { 0, 0, 0, 0, 0 };
    // Test data for individual url parts
    ResultPair[] testScheme = { new ResultPair("http", true), new ResultPair("ftp", false), new ResultPair("httpd", false), new ResultPair("telnet", false) };

    @BeforeEach
    protected void setUp() {
        for (int index = 0; index < testPartsIndex.length - 1; index++) {
            testPartsIndex[index] = 0;
        }
    }

    @Test
    public void testIsValid() {
        testIsValid(testUrlParts, UrlValidator.ALLOW_ALL_SCHEMES);
        setUp();
        final int options = UrlValidator.ALLOW_2_SLASHES + UrlValidator.ALLOW_ALL_SCHEMES + UrlValidator.NO_FRAGMENTS;

        testIsValid(testUrlPartsOptions, options);
    }

    /**
     * Create set of tests by taking the testUrlXXX arrays and running through all possible permutations of their combinations.
     *
     * @param testObjects Used to create a url.
     */
    void testIsValid(final Object[] testObjects, final int options) {
        final UrlValidator urlVal = new UrlValidator(null, options);
        assertTrue(urlVal.isValid("http://www.google.com"));
        assertTrue(urlVal.isValid("http://www.google.com/"));
        int statusPerLine = 60;
        int printed = 0;
        if (printIndex) {
            statusPerLine = 6;
        }
        do {
            final StringBuilder testBuffer = new StringBuilder();
            boolean expected = true;
            for (int testPartsIndexIndex = 0; testPartsIndexIndex < testPartsIndex.length; ++testPartsIndexIndex) {
                final int index = testPartsIndex[testPartsIndexIndex];
                final ResultPair[] part = (ResultPair[]) testObjects[testPartsIndexIndex];
                testBuffer.append(part[index].item);
                expected &= part[index].valid;
            }
            final String url = testBuffer.toString();
            final boolean result = urlVal.isValid(url);
            assertEquals(expected, result, url);
            if (printStatus) {
                if (printIndex) {
                    System.out.print(testPartsIndextoString());
                } else if (result == expected) {
                    System.out.print('.');
                } else {
                    System.out.print('X');
                }
                printed++;
                if (printed == statusPerLine) {
                    System.out.println();
                    printed = 0;
                }
            }
        } while (incrementTestPartsIndex(testPartsIndex, testObjects));
        if (printStatus) {
            System.out.println();
        }
    }

    @Test
    public void testIsValidScheme() {
        if (printStatus) {
            System.out.print("\n testIsValidScheme() ");
        }
        final String[] schemes = { "http", "gopher" };
        // UrlValidator urlVal = new UrlValidator(schemes,false,false,false);
        final UrlValidator urlVal = new UrlValidator(schemes, 0);
        for (final ResultPair testPair : testScheme) {
            final boolean result = urlVal.isValidScheme(testPair.item);
            assertEquals(testPair.valid, result, testPair.item);
            if (printStatus) {
                if (result == testPair.valid) {
                    System.out.print('.');
                } else {
                    System.out.print('X');
                }
            }
        }
        if (printStatus) {
            System.out.println();
        }

    }

    private String testPartsIndextoString() {
        final StringBuilder carryMsg = new StringBuilder("{");
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

    @Test
    public void testValidateUrl() {
        assertTrue(true);
    }

    @Test
    public void testValidator202() {
        final String[] schemes = { "http", "https" };
        final UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.NO_FRAGMENTS);
        urlValidator.isValid(
                "http://www.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.logoworks.comwww.log");
    }

    @Test
    public void testValidator204() {
        final String[] schemes = { "http", "https" };
        final UrlValidator urlValidator = new UrlValidator(schemes);
        assertTrue(urlValidator.isValid("http://tech.yahoo.com/rc/desktops/102;_ylt=Ao8yevQHlZ4On0O3ZJGXLEQFLZA5"));
    }

}
