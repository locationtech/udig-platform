Georeferencing Tools
--------------------

The category of georeferencing tools is used to help you align a plain image file onto a map.

.. list-table::
   :widths: 30 30 30 
   :header-rows: 1

   * - `Georeferencing Tools`_
     - `Georeferencing Image Tools`_
     - `Georeferencing Marker Tools`_
   * - |image0|
     - |image3|
     - |image6|
   * - |image1|
     - |image4|
     - |image7|
   * - |image2|
     - |image5|
     -


When you have any of the image or marker tools selected, you can right-mouse-click on the map to
rotate between them.

-  Right clicking is an alternative method for rotating between these tools besides selecting them
   from the tool menu.
-  right clicking will only work when one of the georeferencing tools is currently selected (as it
   is within this tool group context that right-clicking works).
-  One right-click will rotate the tool to the next one in the list.

The Load Image, Delete Image and Warp Image tools are not used frequently and must be selected from
the tool menu as normal.

Load Image
~~~~~~~~~~

The Load iamge tool will load new images overtop of your map. These are plain image files such as
jpg or png that you want to georeference onto your map. You can load multiple images and they will
all float above the map.

Warp Image
~~~~~~~~~~

After placing at least 6 markers on both the base map and selected image you can select this tool to
execute the warp process. You will be prompted to save the resulting warped image as a TIF file and
it will be loaded onto the map.

Delete Image
~~~~~~~~~~~~

The delete image tool will delete the selected image. A confirmation box will appear before deleting
the image.

Georeferencing Image Tools
--------------------------

Select Image
~~~~~~~~~~~~

Use this tool to select an image floating overtop of the map. Only the selected image will be
modified when you use the other image editing tools. This tool is useful for selecting a specific
image when you have multiple images loaded overtop of the map. A selected image will have a red
border around it.

Move Image
~~~~~~~~~~

Use this tool to move the selected image around overtop of the background base map. Click and drag
the mouse to move the image. Only the selected image with a red border around it will move.

Resize Image
~~~~~~~~~~~~

Use this tool to resize the selected image overtop of the base map. Click and drag the mouse to
stretch the image. The closest corner you click will be stretched using the same aspect ratio as the
original image. Only the selected image with a red border around it will be resized.

Georeferencing Marker Tools
---------------------------

Place Markers
~~~~~~~~~~~~~

This is the tool you will use to place markers on your selected image and the base map to
georeference the image onto the map. Only the selected image with a red border around it will be
used for this function. You can place as markers as you wish on both the base map and selected
image, with a minimum of 6 required.

Move Markers
~~~~~~~~~~~~

Once you have placed markers on the map and selected image, you can move and adjust them using this
tool.

**Related concepts**

:doc:`Georeference`


**Related tasks**

:doc:`Georeference an Image`


.. |image0| image:: images/georeferencing_tools/ref_load_image.png
.. |image1| image:: images/georeferencing_tools/ref_warp_image.png
.. |image2| image:: images/georeferencing_tools/ref_delete_image.png
.. |image3| image:: images/georeferencing_tools/ref_select_image.png
.. |image4| image:: images/georeferencing_tools/ref_move_image.png
.. |image5| image:: images/georeferencing_tools/ref_resize_image.png
.. |image6| image:: images/georeferencing_tools/ref_place_markers.png
.. |image7| image:: images/georeferencing_tools/ref_move_markers.png
