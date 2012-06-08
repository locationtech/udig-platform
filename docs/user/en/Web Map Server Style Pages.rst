Web Map Server Style Pages
##########################

Web Map Servers allow a little bit of control over how content is drawn. The `Style Editor
dialog <Style%20Editor%20dialog.html>`_ makes use of the following pages when working with a WMS
Layer.

* :doc:`Simple`

* :doc:`WMS Named Style`


The pages displayed depend on the configuration of the web map service you are using.

Simple
======

You can change a few simple settings on a WMS Layer. These settings control how the WMS Layer is
intergrated into your Map.

.. figure:: /images/web_map_server_style_pages/WMSSimplePage.png
   :align: center
   :alt: 

Raster
~~~~~~

You can change the opacity of the resulting WMS Layer. The default value is 100%.

Min Scale
~~~~~~~~~

You can define the minimum scale for which this layer should be displayed. This is useful when
shutting off layers that become pixelated when you zoom too close.

Max Scale
~~~~~~~~~

You can define the maximum scale for which this layer is displayed.

This is useful when working with a WMS Server which is slow when looking at too much content.

WMS Named Style
===============

Web Map Servers sometimes publish a list of alternate stylee

.. figure:: /images/web_map_server_style_pages/WMSNamedStylePage.png
   :align: center
   :alt: 

In the picture above the **jpl nasa** WMS is publishing the blue marble dataset broken up according
to months of the year.

Style
~~~~~

Choose the named style from the available list.

Description
~~~~~~~~~~~

If the named style provides any sort of description it will be shown here.
