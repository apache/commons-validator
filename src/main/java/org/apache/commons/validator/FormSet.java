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
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Holds a set of {@code Form}s stored associated with a {@link Locale}
 * based on the country, language, and variant specified. Instances of this
 * class are configured with a &lt;formset&gt; xml element.
 */
public class FormSet implements Serializable {

    private static final long serialVersionUID = -8936513232763306055L;

    /**
     * This is the type of {@code FormSet}s where no locale is specified.
     */
    protected static final int GLOBAL_FORMSET = 1;

    /**
     * This is the type of {@code FormSet}s where only language locale is
     * specified.
     */
    protected static final int LANGUAGE_FORMSET = 2;

    /**
     * This is the type of {@code FormSet}s where only language and country
     * locale are specified.
     */
    protected static final int COUNTRY_FORMSET = 3;

    /**
     * This is the type of {@code FormSet}s where full locale has been set.
     */
    protected static final int VARIANT_FORMSET = 4;

    /** Logging */
    private transient Log log = LogFactory.getLog(FormSet.class);

    /**
     * Whether or not the this {@code FormSet} was processed for replacing
     * variables in strings with their values.
     */
    private boolean processed;

    /** Language component of {@link Locale} (required). */
    private String language;

    /** Country component of {@link Locale} (optional). */
    private String country;

    /** Variant component of {@link Locale} (optional). */
    private String variant;

    /**
     * A {@link Map} of {@code Form}s using the name field of the
     * {@code Form} as the key.
     */
    private final Map<String, Form> forms = new HashMap<>();

    /**
     * A {@link Map} of {@code Constant}s using the name field of the
     * {@code Constant} as the key.
     */
    private final Map<String, String> constants = new HashMap<>();

    /**
     * Flag indicating if this formSet has been merged with its parent (higher
     * rank in Locale hierarchy).
     */
    private volatile boolean merged;

    /**
     * Constructs a new instance.
     */
    public FormSet() {
        // empty
    }

    /**
     * Add a {@code Constant} to the locale level.
     *
     * @param name   The constant name
     * @param value  The constant value
     */
    public void addConstant(final String name, final String value) {
        if (constants.containsKey(name)) {
            getLog().error("Constant '" + name + "' already exists in FormSet[" + displayKey() + "] - ignoring.");
        } else {
            constants.put(name, value);
        }
    }

    /**
     * Add a {@code Form} to the {@code FormSet}.
     *
     * @param f  The form
     */
    public void addForm(final Form f) {

        final String formName = f.getName();
        if (forms.containsKey(formName)) {
            getLog().error("Form '" + formName + "' already exists in FormSet[" + displayKey() + "] - ignoring.");

        } else {
            forms.put(f.getName(), f);
        }

    }

    /**
     * Returns a string representation of the object's key.
     *
     * @return   A string representation of the key
     */
    public String displayKey() {
        final StringBuilder results = new StringBuilder();
        if (language != null && !language.isEmpty()) {
            results.append("language=");
            results.append(language);
        }
        if (country != null && !country.isEmpty()) {
            if (results.length() > 0) {
                results.append(", ");
            }
            results.append("country=");
            results.append(country);
        }
        if (variant != null && !variant.isEmpty()) {
            if (results.length() > 0) {
                results.append(", ");
            }
            results.append("variant=");
            results.append(variant);
        }
        if (results.length() == 0) {
            results.append("default");
        }

        return results.toString();
    }

    /**
     * Gets the equivalent of the country component of {@link Locale}.
     *
     * @return   The country value
     */
    public String getCountry() {
        return country;
    }

    /**
     * Retrieve a {@code Form} based on the form name.
     *
     * @param formName  The form name
     * @return          The form
     */
    public Form getForm(final String formName) {
        return forms.get(formName);
    }

    /**
     * A {@link Map} of {@code Form}s is returned as an unmodifiable
     * {@link Map} with the key based on the form name.
     *
     * @return   The forms map
     */
    public Map<String, Form> getForms() {
        return Collections.unmodifiableMap(forms);
    }

    /**
     * Gets the equivalent of the language component of {@link Locale}.
     *
     * @return   The language value
     */
    public String getLanguage() {
        return language;
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
            log = LogFactory.getLog(FormSet.class);
        }
        return log;
    }

    /**
     * Returns the type of {@code FormSet}:{@code GLOBAL_FORMSET},
     * {@code LANGUAGE_FORMSET},{@code COUNTRY_FORMSET} or {@code VARIANT_FORMSET}.
     *
     * @return                       The type value
     * @throws NullPointerException  if there is inconsistency in the locale
     *      definition (not sure about this)
     * @since 1.2.0
     */
    protected int getType() {
        final String myLanguage = getLanguage();
        final String myCountry = getCountry();
        if (getVariant() != null) {
            Objects.requireNonNull(myLanguage, "When variant is specified, country and language must be specified.");
            Objects.requireNonNull(myCountry, "When variant is specified, country and language must be specified.");
            return VARIANT_FORMSET;
        }
        if (myCountry != null) {
            Objects.requireNonNull(myLanguage, "When country is specified, language must be specified.");
            return COUNTRY_FORMSET;
        }
        if (myLanguage != null) {
            return LANGUAGE_FORMSET;
        }
        return GLOBAL_FORMSET;
    }

    /**
     * Gets the equivalent of the variant component of {@link Locale}.
     *
     * @return   The variant value
     */
    public String getVariant() {
        return variant;
    }

    /**
     * Has this formSet been merged?
     *
     * @return   true if it has been merged
     * @since 1.2.0
     */
    protected boolean isMerged() {
        return merged;
    }

    /**
     * Whether or not the this {@code FormSet} was processed for replacing
     * variables in strings with their values.
     *
     * @return   The processed value
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Merges the given {@code FormSet} into this one. If any of {@code depends}
     * s {@code Forms} are not in this {@code FormSet} then, include
     * them, else merge both {@code Forms}. Theoretically we should only
     * merge a "parent" formSet.
     *
     * @param depends  FormSet to be merged
     * @since 1.2.0
     */
    protected void merge(final FormSet depends) {
        if (depends != null) {
            final Map<String, Form> myForms = getForms();
            final Map<String, Form> dependsForms = depends.getForms();
            for (final Entry<String, Form> entry : dependsForms.entrySet()) {
                final String key = entry.getKey();
                final Form form = myForms.get(key);
                if (form != null) { // merge, but principal 'rules', don't overwrite
                    // anything
                    form.merge(entry.getValue());
                } else { // just add
                    addForm(entry.getValue());
                }
            }
        }
        merged = true;
    }

    /**
     * Processes all of the {@code Form}s.
     *
     * @param globalConstants  Global constants
     */
    synchronized void process(final Map<String, String> globalConstants) {
        for (final Form f : forms.values()) {
            f.process(globalConstants, constants, forms);
        }

        processed = true;
    }

    /**
     * Sets the equivalent of the country component of {@link Locale}.
     *
     * @param country  The new country value
     */
    public void setCountry(final String country) {
        this.country = country;
    }

    /**
     * Sets the equivalent of the language component of {@link Locale}.
     *
     * @param language  The new language value
     */
    public void setLanguage(final String language) {
        this.language = language;
    }

    /**
     * Sets the equivalent of the variant component of {@link Locale}.
     *
     * @param variant  The new variant value
     */
    public void setVariant(final String variant) {
        this.variant = variant;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return   A string representation
     */
    @Override
    public String toString() {
        final StringBuilder results = new StringBuilder();

        results.append("FormSet: language=");
        results.append(language);
        results.append("  country=");
        results.append(country);
        results.append("  variant=");
        results.append(variant);
        results.append("\n");

        for (final Object name : getForms().values()) {
            results.append("   ");
            results.append(name);
            results.append("\n");
        }

        return results.toString();
    }
}
