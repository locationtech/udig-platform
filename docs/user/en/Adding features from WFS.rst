Adding features from WFS
########################

Importing Features from WFS
~~~~~~~~~~~~~~~~~~~~~~~~~~~

To import WFS features you can either :

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

Select Web Feature Server Cannot resolve external resource into attachment. from the Wizard and
press **Next**

-  Enter your WFS get capabilitites URL or select recent WFS and press **Next**
    WFS Servers URLs can be found `here <http://udig.refractions.net:8080/confluence/display/UDIG/Test+Servers>`_

-  Specify your authentification parameters if you havre any

.. figure:: /images/adding_features_from_wfs/importwfs.jpg
   :align: center
   :alt: 

-  Advanced checkbox can be used to :

   -  Set request method to GET or POST
   -  Set the maximum features in the buffer
   -  Adjust the request timeout

-  Select layer(s) that will compose the new map and press **Finish**

.. figure:: /images/adding_features_from_wfs/importwfsfeatures.jpg
   :align: center
   :alt: 

-  WMS Layers will be added to the Catalog and a new map containing selected features will show up.

**Related concepts**


:doc:`Service`

 :doc:`Web Feature Server`

