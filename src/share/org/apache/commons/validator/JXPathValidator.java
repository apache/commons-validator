/*
 *  $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/Attic/JXPathValidator.java,v 1.1 2004/06/08 15:05:38 husted Exp $
 *  $Revision: 1.1 $
 *  $Date: 2004/06/08 15:05:38 $
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

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  <p>
 *
 *  Perform jxpath validations.</p> <p>
 *
 *  The xpath expression in var "test" will be evaluated against the object
 *  scoped by the field property value, another xpath expression. Multiple
 *  matches for the field property xpath expression are allowed. </p> <p>
 *
 *  This implementation is NOT thread safe, however it can be reused multiple
 *  times by one thread. </p> .
 */
public class JXPathValidator {

    private final static Log log = LogFactory.getLog(JXPathValidator.class);
    private Object bean = null;
    private JXPathContext ctx = null;


    /**
     *  Determine whether the field is valid by evaluating the xpath expression
     *  in the "test" variable and returning the boolean result.
     *
     *@param  obj    The object to validate
     *@param  field  The field to validate
     *@return        The boolean result of the xpath evaluation
     */
    public boolean isValid(Object obj, Field field) {

        // Build the context for this bean if one doesn't exist
        if (obj != bean) {
            this.bean = obj;
            ctx = JXPathContext.newContext(this.bean);
        }

        // Iterate through the field matches and run the test
        Pointer ptr;
        JXPathContext localCtx;
        for (Iterator i = ctx.iteratePointers(field.getProperty()); i.hasNext(); ) {
            ptr = (Pointer) i.next();
            localCtx = ctx.getRelativeContext(ptr);
            return evalTest(localCtx, field.getVarValue("test"));
        }
        return false;
    }


    /**
     *  Evaluate the xpath expression for the field context
     *
     *@param  ctx   The field context
     *@param  path  The xpath to evaluate
     *@return       Description of the Return Value
     */
    private boolean evalTest(JXPathContext ctx, String path) {
        Boolean passed = (Boolean) ctx.getValue(path, Boolean.class);
        return passed.booleanValue();
    }

}

