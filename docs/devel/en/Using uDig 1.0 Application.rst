Using uDig 1.0 Application
==========================

There are several aspects to making a custom application:

-  Custom Branding - splash screen, about dialog, icon
-  Custom Application
-  Custom Workbench Advisor - toolbar, menus
-  Custom Perspective - view locations
-  Packaging up into 'Features' for a Product

It should be noted that you can:

-  define your own custom branding while still running the UDIGApplication and Workbench Advisor
-  add custom menus without writing a new Workbench Advisor
-  add new Perspectives at any time

The best source of documentation on these activities is the Eclipse online help.

-  `Building a Rich Client Platform
   application <http://help.eclipse.org/help31/topic/org.eclipse.platform.doc.isv/guide/rcp.htm>`_
-  `Customizing the
   workbench <http://help.eclipse.org/help31/topic/org.eclipse.platform.doc.isv/guide/rcp_advisor.htm>`_
-  `Third party libraries and
   classloading <http://help.eclipse.org/help31/topic/org.eclipse.platform.doc.isv/reference/misc/buddy_loading.html>`_

Sorting out your Classloaders for JAI
-------------------------------------

.. figure:: images/icons/emoticons/forbidden.gif
   :align: center
   :alt: 

**Java Advanced Imaging Bootclasspath**

The uDig application makes use of the JAI JRE extention.
 You need to add the following to your manifest:

::

    Eclipse-BuddyPolicy: ext

This will allow your application to pick up the JRE extentions including the JAI jars and libraries.

The trouble is the Eclipse RCP framework is very conservative about what classes it trusts.

It has defined the following classloaders:

-  (none): Just the classes from your JRE (and anything you depend on)
-  ext: The classes from you JRE **ext** folder (ie your JRE Extentinos aka the BOOTCLASSPATH)
-  (???): The system CLASSPATH environment variable

As you can see each represents more risk then the previous, a user may put anything on the CLASSPATH
environmental variable and break your application. Although less likely they could also install a
JRE extention that would break your will to live.

However we do need to take a little risk to make use of Java Advanced Imaging, so you will need to
do the following.

Manifest-Version: 1.0
 Bundle-ManifestVersion: 2
 Bundle-Name: Ui Plug-in
 Bundle-SymbolicName: za.co.bluesphere.sens.ui; singleton:=true
 Bundle-Version: 1.0.0
 Bundle-Activator: za.co.bluesphere.sens.ui.UiPlugin
 Bundle-Localization: plugin
 **Eclipse-BuddyPolicy: ext**
 Require-Bundle: org.eclipse.ui,
 org.eclipse.core.runtime,
 org.eclipse.ui.intro,
 net.refractions.udig.ui
 Eclipse-AutoStart: true
 Bundle-Vendor: Bluesphere Technologies
 Export-Package: za.co.bluesphere.sens.ui

You may also find documentation that talks about this problem with respect to:

-  bootclass loader command line arguments
-  changes to the application config.ini file
-  changes to the product

Rest assured that after wasting weeks on this problem the above solution is the least amount of
hassle.
