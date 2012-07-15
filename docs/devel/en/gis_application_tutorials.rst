GIS Application Tutorials
-------------------------

The GIS Application is used for the core uDig ideas of map, layers and tools.

The following tutorials are available:

:doc:`map_decorator_tutorial`

 :doc:`feature_editor_tutorial`


Reference:

-  `Disk to Screen Tutorials <Disk%20to%20Screen%20Tutorials.html>`_ tutorials `CSV Renderer
   Tutorial <CSV%20Renderer%20Tutorial.html>`_ and `Style Tutorial <Style%20Tutorial.html>`_

GIS Application
~~~~~~~~~~~~~~~

The GIS Application serves as the basic GIS for your own customisations. It is the second tier of
the `Platform Architecture <Platform%20Architecture.html>`_:

-  Custom Application
-  GIS Application
-  GIS Platform

The GIS Application is essentially the running uDig application including the GUI.

Data Model
~~~~~~~~~~

GIS Application provides a Project-Map-Layer model which you can see in the user interface:

-  **Project** is shown by the ProjectView with a breakdown of your open projects
-  **Map** is shown by the MapEditor - where it is used render your layers and allows you to zoom
   and edit features.
-  **Layer** is shown by the LayersView where you can select what layers to render, and order them.

   -  The actual data from the Layers comes from a GeoResource (like PostGis or a Shape File). The
      rendering system will use IResolve to access the data as appropriate.
   -  The Style Editor (and Style view) allow you to change the style of various map features. This
      information is stored on a "style blackboard" for use by both the style editor and the
      rendering system.
   -  A "decorator" is implemented as a **MapGraphic** (basically a layer without specific data such
      as a scale bar or legend)

.. figure:: /images/gis_application_tutorials/MapDataModel.png
   :align: center
   :alt: 

Notes:

-  uDig generally uses blackboard for inter component communication; the coordinate tool and
   coordinate map graphic is an introduction to this idea

Commands
~~~~~~~~

The second bit of fun here is the above data model is not available for you to change directly. In
earlier copies of uDig we had the data model available for direct modification so you could call
setter methods and the user interface would update.

This resulted in a lot of deadlocks due to these restrictions:

-  code that works with any kind of data should not be in the user interface thread (or the
   application will appear to "freeze up")
-  code that updates the screen or user interface must be in the Display Thread

What we have come up with is the use of **Commands**:

::

    map.sendCommandASynch( command );

There are a number of commands available in uDig and you can role your own:

::

    NavCommand goHome = new SetViewportCenterCommand(new Coordinate(x,y), DefaultGeographicCRS.WGS84 );
    map.sendASyncCommand( goHome );

For more information:

-  `Commands <Commands.html>`_ (Developers Guide)

