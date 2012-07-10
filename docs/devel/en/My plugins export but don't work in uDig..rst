My plugins export but don't work in uDig.
=========================================

Q: My plugins export but don't work in uDig. What is going on?

A:
 The common suspects are:

#. The plugin depends on another plugin that is not part of the uDig your plugin is installed in. If
   a plugin depends on a missing plugin then that plugin will be deactivated
#. Make sure that all the required resources are checked off in th build.properties editor. The
   build is the important one for running and exporting
#. If you made a plugin that contains code as well as other jars then on the runtime tab of the
   Manifest.MF editor you must make sure that the all the extra jars **and** a . are in the
   Classpath list. (The period is intensional as it indicates the code of this plugin).

   -  This last point is important only if the build.properties Runtime Information maps . to your
      source directory. If the mapping is to a jar then make sure that jar is in the classpath of
      the manifest.

#. The exported plugin and the current udig build were compiled with different jdk. -debug
   -consoleLog as program arguments help to keep track of such errors.

