/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/FormSet.java,v 1.16 2004/04/04 13:53:25 rleland Exp $
 * $Revision: 1.16 $
 * $Date: 2004/04/04 13:53:25 $
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Holds a set of <code>Form</code>s stored associated with a
 * <code>Locale</code> based on the country, language, and variant specified.
 * Instances of this class are configured with a &lt;formset&gt; xml element.
 */
public class FormSet implements Serializable {

    /**
     * Whether or not the this <code>FormSet</code> was processed
     * for replacing variables in strings with their values.
     */
    private boolean processed = false;

    /**
     * Language component of <code>Locale</code> (required).
     */
    private String language = null;

    /**
     * Country component of <code>Locale</code> (optional).
     */
    private String country = null;

    /**
     * Variant component of <code>Locale</code> (optional).
     */
    private String variant = null;

    /**
     * A <code>Map</code> of <code>Form</code>s
     * using the name field of the <code>Form</code> as the key.
     */
    private Map forms = new HashMap();

    /**
     * A <code>Map</code> of <code>Constant</code>s
     * using the name field of the <code>Constant</code> as the key.
     */
    private Map constants = new HashMap();

    /**
     * Whether or not the this <code>FormSet</code> was processed
     * for replacing variables in strings with their values.
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Gets the equivalent of the language component of <code>Locale</code>.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the equivalent of the language component of <code>Locale</code>.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Gets the equivalent of the country component of <code>Locale</code>.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the equivalent of the country component of <code>Locale</code>.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the equivalent of the variant component of <code>Locale</code>.
     */
    public String getVariant() {
        return variant;
    }

    /**
     * Sets the equivalent of the variant component of <code>Locale</code>.
     */
    public void setVariant(String variant) {
        this.variant = variant;
    }

    /**
     * Add a <code>Constant</code> (locale level).
     * @deprecated Use addConstant(String, String) instead.
     */
    public void addConstant(Constant c) {
        if (c.getName() != null && c.getName().length() > 0 &&
                c.getValue() != null && c.getValue().length() > 0) {

            constants.put(c.getName(), c.getValue());
        }
    }

    /**
     * Add a <code>Constant</code> to the locale level.
     * @deprecated Use addConstant(String, String) instead.
     */
    public void addConstantParam(String name, String value) {
        if (name != null && name.length() > 0 &&
                value != null && value.length() > 0) {

            constants.put(name, value);
        }
    }

    /**
     * Add a <code>Constant</code> to the locale level.
     */
    public void addConstant(String name, String value) {
        this.constants.put(name, value);
    }

    /**
     * Add a <code>Form</code> to the <code>FormSet</code>.
     */
    public void addForm(Form f) {
        forms.put(f.getName(), f);
    }

    /**
     * Retrieve a <code>Form</code> based on the form name.
     * @deprecated Use getForm(String) instead.
     */
    public Form getForm(Object key) {
        return (Form) this.forms.get(key);
    }

    /**
     * Retrieve a <code>Form</code> based on the form name.
     */
    public Form getForm(String formName) {
        return (Form) this.forms.get(formName);
    }

    /**
     * A <code>Map</code> of <code>Form</code>s is returned as an
     * unmodifiable <code>Map</code> with the key based on the form name.
     */
    public Map getForms() {
        return Collections.unmodifiableMap(forms);
    }

    /**
     * Processes all of the <code>Form</code>s.
     * @deprecated This method is called by the framework.  It will be made protected
     * in a future release.  TODO
     */
    public synchronized void process(Map globalConstants) {
        for (Iterator i = forms.values().iterator(); i.hasNext();) {
            Form f = (Form) i.next();
            f.process(globalConstants, constants, forms);
        }

        processed = true;
    }

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
        StringBuffer results = new StringBuffer();

        results.append("FormSet: language=");
        results.append(language);
        results.append("  country=");
        results.append(country);
        results.append("  variant=");
        results.append(variant);
        results.append("\n");

        for (Iterator i = getForms().values().iterator(); i.hasNext();) {
            results.append("   ");
            results.append(i.next());
            results.append("\n");
        }

        return results.toString();
    }

}