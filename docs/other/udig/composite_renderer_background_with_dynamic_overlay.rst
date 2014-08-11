Composite Renderer Background With Dynamic Overlay
##################################################

uDig : Composite Renderer Background with Dynamic Overlay

This page last changed on Jun 23, 2010 by jgarnett.

We are looking at building a rendering stack that holds a CompositeRenderer for a "background" and
has a dynamic "overlay" displayed on top of it.

`Overview <#CompositeRendererBackgroundwithDynamicOverlay-Overview>`__

-  `Diagram <#CompositeRendererBackgroundwithDynamicOverlay-Diagram>`__

`Questions <#CompositeRendererBackgroundwithDynamicOverlay-Questions>`__

-  `Who is in charge? <#CompositeRendererBackgroundwithDynamicOverlay-Whoisincharge%3F>`__
-  `What is a Renderer+RenderContext
   called? <#CompositeRendererBackgroundwithDynamicOverlay-WhatisaRendererRenderContextcalled%3F>`__
-  `Where is the timer delay
   length <#CompositeRendererBackgroundwithDynamicOverlay-Whereisthetimerdelaylength>`__

`How does Composition
Occur? <#CompositeRendererBackgroundwithDynamicOverlay-HowdoesCompositionOccur%3F>`__

`Updated Images after UDIG
Meeting <#CompositeRendererBackgroundwithDynamicOverlay-UpdatedImagesafterUDIGMeeting>`__

Overview
========

The idea is that the "background" will be constructed from regular layers such as WMS or WFS layers
and built up into a composite that is cached or static. On top of this is a layer that will be
refreshing with updated data very often (such as moving vehicles). The result should be a background
that doesn't need to render every time the dynamic overlay is updated, which means faster rendering.

Diagram
-------

Below is an image of the rendering stack idea with a description:

|image0|

#. We are uncertain what the top-level controller is for this stack (RenderManager?)
#. The dynamic "overlay" that is redrawn/updated often with new data
#. The "background" composite (CompositeRenderer?) which handles constructing the background image
   from various layers
#. Various layers, such as WMS/WFS/TiledWMSC which build up into the background

Notes:

-  The dynamic layer is expected to send a refresh event 3 times per second
-  The `flock demo <http://udig.refractions.net/files/docs/flockdemo.htm>`__ is an example layer
   that refreshes often
-  `uDig-UIandFrameworkRecommendations.pdf <http://udig.refractions.net/files/docs/uDig-UIandFrameworkRecommendations.pdf>`__
   Section 3.7 outlines the user of events and timers
-  HACK:09 Renderers contains a description of the above classes

Questions
=========

Who is in charge?
-----------------

We are currently uncertain how much of this will work within the current rendering system and what
needs to be added. We believe that the current CompositeRenderer can be used for the "background",
but we aren't sure what the parent or top-level controller should be.

A: RenderExecutorComposite

What is a Renderer+RenderContext called?
----------------------------------------

It looks like a CompositeRenderer keeps a number of child Renderers; I assume it owns the
RenderContext for the children and uses them to draw as needed? What is one of these child entries
called?

Where is the timer delay length
-------------------------------

As the Renderers issue state changes a timer is used to control how long to wait before the
"composition" step takes place; we will need to change the amount of this delay based on what is
going on - where in the code should we look?

How does Composition Occur?
===========================

Currently it uses a for loop - apprently Jesse talked to Simone and this was as good a way as any.

For historical interest here is the section of the "UI and Framework Recommendations" pdf where
using JAI for composition was considered.

**Section 3.7 Rendering**

The rendering architecture is organized as a set of renderers, a timer that polls the renderers for
updates and a Java Advanced Imaging (JAI) tree that combines all the rendered images from the
renderers into one image that is displayed. Figure 4 illustrates the current behaviour of the
rendering system.

|image1|

Figure 4 - Current JAI Rendering System

| L1 through L4 are four renderers, each rendering a different layer. The x-axis shows time
increasing from left to right. The solid lines beside L1-L4, beginning with a bar and ending with an
arrow, show the execution of the renderers. The bar indicates the time when the first data arrives
from the data source, and the arrow indicates the end of execution. The dotted lines indicate that
the rendered
|  image is complete. The vertical lines indicate the rasters being merged and displayed; the black
dots indicate which layers are being merged. The line beside T is the timer thread. As Figure 4
illustrates, the timer currently wakes up at regular intervals and obtains the image raster from
each layer, regardless of whether the image has begun rendering. In addition, because the timer
thread wakes every second, each renderer can initiate a display update. The last point to observe is
that all layers are merged each time an update occurs.

|image2|

Figure 5 - Recommended JAI Rendering System

Figure 5 illustrates some improvements on the current implementation.

The first improvement is illustrated in the left-most vertical line. The update thread only merges
rasters that have begun rendering. The second improvement is that the timer thread is reset each
time a renderer initiates an update. Finally, adjacent rasters that have been completed are kept in
a buffer so they can be merged as one raster. These improvements have yet to be implemented.

Updated Images after UDIG Meeting
=================================

This image described how our new renderer will work.  It will make sure of the render executor
composite and rendering metrics to determine which layers to draw on top and which layers need there
own render executer composite.

|image3|

| 

Attachments:

| |image4|
`composite\_render\_stack.jpg <download/attachments/5767181/composite_render_stack.jpg>`__
(image/jpeg)
|  |image5| `render2.PNG <download/attachments/5767181/render2.PNG>`__ (image/png)
|  |image6| `render1.PNG <download/attachments/5767181/render1.PNG>`__ (image/png)
|  |image7|
`composite\_render\_stack\_3.jpg <download/attachments/5767181/composite_render_stack_3.jpg>`__
(image/jpeg)

+------------+----------------------------------------------------------+
| |image9|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+------------+----------------------------------------------------------+

.. |image0| image:: /images/composite_renderer_background_with_dynamic_overlay/composite_render_stack.jpg
.. |image1| image:: /images/composite_renderer_background_with_dynamic_overlay/render1.PNG
.. |image2| image:: /images/composite_renderer_background_with_dynamic_overlay/render2.PNG
.. |image3| image:: /images/composite_renderer_background_with_dynamic_overlay/composite_render_stack_3.jpg
.. |image4| image:: images/icons/bullet_blue.gif
.. |image5| image:: images/icons/bullet_blue.gif
.. |image6| image:: images/icons/bullet_blue.gif
.. |image7| image:: images/icons/bullet_blue.gif
.. |image8| image:: images/border/spacer.gif
.. |image9| image:: images/border/spacer.gif
