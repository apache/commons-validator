/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/Field.java,v 1.31.2.1 2004/06/22 02:24:38 husted Exp $
 * $Revision: 1.31.2.1 $
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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.FastHashMap; // DEPRECATED
import org.apache.commons.validator.util.ValidatorUtils;

/**
 * This contains the list of pluggable validators to run on a field and any 
 * message information and variables to perform the validations and generate 
 * error messages.  Instances of this class are configured with a 
 * &lt;field&gt; xml element.
 *
 * The use of FastHashMap is deprecated and will be replaced in a future
 * release.
 *
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
     * replacement argument.  Start with a 0 length array so that it will
     * only grow to the size of the highest argument position.
     * @since Validator 1.1
     */
    protected Map[] args = new Map[0];

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
     * @param depends A comma separated list of validator names.
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
     * Retrieves the Args for the given validator name.
     * @param key The validator's args to retrieve.
     * @return An Arg[] sorted by the Args' positions (i.e. the Arg at index 0
     * has a position of 0). 
     * @since Validator 1.1.1
     */
    public Arg[] getArgs(String key){
        Arg[] args = new Arg[this.args.length];
        
        for (int i = 0; i < this.args.length; i++) {
		  args[i] = this.getArg(key, i);	
		}
        
        return args;
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
            throw new RuntimeException(e.toString());
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
    
    /**
     * Returns an indexed property from the object we're validating.
     *
     * @param bean The bean to extract the indexed values from.
     * @throws ValidatorException If there's an error looking up the property 
     * or, the property found is not indexed.
     */
    Object[] getIndexedProperty(Object bean) throws ValidatorException {
        Object indexedProperty = null;

        try {
            indexedProperty =
                PropertyUtils.getProperty(bean, this.getIndexedListProperty());

        } catch(IllegalAccessException e) {
            throw new ValidatorException(e.getMessage());
        } catch(InvocationTargetException e) {
            throw new ValidatorException(e.getMessage());
        } catch(NoSuchMethodException e) {
            throw new ValidatorException(e.getMessage());
        }

        if (indexedProperty instanceof Collection) {
            return ((Collection) indexedProperty).toArray();

        } else if (indexedProperty.getClass().isArray()) {
            return (Object[]) indexedProperty;

        } else {
            throw new ValidatorException(this.getKey() + " is not indexed");
        }

    }
    
    /**
     * Executes the given ValidatorAction and all ValidatorActions that it 
     * depends on.
     * @return true if the validation succeeded.
     */
    private boolean validateForRule(
        ValidatorAction va,
        ValidatorResults results,
        Map actions,
        Map params,
        int pos)
        throws ValidatorException {

        ValidatorResult result = results.getValidatorResult(this.getKey());
        if (result != null && result.containsAction(va.getName())) {
            return result.isValid(va.getName());
        }

        if (!this.runDependentValidators(va, results, actions, params, pos)) {
            return false;
        }

        return va.executeValidationMethod(this, params, results, pos);
    }

    /**
     * Calls all of the validators that this validator depends on.
     * TODO ValidatorAction should know how to run its own dependencies.
     * @param va Run dependent validators for this action.
     * @param results
     * @param actions
     * @param pos
     * @return true if all of the dependent validations passed.
     * @throws ValidatorException
     */
    private boolean runDependentValidators(
        ValidatorAction va,
        ValidatorResults results,
        Map actions,
        Map params,
        int pos)
        throws ValidatorException {

        List dependentValidators = va.getDependencyList();

        if (dependentValidators.isEmpty()) {
            return true;
        }

        Iterator iter = dependentValidators.iterator();
        while (iter.hasNext()) {
            String depend = (String) iter.next();

            ValidatorAction action = (ValidatorAction) actions.get(depend);
            if (action == null) {
                this.handleMissingAction(depend);
            }

            if (!this.validateForRule(action, results, actions, params, pos)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Run the configured validations on this field.  Run all validations 
     * in the depends clause over each item in turn, returning when the first 
     * one fails.
     * @param params A Map of parameter class names to parameter values to pass
     * into validation methods.
     * @param actions A Map of validator names to ValidatorAction objects.
     * @return A ValidatorResults object containing validation messages for 
     * this field.
     */
    ValidatorResults validate(Map params, Map actions)
        throws ValidatorException {
        
        if (this.getDepends() == null) {
            return new ValidatorResults();
        }

        ValidatorResults allResults = new ValidatorResults();

        Object bean = params.get(Validator.BEAN_PARAM);
        int numberOfFieldsToValidate =
            this.isIndexed() ? this.getIndexedProperty(bean).length : 1;

        for (int fieldNumber = 0; fieldNumber < numberOfFieldsToValidate; fieldNumber++) {
            
            Iterator dependencies = this.dependencyList.iterator();
            while (dependencies.hasNext()) {
                String depend = (String) dependencies.next();

                ValidatorAction action = (ValidatorAction) actions.get(depend);
                if (action == null) {
                    this.handleMissingAction(depend);
                }

                ValidatorResults results = new ValidatorResults();
                boolean good =
                    validateForRule(action, results, actions, params, fieldNumber);

                allResults.merge(results);

                if (!good) {
                    return allResults;
                }
            }
        }
        
        return allResults;
    }
    
    /**
     * Called when a validator name is used in a depends clause but there is
     * no know ValidatorAction configured for that name.
     * @param name The name of the validator in the depends list.
     * @throws ValidatorException
     */
    private void handleMissingAction(String name) throws ValidatorException {
        throw new ValidatorException(
            "No ValidatorAction named "
                + name
                + " found for field "
                + this.getProperty());
    }

}
