/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/util/Flags.java,v 1.8 2004/02/21 17:10:30 rleland Exp $
 * $Revision: 1.8 $
 * $Date: 2004/02/21 17:10:30 $
 *
 * ====================================================================
 * Copyright 2003-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
 * are represented by powers of 2.  For example,<br/>
 * Flag 1 = 1<br/>
 * Flag 2 = 2<br/>
 * Flag 3 = 4<br/>
 * Flag 4 = 8<br/><br/>
 * or using shift operator to make numbering easier:<br/>
 * Flag 1 = 1 &lt;&lt; 0<br/>
 * Flag 2 = 1 &lt;&lt; 1<br/>
 * Flag 3 = 1 &lt;&lt; 2<br/>
 * Flag 4 = 1 &lt;&lt; 3<br/>
 * 
 * <p>
 * There cannot be a flag with a value of 3 because that represents Flag 1 
 * and Flag 2 both being on/true.
 * </p>
 */
public class Flags implements Serializable {

    /**
     * Represents the current flag state.
     */
    private long flags = 0;

    /**
     * Create a new Flags object.
     */
    public Flags() {
        super();
    }

    /**
     * Initialize a new Flags object with the given flags.
     */
    public Flags(long flags) {
        super();
        this.flags = flags;
    }

    /**
     * Returns the current flags.
     */
    public long getFlags() {
        return this.flags;
    }

    /**
     * Tests whether the given flag is on.  If the flag is not a power of 2 
     * (ie. 3) this tests whether the combination of flags is on.
     */
    public boolean isOn(long flag) {
        return (this.flags & flag) > 0;
    }

    /**
     * Tests whether the given flag is off.  If the flag is not a power of 2 
     * (ie. 3) this tests whether the combination of flags is off.
     */
    public boolean isOff(long flag) {
        return (this.flags & flag) == 0;
    }

    /**
     * Turns on the given flag.  If the flag is not a power of 2 (ie. 3) this
     * turns on multiple flags.
     */
    public void turnOn(long flag) {
        this.flags |= flag;
    }

    /**
     * Turns off the given flag.  If the flag is not a power of 2 (ie. 3) this
     * turns off multiple flags.
     */
    public void turnOff(long flag) {
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
     * @since Validator 1.1.1
     */
    public void clear() {
        this.flags = 0;
    }

    /**
     * Turn on all 64 flags.
     */
    public void turnOnAll() {
        this.flags = Long.MAX_VALUE;
    }

    /**
     * Clone this Flags object.
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            return super.clone();
        } catch(CloneNotSupportedException e) {
            throw new RuntimeException("Couldn't clone Flags object.");
        }
    }

    /**
     * Tests if two Flags objects are in the same state.
     * @param obj object being tested
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof Flags)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        Flags f = (Flags) obj;

        return this.flags == f.flags;
    }

    /**
     * The hash code is based on the current state of the flags.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return (int) this.flags;
    }

    /**
     * Returns a 64 length String with the first flag on the right and the 
     * 64th flag on the left.  A 1 indicates the flag is on, a 0 means it's 
     * off.
     */
    public String toString() {
        StringBuffer bin = new StringBuffer(Long.toBinaryString(this.flags));
        for (int i = 64 - bin.length(); i > 0; i--) {
            bin.insert(0, "0");
        }
        return bin.toString();
    }

}
