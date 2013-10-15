Edit Tools
==========

Edit tools are available in the palette to directly manipulate spatial information. Each map
provides a session for editing - if you are happy with your changes you can press commit to write
out your session. If you have made a mistake and need to start again you can revert the current
session.

The palette provides the following edit tools for direct manipulation:

You can cycle through tools in the same category by pressing the keyboard short cut repeatedly.


+-------------------------+------------------------------+-----------------------------+
| Edit Tools (:kbd:`e`)   |  `Create Tools`_ (:kbd:`c`)  | Feature Tools (:kbd:`f`)    |
+-------------------------+------------------------------+-----------------------------+
| |image0| Edit Geometry  | |image4| `Create Polygon`_   | |image10| Delete Feature    |
+-------------------------+------------------------------+-----------------------------+
| |image1| Add Vertex     | |image5| Create Line         | |image11| Split             |
+-------------------------+------------------------------+-----------------------------+
| |image2| Remove Vertext | |image6| `Create Point`_     | |image12| Merge             |
+-------------------------+------------------------------+-----------------------------+
| |image3| Hole Cutter    | |image7| `Fill Area`_        |                             |
+-------------------------+------------------------------+-----------------------------+
|                         | |image8| `Create Ellipse`_   |                             |
+-------------------------+------------------------------+-----------------------------+
|                         | |image9| `Create Rectangle`_ |                             |
+-------------------------+------------------------------+-----------------------------+

Create Tools
------------

Create Polygon
^^^^^^^^^^^^^^

Create a polygon as a series of points. You must draw at least three points for the polygon to be
valid. Polygons are not allowed to self intersect.

Create Point
^^^^^^^^^^^^

Create a point.

Fill Area
^^^^^^^^^

Create a polygon defined by the unused area. Draw a polygon, any area not already taken up by an
existing polygon will be used.

Create Ellipse
^^^^^^^^^^^^^^

Create an Ellipse by drawing on screen.

Create Rectangle
^^^^^^^^^^^^^^^^

Create a rectangle by drawing a box on the screen.


Edit Commands
-------------

Many of these same commands are available in the :doc:`Edit Menu`.

Your session is managed on the main toolbar using the following commands:

|image13| `Commit`_

|image14| `Revert`_


Commit
^^^^^^

Commit modifications to server, database or file.

Revert
^^^^^^

Throw out modifications; reverting to the current contents of the server, database, or file.

**Related tasks**

:doc:`/tasks/Working with Features`

**Related reference**

:doc:`Edit Tool Preferences`



.. |image0| image:: /images/edit_tools/edit_mode.gif
.. |image1| image:: /images/edit_tools/add_vertext_mode.gif
.. |image2| image:: /images/edit_tools/remove_vertext_mode.gif
.. |image3| image:: /images/edit_tools/hole_vertex_mode.gif
.. |image4| image:: /images/edit_tools/new_polygon_mode.gif
.. |image5| image:: /images/edit_tools/new_line_mode.gif
.. |image6| image:: /images/edit_tools/new_point_mode.gif
.. |image7| image:: /images/edit_tools/difference_feature_mode.gif
.. |image8| image:: /images/edit_tools/new_circle_mode.gif
.. |image9| image:: /images/edit_tools/new_rectangle_mode.gif
.. |image10| image:: /images/edit_tools/delete_feature_mode.gif
.. |image11| image:: /images/edit_tools/split_feature_mode.gif
.. |image12| image:: /images/edit_tools/merge_feature_mode.gif
.. |image13| image:: /images/edit_tools/outgo_synch.gif
.. |image14| image:: /images/edit_tools/incom_synch.gif
