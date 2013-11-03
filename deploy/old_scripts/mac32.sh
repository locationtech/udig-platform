#!/bin/bash
echo "Release $0"

# ignore mac resources when using tar,zip,etc...
#
export COPYFILE_DISABLE=true
source ./versions.sh

# Release macosx if available
if [ -f ${TARGET}/udig-${VERSION}.macosx.cocoa.x86.zip ] 
then
    echo "Releasing macosx"
    
    if [ ! -d ${BUILD}/mac32 ] 
    then
       echo "Creating ${BUILD}/mac32 directory"
       mkdir -p ${BUILD}/mac32
    fi
    
    if [ ! -f ${BUILD}/udig-${VERSION}.macosx.cocoa.x86.zip ]
    then
        echo "Building ${BUILD}/udig-${VERSION}-macosx.macosx.x86.zip ..."
        
        echo "Extracting ${TARGET}/udig-${VERSION}.macosx.cocoa.x86.zip ..."
        unzip -q -d ${BUILD}/mac32 ${TARGET}/udig-${VERSION}.macosx.cocoa.x86.zip
        
        echo "Prepairing ${BUILD}/mac32 .."
        mv ${BUILD}/mac32/udig/udig_internal.app ${BUILD}/mac32/udig/udig.app
        cp ${BASE}/udig-1.2.x.html ${BUILD}/mac32/udig/udig-${VERSION}.html
        
        echo "Assemble ${BUILD}/udig-${VERSION}.macosx.cocoa.x86.zip.."
        cd ${BUILD}/mac32
        zip -9 -r -q ../udig-${VERSION}.macosx.cocoa.x86.zip udig
     else
       echo "Already Exists ${BUILD}/udig-${VERSION}.macosx.cocoa.x86.zip"
     fi
fi
