Georeference an Image
#####################

The process of "georeferencing" an image allows you to place a normal PNG or JPEG image onto a map.
Many spatial image contain enough information allowing them to be placed in the correct location
automatically.

The following steps provide an example of how to georeference an image overtop of your base map from
start to finish.

#. Load a base map layer with a proper projection.
#. Zoom to the area of the map where you want to add the image.
#. Load an image overtop of the map. Use the **Load Image** tool and browse to the image you wish to
   add to the map:

   -  The image will be loaded ontop the bottom left of the map
   -  The image will be selected
   -  The image 1/3 of the size of the current map.

#. Move the image beside the final location so you can see where to put placemarkers on both the
   image and the basemap.

   -  Use the **Select Image** tool (if you have more than one image).
   -  Use the **Move Image** tool and move the image to an area of the map that allows us to see
      both the image and the part of the base map you want to work with.
   -  Use the **Resize Image** tool until you are satisfied that you can easily see both the image
      and the base map.

#. Select the Place Markers tool (a popup window will appear with some instructions).

   -  Place a minimum of 6 markers on both the image and the base map that line up with each other.
   -  Place more than 6 markers for more accurate results.
   -  Check or uncheck the box to remove any previous markers before starting to place new ones.
      This is used to resume placing markers if you needed to move the image half way through.

#. Click OK to start placing new markers in the order of:

   #. place marker 1 on the image
   #. place the equivalent marker 1 on the base map
   #. repeat until at least 6 markers are on both the image and base map:

#. Continue placing markers until you are satisfied that the important areas are covered.

   -  You can also select the **Move Markers** tool to fine tune your marker placement anytime.

#. Select the **Warp Image** tool to begin warping the image based on your marker placement

   -  The image will begin the warp process
   -  When finished a dialog will ask you to save the result as a TIF file

#. Save the file and it will be loaded onto the map (replacing the original image)

You will now have a warped image saved as a TIF file that is loaded on your map.

Tips
----

-  If you are not satisfied with the result, you can try again starting from step 3.
-  Remember that placing more markers (and placing them in important areas that you want to ensure
   line-up) will return better results.
-  Note that when this image is warped to fit onto the base map, the empty space left in the warped
   image will be transparent.

   -  However, if the initial image is not rectangular and has a background color that is not
      tranparent, that background color will still remain. If you want this background color to be
      transparent, edit the image in an image editor and save it with a transparent background
      before loading it into uDig.

**Related concepts**

:doc:`Georeference`


**Related reference**

:doc:`Georeferencing Tools`

