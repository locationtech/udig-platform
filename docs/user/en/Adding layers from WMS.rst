Adding layers from WMS
######################

Adding layers from a Web Map Server
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

To import Web Map Server layer(s) you can either :

There are a number of options for adding a layer to your map.

Toolbar
=======

1. Press the **New** button on the toolbar to show the Add Layer Wizard
#. Select the **Add** item from the **Layer** menu to show the Add Layer Wizard.
#. Use the **Add Layer** wizard to select a service providing data
#. Select the resource you wish to display
#. A style will be automatically created for you and the layer added to your map

Menu
====

Using **Layer** menu:

1. Select the **Add** item from the **Layer** menu to show the Add Layer Wizard.
#. Use the **Add Layer** wizard to select a service providing data
#. Select the resource you wish to display
#. A style will be automatically created for you and the layer added to your map

Using **File** menu:

1. Select the :menuselection:`File --> New --> Layer` from the file menu
#. Use the **Add Layer** wizard to select a service providing data
#. Select the resource you wish to display
#. A style will be automatically created for you and the layer added to your map

Using Filesystem
================

From filesystem to **Layer View**:

1. You can directly drag a file (such as an image or shapefile) into the **Layer view**
#. You can specify exactly where in the Layer view you wish the add the Layer
#. The layer will be created with a default style and added to your Map

From filesystem to **Map editor**:

1. You can directly drag a file (such as an image or shapefile) onto an open map
#. The layer will be created with a default style and added to your Map

Using a Browser
===============

You can quickly add a layer from a web browser (or the embedded **Web view**):

1. Drag the URL of a WMS or WFS into the **Layer view**
#. You can specify exactly where in the Layer view you wish to add the Layer
#. The resource selection page will open up to allow you to choose which layers to add
#. The layer(s) will be created (with a default style) and added to your map

#. Select Web Map Server Cannot resolve external resource into attachment. from the Wizard and press
   Next

#. Enter your WMS getcapabilitites URL or select recent WMS and press Open
   WMS Link can be found `here <http://www.skylab-mobilesystems.com/en/wms_serverlist.html>`_ or
   `Test servers <http://udig.refractions.net:8080/confluence/display/UDIG/Test+Servers>`_

   |image0|

#. Select layer(s) that will compose the new map and press **Finish**

   |image1|

#. WMS Layers will be added to the Catalog and a new map containing selected layers will show up.

   |image2|

**Related concepts**

:doc:`Service`

:doc:`Web Map Server`


**Related concepts**

:doc:`Drag and Drop`


.. |image0| image:: /images/adding_layers_from_wms/importwms.jpg
.. |image1| image:: /images/adding_layers_from_wms/importwmslayers.jpg
.. |image2| image:: /images/adding_layers_from_wms/impotedwms.jpg
