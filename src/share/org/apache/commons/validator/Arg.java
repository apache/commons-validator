/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/Arg.java,v 1.4 2002/03/30 04:33:17 dwinterfeldt Exp $
 * $Revision: 1.4 $
 * $Date: 2002/03/30 04:33:17 $
 *
 * ====================================================================
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


/**
 * <p>An default argument or an argument for a 
 * specific validator definition (ex: required) 
 * can be stored to pass into a message as
 * as parameters.  This can be used in a 
 * pluggable validator for constructing locale 
 * sensitive messages by using <code>java.text.MessageFormat</code> 
 * or an equivalent class.  The resource field can be 
 * used to determine if the value stored in the argument 
 * is a value to be retrieved from a locale sensitive 
 * message retrieval system like <code>java.util.PropertyResourceBundle</code>.
 * The resource field defaults to 'true'.</p>
 *
 * @author David Winterfeldt
 * @version $Revision: 1.4 $ $Date: 2002/03/30 04:33:17 $
*/
public class Arg implements Cloneable, Serializable {

    /**
     * The name dependency that this argument goes with (optional).
    */
    protected String name = null;

    /**
     * The key or value of the argument.
    */
    protected String key = null;

    /**
     * Whether or not the key is a message resource (optional).  Defaults to true.  
     * If it is 'true', the value will try to be resolved as a message resource.
    */
    protected boolean resource = true;


    /**
     * Gets the name of the dependency.
    */
    public String getName() {
       return name;	
    }

    /**
     * Sets the name of the dependency.
    */
    public void setName(String name) {
       this.name = name;	
    }
 
     /**
     * Gets the key/value.
    */
    public String getKey() {
       return key;	
    }

    /**
     * Sets the key/value.
    */
    public void setKey(String key) {
       this.key = key;	
    }
    
    /**
     * Gets whether or not the key is a resource.
    */
    public boolean getResource() {
       return resource;	
    }

    /**
     * Sets whether or not the key is a resource.
    */
    public void setResource(boolean resource) {
       this.resource = resource;	
    }

    /**
     * Creates and returns a copy of this object.
    */
    public Object clone() {
       try {
           Arg arg = (Arg)super.clone();

           if (name != null) {
              arg.setName(new String(name));
           }
              
           if (key != null) {
              arg.setKey(new String(key));
           }
           
           arg.setResource(resource);

           return arg;
       } catch (CloneNotSupportedException e) {
          throw new InternalError(e.toString());
       }
    }  
    
    /**
     * Returns a string representation of the object.
    */
    public String toString() {
       StringBuffer results = new StringBuffer();
       
       results.append("Arg: name=");
       results.append(name);
       results.append("  key=");
       results.append(key);
       results.append("  resource=");
       results.append(resource);
       results.append("\n");
    			 
       return results.toString();
    }
	
}