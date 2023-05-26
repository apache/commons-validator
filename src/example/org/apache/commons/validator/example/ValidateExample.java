/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.validator.example;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResults;
import org.xml.sax.SAXException;

/**                                                       
 * <p>A simple example of setting up and using the Validator.</p> 
 *
 * This simple example shows all the steps needed to set up and use
 * the Validator.  Note that in most cases, some kind of framework
 * would be wrapped around the Validator, such as is the case with
 * the Struts Validator Framework.  However, should you wish to use
 * the Validator against raw Beans in a pure Java application, you
 * can see everything you need to know to get it working here.
 */
public class ValidateExample {

    /**
     * We need a resource bundle to get our field names and errors messages 
     * from.  Note that this is not strictly required to make the Validator 
     * work, but is a good coding practice.
     */
    private static ResourceBundle apps =
        ResourceBundle.getBundle(
            "org.apache.commons.validator.example.applicationResources");

    /**
     * This is the main method that will be called to initialize the Validator, create some sample beans, and
     * run the Validator against them.
     */
    public static void main(String[] args)
        throws ValidatorException, IOException, SAXException {
            
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
        validator.setParameter(Validator.BEAN_PARAM, bean);
        
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
        
        // Now only report failed fields
        validator.setOnlyReturnErrors(true);
        results = validator.validate();
        printResults(bean, results, resources);
        
        // Now everything should pass.
        validator.setOnlyReturnErrors(false);
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
        Iterator<String> propertyNames = results.getPropertyNames().iterator();
        while (propertyNames.hasNext()) {
            String propertyName = propertyNames.next();

            // Get the Field associated with that property in the Form
            Field field = form.getField(propertyName);

            // Look up the formatted name of the field from the Field arg0
            String prettyFieldName = apps.getString(field.getArg(0).getKey());

            // Get the result of validating the property.
            ValidatorResult result = results.getValidatorResult(propertyName);

            // Get all the actions run against the property, and iterate over their names.
            Iterator<String> keys = result.getActions();
            while (keys.hasNext()) {
                String actName = keys.next();

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
