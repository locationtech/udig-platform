This file details the process for creating a new installer for the latest uDIG release,
beginning from the uDIGInstallBase directory.

The install base directly currently contains:

- eclipse-RCP-3.1M4-win32.zip

  This is the zip file that has been extracted into the eclipse directory mentioned below.  This file is
  present simply as a reference to determine exactly which version of the eclipse RCP package is present
  in this install base.  If a new version of the RCP is required, then the install base will need to be
  recreated using the appropriate eclipse RCP version.
  
- LGPL.txt

  This is the license file, and must remain where it is.  The installer reads this file in from its current
  location.
  
- RCP_Extra.zip

  The contents of this archive will have been extracted into the plugins directory (a subdirectory of eclipse)
  
- Soap-lib.zip

  The contents of this archive will have been extracted into the JRE/lib/ext directory (a subdirectory of eclipse)
  
- uDigInstallScript.nsi

  This is the install script.  The only thing that *should* need to be changed in this file is the version
  number of uDIG that is being installed.  The section that needs to be changed is surrounded by 2 :TODO: tags.
  
- README.txt

  This file, detailing exactly what is involved in creating an installer for the latest release of uDIG
  
- eclipse directory

  This is the extracted contents of the RCP file mentioned above.


To create the new installer,

1.  Make a copy of the uDIGInstallBase directory, naming it something like uDIGx.yInstall
2.  Update the uDigInstallScript.nsi file to include the most recent version number of the uDIG application.
3.  Unzip the contents of the latest uDIG zip file into the eclipse directory.  Choose 'Yes to All' if asked
    whether or not to overwrite any files.
4.  Make a copy of this directory, and name it something like uDIGx.yInstallTest
5.  Run uDig.exe out of the directory created in step 4, and make sure that uDIG loads properly.
6.  If uDIG started correctly, close it down, and delete the directory (and contents) created in step 4.
7.  Right click on the uDigInstallScript.nsi file that was updated in step 2, and choose 'Compile NSIS Script'
8.  The uDig installer should be created and named something like uDigx.y.exe.
9.  Copy the file created in step 8 into the appropriate directory on Lion, and onto the uDig ftp site.
