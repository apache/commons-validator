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

/**
 * <p>This class performs validations.  The methods are can be configured to be 
 * used in the framework in the validation.xml file.  (See example webapp)</p>
 *
 * @author David Winterfeldt
*/
public class Validator implements Serializable {
   public static String SERVLET_CONTEXT_KEY = "javax.servlet.ServletContext";
   public static String HTTP_SERVLET_REQUEST_KEY = "javax.servlet.http.HttpServletRequest";
   public static String MESSAGE_RESOURCES_KEY = "org.apache.struts.util.MessageResources";
   public static String LOCALE_KEY = "java.util.Locale";
   public static String BEAN_KEY = "java.lang.Object";
   public static String ACTION_ERRORS_KEY = "org.apache.struts.action.ActionErrors";
   public static String VALIDATOR_ACTION_KEY = "org.apache.commons.validator.ValidatorAction";
   public static String FIELD_KEY = "org.apache.commons.validator.Field";
   
   protected ValidatorResources resources = null;
   protected ValidatorLog logger = null;
   protected String formName = null;
   protected HashMap hResources = new HashMap();
   protected int page = 0;   
   
   public Validator(ValidatorResources resources, String formName) {
      this.resources = resources;
      this.logger = resources.getLogger();
      this.formName = formName;
   }
   
   public void addResource(String key, Object value) {
      hResources.put(key, value);
   }
   
   public void setPage(int page) {
      this.page = page;	
   }

   public int getPage() {
      return page;	
   }
       
   public void validate() throws ValidatorException {
   	Locale locale = null;
   	
   	if (hResources.containsKey(LOCALE_KEY))
   	   locale = (Locale)hResources.get(LOCALE_KEY);
   	
   	if (locale == null)
   	   locale = Locale.getDefault();
   	   
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

                    //if (logger.getDebug() > 10)
		    //   logger.info("***### Validator main - " + va.getName() + " - " + va.getDepends());
		                       
                    if (o == null) {
                       //if (logger.getDebug() > 10)
                       //   logger.info("***### Validator o==null - " + va.getName() + " - " + va.getDepends());
                          
                       lActions.clear();
                       va = null;
                       bMoreActions = false;
                       break;
                    } else {
                       boolean bContinue = ((Boolean)o).booleanValue();
                       
                       //if (logger.getDebug() > 10)
                       //   logger.info("***### Validator - " + va.getName() + "  depend=" + depend + "  bContinue=" + bContinue);
                       
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
	      /**if (logger.getDebug() > 10) {
	         logger.info("***Order ******************************");
	         
	         for (Iterator actions = lActions.iterator(); actions.hasNext(); ) {
	         	 ValidatorAction tmp = (ValidatorAction)actions.next();
                    logger.info("***Order - " + tmp.getName() + " " + tmp.getDepends());
	         }

	         logger.info("***Order End ******************************");
	      }*/
	      
	      if (va != null) {
                 for (Iterator i = form.getFields().iterator(); i.hasNext(); ) {
                    Field field = (Field)i.next();         

                    if (field.getPage() <= page && (field.getDepends() != null && field.isDependency(va.getName()))) {
	               try {
	               	  // Add these two Objects to the resources since they reference 
	               	  // the current validator action and field
	               	  hResources.put(VALIDATOR_ACTION_KEY, va);
	               	  hResources.put(FIELD_KEY, field);

	               	  Class c = Class.forName(va.getClassname(), true, this.getClass().getClassLoader());
	               	  
	               	  List lParams = va.getMethodParamsList();
	               	  int size = lParams.size();
	               	  int beanIndexPos = -1;
	               	  int fieldIndexPos = -1;
	               	  Class[] paramClass = new Class[size];
	               	  Object[] paramValue = new Object[size];

	               	  for (int x = 0; x < size; x++) {
	               	     String paramKey = (String)lParams.get(x);
               	             
               	             if (BEAN_KEY.equals(paramKey))
               	                beanIndexPos = x;
               	                
               	             if (FIELD_KEY.equals(paramKey))
               	                fieldIndexPos = x;
               	             
	               	     // There were problems calling getClass on paramValue[]
	               	     paramClass[x] = Class.forName(paramKey, true, this.getClass().getClassLoader());
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
	                        logger.log("Validator::validate - Couldn't load instance " +
	                                   "of class " + va.getClassname() + ".  " + ex.getMessage());   
	                     }
	                  }

                          Object result = null;
                          
                          if (field.isIndexed()) {
                             Object oIndexed = PropertyUtils.getProperty(hResources.get(BEAN_KEY), field.getIndexedListProperty());
                             Object indexedList[] = new Object[0];
                             
                             if (oIndexed instanceof Collection)
                                indexedList = ((Collection)oIndexed).toArray();
                             else if(oIndexed.getClass().isArray())
                                indexedList = (Object[])oIndexed;
                             
                             for (int pos = 0; pos < indexedList.length; pos++) {
                                // Set current iteration object to the parameter array
                                paramValue[beanIndexPos] = indexedList[pos];
                                
                                // Set field clone with the key modified to represent 
                                // the current field
                                Field indexedField = (Field)field.clone();
                                indexedField.setKey(ValidatorUtil.replace(indexedField.getKey(), Field.TOKEN_INDEXED, "[" + pos + "]"));
                                paramValue[fieldIndexPos] = indexedField;
                                
                             	result = m.invoke(va.getClassnameInstance(), paramValue);

                                if (result instanceof Boolean) {
                                   Boolean valid = (Boolean)result;
                                   if (!valid.booleanValue())
                                      iErrorCount++;
                                }
                             }
                          } else {
                             result = m.invoke(va.getClassnameInstance(), paramValue);

                             if (result instanceof Boolean) {
                                Boolean valid = (Boolean)result;
                                if (!valid.booleanValue())
                                   iErrorCount++;
                             }
                          }
		       } catch (Exception e) {
		          bErrors = true;
		          logger.log("Validator::validate() reflection - " + e.getMessage());
		          
		          if (e instanceof ValidatorException)
		             throw ((ValidatorException)e);
		       }
                 
	            }
	         }
                 
                 if (iErrorCount == 0) {
                    hActionsRun.put(va.getName(), new Boolean(true));
                 } else {
                    hActionsRun.put(va.getName(), new Boolean(false));
	         }
	         
	         if (logger.getDebug() > 10)
	            logger.info("*** Validator - " + va.getName() + "  size=" + lActions.size());
                    
                 if (lActions.size() > 0)
	            lActions.remove(0);
	         
	         if (logger.getDebug() > 10)
	            logger.info("*** Validator - after remove - " + va.getName() + "  size=" + lActions.size());
              }
              
	      if (lActions.size() == 0)
	         bMoreActions = false;        
	   }
	}
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
