    /*$RCSfile: validateUtilities.js,v $ $Revision: 1.1 $ $Date: 2004/03/25 04:56:11 $ */
    /**
    * Retrieves an attribute from an object.
    * This is useful if the attribute is hidden by a htmlElement
    *
    * <p>
    * @param form The form validation is taking place on.
    */
    function getAttribute(theObject,attribName) {
        var attrib = null;
        for (var attribIndex = 0; (attribIndex < theObject.attributes.length) && (attrib == null); attribIndex++) {
            if (theObject.attributes[attribIndex].name == attribName) {
                attrib = theObject.attributes[attribIndex];
            }
        }
        return attrib;
    }
        /**
    * Retrieves an attribute value of an object.
    * This is useful if the attribute is hidden by an htmlElement
    *
    * <p>
    * @param form The form validation is taking place on.
    */
    function getAttributeValue(theObject,attribName) {

        return getAttribute(theObject,attribName).value;
    }