#!/bin/bash
echo "Release mac osx 64"

source ./support_functions.sh

assemble "mac64" "macosx.cocoa.x86_64"
