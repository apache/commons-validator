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
 * Typically this class will be used in support of another class with integer constants
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
		// TODO make sure this is right
		StringBuffer bin = new StringBuffer(Long.toBinaryString(this.flags));
		for (int i = 0; i < 64 - bin.length(); i++) {
			bin.insert(0, "0");
		}
		return bin.toString();
	}

}
