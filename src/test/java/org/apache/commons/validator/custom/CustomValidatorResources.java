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
package org.apache.commons.validator.custom;

import java.io.InputStream;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.apache.commons.validator.ValidatorResources;

/**
 * Custom ValidatorResources implementation.
 *
 * @version $Revision$
 */
public class CustomValidatorResources extends ValidatorResources {

    private static final long serialVersionUID = 1272843199141974642L;

    /**
     * Create a custom ValidatorResources object from an uri
     *
     * @param in InputStream for the validation.xml configuration file.
     * @throws SAXException if the validation XML files are not valid or well formed.
     * @throws IOException  if an I/O error occurs processing the XML files
     */
    public CustomValidatorResources(InputStream in) throws IOException, SAXException {
        super(in);
    }

}