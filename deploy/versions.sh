#!/bin/bash
# ignore mac resources when using tar,zip,etc...
#
export COPYFILE_DISABLE=true
export BASE=`dirname $0`

# Release Configuration
export INSTALLER="${BASE}/installer"
#export TARGET=${BASE}/target
#export TARGET=${BASE}/../features/net.refractions.udig-product/target/products/
export VERSION=1.3.0-SNAPSHOT
export BUILD="${BASE}/build"

# Tycho Build
export PRODUCT_TARGET="${BASE}/../features/net.refractions.udig-product/target/products"

# Tycho SDK Build
export SDK_TARGET="${BASE}/../features/net.refractions.udig_sdk-feature/target"

# net.refractions.udig.libs "qualifier" for SDK (used to update libs source reference
# example: TAG=1.3.2
# example: QUALIFIER=1.3.2.201201031509
export TAG=1.3.1

# Build Resources
export JRE="${BASE}/jre"
export JRE_WIN32=jre1.6.0_25.win32_gdal_ecw
export JRE_WIN64=jre1.6.0.win64
export JRE_LIN32=jre1.6.0_25.lin32_gdal_ecw
export JRE_LIN64=jre1.6.0_25.lin64_gdal_ecw

# echo "Release Version:  ${VERSION}"

# echo "Staged for release in the target directory: ${TARGET}"
# ls ${TARGET}/*.zip

# echo "Staged for release by maven tycho build..."
# ls ${PRODUCT_TARGET}/*.zip

# echo "Available JREs:"
# ls ${JRE}

# The QUALIFIER is based on the time of the build - we will grab the value from the SDK
# (We use this value to ensure the net.refractions.udig.libs source code loads correctly)
# export QUALIFIER=1.3.2.qualifier

# echo "Assigning SDK ${QUALIFIER} qualifier - checking for SDK"
# if [ -f ${PRODUCT_SDK_TARGET}/udig-1.3.SNAPSHOT-sdk-linux.gtk.x86.zip ] 
#then
#    echo "Extracting QUALIFIER from ${PRODUCT_SDK_TARGET}/udig-1.3.SNAPSHOT-sdk-linux.gtk.x86.zip"
#    
#    #     export QUALIFIER=`unzip -l ${TARGET}/udig-${VERSION}-sdk.zip | grep libs_.*MANIFEST.MF | cut -d '_' -f 2 | sed s/.jar//`
#    export QUALIFIER=`unzip -l ${PRODUCT_SDK_TARGET}/udig-1.3.SNAPSHOT-sdk-linux.gtk.x86.zip | grep libs_.*MANIFEST.MF | cut -d '_' -f 3 | cut -d '/' -f 1`
#    echo "Assigned Qualifier is now: ${QUALIFIER}"
#fi