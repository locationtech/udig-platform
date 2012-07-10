14 Bundle a JAR up as a Plugin
==============================

To bundle up an existing jar (say a jdbc driver or some java utility class you want to use):

#. Choose File > New > Other from the menu bar to open the New Wizard
#. Choose Plug-in Development > Plug-in from existing JAR archive. And then press Next.
#. Press Add External button and locate your jar. And then press Next.
#. Fill in some description details about this new plugin:

   -  name: make up a name based on the project; this usually matches the package structure of the
      project (which is based on the reverse of the domain name).
   -  version: fill in the correct version; you will end up changing this version as new copies of
      your jar are made available; having the version information in your plugin manifest will allow
      OSGi to make sure you have the version you expect
   -  Unzip the JAR archives into the project: I always uncheck this one (and leave the raw jar file
      in there); if you want you can let eclipse unzip your jar but I have not tried it yet

#. Press Finish; this will create the new plug-in and open the MANIFEST.MF editor
    |image0|

The MANIFEST.MF runtime tab (shown above for the javacvs.jar) has three sections:

-  Exported Packages: these are based on the packages available in your jar (you can choose which
   ones others are allowed to see). You cannot really fill this in until your .classpath (ie the
   java build environment) is set up correctly
-  Classpath: this lists the jars that are exported out in the .classpath. As you change this list
   your .classpath is recreated (and eclipse will compile for a bit). When it is set up correctly
   you can choose which packages to export.

There are a couple PDE wizards available to manage this stuff (if you right click on your project)

-  update classpath: uses all the MANIFEST.MF stuff to generate a new .classpath (use this if you
   have compile errors or java build path problems)
-  organize manifest: will automatically update the list of exported packages; use this if you
   cannot "see" a class you expect to be able to see

WARNING ABOUT OTHER PLUGIN SYSTEMS
----------------------------------

The OSGi plugin system is based around the concept of bundles and is pretty strict. When making use
of other Java projects you may need to go jump through some hoops to get them to work.

-  Java projects using the Service Provider Interface(SPI) plugin system (like the geotools project)
   depend on the ability to see the manifest/services folder; something the OSGi classloader does
   not let them do (since it is not in the list of exported packages!). As such you may need to
   unpack these jars; or place them all into a single folder
-  Spring projects using the Spring Plugin System depend on you setting up a context that wires up
   all the Java Beans being used. Many of the traditional tricks that ask Spring to look for
   implementations of a specific interface on the classpath are going to fail in an OSGi managed
   environment - the good news is you can still do it by hand; and that the Spring project is
   looking at OSGi integration in the future

.. |image0| image:: /images/14_bundle_a_jar_up_as_a_plugin/jar.png
