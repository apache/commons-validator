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
 * An alternative message can be associated with a <code>Field</code>
 * and a pluggable validator instead of using the default message
 * stored in the <code>ValidatorAction</code> (aka pluggable validator).
 * Instances of this class are configured with a &lt;msg&gt; xml element.
 *
 * @version $Revision$
 */
//TODO mutable non-private fields
public class Msg implements Cloneable, Serializable {

    private static final long serialVersionUID = 5690015734364127124L;

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
     * Whether or not the key is a message resource (optional).  Defaults to
     * true.  If it is 'true', the value will try to be resolved as a message
     * resource.
     * @since Validator 1.1.4
     */
    protected boolean resource = true;

    /**
     * Returns the resource bundle name.
     * @return The bundle name.
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
     * @return The dependency name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the dependency.
     * @param name The dependency name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the key/value.
     * @return The message key/value.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key/value.
     * @param key The message key/value.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Tests whether or not the key is a resource key or literal value.
     * @return <code>true</code> if key is a resource key.
     * @since Validator 1.1.4
     */
    public boolean isResource() {
        return this.resource;
    }

    /**
     * Sets whether or not the key is a resource.
     * @param resource If true indicates the key is a resource.
     * @since Validator 1.1.4
     */
    public void setResource(boolean resource) {
        this.resource = resource;
    }

    /**
     * Creates and returns a copy of this object.
     * @return A copy of the Msg.
     */
    public Object clone() {
        try {
            return super.clone();

        } catch(CloneNotSupportedException e) {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * Returns a string representation of the object.
     * @return Msg String representation.
     */
    public String toString() {
        StringBuilder results = new StringBuilder();

        results.append("Msg: name=");
        results.append(name);
        results.append("  key=");
        results.append(key);
        results.append("  resource=");
        results.append(resource);
        results.append("  bundle=");
        results.append(bundle);
        results.append("\n");

        return results.toString();
    }

}