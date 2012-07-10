1 Creating a Plugin
===================

1) Start the New Project Wizard
'''''''''''''''''''''''''''''''

-  File->New->Project
    |image0|
-  Select "Plug-in Project"
-  Next

2) Plug-in Project
''''''''''''''''''

.. figure:: /images/1_creating_a_plugin/NewProject1.jpg
   :align: center
   :alt: 

-  Set the project name:
    agrees with package structure, starts with net.refractions.udig.\*
-  Project contents:
    use default
-  Project Settings:
    Create a Java project
    default src and bin folders are fine
-  Alternate Format:
    Apprently it is not worth using the the OSGi bundle manifest at this time (see panel below)
-  Next

.. figure:: images/icons/emoticons/information.gif
   :align: center
   :alt: 

**Plugin Name**

What is in a name? Well a clue on what the plugin is for:

Project

Example

Naming Convention

Plug-In

net.refractions.udig.render

named in agreement with internal package structure

JUnit Test Plug-In

net.refractions.udig.render-test

Append "-test"

Plug-In Fragment

net.refractions.udig.german

Provide ".\ *language*" file at the root udig

Plug-In Fragment

net.refractions.render-1

Do anything except add a dot

Features

net.refractions.udig.render-feature

Append "-feature" to associated root Plug-In

The following was taken from the `Repository
Structure <http://udig.refractions.net/confluence//display/UDIG/Repository+Structure>`_ page.

**OSGi (from Rich Client Tutorial - Part 1)**

Eclipse 3.0 introduced a new run-time system based on OSGi standards that uses bundles and a new
manifest file (MANIFEST.MF) to implement plug-ins. The use of MANIFEST.MF, in normal circumstances,
is completely optional.

You will notice that almost all of the 3.0 SDK plug-ins do NOT have one yet all are marked as 3.0
and many do not require the compatibility layer. The only reason you would want to have a
MANIFEST.MF is if you need to use a particular OSGi capability that is not exposed through
plugin.xml (for example, import-package).

Otherwise it's recommended at this time that you don't have one.

3) Plug-In Content
''''''''''''''''''

.. figure:: /images/1_creating_a_plugin/NewProject2.jpg
   :align: center
   :alt: 

-  Plug-in Properties

   -  Plug-in ID: Recommended that this is the same as the full package name (and project name)
   -  Plug-in Version: change to 0.1.0 (or whatever we are current working towards)

      -  It is important to ensure that a version number has 3 digits, version numbers with only 2
         digits have been known to cause odd bugs when other plugins depend on them.

   -  Plug-in Name: name as appropriate (recommended that you prepend "uDig ")
   -  Plug-in Proivider: often "Refractions Research, Inc."
   -  Runtime Library: change to prevent conflict (recommended that you prepend "udig-")

-  Plug-in Class (often not needed)

   -  Generate: only check if neededConfluence - Home
   -  Class Name: name appropriately
   -  Check "This plug-in will make contributions to the UI" to access Templates

-  Next

**Plug-in Class (from Rich Client Tutorial - Part 1)**

The generated plug-in class that you may be familiar with in previous releases is no longer required
in Eclipse 3.0. You can still have one to hold global data if you like.

--------------

In this case we would like to hold some global data, rather than use a singleton. This allows us to
cleanup after the
 application (something that is hard with singletons).

4) Templates
''''''''''''

(Only available when making a UI Plug-in Class)

.. figure:: /images/1_creating_a_plugin/NewProject3.jpg
   :align: center
   :alt: 

-  Finish or ...
-  Check "Create a plug-in using one of the templates"

   -  Choose a ui wizard (such as "Custom plug-in wizard")
   -  Next (to start filling out the template)

5) Template Selection
^^^^^^^^^^^^^^^^^^^^^

(Only available when using "Custom plug-in wizard")

5) Template Selection
'''''''''''''''''''''

.. figure:: /images/1_creating_a_plugin/NewProject4.jpg
   :align: center
   :alt: 

-  Choose wizard components from the list according to the needs of your plug-in
-  Next (to work with selected wizards)

.. |image0| image:: /images/1_creating_a_plugin/NewProject.jpg
