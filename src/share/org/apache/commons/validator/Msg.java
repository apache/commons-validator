/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/Msg.java,v 1.12 2004/02/21 17:10:29 rleland Exp $
 * $Revision: 1.12 $
 * $Date: 2004/02/21 17:10:29 $
 *
 * ====================================================================
 * Copyright 2001-2004 The Apache Software Foundation
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

package org.apache.commons.validator;

import java.io.Serializable;

/**
 * An alternative message can be associated with a <code>Field</code>
 * and a pluggable validator instead of using the default message
 * stored in the <code>ValidatorAction</code> (aka pluggable validator).
 * Instances of this class are configured with a &lt;msg&gt; xml element.
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

        } catch(CloneNotSupportedException e) {
            throw new RuntimeException(e.toString());
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