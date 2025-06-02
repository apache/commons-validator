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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Validations are processed by the validate method. An instance of
 * {@code ValidatorResources} is used to define the validators
 * (validation methods) and the validation rules for a JavaBean.
 */
// TODO mutable fields should be made private and accessed via suitable methods only
public class Validator implements Serializable {

    private static final long serialVersionUID = -7119418755208731611L;

    /**
     * Resources key the JavaBean is stored to perform validation on.
     */
    public static final String BEAN_PARAM = "java.lang.Object";

    /**
     * Resources key the {@code ValidatorAction} is stored under.
     * This will be automatically passed into a validation method
     * with the current {@code ValidatorAction} if it is
     * specified in the method signature.
     */
    public static final String VALIDATOR_ACTION_PARAM =
            "org.apache.commons.validator.ValidatorAction";

    /**
     * Resources key the {@code ValidatorResults} is stored under.
     * This will be automatically passed into a validation method
     * with the current {@code ValidatorResults} if it is
     * specified in the method signature.
     */
    public static final String VALIDATOR_RESULTS_PARAM =
            "org.apache.commons.validator.ValidatorResults";

    /**
     * Resources key the {@code Form} is stored under.
     * This will be automatically passed into a validation method
     * with the current {@code Form} if it is
     * specified in the method signature.
     */
    public static final String FORM_PARAM = "org.apache.commons.validator.Form";

    /**
     * Resources key the {@code Field} is stored under.
     * This will be automatically passed into a validation method
     * with the current {@code Field} if it is
     * specified in the method signature.
     */
    public static final String FIELD_PARAM = "org.apache.commons.validator.Field";

    /**
     * Resources key the {@code Validator} is stored under.
     * This will be automatically passed into a validation method
     * with the current {@code Validator} if it is
     * specified in the method signature.
     */
    public static final String VALIDATOR_PARAM =
            "org.apache.commons.validator.Validator";

    /**
     * Resources key the {@link Locale} is stored.
     * This will be used to retrieve the appropriate
     * {@code FormSet} and {@code Form} to be
     * processed.
     */
    public static final String LOCALE_PARAM = "java.util.Locale";

    /**
     * The Validator Resources.
     *
     * @deprecated Use {@link #getResources()}, will be private in the next major version.
     */
    @Deprecated
    protected ValidatorResources resources;

    /**
     * The name of the form to validate
     *
     * @deprecated Use {@link #getFormName()}, will be private in the next major version.
     */
    @Deprecated
    protected String formName;

    /**
     * The name of the field on the form to validate
     *
     * @since 1.2.0
     *
     * @deprecated Use {@link #getFieldName()}, will be private in the next major version.
     */
    @Deprecated
    protected String fieldName;

    /**
     * Maps validation method parameter class names to the objects to be passed
     * into the method.
     *
     * @deprecated Use {@link #getParameters()}, will be private in the next major version.
     */
    @Deprecated
    protected Map<String, Object> parameters = new HashMap<>();

    /**
     * The current page number to validate.
     *
     * @deprecated Use {@link #getPage()}, will be private in the next major version.
     */
    @Deprecated
    protected int page;

    /**
     * The class loader to use for instantiating application objects.
     * If not specified, the context class loader, or the class loader
     * used to load Digester itself, is used, based on the value of the
     * {@code useContextClassLoader} variable.
     *
     * @deprecated Use {@link #getClassLoader()}, will be private in the next major version.
     */
    @Deprecated
    protected transient ClassLoader classLoader;

    /**
     * Whether or not to use the Context ClassLoader when loading classes
     * for instantiating new objects.  Default is {@code false}.
     *
     * @deprecated Use {@link #getUseContextClassLoader()}, will be private in the next major version.
     */
    @Deprecated
    protected boolean useContextClassLoader;

    /**
     * Sets this to true to not return Fields that pass validation.  Only return failures.
     *
     * @deprecated Use {@link #getOnlyReturnErrors()}, will be private in the next major version.
     */
    @Deprecated
    protected boolean onlyReturnErrors;

    /**
     * Constructs a {@code Validator} that will
     * use the {@code ValidatorResources}
     * passed in to retrieve pluggable validators
     * the different sets of validation rules.
     *
     * @param resources {@code ValidatorResources} to use during validation.
     */
    public Validator(final ValidatorResources resources) {
        this(resources, null);
    }

    /**
     * Constructs a {@code Validator} that will
     * use the {@code ValidatorResources}
     * passed in to retrieve pluggable validators
     * the different sets of validation rules.
     *
     * @param resources {@code ValidatorResources} to use during validation.
     * @param formName Key used for retrieving the set of validation rules.
     */
    public Validator(final ValidatorResources resources, final String formName) {
        if (resources == null) {
            throw new IllegalArgumentException("Resources cannot be null.");
        }

        this.resources = resources;
        this.formName = formName;
    }

    /**
     * Constructs a {@code Validator} that will
     * use the {@code ValidatorResources}
     * passed in to retrieve pluggable validators
     * the different sets of validation rules.
     *
     * @param resources {@code ValidatorResources} to use during validation.
     * @param formName Key used for retrieving the set of validation rules.
     * @param fieldName Key used for retrieving the set of validation rules for a field
     * @since 1.2.0
     */
    public Validator(final ValidatorResources resources, final String formName, final String fieldName) {
        if (resources == null) {
            throw new IllegalArgumentException("Resources cannot be null.");
        }

        this.resources = resources;
        this.formName = formName;
        this.fieldName = fieldName;
    }

    /**
     * Clears the form name, resources that were added, and the page that was
     * set (if any).  This can be called to reinitialize the Validator instance
     * so it can be reused.  The form name (key to set of validation rules) and any
     * resources needed, like the JavaBean being validated, will need to
     * set and/or added to this instance again.  The
     * {@code ValidatorResources} will not be removed since it can be used
     * again and is thread safe.
     */
    public void clear() {
        formName = null;
        fieldName = null;
        parameters.clear();
        page = 0;
    }

    /**
     * Gets the class loader to be used for instantiating application objects
     * when required.  This is determined based upon the following rules:
     * <ul>
     * <li>The class loader set by {@code setClassLoader()}, if any</li>
     * <li>The thread context class loader, if it exists and the
     *     {@code useContextClassLoader} property is set to true</li>
     * <li>The class loader used to load the Digester class itself.
     * </ul>
     * @return the class loader.
     */
    public ClassLoader getClassLoader() {
        if (classLoader != null) {
            return classLoader;
        }

        if (useContextClassLoader) {
            final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            if (contextLoader != null) {
                return contextLoader;
            }
        }

        return this.getClass().getClassLoader();
    }

    /**
     * Gets the field name.
     *
     * @return the field name.
     * @since 1.10.0
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Gets the form name which is the key to a set of validation rules.
     * @return the name of the form.
     */
    public String getFormName() {
        return formName;
    }

    /**
     * Returns true if the Validator is only returning Fields that fail validation.
     * @return whether only failed fields are returned.
     */
    public boolean getOnlyReturnErrors() {
        return onlyReturnErrors;
    }

    /**
     * Gets the page.
     *
     * <p>
     * This in conjunction with the page property of
     * a {@code Field} can control the processing of fields. If the field's
     * page is less than or equal to this page value, it will be processed.
     * </p>
     *
     * @return the page number.
     */
    public int getPage() {
        return page;
    }

    /**
     * Gets the parameter map.
     *
     * @return the parameter map.
     * @since 1.10.0
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Returns the value of the specified parameter that will be used during the
     * processing of validations.
     *
     * @param parameterClassName The full class name of the parameter of the
     * validation method that corresponds to the value/instance passed in with it.
     * @return value of the specified parameter.
     */
    public Object getParameterValue(final String parameterClassName) {
        return parameters.get(parameterClassName);
    }

    /**
     * Gets the validator resource.
     *
     * @return the validator resource.
     * @since 1.10.0
     */
    public ValidatorResources getResources() {
        return resources;
    }

    /**
     * Gets the boolean as to whether the context classloader should be used.
     * @return whether the context classloader should be used.
     */
    public boolean getUseContextClassLoader() {
        return useContextClassLoader;
    }

    /**
     * Sets the class loader to be used for instantiating application objects
     * when required.
     *
     * @param classLoader The new class loader to use, or {@code null}
     *  to revert to the standard rules
     */
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Sets the name of the field to validate in a form (optional)
     *
     * @param fieldName The name of the field in a form set
     * @since 1.2.0
     */
    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Sets the form name which is the key to a set of validation rules.
     * @param formName the name of the form.
     */
    public void setFormName(final String formName) {
        this.formName = formName;
    }

    /**
     * Configures which Fields the Validator returns from the validate() method.  Set this
     * to true to only return Fields that failed validation.  By default, validate() returns
     * all fields.
     * @param onlyReturnErrors whether only failed fields are returned.
     */
    public void setOnlyReturnErrors(final boolean onlyReturnErrors) {
        this.onlyReturnErrors = onlyReturnErrors;
    }

    /**
     * Sets the page.
     * <p>
     * This in conjunction with the page property of
     * a {@code Field} can control the processing of fields. If the field's page
     * is less than or equal to this page value, it will be processed.
     * </p>
     *
     * @param page the page number.
     */
    public void setPage(final int page) {
        this.page = page;
    }

    /**
     * Sets a parameter of a pluggable validation method.
     *
     * @param parameterClassName The full class name of the parameter of the
     * validation method that corresponds to the value/instance passed in with it.
     *
     * @param parameterValue The instance that will be passed into the
     * validation method.
     */
    public void setParameter(final String parameterClassName, final Object parameterValue) {
        parameters.put(parameterClassName, parameterValue);
    }

    /**
     * Sets whether to use the Context ClassLoader (the one found by
     * calling {@code Thread.currentThread().getContextClassLoader()})
     * to resolve/load classes that are defined in various rules.  If not
     * using Context ClassLoader, then the class-loading defaults to
     * using the calling-class' ClassLoader.
     *
     * @param useContextClassLoader determines whether to use Context ClassLoader.
     */
    public void setUseContextClassLoader(final boolean useContextClassLoader) {
        this.useContextClassLoader = useContextClassLoader;
    }

    /**
     * Performs validations based on the configured resources.
     *
     * @return The {@link Map} returned uses the property of the
     * {@code Field} for the key and the value is the number of error the
     * field had.
     * @throws ValidatorException If an error occurs during validation
     */
    public ValidatorResults validate() throws ValidatorException {
        Locale locale = (Locale) getParameterValue(LOCALE_PARAM);

        if (locale == null) {
            locale = Locale.getDefault();
        }

        setParameter(VALIDATOR_PARAM, this);

        final Form form = resources.getForm(locale, formName);
        if (form != null) {
            setParameter(FORM_PARAM, form);
            return form.validate(
                parameters,
                resources.getValidatorActions(),
                page,
                fieldName);
        }

        return new ValidatorResults();
    }

}
