/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/ValidatorAction.java,v 1.7 2003/05/18 21:31:06 rleland Exp $
 * $Revision: 1.7 $
 * $Date: 2003/05/18 21:31:06 $
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

package org.apache.commons.validator;

import java.io.Serializable;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Contains the information to dynamically instantiate  and run a validation 
 * method.  This is the class representation of a pluggable validator that can be defined 
 * in an xml file.
 * </p>
 * <strong>Note</strong>: The validation method is assumed to be thread safe.
 *
 * @author David Winterfeldt
 * @version $Revision: 1.7 $ $Date: 2003/05/18 21:31:06 $
 */
public class ValidatorAction implements Serializable {

	/**
	 * The name of the validation.
	 */
	private String name = null;

	/**
	 * The full class name of the class containing 
	 * the validation method associated with this action.
	 */
	private String classname = null;

	/**
	 * The full method name of the validation to be performed.  The method 
     * must be thread safe.
	 */
	private String method = null;

	// Default for Struts
	/**
	 * <p>The method signature of the validation method.  This should be a comma 
     * delimited list of the full class names of each parameter in the correct order that 
     * the method takes.</p>
	 *
	 * <p>Note: <code>java.lang.Object</code> is reserved for the 
	 * JavaBean that is being validated.  The <code>ValidatorAction</code> 
	 * and <code>Field</code> that are associated with a fields 
	 * validation will automatically be populated if they are 
	 * specified in the method signature.
     * </p>
	 */
	private String methodParams =
		Validator.BEAN_KEY
			+ ","
			+ Validator.VALIDATOR_ACTION_KEY
			+ ","
			+ Validator.FIELD_KEY;

	/**
	 * The other <code>ValidatorAction</code>s that this one depends on.  If any 
     * errors occur in an action that this one depends on, this action will not be 
     * processsed.
	 */
	private String depends = null;

	/**
	 * The default error message associated with this action.
	 */
	private String msg = null;

	/**
	 * An optional field to contain the name to be 
	 * used if JavaScript is generated.
	 */
	private String jsFunctionName = null;

    /**
     * An optional field to contain the class path to be
     * used to retrieve the JavaScript function.
     */
    private String jsFunction = null;

	/**
	 * An optional field to containing a JavaScript representation of the 
	 * java method assocated with this action.
	 */
	private String javascript = null;

	/**
	 * If the java method matching the correct signature isn't static, the instance is 
	 * stored in the action.  This assumes the method is thread safe.
	 */
	private Object instance = null;


    /**
     * Logger
    */
    private static Log log = LogFactory.getLog(ValidatorAction.class);

	/**
	 * A <code>FastHashMap</code> of the other 
	 * <code>ValidatorAction</code>s this one depends on (if any).
	 */
	private FastHashMap dependencies = new FastHashMap();

	/**
	 * A list of all the validation method's parameters.
	 */
	private List methodParameterList = new ArrayList();

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
		return Collections.unmodifiableList(methodParameterList);
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
	 * Gets the message associated with the validator action.
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * Sets the message associated with the validator action.
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * Gets the Javascript function name.  This is optional and can 
	 * be used instead of validator action name for the name of the 
	 * Javascript function/object.
	 */
	public String getJsFunctionName() {
		return jsFunctionName;
	}

	/**
	 * Sets the Javascript function name.  This is optional and can 
	 * be used instead of validator action name for the name of the 
	 * Javascript function/object.
	 */
	public void setJsFunctionName(String jsFunctionName) {
		this.jsFunctionName = jsFunctionName;
	}

    /**
     * Sets the fully qualified class path of the Javascript function.
     * <p>
     * This is optional and can be used <strong>instead</strong> of the setJavascript().
     * Attempting to call both <code>setJsFunction</code> and <code>setJavascript</code>
     * will result in an <code>IllegalStateException</code> being thrown. </p><p>
     * If <strong>neither</strong> setJsFunction or setJavascript is set then validator will attempt
     * to load the default javascript definition.   </p>
     * <pre>
     * <b>Examples</b>
     *   If in the validator.xml :
     * #1:
     *      &lt;validator name="tire"
     *            jsFunction="com.yourcompany.project.tireFuncion"&gt;
     *     Validator will attempt to load com.yourcompany.project.validateTireFunction.js from
     *     its class path.
     * #2:
     *    &lt;validator name="tire"&gt;
     *      Validator will use the name attribute to try and load
     *         org.apache.commons.validator.javascript.validateTire.js
     *      which is the default javascript definition.
     * </pre>
     */
    public void setJsFunction(String jsFunction) {
        if (javascript != null) {
            throw new IllegalStateException("Cannot call setJsFunction() after calling setJavascript()");
        }

        this.jsFunction = jsFunction;
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
        if (jsFunction != null) {
            throw new IllegalStateException("Cannot call setJavascript() after calling setJsFunction()");
        }
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
     * Initialize based on set.
     */
    void init() {
        loadFunction();
    }

    /**
      * Load the javascript function specified by the given path.  For this
      * implementation, the <code>jsFunction</code> property should contain a fully
      * qualified package and script name, separated by periods, to be loaded from
      * the class loader that created this instance.
     *
     *  @TODO if the path begins with a '/' the path will be intepreted as absolute, and remain unchanged.
      * If this fails then it will attempt to treat the path as a file path.
      * It is assumed the script ends with a '.js'.
      */
     protected synchronized void loadFunction() {

        // Have we already loaded the javascript for this action?
        if (javascript != null) {
            return;
        }

        if (log.isTraceEnabled()) {
            log.trace("  Loading function begun");
        }

         // Have we already attempted to load the javascript for this action?
         if (javascript != null) {
             return;
         }

         if (jsFunction == null) {
             jsFunction = generateJsFunction();
         }

         String name;
         // Set up to load the javascript function, if we can
         if (jsFunction.charAt(0) != '/') {
            name = jsFunction.replace('.', '/');
            name += ".js";
         } else {
             name = jsFunction.substring(1);
         }
         InputStream is = null;

         // Load the specified javascript function
         if (log.isTraceEnabled()) {
             log.trace("  Loading js function '" + name + "'");
         }

         ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
         if (classLoader == null) {
             classLoader = this.getClass().getClassLoader();
         }

         is = classLoader.getResourceAsStream(name);
         if (is == null) {
             is = this.getClass().getResourceAsStream(name);
         }

         if (is != null) {
             try {
                 int bufferSize = is.available();
                 StringBuffer function = new StringBuffer();
                 while (bufferSize > 0) {
                    byte[] buffer = new byte[bufferSize];
                    is.read(buffer,0,bufferSize);
                    String functionPart = new String(buffer);
                    function.append(functionPart);
                    bufferSize = is.available();
                 }
                 javascript = function.toString();
             } catch (IOException e) {
                 log.error("loadFunction()", e);

             } finally {
                 try {
                     is.close();
                 } catch (IOException e) {
                     log.error("loadFunction()", e);
                 }
             }
         }

         if (log.isTraceEnabled()) {
             log.trace("  Loading function completed");
         }

    }

    /**
     * Used to generate the javascript name when it is not specified.
     * @return
     */
    private String generateJsFunction() {
        StringBuffer jsName = new StringBuffer("org.apache.commons.validator.javascript");
            jsName.append(".validate");
            jsName.append(name.substring(0,1).toUpperCase());
        jsName.append(name.substring(1,name.length()));
        return jsName.toString();

    }
	/**
	 * Creates a <code>FastHashMap</code> for the isDependency method 
	 * based on depends.
	 */
	public synchronized void process(Map globalConstants) {
		// Create FastHashMap for isDependency method
		if (getDepends() != null) {
			if (dependencies == null) {
				dependencies = new FastHashMap();
			}

			StringTokenizer st = new StringTokenizer(getDepends(), ",");
			String value = "";
			while (st.hasMoreTokens()) {
				String depend = st.nextToken().trim();

				if (depend != null && depend.length() > 0) {
					dependencies.put(depend, value);
				}

			}

			dependencies.setFast(true);
		}

		// Create List for methodParams
		if (getMethodParams() != null) {
			if (methodParameterList == null) {
				methodParameterList = new ArrayList();
			}

			StringTokenizer st = new StringTokenizer(getMethodParams(), ",");

			while (st.hasMoreTokens()) {
				String value = st.nextToken().trim();

				if (value != null && value.length() > 0) {
					methodParameterList.add(value);
				}
			}
		}
	}

	/**
	 * Checks whether or not the value passed in is in the depends field.
	 */
	public boolean isDependency(String key) {
		if (dependencies != null) {
			return dependencies.containsKey(key);
		} else {
			return false;
		}
	}

	/**
	 * Gets the dependencies as a <code>Collection</code>.
	 */
	public Collection getDependencies() {
		return Collections.unmodifiableMap(dependencies).keySet();
	}

	/**
	 * Returns a string representation of the object.
	 */
	public String toString() {
		StringBuffer results = new StringBuffer();

		results.append("ValidatorAction: ");
		results.append(name);
		results.append("\n");

		return results.toString();
	}

}