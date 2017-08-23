Project Preferences
===================

Project preferences and its children provides options for modifying the defaults and behavior of
creating projects and the elements that can be contained by projects.

.. _preferences-page-project-layer:

General Preferences
-------------------

These general settings are used for all type of project-elements such as layers and maps.

.. figure:: /images/preferences/project_prefs.png
   :align: center
   :alt:

Delete project files?
`````````````````````

Indicates to remove file resources from filesystem once the maps or layers were removed from map.

Default is checked

Undo history size
`````````````````

Number of possible steps to undo

Default is 10


Layer Preferences
-----------------

.. figure:: /images/preferences/project_layer_prefs.png
   :align: center
   :alt:

Color Highlighting
``````````````````
TBD

Default is None

.. _preferences-page-project-map:

Map Preferences
---------------

Map Preference page is used to supply system wide defaults for the Map Editor.

.. figure:: /images/preferences/project_map_prefs.png
   :align: center
   :alt:

Remove Temporary Layers on exit
```````````````````````````````

When checked this option will remove temporary layers from the map. If it is not checked the layer
will stay in the map, but all data is lost since the layer is temporary. This option can be useful
if the FeatureType is complex and recreating the FeatureType can be time consuming.

Default is checked

Warn when an irreversible command is about to be executed
`````````````````````````````````````````````````````````

TBD

Default is checked

EPSG code of default CRS
````````````````````````

Declares what the default CRS is, a value of -1 is used to indicate that the value should be
determiend from the data.

You may wish to change this setting when working with a large number of shapefiles that make use of
the same projection.

The default value is -1.

Default map background color
````````````````````````````

The color of the map's background.

Default is white.

Selection Color
```````````````

The color that selected feature will be drawn in.

Default is dark yellow.

Selection Color 2
`````````````````

TBD

Default is black.

Default Palette
```````````````

TBD


Printing Preferences
--------------------

.. figure:: /images/preferences/project_printing_prefs.png
   :align: center
   :alt:

Default Template
````````````````

Declares what page template should be used by default.
