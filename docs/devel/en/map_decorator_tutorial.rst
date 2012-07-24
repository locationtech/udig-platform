Map Decorator Tutorial
======================

This tutorial allows you to quickly draw something on screen using a combination of a Tool (for
drawing) and a Decorator (for drawing on the map).

.. figure:: /images/map_decorator_tutorial/CoordinateMapGraphicWorkbook.png
   :alt: 

References:

* :doc:`working_with_gis_application`

* `crs <http://docs.geotools.org/latest/userguide/library/referencing/crs.html>`_ (GeoTools User Guide)

This workbook is part of our commercial training materials.

-  See the main `uDig website <http://udig.refractions.net/users/>`_ for details on training and
   support
-  Commercial Training materials are available to Academic students on request

Source code:

-  Available in your uDig SDK (import the :doc:`source code <code_examples>` from the plugins
   view)
-  plugin:

   * `net.refractions.udig.tutorials.tool.coordinate <https://github.com/uDig/udig-platform/tree/master/tutorials/net.refractions.udig.tutorials.tool.coordinate>`_
      (github)
   * `net.refractions.udig.tutorials.mapgraphic <https://github.com/uDig/udig-platform/tree/master/tutorials/net.refractions.udig.tutorials.mapgraphic>`_

Introduction
------------

This tutorial will introduce you to a few aspects of using uDig as a "GISApplication". This time out
we will be working with high level concepts familiar to end users (such as Maps and Layers) .

Many plug-ins you write will focus on Layer as the junction of:

-  GeoResource: Access and modify the data associated with the layer
-  Blackboard: A scratch pad for plug-ins to collaborate
-  StyleBlackboard: A black board used to collaborate with renderers

A MapGraphic is used as a "decorator" layer without a specific source of data, often used to draw
decorations like a scale bar or legend.

This tutorial provides hands on experience with:

-  Using a Map Graphic for simple rendering purposes
-  Use of a blackboard for collaboration among plug-ins

What to Do Next
---------------

Here are some other things you can try:

World Coordinates
^^^^^^^^^^^^^^^^^

The points are recorded in screen coordinates (try adding some points on top of a shapefile and
zooming around to see this).

Can you make and record the points in world coordinates?

(Hint: getContext() has a worldToPixel method)

Blackboard
^^^^^^^^^^

The blackboard is used to communicate locations between the co-ordinate tool and the MapGraphic. The
important part of a blackboard is that it does not matter which component supplied the locations.

Try this out yourself by import points onto the blackboard from a text file.

Locations
^^^^^^^^^

The worldToPixel method let you work with coordinates in the world; if you change your maps
projection they will be wrong.

How to reproduce:
 1. You need to complete the **World Coordiantes** task above before starting this one
 2. Zoom into the west coast of Canada
 3. Draw several points with your map graphic tool
 4. Change the CRS to 3005 which is a projection specific to this part of the world?

Can you figure out a couple ways to fix this?

Remove the Circles
^^^^^^^^^^^^^^^^^^

The circles are provided by the map graphic to provide visual feedback. We do this in uDig so people
can tell that the tool is actually listening to them.

Can you update the Coordinate Tool so that the circle is removed once the co-ordinate is safely
stored on the map blackboard?

(Hint: Look up draw commands in the developers guide; or javadocs )

North Arrow
^^^^^^^^^^^

A North Arrow would make an excellent Map Graphic how would you make one?

(Hint: you can check your plan for making a north arrow against the one supplied in uDig)

(Hint If you want to use an Image you can ask your Activator to manage the ImageDescriptor for you)

Tips, Tricks and Suggestions
----------------------------

The following tips have been provided by the udig-devel list; please stop by and introduce yourself.

Check what is stored on the blackboard
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you are having trouble with the **World** and **Location** tasks above take a moment with the
debugger to check the following:

-  what is being stored on the blackboard by the co-ordinate tool
-  what is being taken of the blackboard by the map graphic

Using CoordinateReferenceSystem and MathTransform
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The trick to working with spatial data is to understand information is recorded (both the values;
and what they mean). GeoTools gives us some of the data structures used to track this information:

-  CoordianteReferenceSystem: describes how coordinates are measured
-  MathTransform: can be used to convert Coordinates between different coordinate reference systems
-  DefaultGeographicCRS.WGS84: a coordinate reference system used to quickly work with lat/lon data

It also provides a couple of utility classes to make working with these ideas easier:

-  CRS: can help you work with CoordianteReferenceSystems
-  JTS: can help you transform

The GeoTools documentation mentioned below has many valuable example including the following one
showing how to transform a Geometry between two coordinate reference systems:

.. code-block:: java

    import org.geotools.geometry.jts.JTS;
    import org.geotools.referencing.CRS;

    CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
    CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:23032");

    MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
    Geometry targetGeometry = JTS.transform( sourceGeometry, transform);

References:

* `http://docs.geotools.org/latest/userguide/library/referencing/crs.html <http://docs.geotools.org/latest/userguide/library/referencing/crs.html>`_
* `http://docs.geotools.org/latest/userguide/library/api/jts.html <http://docs.geotools.org/latest/userguide/library/api/jts.html>`_

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
