/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/Validator.java,v 1.21 2003/05/22 02:29:47 dgraham Exp $
 * $Revision: 1.21 $
 * $Date: 2003/05/22 02:29:47 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.util.ValidatorUtils;

/**
 * <p>Validations are processed by the validate method. An instance of 
 * <code>ValidatorResources</code> is used to define the validators 
 * (validation methods) and the validation rules for a JavaBean.</p>
 *
 * @author David Winterfeldt
 * @author James Turner
 * @author David Graham
 * @version $Revision: 1.21 $ $Date: 2003/05/22 02:29:47 $
 */
public class Validator implements Serializable {

    /**
     * Logger.
     */
    protected static Log log = LogFactory.getLog(Validator.class);

    /**
     * Resources key the JavaBean is stored to perform validation on.
     */
    public static final String BEAN_KEY = "java.lang.Object";

    /**
     * Resources key the <code>ValidatorAction</code> is stored under.
     * This will be automatically passed into a validation method
     * with the current <code>ValidatorAction</code> if it is
     * specified in the method signature.
     */
    public static final String VALIDATOR_ACTION_KEY =
        "org.apache.commons.validator.ValidatorAction";

    /**
     * Resources key the <code>Field</code> is stored under.
     * This will be automatically passed into a validation method
     * with the current <code>Field</code> if it is
     * specified in the method signature.
     */
    public static final String FIELD_KEY = "org.apache.commons.validator.Field";

    /**
     * Resources key the <code>Validator</code> is stored under.
     * This will be automatically passed into a validation method
     * with the current <code>Validator</code> if it is
     * specified in the method signature.
     */
    public static final String VALIDATOR_KEY = "org.apache.commons.validator.Validator";

    /**
     * Resources key the <code>Locale</code> is stored.
     * This will be used to retrieve the appropriate
     * <code>FormSet</code> and <code>Form</code> to be
     * processed.
     */
    public static final String LOCALE_KEY = "java.util.Locale";

    protected ValidatorResources resources = null;
    
    protected String formName = null;

    /**
     * Maps validation method parameter class names to the objects to be passed 
     * into the method.
     */
    protected HashMap parameters = new HashMap();

    /**
     * @deprecated Use parameters instead.
     */
    protected HashMap hResources = this.parameters;
    
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
    protected ClassLoader classLoader = null;

    /**
     * Whether or not to use the Context ClassLoader when loading classes
     * for instantiating new objects.  Default is <code>false</code>.
     */
    protected boolean useContextClassLoader = false;

    /**
     * Construct a <code>Validator</code> that will
     * use the <code>ValidatorResources</code>
     * passed in to retrieve pluggable validators
     * the different sets of validation rules.
     *
     * @param resources <code>ValidatorResources</code> to use during validation.
     */
    public Validator(ValidatorResources resources) {
        this.resources = resources;
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
    public Validator(ValidatorResources resources, String formName) {
        this.resources = resources;
        this.formName = formName;
    }

    /**
     * Add a resource to be used during the processing
     * of validations.
     *
     * @param parameterClassName The full class name of the parameter of the 
     * validation method that corresponds to the value/instance passed in with it.
     * 
     * @param parameterValue The instance that will be passed into the 
     * validation method.
     * @deprecated Use addParameter(String, Object) instead.
     */
    public void addResource(String parameterClassName, Object parameterValue) {
        this.addParameter(parameterClassName, parameterValue);
    }
    
    /**
     * Add a resource to be used during the processing
     * of validations.
     *
     * @param parameterClassName The full class name of the parameter of the 
     * validation method that corresponds to the value/instance passed in with it.
     * 
     * @param parameterValue The instance that will be passed into the 
     * validation method.
     */
    public void addParameter(String parameterClassName, Object parameterValue) {
        this.parameters.put(parameterClassName, parameterValue);
    }

    /**
     * Get a resource to be used during the processing of validations.
     *
     * @param parameterClassName The full class name of the parameter of the 
     * validation method that corresponds to the value/instance passed in with it.
     * @deprecated Use getParameterValue(String) instead.
     */
    public Object getResource(String parameterClassName) {
        return this.getParameterValue(parameterClassName);
    }
    
    /**
     * Returns the value of the specified parameter that will be used during the 
     * processing of validations.
     *
     * @param parameterClassName The full class name of the parameter of the 
     * validation method that corresponds to the value/instance passed in with it.
     */
    public Object getParameterValue(String parameterClassName) {
        return this.parameters.get(parameterClassName);
    }

    /**
     * Gets the form name which is the key to a set of validation rules.
     */
    public String getFormName() {
        return formName;
    }

    /**
     * Sets the form name which is the key to a set of validation rules.
     */
    public void setFormName(String formName) {
        this.formName = formName;
    }

    /**
     * Gets the page.  This in conjunction with the page property of 
     * a <code>Field<code> can control the processing of fields. If the field's 
     * page is less than or equal to this page value, it will be processed.
     */
    public int getPage() {
        return page;
    }

    /**
     * Sets the page.  This in conjunction with the page property of 
     * a <code>Field<code> can control the processing of fields. If the field's page 
     * is less than or equal to this page value, it will be processed.
     */
    public void setPage(int page) {
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
        this.parameters = new HashMap();
        this.hResources = this.parameters;
        this.page = 0;
    }

    /**
     * Return the boolean as to whether the context classloader should be used.
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
    public void setUseContextClassLoader(boolean use) {
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
     */
    public ClassLoader getClassLoader() {
        if (this.classLoader != null) {
            return this.classLoader;
        }

        if (this.useContextClassLoader) {
            ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
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
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Executes the given ValidatorAction and all ValidatorActions that it depends on.
     * @return true if the validation succeeded.
     */
    private boolean validateFieldForRule(
        Field field,
        ValidatorAction va,
        ValidatorResults results,
        Map actions,
        int pos)
        throws ValidatorException {

        ValidatorResult result = results.getValidatorResult(field.getKey());
        if (result != null && result.containsAction(va.getName())) {
            return result.isValid(va.getName());
        }

        if(!this.runDependentValidators(field, va, results, actions, pos)){
            return false;
        }

        return this.executeValidationMethod(field, va, results, pos);
    }

    /**
     * Calls all of the validators that this validator depends on.
     * @param field
     * @param va
     * @param results
     * @param actions
     * @param pos
     * @return true if all of the dependent validations passed.
     * @throws ValidatorException
     */
    private boolean runDependentValidators(
        Field field,
        ValidatorAction va,
        ValidatorResults results,
        Map actions,
        int pos)
        throws ValidatorException {

        if (va.getDepends() == null) {
            return true;
        }
        
        StringTokenizer st = new StringTokenizer(va.getDepends(), ",");
        while (st.hasMoreTokens()) {
            String depend = st.nextToken().trim();

            ValidatorAction action = (ValidatorAction) actions.get(depend);
            if (action == null) {
                log.error(
                    "No ValidatorAction named "
                        + depend
                        + " found for field "
                        + field.getProperty());

                return false;
            }

            if (!this.validateFieldForRule(field, action, results, actions, pos)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Dynamically runs the validation method for this validator and returns true if the
     * data is valid. 
     * @param field
     * @param va
     * @param results
     * @param pos
     * @throws ValidatorException
     */
    private boolean executeValidationMethod(
        Field field,
        ValidatorAction va,
        ValidatorResults results,
        int pos)
        throws ValidatorException {
            
        try {
            // Add these two Objects to the resources since they reference
            // the current validator action and field
            this.parameters.put(VALIDATOR_ACTION_KEY, va);
            this.parameters.put(FIELD_KEY, field);
        
            Class validationClass = getClassLoader().loadClass(va.getClassname());
        
            List params = va.getMethodParamsList();
            
            Class[] paramClass = this.getParameterClasses(params);
            Object[] paramValue = this.getParameterValues(params);
        
            Method validationMethod =
                validationClass.getMethod(va.getMethod(), paramClass);
        
            // If the method is static, we don't need an instance of the class
            // to call the method.
            if (!Modifier.isStatic(validationMethod.getModifiers())) {
                this.storeClassInAction(validationClass, va);
            }

            if (field.isIndexed()) {
                this.handleIndexedField(field, pos, params, paramValue);
            } 
            
            Object result =
                validationMethod.invoke(va.getClassnameInstance(), paramValue);
                
            results.add(field, va.getName(), isValid(result), result);
            
            if (!this.isValid(result)) {
                return false;
            }
            
        } catch (Exception e) {
            log.error("reflection: " + e.getMessage(), e);
        
            results.add(field, va.getName(), false);
        
            if (e instanceof ValidatorException) {
                throw (ValidatorException) e;
            }
            return false;
        }
        
        return true;
    }
    
    /**
     * Converts a List of parameter class names into their Class objects.
     * @param paramNames
     * @return An array containing the Class object for each parameter.  This array 
     * is in the same order as the given List and is suitable for passing to the validation
     * method.
     * @throws ClassNotFoundException
     */
    private Class[] getParameterClasses(List paramNames)
        throws ClassNotFoundException {
            
        Class[] paramClass = new Class[paramNames.size()];

        for (int i = 0; i < paramNames.size(); i++) {
            String paramClassName = (String) paramNames.get(i);

            // There were problems calling getClass on paramValue[]
            paramClass[i] = this.getClassLoader().loadClass(paramClassName);
        }

        return paramClass;
    }
    
    /**
     * Converts a List of parameter class names into their values contained in the 
     * parameters Map.
     * @param paramNames
     * @return An array containing the value object for each parameter.  This array 
     * is in the same order as the given List and is suitable for passing to the validation
     * method.
     */
    private Object[] getParameterValues(List paramNames) {
            
        Object[] paramValue = new Object[paramNames.size()];

        for (int i = 0; i < paramNames.size(); i++) {
            String paramClassName = (String) paramNames.get(i);
            paramValue[i] = this.parameters.get(paramClassName);
        }

        return paramValue;
    }

    /**
     * If the given action doesn't already have an instance of the class, store a new
     * instance in the action.
     * @param validationClass The pluggable validation class to store.
     * @param va The ValidatorAction to store the object in.
     */
    private void storeClassInAction(Class validationClass, ValidatorAction va) {
        try {
            if (va.getClassnameInstance() == null) {
                va.setClassnameInstance(validationClass.newInstance());
            }
        } catch (Exception ex) {
            log.error(
                "Couldn't load instance "
                    + "of class "
                    + va.getClassname()
                    + ".  "
                    + ex.getMessage());
        }
    }

    /**
     * Modifies the paramValue array with indexed fields.
     * 
     * @param field
     * @param pos
     * @param params
     * @param paramValue
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    private void handleIndexedField(
        Field field,
        int pos,
        List params,
        Object[] paramValue)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            
        int beanIndexPos = params.indexOf(BEAN_KEY);
        int fieldIndexPos = params.indexOf(FIELD_KEY);
        
        Object oIndexed =
            PropertyUtils.getProperty(
                this.parameters.get(BEAN_KEY),
                field.getIndexedListProperty());
                
        Object indexedList[] = new Object[0];
        
        if (oIndexed instanceof Collection) {
            indexedList = ((Collection) oIndexed).toArray();
            
        } else if (oIndexed.getClass().isArray()) {
            indexedList = (Object[]) oIndexed;
        }
        
        // Set current iteration object to the parameter array
        paramValue[beanIndexPos] = indexedList[pos];
        
        // Set field clone with the key modified to represent
        // the current field
        Field indexedField = (Field) field.clone();
        indexedField.setKey(
            ValidatorUtils.replace(
                indexedField.getKey(),
                Field.TOKEN_INDEXED,
                "[" + pos + "]"));
                
        paramValue[fieldIndexPos] = indexedField;
    }

    /**
     * Run the validations on a given field, modifying the passed
     * ValidatorResults to add in any new errors found.  Run all the validations in 
     * the depends clause over each item in turn, returning when the first one fails.
     */
    private void validateField(Field field, ValidatorResults allResults)
        throws ValidatorException {
            
        int length = 1; // default to non-indexed length of 1
        
        // this block only finds out how many times to run the validation
        if (field.isIndexed()) {
            Object oIndexed;
            try {
                oIndexed =
                    PropertyUtils.getProperty(
                        this.parameters.get(BEAN_KEY),
                        field.getIndexedListProperty());
        
            } catch (Exception e) {
                log.error("in validateField", e);
                return;
            }
        
            Object indexedList[] = new Object[0];
        
            if (oIndexed instanceof Collection) {
                indexedList = ((Collection) oIndexed).toArray();
            } else if (oIndexed.getClass().isArray()) {
                indexedList = (Object[]) oIndexed;
            }
        
            length = indexedList.length;
        }
        
        this.validateList(field, allResults, length);
    }

    /**
     * Runs all validations on the field.
     * @param field
     * @param allResults
     * @param length 1 for non-indexed fields, the array length for indexed fields.
     * @throws ValidatorException
     */
    private void validateList(
        Field field,
        ValidatorResults allResults,
        int length)
        throws ValidatorException {
            
        Map actions = resources.getValidatorActions();
            
        for (int pos = 0; pos < length; pos++) {
            ValidatorResults results = new ValidatorResults();
            StringTokenizer st = new StringTokenizer(field.getDepends(), ",");
            while (st.hasMoreTokens()) {
                String depend = st.nextToken().trim();
        
                ValidatorAction action = (ValidatorAction) actions.get(depend);
                if (action == null) {
                    log.error(
                        "No ValidatorAction named "
                            + depend
                            + " found for field "
                            + field.getProperty());
                            
                    return;
                }
        
                boolean good = validateFieldForRule(field, action, results, actions, pos);
        
                allResults.merge(results);
        
                if (!good) {
                    return;
                }
            }
        }
    }

    /**
     * Performs validations based on the configured resources.
     *
     * @return The <code>Map</code> returned uses the property of the 
     * <code>Field</code> for the key and the value is the number of error the 
     * field had.
     */
    public ValidatorResults validate() throws ValidatorException {
        ValidatorResults results = new ValidatorResults();
        Locale locale = (Locale) this.parameters.get(LOCALE_KEY);

        if (locale == null) {
            locale = Locale.getDefault();
        }
        
        this.parameters.put(VALIDATOR_KEY, this);

        if (this.resources == null) {
            throw new ValidatorException("Resources not defined for Validator");
        }

        Form form = this.resources.getForm(locale, this.formName);
        if (form != null) {
            Iterator forms = form.getFields().iterator();
            while (forms.hasNext()) {
                Field field = (Field) forms.next();
                
                if ((field.getPage() <= page) && (field.getDepends() != null)) {
                    this.validateField(field, results);
                }
            }

        }
        
        return results;
    }

    /**
     * Returns if the result if valid.  If the
     * result object is <code>Boolean</code>, then it will
     * the value.  If the result object isn't <code>Boolean</code>,
     * then it will return <code>false</code> if the result
     * object is <code>null</code> and <code>true</code> if it isn't.
     */
    private boolean isValid(Object result) {

        if (result instanceof Boolean) {
            Boolean valid = (Boolean) result;
            return valid.booleanValue();
        } else {
            return (result != null);
        }

    }
    
}
