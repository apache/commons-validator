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

import java.io.Serializable;

/**
 * Represents a collection of 64 boolean (on/off) flags.  Individual flags
 * are represented by powers of 2.  For example,<br>
 * Flag 1 = 1<br>
 * Flag 2 = 2<br>
 * Flag 3 = 4<br>
 * Flag 4 = 8<br><br>
 * or using shift operator to make numbering easier:<br>
 * Flag 1 = 1 &lt;&lt; 0<br>
 * Flag 2 = 1 &lt;&lt; 1<br>
 * Flag 3 = 1 &lt;&lt; 2<br>
 * Flag 4 = 1 &lt;&lt; 3<br>
 *
 * <p>
 * There cannot be a flag with a value of 3 because that represents Flag 1
 * and Flag 2 both being on/true.
 * </p>
 *
 */
public class Flags implements Serializable, Cloneable {

    private static final long serialVersionUID = 8481587558770237995L;

    /**
     * Represents the current flag state.
     */
    private long flags = 0;

    /**
     * Create a new Flags object.
     */
    public Flags() {
    }

    /**
     * Initialize a new Flags object with the given flags.
     *
     * @param flags collection of boolean flags to represent.
     */
    public Flags(final long flags) {
        this.flags = flags;
    }

    /**
     * Returns the current flags.
     *
     * @return collection of boolean flags represented.
     */
    public long getFlags() {
        return this.flags;
    }

    /**
     * Tests whether the given flag is on.  If the flag is not a power of 2
     * (ie. 3) this tests whether the combination of flags is on.
     *
     * @param flag Flag value to check.
     *
     * @return whether the specified flag value is on.
     */
    public boolean isOn(final long flag) {
        return (this.flags & flag) == flag;
    }

    /**
     * Tests whether the given flag is off.  If the flag is not a power of 2
     * (ie. 3) this tests whether the combination of flags is off.
     *
     * @param flag Flag value to check.
     *
     * @return whether the specified flag value is off.
     */
    public boolean isOff(final long flag) {
        return (this.flags & flag) == 0;
    }

    /**
     * Turns on the given flag.  If the flag is not a power of 2 (ie. 3) this
     * turns on multiple flags.
     *
     * @param flag Flag value to turn on.
     */
    public void turnOn(final long flag) {
        this.flags |= flag;
    }

    /**
     * Turns off the given flag.  If the flag is not a power of 2 (ie. 3) this
     * turns off multiple flags.
     *
     * @param flag Flag value to turn off.
     */
    public void turnOff(final long flag) {
        this.flags &= ~flag;
    }

    /**
     * Turn off all flags.
     */
    public void turnOffAll() {
        this.flags = 0;
    }

    /**
     * Turn off all flags.  This is a synonym for <code>turnOffAll()</code>.
     * @since 1.1.1
     */
    public void clear() {
        this.flags = 0;
    }

    /**
     * Turn on all 64 flags.
     */
    public void turnOnAll() {
        this.flags = 0xFFFFFFFFFFFFFFFFL;
    }

    /**
     * Clone this Flags object.
     *
     * @return a copy of this object.
     * @see Object#clone()
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch(final CloneNotSupportedException e) {
            throw new RuntimeException("Couldn't clone Flags object.");
        }
    }

    /**
     * Tests if two Flags objects are in the same state.
     * @param obj object being tested
     * @see Object#equals(Object)
     *
     * @return whether the objects are equal.
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Flags)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        final Flags f = (Flags) obj;

        return this.flags == f.flags;
    }

    /**
     * The hash code is based on the current state of the flags.
     * @see Object#hashCode()
     *
     * @return the hash code for this object.
     */
    @Override
    public int hashCode() {
        return (int) this.flags;
    }

    /**
     * Returns a 64 length String with the first flag on the right and the
     * 64th flag on the left.  A 1 indicates the flag is on, a 0 means it's
     * off.
     *
     * @return string representation of this object.
     */
    @Override
    public String toString() {
        final StringBuilder bin = new StringBuilder(Long.toBinaryString(this.flags));
        for (int i = 64 - bin.length(); i > 0; i--) { // CHECKSTYLE IGNORE MagicNumber
            bin.insert(0, "0");
        }
        return bin.toString();
    }

}
