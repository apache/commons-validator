/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/Field.java,v 1.25 2003/09/28 18:47:32 dgraham Exp $
 * $Revision: 1.25 $
 * $Date: 2003/09/28 18:47:32 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.collections.FastHashMap;
import org.apache.commons.validator.util.ValidatorUtils;

/**
 * <p>
 * This contains the list of pluggable validators to run on a field and any 
 * message information and variables to perform the validations and generate 
 * error messages.  Instances of this class are configured with a 
 * &lt;field&gt; xml element.
 * </p>
 *
 * @author David Winterfeldt
 * @author David Graham
 * @version $Revision: 1.25 $ $Date: 2003/09/28 18:47:32 $
 * @see org.apache.commons.validator.Form
 */
public class Field implements Cloneable, Serializable {

    /**
     * This is the value that will be used as a key if the <code>Arg</code>
     * name field has no value.
     */
    private static final String DEFAULT_ARG =
            "org.apache.commons.validator.Field.DEFAULT";

    /**
     * This is the value that will be used as a key if the <code>Arg</code>
     * name field has no value.
     * @deprecated
     */
    public static final String ARG_DEFAULT = DEFAULT_ARG;

    /**
     * This indicates an indexed property is being referenced.
     */
    public static final String TOKEN_INDEXED = "[]";

    protected static final String TOKEN_START = "${";
    protected static final String TOKEN_END = "}";
    protected static final String TOKEN_VAR = "var:";

    protected String property = null;
    protected String indexedProperty = null;
    protected String indexedListProperty = null;
    protected String key = null;

    /**
     * A comma separated list of validator's this field depends on.
     */
    protected String depends = null;

    protected int page = 0;
    
    protected int fieldOrder = 0;

    /**
     * @deprecated This is no longer used.
     */
    protected FastHashMap hDependencies = new FastHashMap();

    /**
     * Internal representation of this.depends String as a List.  This List 
     * gets updated whenever setDepends() gets called.  This List is 
     * synchronized so a call to setDepends() (which clears the List) won't 
     * interfere with a call to isDependency().
     */
    private List dependencyList = Collections.synchronizedList(new ArrayList());

    protected FastHashMap hVars = new FastHashMap();
    
    protected FastHashMap hMsgs = new FastHashMap();

    /**
     * Holds Maps of arguments.  args[0] returns the Map for the first 
     * replacement argument.
     * @since Validator 1.1
     */
    protected Map[] args = new Map[10];

    /**
     * @deprecated This variable is no longer used, use args instead.
     */
    protected FastHashMap hArg0 = new FastHashMap();

    /**
     * @deprecated This variable is no longer used, use args instead.
     */
    protected FastHashMap hArg1 = new FastHashMap();

    /**
     * @deprecated This variable is no longer used, use args instead.
     */
    protected FastHashMap hArg2 = new FastHashMap();

    /**
     * @deprecated This variable is no longer used, use args instead.
     */
    protected FastHashMap hArg3 = new FastHashMap();

    /**
     * Gets the page value that the Field is associated with for
     * validation.
     */
    public int getPage() {
        return this.page;
    }

    /**
     * Sets the page value that the Field is associated with for
     * validation.
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * Gets the position of the <code>Field</code> in the validation list.
     */
    public int getFieldOrder() {
        return this.fieldOrder;
    }

    /**
     * Sets the position of the <code>Field</code> in the validation list.
     */
    public void setFieldOrder(int fieldOrder) {
        this.fieldOrder = fieldOrder;
    }

    /**
     * Gets the property name of the field.
     */
    public String getProperty() {
        return this.property;
    }

    /**
     * Sets the property name of the field.
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Gets the indexed property name of the field.  This
     * is the method name that can take an <code>int</code> as
     * a parameter for indexed property value retrieval.
     */
    public String getIndexedProperty() {
        return this.indexedProperty;
    }

    /**
     * Sets the indexed property name of the field.
     */
    public void setIndexedProperty(String indexedProperty) {
        this.indexedProperty = indexedProperty;
    }

    /**
     * Gets the indexed property name of the field.  This
     * is the method name that will return an array or a
     * <code>Collection</code> used to retrieve the
     * list and then loop through the list performing the specified
     * validations.
     */
    public String getIndexedListProperty() {
        return this.indexedListProperty;
    }

    /**
     * Sets the indexed property name of the field.
     */
    public void setIndexedListProperty(String indexedListProperty) {
        this.indexedListProperty = indexedListProperty;
    }

    /**
     * Gets the validation rules for this field as a comma separated list.
     */
    public String getDepends() {
        return this.depends;
    }

    /**
     * Sets the validation rules for this field as a comma separated list.
     */
    public void setDepends(String depends) {
        this.depends = depends;

        this.dependencyList.clear();

        StringTokenizer st = new StringTokenizer(depends, ",");
        while (st.hasMoreTokens()) {
            String depend = st.nextToken().trim();

            if (depend != null && depend.length() > 0) {
                this.dependencyList.add(depend);
            }
        }
    }

    /**
     * Add a <code>Msg</code> to the <code>Field</code>.
     */
    public void addMsg(Msg msg) {
        hMsgs.put(msg.getName(), msg.getKey());
    }

    /**
     * Retrieve a message value.
     */
    public String getMsg(String key) {
        return (String) hMsgs.get(key);
    }

    /**
     * Add an <code>Arg</code> to the replacement argument list.
     * @since Validator 1.1
     */
    public void addArg(Arg arg) {
        // TODO this first if check can go away after arg0, etc. are removed from dtd
        if (arg == null || arg.getKey() == null || arg.getKey().length() == 0) {
            return;
        }

        this.ensureArgsCapacity(arg);

        Map argMap = this.args[arg.getPosition()];
        if (argMap == null) {
            argMap = new HashMap();
            this.args[arg.getPosition()] = argMap;
        }

        if (arg.getName() == null) {
            argMap.put(DEFAULT_ARG, arg);
        } else {
            argMap.put(arg.getName(), arg);
        }

    }

    /**
     * Ensures that the args array can hold the given arg.  Resizes the array as
     * necessary.
     * @param arg Determine if the args array is long enough to store this arg's
     * position.
     */
    private void ensureArgsCapacity(Arg arg) {
        if (arg.getPosition() >= this.args.length) {
            Map[] newArgs = new Map[arg.getPosition() + 1];
            System.arraycopy(this.args, 0, newArgs, 0, this.args.length);
            this.args = newArgs;
        }
    }

    /**
     * Gets the default <code>Arg</code> object at the given position.
     * @return The default Arg or null if not found.
     * @since Validator 1.1
     */
    public Arg getArg(int position) {
        return this.getArg(DEFAULT_ARG, position);
    }

    /**
     * Gets the <code>Arg</code> object at the given position.  If the key
     * finds a <code>null</code> value then the default value will be 
     * retrieved.
     * @param key The name the Arg is stored under.  If not found, the default 
     * Arg for the given position (if any) will be retrieved.
     * @param position The Arg number to find.
     * @return The Arg with the given name and position or null if not found.
     * @since Validator 1.1
     */
    public Arg getArg(String key, int position) {
        if ((position >= this.args.length) || (this.args[position] == null)) {
            return null;
        }

        Arg arg = (Arg) args[position].get(key);

        // Didn't find default arg so exit, otherwise we would get into 
        // infinite recursion
        if ((arg == null) && key.equals(DEFAULT_ARG)) {
            return null;
        }

        return (arg == null) ? this.getArg(position) : arg;
    }

    /**
     * Add a <code>Arg</code> to the arg0 list.
     * @deprecated Use addArg(Arg) instead.
     */
    public void addArg0(Arg arg) {
        arg.setPosition(0);
        this.addArg(arg);
    }

    /**
     * Gets the default arg0 <code>Arg</code> object.
     * @deprecated Use getArg(0) instead.
     */
    public Arg getArg0() {
        return this.getArg(0);
    }

    /**
     * Gets the arg0 <code>Arg</code> object based on the key passed in.  If 
     * the key finds a <code>null</code> value then the default value will 
     * be retrieved.
     * @deprecated Use getArg(String, 0) instead.
     */
    public Arg getArg0(String key) {
        return this.getArg(key, 0);
    }

    /**
     * Add a <code>Arg</code> to the arg1 list.
     * @deprecated Use addArg(Arg) instead.
     */
    public void addArg1(Arg arg) {
        arg.setPosition(1);
        this.addArg(arg);
    }

    /**
     * Gets the default arg1 <code>Arg</code> object.
     * @deprecated Use getArg(1) instead.
     */
    public Arg getArg1() {
        return this.getArg(1);
    }

    /**
     * Gets the arg1 <code>Arg</code> object based on the key passed in.  If the key
     * finds a <code>null</code> value then the default value will try to be retrieved.
     * @deprecated Use getArg(String, 1) instead.
     */
    public Arg getArg1(String key) {
        return this.getArg(key, 1);
    }

    /**
     * Add a <code>Arg</code> to the arg2 list.
     * @deprecated Use addArg(Arg) instead.
     */
    public void addArg2(Arg arg) {
        arg.setPosition(2);
        this.addArg(arg);
    }

    /**
     * Gets the default arg2 <code>Arg</code> object.
     * @deprecated Use getArg(2) instead.
     */
    public Arg getArg2() {
        return this.getArg(2);
    }

    /**
     * Gets the arg2 <code>Arg</code> object based on the key passed in.  If the key
     * finds a <code>null</code> value then the default value will try to be retrieved.
     * @deprecated Use getArg(String, 2) instead.
     */
    public Arg getArg2(String key) {
        return this.getArg(key, 2);
    }

    /**
     * Add a <code>Arg</code> to the arg3 list.
     * @deprecated Use addArg(Arg) instead.
     */
    public void addArg3(Arg arg) {
        arg.setPosition(3);
        this.addArg(arg);
    }

    /**
     * Gets the default arg3 <code>Arg</code> object.
     * @deprecated Use getArg(3) instead.
     */
    public Arg getArg3() {
        return this.getArg(3);
    }

    /**
     * Gets the arg3 <code>Arg</code> object based on the key passed in.  If the key
     * finds a <code>null</code> value then the default value will try to be retrieved.
     * @deprecated Use getArg(String, 3) instead.
     */
    public Arg getArg3(String key) {
        return this.getArg(key, 3);
    }

    /**
     * Add a <code>Var</code> to the <code>Field</code>.
     */
    public void addVar(Var v) {
        this.hVars.put(v.getName(), v);
    }

    /**
     * Add a <code>Var</code>, based on the values passed in, to the
     * <code>Field</code>.
     * @deprecated Use addVar(String, String, String) instead.
     */
    public void addVarParam(String name, String value, String jsType) {
        this.addVar(new Var(name, value, jsType));
    }

    /**
     * Add a <code>Var</code>, based on the values passed in, to the
     * <code>Field</code>.
     * @param name
     * @param value
     * @param jsType
     */
    public void addVar(String name, String value, String jsType) {
        this.addVar(new Var(name, value, jsType));
    }

    /**
     * Retrieve a variable.
     * @param mainKey
     */
    public Var getVar(String mainKey) {
        return (Var) hVars.get(mainKey);
    }

    /**
     * Retrieve a variable's value.
     * @param mainKey
     */
    public String getVarValue(String mainKey) {
        String value = null;

        Object o = hVars.get(mainKey);
        if (o != null && o instanceof Var) {
            Var v = (Var) o;
            value = v.getValue();
        }

        return value;
    }

    /**
     * The <code>Field</code>'s variables are returned as an
     * unmodifiable <code>Map</code>.
     */
    public Map getVars() {
        return Collections.unmodifiableMap(hVars);
    }

    /**
     * Gets a unique key based on the property and indexedProperty fields.
     */
    public String getKey() {
        if (this.key == null) {
            this.generateKey();
        }

        return this.key;
    }

    /**
     * Sets a unique key for the field.  This can be used to change
     * the key temporarily to have a unique key for an indexed field.
     * @param key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * If there is a value specified for the indexedProperty field then
     * <code>true</code> will be returned.  Otherwise it will be 
     * <code>false</code>.
     */
    public boolean isIndexed() {
        return ((indexedListProperty != null && indexedListProperty.length() > 0));
    }

    /**
     * Generate correct <code>key</code> value.
     */
    public void generateKey() {
        if (this.isIndexed()) {
            this.key = this.indexedListProperty + TOKEN_INDEXED + "." + this.property;
        } else {
            this.key = this.property;
        }
    }

    /**
     * Replace constants with values in fields and process the depends field
     * to create the dependency <code>Map</code>.
     * @deprecated This method is called by the framework.  It will be made protected
     * in a future release.  TODO
     */
    public void process(Map globalConstants, Map constants) {
        this.hMsgs.setFast(false);
        this.hVars.setFast(true);

        this.generateKey();

        // Process FormSet Constants
        for (Iterator i = constants.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            String key2 = TOKEN_START + key + TOKEN_END;
            String replaceValue = (String) constants.get(key);

            property = ValidatorUtils.replace(property, key2, replaceValue);

            processVars(key2, replaceValue);

            this.processMessageComponents(key2, replaceValue);
        }

        // Process Global Constants
        for (Iterator i = globalConstants.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            String key2 = TOKEN_START + key + TOKEN_END;
            String replaceValue = (String) globalConstants.get(key);

            property = ValidatorUtils.replace(property, key2, replaceValue);

            processVars(key2, replaceValue);

            this.processMessageComponents(key2, replaceValue);
        }

        // Process Var Constant Replacement
        for (Iterator i = hVars.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            String key2 = TOKEN_START + TOKEN_VAR + key + TOKEN_END;
            Var var = this.getVar(key);
            String replaceValue = var.getValue();

            this.processMessageComponents(key2, replaceValue);
        }

        hMsgs.setFast(true);
    }

    /**
     * Replace the vars value with the key/value pairs passed in.
     */
    private void processVars(String key, String replaceValue) {
        Iterator i = this.hVars.keySet().iterator();
        while (i.hasNext()) {
            String varKey = (String) i.next();
            Var var = this.getVar(varKey);

            var.setValue(ValidatorUtils.replace(var.getValue(), key, replaceValue));
        }

    }

    /**
     * Replace the args key value with the key/value pairs passed in.
     * @deprecated This is an internal setup method that clients don't need to call.
     */
    public void processMessageComponents(String key, String replaceValue) {
        this.internalProcessMessageComponents(key, replaceValue);
    }

    /**
     * Replace the args key value with the key/value pairs passed in.
     * TODO When processMessageComponents() is removed from the public API we
     * should rename this private method to "processMessageComponents".
     */
    private void internalProcessMessageComponents(String key, String replaceValue) {
        String varKey = TOKEN_START + TOKEN_VAR;
        // Process Messages
        if (key != null && !key.startsWith(varKey)) {
            for (Iterator i = hMsgs.keySet().iterator(); i.hasNext();) {
                String msgKey = (String) i.next();
                String value = this.getMsg(msgKey);

                hMsgs.put(msgKey, ValidatorUtils.replace(value, key, replaceValue));
            }
        }

        this.processArg(key, replaceValue);
    }

    /**
     * Replace the arg <code>Collection</code> key value with the key/value 
     * pairs passed in.
     */
    private void processArg(String key, String replaceValue) {
        for (int i = 0; i < this.args.length; i++) {

            Map argMap = this.args[i];
            if (argMap == null) {
                continue;
            }

            Iterator iter = argMap.values().iterator();
            while (iter.hasNext()) {
                Arg arg = (Arg) iter.next();

                if (arg != null) {
                    arg.setKey(
                            ValidatorUtils.replace(arg.getKey(), key, replaceValue));
                }
            }
        }
    }

    /**
     * Checks if the validator is listed as a dependency.
     */
    public boolean isDependency(String validatorName) {
        return this.dependencyList.contains(validatorName);
    }

    /**
     * Gets an unmodifiable <code>Set</code> of the dependencies.
     * @deprecated Use getDependencyList() instead.
     */
    public Collection getDependencies() {
        return this.getDependencyList();
    }

    /**
     * Gets an unmodifiable <code>List</code> of the dependencies in the same 
     * order they were defined in parameter passed to the setDepends() method.
     */
    public List getDependencyList() {
        return Collections.unmodifiableList(this.dependencyList);
    }


    /**
     * Creates and returns a copy of this object.
     */
    public Object clone() {
        Field field = null;
        try {
            field = (Field) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new InternalError(e.toString());
        }

        field.args = new Map[this.args.length];
        for (int i = 0; i < this.args.length; i++) {
            if (this.args[i] == null) {
                continue;
            }

            Map argMap = new HashMap(this.args[i]);
            Iterator iter = argMap.keySet().iterator();
            while (iter.hasNext()) {
                String validatorName = (String) iter.next();
                Arg arg = (Arg) argMap.get(validatorName);
                argMap.put(validatorName, arg.clone());
            }
            field.args[i] = argMap;
        }

        field.hVars = ValidatorUtils.copyFastHashMap(hVars);
        field.hMsgs = ValidatorUtils.copyFastHashMap(hMsgs);
        field.hArg0 = ValidatorUtils.copyFastHashMap(hArg0);
        field.hArg1 = ValidatorUtils.copyFastHashMap(hArg1);
        field.hArg2 = ValidatorUtils.copyFastHashMap(hArg2);
        field.hArg3 = ValidatorUtils.copyFastHashMap(hArg3);

        return field;
    }

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
        StringBuffer results = new StringBuffer();

        results.append("\t\tkey = " + key + "\n");
        results.append("\t\tproperty = " + property + "\n");
        results.append("\t\tindexedProperty = " + indexedProperty + "\n");
        results.append("\t\tindexedListProperty = " + indexedListProperty + "\n");
        results.append("\t\tdepends = " + depends + "\n");
        results.append("\t\tpage = " + page + "\n");
        results.append("\t\tfieldOrder = " + fieldOrder + "\n");

        if (hVars != null) {
            results.append("\t\tVars:\n");
            for (Iterator i = hVars.keySet().iterator(); i.hasNext();) {
                Object key = i.next();
                results.append("\t\t\t");
                results.append(key);
                results.append("=");
                results.append(hVars.get(key));
                results.append("\n");
            }
        }

        return results.toString();
    }

}
