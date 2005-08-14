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
      // if id is available, then use it (for xhtml)
      // otherwise, use the value of the name attribute
      var formName = form.id ? form.id :
          form.getAttributeNode("name").value;
      return formName;
  }  