Custom Application Tutorial
===========================

Here is the tutorial everyone has been waiting for; how to make your own custom application.

Reference:

* `Rich Client Tutorial Part 1 <http://www.eclipse.org/articles/Article-RCP-1/tutorial1.html>`_ 
* `Rich Client Tutorial Part 2 <http://www.eclipse.org/articles/Article-RCP-2/tutorial2.html>`_ - 
  Applications, Workbenches, and Workbench Windows
* `Rich Client Tutorial Part 3 <http://www.eclipse.org/articles/Article-RCP-3/tutorial3.html>`_ - 
  Views, Menus and Toolbars

.. _CustomApplication.pdf: http://udig.refractions.net/files/tutorials/CustomApplication.pdf

.. image:: /images/custom_application_tutorial/CustomAppWorkbook.png
   :target: CustomApplication.pdf_


This workbook is part of our public training materials:

* CustomApplication.pdf_ 

Downloads:

* :download:`rcp_branding.zip <CustomApplication/rcp_branding.zip>` Images and Icons used in the Tutorial

Source code:

-  Available in your uDig SDK (import the :doc:`source code <code_examples>` from the plugins
   view)
-  plugin: `net.refractions.udig.tutorials.customapp <https://github.com/uDig/udig-platform/tree/master/tutorials/net.refractions.udig.tutorials.customapp>`_ (github)
-  feature:
   `net.refractions.udig\_tutorials.custom-feature <https://github.com/uDig/udig-platform/tree/master/tutorials/net.refractions.udig_tutorials.custom-feature>`_ (github)

Introduction
------------

In this workbook we are going to create a custom application based on uDig GIS platform.

We will learn how to:

-  Specify branding elements such as splash screen and title
-  Declare how components in the application will be organized
-  Package up the application for others to download and use

We will be making use of the development environment created in the first workbook. If you also have
completed the Distance Tool Plug-in you can include it in your application.

.. figure:: /images/custom_application_tutorial/HelloWorld.png
   :align: center
   :alt: 

This is very detailed tutorial focused on results; please take the time to explore the **What to do
next section** to ensure you understand how applications, action sets, products and features are
used to assemble your application for distribution.

What to do Next
---------------

Here are some additional challenges for you to try. Each of these ideas results in a more
professional looking application.

JRE
^^^

Manually add the jre folder from the SDK Quickstart to the generated custom-1.0.0.zip file. This
will let users of your application unzip and run.

About
^^^^^

Try a wider about.gif image and see what that looks like.

Icons
^^^^^

Try making window icons with a transparent section.

Search Extension Point Information
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The eclipse Search (Control-H) can be used find all the available action sets.

Try a Plug-in Search with the following parameters.

-  Search string: org.eclipse.ui.actionSets
-  Search for: Extension Point
-  Limit To: References
-  External Scope: Enabled Plug-ins
-  Scope: Workspace

.. image:: /images/custom_application_tutorial/CustomApplicationSearch.png

Control Menu contents with a Perspective Extension
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Add a new perspective extension "actionSet to get the 'Change Style...' layer menu entry.

.. figure:: /images/custom_application_tutorial/CustomApplicationMenu.png
   :align: center
   :alt: 

-  Hint: New for Eclipse 3.5 there is now a "browse" button when adding an ActionSet to your
   perspective extension allowing you to search and choose from a list.

Intro
^^^^^

Add an "intro part" to your application to welcome new users.

Remember to search in the eclipse help menu for documentation on how to do this - yes it is even
better then trying to use google or
`stackoverflow <http://stackoverflow.com/questions/tagged/eclipse>`_.

Branding
^^^^^^^^

Add branding to the net.refractions.udig\_tutorials.custom-feature feature. This is how you can get
your organization's name included in the about box.

Internationalisation
^^^^^^^^^^^^^^^^^^^^

Externalise all the translatable strings in the plugin.xml and the custom.product. You can also
create language specific splash.bmp and about.gif images.

Build from the Command Line (Advanced)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Advanced: Perform a build from the command line, eclipse help has the details.

Tips and Tricks
---------------

The following tips and tricks and suggestions have been collected from the udig-dev email list;
drop by and introduce yourself.

The Application is Missing when I try and Run
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This is common issue; which can be resolved by carefully considering the difference between features
and plugins.

-  plugins are bundles of code that are going to be run; they track their dependencies (which
   plugins they need to be available in order to launch).
-  features gather up plugins into a big pile for distribution (either as part of a zip file or as
   an update site)

Here is the key message; you can make a feature that **does not include everything needed to run**.

Here is one way to identify the problem:

#. Delete your Run Configuration
#. Launch the application from your product (this will create a brand new run configuration using 
   only the plugins mentioned by your product and feature files)
#. Watch it fail
#. Open up the run configuration; and go to the plugin tab; and **verify** the plugins. This will 
   go through all the plugins and check that they have what they need to run.
#. Navigate through the list of plugins that cannot start; and see what plugin they are missing.
#. Add those missing plugins to a feature so they are included in your run configuration

Aside: If you just hit "Add Required Plugins" you will be able to run right away; but that won't
help you when you go to export your application for release.

Branding Contents Shows up from Eclipse but not when I run Standalone
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This often shows up as missing icons.

Check the **build.xml** and ensure that the required files are marked as content to export.

My SDK cannot export Cross Platform
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This is they key step where we finally get to see if your SDK Quickstart worked. Please check the
layout of your eclipse and delta\_pack and ensure they are both listed as your target platform in
Eclipse preferences.

My SDK cannot export
^^^^^^^^^^^^^^^^^^^^

The other possibility is that **gasp** we left something out of the uDig SDK. Please contact us on
the udig-dev list.

Examples of ways we have messed things up in the past:

-  the **udig\_application** feature listed a plugin that was removed (making it impossible to
   export without error)
-  the support plugins for junit were not included when we update versions of Eclipse making (it
   impossible to debug)
-  new plugins were added by eclipse when we upgraded (we needed to update the list of plugins we
   included so that org.eclipse.ui plugin would actually run)

uDig 1.1 Workbook
^^^^^^^^^^^^^^^^^

For uDig 1.1 developers the previous version of this document is available [

* `http://udig.refractions.net/tutorials/rcp\_walkthrough.pdf <http://udig.refractions.net/tutorials/rcp_walkthrough.pdf>`_

