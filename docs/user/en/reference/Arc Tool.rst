


Arc Tool
~~~~~~~~

Creates a linestring approximating an arc given three control points
(initial point, axis, and end point).

The *Arc Tool* allows to create a Feature whose Geometry is a linear
approximation of an arc of circumference, by specifying three points
which define two consecutive arc chords.




Behaviour
---------


The *Arc Tool* is a Feature *creation* tool and operates over the
currently selected Layer, thus the currently selected Layer must allow
LineString geometries, whether its feature types specifies them to be
LineString or MultiLineString.
When the *Arc Tool* proceeds to create the new Feature, it does not
commits it, allowing the user to undo the operation, set the Feature
attribute values or commit the changes as usual in uDig.




Visual consistency
~~~~~~~~~~~~~~~~~~


In order to preserve visual consistency, and thus be consistent with
what the user is doing, the geometry created by the *Arc Tool* will be
first created in the **Map's CRS**, and then projected to the backend
CRS for storage. This allows the user to create the arc in the desired
CRS by setting the Map CRS to a meaningful one for his purposes. A
side effect will be, that if the data and Map CRS differ, the geometry
will be an arc of circumference in the Map CRS but probably not in the
data CRS.




Usage
-----


As shown in *Figure 1*, to start using the *Arc Tool* you have to
first select it from the drop down menu in the uDig's editting tools
Toolbar.
> <img src="download attachments/2425123/arc_1.png" align="left"
border="0"/>
**Figure 1 select** ** *Arc Tool***

Once the tool is selected, you need to specify three points that
defines the arc. For any of the three points you can use the **snap**
area, if activated, in order to snap to a vertex from another Feature.
> <em>Figure 2< em> shows the feedback shown when only two points have
been specified. You'll see the resulting arc at any moment until the
third point is specified.
> <img src="download attachments/2425123/arc_2.png" align="left"
border="0"/>
**Figure 2 specify control points**

With a single click, specify the third point and the Feature with an
arc geometry will be created as shown in *Figure 3*. The resulting
geometry will be a LineString approximation of the actual arc.
> <img src="download attachments/2425123/arc_3.png" align="left"
border="0"/>
**Figure 3 arc created after specifying third point**



