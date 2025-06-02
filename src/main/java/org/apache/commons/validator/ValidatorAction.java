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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.util.ValidatorUtils;

/**
 * Contains the information to dynamically create and run a validation method. This is the class representation of a pluggable validator that can be defined in
 * an xml file with the &lt;validator&gt; element.
 *
 * <strong>Note</strong>: The validation method is assumed to be thread safe.
 */
public class ValidatorAction implements Serializable {

    private static final long serialVersionUID = 1339713700053204597L;

    /**
     * Logger.
     */
    private transient Log log = LogFactory.getLog(ValidatorAction.class);

    /**
     * The name of the validation.
     */
    private String name;

    /**
     * The full class name of the class containing the validation method associated with this action.
     */
    private String className;

    /**
     * The Class object loaded from the class name.
     */
    private Class<?> validationClass;

    /**
     * The full method name of the validation to be performed. The method must be thread safe.
     */
    private String method;

    /**
     * The Method object loaded from the method name.
     */
    private transient Method validationMethod;

    /**
     * <p>
     * The method signature of the validation method. This should be a comma-delimited list of the full class names of each parameter in the correct order that
     * the method takes.
     * </p>
     * <p>
     * Note: {@code java.lang.Object} is reserved for the JavaBean that is being validated. The {@code ValidatorAction} and {@code Field} that
     * are associated with a field's validation will automatically be populated if they are specified in the method signature.
     * </p>
     */
    private String methodParams = Validator.BEAN_PARAM + "," + Validator.VALIDATOR_ACTION_PARAM + "," + Validator.FIELD_PARAM;

    /**
     * The Class objects for each entry in methodParameterList.
     */
    private Class<?>[] parameterClasses;

    /**
     * The other {@code ValidatorAction}s that this one depends on. If any errors occur in an action that this one depends on, this action will not be
     * processed.
     */
    private String depends;

    /**
     * The default error message associated with this action.
     */
    private String msg;

    /**
     * An optional field to contain the name to be used if JavaScript is generated.
     */
    private String jsFunctionName;

    /**
     * An optional field to contain the class path to be used to retrieve the JavaScript function.
     */
    private String jsFunction;

    /**
     * An optional field to containing a JavaScript representation of the Java method associated with this action.
     */
    private String javascript;

    /**
     * If the Java method matching the correct signature isn't static, the instance is stored in the action. This assumes the method is thread safe.
     */
    private Object instance;

    /**
     * An internal List representation of the other {@code ValidatorAction}s this one depends on (if any). This List gets updated whenever setDepends()
     * gets called. This is synchronized so a call to setDepends() (which clears the List) won't interfere with a call to isDependency().
     */
    private final List<String> dependencyList = Collections.synchronizedList(new ArrayList<>());

    /**
     * An internal List representation of all the validation method's parameters defined in the methodParams String.
     */
    private final List<String> methodParameterList = new ArrayList<>();

    /**
     * Constructs a new instance.
     */
    public ValidatorAction() {
        // empty
    }

    /**
     * Dynamically runs the validation method for this validator and returns true if the data is valid.
     *
     * @param field
     * @param params  A Map of class names to parameter values.
     * @param results
     * @param pos     The index of the list property to validate if it's indexed.
     * @throws ValidatorException
     */
    boolean executeValidationMethod(final Field field,
            // TODO What is this the correct value type?
            // both ValidatorAction and Validator are added as parameters
            final Map<String, Object> params, final ValidatorResults results, final int pos) throws ValidatorException {

        params.put(Validator.VALIDATOR_ACTION_PARAM, this);

        try {
            if (validationMethod == null) {
                synchronized (this) {
                    final ClassLoader loader = getClassLoader(params);
                    loadValidationClass(loader);
                    loadParameterClasses(loader);
                    loadValidationMethod();
                }
            }

            final Object[] paramValues = getParameterValues(params);

            if (field.isIndexed()) {
                handleIndexedField(field, pos, paramValues);
            }

            Object result = null;
            try {
                result = validationMethod.invoke(getValidationClassInstance(), paramValues);

            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new ValidatorException(e.getMessage());
            } catch (final InvocationTargetException e) {

                if (e.getTargetException() instanceof Exception) {
                    throw (Exception) e.getTargetException();

                }
                if (e.getTargetException() instanceof Error) {
                    throw (Error) e.getTargetException();
                }
            }

            final boolean valid = isValid(result);
            if (!valid || valid && !onlyReturnErrors(params)) {
                results.add(field, name, valid, result);
            }

            if (!valid) {
                return false;
            }

            // TODO This catch block remains for backward compatibility. Remove
            // this for Validator 2.0 when exception scheme changes.
        } catch (final Exception e) {
            if (e instanceof ValidatorException) {
                throw (ValidatorException) e;
            }

            getLog().error("Unhandled exception thrown during validation: " + e.getMessage(), e);

            results.add(field, name, false);
            return false;
        }

        return true;
    }

    /**
     * @return A file name suitable for passing to a {@link ClassLoader#getResourceAsStream(String)} method.
     */
    private String formatJavaScriptFileName() {
        String fname = jsFunction.substring(1);

        if (!jsFunction.startsWith("/")) {
            fname = jsFunction.replace('.', '/') + ".js";
        }

        return fname;
    }

    /**
     * Used to generate the JavaScript name when it is not specified.
     */
    private String generateJsFunction() {
        final StringBuilder jsName = new StringBuilder("org.apache.commons.validator.javascript");

        jsName.append(".validate");
        jsName.append(name.substring(0, 1).toUpperCase());
        jsName.append(name.substring(1));

        return jsName.toString();
    }

    /**
     * Returns the ClassLoader set in the Validator contained in the parameter Map.
     */
    private ClassLoader getClassLoader(final Map<String, Object> params) {
        final Validator v = getValidator(params);
        return v.getClassLoader();
    }

    /**
     * Gets the class of the validator action.
     *
     * @return Class name of the validator Action.
     */
    public String getClassname() {
        return className;
    }

    /**
     * Returns the dependent validator names as an unmodifiable {@code List}.
     *
     * @return List of the validator action's dependents.
     */
    public List<String> getDependencyList() {
        return Collections.unmodifiableList(dependencyList);
    }

    /**
     * Gets the dependencies of the validator action as a comma separated list of validator names.
     *
     * @return The validator action's dependencies.
     */
    public String getDepends() {
        return depends;
    }

    /**
     * Gets the JavaScript equivalent of the Java class and method associated with this action.
     *
     * @return The JavaScript validation.
     */
    public synchronized String getJavascript() {
        return javascript;
    }

    /**
     * Gets the JavaScript function name. This is optional and can be used instead of validator action name for the name of the JavaScript function/object.
     *
     * @return The JavaScript function name.
     */
    public String getJsFunctionName() {
        return jsFunctionName;
    }

    /**
     * Accessor method for Log instance.
     *
     * The Log instance variable is transient and accessing it through this method ensures it is re-initialized when this instance is de-serialized.
     *
     * @return The Log instance.
     */
    private Log getLog() {
        if (log == null) {
            log = LogFactory.getLog(ValidatorAction.class);
        }
        return log;
    }

    /**
     * Gets the name of method being called for the validator action.
     *
     * @return The method name.
     */
    public String getMethod() {
        return method;
    }

    /**
     * Gets the method parameters for the method.
     *
     * @return Method's parameters.
     */
    public String getMethodParams() {
        return methodParams;
    }

    /**
     * Gets the message associated with the validator action.
     *
     * @return The message for the validator action.
     */
    public String getMsg() {
        return msg;
    }

    /**
     * Gets the name of the validator action.
     *
     * @return Validator Action name.
     */
    public String getName() {
        return name;
    }

    /**
     * Converts a List of parameter class names into their values contained in the parameters Map.
     *
     * @param params A Map of class names to parameter values.
     * @return An array containing the value object for each parameter. This array is in the same order as the given List and is suitable for passing to the
     *         validation method.
     */
    private Object[] getParameterValues(final Map<String, ? super Object> params) {

        final Object[] paramValue = new Object[methodParameterList.size()];

        for (int i = 0; i < methodParameterList.size(); i++) {
            final String paramClassName = methodParameterList.get(i);
            paramValue[i] = params.get(paramClassName);
        }

        return paramValue;
    }

    /**
     * Gets an instance of the validation class or null if the validation method is static so does not require an instance to be executed.
     */
    private Object getValidationClassInstance() throws ValidatorException {
        if (Modifier.isStatic(validationMethod.getModifiers())) {
            instance = null;

        } else if (instance == null) {
            try {
                instance = validationClass.getConstructor().newInstance();
            } catch (final ReflectiveOperationException e) {
                final String msg1 = "Couldn't create instance of " + className + ".  " + e.getMessage();

                throw new ValidatorException(msg1);
            }
        }

        return instance;
    }

    private Validator getValidator(final Map<String, Object> params) {
        return (Validator) params.get(Validator.VALIDATOR_PARAM);
    }

    /**
     * Modifies the paramValue array with indexed fields.
     *
     * @param field
     * @param pos
     * @param paramValues
     */
    private void handleIndexedField(final Field field, final int pos, final Object[] paramValues) throws ValidatorException {

        final int beanIndex = methodParameterList.indexOf(Validator.BEAN_PARAM);
        final int fieldIndex = methodParameterList.indexOf(Validator.FIELD_PARAM);

        final Object[] indexedList = field.getIndexedProperty(paramValues[beanIndex]);

        // Set current iteration object to the parameter array
        paramValues[beanIndex] = indexedList[pos];

        // Set field clone with the key modified to represent
        // the current field
        final Field indexedField = (Field) field.clone();
        indexedField.setKey(ValidatorUtils.replace(indexedField.getKey(), Field.TOKEN_INDEXED, "[" + pos + "]"));

        paramValues[fieldIndex] = indexedField;
    }

    /**
     * Initialize based on set.
     */
    protected void init() {
        loadJavascriptFunction();
    }

    /**
     * Checks whether or not the value passed in is in the depends field.
     *
     * @param validatorName Name of the dependency to check.
     * @return Whether the named validator is a dependant.
     */
    public boolean isDependency(final String validatorName) {
        return dependencyList.contains(validatorName);
    }

    /**
     * If the result object is a {@code Boolean}, it will return its value. If not it will return {@code false} if the object is {@code null} and
     * {@code true} if it isn't.
     */
    private boolean isValid(final Object result) {
        if (result instanceof Boolean) {
            final Boolean valid = (Boolean) result;
            return valid.booleanValue();
        }
        return result != null;
    }

    /**
     * @return true if the JavaScript for this action has already been loaded.
     */
    private boolean javaScriptAlreadyLoaded() {
        return javascript != null;
    }

    /**
     * Load the JavaScript function specified by the given path. For this implementation, the {@code jsFunction} property should contain a fully qualified
     * package and script name, separated by periods, to be loaded from the class loader that created this instance.
     *
     * TODO if the path begins with a '/' the path will be interpreted as absolute, and remain unchanged. If this fails then it will attempt to treat the path as
     * a file path. It is assumed the script ends with a '.js'.
     */
    protected synchronized void loadJavascriptFunction() {

        if (javaScriptAlreadyLoaded()) {
            return;
        }

        if (getLog().isTraceEnabled()) {
            getLog().trace("  Loading function begun");
        }

        if (jsFunction == null) {
            jsFunction = generateJsFunction();
        }

        final String javaScriptFileName = formatJavaScriptFileName();

        if (getLog().isTraceEnabled()) {
            getLog().trace("  Loading js function '" + javaScriptFileName + "'");
        }

        javascript = readJavaScriptFile(javaScriptFileName);

        if (getLog().isTraceEnabled()) {
            getLog().trace("  Loading JavaScript function completed");
        }

    }

    /**
     * Converts a List of parameter class names into their Class objects. Stores the output in {@link #parameterClasses}. This array is in the same order as the
     * given List and is suitable for passing to the validation method.
     *
     * @throws ValidatorException if a class cannot be loaded.
     */
    private void loadParameterClasses(final ClassLoader loader) throws ValidatorException {

        if (parameterClasses != null) {
            return;
        }

        final Class<?>[] parameterClasses = new Class[methodParameterList.size()];

        for (int i = 0; i < methodParameterList.size(); i++) {
            final String paramClassName = methodParameterList.get(i);

            try {
                parameterClasses[i] = loader.loadClass(paramClassName);

            } catch (final ClassNotFoundException e) {
                throw new ValidatorException(e.getMessage());
            }
        }

        this.parameterClasses = parameterClasses;
    }

    /**
     * Load the Class object for the configured validation class name.
     *
     * @param loader The ClassLoader used to load the Class object.
     * @throws ValidatorException
     */
    private void loadValidationClass(final ClassLoader loader) throws ValidatorException {

        if (validationClass != null) {
            return;
        }

        try {
            validationClass = loader.loadClass(className);
        } catch (final ClassNotFoundException e) {
            throw new ValidatorException(e.toString());
        }
    }

    /**
     * Load the Method object for the configured validation method name.
     *
     * @throws ValidatorException
     */
    private void loadValidationMethod() throws ValidatorException {
        if (validationMethod != null) {
            return;
        }

        try {
            validationMethod = validationClass.getMethod(method, parameterClasses);

        } catch (final NoSuchMethodException e) {
            throw new ValidatorException("No such validation method: " + e.getMessage());
        }
    }

    /**
     * Returns the onlyReturnErrors setting in the Validator contained in the parameter Map.
     */
    private boolean onlyReturnErrors(final Map<String, Object> params) {
        final Validator v = getValidator(params);
        return v.getOnlyReturnErrors();
    }

    /**
     * Opens an input stream for reading the specified resource.
     * <p>
     * The search order is described in the documentation for {@link ClassLoader#getResource(String)}.
     * </p>
     *
     * @param javaScriptFileName The resource name
     * @return An input stream for reading the resource, or {@code null} if the resource could not be found
     */
    private InputStream openInputStream(final String javaScriptFileName, final ClassLoader classLoader) {
        InputStream is = null;
        if (classLoader != null) {
            is = classLoader.getResourceAsStream(javaScriptFileName);
        }
        if (is == null) {
            return getClass().getResourceAsStream(javaScriptFileName);
        }
        return is;
    }

    /**
     * Reads a JavaScript function from a file.
     *
     * @param javaScriptFileName The file containing the JavaScript.
     * @return The JavaScript function or null if it could not be loaded.
     */
    private String readJavaScriptFile(final String javaScriptFileName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        // BufferedReader closes InputStreamReader closes InputStream
        final InputStream is = openInputStream(javaScriptFileName, classLoader);
        if (is == null) {
            getLog().debug("  Unable to read javascript name " + javaScriptFileName);
            return null;
        }
        final StringBuilder buffer = new StringBuilder();
        // TODO encoding
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
        } catch (final IOException e) {
            getLog().error("Error reading JavaScript file.", e);

        }
        final String function = buffer.toString();
        return function.isEmpty() ? null : function;
    }

    /**
     * Sets the class of the validator action.
     *
     * @param className Class name of the validator Action.
     * @deprecated Use {@link #setClassName(String)}.
     */
    @Deprecated
    public void setClassname(final String className) {
        this.className = className;
    }

    /**
     * Sets the class of the validator action.
     *
     * @param className Class name of the validator Action.
     */
    public void setClassName(final String className) {
        this.className = className;
    }

    /**
     * Sets the dependencies of the validator action.
     *
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
     * Sets the JavaScript equivalent of the Java class and method associated with this action.
     *
     * @param javaScript The JavaScript validation.
     */
    public synchronized void setJavascript(final String javaScript) {
        if (jsFunction != null) {
            throw new IllegalStateException("Cannot call setJavascript() after calling setJsFunction()");
        }

        this.javascript = javaScript;
    }

    /**
     * Sets the fully qualified class path of the JavaScript function.
     * <p>
     * This is optional and can be used <strong>instead</strong> of the setJavascript(). Attempting to call both {@code setJsFunction} and
     * {@code setJavascript} will result in an {@code IllegalStateException} being thrown.
     * </p>
     * <p>
     * If <strong>neither</strong> setJsFunction nor setJavascript is set then validator will attempt to load the default JavaScript definition.
     * </p>
     *
     * <pre>
     * <strong>Examples</strong>
     *   If in the validator.xml :
     * #1:
     *      &lt;validator name="tire"
     *            jsFunction="com.yourcompany.project.tireFuncion"&gt;
     *     Validator will attempt to load com.yourcompany.project.validateTireFunction.js from
     *     its class path.
     * #2:
     *    &lt;validator name="tire"&gt;
     *      Validator will use the name attribute to try and load
     *         org.apache.commons.validator.javascript.validateTire.js
     *      which is the default JavaScript definition.
     * </pre>
     *
     * @param jsFunction The JavaScript function's fully qualified class path.
     */
    public synchronized void setJsFunction(final String jsFunction) {
        if (javascript != null) {
            throw new IllegalStateException("Cannot call setJsFunction() after calling setJavascript()");
        }

        this.jsFunction = jsFunction;
    }

    /**
     * Sets the JavaScript function name. This is optional and can be used instead of validator action name for the name of the JavaScript function/object.
     *
     * @param jsFunctionName The JavaScript function name.
     */
    public void setJsFunctionName(final String jsFunctionName) {
        this.jsFunctionName = jsFunctionName;
    }

    /**
     * Sets the name of method being called for the validator action.
     *
     * @param method The method name.
     */
    public void setMethod(final String method) {
        this.method = method;
    }

    /**
     * Sets the method parameters for the method.
     *
     * @param methodParams A comma separated list of parameters.
     */
    public void setMethodParams(final String methodParams) {
        this.methodParams = methodParams;

        methodParameterList.clear();

        final StringTokenizer st = new StringTokenizer(methodParams, ",");
        while (st.hasMoreTokens()) {
            final String value = st.nextToken().trim();

            if (value != null && !value.isEmpty()) {
                methodParameterList.add(value);
            }
        }
    }

    /**
     * Sets the message associated with the validator action.
     *
     * @param msg The message for the validator action.
     */
    public void setMsg(final String msg) {
        this.msg = msg;
    }

    /**
     * Sets the name of the validator action.
     *
     * @param name Validator Action name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation.
     */
    @Override
    public String toString() {
        final StringBuilder results = new StringBuilder("ValidatorAction: ");
        results.append(name);
        results.append("\n");

        return results.toString();
    }
}
