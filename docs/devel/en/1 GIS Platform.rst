1 GIS Platform
==============

The "GIS Platform" serves as the foundation of the uDig application, allowing it to manage and
access spatial data.

:doc:`GISPlatform`


* :doc:`net.refractions.udig\_platform-feature`


`Using the GIS Platform in your Own RCP
Application <#1GISPlatform-UsingtheGISPlatforminyourOwnRCPApplication>`_

GISPlatform
===========

Logically the GISPlatform packages up the ability to work with spatial data. This idea is presented
as:

-  GIS Platform concept in the uDig architecture separating data access from map rendering
-  PlatformGIS utility class allowing "easy access" to GIS Platform services
-  net.refractions.udig\_platform-feature gathering up the plugins involved

The PlatformGIS utility class can probably be made available as a workbench service; right now it is
a singleton.

net.refractions.udig\_platform-feature
--------------------------------------

The feature "net.refractions.udig\_platform-feature" containing the following plugins:

-  net.refractions.udig.catalog: The Catalog plug-in handles the management of spatial information
   and services. Work is divided into functionality for searching information (ie remote catalogs)
   and managing local information (ie the local catalog).
-  net.refractions.udig.catalog.ui: views and content providers allowing you to display catalog
   content in your own application
-  net.refractions.udig.ui: grab bag of code to help you bundle up uDig into your own RCP
   application

And some support plugins packaging up GeoTools for use:

-  net.refractions.udig.core - focus on adapters between eclipse and geotools
-  net.refractions.udig.help - online help exported from wiki
-  net.refractions.udig.jai.macosx - will install JAI if needed
-  net.refractions.udig.libs: Rounds up additional open source projects (most noticeably GeoTools)
   that we use to work with spatial data
-  net.refractions.udig.libs.db2 - mixed into libs above using OSGi buddy classloader
-  net.refractions.udig.libs.oracle - mixed into libs above using OSGi buddy classloader
-  net.refractions.udig.ui - focus on making eclipse.ui easier to use

Additional plug-ins are defined teach the catalog about additional file formats, services and
catalogs:

-  net.refractions.udig.catalog.arcgrid
-  net.refractions.udig.catalog.arcsde
-  net.refractions.udig.catalog.db2
-  net.refractions.udig.catalog.geotiff
-  net.refractions.udig.catalog.imageio - requires ImageIO-Ext installed into your JRE
-  net.refractions.udig.catalog.mysql
-  net.refractions.udig.catalog.oracle
-  net.refractions.udig.catalog.postgis
-  net.refractions.udig.catalog.rasterings
-  net.refractions.udig.catalog.shp
-  net.refractions.udig.catalog.wfs
-  net.refractions.udig.catalog.wms
-  net.refractions.udig.catalog.worldimage
-  net.refractions.udig.catalog.\*

Using the GIS Platform in your Own RCP Application
==================================================

These plug-ins are not limited to the uDig application and may be used with normal Eclipse IDE
development as needed. The services provided here are similar to the services provided by the
Eclipse IDE plug-ins.

Compare the purpose of following two plug-ins:

-  **org.eclipse.ide** provides Projects, IResource and a compile / build cycle.
-  **net.refractions.udig.catalog** provides Catalogs, IResolve and the ability to connect to
   services.

