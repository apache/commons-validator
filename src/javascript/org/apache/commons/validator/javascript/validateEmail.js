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
    /*$RCSfile: validateEmail.js,v $ $Rev$ $Date$ */
    /**
    * Check to see if fields are a valid email address.
    * Fields are not checked if they are disabled.
    * @param form The form validation is taking place on.
    */
    function validateEmail(form) {
        var bValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();

        var oEmail = eval('new ' + jcv_retrieveFormName(form) +  '_email()');

        for (var x in oEmail) {
            if (!jcv_verifyArrayElement(x, oEmail[x])) {
                continue;
            }
            var field = form[oEmail[x][0]];
            if (!jcv_isFieldPresent(field)) {
              continue;
            }
            if ((field.type == 'hidden' || 
                 field.type == 'text' ||
                 field.type == 'textarea') &&
                (field.value.length > 0)) {
                if (!jcv_checkEmail(field.value)) {
                    if (i == 0) {
                        focusField = field;
                    }
                    fields[i++] = oEmail[x][1];
                    bValid = false;
                }
            }
        }
        if (fields.length > 0) {
            jcv_handleErrors(fields, focusField);
        }
        return bValid;
    }

    /**
     * Reference: Sandeep V. Tamhankar (stamhankar@hotmail.com),
     * http://javascript.internet.com
     */
    function jcv_checkEmail(emailStr) {
        if (emailStr.length == 0) {
            return true;
        }
        // TLD checking turned off by default
        var checkTLD=0;
        var knownDomsPat=/^(com|net|org|edu|int|mil|gov|arpa|biz|aero|name|coop|info|pro|museum)$/;
        var emailPat=/^(.+)@(.+)$/;
        var specialChars="\\(\\)><@,;:\\\\\\\"\\.\\[\\]";
        var validChars="\[^\\s" + specialChars + "\]";
        var quotedUser="(\"[^\"]*\")";
        var ipDomainPat=/^\[(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\]$/;
        var atom=validChars + '+';
        var word="(" + atom + "|" + quotedUser + ")";
        var userPat=new RegExp("^" + word + "(\\." + word + ")*$");
        var domainPat=new RegExp("^" + atom + "(\\." + atom +")*$");
        var matchArray=emailStr.match(emailPat);
        if (matchArray==null) {
            return false;
        }
        var user=matchArray[1];
        var domain=matchArray[2];
        for (i=0; i<user.length; i++) {
            if (user.charCodeAt(i)>127) {
                return false;
            }
        }
        for (i=0; i<domain.length; i++) {
            if (domain.charCodeAt(i)>127) {
                return false;
            }
        }
        if (user.match(userPat)==null) {
            return false;
        }
        var IPArray=domain.match(ipDomainPat);
        if (IPArray!=null) {
            for (var i=1;i<=4;i++) {
                if (IPArray[i]>255) {
                    return false;
                }
            }
            return true;
        }
        var atomPat=new RegExp("^" + atom + "$");
        var domArr=domain.split(".");
        var len=domArr.length;
        for (i=0;i<len;i++) {
            if (domArr[i].search(atomPat)==-1) {
                return false;
            }
        }
        if (checkTLD && domArr[domArr.length-1].length!=2 && 
            domArr[domArr.length-1].search(knownDomsPat)==-1) {
            return false;
        }
        if (len<2) {
            return false;
        }
        return true;
    }
