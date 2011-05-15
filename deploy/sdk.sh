#!/bin/bash
echo "Release"

# ignore mac resources when using tar,zip,etc...
#
export COPYFILE_DISABLE=true
source ./versions.sh

# Release sdk if available
if [ -f ${TARGET}/udig-${VERSION}-sdk.zip ] 
then
    echo "Releasing SDK"
    
    if [ ! -d ${BUILD}/sdk ] 
    then
       echo "Creating ${BUILD}/sdk"
       mkdir -p ${BUILD}/sdk
    fi
    
    if [ ! -f ${BUILD}/udig-${VERSION}-sdk.zip ]
    then
        echo "Building  ${BUILD}/udig-${VERSION}-sdk.zip ..."
        
        echo "Extracting ${TARGET}/udig-${VERSION}-sdk.zip"
        unzip -q -d ${BUILD}/sdk ${TARGET}/udig-${VERSION}-sdk.zip
        
        echo "Prepairing ${BUILD}/sdk"
        rm -rf ${BUILD}/sdk/udig_sdk/*.app
        rm ${BUILD}/sdk/udig_sdk/*.exe
        rm ${BUILD}/sdk/udig_sdk/*.ini
        rm ${BUILD}/sdk/udig_sdk/*.sh
        
        rm -rf ${BUILD}/sdk/udig_sdk/configuration
        
        rm ${BUILD}/sdk/udig_sdk/plugins/*swt*macosx*
        rm ${BUILD}/sdk/udig_sdk/plugins/*swt*win32*
        rm ${BUILD}/sdk/udig_sdk/plugins/*swt*linux*
        
        # TODO: figure out how to make libs and libs source have the same qualifier
        mv ${BUILD}/sdk/udig_sdk/plugins/net.refractions.udig.libs.source_${QUALIFIER}/src/net.refractions.udig.libs_${TAG}.qualifier \
           ${BUILD}/sdk/udig_sdk/plugins/net.refractions.udig.libs.source_${QUALIFIER}/src/net.refractions.udig.libs_${QUALIFIER}
        
        cp ${BASE}/udig-1.2.x.html ${BUILD}/sdk/udig_sdk/udig-${VERSION}.html
        
        echo "Assemble ${BUILD}/udig-${VERSION}-sdk.zip "
        cd ${BUILD}/sdk
        zip -9 -r -q ../udig-${VERSION}-sdk.zip udig_sdk 
     else 
       echo "Already Exists ${BUILD}/udig-${VERSION}-sdk.zip"
     fi
fi
