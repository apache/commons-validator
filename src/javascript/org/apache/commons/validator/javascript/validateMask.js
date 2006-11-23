/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
    /*$RCSfile: validateMask.js,v $ $Rev$ $Date$ */
    /**
    * Check to see if fields are a valid using a regular expression.
    * Fields are not checked if they are disabled.
    * @param form The form validation is taking place on.
    */
    function validateMask(form) {
        var isValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();
 
        var oMasked = eval('new ' + jcv_retrieveFormName(form) +  '_mask()');      
        for (var x in oMasked) {
            if (!jcv_verifyArrayElement(x, oMasked[x])) {
                continue;
            }
            var field = form[oMasked[x][0]];
            if (!jcv_isFieldPresent(field)) {
              continue;
            }

            if ((field.type == 'hidden' ||
                field.type == 'text' ||
                 field.type == 'textarea' ||
				 field.type == 'file') &&
                 (field.value.length > 0)) {

                if (!jcv_matchPattern(field.value, oMasked[x][2]("mask"))) {
                    if (i == 0) {
                        focusField = field;
                    }
                    fields[i++] = oMasked[x][1];
                    isValid = false;
                }
            }
        }

        if (fields.length > 0) {
           jcv_handleErrors(fields, focusField);
        }
        return isValid;
    }

    function jcv_matchPattern(value, mask) {
       return mask.exec(value);
    }
