Dependencies
============

uDig makes use of several dependencies (as outlined in the `Platform Architecture <platform_architecture>` page)

The vast majority of the dependencies in uDig are isolated into a single OSGi plugin
(org.locationtech.udig.libs). The complete list of dependencies is generated on the fly using an ant
script.

GeoTools
--------

Spatial library providing referencing and spatial format support including rendering.

Documentation:

* `http://geotools.org <http://geotools.org>`_
* `http://docs.geotools.org/latest/userguide/ <http://docs.geotools.org/latest/userguide/>`_
* `http://docs.geotools.org/latest/developers/ <http://docs.geotools.org/latest/developers/>`_

uDig master uses the latest GeoTools SNAPSHOT:

* `https://github.com/geotools/geotools <https://github.com/geotools/geotools>`_

uDig 1.1 uses GeoTools 2.2:

* `http://svn.osgeo.org/geotools/branches/2.2.x/ <http://svn.osgeo.org/geotools/branches/2.2.x/>`_

JTS Topology Suite
------------------

Provides the implementation of Geometry.

Documentation:

* `http://tsusiatsoftware.net/jts/main.html <http://tsusiatsoftware.net/jts/main.html>`_
* `http://sourceforge.net/projects/jts-topo-suite/ <http://sourceforge.net/projects/jts-topo-suite/>`_

Related:

-  `http://www.vividsolutions.com/jcs/ <http://www.vividsolutions.com/jcs/>`_ (JTS Comflation Suite
   is a good source of processing and analysis examples)

