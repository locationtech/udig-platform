Home
====

:doc:`2 Programmer's Guide`

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Background information and guidelines for working in the uDig environment.

* :doc:`1 Welcome to the Programmer's Guide`

* :doc:`2 Why a platform`

* :doc:`3 Platform Architecture`

* :doc:`4 Guidelines and Rules`


:doc:`4 Working with Eclipse RCP`

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Tips, tricks and utility classes for working with the Eclipse RCP platform.

* :doc:`00 Working with Plugins`

* :doc:`01 Working With Features`

* :doc:`02 Making a Branding Plugin`

* :doc:`03 Making a Product and Executable`

* :doc:`04 Using UDIGApplication`

-  `06 Traditional Menus using Actions and
   ActionSets <06%20Traditional%20Menus%20using%20Actions%20and%20ActionSets.html>`_
-  `07 New Menus based on Commands, Handlers and Key
   Bindings <07%20New%20Menus%20based%20on%20Commands,%20Handlers%20and%20Key%20Bindings.html>`_
-  `09 uDig menus using Operations and
   Tools <09%20uDig%20menus%20using%20Operations%20and%20Tools.html>`_
-  `10 Adding History to Dialogs and
   Wizards <10%20Adding%20History%20to%20Dialogs%20and%20Wizards.html>`_
* :doc:`11 Working with SWT and JFace`

* :doc:`12 Working with Extension Points`

* :doc:`13 Testing`

* :doc:`14 Bundle a JAR up as a Plugin`

* :doc:`15 How to turn stuff off`

* :doc:`How do I turn off menus`

* :doc:`Using the UDIGWorkbenchAdvisor`

* :doc:`Using UDIGMenuBuilder`

* :doc:`Using WorkbenchConfigurations`

* :doc:`Working with Cheat Sheets`


:doc:`5 Working with the GIS Platform`

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Using the local catalog as a repository to manage your spatial data

* :doc:`1 GIS Platform`

* :doc:`10 Service and GeoResource Interceptors`

* :doc:`11 Workbench Services`

* :doc:`2 Catalog`

* :doc:`3 Tracking Changes`

* :doc:`4 Drag and Drop`

* :doc:`5 Operations`

-  `6 How to add a New Kind of
   DataStore <6%20How%20to%20add%20a%20New%20Kind%20of%20DataStore.html>`_
* :doc:`7 GISPlatform Utility Classes`

-  `9 How to Ensure a Server exists on
   Startup <9%20How%20to%20Ensure%20a%20Server%20exists%20on%20Startup.html>`_

:doc:`6 Working with GIS Application`

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Using Maps, Layers and pages to visualize your spatial data

* :doc:`01 GIS Application`

* :doc:`02 Project Map and Layer`

* :doc:`03 GeoSelectionService`

* :doc:`04 Commands`

* :doc:`05 Style`

* :doc:`06 Tools`

* :doc:`07 Edit Tools`

* :doc:`08 Operations`

* :doc:`09 Renderers`

* :doc:`10 Interceptors`

* :doc:`11 Adding new Format`

* :doc:`12 Map Decorator`


:doc:`7 Printing`

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

:doc:`Examples`

~~~~~~~~~~~~~~~~~~~~~~~~~~~

The uDig code base contains code examples used in this Developers Guide and the uDig Training
Course.

* :doc:`1 Code Examples`

* :doc:`2 Training Materials`

* :doc:`3 Edit Tool Example`

* :doc:`4 Export SLD Plugin Tutorial`


:doc:`FAQ`

~~~~~~~~~~~~~~~~~

* :doc:`Community Questions`

* :doc:`Developer Questions`

* :doc:`Development Questions`

* :doc:`Eclipse RCP Questions`

* :doc:`GIS Application Questions`


:doc:`Reference`

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

* :doc:`1 Javadocs`

* :doc:`2 Extension Point Reference`

* :doc:`4 Other Reference Information`

* :doc:`5 Reading List`

* :doc:`6 Dependencies`

* :doc:`7 Debugging Tips`


Welcome to the uDig *Developer's Guide*. This guide is intended to help developers who want to
customize and build on top of the uDig core. uDig can be extended by adding standard Eclipse
plug-ins which work against either Eclipse or uDig extension points. Developers define their own
applications which combine existing and possibly new plug-ins.

.. figure:: http://udig.refractions.net/image/DEV/ngrelr.gif
   :align: center
   :alt: 

-  `Programmer's Guide (Eclipse 3.4
   Help) <http://help.eclipse.org/ganymede/topic/org.eclipse.platform.doc.isv/guide/int.htm>`_
-  `Programmer's Guide (Eclipse 3.3
   Help) <http://help.eclipse.org/help33/topic/org.eclipse.platform.doc.isv/guide/int.htm>`_
* :doc:`http://wiki.eclipse.org/`

-  `Rich Client Platform <http://wiki.eclipse.org/Rich_Client_Platform>`_ (wiki.eclipse.org)
* :doc:`User Interface Guidelines`

   (wiki.eclipse.org)
* :doc:`Eclipse Search`


.. figure:: images/icons/emoticons/check.gif
   :align: center
   :alt: 

If you are looking for help on eclipse concepts please do not start with google! You will have more
luck looking in the Eclipse Help menu because the contents match the copy of eclipse you are using
today.
