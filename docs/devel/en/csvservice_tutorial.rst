CSV Service Tutorial
====================

Step one of our disk to screen tour is access to data.

.. figure:: /images/csvservice_tutorial/CSVServiceTutorial.png
   :alt: 

This workbook is part of our commercial training materials.

-  See the main `uDig website <http://udig.refractions.net/users/>`_ for details on training and
   support
-  Commercial Training materials are available to Academic students on request

Source code:

-  Available in your uDig SDK (import the `source code <Code%20Examples.html>`_ from the plugins
   view)
-  plugin:

   * `com.csvreader <https://github.com/uDig/udig-platform/tree/master/tutorials/com.csvreader>`_
      (github)
   * `org.locationtech.udig.tutorials.catalog.csv <https://github.com/uDig/udig-platform/tree/master/tutorials/org.locationtech.udig.tutorials.catalog.csv>`_
      (github)

Introduction
------------

This workbook covers the creation of a custom service for the catalog. Our service is going to parse
a comma separated value file; using a third-party java library that we are going to bundle up into a
separate plug-in.

After completing this workbook you will:

-  Understand how the uDig Catalog interacts with external services.
-  Bundle up a third-party jar as an eclipse plug-in
-  Implement a new uDig catalog service.

This workbook is going to cover several advanced topics in Eclipse RCP Development. In particular we
will cover the relationship between plug-in dependencies and how the Platform stitches everything
together with some very strict Classpath restrictions.

.. figure:: /images/csvservice_tutorial/CSVService.png
   :align: center
   :alt: 

What to Do Next
---------------

Here are some additional challenges for you to try:

Icon
^^^^

The CSV file does not look very pretty in the catalog; can you give it a better title? How about an
Icon?

Hint use your plug-in activator to manage IconDescriptors.

Generate CSV File
^^^^^^^^^^^^^^^^^

The Java CSV project includes a CSVWriter; can you make an Operation that generates a new CSV file
given any point data?

Hint: Remember to re-project into WGS84

URL
^^^

Advanced: There is a specific mime type for CSV files; can you use this information to allow our
service to work with a CSV file on a web server?

Break it
^^^^^^^^

What happens when you try and add this data to a Map?

Hint: Try zooming to the extents of the layer.

Tips, Tricks and Suggestions
----------------------------

The following tips have been provided by the udig-devel list; please stop by and introduce yourself.

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
