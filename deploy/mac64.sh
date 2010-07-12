#!/bin/bash
echo "Release"

# ignore mac resources when using tar,zip,etc...
#
export COPYFILE_DISABLE=true
source ./versions.sh

# Release macosx if available
if [ -f ${TARGET}/udig-${VERSION}.macosx.cocoa.x86_64.zip ] 
then
    echo "Releasing macosx64"
    
    if [ ! -d ${BUILD}/macosx64 ] 
    then
       echo "Creating ${BUILD}/macosx64"
       mkdir -p ${BUILD}/macosx64
    fi
    
    if [ ! -f ${BUILD}/udig-${VERSION}.macosx.cocoa.x86_64.zip ]
    then
        echo "Building ${BUILD}/udig-${VERSION}.macosx.cocoa.x86_64.zip ..."
        
        echo "Extracting ${TARGET}/udig-${VERSION}macosx.cocoa.x86_64.zip"
        unzip -q -d ${BUILD}/macosx64 ${TARGET}/udig-${VERSION}.macosx.cocoa.x86_64.zip
        
        echo "Prepairing ${BUILD}/macosx"
        mv ${BUILD}/macosx64/udig/udig_internal.app ${BUILD}/macosx64/udig/udig.app
        
        echo "Assemble ${BUILD}/udig-${VERSION}.macosx.cocoa.x86_64.zip"
        zip -9 -r -q ${BUILD}/udig-${VERSION}.macosx.cocoa.x86_64.zip ${BUILD}/macosx64/udig 
     else 
       echo "Already Exists ${BUILD}/udig-${VERSION}.macosx.cocoa.x86_64.zip"
     fi
fi
