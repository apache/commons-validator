  //$Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/javascript/org/apache/commons/validator/javascript/validateByte.js,v 1.2 2003/08/15 20:20:40 rleland Exp $
  //$Revision: 1.2 $
  //$Date: 2003/08/15 20:20:40 $


    function validateByte(form) {
        var bValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();
        oByte = new ByteValidations();
        for (x in oByte) {
            var field = form[oByte[x][0]];

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
                        fields[i++] = oByte[x][1];

                    } else {

                        var iValue = parseInt(value);
                        if (isNaN(iValue) || !(iValue >= -128 && iValue <= 127)) {
                            if (i == 0) {
                                focusField = field;
                            }
                            fields[i++] = oByte[x][1];
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