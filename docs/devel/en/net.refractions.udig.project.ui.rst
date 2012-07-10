net.refractions.udig.project.ui
===============================

-  Entry in Project File representing a visual display of GIS information as a series of
   `net.refractions.udig.project.layer <net.refractions.udig.project.layer.html>`_\ s.
-  Each LayerDef refers to a Data Model in the Project File
-  Map is realized by
   :doc:`net.refractions.udig.project.ui.layerManager`

   as a controller
-  Map has Viewport Editor as its main visual display (Eclipse Editor, additional Views such as
   Layer View work in conjunction to allow complete control of Map specification
-  Map must be combined with the Project File Data Model to create Data Source entries in the Local
   Catalog.

