#!/bin/bash
source ./versions.sh
function assemble() {
    PLATFORM=$1
    EXT=$2
    PLATFORM_JRE=$3
    
    echo "Looking for ${PRODUCT_TARGET}/net.refractions.udig-product-${EXT}.zip"
    
    # Release win32 if available
    if [ -f ${PRODUCT_TARGET}/net.refractions.udig-product-${EXT}.zip ] 
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

            echo "Extracting ${PRODUCT_TARGET}/net.refractions.udig-product-${EXT}.zip"
            unzip -q -d ${BUILD}/${PLATFORM}/udig ${PRODUCT_TARGET}/net.refractions.udig-product-${EXT}.zip

            echo "Preparing ${BUILD}/${PLATFORM} with ${JRE}/${PLATFORM_JRE}"

            extract_jre
            
            echo "Preparing ${BUILD}/${PLATFORM} with start up scripts and html files"
            prepare_resources

            echo "Assemble ${BUILD}/udig-${VERSION}.${EXT}.zip"
            cd ${BUILD}/${PLATFORM}
            zip -9 -r -q ../udig-${VERSION}.${EXT}.zip udig
        else 
           echo "Already Exists ${BUILD}/udig-${VERSION}.${EXT}.zip"
        fi
    else 
        echo "Unable to locate ${PRODUCT_TARGET}/net.refractions.udig-product-${EXT}.zip"
        echo
        echo "Available for release in net.refractions.udig_sdk-feature:"
        ls ${PRODUCT_TARGET}/*.zip | xargs -n1 basename
        echo
        echo "To generate use: mvn install -Pproduct"
    fi
    
}

function prepare_resources () {

    for opt in `find ../plugins/ -name .options` ; do cat $opt >> ${BUILD}/${PLATFORM}/udig/debug-options ; done
    
    sed -e "s/VersionXXXX/${VERSION}/g;s/SeriesXXXX/${SERIES}/g" ${BASE}/udig.html > ${BUILD}/${PLATFORM}/udig/udig-${VERSION}.html
    cp ${BASE}/epl-v10.html ${BUILD}/${PLATFORM}/udig
    cp ${BASE}/bsd3-v10.html ${BUILD}/${PLATFORM}/udig
    cp ${BASE}/notice.html ${BUILD}/${PLATFORM}/udig
    
    cat ../plugins/net.refractions.udig.libs/.options >> ${BUILD}/${PLATFORM}/udig/.options
    mkdir ${BUILD}/${PLATFORM}/udig/dropins
    
    if [ ! -f ${BUILD}/udig-${VERSION}.html ]
    then
        sed -e "s/VersionXXXX/${VERSION}/g;s/SeriesXXXX/${SERIES}/g" ${BASE}/udig.html > ${BUILD}/udig-${VERSION}.html
    fi
         
    if [[ $PLATFORM == linux* ]] ; then
        cp udig.sh "${BUILD}/${PLATFORM}/udig"
        chmod 755 "${BUILD}/${PLATFORM}/udig/udig_internal"
        cp udig-clean.sh "${BUILD}/${PLATFORM}/udig"
        cp udig-debug.sh "${BUILD}/${PLATFORM}/udig"
    fi
    if [[ $PLATFORM == win* ]] ; then
        cp *.bat ${BUILD}/${PLATFORM}/udig
        mkdir -p ${BUILD}/${PLATFORM}/udig/icons
        cp installer/*.ico ${BUILD}/${PLATFORM}/udig/icons/
        rm ${BUILD}/${PLATFORM}/udig/eclipsec.exe
        windows_installer
    fi
    if [[ $PLATFORM == mac* ]] ; then
        HERE=`pwd`
    	PLATFORMCONTENT="${HERE}/${BUILD}/${PLATFORM}"

        chmod 755 "${PLATFORMCONTENT}/udig/udig_internal.app/Contents/MacOS/udig_internal"
        mv "${PLATFORMCONTENT}/udig/udig_internal.app" "${PLATFORMCONTENT}/udig/udig.app"
        
        # add in -data ~/udig-workspace to Info.plist
        sed -e "s/\<\string\>-showlocation\<\/string\>/<\string\>-showlocation\<\/string\>\<string\>-data<\/string>\\<string\>~\/udig-workspace\<\/string\>/g" "${PLATFORMCONTENT}/udig/udig.app/Contents/Info.plist" > "${PLATFORMCONTENT}/udig/udig.app/Contents/Info2.plist"
        rm "${PLATFORMCONTENT}/udig/udig.app/Contents/Info.plist"
        mv "${PLATFORMCONTENT}/udig/udig.app/Contents/Info2.plist" "${PLATFORMCONTENT}/udig/udig.app/Contents/Info.plist"
        
        rm "${PLATFORMCONTENT}/udig/udig_internal"
        ln -s "${PLATFORMCONTENT}/udig/udig.app/Contents/MacOS/udig_internal" "${PLATFORMCONTENT}/udig/udig"
        cp "${HERE}/mac-udig-clean.sh" "${PLATFORMCONTENT}/udig/udig-clean.sh"
        cp "${HERE}/mac-udig-debug.sh" "${PLATFORMCONTENT}/udig/udig-debug.sh"
        mv "${PLATFORMCONTENT}/udig/.options" "${PLATFORMCONTENT}/udig/udig.app/Contents/MacOS/"
        
        make_dmg
    fi
}

function windows_installer () {
    MAKENSIS=`which makensis`
    if [ $? == 0 ] ; then
        HERE=`pwd`
        # todo use sed or something to update VERSION inside uDigInstallScript.nsi
        
        cp ${INSTALLER}/* ${BUILD}/${PLATFORM}
        cp ${INSTALLER}/*.txt ${BUILD}/${PLATFORM}
        cp ${INSTALLER}/udig/icons ${BUILD}/${PLATFORM}/icons
        
        sed -e "s/VersionXXXX/${VERSION}/g" ${INSTALLER}/uDigInstallScript.nsi > ${BUILD}/${PLATFORM}/uDigInstallScript.nsi
        
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
            echo "wine is not installed so not creating windows installer"
        fi
    fi
}


function make_dmg () {
    echo -n "Mac packaging utilities available... "
	DMGTOOLS=`which hdiutil`
    if [ $? == 0 ] ; then
		echo "YES!"
		echo -n "Building MacOS DMG for product"
		hdiutil create -fs HFS+ -volname "udig-${VERSION}" -srcfolder "${PLATFORMCONTENT}" "${PLATFORMCONTENT}/../udig-${VERSION}.${EXT}.dmg"
	else
		echo "NO"
	fi
}


function extract_jre () {

    if [[ $PLATFORM == linux* ]] ; then
        echo "Looking for ${JRE}/${PLATFORM_JRE}.tar.gz"
        if [ -f ${JRE}/${PLATFORM_JRE}.tar.gz ] ; then
            echo "Extracting ${JRE}/${PLATFORM_JRE}.tar.gz"
            tar xzf ${JRE}/${PLATFORM_JRE}.tar.gz -C ${BUILD}/${PLATFORM}/udig
        else
            echo "${JRE}/${PLATFORM_JRE}.tar.gz not found - user will require their own JRE"
            exit
        fi
    else
        echo "Looking for ${JRE}/${PLATFORM_JRE}.zip"
        if [ -f ${JRE}/${PLATFORM_JRE}.zip ] ; then
            echo "Extracting ${JRE}/${PLATFORM_JRE}.zip ..."
            unzip -q -d ${BUILD}/${PLATFORM}/udig ${JRE}/${PLATFORM_JRE}.zip
        else
            echo "${JRE}/${PLATFORM_JRE}.zip not found - user will require their own JRE"
        fi
    fi
}