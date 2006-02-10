
    /*$RCSfile: validateMaxLength.js,v $ $Rev$ $Date$ */
    /**
    * A field is considered valid if less than the specified maximum.
    * Fields are not checked if they are disabled.
    * <p>
    * <strong>Caution:</strong> Using <code>validateMaxLength</code> on a password field in a 
    *  login page gives unnecessary information away to hackers. While it only slightly
    *  weakens security, we suggest using it only when modifying a password.</p>
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
