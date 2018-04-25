#!/bin/bash
echo "Release Linux 32"
source ./support_functions.sh

assemble "linux32" "linux.gtk.x86"
