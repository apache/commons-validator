  //$Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/javascript/org/apache/commons/validator/javascript/validateMaxLength.js,v 1.3 2003/10/07 03:00:15 rleland Exp $
  //$Revision: 1.3 $
  //$Date: 2003/10/07 03:00:15 $


    function validateMaxLength(form) {
        var isValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();
        oMaxLength = new maxlength();
        for (x in oMaxLength) {
            var field = form[oMaxLength[x][0]];

            if (field.type == 'text' ||
                field.type == 'password' ||
                field.type == 'textarea') {

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
           focusField.focus();
           alert(fields.join('\n'));
        }
        return isValid;
    }