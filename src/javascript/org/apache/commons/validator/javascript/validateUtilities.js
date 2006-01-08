    /*$RCSfile: validateUtilities.js,v $ $Rev$ $Date$ */

  /**
  * This is a place holder for common utilities used across the javascript validation
  *
  **/

  /**
   * Retreive the name of the form
   * @param form The form validation is taking place on.
   */
  function retrieveFormName(form) {

      // Please refer to Bugs 31534, 35127, 35294, 37315 & 38159
      // for the history of the following code

      var formName;

      if (form.getAttributeNode) {
          if (form.getAttributeNode("id") && form.getAttributeNode("id").value) {
              formName = form.getAttributeNode("id").value;
          } else {
              formName = form.getAttributeNode("name").value;
          }
      } else if (form.getAttribute) {
          if (form.getAttribute("id")) {
              formName = form.getAttribute("id");
          } else {
              formName = form.attributes["name"];
          }
      } else {
          if (form.id) {
              formName = form.id;
          } else {
              formName = form.name;
          }
      }

      return formName;

  }  

  /**
   * Handle error messages.
   * @param messages Array of error messages.
   * @param focusField Field to set focus on.
   */
  function jcv_handleErrors(messages, focusField) {
      if (focusField && focusField != null) {
          var doFocus = true;
          if (focusField.disabled || focusField.type == 'hidden') {
              doFocus = false;
          }
          if (doFocus && 
              focusField.style && 
              focusField.style.visibility &&
              focusField.style.visibility == 'hidden') {
              doFocus = false;
          }
          if (doFocus) {
              focusField.focus();
          }
      }
      alert(messages.join('\n'));
  }

  /**
   * Check a value only contains valid numeric digits
   * @param argvalue The value to check.
   */
  function isAllDigits(argvalue) {
      argvalue = argvalue.toString();
      var validChars = "0123456789";
      var startFrom = 0;
      if (argvalue.substring(0, 2) == "0x") {
         validChars = "0123456789abcdefABCDEF";
         startFrom = 2;
      } else if (argvalue.charAt(0) == "0") {
         validChars = "01234567";
         startFrom = 1;
      } else if (argvalue.charAt(0) == "-") {
          startFrom = 1;
      }

      for (var n = startFrom; n < argvalue.length; n++) {
          if (validChars.indexOf(argvalue.substring(n, n+1)) == -1) return false;
      }
      return true;
  }

  /**
   * Check a value only contains valid decimal digits
   * @param argvalue The value to check.
   */
  function isDecimalDigits(argvalue) {
      argvalue = argvalue.toString();
      var validChars = "0123456789";

      var startFrom = 0;
      if (argvalue.charAt(0) == "-") {
          startFrom = 1;
      }

      for (var n = startFrom; n < argvalue.length; n++) {
          if (validChars.indexOf(argvalue.substring(n, n+1)) == -1) return false;
      }
      return true;
  }
