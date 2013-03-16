Edit Tool Preferences
=====================

.. figure:: /images/edit_tool_preferences/edittoolpreferences.png
   :align: center
   :alt:

Available Preferences
~~~~~~~~~~~~~~~~~~~~~

-  Snap Radius - Declares how big the radius of the snap circle will be (in pixels) when performing
   snap operations. Default setting is 30.

   .. tip::
      Snap Radius can also be set by holding :kbd:`ALT` and moving the scroll wheel when a edit tool is active.

-  Snap Behaviour - Declares how vertex snapping behaves.

   .. tip::
      Snap behaviour can also be changed by pressing :kbd:`CTRL` + :kbd:`SHIFT` + :kbd:`S` when a edit tool is active.


   .. list-table::
      :widths: 30 70

      * - No Snapping
        - Snapping is turned off. **DEFAULT**
      * - Selected Features
        - Will snap to vertices that are part of a feature that has been added
          to the :doc:`/concepts/Edit Blackboard` (has been selected)
      * - Current Layer
        - Searches for the closest vertex in the current layer
      * - All Layers
        - Searches for the closest vertex in all editable layers
      * - Grid
        - Snaps to the nearest grid intersection


-  Grid Size - The size of the grid map graphic. The units are in degrees.
    Default is 20.0 degrees.
-  Vertex Diameter - Declares how large the vertex handles on the **edit** feature will be.
    Default setting is 4.
-  Vertex Outlines - Declares what color the outlines of the vertex handles will be.
    Default is black.
-  Feedback - The color that the feedback animations will be.
    Default is red.

.. note::
   The color of the outlines of the selected features are set in the :ref:`preferences-page-project-map`
   by the selection color preference.


**Related reference**

.. toctree::
   :maxdepth: 1

   Edit Tool Performance Preferences
   Snapping


Peers
~~~~~

:doc:`Catalog Preferences`

:doc:`Project Preferences`

:doc:`Rendering Preferences`
