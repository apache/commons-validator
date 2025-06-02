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

import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

/**
 * Factory class used by Digester to create FormSet's.
 *
 * @since 1.2
 */
public class FormSetFactory extends AbstractObjectCreationFactory {

    /** Logging */
    private transient Log log = LogFactory.getLog(FormSetFactory.class);

    /**
     * Constructs a new instance.
     */
    public FormSetFactory() {
        // empty
    }

    /**
     * <p>Create or retrieve a {@code FormSet} based on the language, country
     *    and variant.</p>
     *
     * @param resources The validator resources.
     * @param language The locale's language.
     * @param country The locale's country.
     * @param variant The locale's language variant.
     * @return The FormSet for a locale.
     * @since 1.2
     */
    private FormSet createFormSet(final ValidatorResources resources,
                                  final String language,
                                  final String country,
                                  final String variant) {

        // Retrieve existing FormSet for the language/country/variant
        FormSet formSet = resources.getFormSet(language, country, variant);
        if (formSet != null) {
            if (getLog().isDebugEnabled()) {
                getLog().debug("FormSet[" + formSet.displayKey() + "] found - merging.");
            }
            return formSet;
        }

        // Create a new FormSet for the language/country/variant
        formSet = new FormSet();
        formSet.setLanguage(language);
        formSet.setCountry(country);
        formSet.setVariant(variant);

        // Add the FormSet to the validator resources
        resources.addFormSet(formSet);

        if (getLog().isDebugEnabled()) {
            getLog().debug("FormSet[" + formSet.displayKey() + "] created.");
        }

        return formSet;

    }

    /**
     * <p>Create or retrieve a {@code FormSet} for the specified
     *    attributes.</p>
     *
     * @param attributes The sax attributes for the formset element.
     * @return The FormSet for a locale.
     * @throws Exception If an error occurs creating the FormSet.
     */
    @Override
    public Object createObject(final Attributes attributes) throws Exception {

        final ValidatorResources resources = (ValidatorResources) digester.peek(0);

        final String language = attributes.getValue("language");
        final String country = attributes.getValue("country");
        final String variant = attributes.getValue("variant");

        return createFormSet(resources, language, country, variant);

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
            log = LogFactory.getLog(FormSetFactory.class);
        }
        return log;
    }

}
