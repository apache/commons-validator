/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/ExceptionTest.java,v 1.1 2004/01/17 21:56:49 dgraham Exp $
 * $Revision: 1.1 $
 * $Date: 2004/01/17 21:56:49 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2004 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names, "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
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

package org.apache.commons.validator;

import java.io.IOException;

import org.xml.sax.SAXException;

/**                                                       
 * Performs Validation Test for exception handling.
 */
public class ExceptionTest extends TestCommon {

    /**
     * The key used to retrieve the set of validation 
     * rules from the xml file.
     */
    protected static String FORM_KEY = "exceptionForm";

    /**
     * The key used to retrieve the validator action.
     */
    protected static String ACTION = "raiseException";

    public ExceptionTest(String name) {
        super(name);
    }

    /**
     * Load <code>ValidatorResources</code> from 
     * validator-exception.xml.
     */
    protected void setUp() throws IOException, SAXException {
        loadResources("validator-exception.xml");
    }

    /**
     * Tests handling of checked exceptions - should become 
     * ValidatorExceptions.
     */
    public void testValidatorException() {
        // Create bean to run test on.
        ValueBean info = new ValueBean();
        info.setValue("VALIDATOR");

        // Construct validator based on the loaded resources 
        // and the form key
        Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource 
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, info);

        // Get results of the validation which can throw ValidatorException
        try {
            validator.validate();
            fail("ValidatorException should occur here!");
        } catch (ValidatorException expected) {
            assertTrue("VALIDATOR-EXCEPTION".equals(expected.getMessage()));
        }
    }

    /**
     * Tests handling of runtime exceptions.
     */
    public void testRuntimeException() throws ValidatorException {
        // Create bean to run test on.
        ValueBean info = new ValueBean();
        info.setValue("RUNTIME");

        // Construct validator based on the loaded resources 
        // and the form key
        Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource 
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, info);

        // Get results of the validation which can throw ValidatorException
        try {
            validator.validate();
            //fail("RuntimeException should occur here!");
        } catch (RuntimeException expected) {
            fail("RuntimeExceptions should be treated as validation failures in Validator 1.x.");
            // This will be true in Validator 2.0
            //assertTrue("RUNTIME-EXCEPTION".equals(expected.getMessage()));
        }
    }

    /**
     * Tests handling of checked exceptions - should become 
     * ValidatorExceptions.
     */
    public void testCheckedException() {
        // Create bean to run test on.
        ValueBean info = new ValueBean();
        info.setValue("CHECKED");

        // Construct validator based on the loaded resources 
        // and the form key
        Validator validator = new Validator(resources, FORM_KEY);
        // add the name bean to the validator as a resource 
        // for the validations to be performed on.
        validator.setParameter(Validator.BEAN_PARAM, info);

        // Get results of the validation which can throw ValidatorException
        
        // Tests Validator 1.x exception handling
        try {
            validator.validate();
        } catch (ValidatorException expected) {
            fail("Checked exceptions are not wrapped in ValidatorException in Validator 1.x.");
        } catch (Exception e) {
            assertTrue("CHECKED-EXCEPTION".equals(e.getMessage()));
        }
        
        // This will be true in Validator 2.0
//        try {
//            validator.validate();
//            fail("ValidatorException should occur here!");
//        } catch (ValidatorException expected) {
//            assertTrue("CHECKED-EXCEPTION".equals(expected.getMessage()));
//        }
    }
}
