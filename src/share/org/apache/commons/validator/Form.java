/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/Form.java,v 1.15 2004/04/04 13:53:25 rleland Exp $
 * $Revision: 1.15 $
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.FastHashMap;

/**
 * <p>
 * This contains a set of validation rules for a form/JavaBean.  The information is
 * contained in a list of <code>Field</code> objects.  Instances of this class are
 * configured with a &lt;form&gt; xml element.
 * </p>
 *
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
	 * The name/key of the form which this form extends from.
	*/
	protected String inherit = null;

	/**
	 * Whether or not the this <code>Form</code> was processed
	 * for replacing variables in strings with their values.
	*/
	private boolean processed = false;

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

		processed = true;
    }

	/**
	 * Processes all of the <code>Form</code>'s <code>Field</code>s.
	*/
	protected void process(Map globalConstants, Map constants, Map forms) {

		if (isProcessed()) {
			return;
		}
		int n = 0; //we want the fields from its parent first
		if (isExtending()) {
			Form parent = (Form) forms.get(inherit);
			if (parent != null) {
				if (!parent.isProcessed()) {
					//we want to go all the way up the tree
					parent.process(constants, globalConstants, forms);
				}
				for (Iterator i = parent.getFields().iterator(); i.hasNext();) {
					Field f = (Field) i.next();
					//we want to be able to override any fields we like
					if (hFields.get(f.getKey()) == null) {
						lFields.add(n, f);
						hFields.put(f.getKey(), f);
						n++;
					}
				}
			}
		}
		hFields.setFast(true);
		//no need to reprocess parent's fields, we iterate from 'n'
		for (Iterator i = lFields.listIterator(n); i.hasNext();) {
			Field f = (Field) i.next();
			f.process(globalConstants, constants);
		}

		processed = true;
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

	/**
 	 * Whether or not the this <code>Form</code> was processed
 	 * for replacing variables in strings with their values.
 	*/
 	public boolean isProcessed() {
 	   return processed;
 	}

 	/**
 	 * Gets the name/key of the parent set of validation rules.
 	 */
 	public String getExtends() {
 		return inherit;
 	}

 	/**
 	 * Sets the name/key of the parent set of validation rules.
 	 */
 	public void setExtends(String string) {
 		inherit = string;
 	}

 	/**
 	 * Get extends flag.
 	 */
 	public boolean isExtending() {
 	  return inherit != null;
 	}
}