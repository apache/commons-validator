/*
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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


package org.apache.commons.validator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.collections.FastHashMap; 


/**
 * <p>Used in the Validation framework.  Reference to the class and method to perform 
 * a validation.  Used to dynamically instantiate and run a validation method defined 
 * in the validation.xml file.<BR>
 * &nbsp;&nbsp;&nbsp; Note: The validation method is assumed to be thread safe.</p>
 *
 * <ul><li>See /WEB-INF/validation.xml for validation rules.</li></ul>
 *
 * @author David Winterfeldt
*/
public class ValidatorAction implements Serializable {
    /**
     * The name of the validation.
    */
    private String name = null;

    private String classname = null;
    private String method = null; 

    // Default for Struts
    private String methodParams = Validator.BEAN_KEY + "," + 
    				  Validator.VALIDATOR_ACTION_KEY + "," + 
    				  Validator.FIELD_KEY + "," + 
    				  Validator.ACTION_ERRORS_KEY + "," + 
    				  Validator.HTTP_SERVLET_REQUEST_KEY + "," + 
    				  Validator.SERVLET_CONTEXT_KEY; 
    private String depends = null;       
    private String msg = null;    
    private String jsFunctionName = null;
    private String javascript = null;

    private Object instance = null;
    

    private FastHashMap hDependencies = new FastHashMap();   
    private List lMethodParams = new ArrayList();   

    /**
     * Gets the name of the validator action.
    */
    public String getName() {
       return name;	
    }

    /**
     * Sets the name of the validator action.
    */
    public void setName(String name) {
       this.name = name;	
    }

    /**
     * Gets the class of the validator action.
    */
    public String getClassname() {
       return classname;	
    }

    /**
     * Sets the class of the validator action.
    */
    public void setClassname(String classname) {
       this.classname = classname;	
    }

    /**
     * Gets the name of method being called for the validator action.
    */
    public String getMethod() {
       return method;	
    }

    /**
     * Sets the name of method being called for the validator action.
    */
    public void setMethod(String method) {
       this.method = method;	
    }
    
    /**
     * Gets the method parameters for the method.
    */
    public String getMethodParams() {
       return methodParams;	
    }

    /**
     * Sets the method parameters for the method.
    */
    public void setMethodParams(String methodParams) {
       this.methodParams = methodParams;	
    }

    /**
     * Gets the method parameters for the method.
    */
    public List getMethodParamsList() {
       return Collections.unmodifiableList(lMethodParams);
    }
    
    /**
     * Gets the dependencies of the validator action.
    */
    public String getDepends() {
       return depends;	
    }

    /**
     * Sets the dependencies of the validator action.
    */
    public void setDepends(String depends) {
       this.depends = depends;	
    }

    /**
     * Gets the resource message associated with the validator action.
    */
    public String getMsg() {
       return msg;	
    }

    /**
     * Sets the resource message associated with the validator action.
    */
    public void setMsg(String msg) {
       this.msg = msg;	
    }

    /**
     * Gets the Javascript function name.  This is optional and will 
     * be used instead of validator action name for the name of the 
     * Javascript function/object.
    */
    public String getJsFunctionName() {
       return jsFunctionName;	
    }

    /**
     * Sets the Javascript function name.  This is optional and will 
     * be used instead of validator action name for the name of the 
     * Javascript function/object.
    */
    public void setJsFunctionName(String jsFunctionName) {
       this.jsFunctionName = jsFunctionName;	
    }
    
    /**
     * Gets the Javascript equivalent of the java class and method 
     * associated with this action.
    */
    public String getJavascript() {
       return javascript;	
    }

    /**
     * Sets the Javascript equivalent of the java class and method 
     * associated with this action.
    */
    public void setJavascript(String javascript) {
       this.javascript = javascript;	
    }

    /**
     * Gets an instance based on the validator action's classname.
    */
    public Object getClassnameInstance() {
       return instance;	
    }

    /**
     * Sets an instance based on the validator action's classname.
    */
    public void setClassnameInstance(Object instance) {
       this.instance = instance;	
    }

    /**
     * Creates a <code>FastHashMap</code> for the isDependency method 
     * based on depends.
    */
    public synchronized void process(Map globalConstants) {
       // Create FastHashMap for isDependency method
       if (getDepends() != null) {
       	  if (hDependencies == null) {
       	     hDependencies = new FastHashMap();
       	  }
       	     
          StringTokenizer st = new StringTokenizer(getDepends(), ",");
          String value = "";
          while (st.hasMoreTokens()) {
             String depend = st.nextToken().trim();
             
             if (depend != null && depend.length() > 0) {
                hDependencies.put(depend, value);
             }
          
          }
              
          hDependencies.setFast(true);
       }
       
       // Create List for methodParams
       if (getMethodParams() != null) {
       	  if (lMethodParams == null) {
       	     lMethodParams = new ArrayList();
       	  }
       	     
          StringTokenizer st = new StringTokenizer(getMethodParams(), ",");

          while (st.hasMoreTokens()) {
             String value = st.nextToken().trim();
             
             if (value != null && value.length() > 0) {
                lMethodParams.add(value);
             }
          }
       }
    }

    /**
     * Checks whether or not the value passed in is in the depends field.
    */
    public boolean isDependency(String key) {
       if (hDependencies != null) {
          return hDependencies.containsKey(key);	
       } else {
          return false;
       }
    }

    /**
     * Gets the dependencies as a <code>Collection</code>.
    */
    public Collection getDependencies() {
       return Collections.unmodifiableMap(hDependencies).keySet();
    }
    
    public String toString() {
       StringBuffer results = new StringBuffer();
       
       results.append("ValidatorAction: ");
       results.append(name);
       results.append("\n");
    
       return results.toString();
    }
	
}