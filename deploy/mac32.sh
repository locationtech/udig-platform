#!/bin/bash
echo "Release"

# ignore mac resources when using tar,zip,etc...
#
export COPYFILE_DISABLE=true
source ./versions.sh

# Release macosx if available
if [ -f ${TARGET}/udig-${VERSION}.macosx.cocoa.x86.zip ] 
then
    echo "Releasing macosx"
    
    if [ ! -d ${BUILD}/macosx ] 
    then
       echo "Creating ${BUILD}/macosx"
       mkdir -p ${BUILD}/macosx
    fi
    
    if [ ! -f ${BUILD}/udig-${VERSION}.macosx.cocoa.x86.zip ]
    then
        echo "Building ${BUILD}/udig-${VERSION}-macosx.macosx.x86.zip ..."
        
        echo "Extracting ${TARGET}/udig-${VERSION}.macosx.cocoa.x86.zip"
        unzip -q -d ${BUILD}/macosx ${TARGET}/udig-${VERSION}.macosx.cocoa.x86.zip
        
        echo "Prepairing ${BUILD}/macosx"
        mv ${BUILD}/macosx/udig/udig_internal.app ${BUILD}/macosx/udig/udig.app
        
        echo "Assemble ${BUILD}/udig-${VERSION}.macosx.cocoa.x86.zip"
        zip -9 -r -q ${BUILD}/udig-${VERSION}.macosx.cocoa.x86.zip ${BUILD}/macosx/udig 
     else 
       echo "Already Exists ${BUILD}/udig-${VERSION}.macosx.cocoa.x86.zip"
     fi
fi
