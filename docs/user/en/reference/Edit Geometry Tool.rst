


Edit Geometry Tool
~~~~~~~~~~~~~~~~~~



Edit Geometry Tool Cannot resolve external resource into attachment.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The Edit Geometry Tool can add, remove, select and move vertices. It
can also move geometries. The functionality illustrated on this page
is also available when using the `Polygon Creation Tool`_, `Line
Creation Tool`_ and `Point Creation Tool`_


#. Create a map using the sample data.

    #. Add **bc_border.shp** to new map
    #. Add **bc_hospitals.shp**
    #. Add **bc_municipality.shp**

#. Zoom into a small area of the map. > <img src="download
   attachments/3571/tooledit1.png" align="absmiddle" border="0"/>
#. Select the layer in the Layers view that you wish to edit
   (bc_municipality)
#. Using the **Edit Geometry** tool, select a municipality. > <img
   src="download attachments/3571/tooleditfeature.png" align="absmiddle"
   border="0"/>
#. You can use the **Feature Edit** tool to move points. Move your
   mouse over a vertex. Click and drag the vertex. > <b>Tip:< b> The
   circle around the point is the current snap area. For more information
   about snapping click `here`_. > <img src="download
   attachments/3571/movevertex.png" align="absmiddle" border="0"/>
#. You can also create new points with the **Feature Edit** tool.
   Click on a straight line where there is no vertex. A vertex will
   appear and it can be moved. > <img src="download
   attachments/3571/addvertex.png" align="absmiddle" border="0"/>
#. To delete a vertex select a vertex and press the delete key or
   select **Edit > Delete**. > <img src="download
   attachments/3571/removevertex.png" align="absmiddle" border="0"/>
#. Multiple vertices can be selected by dragging selection box around
   multiple vertices. > <img src="download
   attachments/3571/selectmanyvertex.png" align="absmiddle" border="0"/>
#. All selected vertices can be moved or deleted together > <img
   src="download attachments/3571/movemany.png" align="absmiddle"
   border="0"/>
#. The geometry can be moved as a whole by holding down **CTRL+ALT**
   and dragging the geometry. > <img src="download
   attachments/3571/movegeometry.png" align="absmiddle" border="0"/>
#. Once the geometry has been modified as much as desired the changes
   can be *accepted* by

    #. Double clicking the left mouse button
    #. Selecting another tool
    #. Clicking the commit button. > <img src="download
       attachments/3571/accept.png" align="absmiddle" border="0"/>



Any operation/change performed can be undone by **CTRL+Z** or **Edit >
Undo**. Undone changes can be redone by **CTRL+Y** or **Edit > Redo**.

You may also wish to look at the Feature Editor, in order to change
attribute information.



Tips
~~~~


+ Advanced Editing Mode can be used to reduce the amount of tool
  switching that is required. For example adding vertices can be
  performed with the Edit Geometry Tool in advanced mode.
+ Multiple Geometries can be added to the `EditBlackboard`_ and edited
  together.

    + **Shift-click** adds features to the `EditBlackboard`_
    + **Alt-click** adds/removes features

+ Esc (or Esc-Esc depending on the platform) clears `EditBlackboard`_.
+ If two selected features share a vertex moving the vertex will move
  the vertex on both features.
+ Use `Snapping`_ to snap to existing features.
+ A line can split by

    #. Selecting a line (select bc_borders and select a line) > <img
       src="download attachments/3571/selectline.png" align="absmiddle"
       border="0"/>
    #. Adding a vertex at location where the split will take place using
       the `Add Vertex Tool`_ > <img src="download
       attachments/3571/addlinevertex.png" align="absmiddle" border="0"/>
    #. The split can now be done by:

        #. Selecting **Edit > Split Line** > <img src="download
           attachments/3571/editmenu.png" align="absmiddle" border="0"/>
        #. Or right clicking and selecting **Operations > Split Line** from
           the context menu. > <img src="download
           attachments/3571/contextmenu.png" align="absmiddle" border="0"/>





+ Advanced Edit Mode
+ `EditBlackboard`_
+ `Snapping`_
+ `Using Vertex Tools`_

    + `Add Vertex Tool`_
    + `Remove Vertex Tool`_
    + `Hole Cutter`_

+ `Using Geometry Creation Tools`_

    + `Polygon Creation Tool`_
    + `Line Creation Tool`_
    + `Point Creation Tool`_
    + `Draw Geometry Tool`_
    + `Rectangle Tool`_
    + `Ellipse Tool`_

+ `Using Feature Edit Tools`_

    + `Fill Tool`_
    + `Split Tool`_
    + `Delete Tool`_



.. _Ellipse Tool: Ellipse Tool.html
.. _Rectangle Tool: Rectangle Tool.html
.. _Snapping: Snapping.html
.. _Using Geometry Creation Tools: Using Geometry Creation Tools.html
.. _Hole Cutter: Hole Cutter.html
.. _Split Tool: Split Tool.html
.. _Add Vertex Tool: Add Vertex Tool.html
.. _Using Feature Edit Tools: Using Feature Edit Tools.html
.. _Remove Vertex Tool: Remove Vertex Tool.html
.. _EditBlackboard: EditBlackboard.html
.. _Draw Geometry Tool: Draw Geometry Tool.html
.. _Delete Tool: Delete Tool.html
.. _Fill Tool: Fill Tool.html
.. _Polygon Creation Tool: Polygon Creation Tool.html
.. _Line Creation Tool: Line Creation Tool.html
.. _Point Creation Tool: Point Creation Tool.html
.. _Using Vertex Tools: Using Vertex Tools.html


