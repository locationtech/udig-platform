Draw Geometry Tool
##################

Draw Geometry Tool – Cannot resolve external resource into attachment.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The Draw Geometry Tool allows lines, polygons and holes to be drawn in a free-hand manner. The tool
attempts to determine what geometry should be created by inspecting the :doc:`Feature Type` of the layer. If it is a polygon then polygons will be created;
similarly lines are created for line layers. If the layer's geometry type is Geometry (ie. can
contain both lines and polygons) then lines are created unless the draw is finished over the
starting vertex (the geometry is closed) in which case polygons are created.

.. figure:: /images/draw_geometry_tool/drawGeom.png
   :align: center
   :alt: 

The tool can create holes in existing polygons as well. In order to do this:

#. Select the geometry you wish to edit (Draw tool will select geometries when in :doc:`Using Advanced Edit Mode`
#. Draw a hole that is enclosed in the polygon

As with the :doc:`Line Creation Tool`, the Draw Geometry Tool can extend existing lines:

1. Select the geometry you wish to edit (Draw tool will select geometries when in :doc:`Using Advanced Edit Mode`
#. Draw a line that continues from the end of selected line.
 
   .. note::
      It has to *start* at the end of the selected line. If a line is ended at an end of the
      selected line a new line will be created rather than extending the selected line.

**Related reference**

:doc:`EditBlackboard`

:doc:`Using Vertex Tools`

  * :doc:`Edit Geometry Tool`

  * :doc:`Add Vertex Tool`

  * :doc:`Remove Vertex Tool`

  * :doc:`Hole Cutter`


:doc:`Using Geometry Creation Tools`

  * :doc:`Polygon Creation Tool`

  * :doc:`Line Creation Tool`

  * :doc:`Point Creation Tool`

  * :doc:`Rectangle Tool`

  * :doc:`Ellipse Tool`


:doc:`Using Feature Edit Tools`

  * :doc:`Fill Tool`

  * :doc:`Split Tool`

  * :doc:`Delete Tool`
