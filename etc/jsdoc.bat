@echo off

rem * $Id$
rem * ====================================================================
rem *
rem * Licensed to the Apache Software Foundation (ASF) under one or more
rem * contributor license agreements.  See the NOTICE file distributed with
rem * this work for additional information regarding copyright ownership.
rem * The ASF licenses this file to You under the Apache License, Version 2.0
rem * (the "License"); you may not use this file except in compliance with
rem * the License.  You may obtain a copy of the License at
rem *
rem *     http://www.apache.org/licenses/LICENSE-2.0
rem *
rem * Unless required by applicable law or agreed to in writing, software
rem * distributed under the License is distributed on an "AS IS" BASIS,
rem * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem * See the License for the specific language governing permissions and
rem * limitations under the License.

rem *--------------------------------------------------------------*
rem *                                                              *
rem * JSDoc is a perl script for generating javadoc for javascript.*
rem *                                                              *
rem * The latest version can be downloaded from :                  *
rem *                                                              *
rem *         http://sourceforge.net/projects/jsdoc/               *
rem *                                                              *
rem * N.B. You also need something like ActivePerl installed to    *
rem *      run this on windows. See the Installation instructions: *
rem *                                                              *
rem *         http://jsdoc.sourceforge.net/#install                *
rem *                                                              *
rem *--------------------------------------------------------------*

set _PACKAGE=/org/apache/commons/validator/javascript
set _JSDOC_HOME=%1
set _JAVASCRIPT_DIR=%2%_PACKAGE%
set _OUTPUT_DIR=%3%_PACKAGE%
set _COPYRIGHT="Copyright © 2000-2012 - Apache Software Foundation"
perl %_JSDOC_HOME%/jsdoc.pl --project-summary %_JAVASCRIPT_DIR%/package.html --project-name "Package %_PACKAGE%" --page-footer %_COPYRIGHT% -d %_OUTPUT_DIR% %_JAVASCRIPT_DIR% 
