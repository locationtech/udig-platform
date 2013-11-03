Polygon Creation Tool
#####################

Polygon Creation Tool – Cannot resolve external resource into attachment.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The Polygon Creation Tool allows polygons created. To create a polygon simply select a polygon layer
in the :doc:`/reference/Layers view` and click to begin a new polygon and continue to click at
new locations to add more vertices.

|image0|

.. note::
   Edited polygons are only filled when the fill polygon preference is set in 
   the :doc:`/reference/Edit Tool Performance Preferences`

Vertex Snapping
~~~~~~~~~~~~~~~

By default :doc:`snapping </reference/Snapping>` is turned off however, if
desired Snapping can be used during polygon geometry creation.

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
-  Holding :kbd:`CTRL` + :kbd:`SHIFT` down will allow the entire geometry to be moved.
-  As with the :doc:`Edit Geometry Tool` the vertex operations can be
   performed on multiple vertices at once if many vertices are selected.

**Related concepts**

:doc:`/concepts/Edit Blackboard`

**Related tasks**

:doc:`Using Vertex Tools`

:doc:`Using Geometry Creation Tools`

:doc:`Using Feature Edit Tools`

**Related tasks**

:doc:`/reference/Snapping`


.. |image0| image:: /images/polygon_creation_tool/createpolygon.png
.. |image1| image:: /images/polygon_creation_tool/currentlayersnapping.png
.. |image2| image:: /images/polygon_creation_tool/alllayersnapping.png
.. |image3| image:: /images/polygon_creation_tool/gridsnapping.png
