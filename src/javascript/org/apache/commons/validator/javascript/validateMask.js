
  //$Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/javascript/org/apache/commons/validator/javascript/validateMask.js,v 1.4 2003/11/17 04:57:50 rleland Exp $
  //$Revision: 1.4 $
  //$Date: 2003/11/17 04:57:50 $

    function validateMask(form) {
        var isValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();
        oMasked = new mask();
        for (x in oMasked) {
            var field = form[oMasked[x][0]];

            if ((field.type == 'text' ||
                 field.type == 'textarea') &&
                 (field.value.length > 0) &&
                 field.disabled == false) {

                if (!matchPattern(field.value, oMasked[x][2]("mask"))) {
                    if (i == 0) {
                        focusField = field;
                    }
                    fields[i++] = oMasked[x][1];
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

    function matchPattern(value, mask) {
       return mask.exec(value);
    }