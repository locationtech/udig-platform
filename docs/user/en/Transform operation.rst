Transform operation
###################

The transform operation allows for general manipulation and processing of feature content. The
transform operation is available from the :doc:`Operations dialog` or using
the context menu when selecting feature data.

Selection
=========

The operation works with the following selections:

-  A selected GeoResource in the :doc:`Catalog view`. Please note you will need to
   open up the service in order to select the GeoResource contents it advertises for use. This
   technique is often used to process an entire Shapefile, database table or WFS feature type.
-  A selected Layer in the :doc:`Layers view`. Please note that the operation will
   limit itself to the features you have selected on the screen. If you have not selected any
   features (to limit the operation) we assume you wish the entire layer to be processed.

Input
-----

The operation opens the :doc:`Transform dialog` which is used to define how
you would like the information processed.

.. figure:: /images/transform_operation/TransformDialog.png
   :align: center
   :alt: 

Result
------

The result is saved out to the scratch area of the catalog; you can use this as a staging area to
preview content before exporting it out to a shapefile or database table for long term storage.

If you have asked to display the result on the map, this scratch GeoResource is automatically added
to your map allowing you to preview the result directly.

**Related tasks**


:doc:`Working with Features`

:doc:`Working with Layers`


**Related reference**


:doc:`Transform dialog`

:doc:`Operations dialog`

:doc:`Layers view`

:doc:`Catalog view`

