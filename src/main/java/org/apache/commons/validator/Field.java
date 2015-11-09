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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.FastHashMap; // DEPRECATED
import org.apache.commons.validator.util.ValidatorUtils;

/**
 * This contains the list of pluggable validators to run on a field and any
 * message information and variables to perform the validations and generate
 * error messages.  Instances of this class are configured with a
 * &lt;field&gt; xml element.
 * <p>
 * The use of FastHashMap is deprecated and will be replaced in a future
 * release.
 * </p>
 *
 * @version $Revision$
 * @see org.apache.commons.validator.Form
 */
// TODO mutable non-private fields
public class Field implements Cloneable, Serializable {

    private static final long serialVersionUID = -8502647722530192185L;

    /**
     * This is the value that will be used as a key if the <code>Arg</code>
     * name field has no value.
     */
    private static final String DEFAULT_ARG =
            "org.apache.commons.validator.Field.DEFAULT";

    /**
     * This indicates an indexed property is being referenced.
     */
    public static final String TOKEN_INDEXED = "[]";

    /**
     * The start of a token.
     */
    protected static final String TOKEN_START = "${";

    /**
     * The end of a token.
     */
    protected static final String TOKEN_END = "}";

    /**
     * A Vriable token.
     */
    protected static final String TOKEN_VAR = "var:";

    /**
     * The Field's property name.
     */
    protected String property = null;

    /**
     * The Field's indexed property name.
     */
    protected String indexedProperty = null;

    /**
     * The Field's indexed list property name.
     */
    protected String indexedListProperty = null;

    /**
     * The Field's unique key.
     */
    protected String key = null;

    /**
     * A comma separated list of validator's this field depends on.
     */
    protected String depends = null;

    /**
     * The Page Number
     */
    protected int page = 0;

    /**
     * The flag that indicates whether scripting should be generated
     * by the client for client-side validation.
     * @since Validator 1.4
     */
    protected boolean clientValidation = true;

    /**
     * The order of the Field in the Form.
     */
    protected int fieldOrder = 0;

    /**
     * Internal representation of this.depends String as a List.  This List
     * gets updated whenever setDepends() gets called.  This List is
     * synchronized so a call to setDepends() (which clears the List) won't
     * interfere with a call to isDependency().
     */
    private final List<String> dependencyList = Collections.synchronizedList(new ArrayList<String>());

    /**
     * @deprecated Subclasses should use getVarMap() instead.
     */
    protected FastHashMap hVars = new FastHashMap(); // <String, Var>

    /**
     * @deprecated Subclasses should use getMsgMap() instead.
     */
    protected FastHashMap hMsgs = new FastHashMap(); // <String, Msg>

    /**
     * Holds Maps of arguments.  args[0] returns the Map for the first
     * replacement argument.  Start with a 0 length array so that it will
     * only grow to the size of the highest argument position.
     * @since Validator 1.1
     */
    @SuppressWarnings("unchecked") // cannot instantiate generic array, so have to assume this is OK
    protected Map<String, Arg>[] args = new Map[0];

    /**
     * Gets the page value that the Field is associated with for
     * validation.
     * @return The page number.
     */
    public int getPage() {
        return this.page;
    }

    /**
     * Sets the page value that the Field is associated with for
     * validation.
     * @param page The page number.
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * Gets the position of the <code>Field</code> in the validation list.
     * @return The field position.
     */
    public int getFieldOrder() {
        return this.fieldOrder;
    }

    /**
     * Sets the position of the <code>Field</code> in the validation list.
     * @param fieldOrder The field position.
     */
    public void setFieldOrder(int fieldOrder) {
        this.fieldOrder = fieldOrder;
    }

    /**
     * Gets the property name of the field.
     * @return The field's property name.
     */
    public String getProperty() {
        return this.property;
    }

    /**
     * Sets the property name of the field.
     * @param property The field's property name.
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Gets the indexed property name of the field.  This
     * is the method name that can take an <code>int</code> as
     * a parameter for indexed property value retrieval.
     * @return The field's indexed property name.
     */
    public String getIndexedProperty() {
        return this.indexedProperty;
    }

    /**
     * Sets the indexed property name of the field.
     * @param indexedProperty The field's indexed property name.
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
     * @return The field's indexed List property name.
     */
    public String getIndexedListProperty() {
        return this.indexedListProperty;
    }

    /**
     * Sets the indexed property name of the field.
     * @param indexedListProperty The field's indexed List property name.
     */
    public void setIndexedListProperty(String indexedListProperty) {
        this.indexedListProperty = indexedListProperty;
    }

    /**
     * Gets the validation rules for this field as a comma separated list.
     * @return A comma separated list of validator names.
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
     * @param msg A validation message.
     */
    public void addMsg(Msg msg) {
        getMsgMap().put(msg.getName(), msg);
    }

    /**
     * Retrieve a message value.
     * @param key Validation key.
     * @return A validation message for a specified validator.
     */
    public String getMsg(String key) {
        Msg msg = getMessage(key);
        return (msg == null) ? null : msg.getKey();
    }

    /**
     * Retrieve a message object.
     * @since Validator 1.1.4
     * @param key Validation key.
     * @return A validation message for a specified validator.
     */
    public Msg getMessage(String key) {
        return (Msg) getMsgMap().get(key);
    }

    /**
     * The <code>Field</code>'s messages are returned as an
     * unmodifiable <code>Map</code>.
     * @since Validator 1.1.4
     * @return Map of validation messages for the field.
     */
    public Map<String, Msg> getMessages() {
        return Collections.unmodifiableMap(getMsgMap());
    }

    /**
     * Determines whether client-side scripting should be generated
     * for this field. The default is <code>true</code>
     * @return <code>true</code> for scripting; otherwise false
     * @see #setClientValidation(boolean)
     * @since Validator 1.4
     */
    public boolean isClientValidation() {
        return this.clientValidation;
    }

    /**
     * Sets the flag that determines whether client-side scripting should
     * be generated for this field.
     * @param clientValidation the scripting flag
     * @see #isClientValidation()
     * @since Validator 1.4
     */
    public void setClientValidation(boolean clientValidation) {
        this.clientValidation = clientValidation;
    }

    /**
     * Add an <code>Arg</code> to the replacement argument list.
     * @since Validator 1.1
     * @param arg Validation message's argument.
     */
    public void addArg(Arg arg) {
        // TODO this first if check can go away after arg0, etc. are removed from dtd
        if (arg == null || arg.getKey() == null || arg.getKey().length() == 0) {
            return;
        }

        determineArgPosition(arg);
        ensureArgsCapacity(arg);

        Map<String, Arg> argMap = this.args[arg.getPosition()];
        if (argMap == null) {
            argMap = new HashMap<String, Arg>();
            this.args[arg.getPosition()] = argMap;
        }

        if (arg.getName() == null) {
            argMap.put(DEFAULT_ARG, arg);
        } else {
            argMap.put(arg.getName(), arg);
        }

    }

    /**
     * Calculate the position of the Arg
     */
    private void determineArgPosition(Arg arg) {

        int position = arg.getPosition();

        // position has been explicity set
        if (position >= 0) {
            return;
        }

        // first arg to be added
        if (args == null || args.length == 0) {
            arg.setPosition(0);
            return;
        }

        // determine the position of the last argument with
        // the same name or the last default argument
        String key = arg.getName() == null ? DEFAULT_ARG : arg.getName();
        int lastPosition = -1;
        int lastDefault  = -1;
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null && args[i].containsKey(key)) {
                lastPosition = i;
            }
            if (args[i] != null && args[i].containsKey(DEFAULT_ARG)) {
                lastDefault = i;
            }
        }

        if (lastPosition < 0) {
            lastPosition = lastDefault;
        }

        // allocate the next position
        arg.setPosition(++lastPosition);

    }

    /**
     * Ensures that the args array can hold the given arg.  Resizes the array as
     * necessary.
     * @param arg Determine if the args array is long enough to store this arg's
     * position.
     */
    private void ensureArgsCapacity(Arg arg) {
        if (arg.getPosition() >= this.args.length) {
            @SuppressWarnings("unchecked") // cannot check this at compile time, but it is OK
            Map<String, Arg>[] newArgs = new Map[arg.getPosition() + 1];
            System.arraycopy(this.args, 0, newArgs, 0, this.args.length);
            this.args = newArgs;
        }
    }

    /**
     * Gets the default <code>Arg</code> object at the given position.
     * @param position Validation message argument's position.
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
     * Add a <code>Var</code> to the <code>Field</code>.
     * @param v The Validator Argument.
     */
    public void addVar(Var v) {
        this.getVarMap().put(v.getName(), v);
    }

    /**
     * Add a <code>Var</code>, based on the values passed in, to the
     * <code>Field</code>.
     * @param name Name of the validation.
     * @param value The Argument's value.
     * @param jsType The Javascript type.
     */
    public void addVar(String name, String value, String jsType) {
        this.addVar(new Var(name, value, jsType));
    }

    /**
     * Retrieve a variable.
     * @param mainKey The Variable's key
     * @return the Variable
     */
    public Var getVar(String mainKey) {
        return (Var) getVarMap().get(mainKey);
    }

    /**
     * Retrieve a variable's value.
     * @param mainKey The Variable's key
     * @return the Variable's value
     */
    public String getVarValue(String mainKey) {
        String value = null;

        Object o = getVarMap().get(mainKey);
        if (o != null && o instanceof Var) {
            Var v = (Var) o;
            value = v.getValue();
        }

        return value;
    }

    /**
     * The <code>Field</code>'s variables are returned as an
     * unmodifiable <code>Map</code>.
     * @return the Map of Variable's for a Field.
     */
    public Map<String, Var> getVars() {
        return Collections.unmodifiableMap(getVarMap());
    }

    /**
     * Gets a unique key based on the property and indexedProperty fields.
     * @return a unique key for the field.
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
     * @param key a unique key for the field
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * If there is a value specified for the indexedProperty field then
     * <code>true</code> will be returned.  Otherwise it will be
     * <code>false</code>.
     * @return Whether the Field is indexed.
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
     */
    void process(Map<String, String> globalConstants, Map<String, String> constants) {
        this.hMsgs.setFast(false);
        this.hVars.setFast(true);

        this.generateKey();

        // Process FormSet Constants
        for (Iterator<Entry<String, String>> i = constants.entrySet().iterator(); i.hasNext();) {
            Entry<String, String> entry = i.next();
            String key = entry.getKey();
            String key2 = TOKEN_START + key + TOKEN_END;
            String replaceValue = entry.getValue();

            property = ValidatorUtils.replace(property, key2, replaceValue);

            processVars(key2, replaceValue);

            this.processMessageComponents(key2, replaceValue);
        }

        // Process Global Constants
        for (Iterator<Entry<String, String>> i = globalConstants.entrySet().iterator(); i.hasNext();) {
            Entry<String, String> entry = i.next();
            String key = entry.getKey();
            String key2 = TOKEN_START + key + TOKEN_END;
            String replaceValue = entry.getValue();

            property = ValidatorUtils.replace(property, key2, replaceValue);

            processVars(key2, replaceValue);

            this.processMessageComponents(key2, replaceValue);
        }

        // Process Var Constant Replacement
        for (Iterator<String> i = getVarMap().keySet().iterator(); i.hasNext();) {
            String key = i.next();
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
        Iterator<String> i = getVarMap().keySet().iterator();
        while (i.hasNext()) {
            String varKey = i.next();
            Var var = this.getVar(varKey);

            var.setValue(ValidatorUtils.replace(var.getValue(), key, replaceValue));
        }

    }

    /**
     * Replace the args key value with the key/value pairs passed in.
     */
    private void processMessageComponents(String key, String replaceValue) {
        String varKey = TOKEN_START + TOKEN_VAR;
        // Process Messages
        if (key != null && !key.startsWith(varKey)) {
            for (Iterator<Msg> i = getMsgMap().values().iterator(); i.hasNext();) {
                Msg msg = i.next();
                msg.setKey(ValidatorUtils.replace(msg.getKey(), key, replaceValue));
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

            Map<String, Arg> argMap = this.args[i];
            if (argMap == null) {
                continue;
            }

            Iterator<Arg> iter = argMap.values().iterator();
            while (iter.hasNext()) {
                Arg arg = iter.next();

                if (arg != null) {
                    arg.setKey(
                            ValidatorUtils.replace(arg.getKey(), key, replaceValue));
                }
            }
        }
    }

    /**
     * Checks if the validator is listed as a dependency.
     * @param validatorName Name of the validator to check.
     * @return Whether the field is dependant on a validator.
     */
    public boolean isDependency(String validatorName) {
        return this.dependencyList.contains(validatorName);
    }

    /**
     * Gets an unmodifiable <code>List</code> of the dependencies in the same
     * order they were defined in parameter passed to the setDepends() method.
     * @return A list of the Field's dependancies.
     */
    public List<String> getDependencyList() {
        return Collections.unmodifiableList(this.dependencyList);
    }

    /**
     * Creates and returns a copy of this object.
     * @return A copy of the Field.
     */
    public Object clone() {
        Field field = null;
        try {
            field = (Field) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new RuntimeException(e.toString());
        }

        @SuppressWarnings("unchecked") // empty array always OK; cannot check this at compile time
        final Map<String, Arg>[] tempMap = new Map[this.args.length];
        field.args = tempMap;
        for (int i = 0; i < this.args.length; i++) {
            if (this.args[i] == null) {
                continue;
            }

            Map<String, Arg> argMap = new HashMap<String, Arg>(this.args[i]);
            Iterator<Entry<String, Arg>> iter = argMap.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, Arg> entry = iter.next();
                String validatorName = entry.getKey();
                Arg arg = entry.getValue();
                argMap.put(validatorName, (Arg) arg.clone());
            }
            field.args[i] = argMap;
        }

        field.hVars = ValidatorUtils.copyFastHashMap(hVars);
        field.hMsgs = ValidatorUtils.copyFastHashMap(hMsgs);

        return field;
    }

    /**
     * Returns a string representation of the object.
     * @return A string representation of the object.
     */
    public String toString() {
        StringBuilder results = new StringBuilder();

        results.append("\t\tkey = " + key + "\n");
        results.append("\t\tproperty = " + property + "\n");
        results.append("\t\tindexedProperty = " + indexedProperty + "\n");
        results.append("\t\tindexedListProperty = " + indexedListProperty + "\n");
        results.append("\t\tdepends = " + depends + "\n");
        results.append("\t\tpage = " + page + "\n");
        results.append("\t\tfieldOrder = " + fieldOrder + "\n");

        if (hVars != null) {
            results.append("\t\tVars:\n");
            for (Iterator<?> i = getVarMap().keySet().iterator(); i.hasNext();) {
                Object key = i.next();
                results.append("\t\t\t");
                results.append(key);
                results.append("=");
                results.append(getVarMap().get(key));
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
            return ((Collection<?>) indexedProperty).toArray();

        } else if (indexedProperty.getClass().isArray()) {
            return (Object[]) indexedProperty;

        } else {
            throw new ValidatorException(this.getKey() + " is not indexed");
        }

    }
    /**
     * Returns the size of an indexed property from the object we're validating.
     *
     * @param bean The bean to extract the indexed values from.
     * @throws ValidatorException If there's an error looking up the property
     * or, the property found is not indexed.
     */
    private int getIndexedPropertySize(Object bean) throws ValidatorException {
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

        if (indexedProperty == null) {
            return 0;
        } else if (indexedProperty instanceof Collection) {
            return ((Collection<?>)indexedProperty).size();
        } else if (indexedProperty.getClass().isArray()) {
            return ((Object[])indexedProperty).length;
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
        Map<String, ValidatorAction> actions,
        Map<String, Object> params,
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
     * @throws ValidatorException If there's an error running a validator
     */
    private boolean runDependentValidators(
        ValidatorAction va,
        ValidatorResults results,
        Map<String, ValidatorAction> actions,
        Map<String, Object> params,
        int pos)
        throws ValidatorException {

        List<String> dependentValidators = va.getDependencyList();

        if (dependentValidators.isEmpty()) {
            return true;
        }

        Iterator<String> iter = dependentValidators.iterator();
        while (iter.hasNext()) {
            String depend = iter.next();

            ValidatorAction action = actions.get(depend);
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
     * @throws ValidatorException If an error occurs during validation.
     */
    public ValidatorResults validate(Map<String, Object> params, Map<String, ValidatorAction> actions)
        throws ValidatorException {

        if (this.getDepends() == null) {
            return new ValidatorResults();
        }

        ValidatorResults allResults = new ValidatorResults();

        Object bean = params.get(Validator.BEAN_PARAM);
        int numberOfFieldsToValidate =
            this.isIndexed() ? this.getIndexedPropertySize(bean) : 1;

        for (int fieldNumber = 0; fieldNumber < numberOfFieldsToValidate; fieldNumber++) {

            Iterator<String> dependencies = this.dependencyList.iterator();
            ValidatorResults results = new ValidatorResults();
            while (dependencies.hasNext()) {
                String depend = dependencies.next();

                ValidatorAction action = actions.get(depend);
                if (action == null) {
                    this.handleMissingAction(depend);
                }

                boolean good =
                    validateForRule(action, results, actions, params, fieldNumber);

                if (!good) {
                    allResults.merge(results);
                    return allResults;
                }
            }
            allResults.merge(results);
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
        throw new ValidatorException("No ValidatorAction named " + name
                + " found for field " + this.getProperty());
    }

    /**
     * Returns a Map of String Msg names to Msg objects.
     * @since Validator 1.2.0
     * @return A Map of the Field's messages.
     */
    @SuppressWarnings("unchecked") // FastHashMap does not support generics
    protected Map<String, Msg> getMsgMap() {
        return hMsgs;
    }

    /**
     * Returns a Map of String Var names to Var objects.
     * @since Validator 1.2.0
     * @return A Map of the Field's variables.
     */
    @SuppressWarnings("unchecked") // FastHashMap does not support generics
    protected Map<String, Var> getVarMap() {
        return hVars;
    }
}

