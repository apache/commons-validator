
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
 
        oMaxLength = eval('new ' + retrieveFormName(form) +  '_maxlength()');        
        for (x in oMaxLength) {
            var field = form[oMaxLength[x][0]];

            if ((field.type == 'hidden' ||
                field.type == 'text' ||
                field.type == 'password' ||
                field.type == 'textarea') &&
                field.disabled == false) {

                var iMax = parseInt(oMaxLength[x][2]("maxlength"));
                if (field.value.length > iMax) {
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
