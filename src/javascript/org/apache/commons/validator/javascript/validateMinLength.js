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
    /*$RCSfile: validateMinLength.js,v $ $Rev$ $Date$ */
    /**
    * A field is considered valid if greater than the specified minimum.
    * Fields are not checked if they are disabled.
    *
    *  Caution: Using validateMinLength() on a password field in a 
    *  login page gives unnecessary information away to hackers. While it only slightly
    *  weakens security, we suggest using it only when modifying a password.
    * @param form The form validation is taking place on.
    */
    function validateMinLength(form) {
        var isValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();

        var oMinLength = eval('new ' + jcv_retrieveFormName(form) +  '_minlength()');

        for (var x in oMinLength) {
            if (!jcv_verifyArrayElement(x, oMinLength[x])) {
                continue;
            }
            var field = form[oMinLength[x][0]];
            if (!jcv_isFieldPresent(field)) {
              continue;
            }

            if ((field.type == 'hidden' ||
                field.type == 'text' ||
                field.type == 'password' ||
                field.type == 'textarea')) {

                /* Adjust length for carriage returns - see Bug 37962 */
                var lineEndLength = oMinLength[x][2]("lineEndLength");
                var adjustAmount = 0;
                if (lineEndLength) {
                    var rCount = 0;
                    var nCount = 0;
                    var crPos = 0;
                    while (crPos < field.value.length) {
                        var currChar = field.value.charAt(crPos);
                        if (currChar == '\r') {
                            rCount++;
                        }
                        if (currChar == '\n') {
                            nCount++;
                        }
                        crPos++;
                    }
                    var endLength = parseInt(lineEndLength);
                    adjustAmount = (nCount * endLength) - (rCount + nCount);
                }

                var iMin = parseInt(oMinLength[x][2]("minlength"));
                if ((trim(field.value).length > 0) && ((field.value.length + adjustAmount) < iMin)) {
                    if (i == 0) {
                        focusField = field;
                    }
                    fields[i++] = oMinLength[x][1];
                    isValid = false;
                }
            }
        }
        if (fields.length > 0) {
           jcv_handleErrors(fields, focusField);
        }
        return isValid;
    }
