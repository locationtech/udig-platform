Map editor
##########

In the Map Editor,you can visualize and edit spatial information. The spatial information is
organized into a series of Layers.

.. figure:: /images/map_editor/palette-map.png
   :align: center
   :alt: 

Please note that many support views are designed to facilitate the use of the Map Editor; often
making use of it for display and interaction.

Tools
-----

Interaction with the Map Editor is defined by the current tool being used. Tools are primary found
in the tool palette on the right of the map, although custom tools for specific needs can be found
in the appropriate view.

You can hide and show the tool palette by clicking the small arrow on the top right of the palette
(|image0|) and (|image1|). This can be useful if you need more room to view your map and have little
need for the tools.

The selected tool will be displayed with a highlighted background as in the image below

.. figure:: /images/map_editor/palette.png
   :align: center
   :alt: 

To find different tools click the draw corresponding to the tool set you wish to use, each draw will
contain different contextual tools, for instance the Pan Tools draw has tools for panning around the
map and the Editing draw has tools specifically for editing features.

Tools are arranged into the following categories:

.. toctree::
   :maxdepth: 1

   Edit Tools
   Georeferencing Tools
   Information Tools
   Navigation Tools
   Selection Tools


Context Menu
------------

The context-menu depends on the tool being used (and the kind of content it is working with). The
context menu is used as a short cut for the operations available in the :doc:`Edit Menu`.

Status Bar
----------

The status bar is used to make available additional information about the context of the current
map.

Tool Options
~~~~~~~~~~~~

Gives quick access to common tool preferences for the current active tool. Clicking on the tool name
will open the preference page associated with that tool.

.. figure:: /images/map_editor/PanToolOptions.jpg
   :align: center
   :alt: 

Scale
~~~~~

Shows the scale of the map. You can enter a new number here to change the scale to an exact value.

.. _map_editor_crs_display:

Coordinate Reference System Display
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Used to display the current projection of the Map. Clicking on the button will open a dialog
allowing the projection to be changed

Location
~~~~~~~~

Displays the current or last location of the Mouse in the Map Editor. Entering 2 numbers separated
by spaces or commas will centre the map editor at that location.

If LL or LATLONG (case doesn't matter) is at the end, the position will be assumed to be in WGS84
and will be correctly transformed

.. |image0| image:: /images/map_editor/close-palette.png
.. |image1| image:: /images/map_editor/open-palette.png
