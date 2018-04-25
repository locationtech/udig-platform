#!/bin/bash
echo "Release Win 64"
source ./support_functions.sh

assemble "win64" "win32.win32.x86_64"
