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
package org.apache.commons.validator;

import java.io.IOException;
import java.util.Locale;

import org.xml.sax.SAXException;

/**                                                       
 * Test that the new Var attributes and the
 * digester rule changes work.
 *
 * @version $Revision$
 */
public class VarTest extends AbstractCommonTest {

   /**
    * The key used to retrieve the set of validation
    * rules from the xml file.
    */
   protected static String FORM_KEY = "testForm";

   /**
    * The key used to retrieve the validator action.
    */
   protected static String ACTION = "byte";



   public VarTest(String name) {
       super(name);
   }

   /**
    * Load <code>ValidatorResources</code> from
    * validator-multipletest.xml.
    */
   @Override
protected void setUp() throws IOException, SAXException {
      // Load resources
      loadResources("VarTest-config.xml");
   }

   @Override
protected void tearDown() {
   }

   /**
    * With nothing provided, we should fail both because both are required.
    */
   public void testVars() {

       Form form = resources.getForm(Locale.getDefault(), FORM_KEY);

       // Get field 1
       Field field1 = form.getField("field-1");
       assertNotNull("field-1 is null.", field1);
       assertEquals("field-1 property is wrong", "field-1", field1.getProperty());

       // Get var-1-1
       Var var11 = field1.getVar("var-1-1");
       assertNotNull("var-1-1 is null.", var11);
       assertEquals("var-1-1 name is wrong", "var-1-1", var11.getName());
       assertEquals("var-1-1 value is wrong", "value-1-1", var11.getValue());
       assertEquals("var-1-1 jstype is wrong", "jstype-1-1", var11.getJsType());
       assertFalse("var-1-1 resource is true", var11.isResource());
       assertNull("var-1-1 bundle is not null.", var11.getBundle());

       // Get field 2
       Field field2 = form.getField("field-2");
       assertNotNull("field-2 is null.", field2);
       assertEquals("field-2 property is wrong", "field-2", field2.getProperty());

       // Get var-2-1
       Var var21 = field2.getVar("var-2-1");
       assertNotNull("var-2-1 is null.", var21);
       assertEquals("var-2-1 name is wrong", "var-2-1", var21.getName());
       assertEquals("var-2-1 value is wrong", "value-2-1", var21.getValue());
       assertEquals("var-2-1 jstype is wrong", "jstype-2-1", var21.getJsType());
       assertTrue("var-2-1 resource is false", var21.isResource());
       assertEquals("var-2-1 bundle is wrong", "bundle-2-1", var21.getBundle());

       // Get var-2-2
       Var var22 = field2.getVar("var-2-2");
       assertNotNull("var-2-2 is null.", var22);
       assertEquals("var-2-2 name is wrong", "var-2-2", var22.getName());
       assertEquals("var-2-2 value is wrong", "value-2-2", var22.getValue());
       assertNull("var-2-2 jstype is not null", var22.getJsType());
       assertFalse("var-2-2 resource is true", var22.isResource());
       assertEquals("var-2-2 bundle is wrong", "bundle-2-2", var22.getBundle());

   }

}                                                         