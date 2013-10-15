CSV Renderer Tutorial
=====================

This is an advanced workbook showing how torender a comma separated value file.

.. figure:: /images/csv_renderer_tutorial/RenderingWorkbook.png
   :alt: 

This workbook is part of our commercial training materials.

-  See the main `uDig website <http://udig.refractions.net/users/>`_ for details on training and
   support
-  Commercial Training materials are available to Academic students on request

Source code:

-  Available in your uDig SDK (import the :doc:`source code <code_examples>` from the plugins
   view)
-  plugin:
   `net.refractions.udig.tutorials.render.csv <https://github.com/uDig/udig-platform/tree/master/tutorials/net.refractions.udig.tutorials.render.csv>`_
   (github)

Introduction
------------

This workbook brings together all your previous work in uDig with one goal - getting your data on
the screen.

This workshop covers

-  Implement a Custom Renderer
-  Interacting with the uDig Rendering Workflow

If you would like to create your own styles please review the styleContent extension point. There
are also extension points for making StyleConfigurator (user interfaces that are used to modify the
style blackboard).

What to Do Next
---------------

For better understanding of these facilities please try the following:

ProgressMonitor
^^^^^^^^^^^^^^^

The CSV.getSize() method provides a count of the total number of records. Use this information to
provide accurate information to the ProgressMonitor (rather than UNKNOWN).

Header
^^^^^^

Can you add a CSV.getCoordinateReferenceSystem() method that is based on reading an comment at the
top of the file?

::

    # EPSG:3005

You will need to modify the IGeoResourceInfo code to produce a ReferencedEnvelope in the correct
coordinate reference system.

Sidecar File
^^^^^^^^^^^^

You will find that several file formats make use of a ".prj" file (containing Well-Known-Text). Can
you create a "cities.prj" file and modify the CSV class to pick it up.

Hint: There is code in geotools to read a PRJ file.

Generate CSV file
^^^^^^^^^^^^^^^^^

Advanced: Can you create an operation to export shapefile as a CSV file? Remember to add the CSV
file to the catalog after it is created.

Style
^^^^^

Advanced: Some StyleContents are already available - such as Font. Can you make your Renderer check
the style blackboard for the current Font?

Hint: You will need to change CSVGeoResource to resolve to a Font (it will be used as the default
value).

Tips, Tricks and Suggestions
----------------------------

The following tips have been provided by the udig-devel list; please stop by and introduce yourself.

Commercial Training Materials
-----------------------------

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
