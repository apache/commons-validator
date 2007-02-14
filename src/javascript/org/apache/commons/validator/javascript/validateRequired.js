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
    /*$RCSfile: validateRequired.js,v $ $Rev$ $Date$ */
    /**
    *  Check to see if fields must contain a value.
    * Fields are not checked if they are disabled.
    *
    * @param form The form validation is taking place on.
    */

    function validateRequired(form) {
        var isValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();

        var oRequired = eval('new ' + jcv_retrieveFormName(form) +  '_required()');

        for (var x in oRequired) {
            if (!jcv_verifyArrayElement(x, oRequired[x])) {
                continue;
            }
            var field = form[oRequired[x][0]];

            if (!jcv_isFieldPresent(field)) {
                fields[i++] = oRequired[x][1];
                isValid=false;
            } else if ((field.type == 'hidden' ||
                field.type == 'text' ||
                field.type == 'textarea' ||
                field.type == 'file' ||
                field.type == 'radio' ||
                field.type == 'checkbox' ||
                field.type == 'select-one' ||
                field.type == 'password')) {

                var value = '';
                // get field's value
                if (field.type == "select-one") {
                    var si = field.selectedIndex;
                    if (si >= 0) {
                        value = field.options[si].value;
                    }
                } else if (field.type == 'radio' || field.type == 'checkbox') {
                    if (field.checked) {
                        value = field.value;
                    }
                } else {
                    value = field.value;
                }

                if (trim(value).length == 0) {

                    if ((i == 0) && (field.type != 'hidden')) {
                        focusField = field;
                    }
                    fields[i++] = oRequired[x][1];
                    isValid = false;
                }
            } else if (field.type == "select-multiple") { 
                var numOptions = field.options.length;
                lastSelected=-1;
                for(loop=numOptions-1;loop>=0;loop--) {
                    if(field.options[loop].selected) {
                        lastSelected = loop;
                        value = field.options[loop].value;
                        break;
                    }
                }
                if(lastSelected < 0 || trim(value).length == 0) {
                    if(i == 0) {
                        focusField = field;
                    }
                    fields[i++] = oRequired[x][1];
                    isValid=false;
                }
            } else if ((field.length > 0) && (field[0].type == 'radio' || field[0].type == 'checkbox')) {
                isChecked=-1;
                for (loop=0;loop < field.length;loop++) {
                    if (field[loop].checked) {
                        isChecked=loop;
                        break; // only one needs to be checked
                    }
                }
                if (isChecked < 0) {
                    if (i == 0) {
                        focusField = field[0];
                    }
                    fields[i++] = oRequired[x][1];
                    isValid=false;
                }
            }   
        }
        if (fields.length > 0) {
           jcv_handleErrors(fields, focusField);
        }
        return isValid;
    }
