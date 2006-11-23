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
    /*$RCSfile: validateIntRange.js,v $ $Rev$ $Date$ */
    /**
    * Check to see if fields is in a valid integer range.
    * Fields are not checked if they are disabled.
    * @param form The form validation is taking place on.
    */
    function validateIntRange(form) {
        var isValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();
 
        var oRange = eval('new ' + jcv_retrieveFormName(form) +  '_intRange()');        
        for (var x in oRange) {
            if (!jcv_verifyArrayElement(x, oRange[x])) {
                continue;
            }
            var field = form[oRange[x][0]];
            if (jcv_isFieldPresent(field)) {
                var value = '';
                if (field.type == 'hidden' ||
                    field.type == 'text' || field.type == 'textarea' ||
                    field.type == 'radio' ) {
                    value = field.value;
                }
                if (field.type == 'select-one') {
                    var si = field.selectedIndex;
                    if (si >= 0) {
                        value = field.options[si].value;
                    }
                }
                if (value.length > 0) {
                    var iMin = parseInt(oRange[x][2]("min"));
                    var iMax = parseInt(oRange[x][2]("max"));
                    var iValue = parseInt(value, 10);
                    if (!(iValue >= iMin && iValue <= iMax)) {
                        if (i == 0) {
                            focusField = field;
                        }
                        fields[i++] = oRange[x][1];
                        isValid = false;
                    }
                }
            }
        }
        if (fields.length > 0) {
            jcv_handleErrors(fields, focusField);
        }
        return isValid;
    }
