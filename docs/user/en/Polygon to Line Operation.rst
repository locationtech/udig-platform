Polygon to Line Operation
#########################

Transform a polygon layer into a LineString layer

The *Polygon to Line Operation* takes the features from the *source layer* and transform them into
lineString features.

The result layer will be a LineString or MultiLineString layer, depends on the source layer geometry
type, and it could be a new created layer or an existent one.

Sample Usage
------------

On this example we are going to do a Polygon to Line operation with the polygon layer countries.shp.

:doc:`|image0|`

 **Figure 1. Before operation.**

Go to the *Spatial Operations* View, and select *Polygon to Line* from the *Operations list* .
 The *Polygon to Line Operation*'s specific input options will show up.

Now is time to indicate the operation's inputs, as shown in *Figure 2*:

-  **Select the** ***Source***
    Select the desired Polygon or MultiPolygon layer to transform.
-  **Select the** ***Result***
    At the *Result Layer* drop down, a tentative Layer name will be proposed for the layer the
   operation will create. You can change the name or select an existing Layer where to store the
   results. This combo will only load existent LineString or MultiLineString layer.

:doc:`|image1|`

 **Figure 2. Define input and target layers.**

-  **Perform the operation**
    Once the input and target layers are defined, press the *Perform* button at the *Spatial
   Operations* View tool bar. The operation will begin processing and its progress will be shown up
   on a progress dialog, as shown in *Figure 3*. The operation may take a while to complete,
   depending on the amount and complexity of the input geometries.

:doc:`|image2|`

 **Figure 3. Progress dialog.**

Shows the newly created Layer (Polygon\_to\_line\_1) added to the map, with the Features resulting
of applying the polygon to line operation to the *Source layer*.

:doc:`|image3|`

 **Figure 4. Result Layer.**

Explode into lines.
~~~~~~~~~~~~~~~~~~~

*Polygon to Line* operation has the option of exploding polygons, this means that every line of each
polygon will be added as a single feature.

:doc:`|image4|`

 **Figure 5. Selection explode.**

In this case, select the country of Ireland, and then check the *explode polygons into lines*
option, execute the operation giving as result the next:

:doc:`|image5|`

 **Figure 6. Exploded lines.**

.. |image0| image:: download/thumbnails/8388985/ptl-before.png
.. |image1| image:: download/thumbnails/8388985/ptl-ui.png
.. |image2| image:: download/thumbnails/8388985/ptl-progress.png
.. |image3| image:: download/thumbnails/8388985/ptl-after.png
.. |image4| image:: download/thumbnails/8388985/ptl-explode2.png
.. |image5| image:: download/thumbnails/8388985/ptl-explode1.png
