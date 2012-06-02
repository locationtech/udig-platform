


Transform operation
~~~~~~~~~~~~~~~~~~~

The transform operation allows for general manipulation and processing
of feature content. The transform operation is available from the
`Operations dialog`_ or using the context menu when selecting feature
data.



Selection
=========

The operation works with the following selections:


+ A selected GeoResource in the `Catalog view`_. Please note you will
  need to open up the service in order to select the GeoResource
  contents it advertises for use. This technique is often used to
  process an entire Shapefile, database table or WFS feature type.
+ A selected Layer in the `Layers view`_. Please note that the
  operation will limit itself to the features you have selected on the
  screen. If you have not selected any features (to limit the operation)
  we assume you wish the entire layer to be processed.




Input
-----

The operation opens the `Transform dialog`_ which is used to define
how you would like the information processed.





Result
------

The result is saved out to the scratch area of the catalog; you can
use this as a staging area to preview content before exporting it out
to a shapefile or database table for long term storage.

If you have asked to display the result on the map, this scratch
GeoResource is automatically added to your map allowing you to preview
the result directly.

`Working with Features`_
> <a href="Working with Layers.html" title="Working with
Layers">Working with Layers< a>

`Transform dialog`_
> <a href="Operations dialog.html" title="Operations
dialog">Operations dialog< a>
> <a href="Layers view.html" title="Layers view">Layers view< a>
> <a href="Catalog view.html" title="Catalog view">Catalog view< a>

.. _Layers view: Layers view.html
.. _Transform dialog: Transform dialog.html
.. _Catalog view: Catalog view.html
.. _Working with Features: Working with Features.html
.. _Operations dialog: Operations dialog.html


