net.refractions.udig.render
===========================

The Rendering Manager is the core controller between the datastores, context, and renderers.

Functional Requirements
'''''''''''''''''''''''

-  Obtains DataSource and GridCoverageExchange objects from Layer Manager
-  Reads Context and creates Renderers for the entries in the Context
-  Creates decoration renderers, ie legend renderers
-  Creates functional renderers, ie editing renderers.
-  Creates virtual renderers, ie renderers derived from other renderers.
-  creates layered renderers, ie renderers that contain multiple renderers that compete to complete
   a request
-  Handles Layer visibility change events
-  Handles layer selectability events

Non-functional
''''''''''''''

-  Decide which services to use for rendering and reprojection

   -  For example, for display it may be quicker to have a WMS render an image. However, during
      editing it will likely be more useful to access the WFS and do the rendering on the client.

-  Provides low-level interface for plug-ins

Notes
'''''

-  WMS requests must be batched into one renderer

