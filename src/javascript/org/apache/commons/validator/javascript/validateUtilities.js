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

      // Please refer to Bugs 31534, 35127, 35294 & 37315
      // for the history of the following code

      if (form.getAttributeNode) {
          if (form.getAttributeNode("id") && form.getAttributeNode("id").value) {
              return form.getAttributeNode("id").value;
          } else {
              return form.getAttributeNode("name").value;
          }
      } else if (form.getAttribute) {
          if (form.getAttribute("id")) {
              return form.getAttribute("id");
          } else {
              form.attributes["name"];
          }
      } else {
          if (form.id) {
              return form.id;
          } else {
              return form.name;
          }
      }

  }  