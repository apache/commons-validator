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
    /*$RCSfile: validateByte.js,v $ $Rev$ $Date$ */
    /**
    * Check to see if fields are a valid byte.
    * Fields are not checked if they are disabled.
    * @param form The form validation is taking place on.
    */
    function validateByte(form) {
        var bValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();
        
        var oByte = eval('new ' + jcv_retrieveFormName(form) + '_ByteValidations()');

        for (var x in oByte) {
            if (!jcv_verifyArrayElement(x, oByte[x])) {
                continue;
            }
            var field = form[oByte[x][0]];
            if (!jcv_isFieldPresent(field)) {
              continue;
            }

            if ((field.type == 'hidden' ||
                field.type == 'text' ||
                field.type == 'textarea' ||
                field.type == 'select-one' ||
                field.type == 'radio')) {

                var value = '';
                // get field's value
                if (field.type == "select-one") {
                    var si = field.selectedIndex;
                    if (si >= 0) {
                        value = field.options[si].value;
                    }
                } else {
                    value = field.value;
                }

                if (value.length > 0) {
                    if (!jcv_isDecimalDigits(value)) {
                        bValid = false;
                        if (i == 0) {
                            focusField = field;
                        }
                        fields[i++] = oByte[x][1];

                    } else {

                        var iValue = parseInt(value, 10);
                        if (isNaN(iValue) || !(iValue >= -128 && iValue <= 127)) {
                            if (i == 0) {
                                focusField = field;
                            }
                            fields[i++] = oByte[x][1];
                            bValid = false;
                        }
                    }
                }

            }
        }
        if (fields.length > 0) {
           jcv_handleErrors(fields, focusField);
        }
        return bValid;
    }
