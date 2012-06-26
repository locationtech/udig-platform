Drag and Drop
#############

Drag and Drop is the general practise of:

#. Pressing the mouse button to grab an object; then
#. Using the mouse to "Drag" the object into position; and
#. Releasing the mouse button to "Drop" the into the desired spot

Layer view
==========

Changing Layer Order
--------------------

You can drag and drop existing layers to sort them into the desired order.

#. Press the mouse button on a Layer
#. Drag the layer into the desired position; a horizontal line will indicate the position where the
   layer will be moved into
#. Release the mouse button

This effect is the same as pressing the move up and move down buttons.

Inserting a Layer
-----------------

You can insert a new layer; into the exact position desired; by dragging and dropping any of the
following:

-  A resource from the catalog
-  A File (from the operating system)
-  A URL (from a web browser)

To insert a layer:

#. Press the mouse button on a File
#. Drag the file into the Layer view
#. Release - If the file contains a single resource (like a shapefile) it will be added immediately.
   The shapefile will also be added to the catalog.

To insert a Service:

#. Press the mouse button on a WMS Capabilities link (in a browser)
#. Drag the url into the Layer view
#. Release; a :doc:`Resource Selection page` will be displayed
   allowing you to choose which layers to insert. The service will also be added to the catalog.

Projects View
=============

Creating a New map
------------------

You can drag a resource from the Catalog; a file or a URL into the Projects view to create a new
Map.

Creating a new Map from a shapefile:

#. Press a mouse button on a shapefile (be sure to choose the file with the extension **shp**)
#. Drag the file into the Project view:

   -  Release it onto a Project to create a new map; The map will be created with the same name as
      the shapefile; the map projection will be based on the **prj** file.
   -  Release it onto an existing Map to insert a layer

  .. note::
     The resource(s) must be dragged to the exact location in the Projects view hierarchy; they
	 cannot be simply dragged and dropped onto a blank area.

Map Editor
==========

You can drag a File, URL or Catalog resource onto an open Map Editor; a layer will be added to the
Map as if you had used the :doc:`Add Data wizard`

**Related reference**

:doc:`Layers view`

:doc:`Projects view`

:doc:`Map editor`
