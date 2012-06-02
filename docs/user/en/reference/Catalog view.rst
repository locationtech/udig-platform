Catalog view
============

The Catalog view is used to explore services and resources known to
the uDig application. Resources in the catalog view can be added to
your map using drag and drop.



The catalog view is populated as import services and you add layers to
your maps. The catalog view is used to track service use and mange the
use of information in the uDig application.



Icons
~~~~~

The Catalog view can show many types of information, including
Shapefiles, Web Map Servers, Web Feature Servers, Spatial Databases,
and a range of raster file formats.
Icon Description Database Datastore Grid Coverage File Grid Coverage
Repository Server Web Feature Service Web Map Service Web Catalog
Service
Right clicking on an icon allows you to access the context menu for a
service or resource.



Toolbar
-------
Import data directly into the catalog, without adding it to a map
Remove a service from the catalog, will only work if the service is
not used by any map.


View Menu
---------

The following commands are available in the view menu:
Command Description Load Load the catalog from disk Save Save the
catalog to disk
The catalog is saved between runs of the uDig application; the above
buttons are simply used to back up your catalog (perhaps before
loading a lot of data into memory).



Context Menu
------------
Service Resource


Add to Current Map
~~~~~~~~~~~~~~~~~~

Add the resource, or service, to the current map. When adding a
service you will be asked to choose which layers.



Add to New Map
~~~~~~~~~~~~~~

Add the resource, or service, to a new map. When adding a service you
will be asked to choose which layers.

The new map will be created with the same projection as the resource;
you can change this behavior in preferences.



Remove
~~~~~~

Used to remove a service from the Catalog view. Please note that since
the Catalog view represent the live uDig application the service may
be added again next time it is used by a Map.



Reset
~~~~~

Reset the connection to the selected service.



Import
~~~~~~

Opens the `Import Wizard`_ allowing you to directly import a service
into the catalog.



Operations
~~~~~~~~~~

Provides access to commonly used operations for the selected service
or resource.

Additional operations may also be available in the menu bar: Edit >
All Operations



Drag and Drop
-------------

You can directly drag URLs and Files into the catalog.

Resources can be dragged from the Catalog view into:


+ `Map editor`_ - add layers on top of the existing map
+ `Layers view`_ - direct add layers in the desired order, not only on
  top


If you drag a Service onto the screen you will be prompted to choose
which layers to add.


+ `Drag and Drop`_
+ `Import Wizard`_


.. _Map editor: Map editor.html
.. _Layers view: Layers view.html
.. _Drag and Drop: Drag and Drop.html
.. _Import Wizard: Import Wizard.html


