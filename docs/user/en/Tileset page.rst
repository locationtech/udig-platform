Tileset page
############

The TileSet dialog is a properties page used to allow a resource to be used as a tileset even when
it is not backed onto a WMS-C tile cache.

.. figure:: /images/tileset_page/properties_page.png
   :align: center
   :alt: 

**Related tasks**


:doc:`Enabling a Tileset from a WMS Server`


Accessing the properties page
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The page can be opened on any layer:

.. figure:: /images/tileset_page/open_page.png
   :align: center
   :alt: 

Use a Tileset for this resource
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Check this checkbox to enable using tilesets for this resource. By pressing this a set of default
scales will be provided - these are taken from the viewport scales.

Width/Height
~~~~~~~~~~~~

The width and height of the tiles - default is 265

Image type
~~~~~~~~~~

The type of image expected from the WMS server

Scales
~~~~~~

The scales need to be specified for the tileset - WMS-C, you can add more if you know the scales
your tileset will work at. These will be used to calculate resolutions for the TileSet.
