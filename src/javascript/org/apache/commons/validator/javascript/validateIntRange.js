
    function validateIntRange(form) {
        var isValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();
        oRange = new intRange();
        for (x in oRange) {
            var field = form[oRange[x][0]];
            var value = '';
            if (field.type == 'text' || field.type == 'textarea' ||
                field.type == 'radio' ) {
                value = field.value;
            }
            if (field.type == 'select-one') {
                var si = field.selectedIndex;
                if (si >= 0) {
                    value = field.options[si].value;
                }
            }
            if (value.length > 0) {
                var iMin = parseInt(oRange[x][2]("min"));
                var iMax = parseInt(oRange[x][2]("max"));
                var iValue = parseInt(value);
                if (!(iValue >= iMin && iValue <= iMax)) {
                    if (i == 0) {
                        focusField = field;
                    }
                    fields[i++] = oRange[x][1];
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