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
 * @version $Revision$
 */
//TODO mutable non-private fields
public class Arg implements Cloneable, Serializable {

    private static final long serialVersionUID = -8922606779669839294L;

    /**
     * The resource bundle name that this Arg's <code>key</code> should be
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
     * This argument's position in the message. Set postion=0 to
     * make a replacement in this string: "some msg {0}".
     * @since Validator 1.1
     */
    protected int position = -1;

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

        } catch(CloneNotSupportedException e) {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * Returns the resource bundle name.
     * @return the bundle name.
     * @since Validator 1.1
     */
    public String getBundle() {
        return this.bundle;
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
     * Argument's replacement position.
     * @return This argument's replacement position.
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * Tests whether or not the key is a resource key or literal value.
     * @return <code>true</code> if key is a resource key.
     */
    public boolean isResource() {
        return this.resource;
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
     * @param position set this argument's replacement position.
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
        StringBuilder results = new StringBuilder();

        results.append("Arg: name=");
        results.append(name);
        results.append("  key=");
        results.append(key);
        results.append("  position=");
        results.append(position);
        results.append("  bundle=");
        results.append(bundle);
        results.append("  resource=");
        results.append(resource);
        results.append("\n");

        return results.toString();
    }

}