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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.FastHashMap;// DEPRECATED

/**
 * <p>
 *
 * This contains a set of validation rules for a form/JavaBean. The information
 * is contained in a list of <code>Field</code> objects. Instances of this class
 * are configured with a &lt;form&gt; xml element. </p> <p>
 *
 * The use of FastHashMap is deprecated and will be replaced in a future
 * release. </p>
 */
//TODO mutable non-private fields
public class Form implements Serializable {

    private static final long serialVersionUID = 6445211789563796371L;

    /** The name/key the set of validation rules is stored under. */
    protected String name;

    /**
     * List of <code>Field</code>s. Used to maintain the order they were added
     * in although individual <code>Field</code>s can be retrieved using <code>Map</code>
     * of <code>Field</code>s.
     */
    protected List<Field> lFields = new ArrayList<>();

    /**
     * Map of <code>Field</code>s keyed on their property value.
     *
     * @deprecated   Subclasses should use getFieldMap() instead.
     */
    @Deprecated
    protected FastHashMap hFields = new FastHashMap(); // <String, Field>

    /**
     * The name/key of the form which this form extends from.
     *
     * @since 1.2.0
     */
    protected String inherit;

    /**
     * Whether or not the this <code>Form</code> was processed for replacing
     * variables in strings with their values.
     */
    private boolean processed;

    /**
     * Gets the name/key of the set of validation rules.
     *
     * @return   The name value
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name/key of the set of validation rules.
     *
     * @param name  The new name value
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Add a <code>Field</code> to the <code>Form</code>.
     *
     * @param f  The field
     */
    public void addField(final Field f) {
        this.lFields.add(f);
        getFieldMap().put(f.getKey(), f);
    }

    /**
     * A <code>List</code> of <code>Field</code>s is returned as an unmodifiable
     * <code>List</code>.
     *
     * @return   The fields value
     */
    public List<Field> getFields() {
        return Collections.unmodifiableList(lFields);
    }

    /**
     * Returns the Field with the given name or null if this Form has no such
     * field.
     *
     * @param fieldName  The field name
     * @return           The field value
     * @since 1.1
     */
    public Field getField(final String fieldName) {
        return getFieldMap().get(fieldName);
    }

    /**
     * Returns true if this Form contains a Field with the given name.
     *
     * @param fieldName  The field name
     * @return           True if this form contains the field by the given name
     * @since 1.1
     */
    public boolean containsField(final String fieldName) {
        return getFieldMap().containsKey(fieldName);
    }

    /**
     * Merges the given form into this one. For any field in <code>depends</code>
     * not present in this form, include it. <code>depends</code> has precedence
     * in the way the fields are ordered.
     *
     * @param depends  the form we want to merge
     * @since 1.2.0
     */
    protected void merge(final Form depends) {

        final List<Field> templFields = new ArrayList<>();
        @SuppressWarnings("unchecked") // FastHashMap is not generic
        final
        Map<String, Field> temphFields = new FastHashMap();
        for (final Field defaultField : depends.getFields()) {
            if (defaultField != null) {
                final String fieldKey = defaultField.getKey();
                if (!this.containsField(fieldKey)) {
                    templFields.add(defaultField);
                    temphFields.put(fieldKey, defaultField);
                }
                else {
                    final Field old = getField(fieldKey);
                    getFieldMap().remove(fieldKey);
                    lFields.remove(old);
                    templFields.add(old);
                    temphFields.put(fieldKey, old);
                }
            }
        }
        lFields.addAll(0, templFields);
        getFieldMap().putAll(temphFields);
    }

    /**
     * Processes all of the <code>Form</code>'s <code>Field</code>s.
     *
     * @param globalConstants  A map of global constants
     * @param constants        Local constants
     * @param forms            Map of forms
     * @since 1.2.0
     */
    protected void process(final Map<String, String> globalConstants, final Map<String, String> constants, final Map<String, Form> forms) {
        if (isProcessed()) {
            return;
        }

        int n = 0;//we want the fields from its parent first
        if (isExtending()) {
            final Form parent = forms.get(inherit);
            if (parent != null) {
                if (!parent.isProcessed()) {
                    //we want to go all the way up the tree
                    parent.process(constants, globalConstants, forms);
                }
                for (final Field f : parent.getFields()) {
                    //we want to be able to override any fields we like
                    if (getFieldMap().get(f.getKey()) == null) {
                        lFields.add(n, f);
                        getFieldMap().put(f.getKey(), f);
                        n++;
                    }
                }
            }
        }
        hFields.setFast(true);
        //no need to reprocess parent's fields, we iterate from 'n'
        for (final Iterator<Field> i = lFields.listIterator(n); i.hasNext(); ) {
            final Field f = i.next();
            f.process(globalConstants, constants);
        }

        processed = true;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        final StringBuilder results = new StringBuilder();

        results.append("Form: ");
        results.append(name);
        results.append("\n");

        for (final Field lField : lFields) {
            results.append("\tField: \n");
            results.append(lField);
            results.append("\n");
        }

        return results.toString();
    }

    /**
     * Validate all Fields in this Form on the given page and below.
     *
     * @param params               A Map of parameter class names to parameter
     *      values to pass into validation methods.
     * @param actions              A Map of validator names to ValidatorAction
     *      objects.
     * @param page                 Fields on pages higher than this will not be
     *      validated.
     * @return                     A ValidatorResults object containing all
     *      validation messages.
     * @throws ValidatorException
     */
    ValidatorResults validate(final Map<String, Object> params, final Map<String, ValidatorAction> actions, final int page)
        throws ValidatorException {
        return validate(params, actions, page, null);
    }

    /**
     * Validate all Fields in this Form on the given page and below.
     *
     * @param params               A Map of parameter class names to parameter
     *      values to pass into validation methods.
     * @param actions              A Map of validator names to ValidatorAction
     *      objects.
     * @param page                 Fields on pages higher than this will not be
     *      validated.
     * @return                     A ValidatorResults object containing all
     *      validation messages.
     * @throws ValidatorException
     * @since 1.2.0
     */
    ValidatorResults validate(final Map<String, Object> params, final Map<String, ValidatorAction> actions, final int page, final String fieldName)
        throws ValidatorException {
        final ValidatorResults results = new ValidatorResults();
        params.put(Validator.VALIDATOR_RESULTS_PARAM, results);

        // Only validate a single field if specified
        if (fieldName != null) {
            final Field field = getFieldMap().get(fieldName);

            if (field == null) {
               throw new ValidatorException("Unknown field "+fieldName+" in form "+getName());
            }
            params.put(Validator.FIELD_PARAM, field);

            if (field.getPage() <= page) {
               results.merge(field.validate(params, actions));
            }
        } else {
            for (final Field field : this.lFields) {

                params.put(Validator.FIELD_PARAM, field);

                if (field.getPage() <= page) {
                    results.merge(field.validate(params, actions));
                }
            }
        }

        return results;
    }

    /**
     * Whether or not the this <code>Form</code> was processed for replacing
     * variables in strings with their values.
     *
     * @return   The processed value
     * @since 1.2.0
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Gets the name/key of the parent set of validation rules.
     *
     * @return   The extends value
     * @since 1.2.0
     */
    public String getExtends() {
        return inherit;
    }

    /**
     * Sets the name/key of the parent set of validation rules.
     *
     * @param inherit  The new extends value
     * @since 1.2.0
     */
    public void setExtends(final String inherit) {
        this.inherit = inherit;
    }

    /**
     * Get extends flag.
     *
     * @return   The extending value
     * @since 1.2.0
     */
    public boolean isExtending() {
        return inherit != null;
    }

    /**
     * Returns a Map of String field keys to Field objects.
     *
     * @return   The fieldMap value
     * @since 1.2.0
     */
    @SuppressWarnings("unchecked") // FastHashMap is not generic
    protected Map<String, Field> getFieldMap() {
        return hFields;
    }
}
