/*
 *  $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/Attic/BSFValidator.java,v 1.1 2004/06/08 14:48:35 husted Exp $
 *  $Revision: 1.1 $
 *  $Date: 2004/06/08 14:48:35 $
 *
 *  ====================================================================
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.validator;

import org.apache.bsf.BSFManager;
import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.commons.beanutils.PropertyUtils;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  <p>
 *
 *  Perform validations using script snippets.</p> <p>
 *
 *  The script snippet in var "eval" will be evaluated against the field value
 *  Any Bean Scripting Framework (BSF) supported language can be used, but
 *  BeanShell is used by default. This validator can be used with passed <code>BSFEngine</code>
 *  and <code>BSFManager</code> instances containing predefined
 *  application-specific objects in the scripting scope. matches for the field
 *  property xpath expression are allowed. </p> <p>
 *
 *  The following variables are automatically defined in the script scope: </p>
 *
 *  <ul>
 *    <li> <code>value</code> - The value of the current field</li>
 *    <li> <code>target</code> - The whole object/form being validated</li>
 *    <li> <code>field</code> - The <code>Field</code> object containg the field
 *    definition</li>
 *  </ul>
 *  <p>
 *
 *  This implementation is thread safe. </p> .
 */
public class BSFValidator {

    /**
     *  Resources key the <code>BSFEngine</code> is stored under. This will be
     *  automatically passed into a validation method with the current <code>BSFEngine</code>
     *  if it is specified in the method signature. The <code>BSFEngine</code>
     *  will be used to execute the script snippet.
     */
    public final static String BSF_ENGINE_PARAM = "org.apache.bsf.BSFEngine";

    /**
     *  Resources key the <code>BSFManager</code> is stored under. This will be
     *  automatically passed into a validation method with the current <code>BSFManager</code>
     *  if it is specified in the method signature. The <code>BSFManager</code>
     *  will be used to declare beans for the engine.
     */
    public final static String BSF_MANAGER_PARAM = "org.apache.bsf.BSFManager";

    private final static Log log = LogFactory.getLog(BSFValidator.class);


    /**
     *  Determine whether the field is valid by evaluating the xpath expression
     *  in the "test" variable and returning the boolean result.
     *
     *@param  obj    The object to validate
     *@param  field  The field to validate
     *@return        The boolean result of the xpath evaluation
     */
    public boolean isValid(Object obj, Field field) {
        BSFManager manager = new BSFManager();
        BSFEngine engine = null;
        try {
            engine = manager.loadScriptingEngine("beanshell");
        } catch (BSFException ex) {
            log.error("Unable to load beanshell bsf engine", ex);
        }
        if (engine != null) {
            return isValid(obj, field, manager, engine);
        } else {
            return false;
        }
    }


    /**
     *  Determine whether the field is valid by evaluating the xpath expression
     *  in the "test" variable and returning the boolean result.
     *
     *@param  obj         The object to validate
     *@param  field       The field to validate
     *@param  bsfManager  Description of the Parameter
     *@param  engine      Description of the Parameter
     *@return             The boolean result of the xpath evaluation
     */
    public boolean isValid(Object obj, Field field, BSFManager bsfManager, BSFEngine engine) {
        try {
            Object value = getFieldValue(field, obj);

            if (value != null) {
                bsfManager.declareBean("value", value, value.getClass());
            }
            bsfManager.declareBean("field", field, Field.class);
            bsfManager.declareBean("target", obj, obj.getClass());
            Object ret = engine.eval("field:" + field.getProperty(), 0, 0, field.getVarValue("eval"));
            if (ret != null) {
                if (ret instanceof Boolean) {
                    log.debug("found boolean:" + ret);
                    return ((Boolean) ret).booleanValue();
                } else {
                    log.debug("unknown object returned");
                    return true;
                }
            } else {
                log.debug("no object returned");
                return false;
            }
        } catch (BSFException ex) {
            log.debug(ex, ex);
            return false;
        }
    }


    /**
     *  Gets the field value from the source object. Override to support other
     *  field location strategies such as JXPath expressions.
     *
     *@param  field  The field definition to validate
     *@param  obj    The object to validate
     *@return        The value of the field
     */
    protected Object getFieldValue(Field field, Object obj) {
        Object value = null;
        try {
            value = PropertyUtils.getProperty(obj, field.getProperty());
        } catch (IllegalAccessException e) {
            log.warn(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.warn(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.warn(e.getMessage(), e);
        }
        return value;
    }
}

