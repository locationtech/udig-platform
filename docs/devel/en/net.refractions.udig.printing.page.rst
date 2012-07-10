net.refractions.udig.printing.page
==================================

Functional Requirements
'''''''''''''''''''''''

-  `net.refractions.udig.printing.template <net.refractions.udig.printing.template.html>`_ acts as a
   Template/Protype for making one of these
-  Persistance - Page is saved to disk and can be printed later
-  Manages `net.refractions.udig.printing.context <net.refractions.udig.printing.context.html>`_, in
   a similar way as LayerManager
-  Manages additional non-context
   `net.refractions.udig.render.decorator <net.refractions.udig.render.decorator.html>`_ specified
   in page coordinates and location of viewport
-  Saves knowledge of the printer and the layout (this may not be portable)

Non-Functional Requirements
'''''''''''''''''''''''''''

-  xml persistence format?

Design Notes
''''''''''''

-  Note: Page saves how to access data, does not contain a copy
-  Can this have more then one Viewport? No
-  This is not the Java `Paper <http://java.sun.com/j2se/1.4.2/docs/api/java/awt/print/Paper.html>`_
   class
-  It is intended that a user should be able to save a page that they have printed so that they can
   print it again, or perhaps even send it to someone else who can then print it.
-  At some point may be managed by a "Report".
-  When persisting, it will need to take the context with it.

