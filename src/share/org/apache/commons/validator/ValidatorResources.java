/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/ValidatorResources.java,v 1.17 2003/05/22 03:28:05 dgraham Exp $
 * $Revision: 1.17 $
 * $Date: 2003/05/22 03:28:05 $
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
 *
 * @author David Winterfeldt
 * @author David Graham
 * @version $Revision: 1.17 $ $Date: 2003/05/22 03:28:05 $
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
        "/org/apache/commons/validator/resources/validator_1_1.dtd"
   };

    /**
     * Logger.
     */
    protected static Log log = LogFactory.getLog(ValidatorResources.class);

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
     */
    public ValidatorResources(InputStream in) throws IOException {
        this(new InputStream[] { in });
    }
    
    /**
     * Create a ValidatorResources object from an InputStream.
     * 
     * @param streams An array of InputStreams to several validation.xml 
     * configuration files that will be read in order and merged into this object.  
     * It's the client's responsibility to close these streams.
     */
    public ValidatorResources(InputStream[] streams) throws IOException {
        super();
        
        URL rulesUrl = this.getClass().getResource("digester-rules.xml");
        Digester digester = DigesterLoader.createDigester(rulesUrl);
        digester.setNamespaceAware(true);
        digester.setValidating(false);
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

            try {
                digester.parse(streams[i]);

            } catch (SAXException e) {
                log.error(e.getMessage(), e);
            }
        }
        
        this.process();
    }

    /**
     * Add a <code>FormSet</code> to this <code>ValidatorResources</code>
     * object.  It will be associated with the <code>Locale</code> of the 
     * <code>FormSet</code>.
     * @deprecated Use addFormSet() instead.
     */
    public void put(FormSet fs) {
        this.addFormSet(fs);
    }
    
    /**
     * Add a <code>FormSet</code> to this <code>ValidatorResources</code>
     * object.  It will be associated with the <code>Locale</code> of the 
     * <code>FormSet</code>.
     */
    public void addFormSet(FormSet fs) {
        if (fs == null) {
            return;
        }
        
        String key = buildKey(fs);
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
    public void addConstant(Constant c) {
        if (c != null
            && c.getName() != null
            && c.getName().length() > 0
            && c.getValue() != null
            && c.getValue().length() > 0) {

            if (log.isDebugEnabled()) {
                log.debug(
                    "Adding Global Constant: " + c.getName() + "," + c.getValue());
            }

            hConstants.put(c.getName(), c.getValue());
        }
    }

    /**
     * Add a global constant to the resource.
     */
    public void addConstantParam(String name, String value) {
        if (name != null
            && name.length() > 0
            && value != null
            && value.length() > 0) {

            if (log.isDebugEnabled()) {
                log.debug("Adding Global Constant: " + name + "," + value);
            }

            hConstants.put(name, value);
        }
    }

    /**
     * <p>Add a <code>ValidatorAction</code> to the resource.  It also creates an instance 
     * of the class based on the <code>ValidatorAction</code>s classname and retrieves 
     * the <code>Method</code> instance and sets them in the <code>ValidatorAction</code>.</p>
     */
    public void addValidatorAction(ValidatorAction va) {
        if (va != null
            && va.getName() != null
            && va.getName().length() > 0
            && va.getClassname() != null
            && va.getClassname().length() > 0
            && va.getMethod() != null
            && va.getMethod().length() > 0) {

            va.init();
            va.process(hConstants);

            hActions.put(va.getName(), va);

            if (log.isDebugEnabled()) {
                log.debug(
                    "Add ValidatorAction: "
                        + va.getName()
                        + ","
                        + va.getClassname());
            }
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
     * Builds a key to store the <code>FormSet</code> under based on it's language, country, 
     * and variant values.
     */
    protected String buildKey(FormSet fs) {
        String lang = fs.getLanguage();
        String country = fs.getCountry();
        String variant = fs.getVariant();

        String key = ((lang != null && lang.length() > 0) ? lang : "");
        key += ((country != null && country.length() > 0) ? "_" + country : "");
        key += ((variant != null && variant.length() > 0) ? "_" + variant : "");

        if (key.length() == 0) {
            key = defaultLocale.toString();
        }

        return key;
    }

    /**
     * <p>Gets a <code>Form</code> based on the name of the form and the <code>Locale</code> that 
     * most closely matches the <code>Locale</code> passed in.  The order of <code>Locale</code> 
     * matching is:</p>
     * <ol>
     *    <li>language + country + variant</li>
     *    <li>language + country</li>
     *    <li>language</li>
     *    <li>default locale</li>
     * </ol>
     * @deprecated Use getForm() instead.
     */
    public Form get(Locale locale, Object formKey) {
        return this.getForm(locale, formKey);
    }
    
    /**
     * <p>Gets a <code>Form</code> based on the name of the form and the <code>Locale</code> that 
     * most closely matches the <code>Locale</code> passed in.  The order of <code>Locale</code> 
     * matching is:</p>
     * <ol>
     *    <li>language + country + variant</li>
     *    <li>language + country</li>
     *    <li>language</li>
     *    <li>default locale</li>
     * </ol>
     */
    public Form getForm(Locale locale, Object formKey) {
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
     * @deprecated Use getForm() instead.
     */
    public Form get(
        String language,
        String country,
        String variant,
        Object formKey) {

        return this.getForm(language, country, variant, formKey);
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
     */
    public Form getForm(
        String language,
        String country,
        String variant,
        Object formKey) {

        String key = null;

        key = (language != null && language.length() > 0) ? language : "";
        key += (country != null && country.length() > 0) ? "_" + country : "";
        key += (variant != null && variant.length() > 0) ? "_" + variant : "";

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
     * <p>Process the <code>ValidatorResources</code> object.  </p>
     *
     * <p>Currently sets the <code>FastHashMap</code>s to the 'fast' 
     * mode and call the processes all other resources.</p>
     */
    public void process() {
        hFormSets.setFast(true);
        hConstants.setFast(true);
        hActions.setFast(true);

        processForms();
    }

    /**
     * <p>Process the <code>Form</code> objects.  This clones the <code>Field</code>s 
     * that don't exist in a <code>FormSet</code> compared to the default 
     * <code>FormSet</code>.</p>
     */
    public void processForms() {
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
                    Form defaultForm = get(defaultLocale, formKey);

                    Iterator defaultFields = defaultForm.getFields().iterator();
                    while (defaultFields.hasNext()) {
                        Field defaultField = (Field) defaultFields.next();
                        String fieldKey = defaultField.getKey();

                        if (form.getFieldMap().containsKey(fieldKey)) {
                            newForm.addField(
                                (Field) form.getFieldMap().get(fieldKey));
                                
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

            Form form = get(language, country, variant, formKey);

            if (form.getFieldMap().containsKey(fieldKey)) {
                field = (Field) form.getFieldMap().get(fieldKey);
            }
        }

        if (field == null) {
            if (!GenericValidator.isBlankOrNull(language)
                && !GenericValidator.isBlankOrNull(country)) {

                Form form = get(language, country, null, formKey);

                if (form.getFieldMap().containsKey(fieldKey)) {
                    field = (Field) form.getFieldMap().get(fieldKey);
                }
            }
        }

        if (field == null) {
            if (!GenericValidator.isBlankOrNull(language)) {
                Form form = get(language, null, null, formKey);

                if (form.getFieldMap().containsKey(fieldKey)) {
                    field = (Field) form.getFieldMap().get(fieldKey);
                }
            }
        }

        if (field == null) {
            Form form = get(defaultLocale, formKey);

            if (form.getFieldMap().containsKey(fieldKey)) {
                field = (Field) form.getFieldMap().get(fieldKey);
            }
        }

        return field;
    }

}
