Hole Cut Operation
##################

Makes a hole inside a polygon feature using a lineString feature

The *Hole Operation* takes the features from the *Hole layer* and uses the LineString features from
the *Using* to make a hole inside them. The LineString feature must be contained inside the polygon
feature.

The result of the *Hole Operation* could be stored on a new layer, on an existent layer or on the
previously used source layer.

Sample Usage
------------

On this example we are going to do a fill operation with the polygon layer edificios.shp and the
lineString layer carre1.shp.

:doc:`|image0|`

 **Figure 1. Before operation.**

Go to the *Spatial Operations* View, and select *Hole* from the *Operations list* .
 The *Hole Operation*'s specific input options will show up.

Now it's time to indicate the operation's inputs, as shown in *Figure 2*:

-  **Select the** ***Source***
    Select the desired Polygon or MultiPolygon layer to use.
-  **Select the** ***Using***
    Select the LineString or MultiLineString layer that will make holes in.
-  **Select the** ***Result***
    At the *Result Layer* drop down, a tentative Layer name will be proposed for the layer created
   by the operation. You can change the name or select an existing Layer where to store the results.
   This combo will only load existent Polygon or MultiPolygon layer.

:doc:`|image1|`

 **Figure 2. Define input and target layers.**

-  **Perform the operation**
    Once the input and target layers are defined, press the *Perform* button at the *Spatial
   Operations* View tool bar. The operation will begin processing and its progress will be shown up
   on a progress dialog, as shown in *Figure 3*. The operation may take a while to complete,
   depending on the amount and complexity of the input geometries.

:doc:`|image2|`

 **Figure 3. Progress dialog.**

Shows the newly created Layer (Hole\_1) added to the map, with the Features resulting of applying
the Hole operation between the *Hole layer* and the *Using layer*.

:doc:`|image3|`

 **Figure 4. Result Layer.**

.. |image0| image:: download/thumbnails/8388994/hole-before.png
.. |image1| image:: download/thumbnails/8388994/hole-ui.png
.. |image2| image:: download/thumbnails/8388994/hole-progress.png
.. |image3| image:: download/thumbnails/8388994/hole-after.png
