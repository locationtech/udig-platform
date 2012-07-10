net.refractions.udig.printing
=============================

The printing engine takes a
`net.refractions.udig.printing.page <net.refractions.udig.printing.page.html>`_ (that is a
`net.refractions.udig.printing.context <net.refractions.udig.printing.context.html>`_ and
`net.refractions.udig.render.decorator <net.refractions.udig.render.decorator.html>`_ ) and
a\ `Printer <http://java.sun.com/j2se/1.4.2/docs/api/java/awt/print/PrinterJob.html>`_ and begins a
specific rendering pipeline.

Functional Requirements
'''''''''''''''''''''''

-  Maintains a cache
-  Process Page +
   :doc:`Printer`

-  Constructs what is to be printed from a
   `net.refractions.udig.printing.context <net.refractions.udig.printing.context.html>`_ with
   additional Metadata and Connection info from Data Manager
-  Passes each layer off to its appropriate Renderer along with the
   `Graphics2D <http://java.sun.com/j2se/1.4.2/docs/api/java/awt/Graphics2D.html>`_ from the
   :doc:`Printer`

-  Print Progress/Cancel

Non-functional Requirements
'''''''''''''''''''''''''''

Design Notes
''''''''''''

-  Pringing Engine is "smart" - determines whether a given WMS and WFS share the same source and can
   use either to retrieve the same data. For printing, this will allow it use WFS when available, as
   that will be much faster in response time.
-  May need to separate out WMS requests into separate calls tiled together on the client side at
   native printer resolution

