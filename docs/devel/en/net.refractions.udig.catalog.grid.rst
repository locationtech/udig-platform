net.refractions.udig.catalog.grid
=================================

The raster pipeline takes a GridCoverage and turns it into a Graphics2D object.
 see `net.refractions.udig.render.features <net.refractions.udig.render.features.html>`_

Functional Requirements
'''''''''''''''''''''''

-  Accept a GridCoverage as input
-  Style the input
-  Reproject the input
-  Output a Graphics2D object

Non-Functional Requirements
'''''''''''''''''''''''''''

-  Perform deformations on the input
-  Give output within two seconds

Design Notes
''''''''''''

-  When zooming in, the raster pipeline will attempt to give immediate feedback by performing a
   manual zoom on the selected area (within one second). It will then go and get the actual
   requested object (within three to five seconds).
-  When zooming out, it will return the image used before the zoom-in was performed, if one is
   available.

