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

public class CountryFormSet extends  FormSetHierarchy{
    private final FormSet formSet;
    private final FormSet defaultFormSet;

    private final Map<String,FormSet> formSets;

    public CountryFormSet(FormSet formSet,FormSet defaultFormSet,Map<String,FormSet> formSets) {
        this.formSet = formSet;
        this.formSets = formSets;
        this.defaultFormSet = defaultFormSet;
    }
    @Override
    public FormSet getParent() {
       FormSet parent = formSets.get(buildLocale(formSet.getLanguage(),
                null, null));
        if (parent == null) {
            parent = defaultFormSet;
        }
        return parent;
    }

}
