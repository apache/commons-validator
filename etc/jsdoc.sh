#!/bin/sh
cmdpath=$1
dirtoprocess=$2
outputto=$3
package=/org/apache/commons/validator/javascript
perl ${cmdpath}/jsdoc.pl --project-summary ${dirtoprocess}${package}/package.html --project-name "Package org.apache.commons.validator.javascript" --page-footer "Copyright © 2000-2003 - Apache Software Foundation
" -d ${outputto}/${package} -r $dirtoprocess
cp -R ${dirtoprocess}/${package}/doc-files $outputto/${package}
