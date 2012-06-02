Adding Features from ArcSDE
~~~~~~~~~~~~~~~~~~~~~~~~~~~

To import data from ArcSDE you can either :

There are a number of options for adding a layer to your map.



Toolbar
=======

1. Press the **New** button on the toolbar to show the Add Layer
Wizard
> 1. Select the <b>Add< b> item from the **Layer** menu to show the
Add Layer Wizard.
> 2. Use the <b>Add Layer< b> wizard to select a service providing
data
> 3. Select the resource you wish to display<br > 4. A style will be
automatically created for you and the layer added to your map



Menu
====

Using **Layer** menu:
> 1. Select the <b>Add< b> item from the **Layer** menu to show the
Add Layer Wizard.
> 2. Use the <b>Add Layer< b> wizard to select a service providing
data
> 3. Select the resource you wish to display<br > 4. A style will be
automatically created for you and the layer added to your map

Using **File** menu:

1. Select the **File > New > Layer** from the file menu
> 2. Use the <b>Add Layer< b> wizard to select a service providing
data
> 3. Select the resource you wish to display<br > 4. A style will be
automatically created for you and the layer added to your map



Using Filesystem
================

From filesystem to **Layer View**:
> 1. You can directly drag a file (such as an image or shapefile) into
the <b>Layer view< b>
> 2. You can specify exactly where in the Layer view you wish the add
the Layer<br > 3. The layer will be created with a default style and
added to your Map

From filesystem to **Map editor**:
> 1. You can directly drag a file (such as an image or shapefile) onto
an open map<br > 3. The layer will be created with a default style and
added to your Map



Using a Browser
===============

You can quickly add a layer from a web browser (or the embedded **Web view**):

1. Drag the URL of a WMS or WFS into the <b>Layer view< b>

2. You can specify exactly where in the Layer view you wish to add
   the Layer<br > 3. The resource selection page will open up to allow
   you to choose which layers to add

4. The layer(s) will be created (with a default style) and added to your map

5. Select ArcSDE Cannot resolve external resource into attachment. from the Wizard

6. Enter your ArcSDE connection parametersUsed to add data from an ArcSDE server.
   The first time you run this wizard you will be asked to locate some ESRI jars
   included with your server software.

Connection Parameters
=====================

Host
----
Server name (or ip address) where the ArcSDE gateway is running.


Port
----
Port number in wich the ArcSDE server is listening for connections.
The default value is 5151


Credentials
-----------


    + username - The name of a valid database user account
    + password - The database user's password



Database
--------
   The specific database to connect to. Only applicable to certain
   databases. Value ignored if not applicable.

    + `ArcSDE Preference Page`_

#. Press **Next**
#. Select layer(s) that will compose the new map
#. Press **Finish**


ArcSDE features will be added to the Catalog and a new map containing
selected Layers will show up.


.. _ArcSDE Preference Page: ArcSDE Preference Page.html


