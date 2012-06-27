Adding a column to a shapefile
##############################

You can use the :doc:`Transform operation` to generate additional
attributes and then export the resulting scratch layer.

#. Select the contents of your shape file:

   -  Exapand the shapefile entry in the catalog view and select the contents
   -  Select the layer (if you happen to have the shape file on screen)

#. Run the :doc:`Transform operation`:

   -  Right click to bring up the context menu and choose **Operations > Transform**
   -  From the menu bar :menuselection:`Edit --> All Operations` to open the :doc:`Operations dialog` then select :menuselection:`Resource --> Transform` and press **Operate**

#. This operation will open the :doc:`Transform dialog` listing the current
   attributes along with a definition of how to populate them.
#. Select where you would like to add an attribute
#. Press the **Add** button to create a new entry
#. Fill in the name of your new Attribute
#. Fill in the :doc:`Expression viewer` to define the values for your new
   attribute.

   -  Use :doc:`Constraint Query Language` to define your
      expression
   -  You can choose different options using the arrow to the right

#. Choose how you would like to handle the result: **Add to Catalog**

   -  Add to Map: can be used when processing a layer, in addition to adding to the catalog the
      result is immediately added to the Map as a preview.
   -  Add to Catalog: the results are saved into the **Scratch** working area

#. Press **OK** to start the transform

   -  A new **Scratch** entry containing the result of the operation has been added to the scratch
      service in the **Catalog view**.

#. Right click on the above scratch entry in the catalog view and select **Export** to open the
   :doc:`Export Wizard`

#. Choose **Resource to Shapefile** and press **Next>**
#. Provide a destination directory for your new shapefile
#. Ensure that your scratch entry is checked off for export
#. Press **Finish**
#. The new shapefile is now available in the **Catalog view**

You can define additional columns using any CQL expression, please be advised that the shapefile
format can only support one geometry column.

**Related concepts**

:doc:`Constraint Query Language`


**Related reference**

:doc:`Transform operation`

:doc:`Catalog view`

:doc:`Resource to Shapefile Wizard`
