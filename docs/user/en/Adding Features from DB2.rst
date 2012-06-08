Adding Features from DB2
########################

Adding Features from a DB2
~~~~~~~~~~~~~~~~~~~~~~~~~~

To import data from a DB2 you can either :

There are a number of options for adding a layer to your map.

Toolbar
=======

1. Press the **New** button on the toolbar to show the Add Layer Wizard
 1. Select the **Add** item from the **Layer** menu to show the Add Layer Wizard.
 2. Use the **Add Layer** wizard to select a service providing data
 3. Select the resource you wish to display
 4. A style will be automatically created for you and the layer added to your map

Menu
====

Using **Layer** menu:
 1. Select the **Add** item from the **Layer** menu to show the Add Layer Wizard.
 2. Use the **Add Layer** wizard to select a service providing data
 3. Select the resource you wish to display
 4. A style will be automatically created for you and the layer added to your map

Using **File** menu:

1. Select the **File > New > Layer** from the file menu
 2. Use the **Add Layer** wizard to select a service providing data
 3. Select the resource you wish to display
 4. A style will be automatically created for you and the layer added to your map

Using Filesystem
================

From filesystem to **Layer View**:
 1. You can directly drag a file (such as an image or shapefile) into the **Layer view**
 2. You can specify exactly where in the Layer view you wish the add the Layer
 3. The layer will be created with a default style and added to your Map

From filesystem to **Map editor**:
 1. You can directly drag a file (such as an image or shapefile) onto an open map
 3. The layer will be created with a default style and added to your Map

Using a Browser
===============

You can quickly add a layer from a web browser (or the embedded **Web view**):
 1. Drag the URL of a WMS or WFS into the **Layer view**
 2. You can specify exactly where in the Layer view you wish to add the Layer
 3. The resource selection page will open up to allow you to choose which layers to add
 4. The layer(s) will be created (with a default style) and added to your map

#. If you have not set your DB2 preference (**Window > Preferences...**) then you will have the
   chance to do so now. See **`DB2 Preferences <DB2%20Preferences.html>`_** for instructions on how
   to do this
#. Select DB2 from the Wizard and press **Next**
#. Fill in the DB2 page

   -  Enter your DB2 host and authentication parameters
   -  Select the database connect and optionally a schema

#. Press **Next**
#. From the `Resource Selection page <Resource%20Selection%20page.html>`_ select the layer(s) that
   will compose the new map
#. Press **Finish**

The selected DB2 tables will be added to the Catalog and a new map containing selected Layers will
show up.

**Related reference**


* :doc:`DB2 page`

* :doc:`DB2 Preferences`


