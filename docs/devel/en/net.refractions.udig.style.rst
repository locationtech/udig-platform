net.refractions.udig.style
==========================

Functional Requirements
~~~~~~~~~~~~~~~~~~~~~~~

-  Aware of all styles currently available
-  Aware of the origin of each style (on disk, WMS, etc)
-  Will return a style if it is requested
-  Can receive new or changed styles
-  Can remove styles

Non-Functional Requirements
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Styles residing on WMSs are read only, but can be copied to another location (to disk, then
   edited)

Design Notes
~~~~~~~~~~~~

-  In order to keep track of styles, needs to listen to WMS getCapabilities responses
-  Can be told where some styles are residing, and then should go retrieve them for storage

