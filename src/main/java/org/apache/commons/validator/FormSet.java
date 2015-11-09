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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Holds a set of <code>Form</code>s stored associated with a <code>Locale</code>
 * based on the country, language, and variant specified. Instances of this
 * class are configured with a &lt;formset&gt; xml element.
 *
 * @version $Revision$
 */
public class FormSet implements Serializable {

    private static final long serialVersionUID = -8936513232763306055L;

    /** Logging */
    private transient Log log = LogFactory.getLog(FormSet.class);

    /**
     * Whether or not the this <code>FormSet</code> was processed for replacing
     * variables in strings with their values.
     */
    private boolean processed = false;

    /** Language component of <code>Locale</code> (required). */
    private String language = null;

    /** Country component of <code>Locale</code> (optional). */
    private String country = null;

    /** Variant component of <code>Locale</code> (optional). */
    private String variant = null;

    /**
     * A <code>Map</code> of <code>Form</code>s using the name field of the
     * <code>Form</code> as the key.
     */
    private final Map<String, Form> forms = new HashMap<String, Form>();

    /**
     * A <code>Map</code> of <code>Constant</code>s using the name field of the
     * <code>Constant</code> as the key.
     */
    private final Map<String, String> constants = new HashMap<String, String>();

    /**
     * This is the type of <code>FormSet</code>s where no locale is specified.
     */
    protected final static int GLOBAL_FORMSET = 1;

    /**
     * This is the type of <code>FormSet</code>s where only language locale is
     * specified.
     */
    protected final static int LANGUAGE_FORMSET = 2;

    /**
     * This is the type of <code>FormSet</code>s where only language and country
     * locale are specified.
     */
    protected final static int COUNTRY_FORMSET = 3;

    /**
     * This is the type of <code>FormSet</code>s where full locale has been set.
     */
    protected final static int VARIANT_FORMSET = 4;

    /**
     * Flag indicating if this formSet has been merged with its parent (higher
     * rank in Locale hierarchy).
     */
    private boolean merged;

    /**
     * Has this formSet been merged?
     *
     * @return   true if it has been merged
     * @since    Validator 1.2.0
     */
    protected boolean isMerged() {
        return merged;
    }

    /**
     * Returns the type of <code>FormSet</code>:<code>GLOBAL_FORMSET</code>,
     * <code>LANGUAGE_FORMSET</code>,<code>COUNTRY_FORMSET</code> or <code>VARIANT_FORMSET</code>
     * .
     *
     * @return                       The type value
     * @since                        Validator 1.2.0
     * @throws NullPointerException  if there is inconsistency in the locale
     *      definition (not sure about this)
     */
    protected int getType() {
        if (getVariant() != null) {
            if (getLanguage() == null || getCountry() == null) {
                throw new NullPointerException(
                    "When variant is specified, country and language must be specified.");
            }
            return VARIANT_FORMSET;
        }
        else if (getCountry() != null) {
            if (getLanguage() == null) {
                throw new NullPointerException(
                    "When country is specified, language must be specified.");
            }
            return COUNTRY_FORMSET;
        }
        else if (getLanguage() != null) {
            return LANGUAGE_FORMSET;
        }
        else {
            return GLOBAL_FORMSET;
        }
    }

    /**
     * Merges the given <code>FormSet</code> into this one. If any of <code>depends</code>
     * s <code>Forms</code> are not in this <code>FormSet</code> then, include
     * them, else merge both <code>Forms</code>. Theoretically we should only
     * merge a "parent" formSet.
     *
     * @param depends  FormSet to be merged
     * @since          Validator 1.2.0
     */
    protected void merge(FormSet depends) {
        if (depends != null) {
            Map<String, Form> pForms = getForms();
            Map<String, Form> dForms = depends.getForms();
            for (Iterator<Entry<String, Form>> it = dForms.entrySet().iterator(); it.hasNext(); ) {
                Entry<String, Form> entry = it.next();
                String key = entry.getKey();
                Form pForm = pForms.get(key);
                if (pForm != null) {//merge, but principal 'rules', don't overwrite
                    // anything
                    pForm.merge(entry.getValue());
                }
                else {//just add
                    addForm(entry.getValue());
                }
            }
        }
        merged = true;
    }

    /**
     * Whether or not the this <code>FormSet</code> was processed for replacing
     * variables in strings with their values.
     *
     * @return   The processed value
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Gets the equivalent of the language component of <code>Locale</code>.
     *
     * @return   The language value
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the equivalent of the language component of <code>Locale</code>.
     *
     * @param language  The new language value
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Gets the equivalent of the country component of <code>Locale</code>.
     *
     * @return   The country value
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the equivalent of the country component of <code>Locale</code>.
     *
     * @param country  The new country value
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the equivalent of the variant component of <code>Locale</code>.
     *
     * @return   The variant value
     */
    public String getVariant() {
        return variant;
    }

    /**
     * Sets the equivalent of the variant component of <code>Locale</code>.
     *
     * @param variant  The new variant value
     */
    public void setVariant(String variant) {
        this.variant = variant;
    }

    /**
     * Add a <code>Constant</code> to the locale level.
     *
     * @param name   The constant name
     * @param value  The constant value
     */
    public void addConstant(String name, String value) {

        if (constants.containsKey(name)) {
            getLog().error("Constant '" + name +  "' already exists in FormSet["
                      + this.displayKey() + "] - ignoring.");

        } else {
            constants.put(name, value);
        }

    }

    /**
     * Add a <code>Form</code> to the <code>FormSet</code>.
     *
     * @param f  The form
     */
    public void addForm(Form f) {

        String formName = f.getName();
        if (forms.containsKey(formName)) {
            getLog().error("Form '" + formName + "' already exists in FormSet["
                      + this.displayKey() + "] - ignoring.");

        } else {
            forms.put(f.getName(), f);
        }

    }

    /**
     * Retrieve a <code>Form</code> based on the form name.
     *
     * @param formName  The form name
     * @return          The form
     */
    public Form getForm(String formName) {
        return (Form) this.forms.get(formName);
    }

    /**
     * A <code>Map</code> of <code>Form</code>s is returned as an unmodifiable
     * <code>Map</code> with the key based on the form name.
     *
     * @return   The forms map
     */
    public Map<String, Form> getForms() {
        return Collections.unmodifiableMap(forms);
    }

    /**
     * Processes all of the <code>Form</code>s.
     *
     * @param globalConstants  Global constants
     */
    synchronized void process(Map<String, String> globalConstants) {
        for (Iterator<Form> i = forms.values().iterator(); i.hasNext(); ) {
            Form f = i.next();
            f.process(globalConstants, constants, forms);
        }

        processed = true;
    }

    /**
     * Returns a string representation of the object's key.
     *
     * @return   A string representation of the key
     */
    public String displayKey() {
        StringBuilder results = new StringBuilder();
        if (language != null && language.length() > 0) {
            results.append("language=");
            results.append(language);
        }
        if (country != null && country.length() > 0) {
            if (results.length() > 0) {
               results.append(", ");
            }
            results.append("country=");
            results.append(country);
        }
        if (variant != null && variant.length() > 0) {
            if (results.length() > 0) {
               results.append(", ");
            }
            results.append("variant=");
            results.append(variant );
        }
        if (results.length() == 0) {
           results.append("default");
        }

        return results.toString();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return   A string representation
     */
    public String toString() {
        StringBuilder results = new StringBuilder();

        results.append("FormSet: language=");
        results.append(language);
        results.append("  country=");
        results.append(country);
        results.append("  variant=");
        results.append(variant);
        results.append("\n");

        for (Iterator<?> i = getForms().values().iterator(); i.hasNext(); ) {
            results.append("   ");
            results.append(i.next());
            results.append("\n");
        }

        return results.toString();
    }

    /**
     * Accessor method for Log instance.
     *
     * The Log instance variable is transient and
     * accessing it through this method ensures it
     * is re-initialized when this instance is
     * de-serialized.
     *
     * @return The Log instance.
     */
    private Log getLog() {
        if (log == null) {
            log =  LogFactory.getLog(FormSet.class);
        }
        return log;
    }
}
