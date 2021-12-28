Running uDig
############

This page covers some of the options you have when starting up uDig. We also cover a few platform
specific issues.

`The UDIG Application`_

* `Workspace`_

* `Configuration`_

* `Language`_

* `Java`_

* `Got Memory`_

* `Proxy Settings`_

`Windows`_

* `uDig does not work on Windows 2000`_

`Max OS X`_

`Linux`_


* `Fedora 10 XULRunner Library Conflict`_

* `Advanced graphics: to disable or not?`_

* `The ugly cursors (UDIG-785 Cursors on non--windows platforms are mangled)`_ 

* `Info and Help require a "Pure" Mozilla Browser ( UDIG-698: Linux MOZILLA\_FIVE\_HOME required for Information tool )`_

* `About the Internal Browser`_

* `The zoom window is opaque when advanced graphics are disabled`_

* `Can I use my Own Java?`_

* `JRE folder vs JAVA\_HOME`_


The command line options can be set up either:

-  on the short cut used to start uDig
-  by modifying the udig.ini file (useful for macosx users)

The UDIG Application
====================

After you install, or unzip, the uDig application in a directory you can start the application by
running:

-  udig.exe on Windows systems
-  udig on Linux systems

By default the windows install creates the following short cut:

::

    C:\Program Files\uDig\1.1\eclipse\udig.exe"
      -data "%HOMEDRIVE%%HOMEPATH%\uDig\"
      -configuration "%APPDATA%\udig\uDig1.1\"
      -vm "C:\Program Files\uDig\1.1\eclipse\jre\bin\javaw.exe"

Workspace
---------

By default uDig will store your work in your home directory:

-  C:\\Documents and Settings\\User\\My Documents\\udig on Windows XP
-  C:\\Users\\User\\udig on Windows Vista
-  ~user/udig-workspace on Linux

You can change the location of your workspace using a command line option:

::

    udig -data <workspace location>

Preferences
-----------

The uDig application has lots of :doc:`../reference/Preferences` with a sensible default. In case the application should have
a different setup, its possible to pre-configure application using an ``.options`` file placed in installation folder to overwrite
defaults.

This requires to start application with arguments (see ``.ini`` file in root installation directory of uDig)

::

    -pluginCustomization
    .options

.. note::
   Its required to add it **before** ``-vmargs`` otherwise preferences values are not applied correctly.

where the content of the ``.options`` file looks like this, one line per preferences option:

::

    <plugin-id>/<preferences constant>=<value

Related articles:

-  https://www.eclipse.org/articles/preferences/preferences.htm
-  https://gnu-mcu-eclipse.github.io/developer/eclipse/runtime-preferences/



Configuration
-------------

The application configuration is stored; by default in the configuration folder

-  C:\\Program Files\\uDig\\1.1\\configuration

If you want to run from a network share; or a from a CD you can ask the configuration to be stored
elsewhere:

::

    udig -configuration <configuration location>

Storing the configuration directory in the users home directory is a good option when working off a
CD

::

    udig -configuration %USERPROFILE%\.udig

Language
--------

The application will run in your current language by default (assuming we have a translation for
that langauge).

To change the language to English use the following:

::

    udig -nl en

To change the language to German:

::

    udig -nl de

To change the language to Korean:

::

    udig -nl ko

To change the language to Basque:

::

    udig -nl eu

To change the language to Italian:

::

    udig -nl it

To change the language to Spanish:

::

    udig -nl es

The community is working on additional translations - if you are interested in volunteering please
the instructions are available
`here <http://udig.refractions.net/confluence//display/ADMIN/Adding+Translations>`_.

Java
----

The application will use the version of Java specified on the command line:

::

    udig -vm <jre location>

If you say nothing it will pick up the **jre** folder next to the udig application executable.
Failing that it will try and find java on the path or by checking JAVA\_HOME.

Got Memory
----------

We have made every effort to make uDig work in a low memory environment, the default application is
very careful to stream information from the disk or Internet to the screen.

But we are still working with geospatial information - and that can get very large. Some editing
operations will benefit from the addition of more memory.

::

    udig -vmargs -Xmx756m

+------------------+--------------------------+
| Minimum Tested   | udig -vmargs -Xmx64m     |
+------------------+--------------------------+
| Default          | udig -vmargs -Xmx512m    |
+------------------+--------------------------+
| Maximum Tested   | udig -vmargs -Xmx1536m   |
+------------------+--------------------------+

Proxy Settings
--------------

We make use of normal Java proxy settings; here is an example

udig.exe -vmargs -DproxySet=true -DproxyPort=8080 -DproxyHost=192.168.20.1

The same thing could be accomplished by modifying your udig.ini file as shown:

-vmargs
 -Xmx386M
 -Dosgi.parentClassloader=ext
 -DproxySet=true
 -DproxyHost=192.168.20.1
 -DproxyPort=8080

 If requires you can also add a Proxy User and Password.

-DproxyUser=<userid>
 -DproxyPassword=<password>

You can also be specific and provide different ports for http, socks and ftp proxy servers:

-Dhttp.proxyPort=8080
 -Dhttp.proxyHost=192.168.20.1
 -DsocksProxyHost=192.168.20.1
 -DsocksProxyPort=8080
 -DftpProxySet=true
 -DftpProxyHost=192.168.20.1
 -DftpProxyPort=8080

Windows
=======

uDig does not work on Windows 2000
----------------------------------

A: You will need to download the **GDI+** dll and place it in either:

-  the correct windows folder as per the installation instructions
-  the udig folder **jre/bin** (this is useful if you you are packaging up a uDig based application
   for hundreds of windows 2000 machines)

A web search shows this page where you can download a GDI plus installer:

-  `Platform SDK Redistributable:
   GDI+ <http://www.microsoft.com/downloads/details.aspx?familyid=6A63AB9C-DF12-4D41-933C-BE590FEAA05A&displaylang=en>`_

This use of **GDI+** is the same problem that forces Vista into a "Downgraded Graphical Experience"
- we expect it to be solved after Eclipse 3.3 is released.

Max OS X
========

To start uDig on a Mac double click on the uDig application.

To change any of the above mentioned command line operations you will need to edit the udig.ini file
inside of the application bundle.

#. Control-click on the the uDig Application to bring up a pop up menu
#. Choose "Show Package Contents"
#. Locate the udig.ini file in **Contents/MacOS**
#. Use your text editor to modify udig.ini

You can also run udig from the command line using the symbolic link provided.

Linux
=====

Here we try to shed some light on some known issues about uDig in Linux.

Fedora 10 XULRunner Library Conflict
------------------------------------

The XULRunner library is used for Mozilla browser integration - and we can run into conflicts if you
have the Fedora 10 Firefox package is installed or or a conflict between 32bin and 64 bit versions
of the library.

This conflict effects Eclipse 3.3 based applications such as uDig 1.1.1 and results in the JRE
crashing - see `UDIG-1429 <http://jira.codehaus.org/browse/UDIG-1429>`_ for details.

The workaround is to explicitly document which library to use on the command line:

::

    udig -configuration ~/.udig -vmargs -Dorg.eclipse.swt.browser.XULRunnerPath=/usr/bin/xulrunner

As with any of these command line settings you can also add them to the udig.ini file.

Advanced graphics: to disable or not?
-------------------------------------

`UDIG-1110 <http://jira.codehaus.org/browse/UDIG-1110>`_: this is a conflict between SWT and Cairo
1.2.x, which usually causes the map to not be visible. Until these two camps sort out their
problems, uDig will be caught in the crossfire (and we have to disable "advanced graphics"). This
problem should be resolved when a) we upgrade eclipse version uDig is based on and b) you upgrade
your linux distribution.

Due to a known problem in one of the supporting libraries, when first started uDig shows a pop-up
window recommending that Linux users disable advanced graphics (check the version of your cairo
package to be certain).

Q: Under what conditions is this necessary, and for what user interface features are there problems?

Cairo 1.2.x = Map in not visible
 Cairo 1.4.x = black edges?

Q: Is there a work-around?

A: Disable advanced graphics to make the map visible, but draw slowly.

Q: Perhaps downloading some extra package and installing it manually?

-  upgrade cairo?
-  upgrade eclipse?

The ugly cursors (`UDIG-785 Cursors on non--windows platforms are mangled <http://jira.codehaus.org/browse/UDIG-785>`_)
-----------------------------------------------------------------------------------------------------------------------

Linux `doesn't support udig's cursors <http://jira.codehaus.org/browse/UDIG-785>`_.

Q: Would it be possible to have also a set of b/w cursors which are simpler, but at least they do
not look ugly in linux?

A: Yes we are limited by time and artwork, specifically we need two black and white bmp files (one
for the image and one for the mask). The current cursors are using the GIF format which is
apparently a problem.

If you have time please attach the artwork to the UDIG-785 (or email them to the developers list).

Info and Help require a "Pure" Mozilla Browser ( `UDIG-698 <http://jira.codehaus.org/browse/UDIG-698>`_: Linux MOZILLA\_FIVE\_HOME required for Information tool )
------------------------------------------------------------------------------------------------------------------------------------------------------------------

uDig uses a web browser to display its help documents and also to show the attribute information
about a feature selected from the map using the Info tool.

About the Internal Browser
--------------------------

For several functions uDig will make use of an internal (or embedded browser).

-  Info View - browser used to display details obtained from a Web Map Server
-  Web Catalog - browser used to display a search service
-  Help - browser used to display this guide

On the Linux platform eclipse tries to launch the Mozilla browser. But recent Linux distributions
ship with a "custom" copy of Firefox so uDig does not recognize it.

Because uDig runs inside eclipse, the Standard Widget Toolkit FAQ at `<http://www.eclipse.org/swt/faq.php#browserlinux>`_

may indicate which web browser to install in case this information is not displayed correctly.

Q: How to fix this?

A: Download and Install Mozilla

(i don't think this is necessary - see `UDIG-698 <http://jira.codehaus.org/browse/UDIG-698>`_)

Ubuntu 8.04 systems that have the Firefox 3 rc1 update installed will need to also install the
xulrunner or xulrunner-gnome-support package to use the information tool. The latter package can be
installed using the command:

::

    sudo apt-get install xulrunner-gnome-support

The zoom window is opaque when advanced graphics are disabled
-------------------------------------------------------------

If the advanced graphics are disabled, the zoom box is opaque yellow, not translucent as it is under
Windows.

A: Understood, should we use a dither pattern? as a temporary measure?

Can I use my Own Java?
----------------------

A: Yes you can (but please make sure it has **Java Advanced Imaging** and **Java Imaging IO**
installed.

You can specify a different version on the command line:

::

    udig -vm vmPath

You should also be able to mess around with the udig.ini file in a manner similar to the command
line options.

This is a useful technique if you ever get a failure "JVM terminated. Exit code=1"; we have only
noticed this when a really old JVM is available.

JRE folder vs JAVA\_HOME
------------------------

uDig will pick up the **jre** folder included in the download, if you rename this (say to **jre2**)
it will be forced look at your **JAVA\_HOME** environmental variable.
