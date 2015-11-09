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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.util.ValidatorUtils;

/**
 * Contains the information to dynamically create and run a validation
 * method.  This is the class representation of a pluggable validator that can
 * be defined in an xml file with the &lt;validator&gt; element.
 *
 * <strong>Note</strong>: The validation method is assumed to be thread safe.
 *
 * @version $Revision$
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
    private String name = null;

    /**
     * The full class name of the class containing
     * the validation method associated with this action.
     */
    private String classname = null;

    /**
     * The Class object loaded from the classname.
     */
    private Class<?> validationClass = null;

    /**
     * The full method name of the validation to be performed.  The method
     * must be thread safe.
     */
    private String method = null;

    /**
     * The Method object loaded from the method name.
     */
    private Method validationMethod = null;

    /**
     * <p>
     * The method signature of the validation method.  This should be a comma
     * delimited list of the full class names of each parameter in the correct
     * order that the method takes.
     * </p>
     * <p>
     * Note: <code>java.lang.Object</code> is reserved for the
     * JavaBean that is being validated.  The <code>ValidatorAction</code>
     * and <code>Field</code> that are associated with a field's
     * validation will automatically be populated if they are
     * specified in the method signature.
     * </p>
     */
    private String methodParams =
            Validator.BEAN_PARAM
            + ","
            + Validator.VALIDATOR_ACTION_PARAM
            + ","
            + Validator.FIELD_PARAM;

    /**
     * The Class objects for each entry in methodParameterList.
     */
    private Class<?>[] parameterClasses = null;

    /**
     * The other <code>ValidatorAction</code>s that this one depends on.  If
     * any errors occur in an action that this one depends on, this action will
     * not be processsed.
     */
    private String depends = null;

    /**
     * The default error message associated with this action.
     */
    private String msg = null;

    /**
     * An optional field to contain the name to be used if JavaScript is
     * generated.
     */
    private String jsFunctionName = null;

    /**
     * An optional field to contain the class path to be used to retrieve the
     * JavaScript function.
     */
    private String jsFunction = null;

    /**
     * An optional field to containing a JavaScript representation of the
     * java method assocated with this action.
     */
    private String javascript = null;

    /**
     * If the java method matching the correct signature isn't static, the
     * instance is stored in the action.  This assumes the method is thread
     * safe.
     */
    private Object instance = null;

    /**
     * An internal List representation of the other <code>ValidatorAction</code>s
     * this one depends on (if any).  This List gets updated
     * whenever setDepends() gets called.  This is synchronized so a call to
     * setDepends() (which clears the List) won't interfere with a call to
     * isDependency().
     */
    private final List<String> dependencyList = Collections.synchronizedList(new ArrayList<String>());

    /**
     * An internal List representation of all the validation method's
     * parameters defined in the methodParams String.
     */
    private final List<String> methodParameterList = new ArrayList<String>();

    /**
     * Gets the name of the validator action.
     * @return Validator Action name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the validator action.
     * @param name Validator Action name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the class of the validator action.
     * @return Class name of the validator Action.
     */
    public String getClassname() {
        return classname;
    }

    /**
     * Sets the class of the validator action.
     * @param classname Class name of the validator Action.
     */
    public void setClassname(String classname) {
        this.classname = classname;
    }

    /**
     * Gets the name of method being called for the validator action.
     * @return The method name.
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the name of method being called for the validator action.
     * @param method The method name.
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Gets the method parameters for the method.
     * @return Method's parameters.
     */
    public String getMethodParams() {
        return methodParams;
    }

    /**
     * Sets the method parameters for the method.
     * @param methodParams A comma separated list of parameters.
     */
    public void setMethodParams(String methodParams) {
        this.methodParams = methodParams;

        this.methodParameterList.clear();

        StringTokenizer st = new StringTokenizer(methodParams, ",");
        while (st.hasMoreTokens()) {
            String value = st.nextToken().trim();

            if (value != null && value.length() > 0) {
                this.methodParameterList.add(value);
            }
        }
    }

    /**
     * Gets the dependencies of the validator action as a comma separated list
     * of validator names.
     * @return The validator action's dependencies.
     */
    public String getDepends() {
        return this.depends;
    }

    /**
     * Sets the dependencies of the validator action.
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
     * Gets the message associated with the validator action.
     * @return The message for the validator action.
     */
    public String getMsg() {
        return msg;
    }

    /**
     * Sets the message associated with the validator action.
     * @param msg The message for the validator action.
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * Gets the Javascript function name.  This is optional and can
     * be used instead of validator action name for the name of the
     * Javascript function/object.
     * @return The Javascript function name.
     */
    public String getJsFunctionName() {
        return jsFunctionName;
    }

    /**
     * Sets the Javascript function name.  This is optional and can
     * be used instead of validator action name for the name of the
     * Javascript function/object.
     * @param jsFunctionName The Javascript function name.
     */
    public void setJsFunctionName(String jsFunctionName) {
        this.jsFunctionName = jsFunctionName;
    }

    /**
     * Sets the fully qualified class path of the Javascript function.
     * <p>
     * This is optional and can be used <strong>instead</strong> of the setJavascript().
     * Attempting to call both <code>setJsFunction</code> and <code>setJavascript</code>
     * will result in an <code>IllegalStateException</code> being thrown. </p>
     * <p>
     * If <strong>neither</strong> setJsFunction or setJavascript is set then
     * validator will attempt to load the default javascript definition.
     * </p>
     * <pre>
     * <b>Examples</b>
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
     *      which is the default javascript definition.
     * </pre>
     * @param jsFunction The Javascript function's fully qualified class path.
     */
    public void setJsFunction(String jsFunction) {
        if (javascript != null) {
            throw new IllegalStateException("Cannot call setJsFunction() after calling setJavascript()");
        }

        this.jsFunction = jsFunction;
    }

    /**
     * Gets the Javascript equivalent of the java class and method
     * associated with this action.
     * @return The Javascript validation.
     */
    public String getJavascript() {
        return javascript;
    }

    /**
     * Sets the Javascript equivalent of the java class and method
     * associated with this action.
     * @param javascript The Javascript validation.
     */
    public void setJavascript(String javascript) {
        if (jsFunction != null) {
            throw new IllegalStateException("Cannot call setJavascript() after calling setJsFunction()");
        }

        this.javascript = javascript;
    }

    /**
     * Initialize based on set.
     */
    protected void init() {
        this.loadJavascriptFunction();
    }

    /**
     * Load the javascript function specified by the given path.  For this
     * implementation, the <code>jsFunction</code> property should contain a
     * fully qualified package and script name, separated by periods, to be
     * loaded from the class loader that created this instance.
     *
     * TODO if the path begins with a '/' the path will be intepreted as
     * absolute, and remain unchanged.  If this fails then it will attempt to
     * treat the path as a file path.  It is assumed the script ends with a
     * '.js'.
     */
    protected synchronized void loadJavascriptFunction() {

        if (this.javascriptAlreadyLoaded()) {
            return;
        }

        if (getLog().isTraceEnabled()) {
            getLog().trace("  Loading function begun");
        }

        if (this.jsFunction == null) {
            this.jsFunction = this.generateJsFunction();
        }

        String javascriptFileName = this.formatJavascriptFileName();

        if (getLog().isTraceEnabled()) {
            getLog().trace("  Loading js function '" + javascriptFileName + "'");
        }

        this.javascript = this.readJavascriptFile(javascriptFileName);

        if (getLog().isTraceEnabled()) {
            getLog().trace("  Loading javascript function completed");
        }

    }

    /**
     * Read a javascript function from a file.
     * @param javascriptFileName The file containing the javascript.
     * @return The javascript function or null if it could not be loaded.
     */
    private String readJavascriptFile(String javascriptFileName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }

        InputStream is = classLoader.getResourceAsStream(javascriptFileName);
        if (is == null) {
            is = this.getClass().getResourceAsStream(javascriptFileName);
        }

        if (is == null) {
            getLog().debug("  Unable to read javascript name "+javascriptFileName);
            return null;
        }

        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

        } catch(IOException e) {
            getLog().error("Error reading javascript file.", e);

        } finally {
            try {
                reader.close();
            } catch(IOException e) {
                getLog().error("Error closing stream to javascript file.", e);
            }
        }

        String function = buffer.toString();
        return function.equals("") ? null : function;
    }

    /**
     * @return A filename suitable for passing to a
     * ClassLoader.getResourceAsStream() method.
     */
    private String formatJavascriptFileName() {
        String name = this.jsFunction.substring(1);

        if (!this.jsFunction.startsWith("/")) {
            name = jsFunction.replace('.', '/') + ".js";
        }

        return name;
    }

    /**
     * @return true if the javascript for this action has already been loaded.
     */
    private boolean javascriptAlreadyLoaded() {
        return (this.javascript != null);
    }

    /**
     * Used to generate the javascript name when it is not specified.
     */
    private String generateJsFunction() {
        StringBuilder jsName =
                new StringBuilder("org.apache.commons.validator.javascript");

        jsName.append(".validate");
        jsName.append(name.substring(0, 1).toUpperCase());
        jsName.append(name.substring(1, name.length()));

        return jsName.toString();
    }

    /**
     * Checks whether or not the value passed in is in the depends field.
     * @param validatorName Name of the dependency to check.
     * @return Whether the named validator is a dependant.
     */
    public boolean isDependency(String validatorName) {
        return this.dependencyList.contains(validatorName);
    }

    /**
     * Returns the dependent validator names as an unmodifiable
     * <code>List</code>.
     * @return List of the validator action's depedents.
     */
    public List<String> getDependencyList() {
        return Collections.unmodifiableList(this.dependencyList);
    }

    /**
     * Returns a string representation of the object.
     * @return a string representation.
     */
    public String toString() {
        StringBuilder results = new StringBuilder("ValidatorAction: ");
        results.append(name);
        results.append("\n");

        return results.toString();
    }

    /**
     * Dynamically runs the validation method for this validator and returns
     * true if the data is valid.
     * @param field
     * @param params A Map of class names to parameter values.
     * @param results
     * @param pos The index of the list property to validate if it's indexed.
     * @throws ValidatorException
     */
    boolean executeValidationMethod(
        Field field,
        // TODO What is this the correct value type?
        // both ValidatorAction and Validator are added as parameters
        Map<String, Object> params,
        ValidatorResults results,
        int pos)
        throws ValidatorException {

        params.put(Validator.VALIDATOR_ACTION_PARAM, this);

        try {
            if (this.validationMethod == null) {
                synchronized(this) {
                    ClassLoader loader = this.getClassLoader(params);
                    this.loadValidationClass(loader);
                    this.loadParameterClasses(loader);
                    this.loadValidationMethod();
                }
            }

            Object[] paramValues = this.getParameterValues(params);

            if (field.isIndexed()) {
                this.handleIndexedField(field, pos, paramValues);
            }

            Object result = null;
            try {
                result =
                    validationMethod.invoke(
                        getValidationClassInstance(),
                        paramValues);

            } catch (IllegalArgumentException e) {
                throw new ValidatorException(e.getMessage());
            } catch (IllegalAccessException e) {
                throw new ValidatorException(e.getMessage());
            } catch (InvocationTargetException e) {

                if (e.getTargetException() instanceof Exception) {
                    throw (Exception) e.getTargetException();

                } else if (e.getTargetException() instanceof Error) {
                    throw (Error) e.getTargetException();
                }
            }

            boolean valid = this.isValid(result);
            if (!valid || (valid && !onlyReturnErrors(params))) {
                results.add(field, this.name, valid, result);
            }

            if (!valid) {
                return false;
            }

            // TODO This catch block remains for backward compatibility.  Remove
            // this for Validator 2.0 when exception scheme changes.
        } catch (Exception e) {
            if (e instanceof ValidatorException) {
                throw (ValidatorException) e;
            }

            getLog().error(
                "Unhandled exception thrown during validation: " + e.getMessage(),
                e);

            results.add(field, this.name, false);
            return false;
        }

        return true;
    }

    /**
     * Load the Method object for the configured validation method name.
     * @throws ValidatorException
     */
    private void loadValidationMethod() throws ValidatorException {
        if (this.validationMethod != null) {
            return;
        }

        try {
            this.validationMethod =
                this.validationClass.getMethod(this.method, this.parameterClasses);

        } catch (NoSuchMethodException e) {
            throw new ValidatorException("No such validation method: " +
                e.getMessage());
        }
    }

    /**
     * Load the Class object for the configured validation class name.
     * @param loader The ClassLoader used to load the Class object.
     * @throws ValidatorException
     */
    private void loadValidationClass(ClassLoader loader)
        throws ValidatorException {

        if (this.validationClass != null) {
            return;
        }

        try {
            this.validationClass = loader.loadClass(this.classname);
        } catch (ClassNotFoundException e) {
            throw new ValidatorException(e.toString());
        }
    }

    /**
     * Converts a List of parameter class names into their Class objects.
     * Stores the output in {@link parameterClasses}.  This
     * array is in the same order as the given List and is suitable for passing
     * to the validation method.
     * @throws ValidatorException if a class cannot be loaded.
     */
    private void loadParameterClasses(ClassLoader loader)
        throws ValidatorException {

        if (this.parameterClasses != null) {
            return;
        }

        Class<?>[] parameterClasses = new Class[this.methodParameterList.size()];

        for (int i = 0; i < this.methodParameterList.size(); i++) {
            String paramClassName = (String) this.methodParameterList.get(i);

            try {
                parameterClasses[i] = loader.loadClass(paramClassName);

            } catch (ClassNotFoundException e) {
                throw new ValidatorException(e.getMessage());
            }
        }

        this.parameterClasses = parameterClasses;
    }

    /**
     * Converts a List of parameter class names into their values contained in
     * the parameters Map.
     * @param params A Map of class names to parameter values.
     * @return An array containing the value object for each parameter.  This
     * array is in the same order as the given List and is suitable for passing
     * to the validation method.
     */
    private Object[] getParameterValues(Map<String, ? super Object> params) {

        Object[] paramValue = new Object[this.methodParameterList.size()];

        for (int i = 0; i < this.methodParameterList.size(); i++) {
            String paramClassName = this.methodParameterList.get(i);
            paramValue[i] = params.get(paramClassName);
        }

        return paramValue;
    }

    /**
     * Return an instance of the validation class or null if the validation
     * method is static so does not require an instance to be executed.
     */
    private Object getValidationClassInstance() throws ValidatorException {
        if (Modifier.isStatic(this.validationMethod.getModifiers())) {
            this.instance = null;

        } else {
            if (this.instance == null) {
                try {
                    this.instance = this.validationClass.newInstance();
                } catch (InstantiationException e) {
                    String msg =
                        "Couldn't create instance of "
                            + this.classname
                            + ".  "
                            + e.getMessage();

                    throw new ValidatorException(msg);

                } catch (IllegalAccessException e) {
                    String msg =
                        "Couldn't create instance of "
                            + this.classname
                            + ".  "
                            + e.getMessage();

                    throw new ValidatorException(msg);
                }
            }
        }

        return this.instance;
    }

    /**
     * Modifies the paramValue array with indexed fields.
     *
     * @param field
     * @param pos
     * @param paramValues
     */
    private void handleIndexedField(Field field, int pos, Object[] paramValues)
        throws ValidatorException {

        int beanIndex = this.methodParameterList.indexOf(Validator.BEAN_PARAM);
        int fieldIndex = this.methodParameterList.indexOf(Validator.FIELD_PARAM);

        Object indexedList[] = field.getIndexedProperty(paramValues[beanIndex]);

        // Set current iteration object to the parameter array
        paramValues[beanIndex] = indexedList[pos];

        // Set field clone with the key modified to represent
        // the current field
        Field indexedField = (Field) field.clone();
        indexedField.setKey(
            ValidatorUtils.replace(
                indexedField.getKey(),
                Field.TOKEN_INDEXED,
                "[" + pos + "]"));

        paramValues[fieldIndex] = indexedField;
    }

    /**
     * If the result object is a <code>Boolean</code>, it will return its
     * value.  If not it will return <code>false</code> if the object is
     * <code>null</code> and <code>true</code> if it isn't.
     */
    private boolean isValid(Object result) {
        if (result instanceof Boolean) {
            Boolean valid = (Boolean) result;
            return valid.booleanValue();
        }
        return result != null;
    }

    /**
     * Returns the ClassLoader set in the Validator contained in the parameter
     * Map.
     */
    private ClassLoader getClassLoader(Map<String, Object> params) {
        Validator v = (Validator) params.get(Validator.VALIDATOR_PARAM);
        return v.getClassLoader();
    }

    /**
     * Returns the onlyReturnErrors setting in the Validator contained in the
     * parameter Map.
     */
    private boolean onlyReturnErrors(Map<String, Object> params) {
        Validator v = (Validator) params.get(Validator.VALIDATOR_PARAM);
        return v.getOnlyReturnErrors();
    }

    /**
     * Accessor method for Log instance.
     *
     * The Log instance variable is transient and
     * accessing it through this method ensures it
     * is re-initialized when this instance is
     * de-serialized.
     *
     * @return The Log instance.
     */
    private Log getLog() {
        if (log == null) {
            log =  LogFactory.getLog(ValidatorAction.class);
        }
        return log;
    }
}
