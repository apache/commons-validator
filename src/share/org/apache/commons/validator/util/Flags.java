/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/util/Flags.java,v 1.3 2003/08/21 19:40:13 rleland Exp $
 * $Revision: 1.3 $
 * $Date: 2003/08/21 19:40:13 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names, "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
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
 
package org.apache.commons.validator.util;

import java.io.Serializable;

/**
 * Represents a collection of 64 boolean (on/off) flags.  Individual flags are 
 * represented by powers of 2.  For example,<br/>
 * Flag 1 = 1<br/>
 * Flag 2 = 2<br/>
 * Flag 3 = 4<br/>
 * Flag 4 = 8<br/>
 * <p>
 * There cannot be a flag with a value of 3 because that represents Flag 1 and Flag
 * 2 both being on/true.
 * </p>    
 * <p>
 * Typically this class will be used in support of another class with constants
 * and not directly by clients.  
 * </p>
 * 
 * @author David Graham
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
	 * Tests whether the given flag is on.  If the flag is not a power of 2 (ie. 3) this 
	 * tests whether the combination of flags is on.
	 */
	public boolean isOn(long flag) {
		return (this.flags & flag) > 0;
	}

	/**
	 * Tests whether the given flag is off.  If the flag is not a power of 2 (ie. 3) this 
	 * tests whether the combination of flags is off.
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
	 * Turn on all 32 flags.
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
		} catch (CloneNotSupportedException e) {
			throw new InternalError("Couldn't clone Flags object.");
		}
	}

	/**
	 * Tests if two Flags objects are in the same state.
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
	 * Returns a 64 length String with the first flag on the right and the 64th flag on 
	 * the left.  A 1 indicates the flag is on, a 0 means it's off.
	 */
	public String toString() {
		StringBuffer bin = new StringBuffer(Long.toBinaryString(this.flags));
		for (int i = 64 - bin.length(); i >0 ; i--) {
			bin.insert(0, "0");
		}
		return bin.toString();
	}

}
