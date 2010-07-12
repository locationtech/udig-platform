#!/bin/bash
echo "Release Versions"

# ignore mac resources when using tar,zip,etc...
#
export COPYFILE_DISABLE=true

# Release Configuration
export TARGET=1.2.0
export VERSION=1.2-SNAPSHOT
export BUILD=build

# Build Resources
export JRE=jre

export JRE_WIN32=jre/jre1.6.0_17.win32_gdal_ecw.zip
export JRE_WIN32_DIR=jre1.6.0_17.win32_gdal_ecw

export JRE_LIN32=jre/jre1.6.0_18.linux32_gdal_ecw_mrsid.tar.gz
export JRE_LIN32_DIR=jre1.6.0_18.linux32_gdal_ecw_mrsid

export JRE_LIN64=jre/jre1.6.0_20.linux.x86_64_gdal_ecw_mrsid.tar.gz
export JRE_LIN64_DIR=jre1.6.0_20.linux.x86_64_gdal_ecw_mrsid

echo "Available for release:"
ls ${TARGET}