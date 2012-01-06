#!/bin/sh
#  $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/etc/jsdoc.sh,v 1.4 2004/05/21 14:28:12 rleland Exp $
#  $Revision$
#  $Date$
# 
#  ====================================================================
#  Licensed to the Apache Software Foundation (ASF) under one or more
#  contributor license agreements.  See the NOTICE file distributed with
#  this work for additional information regarding copyright ownership.
#  The ASF licenses this file to You under the Apache License, Version 2.0
#  (the "License"); you may not use this file except in compliance with
#  the License.  You may obtain a copy of the License at
# 
#      http://www.apache.org/licenses/LICENSE-2.0
# 
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#

# jsdoc is a perl script for generating javadoc for javascript.
# The latest version can be downloaded from :
#      http://sourceforge.net/projects/jsdoc/
#

dirtoprocess=$2
outputto=$3
package=/org/apache/commons/validator/javascript
perl $1/jsdoc.pl --project-summary ${dirtoprocess}${package}/package.html --project-name "Package org.apache.commons.validator.javascript" --page-footer "Copyright © 2000-2012 - Apache Software Foundation
" -d ${outputto}/${package} -r $dirtoprocess
