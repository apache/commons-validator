/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/Attic/ValidatorResourcesInitializer.java,v 1.18 2003/08/21 19:40:13 rleland Exp $
 * $Revision: 1.18 $
 * $Date: 2003/08/21 19:40:13 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names, "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

/**
 * <p>Maps an xml file to <code>ValidatorResources</code>.</p>
 *
 * @author David Winterfeldt
 * @author Dave Derry
 * @version $Revision: 1.18 $ $Date: 2003/08/21 19:40:13 $
 * @deprecated ValidatorResources knows how to initialize itself now.
 */
public class ValidatorResourcesInitializer {

   /**
    * Logger.
    */
   protected static Log log = LogFactory.getLog(ValidatorResourcesInitializer.class);
   

    /**
     * The set of public identifiers, and corresponding resource names, for
     * the versions of the configuration file DTDs that we know about.  There
     * <strong>MUST</strong> be an even number of Strings in this list!
     */
    protected static String registrations[] = {
        "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.0//EN",
        "/org/apache/commons/validator/resources/validator_1_0.dtd",
        "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.0.1//EN",
        "/org/apache/commons/validator/resources/validator_1_0_1.dtd",
        "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.1//EN",
        "/org/apache/commons/validator/resources/validator_1_1.dtd"
   };


   /**
    * Initializes a <code>ValidatorResources</code> based on a
    * file path and automatically process the resources.
    *
    * @param	fileName	The file path for the xml resource.
    */
   public static ValidatorResources initialize(String fileName)
      throws IOException { 
      
      return initialize(new BufferedInputStream(new FileInputStream(fileName)));
   }

   /**
    * Initializes a <code>ValidatorResources</code> based on the 
    * <code>InputStream</code> and automatically process the resources.
    *
    * @param	in <code>InputStream</code> for the xml resource.
    */
    public static ValidatorResources initialize(InputStream in) throws IOException {
    
        ValidatorResources resources = new ValidatorResources();
        initialize(resources, in);
    
        return resources;
    }


   /**
    * Initializes the <code>ValidatorResources</code> based on the <code>InputStream</code> 
    * and automatically process the resources.
    *
    * @param	resources Resources to initialize.
    * @param	in <code>InputStream</code> for the xml resource.
    */   
    public static void initialize(ValidatorResources resources, InputStream in)
        throws IOException {
            
        initialize(resources, in, true);
    }
   
   /**
    * Initializes a <code>ValidatorResources</code> based on the <code>InputStream</code> 
    * and processes the resources based on the <code>boolean</code> passed in.
    *
    * @param	resources Resources to initialize.
    * @param	in <code>InputStream</code> for the xml resource.
    * @param	process Whether or not to call process on 
    * <code>ValidatorResources</code>.
    */   
    public static void initialize(
        ValidatorResources resources,
        InputStream in,
        boolean process)
        throws IOException {

        URL rulesUrl = ValidatorResourcesInitializer.class.getResource("digester-rules.xml");
        Digester digester = DigesterLoader.createDigester(rulesUrl);
        digester.setNamespaceAware(true);
        digester.setValidating(false);
        digester.setUseContextClassLoader(true);
        
        // register DTDs
        for (int i = 0; i < registrations.length; i += 2) {
            URL url =
                ValidatorResourcesInitializer.class.getResource(
                    registrations[i + 1]);
            if (url != null) {
                digester.register(registrations[i], url.toString());
            }
        }

        digester.push(resources);

        try {
            digester.parse(in);

        } catch (SAXException e) {
            log.error(e.getMessage(), e);
            
        } finally {
            if (in != null) {
                in.close();
            }
        }

        if (process) {
            resources.process();
        }

    }
   
}
