Style Editor dialog
###################

The style editor dialog is used to modify the how a layer is displayed on screen.

.. figure:: /images/style_editor_dialog/StyleEditor.png
   :align: center
   :alt: 

Style Pages
~~~~~~~~~~~

A tree of style pages for the selected content is displayed. Each page allowing the modification of
one aspect of the visualization process.

.. toctree::
   :maxdepth: 1

   Feature Style Pages
   Raster Style Pages
   Web Map Server Style Pages


The style pages used by Map Graphics are each unique; for more information please visit:

* :doc:`Grid Decoration`

* :doc:`JGrass Raster Legend`

* :doc:`JGrass Vector Legend`

* :doc:`Legend Decoration`

* :doc:`North Arrow Decoration`

* :doc:`Scalebar Decoration`

* :doc:`The Processing Region`


Apply
~~~~~

Press this button to update the Map with the current settings.

Revert
~~~~~~

Reset the style pages to their previous settings.

Close
~~~~~

Dismiss the style editor.

Import
~~~~~~

Import style settings from an **sld** file.

Export
~~~~~~

Export style settings to an **sld** file.

If you create an **sld** file that matches a shapefile uDig will be able to use this as the default
style when loading the layer into another map.

**Related reference**

:doc:`Style View`
