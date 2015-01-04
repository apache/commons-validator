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
package org.apache.commons.validator.util;

import junit.framework.TestCase;

/**
 * Test the Flags class.
 *
 * @version $Revision$
 */
public class FlagsTest extends TestCase {

    /**
     * Declare some flags for testing.
     */
    private static final long LONG_FLAG = 1;
    private static final long LONG_FLAG_2 = 2;
    private static final int INT_FLAG = 4;

    /**
     * Constructor for FlagsTest.
     */
    public FlagsTest(String name) {
        super(name);
    }

    public void testHashCode() {
        Flags f = new Flags(45);
        assertEquals(f.hashCode(), 45);
    }

    public void testGetFlags() {
        Flags f = new Flags(45);
        assertEquals(f.getFlags(), 45);
    }

    public void testIsOnOff() {
        Flags f = new Flags();
        f.turnOn(LONG_FLAG);
        f.turnOn(INT_FLAG);
        assertTrue(f.isOn(LONG_FLAG));
        assertTrue(!f.isOff(LONG_FLAG));

        assertTrue(f.isOn(INT_FLAG));
        assertTrue(!f.isOff(INT_FLAG));

        assertTrue(f.isOff(LONG_FLAG_2));
    }

    public void testTurnOnOff() {
    }

    public void testTurnOff() {
    }

    public void testTurnOffAll() {
        Flags f = new Flags(98432);
        f.turnOffAll();
        assertEquals(0, f.getFlags());
    }
    
    public void testClear() {
        Flags f = new Flags(98432);
        f.clear();
        assertEquals(0, f.getFlags());
    }

    public void testTurnOnAll() {
        Flags f = new Flags();
        f.turnOnAll();
        assertEquals(~0, f.getFlags());
    }

    public void testIsOn_isFalseWhenNotAllFlagsInArgumentAreOn() {
        Flags first = new Flags(1);
        long firstAndSecond = 3;
        
        assertFalse(first.isOn(firstAndSecond)); 
    }

    public void testIsOn_isTrueWhenHighOrderBitIsSetAndQueried() {
        Flags allOn = new Flags(~0);
        long highOrderBit = 0x8000000000000000L;
        
        assertTrue(allOn.isOn(highOrderBit));
    }

    /**
     * Test for Object clone()
     */
    public void testClone() {
    }

    /**
     * Test for boolean equals(Object)
     */
    public void testEqualsObject() {
    }

    /**
     * Test for String toString()
     */
    public void testToString() {
        Flags f = new Flags();
        String s = f.toString();
        assertEquals(64, s.length());

        f.turnOn(INT_FLAG);
        s = f.toString();
        assertEquals(64, s.length());

        assertEquals(
            "0000000000000000000000000000000000000000000000000000000000000100",
            s);
    }

}
