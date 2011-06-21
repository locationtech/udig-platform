#!/bin/bash
echo "Release $0"

# ignore mac resources when using tar,zip,etc...
#
export COPYFILE_DISABLE=true
source ./versions.sh

# Release macosx if available
if [ -f ${TARGET}/udig-${VERSION}.macosx.cocoa.x86_64.zip ] 
then
    echo "Releasing macosx64"
    
    if [ ! -d ${BUILD}/mac64 ] 
    then
       echo "Creating ${BUILD}/mac64"
       mkdir -p ${BUILD}/mac64
    fi
    
    if [ ! -f ${BUILD}/udig-${VERSION}.macosx.cocoa.x86_64.zip ]
    then
        echo "Building ${BUILD}/udig-${VERSION}.macosx.cocoa.x86_64.zip ..."
        
        echo "Extracting ${TARGET}/udig-${VERSION}macosx.cocoa.x86_64.zip"
        unzip -q -d ${BUILD}/mac64 ${TARGET}/udig-${VERSION}.macosx.cocoa.x86_64.zip
        
        echo "Prepairing ${BUILD}/macosx"
        mv ${BUILD}/mac64/udig/udig_internal.app ${BUILD}/mac64/udig/udig.app
        cp ${BASE}/udig-1.2.x.html ${BUILD}/mac64/udig/udig-${VERSION}.html
        
        echo "Assemble ${BUILD}/udig-${VERSION}.macosx.cocoa.x86_64.zip"
        cd ${BUILD}/mac64
        zip -9 -r -q ../udig-${VERSION}.macosx.cocoa.x86_64.zip udig
     else 
       echo "Already Exists ${BUILD}/udig-${VERSION}.macosx.cocoa.x86_64.zip"
     fi
fi
