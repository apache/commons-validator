/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/Form.java,v 1.14.2.1 2004/06/22 02:24:38 husted Exp $
 * $Revision: 1.14.2.1 $
 * $Date: 2004/06/22 02:24:38 $
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.FastHashMap; // DEPRECATED

/**
 * <p>
 * This contains a set of validation rules for a form/JavaBean.  The information is
 * contained in a list of <code>Field</code> objects.  Instances of this class are
 * configured with a &lt;form&gt; xml element.
 * </p>
 * <p>
 * The use of FastHashMap is deprecated and will be replaced in a future
 * release.
 * </p>
 */
public class Form implements Serializable {

    /**
     * The name/key the set of validation rules is
     * stored under.
     */
    protected String name = null;

    /**
     * List of <code>Field</code>s.  Used to maintain
     * the order they were added in although individual
     * <code>Field</code>s can be retrieved using
     * <code>Map</code> of <code>Field</code>s.
     */
    protected List lFields = new ArrayList();

    /**
     * Map of <code>Field</code>s keyed on their property value.
     */
    protected FastHashMap hFields = new FastHashMap();

    /**
     * Gets the name/key of the set of validation rules.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name/key of the set of validation rules.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Add a <code>Field</code> to the <code>Form</code>.
     */
    public void addField(Field f) {
        this.lFields.add(f);
        this.hFields.put(f.getKey(), f);
    }

    /**
     * A <code>List</code> of <code>Field</code>s is returned as an
     * unmodifiable <code>List</code>.
     */
    public List getFields() {
        return Collections.unmodifiableList(lFields);
    }

    /**
     * The <code>Field</code>s are returned as an unmodifiable <code>Map</code>.
     * @deprecated Use containsField(String) and getField(String) instead.
     */
    public Map getFieldMap() {
        return Collections.unmodifiableMap(hFields);
    }

    /**
     * Returns the Field with the given name or null if this Form has no such
     * field.
     * @since Validator 1.1
     */
    public Field getField(String fieldName) {
        return (Field) this.hFields.get(fieldName);
    }

    /**
     * Returns true if this Form contains a Field with the given name.
     * @since Validator 1.1
     */
    public boolean containsField(String fieldName) {
        return this.hFields.containsKey(fieldName);
    }

    /**
     * Processes all of the <code>Form</code>'s <code>Field</code>s.
     * @deprecated This method is called by the framework.  It will be made protected
     * in a future release.  TODO
     */
    public void process(Map globalConstants, Map constants) {
        hFields.setFast(true);

        for (Iterator i = lFields.iterator(); i.hasNext();) {
            Field f = (Field) i.next();
            f.process(globalConstants, constants);
        }
    }

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
        StringBuffer results = new StringBuffer();

        results.append("Form: ");
        results.append(name);
        results.append("\n");

        for (Iterator i = lFields.iterator(); i.hasNext();) {
            results.append("\tField: \n");
            results.append(i.next());
            results.append("\n");
        }

        return results.toString();
    }
    
    /**
     * Validate all Fields in this Form on the given page and below.
     * @param params A Map of parameter class names to parameter values to pass
     * into validation methods.
     * @param actions A Map of validator names to ValidatorAction objects.
     * @param page Fields on pages higher than this will not be validated.
     * @return A ValidatorResults object containing all validation messages.
     * @throws ValidatorException
     */
    ValidatorResults validate(Map params, Map actions, int page)
        throws ValidatorException {

        ValidatorResults results = new ValidatorResults();

        Iterator fields = this.lFields.iterator();
        while (fields.hasNext()) {
            Field field = (Field) fields.next();

            params.put(Validator.FIELD_PARAM, field);

            if (field.getPage() <= page) {
                results.merge(field.validate(params, actions));
            }
        }

        return results;
    }

}