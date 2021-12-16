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

.. _pref_project_DISABLE_CRS_SELECTION:

Disable CRS Chooser
```````````````````
In the Map Editor is an option to choose different :ref:`Coordinate Reference System <map_editor_crs_display>` for the Map. See
`EPSG code of default CRS`_ how to set a different default Coordinate Reference System for maps using an EPSG code.

In case the organization would like to disallow to change CRS, the Preference Constant

.. code-block:: python

    org.locationtech.udig.project/DISABLE_CRS_SELECTION=true

can be used to disable CRS Chooser Dialog. If not set - this is the default (*false*) - the
Application behaves like before. There is no option for this Prefenences in the Preferences Dialog to change behavior on demand.

.. _pref_project_HIDE_RENDER_JOB:

Hide Render Jobs in Progress View
`````````````````````````````````
In case of a Map has many layers configured and the Progress Views is open, lots of processes are shown "Rendering ..".
The follwoing Prefenences Constant allows to hide Rendering Jobs displayed in Progress View. It helps to see the state
of other long-running Tasks.

.. code-block:: python

    org.locationtech.udig.project/HIDE_RENDER_JOB=true

can be used to hide Render Jobs. If not set - this is the default (*false*) - the
Application behaves like before and the Jobs are shown in Progress View. There is no option for this Prefenences in
the Preferences Dialog to change behavior on demand.

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

.. code-block:: python

    org.locationtech.udig.project/defaultCRSPreference=<EPSG-Code>

For <EPSG-Code> use the number such es 4326 for EPSG:4326.

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
