  //$Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/javascript/org/apache/commons/validator/javascript/validateMaxLength.js,v 1.2 2003/08/15 20:22:03 rleland Exp $
  //$Revision: 1.2 $
  //$Date: 2003/08/15 20:22:03 $


    function validateMaxLength(form) {
        var isValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();
        oMaxLength = new maxlength();
        for (x in oMaxLength) {
            var field = form[oMaxLength[x][0]];

            if (field.type == 'text' ||
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