
    /*$RCSfile: validateMask.js,v $ $Rev$ $Date$ */
    /**
    * Check to see if fields are a valid using a regular expression.
    * Fields are not checked if they are disabled.
    * <p>
    * @param form The form validation is taking place on.
    */
    function validateMask(form) {
        var isValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();
        var formName = form.getAttributeNode("name"); 

        oMasked = eval('new ' + formName.value + '_mask()');      
        for (x in oMasked) {
            var field = form[oMasked[x][0]];

            if ((field.type == 'hidden' ||
                field.type == 'text' ||
                 field.type == 'textarea' ||
				 field.type == 'file') &&
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