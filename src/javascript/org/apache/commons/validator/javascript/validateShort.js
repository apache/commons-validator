  //$Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/javascript/org/apache/commons/validator/javascript/validateShort.js,v 1.2 2003/08/15 20:22:03 rleland Exp $
  //$Revision: 1.2 $
  //$Date: 2003/08/15 20:22:03 $


    function validateShort(form) {
        var bValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();
        oShort = new ShortValidations();
        for (x in oShort) {
            var field = form[oShort[x][0]];

            if (field.type == 'text' ||
                field.type == 'textarea' ||
                field.type == 'select-one' ||
                field.type == 'radio') {

                var value = '';
                // get field's value
                if (field.type == "select-one") {
                    var si = field.selectedIndex;
                    if (si >= 0) {
                        value = field.options[si].value;
                    }
                } else {
                    value = field.value;
                }

                if (value.length > 0) {
                    if (!isAllDigits(value)) {
                        bValid = false;
                        if (i == 0) {
                            focusField = field;
                        }
                        fields[i++] = oShort[x][1];

                    } else {

                        var iValue = parseInt(value);
                        if (isNaN(iValue) || !(iValue >= -32768 && iValue <= 32767)) {
                            if (i == 0) {
                                focusField = field;
                            }
                            fields[i++] = oShort[x][1];
                            bValid = false;
                        }
                   }
               }
            }
        }
        if (fields.length > 0) {
           focusField.focus();
           alert(fields.join('\n'));
        }
        return bValid;
    }