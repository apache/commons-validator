/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.validator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test the Flags class.
 */
class FlagsTest {

    /**
     * Declare some flags for testing.
     */
    private static final long LONG_FLAG = 1;
    private static final long LONG_FLAG_2 = 2;
    private static final int INT_FLAG = 4;

    @Test
    void testClear() {
        final Flags f = new Flags(98432);
        f.clear();
        assertEquals(0, f.getFlags());
    }

    /**
     * Test for Object clone()
     */
    @Test
    void testClone() {
    }

    /**
     * Test for boolean equals(Object)
     */
    @Test
    void testEqualsObject() {
    }

    @Test
    void testGetFlags() {
        final Flags f = new Flags(45);
        assertEquals(f.getFlags(), 45);
    }

    @Test
    void testHashCode() {
        final Flags f = new Flags(45);
        assertEquals(f.hashCode(), 45);
    }

    @Test
    void testIsOnIsFalseWhenNotAllFlagsInArgumentAreOn() {
        final Flags first = new Flags(1);
        final long firstAndSecond = 3;

        assertFalse(first.isOn(firstAndSecond));
    }

    @Test
    void testIsOnIsTrueWhenHighOrderBitIsSetAndQueried() {
        final Flags allOn = new Flags(~0);
        final long highOrderBit = 0x8000000000000000L;

        assertTrue(allOn.isOn(highOrderBit));
    }

    @Test
    void testIsOnOff() {
        final Flags f = new Flags();
        f.turnOn(LONG_FLAG);
        f.turnOn(INT_FLAG);
        assertTrue(f.isOn(LONG_FLAG));
        assertFalse(f.isOff(LONG_FLAG));

        assertTrue(f.isOn(INT_FLAG));
        assertFalse(f.isOff(INT_FLAG));

        assertTrue(f.isOff(LONG_FLAG_2));
    }

    /**
     * Test for String toString()
     */
    @Test
    void testToString() {
        final Flags f = new Flags();
        String s = f.toString();
        assertEquals(64, s.length());

        f.turnOn(INT_FLAG);
        s = f.toString();
        assertEquals(64, s.length());

        assertEquals("0000000000000000000000000000000000000000000000000000000000000100", s);
    }

    @Test
    void testTurnOff() {
    }

    @Test
    void testTurnOffAll() {
        final Flags f = new Flags(98432);
        f.turnOffAll();
        assertEquals(0, f.getFlags());
    }

    @Test
    void testTurnOnAll() {
        final Flags f = new Flags();
        f.turnOnAll();
        assertEquals(~0, f.getFlags());
    }

    @Test
    void testTurnOnOff() {
    }

}
