net.refractions.udig.render.features
====================================

The raster pipeline takes a feature Collection and renders them to a Graphics2D object.

Functional Requirements
'''''''''''''''''''''''

-  Renders geometries from a feature store
-  Maintains a caches to improve rendering performance
-  Transform between the following coordinate systems when needed

   -  LayerCS

      -  The Coordinate System that the features are in when they reach the feature renderer

   -  mapCS

      -  The Coordinate System that the user will see on the screen

   -  textCS

      -  The Java2D coordinate system.

   -  DeviceCS

      -  The device coordinate system. Each "unit" is a pixel of device-dependent size.

-  Styles features with SLD styling
-  Renders non-selected features onto on Graphics2D
-  Selected Features:

   -  Renders selected features to a Graphics2D (not the same Graphics2D as selected features)
   -  Styles selected Features based on SLD

Non-functional Requirements:
''''''''''''''''''''''''''''

-  Render features set in 3-5 seconds
-  Must be able to render customized styles.
-  Multiple rendering options:

   -  Render all features associated with a query
   -  render a selection
   -  Rendering status can be on/off Feature Renderer Pipeline

Design Notes:
'''''''''''''

-  Inputs:

   -  FeatureStore
   -  Query
   -  Selection Filters
   -  Style
   -  view area

-  Outputs:

   -  ImageBuffer with rendered features
   -  ImageBuffer with selected features

-  Listens to `net.refractions.udig.project.context <net.refractions.udig.project.context.html>`_
   for Bbox and SRS events
-  Listens to DataStore for feature change events
-  Listens to
   `net.refractions.udig.project.selection <net.refractions.udig.project.selection.html>`_ for
   selection events

