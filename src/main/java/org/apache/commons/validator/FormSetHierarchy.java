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

public abstract class FormSetHierarchy {
    public abstract FormSet getParent();

    public static FormSetHierarchy getInstance(final FormSet formSet,final FormSet defaultFormSet, final Map<String,FormSet> formSets){
        FormSetHierarchy hierarchy;
        if (formSet.getType() == FormSet.LANGUAGE_FORMSET) {
            hierarchy = new LanguageFormSet(defaultFormSet);
        } else if (formSet.getType() == FormSet.COUNTRY_FORMSET) {
            hierarchy = new CountryFormSet(formSet,defaultFormSet,formSets);
        } else if (formSet.getType() == FormSet.VARIANT_FORMSET) {
            hierarchy = new VariantFormSet(formSet,defaultFormSet,formSets);
        } else {
            throw new IllegalArgumentException("Invalid form set type");
        }
        return hierarchy;
    }

    public String buildLocale(final String lang, final String country, final String variant) {
        String key = ((lang != null && !lang.isEmpty()) ? lang : "");
        key += ((country != null && !country.isEmpty()) ? "_" + country : "");
        key += ((variant != null && !variant.isEmpty()) ? "_" + variant : "");
        return key;
    }
}
