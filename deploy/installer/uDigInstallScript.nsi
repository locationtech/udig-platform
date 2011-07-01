;uDig Null Soft Installer creation file.
;Written by Chris Holmes

;This is an example of a windows installer for uDig.  It was adapted from
;the geoserver nsi file.  There are a few things which I don't know enough
;about uDig to do right, so I will add some comments of suggestions and hints
;through out this file.  I spent a solid day getting everything going right
;for geoserver, and doing this took me less than an hour, and should hopefully
;be sufficient so you don't have to replicate my work.

;Building
;--------
;First thing you'll need is NSIS, the null soft installer.  I think the site
;is http://nsis.sourceforge.net.  You'll need a windows box to run the 
;installer.  It's easy to use, just install, and then run the 'compiler'.
;It compiles the fun little scripting language contained in this file
;to create the .exe file.  I used the modern ui stuff, as it seems to look
;nice and, well, modern.  
;After installing the nsis software what I did was unzip the eclipse rcp,
;unzip uDig on top of that, and copy this file (uDig.nsis) to the directory
;where I unzip eclipse, right next to the eclipse/ directory, where the 
;eclipse dir contains everything.  Then with the NSIS compiler open this 
;file and it will compile the .exe in the same directory.  Hit Test Installer
;to run it.  

;You could make an ant task to do this fairly easily, I just never got around
;to it.  Tomcat does it by having the nsis variable, their source trees have
;nsi files for reference (but I could never get them to work, as their build
;involves setting all these random variables
;For more info on this file read the Users Manual, and especially the
;Modern UI Readme.  And check out the other examples, though I imagine this
;one will be the best...

;--------------------------------
;Include Modern UI

  !include "MUI.nsh"

;--------------------------------
;General

  ;Name and file
  ; The following is changed by win32.sh
  Name "uDig VersionXXXX"
  OutFile "udig-VersionXXXX.exe"
  ;:TODO: End of changes required when upgrading installer to new version of uDIG.


  ;Default installation folder
  InstallDir "$PROGRAMFILES\uDig\VersionXXXX"
  
  ;Get installation folder from registry if available - This will check the registry to see if an install directory
  ;is present, and if so, replace the value in InstallDir with it.  If there is no value, the installer will fall
  ;back on InstallDir as the default install directory.
  InstallDirRegKey HKCU "Software\VersionXXXX" ""

;--------------------------------
;Variables

  Var MUI_TEMP
  Var STARTMENU_FOLDER

;--------------------------------
;User-defined macros to allow us to put a link to the uDig help website.
!macro CreateInternetShortcut FILENAME URL ICONFILE ICONINDEX
    WriteINIStr "${FILENAME}.url" "InternetShortcut" "URL" "${URL}"
    WriteINIStr "${FILENAME}.url" "InternetShortcut" "IconFile" "${ICONFILE}"
    WriteINIStr "${FILENAME}.url" "InternetShortcut" "IconIndex" "${ICONINDEX}"
!macroend

;--------------------------------
;Interface Settings


  ;Used your udig.ico.  All paths are relative to the location of _this_ file,
  ;which is why it needs to be right next to the eclipse folder. -ch
  
  !define MUI_ICON "udig\icons\32-uDigIcon.ico"
  ;I tried to use the same windows uninstaller I did, but NSIS doesn't seem
  ;to like icons of different sizes -ch

;  !define MUI_UNICON "udig\plugins\net.refractions.udig.ui_0.3.0\icons\udig.ico"

  ;!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\win-uninstall.ico"
  !define MUI_UNICON "udig\icons\32-uninstallIcon.ico"
  
  !define MUI_ABORTWARNING
  
  ;You can obviously change any of this text junk. -ch
  !define MUI_WELCOMEPAGE_TEXT "This wizard will guide you through the \
      installation of uDig \r\n \r\n\
      Please report any problems or suggestions for improvement to \
      udig-devel@lists.refractions.net. \r\n \r\n \
      Click Next to continue."
;--------------------------------
;Pages

  !insertmacro MUI_PAGE_WELCOME
  ;custom page I made to detect java.  I have it repeat the java version that
  ;it found.  I like the functionality, unfortunately it looks ghetto, as I 
  ;just did a pop up box.  See my comments on the echoJava function -ch
  ;
  ;Chris included this, but we do not require a JDK of any sort, so it is commented
  ;out for now.
  ;Page custom echoJava
  
  ; ---------------------------------------------------------------------------
  ; At this point, we should also determine the location of the JRE, and what
  ; version we are dealing with.  We should also make sure that JAI and ImageIO
  ; are installed.
  ; ---------------------------------------------------------------------------

  ;A text file for the license here would be better.  And it probably should
  ;be your license text, as aren't you doing lgpl instead of the eclipse one?
  ;You should explain something here, have the license as users install it. -ch
  !insertmacro MUI_PAGE_LICENSE "LICENSE.txt"
  
  ;Add a license for ECW?
  !insertmacro MUI_PAGE_LICENSE "ECWEULA.txt"
  
  !insertmacro MUI_PAGE_DIRECTORY

  ;Not sure about this stuff, some registery storing of preferences as to where
  ;you like the uDig start menu
  ;Start Menu Folder Page Configuration
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\uDigVersionXXXX" 
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"
  
  !insertmacro MUI_PAGE_STARTMENU Application $STARTMENU_FOLDER
  
  !insertmacro MUI_PAGE_INSTFILES

  !insertmacro MUI_PAGE_FINISH
  
  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  !insertmacro MUI_UNPAGE_FINISH

;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

  RequestExecutionLevel admin
            
;--------------------------------
;Installer Sections

Section "uDig Section" SecuDig


  SetOutPath "$INSTDIR"
  
  ;This is where the files to add are.  You could change this to run in
  ;the eclipse folder, and name all the files and directories individually,
  ;which will then install them all directly in the uDig folder.  As it is
  ;uDig is installed in Program Files, with eclipse as a sub folder, and then
  ;it seems to build a bin/ directory in the uDig folder as well. -ch
  ;ADD YOUR OWN FILES HERE...
  File /r udig
    
  ;Store installation folderh
  WriteRegStr HKCU "Software\uDigVersionXXXX" "" $INSTDIR
  
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"
  
  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
    ;Create shortcuts
    
    ;You guys don't need this, as you just run the .exe.  I call geoserver with
    ;java directly, so the findJavaPath gets which one I should use.  Though
    ;actually I prefer this method because you can specify JAVA_HOME instead
    ;of changing your registry around. -ch
    ;Call findJavaPath
    ;Pop $2


    CreateDirectory "$SMPROGRAMS\$STARTMENU_FOLDER"
    SetOutPath "$INSTDIR"

    ;Link for documentation
    !insertmacro CreateInternetShortcut \
        "$SMPROGRAMS\$STARTMENU_FOLDER\uDig Documentation" \
        "http://udig.refractions.net/users" \
        "$INSTDIR\udig\icons\32-uDigIcon.ico" 0

    ;Set specific out page for uDig
    ;SetOutPath "$INSTDIR\UDIG\udig"
    ;SetOutPath "$PROFILE\UDIG\workspace"

    ;Start-up, using the udig.exe file
    ;For some reason, uDig will NOT start if it doesn't have a parameter following -data.
    ;-noop does nothing and seems to be okay.

    Call GetWindowsVersion
    Pop $R0
    

	; I don't know the syntax well enough to
	; do if (!Vista && $DOCUMENTS/uDig/.metadata/.log exists) then 
	; rename $DOCUMENTS/uDig/ to %HOMEDRIVE%%HOMEPATH%/uDig/
	;
	; instead I'm doing a ifs inside of ifs.
    StrCmp $R0 "Vista" DONE OTHER
      OTHER:
        IfFileExists $DOCUMENTS\uDig\.metadata\.log OLD_WS DONE
    
        OLD_WS:
          IfFileExists %HOMEDRIVE%%HOMEPATH%\uDig\.metadata\.log DONE MOVE
            MOVE:
	      Rename "$DOCUMENTS\uDig" "%HOMEDRIVE%%HOMEPATH%\uDig"


     DONE:
    SetOutPath "$INSTDIR\udig\"
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\uDig.lnk" \
                   "$INSTDIR\udig\udig.bat" "-data $\"%HOMEDRIVE%%HOMEPATH%\uDig\$\" -configuration $\"%APPDATA%\udig\VersionXXXX\$\" -vm $\"$INSTDIR\udig\jre\bin\javaw.exe$\"" \
                   "$INSTDIR\udig\icons\32-uDigIcon.ico" 0 SW_SHOWNORMAL

    ;Set path back to normal
    SetOutPath "$INSTDIR"
    ;Commented out the stop, but it shows how you call with java.

    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\Uninstall.lnk" \
                   "$INSTDIR\Uninstall.exe" "" \
                   "$INSTDIR\udig\icons\32-uninstallIcon.ico" 0 SW_SHOWNORMAL

  
  !insertmacro MUI_STARTMENU_WRITE_END

SectionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_uDig ${LANG_ENGLISH} "uDig section"

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecuDig} $(DESC_uDig)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END


Function .onInit

   ClearErrors

FunctionEnd

;--------------------------------
;Uninstaller Section

Section "Uninstall"

  
  ;REMOVE APPLICATION IN A FEW STEPS TO SHOW PROGRESS
  
  RMDIR /r "$INSTDIR\udig\features"
  RMDIR /r "$INSTDIR\udig\jre"
  RMDIR /r "$INSTDIR\udig\plugins"

  RMDIR /r "$INSTDIR"
  ;TRY TO REMOVE THE Program Files\uDig Directory... 
  ;IF THERE ARE OTHER UDIG VERSION THEN THIS WILL FILE SILENTLY
  RMDIR "$INSTDIR\.."

  ;REMOVE THE CONFIGURATION DATA 
  RMDIR /r "$APPDATA\uDig\uDigVersionXXXX"
  ;WILL REMOVE IF THERE ARE NO MORE UDIG INSTALLS
  RMDIR "$APPDATA\uDig"
  
; FOR XP THE WORKSPACE IS UP ONE DIRECTORY FROM APPDATA
; CHECK IF IT EXISTS.  IF IT DOES QUERY USER TO DELETE IT
  IfFileExists "$APPDATA\..\uDig" 0 Removed
     MessageBox MB_YESNO|MB_ICONQUESTION \
          "Delete all files in your workspace? (If you have anything you created that you want to keep, click No)" /SD IDYES IDNO Removed
     RMDIR /r "$APPDATA\..\uDig"
     Sleep 500
     IfFileExists "$APPDATA\..\uDig" 0 Removed
        MessageBox MB_OK|MB_ICONEXCLAMATION \
            "Note: Workspace could not be removed." /SD IDOK

; FOR VISTA THE WORKSPACE IS UP TWO DIRECTORIES FROM APPDATA
; CHECK IF IT EXISTS.  IF IT DOES QUERY USER TO DELETE IT
  IfFileExists "$APPDATA\..\..\uDig" 0 Removed
     MessageBox MB_YESNO|MB_ICONQUESTION \
          "Delete all files in your workspace? (If you have anything you created that you want to keep, click No)" /SD IDYES IDNO Removed
     RMDIR /r "$APPDATA\..\..\uDig"
     Sleep 500
     IfFileExists "$APPDATA\..\..\uDig" 0 Removed
        MessageBox MB_OK|MB_ICONEXCLAMATION \
            "Note: Workspace could not be removed." /SD IDOK


  Removed:

  !insertmacro MUI_STARTMENU_GETFOLDER Application $MUI_TEMP
    
  RMDIR /r "$SMPROGRAMS\$MUI_TEMP"

  DeleteRegKey /ifempty HKCU "Software\uDigVersionXXXX"

SectionEnd

; GetWindowsVersion
 ;
 ; Based on Yazno's function, http://yazno.tripod.com/powerpimpit/
 ; Updated by Joost Verburg
 ;
 ; Returns on top of stack
 ;
 ; Windows Version (95, 98, ME, NT x.x, 2000, XP, 2003, Vista)
 ; or
 ; '' (Unknown Windows Version)
 ;
 ; Usage:
 ;   Call GetWindowsVersion
 ;   Pop $R0
 ;   ; at this point $R0 is "NT 4.0" or whatnot

 Function GetWindowsVersion

   Push $R0
   Push $R1

   ClearErrors

   ReadRegStr $R0 HKLM \
   "SOFTWARE\Microsoft\Windows NT\CurrentVersion" CurrentVersion

   IfErrors 0 lbl_winnt

   ; we are not NT
   ReadRegStr $R0 HKLM \
   "SOFTWARE\Microsoft\Windows\CurrentVersion" VersionNumber

   StrCpy $R1 $R0 1
   StrCmp $R1 '4' 0 lbl_error

   StrCpy $R1 $R0 3

   StrCmp $R1 '4.0' lbl_win32_95
   StrCmp $R1 '4.9' lbl_win32_ME lbl_win32_98

   lbl_win32_95:
     StrCpy $R0 '95'
   Goto lbl_done

   lbl_win32_98:
     StrCpy $R0 '98'
   Goto lbl_done

   lbl_win32_ME:
     StrCpy $R0 'ME'
   Goto lbl_done

   lbl_winnt:

   StrCpy $R1 $R0 1

   StrCmp $R1 '3' lbl_winnt_x
   StrCmp $R1 '4' lbl_winnt_x

   StrCpy $R1 $R0 3

   StrCmp $R1 '5.0' lbl_winnt_2000
   StrCmp $R1 '5.1' lbl_winnt_XP
   StrCmp $R1 '5.2' lbl_winnt_2003
   StrCmp $R1 '6.0' lbl_winnt_vista lbl_error

   lbl_winnt_x:
     StrCpy $R0 "NT $R0" 6
   Goto lbl_done

   lbl_winnt_2000:
     Strcpy $R0 '2000'
   Goto lbl_done

   lbl_winnt_XP:
     Strcpy $R0 'XP'
   Goto lbl_done

   lbl_winnt_2003:
     Strcpy $R0 '2003'
   Goto lbl_done

   lbl_winnt_vista:
     Strcpy $R0 'Vista'
   Goto lbl_done

   lbl_error:
     Strcpy $R0 ''
   lbl_done:

   Pop $R1
   Exch $R0

 FunctionEnd
