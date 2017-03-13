Resource to Shapefile Wizard
############################

Used to export a resource (often selected in the Catalog view) to a shapefile. This wizard is
typically used to export contents from a remote WFS or Spatial database local storage.

The results of this wizard are added to the Catalog for your immediate use.

.. figure:: /images/resource_to_shapefile_wizard/ExportResourceToShapefile.png
   :align: center
   :alt:

Please note that only feature content can be exported to a shapefile. In the above picture
clouds.jpg cannot be selected as it is an image file.

Destination
~~~~~~~~~~~

The location to save the generated shapefile. The filename used will be will be based on the
resource name.

Resource List
~~~~~~~~~~~~~

List of selected resources:

-  Check: you may check off the resources you wish to export
-  Name: Resource name, this will be used as the filename of the generated shapefile
-  Projection: Shows the projection of the resource; you can change setting to transform the
   contents into the requested projection. The projection information will be stored in the
   resulting **prj** file.
-  Charset: Shapefile charset encoding; you can control the encoding of string fields during
   export. The proposed value is controlled by the startup system property **shp.encoding** or if not 
   specified by the preference value **General-->udig UI-->Default Character Set**      

**Related concepts**

:doc:`/concepts/Shapefile`
