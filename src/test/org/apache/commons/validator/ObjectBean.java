/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/Attic/ObjectBean.java,v 1.1 2004/06/08 15:05:38 husted Exp $
 * $Revision: 1.1 $
 * $Date: 2004/06/08 15:05:38 $
 *
 * ====================================================================
 * Copyright 2000-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.validator;

/**                                                       
 * Object object for storing a value to run tests on. 
 */                                                       
public class ObjectBean {
   
   protected Object object = null;
   
   
   /**
    * Gets the object.
    */
   public Object getObject() {
      return object;	
   }

   /**
    * Sets the object.
    */
   public void setObject(Object object) {
      this.object = object;	
   }
      
}                                                         