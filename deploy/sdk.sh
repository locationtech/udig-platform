#!/bin/bash

# ignore mac resources when using tar,zip,etc...
#
export COPYFILE_DISABLE=true
source ./versions.sh
echo "Release SDK ${VERSION}"

# ls ${BASE}/../features/net.refractions.udig_sdk-product/target/udig-1.3.SNAPSHOT-sdk-linux.gtk.x86.zip

SDK_FILE="udig-${VERSION}-sdk.zip"

# Release sdk if available
if [ -f ${SDK_TARGET}/${SDK_FILE} ] 
then
    echo "Releasing SDK from ${SDK_TARGET}/${SDK_FILE} "
    
    if [ ! -d ${BUILD}/sdk ] 
    then
       echo "Creating ${BUILD}/sdk"
       mkdir -p ${BUILD}/sdk
    else
        echo "Clearing ${BUILD}/sdk"
        rm -rf -d ${BUILD}/sdk
        mkdir -p ${BUILD}/sdk
    fi
   
    if [ ! -f ${BUILD}/udig-${VERSION}-sdk.zip ]
    then
        echo "Building  ${BUILD}/udig-${VERSION}-sdk.zip ..."
        
        echo "Extracting ${SDK_TARGET}/${SDK_FILE}"
        BUILD_SDK="${BUILD}/sdk/udig_sdk"
        unzip -q -d ${BUILD_SDK} ${SDK_TARGET}/${SDK_FILE}
        
        rm ${BUILD_SDK}/plugins/*swt*macosx*
        rm ${BUILD_SDK}/plugins/*swt*win32*
        rm ${BUILD_SDK}/plugins/*swt*linux*
        
        # features have to be unpacked, maybe tycho can do this?
        for FILE in ${BUILD_SDK}/features/*.jar
        do
            BASENAME=$(basename "${FILE}" .jar)
            
            if [ ! -d "${BASENAME}" ]; then
                unzip -d "${BUILD_SDK}/features/${BASENAME}" "${FILE}" && rm "${FILE}"
            fi
        done
        
        cp ${BASE}/udig-1.3.x.html ${BUILD_SDK}/udig-${VERSION}.html
        
        echo "Assemble ${BUILD}/udig-${VERSION}-sdk.zip "
        cd ${BUILD}/sdk
        zip -9 -r -q ../udig-${VERSION}-sdk.zip udig_sdk
     else 
       echo "Already Exists ${BUILD}/udig-${VERSION}-sdk.zip"
     fi
fi
