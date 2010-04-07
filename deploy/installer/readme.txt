There is a wiki page which may have more recent instructions:
- http://udig.refractions.net/confluence/display/ADMIN/Create+an+installer

In order to compile the installer script, you'll need to download and install NSIS installer.

This program can be downloaded from here: http://nsis.sourceforge.net/

It shouldn't matter where you install this program.

The install script can be edited with any text editor, and is the file titled uDigInstallScript.nsi

This file and the other files contained in the Installer Tools.zip archive should all lie in the root of the uDIG install 
directory - that is to say, in the same folder that contains the "udig" directory.

To recompile the installer for a new version of uDig, you'll need to do the following:
1. create a fresh archive release of uDig; copy in the jre and gdal_data as a a subfolder to the udig directory
2. Unzip your uDig release (ie the zip file created by the uDig Export Procedure) into deploy/installer/udig
   so that you have the following file structure:
    installer/uDigInstallScript.nsi
    installer/...
    installer/udig/ <-- extracted from your udig release
    installer/udig/.eclipseproduct
    installer/udig/configuration/
    installer/udig/features/
    installer/udig/gdal_data/
    installer/udig/icons/
    installer/udig/imageio-ext-imagereadmt-BSD-LICENSE.txt
    installer/udig/imageio-ext-tiff-BSD-LICENSE.txt
    installer/udig/ImageIO-License.txt
    installer/udig/jre/
    installer/udig/LICENSE.txt
    installer/udig/plugins/
    installer/udig/README.txt
    installer/udig/sun-copyright.txt
    installer/udig/udig.bat
    installer/udig/udig_internal.exe
    installer/udig/udig_internal.ini

3. Next, open up uDigInstallScript.nsi, and edit the parts that state the version of uDig. This includes the following lines: 44,45,50,55,125,163,218,270,303
   (Replace VersionXXXX with whatever version you are working with for example 1.2-M4)

4. use compiler to open the uDigInstallerScript.nsi file and it will compile the .exe in the same directory
7. Hit Test Installer to runt it

