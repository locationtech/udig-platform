#!/bin/bash
echo "Release Versions"

# ignore mac resources when using tar,zip,etc...
#
export COPYFILE_DISABLE=true
export BASE=`dirname $0`

# Release Configuration
export TARGET=~/Desktop/target/1.2.0
export VERSION=1.2-SNAPSHOT
export BUILD=~/Desktop/target/build

# Build Resources
export JRE=~/Desktop/target/jre

export JRE_WIN32=jre1.6.0_17.win32_gdal_ecw
export JRE_LIN32=jre1.6.0_18.linux32_gdal_ecw_mrsid
export JRE_LIN64=jre1.6.0_20.linux.x86_64_gdal_ecw_mrsid

echo "Release Version:"
echo "${VERSION}"
echo "Available for release:"
ls ${TARGET}