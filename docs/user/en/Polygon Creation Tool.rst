Polygon Creation Tool
#####################

Polygon Creation Tool – Cannot resolve external resource into attachment.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The Polygon Creation Tool allows polygons created. To create a polygon simply select a polygon layer
in the :doc:`Layers view` and click to begin a new polygon and continue to click at
new locations to add more vertices.
 |image0|
 
.. note::
   Edited polygons are only filled when the fill polygon preference is set in the :doc:`Edit Tool Performance Preferences`

Vertex Snapping
~~~~~~~~~~~~~~~

By default :doc:`snapping <Snapping>` is turned off however, if desired :doc:`snapping <Snapping>`
can be used during polygon geometry creation.

-  Polygon creation with snapping to the current layer. Notice that it didn't snap to the hospital
   which is in another layer (circled in red)
    |image1|
-  Polygon creation with snapping to the all layers. With this snapping it has snapped to the
   hospital on the second layer.
    |image2|
-  Polygon creation with snapping to the grid. The grid is only seen when the grid map graphic is
   on. **Layer > Grid**
    |image3|

Advanced Editing
~~~~~~~~~~~~~~~~

The Polygon Creation Tool also has an advanced mode that provides all the functionality that the
:doc:`Edit Geometry Tool` has, except only polygons can be edited. A summary of the advanced 
editing features are:

-  Clicking an existing polygon will select it.
-  Vertices in selected polygons can be moved.
-  Vertices in selected liness can be added or deleted
-  Holding CTRL+SHIFT down will allow the entire geometry to be moved.
-  As with the :doc:`Edit Geometry Tool` the vertex operations can be
   performed on multiple vertices at once if many vertices are selected.

**Related reference**


-  Advanced Edit Mode
* :doc:`EditBlackboard`

* :doc:`Snapping`

* :doc:`Using Vertex Tools`


   * :doc:`Edit Geometry Tool`

   * :doc:`Add Vertex Tool`

   * :doc:`Remove Vertex Tool`

   * :doc:`Hole Cutter`


* :doc:`Using Geometry Creation Tools`


   * :doc:`Line Creation Tool`

   * :doc:`Point Creation Tool`

   * :doc:`Draw Geometry Tool`

   * :doc:`Rectangle Tool`

   * :doc:`Ellipse Tool`


* :doc:`Using Feature Edit Tools`


   * :doc:`Fill Tool`

   * :doc:`Split Tool`

   * :doc:`Delete Tool`


.. |image0| image:: /images/polygon_creation_tool/createpolygon.png
.. |image1| image:: /images/polygon_creation_tool/currentlayersnapping.png
.. |image2| image:: /images/polygon_creation_tool/alllayersnapping.png
.. |image3| image:: /images/polygon_creation_tool/gridsnapping.png
