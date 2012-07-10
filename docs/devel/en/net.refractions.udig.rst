net.refractions.udig
====================

The perspective of operations plug-ins, or scripting is all about manipulating data and interacting
with the user.

Use Cases
~~~~~~~~~

-  provides scripting services based on BeanScript, Jython, Groovy, etc...
-  provide facility to load/version java based plug-in

Requirements
~~~~~~~~~~~~

* :doc:`net.refractions.udig.project.context`

* :doc:`net.refractions.udig.project.ui.layerManager`

* :doc:`net.refractions.udig.project.selection`

-  Data Catalog
-  Issues List
* :doc:`net.refractions.udig.core`

-  Script

Diagram
~~~~~~~

.. figure:: /images/net.refractions.udig/ExtentionInteraction.png
   :align: center
   :alt: 

Design Notes
~~~~~~~~~~~~

-  consider providing organization for user's scripts? Script to run selection could be independent
   of a specific script engine

