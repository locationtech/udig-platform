echo on
setlocal
cd %~dp0
start javaw -Xbootclasspath/a:%JAVA_HOME%\jre\lib\ext\clibwrapper_jiio.jar;%JAVA_HOME%\jre\lib\ext\jai_codec.jar;%JAVA_HOME%\jre\lib\ext\jai_core.jar;%JAVA_HOME%\jre\lib\ext\jai_imageio.jar;%JAVA_HOME%\jre\lib\ext\mlibwrapper_jai.jar -cp startup.jar org.eclipse.core.launcher.Main -application net.refractions.udig.ui.uDig %* 
endlocal