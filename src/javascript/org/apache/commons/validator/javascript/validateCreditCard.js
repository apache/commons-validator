  //$Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/javascript/org/apache/commons/validator/javascript/validateCreditCard.js,v 1.2 2003/08/15 20:20:40 rleland Exp $
  //$Revision: 1.2 $
  //$Date: 2003/08/15 20:20:40 $


    function validateCreditCard(form) {
        var bValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();
        oCreditCard = new creditCard();
        for (x in oCreditCard) {
            if ((form[oCreditCard[x][0]].type == 'text' ||
                 form[oCreditCard[x][0]].type == 'textarea') &&
                (form[oCreditCard[x][0]].value.length > 0)) {
                if (!luhnCheck(form[oCreditCard[x][0]].value)) {
                    if (i == 0) {
                        focusField = form[oCreditCard[x][0]];
                    }
                    fields[i++] = oCreditCard[x][1];
                    bValid = false;
                }
            }
        }
        if (fields.length > 0) {
            focusField.focus();
            alert(fields.join('\n'));
        }
        return bValid;
    }

    /**
     * Reference: http://www.ling.nwu.edu/~sburke/pub/luhn_lib.pl
     */
    function luhnCheck(cardNumber) {
        if (isLuhnNum(cardNumber)) {
            var no_digit = cardNumber.length;
            var oddoeven = no_digit & 1;
            var sum = 0;
            for (var count = 0; count < no_digit; count++) {
                var digit = parseInt(cardNumber.charAt(count));
                if (!((count & 1) ^ oddoeven)) {
                    digit *= 2;
                    if (digit > 9) digit -= 9;
                };
                sum += digit;
            };
            if (sum == 0) return false;
            if (sum % 10 == 0) return true;
        };
        return false;
    }

    function isLuhnNum(argvalue) {
        argvalue = argvalue.toString();
        if (argvalue.length == 0) {
            return false;
        }
        for (var n = 0; n < argvalue.length; n++) {
            if ((argvalue.substring(n, n+1) < "0") ||
                (argvalue.substring(n,n+1) > "9")) {
                return false;
            }
        }
        return true;
    }
