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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogSource;


/**
 * <p>Validations are processed by the validate method.  
 * An instance of <code>ValidatorResources</code> is 
 * used to define the validators (validation methods) 
 * and the validation rules for a JavaBean.</p>
 *
 * @author David Winterfeldt
*/
public class Validator implements Serializable {

   /**
    * Logger
   */
   protected static Log log = LogSource.getInstance(Validator.class.getName());

   /**
    * Resources key the JavaBean is stored to perform validation on.
   */
   public static String BEAN_KEY = "java.lang.Object";

   /**
    * Resources key the <code>ValidatorAction</code> is stored under.  
    * This will be automatically passed into a validation method 
    * with the current <code>ValidatorAction</code> if it is 
    * specified in the method signature.
   */
   public static String VALIDATOR_ACTION_KEY = "org.apache.commons.validator.ValidatorAction";

   /**
    * Resources key the <code>Field</code> is stored under.  
    * This will be automatically passed into a validation method 
    * with the current <code>Field</code> if it is 
    * specified in the method signature.
   */
   public static String FIELD_KEY = "org.apache.commons.validator.Field";

   /**
    * Resources key the <code>Locale</code> is stored.
    * This will be used to retrieve the appropriate 
    * <code>FormSet</code> and <code>Form</code> to be 
    * processed.
   */
   public static String LOCALE_KEY = "java.util.Locale";
   
   protected ValidatorResources resources = null;
   protected String formName = null;
   protected HashMap hResources = new HashMap();
   protected int page = 0;   

   /**
    * The class loader to use for instantiating application objects.
    * If not specified, the context class loader, or the class loader
    * used to load Digester itself, is used, based on the value of the
    * <code>useContextClassLoader</code> variable.
   */
   protected ClassLoader classLoader = null;

   /**
    * Do we want to use the Context ClassLoader when loading classes
    * for instantiating new objects?  Default is <code>false</code>.
   */
   protected boolean useContextClassLoader = false;

   /**
    * Construct a <code>Validator</code> that will 
    * use the <code>ValidatorResources</code> 
    * passed in to retrieve pluggable validators 
    * the different sets of validation rules.
    *
    * @param	resources	<code>ValidatorResources</code> 
    *				to use during validation.
   */
   public Validator(ValidatorResources resources) {
      this.resources = resources;
   }

   /**
    * Construct a <code>Validator</code> that will 
    * use the <code>ValidatorResources</code> 
    * passed in to retrieve pluggable validators 
    * the different sets of validation rules.
    *
    * @param	resources	<code>ValidatorResources</code> 
    *				to use during validation.
    * @param	formName	Key used for retrieving the set of 
    *				validation rules.
   */
   public Validator(ValidatorResources resources, String formName) {
      this.resources = resources;
      this.formName = formName;
   }
   
   /**
    * Add a resource to be used during the processing 
    * of validations.
    *
    * @param	key		The full class name of the parameter 
    *				of the validation method that 
    *				corresponds to the value/instance 
    *				passed in with it.
    * @param	value		The instance that will be passed 
    *				into the validation method.
   */
   public void addResource(String key, Object value) {
      hResources.put(key, value);
   }

   /**
    * Gets the form name which is the key 
    * to a set of validation rules.
   */
   public String getFormName() {
      return formName;	
   }
   
   /**
    * Sets the form name which is the key 
    * to a set of validation rules.
   */
   public void setFormName(String formName) {
      this.formName = formName;	
   }

   /**
    * Gets the page.  This in conjunction with 
    * the page property of a <code>Field<code> 
    * can control the processing of fields.  
    * If the field's page is less than or equal 
    * to this page value, it will be processed.
   */
   public int getPage() {
      return page;	
   }
   
   /**
    * Sets the page.  This in conjunction with 
    * the page property of a <code>Field<code> 
    * can control the processing of fields.  
    * If the field's page is less than or equal 
    * to this page value, it will be processed.
   */
   public void setPage(int page) {
      this.page = page;	
   }

   /**
    * Clears the form name, resources that were added, 
    * and the page that was set (if any).  This can 
    * be called to reinitialize the Validator instance 
    * so it can be reused.  The form name (key to 
    * set of validation rules) and any resources needed, 
    * like the JavaBean being validated, will need to 
    * set and/or added to this instance again.  The 
    * <code>ValidatorResources</code> will not be removed 
    * since it can be used again and is thread safe.
   */
   public void clear() {
      formName = null;
      hResources = new HashMap();
      page = 0;   
   }

   /**
    * Return the boolean as to whether the context classloader should be used.
    */
   public boolean getUseContextClassLoader() {

       return useContextClassLoader;

   }

   /**
    * Determine whether to use the Context ClassLoader (the one found by
    * calling <code>Thread.currentThread().getContextClassLoader()</code>)
    * to resolve/load classes that are defined in various rules.  If not
    * using Context ClassLoader, then the class-loading defaults to
    * using the calling-class' ClassLoader.
    *
    * @param boolean determines whether to use Context ClassLoader.
   */
   public void setUseContextClassLoader(boolean use) {

       useContextClassLoader = use;

   }

   /**
    * Return the class loader to be used for instantiating application objects
    * when required.  This is determined based upon the following rules:
    * <ul>
    * <li>The class loader set by <code>setClassLoader()</code>, if any</li>
    * <li>The thread context class loader, if it exists and the
    *     <code>useContextClassLoader</code> property is set to true</li>
    * <li>The class loader used to load the Digester class itself.
    * </ul>
   */
   public ClassLoader getClassLoader() {
      if (this.classLoader != null) {
         return (this.classLoader);
      }
      
      if (this.useContextClassLoader) {
         ClassLoader classLoader =
                 Thread.currentThread().getContextClassLoader();
         if (classLoader != null) {
             return (classLoader);
         }
      }
      
      return (this.getClass().getClassLoader());
   }

   /**
    * Set the class loader to be used for instantiating application objects
    * when required.
    *
    * @param classLoader The new class loader to use, or <code>null</code>
    *  to revert to the standard rules
   */
   public void setClassLoader(ClassLoader classLoader) {

       this.classLoader = classLoader;

   }
      
   /**
    * Performs validations based on the configured resources.  
    * 
    * @return	The <code>Map</code> returned uses the property 
    *		of the <code>Field</code> for the key and the value 
    *		is the number of error the field had.
   */ 
   public ValidatorResults validate() throws ValidatorException {
      ValidatorResults results = new ValidatorResults();
      Locale locale = null;
      
      if (hResources.containsKey(LOCALE_KEY)) {
         locale = (Locale)hResources.get(LOCALE_KEY);
      }
      
      if (locale == null) {
         locale = Locale.getDefault();
      }
         
      Form form = null;
      if ((form = resources.get(locale, formName)) != null) {	    
         Map hActions = resources.getValidatorActions();
         List lActions = new ArrayList();
         Map hActionsRun = new HashMap();
         boolean bMoreActions = true;
         boolean bErrors = false;
      
         for (Iterator actions = hActions.values().iterator(); actions.hasNext(); )
            lActions.add(actions.next());
      
         while (bMoreActions) {
            ValidatorAction va = null;
            int iErrorCount = 0;
            
            // FIX ME - These sorts will not work for all variations.
            // Sort by number dependencies
            Collections.sort(lActions, new DependencyComparator());
      
            // Sort by number of dependencies successfully run
            Collections.sort(lActions, new DependencySuccessComparator(hActionsRun));
            
            if (lActions.size() > 0)
               va = (ValidatorAction)lActions.get(0);
      
            if (va != null && va.getDepends() != null && va.getDepends().length() > 0) {
               StringTokenizer st = new StringTokenizer(va.getDepends(), ",");
               while (st.hasMoreTokens()) {
                  String depend = st.nextToken().trim();
                  Object o = hActionsRun.get(depend);
      
                  if (log.isDebugEnabled()) {
      	             log.debug("ValidatorAction name=" + va.getName() + "  depends=" + va.getDepends());
      	          }
      	                       
                  if (o == null) {
                     lActions.clear();
                     va = null;
                     bMoreActions = false;
                     break;
                  } else {
                     boolean bContinue = ((Boolean)o).booleanValue();

                     if (log.isDebugEnabled()) {
      	                log.debug("ValidatorAction name=" + va.getName() + "  depend=" + depend + "  bContinue=" + bContinue);
      	             }
                     
                     if (!bContinue) {
                        lActions.clear();
                        va = null;
                        bMoreActions = false;
                        break;
                     }
                  }
               }
            }
            
            // For debug   

            if (log.isDebugEnabled()) {
               StringBuffer sbLog = new StringBuffer();
      	       sbLog.append("Order \n");
               
               for (Iterator actions = lActions.iterator(); actions.hasNext(); ) {
                  ValidatorAction tmp = (ValidatorAction)actions.next();
                  sbLog.append("\t ValidatorAction name=" + tmp.getName() + "  depends=" + tmp.getDepends() + "\n");
               }

               log.debug(sbLog.toString());
      	    }
            
            if (va != null) {
               for (Iterator i = form.getFields().iterator(); i.hasNext(); ) {
                  Field field = (Field)i.next();         
      
                  if (field.getPage() <= page && (field.getDepends() != null && field.isDependency(va.getName()))) {
                     try {
                     	  // Add these two Objects to the resources since they reference 
                     	  // the current validator action and field
                     	  hResources.put(VALIDATOR_ACTION_KEY, va);
                     	  hResources.put(FIELD_KEY, field);
      
                     	  Class c = getClassLoader().loadClass(va.getClassname());

                     	  List lParams = va.getMethodParamsList();
                     	  int size = lParams.size();
                     	  int beanIndexPos = -1;
                     	  int fieldIndexPos = -1;
                     	  Class[] paramClass = new Class[size];
                     	  Object[] paramValue = new Object[size];
      
                     	  for (int x = 0; x < size; x++) {
                     	     String paramKey = (String)lParams.get(x);
             	             
             	             if (BEAN_KEY.equals(paramKey)) {
             	                beanIndexPos = x;
             	             }
             	                
             	             if (FIELD_KEY.equals(paramKey)) {
             	                fieldIndexPos = x;
             	             }
             	             
                     	     // There were problems calling getClass on paramValue[]
                     	     paramClass[x] = getClassLoader().loadClass(paramKey);

                     	     paramValue[x] = hResources.get(paramKey);
                     	  }
      
                        Method m = c.getMethod(va.getMethod(), paramClass);
      		  
      		        // If the method is static we don't need an instance of the class 
      		        // to call the method.  If it isn't, we do.
                        if (!Modifier.isStatic(m.getModifiers())) {
                           try {
                              if (va.getClassnameInstance() == null) {
                                 va.setClassnameInstance(c.newInstance());
                              }
                           } catch (Exception ex) {
                              log.error("Couldn't load instance " +
                                        "of class " + va.getClassname() + ".  " + ex.getMessage());
                           }
                        }
      
                        Object result = null;
                        
                        if (field.isIndexed()) {
                           Object oIndexed = PropertyUtils.getProperty(hResources.get(BEAN_KEY), field.getIndexedListProperty());
                           Object indexedList[] = new Object[0];
                           
                           if (oIndexed instanceof Collection) {
                              indexedList = ((Collection)oIndexed).toArray();
                           } else if(oIndexed.getClass().isArray()) {
                              indexedList = (Object[])oIndexed;
                           }
                           
                           for (int pos = 0; pos < indexedList.length; pos++) {
                              // Set current iteration object to the parameter array
                              paramValue[beanIndexPos] = indexedList[pos];
                              
                              // Set field clone with the key modified to represent 
                              // the current field
                              Field indexedField = (Field)field.clone();
                              indexedField.setKey(ValidatorUtil.replace(indexedField.getKey(), Field.TOKEN_INDEXED, "[" + pos + "]"));
                              paramValue[fieldIndexPos] = indexedField;
                              
                              result = m.invoke(va.getClassnameInstance(), paramValue);
                           }
                        } else {
                           result = m.invoke(va.getClassnameInstance(), paramValue);
                        }
                        
                        if (!isValid(result)) {
                           iErrorCount++;
                        }
                         
                        results.add(field, va.getName(), isValid(result));
      	       } catch (Exception e) {
      	          bErrors = true;
      	          log.error("reflection: " + e.getMessage(), e);
      	          
      	          results.add(field, va.getName(), false);
      	          
      	          if (e instanceof ValidatorException) {
      	             throw ((ValidatorException)e);
      	          }
      	       }
               
                  }
               }
               
               if (iErrorCount == 0) {
                  hActionsRun.put(va.getName(), new Boolean(true));
               } else {
                  hActionsRun.put(va.getName(), new Boolean(false));
               }
               
               if (log.isDebugEnabled()) {
                  log.debug("name=" + va.getName() + "  size=" + lActions.size());
               }
                  
               if (lActions.size() > 0) {
                  lActions.remove(0);
               }
               
               if (log.isDebugEnabled()) {
                  log.debug("after remove - name=" + va.getName() + "  size=" + lActions.size());
               }
            }
            
            if (lActions.size() == 0) {
               bMoreActions = false;
            }
         }
      }
      
      return results;
   }

   /**
    * Returns if the result if valid.  If the 
    * result object is <code>Boolean</code>, then it will 
    * the value.  If the result object isn't <code>Boolean</code>, 
    * then it will return <code>false</code> if the result 
    * object is <code>null</code> and <code>true</code> if it 
    * isn't.
   */
   private boolean isValid(Object result) {
      boolean bValid = false;
      
      if (result instanceof Boolean) {
         Boolean valid = (Boolean)result;
         bValid = valid.booleanValue();
      } else {
         bValid = (result != null);
      }
      
      return bValid;
   }

   /**
    * Sort by number dependencies.
   */
   protected class DependencyComparator implements Comparator {
      
      public int compare(Object o1, Object o2) {
         ValidatorAction va1 = (ValidatorAction)o1;
         ValidatorAction va2 = (ValidatorAction)o2;
      
         if ((va1.getDepends() == null || va1.getDepends().length() == 0) && 
              (va2.getDepends() == null || va2.getDepends().length() == 0)) {
            return 0;
         } else if ((va1.getDepends() != null && va1.getDepends().length() > 0) &&
                    (va2.getDepends() == null || va2.getDepends().length() == 0)) {
            return 1;
         } else if ((va1.getDepends() == null || va1.getDepends().length() == 0) && 
                  (va2.getDepends() != null && va2.getDepends().length() > 0)) {
            return -1;
         } else {
            return va1.getDependencies().size() - va2.getDependencies().size();
         }
      }

   }

   /**
    * Sort by number of dependencies successfully run.
   */
   protected class DependencySuccessComparator implements Comparator {
      Map hActionsRun = null;
      
      public DependencySuccessComparator(Map hActionsRun) {
         this.hActionsRun = hActionsRun;
      }
      
      public int compare(Object o1, Object o2) {
         ValidatorAction va1 = (ValidatorAction)o1;
         ValidatorAction va2 = (ValidatorAction)o2;
      
         if ((va1.getDepends() == null || va1.getDepends().length() == 0) && 
              (va2.getDepends() == null || va2.getDepends().length() == 0)) {
            return 0;
         } else if ((va1.getDepends() != null && va1.getDepends().length() > 0) &&
                    (va2.getDepends() == null || va2.getDepends().length() == 0)) {
            return 1;
         } else if ((va1.getDepends() == null || va1.getDepends().length() == 0) && 
                  (va2.getDepends() != null && va2.getDepends().length() > 0)) {
            return -1;
         } else {
            int iVA1 = 0;
            int iVA2 = 0;
            
            for (Iterator i = va1.getDependencies().iterator(); i.hasNext(); ) {
               String depend = ((String)i.next()).trim();
               Object o = hActionsRun.get(depend);
               
               if (o != null) {
                  if (((Boolean)o).booleanValue())
                    iVA1++;
               }
            }
      
            for (Iterator i = va2.getDependencies().iterator(); i.hasNext(); ) {
               String depend = ((String)i.next()).trim();
               Object o = hActionsRun.get(depend);
               
               if (o != null) {
                  if (((Boolean)o).booleanValue())
                    iVA2++;
               }	
            }
            
            return iVA1 - iVA2;
         }
      }

   }

}
