net.refractions.udig.render.decorator
=====================================

Mathmatically generated pseudo-Layers based on CRS and BBox, some may need a location.

Functional Requirements
'''''''''''''''''''''''

-  North Arrow (Compass/Arrrow)
-  Reticules - Grid Background
-  Black & White border arround map
-  Scalebar
-  Legend
-  Reference Map
-  Image
-  Labels (like title, labeling is another beasty - kind of a derived Layer)

Some decorators may be associated with a point in data CRS (although that may be a fancy SLD/GO-1
Style thingy).
 Most are relative to Screen/Page CS.

Non-Functional Requirements
'''''''''''''''''''''''''''

Design Note:
''''''''''''

-  Decorators are Java classes, you need to be a programmer to make up new Decorator types
-  Do these things implement the
   `net.refractions.udig.project.layer <net.refractions.udig.project.layer.html>`_ contract as far
   as Rendering Pipeline is concerned (or are they treated special)??

