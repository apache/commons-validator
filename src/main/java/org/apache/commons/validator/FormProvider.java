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

import org.apache.commons.logging.Log;

import java.util.Map;

public abstract class FormProvider {

    protected Map<String, FormSet> formSets;

    public FormProvider(Map<String, FormSet> formSets) {
        this.formSets = formSets;
    }

    public abstract Form getForm(String formKey);

    public static String buildLocale(final String lang, final String country, final String variant) {
        String key = ((lang != null && !lang.isEmpty()) ? lang : "");
        key += ((country != null && !country.isEmpty()) ? "_" + country : "");
        key += ((variant != null && !variant.isEmpty()) ? "_" + variant : "");
        return key;
    }

    public void logFormInfo(Log log, Form form, String key, String localeKey, String formKey){
        if (form == null) {
            if (log.isWarnEnabled()) {
                log.warn("Form '" + formKey + "' not found for locale '" +
                         localeKey + "'");
            }
        } else if (log.isDebugEnabled()) {
            log.debug("Form '" + formKey + "' found in formset '" +
                      key + "' for locale '" + localeKey + "'");
        }
    }

}
