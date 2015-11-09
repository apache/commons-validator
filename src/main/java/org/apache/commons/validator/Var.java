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
 * A variable that can be associated with a <code>Field</code> for
 * passing in information to a pluggable validator.  Instances of this class are
 * configured with a &lt;var&gt; xml element.
 *
 * @version $Revision$
 */
public class Var implements Cloneable, Serializable {

    private static final long serialVersionUID = -684185211548420224L;

    /**
     * Int Constant for JavaScript type.  This can be used
     * when auto-generating JavaScript.
     */
    public static final String JSTYPE_INT = "int";

    /**
     * String Constant for JavaScript type.  This can be used
     * when auto-generating JavaScript.
     */
    public static final String JSTYPE_STRING = "string";

    /**
     * Regular Expression Constant for JavaScript type.  This can be used
     * when auto-generating JavaScript.
     */
    public static final String JSTYPE_REGEXP = "regexp";

    /**
     * The name of the variable.
     */
    private String name = null;

    /**
     * The key or value the variable.
     */
    private String value = null;

    /**
     * The optional JavaScript type of the variable.
     */
    private String jsType = null;

    /**
     * Whether the variable is a resource [false]
     */
    private boolean resource = false;

    /**
     * The bundle for a variable (when resource = 'true').
     */
    private String bundle = null;

    /**
     * Default Constructor.
     */
    public Var() {
        super();
    }

    /**
     * Constructs a variable with a specified name, value
     * and Javascript type.
     * @param name Variable name.
     * @param value Variable value.
     * @param jsType Variable Javascript type.
     */
    public Var(String name, String value, String jsType) {
        this.name = name;
        this.value = value;
        this.jsType = jsType;
    }

    /**
     * Gets the name of the variable.
     * @return The name of the variable.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the variable.
     * @param name The name of the variable.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the value of the variable.
     * @return The value of the variable.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Sets the value of the variable.
     * @param value The value of the variable.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Tests whether or not the value is a resource key or literal value.
     * @return <code>true</code> if value is a resource key.
     * @since Validator 1.2.0
     */
    public boolean isResource() {
        return this.resource;
    }

    /**
     * Sets whether or not the value is a resource.
     * @param resource If true indicates the value is a resource.
     * @since Validator 1.2.0
     */
    public void setResource(boolean resource) {
        this.resource = resource;
    }

    /**
     * Returns the resource bundle name.
     * @return The bundle name.
     * @since Validator 1.2.0
     */
    public String getBundle() {
        return this.bundle;
    }

    /**
     * Sets the resource bundle name.
     * @param bundle The new bundle name.
     * @since Validator 1.2.0
     */
    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    /**
     * Gets the JavaScript type of the variable.
     * @return The Javascript type of the variable.
     */
    public String getJsType() {
        return this.jsType;
    }

    /**
     * Sets the JavaScript type of the variable.
     * @param jsType The Javascript type of the variable.
     */
    public void setJsType(String jsType) {
        this.jsType = jsType;
    }

    /**
     * Creates and returns a copy of this object.
     * @return A copy of the variable.
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
     * @return A string representation of the variable.
     */
    public String toString() {
        StringBuilder results = new StringBuilder();

        results.append("Var: name=");
        results.append(name);
        results.append("  value=");
        results.append(value);
        results.append("  resource=");
        results.append(resource);
        if (resource) {
            results.append("  bundle=");
            results.append(bundle);
        }
        results.append("  jsType=");
        results.append(jsType);
        results.append("\n");

        return results.toString();
    }

}