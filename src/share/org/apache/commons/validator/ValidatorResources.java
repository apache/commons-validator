/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/ValidatorResources.java,v 1.32 2004/04/08 23:05:39 dgraham Exp $
 * $Revision: 1.32 $
 * $Date: 2004/04/08 23:05:39 $
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.FastHashMap;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.SAXException;

/**
 * <p>
 * General purpose class for storing <code>FormSet</code> objects based
 * on their associated <code>Locale</code>.  Instances of this class are usually
 * configured through a validation.xml file that is parsed in a constructor.
 * </p>
 *
 * <p><strong>Note</strong> - Classes that extend this class
 * must be Serializable so that instances may be used in distributable
 * application server environments.</p>
 */
public class ValidatorResources implements Serializable {

    /**
     * The set of public identifiers, and corresponding resource names, for
     * the versions of the configuration file DTDs that we know about.  There
     * <strong>MUST</strong> be an even number of Strings in this list!
     */
    private static final String registrations[] = {
        "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.0//EN",
        "/org/apache/commons/validator/resources/validator_1_0.dtd",
        "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.0.1//EN",
        "/org/apache/commons/validator/resources/validator_1_0_1.dtd",
        "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.1//EN",
        "/org/apache/commons/validator/resources/validator_1_1.dtd",
        "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.2.0//EN",
        "/org/apache/commons/validator/resources/validator_1_2_0.dtd"
    };

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(ValidatorResources.class);

    /**
     * <code>FastHashMap</code> of <code>FormSet</code>s stored under
     * a <code>Locale</code> key.
     */
    protected FastHashMap hFormSets = new FastHashMap();

    /**
     * <code>FastHashMap</code> of global constant values with
     * the name of the constant as the key.
     */
    protected FastHashMap hConstants = new FastHashMap();

    /**
     * <code>FastHashMap</code> of <code>ValidatorAction</code>s with
     * the name of the <code>ValidatorAction</code> as the key.
     */
    protected FastHashMap hActions = new FastHashMap();

    /**
     * The default locale on our server.
     */
    protected static Locale defaultLocale = Locale.getDefault();

    /**
     * Create an empty ValidatorResources object.
     */
    public ValidatorResources() {
        super();
    }

    /**
     * Create a ValidatorResources object from an InputStream.
     *
     * @param in InputStream to a validation.xml configuration file.  It's the client's
     * responsibility to close this stream.
     * @throws IOException
     * @throws SAXException if the validation XML files are not valid or well
     * formed.
     * @since Validator 1.1
     */
    public ValidatorResources(InputStream in) throws IOException, SAXException {
        this(new InputStream[]{in});
    }

    /**
     * Create a ValidatorResources object from an InputStream.
     *
     * @param streams An array of InputStreams to several validation.xml
     * configuration files that will be read in order and merged into this object.
     * It's the client's responsibility to close these streams.
     * @throws IOException
     * @throws SAXException if the validation XML files are not valid or well
     * formed.
     * @since Validator 1.1
     */
    public ValidatorResources(InputStream[] streams)
            throws IOException, SAXException {

        super();

        URL rulesUrl = this.getClass().getResource("digester-rules.xml");
        Digester digester = DigesterLoader.createDigester(rulesUrl);
        digester.setNamespaceAware(true);
        digester.setValidating(true);
        digester.setUseContextClassLoader(true);

        // register DTDs
        for (int i = 0; i < registrations.length; i += 2) {
            URL url = this.getClass().getResource(registrations[i + 1]);
            if (url != null) {
                digester.register(registrations[i], url.toString());
            }
        }

        for (int i = 0; i < streams.length; i++) {
            digester.push(this);
            digester.parse(streams[i]);
        }

        this.process();
    }

    /**
     * Add a <code>FormSet</code> to this <code>ValidatorResources</code>
     * object.  It will be associated with the <code>Locale</code> of the
     * <code>FormSet</code>.
     * @since Validator 1.1
     */
    public void addFormSet(FormSet fs) {
        String key = this.buildKey(fs);
        List formsets = (List) hFormSets.get(key);

        if (formsets == null) {
            formsets = new ArrayList();
            hFormSets.put(key, formsets);
        }

        if (!formsets.contains(fs)) {
            if (log.isDebugEnabled()) {
                log.debug("Adding FormSet '" + fs.toString() + "'.");
            }
            formsets.add(fs);
        }
    }

    /**
     * Add a global constant to the resource.
     */
    public void addConstant(String name, String value) {
        if (log.isDebugEnabled()) {
            log.debug("Adding Global Constant: " + name + "," + value);
        }

        this.hConstants.put(name, value);
    }

    /**
     * Add a <code>ValidatorAction</code> to the resource.  It also creates an
     * instance of the class based on the <code>ValidatorAction</code>s
     * classname and retrieves the <code>Method</code> instance and sets them
     * in the <code>ValidatorAction</code>.
     */
    public void addValidatorAction(ValidatorAction va) {
        va.init();

        this.hActions.put(va.getName(), va);

        if (log.isDebugEnabled()) {
            log.debug("Add ValidatorAction: " + va.getName() + "," + va.getClassname());
        }
    }

    /**
     * Get a <code>ValidatorAction</code> based on it's name.
     */
    public ValidatorAction getValidatorAction(String key) {
        return (ValidatorAction) hActions.get(key);
    }

    /**
     * Get an unmodifiable <code>Map</code> of the <code>ValidatorAction</code>s.
     */
    public Map getValidatorActions() {
        return Collections.unmodifiableMap(hActions);
    }

    /**
     * Builds a key to store the <code>FormSet</code> under based on it's
     * language, country, and variant values.
     */
    protected String buildKey(FormSet fs) {
        String locale =
                this.buildLocale(fs.getLanguage(), fs.getCountry(), fs.getVariant());

        if (locale.length() == 0) {
            locale = defaultLocale.toString();
        }

        return locale;
    }

    /**
     * Assembles a Locale code from the given parts.
     */
    private String buildLocale(String lang, String country, String variant) {
        String key = ((lang != null && lang.length() > 0) ? lang : "");
        key += ((country != null && country.length() > 0) ? "_" + country : "");
        key += ((variant != null && variant.length() > 0) ? "_" + variant : "");
        return key;
    }

    /**
     * <p>Gets a <code>Form</code> based on the name of the form and the
     * <code>Locale</code> that most closely matches the <code>Locale</code>
     * passed in.  The order of <code>Locale</code> matching is:</p>
     * <ol>
     *    <li>language + country + variant</li>
     *    <li>language + country</li>
     *    <li>language</li>
     *    <li>default locale</li>
     * </ol>
     * @since Validator 1.1
     */
    public Form getForm(Locale locale, String formKey) {
        return this.getForm(
                locale.getLanguage(),
                locale.getCountry(),
                locale.getVariant(),
                formKey);
    }

    /**
     * <p>Gets a <code>Form</code> based on the name of the form and the
     * <code>Locale</code> that most closely matches the <code>Locale</code>
     * passed in.  The order of <code>Locale</code> matching is:</p>
     * <ol>
     *    <li>language + country + variant</li>
     *    <li>language + country</li>
     *    <li>language</li>
     *    <li>default locale</li>
     * </ol>
     * @since Validator 1.1
     */
    public Form getForm(
            String language,
            String country,
            String variant,
            String formKey) {

        String key = this.buildLocale(language, country, variant);

        List v = (List) hFormSets.get(key);

        if (v == null) {
            key = (language != null && language.length() > 0) ? language : "";
            key += (country != null && country.length() > 0) ? "_" + country : "";
            v = (List) hFormSets.get(key);
        }

        if (v == null) {
            key = (language != null && language.length() > 0) ? language : "";
            v = (List) hFormSets.get(key);
        }

        if (v == null) {
            key = defaultLocale.toString();
            v = (List) hFormSets.get(key);
        }

        if (v == null) {
            return null;
        }

        Iterator formsets = v.iterator();
        while (formsets.hasNext()) {
            FormSet set = (FormSet) formsets.next();

            if ((set != null) && (set.getForm(formKey) != null)) {
                return set.getForm(formKey);
            }

        }
        return null;
    }

    /**
     * Process the <code>ValidatorResources</code> object.  Currently sets the
     * <code>FastHashMap</code>s to the 'fast' mode and call the processes all
     * other resources.  <strong>Note</strong>: The framework calls this automatically
     * when ValidatorResources is created from an XML file.  If you create an instance
     * of this class by hand you <strong>must</strong> call this method when finished.
     */
    public void process() {
        hFormSets.setFast(true);
        hConstants.setFast(true);
        hActions.setFast(true);

        this.processForms();
    }

    /**
     * <p>Process the <code>Form</code> objects.  This clones the <code>Field</code>s
     * that don't exist in a <code>FormSet</code> compared to the default
     * <code>FormSet</code>.</p>
     */
    private void processForms() {
        //hFormSets.put(buildKey(fs), fs);
        String defaultKey = defaultLocale.toString();

        // Loop through FormSets
        for (Iterator i = hFormSets.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            // Skip default FormSet
            if (key.equals(defaultKey)) {
                continue;
            }
            List formsets = (List) hFormSets.get(key);
            Iterator formsetsIterator = formsets.iterator();
            while (formsetsIterator.hasNext()) {
                FormSet fs = (FormSet) formsetsIterator.next();

                // Loop through Forms and copy/clone fields from default locale
                for (Iterator x = fs.getForms().keySet().iterator(); x.hasNext();) {
                    String formKey = (String) x.next();
                    Form form = (Form) fs.getForms().get(formKey);
                    // Create a new Form object so the order from the default is
                    // maintained (very noticable in the JavaScript).
                    Form newForm = new Form();
                    newForm.setName(form.getName());

                    // Loop through the default locale form's fields
                    // If they don't exist in the current locale's form, then clone them.
                    Form defaultForm = this.getForm(defaultLocale, formKey);

                    Iterator defaultFields = defaultForm.getFields().iterator();
                    while (defaultFields.hasNext()) {
                        Field defaultField = (Field) defaultFields.next();
                        String fieldKey = defaultField.getKey();

                        if (form.containsField(fieldKey)) {
                            newForm.addField(form.getField(fieldKey));

                        } else {
                            Field field =
                                    getClosestLocaleField(fs, formKey, fieldKey);

                            newForm.addField((Field) field.clone());
                        }
                    }

                    fs.addForm(newForm);
                }
            }
        }

        // Process Fully Constructed FormSets
        for (Iterator i = hFormSets.values().iterator(); i.hasNext();) {
            List formsets = (List) i.next();
            Iterator formsetsIterator = formsets.iterator();
            while (formsetsIterator.hasNext()) {
                FormSet fs = (FormSet) formsetsIterator.next();

                if (!fs.isProcessed()) {
                    fs.process(hConstants);
                }
            }
        }
    }

    /**
     * Retrieves the closest matching <code>Field</code> based
     * on <code>FormSet</code>'s locale.  This is used when
     * constructing a clone, field by field, of partial
     * <code>FormSet</code>.
     */
    protected Field getClosestLocaleField(
            FormSet fs,
            String formKey,
            String fieldKey) {

        Field field = null;
        String language = fs.getLanguage();
        String country = fs.getCountry();
        String variant = fs.getVariant();

        if (!GenericValidator.isBlankOrNull(language)
                && !GenericValidator.isBlankOrNull(country)
                && !GenericValidator.isBlankOrNull(variant)) {

            Form form = this.getForm(language, country, variant, formKey);
            field = form.getField(fieldKey);
        }

        if (field == null) {
            if (!GenericValidator.isBlankOrNull(language)
                    && !GenericValidator.isBlankOrNull(country)) {

                Form form = this.getForm(language, country, null, formKey);
                field = form.getField(fieldKey);
            }
        }

        if (field == null) {
            if (!GenericValidator.isBlankOrNull(language)) {
                Form form = this.getForm(language, null, null, formKey);
                field = form.getField(fieldKey);
            }
        }

        if (field == null) {
            Form form = this.getForm(defaultLocale, formKey);
            field = form.getField(fieldKey);
        }

        return field;
    }

}
