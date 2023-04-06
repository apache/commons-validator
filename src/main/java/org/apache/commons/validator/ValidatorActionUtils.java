/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ValidatorActionUtils {

    /**
     * Load the javascript function specified by the given path.  For this
     * implementation, the <code>jsFunction</code> property should contain a
     * fully qualified package and script name, separated by periods, to be
     * loaded from the class loader that created this instance.
     *
     * TODO if the path begins with a '/' the path will be intepreted as
     * absolute, and remain unchanged.  If this fails then it will attempt to
     * treat the path as a file path.  It is assumed the script ends with a
     * '.js'.
     */
    protected synchronized void loadJavascriptFunction( String jsFunction, String javascript, Log log, String name) {

        if (this.javascriptAlreadyLoaded(javascript)) {
            return;
        }

        if (getLog(log).isTraceEnabled()) {
            getLog(log).trace("  Loading function begun");
        }

        if (jsFunction == null) {
            jsFunction = this.generateJsFunction(name);
        }

        final String javascriptFileName = this.formatJavascriptFileName(jsFunction);

        if (getLog(log).isTraceEnabled()) {
            getLog(log).trace("  Loading js function '" + javascriptFileName + "'");
        }

        javascript = this.readJavascriptFile(javascriptFileName,log);

        if (getLog(log).isTraceEnabled()) {
            getLog(log).trace("  Loading javascript function completed");
        }

    }

    /**
     * Read a javascript function from a file.
     * @param javascriptFileName The file containing the javascript.
     * @return The javascript function or null if it could not be loaded.
     */
    private String readJavascriptFile(final String javascriptFileName,Log log) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }

        InputStream is = classLoader.getResourceAsStream(javascriptFileName);
        if (is == null) {
            is = this.getClass().getResourceAsStream(javascriptFileName);
        }

        if (is == null) {
            getLog(log).debug("  Unable to read javascript name "+javascriptFileName);
            return null;
        }

        final StringBuilder buffer = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is)); // TODO encoding
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

        } catch(final IOException e) {
            getLog(log).error("Error reading javascript file.", e);

        } finally {
            try {
                reader.close();
            } catch(final IOException e) {
                getLog(log).error("Error closing stream to javascript file.", e);
            }
        }

        final String function = buffer.toString();
        return function.equals("") ? null : function;
    }

    /**
     * @return true if the javascript for this action has already been loaded.
     */
    private boolean javascriptAlreadyLoaded(String javascript) {
        return (javascript != null);
    }

    /**
     * Used to generate the javascript name when it is not specified.
     */
    private String generateJsFunction(String name) {
        final StringBuilder jsName =
                new StringBuilder("org.apache.commons.validator.javascript");

        jsName.append(".validate");
        jsName.append(name.substring(0, 1).toUpperCase());
        jsName.append(name.substring(1));

        return jsName.toString();
    }

    /**
     * @return A filename suitable for passing to a
     * ClassLoader.getResourceAsStream() method.
     */
    private String formatJavascriptFileName(String jsFunction) {
        String fname = jsFunction.substring(1);

        if (!jsFunction.startsWith("/")) {
            fname = jsFunction.replace('.', '/') + ".js";
        }

        return fname;
    }

    /**
     * Accessor method for Log instance.
     *
     * The Log instance variable is transient and
     * accessing it through this method ensures it
     * is re-initialized when this instance is
     * de-serialized.
     *
     * @return The Log instance.
     */
    private Log getLog(Log log) {
        if (log == null) {
            log =  LogFactory.getLog(ValidatorAction.class);
        }
        return log;
    }

}
