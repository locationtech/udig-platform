Catalog
=======

The uDIg Catalog is used to mange and interact with spatial services and resources.

The style of programming used for the uDig Catalog is similar to that of the Eclipse IDE (where
concepts such as Project / IResource / IFile are defined).

**NOT IResource**

Since uDig does not depend on the Eclipse IDE (we have no need of a compile / build cycle) we
provide are making use of our own interfaces ICatalog, IService and IGeoResource covered on this
page. By making use of our own API we also have been able to address a number of concerns specific
to spatial data; almost all our data is so big, or remote, or both that latency is an issue. It is
very important to us that you know when you are making use of spatial data that make take some time
to process, or has the possibility of an I/O error.

CatalogPlugin
-------------

The CatalogPlugin provides the following services:

-  The concept of Handles to actual Resources
-  Discovery of additional resources using Search facilities
-  Programmatic Management of spatial resources

The CatalogPlugin supports several formats right out of the box and is easily extended for custom
content.

.. figure:: /images/catalog/CatalogPlugin.PNG
   :align: center
   :alt: 

You can retrieve the single instance of CatalogPlugin using getDefault():

.. code-block:: java

    CatalogPlugin activator = CatalogPlugin.getDefault();


You can add a listener to watch for catalog events:

.. code-block:: java

    CatalogPlugin.getDefault().addListener( listener );
    CatalogPlugin.getDefault().removeListener( listener );


For more information please check out the next section on :doc:`Catalog Notifications <catalog_notifications>`.

The CatalogPlugin keeps a list of known catalogs, including the local catalog and remote web
catalogs.

.. code-block:: java

    CatalogPlugin.getDefault().getCatalogs();

Most of the time you will spend interacting with the LocalCatalog manage live connections to
services:

.. code-block:: java

    IRepository local = CatalogPlugin.getDefault().getLocal();

There is also a service factory used to create services to add to the local catalog:

.. code-block:: java

    IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
    List<IService> created = serviceFactory.createService( file.toURL() );

There are several more examples of using the ServiceFactory below.

The CatalogPlugin is the "Activator" or "Plugin" class for **net.refractions.udig.catalog** - as
such it extends AbstractUIPlugin for the following.

-  getDialogSettings() - used to hold persistent state data for wizards and plug-ins
-  getImageRegistry() - images that are shared for frequently used within the plug-in
-  getPreferenceStore() - used to hold persistent user or internal settings

By convention all Plugins contain a static ID field used to identify the plug-in OSGi bundle, or
Platform methods.

.. code-block:: java

    Bundle bundle = Platform.getBundle( CatalogPlugin.ID );

The ID is also useful when reporting problems:

.. code-block:: java

    IStatus status = new Status(IStatus.ERROR, CatalogPlugin.ID, "error message");
    CatalogPlugin.getDefault().getLog().log( status );

ISearch
^^^^^^^

ISearch is the interface used to represent a generic "catalog" of spatial resources and services,
you can think of it as being similar to a web search engine.

The following ISearch methods are used when working with remote services:

-  getInfo( IProgressMonitor ) - description of the catalog
-  find( URL, IProgressMonitor ) - retrieve a list of services (or resources) for the provided URL,
   including all known alternatives.
-  search( String, Envelope, IProgressMonitor ) - used to query the catalog using a text pattern and
   bounding box

Please keep in mind that the catalog tracks a great deal of information about your services and
resources; the reason find returns a list (rather than just a single entry) is the same information
may be available from a number of sources. Having a range of alternatives available is useful in a
world where external servers are sometimes down for maintenance.

The CatalogPlugin keeps track of all the catalogs (local and remote) that can be used to find
spatial content.

.. code-block:: java

    List<IResolve> found = new ArrayList<IResolve>();
    for( ISearch search : CatalogPlugin.getDefault().getCatalogs() ){
        try {
           found.addAll( search.search( pattern, bbox, process ) );
        }
        catch( IOException problem ){
            catalog.getLog(
                new Status( IStatus.WARNING, CatalogPlugin.ID, IStatus.OK, "Failed to search with:"+pattern, t)
            );
        }
    }

The above example makes use of CatalogPlugin, any problems are reported using the CatalogPlugin ID
to the logging system. We will cover some of the other uses of ICatalog below.

Please note that some of these instances may be instances of **IRepository** which allows you to
register your own spatial information (using the add(IService) and remove(IService) methods).

Local Catalog
^^^^^^^^^^^^^

The local catalog (implemented by the **CatalogImpl** class) is responsible for managing a list of
all the services known to the uDig application. The local catalog is also responsible for tracking
which Services are in use and tracking any life cycle changes (some services such as Databases are
expensive to connect to and care must be taken to clean up after their use).

.. figure:: /images/catalog/ICatalog.PNG
   :align: center
   :alt: 

The following IRepository methods are used when working with a local (or remote) repository.

-  add( IService ) - add a service to the catalog; the service as registered is returned
-  acquire( Map, ProgressMonitor ) - connect to an IService, will create and add a service if needed
-  acquire( URL, ProgressMonitor ) - connect to an IService, will create and add the service if
   needed
-  remove( IService ) - used to communicate when a service is removed (such as a file being deleted
   from disk)
-  replace( URL, IService ) - used to communicate when a service changes location (such as a file
   moving on disk)

The following ICatalog methods are used when working with a local catalog

-  constructServies( URL, ProgresMonitor ) - list of services to consider when adding
-  constructService( Map, ProgressMonitor ) - list of services to consider when adding
-  checkMembers( List ) - short list services that are already in the catalog (and thus do not need
   to be disposed)
-  checkNonMembers( List ) - short list of services that are **not** in the catalog that require
   handling (either by adding them to the catalog or by calling dispose)
-  createTemporaryResource( Object ) - used to create a temporary resources, usually by using a
   FeatureType
-  getTemporaryDescriptorClasses() - list of classes for which a temporary resource can be created

The following ICatalog methods are safe to call from a user interface (ie are non blocking):

-  getById( Class, URL, ProgressMonitor ) - used to look up an exact match
-  addCatalogListener( IResolveChangeListener ) - watch the catalog for changes
-  removeCatalogListener( IResolveChangeListener ) - stop watching the catalog for changes

The CatalogPlugin is mostly used to access a single Local Catalog used to manage live connections to
your databases, external services and local files. The local catalog is used to track all "active"
data connections; even if you find information in a remote catalog, it will be added to the local
catalog as you start to use it.

To find an existing service in the catalog:

.. code-block:: java

    IRepository local = CatalogPlugin.getDefault().getLocalCatalog();
    IService shapefile = local.getById( IService.class, url, progressmonitor );

To find an existing georesource in the catalog:

.. code-block:: java

    ICatalog local = CatalogPlugin.getDefault().getLocalCatalog();
    IGeoResource shapefile = local.getById( IGeoResource.class, url, progressmonitor );

To add a service to the catalog we need to use the ServiceFactory to create the IService; and then
ICatalog.add( service ) to place each service into the catalog.

To use ServiceFactory to connect to a service based on a simple URL.

.. code-block:: java

    File file = new File( "C:\data\cities.shp" );
    URL url = file.toURL();

    IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
    for( IService service : serviceFactory.createService( url ) ){
         try {
             // many different providers may think they can connect to this URL (example WFS, WMS, ...)
             // but we should try connecting to be sure ...
             IServiceInfo info = service.getInfo( null );
             CatalogPlugin.getDefault().getLocalCatalog().add( service );
         }
         catch (IOException couldNotConnect ){
         }
    }
    // The service(s) associated with the provided URL are now in the local catalog.

You can be a little more efficient using the **acquire** method (the acquire method checks using
getById and only creates the service if needed):

.. code-block:: java

    File file = new File( "C:\data\cities.shp" );
    URL url = file.toURL();

    IRepository local = CatalogPlugin.getDefault().getLocal();
    IService service = local.acquire( url, new NullProgressMonitor() );

You can also use connection parameters to be a bit more specific about servic:

.. code-block:: java

    Map<String,Serializable> params = new HashMap<String,Serializable>();
    params.put("ur", url );
    params.put("create spatial index", Boolean.true );

    IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
    for( IService service : serviceFactory.createService( params ) ){
         try {
             IServiceInfo info = service.getInfo( null );  // try connecting to make sure the service works
             CatalogPlugin.getDefault().getLocalCatalog().add( service );
         }
         catch (IOException couldNotConnect ){
         }
    }

To to connect to a more interesting service such as PostGIS.

.. code-block:: java

    Map<String,Serializable> params = new HashMap<String,Serializable>();
    params.put("dbtype", "postgis");           // must be "postgis"
    params.put("host", "www.refractions.net"); // the name or ip address of the machine running PostGIS
    params.put("port", new Integer(5432));     // the port that PostGIS is running on (generally 5432)
    params.put("database", "demo-bc");         // the name of the database to connect to.
    params.put("user", "demo");                // the user to connect with
    params.put("passwd", "demo");

    IRepository local = CatalogPlugin.getDefault().getLocal();
    IService service = local.acquire( params, new NullProgressMonitor() );

Or a Web Feature Server:

.. code-block:: java

    URL url = new URL("http://www2.dmsolutions.ca/cgi-bin/mswfs_gmap?Version=1.0.0&Request=GetCapabilities&Service=wfs");

    Map<String,Serializable> params = new HashMap<String,Serializable>();
    params.put( WFSDataStoreFactory.URL.key, url );
    params.put( WFSDataStoreFactory.LENIENT.key, true );
    params.put( WFSDataStoreFactory.TRY_GZIP.key, true );

    IRepository local = CatalogPlugin.getDefault().getLocal();
    IService service = local.acquire( params, new NullProgressMonitor() );

.. todo:: 
   change to docs.geotools.org

To determine the connection parameters for many common servers review the `GeoTools User
Guide <http://docs.codehaus.org/display/GEOTDOC/Home>`_.

* `http://docs.codehaus.org/display/GEOTDOC/ArcSDE+Plugin <http://docs.codehaus.org/display/GEOTDOC/ArcSDE+Plugin>`_
* `http://docs.codehaus.org/display/GEOTDOC/Shapefile+Plugin <http://docs.codehaus.org/display/GEOTDOC/Shapefile+Plugin>`_
* `http://docs.codehaus.org/display/GEOTDOC/WFS+Plugin <http://docs.codehaus.org/display/GEOTDOC/WFS+Plugin>`_
* `http://docs.codehaus.org/display/GEOTDOC/DB2+Plugin <http://docs.codehaus.org/display/GEOTDOC/DB2+Plugin>`_
* `http://docs.codehaus.org/display/GEOTDOC/PostGIS+Plugin <http://docs.codehaus.org/display/GEOTDOC/PostGIS+Plugin>`_

IService
^^^^^^^^

The CatalogPlugin uses the interface IService to model a local or remote service.

Here are some examples to get us started:

-  A remote Database
-  A local File on disk
-  A Web Feature Server
-  An "internal" service such as the MapGraphics included with uDig

The identifier of a service is available - so you can find the service again at another time.

.. code-block:: java

    // recommended!
    ID id = service.getID();

    // slow!
    URL identifier = service.getIdentifier();

The id is like a quick version of URL (not subject to the usual delays during hashcode and equals).

You can grab a copy of the service title:

.. code-block:: java

    String title = service.getTitle()

This is useful when listing the service in a user interface (as it will make use of a cached copy of
the
 service title and not have to connect).

The connection parameters are available; you can store these parameters if you would like to connect
to the service again at a later time.

.. code-block:: java

    Map<String,Serializable> params = service.getConnectionParams()

For a Map the connection parameters are stored (so as a Map loads we will ensure each required
service is available in the local catalog). The catalog will also store these connection parameters
between runs so it can connect to the service again.

You can figure out which catalog the service belongs to:

.. code-block:: java

    ICatalog catalog = service.parent( new NullProgressMonitor() );

This method actually needs to connect to the service so a ProgressMonitor is used (allowing the user
to cancel).

To retrieve information about a service including its title, description and icon you can ask for
the ServiceInfo object:

.. code-block:: java

    IServiceInfo info = service.getInfo( new NullProgressMonitor());

    String title = info.getTitle();
    String description = info.getDescription();
    double metric = info.getMetric();

Grabbing a IServiceInfo is the best way to check if you can connect to a service. You will find that
the **IService.getMetric()** provides a good measurement of how well the service will work. It is used to
indicate if the service has all the information it needs to function smoothly. If some information
is missing, such as a coordinate reference system or index, some prep may be required.

You can check if a service is connected:

.. code-block:: java

    Status status = service.getStatus();

A service contains children.

.. code-block:: java

    for( IResolve child : service.members(new NullProgressMonitor())){
        //work with child
    }

These children are often IGeoResources representing spatial data; but they may also be folders or
processes depending on the service.

If you are only interested in spatial data there is a specific method that will list only the
GeoResources with useful data.

.. code-block:: java

    for( IGeoResource georesource : service.resources(new NullProgressMonitor()) ){
        // work with resource
    }

Service Specific Examples
'''''''''''''''''''''''''

To access a shapefile:

.. code-block:: java

    if( service.canResolve( ShapefileDataStore.class )){
         ShapefileDataStore shapefile = service.resolve( ShapefileDataStore.class, new NullProgressMonitor() );
    }

To access a WebMapServer:

.. code-block:: java

    if( service.canResolve( WebMapServer.class )){
        WebMapServer wms = service.resolve( WebMapServer.class, new NullProgressMonitor() );
        ...
    }

To access PostGIS data store:

.. code-block:: java

    if( service.canResolve( PostgisDataStore.class )){
         PostgisDataStore database = service.resolve( PostgisDataStore.class, new NullProgressMonitor() );
         ...
    }

To work with PostGIS jdbc connection:

.. code-block:: java

    if( service.canResolve( Connection.class )){
         Connection connection = service.resolve( Connection.class, new NullProgressMonitor() );
         try {
            ... issue jdbc commands...
         }
         finally {

            connection.close();
         }
    }

To access a WebMapServer:

.. code-block:: java

    if( service.canResolve( WebMapServer.class )){
        WebMapServer wms = service.resolve( WebMapServer.class, new NullProgressMonitor() );
        ...
    }

IGeoResource
^^^^^^^^^^^^

One of the most useful things stored in a catalog is actual spatial data. The IGeoResource interface
represents real information, the kind you can display on screen or perform analysis on.

Here are a few examples to get us started with:

-  A Table or View in a database
-  A FeatureCollection made available through a Web Feature Server (WFS)
-  A Web Map Server (WMS) Layer
-  The contents of a shapefile
-  A GridCoverage contained in an ArcGrid file

The **IGeoResource** implementation does not place any restrictions on the interface used to
interact with the external resource. That said here are our top contenders for most popular
interface:

From GeoTools:

-  **org.geotools.data.FeatureSource** used to represent Feature information available in a File,
   Database or Web Feature Server
-  **org.geotools.data.FeatureStore** used to represent Feature information that allows
   modification.
-  **org.geotools.data.ows.Layer** represents a externalized rendering service advertised by a WMS
-  **org.geotools.coverage.io.AbstractGridCoverageReader** represents raster information such as
   GeoTIFF or ArcGRID content

From Java:

-  **java.sql.Connection** a JDBC connection used to directly communicate with a database

Please see the Advanced section for details on making your own content available: CAD file formats,
feature content from other toolkits, and dynamically generated content are all exciting
possibilities.

IGeoResource API Overview
^^^^^^^^^^^^^^^^^^^^^^^^^

-  getInfo( IProgressMonitor ) access to a GeoResourceInfo describing this resource
-  service( IPorgressMonitor ) the service providing this resource
-  getIdentifier() identifier used to locate the resource in the catalog
-  dispose( IProgressMonitor )

IGeoResource instances can formed into a tree:

-  members( IProgressMonitor ) - used to treat IGeoResource like a folder that contains more content
-  parent( IProgressMonitor ) - the parent containing this IGeoResource

Use of IGeoResourceInfo
^^^^^^^^^^^^^^^^^^^^^^^

.. todo:: 
    ....pending...

IResolve
^^^^^^^^

CatalogPlugin uses the model of a "handle" to allow access to spatial resources.

The concept of a resource handle is represented as the IResolve class:

-  acts as a **Proxy** for remote content, you can ask a few basic questions (say askign for the
   bounds) without having to connect to the real remote service
-  acts as an "Adapter" for interacting with data, you can turn your IResolve into the object you
   really want, behind the scenes the catalog will make the connection and return you the class used
   to interact with data.
-  acts as an "Extensible Interface", you can make up your own data access APIs and teach the
   catalog how to make use of them

Here are the core responsibilities of IResolve interface:

-  IResolve.getIdentifier() is a unique URL used to identify this resource in the catalog
-  IResolve.canResolve( Class type ) is a non blocking check to see if a **type** of resource is
   available for the handle
-  IResolve.resolve( Class type, IProgressMonitor monitor ) will acquire the requested resource

IResolve handles can form a tree using the following methods:

-  members( IResolve parent, IProgressMonitor )
-  parent( IProgressMonitor )

Finally, just because a handle exists does not mean the real resource resources exists or is
working. A service may be down, or a shapefile may not be created yet.

Here is how to check on the status of a IResolve:

-  IResolve.getStatus(), one of CONNECTED, NOTCONNECTED or BROKEN

Note: Methods that are blocking make use of a IProgressMonitor, and throw an IOException in the
event of a problem. This allows for both feedback during the operation, and strongly indicates to
calling code that blocking input/output will occur.

Let's quickly work with an example (to make this real)

Use of canResolve and resolve methods

.. code-block:: java

    public count shapes( File shapefile ){
        CatalogPlugin catalog = CatalogPlugin.getDefault();
        IServiceFactory factory = catalog.getServiceFactory();
        for( IResolve resolve : factory.acquire( shapefile.toUrl() ) ){
           if( resolve.canResolve( DataStore.class ) ){
               DataStore shape = resolve.resolve( DataStore.class );
               String typeName = shape.getTypeNames()[0];
               return shape.getFeatureSource( typeName ).count();
           }
        }
        return 0;
    }

.. note:: **Comparison with IResource**

   The IResolve interface follows the same design as the normal Eclipse IResource class.

   IResolve offers the following advantages over normal Eclipse IResource:

   -  IResolve explicitly represents a handle for a remote resource
   -  IResolve blocking behavior is explicit at the API level, anything that takes an IProgressMonitor
      or throws an IOException is blocking
   -  IResolve is available for RCP applications, normal IResource is part of the Eclipse IDE and
      cannot be used in a RCP application
   -  IResolve uses Java 5 enums, type narrowing and Templates for a simplified API

Extending Catalog Plugin (Advanced)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

To extend catalog for additional formats you will need to make an implementation of IService,
IGeoResource and a WizardPage for your new content.

-  ServiceExtention: allow the catalog to work with new kinds of Services
-  ICatalog: teach the CatalogPlugin about new kinds of remote catalogs
-  temporaryResource: create new temporary resources
-  resolvers: teach the existing IResolve Implementations (like ShpGeoResource) about your
   application needs
-  friendly: build up assocations between services that are designed to work together

We are going to launch right into technical details here (this is the advanced section). If you
require additional background information please consider the following references:

-  Contributing to Eclipse (nice explanation of IResource, and IAdaptable)
* :doc:`eclipse_house_rules`


Common mistakes:

-  If you are used to making your own Eclipse plugins you may accidently depend on IResource, it
   will not be available at runtime since it is part of the Eclipse IDE.
-  `Eclipse House Rules <Eclipse%20House%20Rules.html>`_: You may only depend on public API packages
   (example net.refractions.udig.catalog). This is less of a problem since we are able to properly
   restrict packages in Eclipse 3.3.

ResolveManager
^^^^^^^^^^^^^^

Just because the core uDig team knows how to do a few tricks with Shapefiles, and turn them into a
FeatureSource does not mean you are left out of the game. You can teach the uDig catalog system new
tricks, making uDig classes aware of your applications needs at runtime.

.. figure:: /images/catalog/resolvemanager.png
   :align: center
   :alt: 

The ResolutionManager processes an extention point binding IResolve to new classes, you can use this
facility to integrate your own functionality with the uDig application.

Eclipse IDE Integration
^^^^^^^^^^^^^^^^^^^^^^^

When making your own instance of IResolve you can also implement IAdaptable (we ensured that no
method names would conflict). Implementing IAdaptable, and providing an adapter for IResource allows
for seamless integration with the Eclipse IDE.

This is out of scope for our current development effort - however the implementation is straight
forward and would allow integration of the GISPlatform with the wider Eclipse community. The Eclipse
workbench already checks for the classes supporting IAdaptable, and will automatically integrate any
class that responds to isAdaptable( IResource.class ).
