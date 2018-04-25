#!/bin/bash
echo "Release Win 32"
source ./support_functions.sh

assemble "win32" "win32.win32.x86"
