Raster Style Pages
##################

When the :doc:`Style Editor dialog` is opened on a raster layer the
following pages are available.

* `Cache`_

* `Raster Color Mask`_

* `Simple Raster`_

* `Legacy Raster`_

* `Single Band Raster`_

* `XML`_


Cache
=====

Allows control of loading the image into memory.

.. figure:: /images/raster_style_pages/Cache.jpg
   :align: center
   :alt:

Raster Color Mask
=================

The Raster Color Mask makes a single color of a coverage transparent. Often used in satellite images
to indicate areas where no information was recorded.

.. figure:: /images/raster_style_pages/RasterColorMask.jpg
   :align: center
   :alt:

**Related tasks**

:doc:`/tasks/Set raster transparent color`


Simple Raster
=============

Allows simple control over the rendering of a raster image.

.. figure:: /images/raster_style_pages/SimpleRaster.jpg
   :align: center
   :alt:

-  Opacity: allows the transparency of an image to be set; often useful to allow artificial
   boundaries to show through the raster.
-  Scale: Control the scale at which the raster is shown
-  RGB Channel Selection

   Provides control over mapping raster channels to Red, Green and Blue channels for display. The
   gamma of each band can be controlled allowing you to adjust how much of a contribution each band
   makes to the final display.

   -  Band: Allow the selection of a data band. Most processed images are already defied in terms of
      Red, Green and Blue. If you are working with raw satellite information you will need to
      carefully select the correct radar, visual light or infrared band for the analysis being
      performed.
   -  Gamma: Allows fine grain control over the contribution being made.

      -  0-1: Multiplies the contribution; brightening the channel accordingly
      -  1.0: Direct 1 to 1 ratio
      -  1-\*: Minimises the contribution; dimming the channel accordingly

Legacy Raster
===================

Used to handle single band rasters (such as digital elevation models) where you can map value ranges
to artificial colours for display. A number of predefined color maps are provided.

.. figure:: /images/raster_style_pages/SingleBandRasters.jpg
   :align: center
   :alt:

Single Band Raster
===================

An updated interface fo styling single band rasters (ex. digital elevation models).  Users can generate color themes for their
rasters using predefined theme types and color palettes.  This interface also provides tools for generating breaks based on
values from the raster.  Generated values and colors can be manually modified if required.

.. figure:: /images/raster_style_pages/SingleBandRastersNew.png
   :align: center
   :alt:
   
XML
===

This page is used to allow raw access to the xml used to express style information. The XML format
used is the **Stlye Layer Descritor** specificaiton by the Open Geopspatial Consortium.

.. figure:: /images/raster_style_pages/XML.jpg
   :align: center
   :alt:

Validate
--------

Press this button to check that your XML is valid.

**Related concepts**

:doc:`/concepts/Style Layer Descriptor`

