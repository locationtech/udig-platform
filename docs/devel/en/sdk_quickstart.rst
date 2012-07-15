Sdk Quickstart
##############

SDK Quickstart Tutorial
-----------------------

This quickstart is aimed at those looking at doing plugin-development against the UDIG platform.
Follow these instructions to quickly set up a development environment for working on your own
plug-ins.

`|image0| <http://udig.refractions.net/tutorials/SDKQuickstart.pdf>`_

This content is available as a workbook for uDig 1.2 developers:

* `SDKQuickstart.pdf <http://udig.refractions.net/tutorials/SDKQuickstart.pdf>`_
-  `SDKQuickstart.odt <http://svn.refractions.net/udig/docs/trunk/devel/SDKQuickstart.odt>`_ (live
   from version control)

The SDK these instructions were tested uDig 1.2:

* `http://udig.refractions.net/files/downloads/udig-1.2.2-sdk.zip <http://udig.refractions.net/files/downloads/udig-1.2.2-sdk.zip>`_
* `http://udig.refractions.net/files/downloads/udig-1.2-RC2-sdk.zip <http://udig.refractions.net/files/downloads/udig-1.2-RC2-sdk.zip>`_
   (tested!)

The document provides links to the version of eclipse etc used ... in general it is the latest one
we have tested with.

Introduction
~~~~~~~~~~~~

This workbook is aimed at those doing plug-in development against the uDig platform. Follow these
instructions to quickly set up a development environment for working on your own plug-ins. Eclipse
is familiar to most developers as a Java Integrated Development Environment (IDE). The Eclipse IDE
can be extended with additional "capabilities" to work with alternate programming languages (like
C++ or Ruby), or additional subject matter such as Java Enterprise Edition or in this case Eclipse
Plug-in development.

In this Quickstart we are going to use the the Eclipse Plug-in Development capability; with the uDIG
SDK as the target platform. This workbook covers setting up a development environment for working on
your own plug-ins.

If you have an existing Eclipse installation please do not skip this tutorial - we are going to very
carefully set up a copy of Eclipse with a few more additional tools then you are perhaps used to.

The Eclipse developers themselves make use of the Plug-in Development Environment (PDE) day in and
day out - so it has gotten a lot of polish over the years. In some respects it is more polished then
the Java development environment (with custom editors for all kinds of little files).

Some of the terminology used when working with the PDE:

-  Everything is a Plug-in
-  A plug-in can implement an extension
-  A plug-in can provide an extension-point for others

Please keep these ideas in mind - even an "Application" is considered an extension when working with
the Eclipse Platform.

.. figure:: /images/sdk_quickstart/uDigApplicationAsPlugin.jpg
   :align: center
   :alt: 

What to Do Next
~~~~~~~~~~~~~~~

The step by step instructions are only half of the story; here some additional things to try when
running uDig.

Run Configuration
^^^^^^^^^^^^^^^^^

From Eclipse open up Run > Run Configurations to examine or customize configuration of uDig you are
running. Many of these fields were filled in for you by the udig.product.

.. figure:: /images/sdk_quickstart/RunConfiguration.png
   :align: center
   :alt: 

Run Configuration - Program Arguments
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The number one tip is to go to the Arguments tab and enable console logging.

-  To send log information to the console as udig runs add -consolelog to your "program arguments".
-  You can also review the VM arguments; including changing the amount of memory available to your
   uDig application -Xmx512m may be useful when working with large images?
-  The -Dosgi.parentClassload=ext setting allows uDig to find JRE extensions such as Java Advanced
   Imaging and ImageIO.

Run Configuration - Workspace Data Clean
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The running uDig application makes use of the "Workspace Data" folder defined in the Run dialog. Try
checking clear and workspace in order to simulate starting uDig from a fresh install.

Run Configuration - Plugins
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Have a look at the plug-ins tab and see if you can turn off: printing support.

Run Configuration - Tracing
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Have a look on the Tracing tab of the Run dialog; you can control the amount of logging information
produced.

FindBugs (Advanced)
^^^^^^^^^^^^^^^^^^^

When working on the uDig project itself we use the FindBugs tool to check for obvious mistakes prior
to committing.

You can add FindBugz to your environment using the following update site:
`http://findbugs.cs.umd.edu/eclipse <http://findbugs.cs.umd.edu/eclipse>`_

Tips, Ticks and Suggestions
~~~~~~~~~~~~~~~~~~~~~~~~~~~

The following tips, tricks and suggestions have been collected from the udig-devel email list. If
you have any questions feel free to drop by and introduce yourself.

ClassNotFoundException at EclipseStarter
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If your uDig application fails to load due to a ClassNotFoundException at
org.eclipse.core.runtime.adaptor.EclipseStarter then we have a problem
 with the plugin dependencies. The EclipseStarter is doing its best to load the UDIGApplication;
however the UDIGApplication is not available as the Platform refused to load the
net.refractions.udig.ui plugin as some of the dependencies were not available.

This usually happens each time we update the version of Eclipse we use. Each version of eclipse
changes the plugins required; requiring us to review and examine the plugins we include in our SDK.

As a temporary measure:
 1. Open up your Run Configuration
 2. Navigate to the Plugins tab
 3. Hit "Verify Plugins" (to list the plugins that failed to load; you should see that
net.refractions.udig.ui is in this list)
 4. Hit add required plugins

Please email the udig-devel list; and volunteer to test the SDK with the version of eclipse you are
using.

NoClassDefFoundError JAI
^^^^^^^^^^^^^^^^^^^^^^^^

The class JAI is provided as part of the custom **jre** you downloaded. In order for uDig to see
this class it needs to be run using the "ext" classpath. This information is part of the
udig.product file you run during the SDK Quickstart.

For reference here is the command line option it sets: -Dosgi.parentClassloader:ext

Linux
^^^^^

Please follow the same procedure; there is a "prepackaged" JRE available for you in our
`http://udig.refractions.net/downloads/jre/ <http://udig.refractions.net/downloads/jre/>`_ folder.

Please don't do anything tricky like trying to "app get" a copy of Eclipse and Java; version numbers
are important and we are setting up this environment very carefully.

If you would like to patch up your system Java you can do so by installing the versions of JAI and
ImageIO mentioned above.

Mac OS-X
^^^^^^^^

The Java included with your operating system is "good enough" for now - JAI is already installed on
Tiger and Leopard. Some raster formats may not work out.

How to build uDig from Source Code
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you are interested in taking part on trunk development please consider `these instructions for
checking out and building
uDig <http://udig.refractions.net/confluence/display/ADMIN/02+Development+Environment>`_.

SDK Quickstart for uDig 1.1.0
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This content is available as a workbook for uDig 1.1 developers:

* `http://udig.refractions.net/tutorials/SDKQuickstart111.pdf <http://udig.refractions.net/tutorials/SDKQuickstart111.pdf>`_

This document above is written for setting up on a windows machine; please see the notes below for
Linux and OSX advice.

UDIG Software Developers Kit:

`http://udig.refractions.net/files/downloads/udig-1.1.1-sdk.zip <http://udig.refractions.net/files/downloads/udig-1.1.1-sdk.zip>`_

Eclipse:
 Windows:
`eclipse-rcp-europa-winter-win32.zip <http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/europa/winter/eclipse-rcp-europa-winter-win32.zip>`_
 Linux:
`eclipse-rcp-europa-winter-linux-gtk.tar.gz <http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/europa/winter/eclipse-rcp-europa-winter-linux-gtk.tar.gz>`_
 MacOSX:
`eclipse-rcp-europa-winter-macosx-carbon.tar.gz <http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/europa/winter/eclipse-rcp-europa-winter-macosx-carbon.tar.gz>`_

Extras:

`http://udig.refractions.net/files/downloads/extras/extras-3.3.2.zip <http://udig.refractions.net/files/downloads/extras/extras-3.3.2.zip>`_

Java:

`http://udig.refractions.net/files/downloads/jre/jre1.6.0\_06.win32.zip <http://udig.refractions.net/files/downloads/jre/jre1.6.0_06.win32.zip>`_

JVM Terminated with Exit Code=-1
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you have a really old machine with lots of versions of Java installed you may be in trouble! If
you start eclipse and big dialog saying **JVM Terminated with Exit Code=-1** then add the following
to your command line options:

-  -vm C:\\java\\eclipse\\jre\\bin\\javaw.exe

This will force eclipse.exe to use the jre you downloaded.

.. |image0| image:: /images/sdk_quickstart/2EclipseRCP.png
