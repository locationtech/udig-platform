Platform Architecture
---------------------

The UDIG GISPlatform is built around the concept of plug-ins to the base Eclipse Rich Client
Platform. In addition uDig makes use of the several support libraries and a couple extensions to the
Java Runtime Environment.

References:

* `Eclipse Architecture <http://help.eclipse.org/juno/topic/org.eclipse.platform.doc.isv/guide/arch.htm>`_
* `GeoTools Architecture <http://docs.geotools.org/latest/userguide/welcome/architecture.html>`_

It is useful to consider the UDIG application as a series of Tiers each with different
responsibilities and capabilities.

.. figure:: /images/platform_architecture/udig_extending.png
   :align: center
   :alt: 

If you are new to Eclipse RCP development it is helpful to go through the tutorials included with
Eclipse prior to working with uDig.

GIS Application
~~~~~~~~~~~~~~~

Project services opperates as a UDIG application providing Maps and Printing under user control. The
GIS Application layer also provides Rendering and Tool services allow control of display and screen
interaction.

**New Tool**

For some quick experience implementing a new tool:

-  please extend the SimpleTool class. For more details please review the Distance Tool tutorial.

**New Application**

For more information on extending the GIS Application for your own use please review:

-  the custom application tutorial for a uDig like application
-  the source code for the rcp tutorial if you are interested in adding uDig visualization to an
   existing application

**New Renderer**

To visualize your own custom data format you will need to implement a custom RenderMetricsFactory.
This will allow uDig to render a layer created around your data format.

For more information please review the source code for the CSVRenderer tutorial.

GIS Platform
~~~~~~~~~~~~

Catalog Services provide a common API for data access and may serve as foundation for different GIS
applications.

**New Data Format**

To add support for a data format (of any description) you will need to add a new ServiceExtension.
This will allow uDig to hold onto the information you wish in the catalog and make it available to
other parts of the application.

For more information please review the source code for the CSVService tutorial.

Eclipse Rich Client Platform
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

We make use of the plug-ins contained in the Eclipse Rich Client platform. These plug-ins provide
the base implementation of a workbench window, the runtime platform that is used to wire up the
application and so on.

-  runtime - contains the Platform and Application extension points
-  ui - support for the workbench window, workbench selection and menus
-  standard widget toolkit (swt) - direct implementation of user interface widgets
-  jface - ease the use of tree and table widgets by introducing adapters between your domain
   objects and the raw swt widgets

In addition we make use of:

-  Help - to provide online help
-  Install - access to the update manager
-  Eclipse Modeling Framework (EMF) - used to implement the uDig data model of Maps, Layers and
   ViewportModel
-  Graphical Editing Framework (GEF) - used to create a quick visual editor work Page layout

Support Libraries
~~~~~~~~~~~~~~~~~

The support libraries used by uDig are:

-  JTS Topology Suite - provides an implementation of the Simple Feature for SQL definition of
   Geometry (ie Point, Line and Polygon)
-  GeoAPI - provides common java interfaces for geospatial concepts based on OGC standards
-  GeoTools - a grab back of geospatial know-how, including an implementation of the interfaces
   defined by GeoAPI

These libraries provide useful abstractions (such as Geometry and Feature) and data connectivity.
These libraries are all gathered into a single plug-in (net.refractions.udig.libs plugin) so they
may share a classloader.

Currently the GeoTools library makes use of a plug-in system called "factory spi" that depends on
plugins being available in the same classloader as the core library. This is a limitation we would
like to see lifted in the future.

**New GridCoverage** (Geospatial Raster Format)

To add support for a new GridCoverage format (ie geospatial raster) you will need to write a new
plug-in for GeoTools that implements the GridCovreageReader interface. If you implement this
interface you will be able to leverage the existing uDig image rendering system.

For more information please review the GeoTools user guide, source code and test cases.

**New DataStore** (Geospatial Vector Format)

To add support for a new Vector format you will need to implement a new GeoTools plugin that
implements the DataAccess (for rich content) or DataStore (for simple content) interface. If you
implement either of these interfaces you will be able to make use of the existing uDig rendering
system and Style Layer Descriptor support.

For more information please review the GeoTools user guide, source code and test cases.

Java Runtime Environment
------------------------

The uDig application makes use of JRE version 5.0 at this time (although some platforms are making
use of Java 6 we are limiting adoption until Java 6 is available on all platforms).

We also make use of two important extensions to the default JRE install:

-  Java Advanced Imaging - performs image processing tasks; defined as a series of operations on raw
   raster formats
-  Java Image IO - provides raw raster format implementations (Java includes PNG, JPEG etc out of
   the box)
-  ImageIO-EXT - additional geospatial raster formats such as GeoTIFF and MRSID

**New Image Format**

To add support for a new Image Format please consider extending the Java Image IO project. You will
find some GeoTools Alumni have started an ImageIO-EXT project in order to teach ImageIO about common
geospatial formats. The ImageIO-EXT project has made use of GDAL SWIG bindings in order to leverage
C++ support for some of the more exotic formats.

For more information please visit the ImageIO-EXT website.

.. toctree::
   :maxdepth: 1
   
   key_components

