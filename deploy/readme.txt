uDIG Application
----------------

This directory contains a series of scripts used to package the uDig
application after it has been exported from eclipse.

To use:
1. Export udig.product to a subdirectory 1.2.x/ folder
2. Update versions.sh to reflect the above two settings and the version you are releasing
3. ./clean.sh - to remove the build directory
4. Individual scripts to package:
   ./win32 - package jre, udig.bat
   ./win64 - (pending the availability of an appropriate JRE)
   ./lin32 - package jre, udig.sh
   ./lin64 - package jre, udig.sh
   ./mac64 - renames udig_internal back to udig
   ./sdk - very simple script removes configuration and application
5. Upload to:
   http://udig.refractions.net/files/downloads            for stable numbered releases
   http://udig.refractions.net/files/downloads/branches   for SNAPSHOT and M releases

Installer
=========

Instructions for build the NSIS installer are located in:
- installer/readme.txt

uDigDeploy
==========

Possibly used in generating the update site? Not sure.
