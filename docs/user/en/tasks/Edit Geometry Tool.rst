Edit Geometry Tool
##################

Edit Geometry Tool – Cannot resolve external resource into attachment.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The Edit Geometry Tool can add, remove, select and move vertices. It can also move geometries. The
functionality illustrated on this page is also available when using the :doc:`Polygon Creation Tool`, :doc:`Line Creation Tool` and
:doc:`Point Creation Tool`


#. Create a map using the sample data.

   #. Add **bc\_border.shp** to new map
   #. Add **bc\_hospitals.shp**
   #. Add **bc\_municipality.shp**

#. Zoom into a small area of the map.

   |image0|

#. Select the layer in the Layers view that you wish to edit (bc\_municipality)
#. Using the **Edit Geometry** tool, select a municipality.

   |image1|

#. You can use the **Feature Edit** tool to move points. Move your mouse over a vertex. Click and
   drag the vertex.

   .. tip::
      The circle around the point is the current snap area. For more information about
      snapping click :doc:`here </reference/Snapping>`.

   |image2|

#. You can also create new points with the **Feature Edit** tool. Click on a straight line where
   there is no vertex. A vertex will appear and it can be moved.

   |image3|

#. To delete a vertex select a vertex and press the delete key or select **Edit > Delete**.

   |image4|

#. Multiple vertices can be selected by dragging selection box around multiple vertices.

   |image5|

#. All selected vertices can be moved or deleted together

   |image6|

#. The geometry can be moved as a whole by holding down :kbd:`CTRL` + :kbd:`ALT` and dragging the geometry.

   |image7|

#. Once the geometry has been modified as much as desired the changes can be *accepted* by

   #. Double clicking the left mouse button
   #. Selecting another tool
   #. Clicking the commit button.

      |image8|

Any operation/change performed can be undone by :kbd:`CTRL` +  :kbd:`Z` or **Edit > Undo**. Undone changes can be
redone by :kbd:`CTRL` + :kbd:`Y` or **Edit > Redo**.

You may also wish to look at the Feature Editor, in order to change attribute information.

Tips
~~~~

-  Advanced Editing Mode can be used to reduce the amount of tool switching that is required. For
   example adding vertices can be performed with the Edit Geometry Tool in advanced mode.
-  Multiple Geometries can be added to the :doc:`/concepts/Edit Blackboard` and edited
   together.

   -  :kbd:`SHIFT` -click adds features to the :doc:`/concepts/Edit Blackboard`
   -  :kbd:`ALT` -click adds/removes features

-  Esc (or Esc-Esc depending on the platform) clears :doc:`/concepts/Edit Blackboard`.
-  If two selected features share a vertex moving the vertex will move the vertex on both features.
-  Use :doc:`/reference/Snapping` to snap to existing features.
-  A line can split by

   #. Selecting a line (select bc\_borders and select a line)

      |image9|

   #. Adding a vertex at location where the split will take place using the :doc:`Add Vertex Tool`

      |image10|

   #. The split can now be done by:

      #. Selecting **Edit > Split Line**

         |image11|

      #. Or right clicking and selecting **Operations > Split Line** from the context menu.

         |image12|

**Related concepts**

:doc:`/concepts/Edit Blackboard`

**Related tasks**

:doc:`Using Vertex Tools`

:doc:`Using Geometry Creation Tools`

:doc:`Using Feature Edit Tools`

**Related tasks**

:doc:`/reference/Snapping`

.. |image0| image:: /images/edit_geometry_tool/tooledit1.png
.. |image1| image:: /images/edit_geometry_tool/tooleditfeature.png
.. |image2| image:: /images/edit_geometry_tool/movevertex.png
.. |image3| image:: /images/edit_geometry_tool/addvertex.png
.. |image4| image:: /images/edit_geometry_tool/removevertex.png
.. |image5| image:: /images/edit_geometry_tool/selectmanyvertex.png
.. |image6| image:: /images/edit_geometry_tool/movemany.png
.. |image7| image:: /images/edit_geometry_tool/movegeometry.png
.. |image8| image:: /images/edit_geometry_tool/accept.png
.. |image9| image:: /images/edit_geometry_tool/selectline.png
.. |image10| image:: /images/edit_geometry_tool/addlinevertex.png
.. |image11| image:: /images/edit_geometry_tool/edit_geometry_menu.png
.. |image12| image:: /images/edit_geometry_tool/contextmenu.png
