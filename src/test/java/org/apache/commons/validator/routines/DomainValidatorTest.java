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
package org.apache.commons.validator.routines;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Tests for the DomainValidator.
 *
 * @version $Revision$
 */
public class DomainValidatorTest extends TestCase {

    private DomainValidator validator;

    public void setUp() {
        validator = DomainValidator.getInstance();
    }

    public void testValidDomains() {
        assertTrue("apache.org should validate", validator.isValid("apache.org"));
        assertTrue("www.google.com should validate", validator.isValid("www.google.com"));

        assertTrue("test-domain.com should validate", validator.isValid("test-domain.com"));
        assertTrue("test---domain.com should validate", validator.isValid("test---domain.com"));
        assertTrue("test-d-o-m-ain.com should validate", validator.isValid("test-d-o-m-ain.com"));
        assertTrue("two-letter domain label should validate", validator.isValid("as.uk"));

        assertTrue("case-insensitive ApAchE.Org should validate", validator.isValid("ApAchE.Org"));

        assertTrue("single-character domain label should validate", validator.isValid("z.com"));

        assertTrue("i.have.an-example.domain.name should validate", validator.isValid("i.have.an-example.domain.name"));
    }

    public void testInvalidDomains() {
        assertFalse("bare TLD .org shouldn't validate", validator.isValid(".org"));
        assertFalse("domain name with spaces shouldn't validate", validator.isValid(" apache.org "));
        assertFalse("domain name containing spaces shouldn't validate", validator.isValid("apa che.org"));
        assertFalse("domain name starting with dash shouldn't validate", validator.isValid("-testdomain.name"));
        assertFalse("domain name ending with dash shouldn't validate", validator.isValid("testdomain-.name"));
        assertFalse("domain name starting with multiple dashes shouldn't validate", validator.isValid("---c.com"));
        assertFalse("domain name ending with multiple dashes shouldn't validate", validator.isValid("c--.com"));
        assertFalse("domain name with invalid TLD shouldn't validate", validator.isValid("apache.rog"));

        assertFalse("URL shouldn't validate", validator.isValid("http://www.apache.org"));
        assertFalse("Empty string shouldn't validate as domain name", validator.isValid(" "));
        assertFalse("Null shouldn't validate as domain name", validator.isValid(null));
    }

    public void testTopLevelDomains() {
        // infrastructure TLDs
        assertTrue(".arpa should validate as iTLD", validator.isValidInfrastructureTld(".arpa"));
        assertFalse(".com shouldn't validate as iTLD", validator.isValidInfrastructureTld(".com"));

        // generic TLDs
        assertTrue(".name should validate as gTLD", validator.isValidGenericTld(".name"));
        assertFalse(".us shouldn't validate as gTLD", validator.isValidGenericTld(".us"));

        // country code TLDs
        assertTrue(".uk should validate as ccTLD", validator.isValidCountryCodeTld(".uk"));
        assertFalse(".org shouldn't validate as ccTLD", validator.isValidCountryCodeTld(".org"));

        // case-insensitive
        assertTrue(".COM should validate as TLD", validator.isValidTld(".COM"));
        assertTrue(".BiZ should validate as TLD", validator.isValidTld(".BiZ"));

        // corner cases
        assertFalse("invalid TLD shouldn't validate", validator.isValid(".nope")); // TODO this is not guaranteed invalid forever
        assertFalse("empty string shouldn't validate as TLD", validator.isValid(""));
        assertFalse("null shouldn't validate as TLD", validator.isValid(null));
    }
    
    public void testAllowLocal() {
       DomainValidator noLocal = DomainValidator.getInstance(false);
       DomainValidator allowLocal = DomainValidator.getInstance(true);
       
       // Default is false, and should use singletons
       assertEquals(noLocal, validator);
       
       // Default won't allow local
       assertFalse("localhost.localdomain should validate", noLocal.isValid("localhost.localdomain"));
       assertFalse("localhost should validate", noLocal.isValid("localhost"));
       
       // But it may be requested
       assertTrue("localhost.localdomain should validate", allowLocal.isValid("localhost.localdomain"));
       assertTrue("localhost should validate", allowLocal.isValid("localhost"));
       assertTrue("hostname should validate", allowLocal.isValid("hostname"));
       assertTrue("machinename should validate", allowLocal.isValid("machinename"));
       
       // Check the localhost one with a few others
       assertTrue("apache.org should validate", allowLocal.isValid("apache.org"));
       assertFalse("domain name with spaces shouldn't validate", allowLocal.isValid(" apache.org "));
    }
    
    public void testIDN() {
       assertTrue("b\u00fccher.ch in IDN should validate", validator.isValid("www.xn--bcher-kva.ch"));
    }

    // Check array is sorted and is lower-case
    public void test_INFRASTRUCTURE_TLDS_sortedAndLowerCase() throws Exception {
        final boolean sorted = isSortedLowerCase("INFRASTRUCTURE_TLDS");
        assertTrue(sorted);
    }

    // Check array is sorted and is lower-case
    public void test_COUNTRY_CODE_TLDS_sortedAndLowerCase() throws Exception {
        final boolean sorted = isSortedLowerCase("COUNTRY_CODE_TLDS");
        assertTrue(sorted);
    }

    // Check array is sorted and is lower-case
    public void test_GENERIC_TLDS_sortedAndLowerCase() throws Exception {
        final boolean sorted = isSortedLowerCase("GENERIC_TLDS");
        assertTrue(sorted);
    }

    // Check array is sorted and is lower-case
    public void test_LOCAL_TLDS_sortedAndLowerCase() throws Exception {
        final boolean sorted = isSortedLowerCase("LOCAL_TLDS");
        assertTrue(sorted);
    }

    // Download and process local copy of http://data.iana.org/TLD/tlds-alpha-by-domain.txt
    // Check if the internal TLD table is up to date
    // Check if the internal TLD tables have any spurious entries
    public static void main(String a[]) throws Exception {
        Set ianaTlds = new HashSet(); // keep for comparison with array contents
        DomainValidator dv = DomainValidator.getInstance();;
        File f = new File("target/tlds-alpha-by-domain.txt");
        String tldurl="http://data.iana.org/TLD/tlds-alpha-by-domain.txt";
        HttpURLConnection hc = (HttpURLConnection) new URL(tldurl).openConnection();
        if (f.canRead()) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");//Sun, 06 Nov 1994 08:49:37 GMT
            long modTime = f.lastModified();
            String since = sdf.format(new Date(modTime));
            hc.addRequestProperty("If-Modified-Since", since);
            System.out.println("Found " + f + " with date " + since);       
        }   
        if (hc.getResponseCode() == 304) {
            System.out.println("Already have most recent " + tldurl);       
        } else {
            System.out.println("Downloading " + tldurl);
            byte buff[] = new byte[1024];
            InputStream is = hc.getInputStream();
            
            FileOutputStream fos = new FileOutputStream(f);
            int len;
            while((len=is.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
            fos.close();
            is.close();
            System.out.println("Done");
        }
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        final String header;
        line = br.readLine(); // header
        if (line.startsWith("# Version ")) {
            header = line.substring(2);
            System.out.println("        // Taken from " + header);
        } else {
            br.close();
            throw new IOException("File does not have expected Version header");
        }
        final Method toUnicode = getIDNMethod();
        if (toUnicode == null) {
            System.err.println("Cannot convert XN-- entries (no access to java.net.IDN)");
        }

        List missing = new ArrayList();
        while((line = br.readLine()) != null) {
            if (!line.startsWith("#")) {
                final String item;
                if (line.startsWith("XN--")) {
                    if (toUnicode != null) {
                        item = toUnicode(toUnicode, line);                        
                    } else {
                        continue;
                    }
                } else {
                    item = line.toLowerCase(Locale.ENGLISH);
                }
                if (!dv.isValidTld(item)) {
                    missing.add(item);
                }
                ianaTlds.add(item);
            }
        }
        br.close();
        if (!missing.isEmpty()) {
            System.out.println("Entries missing from TLD List\n");
            Collections.sort(missing); // XN-- entries are not in sorted order once converted
            for(int i = 0; i < missing.size(); i++) {
              System.out.println("        \""+missing.get(i)+"\",");
            }
            System.out.println("\nDone");
        }
        // Check if internal tables contain any additional entries
        isInIanaList("INFRASTRUCTURE_TLDS", ianaTlds);
        isInIanaList("COUNTRY_CODE_TLDS", ianaTlds);
        isInIanaList("GENERIC_TLDS", ianaTlds);
        // Don't check local TLDS isInIanaList("LOCAL_TLDS", ianaTlds);
    }

    private static String toUnicode(Method m, String line) {
        try {
            return (String) m.invoke(null, new String[]{line.toLowerCase(Locale.ENGLISH)});
        } catch (Exception e) {
        }
        return line;
    }

    private static Method getIDNMethod() {
        try {
            Class clazz = Class.forName("java.net.IDN", false, DomainValidatorTest.class.getClassLoader());
            return clazz.getDeclaredMethod("toUnicode", new Class[]{String.class});
        } catch (Exception e) {
          return null;
        }
    }

    // isInIanaList and isSorted are split into two methods.
    // If/when access to the arrays is possible without reflection, the intermediate
    // methods can be dropped
    private static boolean isInIanaList(String arrayName, Set ianaTlds) throws Exception {
        Field f = DomainValidator.class.getDeclaredField(arrayName);
        final boolean isPrivate = Modifier.isPrivate(f.getModifiers());
        if (isPrivate) {
            f.setAccessible(true);
        }
        String[] array = (String[]) f.get(null);
        try {
            return isInIanaList(arrayName, array, ianaTlds);
        } finally {
            if (isPrivate) {
                f.setAccessible(false);
            }
        }
    }

    private static boolean isInIanaList(String name, String [] array, Set ianaTlds) {
        for(int i = 0; i < array.length; i++) {
            if (!ianaTlds.contains(array[i])) {
                System.out.println(name + " contains unexpected value: " + array[i]);
            }
        }
        return true;
    }

    private boolean isSortedLowerCase(String arrayName) throws Exception {
        Field f = DomainValidator.class.getDeclaredField(arrayName);
        final boolean isPrivate = Modifier.isPrivate(f.getModifiers());
        if (isPrivate) {
            f.setAccessible(true);
        }
        String[] array = (String[]) f.get(null);
        try {
            return isSortedLowerCase(arrayName, array);
        } finally {
            if (isPrivate) {
                f.setAccessible(false);
            }
        }
    }

    private static boolean isLowerCase(String string) {
        return string.equals(string.toLowerCase(Locale.ENGLISH));
    }

    // Check if an array is strictly sorted - and lowerCase
    private static boolean isSortedLowerCase(String name, String [] array) {
        boolean sorted = true;
        boolean strictlySorted = true;
        final int length = array.length;
        boolean lowerCase = isLowerCase(array[length-1]); // Check the last entry
        for(int i = 0; i < length-1; i++) { // compare all but last entry with next
            final String entry = array[i];
            final int cmp = entry.compareTo(array[i+1]);
            if (cmp > 0) { // out of order
                sorted = false;
            } else if (cmp == 0) {
                strictlySorted = false;
                System.out.println("Duplicated entry: " + entry + " in " + name);
            }
            if (!isLowerCase(entry)) {
                System.out.println("Non lowerCase entry: " + entry + " in " + name);
                lowerCase = false;
            }
        }
        if (!sorted) {
            System.out.println("Resorted: " + name);
            Arrays.sort(array);
            for(int i = 0; i < length; i++) {
                System.out.println("        \"" + array[i] + "\",");
            }
        }
        return sorted && strictlySorted && lowerCase;
    }
}
