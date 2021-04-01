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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Validations are processed by the validate method. An instance of
 * <code>ValidatorResources</code> is used to define the validators
 * (validation methods) and the validation rules for a JavaBean.
 *
 * @version $Revision$
 */
// TODO mutable fields should be made private and accessed via suitable methods only
public class Validator implements Serializable {

    private static final long serialVersionUID = -7119418755208731611L;

    /**
     * Resources key the JavaBean is stored to perform validation on.
     */
    public static final String BEAN_PARAM = "java.lang.Object";

    /**
     * Resources key the <code>ValidatorAction</code> is stored under.
     * This will be automatically passed into a validation method
     * with the current <code>ValidatorAction</code> if it is
     * specified in the method signature.
     */
    public static final String VALIDATOR_ACTION_PARAM =
            "org.apache.commons.validator.ValidatorAction";

    /**
     * Resources key the <code>ValidatorResults</code> is stored under.
     * This will be automatically passed into a validation method
     * with the current <code>ValidatorResults</code> if it is
     * specified in the method signature.
     */
    public static final String VALIDATOR_RESULTS_PARAM =
            "org.apache.commons.validator.ValidatorResults";

    /**
     * Resources key the <code>Form</code> is stored under.
     * This will be automatically passed into a validation method
     * with the current <code>Form</code> if it is
     * specified in the method signature.
     */
    public static final String FORM_PARAM = "org.apache.commons.validator.Form";

    /**
     * Resources key the <code>Field</code> is stored under.
     * This will be automatically passed into a validation method
     * with the current <code>Field</code> if it is
     * specified in the method signature.
     */
    public static final String FIELD_PARAM = "org.apache.commons.validator.Field";

    /**
     * Resources key the <code>Validator</code> is stored under.
     * This will be automatically passed into a validation method
     * with the current <code>Validator</code> if it is
     * specified in the method signature.
     */
    public static final String VALIDATOR_PARAM =
            "org.apache.commons.validator.Validator";

    /**
     * Resources key the <code>Locale</code> is stored.
     * This will be used to retrieve the appropriate
     * <code>FormSet</code> and <code>Form</code> to be
     * processed.
     */
    public static final String LOCALE_PARAM = "java.util.Locale";

    /**
     * The Validator Resources.
     */
    protected ValidatorResources resources = null;

    /**
     * The name of the form to validate
     */
    protected String formName = null;

    /**
     * The name of the field on the form to validate
     * @since 1.2.0
     */
    protected String fieldName = null;

    /**
     * Maps validation method parameter class names to the objects to be passed
     * into the method.
     */
    protected Map<String, Object> parameters = new HashMap<>(); // <String, Object>

    /**
     * The current page number to validate.
     */
    protected int page = 0;

    /**
     * The class loader to use for instantiating application objects.
     * If not specified, the context class loader, or the class loader
     * used to load Digester itself, is used, based on the value of the
     * <code>useContextClassLoader</code> variable.
     */
    protected transient ClassLoader classLoader = null;

    /**
     * Whether or not to use the Context ClassLoader when loading classes
     * for instantiating new objects.  Default is <code>false</code>.
     */
    protected boolean useContextClassLoader = false;

    /**
     * Set this to true to not return Fields that pass validation.  Only return failures.
     */
    protected boolean onlyReturnErrors = false;

    /**
     * Construct a <code>Validator</code> that will
     * use the <code>ValidatorResources</code>
     * passed in to retrieve pluggable validators
     * the different sets of validation rules.
     *
     * @param resources <code>ValidatorResources</code> to use during validation.
     */
    public Validator(final ValidatorResources resources) {
        this(resources, null);
    }

    /**
     * Construct a <code>Validator</code> that will
     * use the <code>ValidatorResources</code>
     * passed in to retrieve pluggable validators
     * the different sets of validation rules.
     *
     * @param resources <code>ValidatorResources</code> to use during validation.
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
     * Construct a <code>Validator</code> that will
     * use the <code>ValidatorResources</code>
     * passed in to retrieve pluggable validators
     * the different sets of validation rules.
     *
     * @param resources <code>ValidatorResources</code> to use during validation.
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
     * Set a parameter of a pluggable validation method.
     *
     * @param parameterClassName The full class name of the parameter of the
     * validation method that corresponds to the value/instance passed in with it.
     *
     * @param parameterValue The instance that will be passed into the
     * validation method.
     */
    public void setParameter(final String parameterClassName, final Object parameterValue) {
        this.parameters.put(parameterClassName, parameterValue);
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
        return this.parameters.get(parameterClassName);
    }

    /**
     * Gets the form name which is the key to a set of validation rules.
     * @return the name of the form.
     */
    public String getFormName() {
        return formName;
    }

    /**
     * Sets the form name which is the key to a set of validation rules.
     * @param formName the name of the form.
     */
    public void setFormName(final String formName) {
        this.formName = formName;
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
     * Clears the form name, resources that were added, and the page that was
     * set (if any).  This can be called to reinitialize the Validator instance
     * so it can be reused.  The form name (key to set of validation rules) and any
     * resources needed, like the JavaBean being validated, will need to
     * set and/or added to this instance again.  The
     * <code>ValidatorResources</code> will not be removed since it can be used
     * again and is thread safe.
     */
    public void clear() {
        this.formName = null;
        this.fieldName = null;
        this.parameters = new HashMap<>();
        this.page = 0;
    }

    /**
     * Return the boolean as to whether the context classloader should be used.
     * @return whether the context classloader should be used.
     */
    public boolean getUseContextClassLoader() {
        return this.useContextClassLoader;
    }

    /**
     * Determine whether to use the Context ClassLoader (the one found by
     * calling <code>Thread.currentThread().getContextClassLoader()</code>)
     * to resolve/load classes that are defined in various rules.  If not
     * using Context ClassLoader, then the class-loading defaults to
     * using the calling-class' ClassLoader.
     *
     * @param use determines whether to use Context ClassLoader.
     */
    public void setUseContextClassLoader(final boolean use) {
        this.useContextClassLoader = use;
    }

    /**
     * Return the class loader to be used for instantiating application objects
     * when required.  This is determined based upon the following rules:
     * <ul>
     * <li>The class loader set by <code>setClassLoader()</code>, if any</li>
     * <li>The thread context class loader, if it exists and the
     *     <code>useContextClassLoader</code> property is set to true</li>
     * <li>The class loader used to load the Digester class itself.
     * </ul>
     * @return the class loader.
     */
    public ClassLoader getClassLoader() {
        if (this.classLoader != null) {
            return this.classLoader;
        }

        if (this.useContextClassLoader) {
            final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            if (contextLoader != null) {
                return contextLoader;
            }
        }

        return this.getClass().getClassLoader();
    }

    /**
     * Set the class loader to be used for instantiating application objects
     * when required.
     *
     * @param classLoader The new class loader to use, or <code>null</code>
     *  to revert to the standard rules
     */
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Performs validations based on the configured resources.
     *
     * @return The <code>Map</code> returned uses the property of the
     * <code>Field</code> for the key and the value is the number of error the
     * field had.
     * @throws ValidatorException If an error occurs during validation
     */
    public ValidatorResults validate() throws ValidatorException {
        Locale locale = (Locale) this.getParameterValue(LOCALE_PARAM);

        if (locale == null) {
            locale = Locale.getDefault();
        }

        this.setParameter(VALIDATOR_PARAM, this);

        final Form form = this.resources.getForm(locale, this.formName);
        if (form != null) {
            this.setParameter(FORM_PARAM, form);
            return form.validate(
                this.parameters,
                this.resources.getValidatorActions(),
                this.page,
                this.fieldName);
        }

        return new ValidatorResults();
    }

    /**
     * Returns true if the Validator is only returning Fields that fail validation.
     * @return whether only failed fields are returned.
     */
    public boolean getOnlyReturnErrors() {
        return onlyReturnErrors;
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

}
