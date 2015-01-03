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

import java.util.*;

import org.apache.commons.validator.util.ValidatorUtils;
                                                          
/**                                                       
 * Contains validation methods for different unit tests.
 *
 * @version $Revision$
 */
public class GenericTypeValidatorImpl {

   /**
    * Checks if the field can be successfully converted to a <code>byte</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return boolean If the field can be successfully converted 
    * to a <code>byte</code> <code>true</code> is returned.  
    * Otherwise <code>false</code>.
    */
   public static Byte validateByte(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericTypeValidator.formatByte(value);
   }

   /**
    * Checks if the field can be successfully converted to a <code>byte</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return boolean If the field can be successfully converted 
    * to a <code>byte</code> <code>true</code> is returned.  
    * Otherwise <code>false</code>.
    */
   public static Byte validateByte(Object bean, Field field, Locale locale) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericTypeValidator.formatByte(value, locale);
   }

   /**
    * Checks if the field can be successfully converted to a <code>short</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return boolean If the field can be successfully converted 
    * to a <code>short</code> <code>true</code> is returned.  
    * Otherwise <code>false</code>.
    */
   public static Short validateShort(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericTypeValidator.formatShort(value);
   }

   /**
    * Checks if the field can be successfully converted to a <code>short</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return boolean If the field can be successfully converted 
    * to a <code>short</code> <code>true</code> is returned.  
    * Otherwise <code>false</code>.
    */
   public static Short validateShort(Object bean, Field field, Locale locale) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericTypeValidator.formatShort(value, locale);
   }

   /**
    * Checks if the field can be successfully converted to a <code>int</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return boolean If the field can be successfully converted 
    * to a <code>int</code> <code>true</code> is returned.  
    * Otherwise <code>false</code>.
    */
   public static Integer validateInt(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericTypeValidator.formatInt(value);
   }

   /**
    * Checks if the field can be successfully converted to a <code>int</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return boolean If the field can be successfully converted 
    * to a <code>int</code> <code>true</code> is returned.  
    * Otherwise <code>false</code>.
    */
   public static Integer validateInt(Object bean, Field field, Locale locale) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericTypeValidator.formatInt(value, locale);
   }

   /**
    * Checks if the field can be successfully converted to a <code>long</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return boolean If the field can be successfully converted 
    * to a <code>long</code> <code>true</code> is returned.  
    * Otherwise <code>false</code>.
    */
   public static Long validateLong(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericTypeValidator.formatLong(value);
   }

   /**
    * Checks if the field can be successfully converted to a <code>long</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return boolean If the field can be successfully converted 
    * to a <code>long</code> <code>true</code> is returned.  
    * Otherwise <code>false</code>.
    */
   public static Long validateLong(Object bean, Field field, Locale locale) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericTypeValidator.formatLong(value, locale);
   }

   /**
    * Checks if the field can be successfully converted to a <code>float</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return boolean If the field can be successfully converted 
    * to a <code>float</code> <code>true</code> is returned.  
    * Otherwise <code>false</code>.
    */
   public static Float validateFloat(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericTypeValidator.formatFloat(value);
   }
   
   /**
    * Checks if the field can be successfully converted to a <code>float</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return boolean If the field can be successfully converted 
    * to a <code>float</code> <code>true</code> is returned.  
    * Otherwise <code>false</code>.
    */
   public static Float validateFloat(Object bean, Field field, Locale locale) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericTypeValidator.formatFloat(value, locale);
   }
   
   /**
    * Checks if the field can be successfully converted to a <code>double</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return boolean If the field can be successfully converted 
    * to a <code>double</code> <code>true</code> is returned.  
    * Otherwise <code>false</code>.
    */
   public static Double validateDouble(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericTypeValidator.formatDouble(value);
   }
   
   /**
    * Checks if the field can be successfully converted to a <code>double</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return boolean If the field can be successfully converted 
    * to a <code>double</code> <code>true</code> is returned.  
    * Otherwise <code>false</code>.
    */
   public static Double validateDouble(Object bean, Field field, Locale locale) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericTypeValidator.formatDouble(value, locale);
   }
   
   /**
    * Checks if the field can be successfully converted to a <code>date</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return boolean If the field can be successfully converted 
    * to a <code>date</code> <code>true</code> is returned.  
    * Otherwise <code>false</code>.
    */
   public static Date validateDate(Object bean, Field field, Locale locale) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericTypeValidator.formatDate(value, locale);
   }
   
   /**
    * Checks if the field can be successfully converted to a <code>date</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return boolean If the field can be successfully converted 
    * to a <code>date</code> <code>true</code> is returned.  
    * Otherwise <code>false</code>.
    */
   public static Date validateDate(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      String datePattern = field.getVarValue("datePattern");
      String datePatternStrict = field.getVarValue("datePatternStrict");
      
      Date result = null;
      if (datePattern != null && datePattern.length() > 0) {
            result = GenericTypeValidator.formatDate(value, datePattern, false);
        } else if (datePatternStrict != null && datePatternStrict.length() > 0) {
            result = GenericTypeValidator.formatDate(value, datePatternStrict, true);
        } 

      return result;
   }
}                                                         