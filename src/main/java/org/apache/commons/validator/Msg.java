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
package org.apache.commons.validator;

import java.io.Serializable;

/**
 * An alternative message can be associated with a {@code Field}
 * and a pluggable validator instead of using the default message
 * stored in the {@code ValidatorAction} (aka pluggable validator).
 * Instances of this class are configured with a &lt;msg&gt; xml element.
 */
//TODO mutable non-private fields
public class Msg implements Cloneable, Serializable {

    private static final long serialVersionUID = 5690015734364127124L;

    /**
     * The resource bundle name that this Msg's {@code key} should be
     * resolved in (optional).
     * @since 1.1
     */
    protected String bundle;

    /**
     * The key or value of the argument.
     */
    protected String key;

    /**
     * The name dependency that this argument goes with (optional).
     */
    protected String name;

    /**
     * Whether or not the key is a message resource (optional).  Defaults to
     * true.  If it is 'true', the value will try to be resolved as a message
     * resource.
     * @since 1.1.4
     */
    protected boolean resource = true;

    /**
     * Constructs a new instance.
     */
    public Msg() {
        // empty
    }

    /**
     * Creates and returns a copy of this object.
     * @return A copy of the Msg.
     */
    @Override
    public Object clone() {
        try {
            return super.clone();

        } catch (final CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e.toString(), e);
        }
    }

    /**
     * Returns the resource bundle name.
     * @return The bundle name.
     * @since 1.1
     */
    public String getBundle() {
        return bundle;
    }

    /**
     * Gets the key/value.
     * @return The message key/value.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the name of the dependency.
     * @return The dependency name.
     */
    public String getName() {
        return name;
    }

    /**
     * Tests whether or not the key is a resource key or literal value.
     * @return {@code true} if key is a resource key.
     * @since 1.1.4
     */
    public boolean isResource() {
        return resource;
    }

    /**
     * Sets the resource bundle name.
     * @param bundle The new bundle name.
     * @since 1.1
     */
    public void setBundle(final String bundle) {
        this.bundle = bundle;
    }

    /**
     * Sets the key/value.
     * @param key The message key/value.
     */
    public void setKey(final String key) {
        this.key = key;
    }

    /**
     * Sets the name of the dependency.
     * @param name The dependency name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets whether or not the key is a resource.
     * @param resource If true indicates the key is a resource.
     * @since 1.1.4
     */
    public void setResource(final boolean resource) {
        this.resource = resource;
    }

    /**
     * Returns a string representation of the object.
     * @return Msg String representation.
     */
    @Override
    public String toString() {
        final StringBuilder results = new StringBuilder();

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