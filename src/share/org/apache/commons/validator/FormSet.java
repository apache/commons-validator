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
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections.FastHashMap; 


/**
 * <p>Used in the Validation framework.  It holds a set of <code>Form</code>s 
 * stored associated with a <code>Locale</code>.</p>
 *
 * <ul><li>See /WEB-INF/validation.xml for validation rules.</li></ul>
 *
 * @author David Winterfeldt
*/
public class FormSet implements Serializable {
    /**
     * Whether or not the this <code>FormSet</code> was processed 
     * for replacing variables in strings with their values.
    */
    private boolean bProcessed = false;

    /**
     * Language component of <code>Locale</code> (required).
    */
    private String language = null;
    
    /**
     * Country component of <code>Locale</code> (optional).
    */
    private String country = null;

    /**
     * Variant component of <code>Locale</code> (optional).
    */
    private String variant = null;
    
    private FastHashMap hForms = new FastHashMap();

    /**
     * Constants
    */
    private FastHashMap hConstants = new FastHashMap();

    /**
     * Whether or not the this <code>FormSet</code> was processed 
     * for replacing variables in strings with their values.
    */    
    public boolean isProcessed() {
       return bProcessed;	
    }

    /**
     * Gets the equivalent of the language component of <code>Locale</code>.
    */
    public String getLanguage() {
       return language;	
    }

    /**
     * Sets the equivalent of the language component of <code>Locale</code>.
    */
    public void setLanguage(String language) {
       this.language = language;	
    }

    /**
     * Gets the equivalent of the country component of <code>Locale</code>.
    */
    public String getCountry() {
       return country;	
    }

    /**
     * Sets the equivalent of the country component of <code>Locale</code>.
    */
    public void setCountry(String country) {
       this.country = country;	
    }

    /**
     * Gets the equivalent of the variant component of <code>Locale</code>.
    */
    public String getVariant() {
       return variant;	
    }

    /**
     * Sets the equivalent of the variant component of <code>Locale</code>.
    */
    public void setVariant(String variant) {
       this.variant = variant;	
    }

    /**
     * Add a <code>Constant</code> (locale level).
    */
    public void addConstant(Constant c) {
       if (c.getName() != null && c.getName().length() > 0 &&
           c.getValue() != null && c.getValue().length() > 0)
       hConstants.put(c.getName(), c.getValue());
       
       //System.out.println("Add Constant: " + c.getName() + "," + c.getValue());
    }

    /**
     * Add a <code>Form</code> to the <code>FormSet</code>.
    */    
    public void addForm(Form f) {
       hForms.put(f.getName(), f);
       
       //System.out.println("Add Form: " + f.getName());
    }

    /**
     * Retrieve a <code>Form</code> based on the form name.
    */
    public Form getForm(Object key) {
    	Form f = null;
    	Object o = hForms.get(key);
    	
    	if (o != null) {
    	   f = (Form)o;
    	}
    	
    	return f;
    }
    
    /**
     * A <code>Map</code> of <code>Form</code>s is returned as an 
     * unmodifiable <code>Map</code> with the key based on the form name.
    */
    public Map getForms() {
    	return Collections.unmodifiableMap(hForms);
    }
    
    /**
     * Processes all of the <code>Form</code>s, set <code>FastHashMap</code>s 
     * to 'fast' mode.
    */
    public synchronized void process(Map globalConstants) {
       for (Iterator i = hForms.values().iterator(); i.hasNext(); ) {
       	  Form f = (Form)i.next();
       	  f.process(globalConstants, hConstants);
       }
       
       hForms.setFast(true);
       hConstants.setFast(true);
       
       bProcessed = true;
    }
    
    public String toString() {
       StringBuffer results = new StringBuffer();
    
       results.append("FormSet: language=");
       results.append(language);
       results.append("  country=");
       results.append(country);
       results.append("  variant=");
       results.append(variant);
       results.append("\n");
       
       for (Iterator i = getForms().values().iterator(); i.hasNext(); ) {
          results.append("   ");
          results.append(i.next());
          results.append("\n");
       }
       
       return results.toString();
    }
	
}