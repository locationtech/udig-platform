Upgrade from uDig 1.1 to uDig 1.2
=================================

uDig 1.2 is focused on upgrading the GeoTools library - as such there are a wide number of changes
you may have to make to your code.

:doc:`GeoAPI Changes`


* :doc:`Use org.opengis.filter.Filter`

* :doc:`Use org.opengis.Feature`

-  `Use of
   org.opengis.simple.SimpleFeature <#UpgradefromuDig1.1touDig1.2-Useoforg.opengis.simple.SimpleFeature>`_

:doc:`GeoTools Changes`


* :doc:`DataAccess`


:doc:`GIS Platform`


* :doc:`IResolve`

* :doc:`IGeoResource`


:doc:`GIS Application`


Reference:

* :doc:`GeoTools User Guide`


GeoAPI Changes
==============

Use org.opengis.filter.Filter
-----------------------------

We now make use of the GeoAPI Filter interface (which is read-only) and requires a little less
typing.

Please see this page for example use:

* :doc:`http://docs.codehaus.org/display/GEOTDOC/Filter`


Use org.opengis.Feature
-----------------------

GeoTools 2.5 has a new rich feature model (so any code you have that uses Features will need to be
upgraded). These classes allow you to describe things such as associations and operations that you
could not do with the previous simple geotools feature class.

Please see this page for example use:

* :doc:`http://docs.codehaus.org/display/GEOTDOC/Feature+Model+Guide`


Use of org.opengis.simple.SimpleFeature
---------------------------------------

This class represents a "flat" feature of simple content (ie Geometry, Integer, String, etc...). It
is very similar to the old GeoTools Feature class and you may find it useful for lots of your work.

GeoTools Changes
================

DataAccess
----------

We now try and use **DataAccess** rather than **DataStore**. **DataAccess** is a super class of
**DataStore** that does not assume simple features.

GIS Platform
============

IResolve
--------

-  You will need to implement getTitle now

IGeoResource
------------

-  Rename your implementation of getInfo to createInfo; the IResolve class has a final getInfo
   method now that will cache.

GIS Application
===============

