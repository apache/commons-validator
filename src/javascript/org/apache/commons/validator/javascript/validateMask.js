
  //$Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/javascript/org/apache/commons/validator/javascript/validateMask.js,v 1.3 2003/10/22 07:20:57 rleland Exp $
  //$Revision: 1.3 $
  //$Date: 2003/10/22 07:20:57 $

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
                 (field.value.length > 0)) {

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