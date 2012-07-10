4 Plugin Internationalization with ResourceBundles
==================================================

Source Code Internationalization
================================

uDig recently made a change regarding how it externalizes its strings so that they may be
internationalized. The new method brings several new advantages, including better type-checking. If
you are internationalizing your plug-in for the first time, please use the new, Eclipse NLS class
based method to externalize the strings in your plug-in.

Policy class based (Old method, deprecated)
-------------------------------------------

This method relied on one Policy class, present in every plug-in. It followed the plug-in's
lifecycle, so it was loaded when the plug-in was loaded, and unloaded when the plug-in was unloaded.
Strings were stored in a messages.properties file, and they were referenced using Policy.bind, which
took the key as a parameter.

Eclipse NLS class based (New method, recommended)
-------------------------------------------------

Overview
~~~~~~~~

In this method, each plug-in maintains one Messages class, which extends the NLS class provided by
Eclipse. This class loads each of keys from a messages.properties file into static Strings, which
are referenced by the source code. This provides for compile-time checking, and eliminates the need
for NON-NLS comments, as no hard-coded Strings are involved.

Structure
~~~~~~~~~

The messages.properties file and accompanying Messages class should be placed in the internal
package of your plug-in:

$PLUGIN/src/$PLUGIN-AS-PACKAGE/internal/messages.properties
 $PLUGIN/src/$PLUGIN-AS-PACKAGE/internal/Messages.java

For example, if your plug-in is net.refractions.udig.catalog.ui, it would look like:

net.refractions.udig.catalog.ui/src/net/refractions/udig/catalog/ui/internal/messages.properties
 net.refractions.udig.catalog.ui/src/net/refractions/udig/catalog/ui/internal/Messages.java

Externalizing for the first time
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Eclipse provides an Externalize Strings Wizard that can be used to automate this process very
nicely.

-  Right click on a plug-in or source code file and select **Source-->Externalize Strings...**

Once inside the wizard, make sure you check **"Use Eclipse's string externalization mechanism"**. If
this is already checked and greyed out, then the plug-in is already configured to use it.

You are then presented with a list of values and keys. The keys are given numerical suffixes. Please
change them to something more meaningful.

-  If a value should **not** be externalized, press **Ignore**

-  If you wish to do nothing, press **Internalize**

-  Press the **Configure...** button and make sure that the Messages class and messages.properties
   file are created in the proper directory (see above under "Structure")

Switching from Policy based to Eclipse NLS based
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Switching from Policy based support to Eclipse NLS based support is rather tedious:

-  Delete Policy.java
-  Remove references to it in your Plug-in classe's start and stop methods
-  Remove references to Policy in import files
-  Change all periods (".") in messages.properties to underscore ("\_"). This must be done to all
   keys in the source code as well.
-  Create Messages.java, with static String fields for each key
-  Change all calls from Policy.bind("$key") to Messages.$key
-  Calls that use parameters will need to be changed.
    Ex: Policy.bind("$key", object1, object2) will become MessageFormat.format(Messages.$key,
   object1, object2)

There are some bash and perl scripts in trunk/scripts/i18n that were used to convert uDig over to
this new system. They did not completely make the conversion, but did save a tremendous amount of
time. Please see the README file in that directory before you use them.

To internationalize your plugin.properties files, see `Plugin
Internationalization <Plugin%20Internationalization.html>`_
