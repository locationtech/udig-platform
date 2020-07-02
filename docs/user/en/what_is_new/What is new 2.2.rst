.. _what_is_new_2_2:

What is new for uDig 2.2
========================

This is a release candidate, prior to the creation of a stable 2.2.x series. GeoTools library has been updated to 22.1 and Eclipse Platform to 2019-03 to allow uDig to ruun with Java 11. Please consult Upgrade guide in case you are using SDK to build your own geospatial application with uDig.

.. contents:: :local:
   :depth: 1

Improvements and Fixes
----------------------
* `#314 <https://github.com/locationtech/udig-platform/issues/314>`_ Allows to configure display delay for edit tools
* `#379 <https://github.com/locationtech/udig-platform/issues/379>`_ Improved copy behavior for feature attributes
* `#367 <https://github.com/locationtech/udig-platform/issues/367>`_ Fixed Display access for Message Bubble
* `#363 <https://github.com/locationtech/udig-platform/issues/363>`_ Fixed Layer Memory Leak on Feature Events
* Java 11 Support:
  * `#354 <https://github.com/locationtech/udig-platform/issues/354>`_ Removed joda-time dependency
  * `#378 <https://github.com/locationtech/udig-platform/issues/378>`_ Update Eclipse Platform to Release 2019-03
  * `#361 <https://github.com/locationtech/udig-platform/issues/361>`_ Update GeoTools to 22.1 (with dependencies)
 
Updated 3rd-party Dependencies
------------------------------

uDig Codebase has been updated to work with `GeoTools 22.1 <http://geotoolsnews.blogspot.com/2019/11/geotools-221-released.html>`_ series. This implies dependency updates for several other libraries as well. In combination with Eclipse Platform update to Release 2019-03 uDig supports Java 11 from now on. This implies that uDig cannot support 32bit Operation Systems anymore, for details see `Eclipse Platform Issue 536766 <https://bugs.eclipse.org/bugs/show_bug.cgi?id=536766>`_.
