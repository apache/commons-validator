/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/example/org/apache/commons/validator/example/ValidateExample.java,v 1.9 2003/05/24 19:40:12 dgraham Exp $
 * $Revision: 1.9 $
 * $Date: 2003/05/24 19:40:12 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.validator.example;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResults;

/**                                                       
 * <p>A simple example of setting up and using the Validator.</p> 
 *
 * @author James Turner
 * @version $Revision: 1.9 $ $Date: 2003/05/24 19:40:12 $
 *
 * This simple example shows all the steps needed to set up and use
 * the Validator.  Note that in most cases, some kind of framework
 * would be wrapped around the Validator, such as is the case with
 * the Struts Validator Framework.  However, should you wish to use
 * the Validator against raw Beans in a pure Java application, you
 * can see everything you need to know to get it working here.
 *
*/
public class ValidateExample extends Object {

    /**
     * We need a resource bundle to get our field names and errors messages from.  Note that this is not strictly
     * required to make the Validator work, but is a good coding practice.
     */
    private static ResourceBundle apps =
        ResourceBundle.getBundle(
            "org.apache.commons.validator.example.applicationResources");

    /**
     * This is the main method that will be called to initialize the Validator, create some sample beans, and
     * run the Validator against them.
     *
     */
    public static void main(String[] args) throws IOException, ValidatorException {
        InputStream in = null;
        ValidatorResources resources = null;
        
        try {
        
            // Create a new instance of a ValidatorResource, then get a stream
            // handle on the XML file with the actions in it, and initialize the
            // resources from it.  This would normally be done by a servlet
            // run during JSP initialization or some other application-startup
            // routine.
            in = ValidateExample.class.getResourceAsStream("validator-example.xml");
            resources = new ValidatorResources(in);
            
        } finally {
            // Make sure we close the input stream.
            if (in != null) {
                in.close();
            }
        }
        
        // Create a test bean to validate against.
        ValidateBean bean = new ValidateBean();
        
        // Create a validator with the ValidateBean actions for the bean
        // we're interested in.
        Validator validator = new Validator(resources, "ValidateBean");
        
        // Tell the validator which bean to validate against.
        validator.setParameter(Validator.BEAN_KEY, bean);
        
        ValidatorResults results = null;
        
        // Run the validation actions against the bean.  Since all of the properties
        // are null, we expect them all to error out except for street2, which has
        // no validations (it's an optional property)
        
        results = validator.validate();
        printResults(bean, results, resources);
        
        // Now set all the required properties, but make the age a non-integer.
        // You'll notice that age will pass the required test, but fail the int
        // test.
        bean.setLastName("Tester");
        bean.setFirstName("John");
        bean.setStreet1("1 Test Street");
        bean.setCity("Testville");
        bean.setState("TE");
        bean.setPostalCode("12345");
        bean.setAge("Too Old");
        results = validator.validate();
        printResults(bean, results, resources);
        
        // Now everything should pass.
        bean.setAge("123");
        results = validator.validate();
        printResults(bean, results, resources);
    }

    /**
     * Dumps out the Bean in question and the results of validating it.
     */
    public static void printResults(
        ValidateBean bean,
        ValidatorResults results,
        ValidatorResources resources) {
            
        boolean success = true;

        // Start by getting the form for the current locale and Bean.
        Form form = resources.getForm(Locale.getDefault(), "ValidateBean");

        System.out.println("\n\nValidating:");
        System.out.println(bean);

        // Iterate over each of the properties of the Bean which had messages.
        Iterator propertyNames = results.getPropertyNames().iterator();
        while (propertyNames.hasNext()) {
            String propertyName = (String) propertyNames.next();

            // Get the Field associated with that property in the Form
            Field field = (Field) form.getFieldMap().get(propertyName);

            // Look up the formatted name of the field from the Field arg0
            String prettyFieldName = apps.getString(field.getArg0().getKey());

            // Get the result of validating the property.
            ValidatorResult result = results.getValidatorResult(propertyName);

            // Get all the actions run against the property, and iterate over their names.
            Map actionMap = result.getActionMap();
            Iterator keys = actionMap.keySet().iterator();
            while (keys.hasNext()) {
                String actName = (String) keys.next();

                // Get the Action for that name.
                ValidatorAction action = resources.getValidatorAction(actName);

                // If the result is valid, print PASSED, otherwise print FAILED
                System.out.println(
                    propertyName
                        + "["
                        + actName
                        + "] ("
                        + (result.isValid(actName) ? "PASSED" : "FAILED")
                        + ")");

                //If the result failed, format the Action's message against the formatted field name
                if (!result.isValid(actName)) {
                    success = false;
                    String message = apps.getString(action.getMsg());
                    Object[] args = { prettyFieldName };
                    System.out.println(
                        "     Error message will be: "
                            + MessageFormat.format(message, args));

                }
            }
        }
        if (success) {
            System.out.println("FORM VALIDATION PASSED");
        } else {
            System.out.println("FORM VALIDATION FAILED");
        }

    }

}
