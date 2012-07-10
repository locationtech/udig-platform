net.refractions.udig.project.context
====================================

The context is used to manage the layer data inside the LayerManager. It retains information about
each layer to be displayed and other information pertaining to all of the layers, such as SRS,
BoundingBox and abstract.

Functional Requirements:
''''''''''''''''''''''''

-  BBox and SRS
-  Models
   :doc:`net.refractions.udig.project.layer <net.refractions.udig.project.layer.html>`_/`net.refractions.udig.render.decorator`

   state, maintains Z order
-  Metadata (abstract, title, etc. from WMS Context Document)
* :doc:`net.refractions.udig.project.ui.layerManager`

   "controls" the Context in a MVC sense
-  Captures all the information for Rendering (contrast w/
   `net.refractions.udig.printing.context <net.refractions.udig.printing.context.html>`_)
-  Throws events when modified (like every good view should)

Non-functional Requirements:
''''''''''''''''''''''''''''

-  export/import a WMS Context Document

Design Notes
''''''''''''

-  This represents a strong separation between user's request and how we fulfill that request
   (allows
   :doc:`net.refractions.udig.project.ui.layerManager`

   to use alternate services, or cache for the response).
-  Context will keep track of decorators, such as a legend, scale bar or compass. This would allow
   it to be passed down to either the printer or the screen renderer.
-  Communicates with the
   :doc:`net.refractions.udig.project.ui.layerManager`

   regarding Layer information

