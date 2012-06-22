Snapping
########

Vertex Snapping
~~~~~~~~~~~~~~~

When vertex snapping is active moving vertices, or adding vertices, will search the area around the
mouse cursor for another vertex. If there is a vertex within the set snap radius then the moved or
created vertex's location will be set to the neighboring vertex's location.

.. figure:: /images/snapping/snaptopoint.png
   :align: center
   :alt: 

In the above image the vertex would snap to the red point feature, as long as the correct snap
behaviour is set. The red circle in the image indicates the area that will be searched. The snap
radius can be set in the :doc:`Edit Tool Preferences` or by holding ALT
while rotating the mouse wheel.

.. note::
   One of the edit tools must be active to set the snap radius using the mouse tool.
   
.. tip::
   The color of the snap area can be set in the :doc:`Edit Tool Preferences`. The feedback color indicates the color of the snap
   area.


Snap Behavior
~~~~~~~~~~~~~

There are different ways that snapping can behave, for example the snapping can snap to a grid
(defined by the user) or it can snap to the nearest vertex on the current layer.
 The normal snapping choices are as follows:

+---------------------+-------------------------------------------------------------------------------------------------------------------------------------------+
| No Snapping         | Snapping is turned off. **DEFAULT**                                                                                                       |
+---------------------+-------------------------------------------------------------------------------------------------------------------------------------------+
| Selected Features   | Will snap to vertices that are part of a feature that has been added to the :doc:`EditBlackboard` (has been selected)                     |
+---------------------+-------------------------------------------------------------------------------------------------------------------------------------------+
| Current Layer       | Searches for the closest vertex in the current layer                                                                                      |
+---------------------+-------------------------------------------------------------------------------------------------------------------------------------------+
| All Layers          | Searches for the closest vertex in all editable layers                                                                                    |
+---------------------+-------------------------------------------------------------------------------------------------------------------------------------------+
| Grid                | Snaps to the nearest grid intersection                                                                                                    |
+---------------------+-------------------------------------------------------------------------------------------------------------------------------------------+

**Note:** The Grid can only be seen if the grid map graphic is added (**Layer > Grid**)
 **Tip:** The size of the grid can be changed in the :doc:`Edit Tool Preferences`.

There is two different ways to change the snap behavior:

#. In the :doc:`Edit Tool Preferences` which can be found in the
   :menuselection:`Window --> Preferences` menu.
#. While an edit to is *active* press CTRL+SHIFT+S. This will cycle through the available snap
   behaviors. A small pop-up will display the new behavior.

**Related reference**


* :doc:`Edit Tool Preferences`


