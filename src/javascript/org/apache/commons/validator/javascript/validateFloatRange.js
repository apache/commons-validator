  
  //$Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/javascript/org/apache/commons/validator/javascript/validateFloatRange.js,v 1.3 2003/10/22 07:20:57 rleland Exp $
  //$Revision: 1.3 $
  //$Date: 2003/10/22 07:20:57 $

    function validateFloatRange(form) {
        var isValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();
        oRange = new floatRange();
        for (x in oRange) {
            var field = form[oRange[x][0]];
            
            if ((field.type == 'text' ||
         field.type == 'textarea') &&
        (field.value.length > 0)) {
        
        var fMin = parseFloat(oRange[x][2]("min"));
        var fMax = parseFloat(oRange[x][2]("max"));
        var fValue = parseFloat(field.value);
        if (!(fValue >= fMin && fValue <= fMax)) {
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