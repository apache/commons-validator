
  //$Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/javascript/org/apache/commons/validator/javascript/validateMinLength.js,v 1.6 2003/11/17 04:57:50 rleland Exp $
  //$Revision: 1.6 $
  //$Date: 2003/11/17 04:57:50 $

    function validateMinLength(form) {
        var isValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();
        oMinLength = new minlength();
        for (x in oMinLength) {
            var field = form[oMinLength[x][0]];

            if ((field.type == 'text' ||
                field.type == 'password' ||
                field.type == 'textarea') &&
                field.disabled == false) {

                var iMin = parseInt(oMinLength[x][2]("minlength"));
                if ((trim(field.value).length > 0) && (field.value.length < iMin)) {
                    if (i == 0) {
                        focusField = field;
                    }
                    fields[i++] = oMinLength[x][1];
                    isValid = false;
                }
            }
        }
        if (fields.length > 0) {
           focusField.focus();
           alert(fields.join('\n'));
        }
        return isValid;
    }