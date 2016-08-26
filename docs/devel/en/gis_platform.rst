GIS Platform
~~~~~~~~~~~~

The "GIS Platform" serves as the foundation of the uDig application, allowing it to manage and
access spatial data

Logically the GISPlatform packages up the ability to work with spatial data. This idea is presented
as:

-  GIS Platform concept in the uDig architecture separating data access from map rendering
-  PlatformGIS utility class allowing "easy access" to GIS Platform services
-  org.locationtech.udig\_platform-feature gathering up the plugins involved

The PlatformGIS utility class can probably be made available as a workbench service; right now it is
a singleton.

org.locationtech.udig\_platform-feature
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The feature "org.locationtech.udig\_platform-feature" containing the following plugins:

-  org.locationtech.udig.catalog: The Catalog plug-in handles the management of spatial information
   and services. Work is divided into functionality for searching information (ie remote catalogs)
   and managing local information (ie the local catalog).
-  org.locationtech.udig.catalog.ui: views and content providers allowing you to display catalog
   content in your own application
-  org.locationtech.udig.ui: grab bag of code to help you bundle up uDig into your own RCP
   application

And some support plugins packaging up GeoTools for use:

-  org.locationtech.udig.core - focus on adapters between eclipse and geotools
-  org.locationtech.udig.help - online help exported from wiki
-  org.locationtech.udig.jai.macosx - will install JAI if needed
-  org.locationtech.udig.libs: Rounds up additional open source projects (most noticeably GeoTools)
   that we use to work with spatial data
-  org.locationtech.udig.libs.db2 - mixed into libs above using OSGi buddy classloader
-  org.locationtech.udig.libs.oracle - mixed into libs above using OSGi buddy classloader
-  org.locationtech.udig.ui - focus on making eclipse.ui easier to use

Additional plug-ins are defined teach the catalog about additional file formats, services and
catalogs:

-  org.locationtech.udig.catalog.arcgrid
-  org.locationtech.udig.catalog.arcsde
-  org.locationtech.udig.catalog.db2
-  org.locationtech.udig.catalog.geotiff
-  org.locationtech.udig.catalog.imageio - requires ImageIO-Ext installed into your JRE
-  org.locationtech.udig.catalog.mysql
-  org.locationtech.udig.catalog.oracle
-  org.locationtech.udig.catalog.postgis
-  org.locationtech.udig.catalog.rasterings
-  org.locationtech.udig.catalog.shp
-  org.locationtech.udig.catalog.wfs
-  org.locationtech.udig.catalog.wms
-  org.locationtech.udig.catalog.worldimage
-  org.locationtech.udig.catalog.\*

Using the GIS Platform in your Own RCP Application
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

These plug-ins are not limited to the uDig application and may be used with normal Eclipse IDE
development as needed. The services provided here are similar to the services provided by the
Eclipse IDE plug-ins.

Compare the purpose of following two plug-ins:

-  **org.eclipse.ide** provides Projects, IResource and a compile / build cycle.
-  **org.locationtech.udig.catalog** provides Catalogs, IResolve and the ability to connect to
   services.

