/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/Field.java,v 1.11 2003/05/22 03:28:05 dgraham Exp $
 * $Revision: 1.11 $
 * $Date: 2003/05/22 03:28:05 $
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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.collections.FastHashMap;
import org.apache.commons.validator.util.ValidatorUtils;

/**
 * <p>
 * This contains the list of pluggable validators to run on a field and any message 
 * information and variables to perform the validations and generate error 
 * messages.  Instances of this class are configured with a &lt;field&gt; xml element.
 * </p>
 *
 * @author David Winterfeldt
 * @version $Revision: 1.11 $ $Date: 2003/05/22 03:28:05 $
 * @see org.apache.commons.validator.Form
 */
public class Field implements Cloneable, Serializable {

    /**
     * This is the value that will be used as a key if the <code>Arg</code> 
     * name field has no value.
     */
    public final static String ARG_DEFAULT = "org.apache.commons.validator.Field.DEFAULT";
    
    /**
     * This indicates an indexed property is being referenced.
     */
    public final static String TOKEN_INDEXED = "[]";
    
    protected final static String TOKEN_START = "${";
    protected final static String TOKEN_END = "}";
    protected final static String TOKEN_VAR = "var:";
    
    protected String property = null;
    protected String indexedProperty = null;
    protected String indexedListProperty = null;
    protected String key = null;
    protected String depends = null;
    protected int page = 0;
    protected int fieldOrder = 0;
    
    protected FastHashMap hDependencies = new FastHashMap();
    protected FastHashMap hVars = new FastHashMap();
    protected FastHashMap hMsgs = new FastHashMap();
    protected FastHashMap hArg0 = new FastHashMap();
    protected FastHashMap hArg1 = new FastHashMap();
    protected FastHashMap hArg2 = new FastHashMap();
    protected FastHashMap hArg3 = new FastHashMap();


    /**
     * Gets the page value that the Field is associated with for 
     * validation.
     */
    public int getPage() {
       return page;	
    }

    /**
     * Sets the page value that the Field is associated with for 
     * validation.
     */
    public void setPage(int page) {
       this.page = page;	
    }

    /**
     * Gets the position of the <code>Field</code> in the validation list.
     */
    public int getFieldOrder() {
       return fieldOrder;	
    }


    /**
     * Sets the position of the <code>Field</code> in the validation list.
     */
    public void setFieldOrder(int fieldOrder) {
       this.fieldOrder = fieldOrder;	
    }
    
   
    /**
     * Gets the property name of the field.
     */
    public String getProperty() {
       return property;	
    }


    /**
     * Sets the property name of the field.
     */
    public void setProperty(String property) {
       this.property = property;	
    }
    
    /**
     * Gets the indexed property name of the field.  This 
     * is the method name that can take an <code>int</code> as 
     * a parameter for indexed property value retrieval.
     */
    public String getIndexedProperty() {
       return indexedProperty;	
    }


    /**
     * Sets the indexed property name of the field.
     */
    public void setIndexedProperty(String indexedProperty) {
       this.indexedProperty = indexedProperty;	
    }

    /**
     * Gets the indexed property name of the field.  This 
     * is the method name that will return an array or a 
     * <code>Collection</code> used to retrieve the 
     * list and then loop through the list performing the specified 
     * validations.
     */
    public String getIndexedListProperty() {
       return indexedListProperty;	
    }


    /**
     * Sets the indexed property name of the field.
     */
    public void setIndexedListProperty(String indexedListProperty) {
       this.indexedListProperty = indexedListProperty;	
    }
    
    /**
     * Gets the validation rules for this field.
     */
    public String getDepends() {
       return depends;	
    }

    /**
     * Sets the validation rules for this field.
     */
    public void setDepends(String depends) {
       this.depends = depends;	
    }

    /**
     * Add a <code>Msg</code> to the <code>Field</code>.
     */
    public void addMsg(Msg msg) {
    	if (msg != null
    		&& msg.getKey() != null
    		&& msg.getKey().length() > 0
    		&& msg.getName() != null
    		&& msg.getName().length() > 0) {
    
    		hMsgs.put(msg.getName(), msg.getKey());
    	}
    }

    /**
     * Retrieve a message value.
     */
    public String getMsg(String key) {
       return (String)hMsgs.get(key);
    }

    /**
     * Add a <code>Arg</code> to the arg0 list.
     */
    public void addArg0(Arg arg) {
    	if (arg != null && arg.getKey() != null && arg.getKey().length() > 0) {
            
    		if (arg.getName() != null && arg.getName().length() > 0) {
    			hArg0.put(arg.getName(), arg);
    		} else {
    			hArg0.put(ARG_DEFAULT, arg);
    		}
            
    	}
    }

    /**
     * Gets the default arg0 <code>Arg</code> object.
     */
    public Arg getArg0() {
       return (Arg) hArg0.get(ARG_DEFAULT);	
    }

    /**
     * Gets the arg0 <code>Arg</code> object based on the key passed in.  If the key 
     * finds a <code>null</code> value then the default value will try to be retrieved.
     */
    public Arg getArg0(String key) {
       Arg arg = (Arg) hArg0.get(key);
       
       return (arg != null) ? arg : getArg0();
    }

    /**
     * Add a <code>Arg</code> to the arg1 list.
     */
    public void addArg1(Arg arg) {
       if (arg != null && arg.getKey() != null && arg.getKey().length() > 0) {
           
          if (arg.getName() != null && arg.getName().length() > 0) {
             hArg1.put(arg.getName(), arg);
          } else {
             hArg1.put(ARG_DEFAULT, arg);
          }
          
       }
    }

    /**
     * Gets the default arg1 <code>Arg</code> object.
     */
    public Arg getArg1() {
       return (Arg) hArg1.get(ARG_DEFAULT);	
    }

    /**
     * Gets the arg1 <code>Arg</code> object based on the key passed in.  If the key 
     * finds a <code>null</code> value then the default value will try to be retrieved.
     */
    public Arg getArg1(String key) {
       Arg arg = (Arg) hArg1.get(key);
       
       return (arg != null) ? arg : getArg1();
    }
    
    /**
     * Add a <code>Arg</code> to the arg2 list.
     */
    public void addArg2(Arg arg) {
       if (arg != null && arg.getKey() != null && arg.getKey().length() > 0) {
           
          if (arg.getName() != null && arg.getName().length() > 0) {
             hArg2.put(arg.getName(), arg);
          } else {
             hArg2.put(ARG_DEFAULT, arg);
          }
          
       }
    }

    /**
     * Gets the default arg2 <code>Arg</code> object.
    */
    public Arg getArg2() {
       return (Arg) hArg2.get(ARG_DEFAULT);	
    }

    /**
     * Gets the arg2 <code>Arg</code> object based on the key passed in.  If the key 
     * finds a <code>null</code> value then the default value will try to be retrieved.
    */
    public Arg getArg2(String key) {
       Arg arg = (Arg) hArg2.get(key);
       
       return (arg != null) ? arg : getArg2();
    }
    
    /**
     * Add a <code>Arg</code> to the arg3 list.
     */
    public void addArg3(Arg arg) {
       if (arg != null && arg.getKey() != null && arg.getKey().length() > 0) {
           
          if (arg.getName() != null && arg.getName().length() > 0) {
             hArg3.put(arg.getName(), arg);
          } else {
             hArg3.put(ARG_DEFAULT, arg);
          }
          
       }
    }

    /**
     * Gets the default arg3 <code>Arg</code> object.
    */
    public Arg getArg3() {
       return (Arg) hArg3.get(ARG_DEFAULT);	
    }

    /**
     * Gets the arg3 <code>Arg</code> object based on the key passed in.  If the key 
     * finds a <code>null</code> value then the default value will try to be retrieved.
     */
    public Arg getArg3(String key) {
       Arg arg = (Arg) hArg3.get(key);
       
       return (arg != null) ? arg : getArg3();
    }
        
    /**
     * Add a <code>Var</code> to the <code>Field</code>.
     */
    public void addVar(Var v) {
        if (v != null
            && v.getName() != null
            && v.getName().length() > 0
            && v.getValue() != null) {
            
            hVars.put(v.getName(), v);
        }
    }

    /**
     * Add a <code>Var</code>, based on the values passed in, to the <code>Field</code>.
     */
    public void addVarParam(String name, String value, String jsType) {
       if (name != null && name.length() > 0 && value != null) {
          hVars.put(name, new Var(name, value, jsType));
       }
    }

    /**
     * Retrieve a variable.
     */
    public Var getVar(String mainKey) {
       return ((Var) hVars.get(mainKey));
    }

    /**
     * Retrieve a variable's value.
     */
    public String getVarValue(String mainKey) {
    	String value = null;
    
    	Object o = hVars.get(mainKey);
    	if (o != null && o instanceof Var) {
    		Var v = (Var) o;
    		value = v.getValue();
    	}
    
    	return value;
    }

    /**
     * The <code>Field</code>'s variables are returned as an 
     * unmodifiable <code>Map</code>.
     */
    public Map getVars() {
    	return Collections.unmodifiableMap(hVars);
    }

    /**
     * Gets a unique key based on the property and indexedProperty fields.
     */
    public String getKey() {
       if (key == null) {
          generateKey();
       }
          
       return key;
    }

    /**
     * Sets a unique key for the field.  This can be used to change 
     * the key temporarily to have a unique key for an indexed field.
     */
    public void setKey(String key) {
       this.key = key;
    }
    
    /**
     * If there is a value specified for the indexedProperty field then 
     * <code>true</code> will be returned.  Otherwise it will be <code>false</code>.
     */
    public boolean isIndexed() {
       return ((indexedListProperty != null && indexedListProperty.length() > 0));
    }

    /**
     * Generate correct <code>key</code> value.
     */    
    public void generateKey() {
       if (isIndexed()) {
          key = indexedListProperty + TOKEN_INDEXED + "." + property;
       } else {
          key = property;
       }
    }
                
    /**
     * Replace constants with values in fields and process the depends field 
     * to create the dependency <code>Map</code>.
     */
    public void process(Map globalConstants, Map constants) {
    	hMsgs.setFast(false);
    	hArg0.setFast(true);
    	hArg1.setFast(true);
    	hArg2.setFast(true);
    	hArg3.setFast(true);
    	hVars.setFast(true);
    
    	generateKey();
    
    	// Process FormSet Constants
    	for (Iterator i = constants.keySet().iterator(); i.hasNext();) {
    		String key = (String) i.next();
    		String key2 = TOKEN_START + key + TOKEN_END;
    		String replaceValue = (String) constants.get(key);
    
    		property = ValidatorUtils.replace(property, key2, replaceValue);
    
    		processVars(key2, replaceValue);
    
    		processMessageComponents(key2, replaceValue);
    	}
    
    	// Process Global Constants
    	for (Iterator i = globalConstants.keySet().iterator(); i.hasNext();) {
    		String key = (String) i.next();
    		String key2 = TOKEN_START + key + TOKEN_END;
    		String replaceValue = (String) globalConstants.get(key);
    
    		property = ValidatorUtils.replace(property, key2, replaceValue);
    
    		processVars(key2, replaceValue);
    
    		processMessageComponents(key2, replaceValue);
    	}
    
    	// Process Var Constant Replacement
    	for (Iterator i = hVars.keySet().iterator(); i.hasNext();) {
    		String key = (String) i.next();
    		String key2 = TOKEN_START + TOKEN_VAR + key + TOKEN_END;
    		Var var = (Var) hVars.get(key);
    		String replaceValue = var.getValue();
    
    		processMessageComponents(key2, replaceValue);
    	}
    
    	if (getDepends() != null) {
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
    	hMsgs.setFast(true);
    }

    /**
     * Replace the vars value with the key/value pairs passed in.
     */
    private void processVars(String key, String replaceValue) {

    	for (Iterator i = hVars.keySet().iterator(); i.hasNext();) {
    		String varKey = (String) i.next();
    		Var var = (Var) hVars.get(varKey);
    
    		var.setValue(ValidatorUtils.replace(var.getValue(), key, replaceValue));
    	}
       
    }
    
    /**
     * Replace the args key value with the key/value pairs passed in.
     */
    public void processMessageComponents(String key, String replaceValue) {
    	String varKey = TOKEN_START + TOKEN_VAR;
    	// Process Messages
    	if (key != null && !key.startsWith(varKey)) {
    		for (Iterator i = hMsgs.keySet().iterator(); i.hasNext();) {
    			String msgKey = (String) i.next();
    			String value = (String) hMsgs.get(msgKey);
    
    			hMsgs.put(msgKey, ValidatorUtils.replace(value, key, replaceValue));
    		}
       }
       
       processArg(hArg0, key, replaceValue);
       processArg(hArg1, key, replaceValue);
       processArg(hArg2, key, replaceValue);
       processArg(hArg3, key, replaceValue);
       
    }

    /**
     * Replace the arg <code>Collection</code> key value with the key/value pairs passed in.
     */
    private void processArg(Map hArgs, String key, String replaceValue) {
        for (Iterator i = hArgs.values().iterator(); i.hasNext();) {
        	Arg arg = (Arg) i.next();
        
        	if (arg != null) {
        		arg.setKey(ValidatorUtils.replace(arg.getKey(), key, replaceValue));
        	}
        }
    }

    /**
     * Checks if the key is listed as a dependency.
     */
    public boolean isDependency(String key) {
       if (hDependencies != null) {
          return hDependencies.containsKey(key);	
       } else {
          return false;
       }
    }

    /**
     * Gets an unmodifiable <code>Set</code> of the dependencies.
    */
    public Collection getDependencies() {
       return Collections.unmodifiableMap(hDependencies).keySet();
    }

    /**
     * Creates and returns a copy of this object.
    */
    public Object clone() {
       try {
           Field field = (Field)super.clone();

           if (key != null) {
              field.setKey(new String(key));
           }

           if (property != null) {
              field.setProperty(new String(property));
           }

           if (indexedProperty != null) {
              field.setIndexedProperty(new String(indexedProperty));
           }

           if (indexedListProperty != null) {
              field.setIndexedListProperty(new String(indexedListProperty));
           }
           
           if (depends != null) {
              field.setDepends(new String(depends));
           }

           // page & fieldOrder should be taken care of by clone method
           
           field.hDependencies = ValidatorUtils.copyFastHashMap(hDependencies);
           field.hVars = ValidatorUtils.copyFastHashMap(hVars);
           field.hMsgs = ValidatorUtils.copyFastHashMap(hMsgs);
           field.hArg0 = ValidatorUtils.copyFastHashMap(hArg0);
           field.hArg1 = ValidatorUtils.copyFastHashMap(hArg1);
           field.hArg2 = ValidatorUtils.copyFastHashMap(hArg2);
           field.hArg3 = ValidatorUtils.copyFastHashMap(hArg3);
    
           return field;
       } catch (CloneNotSupportedException e) {
          throw new InternalError(e.toString());
       }
    }    

    /**
     * Returns a string representation of the object.
    */       
    public String toString() {
       StringBuffer results = new StringBuffer();
       
       results.append("\t\tkey=                 " + key    + "\n");
       results.append("\t\tproperty=            " + property    + "\n");
       results.append("\t\tindexedProperty=     " + indexedProperty    + "\n");
       results.append("\t\tindexedListProperty= " + indexedListProperty    + "\n");
       results.append("\t\tdepends=             " + depends    + "\n");
       results.append("\t\tpage=                " + page    + "\n");
       results.append("\t\tfieldOrder=          " + fieldOrder    + "\n");

       if (hVars != null) {
          results.append("\t\tVars:\n");
    	  for (Iterator i = hVars.keySet().iterator(); i.hasNext(); ) {
    	     Object key = i.next();
    	     results.append("\t\t\t");
    	     results.append(key);
    	     results.append("=");
    	     results.append(hVars.get(key));
    	     results.append("\n");
    	   }
    	}
    	
       return results.toString();
    }
    
}
