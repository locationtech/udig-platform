#!/bin/bash
echo "Release Linux 64"
source ./support_functions.sh

assemble "linux64" "linux.gtk.x86_64"
