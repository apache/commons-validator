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

import org.apache.commons.validator.util.ValidatorUtils;
                                                          
/**                                                       
 * Contains validation methods for different unit tests.
 *
 * @version $Revision$
 */
public class GenericValidatorImpl {
          
    /**
     * Throws a runtime exception if the value of the argument is "RUNTIME", 
     * an exception if the value of the argument is "CHECKED", and a 
     * ValidatorException otherwise.
     * 
     * @throws RuntimeException with "RUNTIME-EXCEPTION as message"
     * if value is "RUNTIME"
     * @throws Exception with "CHECKED-EXCEPTION" as message 
     * if value is "CHECKED"
     * @throws ValidatorException with "VALIDATOR-EXCEPTION" as message  
     * otherwise
     */
    public static boolean validateRaiseException(
        final Object bean,
        final Field field)
        throws Exception {
            
        final String value =
            ValidatorUtils.getValueAsString(bean, field.getProperty());
            
        if ("RUNTIME".equals(value)) {
            throw new RuntimeException("RUNTIME-EXCEPTION");
            
        } else if ("CHECKED".equals(value)) {
            throw new Exception("CHECKED-EXCEPTION");
            
        } else {
            throw new ValidatorException("VALIDATOR-EXCEPTION");
        }
    }
                                                          
   /**
    * Checks if the field is required.
    *
    * @return boolean If the field isn't <code>null</code> and
    * has a length greater than zero, <code>true</code> is returned.  
    * Otherwise <code>false</code>.
    */
   public static boolean validateRequired(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return !GenericValidator.isBlankOrNull(value);
   }

   /**
    * Checks if the field can be successfully converted to a <code>byte</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return    boolean        If the field can be successfully converted 
    *                           to a <code>byte</code> <code>true</code> is returned.  
    *                           Otherwise <code>false</code>.
    */
   public static boolean validateByte(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericValidator.isByte(value);
   }

   /**
    * Checks if the field can be successfully converted to a <code>short</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return    boolean        If the field can be successfully converted 
    *                           to a <code>short</code> <code>true</code> is returned.  
    *                           Otherwise <code>false</code>.
    */
   public static boolean validateShort(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericValidator.isShort(value);
   }

   /**
    * Checks if the field can be successfully converted to a <code>int</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return    boolean        If the field can be successfully converted 
    *                           to a <code>int</code> <code>true</code> is returned.  
    *                           Otherwise <code>false</code>.
    */
   public static boolean validateInt(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericValidator.isInt(value);
   }

   /**
    * Checks if field is positive assuming it is an integer
    * 
    * @param    bean       The value validation is being performed on.
    * @param    field       Description of the field to be evaluated
    * @return   boolean     If the integer field is greater than zero, returns
    *                        true, otherwise returns false.
    */
   public static boolean validatePositive(Object bean , Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
   
      return GenericTypeValidator.formatInt(value).intValue() > 0;                                                      
   }

   /**
    * Checks if the field can be successfully converted to a <code>long</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return    boolean        If the field can be successfully converted 
    *                           to a <code>long</code> <code>true</code> is returned.  
    *                           Otherwise <code>false</code>.
    */
   public static boolean validateLong(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericValidator.isLong(value);
   }

   /**
    * Checks if the field can be successfully converted to a <code>float</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return    boolean        If the field can be successfully converted 
    *                           to a <code>float</code> <code>true</code> is returned.  
    *                           Otherwise <code>false</code>.
    */
   public static boolean validateFloat(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericValidator.isFloat(value);
   }
   
   /**
    * Checks if the field can be successfully converted to a <code>double</code>.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return    boolean        If the field can be successfully converted 
    *                           to a <code>double</code> <code>true</code> is returned.  
    *                           Otherwise <code>false</code>.
    */
   public static boolean validateDouble(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericValidator.isDouble(value);
   }

   /**
    * Checks if the field is an e-mail address.
    *
    * @param bean The value validation is being performed on.
    * @param field the field to use
    * @return    boolean        If the field is an e-mail address
    *                           <code>true</code> is returned.  
    *                           Otherwise <code>false</code>.
    */
   public static boolean validateEmail(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericValidator.isEmail(value);
   }

  public final static String FIELD_TEST_NULL = "NULL";
  public final static String FIELD_TEST_NOTNULL = "NOTNULL";
  public final static String FIELD_TEST_EQUAL = "EQUAL";

    public static boolean validateRequiredIf(
        Object bean,
        Field field,
        Validator validator) {
    
        Object form = validator.getParameterValue(Validator.BEAN_PARAM);
        String value = null;
        boolean required = false;
        if (isStringOrNull(bean)) {
            value = (String) bean;
        } else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        int i = 0;
        String fieldJoin = "AND";
        if (!GenericValidator.isBlankOrNull(field.getVarValue("fieldJoin"))) {
            fieldJoin = field.getVarValue("fieldJoin");
        }
        if (fieldJoin.equalsIgnoreCase("AND")) {
            required = true;
        }
        while (!GenericValidator.isBlankOrNull(field.getVarValue("field[" + i + "]"))) {
            String dependProp = field.getVarValue("field[" + i + "]");
            String dependTest = field.getVarValue("fieldTest[" + i + "]");
            String dependTestValue = field.getVarValue("fieldValue[" + i + "]");
            String dependIndexed = field.getVarValue("fieldIndexed[" + i + "]");
            if (dependIndexed == null) {
                dependIndexed = "false";
            }
            String dependVal = null;
            boolean this_required = false;
            if (field.isIndexed() && dependIndexed.equalsIgnoreCase("true")) {
                String key = field.getKey();
                if ((key.contains("[")) && (key.contains("]"))) {
                    String ind = key.substring(0, key.indexOf(".") + 1);
                    dependProp = ind + dependProp;
                }
            }
            dependVal = ValidatorUtils.getValueAsString(form, dependProp);
            if (dependTest.equals(FIELD_TEST_NULL)) {
                if ((dependVal != null) && (dependVal.length() > 0)) {
                    this_required = false;
                } else {
                    this_required = true;
                }
            }
            if (dependTest.equals(FIELD_TEST_NOTNULL)) {
                if ((dependVal != null) && (dependVal.length() > 0)) {
                    this_required = true;
                } else {
                    this_required = false;
                }
            }
            if (dependTest.equals(FIELD_TEST_EQUAL)) {
                this_required = dependTestValue.equalsIgnoreCase(dependVal);
            }
            if (fieldJoin.equalsIgnoreCase("AND")) {
                required = required && this_required;
            } else {
                required = required || this_required;
            }
            i++;
        }
        if (required) {
            if ((value != null) && (value.length() > 0)) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }
  
  private static boolean isStringOrNull(Object o) {
    if (o == null) {
        return true; // TODO this condition is not exercised by any tests currently
    }
    return (o instanceof String);
  }
      
}                                                         
