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
    /*$RCSfile: validateMaxLength.js,v $ $Rev$ $Date$ */
    /**
    * A field is considered valid if less than the specified maximum.
    * Fields are not checked if they are disabled.
    *
    *  Caution: Using validateMaxLength() on a password field in a 
    *  login page gives unnecessary information away to hackers. While it only slightly
    *  weakens security, we suggest using it only when modifying a password.
    * @param form The form validation is taking place on.
    */
    function validateMaxLength(form) {
        var isValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();
 
        var oMaxLength = eval('new ' + jcv_retrieveFormName(form) +  '_maxlength()');        
        for (var x in oMaxLength) {
            if (!jcv_verifyArrayElement(x, oMaxLength[x])) {
                continue;
            }
            var field = form[oMaxLength[x][0]];
            if (!jcv_isFieldPresent(field)) {
              continue;
            }

            if ((field.type == 'hidden' ||
                field.type == 'text' ||
                field.type == 'password' ||
                field.type == 'textarea')) {

                /* Adjust length for carriage returns - see Bug 37962 */
                var lineEndLength = oMaxLength[x][2]("lineEndLength");
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

                var iMax = parseInt(oMaxLength[x][2]("maxlength"));
                if ((field.value.length + adjustAmount)  > iMax) {
                    if (i == 0) {
                        focusField = field;
                    }
                    fields[i++] = oMaxLength[x][1];
                    isValid = false;
                }
            }
        }
        if (fields.length > 0) {
           jcv_handleErrors(fields, focusField);
        }
        return isValid;
    }
