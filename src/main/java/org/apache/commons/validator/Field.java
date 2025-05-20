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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
 * @see org.apache.commons.validator.Form
 */
// TODO mutable non-private fields
public class Field implements Cloneable, Serializable {

    private static final long serialVersionUID = -8502647722530192185L;

    /**
     * This is the value that will be used as a key if the {@code Arg}
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
     * A Variable token.
     */
    protected static final String TOKEN_VAR = "var:";

    /**
     * The Field's property name.
     */
    protected String property;

    /**
     * The Field's indexed property name.
     */
    protected String indexedProperty;

    /**
     * The Field's indexed list property name.
     */
    protected String indexedListProperty;

    /**
     * The Field's unique key.
     */
    protected String key;

    /**
     * A comma separated list of validator's this field depends on.
     */
    protected String depends;

    /**
     * The Page Number
     */
    protected volatile int page;

    /**
     * The flag that indicates whether scripting should be generated
     * by the client for client-side validation.
     * @since 1.4
     */
    protected volatile boolean clientValidation = true;

    /**
     * The order of the Field in the Form.
     */
    protected volatile int fieldOrder;

    /**
     * Internal representation of this.depends String as a List.  This List
     * gets updated whenever setDepends() gets called.  This List is
     * synchronized so a call to setDepends() (which clears the List) won't
     * interfere with a call to isDependency().
     */
    private final List<String> dependencyList = Collections.synchronizedList(new ArrayList<>());

    /**
     * @deprecated Subclasses should use getVarMap() instead.
     */
    @Deprecated
    protected FastHashMap hVars = new FastHashMap(); // <String, Var>

    /**
     * @deprecated Subclasses should use getMsgMap() instead.
     */
    @Deprecated
    protected FastHashMap hMsgs = new FastHashMap(); // <String, Msg>

    /**
     * Holds Maps of arguments.  args[0] returns the Map for the first
     * replacement argument.  Start with a 0 length array so that it will
     * only grow to the size of the highest argument position.
     * @since 1.1
     */
    @SuppressWarnings("unchecked") // cannot instantiate generic array, so have to assume this is OK
    protected Map<String, Arg>[] args = new Map[0];

    /**
     * Constructs a new instance.
     */
    public Field() {
        // empty
    }

    /**
     * Add an {@code Arg} to the replacement argument list.
     *
     * @param arg Validation message's argument.
     * @since 1.1
     */
    public void addArg(final Arg arg) {
        // TODO this first if check can go away after arg0, etc. are removed from dtd
        if (arg == null || arg.getKey() == null || arg.getKey().isEmpty()) {
            return;
        }

        determineArgPosition(arg);
        ensureArgsCapacity(arg);

        Map<String, Arg> argMap = args[arg.getPosition()];
        if (argMap == null) {
            argMap = new HashMap<>();
            args[arg.getPosition()] = argMap;
        }

        final String name = arg.getName();
        argMap.put(name != null ? name : DEFAULT_ARG, arg);
    }

    /**
     * Add a {@code Msg} to the {@code Field}.
     * @param msg A validation message.
     */
    public void addMsg(final Msg msg) {
        getMsgMap().put(msg.getName(), msg);
    }

    /**
     * Add a {@code Var}, based on the values passed in, to the
     * {@code Field}.
     * @param name Name of the validation.
     * @param value The Argument's value.
     * @param jsType The JavaScript type.
     */
    public void addVar(final String name, final String value, final String jsType) {
        this.addVar(new Var(name, value, jsType));
    }

    /**
     * Add a {@code Var} to the {@code Field}.
     * @param v The Validator Argument.
     */
    public void addVar(final Var v) {
        getVarMap().put(v.getName(), v);
    }

    /**
     * Creates and returns a copy of this object.
     * @return A copy of the Field.
     */
    @Override
    public Object clone() {
        Field field = null;
        try {
            field = (Field) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e.toString(), e);
        }

        @SuppressWarnings("unchecked") // empty array always OK; cannot check this at compile time
        final Map<String, Arg>[] tempMap = new Map[args.length];
        field.args = tempMap;
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                continue;
            }

            final Map<String, Arg> argMap = new HashMap<>(args[i]);
            argMap.forEach((validatorName, arg) -> argMap.put(validatorName, (Arg) arg.clone()));
            field.args[i] = argMap;
        }

        field.hVars = ValidatorUtils.copyFastHashMap(hVars);
        field.hMsgs = ValidatorUtils.copyFastHashMap(hMsgs);

        return field;
    }

    /**
     * Calculate the position of the Arg
     */
    private void determineArgPosition(final Arg arg) {

        final int position = arg.getPosition();

        // position has been explicitly set
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
        final String keyName = arg.getName() == null ? DEFAULT_ARG : arg.getName();
        int lastPosition = -1;
        int lastDefault = -1;
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null && args[i].containsKey(keyName)) {
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
    private void ensureArgsCapacity(final Arg arg) {
        if (arg.getPosition() >= args.length) {
            @SuppressWarnings("unchecked") // cannot check this at compile time, but it is OK
            final
            Map<String, Arg>[] newArgs = new Map[arg.getPosition() + 1];
            System.arraycopy(args, 0, newArgs, 0, args.length);
            args = newArgs;
        }
    }

    /**
     * Generate correct {@code key} value.
     */
    public void generateKey() {
        if (isIndexed()) {
            key = indexedListProperty + TOKEN_INDEXED + "." + property;
        } else {
            key = property;
        }
    }

    /**
     * Gets the default {@code Arg} object at the given position.
     * @param position Validation message argument's position.
     * @return The default Arg or null if not found.
     * @since 1.1
     */
    public Arg getArg(final int position) {
        return this.getArg(DEFAULT_ARG, position);
    }

    /**
     * Gets the {@code Arg} object at the given position.  If the key
     * finds a {@code null} value then the default value will be
     * retrieved.
     * @param key The name the Arg is stored under.  If not found, the default
     * Arg for the given position (if any) will be retrieved.
     * @param position The Arg number to find.
     * @return The Arg with the given name and position or null if not found.
     * @since 1.1
     */
    public Arg getArg(final String key, final int position) {
        if (position >= args.length || args[position] == null) {
            return null;
        }

        final Arg arg = args[position].get(key);

        // Didn't find default arg so exit, otherwise we would get into
        // infinite recursion
        if (arg == null && key.equals(DEFAULT_ARG)) {
            return null;
        }

        return arg == null ? this.getArg(position) : arg;
    }

    /**
     * Gets the Args for the given validator name.
     * @param key The validator's args to retrieve.
     * @return An Arg[] sorted by the Args' positions (for example, the Arg at index 0
     * has a position of 0).
     * @since 1.1.1
     */
    public Arg[] getArgs(final String key) {
        final Arg[] argList = new Arg[args.length];

        for (int i = 0; i < args.length; i++) {
            argList[i] = this.getArg(key, i);
        }

        return argList;
    }

    /**
     * Gets an unmodifiable {@code List} of the dependencies in the same
     * order they were defined in parameter passed to the setDepends() method.
     * @return A list of the Field's dependencies.
     */
    public List<String> getDependencyList() {
        return Collections.unmodifiableList(dependencyList);
    }

    /**
     * Gets the validation rules for this field as a comma separated list.
     * @return A comma separated list of validator names.
     */
    public String getDepends() {
        return depends;
    }

    /**
     * Gets the position of the {@code Field} in the validation list.
     * @return The field position.
     */
    public int getFieldOrder() {
        return fieldOrder;
    }

    /**
     * Gets the indexed property name of the field.  This
     * is the method name that will return an array or a
     * {@link Collection} used to retrieve the
     * list and then loop through the list performing the specified
     * validations.
     * @return The field's indexed List property name.
     */
    public String getIndexedListProperty() {
        return indexedListProperty;
    }

    /**
     * Gets the indexed property name of the field.  This
     * is the method name that can take an {@code int} as
     * a parameter for indexed property value retrieval.
     * @return The field's indexed property name.
     */
    public String getIndexedProperty() {
        return indexedProperty;
    }

    /**
     * Returns an indexed property from the object we're validating.
     *
     * @param bean The bean to extract the indexed values from.
     * @throws ValidatorException If there's an error looking up the property
     * or, the property found is not indexed.
     */
    Object[] getIndexedProperty(final Object bean) throws ValidatorException {
        Object indexProp = null;

        try {
            indexProp = PropertyUtils.getProperty(bean, getIndexedListProperty());

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ValidatorException(e.getMessage());
        }

        if (indexProp instanceof Collection) {
            return ((Collection<?>) indexProp).toArray();

        }
        if (indexProp.getClass().isArray()) {
            return (Object[]) indexProp;

        }
        throw new ValidatorException(getKey() + " is not indexed");

    }

    /**
     * Returns the size of an indexed property from the object we're validating.
     *
     * @param bean The bean to extract the indexed values from.
     * @throws ValidatorException If there's an error looking up the property
     * or, the property found is not indexed.
     */
    private int getIndexedPropertySize(final Object bean) throws ValidatorException {
        Object indexProp = null;

        try {
            indexProp = PropertyUtils.getProperty(bean, getIndexedListProperty());

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ValidatorException(e.getMessage());
        }

        if (indexProp == null) {
            return 0;
        }
        if (indexProp instanceof Collection) {
            return ((Collection<?>) indexProp).size();
        }
        if (indexProp.getClass().isArray()) {
            return ((Object[]) indexProp).length;
        }
        throw new ValidatorException(getKey() + " is not indexed");

    }

    /**
     * Gets a unique key based on the property and indexedProperty fields.
     * @return a unique key for the field.
     */
    public String getKey() {
        if (key == null) {
            generateKey();
        }

        return key;
    }

    /**
     * Retrieve a message object.
     * @param key Validation key.
     * @return A validation message for a specified validator.
     * @since 1.1.4
     */
    public Msg getMessage(final String key) {
        return getMsgMap().get(key);
    }

    /**
     * The {@code Field}'s messages are returned as an
     * unmodifiable {@link Map}.
     * @return Map of validation messages for the field.
     * @since 1.1.4
     */
    public Map<String, Msg> getMessages() {
        return Collections.unmodifiableMap(getMsgMap());
    }

    /**
     * Retrieve a message value.
     * @param key Validation key.
     * @return A validation message for a specified validator.
     */
    public String getMsg(final String key) {
        final Msg msg = getMessage(key);
        return msg == null ? null : msg.getKey();
    }

    /**
     * Returns a Map of String Msg names to Msg objects.
     * @return A Map of the Field's messages.
     * @since 1.2.0
     */
    @SuppressWarnings("unchecked") // FastHashMap does not support generics
    protected Map<String, Msg> getMsgMap() {
        return hMsgs;
    }

    /**
     * Gets the page value that the Field is associated with for
     * validation.
     * @return The page number.
     */
    public int getPage() {
        return page;
    }

    /**
     * Gets the property name of the field.
     * @return The field's property name.
     */
    public String getProperty() {
        return property;
    }

    /**
     * Retrieve a variable.
     * @param mainKey The Variable's key
     * @return the Variable
     */
    public Var getVar(final String mainKey) {
        return getVarMap().get(mainKey);
    }

    /**
     * Returns a Map of String Var names to Var objects.
     * @return A Map of the Field's variables.
     * @since 1.2.0
     */
    @SuppressWarnings("unchecked") // FastHashMap does not support generics
    protected Map<String, Var> getVarMap() {
        return hVars;
    }

    /**
     * The {@code Field}'s variables are returned as an
     * unmodifiable {@link Map}.
     * @return the Map of Variable's for a Field.
     */
    public Map<String, Var> getVars() {
        return Collections.unmodifiableMap(getVarMap());
    }

    /**
     * Retrieve a variable's value.
     * @param mainKey The Variable's key
     * @return the Variable's value
     */
    public String getVarValue(final String mainKey) {
        String value = null;

        final Var v = getVarMap().get(mainKey);
        if (v != null) {
            value = v.getValue();
        }

        return value;
    }

    /**
     * Called when a validator name is used in a depends clause but there is
     * no know ValidatorAction configured for that name.
     * @param name The name of the validator in the depends list.
     * @throws ValidatorException
     */
    private void handleMissingAction(final String name) throws ValidatorException {
        throw new ValidatorException("No ValidatorAction named " + name
                + " found for field " + getProperty());
    }

    /**
     * Determines whether client-side scripting should be generated
     * for this field. The default is {@code true}
     * @return {@code true} for scripting; otherwise false
     * @see #setClientValidation(boolean)
     * @since 1.4
     */
    public boolean isClientValidation() {
        return clientValidation;
    }

    /**
     * Checks if the validator is listed as a dependency.
     * @param validatorName Name of the validator to check.
     * @return Whether the field is dependant on a validator.
     */
    public boolean isDependency(final String validatorName) {
        return dependencyList.contains(validatorName);
    }

    /**
     * If there is a value specified for the indexedProperty field then
     * {@code true} will be returned.  Otherwise, it will be
     * {@code false}.
     * @return Whether the Field is indexed.
     */
    public boolean isIndexed() {
        return indexedListProperty != null && !indexedListProperty.isEmpty();
    }

    /**
     * Replace constants with values in fields and process the depends field
     * to create the dependency {@link Map}.
     */
    void process(final Map<String, String> globalConstants, final Map<String, String> constants) {
        hMsgs.setFast(false);
        hVars.setFast(true);

        generateKey();

        // Process FormSet Constants
        for (final Entry<String, String> entry : constants.entrySet()) {
            final String key1 = entry.getKey();
            final String key2 = TOKEN_START + key1 + TOKEN_END;
            final String replaceValue = entry.getValue();

            property = ValidatorUtils.replace(property, key2, replaceValue);

            processVars(key2, replaceValue);

            processMessageComponents(key2, replaceValue);
        }

        // Process Global Constants
        for (final Entry<String, String> entry : globalConstants.entrySet()) {
            final String key1 = entry.getKey();
            final String key2 = TOKEN_START + key1 + TOKEN_END;
            final String replaceValue = entry.getValue();

            property = ValidatorUtils.replace(property, key2, replaceValue);

            processVars(key2, replaceValue);

            processMessageComponents(key2, replaceValue);
        }

        // Process Var Constant Replacement
        for (final String key1 : getVarMap().keySet()) {
            final String key2 = TOKEN_START + TOKEN_VAR + key1 + TOKEN_END;
            final Var var = getVar(key1);
            final String replaceValue = var.getValue();

            processMessageComponents(key2, replaceValue);
        }

        hMsgs.setFast(true);
    }

    /**
     * Replace the arg {@link Collection} key value with the key/value
     * pairs passed in.
     */
    private void processArg(final String key, final String replaceValue) {
        for (final Map<String, Arg> argMap : args) {
            if (argMap == null) {
                continue;
            }
            for (final Arg arg : argMap.values()) {
                if (arg != null) {
                    arg.setKey(ValidatorUtils.replace(arg.getKey(), key, replaceValue));
                }
            }
        }
    }

    /**
     * Replace the args key value with the key/value pairs passed in.
     */
    private void processMessageComponents(final String key, final String replaceValue) {
        final String varKey = TOKEN_START + TOKEN_VAR;
        // Process Messages
        if (key != null && !key.startsWith(varKey)) {
            for (final Msg msg : getMsgMap().values()) {
                msg.setKey(ValidatorUtils.replace(msg.getKey(), key, replaceValue));
            }
        }

        processArg(key, replaceValue);
    }

    /**
     * Replace the vars value with the key/value pairs passed in.
     */
    private void processVars(final String key, final String replaceValue) {
        for (final String varKey : getVarMap().keySet()) {
            final Var var = getVar(varKey);
            var.setValue(ValidatorUtils.replace(var.getValue(), key, replaceValue));
        }

    }

    /**
     * Calls all of the validators that this validator depends on.
     * TODO ValidatorAction should know how to run its own dependencies.
     * @param va Run dependent validators for this action.
     * @param results
     * @param actions
     * @param pos
     * @return true if all dependent validations passed.
     * @throws ValidatorException If there's an error running a validator
     */
    private boolean runDependentValidators(
        final ValidatorAction va,
        final ValidatorResults results,
        final Map<String, ValidatorAction> actions,
        final Map<String, Object> params,
        final int pos)
        throws ValidatorException {

        final List<String> dependentValidators = va.getDependencyList();

        if (dependentValidators.isEmpty()) {
            return true;
        }

        for (final String depend : dependentValidators) {
            final ValidatorAction action = actions.get(depend);
            if (action == null) {
                handleMissingAction(depend);
            }

            if (!validateForRule(action, results, actions, params, pos)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Sets the flag that determines whether client-side scripting should
     * be generated for this field.
     * @param clientValidation the scripting flag
     * @see #isClientValidation()
     * @since 1.4
     */
    public void setClientValidation(final boolean clientValidation) {
        this.clientValidation = clientValidation;
    }

    /**
     * Sets the validation rules for this field as a comma separated list.
     * @param depends A comma separated list of validator names.
     */
    public void setDepends(final String depends) {
        this.depends = depends;

        dependencyList.clear();

        final StringTokenizer st = new StringTokenizer(depends, ",");
        while (st.hasMoreTokens()) {
            final String depend = st.nextToken().trim();

            if (depend != null && !depend.isEmpty()) {
                dependencyList.add(depend);
            }
        }
    }

    /**
     * Sets the position of the {@code Field} in the validation list.
     * @param fieldOrder The field position.
     */
    public void setFieldOrder(final int fieldOrder) {
        this.fieldOrder = fieldOrder;
    }

    /**
     * Sets the indexed property name of the field.
     * @param indexedListProperty The field's indexed List property name.
     */
    public void setIndexedListProperty(final String indexedListProperty) {
        this.indexedListProperty = indexedListProperty;
    }
    /**
     * Sets the indexed property name of the field.
     * @param indexedProperty The field's indexed property name.
     */
    public void setIndexedProperty(final String indexedProperty) {
        this.indexedProperty = indexedProperty;
    }

    /**
     * Sets a unique key for the field.  This can be used to change
     * the key temporarily to have a unique key for an indexed field.
     * @param key a unique key for the field
     */
    public void setKey(final String key) {
        this.key = key;
    }

    /**
     * Sets the page value that the Field is associated with for
     * validation.
     * @param page The page number.
     */
    public void setPage(final int page) {
        this.page = page;
    }

    /**
     * Sets the property name of the field.
     * @param property The field's property name.
     */
    public void setProperty(final String property) {
        this.property = property;
    }

    /**
     * Returns a string representation of the object.
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        final StringBuilder results = new StringBuilder();

        results.append("\t\tkey = " + key + "\n");
        results.append("\t\tproperty = " + property + "\n");
        results.append("\t\tindexedProperty = " + indexedProperty + "\n");
        results.append("\t\tindexedListProperty = " + indexedListProperty + "\n");
        results.append("\t\tdepends = " + depends + "\n");
        results.append("\t\tpage = " + page + "\n");
        results.append("\t\tfieldOrder = " + fieldOrder + "\n");

        if (hVars != null) {
            results.append("\t\tVars:\n");
            for (final Object key1 : getVarMap().keySet()) {
                results.append("\t\t\t");
                results.append(key1);
                results.append("=");
                results.append(getVarMap().get(key1));
                results.append("\n");
            }
        }

        return results.toString();
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
    public ValidatorResults validate(final Map<String, Object> params, final Map<String, ValidatorAction> actions)
            throws ValidatorException {

        if (getDepends() == null) {
            return new ValidatorResults();
        }

        final ValidatorResults allResults = new ValidatorResults();

        final Object bean = params.get(Validator.BEAN_PARAM);
        final int numberOfFieldsToValidate = isIndexed() ? getIndexedPropertySize(bean) : 1;

        for (int fieldNumber = 0; fieldNumber < numberOfFieldsToValidate; fieldNumber++) {

            final ValidatorResults results = new ValidatorResults();
            synchronized (dependencyList) {
                for (final String depend : dependencyList) {

                    final ValidatorAction action = actions.get(depend);
                    if (action == null) {
                        handleMissingAction(depend);
                    }

                    final boolean good = validateForRule(action, results, actions, params, fieldNumber);

                    if (!good) {
                        allResults.merge(results);
                        return allResults;
                    }
                }
            }
            allResults.merge(results);
        }

        return allResults;
    }

    /**
     * Executes the given ValidatorAction and all ValidatorActions that it
     * depends on.
     * @return true if the validation succeeded.
     */
    private boolean validateForRule(
        final ValidatorAction va,
        final ValidatorResults results,
        final Map<String, ValidatorAction> actions,
        final Map<String, Object> params,
        final int pos)
        throws ValidatorException {

        final ValidatorResult result = results.getValidatorResult(getKey());
        if (result != null && result.containsAction(va.getName())) {
            return result.isValid(va.getName());
        }

        if (!runDependentValidators(va, results, actions, params, pos)) {
            return false;
        }

        return va.executeValidationMethod(this, params, results, pos);
    }
}

