/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/Form.java,v 1.13 2004/02/01 02:25:08 dgraham Exp $
 * $Revision: 1.13 $
 * $Date: 2004/02/01 02:25:08 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names, "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
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