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

import java.util.Map;

public class LanguageCountryFormProvider extends FormProvider{

    private String language;
    private String country;

    public LanguageCountryFormProvider(String language, String country, Map<String, FormSet> formSets) {
        super(formSets);
        this.language = language;
        this.country = country;
    }


    @Override
    public Form getForm(String formKey) {
        String key = buildLocale(language, country, null);
        if (!key.isEmpty()){
            FormSet formSet = formSets.get(key);
            if (formSet != null) {
                return formSet.getForm(formKey);
            } else {
                return null;
            }
        }
        return null;

    }
}
