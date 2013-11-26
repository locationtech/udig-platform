IAdaptable and Operations Tutorial
==================================

An introduction to two key concepts: do stuff (udig IOp) in more places (eclipse IAdaptable)!.

.. figure:: /images/iadaptable_and_operations_tutorial/IAdaptableOperationWorkbook.png
   :alt: 

Developers Guide:

* :doc:`workbench_selection_tutorial`

* :doc:`menus_using_operations_and_tools`


Source code:

-  Available in your uDig SDK (import the :doc:`source code <code_examples>` from the plugins
   view)
-  plugin:
   `org.locationtech.udig.tutorials.urladapter <https://github.com/uDig/udig-platform/tree/master/plugins/org.locationtech.udig.tutorials.urladapter>`_
   (github)

Introduction
------------

**IAdaptable**

IAdaptable is the easies example of the "extensible interface" pattern. This is a core concepts for
Eclipse RCP development also covered in the :doc:`workbench_selection_tutorial`.

It is a straight forward combination of two patterns you may already be familiar with:

-  AbstractFactory - code that creates an instance of a specific instance at runtime
-  Adapter - code that adapts an existing object into a required interface

In this workbook we are going to define an adapter extension allowing an **IGeoResource** to adapt
to a **URL**. We will also create an operation that operates on a URL in order to test our work. The
extensible interface pattern is used to "teach an old dog new tricks", you can make use of this
technique to integrate your concepts and ideas directly into the uDig GIS Platform (without having
to download and modify the source code).

**Operation**

This workbook also covers one of the uDig ui concepts - the idea of an "operation". A lot of your
day to day work will be based on defining operations to get something done; allowing the user to
right click on a "noun" and perform a "verb".

.. figure:: /images/iadaptable_and_operations_tutorial/DisplayURL.png
   :align: center
   :alt: 

These operations actually show up all over the uDig application (because some platforms such as mac
do not have a right click):

-  `Edit Menu <http://udig.refractions.net/confluence//display/EN/Edit+Menu>`_ - lists All
   Operations in a dialog (following the Eclipse House Rules "Other Rule")
-  `Layer Menu <http://udig.refractions.net/confluence//display/EN/Layer+Menu>`_ - lists operations
   available for the currently selected layer
-  `Map Menu <http://udig.refractions.net/confluence//display/EN/Map+Menu>`_ - lists operations
   available for the current map
-  `Data Menu <http://udig.refractions.net/confluence//display/EN/Data+Menu>`_ - lists operations
   for the current selected GeoResource (the data for a layer) or IService (where the data comes
   from)

The general idea is that you can define the operation on a kind of object; and the uDig application
will make it available to users in as many places as possible.

What to Do Next
---------------

For better understanding of these facilities please try the following:

Context Menu
^^^^^^^^^^^^

You now know how to add to the context menu (right click menu) for almost anything!

This is how we make sure a layer acts like a layer no matter where you see it.

IService
^^^^^^^^

See if you can make your Dialog only work on IService.

How about both an IService and an IGeoResource?

Extensible Interface
^^^^^^^^^^^^^^^^^^^^

IAdaptable is an example of the Extensible Interface pattern, can you recognize another use of this
pattern?

Adapters
^^^^^^^^

Adapters usually end up listening for events in the original object (so that everything can stay in
sync). The Eclipse Modeling Framework (EMF), only provides adapters - and you must use them rather
than add a listener for events.

Workbench Selection (Advanced)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you completed the workbench selection tutorial you can see what is happening using the
SelectionView. See what it tells you when a Service or GeoResource is selected in the Catalog view.

Tips, Tricks and Suggestions
----------------------------

Difference between IService and IGeoResource
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

-  IService - represents where information (for example a shapefile on disk)
-  IGeoResource - represents the actual information (for example the contents of the shapefile)

Commercial Training Materials
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Please contact any of the organisations listed on the main `uDig support
page <http://udig.refractions.net/users/>`_ for details on uDig training.

The workbooks and slides for the training course are available here:

* `http://svn.refractions.net/udig\_training/trunk <http://svn.refractions.net/udig_training/trunk>`_

This is a private svn repository that is open to those who have taken the training course.

Academic Access
^^^^^^^^^^^^^^^

The course materials can be made available to those working at academic institutions - we ask for an
email from your Professor.

Please ask your professor to email admin@refractions.net with the request.
