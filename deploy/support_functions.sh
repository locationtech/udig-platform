#!/bin/bash
source ./versions.sh
function assemble() {
    PLATFORM=$1
    EXT=$2
    PLATFORM_JRE=$3

    # Release win32 if available
    if [ -f ${TARGET}/udig-${VERSION}.${EXT}.zip ] 
    then
        echo "Releasing ${PLATFORM}"

        if [ ! -d ${BUILD}/${PLATFORM}/udig ] 
        then
           echo "Creating ${BUILD}/${PLATFORM}/udig"
           mkdir -p ${BUILD}/${PLATFORM}/udig
        fi

        if [ ! -f ${BUILD}/udig-${VERSION}.${EXT}.zip ]
        then
            echo "Building ${BUILD}/udig-${VERSION}.${EXT}.zip ..."

            echo "Extracting ${TARGET}/udig-${VERSION}.${EXT}.zip"
            unzip -q -d ${BUILD}/${PLATFORM} ${TARGET}/udig-${VERSION}.${EXT}.zip

            echo "Preparing ${BUILD}/${PLATFORM}"

            extract_jre

            prepare_resources

            echo "Assemble ${BUILD}/udig-${VERSION}.${EXT}.zip"
            cd ${BUILD}/${PLATFORM}
            zip -9 -r -q ../udig-${VERSION}.${EXT}.zip udig
         else 
           echo "Already Exists ${BUILD}/udig-${VERSION}.${EXT}.zip"
         fi
    fi
    
}

function prepare_resources () {

    for opt in `find ../plugins/ -name .options` ; do cat $opt >> ${BUILD}/${PLATFORM}/udig/debug-options ; done
    cp ${BASE}/udig-1.2.x.html ${BUILD}/${PLATFORM}/udig/udig-${VERSION}.html
    cat ../plugins/net.refractions.udig.libs/.options >> ${BUILD}/${PLATFORM}/udig/.options
    if [[ $PLATFORM == linux* ]] ; then
        cp udig.sh ${BUILD}/${PLATFORM}/udig
        cp udig-clean.sh ${BUILD}/${PLATFORM}/udig
        cp udig-debug.sh ${BUILD}/${PLATFORM}/udig        
    fi
    if [[ $PLATFORM == win* ]] ; then
        cp *.bat ${BUILD}/${PLATFORM}/udig
        mkdir -p ${BUILD}/${PLATFORM}/udig/icons
        cp installer/*.ico ${BUILD}/${PLATFORM}/udig/icons/
        windows_installer
    fi
    if [[ $PLATFORM == mac* ]] ; then
        mv ${BUILD}/${PLATFORM}/udig/udig_internal.app ${BUILD}/${PLATFORM}/udig/udig.app
        cp mac-udig-clean.sh ${BUILD}/${PLATFORM}/udig/udig-clean.sh
        cp mac-udig-debug.sh ${BUILD}/${PLATFORM}/udig/udig-debug.sh
        mv ${BUILD}/${PLATFORM}/udig/.options ${BUILD}/${PLATFORM}/udig/udig.app/Contents/MacOS/
    fi
}

function windows_installer () {
    MAKENSIS=`which makensis`
    if [ $? == 0 ] ; then
        HERE=`pwd`
        # todo use sed or something to update VERSION inside uDigInstallScript.nsi
        
        cp ${INSTALLER}/32-uDigIcon.ico ${BUILD}/${PLATFORM}
        cp ${INSTALLER}/ECWEULA.txt ${BUILD}/${PLATFORM}
        cp ${INSTALLER}/LICENSE.txt ${BUILD}/${PLATFORM}
        sed -e "s/VersionXXXX/${VERSION}/g" ${INSTALLER}/uDigInstallScript.nsi > ${BUILD}/${PLATFORM}/uDigInstallScript.nsi
        cp ${INSTALLER}/32-uninstallIcon.ico ${BUILD}/${PLATFORM}
        cp ${INSTALLER}/LGPL.txt ${BUILD}/${PLATFORM}
        cp ${INSTALLER}/udig/icons ${BUILD}/${PLATFORM}/icons
        
        cd ${BUILD}/${PLATFORM}
        makensis "-NOCD" uDigInstallScript.nsi
        cd ${HERE}
        cp ${BUILD}/${PLATFORM}/udig-*.exe ${BUILD}/udig-${VERSION}.${EXT}.exe
    else
        WINE=`which wine`
        if [ $? == 0 ] ; then
            cp installer/* ${BUILD}/${PLATFORM}/
            HERE=`pwd`
            if [ ! -f "${TOOL}" ] ; then
                TOOL=`find $HOME/.wine -name makensis.exe|sed -n 1p`
            fi
    
            if [ -f "${TOOL}" ] ; then
                cd ${BUILD}/${PLATFORM}
                $WINE "${TOOL}" "/NOCD" uDigInstallScript.nsi
                cd ${HERE}
                cp ${BUILD}/${PLATFORM}/udig-*.exe ${BUILD}/udig-${VERSION}.${EXT}.exe
            else
                echo "makensisw.exe cannot be found"
            fi
        else 
            echo "wine is not installed so creating windows installer"
        fi
    fi
}

function extract_jre () {

    if [[ $PLATFORM == linux* ]] ; then
        if [ -f ${JRE}/${PLATFORM_JRE}.tar.gz ] ; then
            echo "Extracting ${JRE}/${PLATFORM_JRE}.tar.gz"
            tar xzf ${JRE}/${PLATFORM_JRE}.tar.gz -C ${BUILD}/${PLATFORM}/udig
        fi
    else
        if [ -f ${JRE}/${PLATFORM_JRE}.zip ] ; then
            echo "Extracting ${JRE}/${PLATFORM_JRE}.zip ..."
            unzip -q -d ${BUILD}/${PLATFORM}/udig ${JRE}/${PLATFORM_JRE}.zip
        fi
    fi
}