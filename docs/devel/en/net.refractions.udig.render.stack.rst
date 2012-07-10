net.refractions.udig.render.stack
=================================

Visualization Stack merges all the rasters(Image Buffers) created by the rendering pipelines with
JAI and updates the viewing area.

Functional Requirements:
''''''''''''''''''''''''

-  Merges context rasters
-  Merges selection rasters
-  Merges decorators
-  Outputs a final raster that will be displayed on screen
-  Manages cached rasters
-  Notifies `net.refractions.udig.project.selection <net.refractions.udig.project.selection.html>`_
   which layers are modified and the bbox (in screen coords) of the selection

Non-functional Requirements:
''''''''''''''''''''''''''''

-  Allows `net.refractions.udig.project.selection <net.refractions.udig.project.selection.html>`_ to
   perform a "quick" selection on the cached rasters

Design notes:
'''''''''''''

-  Takes the rasters from the renderers, their selection equivalents and merges them into one final
   raster.
-  Quick notification of selection activity can be done by applying the selection style to the
   portion of the features within the selected rubber-band.

