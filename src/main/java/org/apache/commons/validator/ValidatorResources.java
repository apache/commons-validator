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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.FastHashMap; // DEPRECATED
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * <p>
 * General purpose class for storing {@code FormSet} objects based
 * on their associated {@link Locale}.  Instances of this class are usually
 * configured through a validation.xml file that is parsed in a constructor.
 * </p>
 *
 * <p><strong>Note</strong> - Classes that extend this class
 * must be Serializable so that instances may be used in distributable
 * application server environments.</p>
 *
 * <p>
 * The use of FastHashMap is deprecated and will be replaced in a future
 * release.
 * </p>
 */
//TODO mutable non-private fields
public class ValidatorResources implements Serializable {

    private static final long serialVersionUID = -8203745881446239554L;

    /** Name of the digester validator rules file */
    private static final String VALIDATOR_RULES = "digester-rules.xml";

    /**
     * The set of public identifiers, and corresponding resource names, for
     * the versions of the configuration file DTDs that we know about.  There
     * <strong>MUST</strong> be an even number of Strings in this list!
     */
    private static final String[] REGISTRATIONS = {
        "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.0//EN",
        "/org/apache/commons/validator/resources/validator_1_0.dtd",
        "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.0.1//EN",
        "/org/apache/commons/validator/resources/validator_1_0_1.dtd",
        "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.1//EN",
        "/org/apache/commons/validator/resources/validator_1_1.dtd",
        "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.1.3//EN",
        "/org/apache/commons/validator/resources/validator_1_1_3.dtd",
        "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.2.0//EN",
        "/org/apache/commons/validator/resources/validator_1_2_0.dtd",
        "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.3.0//EN",
        "/org/apache/commons/validator/resources/validator_1_3_0.dtd",
        "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.4.0//EN",
        "/org/apache/commons/validator/resources/validator_1_4_0.dtd"
    };

    /**
     * The default locale on our server.
     */
    protected static Locale defaultLocale = Locale.getDefault();

    private static final String ARGS_PATTERN
               = "form-validation/formset/form/field/arg";

    private transient Log log = LogFactory.getLog(ValidatorResources.class);

    /**
     * {@link Map} of {@code FormSet}s stored under
     * a {@link Locale} key (expressed as a String).
     * @deprecated Subclasses should use getFormSets() instead.
     */
    @Deprecated
    protected FastHashMap hFormSets = new FastHashMap(); // <String, FormSet>

    /**
     * {@link Map} of global constant values with
     * the name of the constant as the key.
     * @deprecated Subclasses should use getConstants() instead.
     */
    @Deprecated
    protected FastHashMap hConstants = new FastHashMap(); // <String, String>

    /**
     * {@link Map} of {@code ValidatorAction}s with
     * the name of the {@code ValidatorAction} as the key.
     * @deprecated Subclasses should use getActions() instead.
     */
    @Deprecated
    protected FastHashMap hActions = new FastHashMap(); // <String, ValidatorAction>

    /**
     * This is the default {@code FormSet} (without locale). (We probably don't need
     * the defaultLocale anymore.)
     */
    protected FormSet defaultFormSet;

    /**
     * Create an empty ValidatorResources object.
     */
    public ValidatorResources() {
    }

    /**
     * Create a ValidatorResources object from an InputStream.
     *
     * @param in InputStream to a validation.xml configuration file.  It's the client's
     * responsibility to close this stream.
     * @throws SAXException if the validation XML files are not valid or well-formed.
     * @throws IOException if an I/O error occurs processing the XML files
     * @since 1.1
     */
    public ValidatorResources(final InputStream in) throws IOException, SAXException {
        this(new InputStream[]{in});
    }

    /**
     * Create a ValidatorResources object from an InputStream.
     *
     * @param streams An array of InputStreams to several validation.xml
     * configuration files that will be read in order and merged into this object.
     * It's the client's responsibility to close these streams.
     * @throws SAXException if the validation XML files are not valid or well-formed.
     * @throws IOException if an I/O error occurs processing the XML files
     * @since 1.1
     */
    public ValidatorResources(final InputStream[] streams)
            throws IOException, SAXException {

        final Digester digester = initDigester();
        for (int i = 0; i < streams.length; i++) {
            if (streams[i] == null) {
                throw new IllegalArgumentException("Stream[" + i + "] is null");
            }
            digester.push(this);
            digester.parse(streams[i]);
        }

        process();
    }

    /**
     * Create a ValidatorResources object from an uri
     *
     * @param uri The location of a validation.xml configuration file.
     * @throws SAXException if the validation XML files are not valid or well-formed.
     * @throws IOException if an I/O error occurs processing the XML files
     * @since 1.2
     */
    public ValidatorResources(final String uri) throws IOException, SAXException {
        this(new String[] { uri });
    }

    /**
     * Create a ValidatorResources object from several uris
     *
     * @param uris An array of uris to several validation.xml
     * configuration files that will be read in order and merged into this object.
     * @throws SAXException if the validation XML files are not valid or well-formed.
     * @throws IOException if an I/O error occurs processing the XML files
     * @since 1.2
     */
    public ValidatorResources(final String... uris)
            throws IOException, SAXException {

        final Digester digester = initDigester();
        for (final String element : uris) {
            digester.push(this);
            digester.parse(element);
        }

        process();
    }

    /**
     * Create a ValidatorResources object from a URL.
     *
     * @param url The URL for the validation.xml
     * configuration file that will be read into this object.
     * @throws SAXException if the validation XML file are not valid or well-formed.
     * @throws IOException if an I/O error occurs processing the XML files
     * @since 1.3.1
     */
    public ValidatorResources(final URL url)
            throws IOException, SAXException {
        this(new URL[]{url});
    }

    /**
     * Create a ValidatorResources object from several URL.
     *
     * @param urls An array of URL to several validation.xml
     * configuration files that will be read in order and merged into this object.
     * @throws SAXException if the validation XML files are not valid or well-formed.
     * @throws IOException if an I/O error occurs processing the XML files
     * @since 1.3.1
     */
    public ValidatorResources(final URL[] urls)
            throws IOException, SAXException {

        final Digester digester = initDigester();
        for (final URL url : urls) {
            digester.push(this);
            digester.parse(url);
        }

        process();
    }

    /**
     * Add a global constant to the resource.
     * @param name The constant name.
     * @param value The constant value.
     */
    public void addConstant(final String name, final String value) {
        if (getLog().isDebugEnabled()) {
            getLog().debug("Adding Global Constant: " + name + "," + value);
        }

        hConstants.put(name, value);
    }

    /**
     * Add a {@code FormSet} to this {@code ValidatorResources}
     * object.  It will be associated with the {@link Locale} of the
     * {@code FormSet}.
     * @param fs The form set to add.
     * @since 1.1
     */
    public void addFormSet(final FormSet fs) {
        final String key = buildKey(fs);
        if (key.isEmpty()) { // there can only be one default formset
            if (getLog().isWarnEnabled() && defaultFormSet != null) {
                // warn the user he might not get the expected results
                getLog().warn("Overriding default FormSet definition.");
            }
            defaultFormSet = fs;
        } else {
            final FormSet formset = getFormSets().get(key);
            if (formset == null) { // it hasn't been included yet
                if (getLog().isDebugEnabled()) {
                    getLog().debug("Adding FormSet '" + fs + "'.");
                }
            } else if (getLog().isWarnEnabled()) { // warn the user he might not
                                                   // get the expected results
                getLog().warn("Overriding FormSet definition. Duplicate for locale: " + key);
            }
            getFormSets().put(key, fs);
        }
    }

    /**
     * Create a {@code Rule} to handle {@code arg0-arg3}
     * elements. This will allow validation.xml files that use the
     * versions of the DTD prior to Validator 1.2.0 to continue
     * working.
     */
    private void addOldArgRules(final Digester digester) {
        // Create a new rule to process args elements
        final Rule rule = new Rule() {
            @Override
            public void begin(final String namespace, final String name, final Attributes attributes) {
                // Create the Arg
                final Arg arg = new Arg();
                arg.setKey(attributes.getValue("key"));
                arg.setName(attributes.getValue("name"));
                if ("false".equalsIgnoreCase(attributes.getValue("resource"))) {
                    arg.setResource(false);
                }
                try {
                    final int length = "arg".length(); // skip the arg prefix
                    arg.setPosition(Integer.parseInt(name.substring(length)));
                } catch (final Exception ex) {
                    getLog().error("Error parsing Arg position: " + name + " " + arg + " " + ex);
                }

                // Add the arg to the parent field
                ((Field) getDigester().peek(0)).addArg(arg);
            }
        };

        // Add the rule for each of the arg elements
        digester.addRule(ARGS_PATTERN + "0", rule);
        digester.addRule(ARGS_PATTERN + "1", rule);
        digester.addRule(ARGS_PATTERN + "2", rule);
        digester.addRule(ARGS_PATTERN + "3", rule);

    }

    /**
     * Add a {@code ValidatorAction} to the resource.  It also creates an
     * instance of the class based on the {@code ValidatorAction}s
     * class name and retrieves the {@code Method} instance and sets them
     * in the {@code ValidatorAction}.
     * @param va The validator action.
     */
    public void addValidatorAction(final ValidatorAction va) {
        va.init();

        getActions().put(va.getName(), va);

        if (getLog().isDebugEnabled()) {
            getLog().debug("Add ValidatorAction: " + va.getName() + "," + va.getClassname());
        }
    }

    /**
     * Builds a key to store the {@code FormSet} under based on its
     * language, country, and variant values.
     * @param fs The Form Set.
     * @return generated key for a formset.
     */
    protected String buildKey(final FormSet fs) {
        return
                buildLocale(fs.getLanguage(), fs.getCountry(), fs.getVariant());
    }

    /**
     * Assembles a Locale code from the given parts.
     */
    private String buildLocale(final String lang, final String country, final String variant) {
        final StringBuilder key = new StringBuilder().append(lang != null && !lang.isEmpty() ? lang : "");
        key.append(country != null && !country.isEmpty() ? "_" + country : "");
        key.append(variant != null && !variant.isEmpty() ? "_" + variant : "");
        return key.toString();
    }

    /**
     * Returns a Map of String ValidatorAction names to their ValidatorAction.
     * @return Map of Validator Actions
     * @since 1.2.0
     */
    @SuppressWarnings("unchecked") // FastHashMap is not generic
    protected Map<String, ValidatorAction> getActions() {
        return hActions;
    }

    /**
     * Returns a Map of String constant names to their String values.
     * @return Map of Constants
     * @since 1.2.0
     */
    @SuppressWarnings("unchecked") // FastHashMap is not generic
    protected Map<String, String> getConstants() {
        return hConstants;
    }

    /**
     * <p>Gets a {@code Form} based on the name of the form and the
     * {@link Locale} that most closely matches the {@link Locale}
     * passed in.  The order of {@link Locale} matching is:</p>
     * <ol>
     *    <li>language + country + variant</li>
     *    <li>language + country</li>
     *    <li>language</li>
     *    <li>default locale</li>
     * </ol>
     * @param locale The Locale.
     * @param formKey The key for the Form.
     * @return The validator Form.
     * @since 1.1
     */
    public Form getForm(final Locale locale, final String formKey) {
        return this.getForm(locale.getLanguage(), locale.getCountry(), locale
                .getVariant(), formKey);
    }

    /**
     * <p>Gets a {@code Form} based on the name of the form and the
     * {@link Locale} that most closely matches the {@link Locale}
     * passed in.  The order of {@link Locale} matching is:</p>
     * <ol>
     *    <li>language + country + variant</li>
     *    <li>language + country</li>
     *    <li>language</li>
     *    <li>default locale</li>
     * </ol>
     * @param language The locale's language.
     * @param country The locale's country.
     * @param variant The locale's language variant.
     * @param formKey The key for the Form.
     * @return The validator Form.
     * @since 1.1
     */
    public Form getForm(final String language, final String country, final String variant, final String formKey) {

        Form form = null;

        // Try language/country/variant
        String key = buildLocale(language, country, variant);
        if (!key.isEmpty()) {
            final FormSet formSet = getFormSets().get(key);
            if (formSet != null) {
                form = formSet.getForm(formKey);
            }
        }
        final String localeKey = key;

        // Try language/country
        if (form == null) {
            key = buildLocale(language, country, null);
            if (!key.isEmpty()) {
                final FormSet formSet = getFormSets().get(key);
                if (formSet != null) {
                    form = formSet.getForm(formKey);
                }
            }
        }

        // Try language
        if (form == null) {
            key = buildLocale(language, null, null);
            if (!key.isEmpty()) {
                final FormSet formSet = getFormSets().get(key);
                if (formSet != null) {
                    form = formSet.getForm(formKey);
                }
            }
        }

        // Try default formset
        if (form == null) {
            form = defaultFormSet.getForm(formKey);
            key = "default";
        }

        if (form == null) {
            if (getLog().isWarnEnabled()) {
                getLog().warn("Form '" + formKey + "' not found for locale '" + localeKey + "'");
            }
        } else if (getLog().isDebugEnabled()) {
            getLog().debug("Form '" + formKey + "' found in formset '" + key + "' for locale '" + localeKey + "'");
        }

        return form;

    }

    /**
     * <p>Gets a {@code FormSet} based on the language, country
     *    and variant.</p>
     * @param language The locale's language.
     * @param country The locale's country.
     * @param variant The locale's language variant.
     * @return The FormSet for a locale.
     * @since 1.2
     */
    FormSet getFormSet(final String language, final String country, final String variant) {
        final String key = buildLocale(language, country, variant);
        if (key.isEmpty()) {
            return defaultFormSet;
        }
        return getFormSets().get(key);
    }

    /**
     * Returns a Map of String locale keys to Lists of their FormSets.
     * @return Map of Form sets
     * @since 1.2.0
     */
    @SuppressWarnings("unchecked") // FastHashMap is not generic
    protected Map<String, FormSet> getFormSets() {
        return hFormSets;
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
            log = LogFactory.getLog(ValidatorResources.class);
        }
        return log;
    }

    /**
     * Finds the given formSet's parent. ex: A formSet with locale en_UK_TEST1
     * has a direct parent in the formSet with locale en_UK. If it doesn't
     * exist, find the formSet with locale en, if no found get the
     * defaultFormSet.
     *
     * @param fs
     *            the formSet we want to get the parent from
     * @return fs's parent
     */
    private FormSet getParent(final FormSet fs) {

        FormSet parent = null;
        if (fs.getType() == FormSet.LANGUAGE_FORMSET) {
            parent = defaultFormSet;
        } else if (fs.getType() == FormSet.COUNTRY_FORMSET) {
            parent = getFormSets().get(buildLocale(fs.getLanguage(), null, null));
            if (parent == null) {
                parent = defaultFormSet;
            }
        } else if (fs.getType() == FormSet.VARIANT_FORMSET) {
            parent = getFormSets().get(buildLocale(fs.getLanguage(), fs.getCountry(), null));
            if (parent == null) {
                parent = getFormSets().get(buildLocale(fs.getLanguage(), null, null));
                if (parent == null) {
                    parent = defaultFormSet;
                }
            }
        }
        return parent;
    }

    /**
     * Gets a {@code ValidatorAction} based on its name.
     * @param key The validator action key.
     * @return The validator action.
     */
    public ValidatorAction getValidatorAction(final String key) {
        return getActions().get(key);
    }

    /**
     * Gets an unmodifiable {@link Map} of the {@code ValidatorAction}s.
     * @return Map of validator actions.
     */
    public Map<String, ValidatorAction> getValidatorActions() {
        return Collections.unmodifiableMap(getActions());
    }

    /**
     *  Initialize the digester.
     */
    private Digester initDigester() {
        URL rulesUrl = this.getClass().getResource(VALIDATOR_RULES);
        if (rulesUrl == null) {
            // Fix for Issue# VALIDATOR-195
            rulesUrl = ValidatorResources.class.getResource(VALIDATOR_RULES);
        }
        if (getLog().isDebugEnabled()) {
            getLog().debug("Loading rules from '" + rulesUrl + "'");
        }
        final Digester digester = DigesterLoader.createDigester(rulesUrl);
        digester.setNamespaceAware(true);
        digester.setValidating(true);
        digester.setUseContextClassLoader(true);

        // Add rules for arg0-arg3 elements
        addOldArgRules(digester);

        // register DTDs
        for (int i = 0; i < REGISTRATIONS.length; i += 2) {
            final URL url = this.getClass().getResource(REGISTRATIONS[i + 1]);
            if (url != null) {
                digester.register(REGISTRATIONS[i], url.toString());
            }
        }
        return digester;
    }

    /**
     * Process the {@code ValidatorResources} object. Currently, sets the
     * {@code FastHashMap} s to the 'fast' mode and call the processes
     * all other resources. <strong>Note </strong>: The framework calls this
     * automatically when ValidatorResources is created from an XML file. If you
     * create an instance of this class by hand you <strong>must </strong> call
     * this method when finished.
     */
    public void process() {
        hFormSets.setFast(true);
        hConstants.setFast(true);
        hActions.setFast(true);

        processForms();
    }

    /**
     * <p>Process the {@code Form} objects.  This clones the {@code Field}s
     * that don't exist in a {@code FormSet} compared to its parent
     * {@code FormSet}.</p>
     */
    private void processForms() {
        if (defaultFormSet == null) { // it isn't mandatory to have a
            // default formset
            defaultFormSet = new FormSet();
        }
        defaultFormSet.process(getConstants());
        // Loop through FormSets and merge if necessary
        for (final String key : getFormSets().keySet()) {
            final FormSet fs = getFormSets().get(key);
            fs.merge(getParent(fs));
        }

        // Process Fully Constructed FormSets
        for (final FormSet fs : getFormSets().values()) {
            if (!fs.isProcessed()) {
                fs.process(getConstants());
            }
        }
    }

}
