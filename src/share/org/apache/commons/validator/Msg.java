/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/Msg.java,v 1.8 2003/08/21 19:40:13 rleland Exp $
 * $Revision: 1.8 $
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

package org.apache.commons.validator;

import java.io.Serializable;

/**
 * <p>
 * An alternative message can be associated with a <code>Field</code> 
 * and a pluggable validator instead of using the default message 
 * stored in the <code>ValidatorAction</code> (aka pluggable validator).
 * Instances of this class are configured with a &lt;msg&gt; xml element.
 * </p>
 *
 * @author David Winterfeldt
 * @version $Revision: 1.8 $ $Date: 2003/08/21 19:40:13 $
 */
public class Msg implements Cloneable, Serializable {

    /**
     * The resource bundle name that this Msg's <code>key</code> should be
     * resolved in (optional). 
     * @since Validator 1.1
     */
    protected String bundle = null;

    /**
     * The key or value of the argument.
     */
    protected String key = null;

    /**
     * The name dependency that this argument goes with (optional).
     */
    protected String name = null;

    /**
     * Returns the resource bundle name.
     * @since Validator 1.1
     */
    public String getBundle() {
        return this.bundle;
    }

    /**
     * Sets the resource bundle name.
     * @param bundle The new bundle name.
     * @since Validator 1.1
     */
    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    /**
     * Gets the name of the dependency.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the dependency.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the key/value.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key/value.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Creates and returns a copy of this object.
     */
    public Object clone() {
        try {
            return super.clone();

        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.toString());
        }
    }

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
        StringBuffer results = new StringBuffer();

        results.append("Msg: name=");
        results.append(name);
        results.append("  key=");
        results.append(key);
        results.append("\n");

        return results.toString();
    }

}