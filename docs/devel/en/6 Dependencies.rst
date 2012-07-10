6 Dependencies
==============

uDig makes use of several dependencies (as outlined in the `3 Platform
Architecture <3%20Platform%20Architecture.html>`_ page)

* :doc:`GeoTools`

* :doc:`GeoAPI`

* :doc:`JTS Topology Suite`


Related Links:

:doc:`refresh.xml`

(dependencies needed for uDig)

* :doc:`copy.properties`

   (location of maven repositories)

The vast majority of the dependencies in uDig are isolated into a single OSGi plugin
(net.refractions.udig.libs). The complete list of dependencies is generated on the fly using an ant
script.

GeoTools
========

Spatial library providing referencing and spatial format support including rendering.

Documentation:

* :doc:`Home Page`

-  `Developers Guide <http://docs.codehaus.org/display/GEOT/Home>`_ (includes build instructions for
   the library)
-  `User Guide <http://docs.codehaus.org/display/GEOTDOC/Home>`_ (user guide for the latest version
   of GeoTools)

uDig 1.2 uses the latest GeoTools SNAPSHOT compiled directly from trunk:

* :doc:`http://svn.osgeo.org/geotools/trunk/`


uDig 1.1 uses GeoTools 2.2:

* :doc:`http://svn.osgeo.org/geotools/branches/2.2.x/`


GeoAPI
======

GeoAPI is used by GeoTools as a source of good well documented interfaces; in a sense GeoAPI
provides the interfaces and GeoTools provides the implementation.

Documentation:

* :doc:`http://geoapi.sourceforge.net/`


GeoAPI does not have any additional dependencies; the project is strictly focused on providing
interfaces capturing popular geospatial standards as easy to understand Java code.

JTS Topology Suite
==================

Provides the implementation of Geometry.

Documentation:

* :doc:`http://sourceforge.net/projects/jts-topo-suite/`

* :doc:`http://www.vividsolutions.com/jts/jtshome.htm`

   (Commercial Home Page no longer maintained)

Related:

-  `http://www.vividsolutions.com/jcs/ <http://www.vividsolutions.com/jcs/>`_ (JTS Comflation Suite
   is a good source of processing and analysis examples)

