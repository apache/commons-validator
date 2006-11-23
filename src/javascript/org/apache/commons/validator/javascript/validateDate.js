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
   /*$RCSfile: validateDate.js,v $ $Rev$ $Date$ */
    /**
    * Check to see if fields are a valid date.
    * Fields are not checked if they are disabled.
    * @param form The form validation is taking place on.
    */
    function validateDate(form) {
       var bValid = true;
       var focusField = null;
       var i = 0;
       var fields = new Array();
 
       var oDate = eval('new ' + jcv_retrieveFormName(form) +  '_DateValidations()');

       for (var x in oDate) {
            if (!jcv_verifyArrayElement(x, oDate[x])) {
                continue;
            }
           var field = form[oDate[x][0]];
           if (!jcv_isFieldPresent(field)) {
             continue;
           }
           var value = field.value;
           var isStrict = true;
           var datePattern = oDate[x][2]("datePatternStrict");
           // try loose pattern
           if (datePattern == null) {
               datePattern = oDate[x][2]("datePattern");
               isStrict = false;
           }    
           if ((field.type == 'hidden' ||
                field.type == 'text' ||
                field.type == 'textarea') &&
               (value.length > 0) && (datePattern.length > 0)) {
                 var MONTH = "MM";
                 var DAY = "dd";
                 var YEAR = "yyyy";
                 var orderMonth = datePattern.indexOf(MONTH);
                 var orderDay = datePattern.indexOf(DAY);
                 var orderYear = datePattern.indexOf(YEAR);
                 if ((orderDay < orderYear && orderDay > orderMonth)) {
                     var iDelim1 = orderMonth + MONTH.length;
                     var iDelim2 = orderDay + DAY.length;
                     var delim1 = datePattern.substring(iDelim1, iDelim1 + 1);
                     var delim2 = datePattern.substring(iDelim2, iDelim2 + 1);
                     if (iDelim1 == orderDay && iDelim2 == orderYear) {
                        dateRegexp = isStrict 
                             ? new RegExp("^(\\d{2})(\\d{2})(\\d{4})$") 
                             : new RegExp("^(\\d{1,2})(\\d{1,2})(\\d{4})$");
                     } else if (iDelim1 == orderDay) {
                        dateRegexp = isStrict 
                             ? new RegExp("^(\\d{2})(\\d{2})[" + delim2 + "](\\d{4})$")
                             : new RegExp("^(\\d{1,2})(\\d{1,2})[" + delim2 + "](\\d{4})$");
                     } else if (iDelim2 == orderYear) {
                        dateRegexp = isStrict
                             ? new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})(\\d{4})$")
                             : new RegExp("^(\\d{1,2})[" + delim1 + "](\\d{1,2})(\\d{4})$");
                     } else {
                        dateRegexp = isStrict
                             ? new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})[" + delim2 + "](\\d{4})$")
                             : new RegExp("^(\\d{1,2})[" + delim1 + "](\\d{1,2})[" + delim2 + "](\\d{4})$");
                     }
                     var matched = dateRegexp.exec(value);
                     if(matched != null) {
                        if (!jcv_isValidDate(matched[2], matched[1], matched[3])) {
                           if (i == 0) {
                               focusField = field;
                           }
                           fields[i++] = oDate[x][1];
                           bValid =  false;
                        }
                     } else {
                        if (i == 0) {
                            focusField = field;
                        }
                        fields[i++] = oDate[x][1];
                        bValid =  false;
                     }
                 } else if ((orderMonth < orderYear && orderMonth > orderDay)) {
                     var iDelim1 = orderDay + DAY.length;
                     var iDelim2 = orderMonth + MONTH.length;
                     var delim1 = datePattern.substring(iDelim1, iDelim1 + 1);
                     var delim2 = datePattern.substring(iDelim2, iDelim2 + 1);
                     if (iDelim1 == orderMonth && iDelim2 == orderYear) {
                         dateRegexp = isStrict 
                            ? new RegExp("^(\\d{2})(\\d{2})(\\d{4})$")
                            : new RegExp("^(\\d{1,2})(\\d{1,2})(\\d{4})$");
                     } else if (iDelim1 == orderMonth) {
                         dateRegexp = isStrict
                            ? new RegExp("^(\\d{2})(\\d{2})[" + delim2 + "](\\d{4})$")
                            : new RegExp("^(\\d{1,2})(\\d{1,2})[" + delim2 + "](\\d{4})$");
                     } else if (iDelim2 == orderYear) {
                         dateRegexp = isStrict
                            ? new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})(\\d{4})$")
                            : new RegExp("^(\\d{1,2})[" + delim1 + "](\\d{1,2})(\\d{4})$");
                     } else {
                         dateRegexp = isStrict
                            ? new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})[" + delim2 + "](\\d{4})$")
                            : new RegExp("^(\\d{1,2})[" + delim1 + "](\\d{1,2})[" + delim2 + "](\\d{4})$");
                     }
                     var matched = dateRegexp.exec(value);
                     if(matched != null) {
                         if (!jcv_isValidDate(matched[1], matched[2], matched[3])) {
                             if (i == 0) {
                                  focusField = field;
                             }
                             fields[i++] = oDate[x][1];
                             bValid =  false;
                          }
                     } else {
                         if (i == 0) {
                             focusField = field;
                         }
                         fields[i++] = oDate[x][1];
                         bValid =  false;
                     }
                 } else if ((orderMonth > orderYear && orderMonth < orderDay)) {
                     var iDelim1 = orderYear + YEAR.length;
                     var iDelim2 = orderMonth + MONTH.length;
                     var delim1 = datePattern.substring(iDelim1, iDelim1 + 1);
                     var delim2 = datePattern.substring(iDelim2, iDelim2 + 1);
                     if (iDelim1 == orderMonth && iDelim2 == orderDay) {
                         dateRegexp = isStrict
                            ? new RegExp("^(\\d{4})(\\d{2})(\\d{2})$")
                            : new RegExp("^(\\d{4})(\\d{1,2})(\\d{1,2})$");
                     } else if (iDelim1 == orderMonth) {
                         dateRegexp = isStrict
                            ? new RegExp("^(\\d{4})(\\d{2})[" + delim2 + "](\\d{2})$")
                            : new RegExp("^(\\d{4})(\\d{1,2})[" + delim2 + "](\\d{1,2})$");
                     } else if (iDelim2 == orderDay) {
                         dateRegexp = isStrict
                            ? new RegExp("^(\\d{4})[" + delim1 + "](\\d{2})(\\d{2})$")
                            : new RegExp("^(\\d{4})[" + delim1 + "](\\d{1,2})(\\d{1,2})$");
                     } else {
                         dateRegexp = isStrict
                            ? new RegExp("^(\\d{4})[" + delim1 + "](\\d{2})[" + delim2 + "](\\d{2})$")
                            : new RegExp("^(\\d{4})[" + delim1 + "](\\d{1,2})[" + delim2 + "](\\d{1,2})$");
                     }
                     var matched = dateRegexp.exec(value);
                     if(matched != null) {
                         if (!jcv_isValidDate(matched[3], matched[2], matched[1])) {
                             if (i == 0) {
                                 focusField = field;
                             }
                             fields[i++] = oDate[x][1];
                             bValid =  false;
                         }
                     } else {
                          if (i == 0) {
                              focusField = field;
                          }
                          fields[i++] = oDate[x][1];
                          bValid =  false;
                     }
                 } else {
                     if (i == 0) {
                         focusField = field;
                     }
                     fields[i++] = oDate[x][1];
                     bValid =  false;
                 }
          }
       }
       if (fields.length > 0) {
          jcv_handleErrors(fields, focusField);
       }
       return bValid;
    }
    
    function jcv_isValidDate(day, month, year) {
	    if (month < 1 || month > 12) {
            return false;
        }
        if (day < 1 || day > 31) {
            return false;
        }
        if ((month == 4 || month == 6 || month == 9 || month == 11) &&
            (day == 31)) {
            return false;
        }
        if (month == 2) {
            var leap = (year % 4 == 0 &&
               (year % 100 != 0 || year % 400 == 0));
            if (day>29 || (day == 29 && !leap)) {
                return false;
            }
        }
        return true;
    }
