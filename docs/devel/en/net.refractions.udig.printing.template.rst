net.refractions.udig.printing.template
======================================

A layout represents the way graphics are arranged on a
:doc:`net.refractions.udig.printing.page`


Functional Requirements:
''''''''''''''''''''''''

-  Is a template (in the MS Word sense), describing how to create a Page
-  Provides a default layout (map, legend, scalebar, compass)
-  User must be able to edit/save (may be supplied by allowing export as Layout option for Page
   editing)
-  Persists half of the
   `net.refractions.udig.printing.page <net.refractions.udig.printing.page.html>`_ contract - the
   half that is not the
   :doc:`net.refractions.udig.printing.context`

    **i.e** non-context
   `net.refractions.udig.render.decorator <net.refractions.udig.render.decorator.html>`_ specified
   in page coordinates and location of viewport

Non-functional Requirements:
''''''''''''''''''''''''''''

Design notes:
'''''''''''''

-  capture layout as a series of Boxes rendered by a Decorator or Viewport

