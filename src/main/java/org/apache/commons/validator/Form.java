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
 *
 * @version $Revision$
 */
//TODO mutable non-private fields
public class Form implements Serializable {

    private static final long serialVersionUID = 6445211789563796371L;

    /** The name/key the set of validation rules is stored under. */
    protected String name = null;

    /**
     * List of <code>Field</code>s. Used to maintain the order they were added
     * in although individual <code>Field</code>s can be retrieved using <code>Map</code>
     * of <code>Field</code>s.
     */
    protected List<Field> lFields = new ArrayList<Field>();

    /**
     * Map of <code>Field</code>s keyed on their property value.
     *
     * @deprecated   Subclasses should use getFieldMap() instead.
     */
    protected FastHashMap hFields = new FastHashMap(); // <String, Field>

    /**
     * The name/key of the form which this form extends from.
     *
     * @since   Validator 1.2.0
     */
    protected String inherit = null;

    /**
     * Whether or not the this <code>Form</code> was processed for replacing
     * variables in strings with their values.
     */
    private boolean processed = false;

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
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Add a <code>Field</code> to the <code>Form</code>.
     *
     * @param f  The field
     */
    public void addField(Field f) {
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
     * @since            Validator 1.1
     */
    public Field getField(String fieldName) {
        return getFieldMap().get(fieldName);
    }

    /**
     * Returns true if this Form contains a Field with the given name.
     *
     * @param fieldName  The field name
     * @return           True if this form contains the field by the given name
     * @since            Validator 1.1
     */
    public boolean containsField(String fieldName) {
        return getFieldMap().containsKey(fieldName);
    }

    /**
     * Merges the given form into this one. For any field in <code>depends</code>
     * not present in this form, include it. <code>depends</code> has precedence
     * in the way the fields are ordered.
     *
     * @param depends  the form we want to merge
     * @since          Validator 1.2.0
     */
    protected void merge(Form depends) {

        List<Field> templFields = new ArrayList<Field>();
        @SuppressWarnings("unchecked") // FastHashMap is not generic
        Map<String, Field> temphFields = new FastHashMap();
        Iterator<Field> dependsIt = depends.getFields().iterator();
        while (dependsIt.hasNext()) {
            Field defaultField = (Field) dependsIt.next();
            if (defaultField != null) {
                String fieldKey = defaultField.getKey();
                if (!this.containsField(fieldKey)) {
                    templFields.add(defaultField);
                    temphFields.put(fieldKey, defaultField);
                }
                else {
                    Field old = getField(fieldKey);
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
     * @since                  Validator 1.2.0
     */
    protected void process(Map<String, String> globalConstants, Map<String, String> constants, Map<String, Form> forms) {
        if (isProcessed()) {
            return;
        }

        int n = 0;//we want the fields from its parent first
        if (isExtending()) {
            Form parent = forms.get(inherit);
            if (parent != null) {
                if (!parent.isProcessed()) {
                    //we want to go all the way up the tree
                    parent.process(constants, globalConstants, forms);
                }
                for (Iterator<Field> i = parent.getFields().iterator(); i.hasNext(); ) {
                    Field f = i.next();
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
        for (Iterator<Field> i = lFields.listIterator(n); i.hasNext(); ) {
            Field f = i.next();
            f.process(globalConstants, constants);
        }

        processed = true;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return string representation
     */
    public String toString() {
        StringBuilder results = new StringBuilder();

        results.append("Form: ");
        results.append(name);
        results.append("\n");

        for (Iterator<Field> i = lFields.iterator(); i.hasNext(); ) {
            results.append("\tField: \n");
            results.append(i.next());
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
    ValidatorResults validate(Map<String, Object> params, Map<String, ValidatorAction> actions, int page)
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
    ValidatorResults validate(Map<String, Object> params, Map<String, ValidatorAction> actions, int page, String fieldName)
        throws ValidatorException {
        ValidatorResults results = new ValidatorResults();
        params.put(Validator.VALIDATOR_RESULTS_PARAM, results);

        // Only validate a single field if specified
        if (fieldName != null) {
            Field field = getFieldMap().get(fieldName);

            if (field == null) {
               throw new ValidatorException("Unknown field "+fieldName+" in form "+getName());
            }
            params.put(Validator.FIELD_PARAM, field);

            if (field.getPage() <= page) {
               results.merge(field.validate(params, actions));
            }
        } else {
            Iterator<Field> fields = this.lFields.iterator();
            while (fields.hasNext()) {
                Field field = fields.next();

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
     * @since    Validator 1.2.0
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Gets the name/key of the parent set of validation rules.
     *
     * @return   The extends value
     * @since    Validator 1.2.0
     */
    public String getExtends() {
        return inherit;
    }

    /**
     * Sets the name/key of the parent set of validation rules.
     *
     * @param inherit  The new extends value
     * @since          Validator 1.2.0
     */
    public void setExtends(String inherit) {
        this.inherit = inherit;
    }

    /**
     * Get extends flag.
     *
     * @return   The extending value
     * @since    Validator 1.2.0
     */
    public boolean isExtending() {
        return inherit != null;
    }

    /**
     * Returns a Map of String field keys to Field objects.
     *
     * @return   The fieldMap value
     * @since    Validator 1.2.0
     */
    @SuppressWarnings("unchecked") // FastHashMap is not generic
    protected Map<String, Field> getFieldMap() {
        return hFields;
    }
}
