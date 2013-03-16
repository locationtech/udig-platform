Trim Line Tool
##############

.. |image0| image:: /images/trim_line_tool/trim_feature_mode.gif

|image0| Trims LineStrings at a point using a trimming line

The *Trim Tool* uses a *Trimming Line* drawn by the user to cut *LineString* *Features* from the
point where the *Feature* geometries intersect the *Trimming Line*, **to the right** of the
*Trimming Line*.

Behaviour
---------

The *Trim Tool* operates upon the selected features of the current Layer. You can use the *Bounding Box Selection* 
tool or any other feature selection method, to limit the features to trim prior to use the *Trim Tool*. If the 
current Layer has no selection set, the *Trim Tool* will operate over any Feature in the current Layer whose default 
geometry is crossed by the *Trimming Line*.
It is **very important** to understand the way you draw the *Trimming Line* affects the end result, in the sense 
that the source features will be cut from the intersecting point to the **right** of the *Trimming Line*.

When the *Trim Tool* proceeds to cut a feature, it will modify the feature being trimmed by assigning it a new value 
to its default geometry attribute.

.. note::
   The *Trim Tool* does not commit the result, but lets you undo the operation if that
   is desired, or commit the changes to the backend data repository as you would normally do in uDig.

Usage
-----

Select the *Trim Tool* from the drop down buttons list as is shown in *Figure 1*.

.. figure:: /images/trim_line_tool/trim_1_select_tool.png

   **Figure 1. selecting the Trim Tool.**
 
Draw the line string to be used as the *Trimming Line*, making sure it crosses the geometries you
want to divide, as shown in *Figure 2*.


.. tip::
   You can make use of the *Snap Behaviour* to make the *Trimming Line* vertexes snap to vertexes from
   another features.

.. figure:: /images/trim_line_tool/trim_2_draw_trimline.png
   
   **Figure 2. Draw Trim line to cut a LineString.**
   
Double click to add the last *Trimming Line* vertex and indicate the *Trim Tool* to proceed. *Figure 3* 
shows how the LineString on the current layer that was crossed by the *Trimming Line* was cut from the 
intersection point to the right of the *Trimming Line*.

.. figure:: /images/trim_line_tool/trim_3_finish.png
   
   **Figure 3. LineString trimmed.**
