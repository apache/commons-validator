/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/share/org/apache/commons/validator/Attic/ValidatorResourcesInitializer.java,v 1.23 2004/04/04 13:53:25 rleland Exp $
 * $Revision: 1.23 $
 * $Date: 2004/04/04 13:53:25 $
 *
 * ====================================================================
 * Copyright 2001-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * @deprecated ValidatorResources knows how to initialize itself now.
 */
public class ValidatorResourcesInitializer {

    /**
     * Logger.
     * @deprecated Subclasses should use their own logging instance.
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
        "/org/apache/commons/validator/resources/validator_1_1.dtd",
        "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.2.0//EN",
        "/org/apache/commons/validator/resources/validator_1_2_0.dtd"
    };


    /**
     * Initializes a <code>ValidatorResources</code> based on a
     * file path and automatically process the resources.
     *
     * @param    fileName    The file path for the xml resource.
     */
    public static ValidatorResources initialize(String fileName)
            throws IOException {

        return initialize(new BufferedInputStream(new FileInputStream(fileName)));
    }

    /**
     * Initializes a <code>ValidatorResources</code> based on the
     * <code>InputStream</code> and automatically process the resources.
     *
     * @param    in <code>InputStream</code> for the xml resource.
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
     * @param    resources Resources to initialize.
     * @param    in <code>InputStream</code> for the xml resource.
     */
    public static void initialize(ValidatorResources resources, InputStream in)
            throws IOException {

        initialize(resources, in, true);
    }

    /**
     * Initializes a <code>ValidatorResources</code> based on the <code>InputStream</code>
     * and processes the resources based on the <code>boolean</code> passed in.
     *
     * @param    resources Resources to initialize.
     * @param    in <code>InputStream</code> for the xml resource.
     * @param    process Whether or not to call process on
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

        } catch(SAXException e) {
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
