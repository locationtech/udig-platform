


Raster Style Pages
~~~~~~~~~~~~~~~~~~

When the `Style Editor dialog`_ is opened on a raster layer the
following pages are available.


+ Cache
+ Raster Color Mask
+ Simple Raster
+ Single Band Rasters
+ XML




Cache
=====

Allows control of loading the image into memory.





Raster Color Mask
=================

The Raster Color Mask makes a single color of a coverage transparent.
Often used in satellite images to indicate areas where no information
was recorded.



`Set raster transparent color`_



Simple Raster
=============

Allows simple control over the rendering of a raster image.




+ Opacity: allows the transparency of an image to be set; often useful
  to allow artificial boundaries to show through the raster.
+ Scale: Control the scale at which the raster is shown
+ RGB Channel Selection > Provides control over mapping raster
channels to Red, Green and Blue channels for display. The gamma of
each band can be controlled allowing you to adjust how much of a
contribution each band makes to the final display. <ul> <li>Band:
Allow the selection of a data band. Most processed images are already
defied in terms of Red, Green and Blue. If you are working with raw
satellite information you will need to carefully select the correct
radar, visual light or infrared band for the analysis being
performed.< li>
+ Gamma: Allows fine grain control over the contribution being made.

    + 0-1: Multiplies the contribution; brightening the channel
      accordingly
    + 1.0: Direct 1 to 1 ratio
    + 1-*: Minimises the contribution; dimming the channel accordingly





Single Band Rasters
===================

Used to handle single band rasters (such as digital elevation models)
where you can map value ranges to artificial colours for display. A
number of predefined color maps are provided.





XML
===

This page is used to allow raw access to the xml used to express style
information. The XML format used is the **Stlye Layer Descritor**
specificaiton by the Open Geopspatial Consortium.





Validate
--------

Press this button to check that your XML is valid.

`Style Layer Descriptor`_

.. _Set raster transparent color: Set raster transparent color.html
.. _Style Layer Descriptor: Style Layer Descriptor.html
.. _Style Editor dialog: Style Editor dialog.html


