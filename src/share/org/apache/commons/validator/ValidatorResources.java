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
import java.util.Locale;
import java.util.Map;
import org.apache.commons.collections.FastHashMap;    


/**
 * <p>General purpose class for storing <code>FormSet</code> objects based 
 * on their associated <code>Locale</code>.</p>
 *
 * <p><strong>IMPLEMENTATION NOTE</strong> - Classes that extend this class
 * must be Serializable so that instances may be used in distributable
 * application server environments.</p>
 *
 * @author David Winterfeldt
*/
public class ValidatorResources implements Serializable {
    /**
     * Logging interface.
    */
    protected ValidatorLog logger = new DefaultValidatorLog();
    
    /**
     * <code>FastHashMap</code> of <code>FormSet</code>s stored under 
     * a <code>Locale</code> key.
    */
    protected FastHashMap hFormSets = new FastHashMap();
    
    /**
     * <code>FastHashMap</code> of global constant values with 
     * the name of the constant as the key.
    */
    protected FastHashMap hConstants = new FastHashMap();

    /**
     * <code>FastHashMap</code> of <code>ValidatorAction</code>s with 
     * the name of the <code>ValidatorAction</code> as the key.
    */
    protected FastHashMap hActions = new FastHashMap();

    /**
     * The default locale on our server.
    */
    protected static Locale defaultLocale = Locale.getDefault();

    /**
     * Gets the logger.
    */
    public ValidatorLog getLogger() {
       return logger;	
    }

    /**
     * Sets the logger.
    */
    public void setLogger(ValidatorLog logger) {
       this.logger = logger;	
    }
    
    /**
     * Add a <code>FormSet</code> to this <code>ValidatorResources</code>
     * object.  It will be associated with the <code>Locale</code> of the 
     * <code>FormSet</code>.
    */
    public void put(FormSet fs) {
    	if (fs != null) {
    	   hFormSets.put(buildKey(fs), fs);

           //if (!fs.isProcessed())
           //   fs.process(hConstants);

	   logger.info(fs.toString());	
    	}
    }

    /**
     * Add a global constant to the resource.
    */
    public void addConstant(Constant c) {
       if (c != null &&
           c.getName() != null && c.getName().length() > 0 &&
           c.getValue() != null && c.getValue().length() > 0)
          hConstants.put(c.getName(), c.getValue());
       
       logger.info("Add Global Constant: " + c.getName() + "," + c.getValue());
    }

    /**
     * <p>Add a <code>ValidatorAction</code> to the resource.  It also creates an instance 
     * of the class based on the <code>ValidatorAction</code>s classname and retrieves 
     * the <code>Method</code> instance and sets them in the <code>ValidatorAction</code>.</p>
    */
    public void addValidatorAction(ValidatorAction va) {
       if (va != null && 
           va.getName() != null && va.getName().length() > 0 &&
           va.getClassname() != null && va.getClassname().length() > 0 &&
           va.getMethod() != null && va.getMethod().length() > 0) {

	  va.process(hConstants);

          hActions.put(va.getName(), va);
       
          logger.info("Add ValidatorAction: " + va.getName() + "," + va.getClassname());   
       }
    }
    
    /**
     * Get a <code>ValidatorAction</code> based on it's name.
    */
    public ValidatorAction getValidatorAction(String key) {
       return (ValidatorAction)hActions.get(key);
    }

    /**
     * Get an unmodifiable <code>Map</code> of the <code>ValidatorAction</code>s.
    */
    public Map getValidatorActions() {
       return Collections.unmodifiableMap(hActions);
    }

    /**
     * Builds a key to store the <code>FormSet</code> under based on it's language, country, 
     * and variant values.
    */
    protected String buildKey(FormSet fs) {
       String key = ((fs.getLanguage() != null && fs.getLanguage().length() > 0) ? fs.getLanguage() : "") + 
                    ((fs.getCountry() != null && fs.getCountry().length() > 0) ? "_" + fs.getCountry() : "") + 
                    ((fs.getVariant() != null && fs.getVariant().length() > 0) ? "_" + fs.getVariant() : "");
       
       if (key.length() == 0)        
          key = defaultLocale.toString();
       
       return key;
    }

    /**
     * <p>Gets a <code>Form</code> based on the name of the form and the <code>Locale</code> that 
     * most closely matches the <code>Locale</code> passed in.  The order of <code>Locale</code> 
     * matching is:</p>
     * <ol>
     *    <li>language + country + variant</li>
     *    <li>language + country</li>
     *    <li>language</li>
     *    <li>default locale</li>
     * </ol>
    */
    public Form get(Locale locale, Object formKey) {
       return get(locale.getLanguage(), locale.getCountry(), locale.getVariant(), formKey);
    }

    /**
     * <p>Gets a <code>Form</code> based on the name of the form and the <code>Locale</code> that 
     * most closely matches the <code>Locale</code> passed in.  The order of <code>Locale</code> 
     * matching is:</p>
     * <ol>
     *    <li>language + country + variant</li>
     *    <li>language + country</li>
     *    <li>language</li>
     *    <li>default locale</li>
     * </ol>
    */
    public Form get(String language, String country, String variant, Object formKey) {
       FormSet fs = null;
       Form f = null;
       String key = null;
       Object o = null;

       key = ((language != null && language.length() > 0) ? language : "") + 
             ((country != null && country.length() > 0) ? "_" + country : "") + 
             ((variant != null && variant.length() > 0) ? "_" + variant : "");
       
       //System.out.println("ValidatorResources::get #1 - locale=" + locale.toString() + "  key=" + key);
       
       o = hFormSets.get(key);
       if (o != null) {
          fs = (FormSet)o;
          if (fs != null)
             f = fs.getForm(formKey);
       }

       if (f == null) {
          key = ((language != null && language.length() > 0) ? language : "") + 
                ((country != null && country.length() > 0) ? "_" + country : "");
          
          //System.out.println("ValidatorResources::get #2 - locale=" + locale.toString() + "  key=" + key);
          
          o = hFormSets.get(key);
          if (o != null) {
             fs = (FormSet)o;
             if (fs != null)
                f = fs.getForm(formKey);
          }

       }

       if (f == null) {
          key = ((language != null && language.length() > 0) ? language : "");
          
          //System.out.println("ValidatorResources::get #3 - locale=" + locale.toString() + "  key=" + key);
          
          o = hFormSets.get(key);
          if (o != null) {
             fs = (FormSet)o;
             if (fs != null)
                f = fs.getForm(formKey);
	  }
       }
       
       if (f == null) {
          key = Locale.getDefault().toString();
          
          //System.out.println("ValidatorResources::get #4 - locale=" + locale.toString() + "  key=" + key);
          
          o = hFormSets.get(key);
          if (o != null) {
             fs = (FormSet)o;
             if (fs != null)
                f = fs.getForm(formKey);
	  }
       }
       
       
       return f;	
    }

    /**
     * Gets the debugging detail level for this resource.
    */
    public int getDebug() {
       return logger.getDebug();
    }
    
    /**
     * Sets the debugging detail level for this resource.
    */
    public void setDebug(int debug) {
       logger.setDebug(debug);	
    }
    
    // Commented out for now because it should really try all the different locale combinations 
    // like the get method does.
    //public boolean containsKey(Locale locale, Object key) {
    //   FormSet fs = null;
    //   Object o = hFormSets.get(locale.toString());
    //   if (o != null)
    //      fs = (FormSet)o;
    //      
    //   if (fs != null && fs.getForm(key) != null)
    //      return true;
    //   
    //   return false;	
    //}

    /**
     * <p>Process the <code>ValidatorResources</code> object.  </p>
     *
     * <p>Currently sets the <code>FastHashMap</code>s to the 'fast' mode.</p>
    */
    public void process() {
       hFormSets.setFast(true);	
       hConstants.setFast(true);
       hActions.setFast(true);

       processForms(); 
    }

    /**
     * <p>Process the <code>Form</code> objects.  This clones the <code>Field</code>s 
     * that don't exist in a <code>FormSet</code> compared to the default 
     * <code>FormSet</code>.</p>
    */
    public void processForms() {
       //hFormSets.put(buildKey(fs), fs);
       String defaultKey = defaultLocale.toString();
       
       // Loop through FormSets
       for (Iterator i = hFormSets.keySet().iterator(); i.hasNext(); ) {
          String key = (String)i.next();
          FormSet fs = (FormSet)hFormSets.get(key);

          // Skip default FormSet
          if (key.equals(defaultKey))
             continue;
          
          // Loop through Forms and copy/clone fields from default locale
          for (Iterator x = fs.getForms().keySet().iterator(); x.hasNext(); ) {
             String formKey = (String)x.next();
             Form form = (Form)fs.getForms().get(formKey);
             // Create a new Form object so the order from the default is 
             // maintained (very noticable in the JavaScript).
             Form newForm = new Form();
             newForm.setName(form.getName());

             // Loop through the default locale form's fields
             // If they don't exist in the current locale's form, then clone them.
             Form defaultForm = get(defaultLocale, formKey);

             for (Iterator defaultFields = defaultForm.getFields().iterator(); defaultFields.hasNext(); ) {
                Field defaultField = (Field)defaultFields.next();
                String fieldKey = defaultField.getKey();

                if (form.getFieldMap().containsKey(fieldKey)) {
                   newForm.addField((Field)form.getFieldMap().get(fieldKey));
                } else {
                   Field field = getClosestLocaleField(fs, formKey, fieldKey);
                   newForm.addField((Field)field.clone());   
                }
             }
             
             fs.addForm(newForm);
          }
       }

       // Process Fully Constructed FormSets
       for (Iterator i = hFormSets.values().iterator(); i.hasNext(); ) {
       	  FormSet fs = (FormSet)i.next();
 
          if (!fs.isProcessed())
             fs.process(hConstants);
       }
 
    }

    protected Field getClosestLocaleField(FormSet fs, String formKey, String fieldKey) {
       Field field = null;
       String language = fs.getLanguage();
       String country = fs.getCountry();
       String variant = fs.getVariant();

       if (!GenericValidator.isBlankOrNull(language) && 
           !GenericValidator.isBlankOrNull(country) && 
           !GenericValidator.isBlankOrNull(variant)) {
          Form form = get(language, country, null, formKey);

          if (form.getFieldMap().containsKey(fieldKey))
             field = (Field)form.getFieldMap().get(fieldKey);
       }

       if (field == null) {
          if (!GenericValidator.isBlankOrNull(language) && 
              !GenericValidator.isBlankOrNull(country)) {
             Form form = get(language, null, null, formKey);
          
             if (form.getFieldMap().containsKey(fieldKey))
                field = (Field)form.getFieldMap().get(fieldKey);
          }
       }  

       if (field == null) {
          Form form = get(defaultLocale, formKey);
          
          if (form.getFieldMap().containsKey(fieldKey))
             field = (Field)form.getFieldMap().get(fieldKey);
       }
       
       return field;  	
    }

}
