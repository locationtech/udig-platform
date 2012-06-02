


Draw Geometry Tool
~~~~~~~~~~~~~~~~~~



Draw Geometry Tool Cannot resolve external resource into attachment.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The Draw Geometry Tool allows lines, polygons and holes to be drawn in
a free-hand manner. The tool attempts to determine what geometry
should be created by inspecting the `Feature Type`_ of the layer. If
it is a polygon then polygons will be created; similarly lines are
created for line layers. If the layer's geometry type is Geometry (ie.
can contain both lines and polygons) then lines are created unless the
draw is finished over the starting vertex (the geometry is closed) in
which case polygons are created.



The tool can create holes in existing polygons as well. In order to do
this:
> 1. Select the geometry you wish to edit (Draw tool will select
geometries when in <a href="Using Advanced Edit Mode.html"
title="Using Advanced Edit Mode">Using Advanced Edit Mode< a>
> 2. Draw a hole that is enclosed in the polygon< p>
As with the `Line Creation Tool`_, the Draw Geometry Tool can extend
existing lines:
> 1. Select the geometry you wish to edit (Draw tool will select
geometries when in <a href="Using Advanced Edit Mode.html"
title="Using Advanced Edit Mode">Using Advanced Edit Mode< a>
> 2. Draw a line that continues from the end of selected line. <br >
**Note:** It has to *start* at the end of the selected line. If a line
is ended at an end of the selected line a new line will be created
rather than extending the selected line.


+ Advanced Edit Mode
+ `EditBlackboard`_



+ `Using Vertex Tools`_

    + `Edit Geometry Tool`_
    + `Add Vertex Tool`_
    + `Remove Vertex Tool`_
    + `Hole Cutter`_

+ `Using Geometry Creation Tools`_

    + `Polygon Creation Tool`_
    + `Line Creation Tool`_
    + `Point Creation Tool`_
    + `Rectangle Tool`_
    + `Ellipse Tool`_

+ `Using Feature Edit Tools`_

    + `Fill Tool`_
    + `Split Tool`_
    + `Delete Tool`_




.. _Edit Geometry Tool: Edit Geometry Tool.html
.. _Ellipse Tool: Ellipse Tool.html
.. _Feature Type: Feature Type.html
.. _Rectangle Tool: Rectangle Tool.html
.. _Using Geometry Creation Tools: Using Geometry Creation Tools.html
.. _Hole Cutter: Hole Cutter.html
.. _Split Tool: Split Tool.html
.. _Add Vertex Tool: Add Vertex Tool.html
.. _Using Feature Edit Tools: Using Feature Edit Tools.html
.. _Remove Vertex Tool: Remove Vertex Tool.html
.. _EditBlackboard: EditBlackboard.html
.. _Delete Tool: Delete Tool.html
.. _Fill Tool: Fill Tool.html
.. _Polygon Creation Tool: Polygon Creation Tool.html
.. _Line Creation Tool: Line Creation Tool.html
.. _Point Creation Tool: Point Creation Tool.html
.. _Using Vertex Tools: Using Vertex Tools.html


