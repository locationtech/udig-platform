.. _what_is_new_2_1:

What is new for uDig 2.1
========================

This is a release candidate, prior to the creation of a stable 2.1.x series. GeoTools library has been updated to 19.4, please consult Upgrade guide in case you are using SDK to build your own geospatial application with uDig.

.. contents:: :local:
   :depth: 1

Improvements and Fixes
----------------------
* `#230 <https://github.com/locationtech/udig-platform/issues/203>`_ Initialitiation in SetAttributesCommand
* `#280 <https://github.com/locationtech/udig-platform/issues/280>`_ allows to open StyleEditors for created layers
* `#283 <https://github.com/locationtech/udig-platform/issues/283>`_ ConcurrentModificationException using LayerInterceptor
* `#293 <https://github.com/locationtech/udig-platform/issues/293>`_ fixed Table Sorting issues and selection problems
* `#305 <https://github.com/locationtech/udig-platform/issues/305>`_ Rollback issues were fixed
* `#301 <https://github.com/locationtech/udig-platform/issues/301>`_ fixed Draging selected Nodes
* `#306 <https://github.com/locationtech/udig-platform/issues/306>`_ fix of multiple listeners problems in ToolOptionContributionItem
* `#317 <https://github.com/locationtech/udig-platform/issues/317>`_ preventing Exceptions in EditBlackboard if transformation is not set
* `#325 <https://github.com/locationtech/udig-platform/issues/325>`_ fixed CRS/SRS paremeter for WMS 1.3.0 GetFeatureInfo Requests
* `#330 <https://github.com/locationtech/udig-platform/issues/330>`_ Cursor Position tool considers Locale when parsing Coordinate String
* `#338 <https://github.com/locationtech/udig-platform/issues/338>`_ Exception Handling for LineTool and PolygonTool
* `#343 <https://github.com/locationtech/udig-platform/issues/343>`_ Correctly calculate the winding of triangles
* `#351 <https://github.com/locationtech/udig-platform/issues/351>`_ InitMapCRS Interceptor doesn't init Map-CRS if defaultCRSPreference is set
* `#304 <https://github.com/locationtech/udig-platform/issues/304>`_ selection box doesn't vanish occasionally selecting with Drag&Drop
* `#306 <https://github.com/locationtech/udig-platform/issues/306>`_ Support for Text widget for ToolOptionContributionItem
* `#218 <https://github.com/locationtech/udig-platform/issues/218>`_ Bulk feature Copy or Move between Layers
* `#299 <https://github.com/locationtech/udig-platform/issues/299>`_ Improved rendering by considering min and max scales before read
* `#307 <https://github.com/locationtech/udig-platform/issues/307>`_ externalized strings to allow translations
* `#313 <https://github.com/locationtech/udig-platform/issues/313>`_ Allows to configure fill transparency for edit geometries (Preferences)
* `#324 <https://github.com/locationtech/udig-platform/issues/324>`_ Allows to configure vertex transparency for edit geometries (Preferences)
* `#333 <https://github.com/locationtech/udig-platform/issues/333>`_ DinstanceTool handles Map zoom in/out correctly
* `#284 <https://github.com/locationtech/udig-platform/issues/284>`_ Display character encoding in LayerSummary of Shapefile layers
* `#329 <https://github.com/locationtech/udig-platform/issues/329>`_ DrawCoordinate command added
* `#277 <https://github.com/locationtech/udig-platform/issues/277>`_ Adds Perspective Switcher to Application
* `#292 <https://github.com/locationtech/udig-platform/issues/292>`_ Disallow DropFilterAction between same layers
* `#266 <https://github.com/locationtech/udig-platform/issues/266>`_ document bundle seperated to have more control creation Appliactions using SDK
* `#285 <https://github.com/locationtech/udig-platform/issues/285>`_ test bundles names using same pattern
* `#356 <https://github.com/locationtech/udig-platform/issues/356>`_ Using latest Tycho Release (1.4.0) for build chain


Updated 3rd-party Dependencies
------------------------------

uDig Codebase has been updated to work with `GeoTools 19.4 <http://geotoolsnews.blogspot.com/2018/12/geotools-194-released.html>`_ series. This implies dependency updates for several other libraries as well. 
