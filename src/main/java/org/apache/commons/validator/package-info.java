/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * The Validator package provides validation for JavaBeans based on an xml file.
 *
 * <a id="doc.Description"></a>
 * <div>
 * <a href="http://commons.apache.org/validator/dependencies.html">[Dependencies]</a>
 * <a href="#doc.Intro">[Introduction]</a>
 * <a href="#doc.Overview">[Overview]</a>
 * <a href="#doc.Resources">[Resources]</a>
 * <a href="#doc.Usage">[Usage Example]</a>
 * </div>
 * <a id="doc.Intro"></a>
 * <h2>Introduction</h2>
 * <p>A common issue when receiving data either electronically or from
 * user input is verifying the integrity of the data.  This work is
 * repetitive and becomes even more complicated when different sets
 * of validation rules need to be applied to the same set of data based
 * on locale for example.  Error messages may also vary by locale.
 * This package attempts to address some of these issues and
 * speed development and maintenance of validation rules.
 * </p>
 * <p>In order to use the Validator, the following basic steps are required:</p>
 * <ul>
 * <li>Create a new instance of the
 * <code>org.apache.commons.validator.Validator</code> class.  Currently
 * Validator instances may be safely reused if the current ValidatorResources
 * are the same, as long as
 * you have completed any previous validation, and you do not try to utilize
 * a particular Validator instance from more than one thread at a time.</li>
 * <li>Add any <a href="#doc.Resources">resources</a>
 * needed to perform the validations.  Such as the JavaBean to validate.</li>
 * <li>Call the validate method on <code>org.apache.commons.validator.Validator</code>.</li>
 * </ul>
 * <a id="doc.Overview"></a>
 * <h2>Overview</h2>
 * <p>
 * The Commons Validator is a basic validation framework that
 * lets you define validation rules for a JavaBean in an xml file.
 * Validators, the validation definition, can also be defined in
 * the xml file.  An example of a validator would be defining
 * what method and class will be called to perform the validation
 * for a required field.  Validation rules can be grouped together
 * based on locale and a JavaBean/Form that the rules are associated
 * with.  The framework has basic support for user defined constants
 * which can be used in some field attributes.
 * </p>
 * <p>
 * Validation rules can be defined in an xml file which keeps
 * them abstracted from JavaBean you are validating.  The
 * property reference to a field supports nested properties
 * using the Apache Commons BeanUtils
 * (http://commons.apache.org/beanutils/) package.
 * Error messages and the arguments for error messages can be
 * associated with a fields validation.
 * </p>
 * <a id="doc.Resources"></a>
 * <h2>Resources</h2>
 * <p>
 * After a Validator instance is created, instances of
 * classes can be added to it to be passed into
 * validation methods by calling the setParameter()
 * method.  Below is a list of reserved parameters (class names).
 * </p>
 * <table border="1">
 * <caption>Reserved Parameters</caption>
 * <tr>
 * <th>Class Name</th>
 * <th>Validator Contstant</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>java.lang.Object</td>
 * <td>Validator.BEAN_PARAM</td>
 * <td>JavaBean that is being validated</td>
 * </tr>
 * <tr>
 * <td>java.util.Locale</td>
 * <td>Validator.LOCALE_PARAM</td>
 * <td>
 * Locale to use when retrieving a FormSet.
 * The default locale will be used if one
 * isn't specified.
 * </td>
 * </tr>
 * <tr>
 * <td>org.apache.commons.validator.ValidatorAction</td>
 * <td>Validator.VALIDATOR_ACTION_PARAM</td>
 * <td>
 * This is automatically added to a Validator's
 * resources as a validation is being processed.
 * If this class name is used when defining
 * a method signature for a pluggable validator,
 * the current ValidatorAction will be passed into
 * the validation method.
 * </td>
 * </tr>
 * <tr>
 * <td>org.apache.commons.validator.Field</td>
 * <td>Validator.FIELD_PARAM</td>
 * <td>
 * This is automatically added to a Validator's
 * resources as a validation is being processed.
 * If this class name is used when defining
 * a method signature for a pluggable validator,
 * the current Field will be passed into
 * the validation method.
 * </td>
 * </tr>
 * </table>
 * <a id="doc.Usage"></a>
 * <h2>Usage Example</h2>
 * <p>
 * This is a basic example setting up a required validator for
 * a name bean.  This example is a working unit test (reference
 * <code>org.apache.commons.validator.RequiredNameTest</code> and
 * validator-name-required.xml located under validator/src/test).
 * </p>
 * <p>
 * Create an xml file with your validator and validation rules.
 * Setup your required validator in your xml file.<br>
 * <br>
 * <a href="#doc.Usage.xml">XML Example</a><br>
 * <a href="#doc.Usage.validator">Validator Example</a><br>
 * <a href="#doc.Usage.pluggableValidator">Pluggable Validator Example</a>
 * </p>
 * <a id="doc.Usage.xml"></a>
 * <h2>XML Example</h2>
 * <p>
 * Definition of a 'required' pluggable validator.<br>
 * <pre>
 * &lt;form-validation&gt;
 * &lt;global&gt;
 * &lt;validator name="required"
 * classname="org.apache.commons.validator.TestValidator"
 * method="validateRequired"
 * methodParams="java.lang.Object, org.apache.commons.validator.Field"/&gt;
 * &lt;/global&gt;
 * &lt;formset&gt;
 * &lt;/formset&gt;
 * &lt;/form-validation&gt;
 * </pre>
 * <p>
 * Add validation rules to require a first name and a last name.<br>
 * <pre>
 * &lt;form-validation&gt;
 * &lt;global&gt;
 * &lt;validator name="required"
 * classname="org.apache.commons.validator.TestValidator"
 * method="validateRequired"
 * methodParams="java.lang.Object, org.apache.commons.validator.Field"/&gt;
 * &lt;/global&gt;
 * <b>
 * &lt;formset&gt;
 * &lt;form    name="nameForm"&gt;
 * &lt;field property="firstName" depends="required"&gt;
 * &lt;arg0 key="nameForm.firstname.displayname"/&gt;
 * &lt;/field&gt;
 * &lt;field property="lastName" depends="required"&gt;
 * &lt;arg0 key="nameForm.lastname.displayname"/&gt;
 * &lt;/field&gt;
 * &lt;/form&gt;
 * &lt;/formset&gt;
 * </b>
 * &lt;/form-validation&gt;
 * </pre>
 * <a id="doc.Usage.validator"></a>
 * <h2>Validator Example</h2>
 * <p>
 * Excerpts from org.apache.commons.validator.RequiredNameTest
 * </p>
 * <pre>
 * InputStream in = this.getClass().getResourceAsStream("validator-name-required.xml");
 * // Create an instance of ValidatorResources to initialize from an xml file.
 * ValidatorResources resources = new ValidatorResources(in);
 * // Create bean to run test on.
 * Name name = new Name();
 * // Construct validator based on the loaded resources and the form key
 * Validator validator = new Validator(resources, "nameForm");
 * // add the name bean to the validator as a resource
 * // for the validations to be performed on.
 * validator.setParameter(Validator.BEAN_PARAM, name);
 * // Get results of the validation.
 * Map results;
 * // throws ValidatorException (catch clause not shown here)
 * results = validator.validate();
 * if (results.get("firstName") == null) {
 * // no error
 * } else {
 * // number of errors for first name
 * int errors = ((Integer)results.get("firstName")).intValue();
 * }
 * </pre>
 * <a id="doc.Usage.pluggableValidator"></a>
 * <h2>Pluggable Validator Example</h2>
 * <p>
 * Validation method defined in the 'required' pluggable validator
 * (excerpt from org.apache.commons.validator.TestValidator).
 * </p>
 * <pre>
 * public static boolean validateRequired(Object bean, Field field) {
 * String value = ValidatorUtil.getValueAsString(bean, field.getProperty());
 * return GenericValidator.isBlankOrNull(value);
 * }
 * </pre>
 */
package org.apache.commons.validator;
