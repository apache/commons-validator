
    function validateRequired(form) {
        var isValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();
        oRequired = new required();
        for (x in oRequired) {
            var field = form[oRequired[x][0]];

            if (field.type == 'text' ||
                field.type == 'textarea' ||
                field.type == 'file' ||
                field.type == 'select-one' ||
                field.type == 'radio' ||
                field.type == 'password') {

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

                if (value == '') {

                    if (i == 0) {
                        focusField = field;
                    }
                    fields[i++] = oRequired[x][1];
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