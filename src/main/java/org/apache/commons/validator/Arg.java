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
import java.text.MessageFormat;

/**
 * <p>
 * A default argument or an argument for a
 * specific validator definition (ex: required)
 * can be stored to pass into a message as parameters.  This can be used in a
 * pluggable validator for constructing locale
 * sensitive messages by using {@link MessageFormat}
 * or an equivalent class.  The resource field can be
 * used to determine if the value stored in the argument
 * is a value to be retrieved from a locale sensitive
 * message retrieval system like {@code java.util.PropertyResourceBundle}.
 * The resource field defaults to 'true'.
 * </p>
 * <p>Instances of this class are configured with an &lt;arg&gt; xml element.</p>
 */
//TODO mutable non-private fields
public class Arg implements Cloneable, Serializable {

    private static final long serialVersionUID = -8922606779669839294L;

    /**
     * The resource bundle name that this Arg's {@code key} should be
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
     * This argument's position in the message. Set position=0 to
     * make a replacement in this string: "some msg {0}".
     * @since 1.1
     */
    protected int position = -1;

    /**
     * Whether or not the key is a message resource (optional).  Defaults to
     * true.  If it is 'true', the value will try to be resolved as a message
     * resource.
     */
    protected boolean resource = true;

    /**
     * Constructs a new instance.
     */
    public Arg() {
        // empty
    }

    /**
     * Creates and returns a copy of this object.
     * @return A copy of this object.
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
     * Gets the resource bundle name.
     *
     * @return the bundle name.
     * @since 1.1
     */
    public String getBundle() {
        return bundle;
    }

    /**
     * Gets the key/value.
     *
     * @return the key value.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the name of the dependency.
     *
     * @return the name of the dependency.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the replacement position.
     *
     * @return This replacement position.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Tests whether or not the key is a resource key or literal value.
     *
     * @return {@code true} if key is a resource key.
     */
    public boolean isResource() {
        return resource;
    }

    /**
     * Sets the resource bundle name.
     *
     * @param bundle The new bundle name.
     * @since 1.1
     */
    public void setBundle(final String bundle) {
        this.bundle = bundle;
    }

    /**
     * Sets the key/value.
     *
     * @param key They to access the argument.
     */
    public void setKey(final String key) {
        this.key = key;
    }

    /**
     * Sets the name of the dependency.
     *
     * @param name the name of the dependency.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets this argument's replacement position.
     *
     * @param position set this argument's replacement position.
     */
    public void setPosition(final int position) {
        this.position = position;
    }

    /**
     * Sets whether or not the key is a resource.
     *
     * @param resource If true indicates the key is a resource.
     */
    public void setResource(final boolean resource) {
        this.resource = resource;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        // @formatter:off
        return new StringBuilder()
            .append("Arg: name=")
            .append(name)
            .append("  key=")
            .append(key)
            .append("  position=")
            .append(position)
            .append("  bundle=")
            .append(bundle)
            .append("  resource=")
            .append(resource)
            .append("\n")
            .toString();
        // @formatter:on
    }

}