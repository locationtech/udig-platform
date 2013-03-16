Changing the projection of a shapefile
######################################

In general uDig lets you work with shapefiles of different projections all on the same screen
together. If you need to save a shapefile out in a specific projection for another application
please use the following procedure.

#. Select the contents of your shapefile in the **Catalog view**; or by selecting a Layer if you
   have the shapefile on screen.
#. Open the **Export wizard** and choose **Resource to Shapefile** and press **Next>**
#. Chose a destination directory for your new shapefile:
    C:\\Documents and Settings\\Jody\\Desktop\\data
#. Ensure your shapefile is checked off on in the list
#. Click on the current projection, example GCS\_EGS1984, to open up a CRS chooser
#. Type in the name of the desired projection, example UTM zone 12N, and select the desired EPSG
   code from the list. You can confirm the details by looking at the Custom CRS tab
#. Press **Finish**, your newly created shapefile is available in the **Catalog view**

**Related concepts**

:doc:`/concepts/Shapefile`


**Related reference**

:doc:`/reference/Resource to Shapefile Wizard`
