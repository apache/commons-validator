/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/Arg.java,v 1.10 2003/08/03 17:13:55 dgraham Exp $
 * $Revision: 1.10 $
 * $Date: 2003/08/03 17:13:55 $
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

import java.io.Serializable;

/**
 * <p>
 * A default argument or an argument for a
 * specific validator definition (ex: required)
 * can be stored to pass into a message as parameters.  This can be used in a
 * pluggable validator for constructing locale
 * sensitive messages by using <code>java.text.MessageFormat</code>
 * or an equivalent class.  The resource field can be
 * used to determine if the value stored in the argument
 * is a value to be retrieved from a locale sensitive
 * message retrieval system like <code>java.util.PropertyResourceBundle</code>.
 * The resource field defaults to 'true'.
 * </p>
 * <p>Instances of this class are configured with an &lt;arg&gt; xml element.</p>
 *
 * @author David Winterfeldt
 * @author David Graham
 * @version $Revision: 1.10 $ $Date: 2003/08/03 17:13:55 $
 */
public class Arg implements Cloneable, Serializable {

    /**
     * The key or value of the argument.
     */
    protected String key = null;

    /**
     * The name dependency that this argument goes with (optional).
     */
    protected String name = null;
    
    /**
     * This argument's position in the message (ie. you would set postion=0 to 
     * make a replacement in this string "some msg {0}").
     */
    protected int position = 0;

    /**
     * Whether or not the key is a message resource (optional).  Defaults to 
     * true.  If it is 'true', the value will try to be resolved as a message 
     * resource.
     */
    protected boolean resource = true;

    /**
     * Creates and returns a copy of this object.
     * @return A copy of this object.
     */
    public Object clone() {
        try {
            return super.clone();

        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.toString());
        }
    }

    /**
     * Gets the key/value.
     * @return the key value.
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Gets the name of the dependency.
     * @return the name of the dependency.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return This argument's replacement position.
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * Gets whether or not the key is a resource.
     * @return Returns true if key is a resource.
     * @deprecated Use isResource() instead.
     */
    public boolean getResource() {
        return this.isResource();
    }
    
    /**
     * Tests whether or not the key is a resource key or literal value.
     * @return <code>true</code> if key is a resource key.
     */
    public boolean isResource() {
        return this.resource;
    }

    /**
     * Sets the key/value.
     * @param key They to access the argument.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Sets the name of the dependency.
     * @param name the name of the dependency.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set this argument's replacement position.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Sets whether or not the key is a resource.
     * @param resource If true indicates the key is a resource.
     */
    public void setResource(boolean resource) {
        this.resource = resource;
    }

    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuffer results = new StringBuffer();

        results.append("Arg: name=");
        results.append(name);
        results.append("  key=");
        results.append(key);
        results.append("  resource=");
        results.append(resource);
        results.append("\n");

        return results.toString();
    }

}