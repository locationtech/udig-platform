


Processing the Geometry in a Shapefile
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

You can use the **Reshape** operation to modify the geometry column
and then export the resulting scratch layer.


+ Converting Polygons to Points using Centroid
+ Converting Lines or Points into Polygons using Buffer



+ `Constraint Query Language`_



+ `Transform operation`_
+ `Catalog view`_
+ `Resource to Shapefile Wizard`_




Converting Polygons to Points using Centroid
============================================


#. Select the contents of your shapefile

    + Exapand the shapefile entry in the catalog view and select the
      contents
    + Select the layer (if you happen to have the shape file on screen)

#. Run the **Transform** operations, this operation will show you a
   the current attributes being generated, along with their type and an
   expression used to calculate values.
#. Select **the_geom** in the Transform table
#. Define the expression used to generate your geometry value:
   centroid(the_geom)
#. Press **OK**. A new scratch entry containing the result of the
   operation has been added to the scratch service in the **Catalog
   view**.
#. Right click on the above scratch entry in the catalog view and
   select **Export** to open the `Export Wizard`_
#. Choose **Resource to Shapefile** and press **Next>**
#. Provide a destination directory for your new shapefile
#. Ensure that your scratch entry is checked off for export
#. Press **Finish**
#. The new shapefile is now available in the **Catalog view**


You can define additional columns using any CQL expression, please be
advised that the shapefile format can only support one geometry
column.



Converting Lines or Points into Polygons using Buffer
=====================================================


#. Select the contents of your shapefile

    + Exapand the shapefile entry in the catalog view and select the
      contents
    + Select the layer (if you happen to have the shape file on screen)

#. Run the **Transform** operations, this operation will show you a
   the current attributes being generated, along with their type and an
   expression used to calculate values.
#. Define the process desired for the **the_geom** expression:

::

    buffer(the_geom,0.01)


#. Press **OK**. A new scratch entry containing the result of the
   operation has been added to the scratch service in the **Catalog
   view**.
#. Right click on the above scratch entry in the catalog view and
   select **Export** to open the `Export Wizard`_
#. Choose **Resource to Shapefile** and press **Next>**
#. Provide a destination directory for your new shapefile
#. Ensure that your scratch entry is checked off for export
#. Press **Finish**
#. The new shapefile is now available in the **Catalog view**


You can define additional columns using any CQL expression, please be
advised that the shapefile format can only support one geometry
column.

.. _Constraint Query Language: Constraint Query Language.html
.. _Catalog view: Catalog view.html
.. _Export Wizard: Export Wizard.html
.. _Transform operation: Transform operation.html
.. _Resource to Shapefile Wizard: Resource to Shapefile Wizard.html


