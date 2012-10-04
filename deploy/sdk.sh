#!/bin/bash

# ignore mac resources when using tar,zip,etc...
#
export COPYFILE_DISABLE=true
source ./versions.sh
echo "Release SDK ${VERSION}"

# ls ${BASE}/../features/net.refractions.udig_sdk-product/target/udig-1.3.SNAPSHOT-sdk-linux.gtk.x86.zip

#SDK_FILE="udig-${VERSION}-sdk.zip"
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
        rm -rf ${BUILD}/sdk
        mkdir -p ${BUILD}/sdk
    fi
   
    if [ ! -f ${BUILD}/udig-${VERSION}-sdk.zip ]
    then
        echo "Building  ${BUILD}/udig-${VERSION}-sdk.zip ..."
        
        echo "Extracting ${SDK_TARGET}/${SDK_FILE}"
        BUILD_SDK="${BUILD}/sdk/udig_sdk"
        unzip -q -d ${BUILD_SDK} ${SDK_TARGET}/${SDK_FILE}
        
        rm -f ${BUILD_SDK}/plugins/*swt*macosx*
        rm -f ${BUILD_SDK}/plugins/*swt*win32*
        rm -f ${BUILD_SDK}/plugins/*swt*linux*

        # prevent non-file results in loops
        shopt -s nullglob
        
        # features have to be unpacked, maybe tycho can do this?
        for FILE in ${BUILD_SDK}/features/*.jar
        do
            BASENAME=$(basename "${FILE}" .jar)
            
            if [ ! -d "${BASENAME}" ]; then
                unzip -d "${BUILD_SDK}/features/${BASENAME}" "${FILE}" && rm "${FILE}"
            fi
        done
        
        # some plugins have to be unpacked too, maybe tycho can do this?
        # read the plugin manifests for directive Eclipse-BundleShape: dir
        MANIFESTS=$(grep -irl "Eclipse-BundleShape: dir" --include "MANIFEST.MF" --exclude-dir "src" --exclude-dir "bin" --exclude-dir "target" --exclude-dir "lib*" ../plugins)
        for MANIFEST in ${MANIFESTS}; do
            PLUGIN_NAME=$(grep "Bundle-SymbolicName" ${MANIFEST} | sed -e "s/;.*//" -e "s/^.*:\s*//")
            PLUGIN_VERSION=$(grep "Bundle-Version" ${MANIFEST} | sed -e "s/\.qualifier.*//" -e "s/^.*:\s*//")

            for FILE in "${BUILD_SDK}"/plugins/${PLUGIN_NAME}_${PLUGIN_VERSION}*.jar
            do
                BASENAME=$(basename "${FILE}" .jar)
                if [ ! -d "${BASENAME}" ]; then
                    unzip -d "${BUILD_SDK}/plugins/${BASENAME}" "${FILE}" && rm "${FILE}"
                fi
            done
        done

        # reassemble net.refractions.udig.libs.source*.jar, maybe tycho can do this?
        LIBS_SOURCE_JARFILE=$(find "${BUILD_SDK}"/plugins/ -name "net.refractions.udig.libs.source*.jar" | head -1)
        if [ -f "${LIBS_SOURCE_JARFILE}" ]; then
            echo "Extracting ${LIBS_SOURCE_JARFILE}"
            LIBS_SOURCE_BASEDIR="${LIBS_SOURCE_JARFILE%.*}"
            unzip -d "${LIBS_SOURCE_BASEDIR}" "${LIBS_SOURCE_JARFILE}" && rm "${LIBS_SOURCE_JARFILE}"

            LIB_SRC_DIR="lib-src"
            ROOTS="."
            ROOTS_SEPARATOR=","

            for JARFILE in "${LIBS_SOURCE_BASEDIR}"/"${LIB_SRC_DIR}"/*-sources.jar
            do
                JARNAME=${JARFILE##*/}
                JARNAME=${JARNAME%-sources.jar*}
                if [ ! -d "${JARNAME}" ]; then
                    echo "Extracting ${JARFILE}"
                    unzip -d "${LIBS_SOURCE_BASEDIR}/${LIB_SRC_DIR}/${JARNAME}" "${JARFILE}" && rm "${JARFILE}"
                fi
                ROOTS="${ROOTS}${ROOTS_SEPARATOR}${LIB_SRC_DIR}/${JARNAME}"
            done
            
            MANIFESTFILE="${LIBS_SOURCE_BASEDIR}"/META-INF/MANIFEST.MF
            echo "Editing manifest ${MANIFESTFILE}"
            sed -E -n -i '1h;1!H;${;g;s#(Eclipse-SourceBundle: .*;roots\:=\")\."#\1'"${ROOTS}"'\"#g;p;}' "${MANIFESTFILE}"
        
            echo "Reassembling jar ${LIBS_SOURCE_JARFILE}"
            jar Mcvf "${LIBS_SOURCE_JARFILE}" -C "${LIBS_SOURCE_BASEDIR}" .

            echo "Removing ${LIBS_SOURCE_BASEDIR}"
            rm -fr "${LIBS_SOURCE_BASEDIR}"
        else
            echo "Not found net.refractions.udig.libs.source*.jar"
        fi
        
        sed -e "s/VersionXXXX/${VERSION}/g;s/SeriesXXXX/${SERIES}/g" ${BASE}/udig.html > ${BUILD_SDK}/udig-${VERSION}.html
        
        echo "Assemble ${BUILD}/udig-${VERSION}-sdk.zip "
        cd ${BUILD}/sdk
        zip -9 -r -q ../udig-${VERSION}-sdk.zip udig_sdk
    else 
       echo "Already Exists ${BUILD}/udig-${VERSION}-sdk.zip"
    fi
else 
    echo "Unable to locate ${SDK_TARGET}/${SDK_FILE}"
    echo
    echo "Available for release in net.refractions.udig:"
    ls ${SDK_TARGET}/*.zip | xargs -n1 basename
    echo
    echo "To generate use: mvn install -Dall -Psdk"
fi
