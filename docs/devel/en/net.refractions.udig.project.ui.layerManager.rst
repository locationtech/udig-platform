net.refractions.udig.project.ui.layerManager
============================================

The Layer Manager is the "controller" for a
`net.refractions.udig.project.ui <net.refractions.udig.project.ui.html>`_ in this MVC system.
 The UI and plugins send it requests when
`net.refractions.udig.project.ui <net.refractions.udig.project.ui.html>`_ changes are required.

Functional Requirements
'''''''''''''''''''''''

-  Acts as Controller for
   `net.refractions.udig.project.context <net.refractions.udig.project.context.html>`_ in the MVC
   sense
-  Provide FeatureSource/FeatureStore and GC access for
   `net.refractions.udig.core <net.refractions.udig.core.html>`_\ s by layerRef
-  Locks Features
-  Transactions

Non-Functional Requirements
'''''''''''''''''''''''''''

-  Hide Locking from end user - be magic

