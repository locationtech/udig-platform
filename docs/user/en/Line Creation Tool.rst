Line Creation Tool
##################

Line Creation Tool – Cannot resolve external resource into attachment.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The Line Creation Tool allows lines created. To create a line simply select a line layer in the
:doc:`Layers view` and click to begin a new line.
 |image0|

Vertex Snapping
~~~~~~~~~~~~~~~

By default :doc:`snapping <Snapping>` is turned off however, if desired :doc:`snapping <Snapping>`
can be used during geometry creation.

-  Line creation with snapping to the current layer. Notice that it didn't snap to the municipality
   which is in another layer (circled in red)
    |image1|
-  Line creation with snapping to the all layers. With this snapping it has snapped to the features
   on other layers.
    |image2|
-  Line creation with snapping to the grid. The grid is only seen when the grid map graphic is on.
   **Layer > Grid**
    |image3|

Advanced Editing
~~~~~~~~~~~~~~~~

The Line Creation Tool also has an advanced mode that provides all the functionality that the :doc:`Edit Geometry Tool`
has, except that only lines can be edited. A summary of the advanced editing features are:

-  Clicking an existing line will select it.
-  An existing line can be extended by

#. Clicking on a line to select it
    |image4|
#. Click on an end point to start extending the line
    |image5|

-  Vertices in selected lines can be moved.
-  Vertices in selected lines can be added or deleted
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


   * :doc:`Polygon Creation Tool`

   * :doc:`Point Creation Tool`

   * :doc:`Draw Geometry Tool`

   * :doc:`Rectangle Tool`

   * :doc:`Ellipse Tool`


* :doc:`Using Feature Edit Tools`


   * :doc:`Fill Tool`

   * :doc:`Split Tool`

   * :doc:`Delete Tool`


.. |image0| image:: /images/line_creation_tool/createline.png
.. |image1| image:: /images/line_creation_tool/currentlayersnapping.png
.. |image2| image:: /images/line_creation_tool/alllayersnapping.png
.. |image3| image:: /images/line_creation_tool/gridsnapping.png
.. |image4| image:: /images/line_creation_tool/selectline.png
.. |image5| image:: /images/line_creation_tool/extendline.png
